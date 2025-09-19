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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.BulkOperation;
import com.unboundid.scim2.common.messages.BulkRequest;
import com.unboundid.scim2.common.messages.BulkResponse;
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
   * Create a new bulk request builder.
   *
   * @param target The WebTarget to send the request.
   */
  public BulkRequestBuilder(@NotNull final WebTarget target)
  {
    this(target, List.of());
  }

  public BulkRequestBuilder(@NotNull final WebTarget target,
                            @Nullable final List<BulkOperation> operations)
  {
    super(target);
    append(operations);
  }

  public BulkRequestBuilder(@NotNull final WebTarget target,
                            @Nullable final BulkOperation... ops)
  {
    this(target, (ops == null) ? List.of() : Arrays.asList(ops));
  }

  @NotNull
  public BulkRequestBuilder append(@Nullable final BulkOperation... operations)
  {
    if (operations != null)
    {
      append(Arrays.asList(operations));
    }
    return this;
  }

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

  public BulkRequestBuilder clear()
  {
    operations.clear();
    return this;
  }

  @NotNull
  public BulkResponse invoke() throws ScimException
  {
    return invoke(BulkResponse.class);
  }

  @NotNull
  public <C> C invoke(@NotNull final Class<C> cls)
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
