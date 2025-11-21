/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.schema;

import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * Some basic tests for serializing and de-serializing of a schema resource.
 */
public class SchemaResourceTestCase
{
  /**
   * Test de-serializing a Schema resource. This simply assures that a JSON
   * representation of a SCIM Schema can be de-serialized and that basic fields
   * will be populated.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testBasicSchemaProperties() throws Exception
  {
    SchemaResource schemaResource = schemaResource();

    assertNotNull(schemaResource.getId());
    assertNotNull(schemaResource.getName());
    assertNotNull(schemaResource.getDescription());
    assertNotNull(schemaResource.getSchemaUrns());
    assertFalse(schemaResource.getSchemaUrns().isEmpty());

    Collection<AttributeDefinition> attributeDefinitions =
        schemaResource.getAttributes();
    assertNotNull(attributeDefinitions);
    assertFalse(attributeDefinitions.isEmpty());

    for (AttributeDefinition attributeDefinition : attributeDefinitions)
    {
      assertNotNull(attributeDefinition.getName());
      assertNotNull(attributeDefinition.getDescription());
      assertNotNull(attributeDefinition.getType());
      assertNotNull(attributeDefinition.getMutability());
      assertNotNull(attributeDefinition.getReturned());
      assertNotNull(attributeDefinition.getUniqueness());
    }
  }

  /**
   * Test that ensures that the types of attribute definitions are correct
   * after de-serializing a SCIM Schema resource from its JSON representation.
   *
   * @throws Exception if an error occurs.
   */
  @Test(dependsOnMethods = "testBasicSchemaProperties")
  public void testAttributeDefinitionTypes() throws Exception
  {
    Collection<AttributeDefinition> attributes =
        schemaResource().getAttributes();
    for (AttributeDefinition.Type type : AttributeDefinition.Type.values())
    {
      AttributeDefinition attributeDefinition = null;
      try
      {
        attributeDefinition =
            getAttributeDefinition(attributes, type.getName());
      }
      catch (NoSuchElementException e)
      {
        fail("failed to find attribute definition '" + type.getName() + "'");
      }
      assertEquals(attributeDefinition.getType(), type);
    }
  }

  /**
   * Test that ensures that a SCIM Schema resource can be successfully
   * de-serialized from JSON if an attribute definition contains an incorrectly
   * cased type or mutability value.
   *
   * @throws Exception if an error occurs.
   */
  @Test(dependsOnMethods = "testBasicSchemaProperties")
  public void testCaseInsensitivity() throws Exception
  {
    Collection<AttributeDefinition> attributes =
        schemaResource().getAttributes();

    AttributeDefinition wrongTypeCaseAttr =
        getAttributeDefinition(attributes, "wrongTypeCaseAttr");
    assertEquals(wrongTypeCaseAttr.getType(),
                 AttributeDefinition.Type.DATETIME);

    AttributeDefinition wrongMutabilityCaseAttr =
        getAttributeDefinition(attributes, "wrongMutabilityCaseAttr");
    assertEquals(wrongMutabilityCaseAttr.getMutability(),
                 AttributeDefinition.Mutability.READ_WRITE);
  }

