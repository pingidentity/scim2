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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * A builder for SCIM retrieve requests.
 */
public abstract class RetrieveRequestBuilder
    <T extends RetrieveRequestBuilder<T>>
    extends ResourceReturningRequestBuilder<T>
{
  /**
   * The version to match.
   */
  @Nullable
  protected String version;

  /**
   * Create a new RetrieveRequestBuilder.
   *
   * @param target The WebTarget to GET.
   */
  private RetrieveRequestBuilder(@NotNull final WebTarget target)
  {
    super(target);
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
      request.header(HttpHeaders.IF_NONE_MATCH, version);
    }
    return request;
  }


  /**
   * A builder for SCIM retrieve requests for where the returned resource POJO
   * type will be the same as the original.
   */
  public static final class Generic<T extends ScimResource>
      extends RetrieveRequestBuilder<Generic<T>>
  {
    @NotNull
    private final T resource;

    /**
     * Create a new generic retrieve request builder.
     *
     * @param target The WebTarget to GET.
     * @param resource The SCIM resource to retrieve.
     */
    public Generic(@NotNull final WebTarget target, @NotNull final T resource)
    {
      super(target);
      this.resource = resource;
    }

    /**
     * Retrieve the resource only if the resource has been modified from the
     * resource provided. If the resource has not been modified, the provided
     * resource will be returned by invoke.
     *
     * @return This builder.
     */
    @NotNull
    public Generic<T> ifNoneMatch()
    {
      version = getResourceVersion(resource);
      return this;
    }

    /**
     * Invoke the SCIM retrieve request.
     *
     * @return The successfully retrieved SCIM resource or the resource provided
     *         if the ifNoneMatch method was called and the resource has not
     *         been modified.
     * @throws ScimException If an error occurred.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public T invoke() throws ScimException
    {
      return (T) invoke(resource.getClass());
    }

    /**
     * Invoke the SCIM modify request.
     *
     * @param <C> The type of object to return.
     * @param cls The Java class object used to determine the type to return.
     * @return The successfully modified SCIM resource.
     * @throws ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service responded with an error.
     */
    @NotNull
    public <C> C invoke(@NotNull final Class<C> cls) throws ScimException
    {
      try (Response response = buildRequest().get())
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


  /**
   * A builder for SCIM retrieve requests for where the returned resource POJO
   * type will be provided.
   */
  public static final class Typed extends RetrieveRequestBuilder<Typed>
  {
    /**
     * Create a new generic retrieve request builder.
     *
     * @param target The WebTarget to GET.
     */
    public Typed(@NotNull final WebTarget target)
    {
      super(target);
    }

    /**
     * Retrieve the resource only if the resource has been modified since the
     * provided version. If the resource has not been modified,
     * NotModifiedException will be thrown when calling invoke.
     *
     * @param version The version of the resource to compare.
     * @return This builder.
     */
    @NotNull
    public Typed ifNoneMatch(@Nullable final String version)
    {
      this.version = version;
      return this;
    }

    /**
     * Invoke the SCIM retrieve request.
     *
     * @param <T> The type of object to return.
     * @param cls The Java class object used to determine the type to return.
     * @return The successfully retrieved SCIM resource.
     * @throws ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service responded with an error.
     */
    @NotNull
    public <T> T invoke(@NotNull final Class<T> cls) throws ScimException
    {
      try (Response response = buildRequest().get())
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
}
