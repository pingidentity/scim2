/*
 * Copyright 2016-2024 Ping Identity Corporation
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOperation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;


/**
 * This class can be used to calculate the diffs between two SCIM/JSON
 * resources for the purpose of building a set of patch operations.
 */
public class JsonDiff
{
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
  public List<PatchOperation> diff(@NotNull final ObjectNode source,
                                   @NotNull final ObjectNode target,
                                   final boolean removeMissing)
  {
    List<PatchOperation> ops = new LinkedList<>();
    ObjectNode targetToAdd = target.deepCopy();
    ObjectNode targetToReplace = target.deepCopy();
    diff(Path.root(), source, targetToAdd, targetToReplace, ops, removeMissing);
    if(targetToReplace.size() > 0)
    {
      ops.add(PatchOperation.replace(targetToReplace));
    }
    if(targetToAdd.size() > 0)
    {
      ops.add(PatchOperation.add(targetToAdd));
    }
    return ops;
  }


  /**
   * Internal diff that is used to recursively diff source and target object
   * nodes.
   *
   * @param parentPath The path to the source object node.
   * @param source The source node.
   * @param targetToAdd The target node that will be modified to only contain
   *                    the fields to add.
   * @param targetToReplace The target node that will be modified to only
   *                        contain the fields to replace.
   * @param operations The list of operations to append.
   * @param removeMissing Whether to remove fields that are missing in the
   *                      target node.
   */
  private void diff(@NotNull final Path parentPath,
                    @NotNull final ObjectNode source,
                    @NotNull final ObjectNode targetToAdd,
                    @NotNull final ObjectNode targetToReplace,
                    @NotNull final List<PatchOperation> operations,
                    final boolean removeMissing)
  {
    // First iterate through the source fields and compare it to the target
    Iterator<Map.Entry<String, JsonNode>> si = source.fields();
    while (si.hasNext())
    {
      processEntry(parentPath, targetToAdd, targetToReplace,
          operations, removeMissing, si.next());
    }

    if(targetToAdd != targetToReplace)
    {
      // Now iterate through the fields in targetToAdd and remove any that
      // are not in the source. These new fields should only be in
      // targetToReplace.
      Iterator<String> ai = targetToAdd.fieldNames();
      while (ai.hasNext())
      {
        final String f = ai.next();
        if (!source.has(f))
        {
          ai.remove();
        }
      }
    }

    removeNullAndEmptyValues(targetToAdd);
    removeNullAndEmptyValues(targetToReplace);
  }

  private void processEntry(
      @NotNull final Path parentPath,
      @NotNull final ObjectNode targetToAdd,
      @NotNull final ObjectNode targetToReplace,
      @NotNull final List<PatchOperation> operations,
      final boolean removeMissing,
      @NotNull final Map.Entry<String, JsonNode> sourceEntry)
  {
    String sourceKey = sourceEntry.getKey();
    JsonNode sourceNode = sourceEntry.getValue();

    Path path = computeDiffPath(parentPath, sourceKey);
    JsonNode targetValueToAdd = targetToAdd.remove(sourceKey);
    JsonNode targetValueToReplace =
        targetToReplace == targetToAdd ? targetValueToAdd :
            targetToReplace.remove(sourceKey);

    if (targetValueToAdd == null)
    {
      if(removeMissing)
      {
        operations.add(PatchOperation.remove(path));
      }
      return;
    }


    if (isSameType(sourceNode, targetValueToAdd))
    {
      replaceNode(parentPath, path, targetToAdd, targetToReplace, operations,
          removeMissing, sourceNode, targetValueToAdd, targetValueToReplace,
          sourceKey);
    }
    else
    {
      // Value present in both but they are of different types.
      if (targetValueToAdd.isNull() ||
          (targetValueToAdd.isArray() && targetValueToAdd.size() == 0))
      {
        // Explicitly clear attribute value.
        operations.add(PatchOperation.remove(path));
      }
      else
      {
        // Just replace with the target value.
        targetToReplace.set(sourceKey, targetValueToReplace);
      }
    }
  }

  private void replaceNode(
      @NotNull final Path parentPath,
      @NotNull final Path path,
      @NotNull final ObjectNode targetToAdd,
      @NotNull final ObjectNode targetToReplace,
      @NotNull final List<PatchOperation> operations,
      final boolean removeMissing,
      @NotNull final JsonNode sourceNode,
      @NotNull final JsonNode targetValueToAdd,
      @NotNull final JsonNode targetValueToReplace,
      @NotNull final String sourceKey)
  {
    // Value present in both and they are of the same type.
    if (sourceNode.isObject())
    {
      computeObjectNodeDiffs(path, sourceNode, targetValueToAdd, targetValueToReplace,
          operations, removeMissing, targetToAdd, targetToReplace, sourceKey);
    }
    else if (sourceNode.isArray())
    {
      computeArrayNodeDiffs(parentPath, path, targetToAdd, targetToReplace,
          operations, removeMissing, sourceNode, targetValueToAdd,
          targetValueToReplace, sourceKey);
    }
    else
    {
      // They are value nodes.
      if (compareTo(path.withoutFilters(), sourceNode, targetValueToAdd) != 0)
      {
        // Just replace with the target value.
        targetToReplace.set(sourceKey, targetValueToReplace);
      }
    }
  }


