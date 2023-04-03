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

import com.unboundid.scim2.common.exceptions.ScimException;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

/**
 * A builder for SCIM delete requests.
 */
public class DeleteRequestBuilder extends RequestBuilder<DeleteRequestBuilder>
{
  private String version;

  /**
   * Create a new DeleteRequestBuilder.
   *
   * @param target The WebTarget to DELETE.
   */
  public DeleteRequestBuilder(final WebTarget target)
  {
    super(target);
  }

  /**
   * Delete the resource only if the resource has not been modified since the
   * provided version.
   *
   * @param version The version of the resource to compare.
   * @return This builder.
   */
  public DeleteRequestBuilder ifMatch(final String version)
  {
    this.version = version;
    return this;
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
      request.header(HttpHeaders.IF_MATCH, version);
    }
    return request;
  }

  /**
   * Invoke the SCIM delete request.
   *
   * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
   * @throws ScimException If the SCIM service provider responded with an error.
   */
  public void invoke() throws ScimException
  {
    Response response = buildRequest().delete();
    try
    {
      if(response.getStatusInfo().getFamily() !=
          Response.Status.Family.SUCCESSFUL)
      {
        throw toScimException(response);
      }
    }
    finally
    {
      // This call is idempotent.
      response.close();
    }
  }
}
