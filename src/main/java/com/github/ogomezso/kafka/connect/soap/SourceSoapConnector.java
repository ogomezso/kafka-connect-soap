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

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.TaskConfigs;
import com.github.jcustenborder.kafka.connect.utils.config.Title;

@Description("Kafka Connect source connector for SOAP Services")
@Title("Kafka Connect SOAP")
public class SourceSoapConnector extends SourceConnector {

  private static final Logger log = LoggerFactory.getLogger(SourceSoapConnector.class);
  private SoapSourceConnectorConfig config;
  Map<String, String> settings;

  @Override
  public void start(Map<String, String> map) {
    log.info("Starting Server Sent Events Source Connector");
    config = new SoapSourceConnectorConfig(map);
    this.settings = map;
  }

  @Override
  public Class<? extends Task> taskClass() {
    return null;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    return TaskConfigs.single(this.settings);
  }

  @Override
  public void stop() {

    log.info("Stopping SOAP SourceConnector");
  }

  @Override
  public ConfigDef config() {
    return null;
  }

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }
}
