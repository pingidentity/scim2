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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.utils.JsonUtils;

/**
 * This superclass represents filter types that are used to compare attribute
 * values of SCIM resources.
 * <br><br>
 * For example, an {@link EqualFilter} is a comparison filter. A filter such as
 * {@code preferredLanguage eq "en-US"} is used to perform a comparison between
 * the filter value, {@code "en-US"}, and the value of a SCIM resource's
 * {@code preferredLanguage} attribute.
 * <br><br>
 * To determine whether a filter is a comparison filter, use the
 * {@link Filter#isComparisonFilter()} method.
 */
public abstract class ComparisonFilter extends Filter
{
  @NotNull
  private final Path filterAttribute;

  @NotNull
  private final ValueNode filterValue;

  /**
   * Create a new attribute comparison filter.
   *
   * @param filterAttribute The path to the attribute to compare.
   * @param filterValue The comparison value.
   */
  ComparisonFilter(@NotNull final Path filterAttribute,
                   @Nullable final ValueNode filterValue)
  {
    this.filterAttribute = filterAttribute;
    if (filterValue == null)
    {
      this.filterValue = JsonUtils.getJsonNodeFactory().nullNode();
    }
    else
    {
      this.filterValue = filterValue;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public Path getAttributePath()
  {
    return filterAttribute;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public ValueNode getComparisonValue()
  {
    return filterValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isComparisonFilter()
  {
    return true;
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public void toString(@NotNull final StringBuilder builder)
  {
    builder.append(filterAttribute);
    builder.append(' ');
    builder.append(getFilterType().getStringValue());
    builder.append(' ');
    builder.append(filterValue.toString());
  }
}
