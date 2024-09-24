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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.AndFilter;
import com.unboundid.scim2.common.filters.ComplexValueFilter;
import com.unboundid.scim2.common.filters.ContainsFilter;
import com.unboundid.scim2.common.filters.EndsWithFilter;
import com.unboundid.scim2.common.filters.EqualFilter;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.filters.FilterVisitor;
import com.unboundid.scim2.common.filters.GreaterThanFilter;
import com.unboundid.scim2.common.filters.GreaterThanOrEqualFilter;
import com.unboundid.scim2.common.filters.LessThanFilter;
import com.unboundid.scim2.common.filters.LessThanOrEqualFilter;
import com.unboundid.scim2.common.filters.NotEqualFilter;
import com.unboundid.scim2.common.filters.NotFilter;
import com.unboundid.scim2.common.filters.OrFilter;
import com.unboundid.scim2.common.filters.PresentFilter;
import com.unboundid.scim2.common.filters.StartsWithFilter;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A filter visitor that will evaluate a filter on a JsonNode and return
 * whether the JsonNode matches the filter.
 */
public class FilterEvaluator implements FilterVisitor<Boolean, JsonNode>
{
  @NotNull
  private static final FilterEvaluator SINGLETON = new FilterEvaluator();

  @NotNull
  private static final Path VALUE_PATH = Path.root().attribute("value");

