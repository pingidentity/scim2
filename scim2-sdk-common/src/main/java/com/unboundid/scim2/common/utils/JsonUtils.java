/*
 * Copyright 2015-2024 Ping Identity Corporation
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

package com.unboundid.scim2.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility methods to manipulate JSON nodes using paths.
 */
public class JsonUtils
{
  @NotNull
  private static MapperFactory mapperFactory = new MapperFactory();

  @NotNull
  private static ObjectMapper SDK_OBJECT_MAPPER = createObjectMapper();

  public abstract static class NodeVisitor
  {
    /**
     * Visit a node referenced by a path element before that last element.
     *
     * @param parent The parent container ObjectNode.
     * @param field The field to visit.
     * @param valueFilter The filter for the value(s) to visit.
     * @return The JsonNode referenced by the element in the parent.
     * @throws ScimException If an error occurs.
     */
    @NotNull
    abstract JsonNode visitInnerNode(@NotNull final ObjectNode parent,
                                     @Nullable final String field,
                                     @NotNull final Filter valueFilter)
        throws ScimException;

    /**
     * Visit a node referenced by the last path element.
     *
     * @param parent The parent container ObjectNode.
     * @param field The field to visit.
     * @param valueFilter the filter for the value(s) to visit.
     * @throws ScimException If an error occurs.
     */
    abstract void visitLeafNode(@NotNull final ObjectNode parent,
                                @Nullable final String field,
                                @Nullable final Filter valueFilter)
        throws ScimException;

