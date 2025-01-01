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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

/**
 * An interface for operating on the different types of SCIM filters using the
 * visitor pattern.
 */
public interface FilterVisitor<R, P>
{
  /**
   * Operate on an {@code equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final EqualFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code not equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final NotEqualFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code contains} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final ContainsFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code starts with} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final StartsWithFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on an {@code ends with} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final EndsWithFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code present} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final PresentFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code greater than} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final GreaterThanFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code greater than or equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final GreaterThanOrEqualFilter filter,
          @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code less than} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final LessThanFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code less then or equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final LessThanOrEqualFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on an {@code and} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final AndFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code or} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final OrFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a {@code not} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final NotFilter filter, @Nullable final P param)
      throws ScimException;

  /**
   * Operate on a complex multi-valued attribute value filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  R visit(@NotNull final ComplexValueFilter filter, @Nullable final P param)
      throws ScimException;
}
