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

package com.unboundid.scim2.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.exceptions.InvalidResourceException;
import com.unboundid.scim2.exceptions.SCIMException;
import com.unboundid.scim2.utils.JsonUtils;

import java.util.Date;
import java.util.List;

/**
 * A filter visitor that will evaluate a filter on a JsonNode and return
 * whether the JsonNode matches the filter.
 */
public class FilterEvaluator implements FilterVisitor<Boolean, JsonNode>
{
  private static final FilterEvaluator SINGLETON = new FilterEvaluator();

  /**
   * Evaluate the provided filter against the provided JsonNode.
   *
   * @param filter   The filter to evaluate.
   * @param jsonNode The JsonNode to evaluate the filter against.
   * @return {@code true} if the JsonNode matches the filter or {@code false}
   * otherwise.
   * @throws SCIMException If the filter is not valid for matching.
   */
  public static boolean evaluate(final Filter filter, final JsonNode jsonNode)
      throws SCIMException
  {
    return filter.visit(SINGLETON, jsonNode);
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final EqualFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    if (filter.getComparisonValue().isNull() && nodes.isEmpty())
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      return true;
    }
    for (JsonNode node : nodes)
    {
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if (dateValue != null && compareDateValue != null)
        {
          if (dateValue.compareTo(compareDateValue) == 0)
          {
            return true;
          }
        } else if (node.textValue().equalsIgnoreCase(
            filter.getComparisonValue().textValue()))
        {
          return true;
        }
      }
      if (node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final NotEqualFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    if (filter.getComparisonValue().isNull() && nodes.isEmpty())
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      return false;
    }
    for (JsonNode node : nodes)
    {
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if (dateValue != null && compareDateValue != null)
        {
          if (dateValue.compareTo(compareDateValue) == 0)
          {
            return false;
          }
        } else if (node.textValue().equalsIgnoreCase(
            filter.getComparisonValue().textValue()))
        {
          return false;
        }
      }
      if (node.equals(filter.getComparisonValue()))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final ContainsFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
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
  public Boolean visit(final StartsWithFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
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
  public Boolean visit(final EndsWithFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
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
  public Boolean visit(final PresentFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      if (!node.isNull())
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final GreaterThanFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if (dateValue != null && compareDateValue != null)
        {
          if (dateValue.compareTo(compareDateValue) > 0)
          {
            return true;
          }
        } else if (node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) > 0)
        {
          return true;
        }
      }
      if (node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if ((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) >
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final GreaterThanOrEqualFilter filter,
                       final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if (dateValue != null && compareDateValue != null)
        {
          if (dateValue.compareTo(compareDateValue) >= 0)
          {
            return true;
          }
        } else if (node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) >= 0)
        {
          return true;
        }
      }
      if (node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if ((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) >=
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final LessThanFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if (dateValue != null && compareDateValue != null)
        {
          if (dateValue.compareTo(compareDateValue) < 0)
          {
            return true;
          }
        } else if (node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) < 0)
        {
          return true;
        }
      }
      if (node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if ((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) <
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final LessThanOrEqualFilter filter,
                       final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if (node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if (dateValue != null && compareDateValue != null)
        {
          if (dateValue.compareTo(compareDateValue) <= 0)
          {
            return true;
          }
        } else if (node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) <= 0)
        {
          return true;
        }
      }
      if (node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if ((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) <=
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final AndFilter filter, final JsonNode object)
      throws SCIMException
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
  public Boolean visit(final OrFilter filter, final JsonNode object)
      throws SCIMException
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
  public Boolean visit(final NotFilter filter, final JsonNode object)
      throws SCIMException
  {
    return !filter.getInvertedFilter().visit(this, object);
  }

  /**
   * {@inheritDoc}
   */
  public Boolean visit(final ComplexValueFilter filter, final JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for (JsonNode node : nodes)
    {
      if (filter.getValueFilter().visit(this, node))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Try to parse out a date from a JSON text node.
   *
   * @param node The JSON node to parse.
   *
   * @return A parsed date instance or {@code null} if the text is not an
   * ISO8601 formatted date and time string.
   */
  private Date dateValue(final JsonNode node)
  {
    String text = node.textValue().trim();
    if (text.length() >= 19 &&
        Character.isDigit(text.charAt(0)) &&
        Character.isDigit(text.charAt(1)) &&
        Character.isDigit(text.charAt(2)) &&
        Character.isDigit(text.charAt(3)) &&
        text.charAt(4) == '-')
    {
      try
      {
        return ISO8601Utils.parse(text);
      }
      catch (IllegalArgumentException e)
      {
        // This is not a date after all.
      }
    }
    return null;
  }
}
