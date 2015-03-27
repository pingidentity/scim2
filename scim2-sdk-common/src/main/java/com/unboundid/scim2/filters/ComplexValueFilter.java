package com.unboundid.scim2.filters;

import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;

/**
* Created by boli on 3/26/15.
*/
public final class ComplexValueFilter extends Filter
{
  private final Path filterAttribute;

  private final Filter valueFilter;

  public ComplexValueFilter(Path filterAttribute, Filter valueFilter)
  {
    this.filterAttribute = filterAttribute;
    this.valueFilter = valueFilter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getAttributePath()
  {
    return filterAttribute;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isComplexValueFilter()
  {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Filter getValueFilter()
  {
    return valueFilter;
  }

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder  The buffer to which the string representation of the
   *                 filter is to be appended.
   */
  public void toString(final StringBuilder builder)
  {
    builder.append(filterAttribute);
    builder.append('[');
    builder.append(valueFilter);
    builder.append(']');
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
    return FilterType.COMPLEX_VALUE;
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

    ComplexValueFilter that = (ComplexValueFilter) o;

    if (!filterAttribute.equals(that.filterAttribute))
    {
      return false;
    }
    if (!valueFilter.equals(that.valueFilter))
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
    int result = filterAttribute.hashCode();
    result = 31 * result + valueFilter.hashCode();
    return result;
  }
}
