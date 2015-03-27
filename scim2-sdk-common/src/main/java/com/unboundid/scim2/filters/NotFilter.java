package com.unboundid.scim2.filters;

import com.unboundid.scim2.exceptions.SCIMException;

/**
* Created by boli on 3/26/15.
*/
public final class NotFilter extends Filter
{
  private final Filter filterComponent;

  NotFilter(Filter filterComponent)
  {
    this.filterComponent = filterComponent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNotFilter()
  {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Filter getInvertedFilter()
  {
    return filterComponent;
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder  The buffer to which the string representation of the
   *                 filter is to be appended.
   */
  public void toString(final StringBuilder builder)
  {
    builder.append("not");
    builder.append(' ');
    builder.append('(');
    builder.append(filterComponent);
    builder.append(')');
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
    return FilterType.NOT;
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

    NotFilter notFilter = (NotFilter) o;

    if (!filterComponent.equals(notFilter.filterComponent))
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return filterComponent.hashCode();
  }
}
