/*
 * Copyright 2015-2016 UnboundID Corp.
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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;

/**
 * A complex type that specifies FILTER options.
 */
public class FilterConfig
{
  @Attribute(description = "Boolean value specifying whether the " +
      "operation is supported.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean supported;

  @Attribute(description = "Integer value specifying the maximum " +
      "number of resources returned in a response.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final int maxResults;

  /**
   * Create a new complex type that specifies FILTER options.
   *
   * @param supported Boolean value specifying whether the operation is
   *                  supported.
   * @param maxResults Integer value specifying the maximum number of
   *                   resources returned in a response.
   */
  public FilterConfig(@JsonProperty(value = "supported", required = true)
                      final boolean supported,
                      @JsonProperty(value = "maxResults", required = true)
                      final int maxResults)
  {
    this.supported = supported;
    this.maxResults = maxResults;
  }

  /**
   * Retrieves the boolean value specifying whether the operation is
   * supported.
   *
   * @return {@code true} if the operation is supported or {@code false}
   * otherwise.
   */
  public boolean isSupported()
  {
    return supported;
  }

  /**
   * Retrieves the integer value specifying the maximum number of resources
   * returned in a response.
   *
   * @return The integer value specifying the maximum number of resources
   * returned in a response.
   */
  public int getMaxResults()
  {
    return maxResults;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    FilterConfig that = (FilterConfig) o;

    if (maxResults != that.maxResults)
    {
      return false;
    }
    if (supported != that.supported)
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = (supported ? 1 : 0);
    result = 31 * result + maxResults;
    return result;
  }
}
