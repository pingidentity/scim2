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
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.unboundid.scim2.common.utils.StaticUtils.toNullFreeList;
import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * This class represents a SCIM 2 bulk response. This is an API response
 * returned by a SCIM service after it has processed a bulk request.
 * <br><br>
 *
 * A bulk response takes the following form when represented as JSON:
 * <pre>
 *
 * </pre>
 *
 *
 * <pre><code>
 *   BulkRequest bulkRequest = getBulkRequestFromClient();
 *   BulkResponse response = new BulkResponse();
 *   for (BulkOperation op : bulkRequest)
 *   {
 *     ScimResource opResponse = service.processOperation(op);
 *     response.appendOperation(opResponse);
 *   }
 * </code></pre>
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:BulkResponse",
    name="Bulk Response", description = "SCIM 2.0 Bulk Response")
@JsonPropertyOrder({"schemas", "Operations"})
public class BulkResponse
{
  @NotNull
  private static final BulkOperationResult[] EMPTY_RESULT_ARRAY =
      new BulkOperationResult[0];

  @NotNull
  @Attribute(description = "Patch Operations")
  @JsonProperty(value = "Operations", required = true)
  private final List<BulkOperationResult> operations;

  /**
   * Creates a bulk response with an
   */
  public BulkResponse()
  {
    operations = new ArrayList<>();
  }

  @JsonCreator
  public BulkResponse(@Nullable final List<BulkOperationResult> list)
  {
    operations = new ArrayList<>(toNullFreeList(list));
  }

  public BulkResponse(@NotNull final BulkOperationResult result,
                      @Nullable final BulkOperationResult... results)
  {
    this(toList(result, results));
  }

  @NotNull
  public BulkResponse append(@Nullable final BulkOperationResult... result)
  {
    List<BulkOperationResult> list = (result == null) ?
        List.of() : Arrays.asList(result);
    for (BulkOperationResult operation : list)
    {
      if (operation != null)
      {
        this.operations.add(operation);
      }
    }
    return this;
  }

  /**
   * Returns an immutable list of responses.
   *
   * @return  The Operations list.
   */
  public List<BulkOperationResult> getOperations()
  {
    return List.copyOf(operations);
  }
}
