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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.PayloadTooLargeException;
import com.unboundid.scim2.common.exceptions.ResourceConflictException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * the service will respond with a {@link BulkResponse}.
 * <br><br>
 *
 * As an example, consider the following bulk request that creates two users:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:BulkRequest" ],
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
 *         "userName": "Bob"
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
 *        // TODO: Clarify the threshold comment, and to check the API docs
 *   <li> {@code Operations}: The list of {@link BulkOperation} requests that
 *        represent all of the write requests contained in this API call.
 * </ul>
 * <br><br>
 *
 * To construct the above bulk request, use the following Java code:
 * <pre><code>
 *  BulkRequest request = new BulkRequest(
 *        BulkOperation.post("/Users", new UserResource().setUserName("Alice")),
 *        BulkOperation.post("/Users", new UserResource().setUserName("Bob"))
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
 *   // TODO: Write a test case to make sure this is true
 *   // forEach() can also be used, but this is not recommended because it can
 *   // cause difficulties with debugging or following stack traces.
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
 * <br><br>
 * TODO: Add tests serializing bulk requests and bulk operations.
 * TODO: Test iterating over the object
 *
 * @see BulkOperation
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:BulkRequest",
        name="Bulk Request", description = "SCIM 2.0 Bulk Operation Request")
@JsonPropertyOrder({"schemas", "failOnErrors", "Operations"})
public class BulkRequest extends BaseScimResource
    implements Iterable<BulkOperation>
{
  /**
   * An integer specifying the number of errors that the SCIM service provider
   * should accept before the operation is terminated with an error response.
   * This value is optional in a request, and should never be returned in a
   * response.
   */
  @Nullable
  private Integer failOnErrors;

  @NotNull
  private List<BulkOperation> operations = new ArrayList<>();

  /**
   * Creates a bulk request.
   *
   * @param operations    The list of {@link BulkOperation} objects. Any
   *                      {@code null} elements will be ignored.
   * @param failOnErrors  The number of failed operations that the SCIM service
   *                      is allowed to ignore. If the number of failures
   *                      exceeds this value, the request will fail with a
   *                      {@link ResourceConflictException} (HTTP 409).
   */
  @JsonCreator
  public BulkRequest(@Nullable final List<BulkOperation> operations)
  {
    if (operations != null)
    {
      // TODO: Not a fan
      append(operations.toArray(BulkOperation[]::new));
    }
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
   * Sets the number of failures that should be permitted for the bulk request.
   *
   * @param failureCount  The failure count to specify. Values less than 0 will
   *                      be interpreted as 0, and {@code null} values will
   *                      clear the field from the bulk request, which is
   *                      different from an explicit 0.
   *
   * @return  This object.
   */
  public BulkRequest setFailOnErrors(@Nullable final Integer failureCount)
  {
    failOnErrors = (failureCount == null) ? null : Math.max(failureCount, 0);
    return this;
  }

  @NotNull
  public BulkRequest append(@Nullable final BulkOperation... ops)
  {
    List<BulkOperation> list = (ops == null) ? List.of() : Arrays.asList(ops);
    for (BulkOperation operation : list)
    {
      if (operation != null)
      {
        this.operations.add(operation);
      }
    }

    return this;
  }

  /**
   * Retrieves the value of the permitted failure count. This represents the
   * number of operations that may fail before the SCIM service should return
   * a bulk error response.
   *
   * @return The failure count.
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

  public void apply()
  {

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

    if (!(Objects.equals(failOnErrors, that.failOnErrors)))
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
