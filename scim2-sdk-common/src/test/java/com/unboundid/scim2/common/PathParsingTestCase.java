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

import com.unboundid.scim2.common.exceptions.BadRequestException;
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
                Path.attribute("attr") },
            new Object[] { "urn:extension:attr",
                Path.attribute("urn:extension", "attr") },
            new Object[] { "attr.subAttr",
                Path.attribute("attr").sub("subAttr") },
            new Object[] { "attr[subAttr eq \"78750\"].subAttr",
                Path.attribute("attr", eq("subAttr", "78750")).sub("subAttr") },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"].subAttr",
                Path.attribute("urn:extension", "attr",
                    eq("subAttr", "78750")).sub("subAttr") },

            // The following does not technically conform to the SCIM spec
            // but our path impl is lenient so it may be used with any JSON
            // object.
            new Object[] { "", Path.root() },
            new Object[] { "urn:extension:", Path.extension("urn:extension") },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"]." +
                "subAttr[subSub pr].this.is.crazy[type eq \"good\"].deep",
                Path.attribute("urn:extension", "attr", eq("subAttr", "78750")).
                    sub("subAttr", pr("subSub")).
                    sub("this").
                    sub("is").
                    sub("crazy", eq("type", "good")).
                    sub("deep") },

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
