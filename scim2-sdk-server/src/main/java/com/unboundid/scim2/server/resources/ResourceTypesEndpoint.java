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
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.exceptions.ForbiddenException;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.utils.ResourcePreparer;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.utils.SimpleSearchResults;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
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
  private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION =
      ResourceTypeDefinition.fromJaxRsResource(
          ResourceTypesEndpoint.class);

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
  @Produces(MEDIA_TYPE_SCIM)
  public SimpleSearchResults<ResourceTypeResource> search(
      @QueryParam(QUERY_PARAMETER_FILTER) final String filterString,
      @Context final UriInfo uriInfo)
      throws ScimException
  {
    if(filterString != null)
    {
      throw new ForbiddenException("Filtering not allowed");
    }

    SimpleSearchResults<ResourceTypeResource> results =
        new SimpleSearchResults<ResourceTypeResource>(
            RESOURCE_TYPE_DEFINITION, uriInfo);
    for(ResourceTypeResource resourceType : getResourceTypes())
    {
      results.add(resourceType);
    }
    return results;
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
  @Produces(MEDIA_TYPE_SCIM)
  public ScimResource get(@PathParam("id") final String id,
                          @Context final UriInfo uriInfo)
      throws ScimException
  {
    for(ResourceTypeResource resourceType : getResourceTypes())
    {
      String idOrName = resourceType.getId() == null ?
          resourceType.getName() : resourceType.getId();
      if (idOrName.equalsIgnoreCase(id))
      {
        ResourcePreparer<ResourceTypeResource> resourcePreparer =
            new ResourcePreparer<ResourceTypeResource>(
                RESOURCE_TYPE_DEFINITION, uriInfo);
        return resourcePreparer.trimRetrievedResource(resourceType);
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
  public Collection<ResourceTypeResource> getResourceTypes()
      throws ScimException
  {
    Set<ResourceTypeResource> resourceTypes =
        new HashSet<ResourceTypeResource>();
    for(Class<?> resourceClass : application.getClasses())
    {
      ResourceTypeDefinition resourceTypeDefinition =
          ResourceTypeDefinition.fromJaxRsResource(resourceClass);
      if(resourceTypeDefinition != null &&
          resourceTypeDefinition.isDiscoverable())
      {
        resourceTypes.add(resourceTypeDefinition.toScimResource());
      }
    }

    for(Object resourceInstance : application.getSingletons())
    {
      ResourceTypeDefinition resourceTypeDefinition =
          ResourceTypeDefinition.fromJaxRsResource(resourceInstance.getClass());
      if(resourceTypeDefinition != null &&
          resourceTypeDefinition.isDiscoverable())
      {
        resourceTypes.add(resourceTypeDefinition.toScimResource());
      }
    }

    return resourceTypes;
  }
}
