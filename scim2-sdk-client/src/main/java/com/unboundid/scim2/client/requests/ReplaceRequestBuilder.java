/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * A builder for SCIM PUT requests.
 */
public class ReplaceRequestBuilder<T extends ScimResource>
    extends ResourceReturningRequestBuilder<ReplaceRequestBuilder<T>>
{
  @NotNull
  private final T resource;

  @Nullable
  private String version;

  /**
   * Create a new replace request builder.
   *
   * @param target The WebTarget to PUT.
   * @param resource The SCIM resource to replace.
   */
  public ReplaceRequestBuilder(@NotNull final WebTarget target,
                               @NotNull final T resource)
  {
    super(target);
    this.resource = resource;
  }

  /**
   * Replace the resource only if the resource has not been modified from the
   * resource provided.
   *
   * @return This builder.
   */
  @NotNull
  public ReplaceRequestBuilder<T> ifMatch()
  {
    version = getResourceVersion(resource);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  protected Invocation.Builder buildRequest()
  {
    Invocation.Builder request = super.buildRequest();
    if (version != null)
    {
      request.header(HttpHeaders.IF_MATCH, version);
    }
    return request;
  }

  /**
   * Invoke the SCIM replace request with an HTTP PUT.
   *
   * @return The successfully replaced SCIM resource.
   * @throws ScimException If the SCIM service provider responded with an error.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  public T invoke() throws ScimException
  {
    return (T) invoke(resource.getClass());
  }

  /**
   * Invoke the SCIM replace request with an HTTP PUT.
   *
   * @param <C> The type of object to return.
   * @param cls The Java class object used to determine the type to return.
   * @return The successfully modified SCIM resource.
   * @throws ProcessingException If a JAX-RS runtime exception occurred.
   * @throws ScimException If the SCIM service provider responded with an error.
   */
  @NotNull
  public <C> C invoke(@NotNull final Class<C> cls) throws ScimException
  {
    var entity = Entity.entity(generify(resource), getContentType());
    try (Response response = buildRequest().put(entity))
    {
      if (response.getStatusInfo().getFamily() == SUCCESSFUL)
      {
        return response.readEntity(cls);
      }
      else
      {
        throw toScimException(response);
      }
    }
  }
}
