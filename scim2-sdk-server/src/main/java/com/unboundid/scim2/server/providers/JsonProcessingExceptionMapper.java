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

package com.unboundid.scim2.server.providers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.utils.ApiConstants;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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

  /**
   * {@inheritDoc}
   */
  public Response toResponse(final JsonProcessingException exception)
  {
    StringBuilder builder = new StringBuilder();
    if(exception instanceof JsonParseException)
    {
      builder.append("Unable to parse request: ");
      builder.append(exception.getOriginalMessage());
      if(exception.getLocation() != null)
      {
        builder.append(" at line: ");
        builder.append(exception.getLocation().getLineNr());
        builder.append(", column: ");
        builder.append(exception.getLocation().getColumnNr());
      }
    }
    else
    {
      builder.append(exception.getMessage());
    }

    ErrorResponse errorResponse =
        BadRequestException.invalidSyntax(builder.toString()).getScimError();

    return Response.status(errorResponse.getStatus()).entity(
        errorResponse).type(ApiConstants.MEDIA_TYPE_SCIM).build();
  }
}
