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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Test case for the schema aware filter evaluator.
 */
public class SchemaAwareFilterEvaluatorTestCase
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
    builder.setName("insensitive");
    builder.setType(AttributeDefinition.Type.STRING);
    builder.setCaseExact(false);
    attributeDefinitions.add(builder.build());

    builder.setName("sensitive");
    builder.setCaseExact(true);
    attributeDefinitions.add(builder.build());

    SchemaResource schema =
        new SchemaResource("test", "test", "test", attributeDefinitions);

    resourceTypeDefinition = new ResourceTypeDefinition.Builder(
        "test", "test").setCoreSchema(schema).build();

    ObjectNode node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{  \n" +
                "  \"id\":\"test\",\n" +
                "  \"insensitive\":\"HeRe\",\n" +
                "  \"sensitive\":\"hErE\"\n" +
                "}");
    testResource = new GenericScimResource(node);
  }

  /**
   * Retrieves a set of valid filter strings.
   *
   * @return  A set of valid filter strings.
   */
  @DataProvider(name = "testValidFilterStrings")
  public Object[][] getTestValidFilterStrings()
  {
    return new Object[][]
        {
            new Object[] { "insensitive eq \"here\"", true },
            new Object[] { "insensitive eq \"HERE\"", true },
            new Object[] { "insensitive eq \"HeRe\"", true },
            new Object[] { "insensitive eq \"nothere\"", false },
            new Object[] { "sensitive eq \"here\"", false },
            new Object[] { "sensitive eq \"HERE\"", false },
            new Object[] { "sensitive eq \"hErE\"", true },
            new Object[] { "sensitive eq \"nothere\"", false }
        };
  }

  /**
   * Test that filters matching.
   *
   * @param filter The filter string to evaluate.
   * @param result The expected result.
   * @throws ScimException If the filter string is invalid.
   */
  @Test(dataProvider = "getTestValidFilterStrings")
  public void testFilter(String filter, boolean result)
      throws ScimException
  {
    SchemaAwareFilterEvaluator filterEvaluator =
        new SchemaAwareFilterEvaluator(resourceTypeDefinition);
    boolean actualResult = Filter.fromString(filter).visit(
        filterEvaluator, testResource.getObjectNode());
    assertEquals(actualResult, result);
  }
}
