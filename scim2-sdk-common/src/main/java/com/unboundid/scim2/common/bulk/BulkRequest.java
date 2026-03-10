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
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.BulkRequestException;
import com.unboundid.scim2.common.exceptions.ContentTooLargeException;
import com.unboundid.scim2.common.exceptions.RateLimitException;
import com.unboundid.scim2.common.exceptions.ResourceConflictException;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;

import java.util.ArrayList;
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
 * Establishing an HTTP connection always incurs some level of overhead in
 * request processing (e.g., TLS negotiation). This is typically fine for most
 * use cases, but it can become expensive in cases where a client anticipates
 * sending large amounts of traffic. A SCIM bulk request is an optimization that
 * combines multiple write requests into a single API call, minimizing the
 * number of times that overhead costs are paid by both the client and server.
 * The following documentation discusses some key points on using this class
 * (and others) to support bulk workflows. For further background, see
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.7">
 * RFC 7644</a>.
 * <br><br>
 *
 * Bulk requests contain the following fields:
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
 * When a client sends a bulk request to a SCIM service, the service will return
 * a {@link BulkResponse} (if the service supports bulk requests). Bulk response
 * objects provide a summary of the request processing performed by the service,
 * such as the response code (e.g., {@code 200 OK}) of an individual operation.
 * Note that HTTP servers/services often have limits on the amount of data that
 * can be contained within a single HTTP request, so clients should be careful
 * to avoid exceeding bulk limits. Bulk limits are defined on a SCIM service's
 * {@link ServiceProviderConfigResource /ServiceProviderConfig} endpoint.
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
 * differently. For the first user, an error was returned for a uniqueness
 * violation with the appropriate error details. The second user was processed
 * successfully and returned a response that would be seen from a typical SCIM
 * POST request to the {@code /Users} endpoint. Note that the SCIM standard
 * states that the {@code response} field is always defined with information in
 * the event of an error, but successful responses may omit a value for
 * {@code response}.
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
 * application code should manage JSON data embedded in bulk requests and
 * responses. As seen above, SCIM resource objects are embedded within bulk
 * requests and responses within the {@code data} and {@code response} fields.
 * When interacting with these fields, it is ideal if they are available as
 * objects rather than raw JSON, but the conversion of this data can become
 * complicated. Fortunately, the UnboundID SCIM SDK provides two methods to
 * assist with data handling, which is useful regardless of whether you are
 * handling a bulk request or bulk response:
 * <ul>
 *   <li> {@link BulkOperation#getDataAsScimResource()}
 *   <li> {@link BulkOperationResult#getResponseAsScimResource()}
 * </ul>
 * <br><br>
 *
 * With these methods, along with the {@code instanceof} keyword, application
 * code can directly handle object representations of SCIM resources. As an
 * example, a service handling client bulk requests may use the following
 * structure for processing requests. Note that this example does not handle
 * bulk ID values defined in {@link BulkOperation}.
 * <pre><code>
 *   public BulkResponse processBulkRequest(BulkRequest bulkRequest)
 *   {
 *     int errorCount = 0;
 *     List&lt;BulkOperationResult&gt; results = new ArrayList&lt;&gt;();
 *     for (BulkOperation op : bulkRequest)
 *     {
 *       BulkOperationResult result = switch (op.getMethod())
 *       {
 *         case POST -> processPost(op);
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
 *         // The error threshold was reached. Return immediately.
 *         return new BulkResponse(results);
 *       }
 *     }
 *
 *     // Processing completed. Return the result.
 *     return new BulkResponse(results);
 *   }
 *
 *   private BulkOperationResult processPost(BulkOperation op)
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
 *       // ...
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
 * The {@link BulkResourceMapper} is responsible for managing the conversion to
 * a {@link com.unboundid.scim2.common.ScimResource ScimResource}. See the
 * mapper and {@link BulkOperation#getDataAsScimResource()} for more details.
 * <br><br>
 *
 * <h2>More About Bulk Requests</h2>
 *
 * Some additional background about bulk requests and how SCIM services may
 * process them are shared below:
 * <ul>
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
 *   <li> Bulk operations are not atomic. Successful updates will always be
 *        applied until the optional {@code failOnErrors} threshold is reached.
 *   <li> If a circular reference is contained within a request via
 *        {@code bulkId} fields of the {@link BulkOperation} objects, the
 *        service provider MUST try to resolve it, but MAY stop at a failed
 *        attempt with a {@link ResourceConflictException}.
 *   <li> By their nature, bulk requests can quickly become expensive traffic
 *        for a SCIM service to process. As a client, it is important to be
 *        careful about restrictions such as payload sizes and rate limits when
 *        sending bulk requests to a SCIM service. Be prepared to handle
 *        {@link ContentTooLargeException} and {@link RateLimitException} errors.
 *   <li> As a SCIM service, it is generally a good idea to implement request
 *        limiting on endpoints that facilitate bulk request processing, as
 *        these endpoints can be susceptible to Denial of Service attacks. This
 *        is especially pertinent for cloud-based services, but is not as
 *        relevant for database servers.
 *   <li> Consider memory usage when implementing bulk request support with this
 *        library. Large bulk payloads can result in high memory usage when data
 *        is expanded into Jackson's tree model, especially in containerized
 *        environments that have small heap sizes. Since requests are likely
 *        processed with many threads, this could result in memory pressure
 *        if limits are not placed on JSON payload sizes or the bulk endpoints.
 *        Adding request limiting can be an effective defense against issues
 *        such as Out Of Memory errors and continuous garbage collection.
 * </ul>
 *
 * For more details on what kinds of requests can be issued within a bulk
 * request, see {@link BulkOperation}.
 */
@Schema(id = "urn:ietf:params:scim:api:messages:2.0:BulkRequest",
        name = "Bulk Request", description = "SCIM 2.0 Bulk Request")
public class BulkRequest extends BaseScimResource
    implements Iterable<BulkOperation>
{
  /**
   * An integer specifying the number of errors that the SCIM service provider
   * should accept before the operation is terminated with an error response.
   * See {@link #setFailOnErrors} for more information, as well as some
   * background on the ways this attribute is treated differently by different
   * SCIM services.
   * <br><br>
   *
   * By default, this value is {@code null}, indicating that the SCIM service
   * should attempt to process as many of the bulk requests as it can.
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
  private final List<BulkOperation> operations = new ArrayList<>();

  /**
   * Creates a bulk request.
   * <br><br>
   *
   * Note that while the arguments to this method may be set to {@code null},
   * they cannot both be {@code null}. This behavior ensures that invalid JSON
   * data cannot be set to a BulkRequest. If you wish to create an empty bulk
   * request, use an empty list with {@link java.util.Collections#emptyList()}.
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
    operations.addAll((ops == null) ? List.of() : ops);
  }

  /**
   * Creates a bulk request.
   *
   * @param operations    The list of {@link BulkOperation} objects. Any
   *                      {@code null} elements will be ignored.
   */
  public BulkRequest(@Nullable final List<BulkOperation> operations)
  {
    List<BulkOperation> ops = (operations == null) ? List.of() : operations;
    ops.stream().filter(Objects::nonNull).forEach(this.operations::add);
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
   * avoid attempting any of the remaining bulk operations. However, it is best
   * to consult with the documentation for your SCIM service to be certain of
   * the expected behavior, as some SCIM services may treat this value
   * differently.
   *
   * @param value  The failure count to specify. The value must not be less than
   *               1, and {@code null} values will clear the field from the bulk
   *               request, which indicates that the SCIM service should
   *               attempt all provided operations. If the failure count exceeds
   *               this value, the request should fail with a
   *               {@link ResourceConflictException} (HTTP 409).
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
          "The failOnErrors value was " + value + ", which is less than 1.");
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
   * This is normalized so that SCIM services can easily evaluate thresholds
   * without needing to handle {@code null}. See {@link #setFailOnErrors} for
   * more information.
   *
   * @return The value of {@code failOnErrors}.
   */
  @JsonIgnore
  public int getFailOnErrorsNormalized()
  {
    return (failOnErrors == null) ? Integer.MAX_VALUE : failOnErrors;
  }

  /**
   * Retrieves all the individual operations in this patch request.
   *
   * @return An immutable list of the bulk operations within this request.
   */
  @NotNull
  @JsonProperty("Operations")
  public List<BulkOperation> getOperations()
  {
    return List.copyOf(operations);
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
