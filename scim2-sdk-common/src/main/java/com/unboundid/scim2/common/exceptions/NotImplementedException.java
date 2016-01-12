/*
 * Copyright 2015-2016 UnboundID Corp.
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

package com.unboundid.scim2.common.exceptions;

import com.unboundid.scim2.common.messages.ErrorResponse;

/**
 * Signals the service provider does nto support the request operation;
 * e.g., PATCH.
 *
 * This exception corresponds to HTTP response code 501 NOT IMPLEMENTED.
 */
public class NotImplementedException extends ScimException
{
  /**
   * Create a new <code>NotImplementedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public NotImplementedException(final String errorMessage) {
    super(501, null, errorMessage);
  }

  /**
   * Create a new <code>NotImplementedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public NotImplementedException(final String errorMessage,
                                 final String scimType,
                                 final Throwable cause) {
    super(501, scimType, errorMessage, cause);
  }

  /**
   * Create a new <code>NotImplementedException</code> from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public NotImplementedException(final ErrorResponse scimError,
                                 final Throwable cause) {
    super(scimError, cause);
  }
}
