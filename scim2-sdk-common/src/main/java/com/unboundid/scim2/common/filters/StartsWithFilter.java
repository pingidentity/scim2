/*
 * Copyright 2015-2024 Ping Identity Corporation
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
 * This class represents a SCIM {@code sw} filter. "Starts With" filters are
 * used to determine if a SCIM resource's attribute value begins with the
 * provided filter value.
 * <br><br>
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
 * This example filter can be represented with the following Java code:
 * <pre>
 *   Filter swFilter = Filter.sw("title", "New");
 * </pre>
 */
public final class StartsWithFilter extends ComparisonFilter
{
  /**
   * Creates a new starts with attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  StartsWithFilter(final Path filterAttribute, final ValueNode filterValue)
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
   * Retrieves a hash code for this "starts with" filter.
   *
   * @return  A hash code for this "starts with" filter.
   */
  @Override
  public int hashCode()
  {
    int result = getAttributePath().hashCode();
    result = 31 * result + getComparisonValue().hashCode();
    return result;
  }
}
