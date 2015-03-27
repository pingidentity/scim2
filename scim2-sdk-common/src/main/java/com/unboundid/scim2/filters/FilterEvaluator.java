package com.unboundid.scim2.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.exceptions.InvalidResourceException;
import com.unboundid.scim2.exceptions.SCIMException;
import com.unboundid.scim2.utils.JsonUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by boli on 3/26/15.
 */
public class FilterEvaluator implements FilterVisitor<Boolean, JsonNode>
{
  private static final FilterEvaluator SINGLETON = new FilterEvaluator();

  public static boolean evaluate(Filter filter, JsonNode jsonNode)
      throws SCIMException
  {
    return filter.visit(SINGLETON, jsonNode);
  }

  public Boolean visit(EqualFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    if(filter.getComparisonValue().isNull() && nodes.isEmpty())
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      return true;
    }
    for(JsonNode node : nodes)
    {
      if(node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if(dateValue != null && compareDateValue != null &&
            dateValue.compareTo(compareDateValue) == 0)
        {
          return true;
        }
        if (node.textValue().equalsIgnoreCase(
            filter.getComparisonValue().textValue()))
        {
          return true;
        }
      }
      if(node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(NotEqualFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    if(filter.getComparisonValue().isNull() && nodes.isEmpty())
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      return false;
    }
    for(JsonNode node : nodes)
    {
      if(node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if(dateValue != null && compareDateValue != null &&
            dateValue.compareTo(compareDateValue) != 0)
        {
          return true;
        }
        if (!node.textValue().equalsIgnoreCase(
            filter.getComparisonValue().textValue()))
        {
          return true;
        }
      }
      if(!node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(ContainsFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isTextual() && filter.getComparisonValue().isTextual() &&
          node.textValue().toLowerCase().contains(
              filter.getComparisonValue().textValue().toLowerCase()) ||
          node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(StartsWithFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isTextual() && filter.getComparisonValue().isTextual() &&
          node.textValue().toLowerCase().startsWith(
              filter.getComparisonValue().textValue().toLowerCase()) ||
          node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(EndsWithFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isTextual() && filter.getComparisonValue().isTextual() &&
          node.textValue().toLowerCase().endsWith(
              filter.getComparisonValue().textValue().toLowerCase()) ||
          node.equals(filter.getComparisonValue()))
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(PresentFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      // draft-ietf-scim-core-schema section 2.4 states "Unassigned
      // attributes, the null value, or empty array (in the case of
      // a multi-valued attribute) SHALL be considered to be
      // equivalent in "state".
      if(!node.isNull())
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(GreaterThanFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if(node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if(dateValue != null && compareDateValue != null &&
            dateValue.compareTo(compareDateValue) > 0)
        {
          return true;
        }
        if(node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) > 0)
        {
          return true;
        }
      }
      if(node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) >
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
          return true;
      }
    }
    return false;
  }

  public Boolean visit(GreaterThanOrEqualFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if(node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if(dateValue != null && compareDateValue != null &&
            dateValue.compareTo(compareDateValue) >= 0)
        {
          return true;
        }
        if(node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) >= 0)
        {
          return true;
        }
      }
      if(node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) >=
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
          return true;
      }
    }
    return false;
  }

  public Boolean visit(LessThanFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if(node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if(dateValue != null && compareDateValue != null &&
            dateValue.compareTo(compareDateValue) < 0)
        {
          return true;
        }
        if(node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) < 0)
        {
          return true;
        }
      }
      if(node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) <
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
          return true;
      }
    }
    return false;
  }

  public Boolean visit(LessThanOrEqualFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(node.isBoolean() || node.isBinary())
      {
        throw new InvalidResourceException("Greater than filter may not " +
            "compare boolean or binary attribute values");
      }
      if(node.isTextual() && filter.getComparisonValue().isTextual())
      {
        Date dateValue = dateValue(node);
        Date compareDateValue = dateValue(filter.getComparisonValue());
        if(dateValue != null && compareDateValue != null &&
            dateValue.compareTo(compareDateValue) <= 0)
        {
          return true;
        }
        if(node.textValue().compareToIgnoreCase(
            filter.getComparisonValue().textValue()) <= 0)
        {
          return true;
        }
      }
      if(node.isNumber() && filter.getComparisonValue().isNumber())
      {
        if((node.isFloatingPointNumber() ?
            node.doubleValue() : node.longValue()) <=
            (filter.getComparisonValue().isFloatingPointNumber() ?
                filter.getComparisonValue().doubleValue() :
                filter.getComparisonValue().longValue()))
          return true;
      }
    }
    return false;
  }

  public Boolean visit(AndFilter filter, JsonNode object)
      throws SCIMException
  {
    for(Filter combinedFilter : filter.getCombinedFilters())
    {
      if(!combinedFilter.visit(this, object))
      {
        return false;
      }
    }
    return true;
  }

  public Boolean visit(OrFilter filter, JsonNode object)
      throws SCIMException
  {
    for(Filter combinedFilter : filter.getCombinedFilters())
    {
      if(combinedFilter.visit(this, object))
      {
        return true;
      }
    }
    return false;
  }

  public Boolean visit(NotFilter filter, JsonNode object)
      throws SCIMException
  {
    return !filter.getInvertedFilter().visit(this, object);
  }

  public Boolean visit(ComplexValueFilter filter, JsonNode object)
      throws SCIMException
  {
    List<JsonNode> nodes = JsonUtils.gatherValues(
        filter.getAttributePath(), object);
    for(JsonNode node : nodes)
    {
      if(filter.getValueFilter().visit(this, node))
      {
        return true;
      }
    }
    return false;
  }

  private Date dateValue(JsonNode node)
  {
    String text = node.textValue().trim();
    if(text.length() >= 19 &&
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
      catch(IllegalArgumentException e)
      {
        // This is not a date after all.
      }
    }
    return null;
  }
}
