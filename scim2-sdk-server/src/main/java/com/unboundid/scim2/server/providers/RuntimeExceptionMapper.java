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

package com.unboundid.scim2.server.providers;

import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.server.utils.ServerUtils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * A JAX-RS ExceptionMapper for to convert standard WebApplicationExceptions to
 * SCIM ErrorResponses.
 */
@Provider
public class RuntimeExceptionMapper implements
    ExceptionMapper<RuntimeException>
{
  @Context
  private Request request;
  @Context
  private HttpHeaders headers;

  /**
   * {@inheritDoc}
   */
  public Response toResponse(final RuntimeException exception)
  {
    ErrorResponse errorResponse;

    if(exception instanceof WebApplicationException)
    {
      if(exception.getCause() != null && exception.getCause()
          instanceof NoContentException)
      {
        errorResponse = new ErrorResponse(400);
        errorResponse.setScimType(BadRequestException.INVALID_SYNTAX);
        errorResponse.setDetail("No content provided. A valid SCIM object " +
            "represented as a json document is required");
      }
      else
      {
        errorResponse = new ErrorResponse(
            ((WebApplicationException) exception).getResponse().getStatus());
        errorResponse.setDetail(exception.getMessage());
      }
    }
    else
    {
      errorResponse = new ErrorResponse(500);
      errorResponse.setDetail(exception.toString());
    }

    return ServerUtils.setAcceptableType(
        Response.status(errorResponse.getStatus()).entity(errorResponse),
        headers.getAcceptableMediaTypes()).build();
  }
}
