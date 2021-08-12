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

import com.github.ogomezso.kafka.connect.soap.client.SoapClientConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoapSourceTaskTest {

  private SoapSourceTask classToTest;

  @BeforeEach
  void setup() {
    classToTest = new SoapSourceTask();
  }

  @Test
  public void test_1_given_valid_config_when_start_task_then_no_exception_thrown() {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createValidMockSettings();
    assertDoesNotThrow(() -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_2_given_missing_endpoint_url_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingEndpointUrlSpaceMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_3_given_negative_poll_interval_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createKoPollMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_4_given_conn_time_out_greater_than_poll_interval_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createKoConnTimeOutMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_5_given_missing_port_name_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingPortNameMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_6_given_missing_request_file_when_start_task_then_throws_confIg_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingRequestFileMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_7_given_ko_request_file_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createKoRequestFileMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_8_given_missing_service_name_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingServiceNameMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_9_given_missing_target_name_space_when_start_task_then_throws_config_exception() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingTargetNameSpaceMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  @Test
  public void test_10_given_missing_topic_when_start_task_then_exception_thrown() {
    Map<String, String> mockedSettings = SourceTaskSettingMother
        .createMissingTopicMockSettings();
    assertThrows(ConfigException.class, () -> classToTest.start(mockedSettings));
  }

  // test generated client configurations

  @Test
  public void test_11_given_ok_task_settings_validate_client_configs() {

    String[] topicGenStrategies = {"", "ONE_TOPIC", "CUSTOM_ASSIGNMENT", "TOPIC_PER_REQUEST"};
    int maxRequestFiles = 10;

    for (String topicGenStrategy : topicGenStrategies) {
      for (int files = 1; files <= maxRequestFiles; files++) {
        this.classToTest = new SoapSourceTask();
        validateClientConfigs(files, topicGenStrategy);
      }
    }
  }

  private void validateClientConfigs(int files, String assignmentStrategy) {
    Map<String, String> mockedSettings = SourceTaskSettingMother.createValidMultiRequestMockTaskSettings(files,
        assignmentStrategy);
    List<SoapClientConfig> actual = classToTest.test_validateAndsSetConfigVars(mockedSettings);

    String[] mockedFiles = mockedSettings.get(SoapSourceTaskConfig.REQUEST_MSG_FILES).split(",");
    String[] mockedTopics = mockedSettings.get(SoapSourceTaskConfig.TOPIC).split(",");
    Arrays.setAll(mockedFiles, i -> mockedFiles[i].trim());
    Arrays.setAll(mockedTopics, i -> mockedTopics[i].trim());

    assertEquals(mockedFiles.length, actual.size());

    for (int i = 0; i < actual.size(); i++) {
      assertFalse(actual.get(i).getString(SoapClientConfig.REQUEST_MSG_FILE).isEmpty());
      assertEquals(mockedFiles[i], actual.get(i).getString(SoapClientConfig.REQUEST_MSG_FILE));
      assertTrue(new File(actual.get(i).getString(SoapClientConfig.REQUEST_MSG_FILE)).isAbsolute());

      assertFalse(actual.get(i).getString(SoapClientConfig.TOPIC).isEmpty());
      assertEquals(1, actual.get(i).getString(SoapClientConfig.TOPIC).split(",").length);

      switch (assignmentStrategy) {
        case "":
        case "TOPIC_PER_REQUEST":
          assertEquals(
              ObjectUtils.firstNonNull(mockedSettings.get(SoapSourceTaskConfig.TOPIC_PREFIX), "")
                  + ObjectUtils.firstNonNull(mockedSettings.get(SoapSourceTaskConfig.TOPIC), "")
                  + new File(mockedFiles[i]).getName(),
              actual.get(i).getString(SoapClientConfig.TOPIC)
              );
          assertEquals(mockedFiles[i], actual.get(i).getString(SoapClientConfig.REQUEST_MSG_FILE));
          break;
        case "ONE_TOPIC":
          assertEquals(mockedSettings.get(SoapSourceTaskConfig.TOPIC), actual.get(i).getString(SoapClientConfig.TOPIC));
          assertEquals(mockedFiles[i], actual.get(i).getString(SoapClientConfig.REQUEST_MSG_FILE));
          break;
        case "CUSTOM_ASSIGNMENT":
          assertEquals(mockedTopics[i], actual.get(i).getString(SoapClientConfig.TOPIC));
          assertEquals(mockedFiles[i], actual.get(i).getString(SoapClientConfig.REQUEST_MSG_FILE));
          break;
        default:
          throw new ConfigException("Unknown assignment strategy");
      }
    }
  }
}