  private SchemaResource schemaResource() throws IOException
  {
    String schemaJson = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Schema"
          ],
          "meta": {
            "resourceType": "Schema",
            "location": "https://example.com/scim/v2/Schemas/urn:pingidentity:schemas:exampleSchema"
          },
          "id": "urn:pingidentity:schemas:exampleSchema",
          "name": "Example",
          "description": "Example schema",
          "attributes": [
            {
              "name": "string",
              "type": "string",
              "multiValued": false,
              "description": "A String attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "boolean",
              "type": "boolean",
              "multiValued": false,
              "description": "A Boolean attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "decimal",
              "type": "decimal",
              "multiValued": false,
              "description": "A Decimal attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "integer",
              "type": "integer",
              "multiValued": false,
              "description": "An Integer attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "dateTime",
              "type": "dateTime",
              "multiValued": false,
              "description": "A DateTime attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "binary",
              "type": "binary",
              "multiValued": false,
              "description": "A Binary attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "reference",
              "type": "reference",
              "multiValued": false,
              "description": "A Reference attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "readOnlyAttr",
              "type": "string",
              "multiValued": false,
              "description": "A read-only attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readOnly",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "writeOnlyAttr",
              "type": "string",
              "multiValued": false,
              "description": "A write-only attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "writeOnly",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "readWriteAttr",
              "type": "string",
              "multiValued": false,
              "description": "A ReadWrite attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "immutableAttr",
              "type": "string",
              "multiValued": false,
              "description": "An Immutable attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "immutable",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "returnedAlwaysAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with returned=always.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "always",
              "uniqueness": "none"
            },
            {
              "name": "returnedNeverAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with returned=never.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "never",
              "uniqueness": "none"
            },
            {
              "name": "returnedDefaultAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with returned=default.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "returnedRequestAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with returned=request.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "request",
              "uniqueness": "none"
            },
            {
              "name": "uniquenessNoneAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with uniqueness=none.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "uniquenessServerAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with uniqueness=server.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "server"
            },
            {
              "name": "uniquenessGlobalAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with uniqueness=global.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "global"
            },
            {
              "name": "complex",
              "type": "complex",
              "subAttributes": [
                {
                  "name": "subAttr1",
                  "type": "string",
                  "multiValued": false,
                  "description": "A sub-attribute.",
                  "required": false,
                  "caseExact": false,
                  "mutability": "readWrite",
                  "returned": "default",
                  "uniqueness": "none"
                },
                {
                  "name": "subAttr2",
                  "type": "string",
                  "multiValued": false,
                  "description": "Another sub-attribute.",
                  "required": false,
                  "caseExact": false,
                  "mutability": "readWrite",
                  "returned": "default",
                  "uniqueness": "none"
                }
              ],
              "multiValued": false,
              "description": "A complex single-valued attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "complexMultiValued",
              "type": "complex",
              "subAttributes": [
                {
                  "name": "subAttr1",
                  "type": "string",
                  "multiValued": false,
                  "description": "A sub-attribute.",
                  "required": false,
                  "caseExact": false,
                  "mutability": "readWrite",
                  "returned": "default",
                  "uniqueness": "none"
                },
                {
                  "name": "type",
                  "type": "string",
                  "multiValued": false,
                  "description": "A sub-attribute with \
        canonical values.",
                  "required": false,
                  "canonicalValues": [
                    "one",
                    "two",
                    "three"
                  ],
                  "caseExact": false,
                  "mutability": "readWrite",
                  "returned": "default",
                  "uniqueness": "none"
                }
              ],
              "multiValued": true,
              "description": "A complex multi-valued attribute.",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "wrongTypeCaseAttr",
              "type": "datetime",
              "multiValued": false,
              "description": "A DateTime attribute with type=datetime \
        (instead of 'dateTime').",
              "required": false,
              "caseExact": false,
              "mutability": "readWrite",
              "returned": "default",
              "uniqueness": "none"
            },
            {
              "name": "wrongMutabilityCaseAttr",
              "type": "string",
              "multiValued": false,
              "description": "An attribute with mutability=readwrite \
        (instead of 'readWrite').",
              "required": false,
              "caseExact": false,
              "mutability": "readwrite",
              "returned": "default",
              "uniqueness": "none"
            }
          ]
        }""";
    return JsonUtils.getObjectReader().forType(SchemaResource.class).readValue(
        schemaJson);
  }

  private AttributeDefinition getAttributeDefinition(
      Collection<AttributeDefinition> attributes, final String attributeName)
  {
    return attributes.stream().filter(
        attributeDef -> attributeDef.getName().equalsIgnoreCase(attributeName))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }
}
