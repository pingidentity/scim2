/*
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.util.Map;



/**
 * An abstract class which may be implemented to trim resources down to
 * selected attributes.
 */
public abstract class ResourceTrimmer
{
  /**
   * Trim attributes of the object node to return.
   *
   * @param objectNode The object node to return.
   * @return The trimmed object node ready to return to the client.
   */
  @NotNull
  public ObjectNode trimObjectNode(@NotNull final ObjectNode objectNode)
  {
    return trimObjectNode(objectNode, Path.root());
  }

  /**
   * Trim attributes of an inner object node to return.
   *
   * @param objectNode The object node to return.
   * @param parentPath  The parent path of attributes in the object.
   * @return The trimmed object node ready to return to the client.
   */
  @NotNull
  private ObjectNode trimObjectNode(@NotNull final ObjectNode objectNode,
                                    @NotNull final Path parentPath)
  {
    ObjectNode objectToReturn = JsonUtils.getJsonNodeFactory().objectNode();
    for (Map.Entry<String, JsonNode> field : objectNode.properties())
    {
      final Path path;
      if (parentPath.isRoot() && parentPath.getSchemaUrn() == null &&
          SchemaUtils.isUrn(field.getKey()))
      {
        path = Path.root(field.getKey());
      }
      else
      {
        path = parentPath.attribute(field.getKey());
      }

      if (path.isRoot() || shouldReturn(path))
      {
        if (field.getValue() instanceof ArrayNode valueArray)
        {
          ArrayNode trimmedNode = trimArrayNode(valueArray, path);
          if (!trimmedNode.isEmpty())
          {
            objectToReturn.set(field.getKey(), trimmedNode);
          }
        }
        else if (field.getValue() instanceof ObjectNode valueObject)
        {
          ObjectNode trimmedNode = trimObjectNode(valueObject, path);
          if (!trimmedNode.isEmpty())
          {
            objectToReturn.set(field.getKey(), trimmedNode);
          }
        }
        else
        {
          objectToReturn.set(field.getKey(), field.getValue());
        }
      }
    }
    return objectToReturn;
  }

  /**
   * Trim attributes of the values in the array node to return.
   *
   * @param arrayNode The array node to return.
   * @param parentPath  The parent path of attributes in the array.
   * @return The trimmed object node ready to return to the client.
   */
  @NotNull
  protected ArrayNode trimArrayNode(@NotNull final ArrayNode arrayNode,
                                    @NotNull final Path parentPath)
  {
    ArrayNode arrayToReturn = JsonUtils.getJsonNodeFactory().arrayNode();
    for (JsonNode value : arrayNode)
    {
      if (value instanceof ArrayNode valueArray)
      {
        ArrayNode trimmedNode = trimArrayNode(valueArray, parentPath);
        if (!trimmedNode.isEmpty())
        {
          arrayToReturn.add(trimmedNode);
        }
      }
      else if (value instanceof ObjectNode valueObject)
      {
        ObjectNode trimmedNode = trimObjectNode(valueObject, parentPath);
        if (!trimmedNode.isEmpty())
        {
          arrayToReturn.add(trimmedNode);
        }
      }
      else
      {
        arrayToReturn.add(value);
      }
    }
    return arrayToReturn;
  }

  /**
   * Determine if the attribute specified by the path should be returned.
   *
   * @param path The path for the attribute.
   * @return {@code true} to return the attribute or {@code false} to remove the
   * attribute from the returned resource.
   */
  public abstract boolean shouldReturn(@NotNull final Path path);
}
