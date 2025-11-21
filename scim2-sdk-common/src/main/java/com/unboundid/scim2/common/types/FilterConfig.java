/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Copyright 2015-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.Nullable;

import java.util.Objects;

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
   * Indicates whether the provided object is equal to this filter
   * configuration.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this filter
   *            configuration, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
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
    return supported == that.supported;
  }

  /**
   * Retrieves a hash code for this filter configuration.
   *
   * @return  A hash code for this filter configuration.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(supported, maxResults);
  }
}
