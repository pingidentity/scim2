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

import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.utils.ServerUtils;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * A JAX-RS ExceptionMapper for ScimExceptions.
 */
@Provider
public class ScimExceptionMapper implements ExceptionMapper<ScimException>
{
  @Context
  private HttpHeaders headers;

  /**
   * {@inheritDoc}
   */
  public Response toResponse(final ScimException throwable)
  {
    return ServerUtils.setAcceptableType(
            Response.status(throwable.getScimError().getStatus()).
                entity(throwable.getScimError()),
            headers.getAcceptableMediaTypes()).build();
  }
}
