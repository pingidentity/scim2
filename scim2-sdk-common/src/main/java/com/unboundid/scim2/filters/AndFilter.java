package com.unboundid.scim2.filters;

import com.unboundid.scim2.exceptions.SCIMException;

import java.util.List;

/**
* Created by boli on 3/26/15.
*/
public final class AndFilter extends CombiningFilter
{
  AndFilter(List<Filter> filterComponents)
  {
    super(filterComponents);
  }

  /**
   * {@inheritDoc}
   */
  public final <R, P> R visit(FilterVisitor<R, P> visitor, P param)
      throws SCIMException
  {
    return visitor.visit(this, param);
  }

  @Override
  public FilterType getFilterType()
  {
    return FilterType.AND;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    CombiningFilter that = (CombiningFilter) o;

    if (!getCombinedFilters().containsAll(that.getCombinedFilters()))
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    return getCombinedFilters().hashCode();
  }
}
