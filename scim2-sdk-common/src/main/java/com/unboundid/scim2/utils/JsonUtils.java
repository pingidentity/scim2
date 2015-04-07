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

package com.unboundid.scim2.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.BadRequestException;
import com.unboundid.scim2.exceptions.ScimException;
import com.unboundid.scim2.filters.Filter;
import com.unboundid.scim2.filters.FilterEvaluator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility methods to manipulate JSON nodes using paths.
 */
public class JsonUtils
{
  private abstract static class NodeVisitor
  {
    /**
     * Visit a node referenced by an path element before that last element.
     *
     * @param parent The parent container ObjectNode.
     * @param element The path element.
     * @return The JsonNode referenced by the element in the parent.
     * @throws ScimException If an error occurs.
     */
    abstract JsonNode visitInnerNode(final ObjectNode parent,
                                     final Path.Element element)
        throws ScimException;

    /**
     * Visit a node referenced by the last path element.
     *
     * @param parent The parent container ObjectNode.
     * @param element The path element.
     * @throws ScimException If an error occurs.
     */
    abstract void visitLeafNode(final ObjectNode parent,
                                final Path.Element element)
        throws ScimException;

    /**
     *
     * @param array The ArrayNode to filter.
     * @param valueFilter The value filter.
     * @param removeMatching {@code true} to remove matching values or
     *                       {@code false} otherwise.
     * @return The matching values.
     * @throws ScimException If an error occurs.
     */
    ArrayNode filterArray(final ArrayNode array, final Filter valueFilter,
                          final boolean removeMatching)
        throws ScimException
    {
      ArrayNode matchingArray = JsonNodeFactory.instance.arrayNode();
      Iterator<JsonNode> i = array.elements();
      while(i.hasNext())
      {
        JsonNode node = i.next();
        if(node.isObject() &&
            FilterEvaluator.evaluate(valueFilter, (ObjectNode)node))
        {
          matchingArray.add(node);
          if(removeMatching)
          {
            i.remove();
          }
        }
      }
      return matchingArray;
    }
  }


  private static final class GatheringNodeVisitor extends NodeVisitor
  {
    final List<JsonNode> values = new LinkedList<JsonNode>();
    final boolean removeValues;

    /**
     * Create a new GatheringNodeVisitor.
     *
     * @param removeValues {@code true} to remove the gathered values from
     *                     the container node or {@code false} otherwise.
     */
    private GatheringNodeVisitor(final boolean removeValues)
    {
      this.removeValues = removeValues;
    }

    /**
     * {@inheritDoc}
     */
    JsonNode visitInnerNode(final ObjectNode parent,
                            final Path.Element element)
        throws ScimException
    {
      JsonNode node = parent.path(element.getAttribute());
      if(node.isArray() && element.getValueFilter() != null)
      {
        return filterArray((ArrayNode)node, element.getValueFilter(), false);
      }
      return node;
    }

    /**
     * {@inheritDoc}
     */
    void visitLeafNode(final ObjectNode parent,
                       final Path.Element element) throws ScimException
    {
      JsonNode node = parent.path(element.getAttribute());
      if(node.isArray())
      {
        ArrayNode arrayNode = (ArrayNode) node;
        if(element.getValueFilter() != null)
        {
          arrayNode = filterArray((ArrayNode) node, element.getValueFilter(),
              removeValues);
        }
        for(JsonNode v : arrayNode)
        {
          values.add(v);
        }
        if(removeValues && node.size() == 0)
        {
          // There is no more values left after removing the matching values.
          // Just remove the field.
          parent.remove(element.getAttribute());
        }
      }
      else if(node.isObject() || node.isValueNode())
      {
        values.add(node);
        if(removeValues)
        {
          parent.remove(element.getAttribute());
        }
      }
    }
  }

  private static final class UpdatingNodeVisitor extends NodeVisitor
  {
    private final JsonNode value;
    private final boolean appendValues;

    /**
     * Create a new UpdatingNodeVisitor.
     *
     * @param value The update value.
     * @param appendValues {@code true} to append the update value or
     *                     {@code false} otherwise.
     */
    private UpdatingNodeVisitor(final JsonNode value,
                                final boolean appendValues)
    {
      this.value = value;
      this.appendValues = appendValues;
    }

