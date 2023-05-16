/*
 * Copyright 2015-2023 Ping Identity Corporation
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
 * This class is the base class for all custom checked exceptions defined in
 * the SCIM SDK. This is basically an exception wrapper around
 * ScimErrorResource.
 */
public class ScimException extends Exception
{
  private final ErrorResponse scimError;

  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param errorMessage  The error message for this SCIM exception.
   */
  public ScimException(final int statusCode, final String scimType,
                       final String errorMessage)
  {
    scimError = new ErrorResponse(statusCode);
    scimError.setScimType(scimType);
    scimError.setDetail(errorMessage);
  }



  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ScimException(final int statusCode, final String scimType,
                       final String errorMessage,
                       final Throwable cause)
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
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ScimException(final ErrorResponse scimError, final Throwable cause)
  {
    super(cause);

    this.scimError = scimError;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage()
  {
    return scimError.getDetail();
  }

  /**
   * Retrieves the ScimErrorResource wrapped by this exception.
   *
   * @return the ScimErrorResource wrapped by this exception.
   */
  public ErrorResponse getScimError()
  {
    return scimError;
  }

  /**
   * Create the appropriate SCIMException from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @return The appropriate SCIMException from the provided information.
   */
  public static ScimException createException(final int statusCode,
                                              final String errorMessage)
  {
    return createException(statusCode, errorMessage, null);
  }

  /**
   * Create the appropriate SCIMException from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   * @return The appropriate SCIMException from the provided information.
   */
  public static ScimException createException(final int statusCode,
                                              final String errorMessage,
                                              final Exception cause)
  {
    ErrorResponse scimError = new ErrorResponse(statusCode);
    scimError.setDetail(errorMessage);
    return createException(scimError, cause);
  }

  /**
   * Create the appropriate SCIMException from a SCIM error response.
   *
   * @param scimError     The SCIM error response.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A {@code null} value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   * @return The appropriate SCIMException from the provided information.
   */
  public static ScimException createException(final ErrorResponse scimError,
                                              final Exception cause)
  {
    switch(scimError.getStatus())
    {
//      case -1  : return new ConnectException(errorMessage);
      case 304 : return new NotModifiedException(scimError, null, cause);
      case 400 : return new BadRequestException(scimError, cause);
      case 401 : return new UnauthorizedException(scimError, cause);
      case 403 : return new ForbiddenException(scimError, cause);
      case 404 : return new ResourceNotFoundException(scimError, cause);
      case 405 : return new MethodNotAllowedException(scimError, cause);
      case 409 : return new ResourceConflictException(scimError, cause);
      case 412 : return new PreconditionFailedException(scimError, null, cause);
//      case 413 : return new RequestEntityTooLargeException(errorMessage);
      case 500 : return new ServerErrorException(scimError, cause);
//      case 501 : return new UnsupportedOperationException(errorMessage);
      default : return new ScimException(scimError, cause);
    }
  }

}
