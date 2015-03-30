/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;


/**
 * Signals the server does not support the requested operation.
 *
 * This exception corresponds to HTTP response code 403 FORBIDDEN.
 */
public class ForbiddenException extends SCIMException
{
  /**
   * Create a new <code>ForbiddenException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ForbiddenException(final String errorMessage) {
    super(403, errorMessage);
  }

  /**
   * Create a new <code>ForbiddenException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ForbiddenException(final String errorMessage,
                            final Throwable cause) {
    super(403, errorMessage, cause);
  }
}
