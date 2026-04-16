/*
 * Copyright 2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.bulk;

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * This class is used to simplify the process of obtaining Java objects from
 * bulk requests and responses.
 * <br><br>
 *
 * When dealing with bulk requests or responses, there is often embedded JSON
 * data corresponding to a SCIM resource. For example, the value of the
 * {@code response} field below represents a user that was just created:
 * <pre>
 *  {
 *    "location": "/Users/fa1afe1",
 *    "method": "POST",
 *    "status": "200",
 *    "response": {
 *      "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *      "id": "fa1afe1",
 *      "userName": "Polaroid"
 *    }
 *  }
 * </pre>
 * <br><br>
 *
 * When it comes to handling and interpreting this data, it can be tedious to
 * obtain the resource and convert it from JSON to a usable POJO. The JSON data
 * may correspond to:
 * <ul>
 *   <li> A {@link UserResource}, representing a user.
 *   <li> A {@link GroupResource}, representing a group entity.
 *   <li> A {@link ErrorResponse}, representing an error that occurred.
 *   <li> Any other custom resource type defined by a SCIM service provider.
 * </ul>
 * <br><br>
 *
 * The definition of any SCIM resource type is available in the {@code schemas}
 * attribute. However, fetching this data and conditionally converting it into
 * different objects (with {@link JsonUtils#nodeToValue}) is extra work that the
 * SCIM SDK aims to simplify.
 * <br><br>
 *
 * To solve this problem, this BulkResourceMapper class defines associations
 * between schemas (represented as sets) and a Java type. For example, any JSON
 * with a schemas value of {@code urn:ietf:params:scim:schemas:core:2.0:User}
 * is interpreted as a {@link UserResource}. If you have custom resource types
 * that need to be supported by the SCIM SDK's bulk processing, then add it with
 * one of the following methods.
 * <ul>
 *   <li> {@link #add(Class)}: If the class uses the {@link Schema} annotation.
 *   <li> {@link #put put()}: To define a direct relationship for any class.
 * </ul>
 * <br><br>
 *
 * Note that any custom class must implement the {@link ScimResource} interface
 * in order to be compatible with the methods above. Furthermore, modifying the
 * BulkResourceMapper with the above methods must only be done at application
 * startup.
 */
public class BulkResourceMapper
{
  /**
   * The map that stores schema-to-class associations for this mapper class.
   */
  // This does not need to be a concurrent map since it should only be
  // configured/modified at application startup.
  @NotNull
  static final HashMap<Set<String>, Class<?>> SCHEMAS_MAP = new HashMap<>();

  static
  {
    initialize();
  }

  /**
   * Updates the BulkResourceMapper with a new class. The provided class must
   * include the {@link Schema} annotation. To add a different
   * ScimResource-based class that does not use the annotation, use the
   * {@link #put put()} method instead.
   *
   * @param clazz  The class to register.
   * @param <T>    The Java type, which must implement ScimResource.
   * @throws IllegalArgumentException  If the provided class does not have the
   *                                   required annotation.
   */
  public static synchronized <T extends ScimResource> void add(
      @NotNull final Class<T> clazz)
          throws IllegalArgumentException
  {
    SchemaResource schema;

    try
    {
      schema = SchemaUtils.getSchema(clazz);
    }
    catch (IntrospectionException e)
    {
      Debug.debugException(e);
      throw new RuntimeException(e);
    }

    if (schema == null || schema.getId() == null)
    {
      throw new IllegalArgumentException("Requested schema for the "
          + clazz.getName()
          + " class, which does not have a valid @Schema annotation.");
    }

    SCHEMAS_MAP.put(Set.of(schema.getId()), clazz);
  }

  /**
   * Updates the BulkResourceMapper with a new class. This is an alternative to
   * the {@link #add(Class)} method that supports usage of Java classes that
   * do not or cannot use the {@code @Schema} annotation.
   *
   * @param schemas  The schemas associated with the resource type.
   * @param clazz    The class type that is associated with the resource type.
   * @param <T>      The returned Java type.
   */
  public static synchronized <T extends ScimResource> void put(
      @NotNull final Set<String> schemas,
      @NotNull final Class<T> clazz)
  {
    Objects.requireNonNull(schemas);
    SCHEMAS_MAP.put(schemas, clazz);
  }

  /**
   * Clears all registered relationships between schemas and class types.
   * <br><br>
   *
   * This should generally not be called unless it is absolutely necessary to
   * overwrite data in the map.
   */
  public static void clear()
  {
    SCHEMAS_MAP.clear();
  }

  /**
   * This utility method is the primary entrypoint to this class, and is
   * responsible for converting JSON data into a ScimResource POJO. The subclass
   * of ScimResource must be defined in the map contained within this class, or
   * a {@link GenericScimResource} will be returned instead.
   *
   * @param json  The JSON to convert.
   * @return      A {@link ScimResource} subclass, or {@code null} if the JSON
   *              was also {@code null}.
   *
   * @throws IllegalArgumentException  If the JSON was malformed.
   */
  @Nullable
  public static ScimResource asScimResource(@Nullable final ObjectNode json)
      throws IllegalArgumentException
  {
    if (json == null)
    {
      return null;
    }

    // Attempt fetching the class using data from the "schemas" array. If there
    // is not a mapping, a GenericScimResource will be used.
    Class<ScimResource> clazz = BulkResourceMapper.get(json.get("schemas"));

    try
    {
      return JsonUtils.nodeToValue(json, clazz);
    }
    catch (JacksonException e)
    {
      throw new IllegalArgumentException(
          "Failed to convert bulk data into a " + clazz.getName(), e);
    }
  }

  /**
   * Fetches the class associated with the provided schema(s). If the schemas
   * are not registered, a {@link GenericScimResource} is returned.
   *
   * @param s    The {@code schemas} value of a SCIM resource.
   * @param <T>  The returned Java type.
   *
   * @return  The class type that is associated with the schemas, or a
   *          {@link GenericScimResource} if one is not defined.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  static <T extends ScimResource> Class<T> get(@NotNull final Set<String> s)
  {
    return (Class<T>) SCHEMAS_MAP.getOrDefault(s, GenericScimResource.class);
  }

  /**
   * Fetches the class associated with the provided schema set. This is an
   * alternative to {@link #get(Set)} which accepts a JsonNode.
   *
   * @param node    The JsonNode representing the value of the {@code schemas}
   *                field on a SCIM resource.
   * @param <T>     The returned Java type.
   *
   * @return  The class type that is associated with the schemas, or a
   *          {@link GenericScimResource} if one is not defined.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  static <T extends ScimResource> Class<T> get(@Nullable final JsonNode node)
  {
    if (!(node instanceof ArrayNode arrayNode))
    {
      // 'schemas' should always be an array as defined by the SCIM standard.
      return (Class<T>) GenericScimResource.class;
    }

    Set<String> schemaSet = new HashSet<>();
    for (var value : arrayNode)
    {
      schemaSet.add(value.asString());
    }

    return get(schemaSet);
  }

  /**
   * Initializes this class by registering basic resource types that are
   * generally applicable to SCIM clients.
   */
  static void initialize()
  {
    clear();
    add(UserResource.class);
    add(GroupResource.class);
    add(ErrorResponse.class);
  }
}
