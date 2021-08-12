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

import com.github.ogomezso.kafka.connect.soap.commons.AbstractSoapConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AbstractSoapSourceConfig extends AbstractSoapConfig {

  public static final String REQUEST_MSG_FILES = "requestMessageFiles";
  public static final String REQUEST_TOPIC_ASSIGNMENT = "requestTopicAssignment";

  private static final String REQUEST_MSG_FILES_DOC = "List of java.io.File objects containing the xml service " +
                                                          "message.";
  private static final String REQUEST_TOPIC_ASSIGNMENT_DOC = "The strategy determining in which topic each client " +
                                                                 "response is written into. <ONE_TOPIC | " +
                                                                 "TOPIC_PER_REQUEST | CUSTOM_ASSIGNMENT>";

  private final List<File> requestMsgFiles;
  private final String requestTopicAssignment;


  public AbstractSoapSourceConfig(ConfigDef definition, Map<?, ?> originals) {
    this(definition, originals, Collections.emptyMap(), true);
  }

  public AbstractSoapSourceConfig(ConfigDef definition, Map<?, ?> originals, Map<String, ?> configProviderProps,
                                  boolean doLog) { // todo configProvider & dolog
    super(definition, originals);

    requestMsgFiles = new ArrayList<>();
    for (String f : this.getString(REQUEST_MSG_FILES).split(",")) {
      File file = new File(f.trim());
      if (!file.isAbsolute()) {
        throw new ConfigException(
            "REQUEST_MESSAGE_FILES",
            f,
            "Must be an absolute path."
        );
      }
      requestMsgFiles.add(file);
    }

    requestTopicAssignment = this.getString(REQUEST_TOPIC_ASSIGNMENT);
  }

  public static ConfigDef config() {

    return AbstractSoapConfig.config()
        .define(REQUEST_MSG_FILES, Type.STRING, Importance.HIGH, REQUEST_MSG_FILES_DOC)
        .define(REQUEST_TOPIC_ASSIGNMENT, Type.STRING, "TOPIC_PER_REQUEST", Importance.HIGH,
            REQUEST_TOPIC_ASSIGNMENT_DOC);
  }
}
