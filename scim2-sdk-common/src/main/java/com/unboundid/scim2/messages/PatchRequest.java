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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.exceptions.ScimException;
import com.unboundid.scim2.model.BaseScimResourceObject;
import com.unboundid.scim2.model.GenericScimResourceObject;
import com.unboundid.scim2.schema.SchemaUtils;
import com.unboundid.scim2.utils.JsonUtils;

import java.util.ArrayList;
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
    implements Iterable<PatchRequest.Operation>
{
  @SchemaProperty(description = "Patch Operations")
  @JsonProperty(value = "Operations", required = true)
  private final List<Operation> operations;

  /**
   * Create a new Patch Operation Request.
   *
   * @param operations The list of operations to include.
   */
  @JsonCreator
  private PatchRequest(
      @JsonProperty(value = "Operations", required = true)
      final List<Operation> operations)
  {
    this.operations = operations;
  }

  /**
   * Create a new Patch Operation Request.
   */
  public PatchRequest()
  {
    this.operations = new LinkedList<Operation>();
  }

  /**
   * An individual patch operation.
   */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "op")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = AddOperation.class, name="add"),
      @JsonSubTypes.Type(value = RemoveOperation.class, name="remove"),
      @JsonSubTypes.Type(value = ReplaceOperation.class, name="replace")})
  public abstract static class Operation
  {
    private final String path;

    /**
     * Create a new patch operation.
     *
     * @param path The path targeted by this patch operation.
     */
    protected Operation(final String path)
    {
      this.path = path;
    }

    /**
     * Retrieves the operation type.
     *
     * @return The operation type.
     */
    @JsonIgnore
    public abstract PatchOpType getOpType();

    /**
     * Retrieves the path targeted by this operation.
     *
     * @return The path targeted by this operation.
     */
    public String getPath()
    {
      return path;
    }

    /**
     * Retrieve the value of the patch operation.
     *
     * @param cls The Java class object used to determine the type to return.
     * @param <T> The generic type parameter of the Java class used to determine
     *            the type to return.
     * @return The value of the patch operation.
     * @throws JsonProcessingException If the value can not be parsed to the
     *         type specified by the Java class object.
     * @throws ScimException If the path is invalid.
     * @throws IllegalArgumentException If the operation contains more than one
     *         value, in which case, the getValues method should be used to
     *         retrieve all values.
     */
    public <T> T getValue(final Class<T> cls)
        throws JsonProcessingException, ScimException, IllegalArgumentException
    {
      return null;
    }

    /**
     * Retrieve all values of the patch operation.
     *
     * @param cls The Java class object used to determine the type to return.
     * @param <T> The generic type parameter of the Java class used to determine
     *            the type to return.
     * @return The values of the patch operation.
     * @throws JsonProcessingException If the value can not be parsed to the
     *         type specified by the Java class object.
     * @throws ScimException If the path is invalid.
     */
    public <T> List<T> getValues(final Class<T> cls)
        throws JsonProcessingException, ScimException
    {
      return null;
    }

    /**
     * Apply this patch operation to an ObjectNode.
     *
     * @param node The ObjectNode to apply this patch operation to.
     *
     * @throws ScimException If the patch operation is invalid.
     */
    public abstract void apply(final ObjectNode node) throws ScimException;
  }

  private static final class AddOperation extends Operation
  {
    @JsonProperty
    private final JsonNode value;

    /**
     * Create a new add patch operation.
     *
     * @param path The path targeted by this patch operation.
     * @param value The value(s) to add.
     */
    @JsonCreator
    public AddOperation(
        @JsonProperty(value = "path", required = true) final String path,
        @JsonProperty(value = "value", required = true) final JsonNode value)
    {
      super(path);
      this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchOpType getOpType()
    {
      return PatchOpType.ADD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getValue(final Class<T> cls)
        throws JsonProcessingException, ScimException, IllegalArgumentException
    {
      if(value.isArray())
      {
        throw new IllegalArgumentException("Patch operation contains " +
            "multiple values");
      }
      return SchemaUtils.createSCIMCompatibleMapper().treeToValue(
                value, cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getValues(final Class<T> cls)
        throws JsonProcessingException, ScimException
    {
      ArrayList<T> objects = new ArrayList<T>(value.size());
      for(JsonNode node : value)
      {
        objects.add(
            SchemaUtils.createSCIMCompatibleMapper().treeToValue(node, cls));
      }
      return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(final ObjectNode node) throws ScimException
    {
      JsonUtils.addValue(getPath() == null ? Path.root() :
          Path.fromString(getPath()), node, value);
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

      AddOperation that = (AddOperation) o;
      if (getPath() != null ? !getPath().equals(that.getPath()) :
          that.getPath() != null)
      {
        return false;
      }
      if (!value.equals(that.value))
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
      int result = getPath() != null ? getPath().hashCode() : 0;
      result = 31 * result + value.hashCode();
      return result;
    }
  }

  private static final class RemoveOperation extends Operation
  {
    /**
     * Create a new remove patch operation.
     *
     * @param path The path targeted by this patch operation.
     */
    @JsonCreator
    public RemoveOperation(
        @JsonProperty(value = "path", required = true) final String path)
    {
      super(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchOpType getOpType()
    {
      return PatchOpType.REMOVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(final ObjectNode node) throws ScimException
    {
      JsonUtils.removeValues(Path.fromString(getPath()), node);
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

      RemoveOperation that = (RemoveOperation) o;

      if (getPath() != null ? !getPath().equals(that.getPath()) :
          that.getPath() != null)
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
      return getPath() != null ? getPath().hashCode() : 0;
    }
  }

  private static final class ReplaceOperation extends Operation
  {
    @JsonProperty
    private final JsonNode value;

    /**
     * Create a new replace patch operation.
     *
     * @param path The path targeted by this patch operation.
     * @param value The value(s) to replace.
     */
    @JsonCreator
    public ReplaceOperation(
        @JsonProperty(value = "path", required = true) final String path,
        @JsonProperty(value = "value", required = true) final JsonNode value)
    {
      super(path);
      this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatchOpType getOpType()
    {
      return PatchOpType.REPLACE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getValue(final Class<T> cls)
        throws JsonProcessingException, ScimException, IllegalArgumentException
    {
      if(value.isArray())
      {
        throw new IllegalArgumentException("Patch operation contains " +
            "multiple values");
      }
      return SchemaUtils.createSCIMCompatibleMapper().treeToValue(
                value, cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getValues(final Class<T> cls)
        throws JsonProcessingException, ScimException
    {
      ArrayList<T> objects = new ArrayList<T>(value.size());
      for(JsonNode node : value)
      {
        objects.add(
            SchemaUtils.createSCIMCompatibleMapper().treeToValue(node, cls));
      }
      return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(final ObjectNode node) throws ScimException
    {
      JsonUtils.replaceValue(getPath() == null ? Path.root() :
          Path.fromString(getPath()), node, value);
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

      ReplaceOperation that = (ReplaceOperation) o;

      if (getPath() != null ? !getPath().equals(that.getPath()) :
          that.getPath() != null)
      {
        return false;
      }
      if (!value.equals(that.value))
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
      int result = getPath() != null ? getPath().hashCode() : 0;
      result = 31 * result + value.hashCode();
      return result;
    }
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
    return addOperation(new ReplaceOperation(path, newObjectNode));
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
    return addOperation(new ReplaceOperation(path, newObjectNode));

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
    return addOperation(new ReplaceOperation(path, newObjectNode));
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
    return addOperation(new AddOperation(path, newObjectNode));

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
    return addOperation(new AddOperation(path, newObjectNode));

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
    return addOperation(new RemoveOperation(path));
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
  public PatchRequest addOperation(final Operation op)
  {
    operations.add(op);
    return this;
  }

  /**
   * Retrieves all the individual operations in this patch request.
   *
   * @return The individual operations in this patch request.
   */
  public List<Operation> getOperations()
  {
    return Collections.unmodifiableList(operations);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<Operation> iterator()
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
    for(Operation operation : this)
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
