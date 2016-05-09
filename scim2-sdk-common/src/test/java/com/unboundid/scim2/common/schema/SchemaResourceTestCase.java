/*
 * Copyright 2015-2016 UnboundID Corp.
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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
    String schemaJson = "{\n" +
        "  \"schemas\": [\n" +
        "    \"urn:ietf:params:scim:schemas:core:2.0:Schema\"\n" +
        "  ],\n" +
        "  \"meta\": {\n" +
        "    \"resourceType\": \"Schema\",\n" +
        "    \"location\": \"https://example.comscim/v2/Schemas/" +
        "urn:unboundid:schemas:exampleSchema\"\n" +
        "  },\n" +
        "  \"id\": \"urn:unboundid:schemas:exampleSchema\",\n" +
        "  \"name\": \"Example\",\n" +
        "  \"description\": \"Example schema\",\n" +
        "  \"attributes\": [\n" +
        "    {\n" +
        "      \"name\": \"string\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A String attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },    \n" +
        "    {\n" +
        "      \"name\": \"boolean\",\n" +
        "      \"type\": \"boolean\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A Boolean attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"decimal\",\n" +
        "      \"type\": \"decimal\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A Decimal attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"integer\",\n" +
        "      \"type\": \"integer\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An Integer attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"dateTime\",\n" +
        "      \"type\": \"dateTime\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A DateTime attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"binary\",\n" +
        "      \"type\": \"binary\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A Binary attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"reference\",\n" +
        "      \"type\": \"reference\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A Reference attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },    \n" +
        "    {\n" +
        "      \"name\": \"readOnlyAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A read-only attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readOnly\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"writeOnlyAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A write-only attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"writeOnly\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"readWriteAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A ReadWrite attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"immutableAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An Immutable attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"immutable\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"returnedAlwaysAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with returned=always.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"always\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"returnedNeverAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with returned=never.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"never\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"returnedDefaultAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with returned=default.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"returnedRequestAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with returned=request.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"request\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"uniquenessNoneAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with uniqueness=none.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"uniquenessServerAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with uniqueness=server.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"server\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"uniquenessGlobalAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with uniqueness=global.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"global\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"complex\",\n" +
        "      \"type\": \"complex\",\n" +
        "      \"subAttributes\": [\n" +
        "        {\n" +
        "          \"name\": \"subAttr1\",\n" +
        "          \"type\": \"string\",\n" +
        "          \"multiValued\": false,\n" +
        "          \"description\": \"A sub-attribute.\",\n" +
        "          \"required\": false,\n" +
        "          \"caseExact\": false,\n" +
        "          \"mutability\": \"readWrite\",\n" +
        "          \"returned\": \"default\",\n" +
        "          \"uniqueness\": \"none\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"name\": \"subAttr2\",\n" +
        "          \"type\": \"string\",\n" +
        "          \"multiValued\": false,\n" +
        "          \"description\": \"Another sub-attribute.\",\n" +
        "          \"required\": false,\n" +
        "          \"caseExact\": false,\n" +
        "          \"mutability\": \"readWrite\",\n" +
        "          \"returned\": \"default\",\n" +
        "          \"uniqueness\": \"none\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A complex single-valued attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"complexMultiValued\",\n" +
        "      \"type\": \"complex\",\n" +
        "      \"subAttributes\": [\n" +
        "        {\n" +
        "          \"name\": \"subAttr1\",\n" +
        "          \"type\": \"string\",\n" +
        "          \"multiValued\": false,\n" +
        "          \"description\": \"A sub-attribute.\",\n" +
        "          \"required\": false,\n" +
        "          \"caseExact\": false,\n" +
        "          \"mutability\": \"readWrite\",\n" +
        "          \"returned\": \"default\",\n" +
        "          \"uniqueness\": \"none\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"name\": \"type\",\n" +
        "          \"type\": \"string\",\n" +
        "          \"multiValued\": false,\n" +
        "          \"description\": \"A sub-attribute with " +
        "canonical values.\",\n" +
        "          \"required\": false,\n" +
        "          \"canonicalValues\": [\n" +
        "            \"one\",\n" +
        "            \"two\",\n" +
        "            \"three\"\n" +
        "          ],\n" +
        "          \"caseExact\": false,\n" +
        "          \"mutability\": \"readWrite\",\n" +
        "          \"returned\": \"default\",\n" +
        "          \"uniqueness\": \"none\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"multiValued\": true,\n" +
        "      \"description\": \"A complex multi-valued attribute.\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"wrongTypeCaseAttr\",\n" +
        "      \"type\": \"datetime\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"A DateTime attribute with type=datetime " +
        "(instead of 'dateTime').\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readWrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"wrongMutabilityCaseAttr\",\n" +
        "      \"type\": \"string\",\n" +
        "      \"multiValued\": false,\n" +
        "      \"description\": \"An attribute with mutability=readwrite " +
        "(instead of 'readWrite').\",\n" +
        "      \"required\": false,\n" +
        "      \"caseExact\": false,\n" +
        "      \"mutability\": \"readwrite\",\n" +
        "      \"returned\": \"default\",\n" +
        "      \"uniqueness\": \"none\"\n" +
        "    }\n" +
        "  ]\n" +
        "}";
    return JsonUtils.getObjectReader().forType(SchemaResource.class).readValue(
        schemaJson);
  }

  private AttributeDefinition getAttributeDefinition(
      Collection<AttributeDefinition> attributes, final String attributeName)
  {
    return Iterables.find(
        attributes,
        new Predicate<AttributeDefinition>()
        {
          public boolean apply(AttributeDefinition attributeDefinition)
          {
            return attributeDefinition.getName()
                .equalsIgnoreCase(attributeName);
          }
        }
    );
  }
}
