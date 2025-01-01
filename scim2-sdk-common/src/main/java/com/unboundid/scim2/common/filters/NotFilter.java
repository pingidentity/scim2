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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

/**
 * This class represents a SCIM {@code not} filter. NOT filters check that a
 * provided SCIM filter does not match a SCIM resource. In other words, it
 * inverts a SCIM filter. For instance, consider the following filter:
 * <pre>
 *   not (type eq "Rabbit")
 * </pre>
 * A SCIM resource will match this filter if the resource does not have a
 * {@code type} attribute with a value of {@code "Rabbit"}. This example filter
 * can be represented with the following Java code:
 * <pre>
 *   Filter notFilter = Filter.not(
 *           Filter.eq("type", "Rabbit")
 *   );
 * </pre>
 *
 * Similar to a {@link CombiningFilter}, NOT filters store the original filter
 * that is inverted. This original filter can be obtained by invoking the
 * {@link #getInvertedFilter()} method.
 * <br><br>
 * To determine whether any Filter is a NOT filter, use the
 * {@link Filter#isNotFilter()} method.
 */
public final class NotFilter extends Filter
{
  @NotNull
  private final Filter filterComponent;

  /**
   * Creates a new not function filter.
   *
   * @param filterComponent The filter to invert.
   */
  NotFilter(@NotNull final Filter filterComponent)
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
  @Nullable
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
  public void toString(@NotNull final StringBuilder builder)
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
    return FilterType.NOT;
  }

  /**
   * Indicates whether the provided object is equal to this NOT filter.
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

    NotFilter notFilter = (NotFilter) o;

    if (!filterComponent.equals(notFilter.filterComponent))
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this NOT filter.
   *
   * @return  A hash code for this NOT filter.
   */
  @Override
  public int hashCode()
  {
    return filterComponent.hashCode();
  }
}
