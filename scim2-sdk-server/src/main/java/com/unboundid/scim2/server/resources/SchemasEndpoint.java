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

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.exceptions.ForbiddenException;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.utils.ResourcePreparer;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.utils.SchemaAwareFilterEvaluator;
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
 * An abstract JAX-RS resource class for servicing the Schemas
 * endpoint.
 */
@ResourceType(
    description = "SCIM 2.0 Schema",
    name = "Schema",
    schema = SchemaResource.class,
    discoverable = false)
@Path("Schemas")
public class SchemasEndpoint
{
  private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION =
      ResourceTypeDefinition.fromJaxRsResource(
          SchemasEndpoint.class);

  @Context
  private Application application;

  /**
   * Service SCIM request to retrieve all schemas defined at the
   * service provider using GET.
   *
   * @param filterString The filter string used to request a subset of
   *                     resources. Will throw 403 Forbidden if specified.
   * @param uriInfo UriInfo of the request.
   * @return All schemas in a ListResponse container.
   * @throws ScimException If an error occurs.
   */
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public SimpleSearchResults<SchemaResource> search(
      @QueryParam(QUERY_PARAMETER_FILTER) final String filterString,
      @Context final UriInfo uriInfo)
      throws ScimException
  {
    if(filterString != null)
    {
      throw new ForbiddenException("Filtering not allowed");
    }

    SimpleSearchResults<SchemaResource> results =
        new SimpleSearchResults<SchemaResource>(
            RESOURCE_TYPE_DEFINITION, uriInfo);
    for(SchemaResource schema : getSchemas())
    {
      results.add(schema);
    }
    return results;
  }

  /**
   * Service SCIM request to retrieve a schema by ID.
   *
   * @param id The ID of the schema to retrieve.
   * @param uriInfo UriInfo of the request.
   * @return The retrieved schema.
   * @throws ScimException If an error occurs.
   */
  @Path("{id}")
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public ScimResource get(@PathParam("id") final String id,
                          @Context final UriInfo uriInfo)
      throws ScimException
  {
    Filter filter = Filter.or(Filter.eq("id", id), Filter.eq("name", id));
    SchemaAwareFilterEvaluator filterEvaluator =
        new SchemaAwareFilterEvaluator(RESOURCE_TYPE_DEFINITION);
    for (SchemaResource schema : getSchemas())
    {
      GenericScimResource resource = schema.asGenericScimResource();
      if (filter.visit(filterEvaluator, resource.getObjectNode()))
      {
        ResourcePreparer<GenericScimResource> resourcePreparer =
            new ResourcePreparer<GenericScimResource>(
                RESOURCE_TYPE_DEFINITION, uriInfo);
        return resourcePreparer.trimRetrievedResource(resource);
      }
    }
      throw new ResourceNotFoundException("No schema defined with ID " + id);
  }

  /**
   * Retrieve all schemas defined at the service provider. The default
   * implementation will generate Schemas definitions based on the ResourceType
   * of all JAX-RS resource classes with the ResourceType annotation.
   *
   * @return All schemas defined at the service provider.
   * @throws ScimException If an error occurs.
   */
  public Collection<SchemaResource> getSchemas() throws ScimException
  {
    Set<SchemaResource> schemas =
        new HashSet<SchemaResource>();
    for(Class<?> resourceClass : application.getClasses())
    {
      ResourceTypeDefinition resourceTypeDefinition =
          ResourceTypeDefinition.fromJaxRsResource(resourceClass);
      if(resourceTypeDefinition != null &&
          resourceTypeDefinition.isDiscoverable())
      {
        if(resourceTypeDefinition.getCoreSchema() != null)
        {
          schemas.add(resourceTypeDefinition.getCoreSchema());
        }
        for(SchemaResource schemaExtension :
            resourceTypeDefinition.getSchemaExtensions().keySet())
        {
          schemas.add(schemaExtension);
        }
      }
    }
    for(Object resourceInstance : application.getSingletons())
    {
      ResourceTypeDefinition resourceTypeDefinition =
          ResourceTypeDefinition.fromJaxRsResource(resourceInstance.getClass());
      if(resourceTypeDefinition != null &&
          resourceTypeDefinition.isDiscoverable())
      {
        if(resourceTypeDefinition.getCoreSchema() != null)
        {
          schemas.add(resourceTypeDefinition.getCoreSchema());
        }
        for(SchemaResource schemaExtension :
            resourceTypeDefinition.getSchemaExtensions().keySet())
        {
          schemas.add(schemaExtension);
        }
      }
    }

    return schemas;
  }
}
