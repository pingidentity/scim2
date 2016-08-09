/*
 * Copyright 2015-2016 UnboundID Corp.
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
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
     * @throws ScimException If a value is not valid.
     */
    @JsonCreator
    private AddOperation(
        @JsonProperty(value = "path") final Path path,
        @JsonProperty(value = "value", required = true) final JsonNode value)
        throws ScimException
    {
      super(path);
      if(value == null || value.isNull() ||
           ((value.isArray() || value.isObject()) && value.size() == 0))
       {
         throw BadRequestException.invalidSyntax(
             "value field must not be null or an empty container");
       }
      if(path == null && !value.isObject())
      {
        throw BadRequestException.invalidSyntax(
            "value field must be a JSON object containing the attributes to " +
                "add");
      }
      if(path != null)
      {
        for (Path.Element element : path)
        {
          if(element.getValueFilter() != null)
          {
            throw BadRequestException.invalidPath(
                "path field for add operations must not include any value " +
                    "selection filters");
          }
        }
      }
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
      return value.deepCopy();
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
      return JsonUtils.getObjectReader().treeToValue(
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
        objects.add(JsonUtils.getObjectReader().treeToValue(node, cls));
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
          getPath(), node, value);
      addMissingSchemaUrns(node);
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
     * @throws ScimException If a path is null.
     */
    @JsonCreator
    private RemoveOperation(
        @JsonProperty(value = "path", required = true) final Path path)
        throws ScimException
    {
      super(path);
      if(path == null)
      {
        throw BadRequestException.noTarget(
            "path field must not be null for remove operations");
      }
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
      JsonUtils.removeValues(getPath(), node);
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
     * @throws ScimException If a value is not valid.
     */
    @JsonCreator
    private ReplaceOperation(
        @JsonProperty(value = "path") final Path path,
        @JsonProperty(value = "value", required = true) final JsonNode value)
        throws ScimException
    {
      super(path);
      if(value == null || value.isNull() ||
           ((value.isArray() || value.isObject()) && value.size() == 0))
       {
         throw BadRequestException.invalidSyntax(
             "value field must not be null or an empty container");
       }
      if(path == null && !value.isObject())
      {
        throw BadRequestException.invalidSyntax(
            "value field must be a JSON object containing the attributes to " +
                "replace");
      }
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
      return value.deepCopy();
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
      return JsonUtils.getObjectReader().treeToValue(value, cls);
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
        objects.add(JsonUtils.getObjectReader().treeToValue(node, cls));
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
          getPath(), node, value);
      addMissingSchemaUrns(node);
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

  private final Path path;

  /**
   * Create a new patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @throws ScimException If an value is not valid.
   */
  PatchOperation(final Path path) throws ScimException
  {
    if(path != null)
    {
      if(path.size() > 2)
      {
        throw BadRequestException.invalidPath(
            "Path can not target sub-attributes more than one level deep");
      }

      if(path.size() == 2 && path.getElement(1).getValueFilter() != null)
      {
        throw BadRequestException.invalidPath(
            "Path can not include a value filter on sub-attributes");
      }
    }
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
  public Path getPath()
  {
    return path;
  }

  /**
   * Retrieve the value or values of the patch operation as a JsonNode. The
   * returned JsonNode is a copy so it may be altered without altering this
   * operation.
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
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    try
    {
      return JsonUtils.getObjectWriter().withDefaultPrettyPrinter().
          writeValueAsString(this);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Implicitly add any schema URNs of any extended attributes that are missing
   * from the schemas attribute.
   *
   * @param node The ObjectNode to apply this patch operation to.
   */
  protected void addMissingSchemaUrns(final ObjectNode node)
  {
    // Implicitly add the schema URN of any extended attributes to the
    // schemas attribute.
    JsonNode schemasNode =
        node.path(SchemaUtils.SCHEMAS_ATTRIBUTE_DEFINITION.getName());
    if(schemasNode.isArray())
    {
      ArrayNode schemas = (ArrayNode) schemasNode;
      if (getPath() == null)
      {
        Iterator<String> i = getJsonNode().fieldNames();
        while (i.hasNext())
        {
          String field = i.next();
          if (SchemaUtils.isUrn(field))
          {
            addSchemaUrnIfMissing(schemas, field);
          }
        }
      }
      else if(getPath().getSchemaUrn() != null)
      {
        addSchemaUrnIfMissing(schemas, getPath().getSchemaUrn());
      }
    }
  }

  private void addSchemaUrnIfMissing(final ArrayNode schemas,
                                     final String schemaUrn)
  {
    for(JsonNode node : schemas)
    {
      if(node.isTextual() && node.textValue().equalsIgnoreCase(schemaUrn))
      {
        return;
      }
    }

    schemas.add(schemaUrn);
  }

  /**
   * Create a new add patch operation.
   *
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation add(final JsonNode value)
  {
    return add((Path) null, value);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation add(final String path, final JsonNode value)
      throws ScimException
  {
    return add(Path.fromString(path), value);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation add(final Path path, final JsonNode value)
  {
    try
    {
      return new AddOperation(path, value);
    }
    catch (ScimException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  // String
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addStringValues(
      final String path, final List<String> values) throws ScimException
  {
    return addStringValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addStringValues(
      final Path path, final List<String> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(String value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final String value) throws ScimException
  {
    return replace(path, TextNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final String value)
  {
    return replace(path, TextNode.valueOf(value));
  }

  // Boolean
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addBooleanValues(
      final String path, final List<Boolean> values) throws ScimException
  {
    return addBooleanValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addBooleanValues(
      final Path path, final List<Boolean> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Boolean value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final Boolean value) throws ScimException
  {
    return replace(path, BooleanNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final Boolean value)
  {
    return replace(path, BooleanNode.valueOf(value));
  }


  // Double
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addDoubleValues(
      final String path, final List<Double> values)
      throws ScimException
  {
    return addDoubleValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addDoubleValues(
      final Path path, final List<Double> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Double value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final Double value) throws ScimException
  {
    return replace(path, DoubleNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final Double value)
  {
    return replace(path, DoubleNode.valueOf(value));
  }

  // Integer
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addIntegerValues(
      final String path, final List<Integer> values) throws ScimException
  {
    return addIntegerValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addIntegerValues(
      final Path path, final List<Integer> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Integer value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final Integer value) throws ScimException
  {
    return replace(path, IntNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final Integer value)
  {
    return replace(path, IntNode.valueOf(value));
  }

  // Long
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addLongValues(
      final String path, final List<Long> values) throws ScimException
  {
    return addLongValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addLongValues(
      final Path path, final List<Long> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Long value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final Long value) throws ScimException
  {
    return replace(path, LongNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final Long value)
  {
    return replace(path, LongNode.valueOf(value));
  }

  // Date
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addDateValues(
      final String path, final List<Date> values) throws ScimException
  {
    return addDateValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException if an error occurs.
   */
  public static PatchOperation addDateValues(
      final Path path, final List<Date> values) throws ScimException
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Date value : values)
    {
      arrayNode.add(GenericScimResource.getDateJsonNode(value).textValue());
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final Date value) throws ScimException
  {
    String valueString =
        GenericScimResource.getDateJsonNode(value).textValue();
    return replace(path, valueString);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException if an error occurs.
   */
  public static PatchOperation replace(
      final Path path, final Date value) throws ScimException
  {
    String valueString =
        GenericScimResource.getDateJsonNode(value).textValue();
    return replace(path, valueString);
  }

  // Binary
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The value(s) to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addBinaryValues(
      final String path, final List<byte[]> values) throws ScimException
  {
    return addBinaryValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addBinaryValues(
      final Path path, final List<byte[]> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(byte[] value : values)
    {
      arrayNode.add(Base64Variants.getDefaultVariant().encode(value));
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final byte[] value) throws ScimException
  {
    String valueString = Base64Variants.getDefaultVariant().encode(value);
    return replace(path, valueString);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final byte[] value)
  {
    String valueString = Base64Variants.getDefaultVariant().encode(value);
    return replace(path, valueString);
  }

  // URI
  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation addURIValues(
      final String path, final List<URI> values) throws ScimException
  {
    return addURIValues(Path.fromString(path), values);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  public static PatchOperation addURIValues(
      final Path path, final List<URI> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(URI value : values)
    {
      arrayNode.add(value.toString());
    }
    return add(path, arrayNode);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "{@code userName}"
   * @param value The value(s) to replace.  The value must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(
      final String path, final URI value) throws ScimException
  {
    return replace(path, value.toString());
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(
      final Path path, final URI value)
  {
    return replace(path, value.toString());
  }

  /**
   * Create a new replace patch operation.
   *
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(final ObjectNode value)
  {
    return replace((Path) null, value);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   *             Path string examples:
   *               "{@code userName eq 'bjensen'}"
   *               "userName"
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation replace(final String path, final JsonNode value)
      throws ScimException
  {
    return replace(Path.fromString(path), value);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.  The path
   *             must not be {@code null}.
   * @param value The value(s) to replace.  The value(s) must not be {@code null}.
   *
   * @return The new replace patch operation.
   */
  public static PatchOperation replace(final Path path, final JsonNode value)
  {
    try
    {
      return new ReplaceOperation(path, value);
    }
    catch (ScimException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Create a new remove patch operation.
   *
   * @param path The path targeted by this patch operation.
   *
   * @return The new delete patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation remove(final String path) throws ScimException
  {
    return remove(Path.fromString(path));
  }

  /**
   * Create a new remove patch operation.
   *
   * @param path The path targeted by this patch operation.
   *
   * @return The new delete patch operation.
   */
  public static PatchOperation remove(final Path path)
  {
    try
    {
      return new RemoveOperation(path);
    }
    catch (ScimException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Create a new patch operation based on the parameters provided.
   *
   * @param opType The operation type.
   * @param path The path targeted by this patch operation.
   * @param value The value(s).
   *
   * @return The new patch operation.
   * @throws ScimException If the path is invalid.
   */
  public static PatchOperation create(final PatchOpType opType,
                                      final String path,
                                      final JsonNode value) throws ScimException
  {
    return create(opType, Path.fromString(path), value);
  }

  /**
   * Create a new patch operation based on the parameters provided.
   *
   * @param opType The operation type.
   * @param path The path targeted by this patch operation.
   * @param value The value(s).
   *
   * @return The new patch operation.
   */
  public static PatchOperation create(final PatchOpType opType,
                                      final Path path,
                                      final JsonNode value)
  {
    switch (opType)
    {
      case ADD:
        return add(path, value);
      case REPLACE:
        return replace(path, value);
      case REMOVE:
        return remove(path);
      default:
        throw new IllegalArgumentException("Unknown patch op type " + opType);
    }
  }
}
