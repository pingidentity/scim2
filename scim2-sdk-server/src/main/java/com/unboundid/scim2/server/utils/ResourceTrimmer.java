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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.util.Iterator;
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
  public ObjectNode trimObjectNode(final ObjectNode objectNode)
  {
    ObjectNode objectToReturn = JsonUtils.getJsonNodeFactory().objectNode();
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      final boolean isUrn = SchemaUtils.isUrn(field.getKey());
      final Path path;
      if (isUrn)
      {
        path = Path.root(field.getKey());
      }
      else
      {
        path = Path.root().attribute(field.getKey());
      }

      if(isUrn || shouldReturn(path))
      {
        if (field.getValue().isArray())
        {
          objectToReturn.set(field.getKey(), trimArrayNode(
              (ArrayNode) field.getValue(), path));
        }
        else if (field.getValue().isObject())
        {
          objectToReturn.set(field.getKey(), trimObjectNodeInner(
              (ObjectNode) field.getValue(), path));
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
   * Trim attributes of an inner object node to return.
   *
   * @param objectNode The object node to return.
   * @param parentPath  The parent path of attributes in the object.
   * @return The trimmed object node ready to return to the client.
   */
  private ObjectNode trimObjectNodeInner(final ObjectNode objectNode,
                                         final Path parentPath)
  {
    ObjectNode objectToReturn = JsonUtils.getJsonNodeFactory().objectNode();
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      Path path = parentPath.attribute(field.getKey());
      if(shouldReturn(path))
      {
        if (field.getValue().isArray())
        {
          objectToReturn.set(field.getKey(), trimArrayNode(
              (ArrayNode) field.getValue(), path));
        }
        else if (field.getValue().isObject())
        {
          objectToReturn.set(field.getKey(), trimObjectNodeInner(
              (ObjectNode) field.getValue(), path));
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
  protected ArrayNode trimArrayNode(final ArrayNode arrayNode,
                                    final Path parentPath)
  {
    ArrayNode arrayToReturn = JsonUtils.getJsonNodeFactory().arrayNode();
    for(JsonNode value : arrayNode)
    {
      if(value.isArray())
      {
        arrayToReturn.add(trimArrayNode((ArrayNode) value,
            parentPath));
      }
      else if(value.isObject())
      {
        arrayToReturn.add(trimObjectNodeInner((ObjectNode) value,
                                              parentPath));
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
   * attribute from the returned resource..
   */
  public abstract boolean shouldReturn(final Path path);
}
