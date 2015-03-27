package com.unboundid.scim2.filters;

import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;

/**
* Created by boli on 3/26/15.
*/
public final class PresentFilter extends Filter
{
  private final Path filterAttribute;

  PresentFilter(Path filterAttribute)
  {
    this.filterAttribute = filterAttribute;
  }

  @Override
  public Path getAttributePath()
  {
    return filterAttribute;
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
    builder.append(' ');
    builder.append(FilterType.PRESENT.getStringValue());
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
    return FilterType.PRESENT;
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

    PresentFilter that = (PresentFilter) o;

    if (!filterAttribute.equals(that.filterAttribute))
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
    return filterAttribute.hashCode();
  }
}
