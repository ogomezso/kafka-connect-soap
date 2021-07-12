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

import java.util.HashMap;
import java.util.Map;

public class SourceTaskSettingMother {

  static final String KO_POLL_INTERVAL = "-5";
  static final String POLL_INTERVAL = "5";
  static final String URL = "http://fakeserver:9999/fakeendpoint";
  static final String SERVICE = "fakeService";
  static final String TARGET_NAME_SPACE = "targetNameSpace";
  static final String PORT_NAME = "PortName";
  static final String SOAP_ACTION = "soapAction";
  static final String REQUEST_FILE = "/homer/fakeuser/equest.xml";
  static final String KO_REQUEST_FILE = "request.xml";
  static final String TOPIC = "topic";

  static Map<String, String> createValidMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createMissingEndpointUrlSpaceMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }
  static Map<String, String> createKoPollMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, KO_POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createMissingPortNameMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createMissingRequestFileMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createKoRequestFileMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, KO_REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createMissingServiceNameMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createMissingTargetNameSpaceMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TOPIC, TOPIC);
    }};
  }

  static Map<String, String> createMissingTopicMockSettings() {
    return new HashMap<String, String>() {{
      put(SoapSourceConnectorConfig.ENDPOINT_URL, URL);
      put(SoapSourceConnectorConfig.POLL_INTERVAL_SECONDS, POLL_INTERVAL);
      put(SoapSourceConnectorConfig.PORT_NAME, PORT_NAME);
      put(SoapSourceConnectorConfig.REQUEST_MSG_FILE, REQUEST_FILE);
      put(SoapSourceConnectorConfig.SERVICE_NAME, SERVICE);
      put(SoapSourceConnectorConfig.SOAP_ACTION, SOAP_ACTION);
      put(SoapSourceConnectorConfig.TARGET_NAMESPACE, TARGET_NAME_SPACE);
    }};
  }

}
