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

package com.unboundid.scim2.common.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration class for nonstandard parser options.
 *
 * <p>NOTE: SCIM server implementations are not guaranteed to support a given option.</p>
 */
public class ParserOptions
{
  private Set<Character> extendedAttributeNameCharacters = new HashSet<>();


  /**
   * Add characters (such as semicolons) to the set of extended naming characters
   * currently allowed within attribute names.
   *
   * @param extendedChars  The extended characters to be added.
   *
   * @return The updated {@code ParserOptions}.
   */
  public ParserOptions addExtendedAttributeNameCharacters(
      final Character... extendedChars)
  {
    for (Character extendedChar : extendedChars)
    {
      this.extendedAttributeNameCharacters.add(extendedChar);
    }
    return this;
  }

  /**
   * Clear the set of extended naming characters currently allowed within
   * attribute names, so that only standard attribute naming characters are
   * allowed.
   *
   * @return The updated {@code ParserOptions}.
   */
  public ParserOptions clearExtendedAttributeNameCharacters()
  {
    this.extendedAttributeNameCharacters.clear();
    return this;
  }

  /**
   * Get the set of extended naming characters currently allowed within
   * attribute names.
   *
   * @return The extended set. By default this will be empty, indicating that
   *         only standard attribute naming characters are allowed.
   */
  public Set<Character> getExtendedAttributeNameCharacters()
  {
    return Collections.unmodifiableSet(extendedAttributeNameCharacters);
  }

  /**
   * Indicate whether a given character is in the set of extended naming
   * characters currently allowed within attribute names.
   *
   * @param c  The desired character.
   * @return {@code true} if the character is in the extended set.
   */
  public boolean isExtendedAttributeNameCharacter(final char c)
  {
    return extendedAttributeNameCharacters.contains(c);
  }
}
