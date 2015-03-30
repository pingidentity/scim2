/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;
import com.unboundid.scim2.filters.FilterEvaluator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility methods to manipulate JSON nodes using paths.
 */
public class JsonUtils
{
  /**
   * Retrieve all JSON nodes referenced by the provided path. If a path
   * references a JSON array, all nodes the the array will be traversed.
   *
   * @param path The path to the attributes whose values to retrieve.
   * @param node The JSON node representing the SCIM resource.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws SCIMException If an error occurs while traversing the JSON node.
   */
  public static List<JsonNode> gatherValues(final Path path,
                                            final JsonNode node)
      throws SCIMException
  {
    List<JsonNode> values = new LinkedList<JsonNode>();
    gatherValuesInternal(values, node, 0, path);
    return values;
  }

  /**
   * Internal method to recursively gather values based on path.
   *
   * @param values The currently retrieved values.
   * @param node The JSON node representing the SCIM resource.
   * @param index The index to the current path element.
   * @param path The path to the attributes whose values to retrieve.
   *
   * @throws SCIMException If an error occurs while traversing the JSON node.
   */
  private static void gatherValuesInternal(final List<JsonNode> values,
                                           final JsonNode node,
                                           final int index,
                                           final Path path)
      throws SCIMException
  {
    if(index >= path.getElements().size())
    {
      values.add(node);
      return;
    }

    Path.Element element = path.getElements().get(index);
    JsonNode child = node.path(element.getAttribute());
    if(child.isMissingNode())
    {
      return;
    }

    if(child.isArray())
    {
      Iterator<JsonNode> i = child.elements();
      while(i.hasNext())
      {
        JsonNode childNode = i.next();
        if(element.getValueFilter() != null)
        {
          if(FilterEvaluator.evaluate(element.getValueFilter(), childNode))
          {
            gatherValuesInternal(values, childNode, index + 1, path);
          }
        }
        else
        {
          gatherValuesInternal(values, childNode, index + 1, path);
        }
      }
    }
    else
    {
      gatherValuesInternal(values, child, index + 1, path);
    }
  }
}
