/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * Signals the Resource has not changed on the server since last retrieved
 *
 * This exception corresponds to HTTP response code
 * 304 NOT MODIFIED.
 */
public class NotModifiedException extends SCIMException
{
  private final String version;

  /**
   * Create a new <code>NotModifiedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public NotModifiedException(final String errorMessage) {
    super(304, errorMessage);
    version = null;
  }

  /**
   * Create a new <code>NotModifiedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public NotModifiedException(final String errorMessage,
                              final Throwable cause) {
    super(304, errorMessage, cause);
    version = null;
  }

  /**
   * Create a new <code>NotModifiedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param version       The current version of the Resource.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public NotModifiedException(final String errorMessage,
                              final String version,
                              final Throwable cause) {
    super(304, errorMessage, cause);
    this.version = version;
  }

  /**
   * Retrieves the version of the Resource.
   *
   * @return The current version of the Resource.
   */
  public String getVersion() {
    return version;
  }
}
