package com.unboundid.scim2.filters;

import java.util.Collections;
import java.util.List;

/**
* Created by boli on 3/26/15.
*/
public abstract class CombiningFilter extends Filter
{
  private final List<Filter> filterComponents;

  CombiningFilter(List<Filter> filterComponents)
  {
    this.filterComponents = filterComponents;
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
    return Collections.unmodifiableList(filterComponents);
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder  The buffer to which the string representation of the
   *                 filter is to be appended.
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
