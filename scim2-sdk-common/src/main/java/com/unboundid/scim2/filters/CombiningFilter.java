/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.filters;

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
