/*
 * Copyright 2021 Ping Identity Corporation
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
   * Add extended characters (such as semicolons) to be allowed within attribute
   * names.
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
   * Remove any extended characters and only allow the standard ones within
   * attribute names.
   *
   * @return The updated {@code ParserOptions}.
   */
  public ParserOptions clearExtendedAttributeNameCharacters()
  {
    this.extendedAttributeNameCharacters.clear();
    return this;
  }

  /**
   * Get the current set of extended characters allowed within attribute names.
   *
   * @return The set of extended characters currently allowed. By default this
   *         will be empty.
   */
  public Set<Character> getExtendedAttributeNameCharacters()
  {
    return Collections.unmodifiableSet(extendedAttributeNameCharacters);
  }
}
