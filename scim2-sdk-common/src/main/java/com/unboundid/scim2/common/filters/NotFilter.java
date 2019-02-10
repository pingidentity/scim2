/*
 * Copyright 2015-2019 Ping Identity Corporation
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

import com.unboundid.scim2.common.exceptions.ScimException;

/**
 * Not function filter.
 */
public final class NotFilter extends Filter
{
  private final Filter filterComponent;

  /**
   * Creates a new not function filter.
   *
   * @param filterComponent The filter to inverse.
   */
  NotFilter(final Filter filterComponent)
  {
    this.filterComponent = filterComponent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNotFilter()
  {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Filter getInvertedFilter()
  {
    return filterComponent;
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public void toString(final StringBuilder builder)
  {
    builder.append("not");
    builder.append(' ');
    builder.append('(');
    builder.append(filterComponent);
    builder.append(')');
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
    return FilterType.NOT;
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

    NotFilter notFilter = (NotFilter) o;

    if (!filterComponent.equals(notFilter.filterComponent))
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return filterComponent.hashCode();
  }
}
