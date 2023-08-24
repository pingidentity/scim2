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
 * This class represents a SCIM {@code eq} filter. Equality filters check that a
 * SCIM resource's attribute value is identical to the provided filter value.
 * For instance, consider the following filter:
 * <pre>
 *   displayName eq "Static"
 * </pre>
 * A SCIM resource will match this filter if the resource contains a
 * {@code displayName} attribute with a value of {@code "Static"}. This example
 * filter can be represented with the following Java code:
 * <pre>
 *   Filter displayNameFilter = Filter.eq("displayName", "Static");
 * </pre>
 */
public final class EqualFilter extends ComparisonFilter
{
  /**
   * Creates a new equals attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  EqualFilter(final Path filterAttribute, final ValueNode filterValue)
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
    return FilterType.EQUAL;
  }

  /**
   * Indicates whether the provided object is equal to this equality filter.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this equality
   *            filter, or {@code false} if not.
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
   * Retrieves a hash code for this equality filter.
   *
   * @return  A hash code for this equality filter.
   */
  @Override
  public int hashCode()
  {
    int result = getAttributePath().hashCode();
    result = 31 * result + getComparisonValue().hashCode();
    return result;
  }
}
