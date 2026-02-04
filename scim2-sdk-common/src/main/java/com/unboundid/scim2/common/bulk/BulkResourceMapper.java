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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * This class is used to simplify the process of obtaining Java objects from
 * bulk responses.
 * <br><br>
 *
 * When dealing with bulk responses from a SCIM service, the result of an
 * operation will often include a JSON payload. This JSON (stored in the
 * {@code data} field) contains the contents that the SCIM client would have
 * received if it had sent the single request on its own. For example:
 * <pre>
 *  {
 *    "location" : "/Users/fa1afe1",
 *    "method" : "POST",
 *    "bulkId" : "originalBulkId",
 *    "response" : {
 *      "schemas" : [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *      "userName" : "name"
 *    },
 *    "status" : "200"
 *  }
 * </pre>
 * <br><br>
 *
 * When it comes to handling and interpreting this data, the problem is that it
 * is tedious to obtain the resource and convert it from JSON to a usable POJO.
 * The SCIM standard allows these definitions to be returned in the
 * {@code response}:
 * <ul>
 *   <li> {@link UserResource}, representing a user.
 *   <li> {@link GroupResource}, representing a group entity.
 *   <li> {@link ErrorResponse}, representing an error that occurred.
 *   <li> Any other custom resource type defined by a SCIM service provider.
 * </ul>
 * <br><br>
 *
 * The definitive answer for the resource type is shared in the
 * {@code schemas} attribute. However, fetching this data and manually
 * converting the object with the {@link JsonUtils#nodeToValue} method is extra
 * work that the SCIM SDK aims to simplify.
 * <br><br>
 *
 * To solve this problem, this BulkResourceMapper class defines associations
 * between schemas (represented as sets) and a Java type. If you have custom
 * resources that need to be supported by the SCIM SDK's bulk processing, then
 * add it with one of the following methods. Note that the associated class
 * must be a {@link ScimResource} object.
 * <ul>
 *   <li> {@link #add(Class)}: If the class uses the {@link Schema} annotation.
 *   <li> {@link #put}: To define a direct relationship for any class.
 * </ul>
 * <br><br>
 *
 * By default, the SCIM SDK always defines the default resource types defined in
 * the standard (e.g., {@link UserResource}). If desired, the default values
 * may be deleted by invoking the {@link #clear()} method and manually adding
 * the desired class definitions.
 */
public class BulkResourceMapper
{
  /**
   * The map that backs this mapper class. This does not need to be a concurrent
   * map since it should only be configured/modified at application startup.
   */
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
   * {@link #put} method instead.
   *
   * @param clazz  The class to register.
   * @param <T>    The Java type, which must implement ScimResource.
   * @throws IllegalArgumentException  If the provided class does not have the
   *                                   required annotation.
   */
  public static <T extends ScimResource> void add(
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
  public static <T extends ScimResource> void put(
      @NotNull final Set<String> schemas,
      @NotNull final Class<T> clazz)
  {
    Objects.requireNonNull(schemas);
    SCHEMAS_MAP.put(schemas, clazz);
  }

  /**
   * Fetches the class associated with the provided schema set. If the schemas
   * are not registered, a {@link GenericScimResource} is returned.
   *
   * @param schemas   The schemas for a SCIM resource.
   * @param <T>       The returned Java type.
   *
   * @return  The class type that is associated with the schemas, or a
   *          {@link GenericScimResource} if one is not defined.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  public static <T extends ScimResource> Class<T> get(
      @NotNull final Set<String> schemas)
  {
    return (Class<T>) SCHEMAS_MAP.getOrDefault(schemas,
        GenericScimResource.class);
  }

  /**
   * Fetches the class associated with the provided schema set. This is an
   * alternative to {@link #get(Set)} which accepts a JsonNode.
   *
   * @param jsonNode    The JsonNode representing the value of the
   *                    {@code schemas} field on a SCIM resource.
   * @param <T>         The returned Java type.
   *
   * @return  The class type that is associated with the schemas, or a
   *          {@link GenericScimResource} if one is not defined.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  public static <T extends ScimResource> Class<T> get(
      @Nullable final JsonNode jsonNode)
  {
    if (!(jsonNode instanceof ArrayNode arrayNode))
    {
      return (Class<T>) GenericScimResource.class;
    }

    Set<String> schemaSet = new HashSet<>();
    for (var value : arrayNode)
    {
      schemaSet.add(value.asText());
    }

    return get(schemaSet);
  }

  /**
   * Clears all registered relationships between schemas and class types.
   */
  public static void clear()
  {
    SCHEMAS_MAP.clear();
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
