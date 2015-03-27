package com.unboundid.scim2.filters;

import com.fasterxml.jackson.databind.node.ValueNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;

/**
* Created by boli on 3/26/15.
*/
public final class NotEqualFilter extends ComparisonFilter
{
  NotEqualFilter(Path filterAttribute, ValueNode filterValue)
  {
    super(filterAttribute, filterValue);
  }

  /**
   * {@inheritDoc}
   */
  public final <R, P> R visit(FilterVisitor<R, P> visitor, P param)
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
    return FilterType.NOT_EQUAL;
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

    ComparisonFilter that = (ComparisonFilter) o;

    if (!getAttributePath().equals(that.getAttributePath()))
    {
      return false;
    }
    if (!getComparisonValue().equals(that.getComparisonValue()))
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
    int result = getAttributePath().hashCode();
    result = 31 * result + getComparisonValue().hashCode();
    return result;
  }
}
