/*
 * Copyright 2015-2025 Ping Identity Corporation
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

import java.util.Collections;
import java.util.List;

/**
 * This class is the superclass of filter types that contains two filters. There
 * are two types of combining filters:
 * <ul>
 *   <li> {@link AndFilter}
 *   <li> {@link OrFilter}
 * </ul>
 * <br><br>
 *
 * "Combining" filters contain subordinate filters, which are also referred to
 * as "filter components". To obtain the filter components that comprise a
 * combining filter, use the {@link #getCombinedFilters()} method.
 * <br><br>
 *
 * For more information, see the class-level documentation of the subclasses of
 * CombiningFilter.
 */
public abstract class CombiningFilter extends Filter
{
  @NotNull
  private final List<Filter> filterComponents;

  /**
   * Create a new logical combining filter.
   *
   * @param filterComponents The filter components to combine.
   */
  CombiningFilter(@NotNull final List<Filter> filterComponents)
  {
    this.filterComponents = Collections.unmodifiableList(filterComponents);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCombiningFilter()
  {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public List<Filter> getCombinedFilters()
  {
    return filterComponents;
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public void toString(@NotNull final StringBuilder builder)
  {
    builder.append('(');

    for (int i = 0; i < filterComponents.size(); i++)
    {
      if (i != 0)
      {
        builder.append(' ');
        builder.append(getFilterType().getStringValue());
        builder.append(' ');
      }

      builder.append(filterComponents.get(i));
    }

    builder.append(')');
  }
}
