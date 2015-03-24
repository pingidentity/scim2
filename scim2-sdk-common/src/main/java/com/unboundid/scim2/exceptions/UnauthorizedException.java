/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * Signals an authorization failure from the service provider.
 *
 * This exception corresponds to HTTP response code 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends SCIMException
{
  /**
   * Create a new <code>UnauthorizedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public UnauthorizedException(final String errorMessage) {
    super(401, errorMessage);
  }

  /**
   * Create a new <code>UnauthorizedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public UnauthorizedException(final String errorMessage,
                               final Throwable cause) {
    super(401, errorMessage, cause);
  }
}
