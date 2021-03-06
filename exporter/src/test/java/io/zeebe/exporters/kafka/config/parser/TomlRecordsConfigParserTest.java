/*
 * Copyright © 2019 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.exporters.kafka.config.parser;

import static org.assertj.core.api.Assertions.assertThat;

import io.zeebe.exporters.kafka.config.RecordsConfig;
import io.zeebe.exporters.kafka.config.toml.TomlRecordConfig;
import io.zeebe.exporters.kafka.config.toml.TomlRecordsConfig;
import io.zeebe.exporters.kafka.record.AllowedType;
import io.zeebe.protocol.record.RecordType;
import io.zeebe.protocol.record.ValueType;
import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;

public class TomlRecordsConfigParserTest {
  private static final Set<ValueType> EXPECTED_VALUE_TYPES =
      EnumSet.complementOf(EnumSet.of(ValueType.SBE_UNKNOWN, ValueType.NULL_VAL));

  private final TomlRecordsConfigParser parser = new TomlRecordsConfigParser();

  @Test
  public void shouldParseDefaultsWithDefaultValue() {
    // given
    final TomlRecordsConfig config = new TomlRecordsConfig();

    // when
    final RecordsConfig parsed = parser.parse(config);

    // then
    assertThat(parsed.getDefaults().getAllowedTypes())
        .isEqualTo(TomlRecordsConfigParser.DEFAULT_ALLOWED_TYPES);
    assertThat(parsed.getDefaults().getTopic())
        .isEqualTo(TomlRecordsConfigParser.DEFAULT_TOPIC_NAME);
  }

  @Test
  public void shouldParseRecordConfigUnderCorrectValueType() {
    // given
    final TomlRecordsConfig config = new TomlRecordsConfig();
    config.deployment = newConfigFromType(ValueType.DEPLOYMENT);
    config.error = newConfigFromType(ValueType.ERROR);
    config.incident = newConfigFromType(ValueType.INCIDENT);
    config.job = newConfigFromType(ValueType.JOB);
    config.jobBatch = newConfigFromType(ValueType.JOB_BATCH);
    config.message = newConfigFromType(ValueType.MESSAGE);
    config.messageSubscription = newConfigFromType(ValueType.MESSAGE_SUBSCRIPTION);
    config.messageStartEventSubscription =
        newConfigFromType(ValueType.MESSAGE_START_EVENT_SUBSCRIPTION);
    config.timer = newConfigFromType(ValueType.TIMER);
    config.variable = newConfigFromType(ValueType.VARIABLE);
    config.variableDocument = newConfigFromType(ValueType.VARIABLE_DOCUMENT);
    config.workflowInstance = newConfigFromType(ValueType.WORKFLOW_INSTANCE);
    config.workflowInstanceCreation = newConfigFromType(ValueType.WORKFLOW_INSTANCE_CREATION);
    config.workflowInstanceSubscription =
        newConfigFromType(ValueType.WORKFLOW_INSTANCE_SUBSCRIPTION);

    // when
    final RecordsConfig parsed = parser.parse(config);

    EXPECTED_VALUE_TYPES.remove(ValueType.WORKFLOW_INSTANCE_RESULT);
    // then
    for (final ValueType type : EXPECTED_VALUE_TYPES) {
      assertThat(parsed.forType(type).getTopic()).isEqualTo(type.name());
    }
  }

  @Test
  public void shouldUseDefaultsOnMissingProperties() {
    // given
    final TomlRecordsConfig config = new TomlRecordsConfig();
    config.defaults = new TomlRecordConfig();
    config.defaults.topic = "default";
    config.defaults.type = AllowedType.COMMAND.getTypeName();

    // when
    final RecordsConfig parsed = parser.parse(config);

    // then
    parsed
        .getTypeMap()
        .forEach(
            (t, c) -> {
              assertThat(c.getTopic()).isEqualTo(config.defaults.topic);
              assertThat(c.getAllowedTypes()).containsExactly(RecordType.COMMAND);
            });
  }

  private TomlRecordConfig newConfigFromType(ValueType type) {
    final TomlRecordConfig recordConfig = new TomlRecordConfig();
    recordConfig.topic = type.name();

    return recordConfig;
  }
}
