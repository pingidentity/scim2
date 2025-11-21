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

import com.fasterxml.jackson.databind.node.ValueNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

import java.util.Objects;

/**
 * This class represents a SCIM {@code sw} filter. "Starts With" filters are
 * used to determine if a SCIM resource's attribute value begins with the
 * provided filter value.
 * <br><br>
 *
 * For instance, consider the following filter:
 * <pre>
 *   title sw "New"
 * </pre>
 *
 * This filter can be used in the case where a SCIM client wants to find all
 * resources that have a {@code title} attribute value that begins with
 * {@code "New"}. As an example, it would match a resource with a {@code title}
 * value of {@code "Newspaperman"}.
 * <br><br>
 *
 * This example filter can be represented with the following Java code:
 * <pre><code>
 *   Filter swFilter = Filter.sw("title", "New");
 * </code></pre>
 */
public final class StartsWithFilter extends ComparisonFilter
{
  /**
   * Creates a new starts with attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  StartsWithFilter(@NotNull final Path filterAttribute,
                   @Nullable final ValueNode filterValue)
  {
    super(filterAttribute, filterValue);
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
    return FilterType.STARTS_WITH;
  }

  /**
   * Indicates whether the provided object is equal to this "starts with"
   * filter.
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

    ComparisonFilter that = (ComparisonFilter) o;
    if (!getAttributePath().equals(that.getAttributePath()))
    {
      return false;
    }
    return getComparisonValue().equals(that.getComparisonValue());
  }

  /**
   * Retrieves a hash code for this "starts with" filter.
   *
   * @return  A hash code for this "starts with" filter.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(getAttributePath(), getComparisonValue());
  }
}
