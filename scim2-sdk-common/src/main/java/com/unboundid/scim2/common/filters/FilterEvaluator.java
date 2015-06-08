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

package com.unboundid.scim2.common.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Iterator;
import java.util.List;

/**
 * A filter visitor that will evaluate a filter on a JsonNode and return
 * whether the JsonNode matches the filter.
 */
public class FilterEvaluator implements FilterVisitor<Boolean, ObjectNode>
{
  private static final FilterEvaluator SINGLETON = new FilterEvaluator();

  /**
   * Evaluate the provided filter against the provided JsonNode.
   *
   * @param filter   The filter to evaluate.
   * @param jsonNode The JsonNode to evaluate the filter against.
   * @return {@code true} if the JsonNode matches the filter or {@code false}
   * otherwise.
   * @throws ScimException If the filter is not valid for matching.
   */
  public static boolean evaluate(final Filter filter, final ObjectNode jsonNode)
      throws ScimException
  {
    return filter.visit(SINGLETON, jsonNode);
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final EqualFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
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
      if (JsonUtils.compareTo(node, filter.getComparisonValue()) == 0)
      {
        return true;
      }
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  public Boolean visit(final NotEqualFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
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
      if (JsonUtils.compareTo(node, filter.getComparisonValue()) == 0)
      {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final ContainsFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isTextual() && filter.getComparisonValue().isTextual() &&
          node.textValue().toLowerCase().contains(
              filter.getComparisonValue().textValue().toLowerCase()) ||
          node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final StartsWithFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isTextual() && filter.getComparisonValue().isTextual() &&
          node.textValue().toLowerCase().startsWith(
              filter.getComparisonValue().textValue().toLowerCase()) ||
          node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final EndsWithFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isTextual() && filter.getComparisonValue().isTextual() &&
          node.textValue().toLowerCase().endsWith(
              filter.getComparisonValue().textValue().toLowerCase()) ||
          node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final PresentFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
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
  public Boolean visit(final GreaterThanFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter(
            "Greater than filter may not compare boolean or binary " +
                "attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue()) > 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final GreaterThanOrEqualFilter filter,
                       final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter("Greater than or equal " +
            "filter may not compare boolean or binary attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue()) >= 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final LessThanFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter("Less than or equal " +
            "filter may not compare boolean or binary attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue()) < 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final LessThanOrEqualFilter filter,
                       final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw BadRequestException.invalidFilter("Less than or equal " +
            "filter may not compare boolean or binary attribute values");
      }
      if (JsonUtils.compareTo(node, filter.getComparisonValue()) <= 0)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final AndFilter filter, final ObjectNode object)
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
  public Boolean visit(final OrFilter filter, final ObjectNode object)
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
  public Boolean visit(final NotFilter filter, final ObjectNode object)
      throws ScimException
  {
    return !filter.getInvertedFilter().visit(this, object);
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final ComplexValueFilter filter, final ObjectNode object)
      throws ScimException
  {
    List<JsonNode> nodes = JsonUtils.getValues(
        filter.getAttributePath(), object);

    for (JsonNode node : nodes)
    {
      if (node.isArray())
      {
        // filter each element of the array individually
        Iterator<JsonNode> iterator = node.elements();
        while (iterator.hasNext())
        {
          if (matchValueFilter(iterator.next(), filter.getValueFilter()))
          {
            return true;
          }
        }
      }
      else if (matchValueFilter(node, filter.getValueFilter()))
      {
        return true;
      }
    }
    return false;
  }


  /**
   * Determine if the specified node meets the filter criteria.
   * @param node  node to filter against
   * @param valueFilter filter specification
   * @return true if the filter criteria is met by the node
   * @throws ScimException if filter cannot be evaluated
   */
  public boolean matchValueFilter(
      final JsonNode node,
      final Filter valueFilter)
      throws ScimException
  {
    if(node.isObject())
    {
      return valueFilter.visit(this, (ObjectNode) node);
    }
    else if(valueFilter.isComparisonFilter() &&
        valueFilter.getAttributePath().size() == 1 &&
        valueFilter.getAttributePath().getElement(0).
            getAttribute().equals("value"))
    {
      // Special case for using filters like attr[value eq "value"] to match
      // simple multi-valued attributes whose value is ust an array of
      // values (ie. "attr":["value", "value2"])
      return node.equals(valueFilter.getComparisonValue());
    }
    return false;
  }


  /**
   * Return true if the node is either null or an empty array.
   * @param node node to examine
   * @return boolean
   */
  private boolean isEmpty(final JsonNode node)
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
   * but empty arrays and/or null nodes.
   * @param nodes list of nodes as returned from JsonUtils.getValues
   * @return true if the list contains only empty array(s)
   */
  private boolean isEmpty(final List<JsonNode> nodes)
  {
    for (JsonNode node : nodes) {
      if (!isEmpty(node)) {
        return false;
      }
    }
    return true;
  }
}
