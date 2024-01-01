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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;

import java.util.Set;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * Class representing a SCIM 2 search request.
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:SearchRequest",
    name="Search Operation", description = "SCIM 2.0 Search Request")
public final class SearchRequest extends BaseScimResource
{
  @Attribute(description = "A multi-valued list of strings indicating " +
      "the names of resource attributes to return in the response overriding " +
      "the set of attributes that would be returned by default")
  @JsonProperty
  private final Set<String> attributes;

  @Attribute(description = "A multi-valued list of strings indicating " +
      "the names of resource attributes to be removed from the default set " +
      "of attributes to return")
  @JsonProperty
  private final Set<String> excludedAttributes;

  @Attribute(description = "The filter string used to request a subset " +
      "of resources")
  @JsonProperty
  private final String filter;

  @Attribute(description = "A string indicating the attribute whose " +
      "value shall be used to order the returned responses")
  @JsonProperty
  private final String sortBy;

  @Attribute(description = "A string indicating the order in which the " +
      "sortBy parameter is applied")
  @JsonProperty
  private final SortOrder sortOrder;

  @Attribute(description = "An integer indicating the 1-based index of " +
      "the first query result")
  @JsonProperty
  private final Integer startIndex;

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
  public SearchRequest(@JsonProperty(QUERY_PARAMETER_ATTRIBUTES)
                       final Set<String> attributes,
                       @JsonProperty(QUERY_PARAMETER_EXCLUDED_ATTRIBUTES)
                       final Set<String> excludedAttributes,
                       @JsonProperty(QUERY_PARAMETER_FILTER)
                       final String filter,
                       @JsonProperty(QUERY_PARAMETER_SORT_BY)
                       final String sortBy,
                       @JsonProperty(QUERY_PARAMETER_SORT_ORDER)
                       final SortOrder sortOrder,
                       @JsonProperty(QUERY_PARAMETER_PAGE_START_INDEX)
                       final Integer startIndex,
                       @JsonProperty(QUERY_PARAMETER_PAGE_SIZE)
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
  public Set<String> getExcludedAttributes()
  {
    return excludedAttributes;
  }

  /**
   * Retrieves the filter string used to request a subset of resources.
   *
   * @return The filter string used to request a subset of resources.
   */
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
  public boolean equals(final Object o)
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
