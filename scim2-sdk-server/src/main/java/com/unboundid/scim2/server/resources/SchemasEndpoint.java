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

import com.unboundid.scim2.common.SchemaResource;
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

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Set;

import static com.unboundid.scim2.server.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.server.ApiConstants.QUERY_PARAMETER_FILTER;

/**
 * An abstract JAX-RS resource class for servicing the Schemas
 * endpoint.
 */
@ResourceType(
    description = "SCIM 2.0 Schema",
    name = "Schema",
    schema = SchemaResource.class)
@Path("Schemas")
public class SchemasEndpoint extends AbstractEndpoint
{
  @Context
  private Application application;

  /**
   * Service SCIM request to retrieve all schemas defined at the
   * service provider using GET.
   *
   * @param filterString The filter string used to request a subset of
   *                     resources. Will throw 403 Forbidden if specified.
   * @return All schemas in a ListResponse container.
   * @throws ScimException If an error occurs.
   */
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public ListResponse<SchemaResource> search(
      @QueryParam(QUERY_PARAMETER_FILTER) final String filterString)
      throws ScimException
  {
    if(filterString != null)
    {
      throw new ForbiddenException("Filtering not allowed");
    }

    Set<SchemaResource> schemas = getSchemas();
    for(SchemaResource schema : schemas)
    {
      setResourceTypeAndLocation(schema);
    }
    return new ListResponse<SchemaResource>(schemas);
  }

  /**
   * Service SCIM request to retrieve a schema by ID.
   *
   * @param id The ID of the schema to retrieve.
   * @return The retrieved schema.
   * @throws ScimException If an error occurs.
   */
  @Path("{id}")
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public SchemaResource get(@PathParam("id") final String id)
      throws ScimException
  {
    for(SchemaResource schema : getSchemas())
    {
      String idOrName = schema.getId() == null ?
          schema.getName() : schema.getId();
      if (idOrName.equalsIgnoreCase(id))
      {
        setResourceTypeAndLocation(schema);
        return schema;
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
  public Set<SchemaResource> getSchemas() throws ScimException
  {
    Set<SchemaResource> schemas =
        new HashSet<SchemaResource>();
    for(Class<?> resourceClass : application.getClasses())
    {
      if(!ResourceTypesEndpoint.class.isAssignableFrom(resourceClass) &&
          !SchemasEndpoint.class.isAssignableFrom(resourceClass) &&
          !AbstractServiceProviderConfigEndpoint.class.isAssignableFrom(
              resourceClass))
      {
        getSchemas(resourceClass, schemas);
      }
    }
    for(Object resourceInstance : application.getSingletons())
    {
      if(!(resourceInstance instanceof ResourceTypesEndpoint) &&
          !(resourceInstance instanceof SchemasEndpoint) &&
          !(resourceInstance instanceof AbstractServiceProviderConfigEndpoint))
      {
        getSchemas(resourceInstance.getClass(), schemas);
      }
    }

    return schemas;
  }

  /**
   * Collect schemas from JAX-RS resource classes.
   *
   * @param cls The JAX-RS resource class.
   * @param schemas The set of schemas to add to.
   */
  private void getSchemas(final Class<?> cls,
                          final Set<SchemaResource> schemas)
  {
    ResourceType resourceType = cls.getAnnotation(ResourceType.class);
    Path path = cls.getAnnotation(Path.class);

    if (resourceType != null && path != null)
    {
      try
      {
        schemas.add(SchemaUtils.getSchema(resourceType.schema()));

        if (resourceType.optionalSchemaExtensions().length > 0 ||
            resourceType.requiredSchemaExtensions().length > 0)
        {
          for (Class<?> optionalSchemaExtension :
              resourceType.optionalSchemaExtensions())
          {
            schemas.add(SchemaUtils.getSchema(optionalSchemaExtension));
          }

          for (Class<?> requiredSchemaExtension :
              resourceType.requiredSchemaExtensions())
          {
            schemas.add(SchemaUtils.getSchema(requiredSchemaExtension));
          }
        }
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
      }
    }
  }
}
