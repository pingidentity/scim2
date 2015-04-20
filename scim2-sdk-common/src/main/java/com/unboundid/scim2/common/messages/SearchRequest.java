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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.SchemaInfo;
import com.unboundid.scim2.common.annotations.SchemaProperty;
import com.unboundid.scim2.common.BaseScimResource;

import java.util.List;

/**
 * Class representing a SCIM 2.0 search request.
 */
@SchemaInfo(id="urn:ietf:params:scim:api:messages:2.0:SearchRequest",
    name="Search Operation", description = "SCIM 2.0 Search Request")
public final class SearchRequest extends BaseScimResource
{
  @SchemaProperty(description = "A multi-valued list of strings indicating " +
      "the names of resource attributes to return in the response overriding " +
      "the set of attributes that would be returned by default")
  @JsonProperty
  private List<String> attributes;

  @SchemaProperty(description = "A mulit-valued list of strings indicating " +
      "the names of resource attributes to be removed from the default set " +
      "of attributes to return")
  @JsonProperty
  private List<String> excludedAttributes;

  @SchemaProperty(description = "The filter string used to request a subset " +
      "of resources")
  @JsonProperty
  private String filter;

  @SchemaProperty(description = "A string indicating the attribute whose " +
      "value shall be used to order the returned responses")
  @JsonProperty
  private String sortBy;

  @SchemaProperty(description = "A string indicating the order in which the " +
      "sortBy parameter is applied")
  @JsonProperty
  private SortOrder sortOrder;

  @SchemaProperty(description = "An integer indicating the 1-based index of " +
      "the first query result")
  @JsonProperty
  private Long startIndex;

  @SchemaProperty(description = "An integer indicating the desired maximum " +
      "number of query results per page")
  @JsonProperty
  private Long count;

  /**
   * Retrieves the list of strings indicating the names of resource attributes
   * to return in the response overriding the set of attributes that would be
   * returned by default.
   *
   * @return The list of strings indicating the names of resource attributes
   * to return.
   */
  public List<String> getAttributes()
  {
    return attributes;
  }

  /**
   * Sets the list of strings indicating the names of resource attributes
   * to return in the response overriding the set of attributes that would be
   * returned by default.
   *
   * @param attributes The list of strings indicating the names of resource
   *                   attributes to return.
   * @return This search request.
   */
  public SearchRequest setAttributes(final List<String> attributes)
  {
    this.attributes = attributes;
    return this;
  }

  /**
   * Retrieves the list of strings indicating the names of resource attributes
   * to be removed from the default set of attributes to return.
   *
   * @return The list of strings indicating the names of resource attributes to
   * be removed.
   */
  public List<String> getExcludedAttributes()
  {
    return excludedAttributes;
  }

  /**
   * Sets the list of strings indicating the names of resource attributes
   * to be removed from the default set of attributes to return.
   *
   * @param excludedAttributes The list of strings indicating the names of
   *                           resource attributes to be removed.
   * @return This search request.
   */
  public SearchRequest setExcludedAttributes(
      final List<String> excludedAttributes)
  {
    this.excludedAttributes = excludedAttributes;
    return this;
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
   * Sets the filter string used to request a subset of resources.
   *
   * @param filter The filter string used to request a subset of resources.
   * @return This search request.
   */
  public SearchRequest setFilter(final String filter)
  {
    this.filter = filter;
    return this;
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
   * Sets the string indicating the attribute whose value shall be used to
   * order the returned responses.
   *
   * @param sortBy the string indicating the attribute whose value shall be used
   *               to order the returned responses.
   *
   * @return This search request.
   */
  public SearchRequest setSortBy(final String sortBy)
  {
    this.sortBy = sortBy;
    return this;
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
   * Sets the order in which the sortBy parameter is applied.
   *
   * @param sortOrder the order in which the sortBy parameter is applied.
   *
   * @return This search request.
   */
  public SearchRequest setSortOrder(final SortOrder sortOrder)
  {
    this.sortOrder = sortOrder;
    return this;
  }

  /**
   * Retrieves the 1-based index of the first query result.
   *
   * @return the 1-based index of the first query result or {@code null} if
   * pagination is not required.
   */
  public Long getStartIndex()
  {
    return startIndex;
  }

  /**
   * Sets the 1-based index of the first query result.
   *
   * @param startIndex the 1-based index of the first query result.
   *
   * @return This search request.
   */
  public SearchRequest setStartIndex(final Long startIndex)
  {
    this.startIndex = startIndex;
    return this;
  }

  /**
   * Retrieves the desired maximum number of query results per page.
   *
   * @return the desired maximum number of query results per page or
   * {@code null} to not enforce a limit.
   */
  public Long getCount()
  {
    return count;
  }

  /**
   * Sets the desired maximum number of query results per page.
   *
   * @param count the desired maximum number of query results per page.
   *
   * @return This search request.
   */
  public SearchRequest setCount(final Long count)
  {
    this.count = count;
    return this;
  }

  /**
   * {@inheritDoc}
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
   * {@inheritDoc}
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
