/*
 * Copyright 2015-2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.UserResource;

/**
 * This class represents a SCIM {@code pr} filter. Present filters (also
 * referred to as presence filters) check that the specified filter attribute
 * exists on a SCIM resource, and that the attribute value is both non-null and
 * non-empty. Unlike most filters, presence filters do not contain a value.
 * <br><br>
 * Consider the following filter:
 * <pre>
 *   profileUrl pr
 * </pre>
 *
 * This filter can be used in the case where a SCIM client wants to find all
 * resources that have a value for the {@code profileUrl} attribute. In other
 * words, it requests any resource (likely a {@link UserResource}) that has a
 * profile picture.
 * <br><br>
 * This example filter can be represented with the following Java code:
 * <pre>
 *   Filter presentFilter = Filter.pr("profileUrl");
 * </pre>
 */
public final class PresentFilter extends Filter
{
  @NotNull
  private final Path filterAttribute;

  /**
   * Create a new present filter.
   *
   * @param filterAttribute The path to the attribute.
   */
  PresentFilter(@NotNull final Path filterAttribute)
  {
    this.filterAttribute = filterAttribute;
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
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public void toString(@NotNull final StringBuilder builder)
  {
    builder.append(filterAttribute);
    builder.append(' ');
    builder.append(FilterType.PRESENT.getStringValue());
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public <R, P> R visit(@NotNull final FilterVisitor<R, P> visitor,
                        @Nullable final P param)
      throws ScimException
  {
    return visitor.visit(this, param);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public FilterType getFilterType()
  {
    return FilterType.PRESENT;
  }

  /**
   * Indicates whether the provided object is equal to this presence filter.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this filter, or
   *            {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
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
    return filterAttribute.equals(that.filterAttribute);
  }

  /**
   * Retrieves a hash code for this presence filter.
   *
   * @return  A hash code for this presence filter.
   */
  @Override
  public int hashCode()
  {
    return filterAttribute.hashCode();
  }
}
