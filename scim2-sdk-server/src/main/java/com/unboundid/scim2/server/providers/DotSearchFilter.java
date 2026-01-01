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

package com.unboundid.scim2.server.providers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.StaticUtils;
import com.unboundid.scim2.server.utils.ServerUtils;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * A ContainerRequestFilter implementation to convert a search request using
 * HTTP POST combine with the {@code .search} path extension to a regular search
 * using HTTP GET.
 * <br><br>
 *
 * For more information on this type of search request, see the documentation
 * for the {@link SearchRequest} class.
 */
@Provider
@PreMatching
@Priority(Priorities.ENTITY_CODER)
public class DotSearchFilter implements ContainerRequestFilter
{
  /**
   * {@inheritDoc}
   */
  public void filter(@NotNull final ContainerRequestContext requestContext)
      throws IOException
  {
    if (requestContext.getMethod().equals(HttpMethod.POST) &&
        requestContext.getUriInfo().getPath().endsWith(
            SEARCH_WITH_POST_PATH_EXTENSION))
    {
      if (requestContext.getMediaType() == null ||
          !(requestContext.getMediaType().isCompatible(
              ServerUtils.MEDIA_TYPE_SCIM_TYPE) ||
              requestContext.getMediaType().isCompatible(
                  MediaType.APPLICATION_JSON_TYPE)))
      {
        throw new NotSupportedException();
      }

      ObjectReader reader =
          JsonUtils.getObjectReader().forType(SearchRequest.class);
      JsonParser p = reader.getFactory().createParser(
          requestContext.getEntityStream());
      if (p.nextToken() == null)
      {
        throw new BadRequestException(
            new NoContentException("Empty Entity"));
      }
      SearchRequest searchRequest = reader.readValue(p);
      UriBuilder builder = requestContext.getUriInfo().getBaseUriBuilder();
      List<PathSegment> pathSegments =
          requestContext.getUriInfo().getPathSegments();
      for (int i = 0; i < pathSegments.size() - 1; i++)
      {
        builder.path(pathSegments.get(i).getPath());
      }
      if (searchRequest.getAttributes() != null)
      {
        builder.queryParam(QUERY_PARAMETER_ATTRIBUTES,
            ServerUtils.encodeTemplateNames(
                StaticUtils.collectionToString(
                    searchRequest.getAttributes(), ",")));
      }
      if (searchRequest.getExcludedAttributes() != null)
      {
        builder.queryParam(QUERY_PARAMETER_EXCLUDED_ATTRIBUTES,
            ServerUtils.encodeTemplateNames(
                    StaticUtils.collectionToString(
                    searchRequest.getExcludedAttributes(), ",")));
      }
      if (searchRequest.getFilter() != null)
      {
        builder.queryParam(QUERY_PARAMETER_FILTER,
            ServerUtils.encodeTemplateNames(searchRequest.getFilter()));
      }
      if (searchRequest.getSortBy() != null)
      {
        builder.queryParam(QUERY_PARAMETER_SORT_BY,
            ServerUtils.encodeTemplateNames(searchRequest.getSortBy()));
      }
      if (searchRequest.getSortOrder() != null)
      {
        builder.queryParam(QUERY_PARAMETER_SORT_ORDER,
            ServerUtils.encodeTemplateNames(
                searchRequest.getSortOrder().getName()));
      }
      if (searchRequest.getStartIndex() != null)
      {
        builder.queryParam(QUERY_PARAMETER_PAGE_START_INDEX,
            searchRequest.getStartIndex());
      }
      if (searchRequest.getCount() != null)
      {
        builder.queryParam(QUERY_PARAMETER_PAGE_SIZE,
            searchRequest.getCount());
      }
      requestContext.setRequestUri(builder.build());
      requestContext.setMethod(HttpMethod.GET);
    }
  }
}
