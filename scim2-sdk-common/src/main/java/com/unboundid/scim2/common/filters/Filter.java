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

package com.unboundid.scim2.common.filters;

import com.fasterxml.jackson.databind.node.ValueNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.DateTimeUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class represents a SCIM 2 filter expression. A filter can be used by a
 * SCIM client to request a subset of SCIM resources that match a criteria. For
 * example, the SCIM filter {@code nickName eq "Alice"} would match all
 * resources whose {@code nickName} field equals "Alice".
 * <br><br>
 * In general, filters are comprised of up to three parts. For the
 * {@code nickName eq "Alice"} filter expression, these are:
 * <ul>
 *   <li> An attribute name/path (nickName)
 *   <li> A filter operator (eq)
 *   <li> A filter value (Alice)
 * </ul>
 * Attribute paths and filter operators are not case-sensitive.
 * <br><br>
 * The following filter operator types are defined:
 * <ul>
 *   <li> {@code eq}:
 *        An {@link EqualFilter} will match a SCIM resource if the resource's
 *        attribute value is identical to the filter value.
 *   <li> {@code ne}:
 *        A {@link NotEqualFilter} will match a SCIM resource if the resource's
 *        attribute value is different from the filter value.
 *   <li> {@code co}:
 *        A {@link ContainsFilter} will match a SCIM resource if the entire
 *        filter value is a substring of the resource's attribute value.
 *   <li> {@code sw}:
 *        A {@link StartsWithFilter} will match a SCIM resource if the
 *        resource's attribute value begins with the filter value.
 *   <li> {@code ew}:
 *        An {@link EndsWithFilter} will match a SCIM resource if the resource's
 *        attribute value ends with the filter value.
 *   <li> {@code pr}:
 *        A {@link PresentFilter} will match a SCIM resource if the resource
 *        contains a non-null and non-empty value for the attribute specified in
 *        the filter.
 *   <li> {@code gt}:
 *        A {@link GreaterThanFilter} will match a SCIM resource if the
 *        resource's attribute value is greater than the filter value.
 *   <li> {@code ge}:
 *        A {@link GreaterThanOrEqualFilter} will match a SCIM resource if the
 *        resource's attribute value is greater than or equal to the filter
 *        value.
 *   <li> {@code lt}:
 *        A {@link LessThanFilter} will match a SCIM resource if the resource's
 *        attribute value is less than the filter value.
 *   <li> {@code le}:
 *        A {@link LessThanOrEqualFilter} will match a SCIM resource if the
 *        resource's attribute value is less than or equal to the filter value.
 *   <li> {@code and}:
 *        An {@link AndFilter} is a "combining" filter that joins two filters.
 *        This filter type will match a SCIM resource if both subordinate
 *        filters match that resource.
 *   <li> {@code or}:
 *        An {@link OrFilter} is a "combining" filter that joins two filters.
 *        This filter type will match a SCIM resource if either subordinate
 *        filter matches that resource.
 *   <li> {@code not}:
 *        A {@link NotFilter} inverts another filter. It will match a SCIM
 *        resource if the other filter is not a match.
 * </ul>
 * <br><br>
 * To create a new SCIM filter, use the static methods defined on this parent
 * Filter class. For example, to create an equality filter, use:
 * <pre>
 *   Filter equalFilter = Filter.eq("nickName", "Alice");
 * </pre>
 *
 * A Filter Java object can also be created from a string. This method is most
 * useful when a SCIM filter is received as a string, such as if a filter was
 * provided by a client. Note that creating a filter from a hard-coded string
 * in code is generally discouraged, since it can be easy to cause errors with
 * typos.
 * <pre>
 *   Filter filterObject = Filter.fromString("nickName eq \"Alice\");
 * </pre>
 * Similarly, to retrieve the string representation of a Filter object, use
 * {@link Filter#toString()}.
 * <pre>
 *   String stringRepresentation = filterObject.toString();
 * </pre>
 *
 * A Filter object must be one of the following types (except for
 * {@link PresentFilter}).
 * <ul>
 *   <li> {@link CombiningFilter} ({@code and}, {@code or})
 *   <li> {@link ComparisonFilter} ({@code eq}, {@code gt}, {@code le}, etc.)
 *   <li> {@link ComplexValueFilter} (used to signify a specific value within a
 *                                    multi-valued attribute)
 *   <li> {@link NotFilter} ({@code not})
 * </ul>
 * To determine whether a filter is one of these types, use methods like
 * {@link Filter#isCombiningFilter()} and {@link Filter#isNotFilter()}.
 * <br><br>
 * For more information on a particular filter type, see the class-level Javadoc
 * representing the filter type (e.g., {@link EqualFilter}).
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
   * @throws ScimException The exception thrown from the filter visitor.
   */
  public abstract <R, P> R visit(final FilterVisitor<R, P> visitor,
                                 final P param)
      throws ScimException;

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public abstract void toString(final StringBuilder builder);

  /**
   * Whether this filter instance is a {@link CombiningFilter}.
   *
   * @return {@code true} if this filter is an {@link AndFilter} or
   * {@link OrFilter} logical combining filter, or {@code false} otherwise.
   */
  public boolean isCombiningFilter()
  {
    return false;
  }

  /**
   * Retrieve the combined filters for a logical combining filter.
   *
   * @return The subordinate filter components for a logical combining filter,
   *         or {@code null} if the filter is not a {@link CombiningFilter}.
   */
  public List<Filter> getCombinedFilters()
  {
    return null;
  }

  /**
   * Whether this filter instance is a {@link NotFilter}.
   *
   * @return {@code true} if this filter is a {@code not} filter or
   * {@code false} otherwise.
   */
  public boolean isNotFilter()
  {
    return false;
  }

  /**
   * Retrieve the inverted filter for a {@link NotFilter}. If this Filter
   * instance is not a NOT filter, this method will return {@code null}.
   *
   * @return The inverted version of this filter, or {@code null} if this filter
   *         instance is not a {@link NotFilter}.
   */
  public Filter getInvertedFilter()
  {
    return null;
  }

  /**
   * Whether this filter instance is a complex multi-valued attribute value
   * filter (i.e., a {@link ComplexValueFilter}).
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
   * this filter is not a {@link ComparisonFilter} or a
   * {@link ComplexValueFilter}.
   *
   * @return The attribute or sub-attribute to filter by.
   */
  public Path getAttributePath()
  {
    return null;
  }

  /**
   * Whether this filter instance is a {@link ComparisonFilter}.
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
   * Retrieves a string representation of this filter.
   *
   * @return  A string representation of this filter.
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    toString(builder);
    return builder.toString();
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   */
  public static Filter eq(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new EqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath,
                          final Integer filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath, final Long filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath, final Double filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath, final Float filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath,
                          final Boolean filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().booleanNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath, final byte[] filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().binaryNode(filterValue));
  }

  /**
   * Create a new {@code equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter eq(final String attributePath, final Date filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   */
  public static Filter ne(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new NotEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath,
                          final Integer filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath, final Long filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath, final Double filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath, final Float filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath,
                          final Boolean filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().booleanNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath, final byte[] filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().binaryNode(filterValue));
  }

  /**
   * Create a new {@code not equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ne(final String attributePath, final Date filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@code contains} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code contains} filter.
   */
  public static Filter co(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new ContainsFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code contains} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code contains} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter co(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new ContainsFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code starts with} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code starts with} filter.
   */
  public static Filter sw(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new StartsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code starts with} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code starts with} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter sw(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new StartsWithFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code ends with} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code ends with} filter.
   */
  public static Filter ew(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new EndsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code ends with} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code ends with} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ew(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new EndsWithFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code present} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @return A new {@code present} filter.
   */
  public static Filter pr(final Path attributePath)
  {
    return new PresentFilter(attributePath);
  }

  /**
   * Create a new {@code present} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @return A new {@code present} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter pr(final String attributePath)
      throws BadRequestException
  {
    return new PresentFilter(Path.fromString(attributePath));
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   */
  public static Filter gt(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new GreaterThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter gt(final String attributePath,
                          final Integer filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter gt(final String attributePath, final Long filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter gt(final String attributePath, final Double filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter gt(final String attributePath, final Float filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter gt(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code greater than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter gt(final String attributePath, final Date filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   */
  public static Filter ge(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new GreaterThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ge(final String attributePath,
                          final Integer filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ge(final String attributePath, final Long filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ge(final String attributePath, final Double filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ge(final String attributePath, final Float filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ge(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code greater than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter ge(final String attributePath, final Date filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   */
  public static Filter lt(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new LessThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter lt(final String attributePath,
                          final Integer filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter lt(final String attributePath, final Long filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter lt(final String attributePath, final Double filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter lt(final String attributePath, final Float filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter lt(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code less than} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter lt(final String attributePath, final Date filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   */
  public static Filter le(final Path attributePath,
                          final ValueNode filterValue)
  {
    return new LessThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter le(final String attributePath,
                          final Integer filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter le(final String attributePath, final Long filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter le(final String attributePath, final Double filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter le(final String attributePath, final Float filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter le(final String attributePath, final String filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@code less than or equal} filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter le(final String attributePath, final Date filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@code and} filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code and} filter.
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
   * Create a new {@code and} filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code and} filter.
   * @throws BadRequestException If one of the filters could not be parsed.
   */
  public static Filter and(final String filter1, final String filter2,
                           final String... filters)
      throws BadRequestException
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
   * Create a new {@code or} filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code or} filter.
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
   * Create a new {@code or} filter.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code or} filter.
   * @throws BadRequestException If one of the filters could not be parsed.
   */
  public static Filter or(final String filter1, final String filter2,
                          final String... filters)
      throws BadRequestException
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
   * Create a new {@code and} filter.
   *
   * @param filters The filter components.
   * @return A new {@code and} filter.
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
   * Create a new {@code or} filter.
   *
   * @param filters The filter components.
   * @return A new {@code or} filter.
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
   * Create a new {@code not} filter.
   *
   * @param filter The inverted filter.
   * @return A new {@code not} filter.
   */
  public static Filter not(final Filter filter)
  {
    return new NotFilter(filter);
  }

  /**
   * Create a new {@code not} filter.
   *
   * @param filter The inverted filter.
   * @return A new {@code not} filter.
   * @throws BadRequestException If the filter could not be parsed.
   */
  public static Filter not(final String filter) throws BadRequestException
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
   * @throws BadRequestException If the path could not be parsed.
   */
  public static Filter hasComplexValue(final String attributePath,
                                       final Filter valueFilter)
      throws BadRequestException
  {
    return new ComplexValueFilter(Path.fromString(attributePath), valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   * @throws BadRequestException If the path or filter could not be parsed.
   */
  public static Filter hasComplexValue(final String attributePath,
                                       final String valueFilter)
      throws BadRequestException
  {
    return new ComplexValueFilter(Path.fromString(attributePath),
        fromString(valueFilter));
  }

  /**
   * Parse a filter from its string representation.
   *
   * @param filterString The string representation of the filter expression.
   * @return The parsed filter.
   * @throws BadRequestException If the filter could not be parsed.
   */
  public static Filter fromString(final String filterString)
      throws BadRequestException
  {
    return Parser.parseFilter(filterString);
  }
}
