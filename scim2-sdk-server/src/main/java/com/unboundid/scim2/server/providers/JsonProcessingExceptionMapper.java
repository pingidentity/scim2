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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.exceptions.ServerErrorException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.server.utils.ServerUtils;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * A JAX-RS ExceptionMapper for to convert Jackson JsonProcessingException to
 * SCIM ErrorResponses.
 */
@Provider
public class JsonProcessingExceptionMapper implements
    ExceptionMapper<JsonProcessingException>
{
  @Context
  private Request request;
  @Context
  private HttpHeaders headers;

  /**
   * {@inheritDoc}
   */
  public Response toResponse(final JsonProcessingException exception)
  {
    ErrorResponse errorResponse;
    if((exception instanceof JsonParseException) ||
        (exception instanceof JsonMappingException))
    {
      StringBuilder builder = new StringBuilder();
      builder.append("Unable to parse request: ");
      builder.append(exception.getOriginalMessage());
      if(exception.getLocation() != null)
      {
        builder.append(" at line: ");
        builder.append(exception.getLocation().getLineNr());
        builder.append(", column: ");
        builder.append(exception.getLocation().getColumnNr());
      }
      errorResponse =
          BadRequestException.invalidSyntax(builder.toString()).getScimError();
    }
    else
    {
      if(exception.getCause() != null &&
          exception.getCause() instanceof ScimException)
      {
        errorResponse = ((ScimException) exception.getCause()).getScimError();
      }
      else
      {
        errorResponse =
            new ServerErrorException(exception.getMessage()).getScimError();
      }
    }

    return ServerUtils.setAcceptableType(
        Response.status(errorResponse.getStatus()).entity(errorResponse),
        headers.getAcceptableMediaTypes()).build();
  }
}
