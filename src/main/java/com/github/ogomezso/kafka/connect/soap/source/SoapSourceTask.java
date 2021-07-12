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

package com.github.ogomezso.kafka.connect.soap.source;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.utils.config.ConfigUtils;
import com.github.ogomezso.kafka.connect.soap.client.SoapClient;
import com.github.ogomezso.kafka.connect.soap.model.RecordKey;
import com.github.ogomezso.kafka.connect.soap.model.SourceRecordMapper;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class SoapSourceTask extends SourceTask {

  private final SourceRecordMapper mapper = new SourceRecordMapper();
  SoapSourceConnectorConfig config;
  SoapClient client = new SoapClient();
  private Long pollInterval;
  private String topic;
  private String serviceName;
  private File request;

  public SoapSourceTask(SoapClient client, Long pollInterval, String topic, String serviceName,
      File request) {
    this.client = client;
    this.pollInterval = pollInterval;
    this.topic = topic;
    this.serviceName = serviceName;
    this.request = request;
  }


  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  @Override
  public void start(Map<String, String> map) {
    log.info("Starting SOAP Source Task");
    config = new SoapSourceConnectorConfig(map);
    validateAndsSetConfigVars(config);
    client.start(config);
  }

  private void validateAndsSetConfigVars(SoapSourceConnectorConfig config) {

    pollInterval = Optional.of(config.getLong(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS))
        .filter(p -> p > 0)
        .orElseThrow(() -> new ConfigException("Poll interval must be greater than 0"));
    topic = Optional.ofNullable(this.config.getString(SoapSourceConnectorConfig.TOPIC))
        .orElseThrow(() -> new ConfigException("Topic can't be null"));
    request = ConfigUtils.getAbsoluteFile(config, SoapSourceConnectorConfig.REQUEST_MSG_FILE);
    serviceName = this.config.getString(SoapSourceConnectorConfig.SERVICE_NAME);
  }

  @Override
  public List<SourceRecord> poll() {
    try {

      SOAPMessage clientResponse = client.poll(pollInterval);

      return Collections.singletonList(mapper.getSourceRecordFromSoapMessage(
          createRecordKey(),
          clientResponse,
          topic));
    } catch (ExecutionException | InterruptedException | TimeoutException | SOAPException e) {
      log.error("Error getting response from SOAP Client", e);
    } catch (TransformerException | IOException e) {
      log.error("Error processing the Source Record", e);
    }

    return null;
  }

  private RecordKey createRecordKey() {
    String requestType = request.getName().substring(0, request.getName().lastIndexOf("."));

    return RecordKey.builder()
        .serviceName(serviceName)
        .requestType(requestType)
        .build();
  }

  @Override
  public void stop() {
    client.stop();
  }
}
