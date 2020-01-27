/*
 * Copyright 2015-2020 Ping Identity Corporation
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
  R visit(final EqualFilter filter, final P param) throws ScimException;

  /**
   * Operate on a {@code not equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final NotEqualFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code contains} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final ContainsFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code starts with} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final StartsWithFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on an {@code ends with} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final EndsWithFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code present} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final PresentFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code greater than} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final GreaterThanFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code greater than or equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final GreaterThanOrEqualFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code less than} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final LessThanFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on a {@code less then or equal} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final LessThanOrEqualFilter filter, final P param)
      throws ScimException;

  /**
   * Operate on an {@code and} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final AndFilter filter, final P param) throws ScimException;

  /**
   * Operate on a {@code or} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final OrFilter filter, final P param) throws ScimException;

  /**
   * Operate on a {@code not} filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final NotFilter filter, final P param) throws ScimException;

  /**
   * Operate on a complex mulit-valued attribute value filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  R visit(final ComplexValueFilter filter, final P param)
      throws ScimException;
}
