/*
 * Copyright 2015-2017 UnboundID Corp.
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
 * Signals an authorization failure from the service provider.
 *
 * This exception corresponds to HTTP response code 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends ScimException
{
  /**
   * Create a new <code>UnauthorizedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public UnauthorizedException(final String errorMessage) {
    super(401, null, errorMessage);
  }

  /**
   * Create a new <code>UnauthorizedException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public UnauthorizedException(final String errorMessage,
                               final String scimType,
                               final Throwable cause) {
    super(401, scimType, errorMessage, cause);
  }

  /**
   * Create a new <code>UnauthorizedException</code> from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public UnauthorizedException(final ErrorResponse scimError,
                               final Throwable cause) {
    super(scimError, cause);
  }
}
