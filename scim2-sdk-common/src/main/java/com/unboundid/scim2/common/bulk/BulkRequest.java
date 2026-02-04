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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.PayloadTooLargeException;
import com.unboundid.scim2.common.exceptions.ResourceConflictException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;


/**
 * This class represents a SCIM 2 bulk request as described by
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.7">
 * RFC 7644 Section 3.7</a>.
 * <br><br>
 *
 * Bulk requests are a type of operation that allow a client to perform multiple
 * write requests within a single API call. Bulk requests are comprised of a
 * list of {@link BulkOperation} objects, where each bulk operation represents a
 * single write request. When a client sends a bulk request to a SCIM service,
 * the service will respond with a {@link BulkResponse}. More details about the
 * SCIM SDK's object types are described below.
 * <br><br>
 *
 * As an example, consider a bulk request that creates two users:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
 *   "failOnErrors": 1,
 *   "Operations": [
 *     {
 *       "method": "POST",
 *       "path": "/Users",
 *       "data": {
 *         "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *         "userName": "Alice"
 *       }
 *     },
 *     {
 *       "method": "POST",
 *       "path": "/Users",
 *       "data": {
 *         "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *         "userName": "Whiplash!"
 *       }
 *     }
 *   ]
 * }
 * </pre>
 *
 * As seen above, bulk requests contain the following fields:
 * <ul>
 *   <li> {@code failOnErrors}: An optional integer that specifies a threshold
 *        for the amount of acceptable errors. If this field is not set, then
 *        the SCIM service will attempt to fulfill as many operations as it can.
 *        For more detail on how this is defined, see {@link #setFailOnErrors}.
 *   <li> {@code Operations}: The list of {@link BulkOperation} requests that
 *        represent all of the write requests contained in this API call.
 * </ul>
 * <br><br>
 *
 * <h2>Interfacing with bulk data in the UnboundID SCIM SDK</h2>
 *
 * There are several different types of
 *
 * <br><br>
 *
 * <h2>More about BulkRequests</h2>
 *
 * To construct the above bulk request, use the following Java code:
 * <pre><code>
 *  BulkRequest request = new BulkRequest(
 *    BulkOperation.post("/Users", new UserResource().setUserName("Alice")),
 *    BulkOperation.post("/Users", new UserResource().setUserName("Whiplash!"))
 *  );
 *  request.setFailOnErrors(1);
 *
 *  // Use this method when the arguments are already contained in a list.
 *  BulkRequest request = new BulkRequest(getBulkOperationList(), 1);
 * </code></pre>
 * <br><br>
 *
 * Note that it is possible to iterate over the bulk operations by iterating
 * over the bulk request object itself.
 * <pre><code>
 *   for (BulkOperation operation : bulkRequest)
 *   {
 *     // Perform some action with the operation.
 *   }
 *
 *   // forEach() can technically be used, but this is discouraged because it
 *   // can cause difficulties with debugging or following stack traces.
 *   bulkRequest.forEach(op -> System.out.println(op));
 * </code></pre>
 *
 * Some additional background about bulk requests and how SCIM services may
 * process them are shared below:
 * <ul>
 *   <li> The order of the bulk operations are not guaranteed to be processed
 *        in order. Specifically, RFC 7644 states that "[a] SCIM service
 *        provider MAY elect to optimize the sequence of operations received
 *        (e.g., to improve processing performance)." When doing so, the
 *        service... MUST ensure that the client’s intent is preserved
 *        and the same stateful result is achieved..."
 *   <li> By default, RFC 7644 states that SCIM services "MUST continue
 *        performing as many changes as possible and disregard partial
 *        failures". In other words, bulk requests must attempt to process as
 *        many of the bulk operations as possible, even if failures occur for
 *        some of the bulk operations. As sated above, clients may override this
 *        behavior by setting the {@code failOnErrors} field.
 *   <li> Bulk operations are not atomic. Successful updates will always be
 *        applied until the optional {@code failOnErrors} threshold is reached.
 *   <li> It is important to be careful about payload size restrictions when
 *        sending bulk requests to a SCIM service. Be prepared to handle
 *        {@link PayloadTooLargeException} errors.
 * </ul>
 *
 * @see BulkOperation
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:BulkRequest",
        name="Bulk Request", description = "SCIM 2.0 Bulk Operation Request")
@JsonPropertyOrder({"schemas", "failOnErrors", "Operations"})
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
   * @throws IllegalArgumentException   If the bulk request contains no data.
   *                                    This is most likely due to an invalid
   *                                    JSON being converted to a bulk request.
   */
  @JsonCreator
  public BulkRequest(
      @Nullable @JsonProperty(value="failOnErrors") final Integer failOnErrors,
      @Nullable @JsonProperty(value="Operations") final List<BulkOperation> ops)
          throws IllegalArgumentException
  {
    if (failOnErrors == null && ops == null)
    {
      // This state most likely comes from a source JSON for a completely
      // different resource type. Thus, the failure here must not be silent.
      throw new IllegalArgumentException(
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
    this.operations.addAll(ops);
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
   * operations. The UnboundID SCIM SDK treats this as the number of errors
   * that will cause a completely failed response, though there is variance for
   * some SCIM services.
   * <br><br>
   *
   * Due to ambiguity in RFC 7644, different service providers treat this value
   * differently. The description for this attribute states:
   * <pre>
   *   failOnErrors
   *       An integer specifying the number of errors that the service
   *       provider will accept before the operation is terminated and an
   *       error response is returned.  OPTIONAL in a request.  Not valid in
   *       a response.
   * </pre>
   *
   * This implies that the value is the maximum number of errors that will be
   * accepted before a failure. However, this does not match explanations in
   * follow-up examples, such as the following:
   * <pre>
   *   The "failOnErrors" attribute is set to '1', indicating that the
   *   service provider will stop processing and return results after one
   *   error.
   * </pre>
   *
   * As a result of this discrepancy, some SCIM services treat this value
   * differently:
   * <ol>
   *   <li> Some treat this value as the maximum number of permitted errors.
   *   <li> Others treat this value as the number of errors that will cause a
   *        failed response.
   * </ol>
   * <br><br>
   *
   * Since the attribute is named in a way that suggests the request should
   * "fail on this many errors", and also has multiple examples showcasing
   * intent, the UnboundID SCIM SDK uses the second behavior described in the
   * list above. However, it is still possible to use this SDK with SCIM
   * services that use the first definition. Ultimately, it is important to
   * consult the documentation for your SCIM service to be certain about how it
   * will treat this value.
   *
   * @param value  The failure count to specify. Values less than 0 will be
   *               interpreted as 0, and {@code null} values will clear the
   *               field from the bulk request, which requests that the SCIM
   *               service attempts as many requests as possible. If the
   *               failure count exceeds this value, the request will fail with
   *               a {@link ResourceConflictException} (HTTP 409).
   *
   * @return  This object.
   */
  @NotNull
  public BulkRequest setFailOnErrors(@Nullable final Integer value)
  {
    this.failOnErrors = (value == null) ? null : Math.max(value, 0);
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
   * Returns an iterator over the bulk operations contained in this request.
   */
  @Override
  @NotNull
  public Iterator<BulkOperation> iterator()
  {
    return getOperations().iterator();
  }

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

  @Override
  public int hashCode()
  {
    return Objects.hash(failOnErrors, operations);
  }
}
