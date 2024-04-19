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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.messages.ErrorResponse;

/**
 * This class represents a SCIM exception pertaining to the
 * {@code HTTP 501 NOT IMPLEMENTED} error response code. This exception type
 * should be thrown when a client attempts to perform an operation that the
 * service provider does not support, such as attempting a PATCH operation. This
 * exception can also be used if a client accesses an endpoint that is not
 * supported or defined.
 * <br><br>
 * The following is an example of a NotImplementedException as seen by a SCIM
 * client. This example error response indicates that the client tried to access
 * an endpoint that was not supported.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "501",
 *     "detail": "The requested endpoint is not supported."
 *   }
 * </pre>
 *
 * The NotImplementedException in the above example can be created with the
 * following Java code:
 * <pre>
 *   throw new NotImplementedException("The requested endpoint is not supported.");
 * </pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class NotImplementedException extends ScimException
{
  /**
   * Create a new {@code NotImplementedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public NotImplementedException(@Nullable final String errorMessage)
  {
    super(501, null, errorMessage);
  }

  /**
   * Create a new {@code NotImplementedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword. This should generally
   *                      be {@code null} for NotImplementedExceptions.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public NotImplementedException(@Nullable final String errorMessage,
                                 @Nullable final String scimType,
                                 @Nullable final Throwable cause)
  {
    super(501, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code NotImplementedException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public NotImplementedException(@NotNull final ErrorResponse scimError,
                                 @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
