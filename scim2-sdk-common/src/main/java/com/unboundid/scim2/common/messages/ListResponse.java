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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * This class represents a SCIM 2.0 list response. A list response represents a
 * list of results with some additional metadata. This resource type is used as
 * a response to search requests and "list" requests (e.g., a GET request on
 * {@code /Users}).
 * <br><br>
 *
 * A list response can be broken down into pages, where each page contains a
 * subset of the overall results. Pagination allows the SCIM service provider to
 * return reasonably-sized JSON responses and avoid expensive computations. The
 * next page of results can be retrieved by leveraging the "startIndex" field,
 * which represents the page number. Pagination is not a hard requirement of the
 * SCIM 2.0 protocol, so some SCIM services do not support it.
 * <br><br>
 *
 * List responses contain the following fields:
 * <ul>
 *   <li> {@code Resources}: A list of SCIM resource objects.
 *   <li> {@code itemsPerPage}: Indicates the number of results that are present
 *        in the {@code Resources} array.
 *   <li> {@code totalResults}: Indicates the total number of results that match
 *        the list or query operation. This value may be larger than the value
 *        of {@code itemsPerPage} if all of the matched resources are not
 *        present in the provided {@code Resources} array.
 *   <li> {@code startIndex}: The index indicating the page number, if
 *        pagination is supported by the SCIM service.
 * </ul>
 * <br><br>
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
 *   List&lt;UserResource&gt; list = getUserList();
 *   ListResponse&lt;UserResource&gt; response = new ListResponse&lt;&gt;(list);
 * </pre>
 *
 * When iterating over the elements in a list response's {@code Resources} list,
 * it is possible to iterate directly over the ListResponse object:
 * <pre>
 *   ListResponse&lt;BaseScimResource&gt; listResponse = getResponse();
 *   for (BaseScimResource resource : listResponse)
 *   {
 *     System.out.println(resource.getId());
 *   }
 * </pre>
 *
 * <h2>Deserializing A Raw JSON</h2>
 *
 * If you have a raw JSON object and need to convert it to a ListResponse,
 * deserializing the {@code Resources} array may be tricky at first glance,
 * since the response contains a different object type within it. Fortunately,
 * Jackson has support for performing such conversions with the
 * {@link com.fasterxml.jackson.core.type.TypeReference} class. For example,
 * to convert a JSON string into a {@code ListResponse<UserResource>}, the
 * following Java code may be used:
 * <pre><code>
 *   String json = getJsonString();
 *   ListResponse&lt;UserResource&gt; response = JsonUtils.getObjectReader()
 *       .forType(new TypeReference&lt;ListResponse&lt;UserResource&gt;&gt;(){})
 *       .readValue(json);
 * </code></pre>
 *
 * Some frameworks have their own similar constructs for handling generics in
 * their libraries natively. For example, Spring contains the
 * {@code ParameterizedTypeReference} class, so look for methods that accept
 * this (or similar objects) to perform these conversions efficiently.
 *
 * @param <T> The type of the returned resources.
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:ListResponse",
    name="List Response", description = "SCIM 2.0 List Response")
@JsonPropertyOrder({ "schemas", "totalResults", "itemsPerPage", "startIndex",
        "Resources" })
