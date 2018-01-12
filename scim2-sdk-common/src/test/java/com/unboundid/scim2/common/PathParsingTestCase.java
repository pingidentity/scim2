/*
 * Copyright 2015-2018 Ping Identity Corporation
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

import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.unboundid.scim2.common.filters.Filter.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Tests for parsing SCIM 2 paths.
 */
public class PathParsingTestCase
{
  /**
   * Retrieves a set of valid path strings.
   *
   * @return  A set of valid path strings.
   * @throws BadRequestException if the path string is invalid.
   */
  @DataProvider(name = "testValidPathStrings")
  public Object[][] getTestValidPathStrings() throws BadRequestException
  {
    return new Object[][]
        {
            new Object[] { "attr",
                Path.root().attribute("attr") },
            new Object[] { "urn:extension:attr",
                Path.root("urn:extension").attribute("attr") },
            new Object[] { "attr.subAttr",
                Path.root().attribute("attr").attribute("subAttr") },
            new Object[] { "attr[subAttr eq \"78750\"].subAttr",
                Path.root().attribute("attr", eq("subAttr", "78750")).
                    attribute("subAttr") },
            new Object[] { "attr.$ref",
                Path.root().attribute("attr").attribute("$ref") },
            new Object[] { "attr[$ref eq \"/Users/xxx\"]",
                Path.root().attribute("attr", eq("$ref", "/Users/xxx")) },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"].subAttr",
                Path.root("urn:extension").attribute("attr",
                    eq("subAttr", "78750")).attribute("subAttr") },
            new Object[] { "urn:ietf:params:scim:schemas:extension:" +
                "enterprise:2.0:User:employeeNumber",
                Path.root("urn:ietf:params:scim:schemas:extension:" +
                    "enterprise:2.0:User").attribute("employeeNumber") },
            new Object[] { "urn:pingidentity:schemas:sample:profile:1.0:" +
                "topicPreferences[(id eq " +
                "\"urn:X-UnboundID:topic:clothing:shoes\" and " +
                "strength eq 10 and timeStamp eq " +
                "\"2015-10-12T14:57:36.494Z\")]",
                Path.root("urn:pingidentity:schemas:sample:profile:1.0").
                    attribute("topicPreferences",
                        Filter.and(Filter.eq("id",
                                "urn:X-UnboundID:topic:clothing:shoes"),
                                   Filter.eq("strength", 10),
                                   Filter.eq("timeStamp",
                                       "2015-10-12T14:57:36.494Z"))) },

            // The following does not technically conform to the SCIM spec
            // but our path impl is lenient so it may be used with any JSON
            // object.
            new Object[] { "", Path.root() },
            new Object[] { "urn:extension:", Path.root("urn:extension") },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"]." +
                "subAttr[subSub pr].this.is.crazy[type eq \"good\"].deep",
                Path.root("urn:extension").
                    attribute("attr", eq("subAttr", "78750")).
                    attribute("subAttr", pr("subSub")).
                    attribute("this").
                    attribute("is").
                    attribute("crazy", eq("type", "good")).
                    attribute("deep") },

        };
  }



  /**
   * Retrieves a set of invalid path strings.
   *
   * @return  A set of invalid path strings.
   */
  @DataProvider(name = "testInvalidPathStrings")
  public Object[][] getTestInvalidPathStrings()
  {
    return new Object[][]
        {
            new Object[] { "." },
            new Object[] { "attr." },
            new Object[] { "urn:attr" },
            new Object[] { "attr[].subAttr" },
            new Object[] { ".attr" },
            new Object[] { "urn:extension:." },
            new Object[] { "urn:extension:.attr" },
            new Object[] { "attr[subAttr eq 123]." },
        };
  }



  /**
   * Tests the {@code fromString} method with a valid path string.
   *
   * @param pathString  The string representation of the path to fromString.
   * @param expectedPath The expected parsed path instance.
   *
   * @throws Exception  If the test fails.
   */
  @Test(dataProvider = "testValidPathStrings")
  public void testParseValidPath(final String pathString,
                                 final Path expectedPath)
      throws Exception
  {
    final Path parsedPath = Path.fromString(pathString);
    assertEquals(parsedPath, expectedPath);
  }



  /**
   * Tests the {@code fromString} method with an invalid path string.
   *
   * @param  pathString  The string representation of the path to fromString.
   *
   * @throws Exception If the test fails.
   */
  @Test(dataProvider = "testInvalidPathStrings")
  public void testParseInvalidPath(final String pathString)
      throws Exception
  {
    try
    {
      Path.fromString(pathString);
      fail("Unexpected successful fromString of invalid path: " + pathString);
    }
    catch (BadRequestException e)
    {
      assertEquals(e.getScimError().getScimType(),
          BadRequestException.INVALID_PATH);

    }
  }
}
