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
    this.config = new SoapSourceConnectorConfig(map);
    this.settings = map;
  }

  @Override
  public Class<? extends Task> taskClass() {
    return SoapSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    // Every task will receive one file as config.
    // Every request must be handled by its own task so max tasks must be the number of request records.

    ArrayList<Map<String, String>> taskConfigs = new ArrayList<>();
    if (!this.config.getList(SoapSourceConnectorConfig.REQUEST_MSG_FILES).isEmpty()) {
      List<String> files = this.config.getList(SoapSourceConnectorConfig.REQUEST_MSG_FILES);
      final int tasks = Math.min(maxTasks, files.size());
      for (int i = 0; i < tasks; i++) {
        Map<String, String> taskConfig = new HashMap<>(settings); // TODO Cleanup unused params instead of copying all.
        for (int j = i; j < files.size(); j = j + tasks) {            // distribute files round-robin
          taskConfig.put(SoapSourceTaskConfig.REQUEST_MSG_FILE, files.get(j));
        }
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
