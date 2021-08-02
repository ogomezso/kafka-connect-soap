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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

class SoapSourceConnectorTest {

  private final SoapSourceConnector classToTest = new SoapSourceConnector();

  @Test
  public void given_ok_settings_1_mask_task_when_call_task_config_then_return_settings_as_list(){

    Map<String, String> testSettings = SourceTaskSettingMother.createValidMockSettings();
    classToTest.start(testSettings);

    List<Map<String, String>> actual = classToTest.taskConfigs(1);

    assertEquals(Collections.singletonList(testSettings), actual);
  }

  @Test
  public void given_missing_mandatory_settings_1_mask_task_when_call_task_config_then_throws_config_exception(){

    Map<String, String> testSettings = SourceTaskSettingMother.createMissingTopicMockSettings();

    assertThrows(ConfigException.class, () -> classToTest.start(testSettings));
  }

  private void call_task_config_files_tasks_test(int files, int maxTasks) {

    Map<String, String> testSettings = SourceTaskSettingMother.createValidMultiRequestMockSettings(files);
    classToTest.start(testSettings);

    List<Map<String, String>> actual = classToTest.taskConfigs(maxTasks);

    assertEquals(Math.min(files, maxTasks), actual.size());
    actual.forEach(c -> {
      assertTrue(c.containsKey("requestMessageFile")); // TODO use TaskConfig class
      assertTrue(c.get("requestMessageFile").matches("\\/[^\\, \\s]+"));

    });
  }


  @Test
  public void given_multiple_files_settings_1_max_task_when_call_task_config_then_returns_settings_as_list() {
    call_task_config_files_tasks_test(2, 1);
  }

  @Test
  public void given_2_files_settings_3_max_tasks_when_call_task_config_then_returns_settings_as_list() {
    call_task_config_files_tasks_test(2, 3);
  }

  @Test
  public void given_3_files_settings_3_max_tasks_when_call_task_config_then_returns_settings_as_list() {
    call_task_config_files_tasks_test(3, 3);
  }

  @Test
  public void given_10_files_settings_3_max_tasks_when_call_task_config_then_returns_settings_as_list() {
    call_task_config_files_tasks_test(10, 3);
  }

}