public class ListResponse<T> extends BaseScimResource
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

  @NotNull
  private static final Integer ZERO = 0;

  /**
   * Create a new List Response.
   *
   * @param props  Properties to construct the List Response.
   *
   * @deprecated This constructor was previously utilized by Jackson when
   * deserializing JSON strings into ListResponses, but it is no longer used for
   * this purpose.
   */
  @SuppressWarnings("unchecked")
  @Deprecated(since = "4.0.1")
  public ListResponse(@NotNull final Map<String, Object> props)
      throws IllegalArgumentException, IllegalStateException
  {
    final Map<String, Object> properties =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    properties.putAll(props);

    checkRequiredProperties(properties, "totalResults");
    this.totalResults = (Integer) properties.get("totalResults");
    this.itemsPerPage = (Integer) properties.get("itemsPerPage");
    this.startIndex   = (Integer) properties.get("startIndex");

    var initList = (List<T>) properties.get("Resources");
    this.resources = resourcesOrEmptyList(initList, itemsPerPage, totalResults);

    if (properties.containsKey("schemas"))
    {
      this.setSchemaUrns((Collection<String>) properties.get("schemas"));
    }
  }

  /**
   * Create a new list response, specifying the value of each field.
   *
   * @param totalResults  The total number of results returned. If there are
   *                      more results than on one page, this value can be
   *                      larger than the page size.
   * @param resources     A multi-valued list of SCIM resources representing the
   *                      result set.
   * @param startIndex    The 1-based index of the first result in the current
   *                      set of list results. This can be {@code null} if the
   *                      SCIM service provider does not support pagination.
   * @param itemsPerPage  The number of resources returned in the list response
   *                      page.
   *
   * @throws IllegalStateException If the {@code resources} list is {@code null}
   *                               and {@code totalResults} is non-zero.
   */
  @JsonCreator
  public ListResponse(
      @JsonProperty(value="totalResults", required=true) final int totalResults,
      @NotNull @JsonProperty(value = "Resources") final List<T> resources,
      @Nullable @JsonProperty("startIndex") final Integer startIndex,
      @Nullable @JsonProperty("itemsPerPage") final Integer itemsPerPage)
          throws IllegalStateException
  {
    this.totalResults = totalResults;
    this.startIndex   = startIndex;
    this.itemsPerPage = itemsPerPage;
    this.resources =
        resourcesOrEmptyList(resources, itemsPerPage, totalResults);
  }

  /**
   * Create a new list response. The values for other fields such as
   * {@code totalResults} will be based on the input list size, and all other
   * fields will be set to {@code null}.
   *
   * @param resources A multi-valued list of complex objects containing the
   *                  requested resources.
   */
  public ListResponse(@NotNull final Collection<T> resources)
  {
    this.totalResults = resources.size();
    this.resources = new ArrayList<>(resources);
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
    if (!Objects.equals(itemsPerPage, that.itemsPerPage))
    {
      return false;
    }
    if (!Objects.equals(startIndex, that.startIndex))
    {
      return false;
    }
    return resources.equals(that.resources);
  }

  /**
   * Retrieves a hash code for this list response.
   *
   * @return  A hash code for this list response.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), totalResults, itemsPerPage,
        startIndex, resources);
  }

  private void checkRequiredProperties(
      @NotNull final Map<String, Object> properties,
      @NotNull final String... requiredProperties)
          throws IllegalStateException
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
   * Fetches a non-null representation of the {@code Resources} list, or throws
   * an exception if the ListResponse object would be invalid.
   * <br><br>
   *
   * A JSON list response may contain a null value for {@code Resources} only
   * if there are no results to display. RFC 7644 states:
   * <pre>
   * Resources  A multi-valued list of complex objects containing the
   *            requested resources...  REQUIRED if "totalResults" is non-zero.
   * </pre>
   *
   * This method only permits {@code null} arrays when the provided list should
   * have been empty (i.e., when either integer value is 0).
   *
   * @param resources     The list that should be analyzed.
   * @param itemsPerPage  The value of {@code itemsPerPage} on the ListResponse.
   * @param totalResults  The value of {@code totalResults} on the ListResponse.
   * @return  A non-null list of resources.
   *
   * @throws IllegalStateException  If {@code Resources} is {@code null} but
   *                                neither integer is 0.
   */
  @NotNull
  private List<T> resourcesOrEmptyList(@Nullable final List<T> resources,
                                       @Nullable final Integer itemsPerPage,
                                       final int totalResults)
      throws IllegalStateException
  {
    if (resources != null)
    {
      return resources;
    }

    if (totalResults == 0 || ZERO.equals(itemsPerPage))
    {
      return Collections.emptyList();
    }

    throw new IllegalStateException(
        "Failed to create the ListResponse since it is missing the 'Resources'"
            + " property, which must be present if totalResults is non-zero.");
  }
}
