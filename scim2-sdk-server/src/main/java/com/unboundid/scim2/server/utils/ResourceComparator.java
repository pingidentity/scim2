/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Comparator;
import java.util.List;

/**
 * A comparator implementation that could be used to compare POJOs representing
 * SCIM resources using the SCIM sorting parameters.
 */
public class ResourceComparator<T extends ScimResource>
    implements Comparator<T>
{
  @NotNull
  private final Path sortBy;

  @NotNull
  private final SortOrder sortOrder;

  @Nullable
  private final ResourceTypeDefinition resourceType;

  /**
   * Create a new ScimComparator that will sort in ascending order.
   *
   * @param sortBy The path to the attribute to sort by.
   * @param resourceType The resource type definition containing the schemas or
   *                     {@code null} to compare using case-insensitive matching
   *                     for string values.
   */
  public ResourceComparator(@NotNull final Path sortBy,
                            @Nullable final ResourceTypeDefinition resourceType)
  {
    this(sortBy, SortOrder.ASCENDING, resourceType);
  }

  /**
   * Create a new ScimComparator.
   *
   * @param sortBy The path to the attribute to sort by.
   * @param sortOrder The sort order.
   * @param resourceType The resource type definition containing the schemas or
   *                     {@code null} to compare using case-insensitive matching
   *                     for string values.
   */
  public ResourceComparator(@NotNull final Path sortBy,
                            @Nullable final SortOrder sortOrder,
                            @Nullable final ResourceTypeDefinition resourceType)
  {
    this.sortBy = sortBy;
    this.sortOrder = sortOrder == null ? SortOrder.ASCENDING : sortOrder;
    this.resourceType = resourceType;
  }

  /**
   * {@inheritDoc}
   */
  public int compare(@NotNull final T o1, @NotNull final T o2)
  {
    ObjectNode n1 = o1.asGenericScimResource().getObjectNode();
    ObjectNode n2 = o2.asGenericScimResource().getObjectNode();

    JsonNode v1 = null;
    JsonNode v2 = null;

    try
    {
      List<JsonNode> v1s = JsonUtils.findMatchingPaths(sortBy, n1);
      if (!v1s.isEmpty())
      {
        // Always just use the primary or first value of the first found node.
        v1 = getPrimaryOrFirst(v1s.get(0));
      }
    }
    catch (ScimException e)
    {
      Debug.debugException(e);
    }

    try
    {
      List<JsonNode> v2s = JsonUtils.findMatchingPaths(sortBy, n2);
      if (!v2s.isEmpty())
      {
        // Always just use the primary or first value of the first found node.
        v2 = getPrimaryOrFirst(v2s.get(0));
      }
    }
    catch (ScimException e)
    {
      Debug.debugException(e);
    }

    if (v1 == null && v2 == null)
    {
      return 0;
    }
    // or all attribute types, if there is no data for the specified "sortBy"
    // value they are sorted via the "sortOrder" parameter; i.e., they are
    // ordered last if ascending and first if descending.
    else if (v1 == null)
    {
      return sortOrder == SortOrder.ASCENDING ? 1 : -1;
    }
    else if (v2 == null)
    {
      return sortOrder == SortOrder.ASCENDING ? -1 : 1;
    }
    else
    {
      AttributeDefinition attributeDefinition =
          resourceType == null ? null :
              resourceType.getAttributeDefinition(sortBy);
      return sortOrder == SortOrder.ASCENDING ?
          JsonUtils.compareTo(v1, v2, attributeDefinition) :
          JsonUtils.compareTo(v2, v1, attributeDefinition);
    }
  }

  /**
   * Retrieve the value of a complex multi-valued attribute that is marked as
   * primary or the first value in the list. If the provided node is not an
   * array node, then just return the provided node.
   *
   * @param node The JsonNode to retrieve from.
   * @return The primary or first value or {@code null} if the provided array
   * node is empty.
   */
  @Nullable
  private JsonNode getPrimaryOrFirst(@NotNull final JsonNode node)
  {
    // if it's a multi-valued attribute (see Section 2.4
    // [I-D.ietf - scim - core - schema]), if any, or else the first value in
    // the list, if any.

    if (!node.isArray())
    {
      return node;
    }

    if (node.isEmpty())
    {
      return null;
    }

    for (JsonNode value : node)
    {
      JsonNode primary = value.get("primary");
      if (primary != null && primary.booleanValue())
      {
        return value;
      }
    }
    return node.get(0);
  }
}
