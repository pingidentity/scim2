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
 * {@code HTTP 403 FORBIDDEN} error response code. This exception type should be
 * thrown when a client provides valid credentials, but attempts an operation
 * that they are not authorized to use. This error indicates that the client has
 * insufficient access rights, or that the operation is not permitted by the
 * service provider.
 * <br><br>
 * The following is an example of a ForbiddenException presented to a SCIM
 * client.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "403",
 *     "detail": "You do not have access to this resource."
 *   }
 * </pre>
 *
 * The ForbiddenException in the above example can be created with the following
 * Java code:
 * <pre>
 *   throw new ForbiddenException("You do not have access to this resource.");
 * </pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class ForbiddenException extends ScimException
{
  /**
   * Create a new {@code ForbiddenException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ForbiddenException(final String errorMessage)
  {
    super(403, null, errorMessage);
  }

  /**
   * Create a new {@code ForbiddenException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword. This should generally
   *                      be {@code null} for ForbiddenExceptions.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ForbiddenException(final String errorMessage,
                            final String scimType,
                            final Throwable cause)
  {
    super(403, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code ForbiddenException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ForbiddenException(final ErrorResponse scimError,
                            final Throwable cause)
  {
    super(scimError, cause);
  }
}
