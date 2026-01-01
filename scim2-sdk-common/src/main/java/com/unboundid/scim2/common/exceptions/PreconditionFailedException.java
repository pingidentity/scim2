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
 * {@code HTTP 412 PRECONDITION FAILED} error response code. This exception type
 * should be thrown when a client tries to update a resource and provides an
 * outdated SCIM ETag, indicating that the resource has changed since the last
 * time they viewed it. For more information on SCIM ETags, see the class-level
 * documentation of {@link ETagConfig}.
 * <br><br>
 *
 * The following is an example of a PreconditionFailedException as seen by a
 * SCIM client.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "412",
 *     "detail": "Failed to update. The resource changed on the server."
 *   }
 * </pre>
 *
 * The PreconditionFailedException in the above example can be created with the
 * following Java code:
 * <pre><code>
 *   throw new PreconditionFailedException(
 *          "Failed to update. The resource changed on the server.");
 * </code></pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class PreconditionFailedException extends ScimException
{
  /**
   * Represents the ETag version value of the resource.
   */
  @Nullable
  private final String version;

  /**
   * Create a new {@code PreconditionFailedException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public PreconditionFailedException(@Nullable final String errorMessage)
  {
    super(412, null, errorMessage);
    this.version = null;
  }

  /**
   * Create a new {@code PreconditionFailedException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public PreconditionFailedException(@Nullable final String errorMessage,
                                     @Nullable final Throwable cause)
  {
    super(412, null, errorMessage, cause);
    this.version = null;
  }

  /**
   * Create a new {@code PreconditionFailedException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param version       The current version of the Resource.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public PreconditionFailedException(@Nullable final String errorMessage,
                                     @Nullable final String scimType,
                                     @Nullable final String version,
                                     @Nullable final Throwable cause)
  {
    super(412, scimType, errorMessage, cause);
    this.version = version;
  }

  /**
   * Create a new {@code PreconditionFailedException} from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param version       The current version of the Resource.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public PreconditionFailedException(@NotNull final ErrorResponse scimError,
                                     @Nullable final String version,
                                     @Nullable final Throwable cause)
  {
    super(scimError, cause);
    this.version = version;
  }

  /**
   * Retrieves the current version of the Resource.
   *
   * @return The current version of the Resource.
   */
  @Nullable
  public String getVersion()
  {
    return version;
  }
}
