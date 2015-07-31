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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.utils.FilterEvaluator;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;

/**
 * Tests for evaluating SCIM 2 filters.
 */
public class FilterEvaluatorTestCase
{
  private JsonNode node;
  private Date date;

  /**
   * Sets up the test by creating a new JsonNode.
   *
   * @throws IOException if an unexpected JSON parsing error occurs.
   */
  @BeforeClass
  public void setup() throws IOException
  {
    date = new Date();

    node = JsonUtils.getObjectReader().
        readTree("{\n" +
            "    \"externalId\": \"user:externalId\",\n" +
            "    \"id\": \"user:id\",\n" +
            "    \"meta\": {\n" +
            "        \"created\": \"" +
            ISO8601Utils.format(date, true) + "\",\n" +
            "        \"lastModified\": \"2015-02-27T11:29:39Z\",\n" +
            "        \"location\": \"http://here/user\",\n" +
            "        \"resourceType\": \"some resource type\",\n" +
            "        \"version\": \"1.0\"\n" +
            "    },\n" +
            "    \"name\": {\n" +
            "        \"first\": \"name:first\",\n" +
            "        \"last\": \"name:last\",\n" +
            "        \"middle\": \"name:middle\"\n" +
            "    },\n" +
            "    \"shoeSize\" : \"12W\",\n" +
            "    \"weight\" : 175.6,\n" +
            "    \"children\" : 5,\n" +
            "    \"true\" : true,\n" +
            "    \"false\" : false,\n" +
            "    \"null\" : null,\n" +
            "    \"empty\" : [],\n" +
            "    \"addresses\": [\n" +
            "      {\n" +
            "        \"type\": \"work\",\n" +
            "        \"streetAddress\": \"100 Universal City Plaza\",\n" +
            "        \"locality\": \"Hollywood\",\n" +
            "        \"region\": \"CA\",\n" +
            "        \"postalCode\": \"91608\",\n" +
            "        \"priority\": 0,\n" +
            "        \"country\": \"USA\",\n" +
            "        \"formatted\": \"100 Universal City Plaza\\n" +
            "Hollywood, CA 91608 USA\",\n" +
            "        \"primary\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"home\",\n" +
            "        \"streetAddress\": \"456 Hollywood Blvd\",\n" +
            "        \"locality\": \"Hollywood\",\n" +
            "        \"region\": \"CA\",\n" +
            "        \"postalCode\": \"91608\",\n" +
            "        \"priority\": 10,\n" +
            "        \"country\": \"USA\",\n" +
            "        \"formatted\": \"456 Hollywood Blvd\\n" +
            "Hollywood, CA 91608 USA\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"password\": \"user:password\",\n" +
            "    \"schemas\": [" +
            "    \"urn:unboundid:schemas:baseSchema\", " +
            "    \"urn:unboundid:schemas:favoriteColor\"" +
            "    ],\n" +
            "    \"urn:unboundid:schemas:favoriteColor\": {\n" +
            "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
            "    },\n" +
            "    \"userName\": \"user:username\"\n" +
            "}");
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
            new Object[] { "name.first eq \"nAme:fiRst\"", true },
            new Object[] { "naMe.fIrst ne \"nAme:fiRst\"", false },
            new Object[] { "null eq null", true },
            new Object[] { "unassigned eq null", true },
            new Object[] { "empty eq null", true },
            new Object[] { "null ne null", false },
            new Object[] { "unassigned ne null", false },
            new Object[] { "empty ne null", false },
            new Object[] { "name.first co \"nAme:fiRst\"", true },
            new Object[] { "name.first sw \"nAme:fiRst\"", true },
            new Object[] { "naMe.First ew \"nAme:fiRst\"", true },
            new Object[] { "name.first sw \"nAme:\"", true },
            new Object[] { "name.first ew \":fiRst\"", true },
            new Object[] { "weight gt 175.2", true },
            new Object[] { "weight gt 175", true },
            new Object[] { "weight gt 175.6", false },
            new Object[] { "weight ge 175.6", true },
            new Object[] { "weight ge 175", true },
            new Object[] { "Weight lt 175.8", true },
            new Object[] { "weight lt 176", true },
            new Object[] { "weight lt 175.6", false },
            new Object[] { "weight le 175.6", true },
            new Object[] { "weight le 176", true },
            new Object[] { "children gt 4.5", true },
            new Object[] { "children gt 4", true },
            new Object[] { "children gt 5", false },
            new Object[] { "children ge 5", true },
            new Object[] { "children ge 4", true },
            new Object[] { "Children lt 5.5", true },
            new Object[] { "children lt 6", true },
            new Object[] { "children lt 5", false },
            new Object[] { "children le 5", true },
            new Object[] { "children le 6", true },
            new Object[] { "children pr", true },
            new Object[] { "null pr", false },
            new Object[] { "unassigned pr", false },
            new Object[] { "empty pr", false },
            new Object[] { "true eq true and false eq false", true },
            new Object[] { "true eq true and true eq false", false },
            new Object[] { "true eq true or false eq false", true },
            new Object[] { "true eq true or true eq false", true },
            new Object[] { "not(true eq true)", false },
            new Object[] { "not(true eq false)", true },
            new Object[] { "addresses[type eq \"home\" and " +
                "streetAddress co \"Hollywood\"]", true },
            new Object[] { "addresses[type eq \"work\" and " +
                "streetAddress co \"Hollywood\"]", false },
            new Object[] { "addresses.type eq \"work\" and " +
                "addresses.streetAddress co \"Hollywood\"", true },
            new Object[] { "addresses[priority gt 5 and " +
                "StreetAddress co \"Hollywood\"]", true },
            new Object[] { "addresses.priority ge 10", true },
            new Object[] { "addresses.priority le 0", true },
            new Object[] { "meta.created eq \"" +
                ISO8601Utils.format(date, true) + "\"", true },
            new Object[] { "meta.created eq \"" +
                ISO8601Utils.format(date, true, TimeZone.getTimeZone("CST")) +
                "\"", true },
            new Object[] { "meta.created eq \"" +
                ISO8601Utils.format(date, true, TimeZone.getTimeZone("PST")) +
                "\"", true },
            new Object[] { "meta.created ge \"" +
                ISO8601Utils.format(date, true, TimeZone.getTimeZone("CST")) +
                "\"", true },
            new Object[] { "meta.created le \"" +
                ISO8601Utils.format(date, true, TimeZone.getTimeZone("CST")) +
                "\"", true },
            new Object[] { "meta.created gt \"" +
                ISO8601Utils.format(date, true, TimeZone.getTimeZone("CST")) +
                "\"", false },
            new Object[] { "meta.created lt \"" +
                ISO8601Utils.format(date, true, TimeZone.getTimeZone("CST")) +
                "\"", false },
            new Object[] { "meta.created gt \"" +
                ISO8601Utils.format(new Date(date.getTime() + 1000), false,
                    TimeZone.getTimeZone("CST")) + "\"", false },
            new Object[] { "meta.created lt \"" +
                ISO8601Utils.format(new Date(date.getTime() + 1000), false,
                    TimeZone.getTimeZone("CST")) + "\"", true },
            new Object[] { "meta.created gt \"" +
                ISO8601Utils.format(new Date(date.getTime() - 1000), false,
                    TimeZone.getTimeZone("CST")) + "\"", true },
            new Object[] { "meta.created lt \"" +
                ISO8601Utils.format(new Date(date.getTime() - 1000), false,
                    TimeZone.getTimeZone("CST")) + "\"", false },
            new Object[] { "schemas[value eq " +
                "\"urn:unboundid:schemas:baseSchema\"]", true },
            new Object[] { "schemas eq " +
                "\"urn:unboundid:schemas:baseSchema\"", true },
            new Object[] { "schemas[value eq " +
                "\"urn:unboundid:schemas:something\"]", false },
        };
  }

  /**
   * Test that filters matching.
   *
   * @param filter The filter string to evaluate.
   * @param result The expected result.
   * @throws ScimException If the filter string is invalid.
   */
  @Test(dataProvider = "testValidFilterStrings")
  public void testFilter(String filter, boolean result)
      throws ScimException
  {
    assertEquals(FilterEvaluator.evaluate(Filter.fromString(filter), node),
        result);
  }
}
