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
 * A generic SCIM object. This class can be used if you have no Java object
 * representing the SCIM object being returned.
 * <br><br>
 * This object can be used when the exact structure of the SCIM object, which
 * will be received as JSON text, is not known. This class provides methods that
 * can read attributes from those objects without needing to know the schema
 * ahead of time. If, however, the SCIM object you are working with is clearly
 * defined and has an established structure, you could still use this object,
 * but the {@link BaseScimResource} superclass is likely a better choice.
 * <br><br>
 *
 * This class contains a Jackson {@link ObjectNode} that contains a JSON
 * representation of the SCIM resource, which may be fetched at any time with
 * {@link #getObjectNode()}. Individual fields may be fetched, added, or
 * updated with methods such as:
 * <ul>
 *   <li> {@link #getBooleanValue(String)}
 *   <li> {@link #addStringValues(Path, String, String...)}
 *   <li> {@link #replaceValue(Path, String)}
 * </ul>
 * Note that the "add" methods are used to append values to an array, such as
 * for the {@code emails} attribute on a
 * {@link com.unboundid.scim2.common.types.UserResource}. To set a new value for
 * a single-valued attribute {e.g., {@code userName}}, use the "replace"
 * methods.
 * <br><br>
 *
 * If you have a BaseScimResource derived object, you can always get a
 * {@link GenericScimResource} by calling {@link #asGenericScimResource()}.
 * For example:
 * <pre><code>
 *   UserResource user = new UserResource().setUserName("PhoenixW");
 *   GenericScimResource genericUser = user.asGenericScimResource();
 * </code></pre>
 *
 * It is also possible to convert a GenericScimResource object into an
 * object that inherits from BaseScimResource, provided that the ObjectNode is
 * properly formatted. This requires calling {@link JsonUtils#nodeToValue}:
 * <pre><code>
 *   GenericScimResource genericUser = getUserFromClient();
 *   UserResource user =
 *       JsonUtils.nodeToValue(genericUser.getObjectNode(), UserResource.class);
 * </code></pre>
 *
 * @see BaseScimResource
 */
@JsonDeserialize(using = GenericScimObjectDeserializer.class)
@JsonSerialize(using = GenericScimObjectSerializer.class)
public class GenericScimResource implements ScimResource
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
   * @return An {@code ObjectNode} representing this generic SCIM resource.
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
      if (values.isEmpty())
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
      if (value.isNull())
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
      if (value.isNull() || !value.isArray())
      {
        return Collections.emptyList();
      }

      // This will not return null since the input value is non-null.
      //
      //noinspection DataFlowIssue
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
      if (value.isNull())
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
   * Alternate version of {@link #getValue(Path)} that accepts a path as a
   * string.
   *
   * @param path  The path of the object.
   * @return      The JsonNode at the path, or a NullNode if nothing was found.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public JsonNode getValue(@NotNull final String path)
      throws ScimException
  {
    return getValue(Path.fromString(path));
  }

  /**
   * Fetches a JsonNode representing an attribute's data from a generic SCIM
   * resource. This value may be an ArrayNode. For example, for a generic SCIM
   * resource representing the following JSON:
   * <pre>
   *   {
   *     "name" : "Bob",
   *     "friends" : [ "Amy", "Beth", "Carol" ]
   *   }
   * </pre>
   *
   * To fetch the values of the {@code name} and {@code friends} fields, use
   * the following Java code:
   * <pre><code>
   *   JsonNode nameNode = gsr.getValue("name");
   *   JsonNode friendsNode = gsr.getValue("friends");
   * </code></pre>
   *
   * In the above example, {@code nameNode} would be a TextNode containing the
   * value "Bob", and {@code friendsNode} would be an ArrayNode containing
   * 3 TextNodes with the values "Amy", "Beth", and "Carol".
   *
   * @param path The path of the object.
   * @return     The JsonNode at the path, or a NullNode if nothing was found.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public JsonNode getValue(@NotNull final Path path)
      throws ScimException
  {
    return JsonUtils.getValue(path, objectNode);
  }

  /**
   * Alternate method for {@link #replaceValue(Path, JsonNode)} that accepts a
   * path as a string.
   *
   * @param path   The path to the attribute whose value will be set/replaced.
   * @param value  The value(s) to set.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs when parsing the resource, such as
   *                       the use of an invalid path or value.
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
   * {@link JsonUtils#replaceValue(Path, ObjectNode, JsonNode)} method.
   * <br><br>
   *
   * The {@link JsonUtils#valueToNode} method may be used to convert a value
   * instance to a {@link JsonNode}.
   *
   * @param path   The path to the attribute whose value will be set/replaced.
   * @param value  The value(s) to set.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs when parsing the resource, such as
   *                       the use of an invalid path or value.
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
   * Alternate version of {@link #addValues(Path, ArrayNode)} that accepts a
   * path as a string.
   *
   * @param path   The path to the attribute that will be updated.
   * @param values The value(s) to add.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
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
   * Add new values to a multi-valued attribute at the provided path. Equivalent
   * to using the {@link JsonUtils#addValue(Path, ObjectNode, JsonNode)} method.
   * <br><br>
   *
   * To "add" a single-valued attribute, use the
   * {@link #replaceValue(String, String)} method instead.
   * <br><br>
   *
   * If the path matches multiple values (i.e., if the {@link Path} contains a
   * filter), all paths that match will be updated.
   *
   * @param path   The path to the attribute that will be updated.
   * @param values The value(s) to add.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
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
   * Alternate version of {@link #removeValues(Path)} that accepts a path as
   * a string.
   *
   * @param path The path to the attribute whose values will be removed.
   * @return Whether one or more values were removed.
   *
   * @throws ScimException  If the path is invalid.
   */
  public boolean removeValues(@NotNull final String path)
      throws ScimException
  {
    return removeValues(Path.fromString(path));
  }

  /**
   * Removes values at the provided path. Equivalent to using the
   * {@link JsonUtils#removeValues(Path, ObjectNode)} method.
   * <br><br>
   * If the path matches multiple values (i.e., if the {@link Path} contains a
   * filter), all paths that match will be removed.
   *
   * @param path The path to the attribute whose values will be removed.
   * @return Whether one or more values were removed.
   *
   * @throws ScimException  If the path is invalid.
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
    if (!(o instanceof GenericScimResource resource))
    {
      return false;
    }

    // Null nodes should not occur, but we should be defensive about this
    // possibility.
    return Objects.equals(objectNode, resource.getObjectNode());
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

  /**
   * Alternate version of {@link #replaceValue(Path, String)} that accepts a
   * path as a string.
   *
   * @param path   The path to the string attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final String value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a single-valued string attribute in a generic SCIM resource.
   * Consider a GenericScimResource of the following form:
   * <pre>
   *   {
   *     "favoriteArtist": "unknown"
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   gsr.replaceValue("favoriteArtist", "Slim Shady");
   * </code></pre>
   *
   * @param path   The path to the string attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final String value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().textNode(value));
  }

  /**
   * Alternate version of {@link #addStringValues(Path, List)} that accepts a
   * path as a string.
   *
   * @param path    The path to a multi-valued attribute of strings. If the path
   *                does not exist, a new attribute will be added.
   * @param values  A list containing the new values.
   * @return        The updated generic SCIM resource (this object).
   *
   * @throws ScimException   If the path is invalid.
   */
  @NotNull
  public GenericScimResource addStringValues(@NotNull final String path,
                                             @NotNull final List<String> values)
      throws ScimException
  {
    return addStringValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addStringValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of strings in a generic SCIM
   * resource. If no ArrayNode exists at the specified path, a new ArrayNode
   * will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "starRealmsCards": [ "Trade Bot", "Cutter" ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   gsr.addStringValues("starRealmsCards", "BattleCruiser);
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addStringValues("starRealmsCards", List.of("BattleCruiser"));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addStringValues("sevenWondersCards", "Marketplace");
   * </code></pre>
   *
   * @param path    The path to a multi-valued attribute of strings. If the path
   *                does not exist, a new attribute will be added to the
   *                resource.
   * @param values  A list containing the new values.
   * @return        The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addStringValues(@NotNull final Path path,
                                             @NotNull final List<String> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (String value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addStringValues(Path, List)} that accepts
   * individual values instead of a list.
   *
   * @param path    The path to a multi-valued attribute of strings.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getStringValue(Path)} that accepts a path as
   * a string.
   *
   * @param path The path to the requested attribute.
   * @return The value at the path, or {@code null} if the path does not exist
   *         on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public String getStringValue(@NotNull final String path)
      throws ScimException
  {
    return getStringValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a single-valued attribute in a generic SCIM resource.
   * If the path exists, the JSON node at the path must be a {@link String}. If
   * the path does not exist, {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "favoriteArtist": "Slim Shady"
   *   }
   * </pre>
   *
   * To fetch a value from the resource, use the following Java code:
   * <pre><code>
   *   JsonNode newValue = gsr.getStringValue("favoriteArtist");
   *
   *   // Returns null.
   *   JsonNode nullValue = gsr.getStringValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path The path to the requested attribute.
   * @return The value at the path, or {@code null} if the path does not exist
   *         on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public String getStringValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.textValue();
  }

  /**
   * Alternate version of {@link #getStringValueList(Path)} that accepts a path
   * as a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<String> getStringValueList(@NotNull final String path)
      throws ScimException
  {
    return getStringValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued string attribute in a generic SCIM
   * resource. If the path exists, the JSON node at the path must be a list of
   * {@link String} values. If the path does not exist, an empty list will be
   * returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "starRealmsCards": [ "Trade Bot", "Cutter" ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   // Returns the list.
   *   List&lt;String&gt; stringValues = gsr.getStringValue("starRealmsCards");
   *
   *   // Returns an empty list.
   *   gsr.getStringValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<String> getStringValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<String> values = new ArrayList<>();

    for (JsonNode jsonNode : valueNode)
    {
      values.add(jsonNode.textValue());
    }
    return values;
  }

  /**
   * Alternate version of {@link #replaceValue(Path, Boolean)} that accepts a
   * path as a string.
   *
   * @param path   The path to the boolean attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Boolean value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Sets a boolean value in a generic SCIM resource. This method can update an
   * existing value, or set a new boolean attribute value. For example, for the
   * following GenericScimResource:
   * <pre>
   *   {
   *     "mfaEnabled": true
   *   }
   * </pre>
   *
   * To update this resource, use the following Java code:
   * <pre><code>
   *   // Update the existing value.
   *   gsr.replaceValue("mfaEnabled", false);
   *
   *   // Add a new attribute value to the resource.
   *   gsr.replaceValue("accountLocked", true);
   * </code></pre>
   *
   * @param path   The path to the boolean attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Boolean value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().booleanNode(value));
  }

  /**
   * Alternate version of {@link #getBooleanValue(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If the path is invalid.
   */
  @Nullable
  public Boolean getBooleanValue(@NotNull final String path)
      throws ScimException
  {
    return getBooleanValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a boolean attribute from a generic SCIM resource. If
   * the path does not exist, {@code null} will be returned. For example, for
   * the following generic SCIM resource:
   * <pre>
   *   {
   *     "mfaEnabled": true
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   Boolean isMFAEnabled = gsr.getBooleanValue("mfaEnabled");
   * </code></pre>
   *
   * @param path  The path to the boolean attribute to obtain.
   * @return      The attribute value.
   *
   * @throws ScimException  If the path is invalid.
   */
  @Nullable
  public Boolean getBooleanValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.booleanValue();
  }

  /**
   * Alternate version of {@link #replaceValue(Path, Double)} that accepts a
   * path as a string.
   *
   * @param path   The path to the floating-point attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Double value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a single-valued floating-point attribute in a generic SCIM
   * resource. Consider a GenericScimResource of the following form:
   * <pre>
   *   {
   *     "weightKgs": 120.0
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   gsr.replaceValue("weightKgs", 125.0);
   * </code></pre>
   *
   * @param path   The path to the floating-point attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Double value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().numberNode(value));
  }

  /**
   * Alternate version of {@link #addDoubleValues(Path, List)} that accepts a
   * path as a string.
   *
   * @param path    The path to a multi-valued attribute of floating-point
   *                numbers. If the path does not exist, a new attribute will be
   *                added.
   * @param values  A list containing the new values.
   * @return        The updated generic SCIM resource (this object).
   *
   * @throws ScimException   If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDoubleValues(@NotNull final String path,
                                             @NotNull final List<Double> values)
      throws ScimException
  {
    return addDoubleValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addDoubleValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of floating-point numbers
   * (i.e., an array of decimal values) in a generic SCIM resource. If no
   * ArrayNode exists at the specified path, a new ArrayNode will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "niceNumbers": [ 3.141, 2.718 ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   gsr.addDoubleValues("niceNumbers", 1.414);
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addDoubleValues("niceNumbers", List.of(1.414));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addDoubleValues("newAttribute", 200.0);
   * </code></pre>
   *
   * @param path   The path to a multi-valued attribute of floating-point
   *               numbers. If the path does not exist, a new attribute will be
   *               added to the resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDoubleValues(@NotNull final Path path,
                                             @NotNull final List<Double> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Double value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addDoubleValues(Path, List)} that accepts
   * individual values instead of a list.
   *
   * @param path    The path to a multi-valued attribute of floating-point
   *                numbers.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getDoubleValue(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Double getDoubleValue(@NotNull final String path)
      throws ScimException
  {
    return getDoubleValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a single-valued floating-point attribute in a generic
   * SCIM resource. If the path exists, the JSON node at the path must be a
   * {@link Double}. If the path does not exist, {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "weightKgs": 120.0
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   Double weightKilos = gsr.getDoubleValue("weightKgs");
   *
   *   // Non-existent paths will return null.
   *   gsr.getDoubleValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Double getDoubleValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.doubleValue();
  }

  /**
   * Alternate version of {@link #getDoubleValueList(Path)} that accepts a path
   * as a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Double> getDoubleValueList(@NotNull final String path)
      throws ScimException
  {
    return getDoubleValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued floating-point attribute in a generic
   * SCIM resource. If the path exists, the JSON node at the path must be a list
   * of {@link Double} values. If the path does not exist, an empty list will be
   * returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "niceNumbers": [ 3.141, 2.718 ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   List&lt;Double&gt; niceNumbers = gsr.getDoubleValueList("niceNumbers");
   *
   *   // Non-existent paths will return an empty list.
   *   List&lt;Double&gt; emptyList = gsr.getDoubleValueList("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Double> getDoubleValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Double> values = new ArrayList<>();

    for (JsonNode jsonNode : valueNode)
    {
      values.add(jsonNode.doubleValue());
    }
    return values;
  }

  /**
   * Alternate version of {@link #replaceValue(Path, Integer)} that accepts a
   * path as a string.
   *
   * @param path   The path to the integer attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Integer value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a single-valued integer attribute in a generic SCIM resource.
   * Consider a GenericScimResource of the following form:
   * <pre>
   *   {
   *     "numGroups": 14
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   gsr.replaceValue("numGroups", 13);
   * </code></pre>
   *
   * @param path   The path to the integer attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Integer value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().numberNode(value));
  }

  /**
   * Alternate version of {@link #addIntegerValues(Path, List)} that accepts a
   * path as a string.
   *
   * @param path   The path to a multi-valued attribute of integers. If the path
   *               does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
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
   * Alternate version of {@link #addIntegerValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of integers (i.e., an array of
   * integer values) in a generic SCIM resource. If no ArrayNode exists at the
   * specified path, a new ArrayNode will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "fibonacciNumbers": [ 2, 3, 5, 8, 13 ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   gsr.addIntegerValues("fibonacciNumbers", 21);
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addIntegerValues("fibonacciNumbers", List.of(21, 34));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addIntegerValues("perfectSquares", 1, 4, 9);
   * </code></pre>
   *
   * @param path   The path to a multi-valued attribute of integers. If the path
   *               does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addIntegerValues(
      @NotNull final Path path,
      @NotNull final List<Integer> values)
          throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Integer value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addIntegerValues(Path, List)} that accepts
   * individual values instead of a list.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getIntegerValue(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Integer getIntegerValue(@NotNull final String path)
      throws ScimException
  {
    return getIntegerValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a single-valued integer attribute in a generic SCIM
   * resource. If the path exists, the JSON node at the path must be an
   * {@link Integer}. If the path does not exist, {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "numGroups": 14
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   Integer groupCount = gsr.getIntegerValue("numGroups");
   *
   *   // Non-existent paths will return null.
   *   gsr.getIntegerValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Integer getIntegerValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.intValue();
  }

  /**
   * Alternate version of {@link #getIntegerValueList(Path)} that accepts a path
   * as a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Integer> getIntegerValueList(@NotNull final String path)
      throws ScimException
  {
    return getIntegerValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued attribute of integers in a generic
   * SCIM resource. If the path exists, the JSON node at the path must be a list
   * of {@link Integer} values. If the path does not exist, an empty list will be
   * returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "fibonacciNumbers": [ 2, 3, 5, 8, 13 ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   List&lt;Integer&gt; fibonacciNumbers =
   *           gsr.getIntegerValueList("fibonacciNumbers");
   *
   *   // Non-existent paths will return an empty list.
   *   List&lt;Integer&gt; emptyList =
   *           gsr.getIntegerValueList("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Integer> getIntegerValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Integer> values = new ArrayList<>();

    for (JsonNode jsonNode : valueNode)
    {
      values.add(jsonNode.intValue());
    }
    return values;
  }

  /**
   * Alternate version of {@link #replaceValue(Path, Long)} that accepts a path
   * as a string.
   *
   * @param path   The path to the 64-bit attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Long value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a single-valued 64-bit numerical attribute in a generic SCIM
   * resource. Consider a GenericScimResource of the following form:
   * <pre>
   *   {
   *     "numGroups": 14
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   gsr.replaceValue("numGroups", 13L);
   * </code></pre>
   *
   * @param path   The path to the 64-bit attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Long value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().numberNode(value));
  }

  /**
   * Alternate version of {@link #addLongValues(Path, List)} that accepts a path
   * as a string.
   *
   * @param path   The path to a multi-valued attribute of 64-bit values. If the
   *               path does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addLongValues(@NotNull final String path,
                                           @NotNull final List<Long> values)
      throws ScimException
  {
    return addLongValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addLongValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of 64-bit quantities in a
   * generic SCIM resource. If no ArrayNode exists at the specified path, a new
   * ArrayNode will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "fibonacciNumbers": [ 2, 3, 5, 8, 13 ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   gsr.addLongValues("fibonacciNumbers", 21L);
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addLongValues("fibonacciNumbers", List.of(21L, 34L));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addLongValues("perfectSquares", 1L, 4L, 9L);
   * </code></pre>
   *
   * @param path   The path to a multi-valued attribute of 64-bit values. If the
   *               path does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addLongValues(@NotNull final Path path,
                                           @NotNull final List<Long> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Long value : values)
    {
      valuesArrayNode.add(value);
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addLongValues(Path, List)} that accepts
   * individual values instead of a list.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getLongValue(Path)} that accepts a path as a
   * string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Long getLongValue(@NotNull final String path)
      throws ScimException
  {
    return getLongValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a single-valued 64-bit numerical attribute in a
   * generic SCIM resource. If the path exists, the JSON node at the path must
   * be a {@link Long}. If the path does not exist, {@code null} will be
   * returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "numGroups": 14
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   Long groupCount = gsr.getLongValue("numGroups");
   *
   *   // Non-existent paths will return null.
   *   gsr.getLongValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Long getLongValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    return jsonNode.isNull() ? null : jsonNode.longValue();
  }

  /**
   * Alternate version of {@link #getLongValueList(Path)} that accepts a path
   * as a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Long> getLongValueList(@NotNull final String path)
      throws ScimException
  {
    return getLongValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued attribute of 64-bit numbers in a
   * generic SCIM resource. If the path exists, the JSON node at the path must
   * be a list of {@link Long} values. If the path does not exist, an empty list
   * will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "fibonacciNumbers": [ 2, 3, 5, 8, 13 ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   List&lt;Long&gt; fibonacciNumbers =
   *           gsr.getLongValueList("fibonacciNumbers");
   *
   *   // Non-existent paths will return an empty list.
   *   List&lt;Long&gt; emptyList =
   *           gsr.getLongValueList("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Long> getLongValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Long> values = new ArrayList<>();

    for (JsonNode jsonNode : valueNode)
    {
      values.add(jsonNode.longValue());
    }
    return values;
  }

  /**
   * Alternate version of {@link #replaceValue(Path, Date)} that accepts a path
   * as a string.
   *
   * @param path   The path to the timestamp attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final Date value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a timestamp attribute in a generic SCIM resource. Consider a
   * GenericScimResource of the following form:
   * <pre>
   *   {
   *     "lastModified": "2016-04-16T12:17:42.000Z"
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   // October 30, 2024, 9:20 AM.
   *   gsr.replaceValue("lastModified",
   *       new GregorianCalendar(2024, Calendar.OCTOBER, 30, 9, 20).getTime());
   * </code></pre>
   *
   * @param path   The path to the timestamp attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final Date value)
      throws ScimException
  {
    return replaceValue(path, GenericScimResource.getDateJsonNode(value));
  }

  /**
   * Alternate version of {@link #addDateValues(Path, List)} that accepts a path
   * as a string.
   *
   * @param path   The path to a multi-valued attribute of 64-bit values. If the
   *               path does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addDateValues(@NotNull final String path,
                                           @NotNull final List<Date> values)
      throws ScimException
  {
    return addDateValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addDateValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of timestamps in a generic SCIM
   * resource. If no ArrayNode exists at the specified path, a new ArrayNode
   * will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "notableTimestamps": [
   *         "2015-03-20T20:38:42.000Z",
   *         "2016-04-16T12:17:42.000Z"
   *     ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   Date newTimestamp =
   *       new GregorianCalendar(2024, Calendar.OCTOBER, 30, 9, 20).getTime();
   *
   *   gsr.addDateValues("notableTimestamps", newTimestamp);
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addDateValues("notableTimestamps", List.of(newTimestamp));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addDateValues("otherTimes", newTimestamp);
   * </code></pre>
   *
   * @param path   The path to a multi-valued attribute of 64-bit values. If the
   *               path does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
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
   * Alternate version of {@link #addDateValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getDateValue(Path)} that accepts a path as a
   * string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Date getDateValue(@NotNull final String path)
      throws ScimException
  {
    return getDateValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a timestamp attribute in a generic SCIM resource. If
   * the path exists, the JSON node at the path must be a {@link Date}. If the
   * path does not exist, {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "lastModified": "2016-04-16T12:17:42.000Z"
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   Date modifyTimestamp = gsr.getDateValue("lastModified");
   *
   *   // Non-existent paths will return null.
   *   gsr.getDateValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public Date getDateValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    if (jsonNode.isNull())
    {
      return null;
    }
    return getDateFromJsonNode(jsonNode);
  }

  /**
   * Alternate version of {@link #getDateValueList(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Date> getDateValueList(@NotNull final String path)
      throws ScimException
  {
    return getDateValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued attribute of timestamps in a generic
   * SCIM resource. If the path exists, the JSON node at the path must be a list
   * of {@link Date} values. If the path does not exist, an empty list will be
   * returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "notableTimestamps": [
   *         "2015-03-20T20:38:42.000Z",
   *         "2016-04-16T12:17:42.000Z"
   *     ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   List&lt;Date&gt; timestamps = gsr.getDateValueList("notableTimestamps");
   *
   *   // Non-existent paths will return an empty list.
   *   List&lt;Date&gt; emptyList = gsr.getDateValueList("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<Date> getDateValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<Date> values = new ArrayList<>();

    for (JsonNode jsonNode : valueNode)
    {
      values.add(GenericScimResource.getDateFromJsonNode(jsonNode));
    }
    return values;
  }

  /**
   * Converts a {@link Date} object into a Jackson JsonNode.
   *
   * @param date  The date to represent as a JsonNode.
   * @return      A JsonNode representing the date.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public static TextNode getDateJsonNode(@Nullable final Date date)
      throws ScimException
  {
    return JsonUtils.valueToNode(date);
  }

  /**
   * Converts a {@link JsonNode} into a {@link Date} object.
   *
   * @param node  The JsonNode representing the date.
   * @return      The date represented by the JsonNode.
   *
   * @throws ScimException  If the JsonNode was not properly formatted.
   */
  @Nullable
  public static Date getDateFromJsonNode(@NotNull final JsonNode node)
      throws ScimException
  {
    try
    {
      return JsonUtils.getObjectReader().forType(Date.class).readValue(node);
    }
    catch (IOException ex)
    {
      throw new ServerErrorException(ex.getMessage());
    }
  }

  /**
   * Alternate version of {@link #replaceValue(Path, byte[])} that accepts a
   * path as a string.
   *
   * @param path   The path to the binary attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final byte[] value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a single-valued binary attribute in a generic SCIM resource. Binary
   * data is displayed on SCIM resources in base64-encoded form. Consider a
   * GenericScimResource of the following form:
   * <pre>
   *   {
   *     "encodedMessage": "VW5ib3VuZElECg=="
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   byte[] data = new byte[] { 0x50, 0x69, 0x6E, 0x67, 0x0A });
   *   gsr.replaceValue("encodedMessage", data);
   * </code></pre>
   *
   * @param path   The path to the binary attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final byte[] value)
      throws ScimException
  {
    return replaceValue(path, JsonUtils.getJsonNodeFactory().binaryNode(value));
  }

  /**
   * Alternate version of {@link #addBinaryValues(Path, List)} that accepts a
   * path as a string.
   *
   * @param path   The path to a multi-valued attribute of base64-encoded
   *               values. If the path does not exist, a new attribute will be
   *               added to the resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addBinaryValues(@NotNull final String path,
                                             @NotNull final List<byte[]> values)
      throws ScimException
  {
    return addBinaryValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addBinaryValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of base64-encoded strings in a
   * generic SCIM resource. If no ArrayNode exists at the specified path, a new
   * ArrayNode will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "certificates": [
   *         "VGhhbmtzIGZvciBkZWNvZGluZyBvdXIgbGl0dGxlIGVhc3RlciBlZ2cuCg==",
   *         "V2UgaG9wZSB0aGlzIGRvY3VtZW50YXRpb24gaXMgdXNlZnVsIHRvIHlvdS4K",
   *         "WW91IGFyZSBsb3ZlZCwgYW5kIHlvdSBtYXR0ZXIuCg=="
   *     ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   byte[] data = new byte[] { 0x54, 0x68, 0x61, 0x6E, 0x6B };
   *   byte[] moreData = new byte[] { 0x79, 0x6F, 0x75 };
   *   gsr.addBinaryValues("certificates", data, moreData);
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addBinaryValues("certificates", List.of(data, moreData));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addBinaryValues("otherData", data);
   * </code></pre>
   *
   * @param path   The path to a multi-valued attribute of base64-encoded
   *               values. If the path does not exist, a new attribute will be
   *               added to the resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  @SuppressWarnings("SpellCheckingInspection")
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
   * Alternate version of {@link #addBinaryValues(Path, List)} that accepts
   * individual values instead of a list.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getBinaryValue(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public byte[] getBinaryValue(@NotNull final String path)
      throws ScimException
  {
    return getBinaryValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a base64-encoded attribute in a generic SCIM resource.
   * If the path exists, the JSON node at the path must be a {@code byte[]}. If
   * the path does not exist, {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "encodedMessage": "VW5ib3VuZElECg=="
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   byte[] message = gsr.getBinaryValue("encodedMessage");
   *
   *   // Non-existent paths will return null.
   *   gsr.getBinaryValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public byte[] getBinaryValue(@NotNull final Path path)
      throws ScimException
  {
    JsonNode jsonNode = getValue(path);
    if (jsonNode.isNull())
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
   * Alternate version of {@link #getBinaryValue(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<byte[]> getBinaryValueList(@NotNull final String path)
      throws ScimException
  {
    return getBinaryValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued attribute of base64-encoded strings in
   * a generic SCIM resource. If the path exists, the JSON node at the path must
   * be a list of {@code byte[]} values. If the path does not exist,
   * {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "certificates": [
   *         "VGhhbmtzIGZvciBkZWNvZGluZyBvdXIgbGl0dGxlIGVhc3RlciBlZ2cuCg==",
   *         "V2UgaG9wZSB0aGlzIGRvY3VtZW50YXRpb24gaXMgdXNlZnVsIHRvIHlvdS4K",
   *         "WW91IGFyZSBsb3ZlZCwgYW5kIHlvdSBtYXR0ZXIuCg=="
   *     ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   List&lt;byte[]&gt; certs = gsr.getBinaryValueList("certificates");
   *
   *   // Non-existent paths will return an empty list.
   *   List&lt;byte[]&gt; emptyList = gsr.getBinaryValueList("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  @SuppressWarnings("SpellCheckingInspection")
  public List<byte[]> getBinaryValueList(@NotNull final Path path)
      throws ScimException
  {
    JsonNode valueNode = getValue(path);
    List<byte[]> values = new ArrayList<>();

    Iterator<JsonNode> iterator = valueNode.iterator();
    while (iterator.hasNext())
    {
      try
      {
        byte[] value = iterator.next().binaryValue();
        if (value == null)
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

  /**
   * Alternate version of {@link #replaceValue(Path, URI)} that accepts a path
   * as a string.
   *
   * @param path   The path to the URI attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  public GenericScimResource replaceValue(@NotNull final String path,
                                          @NotNull final URI value)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), value);
  }

  /**
   * Updates a single-valued URI attribute in a generic SCIM resource. Consider
   * a GenericScimResource of the following form:
   * <pre>
   *   {
   *     "profilePicture": "https://example.com/trucy.png"
   *   }
   * </pre>
   *
   * To update this value, use the following Java code:
   * <pre><code>
   *   gsr.replaceValue("profilePicture",
   *           new URI("https://example.com/trucy_new.png"));
   * </code></pre>
   *
   * @param path   The path to the URI attribute.
   * @param value  The new value.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException If an error occurs while parsing the resource (e.g.,
   *                       an invalid path was provided).
   */
  @NotNull
  @SuppressWarnings("JavadocLinkAsPlainText")
  public GenericScimResource replaceValue(@NotNull final Path path,
                                          @NotNull final URI value)
      throws ScimException
  {
    return replaceValue(path,
        JsonUtils.getJsonNodeFactory().textNode(value.toString()));
  }

  /**
   * Alternate version of {@link #addURIValues(Path, List)} that accepts a path
   * as a string.
   *
   * @param path   The path to a multi-valued attribute of URI values. If the
   *               path does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public GenericScimResource addURIValues(@NotNull final String path,
                                          @NotNull final List<URI> values)
      throws ScimException
  {
    return addURIValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addURIValues(Path, List)} that accepts
   * individual values instead of a list, and accepts a path as a string.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Adds new values to a multi-valued attribute of URIs in a generic SCIM
   * resource. If no ArrayNode exists at the specified path, a new ArrayNode
   * will be created.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "bookmarks": [
   *         "https://example.com/rickRoll.mp4",
   *         "file:///home/UnboundID/sdk-zips"
   *     ]
   *   }
   * </pre>
   *
   * To add new values, use one of the following:
   * <pre><code>
   *   gsr.addURIValues("bookmarks",
   *           new URI("https://example.org/index.html"),
   *           new URI("https://example.org/login")
   *   );
   *
   *   // This method is useful when the new values are already in a list.
   *   gsr.addURIValues("bookmarks", List.of(new URI("https://example.org")));
   *
   *   // Creates a new array of values on the resource.
   *   gsr.addURIValues("ldapURLs", new URI("ldap://ds.example.com"));
   * </code></pre>
   *
   * @param path   The path to a multi-valued attribute of URI values. If the
   *               path does not exist, a new attribute will be added to the
   *               resource.
   * @param values A list containing the new values.
   * @return       The updated generic SCIM resource (this object).
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  @SuppressWarnings("JavadocLinkAsPlainText")
  public GenericScimResource addURIValues(@NotNull final Path path,
                                          @NotNull final List<URI> values)
      throws ScimException
  {
    ArrayNode valuesArrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (URI value : values)
    {
      valuesArrayNode.add(value.toString());
    }

    return addValues(path, valuesArrayNode);
  }

  /**
   * Alternate version of {@link #addURIValues(Path, List)} that accepts
   * individual values instead of a list.
   *
   * @param path    The path to the attribute that should be updated.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        The updated generic SCIM resource (this object).
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
   * Alternate version of {@link #getURIValue(Path)} that accepts a path as a
   * string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  public URI getURIValue(@NotNull final String path)
      throws ScimException
  {
    return getURIValue(Path.fromString(path));
  }

  /**
   * Fetches the value of a single-valued URI attribute in a generic SCIM
   * resource. If the path exists, the JSON node at the path must be a
   * {@link URI}. If the path does not exist, {@code null} will be returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "profilePicture": "https://example.com/trucy.png"
   *   }
   * </pre>
   *
   * To fetch this value, use the following Java code:
   * <pre><code>
   *   URI message = gsr.getURIValue("profilePicture");
   *
   *   // Non-existent paths will return null.
   *   gsr.getURIValue("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or {@code null} if the path does not
   *              exist on the resource.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @Nullable
  @SuppressWarnings("JavadocLinkAsPlainText")
  public URI getURIValue(@NotNull final Path path)
      throws ScimException
  {
    try
    {
      JsonNode jsonNode = getValue(path);
      return jsonNode.isNull() ? null : new URI(jsonNode.textValue());
    }
    catch (URISyntaxException ex)
    {
      throw new ServerErrorException(ex.getMessage());
    }
  }

  /**
   * Alternate version of {@link #getURIValueList(Path)} that accepts a path as
   * a string.
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  public List<URI> getURIValueList(@NotNull final String path)
      throws ScimException
  {
    return getURIValueList(Path.fromString(path));
  }

  /**
   * Fetches the values of a multi-valued attribute of URIs in a generic SCIM
   * resource. If the path exists, the JSON node at the path must be a list of
   * {@link URI} values. If the path does not exist, an empty list will be
   * returned.
   * <br><br>
   *
   * For example, consider the following GenericScimResource:
   * <pre>
   *   {
   *     "bookmarks": [
   *         "https://example.com/rickRoll.mp4",
   *         "file:///home/unboundid/sdk-zips"
   *     ]
   *   }
   * </pre>
   *
   * To fetch all of this attribute's values, use the following Java code:
   * <pre><code>
   *   List&lt;URI&gt; links = gsr.getURIValueList("bookmarks");
   *
   *   // Non-existent paths will return an empty list.
   *   List&lt;URI&gt; emptyList = gsr.getURIValueList("pathThatDoesNotExist");
   * </code></pre>
   *
   * @param path  The path to the requested attribute.
   * @return      The value at the path, or an empty list.
   *
   * @throws ScimException  If an error occurs while parsing the resource.
   */
  @NotNull
  @SuppressWarnings("JavadocLinkAsPlainText")
  public List<URI> getURIValueList(@NotNull final Path path)
      throws ScimException
  {
    try
    {
      JsonNode valueNode = getValue(path);
      List<URI> values = new ArrayList<>();

      for (JsonNode jsonNode : valueNode)
      {
        String uriString = jsonNode.textValue();
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
