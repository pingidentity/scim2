/*
 * Copyright 2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2026 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Tests for {@link AttributeDefinition}.
 */
public class AttributeDefinitionTest
{
  /**
   * Test default attribute values when a minimal builder object is built.
   */
  @Test
  public void testBuilderDefaults()
  {
    AttributeDefinition definition = new AttributeDefinition.Builder()
        .setName("Initial").build();

    assertThat(definition.getName()).isEqualTo("Initial");
    assertThat(definition.getType()).isEqualTo(AttributeDefinition.Type.STRING);
    assertThat(definition.isMultiValued()).isFalse();
    assertThat(definition.getMutability())
        .isEqualTo(AttributeDefinition.Mutability.READ_WRITE);
    assertThat(definition.getReturned())
        .isEqualTo(AttributeDefinition.Returned.DEFAULT);
    assertThat(definition.getUniqueness())
        .isEqualTo(AttributeDefinition.Uniqueness.NONE);
    assertThat(definition.isRequired()).isFalse();
    assertThat(definition.isCaseExact()).isFalse();
  }

  /**
   * Test serialization of an AttributeDefinition.
   */
  @Test
  public void testAttributeSerialization()
  {
    var reader = JsonUtils.getObjectReader().forType(AttributeDefinition.class);
    String json = """
        {
          "name": "customCert",
          "type": "binary",
          "multiValued": false,
          "description": "pictures",
          "required": false,
          "caseExact": false,
          "mutability": "readWrite",
          "returned": "request",
          "uniqueness": "none"
        }""";

    AttributeDefinition deserialized = reader.readValue(json);
    AttributeDefinition.Builder builder = new AttributeDefinition.Builder()
        .setName("customCert")
        .setType(AttributeDefinition.Type.BINARY)
        .setMultiValued(false)
        .setDescription("pictures")
        .setRequired(false)
        .setMutability(AttributeDefinition.Mutability.READ_WRITE)
        .setReturned(AttributeDefinition.Returned.REQUEST)
        .setUniqueness(AttributeDefinition.Uniqueness.NONE)
        .addCanonicalValues((String[]) null)
        .addReferenceTypes((String[]) null)
        .setCaseExact(false);

    AttributeDefinition expected = builder.build();
    assertThat(deserialized).isEqualTo(expected);

    // Reformat the source JSON and validate that a serialized
    // AttributeDefinition results in an expected JSON string.
    String expectedJSON = JsonUtils.getObjectReader().readTree(json).toString();
    String reserialized = JsonUtils.valueToNode(deserialized).toString();
    assertThat(reserialized).isEqualTo(expectedJSON);

    // Ensure that non-required values may be omitted from the source JSON but
    // still populated on deserialization.
    String minimalJson = """
        {
          "name": "attrName",
          "multiValued": false,
          "pattern": "[a-z]+"
        }""";

    AttributeDefinition deserializedMinimal = reader.readValue(minimalJson);
    AttributeDefinition expectedMinimal = builder.clear().setName("attrName")
        .setType(AttributeDefinition.Type.STRING)
        .setMultiValued(false)
        .setRequired(false)
        .setMutability(AttributeDefinition.Mutability.READ_WRITE)
        .setReturned(AttributeDefinition.Returned.DEFAULT)
        .setUniqueness(AttributeDefinition.Uniqueness.NONE)
        .setCaseExact(false)
        .setPattern("[a-z]+")
        .build();

    assertThat(deserializedMinimal).isEqualTo(expectedMinimal);

    // Validate required fields.
    String noName = """
        {
          "multiValued": false
        }""";
    assertThatThrownBy(() -> reader.readValue(noName))
        .isInstanceOf(JacksonException.class);
    String noMultiValued = """
        {
          "name": "value"
        }""";
    assertThatThrownBy(() -> reader.readValue(noMultiValued))
        .isInstanceOf(JacksonException.class);

    // Attributes cannot use patterns on non-string types.
    String invalidPattern = """
        {
          "name": "attrName",
          "type": "boolean",
          "multiValued": false,
          "pattern": "*"
        }""";
    assertThatThrownBy(() -> reader.readValue(invalidPattern))
        .isInstanceOf(JacksonException.class)
        .hasMessageContaining("Cannot set the 'pattern' of an attribute for")
        .hasMessageContaining("non-string types");

  }

  /**
   * Tests that invalid inputs to the {@code fromName()} methods are reported.
   */
  @Test
  public void testInvalidStringEnums()
  {
    assertThatThrownBy(() -> AttributeDefinition.Mutability.fromName("unknown"))
        .isInstanceOf(BadRequestException.class);
    assertThatThrownBy(() -> AttributeDefinition.Returned.fromName("unknown"))
        .isInstanceOf(BadRequestException.class);
    assertThatThrownBy(() -> AttributeDefinition.Uniqueness.fromName("unknown"))
        .isInstanceOf(BadRequestException.class);

    // For backward compatibility reasons, this is left as a RuntimeException.
    assertThatThrownBy(() -> AttributeDefinition.Type.fromName("unknown"))
        .isInstanceOf(RuntimeException.class);
  }
}
