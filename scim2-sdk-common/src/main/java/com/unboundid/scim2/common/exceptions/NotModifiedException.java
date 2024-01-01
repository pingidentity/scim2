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
import com.unboundid.scim2.common.types.ETagConfig;

/**
 * This class represents a SCIM exception pertaining to the
 * {@code HTTP 304 NOT MODIFIED} error response code. This exception class is
 * related to the use of SCIM ETags, which are discussed in
 * {@link ETagConfig}. Note that while this class constructs a JSON response
 * body like other implementations of {@link ScimException}, it is expected that
 * a SCIM service provider returns an empty JSON body when returning this error
 * code.
 * <br><br>
 * If SCIM ETags are supported by a service provider, a client may request a
 * resource only if it has been modified. To perform this, a client must issue
 * a {@code GET} request with an {@code If-None-Match} HTTP header that includes
 * the ETag. If the current resource's {@code meta.version} field matches the
 * ETag provided by the client, then the service provider should return a
 * NotModifiedException if this is not expected.
 * <br><br>
 * A NotModifiedException can be created with the following Java code:
 * <pre>
 *   throw new NotModifiedException("The resource has not been modified.");
 * </pre>
 *
 * This exception type generally does not have a {@code scimType} value.
 */
public class NotModifiedException extends ScimException
{
  private final String version;

  /**
   * Create a new {@code NotModifiedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public NotModifiedException(final String errorMessage)
  {
    super(304, null, errorMessage);
    version = null;
  }

  /**
   * Create a new {@code NotModifiedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public NotModifiedException(final String errorMessage,
                              final Throwable cause)
  {
    super(304, null, errorMessage, cause);
    version = null;
  }

  /**
   * Create a new {@code NotModifiedException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param version       The current version of the Resource.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public NotModifiedException(final String errorMessage,
                              final String scimType,
                              final String version,
                              final Throwable cause)
  {
    super(304, scimType, errorMessage, cause);
    this.version = version;
  }

  /**
   * Create a new {@code NotModifiedException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param version       The current version of the Resource.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public NotModifiedException(final ErrorResponse scimError,
                              final String version,
                              final Throwable cause)
  {
    super(scimError, cause);
    this.version = version;
  }


  /**
   * Retrieves the version of the Resource.
   *
   * @return The current version of the Resource.
   */
  public String getVersion()
  {
    return version;
  }
}
