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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
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
 * This class represents a SCIM 2.0 list response. A list response represents a
 * list of results with some additional metadata.
 * <br><br>
 *
 * A list response can be broken down into pages, where each page contains a
 * subset of the overall results. Pagination allows the SCIM service provider to
 * return reasonably-sized JSON responses and avoid expensive computations. The
 * next set/page of results can be retrieved by leveraging the "startIndex"
 * field, which represent the page number. Note that pagination is not a hard
 * requirement of the SCIM 2.0 protocol, so some service providers do not
 * support it.
 * <br><br>
 *
 * List responses contain the following fields:
 * <ul>
 *   <li> {@code Resources}: An array containing the list of results returned.
 *
 *   <li> {@code itemsPerPage}: Indicates the number of results present in the
 *        {@code Resources} array.
 *
 *   <li> {@code totalResults}: Indicates the total number of results for the
 *        query. If all of the results are present within the Resources
 *        array, then this value will be equivalent to {@code itemsPerPage}.
 *
 *   <li> {@code startIndex}: The index indicating the page number that is
 *         desired, if pagination is supported by the SCIM service.
 * </ul>
 *
 * An example list response takes the following form:
 * <pre>
 *   {
 *       "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
 *       "totalResults": 100,
 *       "itemsPerPage": 1,
 *       "Resources": [
 *           {
 *               "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *               "userName": "muhammad.ali",
 *               "title": "Champ"
 *           }
 *       ]
 *   }
 * </pre>
 *
 * To create the above list response, use the following Java code:
 * <pre>
 *   UserResource muhammad = new UserResource()
 *           .setUserName("muhammad.ali")
 *           .setTitle("Champ");
 *   ListResponse&lt;UserResource&gt; response =
 *           new ListResponse&lt;&gt;(100, List.of(muhammad), 1, null);
 * </pre>
 *
 * Any Collection may be passed directly into the alternate constructor.
 * <pre>
 *   List&lt;UserResource&gt; users = getUserList();
 *   ListResponse&lt;UserResource&gt; response = new ListResponse&lt;&gt;(users);
 * </pre>
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:ListResponse",
    name="List Response", description = "SCIM 2.0 List Response")
@JsonPropertyOrder({ "schemas", "totalResults", "itemsPerPage", "startIndex",
        "Resources" })
public final class ListResponse<T> extends BaseScimResource
    implements Iterable<T>
{
  @Attribute(description = "The total number of results returned by the " +
      "list or query operation")
  @JsonProperty(value = "totalResults", required = true)
  private final int totalResults;

  @Attribute(description = "The number of resources returned in a list " +
      "response page")
  @Nullable
  @JsonProperty("itemsPerPage")
  private final Integer itemsPerPage;

  @Attribute(description = "The 1-based index of the first result in " +
      "the current set of list results")
  @Nullable
  @JsonProperty("startIndex")
  private final Integer startIndex;

  @Attribute(description = "A multi-valued list of complex objects " +
      "containing the requested resources")
  @NotNull
  @JsonProperty(value = "Resources", required = true)
  private final List<T> resources;

  /**
   * Create a new List Response.
   * <br><br>
   *
   * This constructor is primarily utilized by Jackson when converting JSON
   * strings into a ListResponse object. To create a ListResponse in code,
   * it is suggested to use
   * {@link ListResponse#ListResponse(int, List, Integer, Integer)}.
   *
   * @param props  Properties to construct the List Response.
   */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  @SuppressWarnings("unchecked")
  public ListResponse(@NotNull final Map<String, Object> props)
      throws IllegalArgumentException
  {
    final Map<String, Object> properties =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    properties.putAll(props);

    checkRequiredProperties(properties, "totalResults");
    this.totalResults = (Integer) properties.get("totalResults");
    this.startIndex   = (Integer) properties.get("startIndex");
    this.itemsPerPage = (Integer) properties.get("itemsPerPage");

    var resourcesList = (List<T>) properties.get("Resources");
    this.resources = getResources(resourcesList, itemsPerPage, totalResults);

    if (properties.containsKey("schemas"))
    {
      this.setSchemaUrns((Collection<String>) properties.get("schemas"));
    }
  }

  /**
   * Create a new List Response.
   *
   * @param totalResults The total number of results returned.
   * @param resources A multi-valued list of complex objects containing the
   *                  requested resources
   * @param startIndex The 1-based index of the first result in the current
   *                   set of list results
   * @param itemsPerPage The number of resources returned in a list response
   *                     page.
   */
  public ListResponse(final int totalResults,
                      @NotNull final List<T> resources,
                      @Nullable final Integer startIndex,
                      @Nullable final Integer itemsPerPage)
      throws IllegalArgumentException
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
  public ListResponse(@NotNull final Collection<T> resources)
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
  @NotNull
  public List<T> getResources()
  {
    return Collections.unmodifiableList(resources);
  }

  /**
   * Retrieves the 1-based index of the first result in the current set of list
   * results.
   *
   * @return The 1-based index of the first result in the current set of list
   * results or {@code null} if pagination is not used and the full results are
   * returned.
   */
  @Nullable
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
  @Nullable
  public Integer getItemsPerPage()
  {
    return itemsPerPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public Iterator<T> iterator()
  {
    return resources.iterator();
  }

  /**
   * Indicates whether the provided object is equal to this list response.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this list
   *            response, or {@code false} if not.
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

    ListResponse<?> that = (ListResponse<?>) o;

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
   * Retrieves a hash code for this list response.
   *
   * @return  A hash code for this list response.
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

  private void checkRequiredProperties(
      @NotNull final Map<String, Object> properties,
      @NotNull final String... requiredProperties)
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

  /**
   * Computes the appropriate value for the {@code Resources} list, or throws
   * an exception if the ListResponse object would be invalid.
   * <br><br>
   *
   * In most cases, the list of resources is non-null. However, we must permit
   * null {@code Resources} lists whenever the value of {@code totalResults} or
   * {@code itemsPerPage} is 0. RFC 7644 states:
   * <pre>
   * Resources  A multi-valued list of complex objects containing the
   *            requested resources...  REQUIRED if "totalResults" is non-zero.
   * </pre>
   *
   * Though not mentioned in the RFC, this method also checks the value of
   * {@code itemsPerPage} to permit null arrays in this unlikely case as well.
   *
   * @param resources     The resource list that should be analyzed.
   * @param itemsPerPage  The value of {@code itemsPerPage} on the ListResponse.
   * @param totalResults  The value of {@code totalResults} on the ListResponse.
   * @return  A non-null list of resources.
   *
   * @throws IllegalArgumentException   If the {@code Resources} list is
   *                                    {@code null} but {@code totalResults} is
   *                                    non-zero.
   */
  @NotNull
  private List<T> getResources(final @Nullable List<T> resources,
                               final @Nullable Integer itemsPerPage,
                               final int totalResults)
      throws IllegalArgumentException
  {
    if (resources == null)
    {
      boolean itemsPerPageIsZero = (itemsPerPage != null && itemsPerPage == 0);
      if (totalResults == 0 || itemsPerPageIsZero)
      {
        return Collections.emptyList();
      }

      throw new IllegalArgumentException(
          "Failed to create the ListResponse since it is missing the"
              + " 'Resources' property, which must be present if totalResults"
              + " is non-zero.");
    }

    return resources;
  }
}
