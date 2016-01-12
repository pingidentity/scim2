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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.GenericScimObjectDeserializer;
import com.unboundid.scim2.common.utils.GenericScimObjectSerializer;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A generic SCIM object.  This object can be used if you have no
 * java object representing the SCIM object being returned.
 *
 * This object can be used when the exact structure of the SCIM object
 * that will be recieved as JSON text is not known.  This will provide
 * methods that can read attributes from those objects without needing
 * to know the schema ahead of time.  Another way to work with SCIM
 * objects is when you know ahead of time what the schema will be.  In
 * that case you could still use this object, but BaseScimResource
 * might be a better choice.
 *
 * If you have a BaseScimResource derived object, you can always get a
 * GenericScimResource by serializing The BaseScimResource
 * derived object into a JSON string, and deserializing back to a
 * GenericScimResource.  You could also go the other way.
 *
 * {@link BaseScimResource}
 */
@JsonDeserialize(using = GenericScimObjectDeserializer.class)
@JsonSerialize(using = GenericScimObjectSerializer.class)
public final class GenericScimResource implements ScimResource
{
  private static final Path SCHEMAS = Path.root().attribute("schemas");
  private static final Path ID = Path.root().attribute("id");
  private static final Path EXTERNAL_ID = Path.root().attribute("externalId");
  private static final Path META = Path.root().attribute("meta");

  private final ObjectNode objectNode;

  /**
   * Create a new empty GenericScimResource.
   */
  public GenericScimResource()
  {
    this.objectNode = JsonUtils.getJsonNodeFactory().objectNode();
  }

  /**
   * Create a new GenericScimResource backed by an ObjectNode.
   *
   * @param objectNode The ObjectNode that backs this object.
   */
  public GenericScimResource(final ObjectNode objectNode)
  {
    this.objectNode = objectNode;
  }

  /**
   * Gets the <code>ObjectNode</code> that backs this object.
   * @return a <code>ObjectNode</code>.
   */
  public ObjectNode getObjectNode()
  {
    return this.objectNode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Meta getMeta()
  {
    try
    {
      List<JsonNode> values = JsonUtils.getValues(META, objectNode);
      if(values.isEmpty())
      {
        return null;
      }
      return JsonUtils.nodeToValue(values.get(0), Meta.class);
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMeta(final Meta meta)
  {
    try
    {
      JsonUtils.replaceValue(META, objectNode, JsonUtils.valueToNode(meta));
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getId()
  {
    try
    {
      List<JsonNode> values = JsonUtils.getValues(ID, objectNode);
      if(values.isEmpty())
      {
        return null;
      }
      return JsonUtils.nodeToValue(values.get(0), String.class);
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final String id)
  {
    try
    {
      JsonUtils.replaceValue(ID, objectNode, JsonUtils.valueToNode(id));
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getSchemaUrns()
  {
    try
    {
      List<JsonNode> values = JsonUtils.getValues(SCHEMAS, objectNode);
      if(values.isEmpty() || !values.get(0).isArray())
      {
        return Collections.emptyList();
      }
      return JsonUtils.nodeToValues((ArrayNode) values.get(0), String.class);
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(final Collection<String> schemaUrns)
  {
    try
    {
      JsonUtils.replaceValue(SCHEMAS, objectNode,
          JsonUtils.valueToNode(schemaUrns));
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getExternalId()
  {
    try
    {
      List<JsonNode> values = JsonUtils.getValues(EXTERNAL_ID, objectNode);
      if(values.isEmpty())
      {
        return null;
      }
      return JsonUtils.nodeToValue(values.get(0), String.class);
    }
    catch (Exception e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setExternalId(final String externalId)
  {
    try
    {
      JsonUtils.replaceValue(EXTERNAL_ID, objectNode,
          JsonUtils.valueToNode(externalId));
    }
    catch (ScimException e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieve all JSON nodes referenced by the provided path. Equivalent to
   * using the {@link JsonUtils#getValues(Path, ObjectNode)} method:
   * JsonUtils.getValues(Path.fromString(path), getObjectNode()).
   *
   * The {@link JsonUtils#nodeToValue(JsonNode, Class)} method may be used to
   * bind the retrieved JSON node into specific value type instances.
   *
   * @param path The path to the attribute whose value to retrieve.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If the path is invalid.
   */
  public List<JsonNode> getValues(final String path)
      throws ScimException
  {
    return getValues(Path.fromString(path));
  }

  /**
   * Retrieve all JSON nodes referenced by the provided path. Equivalent to
   * using the {@link JsonUtils#getValues(Path, ObjectNode)} method:
   * JsonUtils.getValues(path, getObjectNode()).
   *
   * The {@link JsonUtils#nodeToValue(JsonNode, Class)} method may be used to
   * convert the retrieved JSON node into specific value type instances.
   *
   * @param path The path to the attribute whose value to retrieve.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If the path is invalid.
   */
  public List<JsonNode> getValues(final Path path)
      throws ScimException
  {
    return JsonUtils.getValues(path, objectNode);
  }

  /**
   * Update the value at the provided path. Equivalent to using the
   * {@link JsonUtils#replaceValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.replaceValues(Path.fromString(path), getObjectNode(), value).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @return This object.
   * @throws ScimException If the path is invalid.
   */
  public GenericScimResource replaceValue(final String path,
                                          final JsonNode value)
      throws ScimException
  {
    replaceValue(Path.fromString(path), value);
    return this;
  }

  /**
   * Update the value at the provided path. Equivalent to using the
   * {@link JsonUtils#replaceValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.replaceValues(path, getObjectNode(), value).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @return This object.
   * @throws ScimException If the path is invalid.
   */
  public GenericScimResource replaceValue(final Path path,
                                          final JsonNode value)
      throws ScimException
  {
    JsonUtils.replaceValue(path, objectNode, value);
    return this;
  }

  /**
   * Add new values at the provided path. Equivalent to using the
   * {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.addValue(Path.fromString(path), getObjectNode(), values).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @return This object.
   * @throws ScimException If the path is invalid.
   */
  public GenericScimResource addValues(final String path,
                                       final ArrayNode values)
      throws ScimException
  {
    addValues(Path.fromString(path), values);
    return this;
  }

  /**
   * Add new values at the provided path. Equivalent to using the
   * {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.addValue(path, getObjectNode(), values).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @return This object.
   * @throws ScimException If the path is invalid.
   */
  public GenericScimResource addValues(final Path path,
                                       final ArrayNode values)
      throws ScimException
  {
    JsonUtils.addValue(path, objectNode, values);
    return this;
  }

  /**
   * Removes values at the provided path. Equivalent
   * to using the {@link JsonUtils#removeValues(Path, ObjectNode)} method:
   * JsonUtils.removeValue(Path.fromString(path), getObjectNode(), values).
   *
   * @param path The path to the attribute whose values to remove.
   * @return Whether one or more values where removed.
   * @throws ScimException If the path is invalid.
   */
  public boolean removeValues(final String path)
      throws ScimException
  {
    return removeValues(Path.fromString(path));
  }

  /**
   * Removes values at the provided path. Equivalent
   * to using the {@link JsonUtils#removeValues(Path, ObjectNode)} method:
   * JsonUtils.removeValue(Path.fromString(path), getObjectNode(), values).
   *
   * @param path The path to the attribute whose values to remove.
   * @return Whether one or more values where removed.
   * @throws ScimException If the path is invalid.
   */
  public boolean removeValues(final Path path)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.removeValues(path, objectNode);
    return !nodes.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  public GenericScimResource asGenericScimResource()
  {
    return this;
  }

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
}
