/**
 * Copyright © 2021 Oscar Gómez (ogomezso0@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.ogomezso.kafka.connect.soap.client;

import com.github.jcustenborder.kafka.connect.utils.config.ConfigUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.SOAPBinding;
import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SoapClient {

  private SoapClientConfig config;

  public SoapClientConfig getConfig() {
    return config;
  }

  private static final int MAX_CORES_USED = 1; // client must be single-threaded, see https://docs.confluent.io/platform/current/streams/architecture.html#threading-model
  private final ScheduledExecutorService executor = Executors
      .newScheduledThreadPool(MAX_CORES_USED);

  private Callable<SOAPMessage> task;

  public void start(SoapClientConfig config) {
    this.config = config;

    QName serviceName = new QName(config.getString(SoapClientConfig.TARGET_NAMESPACE),
        config.getString(SoapClientConfig.SERVICE_NAME));
    QName portName = new QName(config.getString(SoapClientConfig.TARGET_NAMESPACE),
        config.getString(SoapClientConfig.PORT_NAME));
    String endpointUrl = config.getString(SoapClientConfig.ENDPOINT_URL);
    String actionUrl = config.getString(SoapClientConfig.SOAP_ACTION);
    File messageFile = ConfigUtils
        .getAbsoluteFile(config, SoapClientConfig.REQUEST_MSG_FILE);
    Long connectionTimeout = config.getLong(SoapClientConfig.CONNECTION_TIMEOUT);
    Long requestTimeout = config.getLong(SoapClientConfig.REQUEST_TIMEOUT);
    createCircuitBreakerTask(serviceName, portName, endpointUrl, actionUrl, messageFile, connectionTimeout,
        requestTimeout);
  }

  private void createCircuitBreakerTask(QName serviceName, QName portName, String endpointUrl, String actionUrl,
      File messageFile, Long connectionTimeout, Long requestTimeout) {
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(10)
        .slidingWindow(connectionTimeout.intValue() * 3, 3, SlidingWindowType.TIME_BASED)
        .build();

    CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig);
    CircuitBreaker circuitBreaker = registry.circuitBreaker("SoapTask");
    this.task = CircuitBreaker
        .decorateCallable(circuitBreaker, () -> {
          SOAPMessage result = null;
          try {
            result = invoke(serviceName, portName, endpointUrl, actionUrl,
                messageFile, connectionTimeout, requestTimeout);
          } catch (SOAPException e) {
            log.error("Error executing SOAP Call", e);
          } catch (FileNotFoundException e) {
            log.error("Request file not present", e);
          }
          return Optional.ofNullable(result)
              .orElseThrow(() -> new RuntimeException("Unexpected Error fetching Service"));
        });
  }

  public SOAPMessage poll(Long pollInterval)
      throws ExecutionException, InterruptedException, TimeoutException {

    ScheduledFuture<SOAPMessage> future = executor
        .schedule(task, pollInterval,
            TimeUnit.MILLISECONDS);

    log.debug("invoking at: " + LocalDateTime.now());
    SOAPMessage result = future
        .get(pollInterval + 5000L,
            TimeUnit.MILLISECONDS);
    log.debug("result: " + result.toString());
    log.debug("get future at : " + LocalDateTime.now());
    return result;
  }

  private SOAPMessage invoke(QName serviceName, QName portName, String endpointUrl,
      String soapActionUri, File pathToMessage, Long connectionTimeout, Long requestTimeout)
      throws SOAPException, FileNotFoundException {
    Service service = Service.create(serviceName);
    service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);

    Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
        SOAPMessage.class, Service.Mode.MESSAGE);
    dispatch.getRequestContext()
        .put("com.sun.xml.ws.connect.timeout", connectionTimeout.intValue());
    dispatch.getRequestContext().put("com.sun.xml.ws.request.timeout", requestTimeout.intValue());
    dispatch.getRequestContext().put(Dispatch.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
    dispatch.getRequestContext().put(Dispatch.SOAPACTION_URI_PROPERTY, soapActionUri);

    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage message = messageFactory.createMessage();
    SOAPPart soapPart = message.getSOAPPart();
    StreamSource preppedMsgSrc = new StreamSource(new FileInputStream(
        pathToMessage));
    soapPart.setContent(preppedMsgSrc);
    message.saveChanges();

    return dispatch.invoke(message);
  }

  public void stop() {
    executor.shutdown();
  }

}
