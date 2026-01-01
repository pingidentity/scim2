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

import com.unboundid.scim2.client.ScimServiceException;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

/**
 * Abstract SCIM request builder.
 */
public class RequestBuilder<T extends RequestBuilder<T>>
{
  /**
   * The web target to send the request.
   */
  @NotNull
  private WebTarget target;

  /**
   * Arbitrary request headers.
   */
  @NotNull
  protected final MultivaluedMap<String, Object> headers =
      new MultivaluedHashMap<>();

  /**
   * Arbitrary query parameters.
   */
  @NotNull
  protected final MultivaluedMap<String, Object> queryParams =
      new MultivaluedHashMap<>();

  @Nullable
  private String contentType = MEDIA_TYPE_SCIM;

  @NotNull
  private final List<String> accept = new ArrayList<>();

  /**
   * Create a new SCIM request builder.
   *
   * @param target The WebTarget to send the request.
   */
  RequestBuilder(@NotNull final WebTarget target)
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
  @NotNull
  @SuppressWarnings("unchecked")
  public T header(@NotNull final String name, @NotNull final Object... value)
  {
    headers.addAll(name, value);
    return (T) this;
  }

  /**
   * Sets the media type for any content sent to the server.  The default
   * value is ApiConstants.MEDIA_TYPE_SCIM ("application/scim+json").
   *
   * @param contentType a string describing the media type of content
   *                    sent to the server.
   * @return This builder.
   */
  @NotNull
  public T contentType(@Nullable final String contentType)
  {
    this.contentType = contentType;
    //noinspection unchecked
    return (T) this;
  }

  /**
   * Sets the media type(s) that are acceptable as a return from the server.
   * The default accepted media types are
   * ApiConstants.MEDIA_TYPE_SCIM ("application/scim+json") and
   * MediaType.APPLICATION_JSON ("application/json")
   *
   * @param acceptStrings a string (or strings) describing the media type that
   *                      will be accepted from the server.  This parameter may
   *                      not be null.
   * @return This builder.
   */
  @NotNull
  public T accept(@NotNull final String... acceptStrings)
  {
    if ((acceptStrings == null) || (acceptStrings.length == 0))
    {
      throw new IllegalArgumentException(
          "Accepted media types must not be null or empty");
    }

    this.accept.clear();
    accept.addAll(Arrays.asList(acceptStrings));

    //noinspection unchecked
    return (T) this;
  }

  /**
   * Add an arbitrary query parameter to the request.
   *
   * @param name The query parameter name.
   * @param value The query parameter value(s).
   * @return This builder.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  public T queryParam(@NotNull final String name,
                      @NotNull final Object... value)
  {
    queryParams.addAll(name, value);
    return (T) this;
  }

  /**
   * Retrieve the {@code meta.version} attribute of the resource.
   *
   * @param resource The resource whose version to retrieve.
   * @return The resource version.
   * @throws IllegalArgumentException if the resource does not contain the
   * {@code meta.version} attribute.
   */
  @NotNull
  static String getResourceVersion(@NotNull final ScimResource resource)
      throws IllegalArgumentException
  {
    if (resource == null || resource.getMeta() == null ||
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
  @NotNull
  static ScimException toScimException(@NotNull final Response response)
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
    catch (ProcessingException ex)
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
  @NotNull
  protected WebTarget target()
  {
    return target;
  }

  /**
   * Build the WebTarget for the request.
   *
   * @return The WebTarget for the request.
   */
  @NotNull
  WebTarget buildTarget()
  {
    for (Map.Entry<String, List<Object>> queryParam : queryParams.entrySet())
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
  @Nullable
  protected String getContentType()
  {
    return contentType;
  }

  /**
   * Gets the media type(s) that are acceptable as a return from the server.
   *
   * @return the media type(s) that are acceptable as a return from the server.
   */
  @NotNull
  protected List<String> getAccept()
  {
    return accept;
  }

  /**
   * Build the Invocation.Builder for the request.
   *
   * @return The Invocation.Builder for the request.
   */
  @NotNull
  protected Invocation.Builder buildRequest()
  {
    Invocation.Builder builder =
        buildTarget().request(accept.toArray(new String[0]));
    for (Map.Entry<String, List<Object>> header : headers.entrySet())
    {
      String stringValue = StaticUtils.listToString(header.getValue(), ", ");
      builder = builder.header(header.getKey(), stringValue);
    }
    return builder;
  }

  /**
   * This method is equivalent to calling {@code asGenericScimResource()}. This
   * is used by the request builders as a halfway step toward converting an
   * object into a JSON payload. This minimizes the likelihood for improper JSON
   * conversions, such as {@code null} fields being included in the JSON.
   * <br><br>
   *
   * Before sending a request to a SCIM service, the Java object representing a
   * SCIM resource must be converted into a string JSON payload. It's possible
   * to simply pass in the Java object and rely on other libraries to serialize
   * it into JSON later, but this approach requires projects to perform manual
   * tuning of Jackson settings in the environment so that other libraries will
   * serialize the data correctly. This can be burdensome to configure, and can
   * result in problems with the HTTP request that are difficult to resolve.
   * <br><br>
   *
   * Since any object may be used as the JSON payload, it is valuable to convert
   * these SCIM resources into a JSON-like form before sending an HTTP request.
   * GenericScimResource is well-suited for this, as it is a wrapper around a
   * JSON ObjectNode that is more likely to result in a proper JSON string.
   * <br><br>
   *
   * The behavior of the SCIM SDK's object mapper can be customized by updating
   * fields in the {@link com.unboundid.scim2.common.utils.MapperFactory} class.
   *
   * @param resource  The SCIM resource that will be serialized into JSON.
   * @return  An equivalent GenericScimResource.
   *
   * @since 4.1.0
   */
  @NotNull
  protected GenericScimResource generify(@NotNull final ScimResource resource)
  {
    return resource.asGenericScimResource();
  }
}
