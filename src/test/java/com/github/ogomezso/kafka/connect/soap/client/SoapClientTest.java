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

import jakarta.xml.soap.SOAPMessage;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoapClientTest {

  private static final String URL = "http://fakeserver:9999/fakeendpoint";
  private static final String SERVICE = "fakeService";
  private static final String TARGET_NAME_SPACE = "targetNameSpace";
  private static final String PORT_NAME = "PortName";
  private static final String SOAP_ACTION = "soapAction";
  private static final String REQUEST_FILE = "/homer/fakeuser/request.xml";
  private static final String KO_REQUEST_FILE = "request.xml";
  private static final Long CONNECTION_TIMEOUT = 5L;

  @InjectMocks
  private final SoapClient stubbedClassToTest = new SoapClient();
  private final SoapClient classToTest = new SoapClient();
  @Mock
  private SoapClientConfig mockConfig;
  @Mock
  private Callable<SOAPMessage> mockTask;
  @Mock
  private SOAPMessage mockReturnMessage;

  @Test
  public void given_not_null_config_client_starts() {
    when(mockConfig.getString(SoapClientConfig.ENDPOINT_URL)).thenReturn(URL);
    when(mockConfig.getString(SoapClientConfig.PORT_NAME)).thenReturn(PORT_NAME);
    when(mockConfig.getString(SoapClientConfig.REQUEST_MSG_FILE)).thenReturn(REQUEST_FILE);
    when(mockConfig.getString(SoapClientConfig.SERVICE_NAME)).thenReturn(SERVICE);
    when(mockConfig.getString(SoapClientConfig.SOAP_ACTION)).thenReturn(SOAP_ACTION);
    when(mockConfig.getString(SoapClientConfig.TARGET_NAMESPACE))
        .thenReturn(TARGET_NAME_SPACE);
    when(mockConfig.getLong(SoapClientConfig.CONNECTION_TIMEOUT))
        .thenReturn(CONNECTION_TIMEOUT);

    assertDoesNotThrow(() -> classToTest.start(mockConfig));
  }

  @Test
  public void given_not_absolute_path_request_file_when_client_starts_then_config_exception_thrown() {
    when(mockConfig.getString(SoapClientConfig.ENDPOINT_URL)).thenReturn(URL);
    when(mockConfig.getString(SoapClientConfig.PORT_NAME)).thenReturn(PORT_NAME);
    when(mockConfig.getString(SoapClientConfig.REQUEST_MSG_FILE))
        .thenReturn(KO_REQUEST_FILE);
    when(mockConfig.getString(SoapClientConfig.SERVICE_NAME)).thenReturn(SERVICE);
    when(mockConfig.getString(SoapClientConfig.SOAP_ACTION)).thenReturn(SOAP_ACTION);
    when(mockConfig.getString(SoapClientConfig.TARGET_NAMESPACE))
        .thenReturn(TARGET_NAME_SPACE);

    assertThrows(ConfigException.class, () -> classToTest.start(mockConfig));
  }

  @Test
  public void given_ok_poll_interval__when_poll_then_future_complete_ok() throws Exception {

    when(mockTask.call()).thenReturn(mockReturnMessage);
    assertDoesNotThrow(() -> stubbedClassToTest.poll(5000L));
  }

}