  /**
   * Compare the JSON value nodes at the specified path.
   *
   * @param path path
   * @param sourceNode source node
   * @param targetNode target node
   * @return a negative integer, zero, or a positive integer as the
   *         first argument is less than, equal to, or greater than the second.
   */
  protected int compareTo(
      @NotNull final Path path,
      @NotNull final JsonNode sourceNode,
      @NotNull final JsonNode targetNode)
  {
    return JsonUtils.compareTo(sourceNode, targetNode, null);
  }

  private void computeArrayNodeDiffs(
      @NotNull final Path parentPath,
      @NotNull final Path path,
      @NotNull final ObjectNode targetToAdd,
      @NotNull final ObjectNode targetToReplace,
      @NotNull final List<PatchOperation> operations,
      final boolean removeMissing,
      @NotNull final JsonNode sourceNode,
      @NotNull final JsonNode targetValueToAdd,
      @NotNull final JsonNode targetValueToReplace,
      @NotNull final String sourceKey)
  {
    if (targetValueToAdd.size() == 0)
    {
      if((sourceNode != null) &&
          (sourceNode.isArray()) &&
          (sourceNode.size() == 0))
      {
        return;
      }

      // Explicitly clear all attribute values.
      operations.add(PatchOperation.remove(path));
    }
    else
    {
      // Go through each value and try to individually patch them first
      // instead of replacing all values.
      List<PatchOperation> targetOpToRemoveOrReplace =
          new LinkedList<PatchOperation>();
      boolean replaceAllValues = false;
      for (JsonNode sv : sourceNode)
      {
        JsonNode tv = removeMatchingValue(sv,
            (ArrayNode) targetValueToAdd);
        Filter valueFilter = generateValueFilter(sv);
        if (valueFilter == null)
        {
          replaceAllValues = true;
          Debug.debug(Level.WARNING, DebugType.OTHER,
              "Performing full replace of target " +
                  "array node " + path + " since the it is not " +
                  "possible to generate a value filter to uniquely " +
                  "identify the value " + sv.toString());
          break;
        }
        Path valuePath = parentPath.attribute(
            sourceKey, valueFilter);
        if (tv != null)
        {
          // The value is in both source and target arrays.
          if (sv.isObject() && tv.isObject())
          {
            // Recursively diff the object node.
            diff(valuePath, (ObjectNode) sv, (ObjectNode) tv,
                (ObjectNode) tv, operations, removeMissing);
            if (tv.size() > 0)
            {
              targetOpToRemoveOrReplace.add(
                  PatchOperation.replace(valuePath, tv));
            }
          }
        }
        else
        {
          targetOpToRemoveOrReplace.add(
              PatchOperation.remove(valuePath));
        }
      }
      if (!replaceAllValues && targetValueToReplace.size() <=
          targetValueToAdd.size() + targetOpToRemoveOrReplace.size())
      {
        // We are better off replacing the entire array.
        Debug.debug(Level.INFO, DebugType.OTHER,
            "Performing full replace of target " +
                "array node " + path + " since the " +
                "array (" + targetValueToReplace.size() + ") " +
                "is smaller than removing and " +
                "replacing (" + targetOpToRemoveOrReplace.size() + ") " +
                "then adding (" + targetValueToAdd.size() + ")  " +
                "the values individually");
        replaceAllValues = true;
        targetToReplace.set(sourceKey, targetValueToReplace);

      }
      if (replaceAllValues)
      {
        targetToReplace.set(sourceKey, targetValueToReplace);
      }
      else
      {
        if (!targetOpToRemoveOrReplace.isEmpty())
        {
          operations.addAll(targetOpToRemoveOrReplace);
        }
        if (targetValueToAdd.size() > 0)
        {
          targetToAdd.set(sourceKey, targetValueToAdd);
        }
      }
    }
  }

  private void computeObjectNodeDiffs(
      @NotNull final Path path,
      @NotNull final JsonNode sourceNode,
      @NotNull final JsonNode targetValueToAdd,
      @NotNull final JsonNode targetValueToReplace,
      @NotNull final List<PatchOperation> operations,
      final boolean removeMissing,
      @NotNull final ObjectNode targetToAdd,
      @NotNull final ObjectNode targetToReplace,
      @NotNull final String sourceKey)
  {
    // Recursively diff the object node.
    diff(path,
        (ObjectNode) sourceNode, (ObjectNode) targetValueToAdd,
        (ObjectNode) targetValueToReplace, operations, removeMissing);
    // Include the object node if there are fields to add or replace.
    if (targetValueToAdd.size() > 0)
    {
      targetToAdd.set(sourceKey, targetValueToAdd);
    }
    if (targetValueToReplace.size() > 0)
    {
      targetToReplace.set(sourceKey, targetValueToReplace);
    }
  }

