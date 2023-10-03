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

package com.unboundid.scim2.server.resources;

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.utils.ResourcePreparer;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

/**
 * An abstract JAX-RS resource class for servicing the Service Provider Config
 * endpoint.
 */
@ResourceType(
    description = "SCIM 2.0 Service Provider Config",
    name = "ServiceProviderConfig",
    schema = ServiceProviderConfigResource.class,
    discoverable = false)
@Path("ServiceProviderConfig")
public abstract class AbstractServiceProviderConfigEndpoint
{
  private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION =
      ResourceTypeDefinition.fromJaxRsResource(
          AbstractServiceProviderConfigEndpoint.class);

  /**
   * Service request to retrieve the Service Provider Config.
   *
   * @param uriInfo UriInfo of the request.
   * @return The Service Provider Config.
   * @throws ScimException if an error occurs.
   */
  @GET
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public GenericScimResource get(@Context final UriInfo uriInfo)
      throws ScimException
  {
    ServiceProviderConfigResource serviceProviderConfig =
        getServiceProviderConfig();
    ResourcePreparer<GenericScimResource> resourcePreparer =
        new ResourcePreparer<GenericScimResource>(
            RESOURCE_TYPE_DEFINITION, uriInfo);
    GenericScimResource resource =
        serviceProviderConfig.asGenericScimResource();
    resourcePreparer.setResourceTypeAndLocation(resource);
    return resource;
  }

  /**
   * Retrieve the current service provider config.
   *
   * @return The current service provider config.
   * @throws ScimException if an error occurs.
   */
  public abstract ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException;
}
