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
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.BulkRequestException;
import com.unboundid.scim2.common.exceptions.BulkResponseException;
import com.unboundid.scim2.common.exceptions.ContentTooLargeException;
import com.unboundid.scim2.common.exceptions.RateLimitException;
import com.unboundid.scim2.common.exceptions.ResourceConflictException;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;


/**
 * This class represents a SCIM 2 bulk request as described by
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.7">
 * RFC 7644 Section 3.7</a>.
 *
 * <h2>SCIM 2 Bulk Requests and Responses</h2>
 *
 * Issuing a REST API call always incurs request processing overhead, namely
 * DNS resolution, TCP handshakes and termination, and TLS negotiation. This is
 * necessary work for all API calls, but can become expensive in cases where a
 * client anticipates sending large amounts of traffic (e.g., on the order of
 * millions of requests). A SCIM bulk request is an optimization that combines
 * multiple write requests into a single API call, minimizing the number of
 * times that overhead costs are paid by both the client and server. The
 * following documentation discusses some key points on utilizing this class
 * to support bulk workflows. For further background, see
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.7">
 * RFC 7644</a>.
 * <br><br>
 *
 * The SCIM standard's protocol for batching requests is centered on write
 * traffic. Clients may issue a "bulk request" to a SCIM service, which
 * represents a collection of write "operations". After the SCIM service has
 * processed the request, it will respond with a {@link BulkResponse}, which
 * contains a summary of the request processing that was performed by the
 * service. Bulk requests contain a list of {@link BulkOperation} objects, each
 * representing a write operation, and bulk responses contain a list of
 * {@link BulkOperationResult} objects, each representing the result of a client
 * operation that was processed.
 * <br><br>
 *
 * Note that HTTP infrastructure generally has limits on the size of request
 * payloads, so clients should be careful to avoid placing too many operations
 * in a bulk request, as it could exceed service bulk limits. To determine the
 * limits and configuration of a SCIM service provider, consult the service's
 * {@link ServiceProviderConfigResource /ServiceProviderConfig} endpoint, or
 * consult the vendor's documentation.
 * <br><br>
 *
 * Bulk requests, which are represented by this class, contain the following
 * fields:
 * <ul>
 *   <li> {@code failOnErrors}: An optional integer that specifies a threshold
 *        for the amount of acceptable errors. If this field is not set, then
 *        the SCIM service will attempt to fulfill as many operations as it can.
 *        For more detail on how this is defined, see {@link #setFailOnErrors}.
 *   <li> {@code Operations}: A list of {@link BulkOperation} values, where each
 *        operation represents a single write request.
 * </ul>
 * <br><br>
 *
 * The following JSON is an example of a bulk request. This request contains two
 * bulk operations that each attempt to create a user. The {@code failOnErrors}
 * parameter is not specified in this bulk request, so the SCIM service should
 * attempt to process all provided bulk operations.
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
 *   "Operations": [ {
 *     "method": "POST",
 *     "path": "/Users",
 *     "data": {
 *       "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *       "userName": "Alice"
 *     }
 *   }, {
 *     "method": "POST",
 *     "path": "/Users",
 *     "data": {
 *       "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *       "userName": "Whiplash!"
 *     }
 *   } ]
 * }
 * </pre>
 *
 * After this request is processed by a SCIM service, it may return a bulk
 * response such as the following:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkResponse" ],
 *   "Operations": [ {
 *     "method": "POST",
 *     "response": {
 *       "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *       "scimType": "uniqueness",
 *       "detail": "The requested username was not available.",
 *       "status": "400"
 *     },
 *     "status": "400"
 *   }, {
 *     "location": "https://example.com/v2/Users/5b261bdf",
 *     "method": "POST",
 *     "response": {
 *       "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *       "userName": "Whiplash!"
 *       "id": "5b261bdf",
 *       "meta": {
 *         "created": "1970-01-01T13:00:00Z"
 *       },
 *     },
 *     "status": "201"
 *   } ]
 * }
 * </pre>
 *
 * In the above response, the SCIM service responded to each user request
 * differently. For the first user, an error was returned since the service
 * enforces uniqueness for usernames, and the requested username was already
 * taken. The second user was processed successfully and returned a response
 * that would be seen from a typical SCIM POST request to the {@code /Users}
 * endpoint. Note that the SCIM standard states that the {@code response} field
 * is always defined with information in the event of an error, but successful
 * responses may omit the value to reduce the size of the response.
 * <br><br>
 *
 * To create the above bulk request and response, the following Java code may
 * be used.
 * <pre><code>
 *   // Create the bulk request.
 *   BulkRequest bulkRequest = new BulkRequest(
 *     BulkOperation.post("/Users", new UserResource().setUserName("Alice")),
 *     BulkOperation.post("/Users", new UserResource().setUserName("Whiplash!"))
 *   );
 *
 *   // Create the bulk response.
 *   //
 *   // This example uses raw values to showcase the appropriate library calls.
 *   // In production systems, metadata values are obtained from the data store.
 *   BadRequestException e = BadRequestException.uniqueness(
 *       "The requested username was not available.");
 *   final BulkOperationResult failedResult =
 *       new BulkOperationResult(e, BulkOpType.POST, null);
 *
 *   final BulkOperationResult successResult = new BulkOperationResult(
 *       BulkOpType.POST,
 *       BulkOperationResult.HTTP_STATUS_CREATED,
 *       "https://example.com/v2/Users/5b261bdf");
 *   // Set the JSON payload value.
 *   UserResource user = new UserResource().setUserName("Whiplash!");
 *   user.setId("5b261bdf");
 *   user.setMeta(new Meta().setCreated(Calendar.getInstance()));
 *   successResult.setResponse(user);
 *
 *   BulkResponse response = new BulkResponse(failedResult, successResult);
 * </code></pre>
 *
 * <h2>Handling Bulk Request and Response Data</h2>
 *
 * Before discussing an example workflow, it's important to highlight how
 * application code should manage JSON data contained within bulk requests and
 * responses. As seen in the example JSON object above, SCIM resource objects
 * are embedded within bulk requests (within the {@code data} field) and bulk
 * responses (within the {@code response} field). However, the objects contained
 * within these fields could be anything: they could be error responses, user
 * resources, group resources, or custom resource types defined by the service.
 * Since the objects returned are not guaranteed to be the same type, this class
 * is not typed like {@link ListResponse}. Instead, the UnboundID SCIM SDK
 * provides two helper methods to simplify conversion of any SCIM resource that
 * is placed within this field. This manages the conversion of JSON data into
 * Java POJOs by viewing the {@code schemas} attribute. These are useful whether
 * you are handling a bulk request or response:
 * <ul>
 *   <li> {@link BulkOperation#getDataAsScimResource()}
 *   <li> {@link BulkOperationResult#getResponseAsScimResource()}
 * </ul>
 * <br><br>
 *
 * With these methods, along with the {@code instanceof} keyword, application
 * code can easily and directly handle object representations of SCIM resources.
 * As an example, a service handling client bulk requests may use the following
 * structure. This example does not handle client bulk ID values (see
 * {@link BulkOperation}), and it is intentionally verbose to indicate cases that
 * can occur for all types of requests and responses.
 * <pre><code>
 *   public BulkResponse processBulkRequest(BulkRequest bulkRequest)
 *   {
 *     int errorCount = 0;
 *     List&lt;BulkOperationResult&gt; results = new ArrayList&lt;&gt;();
 *     for (BulkOperation op : bulkRequest)
 *     {
 *       BulkOperationResult result = switch (op.getMethod())
 *       {
 *         case POST -> processPostOperation(op);
 *         // ...
 *       };
 *
 *       // Add the result to the list.
 *       results.add(result);
 *
 *       // Track whether this result exceeded the failure threshold.
 *       if (result.isClientError() || result.isServerError())
 *       {
 *         errorCount++;
 *       }
 *       if (errorCount >= bulkRequest.getFailOnErrorsNormalized())
 *       {
 *         // Stop processing since the error threshold was reached.
 *         break;
 *       }
 *     }
 *
 *     // Processing completed. Return the result.
 *     return new BulkResponse(results);
 *   }
 *
 *   private BulkOperationResult processPostOperation(BulkOperation op)
 *   {
 *     ScimResource data = op.getDataAsScimResource();
 *     if (data == null)
 *     {
 *       // POST requests should always have a JSON body.
 *       throw new RuntimeException();
 *     }
 *     if (data instanceof UserResource user)
 *     {
 *       try
 *       {
 *         UserResource createdUser = validateAndCreateUser(user);
 *         String location = createdUser.getMeta().getLocation().toString();
 *         return new BulkOperationResult(op, HTTP_STATUS_CREATED, location);
 *       }
 *       catch (ScimException e)
 *       {
 *         // Locations are always null for POST failures.
 *         String location = null;
 *         return new BulkOperationResult(e, op, location);
 *       }
 *     }
 *     else if (data instanceof GroupResource group)
 *     {
 *       // Group creation logic.
 *     }
 *     else if (data instanceof PatchRequest)
 *     {
 *       // PATCH requests should only be used for bulk patch operations, not
 *       // for a POST.
 *       throw new RuntimeException();
 *     }
 *     else if (data instanceof ErrorResponse)
 *     {
 *       // Error responses may be present within bulk responses, but not client
 *       // bulk requests.
 *       throw new RuntimeException();
 *     }
 *     else if (data instanceof GenericScimResource)
 *     {
 *       // The data did not match an expected object model, so it is invalid.
 *       throw new RuntimeException();
 *     }
 *     else
 *     {
 *       // Other object types are not expected.
 *       throw new RuntimeException();
 *     }
 *   }
 * </code></pre>
 *
 * The {@link BulkResourceMapper} is responsible for managing the conversions to
 * {@link ScimResource} objects. See the mapper and
 * {@link BulkOperation#getDataAsScimResource()} for more details.
 * <br><br>
 *
 * <h2>Bulk IDs</h2>
 * When regular, non-bulk SCIM requests are processed, there are sometimes some
 * dependencies between requests. For example, a client may create a user, then
 * create a group that contains that user. It is still possible to batch this
 * workflow together into a single bulk request by leveraging something called
 * bulk IDs.
 * <br><br>
 *
 * Bulk IDs grant a temporary identifier that may be added to POST operations
 * within a bulk request, since those resources don't yet exist. Creating a user
 * and placing it in a newly-created group can be accomplished with:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
 *   "Operations": [ {
 *       "method": "POST",
 *       "path": "/Users",
 *       "bulkId": "hallmarkCards",
 *       "data": {
 *         "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *         "userName": "Hallmarks"
 *       }
 *     }, {
 *       "method": "POST",
 *       "path": "/Groups",
 *       "data": {
 *         "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:Group" ],
 *         "displayName": "Bodega",
 *         "members": [ {
 *           "type": "User",
 *           "value": "bulkId:hallmarkCards"
 *         } ]
 *       }
 *   } ]
 * }
 * </pre>
 *
 * The first operation creates a user and denotes it with a bulk ID of
 * "hallmarkCards". The second operation creates a group and adds the user as a
 * member, which is done by referencing its bulk ID. This enables clients to
 * batch more requests together, though it creates coupling between operations
 * that SCIM services need to consider for bulk workflows.
 * <br><br>
 *
 * For SCIM service applications, the {@link #replaceBulkIdValues} method may be
 * used to help with this processing. After an operation (e.g., creating a user)
 * has completed successfully, all other operations that reference the bulk ID
 * can be updated to start using the real {@code id} of the resource once it is
 * available. Note that this directly modifies the operations contained within
 * the bulk request to avoid repeatedly duplicating bulk objects in memory, and
 * this method is not thread safe.
 * <pre><code>
 *   BulkOperation op;
 *   BulkOperationResult result;
 *   ScimResource createdResource;
 *
 *   String bulkId = op.getBulkId();
 *   if (result.isSuccess() &amp;&amp; bulkId != null)
 *   {
 *     // This may be called while iterating over the 'bulkRequest' object.
 *     bulkRequest.replaceBulkIdValues(bulkId, createdResource.getId());
 *   }
 * </code></pre>
 *
 * One complication with bulk IDs is that it can be possible to create circular
 * dependencies. If a circular reference is contained within a request via
 * {@code bulkId} fields of the {@link BulkOperation} objects, the service
 * provider MUST try to resolve it, but MAY stop at a failed attempt with a
 * {@link ResourceConflictException}. A simple example of
 * a circular bulk request can be seen in
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.7.1">
 * RFC 7644 Section 3.7.1</a>. Note that if a SCIM service allows creating loops
 * in the graph representation of their groups, the linked example can be
 * considered a valid circular dependency.
 *
 * <h2>More About Bulk Requests</h2>
 *
 * Some additional background about bulk requests and how SCIM services may
 * process them are shared below.
 * <ul>
 *   <li> Bulk responses can return fewer results than what the client bulk
 *        request contained if the {@code failOnErrors} threshold was reached.
 *   <li> If a JSON fails to deserialize into a bulk request or bulk response
 *        object, it will throw a runtime {@link BulkRequestException} or
 *        {@link BulkResponseException} to indicate a malformed bulk object.
 *        Spring applications may leverage these in a controller advice.
 *   <li> Bulk operations within a request are not guaranteed to be processed
 *        in order. Specifically, RFC 7644 states that "[a] SCIM service
 *        provider MAY elect to optimize the sequence of operations received
 *        (e.g., to improve processing performance). When doing so, the
 *        service provider MUST ensure that the client’s intent is preserved
 *        and the same stateful result is achieved..."
 *   <li> By default, RFC 7644 states that SCIM services "MUST continue
 *        performing as many changes as possible and disregard partial
 *        failures". In other words, bulk requests must attempt to process as
 *        many of the bulk operations as possible, even if failures occur for
 *        some of the bulk operations. As stated above, clients may override
 *        this behavior by setting the {@code failOnErrors} field.
 *   <li> Bulk requests are not atomic. Successful updates will always be
 *        applied until the optional {@code failOnErrors} threshold is reached.
 *   <li> By their nature, bulk requests can quickly become expensive traffic
 *        for a SCIM service to process. As a client, it is important to be
 *        careful about restrictions such as payload sizes and rate limits when
 *        sending bulk requests to a SCIM service. Be prepared to handle
 *        {@link ContentTooLargeException} and {@link RateLimitException} errors.
 *   <li> As a SCIM service, it is generally a good idea to implement request
 *        limiting on endpoints that facilitate bulk request processing, as
 *        these endpoints can be a potential attack vector. This is crucial for
 *        cloud-based services, but is not as relevant for databases that expose
 *        a SCIM interface.
 *   <li> Consider memory usage when implementing bulk request support with this
 *        library. Large bulk payloads can result in high memory usage when JSON
 *        data is parsed into Jackson's tree model, especially in containerized
 *        environments that have small heap sizes. Since requests are likely
 *        processed with many threads, this could result in memory pressure
 *        if limits are not placed on JSON payload sizes or the bulk endpoints.
 *        Adding request limiting can be an effective defense against issues
 *        such as OutOfMemory errors and continuous garbage collection.
 * </ul>
 *
 * For more details on the types of updates that can be issued within a bulk
 * request, see {@link BulkOperation}.
 *
 * @since 5.1.0
 */
