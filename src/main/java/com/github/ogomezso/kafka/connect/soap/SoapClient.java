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

package com.github.ogomezso.kafka.connect.soap;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.SOAPBinding;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoapClient {

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
  private final RecordMapper mapper = new RecordMapper();

  private Callable<SOAPMessage> task;

  @SneakyThrows
  public void start(SoapSourceConnectorConfig config) {
    QName serviceName = new QName(config.getString(SoapSourceConnectorConfig.TARGET_NAMESPACE),
        config.getString(SoapSourceConnectorConfig.SERVICE_NAME));
    QName portName = new QName(config.getString(SoapSourceConnectorConfig.TARGET_NAMESPACE),
        config.getString(SoapSourceConnectorConfig.PORT_NAME));
    String endpointUrl = config.getString(SoapSourceConnectorConfig.ENDPOINT_URL);
    String actionUrl = Optional.of(SoapSourceConnectorConfig.SOAP_ACTION).orElse("");
    String pathToMessage = config.getString(SoapSourceConnectorConfig.REQUEST_MSG_FILE);

    this.task = () -> invoke(serviceName, portName, endpointUrl, actionUrl,
        pathToMessage);
  }

  @SneakyThrows
  public Record poll(Long pollInterval) {
    ScheduledFuture<SOAPMessage> future = executor
        .schedule(task, pollInterval,
            TimeUnit.SECONDS);

    log.info("invonking at: " + LocalDateTime.now());
    SOAPMessage result = future
        .get(pollInterval + 5L,
            TimeUnit.SECONDS);
    log.info("result: " + result.toString());
    log.info("get future at : " + LocalDateTime.now());
    return mapper.getRecordFromSoapMessage(result);
  }

  public SOAPMessage invoke(QName serviceName, QName portName, String endpointUrl,
      String soapActionUri, String pathToMessage) throws Exception {
    Service service = Service.create(serviceName);
    service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);

    Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
        SOAPMessage.class, Service.Mode.MESSAGE);
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