    /**
     * {@inheritDoc}
     */
    JsonNode visitInnerNode(final ObjectNode parent,
                            final Path.Element element)
        throws ScimException
    {
      JsonNode node = parent.path(element.getAttribute());
      if(node.isValueNode() || ((node.isMissingNode() || node.isNull()) &&
          element.getValueFilter() != null))
      {
        throw BadRequestException.noTarget("Attribute " +
            element.getAttribute() + " does not have a multi-valued or " +
            "complex value");
      }
      if(node.isMissingNode() || node.isNull())
      {
        // Create the missing node as an JSON object node.
        ObjectNode newObjectNode = JsonNodeFactory.instance.objectNode();
        parent.put(element.getAttribute(), newObjectNode);
        return newObjectNode;
      }
      else if(node.isArray())
      {
        ArrayNode arrayNode = (ArrayNode) node;
        if(element.getValueFilter() != null)
        {
          arrayNode =
              filterArray((ArrayNode)node, element.getValueFilter(), false);
        }
        if(arrayNode.size() == 0)
        {
          throw BadRequestException.noTarget("Attribute " +
              element.getAttribute() + " does not have a value matching the " +
              "filter " + element.getValueFilter().toString());
        }
        return arrayNode;
      }
      return node;
    }

    /**
     * {@inheritDoc}
     */
    void visitLeafNode(final ObjectNode parent,
                       final Path.Element element)
        throws ScimException
    {
      String attributeName = null;
      if(element != null)
      {
        attributeName = element.getAttribute();
        JsonNode node = parent.path(attributeName);
        if (!appendValues && node.isArray() &&
            element.getValueFilter() != null && value.isObject())
        {
          // If the target is an array and there is a value filter, make sure
          // it matches at least one value.
          boolean matchesFound = false;
          for(JsonNode matchingValues :
              filterArray((ArrayNode) node, element.getValueFilter(), false))
          {
            matchesFound = true;
            updateValues((ObjectNode)matchingValues, null, value);
          }
          if(!matchesFound)
          {
            throw BadRequestException.noTarget("Attribute " +
                element.getAttribute() + " does not have a value matching " +
                "the filter " + element.getValueFilter().toString());
          }
          return;
        }
      }
      updateValues(parent, attributeName, value);
    }

    /**
     * Update the value(s) of the field specified by the key in the parent
     * container node.
     *
     * @param parent The container node.
     * @param key The key of the field to update.
     * @param value The update value.
     */
    private void updateValues(final ObjectNode parent, final String key,
                              final JsonNode value)
    {
      if(value.isNull() || value.isArray() && value.size() == 0)
      {
        // draft-ietf-scim-core-schema section 2.4 states "Unassigned
        // attributes, the null value, or empty array (in the case of
        // a multi-valued attribute) SHALL be considered to be
        // equivalent in "state".
        return ;
      }
      // When key is null, the node to update is the parent it self.
      JsonNode node = key == null ? parent : parent.path(key);
      if(node.isObject())
      {
        if(value.isObject())
        {
          // Go through the fields of both objects and merge them.
          ObjectNode targetObject = (ObjectNode) node;
          ObjectNode valueObject = (ObjectNode) value;
          Iterator<Map.Entry<String, JsonNode>> i = valueObject.fields();
          while (i.hasNext())
          {
            Map.Entry<String, JsonNode> field = i.next();
            updateValues(targetObject, field.getKey(), field.getValue());
          }
        }
        else
        {
          // Replace the field.
          parent.put(key, value);
        }
      }
      else if(node.isArray())
      {
        if(value.isArray() && appendValues)
        {
          // Append the new values to the existing ones.
          ArrayNode targetArray = (ArrayNode) node;
          ArrayNode valueArray = (ArrayNode) value;
          targetArray.addAll(valueArray);
        }
        else
        {
          // Replace the field.
          parent.put(key, value);
        }
      }
      else
      {
        // Replace the field.
        parent.put(key, value);
      }
    }
  }