  @NotNull
  private Path computeDiffPath(@NotNull final Path parentPath,
                               @NotNull final String sourceKey)
  {
    return parentPath.isRoot() &&
        SchemaUtils.isUrn(sourceKey) ?
        Path.root(sourceKey) :
        parentPath.attribute(sourceKey);
  }


  /**
   * Removes the value from an ArrayNode that matches the provided node.
   *
   * @param sourceValue The sourceValue node to match.
   * @param targetValues The ArrayNode containing the values to remove from.
   * @return The matching value that was removed or {@code null} if no matching
   *         value was found.
   */
  @Nullable
  private JsonNode removeMatchingValue(@NotNull final JsonNode sourceValue,
                                       @NotNull final ArrayNode targetValues)
  {
    if(sourceValue.isObject())
    {
      // Find a target value that has the most fields in common with the source
      // and have identical values. Common fields that are also one of the
      // SCIM standard multi-value sub-attributes (ie. type, value, etc...) have
      // a higher weight when determining the best matching value.
      TreeMap<Integer, Integer> matchScoreToIndex =
          new TreeMap<Integer, Integer>();
      for(int i = 0; i < targetValues.size(); i++)
      {
        JsonNode targetValue = targetValues.get(i);
        if(targetValue.isObject())
        {
          int matchScore = 0;
          Iterator<String> si = sourceValue.fieldNames();
          while(si.hasNext())
          {
            String field = si.next();
            if(sourceValue.get(field).equals(targetValue.path(field)))
            {
              if(field.equals("value") || field.equals("$ref"))
              {
                // These fields have the highest chance of having unique values.
                matchScore += 3;
              }
              else if(field.equals("type") || field.equals("display"))
              {
                // These fields should mostly be unique.
                matchScore += 2;
              }
              else if(field.equals("primary"))
              {
                // This field will definitely not be unique.
                matchScore += 0;
              }
              else
              {
                // Not one of the normative fields. Use the default weight.
                matchScore += 1;
              }
            }
          }
          // Only consider the match if there is not already match with the same
          // score. This will prefer matches at the same index in the array.
          if(matchScore > 0 && !matchScoreToIndex.containsKey(matchScore))
          {
            matchScoreToIndex.put(matchScore, i);
          }
        }
      }
      if(!matchScoreToIndex.isEmpty())
      {
        return targetValues.remove(matchScoreToIndex.lastEntry().getValue());
      }
    }
    else
    {
      // Find an exact match
      for(int i = 0; i < targetValues.size(); i++)
      {
        if (JsonUtils.compareTo(sourceValue, targetValues.get(i), null) == 0)
        {
          return targetValues.remove(i);
        }
      }
    }

    // Can't find a match at all.
    return null;
  }

  /**
   * Generate a value filter that may be used to uniquely identify this value
   * in an array node.
   *
   * @param value The value to generate a filter from.
   * @return The value filter or {@code null} if a value filter can not be used
   *         to uniquely identify the node.
   */
  @Nullable
  private Filter generateValueFilter(@NotNull final JsonNode value)
  {
    if (value.isValueNode())
    {
      // Use the implicit "value" sub-attribute to reference this value.
      return Filter.eq(Path.root().attribute("value"), (ValueNode) value);
    }
    if (value.isObject())
    {
      List<Filter> filters = new ArrayList<Filter>(value.size());
      Iterator<Map.Entry<String, JsonNode>> fieldsIterator = value.fields();
      while (fieldsIterator.hasNext())
      {
        Map.Entry<String, JsonNode> field = fieldsIterator.next();
        if (!field.getValue().isValueNode())
        {
          // We can't nest value filters.
          return null;
        }
        filters.add(Filter.eq(Path.root().attribute(field.getKey()),
            (ValueNode) field.getValue()));
      }

      if (filters.size() == 0)
      {
        return null;
      }
      else if (filters.size() == 1)
      {
        return filters.get(0);
      }
      else
      {
        return Filter.and(filters);
      }
    }

    // We can't uniquely identify this value with a filter.
    return null;
  }


  /**
   * Removes any fields with the {@code null} value or an empty array.
   *
   * @param node The node with {@code null} and empty array values removed.
   */
  private void removeNullAndEmptyValues(@NotNull final JsonNode node)
  {
    Iterator<JsonNode> si = node.elements();
    while (si.hasNext())
    {
      JsonNode field = si.next();
      if(field.isNull() || field.isArray() && field.size() == 0)
      {
        si.remove();
      }
      else if(field.isContainerNode())
      {
        removeNullAndEmptyValues(field);
      }
    }
  }

  /**
   * Determines whether the provided JSON nodes have the same JSON data type.
   *
   * @param n1  The first node.
   * @param n2  The second node.
   * @return  {@code true} iff the nodes have the same JSON data type.
   */
  public boolean isSameType(@NotNull final JsonNode n1,
                            @NotNull final JsonNode n2)
  {
    return (n1.getNodeType() == n2.getNodeType() ||
        ((n1.isTextual() || n1.isBinary()) &&
            (n2.isTextual() || n2.isBinary())));
  }
}
