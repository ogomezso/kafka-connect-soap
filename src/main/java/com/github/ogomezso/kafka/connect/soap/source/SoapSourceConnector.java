/**
 *  Copyright © 2021 Oscar Gómez (ogomezso0@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.ogomezso.kafka.connect.soap.source;



import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.Title;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Description("Kafka Connect source connector for SOAP Services")
@Title("Kafka Connect SOAP")
@Slf4j
public class SoapSourceConnector extends SourceConnector {

  Map<String, String> settings;
  SoapSourceConnectorConfig config;

  @Override
  public void start(Map<String, String> map) {
    log.info("Starting Server Sent Events Source Connector");
    this.config = new SoapSourceConnectorConfig(SoapSourceConnectorConfig.config(), map);
    this.settings = map;
  }

  @Override
  public Class<? extends Task> taskClass() {
    return SoapSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    ArrayList<Map<String, String>> taskConfigs = new ArrayList<>();
    if (!this.config.getString(SoapSourceConnectorConfig.REQUEST_MSG_FILES).isEmpty()) {
      String[] files = this.config.getString(SoapSourceConnectorConfig.REQUEST_MSG_FILES).split(",");
      String[] topics = this.config.getString(SoapSourceConnectorConfig.TOPIC).split(",");
      Arrays.setAll(files, i -> files[i].trim());
      Arrays.setAll(topics, i -> topics[i].trim());

      final int tasks = Math.min(maxTasks, files.length); // determine the actual no of tasks

      for (int i = 0; i < tasks; i++) {
        Map<String, String> taskConfig = new HashMap<>(settings);
        StringBuilder taskFiles = new StringBuilder();
        StringBuilder taskTopics = new StringBuilder();
        for (int j = i; j < files.length; j = j + tasks) {            // distribute files (& topics) round-robin
          taskFiles
              .append(taskFiles.length() == 0 ? "" : ", ")
              .append(files[j]);
          if (topics.length > 1) {
            assert files.length == topics.length; // should always be true at this point
            taskTopics
                .append(taskTopics.length() == 0 ? "" : ", ")
                .append(topics[j]);
          }
        }

        taskConfig.put(SoapSourceTaskConfig.REQUEST_MSG_FILES, taskFiles.toString());
        if (taskTopics.length() > 0)
          taskConfig.put(SoapSourceTaskConfig.TOPIC,  taskTopics.toString());

        taskConfigs.add(taskConfig);
      }
    } else {
      throw new ConfigException("No request file provided.");
    }
    return taskConfigs;
  }

  @Override
  public void stop() {

    log.info("Stopping SOAP SourceConnector");
  }

  @Override
  public ConfigDef config() {
    return SoapSourceConnectorConfig.config();
  }

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }
}
