package com.unboundid.scim2.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;
import com.unboundid.scim2.filters.FilterEvaluator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by boli on 3/26/15.
 */
public class JsonUtils
{
  public static List<JsonNode> gatherValues(Path path, JsonNode node)
      throws SCIMException
  {
    List<JsonNode> values = new LinkedList<JsonNode>();
    gatherValuesInternal(values, node, 0, path);
    return values;
  }

  private static void gatherValuesInternal(List<JsonNode> values,
                                           JsonNode node,
                                           int index, Path path)
      throws SCIMException
  {
    if(index >= path.getElements().size())
    {
      values.add(node);
      return;
    }

    Path.Element element = path.getElements().get(index++);
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
            gatherValuesInternal(values, childNode, index, path);
          }
        }
        else
        {
          gatherValuesInternal(values, childNode, index, path);
        }
      }
    }
    else
    {
      gatherValuesInternal(values, child, index, path);
    }
  }
}
