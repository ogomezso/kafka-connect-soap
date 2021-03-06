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

import org.apache.kafka.common.config.ConfigDef;

import java.util.Collections;
import java.util.Map;

public class SoapSourceConnectorConfig extends AbstractSoapSourceConfig {

  public SoapSourceConnectorConfig(ConfigDef definition, Map<?, ?> originals) {
    this(definition, originals, Collections.emptyMap(), true);
  }

  public SoapSourceConnectorConfig(ConfigDef definition, Map<?, ?> originals, Map<String, ?> configProviderProps,
                                   boolean doLog) { // todo configProvider & dolog
    super(definition, originals, configProviderProps, doLog);
  }

  public static ConfigDef config() {

    return AbstractSoapSourceConfig.config();
  }
}
