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

import java.util.List;

/**
 * This class represents a SCIM {@code or} filter. An OR filter allows
 * a client to specify multiple filter criteria, where at least one of the
 * criteria must match.
 * <br><br>
 * For instance, consider the following filter. Parentheses have been added for
 * clarity.
 * <pre>
 *   (name.familyName sw "Sa") or (nickName sw "Sa")
 * </pre>
 * This is a filter with two components: a {@code sw} filter that matches SCIM
 * resources with a {@code familyName} starting with {@code "Sa"}, and another
 * {@code sw} filter that matches resources with a {@code nickName} starting
 * with "Sa". As an example, this filter would match a resource whose
 * {@code name.familyName} is {@code "Neighbors"} and has a {@code nickName} of
 * {@code "Sails"}.
 * <br><br>
 * This example filter can be represented with the following Java code:
 * <pre>
 *   Filter orFilter = Filter.or(
 *           Filter.sw("name.familyName", "Sa"),
 *           Filter.sw("nickName", "Sa")
 *   );
 * </pre>
 * A SCIM resource will match an OR filter if one of its subordinate filters
 * (also referred to as "filter components") match the resource.
 * <br><br>
 * This class allows for the use of multiple filter components, but {@code or}
 * filters generally only have two components.
 */
public final class OrFilter extends CombiningFilter
{
  /**
   * Create a new logical OR combining filter.
   *
   * @param filterComponents The component filters to logically OR together.
   */
  OrFilter(@NotNull final List<Filter> filterComponents)
  {
    super(filterComponents);
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
    return FilterType.OR;
  }

  /**
   * Indicates whether the provided object is equal to this OR filter.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this OR filter,
   *            or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof OrFilter that))
    {
      return false;
    }

    if (getCombinedFilters().size() != that.getCombinedFilters().size())
    {
      return false;
    }

    return getCombinedFilters().containsAll(that.getCombinedFilters());
  }

  /**
   * Retrieves a hash code for this OR filter.
   *
   * @return  A hash code for this OR filter.
   */
  @Override
  public int hashCode()
  {
    return getCombinedFilters().hashCode();
  }
}