  /**
   * Evaluate the provided filter against the provided JsonNode.
   *
   * @param filter   The filter to evaluate.
   * @param jsonNode The JsonNode to evaluate the filter against.
   * @return {@code true} if the JsonNode matches the filter or {@code false}
   * otherwise.
   * @throws ScimException If the filter is not valid for matching.
   */
  public static boolean evaluate(@NotNull final Filter filter,
                                 @NotNull final JsonNode jsonNode)
      throws ScimException
  {
    return filter.visit(SINGLETON, jsonNode);
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final EqualFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    if (filter.getComparisonValue().isNull() && isEmpty(nodes))
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      return true;
    }
    for (JsonNode node : nodes)
    {
      if (JsonUtils.compareTo(node, filter.getComparisonValue(),
          getAttributeDefinition(filter.getAttributePath())) == 0)
      {
        return true;
      }
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final NotEqualFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    if (filter.getComparisonValue().isNull() && isEmpty(nodes))
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      return false;
    }
    for (JsonNode node : nodes)
    {
      if (JsonUtils.compareTo(node, filter.getComparisonValue(),
          getAttributeDefinition(filter.getAttributePath())) == 0)
      {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final ContainsFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    return substringMatch(filter, object);
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final StartsWithFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    return substringMatch(filter, object);
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final EndsWithFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    return substringMatch(filter, object);
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final PresentFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      if (!isEmpty(node))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final GreaterThanFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter(
            "Greater than filter may not compare boolean or binary " +
                "attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue(),
          getAttributeDefinition(filter.getAttributePath())) > 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final GreaterThanOrEqualFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter("Greater than or equal " +
            "filter may not compare boolean or binary attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue(),
          getAttributeDefinition(filter.getAttributePath())) >= 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final LessThanFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter("Less than or equal " +
            "filter may not compare boolean or binary attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue(),
          getAttributeDefinition(filter.getAttributePath())) < 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final LessThanOrEqualFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter("Less than or equal " +
            "filter may not compare boolean or binary attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue(),
          getAttributeDefinition(filter.getAttributePath())) <= 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final AndFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    for (Filter combinedFilter : filter.getCombinedFilters())
    {
      if (!combinedFilter.visit(this, object))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final OrFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    for (Filter combinedFilter : filter.getCombinedFilters())
    {
      if (combinedFilter.visit(this, object))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final NotFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    return !filter.getInvertedFilter().visit(this, object);
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Boolean visit(@NotNull final ComplexValueFilter filter,
                       @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);

    for (JsonNode node : nodes)
    {
      if (node.isArray())
      {
        // filter each element of the array individually
        for (JsonNode value : node)
        {
          if (filter.getValueFilter().visit(this, value))
          {
            return true;
          }
        }
      }
      else if (filter.getValueFilter().visit(this, node))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieve the attribute definition for the attribute specified by the path
   * to determine case sensitivity during string matching.
   *
   * @param path The path to the attribute whose definition to retrieve.
   * @return the attribute definition or {@code null} if not available, in which
   *         case case insensitive string value matching will be performed.
   */
  @Nullable
  protected AttributeDefinition getAttributeDefinition(@NotNull final Path path)
  {
    return null;
  }

  /**
   * Retrieves the JsonNodes to compare against.
   *
   * @param path The path to the value.
   * @param jsonNode The JsonNode containing the value.
   * @return The JsonNodes to compare against.
   * @throws ScimException If an exception occurs during the operation.
   */
  @NotNull
  private Iterable<JsonNode> getCandidateNodes(@NotNull final Path path,
                                               @NotNull final JsonNode jsonNode)
      throws ScimException
  {
    if (jsonNode.isArray())
    {
      return jsonNode;
    }
    if (jsonNode.isObject())
    {
      List<JsonNode> nodes =
          JsonUtils.findMatchingPaths(path, (ObjectNode) jsonNode);
      ArrayList<JsonNode> flattenedNodes =
          new ArrayList<JsonNode>(nodes.size());
      for (JsonNode node : nodes)
      {
        if (node.isArray())
        {
          for (JsonNode child : node)
          {
            flattenedNodes.add(child);
          }
        }
        else
        {
          flattenedNodes.add(node);
        }
      }
      return flattenedNodes;
    }
    if (jsonNode.isValueNode() && path.equals(VALUE_PATH))
    {
      // Special case for the "value" path to reference the value itself.
      // Used for referencing the value nodes of an array when the filter is
      // attr[value eq "value1"] and the multi-valued attribute is
      // "attr": ["value1", "value2", "value3"].
      return Collections.singletonList(jsonNode);
    }
    return Collections.emptyList();
  }


  /**
   * Return true if the node is either {@code null} or an empty array.
   *
   * @param node node to examine
   * @return boolean
   */
  private boolean isEmpty(@NotNull final JsonNode node)
  {
    if (node.isArray())
    {
      Iterator<JsonNode> iterator = node.elements();
      while (iterator.hasNext()) {
        if (!isEmpty(iterator.next()))
        {
          return false;
        }
      }
      return true;
    }
    else
    {
      return node.isNull();
    }
  }

  /**
   * Return true if the specified node list contains nothing
   * but empty arrays and/or {@code null} nodes.
   *
   * @param nodes list of nodes as returned from JsonUtils.findMatchingPaths
   * @return true if the list contains only empty array(s)
   */
  private boolean isEmpty(@NotNull final Iterable<JsonNode> nodes)
  {
    for (JsonNode node : nodes) {
      if (!isEmpty(node)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Evaluate a substring match filter.
   *
   * @param filter The filter to operate on.
   * @param object The object to evaluate.
   * @return The return value from the operation.
   * @throws ScimException If an exception occurs during the operation.
   */
  private boolean substringMatch(@NotNull final Filter filter,
                                 @NotNull final JsonNode object)
      throws ScimException
  {
    Iterable<JsonNode> nodes =
        getCandidateNodes(filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        AttributeDefinition attributeDefinition =
            getAttributeDefinition(filter.getAttributePath());
        String nodeValue = node.textValue();
        String comparisonValue = filter.getComparisonValue().textValue();
        if (attributeDefinition == null || !attributeDefinition.isCaseExact())
        {
          nodeValue = StaticUtils.toLowerCase(nodeValue);
          comparisonValue = StaticUtils.toLowerCase(comparisonValue);
        }
        switch (filter.getFilterType())
        {
          case CONTAINS:
            if (nodeValue.contains(comparisonValue))
            {
              return true;
            }
            break;
          case STARTS_WITH:
            if (nodeValue.startsWith(comparisonValue))
            {
              return true;
            }
            break;
          case ENDS_WITH:
            if (nodeValue.endsWith(comparisonValue))
            {
              return true;
            }
            break;
        }
      }
      else if (node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }
}
