/*
 * Copyright 2021-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2021-2026 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration class for nonstandard parser options.
 * <p>
 * NOTE: SCIM server implementations are not guaranteed to support a given
 * option.
 */
public class ParserOptions
{
  @NotNull
  private Set<Character> extendedAttributeNameCharacters = new HashSet<>();


  /**
   * Add characters (e.g., semicolons) to the set of extended naming characters
   * currently allowed within attribute names.
   *
   * @param extendedChars  The extended characters to be added.
   *
   * @return The updated {@code ParserOptions}.
   */
  @NotNull
  public ParserOptions addExtendedAttributeNameCharacters(
      @NotNull final Character... extendedChars)
  {
    Collections.addAll(this.extendedAttributeNameCharacters, extendedChars);
    return this;
  }

  /**
   * Clear the set of extended naming characters currently allowed within
   * attribute names, so that only standard attribute naming characters are
   * allowed.
   *
   * @return The updated {@code ParserOptions}.
   */
  @NotNull
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
  @NotNull
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
