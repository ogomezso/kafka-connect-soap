/**
 * Copyright © 2021 Oscar Gómez (ogomezso0@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.ogomezso.kafka.connect.soap.source;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.ogomezso.kafka.connect.soap.client.SoapClient;
import com.github.ogomezso.kafka.connect.soap.client.SoapClientConfig;
import com.github.ogomezso.kafka.connect.soap.model.RecordKey;
import com.github.ogomezso.kafka.connect.soap.model.SourceRecordMapper;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@Slf4j
@NoArgsConstructor
public class SoapSourceTask extends SourceTask {

  private final SourceRecordMapper mapper = new SourceRecordMapper();
  private final List<String> validAssignmentModes = Arrays.asList(
      "",
      "ONE_TOPIC",
      "TOPIC_PER_REQUEST",
      "CUSTOM_ASSIGNMENT"
  );
  SoapSourceTaskConfig config;
  private List<SoapClient> clients = new ArrayList<>();
  private Long pollInterval;
  private Long connectionTimeout;
  private String topicPrefix;
  private String[] topics;
  private String[] files;
  private String serviceName;
  private String topicAssignmentStrategy;
  private List<SoapClientConfig> requests = new ArrayList<>();


  public SoapSourceTask(SoapClient client, Long pollInterval, String topic, String serviceName,
                        List<File> requests) {
    this.clients.add(client);
    this.pollInterval = pollInterval;
    this.topics[0] = topic;
    this.serviceName = serviceName;
    for (File r : requests) {
      Map<String, String> c = new HashMap<>();
      c.put(SoapClientConfig.TOPIC, topic);
      c.put(SoapClientConfig.REQUEST_MSG_FILE, r.getAbsolutePath());
      c.put(SoapClientConfig.CONNECTION_TIMEOUT, "1000");
      c.put(SoapClientConfig.SERVICE_NAME, serviceName);

      this.requests.add(new SoapClientConfig(c));
    }
  }

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }


  @Override
  public void start(Map<String, String> map) {
    this.config = new SoapSourceTaskConfig(SoapSourceTaskConfig.config(), map);
    validateAndsSetConfigVars(config);

    for (SoapClientConfig r : requests) {
      SoapClient cl = new SoapClient();
      cl.start(r);
      clients.add(cl);
    }
  }

  List<SoapClientConfig> test_validateAndsSetConfigVars(Map<String, String> map) {
    this.config = new SoapSourceTaskConfig(SoapSourceTaskConfig.config(), map);
    validateAndsSetConfigVars(config);

    return this.requests;
  }

  private void validateAndsSetConfigVars(SoapSourceTaskConfig config) {

    // validation of parameters with values that are set equally to all clients

    pollInterval = Optional.of(config.getLong(SoapSourceTaskConfig.POLL_INTERVAL))
        .filter(p -> p > 0)
        .orElseThrow(() -> new ConfigException("Poll interval must be greater than 0"));
    connectionTimeout = Optional.of(config.getLong(SoapSourceTaskConfig.CONNECTION_TIMEOUT))
        .filter(p -> (p > 0 && p < pollInterval))
        .orElseThrow(() -> new ConfigException("Poll interval must be greater than connection timeout"));
    serviceName = this.config.getString(SoapSourceTaskConfig.SERVICE_NAME);
    topicPrefix = this.config.getString(SoapSourceTaskConfig.TOPIC_PREFIX);
    topicAssignmentStrategy = this.config.getString(SoapSourceTaskConfig.REQUEST_TOPIC_ASSIGNMENT);

    files = this.config.getString(SoapSourceTaskConfig.REQUEST_MSG_FILES).split(",");
    topics = this.config.getString(SoapSourceTaskConfig.TOPIC).split(",");

    Arrays.setAll(files, i -> files[i].trim());
    Arrays.setAll(topics, i -> topics[i].trim());

    switch (topicAssignmentStrategy) {
      case "CUSTOM_ASSIGNMENT":
        if (files.length != topics.length) {
          throw new ConfigException("Custom request->topic assignment impossible due to an incompatible combination" +
                                        " of config parameters");
        }
        break;

      case "ONE_TOPIC":
      case "TOPIC_PER_REQUEST":
      default:
        if (topics.length > 1) {
          throw new ConfigException("Config parameter 'topic' is incompatible with chosen assignment strategy");
        }
    }

    // client-specific configurations

    for (int i = 0; i < files.length; i++) {
      Map<String, String> clientConf = config.originalsStrings();

      clientConf.put(SoapClientConfig.POLL_INTERVAL, pollInterval.toString());
      clientConf.put(SoapClientConfig.CONNECTION_TIMEOUT, connectionTimeout.toString());
      clientConf.put(SoapClientConfig.SERVICE_NAME, serviceName);
      clientConf.put(SoapClientConfig.REQUEST_MSG_FILE, files[i]);

      switch (topicAssignmentStrategy) {
        case "ONE_TOPIC": // all clients write into one topic called topic_prefix+topic
          clientConf.put(SoapClientConfig.TOPIC,
                  topics[0]
          );
          break;

        case "TOPIC_PER_REQUEST": // clients write into their topic called topic_prefix+topic+filename
          clientConf.put(SoapClientConfig.TOPIC,
                  topics[0]
                  + new File(files[i]).getName()
          );
          break;

        case "CUSTOM_ASSIGNMENT": // clients write into their topic called topic_prefix+topic[position]
          clientConf.put(SoapClientConfig.TOPIC,
                  topics[i]
          );
          break;

        default:
          throw new ConfigException("Unknown topic-assignment strategy");
      } // end switch - topic assignment strategies
      requests.add(new SoapClientConfig(clientConf));
    } // end for - client specific-configurations
  }

  @Override
  public List<SourceRecord> poll() {
    // This currently blocks for each client
    // TODO rewrite poll() with an event queue, and allow quotas per client via individual pollInterval

    ArrayList<SourceRecord> records = new ArrayList<>();

    for (SoapClient client : clients) {
      try {
        SOAPMessage clientResponse = client.poll(pollInterval);
        records.add(mapper.getSourceRecordFromSoapMessage(
                createRecordKey(client.getConfig()),
                clientResponse,
            client.getConfig().getString(SoapClientConfig.TOPIC_PREFIX) + client.getConfig().getString(SoapClientConfig.TOPIC) // TODO remove topic from ClientConfig
            )
        );
      } catch (ExecutionException | InterruptedException | TimeoutException | SOAPException e) {
        log.error("Error getting response from SOAP Client", e);
      } catch (TransformerException | IOException e) {
        log.error("Error processing the Source Record", e);
      }
    }
    return records;
  }

  private RecordKey createRecordKey(SoapClientConfig meta) {
    String requestType = meta.getString(SoapClientConfig.REQUEST_MSG_FILE)
        .substring(0, meta.getString(SoapClientConfig.REQUEST_MSG_FILE).lastIndexOf("."));

    return RecordKey.builder()
        .serviceName(serviceName)
        .requestType(requestType)
        .build();
  }

  @Override
  public void stop() {
    for (SoapClient c : clients) {
      c.stop();
    }
  }
}
