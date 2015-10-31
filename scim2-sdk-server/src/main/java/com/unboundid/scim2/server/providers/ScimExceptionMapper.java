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

import com.unboundid.scim2.common.exceptions.ScimException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * A JAX-RS ExceptionMapper for ScimExceptions.
 */
@Provider
public class ScimExceptionMapper implements ExceptionMapper<ScimException>
{
  /**
   * {@inheritDoc}
   */
  public Response toResponse(final ScimException throwable)
  {
    // We don't need to deal with selecting a content type here because
    // ScimExceptions are only thrown by resource methods and the JAX-RS
    // runtime will use the @Produce annotation to pick the right type.
    return Response.status(throwable.getScimError().getStatus()).entity(
        throwable.getScimError()).build();
  }
}
