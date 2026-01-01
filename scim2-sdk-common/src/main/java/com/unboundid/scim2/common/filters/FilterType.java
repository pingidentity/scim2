/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.filters;


import com.unboundid.scim2.common.annotations.NotNull;

/**
 * This enumeration defines the set of possible filter types that may
 * be used for SCIM query filters.
 */
public enum FilterType
{
  /**
   * The filter type for {@code and} filters.
   */
  AND("and"),


  /**
   * The filter type for {@code or} filters.
   */
  OR("or"),


  /**
   * The filter type for {@code not} filters.
   */
  NOT("not"),


  /**
   * The filter type for complex attribute value filters.
   */
  COMPLEX_VALUE("complex"),


  /**
   * The filter type for {@code equal} filters.
   */
  EQUAL("eq"),


  /**
   * The filter type for {@code not equal} filters.
   */
  NOT_EQUAL("ne"),


  /**
   * The filter type for {@code contains} filters.
   */
  CONTAINS("co"),


  /**
   * The filter type for {@code starts with} filters.
   */
  STARTS_WITH("sw"),


  /**
   * The filter type for {@code starts ends} filters.
   */
  ENDS_WITH("ew"),


  /**
   * The filter type for {@code present} filters.
   */
  PRESENT("pr"),


  /**
   * The filter type for {@code greater than} filters.
   */
  GREATER_THAN("gt"),


  /**
   * The filter type for {@code greater or equal} filters.
   */
  GREATER_OR_EQUAL("ge"),


  /**
   * The filter type for {@code less than} filters.
   */
  LESS_THAN("lt"),


  /**
   * The filter type for {@code less or equal} filters.
   */
  LESS_OR_EQUAL("le");


  /**
   * The lower case string value for this filter type.
   */
  @NotNull
  private String stringValue;


  /**
   * Creates a new filter type with the provided string value.
   *
   * @param stringValue The lower case string value for this filter type.
   */
  FilterType(@NotNull final String stringValue)
  {
    this.stringValue = stringValue;
  }


  /**
   * Retrieves the lower case string value for this filter type.
   *
   * @return The lower case string value for this filter type.
   */
  @NotNull
  public String getStringValue()
  {
    return stringValue;
  }


  /**
   * Retrieves a string representation of this filter type.
   *
   * @return A string representation of this filter type.
   */
  @NotNull
  public String toString()
  {
    return getStringValue();
  }
}

