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
 * This class represents a SCIM {@code co} filter. Containing filters are used
 * to determine if the provided filter value is a substring of a SCIM resource's
 * attribute value.
 * <br><br>
 *
 * For instance, consider the following filter:
 * <pre>
 *   displayName co "eet"
 * </pre>
 *
 * This filter can be used in the case where a SCIM client wants to find all
 * resources that have a {@code displayName} attribute value containing the
 * {@code "eet"} substring within it. As an example, it would match a
 * resource with a {@code displayName} attribute value of {@code "meeting"}.
 * <br><br>
 *
 * This example filter can be represented with the following Java code:
 * <pre><code>
 *   Filter containsFilter = Filter.co("displayName", "eet");
 * </code></pre>
 */
public final class ContainsFilter extends ComparisonFilter
{
  /**
   * Creates a new contains attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  ContainsFilter(@NotNull final Path filterAttribute,
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
    return FilterType.CONTAINS;
  }

  /**
   * Indicates whether the provided object is equal to this contains filter.
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
   * Retrieves a hash code for this contains filter.
   *
   * @return  A hash code for this contains filter.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(getAttributePath(), getComparisonValue());
  }
}
