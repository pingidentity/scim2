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
 * This class represents a SCIM exception pertaining to the
 * {@code HTTP 429 TOO MANY REQUESTS} error response code. This exception type
 * is thrown when a client exceeds a defined rate limit of an HTTP service, and
 * occurs commonly when a client sends excessive requests within a brief period
 * of time. By denying excessive request volumes, the service defends against
 * expensive traffic that could affect availability. This also defends against
 * malicious actors that attempt to overwhelm the service with attacks such as a
 * distributed denial of service.
 * <br><br>
 *
 * The following is an example of a rate limit exceeded exception presented to
 * a SCIM client:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *   "detail": "Too many requests. Please try again later.",
 *   "status": "429"
 * }
 * </pre>
 *
 * This exception can be created with the following Java code:
 * <pre><code>
 *   throw new RateLimitException("Too many requests. Please try again later.");
 * </code></pre>
 *
 * This exception type does not have a {@code scimType} value.
 *
 * @since 5.1.0
 */
public class RateLimitException extends ScimException
{
  private static final int TOO_MANY_REQUESTS_HTTP_STATUS = 429;

  /**
   * Returns the {@code 429 TOO MANY REQUESTS} HTTP status code value.
   *
   * @return  The HTTP status value.
   */
  public static int statusInt()
  {
    return TOO_MANY_REQUESTS_HTTP_STATUS;
  }

  /**
   * Returns the {@code 429 TOO MANY REQUESTS} HTTP status code string value.
   *
   * @return  The HTTP status value as a string.
   */
  @NotNull
  public static String status()
  {
    return "429";
  }

  /**
   * Create a new {@code RateLimitException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public RateLimitException(@Nullable final String errorMessage)
  {
    super(TOO_MANY_REQUESTS_HTTP_STATUS, errorMessage);
  }

  /**
   * Create a new {@code RateLimitException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public RateLimitException(@NotNull final ErrorResponse scimError,
                            @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
