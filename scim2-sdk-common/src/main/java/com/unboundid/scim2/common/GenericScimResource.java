/*
 * Copyright 2015-2024 Ping Identity Corporation
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
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.exceptions.ServerErrorException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.GenericScimObjectDeserializer;
import com.unboundid.scim2.common.utils.GenericScimObjectSerializer;
import com.unboundid.scim2.common.utils.JsonUtils;
import static com.unboundid.scim2.common.utils.StaticUtils.toList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * <p>A generic SCIM object.  This object can be used if you have no
 * Java object representing the SCIM object being returned.</p>
 *
 * <p>This object can be used when the exact structure of the SCIM object
 * that will be received as JSON text is not known.  This will provide
 * methods that can read attributes from those objects without needing
 * to know the schema ahead of time.  Another way to work with SCIM
 * objects is when you know ahead of time what the schema will be.  In
 * that case you could still use this object, but {@link BaseScimResource}
 * might be a better choice.</p>
 *
 * <p>If you have a BaseScimResource derived object, you can always get a
 * {@link GenericScimResource} by calling {@link #asGenericScimResource()}.
 * You could also go the other way by calling
 * {@link GenericScimResource#getObjectNode()}, followed by
 * {@link JsonUtils#nodeToValue(JsonNode, Class)}.</p>
 *
 * @see BaseScimResource
 */
@JsonDeserialize(using = GenericScimObjectDeserializer.class)
@JsonSerialize(using = GenericScimObjectSerializer.class)
public final class GenericScimResource implements ScimResource
{
  @NotNull
  private static final Path SCHEMAS = Path.root().attribute("schemas");

  @NotNull
  private static final Path ID = Path.root().attribute("id");

  @NotNull
  private static final Path EXTERNAL_ID = Path.root().attribute("externalId");

  @NotNull
  private static final Path META = Path.root().attribute("meta");

  @NotNull
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
  public GenericScimResource(@NotNull final ObjectNode objectNode)
  {
    this.objectNode = objectNode;
  }

