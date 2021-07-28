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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

class SoapSourceTaskTest {

  private final SoapSourceTask classToTest = new SoapSourceTask();

  @Test
  public void given_valid_config_when_start_task_then_no_exception_thrown() {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createValidMockSettings();
    assertDoesNotThrow(() -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_missing_endpoint_url_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingEndpointUrlSpaceMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_negative_poll_interval_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createKoPollMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_conn_time_out_greater_than_poll_interval_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createKoConnTimeOutMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_missing_port_name_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingPortNameMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_missing_request_file_when_start_task_then_throws_confIg_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingRequestFileMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_ko_request_file_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createKoRequestFileMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_missing_service_name_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingServiceNameMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_missing_target_name_space_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingTargetNameSpaceMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void given_missing_topic_when_start_task_then_exception_thrown() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingTopicMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

}

