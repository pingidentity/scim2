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
 * {@code HTTP 404 NOT FOUND} error response code. This exception type should be
 * thrown when a client attempts to access a resource or an endpoint that does
 * not exist.
 * <br><br>
 * The following is an example of a ResourceNotFoundException presented to a
 * SCIM client. This example error response indicates that the client referenced
 * a SCIM resource that does not exist.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "404",
 *     "detail": "The requested resource was not found."
 *   }
 * </pre>
 *
 * The ResourceNotFoundException in the above example can be created with the
 * following Java code:
 * <pre>
 *   throw new ResourceNotFoundException("The requested resource was not found.");
 * </pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class ResourceNotFoundException extends ScimException
{
  /**
   * Create a new {@code ResourceNotFoundException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ResourceNotFoundException(@Nullable final String errorMessage)
  {
    super(404, null, errorMessage);
  }

  /**
   * Create a new {@code ResourceNotFoundException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ResourceNotFoundException(@Nullable final String errorMessage,
                                   @Nullable final String scimType,
                                   @Nullable final Throwable cause)
  {
    super(404, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code ResourceNotFoundException} from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ResourceNotFoundException(@NotNull final ErrorResponse scimError,
                                   @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