@Schema(id = "urn:ietf:params:scim:api:messages:2.0:BulkRequest",
        name = "Bulk Request", description = "SCIM 2.0 Bulk Request")
public class BulkRequest extends BaseScimResource
    implements Iterable<BulkOperation>
{
  /**
   * An integer specifying the number of errors that the SCIM service provider
   * should accept before the operation is terminated with an error response.
   * See {@link #setFailOnErrors} for more information.
   * <br><br>
   *
   * By default, this value is {@code null}, indicating that the SCIM service
   * should attempt to process as many of the bulk operations as it can.
   */
  @Nullable
  @Attribute(description = """
      An integer specifying the number of errors that the service \
      provider will accept before the operation is terminated and an \
      error response is returned.""")
  private Integer failOnErrors;

  @NotNull
  @Attribute(description = """
      Defines operations within a bulk job.  Each operation corresponds to a \
      single HTTP request against a resource endpoint.""",
      isRequired = true,
      multiValueClass = BulkOperation.class)
  private final List<BulkOperation> operations;

  /**
   * Creates a bulk request.
   * <br><br>
   *
   * Note that while the arguments to this method may each be {@code null}, they
   * cannot both be {@code null}. This behavior ensures that invalid JSON data
   * cannot be set to a BulkRequest during deserialization. If you wish to
   * create an empty bulk request, use {@link BulkRequest#BulkRequest(List)}.
   *
   * @param failOnErrors  The failure threshold as defined by
   *                      {@link #setFailOnErrors(Integer)}.
   * @param ops           The list of {@link BulkOperation} objects. Any
   *                      {@code null} elements will be ignored.
   *
   * @throws BulkRequestException  If the bulk request contains no data. This is
   *                               most likely due to an invalid JSON being
   *                               converted to a bulk request.
   */
  @JsonCreator
  public BulkRequest(
      @Nullable @JsonProperty(value="failOnErrors") final Integer failOnErrors,
      @Nullable @JsonProperty(value="Operations") final List<BulkOperation> ops)
          throws BulkRequestException
  {
    if (failOnErrors == null && ops == null)
    {
      // This state most likely comes from a source JSON for a completely
      // different resource type. Thus, the failure here must not be silent.
      throw new BulkRequestException(
          "Could not create the bulk request since it would have been empty.");
    }

    this.failOnErrors = failOnErrors;

    // Add the contents to an unmodifiable list.
    List<BulkOperation> opsList = (ops == null) ? List.of() : ops;
    operations = opsList.stream().filter(Objects::nonNull).toList();
  }

  /**
   * Creates a bulk request.
   *
   * @param operations    The list of {@link BulkOperation} objects. Any
   *                      {@code null} elements will be ignored.
   */
  public BulkRequest(@Nullable final List<BulkOperation> operations)
  {
    this(null, Objects.requireNonNullElse(operations, List.of()));
  }

  /**
   * Alternate constructor that allows specifying bulk operations individually.
   * The optional {@code failOnErrors} field will not be set, but a value can be
   * specified with the {@link #setFailOnErrors} method.
   *
   * @param operation   The first bulk operation. This must not be {@code null}.
   * @param operations  An optional field for additional bulk operations. Any
   *                    {@code null} values will be ignored.
   */
  public BulkRequest(@NotNull final BulkOperation operation,
                     @Nullable final BulkOperation... operations)
  {
    this(toList(operation, operations));
  }

  /**
   * Sets the failure threshold for the bulk request. If the value is
   * {@code null}, the SCIM service provider will attempt to process all
   * operations. Any value less than {@code 1} is not permitted.
   * <br><br>
   *
   * In general, this value refers to the number of errors that will cause a
   * failed response. For example, if the value is {@code 1}, then any bulk
   * operation that results in a failure will cause the SCIM service to stop and
   * avoid attempting any of the remaining bulk operations present within the
   * client bulk request. However, it is best to consult with the documentation
   * for your SCIM service to be certain of the expected behavior, as some SCIM
   * services may treat this value differently.
   *
   * @param value  The failure count to specify. The value must not be less than
   *               1, and {@code null} values will clear the field from the bulk
   *               request, which indicates that the SCIM service should
   *               attempt all provided operations. If the failure count exceeds
   *               this value, the request should return a
   *               {@link ResourceConflictException} (HTTP 409) status code.
   *
   * @return  This object.
   * @throws BulkRequestException  If the provided value is less than 1.
   */
  @NotNull
  public BulkRequest setFailOnErrors(@Nullable final Integer value)
      throws BulkRequestException
  {
    if (value != null && value < 1)
    {
      throw new BulkRequestException(
          "The failOnErrors value (" + value + ") may not be less than 1.");
    }
    this.failOnErrors = value;
    return this;
  }

  /**
   * Retrieves the value indicating the threshold for failing a bulk request.
   * See {@link #setFailOnErrors} for more information.
   *
   * @return The value of {@code failOnErrors}.
   */
  @Nullable
  public Integer getFailOnErrors()
  {
    return failOnErrors;
  }

  /**
   * Retrieves the value indicating the threshold for failing a bulk request.
   * This value will always be non-null so that SCIM services can easily
   * evaluate thresholds without needing to handle {@code null}. See
   * {@link #setFailOnErrors} for more information.
   *
   * @return The value of {@code failOnErrors}.
   */
  @JsonIgnore
  public int getFailOnErrorsNormalized()
  {
    return (failOnErrors == null) ? Integer.MAX_VALUE : failOnErrors;
  }

  /**
   * Retrieves an immutable list of the operations in this bulk request.
   *
   * @return The Operations list.
   */
  @NotNull
  @JsonProperty("Operations")
  public List<BulkOperation> getOperations()
  {
    return operations;
  }

  /**
   * Replaces any temporary bulk ID values stored within the bulk request with
   * the appropriate new value. This specifically targets the {@code data} field
   * of each bulk operation, and does not affect the order of the operations in
   * the list. See the class-level Javadoc for more information.
   * <br><br>
   *
   * This method permanently alters the bulk request directly, and it is not
   * thread safe.
   *
   * @param bulkId      The temporary bulk ID value.
   * @param realValue   The real value after the resource has been created.
   */
  public void replaceBulkIdValues(@NotNull final String bulkId,
                                  @NotNull final String realValue)
  {
    for (BulkOperation op : operations)
    {
      op.replaceBulkIdValue(bulkId, realValue);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public Iterator<BulkOperation> iterator()
  {
    return getOperations().iterator();
  }

  /**
   * Indicates whether the provided object is equal to this bulk request.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this bulk
   *            request, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof BulkRequest that))
    {
      return false;
    }

    if (!Objects.equals(failOnErrors, that.failOnErrors))
    {
      return false;
    }
    return operations.equals(that.getOperations());
  }

  /**
   * Retrieves a hash code for this bulk request.
   *
   * @return  A hash code for this bulk request.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(failOnErrors, operations);
  }
}