    /**
     * Identifies the elements within an ArrayNode that match a filter. This
     * method can optionally remove the elements/values that were matched.
     *
     * @param array The ArrayNode to filter.
     * @param valueFilter The value filter.
     * @param removeMatching {@code true} to remove matching values or
     *                       {@code false} otherwise.
     * @return The matching values.
     * @throws ScimException If an error occurs.
     */
    @NotNull
    ArrayNode filterArray(@NotNull final ArrayNode array,
                          @NotNull final Filter valueFilter,
                          final boolean removeMatching)
        throws ScimException
    {
      ArrayNode matchingArray = getJsonNodeFactory().arrayNode();
      Iterator<JsonNode> i = array.elements();
      while(i.hasNext())
      {
        JsonNode node = i.next();
        if(FilterEvaluator.evaluate(valueFilter, node))
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
    @NotNull
    final List<JsonNode> values = new LinkedList<>();

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
    @NotNull
    JsonNode visitInnerNode(@NotNull final ObjectNode parent,
                            @Nullable final String field,
                            @Nullable final Filter valueFilter)
        throws ScimException
    {
      JsonNode node = parent.path(field);
      if(node.isArray() && valueFilter != null)
      {
        return filterArray((ArrayNode) node, valueFilter, false);
      }
      return node;
    }

    /**
     * {@inheritDoc}
     */
    void visitLeafNode(@NotNull final ObjectNode parent,
                       @Nullable final String field,
                       @Nullable final Filter valueFilter)
        throws ScimException
    {
      JsonNode node = parent.path(field);
      if(node.isArray())
      {
        ArrayNode arrayNode = (ArrayNode) node;

        if(valueFilter != null)
        {
          arrayNode = filterArray((ArrayNode) node, valueFilter,
              removeValues);
        }
        if (arrayNode.size() > 0)
        {
          values.add(arrayNode);
        }

        if(removeValues && (valueFilter == null || node.size() == 0))
        {
          // There are no more values left after removing the matching values.
          // Just remove the field.
          parent.remove(field);
        }
      }
      else if(node.isObject() || node.isValueNode())
      {
        values.add(node);
        if(removeValues)
        {
          parent.remove(field);
        }
      }
    }
  }

  public static class UpdatingNodeVisitor extends NodeVisitor
  {
    /**
     * The updated value.
     */
    @NotNull
    protected final JsonNode value;

    /**
     * Whether to append or replace array values.
     */
    protected final boolean appendValues;


    /**
     * Create a new UpdatingNodeVisitor.
     *
     * @param value The update value.
     * @param appendValues {@code true} to append the update value or
     *                     {@code false} otherwise.
     */
    protected UpdatingNodeVisitor(@NotNull final JsonNode value,
                                  final boolean appendValues)
    {
      this.value = value.deepCopy();
      this.appendValues = appendValues;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    protected JsonNode visitInnerNode(@NotNull final ObjectNode parent,
                                      @Nullable final String field,
                                      @Nullable final Filter valueFilter)
        throws ScimException
    {
      JsonNode node = parent.path(field);
      if(node.isValueNode() || ((node.isMissingNode() || node.isNull()) &&
          valueFilter != null))
      {
        throw BadRequestException.noTarget("Attribute " +
            field + " does not have a multi-valued or " +
            "complex value");
      }
      if(node.isMissingNode() || node.isNull())
      {
        // Create the missing node as an JSON object node.
        ObjectNode newObjectNode = getJsonNodeFactory().objectNode();
        parent.set(field, newObjectNode);
        return newObjectNode;
      }
      else if(node.isArray())
      {
        ArrayNode arrayNode = (ArrayNode) node;
        if(valueFilter != null)
        {
          arrayNode = filterArray(arrayNode, valueFilter, false);
          if(arrayNode.size() == 0)
          {
            throw BadRequestException.noTarget("Attribute " +
                field + " does not have a value matching the " +
                "filter " + valueFilter);
          }
        }
        return arrayNode;
      }
      return node;
    }

    /**
     * {@inheritDoc}
     */
    protected void visitLeafNode(@NotNull final ObjectNode parent,
                                 @Nullable final String field,
                                 @Nullable final Filter valueFilter)
        throws ScimException
    {
      boolean matchesFound = false;

      if (field == null)
      {
        updateNode(parent, null, value);
        return;
      }

      // Operations without a value filter can be updated normally without any
      // special considerations. Note that add operations with value filters are
      // already handled in PatchOperation#applyAddWithValueFilter.
      if (appendValues || valueFilter == null)
      {
        updateNode(parent, field, value);
        return;
      }

      // in replace mode, a value filter requires that the target node
      // be an array and that we can find matching value(s)
      JsonNode node = parent.path(field);
      if (node.isArray())
      {
        var array = (ArrayNode) node;
        matchesFound = processArrayReplace(parent, field, array, valueFilter);
      }
      // exception: this allows filters on singular values if
      // and only if the filter uses the "value" attribute to
      // reference the value of the value node.
      else if (FilterEvaluator.evaluate(valueFilter, node))
      {
        matchesFound = true;
        updateNode(parent, field, value);
      }

      if (!matchesFound)
      {
        throw BadRequestException.noTarget("Attribute " +
            field + " does not have a value matching " +
            "the filter " + valueFilter);
      }
    }

    /**
     * This method processes a replace operation for a multi-valued attribute
     * (e.g., {@code emails, addresses}) with a value selection filter. If the
     * {@code value} of the patch operation is an empty array, then attribute
     * values that match the filter will be removed from the target resource.
     *
     * @param parent        The resource that contains the multi-valued
     *                      attribute.
     * @param field         The name of the multi-valued attribute (e.g.,
     *                      {@code emails}).
     * @param array         The current value of the multi-valued attribute.
     * @param valueFilter   The value filter indicating the subset of attribute
     *                      values that should be modified.
     *
     * @return  {@code true} if at least one value in the {@code array} was
     *          updated with the new value.
     *
     * @throws ScimException  If an error occurred while processing the filter.
     */
    private boolean processArrayReplace(@NotNull final ObjectNode parent,
                                        @NotNull final String field,
                                        @NotNull final ArrayNode array,
                                        @NotNull final Filter valueFilter)
        throws ScimException
    {
      boolean nodeUpdated = false;

      // If the 'value' is an empty array, then the client is actually
      // requesting that we delete values that match the filter.
      boolean isDelete = isNullNodeOrEmptyArray(value);
      List<Integer> indexesToDelete = new ArrayList<>();

      for (int i = 0; i < array.size(); i++)
      {
        JsonNode attributeValue = array.get(i);

        // We should only perform processing on this inner node if it matches
        // the filter.
        if (!FilterEvaluator.evaluate(valueFilter, attributeValue))
        {
          continue;
        }
        nodeUpdated = true;


        if (attributeValue.isObject() && value.isObject())
        {
          updateNode((ObjectNode) attributeValue, null, value);
        }
        else if (isDelete && attributeValue.isObject())
        {
          // Mark the current index as a child that must be deleted. We cannot
          // delete this yet because it would modify the 'i' index, which we are
          // currently iterating over.
          indexesToDelete.add(i);
        }
        else
        {
          array.set(i, value);
        }
      }

      for (int j = indexesToDelete.size() - 1; j >= 0; j--)
      {
        var nodeIndex = indexesToDelete.get(j);
        array.remove(nodeIndex);
      }

      // If we have pruned all remaining values on the field, then we should
      // completely delete the attribute. For example, if a resource only has a
      // single email that is deleted, the 'emails' field in the JsonNode should
      // explicitly be set to null.
      if (array.isEmpty())
      {
        updateNode(parent, field, NullNode.getInstance());
      }

      return nodeUpdated;
    }

    /**
     * Update the value(s) of the field specified by the key in the parent
     * container node.
     *
     * @param parent The container node.
     * @param key The key of the field to update.
     * @param value The update value.
     */
    protected void updateNode(@NotNull final ObjectNode parent,
                              @Nullable final String key,
                              @Nullable final JsonNode value)
    {
      // RFC 7643 section 2.5 states:
      // "Unassigned attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be equivalent in
      // 'state'. Assigning an attribute with the value 'null' or an empty
      // array... has the effect of making the attribute 'unassigned'."
      if (isNullNodeOrEmptyArray(value))
      {
        // Replace operations should remove the field. If this is an add
        // operation, there is no update to make.
        if (!appendValues && key != null)
        {
          parent.remove(key);
        }

        return;
      }

      // When key is null, the node to update is the parent itself.
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
            updateNode(targetObject, field.getKey(), field.getValue());
          }
        }
        else
        {
          // Replace the field.
          parent.set(key, value);
        }
      }
      else if(node.isArray())
      {
        if(value.isArray() && appendValues)
        {
          // Append the new values to the existing ones.
          ArrayNode targetArray = (ArrayNode) node;
          ArrayNode valueArray = (ArrayNode) value;
          for(JsonNode valueNode : valueArray)
          {
            boolean valueFound = false;
            for(JsonNode targetNode : targetArray)
            {
              if(valueNode.equals(targetNode))
              {
                valueFound = true;
                break;
              }
            }
            if(!valueFound)
            {
              targetArray.add(valueNode);
            }
          }
        }
        else
        {
          // Replace the field.
          parent.set(key, value);
        }
      }
      else
      {
        // Replace the field.
        parent.set(key, value);
      }
    }
  }

