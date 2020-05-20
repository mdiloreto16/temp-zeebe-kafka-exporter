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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.zeebe.exporters.kafka.config.RecordConfig;
import io.zeebe.exporters.kafka.config.toml.TomlRecordConfig;
import io.zeebe.exporters.kafka.record.AllowedType;
import io.zeebe.protocol.record.RecordType;
import org.junit.Test;

public class TomlRecordConfigParserTest {
  private final TomlRecordConfigParser parser = new TomlRecordConfigParser();

  @Test
  public void shouldParseAllowedTypes() {
    // given
    final TomlRecordConfig config = new TomlRecordConfig();
    config.type = AllowedType.EVENT.getTypeName();

    // when
    final RecordConfig parsed = parser.parse(config);

    // then
    assertThat(parsed.getAllowedTypes()).containsExactlyInAnyOrder(RecordType.EVENT);
  }

  @Test
  public void shouldParseTopic() {
    // given
    final TomlRecordConfig config = new TomlRecordConfig();
    config.topic = "something";

    // when
    final RecordConfig parsed = parser.parse(config);

    // then
    assertThat(parsed.getTopic()).isEqualTo("something");
  }

  @Test
  public void shouldNotSetAnythingIfNull() {
    // given
    final TomlRecordConfig config = new TomlRecordConfig();

    // when
    final RecordConfig parsed = parser.parse(config);

    // then
    assertThat(parsed.getTopic()).isNull();
    assertThat(parsed.getAllowedTypes()).isNull();
  }

  @Test
  public void shouldThrowExceptionIfAllowedTypeIsUnknown() {
    // given
    final TomlRecordConfig config = new TomlRecordConfig();
    config.type = "something unlikely";

    // when - then
    assertThatThrownBy(() -> parser.parse(config)).isInstanceOf(IllegalArgumentException.class);
  }
}
