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

package com.unboundid.scim2.server;


import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.bulk.BulkOpType;
import com.unboundid.scim2.common.bulk.BulkOperation;
import com.unboundid.scim2.common.bulk.BulkOperationResult;
import com.unboundid.scim2.common.bulk.BulkRequest;
import com.unboundid.scim2.common.bulk.BulkResponse;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.server.annotations.ResourceType;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;


/**
 * A test implementation of a Bulk endpoint that returns successful responses
 * for each operation.
 */
@ResourceType(
    description = "Bulk Request Endpoint",
    name = "Bulk Request Endpoint",
    schema = BulkResponse.class)
@Path("/Bulk")
public class BulkEndpoint
{
  // A UUID for "location" values on create operations. This is stored as a
  // constant so that multiple POST operations within a single test will result
  // in a consistent value, simplifying comparison logic.
  private static final UUID CREATED_RESOURCE_ID = UUID.randomUUID();


  /**
   * This endpoint simulates successful responses from bulk requests. The
   * {@code response} and {@code version} fields will always be {@code null}.
   *
   * @param request  The bulk request.
   * @return  A successful bulk response.
   */
  @POST
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public BulkResponse processBulkRequest(@NotNull final BulkRequest request)
  {
    List<BulkOperationResult> results = new ArrayList<>();
    for (BulkOperation op : request)
    {
      String endpoint = op.getPath();
      String status = "200";
      if (op.getMethod() == BulkOpType.POST)
      {
        status = "201";
        endpoint += "/" + CREATED_RESOURCE_ID;
      }
      else if (op.getMethod() == BulkOpType.DELETE)
      {
        status = "204";
      }

      BulkOperationResult result = new BulkOperationResult(op, status,
          "https://example.com/v2" + endpoint);
      results.add(result);
    }

    return new BulkResponse(results);
  }

  /**
   * This endpoint simulates an error when initiating a bulk request.
   *
   * @param ignored  The client bulk request.
   * @return  This method never returns since it always throws an exception.
   */
  @POST
  @Path("/BulkError")
  public BulkResponse error(@NotNull final BulkRequest ignored)
      throws BadRequestException
  {
    throw BadRequestException.tooMany(
        "Simulated error for too many bulk operations.");
  }
}
