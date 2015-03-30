/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * Signals the specified version number does not match the resource's latest
 * version number or a Service Provider refused to create a new,
 * duplicate resource.
 *
 * This exception corresponds to HTTP response code 409 CONFLICT.
 */
public class ResourceConflictException extends SCIMException
{
  /**
   * Create a new <code>ResourceConflictException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ResourceConflictException(final String errorMessage) {
    super(409, errorMessage);
  }

  /**
   * Create a new <code>ResourceConflictException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ResourceConflictException(final String errorMessage,
                                   final Throwable cause) {
    super(409, errorMessage, cause);
  }
}
