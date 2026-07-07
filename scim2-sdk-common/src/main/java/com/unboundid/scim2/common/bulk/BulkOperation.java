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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.runtime.BulkRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.JsonUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.ApiConstants.BULK_PREFIX;
import static com.unboundid.scim2.common.utils.StaticUtils.toList;


/**
 * This class represents a SCIM 2 bulk operation. A bulk operation is an
 * individual write operation that is included within a {@link BulkRequest}. For
 * an introduction to SCIM bulk requests and responses, see {@link BulkRequest}.
 * <br><br>
 *
 * A bulk operation can contain the following properties, which collectively
 * specify information about the request.
 * <ul>
 *   <li> {@code method}: The HTTP method that should be used for the request.
 *        Permitted values are {@code POST}, {@code PUT}, {@code PATCH}, and
 *        {@code DELETE}.
 *   <li> {@code path}: The HTTP endpoint that the request should target.
 *   <li> {@code bulkId}: An optional field that allows other operations within
 *        the bulk request to reference this operation. This field may only be
 *        set on POST requests.
 *   <li> {@code version}: The ETag version of the resource. For more
 *        information, see {@link ETagConfig}.
 *   <li> {@code data}: The JSON body/payload of the SCIM request. For example,
 *        in a POST operation, this represents the resource to create.
 * </ul>
 * <br><br>
 *
 * The following JSON is an example bulk operation that creates a new user
 * resource. It may be included in a request to a SCIM service, alongside other
 * bulk operations.
 * <pre>
 *   {
 *     "method": "POST",
 *     "path": "/Users/fa1afe1",
 *     "bulkId": "qwerty",
 *     "data": {
 *       "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *       "userName": "Eggs",
 *       "active": true
 *     }
 *   }
 * </pre>
 *
 * This bulk operation can be created with the following Java code. These
 * objects are constructed with static methods such as {@link #delete(String)}.
 * <pre><code>
 *   UserResource user = new UserResource().setUserName("Eggs").setActive(true);
 *   BulkOperation op = BulkOperation.post("/Users", user).setBulkId("qwerty");
 * </code></pre>
 *
 * Note that the {@code bulkId} and {@code version} fields may be set with the
 * {@link #setBulkId} and {@link #setVersion} methods. This class also contains
 * the {@link #getDataAsScimResource()} method to easily fetch the {@code data}
 * field as a Java object.
 * <br><br>
 *
 * <h2>Bulk Patch Operations</h2>
 * A bulk operation with a "PATCH" method is called a bulk patch operation. This
 * is different from a standard SCIM {@link PatchOperation}, which is a subfield
 * of a {@link PatchRequest}. The UnboundID SCIM SDK always prints bulk patch
 * operations in the form of:
 * <pre>
 * {
 *   "method": "PATCH",
 *   "path": "/Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc",
 *   "data": {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
 *     "Operations": [ {
 *       "op": "remove",
 *       "path": "nickName"
 *     }, {
 *       "op": "replace",
 *       "path": "userName",
 *       "value": "Alice"
 *     }
 *   ] }
 * }
 * </pre>
 *
 * Some SCIM services may display the {@code data} of bulk patch operations
 * differently due to an error in the initial publication of RFC 7644, where the
 * field only contained a list of patch operations and was not valid JSON. The
 * <a href="https://errata.rfc-editor.org/eid5050/">errata</a> of RFC 7644 fixes
 * this however, and states that the {@code data} JSON should represent a
 * {@link PatchRequest} similar to a request to {@code PATCH /Users/{userID}}.
 * <br><br>
 *
 * To ensure broad compatibility with JSON that may be formatted differently,
 * the SCIM SDK is tolerant when converting JSON into a BulkOperation, as long
 * as an {@code Operations} field is present and contains patch operations. The
 * JSON will then be re-encoded to represent a full PatchRequest. Thus, when
 * {@link #getDataAsScimResource()} is called on any {@link BulkOperation} with
 * a method of {@code PATCH}, a {@link PatchRequest} will always be returned.
 *
 * @see BulkRequest
 * @since 5.1.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "method")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BulkOperation.PostOperation.class,
        names = {"POST", "Post", "post"}),
    @JsonSubTypes.Type(value = BulkOperation.PutOperation.class,
        names = {"PUT", "Put", "put"}),
    @JsonSubTypes.Type(value = BulkOperation.BulkPatchOperation.class,
        names = {"PATCH", "Patch", "patch"}),
    @JsonSubTypes.Type(value = BulkOperation.DeleteOperation.class,
        names = {"DELETE", "Delete", "delete"}),
})
@JsonPropertyOrder({ "method", "path", "bulkId", "version", "data" })
public abstract class BulkOperation
{
  /**
   * A type reference for deserializing a JsonNode into a patch operation list.
   */
  @NotNull
  private static final TypeReference<List<PatchOperation>> PATCH_REF =
      new TypeReference<>(){};

  /**
   * A flag that indicates whether the bulk operation contains a temporary
   * {@code bulkId} field that will likely be replaced during processing.
   */
  @JsonIgnore
  private boolean hasBulkReference;

  /**
   * This field represents the HTTP operation type (e.g., {@code POST}).
   */
  @NotNull
  private final BulkOpType method;

  /**
   * The endpoint that should receive the request. For example, to create a new
   * user, the value of this parameter should be {@code "/Users"}. Note that
   * this is not a full URI.
   * <br><br>
   *
   * This field refers to an HTTP path/endpoint, and not an attribute path for a
   * patch request (i.e., it is not a {@link Path}).
   */
  @NotNull
  private final String path;

  /**
   * This field represents an optional bulk identifier field, which allows other
   * operations in the same bulk request to reference this operation. For more
   * information, view the class-level documentation.
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
   * The data field containing the contents of the write request. This field
   * will be {@code null} only if this is a bulk delete operation.
   */
  @Nullable
  private final ObjectNode data;

  /**
   * Sets the bulk ID of the bulk operation. Since bulk IDs may only be used for
   * POST requests, this method throws an exception if this call does not target
   * a {@code POST}.
   *
   * @param bulkId  A bulk identifier that can allow other operations within
   *                a {@link BulkRequest} to reference the SCIM resource
   *                targeted by this bulk operation.
   * @return        This object.
   *
   * @throws BulkRequestException   If a caller attempts setting a bulk ID value
   *                                for a method other than POST.
   */
  @NotNull
  public BulkOperation setBulkId(@Nullable final String bulkId)
      throws BulkRequestException
  {
    if (method != BulkOpType.POST)
    {
      throw new BulkRequestException(
          "Bulk IDs may only be set for POST requests. Invalid HTTP method: "
              + method);
    }

    this.bulkId = bulkId;
    return this;
  }

  /**
   * Sets the ETag version for the bulk operation.
   * <br><br>
   *
   * When a new resource is contained in a bulk request, it does not exist until
   * after the bulk request has been processed successfully. Thus, a bulk post
   * operation cannot reference any resource with a version tag, since the SCIM
   * service has not yet assigned a version tag value. For this reason, calling
   * this method for a bulk post operation is not permitted.
   *
   * @param version   The version tag to use.
   * @return          This object.
   *
   * @throws BulkRequestException   If a caller attempts setting a version value
   *                                for a bulk POST request.
   */
  @NotNull
  public BulkOperation setVersion(@Nullable final String version)
      throws BulkRequestException
  {
    if (method == BulkOpType.POST)
    {
      throw new BulkRequestException(
          "Cannot set the 'version' field of a bulk POST operation.");
    }

    this.version = version;
    return this;
  }

  /**
   * Fetches the type of this bulk operation.
   *
   * @return  The bulk operation type.
   */
  @NotNull
  @JsonIgnore
  public BulkOpType getMethod()
  {
    return method;
  }

  /**
   * Fetches the HTTP path or endpoint targeted by this bulk operation (e.g.,
   * {@code /Users}).
   *
   * @return  The endpoint.
   */
  @NotNull
  public String getPath()
  {
    return path;
  }

  /**
   * Fetches the {@code bulkId} of the bulk operation, if it exists.
   *
   * @return  The bulk ID, or {@code null} if it is not set.
   */
  @Nullable
  public String getBulkId()
  {
    return bulkId;
  }

  /**
   * Fetches the ETag version associated with the bulk operation, if it exists.
   *
   * @return  The version, or {@code null} if it is not set.
   */
  @Nullable
  public String getVersion()
  {
    return version;
  }

  /**
   * Fetches the {@code data} field representing the update data for a SCIM
   * resource.
   * <br><br>
   *
   * This method returns the data as a JSON ObjectNode. If this field should be
   * represented as a Java object, use {@link #getDataAsScimResource()} instead.
   *
   * @return  The {@code data}.
   */
  @Nullable
  public ObjectNode getData()
  {
    return (data == null) ? null : data.deepCopy();
  }

  /**
   * This utility method obtains the {@code data} field of this bulk operation
   * and converts it to a {@link ScimResource}-based POJO. For example, a JSON
   * representing a user resource with a schema of
   * {@code urn:ietf:params:scim:schemas:core:2.0:User} would be returned as a
   * {@link UserResource}. By default, if a suitable class cannot be found, a
   * {@link GenericScimResource} instance will be returned.
   * <br><br>
   *
   * If you have custom classes that should be used, they may be registered with
   * the {@link BulkResourceMapper}. However, any JSON object may be manually
   * converted to a Java object with {@link JsonUtils#nodeToValue}:
   * <pre><code>
   *   ObjectNode data = bulkOperation.getData();
   *   if (data != null)
   *   {
   *     return JsonUtils.nodeToValue(data, CustomResource.class);
   *   }
   * </code></pre>
   *
   * For examples on how to use this method, see the class-level documentation
   * of {@link BulkRequest}.
   *
   * @return  The {@code data} field as an object of the specified class, or
   *          {@code null} for a delete request.
   *
   * @throws BulkRequestException  If an error occurs while converting the
   *                               object into a ScimResource type.
   */
  @Nullable
  @JsonIgnore
  public ScimResource getDataAsScimResource()
    throws BulkRequestException
  {
    try
    {
      return BulkResourceMapper.asScimResource(data);
    }
    catch (IllegalArgumentException e)
    {
      throw new BulkRequestException(
          "Failed to convert a malformed JSON into a SCIM resource.", e);
    }
  }

  /**
   * Replaces any temporary bulk ID values in the {@code data} field with a
   * real value. It is generally easier to call this on the BulkRequest object
   * (with {@link BulkRequest#replaceBulkIdValues}) since this will apply the
   * change to all operations within the bulk request.
   * <br><br>
   *
   * This method permanently alters the bulk operation directly, and it is not
   * thread safe.
   *
   * @param bulkId      The temporary bulk ID value. For example, to replace all
   *                    instances of {@code "bulkId:hallmarkCards"}, this string
   *                    should be set to {@code "hallmarkCards"}.
   * @param realValue   The real value after the resource has been created.
   */
  public void replaceBulkIdValue(@NotNull final String bulkId,
                                 @NotNull final String realValue)
  {
    if (!hasBulkReference)
    {
      return;
    }
    Objects.requireNonNull(bulkId);
    Objects.requireNonNull(realValue);
    Objects.requireNonNull(data);

    // Recursively traverse the node and replace all text bulk ID references to
    // the new value. This approach minimizes the amount of garbage generated.
    JsonUtils.replaceAllTextValues(data, BULK_PREFIX + bulkId, realValue);

    hasBulkReference = data.toString().contains(BULK_PREFIX);
  }

  /**
   * Constructor for common settings that apply to all bulk operations.
   *
   * @param opType  The bulk operation type.
   * @param path    The endpoint targeted by the request.
   * @param data    The JSON payload data. This may only be {@code null} for
   *                delete operations.
   */
  private BulkOperation(@NotNull final BulkOpType opType,
                        @NotNull final String path,
                        @Nullable final ObjectNode data)
  {
    Objects.requireNonNull(path,
        "The 'path' field cannot be null for a bulk operation.");
    if (opType != BulkOpType.DELETE)
    {
      Objects.requireNonNull(data,
          "The 'data' field cannot be null for a '" + opType + "' operation.");
    }

    this.method = opType;
    this.path = path;
    this.data = data;
    this.bulkId = null;
    this.version = null;

    // Mark whether this operation contains bulk references for short-circuiting
    // later.
    hasBulkReference = data != null && data.toString().contains(BULK_PREFIX);
  }

  /**
   * This class represents a bulk POST operation. To instantiate an operation of
   * this type, use {@link #post(String, ScimResource)}.
   */
  protected static final class PostOperation extends BulkOperation
  {
    private PostOperation(
        @NotNull @JsonProperty(value = "path", required = true)
        final String path,
        @NotNull @JsonProperty(value = "data", required = true)
        final ObjectNode data)
    {
      super(BulkOpType.POST, path, data);
    }
  }

  /**
   * This class represents a bulk PUT operation. To instantiate an operation of
   * this type, use {@link #put(String, ScimResource)}.
   */
  protected static final class PutOperation extends BulkOperation
  {
    private PutOperation(
        @NotNull @JsonProperty(value = "path", required = true)
        final String path,
        @NotNull @JsonProperty(value = "data", required = true)
        final ObjectNode data)
    {
      super(BulkOpType.PUT, path, data);
    }
  }

  /**
   * This class represents a bulk PATCH operation. To instantiate an operation
   * of this type, use {@link #patch(String, List)}.
   * <br><br>
   *
   * Implementation note: This class is named BulkPatchOperation to avoid
   * confusion with {@link PatchOperation} objects.
   */
  protected static final class BulkPatchOperation extends BulkOperation
  {
    // Represents the 'schemas' array value for a PatchRequest resource.
    @NotNull
    private static final ArrayNode PATCH_REQUEST_SCHEMAS =
        JsonUtils.getJsonNodeFactory().arrayNode()
            .add("urn:ietf:params:scim:api:messages:2.0:PatchOp");

    @JsonCreator
    private BulkPatchOperation(
        @NotNull @JsonProperty(value = "path", required = true)
        final String path,
        @NotNull @JsonProperty(value = "data", required = true)
        final ObjectNode data)
            throws BulkRequestException
    {
      // Re-encode the source JSON into an object to reformat the input.
      this(path, asPatchRequest(data));
    }

    private BulkPatchOperation(@NotNull final String path,
                               @NotNull final PatchRequest pr)
    {
      // The patch request is not cached since it may contain bulkID references.
      super(BulkOpType.PATCH, path, pr.asGenericScimResource().getObjectNode());
    }

    /**
     * This method deserializes data into a PatchRequest with extra flexibility.
     * Due to an erroneous patch request model in the first publication of RFC
     * 7644, there is variance on how this JSON data is displayed by some SCIM
     * services. This method is called from the constructor to guarantee that
     * all bulk patch operations have a usable PatchRequest model at runtime.
     */
    private static PatchRequest asPatchRequest(@NotNull final ObjectNode data)
        throws BulkRequestException
    {
      JsonNode operationsNode = data.get("Operations");
      if (operationsNode == null)
      {
        // Check for an Operations list that could have been empty.
        if (data.isEmpty() || PATCH_REQUEST_SCHEMAS.equals(data.get("schemas")))
        {
          return new PatchRequest(List.of());
        }

        // This most likely indicates that the JSON is not a bulk operation, and
        // instead represents some other data type.
        throw new BulkRequestException(
            "Could not parse the patch operation list from the bulk operation"
                + " because the value of 'Operations' is absent.");
      }
      if (!(operationsNode instanceof ArrayNode arrayNode))
      {
        throw new BulkRequestException(
            "Could not parse the patch operation list from the bulk operation"
                + " because the 'Operations' field is not an array.");
      }

      try
      {
        List<PatchOperation> patchOperations = JsonUtils.getObjectReader()
            .forType(PATCH_REF).readValue(arrayNode);
        return new PatchRequest(patchOperations);
      }
      catch (JacksonException e)
      {
        Debug.debugException(e);
        throw new BulkRequestException(
            "Failed to convert a malformed patch operation list.", e);
      }
    }
  }


  /**
   * This class represents a bulk DELETE operation. To instantiate an operation
   * of this type, use {@link #delete(String)}.
   */
  protected static final class DeleteOperation extends BulkOperation
  {
    private DeleteOperation(
        @NotNull @JsonProperty(value = "path", required = true)
        final String path)
    {
      super(BulkOpType.DELETE, path, null);
    }
  }

  /**
   * Constructs a bulk POST operation. The following example shows a JSON
   * representation of a bulk POST operation which creates a user with a bulk ID
   * value:
   * <pre>
   *  {
   *    "method": "POST",
   *    "path": "/Users",
   *    "bulkId": "qwerty",
   *    "data": {
   *      "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
   *      "userName": "Eggs",
   *      "active": true
   *    }
   * }
   * </pre>
   *
   * To create this bulk operation, use the following Java code.
   *
   * <pre><code>
   * UserResource user = new UserResource().setUserName("Eggs").setActive(true);
   * BulkOperation op = BulkOperation.post("/Users", user).setBulkId("qwerty");
   * </code></pre>
   *
   * @param endpoint  The endpoint/path that will receive the POST request,
   *                  e.g., {@code /Users}.
   * @param data      The SCIM resource to create.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation post(@NotNull final String endpoint,
                                   @NotNull final ScimResource data)
  {
    return post(endpoint, data.asGenericScimResource().getObjectNode());
  }

  /**
   * Constructs a bulk POST operation.
   *
   * @param endpoint  The endpoint/path that will receive the POST request,
   *                  e.g., {@code /Users}.
   * @param data      The SCIM resource to create, in ObjectNode form.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation post(@NotNull final String endpoint,
                                   @NotNull final ObjectNode data)
  {
    return new PostOperation(endpoint, data.deepCopy());
  }

  /**
   * Constructs a bulk PUT operation. The following example shows a JSON
   * representation of a bulk PUT operation:
   * <pre>
   *  {
   *    "method": "PUT",
   *    "path": "/Users/b7c14771-226c-4d05-8860-134711653041",
   *    "data": {
   *      "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
   *      "id": "b7c14771-226c-4d05-8860-134711653041",
   *      "userName": "Pseudo"
   *    }
   *  }
   * </pre>
   *
   * This operation can be created with the following Java code:
   * <pre><code>
   *   UserResource user = new UserResource().setUserName("Pseudo");
   *   user.setId("b7c14771-226c-4d05-8860-134711653041");
   *   BulkOperation.put("/Users/b7c14771-226c-4d05-8860-134711653041", user);
   * </code></pre>
   *
   * @param endpoint  The endpoint/path that will receive the PUT request, e.g.,
   *                  {@code /Users/b7c14771-226c-4d05-8860-134711653041}.
   * @param data      The SCIM resource to create.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation put(@NotNull final String endpoint,
                                  @NotNull final ScimResource data)
  {
    return put(endpoint, data.asGenericScimResource().getObjectNode());
  }

  /**
   * Constructs a bulk PUT operation.
   *
   * @param endpoint  The endpoint/path that will receive the PUT request, e.g.,
   *                  {@code /Users/b7c14771-226c-4d05-8860-134711653041}.
   * @param data      The SCIM resource to create.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation put(@NotNull final String endpoint,
                                  @NotNull final ObjectNode data)
  {
    return new PutOperation(endpoint, data.deepCopy());
  }

  /**
   * Constructs a bulk patch operation. For example:
   * <pre>
   * {
   *   "method": "PATCH",
   *   "path": "/Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc",
   *   "data": {
   *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
   *     "Operations": [ {
   *       "op": "remove",
   *       "path": "nickName"
   *     }, {
   *       "op": "replace",
   *       "path": "userName",
   *       "value": "Alice"
   *     }
   *   ] }
   * }
   * </pre>
   *
   * This operation can be created with the following Java code:
   * <pre><code>
   *   String httpPath = "/Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc";
   *   BulkOperation.patch(httpPath,
   *       PatchOperation.remove("nickName"),
   *       PatchOperation.replace("userName", "Alice")
   *   );
   * </code></pre>
   *
   * @param endpoint  The endpoint/path that will receive the PATCH request,
   *                  e.g., {@code /Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc}.
   * @param op1       The first non-null patch operation to be provided in the
   *                  bulk operation.
   * @param ops       An optional field for additional patch operations. Any
   *                  {@code null} values will be ignored.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String endpoint,
                                    @NotNull final PatchOperation op1,
                                    @Nullable final PatchOperation... ops)
  {
    return new BulkPatchOperation(endpoint, new PatchRequest(toList(op1, ops)));
  }

  /**
   * Constructs a bulk patch operation.
   *
   * @param endpoint  The endpoint/path that will receive the PATCH request,
   *                  e.g., {@code /Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc}.
   * @param ops       The list of patch operations to be provided in the bulk
   *                  operation.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String endpoint,
                                    @Nullable final List<PatchOperation> ops)
  {
    List<PatchOperation> operationList = (ops == null) ? List.of() : ops;
    operationList = operationList.stream().filter(Objects::nonNull).toList();
    return new BulkPatchOperation(endpoint, new PatchRequest(operationList));
  }

  /**
   * Creates a bulk patch operation. The provided ObjectNode should be
   * structured as a {@link PatchRequest}:
   * <pre>
   *   {
   *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
   *     "Operations": [
   *       {
   *         (patch operation 1),
   *         (patch operation 2)
   *       },
   *       ...
   *     ]
   *   }
   * </pre>
   *
   * @param endpoint  The endpoint/path that will receive the PATCH request,
   *                  e.g., {@code /Users/b7c14771-226c-4d05-8860-134711653041}.
   * @param data      The ObjectNode representing the list of
   *                  {@link PatchOperation} objects.
   *
   * @return  The new bulk operation.
   * @throws BulkRequestException  If the ObjectNode is improperly formatted.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String endpoint,
                                    @NotNull final ObjectNode data)
      throws BulkRequestException
  {
    return new BulkPatchOperation(endpoint, Objects.requireNonNull(data));
  }

  /**
   * Constructs a bulk DELETE operation. This represents an individual delete
   * operation for a resource. The following example shows a JSON representation
   * of a bulk DELETE operation:
   * <pre>
   *  {
   *    "method": "DELETE",
   *    "path": "/Users/b7c14771-226c-4d05-8860-134711653041"
   *  }
   * </pre>
   *
   * @param endpoint  The endpoint/path that will receive the DELETE request,
   *                  e.g., {@code /Users/b7c14771-226c-4d05-8860-134711653041}.
   *
   * @return  The new bulk operation.
   */
  @NotNull
  public static BulkOperation delete(@NotNull final String endpoint)
  {
    return new DeleteOperation(endpoint);
  }

  /**
   * Retrieves a string representation of this bulk operation.
   *
   * @return  A string representation of this bulk operation.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.getObjectWriter().withDefaultPrettyPrinter()
        .writeValueAsString(this);
  }

  /**
   * Indicates whether the provided object is equal to this bulk operation.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this bulk
   *            operation, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof BulkOperation that))
    {
      return false;
    }

    return Objects.equals(method, that.method)
        && Objects.equals(path, that.path)
        && Objects.equals(bulkId, that.bulkId)
        && Objects.equals(version, that.version)
        && Objects.equals(data, that.data);
  }

  /**
   * Retrieves a hash code for this bulk operation.
   *
   * @return  A hash code for this bulk operation.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(method, path, bulkId, version, data);
  }

  /**
   * Provides another instance of this bulk operation.
   *
   * @return  A copy of this bulk operation.
   */
  @NotNull
  @SuppressWarnings("DataFlowIssue")
  public BulkOperation copy()
  {
    return switch (method)
    {
      case POST -> post(path, data).setBulkId(bulkId);
      case PUT -> put(path, data).setVersion(version);
      case PATCH -> patch(path, data).setVersion(version);
      case DELETE -> delete(path).setVersion(version);
    };
  }
}
