/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * Signals the specified resource; e.g., User, does not exist.
 *
 * This exception corresponds to HTTP response code 404 NOT FOUND.
 */
public class ResourceNotFoundException extends SCIMException
{
  /**
   * Create a new <code>ResourceNotFoundException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ResourceNotFoundException(final String errorMessage) {
    super(404, errorMessage);
  }

  /**
   * Create a new <code>ResourceNotFoundException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ResourceNotFoundException(final String errorMessage,
                                   final Throwable cause) {
    super(404, errorMessage, cause);
  }
}
