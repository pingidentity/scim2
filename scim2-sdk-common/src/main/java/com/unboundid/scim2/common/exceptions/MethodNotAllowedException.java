/*
 * Copyright 2015-2019 Ping Identity Corporation
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
 * Indicates that the HTTP method requested is not allowed for the
 * requested resource.
 *
 * This exception corresponds to HTTP response code 405 METHOD NOT ALLOWED.
 */
public class MethodNotAllowedException extends ScimException
{



  /**
   * Create a new <code>MethodNotAllowedException</code> from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public MethodNotAllowedException(final ErrorResponse scimError,
                            final Throwable cause) {
    super(scimError, cause);
  }



}
