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
 * This exception type can have a {@code scimType} value of {@code sensitive}.
 * See {@link #sensitive(String)} for more information.
 */
public class ForbiddenException extends ScimException
{
  /**
   * The SCIM detailed error keyword that indicates the provided filter in a
   * GET search request contained sensitive or confidential information. See
   * {@link #sensitive(String)} for more information.
   */
  @NotNull
  public static final String SENSITIVE = "sensitive";

  /**
   * Create a new {@code ForbiddenException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ForbiddenException(@Nullable final String errorMessage)
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
  public ForbiddenException(@Nullable final String errorMessage,
                            @Nullable final String scimType,
                            @Nullable final Throwable cause)
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
  public ForbiddenException(@NotNull final ErrorResponse scimError,
                            @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }

  /**
   * Factory method to create a new {@code ForbiddenException} with the
   * {@code sensitive} SCIM detailed error keyword.
   * <br><br>
   *
   * This {@code scimType} should be used when a client issues a GET search
   * request with data in the URI that is potentially sensitive or confidential.
   * Requesting sensitive information in this manner could cause a breach of
   * security or confidentiality through leakage in web browsers or server logs.
   * For this reason, a client that receives this error response should re-issue
   * their search as a POST search. For more information on POST searches, see
   * {@link com.unboundid.scim2.common.messages.SearchRequest}.
   *
   * @param errorMsg  The error message for this SCIM exception.
   * @return  The new {@code ForbiddenException}.
   *
   * @since 4.1.0
   */
  @NotNull
  public static ForbiddenException sensitive(@Nullable final String errorMsg)
  {
    return new ForbiddenException(errorMsg, SENSITIVE, null);
  }
}
