/*
 * Copyright 2015 UnboundID Corp.
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

package com.unboundid.scim2.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.exceptions.ScimException;
import com.unboundid.scim2.model.BaseScimResourceObject;
import com.unboundid.scim2.model.GenericScimResourceObject;
import com.unboundid.scim2.schema.SchemaUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a SCIM 2.0 patch request.
 */
@SchemaInfo(id="urn:ietf:params:scim:api:messages:2.0:PatchOp",
    name="Patch Operation", description = "SCIM 2.0 Patch Operation Request")
public final class PatchRequest
    extends BaseScimResourceObject
    implements Iterable<PatchOperation>
{
  @SchemaProperty(description = "Patch Operations")
  @JsonProperty(value = "Operations", required = true)
  private final List<PatchOperation> operations;

  /**
   * Create a new Patch Operation Request.
   *
   * @param operations The list of operations to include.
   */
  @JsonCreator
  private PatchRequest(
      @JsonProperty(value = "Operations", required = true)
      final List<PatchOperation> operations)
  {
    this.operations = operations;
  }

  /**
   * Create a new Patch Operation Request.
   */
  public PatchRequest()
  {
    this.operations = new LinkedList<PatchOperation>();
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   *
   * @return This patch operation request.
   */
  public PatchRequest replaceValue(final String path,
                                            final Object object)
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    return addOperation(PatchOperation.replace(path, newObjectNode));
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   *
   * @return This patch operation request.
   */
  public PatchRequest replaceValue(final Path path,
                                            final Object object)
  {
    return replaceValue(path.toString(), object);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   */
  public PatchRequest replaceValues(final String path,
                               final Collection<Object> objects)
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    return addOperation(PatchOperation.replace(path, newObjectNode));

  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   */
  public PatchRequest replaceValues(final Path path,
                               final Collection<Object> objects)
  {
    return replaceValues(path.toString(), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   */
  public PatchRequest replaceValues(final String path,
                                             final Object... objects)
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    return addOperation(PatchOperation.replace(path, newObjectNode));
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   */
  public PatchRequest replaceValues(final Path path,
                                             final Object... objects)
  {
    return replaceValues(path.toString(), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   */
  public PatchRequest addValues(final String path,
                                         final Collection<?> objects)
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    return addOperation(PatchOperation.add(path, newObjectNode));

  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   */
  public PatchRequest addValues(final Path path,
                                         final Collection<?> objects)
  {
    return addValues(path.toString(), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   */
  public PatchRequest addValues(final String path,
                                         final Object... objects)
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    return addOperation(PatchOperation.add(path, newObjectNode));

  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   */
  public PatchRequest addValues(final Path path,
                                         final Object... objects)
  {
    return addValues(path.toString(), objects);
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.

   * @return This patch operation request.
   */
  public PatchRequest removeValues(final String path)
  {
    return addOperation(PatchOperation.remove(path));
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.

   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  public PatchRequest removeValues(final Path path)
      throws ScimException
  {
    return removeValues(path.toString());
  }

  /**
   * Add a new patch operation this this patch request.
   *
   * @param op The patch operation to add.
   *
   * @return This patch operation request.
   */
  public PatchRequest addOperation(final PatchOperation op)
  {
    operations.add(op);
    return this;
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
  public void apply(final GenericScimResourceObject object) throws ScimException
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
    return operations.hashCode();
  }
}
