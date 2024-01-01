/*
 * Copyright 2021-2024 Ping Identity Corporation
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.utils.Parser;


/**
 * Test coverage for using {@code ParserOptions} with {@code Parser}.
 */
public class ParserOptionsTestCase
{

  /**
   * Tests {@code ParserOptions} with an extended attribute name character.
   *
   * @throws Exception  If the test fails.
   */
  @Test
  public void testAllowSemicolons()
      throws Exception
  {
    String attributeWithSemicolon = "attribute;x-tag";
    String filterString = attributeWithSemicolon + " eq 123";

    // Verify filter is rejected by default
    assertFalse(
        Parser.getOptions().getExtendedAttributeNameCharacters().contains(';'));
    try
    {
      Parser.parseFilter(filterString);
      fail("Parser should have rejected '" + filterString + "'");
    }
    catch (BadRequestException ex)
    {
      assertTrue(
          ex.getMessage().startsWith("Unexpected character ';' at position 9"));
    }

    // Verify filter is permitted after we specify the option.
    Parser.getOptions().addExtendedAttributeNameCharacters(';');
    assertTrue(
        Parser.getOptions().getExtendedAttributeNameCharacters().contains(';'));

    Filter filter = Parser.parseFilter(filterString);
    assertEquals(filter.getAttributePath().toString(), attributeWithSemicolon);
    assertEquals(filter.getFilterType().toString(), "eq");
    assertEquals(filter.getComparisonValue().toString(), "123");

    // Verify attribute is rejected after we remove the option.
    Parser.getOptions().clearExtendedAttributeNameCharacters();
    assertFalse(
        Parser.getOptions().getExtendedAttributeNameCharacters().contains(';'));
    try
    {
      Parser.parseFilter(filterString);
      fail("Parser should have rejected '" + filterString + "'");
    }
    catch (BadRequestException ex)
    {
      assertTrue(
          ex.getMessage().startsWith("Unexpected character ';' at position 9"));
    }
  }
}
