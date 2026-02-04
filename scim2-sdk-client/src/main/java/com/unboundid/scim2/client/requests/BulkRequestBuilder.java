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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.bulk.BulkOperation;
import com.unboundid.scim2.common.bulk.BulkRequest;
import com.unboundid.scim2.common.bulk.BulkResponse;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * This class provides a builder for SCIM 2 bulk requests. Bulk requests provide
 * a way for clients to send multiple write requests in a single API call.
 * <br><br>
 *
 * For more information, see the documentation in {@link BulkRequest}.
 */
public class BulkRequestBuilder
    extends ResourceReturningRequestBuilder<BulkRequestBuilder>
{
  @NotNull
  private final List<BulkOperation> operations = new ArrayList<>();

  /**
   * Create a new bulk request builder that will be used to apply a list of
   * write operations to the given web target.
   *
   * @param target  The WebTarget that will receive the bulk request.
   */
  public BulkRequestBuilder(@NotNull final WebTarget target)
  {
    super(target);
  }

  /**
   * Append a list of bulk operations to this bulk request. Any {@code null}
   * values will be ignored.
   *
   * @param ops  The list of bulk operations to add.
   *
   * @return  This bulk request builder.
   */
  @NotNull
  public BulkRequestBuilder append(@Nullable final List<BulkOperation> ops)
  {
    List<BulkOperation> operationList = (ops == null) ? List.of() : ops;
    for (BulkOperation operation : operationList)
    {
      if (operation != null)
      {
        operations.add(operation);
      }
    }

    return this;
  }

  /**
   * Append one or more bulk operations to this bulk request. Any {@code null}
   * values will be ignored.
   *
   * @param operations  The bulk operation(s) to add.
   *
   * @return  This bulk request builder.
   */
  @NotNull
  public BulkRequestBuilder append(@Nullable final BulkOperation... operations)
  {
    if (operations != null)
    {
      append(Arrays.asList(operations));
    }

    return this;
  }

  /**
   * Invoke the SCIM bulk request and return the response.
   *
   * @return  The bulk response representing the summary of the write requests
   *          that were attempted and whether they were successful.
   * @throws ScimException If the SCIM service responded with an error.
   */
  @NotNull
  public BulkResponse invoke() throws ScimException
  {
    return invoke(BulkResponse.class);
  }

  /**
   * Invoke the SCIM bulk request.
   *
   * @param <C> The Java type to return. This should be a {@link BulkResponse}.
   * @param cls The Java type to return. This should be a {@link BulkResponse}.
   *
   * @return  The bulk response.
   * @throws ScimException  If the SCIM service responded with an error, such
   *                        as if the JSON body was too big, or there was a
   *                        circular reference in the {@code bulkId} fields.
   */
  @NotNull
  protected  <C> C invoke(@NotNull final Class<C> cls)
      throws ScimException
  {
    BulkRequest request = new BulkRequest(operations);
    var entity = Entity.entity(generify(request), getContentType());
    try (Response response = buildRequest().method(HttpMethod.POST, entity))
    {
      if (response.getStatusInfo().getFamily() == SUCCESSFUL)
      {
        return response.readEntity(cls);
      }
      else
      {
        throw toScimException(response);
      }
    }
  }
}
