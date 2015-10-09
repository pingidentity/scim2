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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.JsonUtils;

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
 * {@link GenericScimResource}
 */
@JsonPropertyOrder({ "schemas", "id", "externalId" })
public abstract class BaseScimResource
    implements ScimResource
{
  private String id;

  private String externalId;

  private Meta meta;

  @JsonProperty("schemas")
  private Set<String> schemaUrns = new HashSet<String>();

  private final ObjectNode extensionObjectNode =
      JsonUtils.getJsonNodeFactory().objectNode();

  /**
   * Constructs a new BaseScimResource object, and sets the urn if
   * the class extending this one is annotated.
   */
  public BaseScimResource()
  {
    this(null);
  }

  /**
   * Constructs a new BaseScimResource object, and sets the urn if
   * the class extending this one is annotated.
   *
   * @param id The ID fo the object.
   */
  public BaseScimResource(final String id)
  {
    this.id = id;
    addMyUrn();
  }

  /**
   * {@inheritDoc}
   */
  public Meta getMeta()
  {
    return meta;
  }

  /**
   * {@inheritDoc}
   */
  public void setMeta(final Meta meta)
  {
    this.meta = meta;
  }

  /**
   * {@inheritDoc}
   */
  public String getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(final String id)
  {
    this.id = id;
  }

  /**
   * {@inheritDoc}
   */
  public String getExternalId()
  {
    return externalId;
  }

  /**
   * {@inheritDoc}
   */
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
   *
   * @throws ScimException if the key is not an extension attribute namespace.
   */
  @JsonAnySetter
  protected void setAny(final String key,
                        final JsonNode value)
      throws ScimException
  {
    if(SchemaUtils.isUrn(key) && value.isObject())
    {
      extensionObjectNode.set(key, value);
    }
    else
    {
      String message = "Core attribute " + key +  " is undefined";
      Schema schemaAnnotation = this.getClass().getAnnotation(Schema.class);
      if(schemaAnnotation != null)
      {
        message += " for schema " + schemaAnnotation.id();
      }
      throw BadRequestException.invalidSyntax(message);
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
    return JsonUtils.getObjectReader().treeToValue(nodes.get(0), cls);
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
      objects.add(JsonUtils.getObjectReader().treeToValue(node, cls));
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
        JsonUtils.valueToTree(object);
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
        JsonUtils.valueToTree(object);
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
        JsonUtils.valueToTree(object);
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
        JsonUtils.valueToTree(objects);
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
        JsonUtils.valueToTree(objects);
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

  /**
   * {@inheritDoc}
   */
  public GenericScimResource asGenericScimResource()
  {
    ObjectNode object =
        JsonUtils.valueToTree(this);
    return new GenericScimResource(object);
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

    BaseScimResource that = (BaseScimResource) o;

    if (extensionObjectNode != null ?
        !extensionObjectNode.equals(that.extensionObjectNode) :
        that.extensionObjectNode != null)
    {
      return false;
    }
    if (externalId != null ? !externalId.equals(that.externalId) :
        that.externalId != null)
    {
      return false;
    }
    if (id != null ? !id.equals(that.id) : that.id != null)
    {
      return false;
    }
    if (meta != null ? !meta.equals(that.meta) : that.meta != null)
    {
      return false;
    }
    if (schemaUrns != null ? !schemaUrns.equals(that.schemaUrns) :
        that.schemaUrns != null)
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
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (externalId != null ? externalId.hashCode() : 0);
    result = 31 * result + (meta != null ? meta.hashCode() : 0);
    result = 31 * result + (schemaUrns != null ? schemaUrns.hashCode() : 0);
    result = 31 * result + (extensionObjectNode != null ?
        extensionObjectNode.hashCode() : 0);
    return result;
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
