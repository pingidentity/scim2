/*
 * Copyright 2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
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
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.DebugType;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Objects;
import java.util.logging.Level;


/**
 * This class represents an operation contained within a {@link BulkResponse}.
 * <br><br>
 *
 * According to RFC 7644, operations contained within {@link BulkRequest} and
 * {@link BulkResponse} entities are the same, with differing rules on certain
 * attributes. However, there are specific restrictions on when attributes
 * may be used. Some only apply to requests (e.g., {@code data}}, some only
 * apply to a response (e.g., {@code status}), and some have other restrictions
 * that must be considered (e.g., failed POST responses should not include a
 * {@code location}, but all other successful responses and error responses must
 * provide this). To help enforce these considerations, BulkRequest objects
 * contain {@link BulkOperation} objects, and BulkResponse objects contain
 * {@link BulkOperationResult} objects.
 * <br><br>
 *
 * Bulk operation responses contain the following fields:
 * <ul>
 *   <li> {@code location}: The location URI for the resource that was targeted.
 *        This field MUST be present, except in the case that a POST operation
 *        fails. Since a failure to create a resource means that a URI does not
 *        exist, this is the only case where the location will be {@code null}.
 *   <li> {@code method}: The HTTP method that was used for the request.
 *   <li> {@code response}: The JSON response body for the write request. If the
 *        response was successful, this may optionally be {@code null}, but it
 *        must be present for an error.
 *   <li> {@code status}: The HTTP status code for the operation (e.g.,
 *        {@code 200 OK}, {@code 404 NOT FOUND}, etc.
 *   <li> {@code bulkId}: The bulk ID that was provided in the POST request.
 *   <li> {@code version}: The ETag version of the resource. For more
 *        information, see {@link ETagConfig}.
 * </ul>
 * <br><br>
 *
 * The following is an example of a bulk operation result that corresponds to
 * a successful user create response:
 * TODO: Freshen examples with a consistent theme
 * <pre>
 * {
 *   "location" : "/Users/fa1afe1",
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
 * The easiest way is to use the constructor that accepts a source bulk operation
 * from a client request:
 * <pre><code>
 *   var result = new BulkOperationResult(sourceOp, "200", "/Users/fa1afe1")
 *       .setResponse(userResource);
 *
 *   // If desired, the fields may be set directly.
 *   UserResource userResource = new UserResource().setUserName("name");
 *   BulkOperationResult result = new BulkOperationResult(BulkOpType.POST,
 *       "200",
 *       "/Users/fa1afe1",
 *       userResource.asGenericScimResource().getObjectNode(),
 *       "originalBulkId",
 *       null);
 * </code></pre>
 *
 * Note that unlike {@link BulkOperation}, this method does not use static
 * helper methods for successful bulk results since these all have the same
 * form.
 * <br><br>
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
 *   ErrorResponse response = BadRequestException.invalidSyntax(
 *       "Request is unparsable or violates the schema.")
 *       .getScimError();
 *   BulkOperationResult.error(sourceOp, response, null);
 *   BulkOperationResult.error(BulkOpType.POST, response, null);
 *
 *   // POST errors will not have a 'location', but other operation types do.
 *   BulkOperationResult.error(PUT, new ErrorResponse(500), "/Users/fa1afe1");
 * </code></pre>
 * <br><br>
 * // TODO: Test deserializing with BulkOperation fields since we
 *          ignore unknown fields for this class.
 */
@JsonPropertyOrder({ "location", "method", "bulkId", "version", "response",
                     "status" })
public class BulkOperationResult
{
  @NotNull public static final String HTTP_STATUS_OK = "200";
  @NotNull public static final String HTTP_STATUS_CREATED = "201";
  @NotNull public static final String HTTP_STATUS_NO_CONTENT = "204";
  @NotNull public static final String HTTP_STATUS_NOT_FOUND = "404";
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
    this.status = Objects.requireNonNull(status);
    this.location = location;
    this.response = response;
    this.bulkId = bulkId;
    this.version = version;

    boolean isUnsuccessfulPost = method == BulkOpType.POST &&
        (status.startsWith("4") || status.startsWith("5"));
    if (!isUnsuccessfulPost && location == null)
    {
      throw new IllegalArgumentException(
          "The 'location' of a bulk response must be defined, with the"
              + " exception of unsuccessful POST requests."
      );
    }
  }

  /**
   * Constructs a bulk operation result that represents an error. See the
   * class-level documentation for an example.
   *
   * @param operation       The bulk operation that was attempted.
   * @param errorResponse   The SCIM error response that should be displayed in
   *                        the {@code response} field.
   * @param location        The URI of the resource targeted by the bulk
   *                        operation. This field will be ignored for POST
   *                        operations, as the resource will not exist. This
   *    *                   value may only be {@code null} for failed POSTs.
   *
   * @return  A bulk operation result that represents an error.
   */
  public static BulkOperationResult error(
      @NotNull final BulkOperation operation,
      @NotNull final ErrorResponse errorResponse,
      @Nullable final String location)
  {
    final BulkOpType method = operation.getMethod();
    BulkOperationResult result = error(method, errorResponse, location);

    // Bulk IDs should only be handled for POST requests.
    String bulkId = (method == BulkOpType.POST) ? operation.getBulkId() : null;
    return result.setBulkId(bulkId);
  }

  /**
   * Constructs a bulk operation result that represents an error.
   *
   * @param opType          The type of the bulk operation.
   * @param errorResponse   The SCIM error response that should be displayed in
   *                        the {@code response} field.
   * @param location        The URI of the resource targeted by the bulk
   *                        operation. This field will be ignored for POST
   *                        operations, as the resource will not exist. This
   *                        value may only be {@code null} for failed POSTs.
   *
   * @return  A bulk operation result that represents an error.
   */
  public static BulkOperationResult error(
      @NotNull final BulkOpType opType,
      @NotNull final ErrorResponse errorResponse,
      @Nullable final String location)
  {
    // A failure to create a resource means that there is no location to return.
    String locationValue = (opType == BulkOpType.POST) ? null : location;

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
  public BulkOperationResult setLocation(@Nullable final String location)
  {
    if (method != BulkOpType.POST)
    {
      Objects.requireNonNull(location);
    }

    this.location = location;
    return this;
  }

  public BulkOperationResult setBulkId(@Nullable final String bulkId)
  {
    this.bulkId = bulkId;
    return this;
  }

  public BulkOperationResult setVersion(@Nullable final String version)
  {
    this.version = version;
    return this;
  }

  @JsonSetter
  public BulkOperationResult setResponse(@Nullable final ObjectNode response)
  {
    this.response = response;
    return this;
  }

  public BulkOperationResult setResponse(@Nullable final ScimResource response)
  {
    ObjectNode node = (response == null) ?
        null : response.asGenericScimResource().getObjectNode();
    return setResponse(node);
  }

  @JsonSetter
  public BulkOperationResult setStatus(@NotNull final String status)
  {
    Objects.requireNonNull(status);
    this.status = status;
    return this;
  }

  public BulkOperationResult setStatus(@NotNull final Integer status)
  {
    this.status = status.toString();
    return this;
  }

  @Nullable
  public String getLocation()
  {
    return location;
  }

  @NotNull
  public BulkOpType getMethod()
  {
    return method;
  }

  @Nullable
  public String getBulkId()
  {
    return bulkId;
  }

  @Nullable
  public String getVersion()
  {
    return version;
  }

  @Nullable
  public ObjectNode getResponse()
  {
    return response;
  }

  @NotNull
  public String getStatus()
  {
    return status;
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
