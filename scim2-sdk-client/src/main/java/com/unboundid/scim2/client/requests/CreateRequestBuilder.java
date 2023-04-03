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

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * A builder for SCIM create requests.
 */
public final class CreateRequestBuilder<T extends ScimResource>
    extends ResourceReturningRequestBuilder<CreateRequestBuilder<T>>
{
  private final T resource;

  /**
   * Create a new SCIM create request builder that will POST the given resource
   * to the given web target.
   *
   * @param target The WebTarget to POST to.
   * @param resource The SCIM resource to POST.
   */
  public CreateRequestBuilder(final WebTarget target, final T resource)
  {
    super(target);
    this.resource = resource;
  }

  /**
   * Invoke the SCIM create request.
   *
   * @return The successfully create SCIM resource.
   * @throws ScimException If an error occurred.
   */
  @SuppressWarnings("unchecked")
  public T invoke() throws ScimException
  {
    return (T) invoke(resource.getClass());
  }

  /**
   * Invoke the SCIM create request.
   *
   * @param <C> The type of object to return.
   * @param cls The Java class object used to determine the type to return.
   * @return The successfully modified SCIM resource.
   * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
   * @throws ScimException If the SCIM service provider responded with an error.
   */
  public <C> C invoke(final Class<C> cls) throws ScimException
  {
    Response response = buildRequest().post(
        Entity.entity(resource, getContentType()));
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
