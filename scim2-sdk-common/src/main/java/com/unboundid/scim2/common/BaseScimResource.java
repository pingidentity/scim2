/*
 * Copyright 2015-2023 Ping Identity Corporation
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * <p>The base SCIM object.  This object contains all of the
 * attributes required of SCIM objects.</p>
 *
 * <p>BaseScimResource is used when the schema is known ahead of
 * time.  In that case a developer can derive a class from
 * BaseScimResource and annotate the class.  The class should
 * be a Java bean.  This will make it easier to work with the SCIM
 * object since you will just have plain old getters and setters
 * for core attributes. Extension attributes cannot be bound to
 * members of the class but they can still be accessed using the
 * {@link #getExtensionObjectNode} method or the {@link #getExtensionValues},
 * {@link #replaceExtensionValue}, and {@link #addExtensionValue} methods.</p>
 *
 * <p>If you have a BaseScimResource derived object, you can always get a
 * {@link GenericScimResource} by calling {@link #asGenericScimResource()}.
 * You could also go the other way by calling
 * {@link GenericScimResource#getObjectNode()}, followed by
 * {@link JsonUtils#nodeToValue(JsonNode, Class)}.</p>
 *
 * @see GenericScimResource
 */
@JsonPropertyOrder({ "schemas", "id", "externalId" })
public abstract class BaseScimResource
    implements ScimResource
{
  private String id;

  private String externalId;

  private Meta meta;

  @JsonProperty("schemas")
  private Set<String> schemaUrns = new LinkedHashSet<>();

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
   * Gets the {@code ObjectNode} that contains all extension attributes.
   *
   * @return an {@code ObjectNode}.
   */
  @JsonIgnore
  public ObjectNode getExtensionObjectNode()
  {
    return this.extensionObjectNode;
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
    return schemaUrns;
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(final Collection<String> schemaUrns)
  {
    Objects.requireNonNull(schemaUrns);
    this.schemaUrns = new LinkedHashSet<>(schemaUrns);
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(final String schemaUrn, final String... schemaUrns)
  {
    setSchemaUrns(toList(schemaUrn, schemaUrns));
  }

  /**
   * This method is used during json deserialization.  It will be called
   * in the event that a value is given for a field that is not defined
   * in the class.
   *
   * @param key name of the field.
   * @param value value of the field.
   *
   * @throws ScimException if the key is not an extension attribute namespace
   * (the key name doesn't start with "{@code urn:}").
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
   *
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
   * Retrieve all JSON nodes of the extension attribute referenced by the
   * provided path. Equivalent to using the
   * {@link JsonUtils#findMatchingPaths(Path, ObjectNode)}
   * method: JsonUtils.getValues(Path.fromString(path),
   * getExtensionObjectNode()).
   *
   * The {@link JsonUtils#nodeToValue(JsonNode, Class)} method may be used to
   * bind the retrieved JSON node into specific value type instances.
   *
   * @param path The path to the attribute whose value to retrieve.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If the path is invalid.
   */
  public List<JsonNode> getExtensionValues(final String path)
      throws ScimException
  {
    return getExtensionValues(Path.fromString(path));
  }

  /**
   * Retrieve all JSON nodes of the extension attribute referenced by the
   * provided path. Equivalent to using the
   * {@link JsonUtils#findMatchingPaths(Path, ObjectNode)}
   * method: JsonUtils.getValues(path, getExtensionObjectNode()).
   *
   * The {@link JsonUtils#nodeToValue(JsonNode, Class)} method may be used to
   * bind the retrieved JSON node into specific value type instances.
   *
   * @param path The path to the attribute whose value to retrieve.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If the path is invalid.
   */
  public List<JsonNode> getExtensionValues(final Path path)
      throws ScimException
  {
    return JsonUtils.findMatchingPaths(path, extensionObjectNode);
  }

  /**
   * Update the value of the extension attribute at the provided path.
   * Equivalent to using the {@link JsonUtils#replaceValue(Path, ObjectNode,
   * JsonNode)} method: JsonUtils.replaceValues(Path.fromString(path),
   * getExtensionObjectNode(), value).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void replaceExtensionValue(final String path, final JsonNode value)
      throws ScimException
  {
    replaceExtensionValue(Path.fromString(path), value);
  }

  /**
   * Update the value of the extension attribute at the provided path.
   * Equivalent to using the {@link JsonUtils#replaceValue(Path, ObjectNode,
   * JsonNode)} method: JsonUtils.replaceValues(path, getExtensionObjectNode(),
   * value).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void replaceExtensionValue(final Path path, final JsonNode value)
      throws ScimException
  {
    JsonUtils.replaceValue(path, extensionObjectNode, value);
  }

  /**
   * Retrieve a SCIM extension based on the annotations of the class
   * provided.  The returned value will be converted to a POJO of the
   * type specified.
   *
   * @param clazz The class used to determine the type of the object returned
   *              and the schema of the extension.
   * @param <T> the type of object to return.
   *
   * @return The matching extension object, or {@code null} if no extension of
   * that type exists.
   */
  @JsonIgnore
  public <T> T getExtension(final Class<T> clazz)
  {
    try
    {
      JsonNode extensionNode =
          extensionObjectNode.path(getSchemaUrnOrThrowException(clazz));
      if(extensionNode.isMissingNode())
      {
        return null;
      }
      else
      {
        return JsonUtils.nodeToValue(extensionNode, clazz);
      }
    }
    catch(JsonProcessingException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Sets a SCIM extension to the given value based on the annotations
   * of the class provided.  The value will be set for an extension named
   * based on the annotations of the class supplied.
   *
   * @param extension The value to set.  This also is used to determine what
   *              the extension's urn is.
   * @param <T> the type of object.
   */
  @JsonIgnore
  public <T> void setExtension(final T extension)
  {
    String schemaUrn = getSchemaUrnOrThrowException(extension.getClass());
    extensionObjectNode.set(schemaUrn, JsonUtils.valueToNode(extension));
    schemaUrns.add(schemaUrn);
  }

  /**
   * Removes a SCIM extension.  The extension urn is based on the annotations
   * of the class provided.
   *
   * @param clazz the class used to determine the schema urn.
   * @param <T> the type of the class object.
   *
   * @return  true if the extension was removed, or false if the extension
   *          was not present.
   */
  public <T> boolean removeExtension(final Class<T> clazz)
  {
    String schemaUrn = getSchemaUrnOrThrowException(clazz);
    if(extensionObjectNode.remove(schemaUrn) == null)
    {
      return false;
    }
    else
    {
      schemaUrns.remove(schemaUrn);
      return true;
    }
  }

  private <T> String getSchemaUrnOrThrowException(final Class<T> clazz)
  {
    String schemaUrn = SchemaUtils.getSchemaUrn(clazz);
    if(schemaUrn == null)
    {
      throw new IllegalArgumentException(
          "Unable to determine the extension class schema.");
    }
    return schemaUrn;
  }

  /**
   * Add new values for the extension attribute at the provided path. Equivalent
   * to using the {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.addValue(Path.fromString(path), getExtensionObjectNode(),
   * values).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @throws ScimException If the path is invalid.
   */
  public void addExtensionValue(final String path, final ArrayNode values)
      throws ScimException
  {
    addExtensionValue(Path.fromString(path), values);
  }

  /**
   * Add new values to the extension attribute at the provided path. Equivalent
   * to using the {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.addValue(path, getObjectNode(), values).
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @throws ScimException If the path is invalid.
   */
  public void addExtensionValue(final Path path, final ArrayNode values)
      throws ScimException
  {
    JsonUtils.addValue(path, extensionObjectNode, values);

  }

  /**
   * Removes values of the extension attribute at the provided path. Equivalent
   * to using the {@link JsonUtils#removeValues(Path, ObjectNode)} method:
   * JsonUtils.removeValue(Path.fromString(path), getObjectNode(), values).
   *
   * @param path The path to the attribute whose values to remove.
   * @return Whether one or more values where removed.
   * @throws ScimException If the path is invalid.
   */
  public boolean removeExtensionValues(final String path)
      throws ScimException
  {
    return removeExtensionValues(Path.fromString(path));
  }

  /**
   * Removes values of the extension attribute at the provided path. Equivalent
   * to using the {@link JsonUtils#removeValues(Path, ObjectNode)} method:
   * JsonUtils.removeValue(Path.fromString(path), getObjectNode(), values).
   *
   * @param path The path to the attribute whose values to remove.
   * @return Whether one or more values where removed.
   * @throws ScimException If the path is invalid.
   */
  public boolean removeExtensionValues(final Path path)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.removeValues(path, extensionObjectNode);
    return !nodes.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  public GenericScimResource asGenericScimResource()
  {
    ObjectNode object =
        JsonUtils.valueToNode(this);
    return new GenericScimResource(object);
  }

  /**
   * Indicates whether the provided object is equal to this BaseScimResource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this
   *            BaseScimResource, or {@code false} if not.
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
    if (!schemaUrns.equals(that.schemaUrns))
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this BaseScimResource.
   *
   * @return  A hash code for this BaseScimResource.
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
   * Retrieves a string representation of this BaseScimResource.
   *
   * @return  A string representation of this BaseScimResource.
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
