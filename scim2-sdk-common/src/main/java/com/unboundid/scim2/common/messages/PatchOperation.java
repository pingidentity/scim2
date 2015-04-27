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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An individual patch operation.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "op")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PatchOperation.AddOperation.class,
        name="add"),
    @JsonSubTypes.Type(value = PatchOperation.RemoveOperation.class,
        name="remove"),
    @JsonSubTypes.Type(value = PatchOperation.ReplaceOperation.class,
        name="replace")})
public abstract class PatchOperation
{
  static final class AddOperation extends PatchOperation
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
    private AddOperation(
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
    public JsonNode getJsonNode()
    {
      return value;
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

  static final class RemoveOperation extends PatchOperation
  {
    /**
     * Create a new remove patch operation.
     *
     * @param path The path targeted by this patch operation.
     */
    @JsonCreator
    private RemoveOperation(
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

  static final class ReplaceOperation extends PatchOperation
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
    private ReplaceOperation(
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
    public JsonNode getJsonNode()
    {
      return value;
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

  private final String path;

  /**
   * Create a new patch operation.
   *
   * @param path The path targeted by this patch operation.
   */
  PatchOperation(final String path)
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
   * Retrieve the value or values of the patch operation as a JsonNode.
   *
   * @return  The value or values of the patch operation, or {@code null}
   *          if this operation is a remove operation.
   */
  @JsonIgnore
  public JsonNode getJsonNode()
  {
    return null;
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

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation add(final String path, final JsonNode value)
  {
    return new AddOperation(path, value);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(final String path, final JsonNode value)
  {
    return new ReplaceOperation(path, value);
  }

  /**
   * Create a new remove patch operation.
   *
   * @param path The path targeted by this patch operation.
   *
   * @return The new delete patch operation.
   */
  public static PatchOperation remove(final String path)
  {
    return new RemoveOperation(path);
  }
}
