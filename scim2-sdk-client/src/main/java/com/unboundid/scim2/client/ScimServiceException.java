/*
 * Copyright 2016-2023 Ping Identity Corporation
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

package com.unboundid.scim2.client;

import com.unboundid.scim2.common.exceptions.ScimException;

/**
 * This exception is thrown when problems occur in the {@link ScimService}. This
 * class allows a client application to differentiate between errors that
 * arise on the client side from errors that come from the server.
 */
public class ScimServiceException extends ScimException
{
  /**
   * Create a new ScimServiceException from the provided information.
   *
   * @param statusCode    The HTTP status code for this exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */

  public ScimServiceException(final int statusCode,
                              final String errorMessage,
                              final Throwable cause)
  {
    super(statusCode, null, errorMessage, cause);
  }
}
