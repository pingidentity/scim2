/*
 * Copyright 2015-2024 Ping Identity Corporation
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
 * This class represents a SCIM exception pertaining to the
 * {@code HTTP 500 INTERNAL SERVER ERROR} error response code. This exception
 * type should be thrown when a SCIM service provider encounters an unexpected
 * error.
 * <br><br>
 * The following is an example of a ServerErrorException as seen by a SCIM client.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "500",
 *     "detail": "An unexpected error occurred."
 *   }
 * </pre>
 *
 * The ServerErrorException in the above example can be created with the
 * following Java code:
 * <pre>
 *   throw new ServerErrorException("An unexpected error occurred.");
 * </pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class ServerErrorException extends ScimException
{
  /**
   * Create a new {@code ServerErrorException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ServerErrorException(final String errorMessage)
  {
    super(500, null, errorMessage);
  }

  /**
   * Create a new {@code ServerErrorException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ServerErrorException(final String errorMessage,
                              final String scimType,
                              final Throwable cause)
  {
    super(500, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code ServerErrorException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ServerErrorException(final ErrorResponse scimError,
                              final Throwable cause)
  {
    super(scimError, cause);
  }
}