  /**
   * Gets the {@code ObjectNode} that backs this object.
   *
   * @return an {@code ObjectNode} representing this generic SCIM resource.
   */
  @NotNull
  public ObjectNode getObjectNode()
  {
    return this.objectNode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public Meta getMeta()
  {
    try
    {
      List<JsonNode> values = JsonUtils.findMatchingPaths(META, objectNode);
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
  public void setMeta(@Nullable final Meta meta)
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
  @Nullable
  public String getId()
  {
    try
    {
      JsonNode value = JsonUtils.getValue(ID, objectNode);
      if(value.isNull())
      {
        return null;
      }
      return JsonUtils.nodeToValue(value, String.class);
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
  public void setId(@Nullable final String id)
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
  @NotNull
  public List<String> getSchemaUrns()
  {
    try
    {
      JsonNode value = JsonUtils.getValue(SCHEMAS, objectNode);
      if(value.isNull() || !value.isArray())
      {
        return Collections.emptyList();
      }
      return JsonUtils.nodeToValues((ArrayNode) value, String.class);
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
  public void setSchemaUrns(@NotNull final Collection<String> schemaUrns)
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
  public void setSchemaUrns(@NotNull final String schemaUrn,
                            @Nullable final String... schemaUrns)
  {
    setSchemaUrns(toList(schemaUrn, schemaUrns));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public String getExternalId()
  {
    try
    {
      JsonNode value = JsonUtils.getValue(EXTERNAL_ID, objectNode);
      if(value.isNull())
      {
        return null;
      }
      return JsonUtils.nodeToValue(value, String.class);
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
  public void setExternalId(@Nullable final String externalId)
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
   * Gets a single JsonNode from a generic SCIM resource.  This value may
   * be an ArrayNode.
   *   <p>
   *
   * For example:
   * With a generic SCIM resource representing the folowing JSON:
   * <pre><code>
   *   {
   *     "name" : "Bob",
   *     "friends" : [ "Amy", "Beth", "Carol" ]
   *   }
   * </code></pre>
   * <p>
   * gsr.getValue("name");
   * would return a TextNode containing "Bob"
   *   <p>
   *
   * gsr.getValue("friends");
   * would return an ArrayNode containing 3 TextNodes with the values
   * "Amy", "Beth", and "Carol"
   *
   * @param path the String path of the object.
   * @return the JsonNode at the path, or a NullNode if nothing is found
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public JsonNode getValue(@NotNull final String path)
      throws ScimException
  {
    return getValue(Path.fromString(path));
  }

  /**
   * Gets a single JsonNode from a generic SCIM resource.  This value may
   * be an ArrayNode.
   *   <p>
   *
   * For example:
   * With a generic SCIM resource representing the folowing JSON:
   * <pre><code>
   *   {
   *     "name" : "Bob",
   *     "friends" : [ "Amy", "Beth", "Carol" ]
   *   }
   * </code></pre>
   *   <p>
   *
   * gsr.getValue("name");
   * would return a TextNode containing "Bob"
   *   <p>
   *
   * gsr.getValue("friends");
   * would return an ArrayNode containing 3 TextNodes with the values
   * "Amy", "Beth", and "Carol"
   *
   * @param path the path of the object.
   * @return the JsonNode at the path, or a NullNode if nothing is found
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public JsonNode getValue(@NotNull final Path path)
      throws ScimException
  {
    return JsonUtils.getValue(path, objectNode);
  }

  /**
   * Update the value at the provided path. Equivalent to using the
   * {@link JsonUtils#replaceValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.replaceValues(Path.fromString(path), getObjectNode(), value).
   *   <p>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *   <p>
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @return This object.
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final JsonNode value)
      throws ScimException
  {
    replaceValue(Path.fromString(path), value);
    return this;
  }

  /**
   * Update the value at the provided path. Equivalent to using the
   * {@link JsonUtils#replaceValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.replaceValues(path, getObjectNode(), value).
   *   <p>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *   <p>
   *
   * @param path The path to the attribute whose value to set.
   * @param value The value(s) to set.
   * @return This object.
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final JsonNode value)
      throws ScimException
  {
    JsonUtils.replaceValue(path, objectNode, value);
    return this;
  }

  /**
   * Add new values at the provided path. Equivalent to using the
   * {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.addValue(Path.fromString(path), getObjectNode(), values).
   *   <p>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *   <p>
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @return This object.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public GenericScimResource addValues(@NotNull final String path,
                                       @NotNull final ArrayNode values)
      throws ScimException
  {
    addValues(Path.fromString(path), values);
    return this;
  }

  /**
   * Add new values at the provided path. Equivalent to using the
   * {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method:
   * JsonUtils.addValue(path, getObjectNode(), values).
   *   <p>
   *
   * The {@link JsonUtils#valueToNode(Object)} method may be used to convert
   * the given value instance to a JSON node.
   *   <p>
   *
   * @param path The path to the attribute whose values to add.
   * @param values The value(s) to add.
   * @return This object.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public GenericScimResource addValues(@NotNull final Path path,
                                       @NotNull final ArrayNode values)
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
   * @return Whether one or more values were removed.
   * @throws ScimException If the path is invalid.
   */
  public boolean removeValues(@NotNull final String path)
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
   * @return Whether one or more values were removed.
   * @throws ScimException If the path is invalid.
   */
  public boolean removeValues(@NotNull final Path path)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.removeValues(path, objectNode);
    return !nodes.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public GenericScimResource asGenericScimResource()
  {
    return this;
  }

  /**
   * Retrieves a string representation of this generic SCIM resource.
   *
   * @return  A string representation of this generic SCIM resource.
   * @throws RuntimeException   If the resource cannot be parsed as a valid JSON
   *                            object.
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

  /**
   * Indicates whether the provided object is equal to this generic SCIM
   * resource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this generic
   *            SCIM resource, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (!(o instanceof GenericScimResource))
    {
      return false;
    }

    ObjectNode otherNode = ((GenericScimResource) o).getObjectNode();
    if (objectNode == null)
    {
      return (otherNode == null);
    }

    return objectNode.equals(otherNode);
  }

  /**
   * Retrieves a hash code for this generic SCIM resource.
   *
   * @return  A hash code for this generic SCIM resource.
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(objectNode);
  }


  /////////////////////////////////////
  // SCIM String methods
  /////////////////////////////////////
  /**
   * Adds or replaces a String value in a generic SCIM resource.
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"stringValue"
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a String
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a String
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final String value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a String value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"stringValue"
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a String
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a String
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final String value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().textNode(value));
  }

  /**
   * Adds String values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ "stringValue1", "stringValue2" ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addStringValues("path1", path1values)
   *   where path1Value is a List of String.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addStringValues("path2", path2values)
   *   where path2values is a List of String.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addStringValues(@NotNull final String path,
                                             @NotNull final List<String> values)
      throws ScimException
  {
    return addStringValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addStringValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addStringValues(@NotNull final String path,
                                             @NotNull final String value1,
                                             @Nullable final String... values)
      throws ScimException
  {
    return addStringValues(path, toList(value1, values));
  }

  /**
   * Adds String values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ "stringValue1", "stringValue2" ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addStringValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of String.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addStringValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of String.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addStringValues(@NotNull final Path path,
                                             @NotNull final List<String> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(String value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addStringValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addStringValues(@NotNull final Path path,
                                             @NotNull final String value1,
                                             @Nullable final String... values)
      throws ScimException
  {
    return addStringValues(path, toList(value1, values));
  }

  /**
   * Gets a String value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a String.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"stringValue1"
   *   }
   * </code></pre>
   *   <p>
   *
   *   getStringValue("path1")
   *   returns "stringValue1"
   *   <p>
   *
   *   getStringValue("bogusPath")
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public String getStringValue(@NotNull final String path)
      throws ScimException
  {
    return getStringValue(Path.fromString(path));
  }

  /**
   * Gets a String value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a String.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"stringValue1"
   *   }
   * </code></pre>
   *   <p>
   *
   *   getStringValue(Path.fromString("path1"))
   *   returns Stringexample
   *   <p>
   *
   *   getStringValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public String getStringValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.textValue();
  }

  /**
   * Gets a list of String from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of String.  If the path does
   * not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1": ["stringValue1", "stringValue2"]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getStringValueList("path1")
   *   returns a list containing "stringValue1", "stringValue2"
   *   <p>
   *
   *   getStringValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<String> getStringValueList(@NotNull final String path)
      throws ScimException
  {
    return getStringValueList(Path.fromString(path));
  }

  /**
   * Gets a list of String from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of String.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":["stringValue1", "stringValue2"]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getStringValueList(Path.fromString("path1"))
   *   returns a list containing "stringValue1", "stringValue2"
   *   <p>
   *
   *   getStringValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<String> getStringValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<String> values = new ArrayList<>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      values.add(iterator.next().textValue());
    }
    return values;
  }

  /////////////////////////////////////
  // SCIM Boolean methods
  /////////////////////////////////////
  /**
   * Adds or replaces a Boolean value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":true
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a Boolean
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a Boolean
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Boolean value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a Boolean value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":true
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a Boolean
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a Boolean
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *   <p>
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Boolean value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().booleanNode(value));
  }

  /**
   * Gets a Boolean value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Boolean.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":true
   *   }
   * </code></pre>
   *   <p>
   *
   *   getBooleanValue("path1")
   *   returns true
   *   <p>
   *
   *   getBooleanValue("bogusPath")
   *   returns null
   *   <p>
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Boolean getBooleanValue(@NotNull final String path)
      throws ScimException
  {
    return getBooleanValue(Path.fromString(path));
  }

  /**
   * Gets a Boolean value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Boolean.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":true
   *   }
   * </code></pre>
   *   <p>
   *
   *   getBooleanValue(Path.fromString("path1"))
   *   returns true
   *   <p>
   *
   *   getBooleanValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Boolean getBooleanValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.booleanValue();
  }

  /////////////////////////////////////
  // SCIM Decimal methods
  /////////////////////////////////////
  /**
   * Adds or replaces a Double value in a generic SCIM resource.
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":2.0
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a Double
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a Double
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Double value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a Double value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":2.0
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a Double
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a Double
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Double value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().numberNode(value));
  }

  /**
   * Adds Double values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ 2.1, 2.2 ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addDoubleValues("path1", path1values)
   *   where path1Value is a List of Double.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addDoubleValues("path2", path2values)
   *   where path2values is a List of Double.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addDoubleValues(@NotNull final String path,
                                             @NotNull final List<Double> values)
      throws ScimException
  {
    return addDoubleValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addDoubleValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDoubleValues(@NotNull final String path,
                                             @NotNull final Double value1,
                                             @Nullable final Double... values)
      throws ScimException
  {
    return addDoubleValues(path, toList(value1, values));
  }

  /**
   * Adds Double values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ 2.1, 2.2 ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addDoubleValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of Double.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addDoubleValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of Double.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addDoubleValues(@NotNull final Path path,
                                             @NotNull final List<Double> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Double value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addDoubleValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDoubleValues(@NotNull final Path path,
                                             @NotNull final Double value1,
                                             @Nullable final Double... values)
      throws ScimException
  {
    return addDoubleValues(path, toList(value1, values));
  }

  /**
   * Gets a Double value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Double.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":2.0
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDoubleValue("path1")
   *   returns 2.0
   *   <p>
   *
   *   getDoubleValue("bogusPath")
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Double getDoubleValue(@NotNull final String path)
      throws ScimException
  {
    return getDoubleValue(Path.fromString(path));
  }

  /**
   * Gets a Double value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Double.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":2.0
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDoubleValue(Path.fromString("path1"))
   *   returns 2.0
   *   <p>
   *
   *   getDoubleValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Double getDoubleValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.doubleValue();
  }

  /**
   * Gets a list of Double from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Double.  If the path does
   * not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":[2.1, 2.2]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDoubleValueList("path1")
   *   returns a list containing 2.1, 2.2
   *   <p>
   *
   *   getDoubleValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Double> getDoubleValueList(@NotNull final String path)
      throws ScimException
  {
    return getDoubleValueList(Path.fromString(path));
  }

  /**
   * Gets a list of Double from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Double.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":[2.1, 2.2]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDoubleValueList(Path.fromString("path1"))
   *   returns a list containing 2.1, 2.2
   *   <p>
   *
   *   getDoubleValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Double> getDoubleValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Double> values = new ArrayList<Double>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      values.add(iterator.next().doubleValue());
    }
    return values;
  }

  /////////////////////////////////////
  // SCIM Integer methods
  /////////////////////////////////////
  /**
   * Adds or replaces an Integer value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":7
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is an Integer
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is an Integer
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Integer value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces an Integer value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":7
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is an Integer
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is an Integer
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Integer value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().numberNode(value));
  }

  /**
   * Adds Integer values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ 11, 13 ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addIntegerValues("path1", path1values)
   *   where path1Value is a List of Integer.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addIntegerValues("path2", path2values)
   *   where path2values is a List of Integer.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addIntegerValues(
      @NotNull final String path,
      @NotNull final List<Integer> values)
          throws ScimException
  {
    return addIntegerValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addIntegerValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addIntegerValues(@NotNull final String path,
                                              @NotNull final Integer value1,
                                              @Nullable final Integer... values)
      throws ScimException
  {
    return addIntegerValues(path, toList(value1, values));
  }

  /**
   * Adds Integer values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ 11, 13 ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addIntegerValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of Integer.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addIntegerValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of Integer.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addIntegerValues(
      @NotNull final Path path,
      @NotNull final List<Integer> values)
          throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Integer value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addIntegerValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addIntegerValues(@NotNull final Path path,
                                              @NotNull final Integer value1,
                                              @Nullable final Integer... values)
      throws ScimException
  {
    return addIntegerValues(path, toList(value1, values));
  }

  /**
   * Gets an Integer value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be an Integer.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":7
   *   }
   * </code></pre>
   *   <p>
   *
   *   getIntegerValue("path1")
   *   returns 7
   *   <p>
   *
   *   getIntegerValue("bogusPath")
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Integer getIntegerValue(@NotNull final String path)
      throws ScimException
  {
    return getIntegerValue(Path.fromString(path));
  }

  /**
   * Gets an Integer value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be an Integer.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":7
   *   }
   * </code></pre>
   *   <p>
   *
   *   getIntegerValue(Path.fromString("path1"))
   *   returns 7
   *   <p>
   *
   *   getIntegerValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Integer getIntegerValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.intValue();
  }

  /**
   * Gets a list of Integer from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Integer.  If the path does
   * not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":[11, 13]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getIntegerValueList("path1")
   *   returns a list containing 11, 13
   *   <p>
   *
   *   getIntegerValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Integer> getIntegerValueList(@NotNull final String path)
      throws ScimException
  {
    return getIntegerValueList(Path.fromString(path));
  }

  /**
   * Gets a list of Integer from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Integer.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":[11, 13]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getIntegerValueList(Path.fromString("path1"))
   *   returns a list containing 11, 13
   *   <p>
   *
   *   getIntegerValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Integer> getIntegerValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Integer> values = new ArrayList<Integer>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      values.add(iterator.next().intValue());
    }
    return values;
  }

  /////////////////////////////////////
  // SCIM Long methods
  /////////////////////////////////////
  /**
   * Adds or replaces a Long value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":7
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a Long
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a Long
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Long value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a Long value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":7
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a Long
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a Long
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Long value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().numberNode(value));
  }

  /**
   * Adds Long values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ 11, 13 ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addLongValues("path1", path1values)
   *   where path1Value is a List of Long.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addLongValues("path2", path2values)
   *   where path2values is a List of Long.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addLongValues(@NotNull final String path,
                                           @NotNull final List<Long> values)
      throws ScimException
  {
    return addLongValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addLongValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addLongValues(@NotNull final String path,
                                           @NotNull final Long value1,
                                           @Nullable final Long... values)
      throws ScimException
  {
    return addLongValues(path, toList(value1, values));
  }

  /**
   * Adds Long values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ 11, 13 ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addLongValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of Long.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addLongValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of Long.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addLongValues(@NotNull final Path path,
                                           @NotNull final List<Long> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(Long value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addLongValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addLongValues(@NotNull final Path path,
                                           @NotNull final Long value1,
                                           @Nullable final Long... values)
      throws ScimException
  {
    return addLongValues(path, toList(value1, values));
  }

  /**
   * Gets a Long value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Long.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":7
   *   }
   * </code></pre>
   *   <p>
   *
   *   getLongValue("path1")
   *   returns 7
   *   <p>
   *
   *   getLongValue("bogusPath")
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Long getLongValue(@NotNull final String path)
      throws ScimException
  {
    return getLongValue(Path.fromString(path));
  }

  /**
   * Gets a Long value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Long.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":7
   *   }
   * </code></pre>
   *   <p>
   *
   *   getLongValue(Path.fromString("path1"))
   *   returns 7
   *   <p>
   *
   *   getLongValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Long getLongValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.longValue();
  }

  /**
   * Gets a list of Long from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Long.  If the path does
   * not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":[11, 13]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getLongValueList("path1")
   *   returns a list containing 11, 13
   *   <p>
   *
   *   getLongValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Long> getLongValueList(@NotNull final String path)
      throws ScimException
  {
    return getLongValueList(Path.fromString(path));
  }

  /**
   * Gets a list of Long from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Long.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":[11, 13]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getLongValueList(Path.fromString("path1"))
   *   returns a list containing 11, 13
   *   <p>
   *
   *   getLongValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Long> getLongValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Long> values = new ArrayList<Long>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      values.add(iterator.next().longValue());
    }
    return values;
  }

  /////////////////////////////////////
  // SCIM Date/Time methods
  /////////////////////////////////////
  /**
   * Adds or replaces a Date value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"1970-04-20T17:54:47.542Z"
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a Date
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a Date
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Date value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a Date value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"1970-04-20T17:54:47.542Z"
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a Date
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a Date
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Date value)
      throws ScimException
  {
    return replaceValue(path, GenericScimResource.getDateJsonNode(value));
  }

  /**
   * Adds Date values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ "1970-04-20T17:54:47.542Z", "2000-04-20T17:54:47.542Z" ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addDateValues("path1", path1values)
   *   where path1Value is a List of Date.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addDateValues("path2", path2values)
   *   where path2values is a List of Date.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addDateValues(@NotNull final String path,
                                           @NotNull final List<Date> values)
      throws ScimException
  {
    return addDateValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addDateValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDateValues(@NotNull final String path,
                                           @NotNull final Date value1,
                                           @Nullable final Date... values)
      throws ScimException
  {
    return addDateValues(path, toList(value1, values));
  }

  /**
   * Adds Date values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ "1970-04-20T17:54:47.542Z", "2000-04-20T17:54:47.542Z" ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addDateValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of Date.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addDateValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of Date.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addDateValues(@NotNull final Path path,
                                           @NotNull final List<Date> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Date value : values)
    {
      valuesArrayNode.add(GenericScimResource.getDateJsonNode(value));
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addDateValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDateValues(@NotNull final Path path,
                                           @NotNull final Date value1,
                                           @Nullable final Date... values)
      throws ScimException
  {
    return addDateValues(path, toList(value1, values));
  }

  /**
   * Gets a Date value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Date.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"1970-04-20T17:54:47.542Z"
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDateValue("path1")
   *   returns a Date representing "1970-04-20T17:54:47.542Z"
   *   <p>
   *
   *   getDateValue("bogusPath")
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Date getDateValue(@NotNull final String path)
      throws ScimException
  {
    return getDateValue(Path.fromString(path));
  }

  /**
   * Gets a Date value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a Date.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"1970-04-20T17:54:47.542Z"
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDateValue(Path.fromString("path1"))
   *   returns a Date representing "1970-04-20T17:54:47.542Z"
   *   <p>
   *
   *   getDateValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public Date getDateValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    if(jsonNode.isNull())
    {
      return null;
    }
    return getDateFromJsonNode(jsonNode);
  }

  /**
   * Gets a list of Date from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Date.  If the path does
   * not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":["1970-04-20T17:54:47.542Z", "2000-04-20T17:54:47.542Z"]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDateValueList("path1")
   *   returns a list containing dates representing
   *       "1970-04-20T17:54:47.542Z", "2000-04-20T17:54:47.542Z"
   *   <p>
   *
   *   getDateValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Date> getDateValueList(@NotNull final String path)
      throws ScimException
  {
    return getDateValueList(Path.fromString(path));
  }

  /**
   * Gets a list of Date from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of Date.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":["1970-04-20T17:54:47.542Z", "2000-04-20T17:54:47.542Z"]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getDateValueList(Path.fromString("path1"))
   *   returns a list containing dates representing
   *       "1970-04-20T17:54:47.542Z", "2000-04-20T17:54:47.542Z"
   *   <p>
   *
   *   getDateValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<Date> getDateValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Date> values = new ArrayList<Date>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      values.add(GenericScimResource.getDateFromJsonNode(iterator.next()));
    }
    return values;
  }

  /**
   * Gets a JsonNode that represents the supplied date.
   *
   * @param date the date to represent as a JsonNode.
   * @return the JsonNode representing the date.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public static TextNode getDateJsonNode(@Nullable final Date date)
      throws ScimException
  {
    return JsonUtils.valueToNode(date);
  }

  /**
   * Gets the date represented by the supplied JsonNode.
   *
   * @param node the JsonNode representing the date.
   * @return the date represented by the JsonNode.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public static Date getDateFromJsonNode(@NotNull final JsonNode node)
      throws ScimException
  {
    try
    {
      return JsonUtils.getObjectReader().forType(Date.class).readValue(node);
    }
    catch(JsonProcessingException ex)
    {
      throw new ServerErrorException(ex.getMessage());
    }
    catch(IOException ex)
    {
      throw new ServerErrorException(ex.getMessage());
    }
  }

  /////////////////////////////////////
  // SCIM Binary methods
  /////////////////////////////////////
  /**
   * Adds or replaces a binary value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"AjIzLg=="
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a byte[]
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a byte[]
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final byte[] value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a binary value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"AjIzLg=="
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a byte[]
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a byte[]
   *   would add a field called "path2" with the value of the path2value
   *   variabl
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final byte[] value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().binaryNode(value));
  }

  /**
   * Adds binary values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ "AjIzLg==", "AjNjLp==" ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addBinaryValues("path1", path1values)
   *   where path1Value is a List of byte[].
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addBinaryValues("path2", path2values)
   *   where path2values is a List of byte[].
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addBinaryValues(@NotNull final String path,
                                             @NotNull final List<byte[]> values)
      throws ScimException
  {
    return addBinaryValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addBinaryValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addBinaryValues(@NotNull final String path,
                                             @NotNull final byte[] value1,
                                             @Nullable final byte[]... values)
      throws ScimException
  {
    return addBinaryValues(path, toList(value1, values));
  }

  /**
   * Adds binary values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":[ "AjIzLg==", "AjNjLp==" ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addBinaryValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of byte[].
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addBinaryValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of byte[].
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addBinaryValues(@NotNull final Path path,
                                             @NotNull final List<byte[]> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (byte[] value : values)
    {
      valuesArrayNode.add(JsonUtils.getJsonNodeFactory().binaryNode(value));
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addBinaryValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addBinaryValues(@NotNull final Path path,
                                             @NotNull final byte[] value1,
                                             @Nullable final byte[]... values)
      throws ScimException
  {
    return addBinaryValues(path, toList(value1, values));
  }

  /**
   * Gets a binary value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a binary value.  If the path does not
   * exist, "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"AjIzLg=="
   *   }
   * </code></pre>
   *   <p>
   *
   *   getBinaryValue("path1")
   *   returns the byte array decoded from "AjIzLg=="
   *   <p>
   *
   *   getBinaryValue("bogusPath")
   *   returns null
   *   <p>
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public byte[] getBinaryValue(@NotNull final String path)
      throws ScimException
  {
    return getBinaryValue(Path.fromString(path));
  }

  /**
   * Gets a binary value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a binary value.  If the path does
   * not exist, "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"AjIzLg=="
   *   }
   * </code></pre>
   *   <p>
   *
   *   getBinaryValue(Path.fromString("path1"))
   *   returns the byte array decoded from "AjIzLg=="
   *   <p>
   *
   *   getBinaryValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public byte[] getBinaryValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    if(jsonNode.isNull())
    {
      return null;
    }

    try
    {
      return jsonNode.binaryValue();
    }
    catch (IOException e)
    {
      throw new ServerErrorException(e.getMessage());
    }
  }

  /**
   * Gets a list of byte[] from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of binary values.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":["AjIzLg==", "AjNjLp=="]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getBinaryValueList("path1")
   *   returns a list containing the byte arrays decoded from
   *       "AjIzLg==", "AjNjLp=="
   *   <p>
   *
   *   getBinaryValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<byte[]> getBinaryValueList(@NotNull final String path)
      throws ScimException
  {
    return getBinaryValueList(Path.fromString(path));
  }

  /**
   * Gets a list of byte[] from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of binary values.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":["AjIzLg==", "AjNjLp=="]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getBinaryValueList(Path.fromString("path1"))
   *   returns a list containing the byte arrays decoded from
   *       "AjIzLg==", "AjNjLp=="
   *   <p>
   *
   *   getBinaryValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<byte[]> getBinaryValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<byte[]> values = new ArrayList<byte[]>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      try
      {
        byte[] value = iterator.next().binaryValue();
        if(value == null)
        {
          // this is not a binary or text node.
          throw new ServerErrorException("Value at path " + path +
              " is not a valid base64 string");
        }
        values.add(value);
      }
      catch (IOException e)
      {
        throw new ServerErrorException(e.getMessage());
      }
    }
    return values;
  }

  /////////////////////////////////////
  // SCIM Ref methods
  /////////////////////////////////////
  /**
   * Adds or replaces a URI value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"http://localhost:8080/uri/One"
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue("path1", path1value)
   *   where path1value is a URI
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *
   *   gsr.replaceValue("path2", path2value)
   *   where path2value is a URI
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final URI value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Adds or replaces a URI value in a generic SCIM resource.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":"http://localhost:8080/uri/One"
   * }
   * </code></pre>
   *   <p>
   *   gsr.replaceValue(Path.fromString("path1"), path1value)
   *   where path1value is a URI
   *   would change the "path1" field to the value of the path1value
   *   variable
   *   <p>
   *   gsr.replaceValue(Path.fromString("path2"), path2value)
   *   where path2value is a URI
   *   would add a field called "path2" with the value of the path2value
   *   variable
   *   <p>
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to replace the value for.
   * @param value the new value.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final URI value)
      throws ScimException
  {
    return replaceValue(path,
        JsonUtils.getJsonNodeFactory().textNode(value.toString()));
  }

  /**
   * Adds URI values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   * For example:
   *   <p>
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":
   *   [
   *       "http://localhost:8080/uri/One", "http://localhost:8080/uri/Two"
   *   ]
   * }
   * </code></pre>
   *
   *   <p>
   *   gsr.addURIValues("path1", path1values)
   *   where path1Value is a List of URI.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addURIValues("path2", path2values)
   *   where path2values is a List of URI.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addURIValues(@NotNull final String path,
                                          @NotNull final List<URI> values)
      throws ScimException
  {
    return addURIValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addURIValues(String, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addURIValues(@NotNull final String path,
                                          @NotNull final URI value1,
                                          @Nullable final URI... values)
      throws ScimException
  {
    return addURIValues(path, toList(value1, values));
  }

  /**
   * Adds URI values to an array node.  If no array node exists at the
   * specified path, a new array node will be created.
   *   <p>
   * For example:
   * In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   * {
   *   "path1":
   *   [
   *       "http://localhost:8080/uri/One", "http://localhost:8080/uri/Two"
   *   ]
   * }
   * </code></pre>
   *   <p>
   *   gsr.addURIValues(Path.fromString("path1"), path1values)
   *   where path1Value is a List of URI.
   *   Would add each of the items in the path1values list to the
   *   "path1" list in the generic SCIM resource
   *   <p>
   *
   *   gsr.addURIValues(Path.fromString("path2"), path2values)
   *   where path2values is a List of URI.
   *   Would create a new array called "path2"
   *   <p>
   *
   *   Note that in a case where multiple paths match (for example
   *   a path with a filter), all paths that match will be affected.
   *
   * @param path the path to add the list to.
   * @param values a list containing the new values.
   * @return returns the new generic SCIM resource (this).
   * @throws ScimException thrown if an error occurs (for example
   * if the path or value is "{@code null}" or invalid).
   */
  @NotNull
  public GenericScimResource addURIValues(@NotNull final Path path,
                                          @NotNull final List<URI> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for(URI value : values)
    {
      valuesArrayNode.add(value.toString());
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addURIValues(Path, List)}.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        This object.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addURIValues(@NotNull final Path path,
                                          @NotNull final URI value1,
                                          @Nullable final URI... values)
      throws ScimException
  {
    return addURIValues(path, toList(value1, values));
  }

  /**
   * Gets a URI value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a URI.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"http://localhost:8080/uri/One"
   *   }
   * </code></pre>
   *   <p>
   *
   *   getURIValue("path1")
   *   returns "http://localhost:8080/uri/One"
   *   <p>
   *
   *   getURIValue("bogusPath")
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public URI getURIValue(@NotNull final String path)
      throws ScimException
  {
    return getURIValue(Path.fromString(path));
  }

  /**
   * Gets a URI value from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a URI.  If the path does not exist,
   * "{@code null}" will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":"http://localhost:8080/uri/One"
   *   }
   * </code></pre>
   *   <p>
   *
   *   getURIValue(Path.fromString("path1"))
   *   returns "http://localhost:8080/uri/One"
   *   <p>
   *
   *   getURIValue(Path.fromString("bogusPath"))
   *   returns null
   *
   * @param path the path to get the value from.
   * @return the value at the path, or null.
   * @throws ScimException thrown if an error occurs.
   */
  @Nullable
  public URI getURIValue(@NotNull final Path path)
      throws ScimException
  {
    try
    {
      JsonNode jsonNode = getValue(path);
      return jsonNode.isNull() ? null : new URI(jsonNode.textValue());
    }
    catch(URISyntaxException ex)
    {
      throw new ServerErrorException(ex.getMessage());
    }
  }

  /**
   * Gets a list of URI from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of URI.  If the path does
   * not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":
   *     [
   *       "http://localhost:8080/uri/One", "http://localhost:8080/uri/Two"
   *     ]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getURIValueList("path1")
   *   returns a list containing
   *       "http://localhost:8080/uri/One", "http://localhost:8080/uri/Two"
   *   <p>
   *
   *   getURIValueList("bogusPath")
   *   returns an empty list
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<URI> getURIValueList(@NotNull final String path)
      throws ScimException
  {
    return getURIValueList(Path.fromString(path));
  }

  /**
   * Gets a list of URI from a generic SCIM resource.  If the path exists,
   * the JSON node at the path must be a list of URI.  If the path
   * does not exist, an empty list will be returned.
   *   <p>
   *
   * For example:
   *   In a GenericScimResource (gsr) representing the following resource:
   * <pre><code>
   *   {
   *     "path1":
   *     [
   *         "http://localhost:8080/uri/One", "http://localhost:8080/uri/Two"
   *     ]
   *   }
   * </code></pre>
   *   <p>
   *
   *   getURIValueList(Path.fromString("path1"))
   *   returns a list containing
   *       "http://localhost:8080/uri/One", "http://localhost:8080/uri/Two"
   *   <p>
   *
   *   getURIValueList(Path.fromString("bogusPath"))
   *   returns an empty list
   *   <p>
   *
   * @param path the path to get the value from.
   * @return the value at the path, or an empty list.
   * @throws ScimException thrown if an error occurs.
   */
  @NotNull
  public List<URI> getURIValueList(@NotNull final Path path)
      throws ScimException
  {
    try
    {
      JsonNode valueNode = getValue(path);
      List<URI> values = new ArrayList<URI>();

      Iterator<JsonNode> iterator = valueNode.iterator();
      while (iterator.hasNext())
      {
        String uriString = iterator.next().textValue();
        values.add(new URI(uriString));
      }
      return values;
    }
    catch (URISyntaxException ex)
    {
      throw new ServerErrorException(ex.getMessage());
    }
  }
}
