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

package com.unboundid.scim2.server.resources;

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.exceptions.ForbiddenException;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.utils.ResourcePreparer;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.utils.SchemaAwareFilterEvaluator;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * An abstract JAX-RS resource class for servicing the Resource Types
 * endpoint.
 */
@ResourceType(
    description = "SCIM 2.0 Resource Type",
    name = "ResourceType",
    schema = ResourceTypeResource.class,
    discoverable = false)
@Path("ResourceTypes")
public class ResourceTypesEndpoint
{
  @NotNull
  private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION =
      ResourceTypeDefinition.fromJaxRsResource(
          ResourceTypesEndpoint.class);

  @NotNull
  @Context
  private Application application;

  /**
   * Service SCIM request to retrieve all resource types defined at the
   * service provider using GET.
   *
   * @param filterString The filter string used to request a subset of
   *                     resources. Will throw 403 Forbidden if specified.
   * @param uriInfo UriInfo of the request.
   * @return All resource types in a ListResponse container.
   * @throws ScimException If an error occurs.
   */
  @GET
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @NotNull
  public ListResponse<GenericScimResource> search(
      @Nullable @QueryParam(QUERY_PARAMETER_FILTER) final String filterString,
      @NotNull @Context final UriInfo uriInfo)
          throws ScimException
  {
    if (filterString != null)
    {
      throw new ForbiddenException("Filtering not allowed");
    }

    // RFC 7644 Section 4 says query parameters should be ignored for discovery
    // endpoints, so we can't use SimpleSearchResults.
    ResourcePreparer<GenericScimResource> preparer =
        new ResourcePreparer<>(RESOURCE_TYPE_DEFINITION, uriInfo);
    Collection<ResourceTypeResource> resourceTypes = getResourceTypes();
    Collection<GenericScimResource> preparedResources =
        new ArrayList<>(resourceTypes.size());
    for (ResourceTypeResource resourceType : resourceTypes)
    {
      GenericScimResource preparedResource =
          resourceType.asGenericScimResource();
      preparer.setResourceTypeAndLocation(preparedResource);
      preparedResources.add(preparedResource);
    }
    return new ListResponse<>(preparedResources);
  }

  /**
   * Service SCIM request to retrieve a resource type by ID.
   *
   * @param id The ID of the resource type to retrieve.
   * @param uriInfo UriInfo of the request.
   * @return The retrieved resource type.
   * @throws ScimException If an error occurs.
   */
  @Path("{id}")
  @GET
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @NotNull
  public ScimResource get(@NotNull @PathParam("id") final String id,
                          @NotNull @Context final UriInfo uriInfo)
      throws ScimException
  {
    Filter filter = Filter.or(Filter.eq("id", id), Filter.eq("name", id));
    SchemaAwareFilterEvaluator filterEvaluator =
        new SchemaAwareFilterEvaluator(RESOURCE_TYPE_DEFINITION);
    ResourcePreparer<GenericScimResource> resourcePreparer =
        new ResourcePreparer<>(RESOURCE_TYPE_DEFINITION, uriInfo);
    for (ResourceTypeResource resourceType : getResourceTypes())
    {
      GenericScimResource resource = resourceType.asGenericScimResource();
      if (filter.visit(filterEvaluator, resource.getObjectNode()))
      {
        resourcePreparer.setResourceTypeAndLocation(resource);
        return resource;
      }
    }

    throw new ResourceNotFoundException(
        "No resource type defined with ID or name " + id);
  }

  /**
   * Retrieve all resource types defined at the service provider. The default
   * implementation will generate ResourceType definitions from all JAX-RS
   * resource classes with the ResourceType annotation.
   *
   * @return All resource types defined at the service provider.
   * @throws ScimException If an error occurs.
   */
  @NotNull
  public Collection<ResourceTypeResource> getResourceTypes()
      throws ScimException
  {
    Set<ResourceTypeResource> resourceTypes = new HashSet<>();
    for (Class<?> resourceClass : application.getClasses())
    {
      ResourceTypeDefinition resourceTypeDefinition =
          ResourceTypeDefinition.fromJaxRsResource(resourceClass);
      if (resourceTypeDefinition != null &&
          resourceTypeDefinition.isDiscoverable())
      {
        resourceTypes.add(resourceTypeDefinition.toScimResource());
      }
    }

    for (Object resourceInstance : application.getSingletons())
    {
      ResourceTypeDefinition resourceTypeDefinition =
          ResourceTypeDefinition.fromJaxRsResource(resourceInstance.getClass());
      if (resourceTypeDefinition != null &&
          resourceTypeDefinition.isDiscoverable())
      {
        resourceTypes.add(resourceTypeDefinition.toScimResource());
      }
    }

    return resourceTypes;
  }
}
