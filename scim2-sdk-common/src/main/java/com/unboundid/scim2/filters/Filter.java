/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.filters;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.Path;
import com.unboundid.scim2.exceptions.SCIMException;
import com.unboundid.scim2.utils.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Base class for parsing and creating new SCIM 2 filter instances.
 */
public abstract class Filter
{
  /**
   * Retrieve the filter type.
   *
   * @return The filter type.
   */
  public abstract FilterType getFilterType();

  /**
   * Visit this filter using the provided filter visitor.
   *
   * @param visitor The {@code FilterVisitor} instance.
   * @param param   An optional parameter.
   * @param <R>     The return type of the filter visitor.
   * @param <P>     The optional parameter type accepted by the filter visitor.
   * @return The return type from the filter visitor.
   * @throws SCIMException The exception thrown from the filter visitor.
   */
  public abstract <R, P> R visit(final FilterVisitor<R, P> visitor,
                                 final P param)
      throws SCIMException;

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public abstract void toString(final StringBuilder builder);

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
   * @return The filter components for a logical combining filter.
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
   * @return The inverted filter for a 'not' filter or {@code null} if this
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
   * @return The value filter for complex multi-valued attribute value filter
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
   * @return The attribute or sub-attribute to filter by
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
   * @return The comparison value, or {@code null} if this filter is not
   * a comparison filter.
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
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new EqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath,
                          final Integer filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath, final Long filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath, final Double filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath, final Float filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath,
                          final BigDecimal filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath, final String filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath,
                          final Boolean filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.booleanNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath, final byte[] filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.binaryNode(filterValue));
  }

  /**
   * Create a new equality filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new equality filter.
   */
  public static Filter eq(final String attributePath, final Date filterValue)
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new NotEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath,
                          final Integer filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath, final Long filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath, final Double filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath, final Float filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath,
                          final BigDecimal filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath, final String filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath,
                          final Boolean filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.booleanNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath, final byte[] filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.binaryNode(filterValue));
  }

  /**
   * Create a new not equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new not equal filter.
   */
  public static Filter ne(final String attributePath, final Date filterValue)
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new contains filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new contains filter.
   */
  public static Filter co(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new ContainsFilter(attributePath, filterValue);
  }

  /**
   * Create a new contains filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new contains filter.
   */
  public static Filter co(final String attributePath, final String filterValue)
  {
    return new ContainsFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new starts with filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new starts with filter.
   */
  public static Filter sw(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new StartsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new starts with filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new starts with filter.
   */
  public static Filter sw(final String attributePath, final String filterValue)
  {
    return new StartsWithFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new ends with filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new starts with filter.
   */
  public static Filter ew(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new EndsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new ends with filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new starts with filter.
   */
  public static Filter ew(final String attributePath, final String filterValue)
  {
    return new EndsWithFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new presence filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @return A new presence filter.
   */
  public static Filter pr(final Path attributePath)
  {
    return new PresentFilter(attributePath);
  }

  /**
   * Create a new presence filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @return A new presence filter.
   */
  public static Filter pr(final String attributePath)
  {
    return new PresentFilter(Path.fromString(attributePath));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new GreaterThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath,
                          final Integer filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath, final Long filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath, final Double filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath, final Float filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath,
                          final BigDecimal filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath, final String filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new greater than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than filter.
   */
  public static Filter gt(final String attributePath, final Date filterValue)
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new GreaterThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath,
                          final Integer filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath, final Long filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath, final Double filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath, final Float filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath,
                          final BigDecimal filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath, final String filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new greater than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal filter.
   */
  public static Filter ge(final String attributePath, final Date filterValue)
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new LessThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath,
                          final Integer filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath, final Long filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath, final Double filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath, final Float filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath,
                          final BigDecimal filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath, final String filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new less than filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new greater than or equal to filter.
   */
  public static Filter lt(final String attributePath, final Date filterValue)
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new LessThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath,
                          final Integer filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath, final Long filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath, final Double filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath, final Float filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath,
                          final BigDecimal filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.numberNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath, final String filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(filterValue));
  }

  /**
   * Create a new less than or equal filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new less than or equal filter.
   */
  public static Filter le(final String attributePath, final Date filterValue)
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonNodeFactory.instance.textNode(
            ISO8601Utils.format(filterValue)));
  }

  /**
   * Create a new and filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new and filter.
   */
  public static Filter and(final Filter filter1, final Filter filter2,
                           final Filter... filters)
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(filter1);
    components.add(filter2);
    if (filters != null)
    {
      Collections.addAll(components, filters);
    }
    return new AndFilter(components);
  }

  /**
   * Create a new and filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new and filter.
   * @throws SCIMException if the one or more of the filters is an invalid SCIM
   *                       filter.
   */
  public static Filter and(final String filter1, final String filter2,
                           final String... filters)
      throws SCIMException
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(fromString(filter1));
    components.add(fromString(filter2));
    if (filters != null)
    {
      for (final String filter : filters)
      {
        components.add(fromString(filter));
      }
    }
    return new AndFilter(components);
  }

  /**
   * Create a new or filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new or filter.
   */
  public static Filter or(final Filter filter1, final Filter filter2,
                          final Filter... filters)
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(filter1);
    components.add(filter2);
    if (filters != null)
    {
      Collections.addAll(components, filters);
    }
    return new OrFilter(components);
  }

  /**
   * Create a new or filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new or filter.
   * @throws SCIMException if the one or more of the filters is an invalid SCIM
   *                       filter.
   */
  public static Filter or(final String filter1, final String filter2,
                          final String... filters)
      throws SCIMException
  {
    ArrayList<Filter> components =
        new ArrayList<Filter>(filters != null ? 2 + filters.length : 2);
    components.add(fromString(filter1));
    components.add(fromString(filter2));
    if (filters != null)
    {
      for (final String filter : filters)
      {
        components.add(fromString(filter));
      }
    }
    return new OrFilter(components);
  }

  /**
   * Create a new and filter.
   *
   * @param filters The filter components.
   * @return A new and filter.
   */
  public static Filter and(final List<Filter> filters)
  {
    if (filters.size() < 2)
    {
      throw new IllegalArgumentException(
          "and logical filter must combine at least 2 filters");
    }
    return new AndFilter(new ArrayList<Filter>(filters));
  }

  /**
   * Create a new or filter.
   *
   * @param filters The filter components.
   * @return A new or filter.
   */
  public static Filter or(final List<Filter> filters)
  {
    if (filters.size() < 2)
    {
      throw new IllegalArgumentException(
          "or logical filter must combine at least 2 filters");
    }
    return new OrFilter(new ArrayList<Filter>(filters));
  }

  /**
   * Create a new not filter.
   *
   * @param filter The inverted filter.
   * @return A new not filter.
   */
  public static Filter not(final Filter filter)
  {
    return new NotFilter(filter);
  }

  /**
   * Create a new not filter.
   *
   * @param filter The inverted filter.
   * @return A new not filter.
   * @throws SCIMException if the inverted filter is an invalid SCIM
   *                       filter.
   */
  public static Filter not(final String filter) throws SCIMException
  {
    return new NotFilter(fromString(filter));
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   */
  public static Filter hasComplexValue(final Path attributePath,
                                       final Filter valueFilter)
  {
    return new ComplexValueFilter(attributePath, valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   */
  public static Filter hasComplexValue(final String attributePath,
                                       final Filter valueFilter)
  {
    return new ComplexValueFilter(Path.fromString(attributePath), valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   * @throws SCIMException if the inverted filter is an invalid SCIM
   *                       filter.
   */
  public static Filter hasComplexValue(final String attributePath,
                                       final String valueFilter)
      throws SCIMException
  {
    return new ComplexValueFilter(Path.fromString(attributePath),
        fromString(valueFilter));
  }

  /**
   * Parse a filter from its string representation.
   *
   * @param filterString The string representation of the filter expression.
   * @return The parsed filter.
   * @throws SCIMException If the filter string could not be parsed.
   */
  public static Filter fromString(final String filterString)
      throws SCIMException
  {
    return Parser.parseFilter(filterString);
  }
}
