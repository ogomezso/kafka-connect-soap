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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoapSourceTask extends SourceTask {

  SoapSourceConnectorConfig config;
  SoapClient client = new SoapClient();

  @Override
  public String version() {

    return VersionUtil.version(this.getClass());
  }

  @Override
  public void start(Map<String, String> map) {

    log.info("Starting SOAP Source Task");
    this.config = new SoapSourceConnectorConfig(map);

    client.start(config);
  }

  @Override
  public List<SourceRecord> poll() {
    Record record = client.poll(config.getLong(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS));

    return Collections.singletonList(createSourceRecordFromSseEvent(record));
  }

  private SourceRecord createSourceRecordFromSseEvent(Record event) {
    Map<String, ?> srcOffset = Collections.emptyMap();
    Map<String, ?> srcPartition = Collections.emptyMap();

    log.debug("Event " + event.toString());
    return new SourceRecord(
        srcPartition,
        srcOffset,
        this.config.getString(SoapSourceConnectorConfig.TOPIC),
        null,
        null,
        Record.SCHEMA,
        event
    );

  }

  @Override
  public void stop() {
    client.stop();
  }
}
