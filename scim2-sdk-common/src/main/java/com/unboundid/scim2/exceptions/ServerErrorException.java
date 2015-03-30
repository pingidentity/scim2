/*
 * Copyright 2015 UnboundID Corp.
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

package com.unboundid.scim2.exceptions;

/**
 * Signals an internal error from the service provider.
 *
 * This exception corresponds to HTTP response code 500 INTERNAL SERVER ERROR.
 */
public class ServerErrorException extends SCIMException
{
  /**
   * Create a new <code>ServerErrorException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ServerErrorException(final String errorMessage) {
    super(500, errorMessage);
  }

  /**
   * Create a new <code>ServerErrorException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ServerErrorException(final String errorMessage,
                              final Throwable cause) {
    super(500, errorMessage, cause);
  }
}
