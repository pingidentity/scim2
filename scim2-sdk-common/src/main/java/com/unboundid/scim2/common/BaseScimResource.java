/*
 * Copyright 2015-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.util.Collection;
import java.util.HashMap;
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
 * {@link #getExtensionObjectNode} method or the {@link #getExtensionValues}
 * and {@link #replaceExtensionValue} methods.
 *
 * <p>If you have a BaseScimResource derived object, you can always get a
 * {@link GenericScimResource} by calling {@link #asGenericScimResource()}.
 * You could also go the other way by calling
 * {@link GenericScimResource#getObjectNode()}, followed by
 * {@link JsonUtils#nodeToValue(JsonNode, Class)}.</p>
 * See the {@code GenericScimResource} class-level documentation for more
 * information.
 *
 * @see GenericScimResource
 */
@JsonPropertyOrder({ "schemas", "id", "externalId" })
public abstract class BaseScimResource
    implements ScimResource
{
  /**
   * This field specifies customizable behavior when converting JSON data into
   * subclasses of BaseScimResource.
   * <br><br>
   *
   * By default, the SCIM SDK can throw a {@code JsonMappingException} during
   * the Jackson deserialization process if it is converting an object to a
   * BaseScimResource or one of its subclasses. This occurs if the source JSON
   * contains fields that are not defined on the Java object, and the field is
   * also not a schema extension such as
   * {@code urn:ietf:params:scim:schemas:extension:enterprise:2.0:User}.
   * <br><br>
   *
   * If a SCIM service includes additional non-standard fields in their
   * responses, it can cause these exceptions to be thrown when they are likely
   * undesired. To avoid this problem, this property may be set to {@code true}
   * so that unknown fields are ignored instead of causing exceptions.
   *
   * @since 4.0.0
   */
  public static boolean IGNORE_UNKNOWN_FIELDS = false;

  @Nullable
  private String id;

  @Nullable
  private String externalId;

  @Nullable
  private Meta meta;

  @NotNull
  @JsonProperty("schemas")
  private Set<String> schemaUrns = new LinkedHashSet<>();

  @NotNull
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
  public BaseScimResource(@Nullable final String id)
  {
    this.id = id;
    addClassDefinedUrn();
  }

  /**
   * Gets the {@code ObjectNode} that contains all extension attributes.
   *
   * @return an {@code ObjectNode}.
   */
  @NotNull
  @JsonIgnore
  public ObjectNode getExtensionObjectNode()
  {
    return this.extensionObjectNode;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public Meta getMeta()
  {
    return meta;
  }

  /**
   * {@inheritDoc}
   */
  public void setMeta(@Nullable final Meta meta)
  {
    this.meta = meta;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(@Nullable final String id)
  {
    this.id = id;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getExternalId()
  {
    return externalId;
  }

  /**
   * {@inheritDoc}
   */
  public void setExternalId(@Nullable final String externalId)
  {
    this.externalId = externalId;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Set<String> getSchemaUrns()
  {
    return schemaUrns;
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(@NotNull final Collection<String> schemaUrns)
  {
    Objects.requireNonNull(schemaUrns);
    this.schemaUrns = new LinkedHashSet<>(schemaUrns);
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(@NotNull final String schemaUrn,
                            @Nullable final String... schemaUrns)
  {
    setSchemaUrns(toList(schemaUrn, schemaUrns));
  }

  /**
   * This method is used by Jackson when deserializing JSON data into a Java
   * object. This will be called for schema extensions and any unknown fields
   * (i.e., any fields in the JSON that are not defined in the Java class). If
   * the unknown field is not a schema extension, then an exception will be
   * thrown by default, indicating that the JSON representation of a resource
   * could not be properly mapped to the schema defined by the Java object.
   * <br><br>
   *
   * If it is better for your application to ignore unknown fields instead of
   * throwing exceptions, set the {@link #IGNORE_UNKNOWN_FIELDS} flag to
   * {@code true}. This is helpful when working with SCIM services that include
   * additional non-standard fields in their responses.
   *
   * @param key    The name of the unknown field.
   * @param value  The value of the field.
   *
   * @throws ScimException  If the key is not an extension attribute namespace
   *                        (i.e., the key name doesn't start with "urn:").
   */
  @JsonAnySetter
  protected void setAny(@NotNull final String key,
                        @NotNull final JsonNode value)
      throws ScimException
  {
    if (SchemaUtils.isUrn(key) && value.isObject())
    {
      extensionObjectNode.set(key, value);
    }
    else if (!BaseScimResource.IGNORE_UNKNOWN_FIELDS)
    {
      // If the field is not an extension attribute, then it is an unexpected
      // attribute that cannot be mapped to a field on the Java object.
      String message = "Core attribute " + key +  " is undefined";
      Schema schemaAnnotation = this.getClass().getAnnotation(Schema.class);
      if (schemaAnnotation != null)
      {
        message += " for schema " + schemaAnnotation.id();
      }
      throw BadRequestException.invalidSyntax(message);
    }
  }

  /**
   * This method is used by Jackson when serializing class data into JSON to
   * ensure that any stored schema extensions are included in the JSON output.
   *
   * @return  A map of all extension attributes and their values.
   */
  @JsonAnyGetter
  @NotNull
  protected Map<String, Object> getAny()
  {
    HashMap<String, Object> map = new HashMap<>(extensionObjectNode.size());
    for (Map.Entry<String, JsonNode> field : extensionObjectNode.properties())
    {
      map.put(field.getKey(), field.getValue());
    }
    return map;
  }

  /**
   * Adds the URN of this class to the list of schemas for this object. This is
   * taken from the {@link Schema} annotation of the subclass.
   */
  private void addClassDefinedUrn()
  {
    String urn = SchemaUtils.getSchemaUrn(this.getClass());
    getSchemaUrns().add(urn);
  }

  /**
   * Retrieve all JSON nodes of the extension attribute referenced by the
   * provided path.
   * <br><br>
   *
   * The {@link JsonUtils#nodeToValue(JsonNode, Class)} method may be used to
   * bind the retrieved JSON node into specific value type instances.
   *
   * @param path The path to the attribute whose value to retrieve.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public List<JsonNode> getExtensionValues(@NotNull final String path)
      throws ScimException
  {
    return getExtensionValues(Path.root(path));
  }

  /**
   * Retrieve all JSON nodes of the extension attribute referenced by the
   * provided path.
   * <br><br>
   *
   * The {@link JsonUtils#nodeToValue(JsonNode, Class)} method may be used to
   * bind the retrieved JSON node into specific value type instances.
   *
   * @param path The path to the attribute whose value to retrieve.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public List<JsonNode> getExtensionValues(@NotNull final Path path)
      throws ScimException
  {
    return JsonUtils.findMatchingPaths(path, extensionObjectNode);
  }

  /**
   * Update the value of the extension attribute at the provided path.
   * Equivalent to using the {@link JsonUtils#replaceValue(Path, ObjectNode,
   * JsonNode)} method.
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void replaceExtensionValue(@NotNull final String path,
                                    @NotNull final JsonNode value)
      throws ScimException
  {
    replaceExtensionValue(Path.root(path), value);
  }

  /**
   * Update the value of the extension attribute at the provided path.
   * <br><br>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @throws ScimException If the path is invalid.
   */
  public void replaceExtensionValue(@NotNull final Path path,
                                    @NotNull final JsonNode value)
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
  @Nullable
  @JsonIgnore
  public <T> T getExtension(@NotNull final Class<T> clazz)
  {
    try
    {
      JsonNode extensionNode =
          extensionObjectNode.path(getSchemaUrnOrThrowException(clazz));
      if (extensionNode.isMissingNode())
      {
        return null;
      }
      else
      {
        return JsonUtils.nodeToValue(extensionNode, clazz);
      }
    }
    catch (JsonProcessingException ex)
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
  public <T> void setExtension(@NotNull final T extension)
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
  public <T> boolean removeExtension(@NotNull final Class<T> clazz)
  {
    String schemaUrn = getSchemaUrnOrThrowException(clazz);
    if (extensionObjectNode.remove(schemaUrn) == null)
    {
      return false;
    }
    else
    {
      schemaUrns.remove(schemaUrn);
      return true;
    }
  }

  @NotNull
  private <T> String getSchemaUrnOrThrowException(@NotNull final Class<T> clazz)
  {
    String schemaUrn = SchemaUtils.getSchemaUrn(clazz);
    if (schemaUrn == null)
    {
      throw new IllegalArgumentException(
          "Unable to determine the extension class schema.");
    }
    return schemaUrn;
  }

  /**
   * Add new values for the extension attribute at the provided path.
   * <br><br>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @throws ScimException If the path is invalid.
   *
   * @deprecated  This method adds a schema extension whose value is an array,
   *              which is not used in practice. Additionally, the SCIM SDK does
   *              not support deserialization of JSON with array extensions into
   *              a POJO. If necessary, this behavior can still be achieved by
   *              fetching the ObjectNode with {@link #getExtensionObjectNode()}
   *              and adding the array value manually.
   */
  @Deprecated(since = "4.0.1")
  public void addExtensionValue(@Nullable final String path,
                                @NotNull final ArrayNode values)
      throws ScimException
  {
    addExtensionValue(Path.fromString(path), values);
  }

  /**
   * Add new values to the extension attribute at the provided path.
   * <br><br>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @throws ScimException If the path is invalid.
   *
   * @deprecated  This method adds a schema extension whose value is an array,
   *              which is not used in practice. Additionally, the SCIM SDK does
   *              not support deserialization of JSON with array extensions into
   *              a POJO. If necessary, this behavior can still be achieved by
   *              fetching the ObjectNode with {@link #getExtensionObjectNode()}
   *              and adding the array value manually.
   */
  @Deprecated(since = "4.0.1")
  public void addExtensionValue(@Nullable final Path path,
                                @NotNull final ArrayNode values)
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
  public boolean removeExtensionValues(@Nullable final String path)
      throws ScimException
  {
    return removeExtensionValues(Path.root(path));
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
  public boolean removeExtensionValues(@NotNull final Path path)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.removeValues(path, extensionObjectNode);
    return !nodes.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public GenericScimResource asGenericScimResource()
  {
    ObjectNode object = JsonUtils.valueToNode(this);
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
  public boolean equals(@Nullable final Object o)
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
    if (!schemaUrns.equals(that.schemaUrns))
    {
      return false;
    }
    if (!Objects.equals(id, that.id))
    {
      return false;
    }
    if (!Objects.equals(externalId, that.externalId))
    {
      return false;
    }
    if (!Objects.equals(meta, that.meta))
    {
      return false;
    }
    return Objects.equals(extensionObjectNode, that.extensionObjectNode);
  }

  /**
   * Retrieves a hash code for this BaseScimResource.
   *
   * @return  A hash code for this BaseScimResource.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(schemaUrns, id, externalId, meta, extensionObjectNode);
  }

  /**
   * Retrieves a string representation of this BaseScimResource.
   *
   * @return  A string representation of this BaseScimResource.
   */
  @Override
  @NotNull
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
