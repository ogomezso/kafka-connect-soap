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

package com.github.ogomezso.kafka.connect.soap.commons;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Collections;
import java.util.Map;

public class AbstractSoapConfig extends AbstractConfig {

  /* The super-class carrying configuration that must be in ALL Config classes in the SOAP Connector
  *
  *                                   AbstractSoapConfig
  *                                   /                \
  *           AbstractSoapSourceConfig                  -> SoapClientConfig
  *              |
  *             -> SoapSourceConnectorConfig
  *             -> SoapSourceTaskConfig
  *
  *  */

  public static final String CONNECTION_TIMEOUT = "connectionTimeOut";
  public static final String ENDPOINT_URL = "endpointUrl";
  public static final String POLL_INTERVAL = "pollInterval";
  public static final String PORT_NAME = "portName";
  public static final String REQUEST_TIMEOUT = "requestTimeOut";
  public static final String SERVICE_NAME = "serviceName";
  public static final String SOAP_ACTION = "SOAPAction";
  public static final String TARGET_NAMESPACE = "targetNameSpace";
  public static final String TOPIC_PREFIX = "topicPrefix";
  public static final String TOPIC = "topic";

  private static final String CONNECTION_TIMEOUT_DOC = " SOAP Service Connection timeout in milliseconds";
  private static final String ENDPOINT_URL_DOC = "Endpoint url for a service";
  private static final String POLL_INTERVAL_DOC = "Time between service calls in milliseconds";
  private static final String PORT_NAME_DOC = "Port Name for a service";
  private static final String REQUEST_TIMEOUT_DOC = "SOAP Request Timeout in Milliseconds";
  private static final String SERVICE_NAME_DOC = "Service Name for SOAP will be invoked";
  private static final String SOAP_ACTION_DOC = "SOAP Action for a message";
  private static final String TARGET_NAMESPACE_DOC = "Target Namespace for the SOAP Client";
  private static final String TOPIC_PREFIX_DOC = "The prefix that is added to each topic name.";
  private static final String TOPIC_DOC = "Topic to send events to";

  private final Long connectionTimeOut;
  private final String endpointUrl;
  private final String portName;
  private final Long pollIntervalSeconds;
  private final Long requestTimeout;
  private final String serviceName;
  private final String soapAction;
  private final String targetNameSpace;
  private final String topicPrefix;
  private final String topic;

  public AbstractSoapConfig(ConfigDef definition, Map<?, ?> originals) {
    this(definition, originals, Collections.emptyMap(), true);
  }

  public AbstractSoapConfig(ConfigDef definition, Map<?, ?> originals, Map<String, ?> configProviderProps,
                          boolean doLog) {
    // TODO configprovider & logging

    super(definition, originals);
    connectionTimeOut = this.getLong(CONNECTION_TIMEOUT);
    endpointUrl = this.getString(ENDPOINT_URL);
    portName = this.getString(PORT_NAME);
    pollIntervalSeconds = this.getLong(POLL_INTERVAL);
    requestTimeout = this.getLong(REQUEST_TIMEOUT);
    serviceName = this.getString(SERVICE_NAME);
    soapAction = this.getString(SOAP_ACTION);
    targetNameSpace = this.getString(TARGET_NAMESPACE);
    topicPrefix = this.getString(TOPIC_PREFIX);
    topic = this.getString(TOPIC);
  }

  public static ConfigDef config() {

    return new ConfigDef()
        .define(CONNECTION_TIMEOUT, Type.LONG, 30000, Importance.MEDIUM, CONNECTION_TIMEOUT_DOC)
        .define(ENDPOINT_URL, Type.STRING, Importance.HIGH, ENDPOINT_URL_DOC)
        .define(PORT_NAME, Type.STRING, Importance.HIGH, PORT_NAME_DOC)
        .define(POLL_INTERVAL, Type.LONG, 6000, Importance.HIGH, POLL_INTERVAL_DOC)
        .define(REQUEST_TIMEOUT, Type.LONG, 30000, Importance.LOW, REQUEST_TIMEOUT_DOC)
        .define(SERVICE_NAME, Type.STRING, Importance.HIGH, SERVICE_NAME_DOC)
        .define(SOAP_ACTION, Type.STRING, "", Importance.HIGH, SOAP_ACTION_DOC)
        .define(TARGET_NAMESPACE, Type.STRING, Importance.HIGH, TARGET_NAMESPACE_DOC)
        .define(TOPIC_PREFIX, Type.STRING, "", Importance.HIGH, TOPIC_PREFIX_DOC)
        .define(TOPIC, Type.STRING, Importance.HIGH, TOPIC_DOC);
  }
}