  private static class PathExistsVisitor extends NodeVisitor
  {
    private boolean pathPresent = false;

    @Override
    @NotNull
    JsonNode visitInnerNode(@NotNull final ObjectNode parent,
                            @Nullable final String field,
                            @Nullable final Filter valueFilter)
        throws ScimException
    {
      JsonNode node = parent.path(field);
      if(node.isArray() && valueFilter != null)
      {
        return filterArray((ArrayNode) node, valueFilter, false);
      }
      return node;
    }

    @Override
    void visitLeafNode(@NotNull final ObjectNode parent,
                       @Nullable final String field,
                       @Nullable final Filter valueFilter)
        throws ScimException
    {
      JsonNode node = parent.path(field);
      if(node.isArray() && valueFilter != null)
      {
        node = filterArray((ArrayNode) node, valueFilter, false);
      }

      if(node.isArray())
      {
        if(node.size() > 0)
        {
          setPathPresent(true);
        }
      } else if(! node.isMissingNode())
      {
        setPathPresent(true);
      }
    }

    /**
     * Gets the value of pathPresent.  Path present will be set to
     * true during a traversal if the path was present or false if not.
     *
     * @return returns the value of pathPresent
     */
    public boolean isPathPresent()
    {
      return pathPresent;
    }

    /**
     * Sets the value of pathPresent.
     *
     * @param pathPresent the new value of pathPresent.
     */
    private void setPathPresent(final boolean pathPresent)
    {
      this.pathPresent = pathPresent;
    }
  }

