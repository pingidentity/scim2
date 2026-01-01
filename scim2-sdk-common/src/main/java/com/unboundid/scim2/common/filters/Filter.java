/*
 * Copyright 2015-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
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
 * This class represents a SCIM 2 filter expression as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.4.2.2">
 * RFC 7644 Section 3.4.2.2</a>. A filter can be used by a SCIM client to
 * request a subset of SCIM resources that match a criteria. For example, the
 * SCIM filter {@code nickName eq "Alice"} would match all resources whose
 * {@code nickName} field equals "Alice".
 * <br><br>
 *
 * In general, filters are comprised of up to three parts. For the
 * {@code nickName eq "Alice"} filter expression, these are:
 * <ul>
 *   <li> An attribute name/path (nickName)
 *   <li> A filter operator (eq)
 *   <li> A filter value (Alice)
 * </ul>
 * Attribute paths and filter operators are not case-sensitive.
 * <br><br>
 *
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
 *   <li> {@code []}:
 *        A {@link ComplexValueFilter} (e.g., {@code emails[primary eq true]})
 *        will match a subset of values within a multi-valued attribute.
 * </ul>
 * <br><br>
 *
 * To create a new SCIM filter, use the static methods defined on this parent
 * Filter class. For example, to create an equality filter, use:
 * <pre><code>
 *   Filter equalFilter = Filter.eq("nickName", "Alice");
 * </code></pre>
 *
 * A Filter Java object can also be created from a string. This method is most
 * useful when a SCIM filter is received as a string, such as if a filter was
 * provided by a client. Note that creating a filter from a hard-coded string
 * in code is generally discouraged, since it can be easy to cause errors with
 * typos.
 * <pre><code>
 *   Filter filterObject = Filter.fromString("nickName eq \"Alice\");
 * </code></pre>
 *
 * Similarly, to retrieve the string representation of a Filter object, use
 * {@link Filter#toString()}.
 * <pre><code>
 *   String stringRepresentation = filterObject.toString();
 * </code></pre>
 *
 * All Filter objects are one of the following types. To determine the type for
 * a particular filter object, use the corresponding helper function:
 * <ul>
 * <li> {@link CombiningFilter} (and, or): {@link #isCombiningFilter()}
 * <li> {@link ComparisonFilter} (eq, gt, etc.): {@link #isComparisonFilter()}
 * <li> {@link ComplexValueFilter} ({@code []}): {@link #isComplexValueFilter()}
 * <li> {@link NotFilter} (not): {@link #isNotFilter()}
 * <li> {@link PresentFilter} (pr): {@link #isPresentFilter()}
 * </ul>
 *
 * For example:
 * <pre><code>
 *   Filter clientFilter = getClientFilter();
 *   if (clientFilter.isCombiningFilter())
 *   {
 *     // Logic that is specific to AND/OR filter types.
 *   }
 * </code></pre>
 *
 * For more information on a particular filter type, see its class-level Javadoc
 * (e.g., {@link EqualFilter}).
 */
public abstract class Filter
{
  /**
   * Retrieve the filter type.
   *
   * @return The filter type.
   */
  @NotNull
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
  @NotNull
  public abstract <R, P> R visit(@NotNull final FilterVisitor<R, P> visitor,
                                 @Nullable final P param)
      throws ScimException;

  /**
   * Append the string representation of the filter to the provided buffer.
   *
   * @param builder The buffer to which the string representation of the
   *                filter is to be appended.
   */
  public abstract void toString(@NotNull final StringBuilder builder);

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
  @Nullable
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
  @Nullable
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
  @Nullable
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
  @Nullable
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
  @Nullable
  public ValueNode getComparisonValue()
  {
    return null;
  }

  /**
   * Whether this filter instance is a {@link PresentFilter}.
   *
   * @return {@code true} if this is a presence filter or {@code false}
   * otherwise.
   *
   * @since 4.0.0
   */
  public boolean isPresentFilter()
  {
    return false;
  }

