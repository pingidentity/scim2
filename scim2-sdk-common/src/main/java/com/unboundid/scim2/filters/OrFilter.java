/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.filters;

import com.unboundid.scim2.exceptions.SCIMException;

import java.util.List;

/**
 * Logical OR combining filter.
 */
public final class OrFilter extends CombiningFilter
{
  /**
   * Create a new logical OR combining filter.
   *
   * @param filterComponents The component filters to logically OR together.
   */
  OrFilter(final List<Filter> filterComponents)
  {
    super(filterComponents);
  }

  /**
   * {@inheritDoc}
   */
  public <R, P> R visit(final FilterVisitor<R, P> visitor, final P param)
      throws SCIMException
  {
    return visitor.visit(this, param);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FilterType getFilterType()
  {
    return FilterType.OR;
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
