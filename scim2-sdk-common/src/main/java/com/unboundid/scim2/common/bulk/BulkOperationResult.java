/*
 * Copyright 2026 Ping Identity Corporation
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
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.bulk;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.utils.BulkStatusDeserializer;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.DebugType;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Objects;
import java.util.logging.Level;


/**
 * This class represents an operation contained within a {@link BulkResponse}.
 * <br><br>
 *
 * Both {@link BulkRequest} and {@link BulkResponse} objects contain an
 * {@code Operations} array, with similar elements contained within them.
 * However, there are specific restrictions on which structures may be used.
 * For example, failed POST responses should not include a {@code location}, but
 * all other successful responses and error responses must provide this field.
 * The SCIM SDK defines operations within a bulk response as a "bulk operation
 * result", which is represented by this class.
 * <br><br>
 *
 * Bulk operation results contain the following fields:
 * <ul>
 *   <li> {@code location}: The location URI for the resource that was targeted.
 *        This field MUST be present, except in the case that a POST operation
 *        fails. Since a failure to create a resource means that a URI does not
 *        exist, this is the only case where the location will be {@code null}.
 *   <li> {@code method}: The HTTP method that was used for the request.
 *   <li> {@code response}: The JSON response body for the write request. If the
 *        response was successful, this may optionally be {@code null}, but it
 *        must be present for an error.
 *   <li> {@code status}: The HTTP status code for the operation, e.g.,
 *        {@code 200 OK}, {@code 404 NOT FOUND}, etc.
 *   <li> {@code bulkId}: The bulk ID that was provided in the POST request.
 *   <li> {@code version}: The ETag version of the resource. For more
 *        information, see {@link ETagConfig}.
 * </ul>
 * <br><br>
 *
 * The following is an example of a bulk operation result that corresponds to
 * a successful user create response:
 * <pre>
 * {
 *   "location" : "https://example.com/v2/Users/fa1afe1",
 *   "method" : "POST",
 *   "bulkId" : "originalBulkId",
 *   "response" : {
 *     "schemas" : [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *     "userName" : "name"
 *   },
 *   "status" : "200"
 * }
 * </pre>
 *
 * Bulk operation responses can be created with Java code of the following form.
 * The easiest way is to use the constructor that accepts a source bulk
 * operation from a client request, as shown below. Note that unlike
 * {@link BulkOperation}, this method does not use static helper methods for
 * successful bulk results since all HTTP methods have the same form.
 * <pre><code>
 *   UserResource userResource = new UserResource().setUserName("name");
 *   BulkOperationResult result = new BulkOperationResult(
 *       sourceOp,
 *       "200",
 *       "https://example.com/v2/Users/fa1afe1"
 *   );
 *   result.setResponse(userResource);
 *
 *   // If desired, the fields may be set directly.
 *   UserResource userResource = new UserResource().setUserName("name");
 *   BulkOperationResult result = new BulkOperationResult(BulkOpType.POST,
 *       "201",
 *       "https://example.com/v2/Users/fa1afe1",
 *       userResource.asGenericScimResource().getObjectNode(),
 *       "originalBulkId",
 *       null);
 * </code></pre>
 *
 * Unsuccessful bulk operation result objects have a slightly different format:
 * <pre>
 * {
 *   "method" : "POST",
 *   "response" : {
 *     "schemas" : [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *     "status" : "400",
 *     "scimType" : "invalidSyntax",
 *     "detail" : "Request is unparsable or violates the schema."
 *   },
 *   "status" : "400"
 * }
 * </pre>
 *
 * Since bulk response errors must display the errors, the {@link #error} helper
 * methods may be used:
 * <pre><code>
 *   // The location is null for POST request failures.
 *   ScimException e = BadRequestException.invalidSyntax(
 *       "Request is unparsable or violates the schema.");
 *   BulkOperationResult.error(sourceOp, e, null);
 *   BulkOperationResult.error(BulkOpType.POST, e, null);
 *
 *   // Provide the location for other operation types do.
 *   var scimException = new ForbiddenException("User is unauthorized.");
 *   BulkOperationResult.error(BulkOpType.PUT, scimException, "/Users/fa1afe1");
 * </code></pre>
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@JsonPropertyOrder({ "location", "method", "bulkId", "version", "response",
                     "status" })
public class BulkOperationResult
{
  /**
   * Represents the status code for {@code HTTP 200 OK}.
   */
  @NotNull public static final String HTTP_STATUS_OK = "200";

  /**
   * Represents the status code for {@code HTTP 201 CREATED}.
   */
  @NotNull public static final String HTTP_STATUS_CREATED = "201";

  /**
   * Represents the status code for {@code HTTP 204 NO_CONTENT}.
   */
  @NotNull public static final String HTTP_STATUS_NO_CONTENT = "204";

  /**
   * Represents the status code for {@code HTTP 404 NOT_FOUND}.
   */
  @NotNull public static final String HTTP_STATUS_NOT_FOUND = "404";

  /**
   * Represents the status code for {@code HTTP 500 INTERNAL_ERROR}.
   */
  @NotNull public static final String HTTP_STATUS_INTERNAL_ERROR = "500";

  /**
   * The location of the resource referenced by this bulk operation result. This
   * will only be {@code null} for client POST request failures, since there is
   * not an existing resource to reference.
   */
  @Nullable
  private String location;

  /**
   * This field represents the HTTP operation type (e.g., POST).
   */
  @NotNull
  private final BulkOpType method;

  /**
   * The bulk ID of the original bulk operation. For more information, see
   * {@link BulkOperation#bulkId}.
   */
  @Nullable
  private String bulkId;

  /**
   * The optional version tag, which can be used if the SCIM service provider
   * supports ETag versioning. See {@link ETagConfig} for more information.
   */
  @Nullable
  private String version;

  /**
   * The JSON data representing the response for the individual write request.
   * The form of this value depends on the nature of the bulk operation result:
   * <ul>
   *   <li> For successful {@code DELETE} operations, this will be {@code null}.
   *   <li> For successful responses, this will contain the updated resource
   *        (e.g., creating a user will provide the contents of the new user).
   *   <li> For unsuccessful responses, this will return the error response,
   *        represented as an {@link ErrorResponse} object.
   * </ul>
   */
  @Nullable
  private ObjectNode response;

  /**
   * The status code to return for the write request, e.g., {@code "200"}
   * representing a {@code 200 OK} response for a successful modification.
   */
  @NotNull
  @JsonDeserialize(using = BulkStatusDeserializer.class)
  private String status;

  /**
   * The status value as an integer.
   */
  private int statusInt;

  /**
   * Constructs an entry to be contained in a {@link BulkResponse} based on the
   * initial bulk operation that requested the update.
   * <br><br>
   *
   * Since bulk IDs should only be used for POST requests, this constructor will
   * only copy the bulk ID from the {@code operation} if it refers to a POST.
   *
   * @param operation The source bulk operation.
   * @param status    The HTTP response status code for the update, e.g., "200".
   * @param location  The location URI of the modified resource. This must not
   *                  be {@code null}, except in the case of a POST failure.
   *
   * @throws IllegalArgumentException  If the location is {@code null} for a
   *                                   response that is not a POST failure.
   */
  public BulkOperationResult(@NotNull final BulkOperation operation,
                             @NotNull final String status,
                             @Nullable final String location)
      throws IllegalArgumentException
  {
    this(operation.getMethod(),
        status,
        location,
        null,
        operation.getMethod() == BulkOpType.POST ? operation.getBulkId() : null,
        null);
  }

  /**
   * Constructs an entry to be contained in a {@link BulkResponse}.
   *
   * @param method    The HTTP method (e.g., POST).
   * @param status    A string indicating the HTTP response code, e.g., "200".
   * @param response  The JSON data indicating the response for the operation.
   *                  This may optionally be {@code null} if the status is a
   *                  {@code 2xx} successful response.
   * @param location  The location URI of the modified resource. This must not
   *                  be {@code null}, except in the case of a POST failure.
   * @param bulkId    The bulk ID specified by the client, if it exists.
   * @param version   The ETag version of the resource, if the service provider
   *                  supports it.
   *
   * @throws IllegalArgumentException  If the location is {@code null} for a
   *                                   response that is not a POST failure.
   */
  @JsonCreator
  public BulkOperationResult(
      @NotNull @JsonProperty(value = "method") final BulkOpType method,
      @NotNull @JsonProperty(value = "status") final String status,
      @Nullable @JsonProperty(value = "location") final String location,
      @Nullable @JsonProperty(value = "response") final ObjectNode response,
      @Nullable @JsonProperty(value = "bulkId") final String bulkId,
      @Nullable @JsonProperty(value = "version") final String version)
          throws IllegalArgumentException
  {
    this.method = Objects.requireNonNull(method);
    this.location = location;
    this.response = response;
    this.version = version;
    setBulkId(bulkId);
    setStatus(status);

    boolean isUnsuccessfulPost = method == BulkOpType.POST &&
        (status.startsWith("4") || status.startsWith("5"));
    if (location == null && !isUnsuccessfulPost)
    {
      throw new IllegalArgumentException(
          "The 'location' of a bulk response must be defined, with the"
              + " exception of unsuccessful POST requests."
      );
    }
  }

  /**
   * Alternate constructor for creating a bulk operation result with core
   * essential fields.
   *
   * @param method    The HTTP method (e.g., POST).
   * @param status    A string indicating the HTTP response code, e.g., "200".
   * @param location  The location URI of the modified resource. This must not
   *                  be {@code null}, except in the case of a POST failure.
   *
   * @throws IllegalArgumentException  If the location is {@code null} for a
   *                                   response that is not a POST failure.
   */
  public BulkOperationResult(@NotNull final BulkOpType method,
                             @NotNull final String status,
                             @Nullable final String location)
      throws IllegalArgumentException
  {
    this(method, status, location, null, null, null);
  }

  /**
   * Constructs a bulk operation result that represents an error. See the
   * class-level documentation for an example.
   *
   * @param operation       The bulk operation that was attempted.
   * @param scimException   The SCIM error that will be displayed in the
   *                        {@code response} field.
   * @param location        The URI of the resource targeted by the bulk
   *                        operation. This field will be ignored for POST
   *                        operations, as the resource will not exist. This
   *                        value may only be {@code null} for failed POSTs.
   *
   * @return  A bulk operation result that represents an error.
   */
  @NotNull
  public static BulkOperationResult error(
      @NotNull final BulkOperation operation,
      @NotNull final ScimException scimException,
      @Nullable final String location)
  {
    final BulkOpType method = operation.getMethod();
    BulkOperationResult result = error(method, scimException, location);

    // Bulk IDs should only be handled for POST requests.
    String bulkId = (method == BulkOpType.POST) ? operation.getBulkId() : null;
    return result.setBulkId(bulkId);
  }

  /**
   * Constructs a bulk operation result that represents an error.
   *
   * @param opType          The type of the bulk operation.
   * @param scimException   The SCIM error that will be displayed in the
   *                        {@code response} field.
   * @param location        The URI of the resource targeted by the bulk
   *                        operation. This field will be ignored for POST
   *                        operations, as the resource will not exist. This
   *                        value may only be {@code null} for failed POSTs.
   *
   * @return  A bulk operation result that represents an error.
   */
  @NotNull
  public static BulkOperationResult error(
      @NotNull final BulkOpType opType,
      @NotNull final ScimException scimException,
      @Nullable final String location)
  {
    // A failure to create a resource means that there is no location to return.
    String locationValue = (opType == BulkOpType.POST) ? null : location;

    final ErrorResponse errorResponse = scimException.getScimError();
    return new BulkOperationResult(opType,
        errorResponse.getStatus().toString(),
        locationValue,
        JsonUtils.valueToNode(errorResponse),
        null,
        null);
  }

  /**
   * Sets the {@code location} field for this bulk operation result. For
   * example, for a bulk operation result that created a resource, the location
   * could take the form of {@code https://example.com/v2/fa1afe1}.
   * <br><br>
   *
   * This may only be {@code null} for client POST request failures, since there
   * is not an existing resource to reference.
   *
   * @param location  The new value for the location.
   * @return  This bulk operation result.
   */
  @NotNull
  public BulkOperationResult setLocation(@Nullable final String location)
  {
    if (method != BulkOpType.POST)
    {
      Objects.requireNonNull(location);
    }

    this.location = location;
    return this;
  }

  /**
   * Sets the bulk ID of this bulk operation result. Since bulk IDs may only be
   * used for POST requests, this method does not perform any action for
   * bulk operation results that are not of type {@code POST}.
   *
   * @param bulkId  The bulk ID that was provided on the original client
   *                {@link BulkOperation}.
   * @return  This bulk operation result.
   */
  @NotNull
  public BulkOperationResult setBulkId(@Nullable final String bulkId)
  {
    this.bulkId = (method == BulkOpType.POST) ? bulkId : null;
    return this;
  }

  /**
   * Sets the ETag version of this bulk operation result.
   *
   * @param version  The version tag to use.
   * @return         This bulk operation result.
   */
  @NotNull
  public BulkOperationResult setVersion(@Nullable final String version)
  {
    this.version = version;
    return this;
  }

  /**
   * Sets the response of this bulk operation result.
   *
   * @param response  The JSON payload representing the response to the
   *                  individual client {@link BulkOperation}.
   * @return  This bulk operation result.
   */
  @NotNull
  @JsonSetter
  public BulkOperationResult setResponse(@Nullable final ObjectNode response)
  {
    this.response = response;
    return this;
  }

  /**
   * Sets the response of this bulk operation result.
   *
   * @param response  The JSON payload representing the response to the
   *                  individual client {@link BulkOperation}.
   * @return  This bulk operation result.
   */
  @NotNull
  public BulkOperationResult setResponse(@Nullable final ScimResource response)
  {
    ObjectNode node = (response == null) ?
        null : response.asGenericScimResource().getObjectNode();
    return setResponse(node);
  }

  /**
   * Sets the HTTP status code representing the result code for the original
   * client {@link BulkOperation}.
   *
   * @param status  The HTTP status code.
   * @return  This bulk operation result.
   */
  @NotNull
  @JsonSetter
  public BulkOperationResult setStatus(@NotNull final String status)
      throws IllegalArgumentException
  {
    Objects.requireNonNull(status);

    try
    {
      // Ensure the status is an integer.
      Integer intValue = Integer.parseInt(status);
      return setStatus(intValue);
    }
    catch (NumberFormatException e)
    {
      throw new IllegalArgumentException(
          "Could not convert '" + status + "' to an integer HTTP status code.");
    }
  }

  /**
   * Sets the HTTP status code representing the result code for the original
   * client {@link BulkOperation}.
   *
   * @param status  The HTTP status code.
   * @return  This bulk operation result.
   */
  @NotNull
  public BulkOperationResult setStatus(@NotNull final Integer status)
  {
    this.status = status.toString();
    statusInt = status;
    return this;
  }

  /**
   * Retrieves the location of this bulk operation result.
   *
   * @return  The bulk operation result location.
   */
  @Nullable
  public String getLocation()
  {
    return location;
  }

  /**
   * Retrieves the method of this bulk operation result.
   *
   * @return  The bulk operation result method.
   */
  @NotNull
  public BulkOpType getMethod()
  {
    return method;
  }

  /**
   * Retrieves the bulk ID of this bulk operation result.
   *
   * @return  The bulk operation result bulk.
   */
  @Nullable
  public String getBulkId()
  {
    return bulkId;
  }

  /**
   * Retrieves the version of this bulk operation result.
   *
   * @return  The bulk operation result version.
   */
  @Nullable
  public String getVersion()
  {
    return version;
  }

  /**
   * Retrieves the response of this bulk operation result.
   *
   * @return  The bulk operation result response.
   */
  @Nullable
  public ObjectNode getResponse()
  {
    return response;
  }

  /**
   * Retrieves the response of this bulk operation result as a
   * {@link ScimResource} based POJO. By default, if a suitable class cannot be
   * found, the resource will be returned as a
   * {@link com.unboundid.scim2.common.GenericScimResource}.
   * <br><br>
   *
   * To register a custom resource type so that it the SCIM SDK automatically
   * supports additional classes, use the {@link BulkResourceMapper} class.
   *
   * @return  The bulk operation result response, or {@code null} if there was
   *          no response.
   * @throws IllegalStateException  If an error occurs while converting the
   *                                object into the ScimResource type.
   */
  @Nullable
  @JsonIgnore
  public ScimResource getResponseAsScimResource()
      throws IllegalStateException
  {
    if (response == null)
    {
      return null;
    }

    // Attempt fetching the class associated with the value(s) in the "schemas"
    // array. If there is not a resource defined, return a GenericScimResource.
    Class<ScimResource> clazz = BulkResourceMapper.get(response.get("schemas"));
    try
    {
      return JsonUtils.nodeToValue(response, clazz);
    }
    catch (JsonProcessingException e)
    {
      // This is most likely to occur if a resource contains a schema value but
      // is malformed in some way.
      throw new IllegalStateException("Failed to convert a bulk operation"
          + " result into a " + clazz.getName(), e);
    }
  }

  /**
   * Retrieves the status of this bulk operation result.
   *
   * @return  The bulk operation result status.
   */
  @NotNull
  public String getStatus()
  {
    return status;
  }

  /**
   * Returns the numerical value of {@link #getStatus()}.
   *
   * @return  The bulk operation result status as an integer.
   */
  @JsonIgnore
  public int getStatusInt()
  {
    return statusInt;
  }

  /**
   * When deserializing a JSON into this object, there's a possibility that an
   * unknown attribute is contained within the JSON. This method captures
   * attempts to set undefined attributes and ignores them in the interest of
   * preventing JsonProcessingException errors. This method should only be
   * called by Jackson.
   * <br><br>
   *
   * Since this object is very similar to {@link BulkOperation}, it's possible
   * that some fields that are generally not permitted in results could be
   * present within a JSON response. For this reason, such fields will be
   * ignored instead of failing the request, since these errors can be difficult
   * for application developers to work around.
   *
   * @param key           The unknown attribute name.
   * @param ignoredValue  The value of the attribute.
   */
  @JsonAnySetter
  protected void setAny(@NotNull final String key,
                        @NotNull final JsonNode ignoredValue)
  {
    // The value is not logged, since it's not needed and may contain PII.
    Debug.debug(Level.WARNING, DebugType.OTHER,
        "Attempted setting an undefined attribute: " + key);
  }

  @Override
  @NotNull
  public String toString()
  {
    try
    {
      var objectWriter = JsonUtils.getObjectWriter();
      return objectWriter.withDefaultPrettyPrinter().writeValueAsString(this);
    }
    catch (JsonProcessingException e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * Indicates whether the provided object is equal to this bulk operation
   * result.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this bulk
   *            operation result, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof BulkOperationResult that))
    {
      return false;
    }

    if (!Objects.equals(location, that.location))
    {
      return false;
    }
    if (!method.equals(that.method))
    {
      return false;
    }
    if (!Objects.equals(bulkId, that.bulkId))
    {
      return false;
    }
    if (!Objects.equals(version, that.version))
    {
      return false;
    }
    if (!status.equals(that.status))
    {
      return false;
    }
    return Objects.equals(response, that.response);
  }

  /**
   * Retrieves a hash code for this bulk operation result.
   *
   * @return  A hash code for this bulk operation result.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(location, method, bulkId, version, status, response);
  }

  /**
   * Provides another instance of this bulk operation result.
   *
   * @return  A copy of this BulkOperationResult.
   */
  @NotNull
  public BulkOperationResult copy()
  {
    ObjectNode responseCopy = (response == null) ? null : response.deepCopy();
    return new BulkOperationResult(
        method, status, location, responseCopy, bulkId, version);
  }
}
