/*
 * Copyright 2015-2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.messages.ErrorResponse;

/**
 * This class represents a SCIM exception pertaining to the
 * {@code HTTP 401 UNAUTHORIZED} error response code. This exception type should
 * be thrown when a client provides an invalid or missing authorization header
 * in the request. This error most commonly occurs when a client does not have a
 * valid bearer token for their SCIM request.
 * <br><br>
 * The following is an example of an UnauthorizedException presented to a SCIM
 * client. This example error response indicates that the client request was
 * denied due to insufficient access rights.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "401",
 *     "detail": "The client is not authorized to perform the operation."
 *   }
 * </pre>
 *
 * The UnauthorizedException in the above example can be created with the
 * following Java code:
 * <pre>
 *   throw new UnauthorizedException(
 *           "The client is not authorized to perform the operation.");
 * </pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class UnauthorizedException extends ScimException
{
  /**
   * Create a new {@code UnauthorizedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public UnauthorizedException(@Nullable final String errorMessage)
  {
    super(401, null, errorMessage);
  }

  /**
   * Create a new {@code UnauthorizedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public UnauthorizedException(@Nullable final String errorMessage,
                               @Nullable final String scimType,
                               @Nullable final Throwable cause)
  {
    super(401, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code UnauthorizedException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public UnauthorizedException(@NotNull final ErrorResponse scimError,
                               @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
