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
 * This class represents a SCIM exception pertaining to the {@code HTTP 400}
 * error response code. This exception type should be thrown when a client sends
 * a JSON payload that cannot be parsed, is syntactically incorrect, or violates
 * the schema.
 * <br><br>
 * BadRequestExceptions, as well as {@link ResourceConflictException} objects,
 * are unique types of SCIM exceptions since they sometimes include an optional
 * {@code scimType} field. A {@code scimType} represents a "SCIM detail error
 * keyword", which succinctly describes the reason for the failure in camelcase
 * (e.g., {@code "noTarget"}). See the constants defined on this class, such as
 * {@link #NO_TARGET}, for more detail on what situations a given
 * {@code scimType} is typically used for.
 * <br><br>
 * The following is an example of a BadRequestException presented to a SCIM
 * client. This example error response indicates that the client tried to modify
 * an attribute that is defined as read-only in the schema.
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "400",
 *     "scimType": "mutability",
 *     "detail": "Read-only attributes cannot be modified."
 *   }
 * </pre>
 *
 * The BadRequestException in the above example can be created with the
 * following Java code. Note that this uses a static method to populate the
 * {@code scimType} field.
 * <pre>
 *   throw BadRequestException.mutability(
 *           "Read-only attributes cannot be modified.");
 * </pre>
 *
 * The following shows more examples for creating a BadRequestException:
 * <pre>
 *   throw BadRequestException.invalidPath("Null paths are not permitted.");
 *   throw BadRequestException.tooMany(
 *          "Too many results returned. Narrow the scope of the search.");
 *
 *   // Create a generic BadRequestException without a 'scimType'.
 *   throw new BadRequestException("Detailed message explaining the error.");
 * </pre>
 */
public class BadRequestException extends ScimException
{
  /**
   * The SCIM detailed error keyword that indicates the specified filter syntax
   * was invalid.
   */
  @NotNull
  public static final String INVALID_FILTER = "invalidFilter";

  /**
   * The SCIM detailed error keyword that indicates the specified filter yields
   * many more results than the server is willing to calculate or process.
   */
  @NotNull
  public static final String TOO_MANY = "tooMany";

  /**
   * The SCIM detailed error keyword that indicates one or more of the attribute
   * values is already in use or is reserved.
   */
  @NotNull
  public static final String UNIQUENESS = "uniqueness";

  /**
   * The SCIM detailed error keyword that indicates the attempted modification
   * is not compatible with the target attributes mutability or current state.
   */
  @NotNull
  public static final String MUTABILITY = "mutability";

  /**
   * The SCIM detailed error keyword that indicates the request body message
   * structure was invalid or did not conform to the request schema.
   */
  @NotNull
  public static final String INVALID_SYNTAX = "invalidSyntax";

  /**
   * The SCIM detailed error keyword that indicates the path attribute was
   * invalid or malformed.
   */
  @NotNull
  public static final String INVALID_PATH = "invalidPath";

  /**
   * The SCIM detailed error keyword that indicates the specified path did not
   * yield an attribute or attribute value that could be operated on.
   */
  @NotNull
  public static final String NO_TARGET = "noTarget";

  /**
   * The SCIM detailed error keyword that indicates a required value was
   * missing, or the value specified was not compatible with the operation or
   * attribute type.
   */
  @NotNull
  public static final String INVALID_VALUE = "invalidValue";

  /**
   * The SCIM detailed error keyword that indicates the specified SCIM
   * protocol version is not supported.
   */
  @NotNull
  public static final String INVALID_VERSION = "invalidVersion";


  /**
   * Create a generic BadRequestException without a {@code} scimType field.
   *
   * @param errorMessage  The error message for this SCIM exception.
   */
  public BadRequestException(@Nullable final String errorMessage)
  {
    this(errorMessage, (String) null);
  }

  /**
   * Create a new {@code BadRequestException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   */
  public BadRequestException(@Nullable final String errorMessage,
                             @Nullable final String scimType)
  {
    super(400, scimType, errorMessage);
  }

  /**
   * Create a new {@code BadRequestException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public BadRequestException(@Nullable final String errorMessage,
                             @Nullable final Throwable cause)
  {
    this(errorMessage, null, cause);
  }

  /**
   * Create a new {@code BadRequestException} from the provided information.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public BadRequestException(@Nullable final String errorMessage,
                             @Nullable final String scimType,
                             @Nullable final Throwable cause)
  {
    super(400, scimType, errorMessage, cause);
  }

  /**
   * Create a new {@code BadRequestException} from the provided information.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public BadRequestException(@NotNull final ErrorResponse scimError,
                             @Nullable final Throwable cause)
  {
    super(scimError, cause);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * invalidFilter SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException invalidFilter(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_FILTER);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * tooMany SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException tooMany(@Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, TOO_MANY);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * uniqueness SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException uniqueness(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, UNIQUENESS);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * mutability SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException mutability(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, MUTABILITY);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * invalidSyntax SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException invalidSyntax(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_SYNTAX);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * invalidPath SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException invalidPath(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_PATH);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * noTarget SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException noTarget(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, NO_TARGET);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * invalidValue SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException invalidValue(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_VALUE);
  }

  /**
   * Factory method to create a new {@code BadRequestException} with the
   * invalidVersion SCIM detailed error keyword.
   *
   * @param errorMessage  The error message for this SCIM exception.
   * @return The new {@code BadRequestException}.
   */
  @NotNull
  public static BadRequestException invalidVersion(
      @Nullable final String errorMessage)
  {
    return new BadRequestException(errorMessage, INVALID_VERSION);
  }
}
