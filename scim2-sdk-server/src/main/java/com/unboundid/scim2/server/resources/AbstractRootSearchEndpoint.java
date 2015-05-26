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

package com.unboundid.scim2.server.resources;

import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.server.ListResponseStreamingOutput;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static com.unboundid.scim2.server.ApiConstants.*;

/**
 * An abstract JAX-RS resource class for servicing search requests at the SCIM
 * service provider root.
 */
@Path("/")
public abstract class AbstractRootSearchEndpoint
{
  /**
   * Service a SCIM list/query request using POST.
   *
   * @param searchRequest The search request.
   * @return A ListResponseStreamingOutput instance that will be called to
   * start streaming the resources to the client.
   * @throws ScimException If an error occurs.
   */
  @Path(".search")
  @POST
  @Consumes(MEDIA_TYPE_SCIM)
  @Produces(MEDIA_TYPE_SCIM)
  public abstract ListResponseStreamingOutput<ScimResource> search(
      final SearchRequest searchRequest)
      throws ScimException;

  /**
   * Service a SCIM list/query operation using GET. The default implementation
   * will use the search(SearchRequest) method to process this request.
   *
   * @param attributes A multi-valued list of strings indicating the names of
   *                   resource attributes to return in the response overriding
   *                   the set of attributes that would be returned by default.
   * @param excludedAttributes A mulit-valued list of strings indicating the
   *                           names of resource attributes to be removed from
   *                           the default set of attributes to return.
   * @param filterString The filter string used to request a subset of
   *                     resources .
   * @param sortBy A string indicating the attribute whose value shall be used
   *               to order the returned responses.
   * @param sortOrder A string indicating the order in which the sortBy
   *                  parameter is applied.
   * @param pageStartIndex An integer indicating the 1-based index of the first
   *                       query result.
   * @param pageSize An integer indicating the desired maximum number of query
   *                 results per page.
   * @return A ListResponseStreamingOutput instance that will be called to
   * start streaming the resources to the client.
   * @throws ScimException If an error occurs.
   */
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public ListResponseStreamingOutput<ScimResource>  search(
      @QueryParam(QUERY_PARAMETER_ATTRIBUTES)
      final String attributes,
      @QueryParam(QUERY_PARAMETER_EXCLUDED_ATTRIBUTES)
      final String excludedAttributes,
      @QueryParam(QUERY_PARAMETER_FILTER)
      final String filterString,
      @QueryParam(QUERY_PARAMETER_SORT_BY)
      final String sortBy,
      @QueryParam(QUERY_PARAMETER_SORT_ORDER)
      final SortOrder sortOrder,
      @QueryParam(QUERY_PARAMETER_PAGE_START_INDEX)
      final Integer pageStartIndex,
      @QueryParam(QUERY_PARAMETER_PAGE_SIZE)
      final Integer pageSize)
      throws ScimException
  {
    final SearchRequest searchRequest = new SearchRequest(
        attributes, excludedAttributes, filterString, sortBy,
        sortOrder, pageStartIndex, pageSize);
    return search(searchRequest);
  }


}
