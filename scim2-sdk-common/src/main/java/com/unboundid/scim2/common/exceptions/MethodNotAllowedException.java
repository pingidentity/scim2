/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * {@code HTTP 405 METHOD NOT ALLOWED} error response code. This exception type
 * should be thrown when a client sends a request to a valid endpoint, but
 * provides an unsupported REST method (e.g., {@code GET}, {@code POST}).
 * <br><br>
 *
 * The following is an example of a MethodNotAllowedException as seen by a SCIM
 * client.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "405",
 *     "detail": "The /.search endpoint only supports POST requests."
 *   }
 * </pre>
 *
 * The MethodNotAllowedException in the above example can be created with the
 * following Java code:
 * <pre><code>
 *   throw new MethodNotAllowedException(
 *           "The /.search endpoint only supports POST requests.");
 * </code></pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class MethodNotAllowedException extends ScimException
{
  private static final int METHOD_NOT_ALLOWED_CODE = 405;


  /**
   * Create a new {@code MethodNotAllowedException} from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public MethodNotAllowedException(@Nullable final String errorMessage)
  {
    super(METHOD_NOT_ALLOWED_CODE, errorMessage);
  }


  /**
   * Create a new {@code MethodNotAllowedException} from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public MethodNotAllowedException(@NotNull final ErrorResponse scimError,
                                   @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }
}
