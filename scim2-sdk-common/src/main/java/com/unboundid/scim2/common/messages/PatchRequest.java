/*
 * Copyright 2015-2018 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.GenericScimResource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing a SCIM 2 patch request.
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:PatchOp",
    name="Patch Operation", description = "SCIM 2.0 Patch Operation Request")
public final class PatchRequest
    extends BaseScimResource
    implements Iterable<PatchOperation>
{
  @Attribute(description = "Patch Operations")
  @JsonProperty(value = "Operations", required = true)
  private final List<PatchOperation> operations;

  /**
   * Create a new Patch Operation Request.
   *
   * @param operations The list of operations to include.
   */
  @JsonCreator
  public PatchRequest(
      @JsonProperty(value = "Operations", required = true)
      final List<PatchOperation> operations)
  {
    this.operations = Collections.unmodifiableList(operations);
  }

  /**
   * Retrieves all the individual operations in this patch request.
   *
   * @return The individual operations in this patch request.
   */
  public List<PatchOperation> getOperations()
  {
    return Collections.unmodifiableList(operations);
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
  public void apply(final GenericScimResource object) throws ScimException
  {
    for(PatchOperation operation : this)
    {
      operation.apply(object.getObjectNode());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
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
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + operations.hashCode();
    return result;
  }
}