  /**
   * Retrieve all JSON nodes referenced by the provided path. If a path
   * references a JSON array, all nodes the the array will be traversed.
   *
   * @param path The path to the attributes whose values to retrieve.
   * @param node The JSON node representing the SCIM resource.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static List<JsonNode> getValues(final Path path,
                                         final ObjectNode node)
      throws ScimException
  {
    GatheringNodeVisitor visitor = new GatheringNodeVisitor(false);
    traverseValues(visitor, node, 0, path);
    return visitor.values;
  }

  /**
   * Add a new value at the provided path to the provided JSON node. If the path
   * contains any value filters, they will be ignored. The following processing
   * rules are applied depending on the path and value to add:
   *
   * <ul>
   *   <li>
   *     If the path is a root path and targets the core or extension
   *     attributes, the value must be a JSON object containing the
   *     set of attributes to be added to the resource.
   *   </li>
   *   <li>
   *     If the path does not exist, the attribute and value is added.
   *   </li>
   *   <li>
   *     If the path targets a complex attribute (an attribute whose value is
   *     a JSON Object), the value must be a JSON object containing the
   *     set of sub-attributes to be added to the complex value.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute (an attribute whose value
   *     if a JSON Array), the value to add must be a JSON array containing the
   *     set of values to be added to the attribute.
   *   </li>
   *   <li>
   *     If the path targets a single-valued attribute, the existing value is
   *     replaced.
   *   </li>
   *   <li>
   *     If the path targets an attribute that does not exist (has not value),
   *     the attribute is added with the new value.
   *   </li>
   *   <li>
   *     If the path targets an existing attribute, the value is replaced.
   *   </li>
   *   <li>
   *     If the path targets an existing attribute which already contains the
   *     value specified, no changes will be made to the node.
   *   </li>
   * </ul>
   *
   * @param path The path to the attribute.
   * @param node The JSON object node containing the attribute.
   * @param value The value to add.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static void addValue(final Path path, final ObjectNode node,
                              final JsonNode value) throws ScimException
  {
    UpdatingNodeVisitor visitor = new UpdatingNodeVisitor(value, true);
    traverseValues(visitor, node, 0, path);
  }

  /**
   * Remove the value at the provided path. The following processing
   * rules are applied:
   *
   * <ul>
   *   <li>
   *     If the path targets a single-valued attribute, the attribute and its
   *     associated value is removed.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute and no value filter is
   *     specified, the attribute and all values are removed.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute and a value filter is
   *     specified, the values matched by the filter are removed. If after
   *     removal of the selected values, no other values remain, the
   *     multi-valued attribute is removed.
   *   </li>
   * </ul>
   *
   * @param path The path to the attribute.
   * @param node The JSON object node containing the attribute.
   * @return The list of nodes that were removed.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static List<JsonNode> removeValues(final Path path,
                                            final ObjectNode node)
      throws ScimException
  {
    GatheringNodeVisitor visitor = new GatheringNodeVisitor(true);
    traverseValues(visitor, node, 0, path);
    return visitor.values;
  }

  /**
   * Update the value at the provided path. The following processing rules are
   * applied:
   *
   * <ul>
   *   <li>
   *     If the path is a root path and targets the core or extension
   *     attributes, the value must be a JSON object containing the
   *     set of attributes to be replaced on the resource.
   *   </li>
   *   <li>
   *     If the path targets a single-valued attribute, the attribute's value
   *     is replaced.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute and no value filter is
   *     specified, the attribute and all values are replaced.
   *   </li>
   *   <li>
   *     If the path targets an attribute that does not exist, treat the
   *     operation as an add.
   *   </li>
   *   <li>
   *     If the path targets a complex attribute (an attribute whose value is
   *     a JSON Object), the value must be a JSON object containing the
   *     set of sub-attributes to be replaced in the complex value.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute and a value filter is
   *     specified that matches one or more values of the multi-valued
   *     attribute, then all matching record values will be replaced.
   *   </li>
   *   <li>
   *     If the path targets a complex multi-valued attribute with a value
   *     filter and a specific sub-attribute
   *     (e.g. "addresses[type eq "work"].streetAddress"), the matching
   *     sub-attribute of all matching records is replaced.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute for which a value filter
   *     is specified and no records match was made, the NoTarget exception
   *     will be thrown.
   *   </li>
   * </ul>
   * @param path The path to the attribute.
   * @param node The JSON object node containing the attribute.
   * @param value The replacement value.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static void replaceValue(final Path path,
                                  final ObjectNode node,
                                  final JsonNode value) throws ScimException
  {
    UpdatingNodeVisitor visitor = new UpdatingNodeVisitor(value, false);
    traverseValues(visitor, node, 0, path);
  }

  /**
   * Internal method to recursively gather values based on path.
   *
   * @param nodeVisitor The NodeVisitor to use to handle the traversed nodes.
   * @param node The JSON node representing the SCIM resource.
   * @param index The index to the current path element.
   * @param path The path to the attributes whose values to retrieve.
   *
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  private static void traverseValues(final NodeVisitor nodeVisitor,
                                     final ObjectNode node,
                                     final int index,
                                     final Path path)
      throws ScimException
  {
    Path.Element element = path.isRoot() ? null : path.getElements().get(index);
    if(index < path.getElements().size() - 1)
    {
      JsonNode child = nodeVisitor.visitInnerNode(node, element);
      if(child.isArray())
      {
        for(JsonNode value : child)
        {
          if(value.isObject())
          {
            traverseValues(nodeVisitor, (ObjectNode)value, index + 1, path);
          }
        }
      }
      else if(child.isObject())
      {
        traverseValues(nodeVisitor, (ObjectNode)child, index + 1, path);
      }
    }
    else
    {
      nodeVisitor.visitLeafNode(node, element);
    }
  }
}
