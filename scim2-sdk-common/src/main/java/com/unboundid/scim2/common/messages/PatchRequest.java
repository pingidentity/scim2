/*
 * Copyright 2015-2024 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.GenericScimResource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * This class represents a SCIM 2 PATCH request. A patch request contains a list
 * of {@link PatchOperation} elements, where each patch operation represents a
 * change that should be applied to a SCIM resource. The following is an example
 * of a patch request in JSON form:
 * <pre>
 * {
 *   "schemas": [
 *     "urn:ietf:params:scim:api:messages:2.0:PatchOp"
 *   ],
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
 * value to {@code true}. This request can be created with the following Java
 * code:
 * <pre>
 *   PatchRequest request = new PatchRequest(
 *       PatchOperation.replace("active", true)
 *   );
 * </pre>
 *
 * All patch requests are performed atomically. RFC 7644 Section 3.5.2 states
 * that if any operation within the operation list fails, then the resource
 * SHALL not be updated at all.
 *
 * @see PatchOperation
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:PatchOp",
    name="Patch Operation", description = "SCIM 2.0 Patch Operation Request")
public final class PatchRequest
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
    return Collections.unmodifiableList(operations);
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
   * Apply this patch request to the GenericScimResourceObject.
   *
   * @param object The GenericScimResourceObject to apply this patch to.
   *
   * @throws ScimException If the one or more patch operations is invalid.
   */
  public void apply(@NotNull final GenericScimResource object)
      throws ScimException
  {
    for(PatchOperation operation : this)
    {
      operation.apply(object.getObjectNode());
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

    if (!operations.equals(that.operations))
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this patch request.
   *
   * @return  A hash code for this patch request.
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + operations.hashCode();
    return result;
  }
}
