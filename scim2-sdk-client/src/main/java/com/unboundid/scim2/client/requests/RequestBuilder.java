/*
 * Copyright 2015-2016 UnboundID Corp.
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

import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.utils.StaticUtils;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Abstract SCIM request builder.
 */
public class RequestBuilder<T extends RequestBuilder>
{
  /**
   * The web target to send the request.
   */
  private WebTarget target;

  /**
   * Arbitrary request headers.
   */
  protected final MultivaluedMap<String, Object> headers =
      new MultivaluedHashMap<String, Object>();

  /**
   * Arbitrary query parameters.
   */
  protected final MultivaluedMap<String, Object> queryParams =
      new MultivaluedHashMap<String, Object>();

  /**
   * Create a new SCIM request builder.
   *
   * @param target The WebTarget to send the request.
   */
  RequestBuilder(final WebTarget target)
  {
    this.target = target;
  }

  /**
   * Add an arbitrary HTTP header to the request.
   *
   * @param name The header name.
   * @param value The header value(s).
   * @return This builder.
   */
  @SuppressWarnings("unchecked")
  public T header(final String name, final Object... value)
  {
    headers.addAll(name, value);
    return (T) this;
  }

  /**
   * Add an arbitrary query parameter to the request.
   *
   * @param name The query parameter name.
   * @param value The query parameter value(s).
   * @return This builder.
   */
  @SuppressWarnings("unchecked")
  public T queryParam(final String name, final Object... value)
  {
    queryParams.addAll(name, value);
    return (T) this;
  }

  /**
   * Retrieve the meta.version attribute of the resource.
   *
   * @param resource The resource whose version to retrieve.
   * @return The resource version.
   * @throws IllegalArgumentException if the resource does not contain a the
   * meta.version attribute.
   */
  static String getResourceVersion(final ScimResource resource)
      throws IllegalArgumentException
  {
    if(resource == null || resource.getMeta() == null ||
        resource.getMeta().getVersion() == null)
    {
      throw new IllegalArgumentException(
          "Resource version must be specified by meta.version");
    }
    return resource.getMeta().getVersion();
  }

  /**
   * Convert a JAX-RS response to a ScimException.
   *
   * @param response The JAX-RS response.
   * @return the converted ScimException.
   */
  static ScimException toScimException(final Response response)
  {
    ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
    ScimException exception =
      ScimException.createException(errorResponse, null);
    response.close();
    return exception;
  }

  /**
   * Returns the unbuilt WebTarget for the request. In most cases,
   * {@link #buildTarget()} should be used instead.
   *
   * @return The WebTarget for the request.
   */
  protected WebTarget target()
  {
    return target;
  }

  /**
   * Build the WebTarget for the request.
   *
   * @return The WebTarget for the request.
   */
  WebTarget buildTarget()
  {
    for(Map.Entry<String, List<Object>> queryParam : queryParams.entrySet())
    {
      target = target.queryParam(queryParam.getKey(),
                                 queryParam.getValue().toArray());
    }
    return target;
  }

  /**
   * Build the Invocation.Builder for the request.
   *
   * @return The Invocation.Builder for the request.
   */
  Invocation.Builder buildRequest()
  {
    Invocation.Builder builder = buildTarget().request(
        ScimService.MEDIA_TYPE_SCIM_TYPE, MediaType.APPLICATION_JSON_TYPE);
    for(Map.Entry<String, List<Object>> header : headers.entrySet())
    {
      builder = builder.header(header.getKey(),
                               StaticUtils.listToString(header.getValue(),
                                                        ", "));
    }
    return builder;
  }
}
