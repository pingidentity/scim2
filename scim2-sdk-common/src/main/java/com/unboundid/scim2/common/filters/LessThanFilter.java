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
 * This class represents a SCIM {@code lt} filter. For a given attribute name,
 * "Less Than" filters match SCIM resources that contain a smaller value than
 * the provided filter value. For instance, consider the following filter:
 * <pre>
 *   meta.created lt "2023-07-25T08:00:00.000Z"
 * </pre>
 *
 * A SCIM resource will match this filter if the resource contains a
 * {@code meta.created} attribute with a smaller value. In other words, this
 * filter matches any resource that was created before the provided timestamp.
 * <br><br>
 * This example filter can be represented with the following Java code:
 * <pre>
 *   Calendar calendar = Calendar.getInstance();
 *   calendar.set(2023, Calendar.JULY, 25, 8, 0);
 *   Filter ltFilter = Filter.lt("meta.created", calendar.getTime());
 * </pre>
 */
public final class LessThanFilter extends ComparisonFilter
{
  /**
   * Creates a new less than attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  LessThanFilter(final Path filterAttribute, final ValueNode filterValue)
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
    return FilterType.LESS_THAN;
  }

  /**
   * Indicates whether the provided object is equal to this "less than" filter.
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
   * Retrieves a hash code for this "less than" filter.
   *
   * @return  A hash code for this "less than" filter.
   */
  @Override
  public int hashCode()
  {
    int result = getAttributePath().hashCode();
    result = 31 * result + getComparisonValue().hashCode();
    return result;
  }
}