  /**
   * Gets a single value (node) from an ObjectNode at the supplied path.
   * It is expected that there will only be one matching path.  If there
   * are multiple matching paths (for example a path with filters can
   * match multiple nodes), an exception will be thrown.
   *
   * For example:
   *   With an ObjectNode representing:
   *     {
   *       "name":"Bob",
   *       "favoriteColors":["red","green","blue"]
   *     }
   *
   *   getValue(Path.fromString("name")
   *   will return a TextNode containing "{@code Bob}"
   *
   *   getValue(Path.fromString("favoriteColors"))
   *   will return an ArrayNode containing TextNodes with the following
   *   values - "{@code red}", "{@code green}", and "{@code blue}".
   *
   * @param path The path to the attributes whose values to retrieve.
   * @param node the ObjectNode to find the path in.
   * @return the node located at the path, or a NullNode.
   * @throws ScimException throw in case of errors.
   */
  @NotNull
  public static JsonNode getValue(@NotNull final Path path,
                                  @NotNull final ObjectNode node)
      throws ScimException
  {
    GatheringNodeVisitor visitor = new GatheringNodeVisitor(false);
    traverseValues(visitor, node, path);
    if(visitor.values.isEmpty())
    {
      return NullNode.getInstance();
    }
    else
    {
      return visitor.values.get(0);
    }
  }

