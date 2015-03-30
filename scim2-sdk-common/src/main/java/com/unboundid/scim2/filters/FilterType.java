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

package com.unboundid.scim2.filters;


/**
 * This enumeration defines the set of possible filter types that may
 * be used for SCIM query filters.
 */
public enum FilterType
{
  /**
   * The filter type for and filters.
   */
  AND("and"),


  /**
   * The filter type for or filters.
   */
  OR("or"),


  /**
   * The filter type for not filters.
   */
  NOT("not"),


  /**
   * The filter type for complex attribute value filters.
   */
  COMPLEX_VALUE("complex"),


  /**
   * The filter type for equal filters.
   */
  EQUAL("eq"),


  /**
   * The filter type for not equal filters.
   */
  NOT_EQUAL("ne"),


  /**
   * The filter type for contains filters.
   */
  CONTAINS("co"),


  /**
   * The filter type for starts with filters.
   */
  STARTS_WITH("sw"),


  /**
   * The filter type for ends with filters.
   */
  ENDS_WITH("ew"),


  /**
   * The filter type for present filters.
   */
  PRESENT("pr"),


  /**
   * The filter type for greater than filters.
   */
  GREATER_THAN("gt"),


  /**
   * The filter type for greater or equal filters.
   */
  GREATER_OR_EQUAL("ge"),


  /**
   * The filter type for less than filters.
   */
  LESS_THAN("lt"),


  /**
   * The filter type for less or equal filters.
   */
  LESS_OR_EQUAL("le");


  /**
   * The lower case string value for this filter type.
   */
  private String stringValue;


  /**
   * Creates a new filter type with the provided string value.
   *
   * @param stringValue The lower case string value for this filter type.
   */
  private FilterType(final String stringValue)
  {
    this.stringValue = stringValue;
  }


  /**
   * Retrieves the lower case string value for this filter type.
   *
   * @return The lower case string value for this filter type.
   */
  public String getStringValue()
  {
    return stringValue;
  }


  /**
   * Retrieves a string representation of this filter type.
   *
   * @return A string representation of this filter type.
   */
  public String toString()
  {
    return getStringValue();
  }
}

