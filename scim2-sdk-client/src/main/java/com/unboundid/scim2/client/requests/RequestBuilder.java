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

import com.unboundid.scim2.client.ScimServiceException;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.utils.StaticUtils;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

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

  private String contentType = MEDIA_TYPE_SCIM;

  private List<String> accept = new ArrayList<String>();

  /**
   * Create a new SCIM request builder.
   *
   * @param target The WebTarget to send the request.
   */
  RequestBuilder(final WebTarget target)
  {
    this.target = target;
    accept(MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON);
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
   * Sets the media type for any content sent to the server.  The default
   * value is ApiConstants.MEDIA_TYPE_SCIM ("application/scim+json").
   * @param contentType a string describing the media type of content
   *                    sent to the server.
   * @return This builder.
   */
  public T contentType(final String contentType)
  {
    this.contentType = contentType;
    return (T) this;
  }

  /**
   * Sets the media type(s) that are acceptable as a return from the server.
   * The default accepted media types are
   * ApiConstants.MEDIA_TYPE_SCIM ("application/scim+json") and
   * MediaType.APPLICATION_JSON ("application/json")
   * @param acceptStrings a string (or strings) describing the media type that
   *                      will be accepted from the server.  This parameter may
   *                      not be null.
   * @return This builder.
   */
  public T accept(final String ... acceptStrings)
  {
    this.accept.clear();
    if((acceptStrings == null) || (acceptStrings.length == 0))
    {
      throw new IllegalArgumentException(
          "Accepted media types must not be null or empty");
    }

    for(String acceptString : acceptStrings)
    {
      accept.add(acceptString);
    }

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
    try
    {
      ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
      // If are able to read an error response, use it to build the exception.
      // If not, use the http status code to determine the exception.
      ScimException exception = (errorResponse == null) ?
        ScimException.createException(response.getStatus(), null) :
        ScimException.createException(errorResponse, null);
      response.close();

      return exception;
    }
    catch(ProcessingException ex)
    {
      // The exception message likely contains unwanted details about why the
      // server failed to process the response, instead of the actual SCIM
      // issue. Replace it with a general reason phrase for the status code.
      String genericDetails = response.getStatusInfo().getReasonPhrase();

      return new ScimServiceException(
          response.getStatus(), genericDetails, ex);
    }
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
   * Gets the media type for any content sent to the server.
   *
   * @return the media type for any content sent to the server.
   */
  protected String getContentType()
  {
    return contentType;
  }

  /**
   * Gets the media type(s) that are acceptable as a return from the server.
   *
   * @return the media type(s) that are acceptable as a return from the server.
   */
  protected List<String> getAccept()
  {
    return accept;
  }
  /**
   * Build the Invocation.Builder for the request.
   *
   * @return The Invocation.Builder for the request.
   */
  Invocation.Builder buildRequest()
  {
    Invocation.Builder builder =
        buildTarget().request(accept.toArray(new String[accept.size()]));
    for(Map.Entry<String, List<Object>> header : headers.entrySet())
    {
      builder = builder.header(header.getKey(),
                               StaticUtils.listToString(header.getValue(),
                                                        ", "));
    }
    return builder;
  }
}
