/*
 * Copyright 2020 Ping Identity Corporation
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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test coverage for ServerUtils.EncodeTemplateNames.
 */
public class EncodeTemplateNamesTestCase
{
  /**
   * Retrieves a set of strings to test encodeTemplateNames.
   *
   * @return  A set of input strings with expected output.
   */
  @DataProvider(name = "testEncodeTemplateNamesStrings")
  public Object[][] getTestEncodeTemplateNames()
  {
    return new Object[][]
        {
            new Object[] { "cn eq \"{xx}\"", "cn eq \"%7Bxx%7D\"" },
            new Object[] { "{a}{b}", "%7Ba%7D%7Bb%7D" },
            new Object[] { "%7B%7D", "%7B%7D" },
            new Object[] { "{{a}}", "%7B%7Ba%7D%7D" },
            new Object[] { "}}}}{{{{", "%7D%7D%7D%7D%7B%7B%7B%7B" },
            new Object[] { "{", "%7B" },
            new Object[] { "}", "%7D" },
            new Object[] { "", "" },
        };
  }

  /**
   * Test the encodeTemplateNames method.
   *
   * @param input The string to evaluate.
   * @param output The expected result.
   */
  @Test(dataProvider = "testEncodeTemplateNamesStrings")
  public void testFilter(String input, String output)
  {
    final String actualOutput = ServerUtils.encodeTemplateNames(input);
    assertEquals(actualOutput, output);
  }
}
