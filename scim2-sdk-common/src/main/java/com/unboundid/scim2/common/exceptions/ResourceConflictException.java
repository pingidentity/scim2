/*
 * Copyright 2015-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.types.ETagConfig;

/**
 * This class represents a SCIM exception pertaining to the
 * {@code HTTP 409 CONFLICT} error response code. This exception type should be
 * thrown in the following scenarios:
 * <ul>
 *   <li> If the client provides an invalid ETag for a resource.
 *   <li> When a client attempts to create a new duplicate resource.
 * </ul>
 *
 * For an explanation on SCIM ETags, see {@link ETagConfig}. If a client
 * provides an ETag that does not match the current ETag of the SCIM resource,
 * then it likely indicates that the client is referencing an outdated version
 * of the resource. In this situation, the client should fetch the latest
 * version of the resource and attempt the operation again.
 * <br><br>
 *
 * The following is an example of a ResourceConflictException as seen by a SCIM
 * client. This example error response indicates that the client tried to create
 * a user whose unique username is already in use by another user.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "409",
 *     "scimType": "uniqueness",
 *     "detail": "The userName is already in use."
 *   }
 * </pre>
 *
 * Note that a userName uniqueness violation should include a {@code scimType}
 * value of {@code "uniqueness"}. The ResourceConflictException in the above
 * example can be created with the following Java code:
 * <pre><code>
 *   throw ResourceConflictException.uniqueness(
 *           "The userName is already in use.");
 * </code></pre>
 *
 * To create a generic ResourceConflictException that does not contain
 * {@code scimType}, the constructors on this class may be used instead:
 * <pre><code>
 *   throw new ResourceConflictException("Detailed error message.");
 * </code></pre>
 */
public class ResourceConflictException extends ScimException
{
  private static final int CONFLICT_HTTP_STATUS = 409;

  /**
   * The SCIM detailed error keyword that indicates a uniqueness conflict.
   */
  @NotNull
  public static final String UNIQUENESS = "uniqueness";

  /**
   * Returns the {@code 409 CONFLICT} HTTP status code value.
   *
   * @return  The HTTP status value.
   * @since 5.1.0
   */
  public static int statusInt()
  {
    return CONFLICT_HTTP_STATUS;
  }

  /**
   * Returns the {@code 409 CONFLICT} HTTP status code string value.
   *
   * @return  The HTTP status value as a string.
   * @since 5.1.0
   */
  @NotNull
  public static String status()
  {
    return "409";
  }

  /**
   * Create a new {@code ResourceConflictException} from the provided
   * information. This constructor sets the {@code scimType} field to
   * {@code null}.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ResourceConflictException(@Nullable final String errorMessage)
  {
    super(CONFLICT_HTTP_STATUS, null, errorMessage);
  }

  /**
   * Create a new {@code ResourceConflictException} from the provided
   * information. This constructor sets the {@code scimType} field to
   * {@code "uniqueness"}.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detail error keyword.
   */
  public ResourceConflictException(@Nullable final String errorMessage,
                                   @Nullable final String scimType)
  {
    super(CONFLICT_HTTP_STATUS, scimType, errorMessage);
  }

  /**
   * Create a new {@code ResourceConflictException} from the provided
   * information. This constructor sets the {@code scimType} field to
   * {@code "uniqueness"}.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return  The new {@code ResourceConflictException}.
   */
  @NotNull
  public static ResourceConflictException uniqueness(
      @Nullable final String errorMessage)
  {
    return new ResourceConflictException(errorMessage, UNIQUENESS);
  }

  /**
   * Create a new {@code ResourceConflictException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ResourceConflictException(@Nullable final String errorMessage,
                                   @Nullable final String scimType,
                                   @Nullable final Throwable cause)
  {
    super(CONFLICT_HTTP_STATUS, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code ResourceConflictException} from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ResourceConflictException(@NotNull final ErrorResponse scimError,
                                   @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
