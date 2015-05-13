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

import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.server.ApiConstants;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * A JAX-RS ExceptionMapper for to convert standard WebApplicationExceptions to
 * SCIM ErrorResponses.
 */
@Provider
public class WebApplicationExceptionMapper implements
    ExceptionMapper<WebApplicationException>
{
  @Context
  private Request request;

  /**
   * {@inheritDoc}
   */
  public Response toResponse(final WebApplicationException exception)
  {
    ErrorResponse errorResponse;

    if(exception instanceof NotAllowedException)
    {
      // SCIM 2.0 uses 501 instead of 405.
      errorResponse = new ErrorResponse(501);
      errorResponse.setDetail(request.getMethod() + " not supported");
    }
    else
    {
      errorResponse = new ErrorResponse(exception.getResponse().getStatus());
      errorResponse.setDetail(exception.getMessage());
    }

    return Response.status(errorResponse.getStatus()).entity(
        errorResponse).type(ApiConstants.MEDIA_TYPE_SCIM_TYPE).build();
  }
}
