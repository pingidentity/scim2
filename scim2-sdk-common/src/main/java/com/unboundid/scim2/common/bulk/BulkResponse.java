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
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;


/**
 * This class represents a SCIM 2 bulk response. This is an API response
 * returned by a SCIM service after it has processed a bulk request, and it
 * represents a summary of all bulk operations that were processed by the SCIM
 * service. For an introduction to SCIM bulk processing, see the documentation
 * for {@link BulkRequest}.
 * <br><br>
 *
 * The only field contained within a bulk response is {@code Operations}. Each
 * individual object within the {@code Operations} array of a bulk response is
 * represented by the {@link BulkOperationResult} class.
 * <br><br>
 *
 * An example bulk response is shown below:
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkResponse" ],
 *     "Operations": [
 *       {
 *         "location": "https://example.com/v2/Users/fa1afe1",
 *         "method": "POST",
 *         "bulkId": "qwerty",
 *         "status": "201",
 *         "response": {
 *           "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *           "userName": "Alice"
 *         }
 *       }
 *     ]
 *   }
 * </pre>
 *
 * The bulk response indicates that the server-side processing successfully
 * created one user resource. It also details the URI for the new resource,
 * along with the original HTTP method, the bulk ID set in the client bulk
 * request, the HTTP response status code, and the optional JSON response data.
 * <br><br>
 *
 * This bulk response can be created with the following Java code. See
 * {@link BulkOperationResult} for additional ways to create individual results.
 * <pre><code>
 *   BulkOperationResult result = new BulkOperationResult(
 *     BulkOpType.POST,
 *     BulkOperationResult.HTTP_STATUS_CREATED,
 *     "https://example.com/v2/Users/fa1afe1",
 *     null,
 *     null,
 *     "W/\"4weymrEsh5O6cAEK\"");
 *
 *   result.setResponse(new UserResource().setUserName("Alice"));
 *   BulkResponse response = new BulkResponse(result);
 * </code></pre>
 *
 * Bulk responses are most commonly constructed while iterating over a client
 * bulk request. For more examples showcasing this, see {@link BulkRequest}.
 * <br><br>
 *
 * Like BulkRequest, BulkResponse objects are iterable, so it is possible for
 * SCIM clients to iterate over the response object directly to validate the
 * response from the server. The following code illustrates an example of a
 * client handling a bulk response to track statistics:
 * <pre><code>
 *   public void process()
 *   {
 *     BulkResponse response = sendBulkRequestAndFetchResponse();
 *     for (BulkOperationResult result : response)
 *     {
 *       if (result.isClientError() || result.isServerError())
 *       {
 *         logFailure(result);
 *       }
 *     }
 *   }
 * </code></pre>
 *
 * @since 5.1.0
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Schema(id = "urn:ietf:params:scim:api:messages:2.0:BulkResponse",
    name = "Bulk Response", description = "SCIM 2.0 Bulk Response")
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
  private final List<BulkOperationResult> operations;


  /**
   * Creates a bulk response that summarizes the results of a client bulk
   * request.
   *
   * @param results  The list of bulk operation results to display in the bulk
   *                 response.
   */
  @JsonCreator
  public BulkResponse(
      @Nullable @JsonProperty(value = "Operations")
      final List<BulkOperationResult> results)
  {
    List<BulkOperationResult> ops = (results == null) ? List.of() : results;
    operations = ops.stream().filter(Objects::nonNull).toList();
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
   * Returns an immutable list of result objects contained within this bulk
   * response.
   *
   * @return  The Operations list.
   */
  @NotNull
  public List<BulkOperationResult> getOperations()
  {
    return operations;
  }

  /**
   * Returns an iterator over results contained in this bulk response.
   */
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
