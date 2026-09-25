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

import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.ScimResourceMapper;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import tools.jackson.databind.node.ObjectNode;

import java.util.Set;


/**
 * This class was the first implementation of {@link ScimResourceMapper}. It was
 * renamed from "BulkResourceMapper" to "ScimResourceMapper" so that the mapper
 * is more general-purpose. This allows using the mapper for similar JSON
 * conversion tasks that are unrelated bulk processing.
 *
 * @deprecated Use the ScimResourceMapper class instead, which contains all the
 *             same methods as this class.
 */
@Deprecated(since = "6.1.0")
public class BulkResourceMapper
{
  /**
   * Wrapper for {@link ScimResourceMapper}.
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
    ScimResourceMapper.add(clazz);
  }

  /**
   * Wrapper for {@link ScimResourceMapper}.
   *
   * @param schemas  The schemas associated with the resource type.
   * @param clazz    The class type that is associated with the resource type.
   * @param <T>      The returned Java type.
   */
  public static synchronized <T extends ScimResource> void put(
      @NotNull final Set<String> schemas,
      @NotNull final Class<T> clazz)
  {
    ScimResourceMapper.put(schemas, clazz);
  }

  /**
   * Wrapper for {@link ScimResourceMapper}.
   */
  public static void clear()
  {
    ScimResourceMapper.clear();
  }

  /**
   * Wrapper for {@link ScimResourceMapper}.
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
    return ScimResourceMapper.asScimResource(json);
  }
}
