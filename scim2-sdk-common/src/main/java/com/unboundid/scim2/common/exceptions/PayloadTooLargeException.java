/*
 * Copyright 2026 Ping Identity Corporation
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
 * Copyright 2026 Ping Identity Corporation
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
 * This class represents a SCIM exception pertaining to the {@code HTTP 413}
 * error response code. This exception type should be thrown when a client sends
 * a request with a JSON body that exceeds the threshold for an acceptable
 * payload size. This is most likely to occur when processing bulk operations
 * that contain many different requests, but can also occur for other general
 * endpoints.
 * <br><br>
 *
 * The following is an example of a PayloadTooLargeException presented to a
 * SCIM client:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *   "status": "413",
 *   "detail": "The size of the request exceeds the maxPayloadSize (1048576)"
 * }
 * </pre>
 *
 * To create the above error as an exception, use the following Java code:
 * <pre><code>
 *   throw new PayloadTooLargeException(
 *       "The size of the request exceeds the maxPayloadSize (" + MAX_SIZE + ")"
 *   );
 * </code></pre>
 *
 * This exception should never have a {@code scimType}.
 */
public class PayloadTooLargeException extends ScimException
{
  private static final int errorCode = 413;

  /**
   * Create a new {@code PayloadTooLargeException} with the provided message.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public PayloadTooLargeException(@Nullable final String errorMessage)
  {
    super(errorCode, errorMessage);
  }

  /**
   * Create a new {@code PayloadTooLargeException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value is
   *                      permitted, and indicates that the cause is nonexistent
   *                      or unknown.
   */
  public PayloadTooLargeException(@Nullable final String errorMessage,
                                  @Nullable final Throwable cause)
  {
    super(errorCode, null, errorMessage, cause);
  }

  /**
   * Create a new {@code PayloadTooLargeException} from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value is
   *                      permitted, and indicates that the cause is nonexistent
   *                      or unknown.
   */
  public PayloadTooLargeException(@NotNull final ErrorResponse scimError,
                                  @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
