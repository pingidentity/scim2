/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * Signals server failed to update as Resource changed on the server since last
 * retrieved
 *
 * This exception corresponds to HTTP response code
 * 412 PRECONDITION FAILED.
 */
public class PreconditionFailedException extends SCIMException
{
  private final String version;

  /**
   * Create a new <code>PreconditionFailedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public PreconditionFailedException(final String errorMessage) {
    super(412, errorMessage);
    this.version = null;
  }

  /**
   * Create a new <code>PreconditionFailedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public PreconditionFailedException(final String errorMessage,
                                     final Throwable cause) {
    super(412, errorMessage, cause);
    this.version = null;
  }

  /**
   * Create a new <code>PreconditionFailedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param version       The current version of the Resource.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public PreconditionFailedException(final String errorMessage,
                                     final String version,
                                     final Throwable cause) {
    super(412, errorMessage, cause);
    this.version = version;
  }

  /**
   * Retrieves the current version of the Resource.
   *
   * @return The current version of the Resource.
   */
  public String getVersion() {
    return version;
  }
}
