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

import java.io.File;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import com.github.jcustenborder.kafka.connect.utils.config.ConfigUtils;

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

  private final String endpointUrl;
  private final String portName;
  private final Long pollIntervalSeconds;
  private final File requestMsgFile;
  private final String serviceName;
  private final String soapAction;
  private final String targetNameSpace;
  private final String topic;

  public SoapSourceConnectorConfig(Map<?, ?> originals) {
    super(config(), originals);
    endpointUrl = this.getString(ENDPOINT_URL);
    portName = this.getString(PORT_NAME);
    pollIntervalSeconds = this.getLong(POLL_INTERVAL_SECONDS);
    requestMsgFile = ConfigUtils.getAbsoluteFile(this, REQUEST_MSG_FILE);
    serviceName = this.getString(SERVICE_NAME);
    soapAction = this.getString(SOAP_ACTION);
    targetNameSpace = this.getString(TARGET_NAMESPACE);
    topic = this.getString(TOPIC);
  }

  public static ConfigDef config() {

    return new ConfigDef()
        .define(ENDPOINT_URL, Type.STRING, Importance.HIGH, ENDPOINT_URL_DOC)
        .define(PORT_NAME, Type.STRING, Importance.HIGH, PORT_NAME_DOC)
        .define(POLL_INTERVAL_SECONDS, Type.LONG, 30, Importance.HIGH, POLL_INTERVAL_SECONDS_DOC)
        .define(REQUEST_MSG_FILE, Type.STRING, Importance.HIGH, REQUEST_MSG_FILE_DOC)
        .define(SERVICE_NAME, Type.STRING, Importance.HIGH, SERVICE_NAME_DOC)
        .define(SOAP_ACTION, Type.STRING, "", Importance.HIGH, SOAP_ACTION_DOC)
        .define(TARGET_NAMESPACE, Type.STRING, Importance.HIGH, TARGET_NAMESPACE_DOC)
        .define(TOPIC, Type.STRING, Importance.HIGH, TOPIC_DOC);
  }
}
