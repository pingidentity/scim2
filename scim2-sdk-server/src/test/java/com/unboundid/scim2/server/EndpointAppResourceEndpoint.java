/*
 * Copyright 2026 Ping Identity Corporation
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
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.server;

import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.common.types.devices.EndpointAppResource;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.utils.SimpleSearchResults;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.server.utils.ResourceTypeDefinition.fromJaxRsResource;
import static java.util.Objects.requireNonNull;

/**
 * A JAX-RS test endpoint for {@link EndpointAppResource}.
 */
@ResourceType(
    description = "Endpoint Application resource (RFC 9944)",
    name = "EndpointApp",
    schema = EndpointAppResource.class)
@Path("/EndpointApps")
public class EndpointAppResourceEndpoint
{
  private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION =
      requireNonNull(fromJaxRsResource(EndpointAppResourceEndpoint.class));

  /**
   * A SCIM search endpoint returning a single endpoint application.
   *
   * @param uriInfo The UriInfo.
   * @return The results.
   */
  @GET
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public SimpleSearchResults<EndpointAppResource> search(
      @Context final UriInfo uriInfo) throws ScimException
  {
    SimpleSearchResults<EndpointAppResource> results =
        new SimpleSearchResults<>(RESOURCE_TYPE_DEFINITION, uriInfo);
    EndpointAppResource app = new EndpointAppResource()
        .setApplicationType("deviceControl")
        .setApplicationName("Test App");
    app.setId("app-1");
    results.add(app);

    return results;
  }
}
