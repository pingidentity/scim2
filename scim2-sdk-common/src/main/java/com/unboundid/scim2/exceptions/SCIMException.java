/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.exceptions;

/**
 * This class is the base class for all custom checked exceptions defined in
 * the SCIM SDK.
 */
public class SCIMException extends Exception
{
  /**
   * The HTTP status code for this SCIM exception.
   */
  private final int statusCode;



  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   */
  public SCIMException(final int statusCode, final String errorMessage)
  {
    super(errorMessage);

    this.statusCode = statusCode;
  }



  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public SCIMException(final int statusCode, final String errorMessage,
                          final Throwable cause)
  {
    super(errorMessage, cause);

    this.statusCode = statusCode;
  }



  /**
   * Retrieve the HTTP status code for this SCIM exception.
   *
   * @return  The HTTP status code for this SCIM exception.
   */
  public int getStatusCode()
  {
    return statusCode;
  }

  /**
   * Create the appropriate SCIMException from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param errorMessage  The error message for this SCIM exception.
   * @return The appropriate SCIMException from the provided information.
   */
  public static SCIMException createException(final int statusCode,
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
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   * @return The appropriate SCIMException from the provided information.
   */
  public static SCIMException createException(final int statusCode,
                                              final String errorMessage,
                                              final Exception cause)
  {
    switch(statusCode)
    {
//      case -1  : return new ConnectException(errorMessage);
      case 304 : return new NotModifiedException(errorMessage);
      case 400 : return new InvalidResourceException(errorMessage);
      case 401 : return new UnauthorizedException(errorMessage);
      case 403 : return new ForbiddenException(errorMessage);
      case 404 : return new ResourceNotFoundException(errorMessage);
      case 409 : return new ResourceConflictException(errorMessage);
      case 412 : return new PreconditionFailedException(errorMessage);
//      case 413 : return new RequestEntityTooLargeException(errorMessage);
      case 500 : return new ServerErrorException(errorMessage);
//      case 501 : return new UnsupportedOperationException(errorMessage);
      default : return new SCIMException(statusCode, errorMessage, cause);
    }
  }

}
