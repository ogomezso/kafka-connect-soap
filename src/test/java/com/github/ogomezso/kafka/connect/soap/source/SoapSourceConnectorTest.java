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

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoapSourceConnectorTest {

  private SoapSourceConnector classToTest;

  @BeforeEach
  void setup() {
    classToTest = new SoapSourceConnector();
  }


  @Test
  public void test_1_given_ok_settings_1_mask_task_when_call_task_config_then_return_settings_as_list(){

    Map<String, String> testSettings = SourceTaskSettingMother.createValidMockSettings();
    classToTest.start(testSettings);

    List<Map<String, String>> actual = classToTest.taskConfigs(1);
    assertEquals(Collections.singletonList(testSettings), actual);
  }


  @Test
  public void test_2_given_missing_mandatory_settings_1_mask_task_when_call_task_config_then_throws_config_exception(){

    Map<String, String> testSettings = SourceTaskSettingMother.createMissingTopicMockSettings();

    assertThrows(ConfigException.class, () -> classToTest.start(testSettings));
  }

  private void call_task_config_files_tasks_test(int files, int maxTasks) {

    Map<String, String> testSettings = SourceTaskSettingMother.createValidMultiRequestMockSettings(files);
    classToTest.start(testSettings);

    List<Map<String, String>> actual = classToTest.taskConfigs(maxTasks);

    assertEquals(Math.min(files, maxTasks), actual.size());
    actual.forEach(c -> {
      assertTrue(c.containsKey(SoapSourceTaskConfig.REQUEST_MSG_FILES));
      assertTrue(c.get(SoapSourceTaskConfig.REQUEST_MSG_FILES).matches("^((\\/[^\\, \\s]+)[,\\s]+)*(\\/[^\\, \\s]+)$"));

      for (String s : c.get(SoapSourceTaskConfig.REQUEST_MSG_FILES).split(",")) {
        assertTrue(new File(s.trim()).isAbsolute());
      }

      assertTrue(c.containsKey(SoapSourceTaskConfig.TOPIC));
      assertEquals(1, c.get(SoapSourceTaskConfig.TOPIC).split(",").length);

    });
  }

  @Test
  public void test_3_given_1_file_1_maxtask_check_settings_as_list() {
    call_task_config_files_tasks_test(1, 1);
  }

  @Test
  public void test_4_given_2_file_2_maxtask_check_settings_as_list() {
    call_task_config_files_tasks_test(2, 2);
  }

  @Test
  public void test_5_given_1_file_2_maxtask_check_settings_as_list() {
    call_task_config_files_tasks_test(1, 2);
  }

  @Test
  public void test_6_given_2_file_1_maxtask_check_settings_as_list() {
    call_task_config_files_tasks_test(2, 1);
  }

  @Test
  public void test_7_given_multiple_files_settings_check_settings_as_list() {

    int maxFiles = 10;
    int maxTasks = 10;

    for (int files = 1; files <= maxFiles; files ++) {
      for (int tasks = 1; tasks <= maxTasks; tasks++) {
        this.classToTest = new SoapSourceConnector();
        call_task_config_files_tasks_test(files, tasks);
      }
    }
  }

  private void CUSTOM_ASSIGNMENT_call_task_config_files_tasks_test(int files, int maxTasks) {

    Map<String, String> testSettings = SourceTaskSettingMother.createValidMultiRequestMockSettings(files, "CUSTOM_ASSIGNMENT");
    classToTest.start(testSettings);

    List<Map<String, String>> actual = classToTest.taskConfigs(maxTasks);

    assertEquals(Math.min(files, maxTasks), actual.size());
    actual.forEach(c -> {
      assertTrue(c.containsKey(SoapSourceTaskConfig.REQUEST_MSG_FILES));
      assertTrue(c.get(SoapSourceTaskConfig.REQUEST_MSG_FILES).matches("^((\\/[^\\, \\s]+)[,\\s]+)*(\\/[^\\, \\s]+)$"));

      for (String s : c.get(SoapSourceTaskConfig.REQUEST_MSG_FILES).split(",")) {
        File f = new File(s.trim());
        assertTrue(f.isAbsolute());
      }
      assertTrue(c.containsKey(SoapSourceTaskConfig.TOPIC));
      assertEquals(c.get(SoapSourceTaskConfig.REQUEST_MSG_FILES).split(",").length,
          c.get(SoapSourceTaskConfig.TOPIC).split(",").length);
    });
  }

  @Test
  public void test_8_given_custom_assignment_1file_1task_check_returns_settings() {
    CUSTOM_ASSIGNMENT_call_task_config_files_tasks_test(1, 1);
  }

  @Test
  public void test_9_given_custom_assignment_2file_2task_check_returns_settings() {
    CUSTOM_ASSIGNMENT_call_task_config_files_tasks_test(2, 2);
  }

  @Test
  public void test_10_given_custom_assignment_1file_2task_check_returns_settings() {
    CUSTOM_ASSIGNMENT_call_task_config_files_tasks_test(1, 2);
  }

  @Test
  public void test_11_given_custom_assignment_2file_1task_check_returns_settings() {
    CUSTOM_ASSIGNMENT_call_task_config_files_tasks_test(2, 1);
  }

  @Test
  public void test_12_given_custom_assignment_check_returns_settings() {

    int maxFiles = 10;
    int maxTasks = 10;

    for (int files = 1; files <= maxFiles; files ++) {
      for (int tasks = 1; tasks <= maxTasks; tasks++) {
        this.classToTest = new SoapSourceConnector();
        CUSTOM_ASSIGNMENT_call_task_config_files_tasks_test(files, tasks);
      }
    }
  }
}
