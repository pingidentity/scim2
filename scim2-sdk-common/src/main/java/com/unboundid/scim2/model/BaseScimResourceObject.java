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

package com.unboundid.scim2.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.ScimException;
import com.unboundid.scim2.schema.SchemaUtils;
import com.unboundid.scim2.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The base SCIM object.  This object contains all of the
 * attributes required of SCIM objects.
 *
 *
 * BaseScimResourceObject is used when the schema is known ahead of
 * time.  In that case a developer can derive a class from
 * BaseScimResourceObject and annotate the class.  The class should
 * be a java bean.  This will make it easier to work with the SCIM
 * object since you will just have plain old getters and setters.
 *
 *
 * If you have a BaseScimResourceObject derived object, you can always get a
 * GenericScimResourceObject by serializing The BaseScimResourceObject
 * derived object into a JSON string, and deserializing back to a
 * GenericScimResourceObject.  You could also go the other way.
 *
 * {@link com.unboundid.scim2.model.GenericScimResourceObject}
 */
public class BaseScimResourceObject
    implements ScimResource
{
  private Meta meta;

  private String id;

  private String externalId;

  @JsonProperty("schemas")
  private Set<String> schemaUrns = new HashSet<String>();

  private final ObjectNode extensionObjectNode =
      JsonNodeFactory.instance.objectNode();

  /**
   * Constructs a new BaseScimResource object, and sets the urn if
   * the class extending this one is annotated.
   */
  public BaseScimResourceObject()
  {
    addMyUrn();
  }

  /**
   * Constructs a base scim resource object with the given
   * schema urn.
   *
   * @param schemaUrn urn of the schema for this object.
   */
  public BaseScimResourceObject(final String schemaUrn)
  {
    getSchemaUrns().add(schemaUrn);
  }

  /**
   * Gets metadata about the object.
   * @return <code>Meta</code> containing metadata about the object.
   */
  @Override
  public Meta getMeta()
  {
    return meta;
  }

  /**
   * Sets metadata for the object.
   * @param meta <code>Meta</code> containing metadata for the object.
   */
  @Override
  public void setMeta(final Meta meta)
  {
    this.meta = meta;
  }

  /**
   * Gets the id of the object.
   * @return the id of the object.
   */
  @Override
  public String getId()
  {
    return id;
  }

  /**
   * Sets the id of the object.
   * @param id The object's id.
   */
  @Override
  public void setId(final String id)
  {
    this.id = id;
  }

  /**
   * Gets the objects external id.
   * @return The external id of the object.
   */
  @Override
  public String getExternalId()
  {
    return externalId;
  }

  /**
   * Sets the object's external id.
   * @param externalId The external id of the object.
   */
  @Override
  public void setExternalId(final String externalId)
  {
    this.externalId = externalId;
  }

  /**
   * {@inheritDoc}
   */
  public Set<String> getSchemaUrns()
  {
    if(schemaUrns == null)
    {
      schemaUrns = new HashSet<String>();
    }

    return schemaUrns;
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(final Collection<String> schemaUrns)
  {
    this.schemaUrns = new HashSet<String>(schemaUrns);
  }

  /**
   * This method is used during json deserialization.  It will be called
   * in the event that a value is given for an field that is not defined
   * in the class.
   *
   * @param key name of the field.
   * @param value value of the field.
   */
  @JsonAnySetter
  protected void setAny(final String key,
                        final JsonNode value)
  {
    if(SchemaUtils.isUrn(key) && value.isObject())
    {
      extensionObjectNode.set(key, value);
    }
  }

  /**
   * Used to get values that were deserialized from json where there was
   * no matching field in the class.
   * @return the value of the field.
   */
  @JsonAnyGetter
  protected Map<String, Object> getAny()
  {
    HashMap<String, Object> map =
        new HashMap<String, Object>(extensionObjectNode.size());
    Iterator<Map.Entry<String, JsonNode>> i = extensionObjectNode.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      map.put(field.getKey(), field.getValue());
    }
    return map;
  }

  /**
   * Adds the urn of this class to the list of schemas for this object.
   * This is taken from the schema annotation of a class that extends
   * this class.  If the class has no schema annotation, no schema urn
   * will be added.
   */
  private void addMyUrn()
  {
    String mySchema = SchemaUtils.getSchemaUrn(this.getClass());
    if((mySchema != null) && (!mySchema.isEmpty()))
    {
      getSchemaUrns().add(mySchema);
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
  public <T> T getExtensionValue(final String path, final Class<T> cls)
      throws JsonProcessingException, ScimException, IllegalArgumentException
  {
    return getExtensionValue(Path.fromString(path), cls);
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
  public <T> T getExtensionValue(final Path path, final Class<T> cls)
      throws JsonProcessingException, ScimException, IllegalArgumentException
  {
    List<JsonNode> nodes = JsonUtils.getValues(path, extensionObjectNode);
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
  public <T> List<T> getExtensionValues(final String path, final Class<T> cls)
      throws JsonProcessingException, ScimException
  {
    return getExtensionValues(Path.fromString(path), cls);
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
  public <T> List<T> getExtensionValues(final Path path, final Class<T> cls)
      throws JsonProcessingException, ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(path, extensionObjectNode);
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
  public void setExtensionValue(final String path, final Object object)
      throws ScimException
  {
    setExtensionValue(Path.fromString(path), object);
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   * @throws ScimException If the path is invalid.
   */
  public void setExtensionValue(final Path path, final Object object)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    JsonUtils.replaceValue(path, extensionObjectNode, newObjectNode);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setExtensionValues(final String path,
                                 final Collection<Object> objects)
      throws ScimException
  {
    setExtensionValues(Path.fromString(path), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setExtensionValues(final Path path,
                                 final Collection<Object> object)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    JsonUtils.replaceValue(path, extensionObjectNode, newObjectNode);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setExtensionValues(final String path, final Object... objects)
      throws ScimException
  {
    setExtensionValues(Path.fromString(path), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void setExtensionValues(final Path path, final Object... object)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(object);
    JsonUtils.replaceValue(path, extensionObjectNode, newObjectNode);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addExtensionValues(final String path, final Collection<?> objects)
      throws ScimException
  {
    addExtensionValues(Path.fromString(path), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addExtensionValues(final Path path, final Collection<?> objects)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    JsonUtils.addValue(path, extensionObjectNode, newObjectNode);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addExtensionValues(final String path, final Object... objects)
      throws ScimException
  {
    addExtensionValues(Path.fromString(path), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   * @throws ScimException If the path is invalid.
   */
  public void addExtensionValues(final Path path, final Object... objects)
      throws ScimException
  {
    JsonNode newObjectNode =
        SchemaUtils.createSCIMCompatibleMapper().valueToTree(objects);
    JsonUtils.addValue(path, extensionObjectNode, newObjectNode);
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.
   * @return The number of values removed.
   * @throws ScimException If the path is invalid.
   */
  public int removeExtensionValues(final String path)
      throws ScimException
  {
    return removeExtensionValues(Path.fromString(path));
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.
   * @return The number of values removed.
   * @throws ScimException If the path is invalid.
   */
  public int removeExtensionValues(final Path path)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.removeValues(path, extensionObjectNode);
    return nodes.size();
  }
}
