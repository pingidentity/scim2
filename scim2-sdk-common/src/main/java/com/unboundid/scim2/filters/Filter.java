package com.unboundid.scim2.filters;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;
import com.unboundid.scim2.utils.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by boli on 3/23/15.
 */
public abstract class Filter
{
  public abstract FilterType getFilterType();

  public abstract <R, P> R visit(FilterVisitor<R, P> visitor, P param)
      throws SCIMException;

  /**
   * Whether this filter is an 'and' or 'or' logical combining filter.
   *
   * @return {@code true} if this filter is an 'and' or 'or' logical
   * combining filter or {@code false} otherwise.
   */
  public boolean isCombiningFilter()
  {
    return false;
  }

  /**
   * Retrieve the combined filters for a logical combining filter.
   *
   * @return  The filter components for a logical combining filter.
   */
  public List<Filter> getCombinedFilters()
  {
    return null;
  }

  /**
   * Whether this filter is 'not' filter.
   *
   * @return {@code true} if this filter is an 'not' filter or
   * {@code false} otherwise.
   */
  public boolean isNotFilter()
  {
    return false;
  }

  /**
   * Retrieve the inverted filter for a 'not' filter or {@code null} if this
   * filter is not a 'not' filter.
   *
   * @return  The inverted filter for a 'not' filter or {@code null} if this
   * filter is not a 'not' filter.
   */
  public Filter getInvertedFilter()
  {
    return null;
  }

  /**
   * Whether this filter is a complex multi-valued attribute value filter.
   *
   * @return {@code true} if this filter is a complex multi-valued attribute
   * value filter or {@code false} otherwise.
   */
  public boolean isComplexValueFilter()
  {
    return false;
  }

  /**
   * Retrieve the value filter for complex multi-valued attribute value filter
   * or {@code null} if this filter is not a value filter.
   *
   * @return  The value filter for complex multi-valued attribute value filter
   * or {@code null} if this filter is not a value filter.
   */
  public Filter getValueFilter()
  {
    return null;
  }

  /**
   * Retrieve the path to the attribute to filter by, or {@code null} if
   * this filter is not a comparison filter or a value filter for complex
   * multi-valued attributes.
   *
   * @return  The attribute or sub-attribute to filter by
   */
  public Path getAttributePath()
  {
    return null;
  }

  /**
   * Whether this filter is a filter that compares attribute values against
   * a comparison value. The following are comparison filters:
   * eq, ne, co, sw, ew, gt, ge, lt, le.
   *
   * @return {@code true} if this is a comparison filter or {@code false}
   * otherwise.
   */
  public boolean isComparisonFilter()
  {
    return false;
  }

