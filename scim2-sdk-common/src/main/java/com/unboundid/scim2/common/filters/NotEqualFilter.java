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
 * This class represents a SCIM {@code ne} filter. "Not Equal" filters are used
 * to determine if a SCIM resource's attribute value is not equivalent to the
 * provided filter value.
 * <br><br>
 * For instance, consider the following filter:
 * <pre>
 *   name.familyName ne "Traffic"
 * </pre>
 *
 * This filter can be used in the case where a SCIM client wants to find all
 * users that do not have a {@code name.familyName} value of {@code "Traffic"}.
 * As an example, it would match a resource with a {@code name.familyName} value
 * of {@code "Neighbors"}. This would also match a resource that does not have a
 * value for {@code name.familyName}.
 * <br><br>
 * This example filter can be represented with the following Java code:
 * <pre>
 *   Filter notEqualFilter = Filter.ne("name.familyName", "Traffic");
 * </pre>
 */
public final class NotEqualFilter extends ComparisonFilter
{
  /**
   * Creates a new not equal attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  NotEqualFilter(@NotNull final Path filterAttribute,
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
    return FilterType.NOT_EQUAL;
  }

  /**
   * Indicates whether the provided object is equal to this "not equal" filter.
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
   * Retrieves a hash code for this "not equal" filter.
   *
   * @return  A hash code for this "not equal" filter.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(getAttributePath(), getComparisonValue());
  }
}
