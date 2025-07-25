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

package com.unboundid.scim2.client.requests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.client.SearchResultHandler;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.common.utils.ApiConstants;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.StaticUtils;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_FILTER;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_PAGE_SIZE;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_PAGE_START_INDEX;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_SORT_BY;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_SORT_ORDER;
import static com.unboundid.scim2.common.utils.StaticUtils.toLowerCase;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * This class provides a builder for SCIM 2.0 search requests. For more
 * information, see the documentation in {@link SearchRequest}.
 */
public class SearchRequestBuilder
    extends ResourceReturningRequestBuilder<SearchRequestBuilder>
{
  @Nullable
  private String filter;

  @Nullable
  private String sortBy;

  @Nullable
  private SortOrder sortOrder;

  @Nullable
  private Integer startIndex;

  @Nullable
  private Integer count;

  /**
   * Create a new search request builder.
   *
   * @param target The WebTarget to search.
   */
  public SearchRequestBuilder(@NotNull final WebTarget target)
  {
    super(target);
  }

  /**
   * Request filtering of resources from a string representation of a
   * {@link Filter}.
   *
   * @param filter the filter string used to request a subset of resources.
   * @return This builder.
   */
  @NotNull
  public SearchRequestBuilder filter(@Nullable final String filter)
  {
    this.filter = filter;
    return this;
  }

  /**
   * Request filtering of resources from a {@link Filter} object.
   *
   * @param filter the filter object used to request a subset of resources.
   * @return This builder.
   */
  @NotNull
  public SearchRequestBuilder filter(@Nullable final Filter filter)
  {
    String stringFilter = (filter == null) ? null : filter.toString();
    return filter(stringFilter);
  }

  /**
   * Request sorting of resources.
   *
   * @param sortBy the string indicating the attribute whose value shall be used
   *               to order the returned responses.
   * @param sortOrder the order in which the sortBy parameter is applied.
   * @return This builder.
   */
  @NotNull
  public SearchRequestBuilder sort(@Nullable final String sortBy,
                                   @Nullable final SortOrder sortOrder)
  {
    this.sortBy = sortBy;
    this.sortOrder = sortOrder;
    return this;
  }

  /**
   * Request pagination of resources.
   *
   * @param startIndex the 1-based index of the first query result.
   * @param count the desired maximum number of query results per page.
   * @return This builder.
   */
  @NotNull
  public SearchRequestBuilder page(final int startIndex,
                                   final int count)
  {
    this.startIndex = startIndex;
    this.count = count;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  WebTarget buildTarget()
  {
    WebTarget target = super.buildTarget();
    if (filter != null)
    {
      target = target.queryParam(QUERY_PARAMETER_FILTER, filter);
    }
    if (sortBy != null && sortOrder != null)
    {
      target = target.queryParam(QUERY_PARAMETER_SORT_BY, sortBy);
      target = target.queryParam(QUERY_PARAMETER_SORT_ORDER,
          sortOrder.getName());
    }
    if (startIndex != null && count != null)
    {
      target = target.queryParam(QUERY_PARAMETER_PAGE_START_INDEX, startIndex);
      target = target.queryParam(QUERY_PARAMETER_PAGE_SIZE, count);
    }
    return target;
  }

  /**
   * Invoke the SCIM retrieve request using GET.
   *
   * @param <T> The type of objects to return.
   * @param cls The Java class object used to determine the type to return.
   * @return The ListResponse containing the search results.
   * @throws ScimException If an error occurred.
   */
  @NotNull
  public <T> ListResponse<T> invoke(@NotNull final Class<T> cls)
      throws ScimException
  {
    ListResponseBuilder<T> listResponseBuilder = new ListResponseBuilder<>();
    invoke(false, listResponseBuilder, cls);
    return listResponseBuilder.build();
  }

  /**
   * Invoke the SCIM retrieve request using GET.
   *
   * @param <T> The type of objects to return.
   * @param resultHandler The search result handler that should be used to
   *                      process the resources.
   * @param cls The Java class object used to determine the type to return.
   * @throws ScimException If an error occurred.
   */
  public <T> void invoke(@NotNull final SearchResultHandler<T> resultHandler,
                         @NotNull final Class<T> cls)
      throws ScimException
  {
    invoke(false, resultHandler, cls);
  }

  /**
   * Invoke the SCIM retrieve request using POST.
   *
   * @param <T> The type of objects to return.
   * @param cls The Java class object used to determine the type to return.
   * @return The ListResponse containing the search results.
   * @throws ScimException If an error occurred.
   */
  @NotNull
  public <T extends ScimResource> ListResponse<T> invokePost(
      @NotNull final Class<T> cls)
          throws ScimException
  {
    ListResponseBuilder<T> listResponseBuilder = new ListResponseBuilder<>();
    invoke(true, listResponseBuilder, cls);
    return listResponseBuilder.build();
  }

  /**
   * Invoke the SCIM retrieve request using POST.
   *
   * @param <T> The type of objects to return.
   * @param resultHandler The search result handler that should be used to
   *                      process the resources.
   * @param cls The Java class object used to determine the type to return.
   * @throws ScimException If an error occurred.
   */
  public <T> void invokePost(@NotNull final SearchResultHandler<T> resultHandler,
                             @NotNull final Class<T> cls)
      throws ScimException
  {
    invoke(true, resultHandler, cls);
  }

  /**
   * Invoke the SCIM retrieve request.
   *
   * @param post {@code true} to send the request using POST or {@code false}
   *             to send the request using GET.
   * @param <T> The type of objects to return.
   * @param resultHandler The search result handler that should be used to
   *                      process the resources.
   * @param cls The Java class object used to determine the type to return.
   * @throws ProcessingException If a JAX-RS runtime exception occurred.
   * @throws ScimException If the SCIM service provider responded with an error.
   */
  @SuppressWarnings("SpellCheckingInspection")
  private <T> void invoke(final boolean post,
                          @NotNull final SearchResultHandler<T> resultHandler,
                          @NotNull final Class<T> cls)
      throws ScimException
  {
    try (Response response = (post) ? sendPostSearch() : buildRequest().get())
    {
      if (response.getStatusInfo().getFamily() != SUCCESSFUL)
      {
        throw toScimException(response);
      }

      final JsonFactory factory = JsonUtils.getObjectReader().getFactory();
      try (InputStream inputStream = response.readEntity(InputStream.class);
           JsonParser parser = factory.createParser(inputStream))
      {
        parser.nextToken();

        boolean proceed = true;
        while (proceed && parser.nextToken() != JsonToken.END_OBJECT)
        {
          String field = String.valueOf(parser.currentName());
          parser.nextToken();

          switch (toLowerCase(field))
          {
            case "schemas":
              parser.skipChildren();
              break;
            case "totalresults":
              resultHandler.totalResults(parser.getIntValue());
              break;
            case "startindex":
              resultHandler.startIndex(parser.getIntValue());
              break;
            case "itemsperpage":
              resultHandler.itemsPerPage(parser.getIntValue());
              break;
            case "resources":
              while (parser.nextToken() != JsonToken.END_ARRAY)
              {
                if (!resultHandler.resource(parser.readValueAs(cls)))
                {
                  proceed = false;
                  break;
                }
              }
              break;

            default:
              if (SchemaUtils.isUrn(field))
              {
                resultHandler.extension(field, parser.readValueAsTree());
              }
              else
              {
                // Just skip this field
                parser.nextToken();
              }
          }
        }
      }
      catch (IOException e)
      {
        throw new ResponseProcessingException(response, e);
      }
    }
  }

  /**
   * Issues a POST search request, i.e., a {@link SearchRequest}. A common
   * example of this is the {@code /Users/.search} endpoint.
   *
   * @return  The HTTP {@link Response} to the POST request.
   */
  @NotNull
  private Response sendPostSearch()
  {
    Set<String> attributeSet = null;
    Set<String> excludedAttributeSet = null;
    if (attributes != null && !attributes.isEmpty())
    {
      if (excluded)
      {
        excludedAttributeSet = attributes;
      }
      else
      {
        attributeSet = attributes;
      }
    }

    SearchRequest searchRequest = new SearchRequest(attributeSet,
        excludedAttributeSet, filter, sortBy, sortOrder, startIndex, count);

    Invocation.Builder builder = target().
        path(ApiConstants.SEARCH_WITH_POST_PATH_EXTENSION).
        request(ScimService.MEDIA_TYPE_SCIM_TYPE, APPLICATION_JSON_TYPE);
    for (Map.Entry<String, List<Object>> header : headers.entrySet())
    {
      String stringValue = StaticUtils.listToString(header.getValue(), ", ");
      builder = builder.header(header.getKey(), stringValue);
    }

    var entity = Entity.entity(generify(searchRequest), getContentType());
    return builder.post(entity);
  }
}
