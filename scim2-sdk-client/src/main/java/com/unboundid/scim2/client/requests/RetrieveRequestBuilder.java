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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.ScimException;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

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
  protected String version;

  /**
   * Create a new RetrieveRequestBuilder.
   *
   * @param target The WebTarget to GET.
   */
  private RetrieveRequestBuilder(final WebTarget target)
  {
    super(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  Invocation.Builder buildRequest()
  {
    Invocation.Builder request = super.buildRequest();
    if(version != null)
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
    private final T resource;

    /**
     * Create a new generic retrieve request builder.
     *
     * @param target The WebTarget to GET.
     * @param resource The SCIM resource to retrieve.
     */
    public Generic(final WebTarget target, final T resource)
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
     * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
     * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service provider responded with an error.
     */
    public <C> C invoke(final Class<C> cls) throws ScimException
    {
      Response response = buildRequest().get();
      try
      {
        if(response.getStatusInfo().getFamily() ==
            Response.Status.Family.SUCCESSFUL)
        {
          return response.readEntity(cls);
        }
        else
        {
          throw toScimException(response);
        }
      }
      finally
      {
        response.close();
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
    public Typed(final WebTarget target)
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
    public Typed ifNoneMatch(final String version)
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
     * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service provider responded with an error.
     */
    public <T> T invoke(final Class<T> cls) throws ScimException
    {
      Response response = buildRequest().get();
      try
      {
        if(response.getStatusInfo().getFamily() ==
            Response.Status.Family.SUCCESSFUL)
        {
          return response.readEntity(cls);
        }
        else
        {
          throw toScimException(response);
        }
      }
      finally
      {
        response.close();
      }
    }
  }
}
