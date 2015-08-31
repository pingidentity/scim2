/*
 * Copyright 2015 UnboundID Corp.
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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

/**
 * Test coverage for ObligationResourceTrimmer.
 */
public class ObligationResourceTrimmerTestCase
{
  private ResourceTypeDefinition resourceTypeDefinition;
  private GenericScimResource testResource;

  /**
   * Setup a resource def with a test schema.
   *
   * @throws Exception If an error occurs.
   */
  @BeforeClass
  public void setUp() throws Exception
  {
    List<AttributeDefinition> attributeDefinitions =
        new ArrayList<AttributeDefinition>(4);

    AttributeDefinition.Builder builder = new AttributeDefinition.Builder();
    builder.setName("a");
    builder.setType(AttributeDefinition.Type.STRING);
    attributeDefinitions.add(builder.build());

    builder.setName("b");
    attributeDefinitions.add(builder.build());

    builder.setName("c");
    attributeDefinitions.add(builder.build());

    builder.setName("d");
    attributeDefinitions.add(builder.build());

    SchemaResource schema =
        new SchemaResource("urn:test", "test", "test", attributeDefinitions);

    resourceTypeDefinition = new ResourceTypeDefinition.Builder(
        "test", "test").setCoreSchema(schema).build();

    ObjectNode node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{  \n" +
                "  \"id\":\"test\",\n" +
                "  \"a\":\"here\",\n" +
                "  \"B\":\"here\",\n" +
                "  \"c\":\"here\",\n" +
                "  \"d\":\"here\"\n" +
                "}");
    testResource = new GenericScimResource(node);
  }

  /**
   * Test combination data provider.
   *
   * @return The combinations of includeAttributes/excludeAttributes to test.
   */
  @DataProvider
  public Object[][] dataProvider()
  {
    return new Object[][]
        {
            new Object[] { null, null,
                           asList("id", "a", "B", "c", "d") },
            new Object[] { null, asList("a", "d", "x"),
                           asList("id", "B", "c") },
            new Object[] { asList("b", "C", "x"), null,
                           asList("B", "c") },
            new Object[] { asList("urn:test:a", "urn:test:b", "urn:test:X"),
                           asList("urn:test:A", "urn:test:C", "urn:test:Y"),
                           Collections.singletonList("B")},
        };
  }

  /**
   * Test trimming.
   *
   * @param includeAttributes The value to test as includeAttributes
   * @param excludeAttributes the value to test as excludeAttributes.
   * @param expectedAttributes the set of attributes to expect in the result.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testTrim(List<String> includeAttributes,
                       List<String> excludeAttributes,
                       List<String> expectedAttributes)
      throws BadRequestException
  {
    ResourceTrimmer trimmer = new
        ObligationResourceTrimmer(resourceTypeDefinition,
                                  includeAttributes, excludeAttributes);

    ObjectNode trimmed =
        trimmer.trimObjectNode(testResource.getObjectNode(), Path.root());

    for (String a : expectedAttributes)
    {
      assertTrue(trimmed.has(a));

    }

    assertEquals(trimmed.size(), expectedAttributes.size(),
                 "wrong number of attributes in trimmed object");
  }
}
