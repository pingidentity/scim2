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

package com.unboundid.scim2.server.providers;

import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.StaticUtils;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * A ContainerRequestFilter implementation to convert a search request using
 * HTTP POST combine with the ".search" path extension to a regular search
 * using HTTP GET.
 */
@Provider
@PreMatching
public class DotSearchFilter implements ContainerRequestFilter
{
  /**
   * The SCIM media type.
   */
  private static final MediaType MEDIA_TYPE_SCIM_TYPE =
      MediaType.valueOf(MEDIA_TYPE_SCIM);

  /**
   * {@inheritDoc}
   */
  public void filter(final ContainerRequestContext requestContext)
      throws IOException
  {
    if(requestContext.getMethod().equals(HttpMethod.POST) &&
        requestContext.getUriInfo().getPath().endsWith(
            SEARCH_WITH_POST_PATH_EXTENSION) &&
        (requestContext.getMediaType().equals(MEDIA_TYPE_SCIM_TYPE) ||
            requestContext.getMediaType().equals(
                MediaType.APPLICATION_JSON_TYPE)))
    {
      SearchRequest searchRequest =
          SchemaUtils.createSCIMCompatibleMapper().readValue(
              requestContext.getEntityStream(), SearchRequest.class);
      UriBuilder builder = requestContext.getUriInfo().getBaseUriBuilder();
      List<PathSegment> pathSegments =
          requestContext.getUriInfo().getPathSegments();
      for(int i = 0; i < pathSegments.size() - 1; i ++)
      {
        builder.path(pathSegments.get(i).getPath());
      }
      if(searchRequest.getAttributes() != null)
      {
        builder.queryParam(QUERY_PARAMETER_ATTRIBUTES,
            StaticUtils.collectionToString(searchRequest.getAttributes(), ","));
      }
      if(searchRequest.getExcludedAttributes() != null)
      {
        builder.queryParam(QUERY_PARAMETER_EXCLUDED_ATTRIBUTES,
            StaticUtils.collectionToString(
                searchRequest.getExcludedAttributes(), ","));
      }
      if(searchRequest.getFilter() != null)
      {
        builder.queryParam(QUERY_PARAMETER_FILTER,
            searchRequest.getFilter());
      }
      if(searchRequest.getSortBy() != null)
      {
        builder.queryParam(QUERY_PARAMETER_SORT_BY,
            searchRequest.getSortBy());
      }
      if(searchRequest.getSortOrder() != null)
      {
        builder.queryParam(QUERY_PARAMETER_SORT_ORDER,
            searchRequest.getSortOrder().getName());
      }
      if(searchRequest.getStartIndex() != null)
      {
        builder.queryParam(QUERY_PARAMETER_PAGE_START_INDEX,
            searchRequest.getStartIndex());
      }
      if(searchRequest.getCount() != null)
      {
        builder.queryParam(QUERY_PARAMETER_PAGE_SIZE,
            searchRequest.getCount());
      }
      requestContext.setRequestUri(builder.build());
      requestContext.setMethod(HttpMethod.GET);
    }
  }
}
