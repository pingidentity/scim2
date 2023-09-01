/*
 * Copyright 2015-2023 Ping Identity Corporation
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
import com.unboundid.scim2.common.exceptions.ScimException;

/**
 * This class represents a SCIM {@code le} filter. For a given attribute name,
 * "Less Than Or Equal To" filters match SCIM resources that contain a smaller
 * or equivalent value when compared to the provided filter value.
 * <br><br>
 * For instance, consider the following filter:
 * <pre>
 *   meta.created le "2023-07-25T08:00:00.000Z"
 * </pre>
 *
 * This filter can be used in the case where a SCIM client wants to find all
 * resources whose {@code meta.created} attribute with is either smaller than or
 * equivalent to the filter value. In other words, it matches any resource that
 * was created at or before the provided timestamp.
 * <br><br>
 * This example filter can be represented with the following Java code:
 * <pre>
 *   Calendar calendar = Calendar.getInstance();
 *   calendar.set(2023, Calendar.JULY, 25, 8, 0);
 *   Filter leFilter = Filter.le("meta.created", calendar.getTime());
 * </pre>
 */
public final class LessThanOrEqualFilter extends ComparisonFilter
{
  /**
   * Creates a new less than or equal attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  LessThanOrEqualFilter(final Path filterAttribute, final ValueNode filterValue)
  {
    super(filterAttribute, filterValue);
  }

  /**
   * {@inheritDoc}
   */
  public <R, P> R visit(final FilterVisitor<R, P> visitor, final P param)
      throws ScimException
  {
    return visitor.visit(this, param);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FilterType getFilterType()
  {
    return FilterType.LESS_OR_EQUAL;
  }

  /**
   * Indicates whether the provided object is equal to this "less than or equal"
   * filter.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this filter, or
   *            {@code false} if not.
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

    ComparisonFilter that = (ComparisonFilter) o;

    if (!getAttributePath().equals(that.getAttributePath()))
    {
      return false;
    }
    if (!getComparisonValue().equals(that.getComparisonValue()))
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this "less than or equal" filter.
   *
   * @return  A hash code for this "less than or equal" filter.
   */
  @Override
  public int hashCode()
  {
    int result = getAttributePath().hashCode();
    result = 31 * result + getComparisonValue().hashCode();
    return result;
  }
}
