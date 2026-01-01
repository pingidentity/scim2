/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.types.UserResource;

import java.util.List;

/**
 * This class represents a SCIM {@code and} filter. An AND filter allows a
 * SCIM client to specify multiple filter criteria, where all criteria must
 * match a resource.
 * <br><br>
 *
 * For instance, consider the following filter. Parentheses have been added for
 * clarity.
 * <pre>
 *   (userName sw "win") and (meta.resourceType eq "User")
 * </pre>
 *
 * This is a filter with two components: a {@code sw} filter that matches
 * resources with a {@code userName} starting with "win", and an {@code eq}
 * filter that matches {@code User} resources. As an example, this filter would
 * match a {@link UserResource} whose {@code userName} is {@code "wind"}.
 * <br><br>
 *
 * This example filter can be represented with the following Java code:
 * <pre><code>
 *   Filter andFilter = Filter.and(
 *           Filter.sw("userName", "win"),
 *           Filter.eq("meta.resourceType", "User")
 *   );
 * </code></pre>
 *
 * It is also possible to create an AND filter from the contents of a list:
 * <pre><code>
 *   List&lt;Filter&gt; existingList = getExistingFilters();
 *   Filter andFilter = Filter.and(existingList);
 * </code></pre>
 *
 * A SCIM resource will match an AND filter if all of its subordinate filters
 * (also referred to as "filter components") match the resource.
 */
public final class AndFilter extends CombiningFilter
{
  /**
   * Create a new logical and combining filter.
   *
   * @param filterComponents The component filters to logically AND together.
   */
  AndFilter(@NotNull final List<Filter> filterComponents)
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
    return FilterType.AND;
  }

  /**
   * Indicates whether the provided object is equal to this AND filter.
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
    if (!(o instanceof AndFilter that))
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
   * Retrieves a hash code for this AND filter.
   *
   * @return  A hash code for this AND filter.
   */
  @Override
  public int hashCode()
  {
    return getCombinedFilters().hashCode();
  }
}
