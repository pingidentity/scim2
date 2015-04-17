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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.GenericScimObjectDeserializer;
import com.unboundid.scim2.common.utils.GenericScimObjectSerializer;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
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
 * that case you could still use this object, but BaseScimResourceObject
 * might be a better choice.
 *
 * If you have a BaseScimResourceObject derived object, you can always get a
 * GenericScimResourceObject by serializing The BaseScimResourceObject
 * derived object into a JSON string, and deserializing back to a
 * GenericScimResourceObject.  You could also go the other way.
 *
 * {@link BaseScimResource}
 */
@JsonDeserialize(using = GenericScimObjectDeserializer.class)
@JsonSerialize(using = GenericScimObjectSerializer.class)
public final class GenericScimResource implements ScimResource
{
  private static final Path SCHEMAS = Path.attribute("schemas");
  private static final Path ID = Path.attribute("id");
  private static final Path EXTERNAL_ID = Path.attribute("externalId");
  private static final Path META = Path.attribute("meta");

  private final ObjectNode objectNode;

  /**
   * Create a new empty GenericScimResourceObject.
   */
  public GenericScimResource()
  {
    this.objectNode = JsonNodeFactory.instance.objectNode();
  }

  /**
   * Create a new GenericScimResourceObject backed by an ObjectNode.
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
      return getValue(META, Meta.class);
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
      setValue(META, meta);
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
      return getValue(ID, String.class);
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
      setValue(ID, id);
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
      return getValues(SCHEMAS, String.class);
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
      setValue(SCHEMAS, schemaUrns);
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
      return getValue(EXTERNAL_ID, String.class);
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
      setValue(EXTERNAL_ID, externalId);
    }
    catch (ScimException e)
    {
      // This should never happen.
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieve the value of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to retrieve.
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The generic type parameter of the Java class used to determine
   *            the type to return.
   * @return The value of the attribute specified by the path.
   * @throws JsonProcessingException If the value can not be parsed to the
   *         type specified by the Java class object.
   * @throws ScimException If the path is invalid.
   * @throws IllegalArgumentException If the attribute contains more than one
   *         value, in which case, the getValues method should be used to
   *         retrieve all values.
   */
  public <T> T getValue(final String path, final Class<T> cls)
      throws JsonProcessingException, ScimException, IllegalArgumentException
  {
    return getValue(Path.fromString(path), cls);
  }

  /**
   * Retrieve the value of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to retrieve.
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The generic type parameter of the Java class used to determine
   *            the type to return.
   * @return The value of the attribute specified by the path.
   * @throws JsonProcessingException If the value can not be parsed to the
   *         type specified by the Java class object.
   * @throws ScimException If the path is invalid.
   * @throws IllegalArgumentException If the attribute contains more than one
   *         value, in which case, the getValues method should be used to
   *         retrieve all values.
   */
  public <T> T getValue(final Path path, final Class<T> cls)
      throws JsonProcessingException, ScimException, IllegalArgumentException
  {
    List<JsonNode> nodes = JsonUtils.getValues(path, objectNode);
    if(nodes.isEmpty())
    {
      return null;
    }
    if(nodes.size() > 1)
    {
      throw new IllegalArgumentException("Path references multiple values");
    }
    return SchemaUtils.createSCIMCompatibleMapper().treeToValue(
        nodes.get(0), cls);
  }

  /**
   * Retrieve all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to retrieve.
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The generic type parameter of the Java class used to determine
   *            the type to return.
   * @return The values of the attribute specified by the path.
   * @throws JsonProcessingException If the value can not be parsed to the
   *         type specified by the Java class object.
   * @throws ScimException If the path is invalid.
   */
  public <T> List<T> getValues(final String path, final Class<T> cls)
      throws JsonProcessingException, ScimException
  {
    return getValues(Path.fromString(path), cls);
  }

  /**
   * Retrieve all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to retrieve.
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The generic type parameter of the Java class used to determine
   *            the type to return.
   * @return The values of the attribute specified by the path.
   * @throws JsonProcessingException If the value can not be parsed to the
   *         type specified by the Java class object.
   * @throws ScimException If the path is invalid.
   */
  public <T> List<T> getValues(final Path path, final Class<T> cls)
      throws JsonProcessingException, ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(path, objectNode);
    ArrayList<T> objects = new ArrayList<T>(nodes.size());
    for(JsonNode node : nodes)
    {
      objects.add(
          SchemaUtils.createSCIMCompatibleMapper().treeToValue(node, cls));
    }
    return objects;
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   * @throws ScimException If the path is invalid.
   */
  public void setValue(final String path, final Object object)
      throws ScimException
  {
    setValue(Path.fromString(path), object);
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   * @throws ScimException If the path is invalid.
   */
  public void setValue(final Path path, final Object object)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    JsonUtils.replaceValue(path, objectNode, newObjectNode);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setValues(final String path, final Collection<Object> objects)
      throws ScimException
  {
    setValue(Path.fromString(path), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setValues(final Path path, final Collection<Object> object)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    JsonUtils.replaceValue(path, objectNode, newObjectNode);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setValues(final String path, final Object... objects)
      throws ScimException
  {
    setValue(Path.fromString(path), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setValues(final Path path, final Object... object)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    JsonUtils.replaceValue(path, objectNode, newObjectNode);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addValues(final String path, final Collection<?> objects)
      throws ScimException
  {
    addValues(Path.fromString(path), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addValues(final Path path, final Collection<?> objects)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    JsonUtils.addValue(path, objectNode, newObjectNode);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addValues(final String path, final Object... objects)
      throws ScimException
  {
    addValues(Path.fromString(path), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addValues(final Path path, final Object... objects)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    JsonUtils.addValue(path, objectNode, newObjectNode);
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.
   * @return The number of values removed.
   * @throws ScimException If the path is invalid.
   */
  public int removeValues(final String path)
      throws ScimException
  {
    return removeValues(Path.fromString(path));
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.
   * @return The number of values removed.
   * @throws ScimException If the path is invalid.
   */
  public int removeValues(final Path path)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.removeValues(path, objectNode);
    return nodes.size();
  }
}
