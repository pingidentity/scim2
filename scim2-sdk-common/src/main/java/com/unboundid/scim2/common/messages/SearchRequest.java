/*
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.filters.Filter;

import java.util.Set;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * This class represents a SCIM 2.0 search request.
 * <br><br>
 *
 * A SCIM search involves requests to endpoints such as {@code /Users} or
 * {@code /Groups}, where multiple results may be returned. When a client sends
 * a search request, the HTTP response that they will receive from the SCIM
 * service will be a {@link ListResponse}, which will provide a list of
 * resources.
 * <br><br>
 *
 * Search requests can include the following parameters to fine-tune the result
 * set:
 * <ul>
 *   <li> {@code filter}: A SCIM {@link Filter} that requests specific resources
 *        that match a given filter criteria.
 *   <li> {@code attributes}: A set of values indicating which attributes
 *        should be included in the response. For example, including "userName"
 *        would ensure that the returned resources will only display the value
 *        of the {@code userName} attribute. Note that the {@code id} attribute
 *        is always returned.
 *   <li> {@code excludedAttributes}: A set of values indicating attributes
 *        that should not be included on the returned resources.
 *   <li> {@code sortBy}: Indicates the attribute whose value should be used to
 *         sort the resources, if the SCIM service supports sorting.
 *   <li> {@code sortOrder}: The order that the {@code sortBy} parameter is
 *        applied. This may be set to "ascending" (the default) or "descending".
 *   <li> {@code startIndex}: The page number of the ListResponse, if the SCIM
 *        service provider supports pagination.
 *   <li> {@code count}: The maximum number of resources to return.
 * </ul>
 * <br><br>
 *
 * Search requests can be issued in two ways: with GET requests or POST
 * requests. A GET search request involves the use of HTTP query parameters,
 * e.g.:
 * <pre>
 *   GET  https://example.com/v2/Users?filter=userName eq "K.Dot"
 *
 *   // Sometimes URLs must encode characters.
 *   GET  https://example.com/v2/Users?filter=userName%20eq%20%K.Dot%22
 * </pre>
 *
 * A POST search request is typically issued to an endpoint ending in
 * {@code /.search}, e.g., {@code /Users/.search}. This allows clients to pass
 * search criteria in a JSON body instead of passing them as query parameters.
 * An example request is shown below:
 * <pre>
 *    POST https://example.com/v2/Users/.search
 *
 *    {
 *      "schemas": [ "urn:ietf:params:scim:api:messages:2.0:SearchRequest" ],
 *      "attributes": [ "userName", "displayName" ],
 *      "filter": "userName eq \"K.Dot\"",
 *      "count": 2
 *    }
 * </pre>
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Schema(id="urn:ietf:params:scim:api:messages:2.0:SearchRequest",
    name="Search Operation", description = "SCIM 2.0 Search Request")
public final class SearchRequest extends BaseScimResource
{
  @Nullable
  @Attribute(description = "A multi-valued list of strings indicating " +
      "the names of resource attributes to return in the response overriding " +
      "the set of attributes that would be returned by default")
  @JsonProperty
  private final Set<String> attributes;

  @Nullable
  @Attribute(description = "A multi-valued list of strings indicating " +
      "the names of resource attributes to be removed from the default set " +
      "of attributes to return")
  @JsonProperty
  private final Set<String> excludedAttributes;

  @Nullable
  @Attribute(description = "The filter string used to request a subset " +
      "of resources")
  @JsonProperty
  private final String filter;

  @Nullable
  @Attribute(description = "A string indicating the attribute whose " +
      "value shall be used to order the returned responses")
  @JsonProperty
  private final String sortBy;

  @Nullable
  @Attribute(description = "A string indicating the order in which the " +
      "sortBy parameter is applied")
  @JsonProperty
  private final SortOrder sortOrder;

  @Nullable
  @Attribute(description = "An integer indicating the 1-based index of " +
      "the first query result")
  @JsonProperty
  private final Integer startIndex;

  @Nullable
  @Attribute(description = "An integer indicating the desired maximum " +
      "number of query results per page")
  @JsonProperty
  private final Integer count;

  /**
   * Create a new SearchRequest.
   *
   * @param attributes the list of strings indicating the names of resource
   *                   attributes to return in the response overriding the set
   *                   of attributes that would be returned by default.
   * @param excludedAttributes the list of strings indicating the names of
   *                           resource attributes to be removed from the
   *                           default set of attributes to return.
   * @param filter the filter string used to request a subset of resources.
   * @param sortBy the string indicating the attribute whose value shall be used
   *               to order the returned responses.
   * @param sortOrder the order in which the sortBy parameter is applied.
   * @param startIndex the 1-based index of the first query result.
   * @param count the desired maximum number of query results per page.
   */
  @JsonCreator
  public SearchRequest(@Nullable @JsonProperty(QUERY_PARAMETER_ATTRIBUTES)
                       final Set<String> attributes,
                       @Nullable @JsonProperty(QUERY_PARAMETER_EXCLUDED_ATTRIBUTES)
                       final Set<String> excludedAttributes,
                       @Nullable @JsonProperty(QUERY_PARAMETER_FILTER)
                       final String filter,
                       @Nullable @JsonProperty(QUERY_PARAMETER_SORT_BY)
                       final String sortBy,
                       @Nullable @JsonProperty(QUERY_PARAMETER_SORT_ORDER)
                       final SortOrder sortOrder,
                       @Nullable @JsonProperty(QUERY_PARAMETER_PAGE_START_INDEX)
                       final Integer startIndex,
                       @Nullable @JsonProperty(QUERY_PARAMETER_PAGE_SIZE)
                       final Integer count)
  {
    this.attributes = attributes;
    this.excludedAttributes = excludedAttributes;
    this.filter = filter;
    this.sortBy = sortBy;
    this.sortOrder = sortOrder;
    this.startIndex = startIndex;
    this.count = count;
  }

  /**
   * Retrieves the list of strings indicating the names of resource attributes
   * to return in the response overriding the set of attributes that would be
   * returned by default.
   *
   * @return The list of strings indicating the names of resource attributes
   * to return.
   */
  @Nullable
  public Set<String> getAttributes()
  {
    return attributes;
  }

  /**
   * Retrieves the list of strings indicating the names of resource attributes
   * to be removed from the default set of attributes to return.
   *
   * @return The list of strings indicating the names of resource attributes to
   * be removed.
   */
  @Nullable
  public Set<String> getExcludedAttributes()
  {
    return excludedAttributes;
  }

  /**
   * Retrieves the filter string used to request a subset of resources.
   *
   * @return The filter string used to request a subset of resources.
   */
  @Nullable
  public String getFilter()
  {
    return filter;
  }

  /**
   * Retrieves the string indicating the attribute whose value shall be used to
   * order the returned responses.
   *
   * @return The string indicating the attribute whose value shall be used to
   * order the returned responses or {@code null} if sorting is not required.
   */
  @Nullable
  public String getSortBy()
  {
    return sortBy;
  }

  /**
   * Retrieves the order in which the sortBy parameter is applied.
   *
   * @return the order in which the sortBy parameter is applied or {@code null}
   * if sorting is not required.
   */
  @Nullable
  public SortOrder getSortOrder()
  {
    return sortOrder;
  }

  /**
   * Retrieves the 1-based index of the first query result.
   *
   * @return the 1-based index of the first query result or {@code null} if
   * pagination is not required.
   */
  @Nullable
  public Integer getStartIndex()
  {
    return startIndex;
  }

  /**
   * Retrieves the desired maximum number of query results per page.
   *
   * @return the desired maximum number of query results per page or
   * {@code null} to not enforce a limit.
   */
  @Nullable
  public Integer getCount()
  {
    return count;
  }

  /**
   * Indicates whether the provided object is equal to this search request.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this search
   *            request, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    SearchRequest that = (SearchRequest) o;

    if (attributes != null ? !attributes.equals(that.attributes) :
        that.attributes != null)
    {
      return false;
    }
    if (count != null ? !count.equals(that.count) : that.count != null)
    {
      return false;
    }
    if (excludedAttributes != null ?
        !excludedAttributes.equals(that.excludedAttributes) :
        that.excludedAttributes != null)
    {
      return false;
    }
    if (filter != null ? !filter.equals(that.filter) : that.filter != null)
    {
      return false;
    }
    if (sortBy != null ? !sortBy.equals(that.sortBy) : that.sortBy != null)
    {
      return false;
    }
    if (sortOrder != that.sortOrder)
    {
      return false;
    }
    if (startIndex != null ? !startIndex.equals(that.startIndex) :
        that.startIndex != null)
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this search request.
   *
   * @return  A hash code for this search request.
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
    result = 31 * result + (excludedAttributes != null ?
        excludedAttributes.hashCode() : 0);
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (sortBy != null ? sortBy.hashCode() : 0);
    result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
    result = 31 * result + (startIndex != null ? startIndex.hashCode() : 0);
    result = 31 * result + (count != null ? count.hashCode() : 0);
    return result;
  }
}
