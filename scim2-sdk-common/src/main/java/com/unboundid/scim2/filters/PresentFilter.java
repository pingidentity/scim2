/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.filters;

import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;

/**
 * Attribute present filter.
 */
public final class PresentFilter extends Filter
{
  private final Path filterAttribute;

  /**
   * Create a new present filter.
   *
   * @param filterAttribute The path to the attribute.
   */
  PresentFilter(final Path filterAttribute)
  {
    this.filterAttribute = filterAttribute;
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
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
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
    return FilterType.PRESENT;
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
