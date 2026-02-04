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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.DebugType;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;


/**
 * This class represents a SCIM 2 bulk operation.  A bulk operation is an
 * individual write request that is included within a bulk request, For more
 * background on SCIM bulk requests and responses, see {@link BulkRequest}. For
 * fields contained within a {@link BulkResponse} to a client, see
 * {@link BulkOperationResult}, which contains similar but slightly different
 * fields.
 * <br><br>
 *
 * The following JSON is an example bulk operation that requests the creation of
 * a user resource. It may be included in a request with other bulk operations.
 * <pre>
 * {
 *     "method": "POST",
 *     "path": "/Users",
 *     "bulkId": "qwerty",
 *     "data": {
 *         "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *         "userName": "bill",
 *         "active": true
 *     }
 * }
 * </pre>
 *
 * A bulk operation can contain the following properties, which specify some
 * information about the request.
 * <ul>
 *   <li> {@code method}: The HTTP method that should be used for the request.
 *        Permitted values are {@code POST}, {@code PUT}, {@code PATCH}, and
 *        {@code DELETE}.
 *   <li> {@code path}: The path or endpoint that the request should target.
 *   <li> {@code bulkId}: An optional field that allows other operations within
 *        the bulk request to reference this operation. This may only be used
 *        for POST requests.
 *   <li> {@code version}: The ETag version of the resource. For more
 *        information, see {@link ETagConfig}.
 *   <li> {@code data} The JSON body/payload of the SCIM request.
 * </ul>
 * <br><br>
 *
 * To construct a bulk operation, use one of the static methods defined on this
 * class. Some examples include:
 * <ul>
 *   <li> {@link #post(String, ScimResource)}
 *   <li> {@link #put(String, ScimResource)}
 *   <li> {@link #patch(String, PatchOperation, PatchOperation...)}
 *   <li> {@link #delete(String)}
 * </ul>
 *
 * The example JSON above can be created with the following Java code. For more
 * examples, view the documentation for each method (e.g., {@link #delete}).
 * <pre><code>
 *   UserResource user = new UserResource().setUserName("bill").setActive(true);
 *   BulkOperation op = BulkOperation.post("/Users", user)
 *       .setBulkId("qwerty");
 * </code></pre>
 *
 * Note that the {@code bulkId} and {@code version} fields may be set with the
 * {@link #setBulkId} and {@link #setVersion} methods.
 * <br><br>
 *
 * Because bulk operations can contain data of many forms, this class contains
 * the following helper methods to help easily format the {@code data} field
 * into useful forms:
 * <ul>
 *   <li> {@link #getDataAs(Class)}: Reformats the JSON object node into the
 *                                   requested class.
 *   <li> {@link #getPatchOperationList}: Reformats the JSON object node into a
 *                                        list of {@link PatchOperation} fields.
 * </ul>
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
   * This field represents the HTTP operation type (e.g., POST).
   */
  @NotNull
  protected BulkOpType method;

  /**
   * The endpoint that should receive the request. For example, to create a new
   * user, the "path" parameter should point to the {@code /Users} endpoint.
   * Note that this refers to an HTTP path/endpoint, and not an attribute path
   * (i.e., it is not a {@link Path}).
   */
  @NotNull
  protected String path;

  /**
   * This field represents an optional bulk identifier field, which allows other
   * operations in the same bulk request to reference this operation. Bulk IDs
   * are useful to reference resources that will be created by a bulk request,
   * but don't yet exist.
   * <br><br>
   *
   * For example, consider a bulk request that creates one user and one group
   * that contains the user. This can be represented with:
   * <pre>
   * {
   *   "schemas": ["urn:ietf:params:scim:api:messages:2.0:BulkRequest"],
   *   "Operations": [
   *     {
   *       "method": "POST",
   *       "path": "/Users",
   *       "bulkId": "qwerty",
   *       "data": {
   *         "schemas": ["urn:ietf:params:scim:schemas:core:2.0:User"],
   *         "userName": "Alice"
   *       }
   *     },
   *     {
   *       "method": "POST",
   *       "path": "/Groups",
   *       "data": {
   *         "schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],
   *         "displayName": "Tour Guides",
   *         "members": [
   *           {
   *             "type": "User",
   *             "value": "bulkId:qwerty"
   *           }
   *         ]
   *       }
   *     }
   *   ]
   * }
   * </pre>
   *
   * The first operation creates a user and denotes it with a bulk ID of
   * "qwerty". The second operation creates a group and adds that user as a
   * member of the group by referencing its bulk ID.
   */
  @Nullable
  protected String bulkId;

  /**
   * The optional version tag, which can be used if the SCIM service provider
   * supports ETag versioning. See {@link ETagConfig} for more information.
   */
  @Nullable
  protected String version;

  /**
   * The data field containing the contents of the write request. This field
   * will be {@code null} only if this is a bulk delete operation.
   */
  @Nullable
  protected ObjectNode data;

  /**
   * Sets the {@code bulkId} value of the bulk operation. This field may only be
   * assigned for bulk POST operations.
   *
   * @param newId  A "bulk ID" identifier that can allow other operations
   *               within a {@link BulkRequest} to reference the SCIM resource
   *               targeted by this bulk operation.
   * @return       This object.
   *
   * @throws IllegalArgumentException   If a caller attempts setting a bulk
   *                                    operation for a method other than POST.
   */
  @NotNull
  public BulkOperation setBulkId(@Nullable final String newId)
      throws IllegalArgumentException
  {
    if (method != BulkOpType.POST)
    {
      throw new IllegalArgumentException(
          "Bulk IDs may only be set for POST requests. Invalid HTTP method: "
              + method);
    }

    bulkId = newId;
    return this;
  }

  /**
   * Sets the ETag version for the bulk operation. For more information on
   * ETags, see {@link ETagConfig}.
   * <br><br>
   *
   * This method may not be called for bulk post operations. When a resource is
   * specified in a bulk post request, that resource has not been created yet.
   * Thus, the SCIM service has not yet assigned a version tag to this resource,
   * so it is impossible for the client to use a version tag for any bulk post
   * operation.
   *
   * @param version   The version tag to use.
   * @return          This object.
   *
   * @throws IllegalStateException  If this method was invoked on a bulk post
   *                                operation.
   */
  @NotNull
  public BulkOperation setVersion(@Nullable final String version)
      throws IllegalStateException
  {
    if (method == BulkOpType.POST)
    {
      throw new IllegalStateException(
          "Cannot set the 'version' field of a bulk POST operation."
      );
    }

    this.version = version;
    return this;
  }

  /**
   * Returns the type of this bulk operation.
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
   * Returns the type of this bulk operation. This is an alternate name for the
   * {@link #getMethod} function.
   *
   * @return  The bulk operation type.
   */
  @NotNull
  @JsonIgnore
  public final BulkOpType getOperationType()
  {
    return getMethod();
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
   * Fetches the ETag associated with the bulk operation, if it exists.
   *
   * @return  The {@code version}, or {@code null} if it is not set.
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
   * This method returns the data as a JSON ObjectNode. If you need the data
   * as a Java object, use the {@link #getDataAs} or
   * {@link #getPatchOperationList} methods instead.
   *
   * @return  The {@code data}.
   */
  @Nullable
  public ObjectNode getData()
  {
    return (data == null) ? null : data.deepCopy();
  }

  /**
   * Fetches the {@code data} field of the bulk operation as the specified
   * class. For example, if the data is known to be a user resource, this
   * can be fetched as a {@link UserResource} object with:
   * <pre><code>
   *   try
   *   {
   *     UserResource user = bulkOperation.getDataAs(UserResource.class);
   *   }
   *   catch (BadRequestException e)
   *   {
   *     // The data was not a properly formatted user. Handle the exception.
   *   }
   * </code></pre>
   *
   * To retrieve the contents of a bulk patch operation, use the
   * {@link #getPatchOperationList()} method instead.
   *
   * @param <T>     The Java type that should be returned.
   * @param clazz   The class that should be returned.
   * @return        The {@code data} field as an object of the specified class.
   *
   * @throws BadRequestException  If the data could not be transformed into the
   *                              requested class.
   */
  @Nullable
  @JsonIgnore
  public <T> T getDataAs(@NotNull final Class<T> clazz)
      throws BadRequestException
  {
    try
    {
      return JsonUtils.nodeToValue(data, clazz);
    }
    catch (JsonProcessingException e)
    {
      // Re-throw with a clear error message.
      throw new BadRequestException("Failed to convert the bulk operation's"
          + " data to a " + clazz.getName() + ".", e);
    }
  }

  /**
   * Fetches the {@code data} field of the bulk operation as a list of patch
   * operations. This method may only be used for bulk PATCH operations.
   *
   * @return  The list of patch operations in the bulk operation.
   * @throws IllegalStateException  If this bulk operation is not a bulk PATCH.
   */
  @NotNull
  @JsonIgnore
  public List<PatchOperation> getPatchOperationList()
      throws IllegalStateException
  {
    if (this instanceof BulkPatchOperation bulkPatchOp)
    {
      return bulkPatchOp.patchOperationList;
    }

    throw new IllegalStateException("Attempted fetching patch data for a '"
        + method + "' bulk operation.");
  }

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

    method = opType;
    this.path = path;
    this.data = data;
    this.bulkId = null;
    this.version = null;
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
   * confusion with the individual {@link PatchOperation} objects is contained
   * within this bulk operation.
   */
  protected static final class BulkPatchOperation extends BulkOperation
  {
    @NotNull
    private final List<PatchOperation> patchOperationList;

    private BulkPatchOperation(
        @NotNull @JsonProperty(value = "path", required = true)
        final String path,
        @NotNull @JsonProperty(value = "data", required = true)
        final ObjectNode data)
            throws IllegalArgumentException
    {
      super(BulkOpType.PATCH, path, data);
      patchOperationList = parseOperations(data);
    }

    /**
     * Converts a bulk patch operation's {@code data} field into a usable list
     * of PatchOperations.
     */
    private List<PatchOperation> parseOperations(@NotNull final ObjectNode data)
        throws IllegalArgumentException
    {
      JsonNode operationsNode = data.get("Operations");
      if (operationsNode == null)
      {
        throw new IllegalArgumentException(
            "Could not parse the patch operation list from the bulk operation"
                + " because the value of 'Operations' is absent.");
      }
      if (!(operationsNode instanceof ArrayNode operationList))
      {
        throw new IllegalArgumentException(
            "Could not parse the patch operation list from the bulk operation"
                + " because the 'Operations' field is not an array.");
      }

      ArrayList<PatchOperation> resultList = new ArrayList<>();
      try
      {
        for (var op : operationList)
        {
          var convertedPatch = JsonUtils.nodeToValue(op, PatchOperation.class);
          resultList.add(convertedPatch);
        }
      }
      catch (JsonProcessingException e)
      {
        Debug.debugException(e);
        throw new IllegalArgumentException(e);
      }

      return resultList;
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
   *      "userName": "bill",
   *      "active": true
   *    }
   * }
   * </pre>
   *
   * To create this bulk operation, use the following Java code. Note that the
   * value of the bulk ID is set with the {@link #setBulkId} method.
   *
   * <pre><code>
   * UserResource user = new UserResource()
   *     .setUserName("bill")
   *     .setActive(true);
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
   *      "userName": "Bob"
   *    }
   *  }
   * </pre>
   *
   * This operation can be created with the following Java code:
   * <pre><code>
   *   UserResource user = new UserResource().setUserName("Bob");
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
   *     "Operations": [
   *         {
   *             "op": "remove",
   *             "path": "nickName"
   *         },
   *         {
   *             "op": "replace",
   *             "path": "userName",
   *             "value": "Dave"
   *         }
   *       ]}
   * }
   * </pre>
   *
   * This operation can be created with the following Java code:
   * <pre><code>
   *   String httpPath = "/Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc";
   *   BulkOperation.patch(httpPath,
   *       PatchOperation.remove("nickName"),
   *       PatchOperation.replace("userName", "Dave")
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
    return patch(endpoint, toList(op1, ops));
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

    // Convert the list to an ArrayNode by iteration, since valueToNode() can
    // miss fields if the entire list is passed in.
    ArrayNode array = JsonUtils.getJsonNodeFactory().arrayNode();
    for (PatchOperation operation : operationList)
    {
      array.add(JsonUtils.valueToNode(operation));
    }

    ObjectNode node = JsonUtils.getJsonNodeFactory().objectNode();
    return patch(endpoint, node.set("Operations", array));
  }

  /**
   * Creates a bulk patch operation. This ObjectNode should look very similar to
   * a {@link com.unboundid.scim2.common.messages.PatchRequest} JSON. An example
   * is shown below:
   * <pre>
   *   {
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
   * @throws IllegalArgumentException  If the ObjectNode improperly formatted.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String endpoint,
                                    @NotNull final ObjectNode data)
      throws IllegalArgumentException
  {
    return new BulkPatchOperation(endpoint, data.deepCopy());
  }

  /**
   * Constructs a bulk DELETE operation. This represents an individual delete
   * operation for a resource, where the operation is contained within a bulk
   * request. The following example shows a JSON representation of a bulk DELETE
   * operation:
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
   * When deserializing a JSON into this object, there's a possibility that an
   * unknown attribute is contained within the JSON. This method captures
   * attempts to set undefined attributes and ignores them in the interest of
   * preventing JsonProcessingException errors. This method should only be
   * called by Jackson.
   * <br><br>
   *
   * Since this object is similar to {@link BulkOperationResult}, it's possible
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

  /**
   * Retrieves a string representation of this bulk operation.
   *
   * @return  A string representation of this bulk operation.
   */
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

    if (!Objects.equals(method, that.method))
    {
      return false;
    }
    if (!Objects.equals(path, that.path))
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
    return Objects.equals(data, that.data);
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
