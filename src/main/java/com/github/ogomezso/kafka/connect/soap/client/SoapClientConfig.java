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

import com.github.ogomezso.kafka.connect.soap.commons.AbstractSoapConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class SoapClientConfig extends AbstractSoapConfig {

  public static final String REQUEST_MSG_FILE = "requestMessageFile";
  private static final String REQUEST_MSG_FILE_DOC = "Path to a file containing the xml service message.";
  private final File requestMsgFile;


  public SoapClientConfig(Map<?, ?> originals) {
    this(SoapClientConfig.config(), originals, Collections.emptyMap(), true);
  }

  public SoapClientConfig(ConfigDef definition, Map<?, ?> originals, Map<String, ?> configProviderProps,
                          boolean doLog) { // TODO ConfigProviderProps & doLog
    super(definition, originals, configProviderProps, doLog);
    this.requestMsgFile = new File(this.getString(REQUEST_MSG_FILE));
  }

  public static ConfigDef config() {
    return AbstractSoapConfig.config()
        .define(REQUEST_MSG_FILE, Type.STRING, Importance.HIGH, REQUEST_MSG_FILE_DOC);
  }
}