  /**
   * Retrieve all JSON nodes referenced by the provided path. If the path
   * traverses through a JSON array, all nodes the array will be traversed.
   * For example, given the following ObjectNode:
   *
   * <pre>
   *   {
   *     "emails": [
   *       {
   *         "type": "work",
   *         "value": "bob@work.com"
   *       },
   *       {
   *         "type": "home",
   *         "value": "bob@home.com"
   *       }
   *     ]
   *   }
   * </pre>
   *
   * Calling getValues with path of emails.value will return a list of all
   * TextNodes of the "{@code value}" field in the "{@code emails}" array:
   *
   * <pre>
   *   [ TextNode("bob@work.com"), TextNode("bob@home.com") ]
   * </pre>
   *
   * However, if the last element of the path references a JSON array, the
   * entire ArrayNode will returned. For example given the following ObjectNode:
   *
   * <pre>
   *   {
   *     "books": [
   *       {
   *         "title": "Brown Bear, Brown Bear, What Do You See?",
   *         "authors": ["Bill Martin, Jr.", "Eric Carle"]
   *       },
   *       {
   *         "title": "The Cat In The Hat",
   *         "authors": ["Dr. Seuss"]
   *       }
   *     ]
   *   }
   * </pre>
   *
   * Calling getValues with path of books.authors will return a list of all
   * ArrayNodes of the "{@code authors}" field in the "{@code books}" array:
   *
   * <pre>
   * [ ArrayNode(["Bill Martin, Jr.", "Eric Carle"]), ArrayNode(["Dr. Seuss"]) ]
   * </pre>
   *
   * @param path The path to the attributes whose values to retrieve.
   * @param node The JSON node representing the SCIM resource.
   *
   * @return List of all JSON nodes referenced by the provided path.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  @NotNull
  public static List<JsonNode> findMatchingPaths(@NotNull final Path path,
                                                 @NotNull final ObjectNode node)
      throws ScimException
  {
    GatheringNodeVisitor visitor = new GatheringNodeVisitor(false);
    traverseValues(visitor, node, path);
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
  public static void addValue(@NotNull final Path path,
                              @NotNull final ObjectNode node,
                              @NotNull final JsonNode value)
      throws ScimException
  {
    UpdatingNodeVisitor visitor = new UpdatingNodeVisitor(value, true);
    traverseValues(visitor, node, path);
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
  @NotNull
  public static List<JsonNode> removeValues(@NotNull final Path path,
                                            @NotNull final ObjectNode node)
      throws ScimException
  {
    GatheringNodeVisitor visitor = new GatheringNodeVisitor(true);
    traverseValues(visitor, node, path);
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
   *     (for example, "addresses[type eq "work"].streetAddress"), the matching
   *     sub-attribute of all matching records is replaced.
   *   </li>
   *   <li>
   *     If the path targets a multi-valued attribute for which a value filter
   *     is specified and no records match was made, the NoTarget exception
   *     will be thrown.
   *   </li>
   * </ul>
   *
   * @param path The path to the attribute.
   * @param node The JSON object node containing the attribute.
   * @param value The replacement value.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static void replaceValue(@NotNull final Path path,
                                  @NotNull final ObjectNode node,
                                  @NotNull final JsonNode value)
      throws ScimException
  {
    UpdatingNodeVisitor visitor = new UpdatingNodeVisitor(value, false);
    traverseValues(visitor, node, path);
  }

  /**
   * Checks for the existence of a path.  This will return true if the
   * path is present (even if the value is {@code null}).  This allows the caller
   * to know if the original json string  had something like
   * ... "{@code myPath}":{@code null} ... rather than just leaving the value out of the
   * json string entirely.
   *
   * @param path The path to the attribute.
   * @param node The JSON object node to search for the path in.
   * @return true if the path has a value set (even if that value is
   * set to {@code null}), or false if not.
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static boolean pathExists(@NotNull final Path path,
                                   @NotNull final ObjectNode node)
      throws ScimException
  {
    PathExistsVisitor pathExistsVisitor = new PathExistsVisitor();
    traverseValues(pathExistsVisitor, node, path);
    return pathExistsVisitor.isPathPresent();
  }

  /**
   * Compares two JsonNodes for order. Nodes containing datetime and numerical
   * values are ordered accordingly. Otherwise, the values' string
   * representation will be compared lexicographically.
   *
   * @param n1 the first node to be compared.
   * @param n2 the second node to be compared.
   * @param attributeDefinition The attribute definition of the attribute
   *                            whose values to compare or {@code null} to
   *                            compare string values using case-insensitive
   *                            matching.
   * @return a negative integer, zero, or a positive integer as the
   *         first argument is less than, equal to, or greater than the second.
   */
  public static int compareTo(
      @NotNull final JsonNode n1,
      @NotNull final JsonNode n2,
      @Nullable final AttributeDefinition attributeDefinition)
  {
    if (n1.isTextual() && n2.isTextual())
    {
      Date d1 = dateValue(n1);
      Date d2 = dateValue(n2);
      if (d1 != null && d2 != null)
      {
        return d1.compareTo(d2);
      }
      else
      {
        if(attributeDefinition != null &&
            attributeDefinition.getType() == AttributeDefinition.Type.STRING &&
            attributeDefinition.isCaseExact())
        {
          return n1.textValue().compareTo(n2.textValue());
        }
        return StaticUtils.toLowerCase(n1.textValue()).compareTo(
            StaticUtils.toLowerCase(n2.textValue()));
      }
    }

    if (n1.isNumber() && n2.isNumber())
    {
      if (n1.isBigDecimal() || n2.isBigDecimal())
      {
        return n1.decimalValue().compareTo(n2.decimalValue());
      }

      if(n1.isFloatingPointNumber() || n2.isFloatingPointNumber())
      {
        return Double.compare(n1.doubleValue(), n2.doubleValue());
      }

      if (n1.isBigInteger() || n2.isBigInteger())
      {
        return n1.bigIntegerValue().compareTo(n2.bigIntegerValue());
      }

      return Long.compare(n1.longValue(), n2.longValue());
    }

    // Compare everything else lexicographically
    return n1.asText().compareTo(n2.asText());
  }

