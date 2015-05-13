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

import com.unboundid.scim2.common.ServiceProviderConfigResource;
import com.unboundid.scim2.common.exceptions.ScimException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static com.unboundid.scim2.server.ApiConstants.MEDIA_TYPE_SCIM;

/**
 * An abstract JAX-RS resource class for servicing the Service Provider Config
 * endpoint.
 */
@Path("ServiceProviderConfig")
public abstract class AbstractServiceProviderConfigEndpoint
{
  /**
   * Service request to retrieve the Service Provider Config.
   *
   * @return The Service Provider Config.
   * @throws ScimException if an error occurs.
   */
  @GET
  @Produces(MEDIA_TYPE_SCIM)
  public abstract ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException;
}
