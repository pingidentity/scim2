/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * This class represents a SCIM 2 PATCH request as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.5.2">
 * RFC 7644 Section 3.5.2</a>.
 * <br><br>
 *
 * A patch request contains a list of {@link PatchOperation} elements. Each
 * operation represents an update to a SCIM resource. This is an example
 * of a patch request in JSON form:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
 *   "Operations": [
 *     {
 *       "op": "replace",
 *       "path": "active",
 *       "value": true
 *     }
 *   ]
 * }
 * </pre>
 *
 * This example request contains a single operation that sets the {@code active}
 * value of a targeted SCIM resource to {@code true}. This request can be
 * created with the following Java code:
 * <pre><code>
 *   PatchRequest request = new PatchRequest(
 *       PatchOperation.replace("active", true)
 *   );
 * </code></pre>
 *
 * All patch requests are performed atomically. RFC 7644 Section 3.5.2 states
 * that if any operation within the operation list fails, then the resource
 * SHALL not be updated at all.
 * <br><br>
 *
 * A patch request object, as well as an individual patch operation, can be used
 * to update a SCIM resource with the {@link #applyToResource(ScimResource)}
 * methods. For example, a user's {@code active} status can be updated with the
 * above patch request with the following:
 * <pre><code>
 *   UserResource user = new UserResource().setUserName("link");
 *   UserResource updatedUser = request.applyToResource(user);
 *
 *   // An example representing an update from an individual patch operation.
 *   PatchOperation patchOperation = request.getOperations().get(0);
 *   UserResource firstUpdate = patchOperation.applyToResource(user);
 * </code></pre>
 *
 * @see PatchOperation
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:PatchOp",
    name="Patch Operation", description = "SCIM 2.0 Patch Operation Request")
public class PatchRequest
    extends BaseScimResource
    implements Iterable<PatchOperation>
{
  @NotNull
  @Attribute(description = "Patch Operations")
  @JsonProperty(value = "Operations", required = true)
  private final List<PatchOperation> operations;

  /**
   * Create a new Patch Request.
   *
   * @param operations The list of operations to include.
   */
  @JsonCreator
  public PatchRequest(
      @NotNull @JsonProperty(value = "Operations", required = true)
      final List<PatchOperation> operations)
  {
    this.operations = Collections.unmodifiableList(operations);
  }

  /**
   * Create a new Patch Request.
   *
   * @param operation   The first operation in the patch request. This must not
   *                    be {@code null}.
   * @param operations  An optional field for additional patch operations. Any
   *                    {@code null} values will be ignored.
   */
  public PatchRequest(@NotNull final PatchOperation operation,
                      @Nullable final PatchOperation... operations)
  {
    this(toList(operation, operations));
  }

  /**
   * Retrieves all the individual operations in this patch request.
   *
   * @return The individual operations in this patch request.
   */
  @NotNull
  public List<PatchOperation> getOperations()
  {
    return List.copyOf(operations);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public Iterator<PatchOperation> iterator()
  {
    return getOperations().iterator();
  }

  /**
   * Apply this patch request to the GenericScimResource.
   * <br><br>
   *
   * The {@link #applyToResource} method is likely a better choice if you have a
   * {@link BaseScimResource} derived object. See the class-level Javadoc for
   * more details.
   *
   * @param object The GenericScimResource that will be the target of this patch
   *               request.
   * @return  The {@code object} provided to this method.
   *
   * @throws ScimException If one or more patch operations are invalid.
   */
  @NotNull
  public GenericScimResource apply(@NotNull final GenericScimResource object)
      throws ScimException
  {
    for (PatchOperation operation : this)
    {
      operation.apply(object.getObjectNode());
    }

    return object;
  }

  /**
   * Apply this patch request to the provided ScimResource.
   *
   * @param <T>      The Java type of the SCIM resource.
   * @param resource The resource that will be the target of this patch request.
   * @return         The updated SCIM resource with the same Java type.
   *
   * @throws ScimException  If the update resulted in a malformed resource,
   *                        e.g., a boolean value for a timestamp attribute.
   */
  @NotNull
  public <T extends ScimResource> T applyToResource(@NotNull final T resource)
      throws ScimException
  {
    try
    {
      // Use this instead of PatchOperation#applyToResource so that the Jackson
      // parsing and conversion is only done once.
      GenericScimResource updatedJson = apply(resource.asGenericScimResource());

      return JsonUtils.getObjectReader().forType(resource.getClass())
          .readValue(updatedJson.getObjectNode());
    }
    catch (IOException e)
    {
      throw new BadRequestException(
          "The patch request resulted in an invalid object model.", e);
    }
  }

  /**
   * Indicates whether the provided object is equal to this patch request.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this patch
   *            request, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    PatchRequest that = (PatchRequest) o;
    return operations.equals(that.operations);
  }

  /**
   * Retrieves a hash code for this patch request.
   *
   * @return  A hash code for this patch request.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), operations);
  }
}
