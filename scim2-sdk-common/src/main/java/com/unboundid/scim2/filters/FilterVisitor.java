/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.filters;

import com.unboundid.scim2.exceptions.SCIMException;

/**
 * An interface for operating on the different types of SCIM filters using the
 * visitor pattern.
 */
public interface FilterVisitor<R, P>
{
  /**
   * Operate on an equal filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final EqualFilter filter, final P param) throws SCIMException;

  /**
   * Operate on a not equal filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final NotEqualFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a contains filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final ContainsFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a starts with filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final StartsWithFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on an ends with filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final EndsWithFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a present filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final PresentFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a greater than filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final GreaterThanFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a greater than or equal filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final GreaterThanOrEqualFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a less than filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final LessThanFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a less then or equal filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final LessThanOrEqualFilter filter, final P param)
      throws SCIMException;

  /**
   * Operate on a and filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final AndFilter filter, final P param) throws SCIMException;

  /**
   * Operate on a or filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final OrFilter filter, final P param) throws SCIMException;

  /**
   * Operate on a not filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final NotFilter filter, final P param) throws SCIMException;

  /**
   * Operate on a complex mulit-valued attribute value filter.
   *
   * @param filter The filter to operate on.
   * @param param  The optional operational parameter.
   * @return The return value from the operation.
   * @throws SCIMException If an exception occurs during the operation.
   */
  R visit(final ComplexValueFilter filter, final P param)
      throws SCIMException;
}
