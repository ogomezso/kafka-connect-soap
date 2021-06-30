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

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import com.github.jcustenborder.kafka.connect.utils.config.ConfigKeyBuilder;

public class SoapSourceConnectorConfig extends AbstractConfig {

  public static final String ENDPOINT_URL = "endpointUrl";
  public static final String POLL_INTERVAL_SECONDS = "pollTime";
  public static final String PORT_NAME = "portName";
  public static final String REQUEST_MSG_FILE = "requestMessageFile";
  public static final String SERVICE_NAME = "serviceName";
  public static final String SOAP_ACTION = "SOAPAction";
  public static final String TARGET_NAMESPACE = "targetNameSpace";
  public static final String TOPIC = "topic";
  private static final String ENDPOINT_URL_DOC = "Endpoint url for a service";
  private static final String POLL_INTERVAL_SECONDS_DOC = "Time between service calls in seconds";
  private static final String PORT_NAME_DOC = "Port Name for a service";
  private static final String REQUEST_MSG_FILE_DOC = "Absolute path to xml file containing the service message";
  private static final String SERVICE_NAME_DOC = "Service Name for SOAP will be invoked";
  private static final String SOAP_ACTION_DOC = "SOAP Action for a message";
  private static final String TARGET_NAMESPACE_DOC = "Target Namespace for the SOAP Client";
  private static final String TOPIC_DOC = "Topic to send events to";

  private final String enpointUrl;
  private final String portName;
  private final String requestMsgFile;
  private final String serviceName;
  private final String soapAction;
  private final String targetNameSpace;
  private final String topic;

  public SoapSourceConnectorConfig(Map<?, ?> originals) {
    super(config(), originals);
    this.enpointUrl = this.getString(ENDPOINT_URL);
    this.portName = this.getString(PORT_NAME);
    this.requestMsgFile = this.getString(REQUEST_MSG_FILE);
    this.serviceName = this.getString(SERVICE_NAME);
    this.soapAction = this.getString(SOAP_ACTION);
    this.targetNameSpace = this.getString(TARGET_NAMESPACE);
    this.topic = this.getString(TOPIC);
  }

  public static ConfigDef config() {

    return new ConfigDef()
        .define(
            ConfigKeyBuilder.of(ENDPOINT_URL, Type.STRING)
                .documentation(ENDPOINT_URL_DOC)
                .importance(Importance.HIGH)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(PORT_NAME, Type.STRING)
                .documentation(PORT_NAME_DOC)
                .importance(Importance.HIGH)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(POLL_INTERVAL_SECONDS, Type.LONG)
                .documentation(POLL_INTERVAL_SECONDS_DOC)
                .importance(Importance.HIGH)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(REQUEST_MSG_FILE, Type.STRING)
                .documentation(REQUEST_MSG_FILE_DOC)
                .importance(Importance.HIGH)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(SERVICE_NAME, Type.STRING)
                .documentation(SERVICE_NAME_DOC)
                .importance(Importance.HIGH)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(SOAP_ACTION, Type.STRING)
                .documentation(SOAP_ACTION_DOC)
                .importance(Importance.LOW)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(TARGET_NAMESPACE, Type.STRING)
                .documentation(TARGET_NAMESPACE_DOC)
                .importance(Importance.HIGH)
                .build()
        )
        .define(
            ConfigKeyBuilder.of(TOPIC, Type.STRING)
                .documentation(TOPIC_DOC)
                .importance(Importance.HIGH)
                .build()
        );
  }
}