  /**
   * Retrieve the comparison value, or {@code null} if this filter is not
   * a comparison filter.
   *
   * @return  The comparison value, or {@code null} if this filter is not
   *          a comparison filter.
   */
  public ValueNode getComparisonValue()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    toString(builder);
    return builder.toString();
  }



  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder  The buffer to which the string representation of the
   *                 filter is to be appended.
   */
  public abstract void toString(final StringBuilder builder);

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(Path attributePath, ValueNode filterValue)
  {
    return new EqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, Integer filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, Long filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, Double filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, Float filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, BigDecimal filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, String filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, Boolean filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.booleanNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, byte[] filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.binaryNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new equality filter.
   */
  public static Filter eq(String attributePath, Date filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(Path attributePath, ValueNode filterValue)
  {
    return new NotEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, Integer filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, Long filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, Double filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, Float filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, BigDecimal filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, String filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, Boolean filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.booleanNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, byte[] filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.binaryNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new not equal filter.
   */
  public static Filter ne(String attributePath, Date filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new contains filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new contains filter.
   */
  public static Filter co(Path attributePath, ValueNode filterValue)
  {
    return new ContainsFilter(attributePath, filterValue);
  }

  /**
   * Create a new contains filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new contains filter.
   */
  public static Filter co(String attributePath, String filterValue)
  {
    return new ContainsFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new starts with filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new starts with filter.
   */
  public static Filter sw(Path attributePath, ValueNode filterValue)
  {
    return new StartsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new starts with filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new starts with filter.
   */
  public static Filter sw(String attributePath, String filterValue)
  {
    return new StartsWithFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new ends with filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new starts with filter.
   */
  public static Filter ew(Path attributePath, ValueNode filterValue)
  {
    return new EndsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new ends with filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new starts with filter.
   */
  public static Filter ew(String attributePath, String filterValue)
  {
    return new EndsWithFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new presence filter.
   *
   * @param attributePath  The path to the attribute to filter by.
   *
   * @return  A new presence filter.
   */
  public static Filter pr(Path attributePath)
  {
    return new PresentFilter(attributePath);
  }

  /**
   * Create a new presence filter.
   *
   * @param attributePath  The path to the attribute to filter by.
   *
   * @return  A new presence filter.
   */
  public static Filter pr(String attributePath)
  {
    return new PresentFilter(Path.fromString(attributePath));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(Path attributePath, ValueNode filterValue)
  {
    return new GreaterThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, Integer filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, Long filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, Double filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, Float filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, BigDecimal filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, String filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than filter.
   */
  public static Filter gt(String attributePath, Date filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(Path attributePath, ValueNode filterValue)
  {
    return new GreaterThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, Integer filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, Long filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, Double filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, Float filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, BigDecimal filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, String filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal filter.
   */
  public static Filter ge(String attributePath, Date filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(Path attributePath, ValueNode filterValue)
  {
    return new LessThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, Integer filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, Long filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, Double filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, Float filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, BigDecimal filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, String filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new greater than or equal to filter.
   */
  public static Filter lt(String attributePath, Date filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(Path attributePath, ValueNode filterValue)
  {
    return new LessThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, Integer filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, Long filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, Double filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, Float filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, BigDecimal filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, String filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath    The path to the attribute to filter by.
   * @param filterValue      The filter attribute value.
   *
   * @return  A new less than or equal filter.
   */
  public static Filter le(String attributePath, Date filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new and filter.
   *
   * @param filter1  The first filter.
   * @param filter2  The second filter.
   * @param filters  Additional filter components.
   *
   * @return  A new and filter.
   */
  public static Filter and(Filter filter1, Filter filter2, Filter... filters)
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(filter1);
    components.add(filter2);
    if(filters != null)
    {
      Collections.addAll(components, filters);
    }
    return new AndFilter(components);
  }

  /**
   * Create a new and filter.
   *
   * @param filter1  The first filter.
   * @param filter2  The second filter.
   * @param filters  Additional filter components.
   *
   * @return  A new and filter.
   */
  public static Filter and(String filter1, String filter2, String... filters)
      throws SCIMException
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(fromString(filter1));
    components.add(fromString(filter2));
    if (filters != null)
    {
      for (String filter : filters)
      {
        components.add(fromString(filter));
      }
    }
    return new AndFilter(components);
  }

  /**
   * Create a new or filter.
   *
   * @param filter1  The first filter.
   * @param filter2  The second filter.
   * @param filters  Additional filter components.
   *
   * @return  A new or filter.
   */
  public static Filter or(Filter filter1, Filter filter2, Filter... filters)
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(filter1);
    components.add(filter2);
    if(filters != null)
    {
      Collections.addAll(components, filters);
    }
    return new OrFilter(components);
  }

  /**
   * Create a new or filter.
   *
   * @param filter1  The first filter.
   * @param filter2  The second filter.
   * @param filters  Additional filter components.
   *
   * @return  A new or filter.
   */
  public static Filter or(String filter1, String filter2, String... filters)
      throws SCIMException
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(fromString(filter1));
    components.add(fromString(filter2));
    if (filters != null)
    {
      for (String filter : filters)
      {
        components.add(fromString(filter));
      }
    }
    return new OrFilter(components);
  }

  /**
   * Create a new and filter.
   *
   * @param filters  The filter components.
   *
   * @return  A new and filter.
   */
  public static Filter and(List<Filter> filters)
  {
    if(filters.size() < 2)
    {
      throw new IllegalArgumentException(
          "and logical filter must combine at least 2 filters");
    }
    return new AndFilter(new ArrayList<Filter>(filters));
  }

  /**
   * Create a new or filter.
   *
   * @param filters  The filter components.
   *
   * @return  A new or filter.
   */
  public static Filter or(List<Filter> filters)
  {
    if(filters.size() < 2)
    {
      throw new IllegalArgumentException(
          "or logical filter must combine at least 2 filters");
    }
    return new OrFilter(new ArrayList<Filter>(filters));
  }

  /**
   * Create a new not filter.
   *
   * @param filter   The inverted filter.
   *
   * @return  A new not filter.
   */
  public static Filter not(Filter filter)
  {
    return new NotFilter(filter);
  }

  /**
   * Create a new not filter.
   *
   * @param filter   The inverted filter.
   *
   * @return  A new not filter.
   * @throws SCIMException if the inverted filter is an invalid SCIM
   *                       filter.
   */
  public static Filter not(String filter) throws SCIMException
  {
    return new NotFilter(fromString(filter));
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   *
   * @return  A new complex multi-valued attribute value filter.
   */
  public static Filter hasComplexValue(Path attributePath, Filter valueFilter)
  {
    return new ComplexValueFilter(attributePath, valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   *
   * @return  A new complex multi-valued attribute value filter.
   */
  public static Filter hasComplexValue(String attributePath,
                                       Filter valueFilter)
  {
    return new ComplexValueFilter(Path.fromString(attributePath), valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   *
   * @return  A new complex multi-valued attribute value filter.
   * @throws SCIMException if the inverted filter is an invalid SCIM
   *                       filter.
   */
  public static Filter hasComplexValue(String attributePath,
                                       String valueFilter)
      throws SCIMException
  {
    return new ComplexValueFilter(Path.fromString(attributePath),
        fromString(valueFilter));
  }

  /**
   * Parse a filter from its string representation.
   *
   * @param filterString  The string representation of the filter expression.
   *
   * @return  The parsed filter.
   *
   * @throws  SCIMException  If the filter string could not be parsed.
   */
  public static Filter fromString(String filterString) throws SCIMException
  {
    return Parser.parseFilter(filterString);
  }
}