  /**
   * Retrieves a string representation of this filter.
   *
   * @return  A string representation of this filter.
   */
  @Override
  @NotNull
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    toString(builder);
    return builder.toString();
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   */
  @NotNull
  public static Filter eq(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new EqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final Integer filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final Long filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final Double filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final Float filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final Boolean filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().booleanNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final byte[] filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().binaryNode(filterValue));
  }

  /**
   * Create a new {@link EqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter eq(@NotNull final String attributePath,
                          @Nullable final Date filterValue)
      throws BadRequestException
  {
    return new EqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   */
  @NotNull
  public static Filter ne(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new NotEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final Integer filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final Long filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final Double filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final Float filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final Boolean filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().booleanNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final byte[] filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().binaryNode(filterValue));
  }

  /**
   * Create a new {@link NotEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code not equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ne(@NotNull final String attributePath,
                          @Nullable final Date filterValue)
      throws BadRequestException
  {
    return new NotEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@link ContainsFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code contains} filter.
   */
  @NotNull
  public static Filter co(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new ContainsFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link ContainsFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code contains} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter co(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new ContainsFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link StartsWithFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code starts with} filter.
   */
  @NotNull
  public static Filter sw(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new StartsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link StartsWithFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code starts with} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter sw(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new StartsWithFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link EndsWithFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code ends with} filter.
   */
  @NotNull
  public static Filter ew(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new EndsWithFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link EndsWithFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code ends with} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ew(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new EndsWithFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link PresentFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @return A new {@code present} filter.
   */
  @NotNull
  public static Filter pr(@NotNull final Path attributePath)
  {
    return new PresentFilter(attributePath);
  }

  /**
   * Create a new {@link PresentFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @return A new {@code present} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter pr(@NotNull final String attributePath)
      throws BadRequestException
  {
    return new PresentFilter(Path.fromString(attributePath));
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   */
  @NotNull
  public static Filter gt(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new GreaterThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter gt(@NotNull final String attributePath,
                          @Nullable final Integer filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter gt(@NotNull final String attributePath,
                          @Nullable final Long filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter gt(@NotNull final String attributePath,
                          @Nullable final Double filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter gt(@NotNull final String attributePath,
                          @Nullable final Float filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter gt(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter gt(@NotNull final String attributePath,
                          @Nullable final Date filterValue)
      throws BadRequestException
  {
    return new GreaterThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   */
  @NotNull
  public static Filter ge(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new GreaterThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ge(@NotNull final String attributePath,
                          @Nullable final Integer filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ge(@NotNull final String attributePath,
                          @Nullable final Long filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ge(@NotNull final String attributePath,
                          @Nullable final Double filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ge(@NotNull final String attributePath,
                          @Nullable final Float filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ge(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link GreaterThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code greater than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter ge(@NotNull final String attributePath,
                          @Nullable final Date filterValue)
      throws BadRequestException
  {
    return new GreaterThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   */
  @NotNull
  public static Filter lt(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new LessThanFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter lt(@NotNull final String attributePath,
                          @Nullable final Integer filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter lt(@NotNull final String attributePath,
                          @Nullable final Long filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter lt(@NotNull final String attributePath,
                          @Nullable final Double filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter lt(@NotNull final String attributePath,
                          @Nullable final Float filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter lt(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link LessThanFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter lt(@NotNull final String attributePath,
                          @Nullable final Date filterValue)
      throws BadRequestException
  {
    return new LessThanFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   */
  @NotNull
  public static Filter le(@NotNull final Path attributePath,
                          @Nullable final ValueNode filterValue)
  {
    return new LessThanOrEqualFilter(attributePath, filterValue);
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter le(@NotNull final String attributePath,
                          @Nullable final Integer filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter le(@NotNull final String attributePath,
                          @Nullable final Long filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter le(@NotNull final String attributePath,
                          @Nullable final Double filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter le(@NotNull final String attributePath,
                          @Nullable final Float filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().numberNode(filterValue));
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter le(@NotNull final String attributePath,
                          @Nullable final String filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(filterValue));
  }

  /**
   * Create a new {@link LessThanOrEqualFilter}.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param filterValue   The filter attribute value.
   * @return A new {@code less than or equal} filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter le(@NotNull final String attributePath,
                          @Nullable final Date filterValue)
      throws BadRequestException
  {
    return new LessThanOrEqualFilter(Path.fromString(attributePath),
        JsonUtils.getJsonNodeFactory().textNode(
            DateTimeUtils.format(filterValue)));
  }

  /**
   * Create a new {@link AndFilter}.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code and} filter.
   */
  @NotNull
  public static Filter and(@NotNull final Filter filter1,
                           @NotNull final Filter filter2,
                           @Nullable final Filter... filters)
  {
    ArrayList<Filter> components =
        new ArrayList<>(filters != null ? 2 + filters.length : 2);
    components.add(filter1);
    components.add(filter2);
    if (filters != null)
    {
      Collections.addAll(components, filters);
    }
    return new AndFilter(components);
  }

  /**
   * Create a new {@link AndFilter}.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code and} filter.
   * @throws BadRequestException If one of the filters could not be parsed.
   */
  @NotNull
  public static Filter and(@NotNull final String filter1,
                           @NotNull final String filter2,
                           @Nullable final String... filters)
      throws BadRequestException
  {
    ArrayList<Filter> components =
        new ArrayList<>(filters != null ? 2 + filters.length : 2);
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
   * Create a new {@link AndFilter}.
   *
   * @param filters The filter components.
   * @return A new {@code and} filter.
   */
  @NotNull
  public static Filter and(@NotNull final List<Filter> filters)
  {
    if (filters.size() < 2)
    {
      throw new IllegalArgumentException(
          "and logical filter must combine at least 2 filters");
    }
    return new AndFilter(new ArrayList<>(filters));
  }

  /**
   * Create a new {@link OrFilter}.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code or} filter.
   */
  @NotNull
  public static Filter or(@NotNull final Filter filter1,
                          @NotNull final Filter filter2,
                          @Nullable final Filter... filters)
  {
    ArrayList<Filter> components =
        new ArrayList<>(filters != null ? 2 + filters.length : 2);
    components.add(filter1);
    components.add(filter2);
    if (filters != null)
    {
      Collections.addAll(components, filters);
    }
    return new OrFilter(components);
  }

  /**
   * Create a new {@link OrFilter}.
   *
   * @param filter1 The first filter.
   * @param filter2 The second filter.
   * @param filters Additional filter components.
   * @return A new {@code or} filter.
   * @throws BadRequestException If one of the filters could not be parsed.
   */
  @NotNull
  public static Filter or(@NotNull final String filter1,
                          @NotNull final String filter2,
                          @Nullable final String... filters)
      throws BadRequestException
  {
    ArrayList<Filter> components =
        new ArrayList<>(filters != null ? 2 + filters.length : 2);
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
   * Create a new {@link OrFilter}.
   *
   * @param filters The filter components.
   * @return A new {@code or} filter.
   */
  @NotNull
  public static Filter or(@NotNull final List<Filter> filters)
  {
    if (filters.size() < 2)
    {
      throw new IllegalArgumentException(
          "or logical filter must combine at least 2 filters");
    }
    return new OrFilter(new ArrayList<>(filters));
  }

  /**
   * Create a new {@link NotFilter}.
   *
   * @param filter The filter that should be inverted.
   * @return A new {@code not} filter.
   */
  @NotNull
  public static Filter not(@NotNull final Filter filter)
  {
    return new NotFilter(filter);
  }

  /**
   * Create a new {@link NotFilter}.
   *
   * @param filter The filter that should be inverted.
   * @return A new {@code not} filter.
   * @throws BadRequestException If the filter could not be parsed.
   */
  @NotNull
  public static Filter not(@NotNull final String filter)
      throws BadRequestException
  {
    return new NotFilter(fromString(filter));
  }

  /**
   * Identical to the {@link #complex(Path, Filter)} method, which creates a
   * {@link ComplexValueFilter}. It is encouraged to use the other method, but
   * this one may still be used.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   */
  @NotNull
  public static Filter hasComplexValue(@NotNull final Path attributePath,
                                       @Nullable final Filter valueFilter)
  {
    return Filter.complex(attributePath, valueFilter);
  }

  /**
   * Identical to the {@link #complex(String, Filter)} method, which creates a
   * {@link ComplexValueFilter}. It is encouraged to use the other method, but
   * this one may still be used.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   * @throws BadRequestException If the path could not be parsed.
   */
  @NotNull
  public static Filter hasComplexValue(@NotNull final String attributePath,
                                       @Nullable final Filter valueFilter)
      throws BadRequestException
  {
    return Filter.complex(attributePath, valueFilter);
  }

  /**
   * Identical to the {@link #complex(String, String)} method, which creates a
   * {@link ComplexValueFilter}. It is encouraged to use the other method, but
   * this one may still be used.
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   * @throws BadRequestException If the path or filter could not be parsed.
   */
  @NotNull
  public static Filter hasComplexValue(@NotNull final String attributePath,
                                       @Nullable final String valueFilter)
      throws BadRequestException
  {
    return Filter.complex(attributePath, valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter (i.e., a
   * {@link ComplexValueFilter}). For example, to create a filter representing
   * {@code addresses[postalCode eq \"12345\"]}, use the following Java code:
   *
   * <pre><code>
   *   Filter complexFilter =
   *       Filter.complex("addresses", Filter.eq("postalCode", "12345"));
   * </code></pre>
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   *
   * @since 4.0.0
   */
  @NotNull
  public static Filter complex(@NotNull final Path attributePath,
                               @Nullable final Filter valueFilter)
  {
    return new ComplexValueFilter(attributePath, valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter (i.e., a
   * {@link ComplexValueFilter}). For example, to create a filter representing
   * {@code addresses[postalCode eq \"12345\"]}, use the following Java code:
   *
   * <pre><code>
   *   Filter complexFilter =
   *       Filter.complex("addresses", Filter.eq("postalCode", "12345"));
   * </code></pre>
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   *
   * @throws BadRequestException  If the path could not be parsed.
   * @since 4.0.0
   */
  @NotNull
  public static Filter complex(@NotNull final String attributePath,
                               @Nullable final Filter valueFilter)
      throws BadRequestException
  {
    return new ComplexValueFilter(Path.fromString(attributePath), valueFilter);
  }

  /**
   * Create a new complex multi-valued attribute value filter (i.e., a
   * {@link ComplexValueFilter}). For example, to create a filter representing
   * {@code addresses[postalCode eq \"12345\"]}, use the following Java code:
   *
   * <pre><code>
   *   Filter complexFilter =
   *       Filter.complex("addresses", Filter.eq("postalCode", "12345"));
   * </code></pre>
   *
   * @param attributePath The path to the attribute to filter by.
   * @param valueFilter   The value filter.
   * @return A new complex multi-valued attribute value filter.
   *
   * @throws BadRequestException  If the path or filter could not be parsed.
   * @since 4.0.0
   */
  @NotNull
  public static Filter complex(@NotNull final String attributePath,
                               @Nullable final String valueFilter)
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
  @NotNull
  public static Filter fromString(@NotNull final String filterString)
      throws BadRequestException
  {
    return Parser.parseFilter(filterString);
  }
}
