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

import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.server.annotations.ResourceType;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract JAX-RS resource class that services SCIM requests.
 */
public abstract class AbstractEndpoint
{
  /**
   *   The JAX-RS UriInfo instance injected by @Context.
   */
  @Context
  protected UriInfo uriInfo;

  /**
   * Sets the meta.resourceType and meta.location metadata attribute values
   * based on the ResourceType and Path annotations along with the request
   * context.
   *
   * @param scimResource The SCIM resource to set.
   * @param endpointParamValues Values for parameters in the endpoint path.
   * @param <T> The type of SCIM resource.
   */
  protected <T extends ScimResource> void setResourceTypeAndLocation(
      final T scimResource, final Object... endpointParamValues)
  {
    ResourceTypeResource resourceType = getResourceType(this.getClass());
    if(resourceType == null)
    {
      throw new IllegalArgumentException(
          "Class not annotated with @ResourceType and @Path");
    }
    setResourceTypeAndLocation(
        resourceType.getName(), resourceType.getEndpoint().getPath(),
        scimResource, endpointParamValues);
  }

  /**
   * Sets the meta.resourceType and meta.location metadata attribute values
   * based on the Path annotation along with the request context.
   *
   * @param resourceType The value to set the meta.resourceType metadata
   *                     attribute.
   * @param scimResource The SCIM resource to set.
   * @param endpointParamValues Values for parameters in the endpoint path.
   * @param <T> The type of SCIM resource.
   */
  protected <T extends ScimResource> void setResourceTypeAndLocation(
      final String resourceType, final T scimResource,
      final Object... endpointParamValues)
  {
    Path path = this.getClass().getAnnotation(Path.class);
    if(path == null)
    {
      throw new IllegalArgumentException(
          "Class not annotated with @Path");
    }
    setResourceTypeAndLocation(resourceType, path.value(),
        scimResource, endpointParamValues);
  }

  /**
   * Sets the meta.resourceType and meta.location metadata attribute values
   * based on the request context.
   *
   * @param resourceType The value to set the meta.resourceType metadata
   *                     attribute.
   * @param endpoint The value to set the meta.location metadata attribute.
   * @param scimResource The SCIM resource to set.
   * @param endpointParamValues Values for parameters in the endpoint path.
   * @param <T> The type of SCIM resource.
   */
  protected <T extends ScimResource> void setResourceTypeAndLocation(
      final String resourceType, final String endpoint, final T scimResource,
      final Object... endpointParamValues)
  {
    UriBuilder locationBuilder = uriInfo.getBaseUriBuilder().path(endpoint);
    if(scimResource.getId() != null)
    {
      locationBuilder.path(scimResource.getId());
    }

    Meta meta = scimResource.getMeta();
    if(meta == null)
    {
      meta = new Meta();
    }
    meta.setLocation(locationBuilder.build(endpointParamValues));
    meta.setResourceType(resourceType);
    scimResource.setMeta(meta);
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
    Class<?> c = cls;
    ResourceType resourceType;
    do
    {
      resourceType = c.getAnnotation(ResourceType.class);
      c = c.getSuperclass();
    }
    while(c != null && resourceType == null);

    c = cls;
    Path path;
    do
    {
      path = c.getAnnotation(Path.class);
      c = c.getSuperclass();
    }
    while(c != null && path == null);

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
