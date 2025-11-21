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

package com.unboundid.scim2.common.filters;

import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

import java.util.Objects;

/**
 * This class represents a complex attribute value filter. This filter type is
 * used to match specific values on multi-valued attributes. This is generally
 * used to filter based on a sub-attribute (e.g., {@code addresses.type}).
 * <br><br>
 *
 * For example, consider the case where a SCIM client wants to find emails
 * explicitly listed as "work" emails. This value is stored in the
 * {@code emails.type} attribute. This request can be represented by the
 * following SCIM filter:
 * <pre>
 *   emails[type eq "work"]
 * </pre>
 *
 * This example filter can be represented with the following Java code:
 * <pre><code>
 *   Filter complexFilter = Filter.complex("emails", Filter.eq("type", "work"));
 * </code></pre>
 *
 * The following utility methods can be used for complex filters:
 * <ul>
 *   <li> {@link Filter#isComplexValueFilter()}: Determines whether a filter
 *        object is a complex value filter.
 *   <li> {@link Filter#getAttributePath()}: Fetches the filter attribute. In
 *        the example above, this is the {@code emails} field.
 *   <li> {@link Filter#getValueFilter()}: Fetches the inner filter of a complex
 *        value filter, or {@code null} if the filter is a different type.
 * </ul>
 */
public final class ComplexValueFilter extends Filter
{
  @NotNull
  private final Path filterAttribute;

  @NotNull
  private final Filter valueFilter;

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param filterAttribute The complex attribute to filter.
   * @param valueFilter The value filter.
   */
  ComplexValueFilter(@NotNull final Path filterAttribute,
                     @NotNull final Filter valueFilter)
  {
    this.filterAttribute = filterAttribute;
    this.valueFilter = valueFilter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public Path getAttributePath()
  {
    return filterAttribute;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isComplexValueFilter()
  {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public Filter getValueFilter()
  {
    return valueFilter;
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public void toString(@NotNull final StringBuilder builder)
  {
    builder.append(filterAttribute);
    builder.append('[');
    builder.append(valueFilter);
    builder.append(']');
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public <R, P> R visit(@NotNull final FilterVisitor<R, P> visitor,
                        @Nullable final P param)
      throws ScimException
  {
    return visitor.visit(this, param);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public FilterType getFilterType()
  {
    return FilterType.COMPLEX_VALUE;
  }

  /**
   * Indicates whether the provided object is equal to this complex
   * multi-valued attribute filter.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this filter, or
   *            {@code false} if not.
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

    ComplexValueFilter that = (ComplexValueFilter) o;
    if (!filterAttribute.equals(that.filterAttribute))
    {
      return false;
    }
    return valueFilter.equals(that.valueFilter);
  }

  /**
   * Retrieves a hash code for this complex multi-valued attribute filter.
   *
   * @return  A hash code for this complex multi-valued attribute filter.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(filterAttribute, valueFilter);
  }
}