  /**
   * Generates a list of patch operations that can be applied to the source
   * node in order to make it match the target node.
   *
   * @param source The source node for which the set of modifications should
   *               be generated.
   * @param target The target node, which is what the source node should
   *               look like if the returned modifications are applied.
   * @param removeMissing Whether to remove fields that are missing in the
   *                      target node.
   * @return A diff with modifications that can be applied to the source
   *         resource in order to make it match the target resource.
   */
  @NotNull
  public static List<PatchOperation> diff(@NotNull final ObjectNode source,
      @NotNull final ObjectNode target,
      final boolean removeMissing)
  {
    return new JsonDiff().diff(source, target, removeMissing);
  }



  /**
   * Try to parse out a date from a JSON text node.
   *
   * @param node The JSON node to parse.
   *
   * @return A parsed date instance or {@code null} if the text is not an
   * xsd:dateTime formatted date and time string.
   */
  @Nullable
  private static Date dateValue(@NotNull final JsonNode node)
  {
    try
    {
      return nodeToDateValue(node);
    }
    catch (IllegalArgumentException e)
    {
      return null;
    }
  }

  /**
   * Recursively traver JSON nodes based on a path using the provided node
   * visitor.
   *
   * @param nodeVisitor The NodeVisitor to use to handle the traversed nodes.
   * @param node The JSON node representing the SCIM resource.
   * @param path The path to the attributes whose values to retrieve.
   *
   * @throws ScimException If an error occurs while traversing the JSON node.
   */
  public static void traverseValues(@NotNull final NodeVisitor nodeVisitor,
                                    @NotNull final ObjectNode node,
                                    @NotNull final Path path)
      throws ScimException
  {
    traverseValues(nodeVisitor, node, 0, path);
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
  private static void traverseValues(@NotNull final NodeVisitor nodeVisitor,
                                     @NotNull final ObjectNode node,
                                     final int index,
                                     @NotNull final Path path)
      throws ScimException
  {
    String field = null;
    Filter valueFilter = null;
    int pathDepth = path.size();
    if(path.getSchemaUrn() != null)
    {
      if(index > 0)
      {
        Path.Element element = path.getElement(index - 1);
        field = element.getAttribute();
        valueFilter = element.getValueFilter();
      }
      else
      {
        field = path.getSchemaUrn();
      }
      pathDepth += 1;
    }
    else if(path.size() > 0)
    {
      Path.Element element = path.getElement(index);
      field = element.getAttribute();
      valueFilter = element.getValueFilter();
    }

    if(index < pathDepth - 1)
    {
      JsonNode child = nodeVisitor.visitInnerNode(node, field, valueFilter);
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
      nodeVisitor.visitLeafNode(node, field, valueFilter);
    }
  }

  /**
   * Factory method for constructing a SCIM compatible Jackson
   * {@link ObjectReader} with default settings. Note that the resulting
   * instance is NOT usable as is, without defining expected value type with
   * ObjectReader.forType.
   *
   * @return A Jackson {@link ObjectReader} with default settings.
   */
  @NotNull
  public static ObjectReader getObjectReader()
  {
    return SDK_OBJECT_MAPPER.reader();
  }

  /**
   * Factory method for constructing a SCIM compatible Jackson
   * {@link ObjectWriter} with default settings.
   *
   * @return A Jackson {@link ObjectWriter} with default settings.
   */
  @NotNull
  public static ObjectWriter getObjectWriter()
  {
    return SDK_OBJECT_MAPPER.writer();
  }

  /**
   * Retrieve the SCIM compatible Jackson JsonNodeFactory that may be used
   * to create tree model JsonNode instances.
   *
   * @return The Jackson JsonNodeFactory.
   */
  @NotNull
  public static JsonNodeFactory getJsonNodeFactory()
  {
    return SDK_OBJECT_MAPPER.getNodeFactory();
  }

  /**
   * Utility method to convert a POJO to Jackson JSON node. This behaves
   * exactly the same as Jackson's ObjectMapper.valueToTree.
   *
   * @param <T> Actual node type.
   * @param fromValue POJO to convert.
   * @return converted JsonNode.
   */
  @NotNull
  public static <T extends JsonNode> T valueToNode(
      @Nullable final Object fromValue)
  {
    return SDK_OBJECT_MAPPER.valueToTree(fromValue);
  }

  /**
   * Utility method to convert Jackson JSON node to a POJO. This behaves
   * exactly the same as Jackson's ObjectMapper.treeToValue.
   *
   * @param <T> Actual node type.
   * @param fromNode node to convert.
   * @param valueType The value type.
   * @return converted POJO.
   * @throws JsonProcessingException if an error occurs while binding the JSON
   * node to the value type.
   */
  @Nullable
  public static <T> T nodeToValue(@Nullable final JsonNode fromNode,
                                  @NotNull final Class<T> valueType)
      throws JsonProcessingException
  {
    return SDK_OBJECT_MAPPER.treeToValue(fromNode, valueType);
  }

  /**
   * Utility method to convert Jackson JSON array node to a list of POJOs.
   *
   * @param <T> Actual node type.
   * @param fromNode node to convert.
   * @param valueType The value type.
   * @return converted list of POJOs.
   * @throws JsonProcessingException if an error occurs while binding the JSON
   * node to the value type.
   */
  @Nullable
  public static <T> List<T> nodeToValues(@NotNull final ArrayNode fromNode,
                                         @NotNull final Class<T> valueType)
      throws JsonProcessingException
  {
    final CollectionType collectionType =
        SDK_OBJECT_MAPPER.getTypeFactory().constructCollectionType(
            List.class, valueType);

    try
    {
      return SDK_OBJECT_MAPPER.readValue(
          SDK_OBJECT_MAPPER.treeAsTokens(fromNode), collectionType);
    } catch (JsonProcessingException e)
    {
      throw e;
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Utility method to convert a Jackson node to a {@link Date} object.
   *
   * @param node Node to convert. The node must be textual.
   * @return The converted Date value.
   * @throws IllegalArgumentException if the node is not textual or its value
   * cannot be parsed as a SCIM DateTime value.
   */
  @NotNull
  public static Date nodeToDateValue(@NotNull final JsonNode node)
      throws IllegalArgumentException
  {
    if(!node.isTextual())
    {
      throw new IllegalArgumentException(
          "non-textual node cannot be parsed as DateTime type");
    }
    String text = node.textValue().trim();
    return DateTimeUtils.parse(text).getTime();
  }

  /**
   * Creates a configured SCIM-compatible Jackson ObjectMapper. Creating new
   * ObjectMapper instances are expensive, so instances should be shared if
   * possible. Alternatively, consider using one of the
   * {@link #getObjectReader}, {@link #getObjectWriter},
   * {@link #getJsonNodeFactory}, or {@link #valueToNode} methods, which use the
   * SCIM 2 SDK's ObjectMapper singleton.
   *
   * @return an Object Mapper with the correct options set for serializing
   *     and deserializing SCIM JSON objects.
   */
  @NotNull
  public static ObjectMapper createObjectMapper()
  {
    return mapperFactory.createObjectMapper();
  }

  /**
   * Sets the MapperFactory used to create the object mappers used by the SCIM 2
   * SDK.  If this method is called, it should be called prior to the first use
   * of any other method that may use an ObjectMapper (most of the methods of
   * JsonUtils use an object mapper).
   *
   * @param customMapperFactory the custom JSON object mapper.
   */
  public static void setCustomMapperFactory(
      @NotNull final MapperFactory customMapperFactory)
  {
    JsonUtils.mapperFactory = customMapperFactory;
    SDK_OBJECT_MAPPER = customMapperFactory.createObjectMapper();
  }


  /**
   * Validates whether the provided node is null or an empty array.
   */
  private static boolean isNullNodeOrEmptyArray(@Nullable final JsonNode node)
  {
    if (node == null || node.isNull())
    {
      return true;
    }

    return (node.isArray() && node.isEmpty());
  }
}
