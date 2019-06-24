/*
 * Copyright 2015-2019 Ping Identity Corporation
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

/**
 * Signals an error while looking up resources and attributes.
 *
 * This exception corresponds to HTTP response code 400 BAD REQUEST.
 */
public class BadRequestException extends ScimException
{
  /**
   * The SCIM detailed error keyword that indicates the specified filter syntax
   * was invalid.
   */
  public static final String INVALID_FILTER = "invalidFilter";

  /**
   * The SCIM detailed error keyword that indicates the specified filter yields
   * many more results than the server is willing to calculate or process.
   */
  public static final String TOO_MANY = "tooMany";

  /**
   * The SCIM detailed error keyword that indicates one or more of the attribute
   * values is already in use or is reserved.
   */
  public static final String UNIQUENESS = "uniqueness";

  /**
   * The SCIM detailed error keyword that indicates the attempted modification
   * is not compatible with the target attributes mutability or current state.
   */
  public static final String MUTABILITY = "mutability";

  /**
   * The SCIM detailed error keyword that indicates the request body message
   * structure was invalid or did not conform to the request schema.
   */
  public static final String INVALID_SYNTAX = "invalidSyntax";

  /**
   * The SCIM detailed error keyword that indicates the the path attribute was
   * invalid or malformed.
   */
  public static final String INVALID_PATH = "invalidPath";

  /**
   * The SCIM detailed error keyword that indicates the specified path did not
   * yield an attribute or attribute value that could be operated on.
   */
  public static final String NO_TARGET = "noTarget";

  /**
   * The SCIM detailed error keyword that indicates a required value was
   * missing, or the value specified was not compatible with the operation or
   * attribute type.
   */
  public static final String INVALID_VALUE = "invalidValue";

  /**
   * The SCIM detailed error keyword that indicates the specified SCIM
   * protocol version is not supported.
   */
  public static final String INVALID_VERSION = "invalidVersion";

  /**
   * Create a new <code>BadRequestException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   */
  public BadRequestException(final String errorMessage,
                             final String scimType) {
    super(400, scimType, errorMessage);
  }

  /**
   * Create a new <code>BadRequestException</code> from the provided
   * information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public BadRequestException(final String errorMessage,
                             final String scimType,
                             final Throwable cause) {
    super(400, scimType, errorMessage, cause);
  }

  /**
   * Create a new <code>BadRequestException</code> from the provided
   * information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public BadRequestException(final ErrorResponse scimError,
                             final Throwable cause) {
    super(scimError, cause);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * invalidFilter SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException invalidFilter(final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_FILTER);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * tooMany SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException tooMany(final String errorMessage)
  {
    return new BadRequestException(errorMessage, TOO_MANY);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * uniqueness SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException uniqueness(final String errorMessage)
  {
    return new BadRequestException(errorMessage, UNIQUENESS);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * mutability SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException mutability(final String errorMessage)
  {
    return new BadRequestException(errorMessage, MUTABILITY);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * invalidSyntax SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException invalidSyntax(final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_SYNTAX);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * invalidPath SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException invalidPath(final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_PATH);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * noTarget SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException noTarget(final String errorMessage)
  {
    return new BadRequestException(errorMessage, NO_TARGET);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * invalidValue SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException invalidValue(final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_VALUE);
  }

  /**
   * Factory method to create a new <code>BadRequestException</code> with the
   * invalidVersion SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new <code>BadRequestException</code>.
   */
  public static BadRequestException invalidVersion(final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_VERSION);
  }
}
