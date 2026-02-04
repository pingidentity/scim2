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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * This class represents a SCIM 2 bulk response. This is an API response
 * returned by a SCIM service after it has processed a bulk request. For more
 * background on bulk responses, see the documentation for {@link BulkRequest}.
 * <br><br>
 *
 * A bulk response takes the following form when represented as a JSON:
 * <pre>
 *   {
 *     "schemas": ["urn:ietf:params:scim:api:messages:2.0:BulkResponse"],
 *     "Operations": [
 *       {
 *         "location": "https://example.com/v2/Users/92b725cd",
 *         "method": "POST",
 *         "bulkId": "qwerty",
 *         "version": "W/\"4weymrEsh5O6cAEK\"",
 *         "status": "201"
 *       }
 *     ]
 *   }
 * </pre>
 *
 * Each individual object within the {@code Operations} array is represented by
 * the {@link BulkOperationResult} class. To create a bulk response, it will
 * require including the BulkOperationResult objects that should be included in
 * the response to the SCIM client.
 * <br><br>
 *
 * The BulkResponse above can be created with the following Java code. For more
 * ways to create the bulk operation results, see {@link BulkOperationResult}.
 * <pre><code>
 *   // First create the BulkOperationResult object.
 *   BulkOperationResult result = new BulkOperationResult(BulkOpType.POST,
 *     BulkOperationResult.HTTP_STATUS_CREATED,
 *     "https://example.com/v2/Users/92b725cd",
 *     null,
 *     "qwerty",
 *     "W/\"4weymrEsh5O6cAEK\"");
 *
 *   return new BulkResponse(result);
 * </code></pre>
 *
 * A bulk response will contain multiple BulkOperationResult objects when
 * processing a client request that included more than one request in its
 * {@code Operations} array. The following Java code illustrates an example that
 * relies on a helper method to process a bulk operation and generate the
 * result summary as a BulkOperationResult.
 * <pre><code>
 *   BulkRequest bulkRequest = getBulkRequestFromClient();
 *
 *   BulkResponse bulkResponse = new BulkResponse();
 *   for (BulkOperation op : bulkRequest)
 *   {
 *     BulkOperationResult result = processOperationHelperMethod(op);
 *     bulkResponse.appendOperation(result);
 *   }
 *
 *   return bulkResponse;
 * </code></pre>
 *
 * Bulk response objects are also iterable, so it is possible for SCIM clients
 * to iterate over the response object directly. The following example
 * illustrates how a client can handle a bulk response returned from a service:
 * <pre><code>
 *   public static void main()
 *   {
 *     BulkResponse response = sendBulkRequestAndFetchResponse();
 *     for (BulkOperationResult result : response)
 *     {
 *       handle(result);
 *     }
 *   }
 *
 *   private void handle(BulkOperationResult result)
 *   {
 *     // If desired, perform some action to process the result data. Use
 *     // getResponseAsScimResource() to fetch the data as a ScimResource.
 *     ScimResource resource = result.getResponseAsScimResource();
 *     if (resource == null)
 *     {
 *       // There was no "response" field returned by the SCIM service.
 *       return;
 *     }
 *     else if (resource instanceof ErrorResponse error)
 *     {
 *       // SCIM services are required to return an error in the JSON body.
 *       incrementErrorCountMetric(error);
 *     }
 *     else if (resource instanceof UserResource user)
 *     {
 *       runValidation(user);
 *     }
 *     else if (resource instanceof GroupResource group)
 *     {
 *       runGroupValidation(group);
 *     }
 *     else if (resource instanceof GenericScimResource gsr)
 *     {
 *       // The object could not be converted into one of the model classes
 *       // defined in the BulkResourceMapper.
 *     }
 *   }
 * </code></pre>
 *
 * In the above code, to define other resource types that the SCIM SDK should
 * consider, define the class on the {@link BulkResourceMapper} class.
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Schema(id="urn:ietf:params:scim:api:messages:2.0:BulkResponse",
    name="Bulk Response", description = "SCIM 2.0 Bulk Response")
@JsonPropertyOrder({"schemas", "Operations"})
public class BulkResponse extends BaseScimResource
    implements Iterable<BulkOperationResult>
{
  @NotNull
  @Attribute(description = """
      Defines operations within a bulk job.  Each operation corresponds to a \
      single HTTP request against a resource endpoint.""",
      isRequired = true,
      multiValueClass = BulkOperationResult.class)
  @JsonProperty(value = "Operations", required = true)
  private final List<BulkOperationResult> operations = new ArrayList<>();


  /**
   * Creates a bulk response that summarizes the results of a client bulk
   * request. This will have an empty operations list that can be updated with
   * the {@link #appendOperation appendOperation()} method.
   */
  public BulkResponse()
  {
    // No implementation required.
  }

  /**
   * Creates a bulk response that summarizes the results of a client bulk
   * request.
   *
   * @param results  The list of bulk operation results to display in the bulk
   *                 response.
   */
  @JsonCreator
  public BulkResponse(@Nullable final List<BulkOperationResult> results)
  {
    List<BulkOperationResult> ops = (results == null) ? List.of() : results;
    operations.addAll(ops);
  }

  /**
   * Creates a bulk response that summarizes the results of a client bulk
   * request.
   *
   * @param result   The initial bulk operation result to include in the bulk
   *                 response.
   * @param results  An optional field for additional bulk operation results.
   *                 Any {@code null} values will be ignored.
   */
  public BulkResponse(@NotNull final BulkOperationResult result,
                      @Nullable final BulkOperationResult... results)
  {
    this(toList(result, results));
  }

  /**
   * Returns an immutable list of responses.
   *
   * @return  The Operations list.
   */
  @NotNull
  public List<BulkOperationResult> getOperations()
  {
    return List.copyOf(operations);
  }

  /**
   * Appends a BulkOperationResult object to the BulkResponse.
   *
   * @param result  The BulkOperationResult.
   */
  public void appendOperation(@Nullable final BulkOperationResult result)
  {
    if (result != null)
    {
      operations.add(result);
    }
  }

  @Override
  @NotNull
  public Iterator<BulkOperationResult> iterator()
  {
    return getOperations().iterator();
  }

  /**
   * Indicates whether the provided object is equal to this bulk response.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this bulk
   *            response, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof BulkResponse that))
    {
      return false;
    }

    return operations.equals(that.operations);
  }

  /**
   * Retrieves a hash code for this bulk response.
   *
   * @return  A hash code for this bulk response.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(operations);
  }
}
