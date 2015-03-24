/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * Signals an internal error from the service provider.
 *
 * This exception corresponds to HTTP response code 500 INTERNAL SERVER ERROR.
 */
public class ServerErrorException extends SCIMException
{
  /**
   * Create a new <code>ServerErrorException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ServerErrorException(final String errorMessage) {
    super(500, errorMessage);
  }

  /**
   * Create a new <code>ServerErrorException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ServerErrorException(final String errorMessage,
                              final Throwable cause) {
    super(500, errorMessage, cause);
  }
}
