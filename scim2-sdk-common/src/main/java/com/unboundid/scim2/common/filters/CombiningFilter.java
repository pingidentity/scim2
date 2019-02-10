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

import java.util.Collections;
import java.util.List;

/**
 * Logical combining filter.
 */
public abstract class CombiningFilter extends Filter
{
  private final List<Filter> filterComponents;

  /**
   * Create a new logical combining filter.
   *
   * @param filterComponents The filter components to combine.
   */
  CombiningFilter(final List<Filter> filterComponents)
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
  public void toString(final StringBuilder builder)
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
