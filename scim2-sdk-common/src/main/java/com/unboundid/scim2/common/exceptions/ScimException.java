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
 * This superclass defines the base exception class for all SCIM error types. A
 * ScimException contains a SCIM {@link ErrorResponse}, which represents the
 * JSON error that should be displayed to a SCIM client. This field can be
 * obtained by calling {@link #getScimError()}. An example error response is
 * provided below:
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status": "400",
 *     "scimType": "invalidFilter",
 *     "detail": "The provided filter could not be parsed."
 *   }
 * </pre>
 *
 * As seen in the example above, SCIM errors sometimes include an optional
 * {@code scimType} field. The SCIM specification explicitly mentions this field
 * only for {@link BadRequestException} and {@link ResourceConflictException}
 * errors. Other exception types may include this field if they wish, but these
 * usages are optional and are not standardized.
 * <br><br>
 * The following HTTP error codes correspond to a subclass of ScimException:
 * <ul>
 *   <li> {@code HTTP 304}: {@link NotModifiedException}
 *   <li> {@code HTTP 400}: {@link BadRequestException}
 *   <li> {@code HTTP 401}: {@link UnauthorizedException}
 *   <li> {@code HTTP 403}: {@link ForbiddenException}
 *   <li> {@code HTTP 404}: {@link ResourceNotFoundException}
 *   <li> {@code HTTP 405}: {@link MethodNotAllowedException}
 *   <li> {@code HTTP 409}: {@link ResourceConflictException}
 *   <li> {@code HTTP 412}: {@link PreconditionFailedException}
 *   <li> {@code HTTP 500}: {@link ServerErrorException}
 *   <li> {@code HTTP 501}: {@link NotImplementedException}
 * </ul>
 * <br><br>
 * To create a SCIM exception, use the subclasses of ScimException whenever
 * possible. For example:
 * <pre>
 *   throw new UnauthorizedException("Permission denied");
 *   throw new ServerErrorException("An unexpected error occurred.");
 * </pre>
 *
 * Some classes contain static helper methods to construct exceptions with
 * {@code scimType} fields.
 * <pre>
 *   throw BadRequestException.invalidFilter(
 *           "The provided filter could not be parsed.");
 * </pre>
 *
 * To create a SCIM exception with a custom HTTP error code that does not have
 * a dedicated class defined in the SCIM SDK, the constructors on this class may
 * be used directly:
 * <pre>
 *   throw new ScimException(429, "Detailed error message");
 * </pre>
 *
 * For more details on a particular exception type, see the class-level
 * documentation for that exception class (e.g., {@link BadRequestException}).
 *
 * @see ErrorResponse
 */
public class ScimException extends Exception
{
  /**
   * This field contains the core SCIM error information. See the class
   * documentation for {@link ErrorResponse} for more details.
   */
  @NotNull
  private final ErrorResponse scimError;


  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ScimException(final int statusCode,
                       @Nullable final String errorMessage)
  {
    this(statusCode, null, errorMessage);
  }

  /**
   * Create a new SCIM exception from the provided information.
   * <p>
   * For standard exception types, it is generally encouraged to use the
   * subclasses of ScimException. For example, to return an {@code HTTP 500}
   * error, use {@link ServerErrorException#ServerErrorException(String)}.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword. This is optional
   *                      and may be {@code null}.
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ScimException(final int statusCode,
                       @Nullable final String scimType,
                       @Nullable final String errorMessage)
  {
    scimError = new ErrorResponse(statusCode);
    scimError.setScimType(scimType);
    scimError.setDetail(errorMessage);
  }

  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword. This is optional
   *                      and may be {@code null}.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ScimException(final int statusCode,
                       @Nullable final String scimType,
                       @Nullable final String errorMessage,
                       @Nullable final Throwable cause)
  {
    super(cause);

    scimError = new ErrorResponse(statusCode);
    scimError.setScimType(scimType);
    scimError.setDetail(errorMessage);
  }

  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param scimError     The SCIM Error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   */
  public ScimException(@NotNull final ErrorResponse scimError,
                       @Nullable final Throwable cause)
  {
    super(cause);

    this.scimError = scimError;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public String getMessage()
  {
    return scimError.getDetail();
  }

  /**
   * Retrieves the ErrorResponse wrapped by this exception.
   *
   * @return the ErrorResponse wrapped by this exception.
   */
  @NotNull
  public ErrorResponse getScimError()
  {
    return scimError;
  }

  /**
   * Create the appropriate ScimException from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @return The appropriate ScimException from the provided information.
   */
  @NotNull
  public static ScimException createException(
      final int statusCode, @Nullable final String errorMessage)
  {
    return createException(statusCode, errorMessage, null);
  }

  /**
   * Create the appropriate ScimException from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   * @return The appropriate ScimException from the provided information.
   */
  @NotNull
  public static ScimException createException(final int statusCode,
                                              @Nullable final String errorMessage,
                                              @Nullable final Exception cause)
  {
    ErrorResponse scimError = new ErrorResponse(statusCode);
    scimError.setDetail(errorMessage);
    return createException(scimError, cause);
  }

  /**
   * Create the appropriate ScimException from a SCIM error response.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method). A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.
   * @return The appropriate ScimException from the provided information.
   */
  @NotNull
  public static ScimException createException(
      @NotNull final ErrorResponse scimError, @Nullable final Exception cause)
  {
    return switch (scimError.getStatus())
    {
      case 304 -> new NotModifiedException(scimError, null, cause);
      case 400 -> new BadRequestException(scimError, cause);
      case 401 -> new UnauthorizedException(scimError, cause);
      case 403 -> new ForbiddenException(scimError, cause);
      case 404 -> new ResourceNotFoundException(scimError, cause);
      case 405 -> new MethodNotAllowedException(scimError, cause);
      case 409 -> new ResourceConflictException(scimError, cause);
      case 412 -> new PreconditionFailedException(scimError, null, cause);
      case 500 -> new ServerErrorException(scimError, cause);
      case 501 -> new NotImplementedException(scimError, cause);
      default -> new ScimException(scimError, cause);
    };
  }
}
