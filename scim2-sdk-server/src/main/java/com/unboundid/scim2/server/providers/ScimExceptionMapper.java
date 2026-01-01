/*
 * Copyright 2015-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2015-2026 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;
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
  @NotNull
  @Context
  private HttpHeaders headers;

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Response toResponse(@NotNull final ScimException throwable)
  {
    return ServerUtils.setAcceptableType(
            Response.status(throwable.getScimError().getStatus()).
                entity(throwable.getScimError()),
            headers.getAcceptableMediaTypes()).build();
  }
}
