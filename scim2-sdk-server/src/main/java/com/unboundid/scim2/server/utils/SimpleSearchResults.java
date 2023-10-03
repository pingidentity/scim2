/*
 * Copyright 2015-2023 Ping Identity Corporation
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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.server.ListResponseStreamingOutput;
import com.unboundid.scim2.server.ListResponseWriter;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * A utility ListResponseStreamingOutput that will filter, sort, and paginate
 * the search results for simple search implementations that always returns the
 * entire result set.
 */
public class SimpleSearchResults<T extends ScimResource>
    extends ListResponseStreamingOutput<T>
{
  private final List<ScimResource> resources;
  private final Filter filter;
  private final Integer startIndex;
  private final Integer count;
  private final SchemaAwareFilterEvaluator filterEvaluator;
  private final ResourceComparator<ScimResource> resourceComparator;
  private final ResourcePreparer<ScimResource> responsePreparer;

  /**
   * Create a new SimpleSearchResults for results from a search operation.
   *
   * @param resourceType The resource type definition of result resources.
   * @param uriInfo The UriInfo from the search operation.
   * @throws BadRequestException if the filter or paths in the search operation
   * is invalid.
   */
  public SimpleSearchResults(final ResourceTypeDefinition resourceType,
                             final UriInfo uriInfo) throws BadRequestException
  {
    this.filterEvaluator = new SchemaAwareFilterEvaluator(resourceType);
    this.responsePreparer =
        new ResourcePreparer<ScimResource>(resourceType, uriInfo);
    this.resources = new LinkedList<ScimResource>();

    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    String filterString = queryParams.getFirst(QUERY_PARAMETER_FILTER);
    String startIndexString = queryParams.getFirst(
        QUERY_PARAMETER_PAGE_START_INDEX);
    String countString = queryParams.getFirst(QUERY_PARAMETER_PAGE_SIZE);
    String sortByString = queryParams.getFirst(QUERY_PARAMETER_SORT_BY);
    String  sortOrderString = queryParams.getFirst(QUERY_PARAMETER_SORT_ORDER);

    if(filterString != null)
    {
      this.filter = Filter.fromString(filterString);
    }
    else
    {
      this.filter = null;
    }

    if(startIndexString != null)
    {
      int i = Integer.valueOf(startIndexString);
      // 3.4.2.4: A value less than 1 SHALL be interpreted as 1.
      startIndex = i < 1 ? 1 : i;
    }
    else
    {
      startIndex = null;
    }

    if(countString != null)
    {
      int i = Integer.valueOf(countString);
      // 3.4.2.4: A negative value SHALL be interpreted as 0.
      count = i < 0 ? 0 : i;
    }
    else
    {
      count = null;
    }

    Path sortBy;
    try
    {
      sortBy = sortByString == null ? null : Path.fromString(sortByString);
    }
    catch (BadRequestException e)
    {
      throw BadRequestException.invalidValue("'" + sortByString +
          "' is not a valid value for the sortBy parameter: " +
          e.getMessage());
    }
    SortOrder sortOrder = sortOrderString == null ?
        SortOrder.ASCENDING : SortOrder.fromName(sortOrderString);
    if(sortBy != null)
    {
      this.resourceComparator = new ResourceComparator<ScimResource>(
          sortBy, sortOrder, resourceType);
    }
    else
    {
      this.resourceComparator = null;
    }
  }

  /**
   * Add a resource to include in the search results.
   *
   * @param resource The resource to add.
   * @return this object.
   * @throws ScimException If an error occurs during filtering or setting the
   * meta attributes.
   */
  public SimpleSearchResults add(final T resource) throws ScimException
  {
    // Convert to GenericScimResource
    GenericScimResource genericResource;
    if(resource instanceof GenericScimResource)
    {
      // Make a copy
      genericResource = new GenericScimResource(
          ((GenericScimResource) resource).getObjectNode().deepCopy());
    }
    else
    {
      genericResource = resource.asGenericScimResource();
    }

    // Set meta attributes so they can be used in the following filter eval
    responsePreparer.setResourceTypeAndLocation(genericResource);

    if(filter == null || filter.visit(filterEvaluator,
        genericResource.getObjectNode()))
    {
      resources.add(genericResource);
    }

    return this;
  }

  /**
   * Add resources to include in the search results.
   *
   * @param resources The resources to add.
   * @return this object.
   * @throws ScimException If an error occurs during filtering or setting the
   * meta attributes.
   */
  public SimpleSearchResults addAll(final Collection<T> resources)
      throws ScimException
  {
    for(T resource : resources)
    {
      add(resource);
    }
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void write(final ListResponseWriter<T> os)
      throws IOException
  {
    if(resourceComparator != null)
    {
      Collections.sort(resources, resourceComparator);
    }
    List<ScimResource> resultsToReturn = resources;
    if(startIndex != null)
    {
      if(startIndex > resources.size())
      {
        resultsToReturn = Collections.emptyList();
      }
      else
      {
        resultsToReturn = resources.subList(startIndex - 1, resources.size());
      }
    }
    if(count != null && !resultsToReturn.isEmpty())
    {
      resultsToReturn = resultsToReturn.subList(
          0, Math.min(count, resultsToReturn.size()));
    }
    os.totalResults(resources.size());
    if(startIndex != null || count != null)
    {
      os.startIndex(startIndex == null ? 1 : startIndex);
      os.itemsPerPage(resultsToReturn.size());
    }
    for(ScimResource resource : resultsToReturn)
    {
      os.resource((T) responsePreparer.trimRetrievedResource(resource));
    }
  }
}
