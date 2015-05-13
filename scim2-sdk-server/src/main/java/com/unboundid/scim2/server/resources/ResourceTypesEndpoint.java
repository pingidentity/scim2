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

import com.unboundid.scim2.common.ResourceTypeResource;
import com.unboundid.scim2.common.exceptions.ForbiddenException;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.server.annotations.ResourceType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.unboundid.scim2.server.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.server.ApiConstants.QUERY_PARAMETER_FILTER;

/**
 * An abstract JAX-RS resource class for servicing the Resource Types
 * endpoint.
 */
@Path("ResourceTypes")
public class ResourceTypesEndpoint
{
  @Context
  private Application application;

  /**
   * Service SCIM request to retrieve all resource types defined at the
   * service provider using GET.
   *
   * @param filterString The filter string used to request a subset of
   *                     resources. Will throw 403 Forbidden if specified.
   * @return All resource types in a ListResponse container.
   * @throws ScimException If an error occurs.
   */
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public ListResponse<ResourceTypeResource> getResourceTypes(
      @QueryParam(QUERY_PARAMETER_FILTER) final String filterString)
      throws ScimException
  {
    if(filterString != null)
    {
      throw new ForbiddenException("Filtering not allowed");
    }

    return new ListResponse<ResourceTypeResource>(getResourceTypes());
  }

  /**
   * Service SCIM request to retrieve a resource type by ID.
   *
   * @param id The ID of the resource type to retrieve.
   * @return The retrieved resource type.
   * @throws ScimException If an error occurs.
   */
  @Path("{id}")
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public ResourceTypeResource getResourceType(@PathParam("id") final String id)
      throws ScimException
  {
    for(ResourceTypeResource resourceType : getResourceTypes())
    {
      String idOrName = resourceType.getId() == null ?
          resourceType.getName() : resourceType.getId();
      if (idOrName.equalsIgnoreCase(id))
      {
        return resourceType;
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
  public Set<ResourceTypeResource> getResourceTypes() throws ScimException
  {
    Set<ResourceTypeResource> resourceTypes =
        new HashSet<ResourceTypeResource>();
    for(Class<?> resourceClass : application.getClasses())
    {
      ResourceTypeResource resourceType = getResourceType(resourceClass);
      if(resourceType != null)
      {
        resourceTypes.add(resourceType);
      }
    }

    for(Object resourceInstance : application.getSingletons())
    {
      ResourceTypeResource resourceType =
          getResourceType(resourceInstance.getClass());
      if(resourceType != null)
      {
        resourceTypes.add(resourceType);
      }
    }

    return resourceTypes;
  }

  /**
   * Generate a ResourceType definition from a JAX-RS resource class annotated
   * with the ResourceType annotation.
   *
   * @param cls The JAX-RS resource class.
   * @return The generated ResourceType definition.
   */
  public static ResourceTypeResource getResourceType(final Class<?> cls)
  {
    ResourceType resourceType = cls.getAnnotation(ResourceType.class);
    Path path = cls.getAnnotation(Path.class);

    if(resourceType != null && path != null)
    {
      try
      {
        String schema = SchemaUtils.getSchemaIdFromAnnotation(
            resourceType.schema());
        List<ResourceTypeResource.SchemaExtension> schemaExtensions = null;
        if (resourceType.optionalSchemaExtensions().length > 0 ||
            resourceType.requiredSchemaExtensions().length > 0)
        {
          schemaExtensions =
              new ArrayList<ResourceTypeResource.SchemaExtension>(
                  resourceType.optionalSchemaExtensions().length +
                      resourceType.requiredSchemaExtensions().length);

          for (Class<?> optionalSchemaExtension :
              resourceType.optionalSchemaExtensions())
          {
            String schemaId =
                SchemaUtils.getSchemaIdFromAnnotation(optionalSchemaExtension);
            schemaExtensions.add(new ResourceTypeResource.SchemaExtension(
                new URI(schemaId), false));
          }

          for (Class<?> requiredSchemaExtension :
              resourceType.requiredSchemaExtensions())
          {
            String schemaId =
                SchemaUtils.getSchemaIdFromAnnotation(requiredSchemaExtension);
            schemaExtensions.add(new ResourceTypeResource.SchemaExtension(
                new URI(schemaId), true));
          }
        }

        return new ResourceTypeResource(resourceType.name(),
            resourceType.name(), resourceType.description(),
            new URI(path.value()), new URI(schema), schemaExtensions);
      }
      catch(URISyntaxException e)
      {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
}
