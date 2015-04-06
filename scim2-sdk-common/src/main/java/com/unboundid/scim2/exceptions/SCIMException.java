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
 * This class is the base class for all custom checked exceptions defined in
 * the SCIM SDK.
 */
public class ScimException extends Exception
{
  /**
   * The HTTP status code for this SCIM exception.
   */
  private final int statusCode;

  private final String scimType;

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
    super(errorMessage);

    this.scimType = scimType;
    this.statusCode = statusCode;
  }



  /**
   * Create a new SCIM exception from the provided information.
   *
   * @param statusCode    The HTTP status code for this SCIM exception.
   * @param scimType      The SCIM detailed error keyword.
   * @param errorMessage  The error message for this SCIM exception.
   * @param cause         The cause (which is saved for later retrieval by the
   *                      {@link #getCause()} method).  (A <tt>null</tt> value
   *                      is permitted, and indicates that the cause is
   *                      nonexistent or unknown.)
   */
  public ScimException(final int statusCode, final String scimType,
                       final String errorMessage,
                       final Throwable cause)
  {
    super(errorMessage, cause);

    this.scimType = scimType;
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
   * Retrieve the SCIM detailed error keyword.
   *
   * @return  The SCIM detailed error keyword.
   */
  public String getScimType()
  {
    return scimType;
  }

}
