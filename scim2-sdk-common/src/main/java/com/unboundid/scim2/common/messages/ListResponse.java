/*
 * Copyright 2015-2020 Ping Identity Corporation
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class representing a SCIM 2 list response.
 *
 * @param <T> The type of the returned resources.
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:ListResponse",
    name="List Response", description = "SCIM 2.0 List Response")
public final class ListResponse<T> extends BaseScimResource
    implements Iterable<T>
{
  @Attribute(description = "The total number of results returned by the " +
      "list or query operation")
  @JsonProperty(value = "totalResults", required = true)
  private final int totalResults;


  @Attribute(description = "A multi-valued list of complex objects " +
      "containing the requested resources")
  @JsonProperty(value = "Resources", required = true)
  private final List<T> resources;

  @Attribute(description = "The 1-based index of the first result in " +
      "the current set of list results")
  @JsonProperty("startIndex")
  private final Integer startIndex;

  @Attribute(description = "The number of resources returned in a list " +
      "response page")
  @JsonProperty("itemsPerPage")
  private final Integer itemsPerPage;

  /**
   * Create a new List Response.
   *
   * @param props  Properties to construct the List Response.
   */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  @SuppressWarnings("unchecked")
  public ListResponse(final Map<String,Object> props)
  {
    final Map<String,Object> properties =
      new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
    properties.putAll(props);

    checkRequiredProperties(properties, new String[] {"totalResults",
      "resources"});

    this.totalResults = (Integer)properties.get("totalResults");
    this.resources =  (List<T>)properties.get("resources");
    this.startIndex = properties.containsKey("startIndex") ?
      (Integer)properties.get("startIndex") : null;
    this.itemsPerPage =  properties.containsKey("itemsPerPage") ?
      (Integer)properties.get("itemsPerPage") : null;
    if (properties.containsKey("schemas"))
    {
      this.setSchemaUrns((Collection<String>)properties.get("schemas"));
    }
  }

  /**
   * Create a new List Response.
   *
   * @param totalResults The total number of results returned.
   * @param resources A multi-valued list of complex objects containing the
   *                  requested resources
   * @param startIndex The 1-based index of hte first result in the current
   *                   set of list results
   * @param itemsPerPage The number of resources returned in a list response
   *                     page.
   */
  public ListResponse(final int totalResults,
                      final List<T> resources,
                      final Integer startIndex,
                      final Integer itemsPerPage)
  {
    this.totalResults = totalResults;
    this.startIndex   = startIndex;
    this.itemsPerPage = itemsPerPage;

    final ObjectReader reader = JsonUtils.getObjectReader();
    final ObjectWriter writer = JsonUtils.getObjectWriter();
    try
    {
      final String rawResources = writer.writeValueAsString(resources);
      this.resources = reader.forType(
        new TypeReference<List<T>>(){}).readValue(rawResources);
    }
    catch (final IOException ie)
    {
      throw new IllegalArgumentException("Resources exception", ie);
    }
  }

  /**
   * Create a new List Response.
   *
   * @param resources A multi-valued list of complex objects containing the
   *                  requested resources.
   */
  public ListResponse(final Collection<T> resources)
  {
    this.totalResults = resources.size();
    this.resources = new ArrayList<T>(resources);
    this.startIndex = null;
    this.itemsPerPage = null;
  }

  /**
   * Retrieves the total number of results returned by the list or query
   * operation.
   *
   * @return The total number of results returned by the list or query
   * operation.
   */
  public int getTotalResults()
  {
    return totalResults;
  }

  /**
   * Retrieves the list of results returned by the list or query operation.
   *
   * @return The list of results returned by the list or query operation.
   */
  public List<T> getResources()
  {
    return Collections.unmodifiableList(resources);
  }

  /**
   * Retrieves the 1-based index of hte first result in the current set of list
   * results.
   *
   * @return The 1-based index of hte first result in the current set of list
   * results or {@code null} if pagination is not used and the full results are
   * returned.
   */
  public Integer getStartIndex()
  {
    return startIndex;
  }

  /**
   * Retrieves the number of resources returned in a list response page.
   *
   * @return The number of resources returned in a list response page or
   * {@code null} if pagination is not used and the full results are returned.
   */
  public Integer getItemsPerPage()
  {
    return itemsPerPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<T> iterator()
  {
    return resources.iterator();
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

    ListResponse that = (ListResponse) o;

    if (totalResults != that.totalResults)
    {
      return false;
    }
    if (itemsPerPage != null ? !itemsPerPage.equals(that.itemsPerPage) :
        that.itemsPerPage != null)
    {
      return false;
    }
    if (!resources.equals(that.resources))
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
    result = 31 * result + (int) (totalResults ^ (totalResults >>> 32));
    result = 31 * result + resources.hashCode();
    result = 31 * result + (startIndex != null ? startIndex.hashCode() : 0);
    result = 31 * result + (itemsPerPage != null ? itemsPerPage.hashCode() : 0);
    return result;
  }

  private void checkRequiredProperties(final Map<String,Object> properties,
                                       final String[] requiredProperties)
  {
    for (final String property : requiredProperties)
    {
      if (!properties.containsKey(property))
      {
        throw new IllegalStateException(String.format(
          "Missing required creator property '%s'", property));
      }
    }
  }

}
