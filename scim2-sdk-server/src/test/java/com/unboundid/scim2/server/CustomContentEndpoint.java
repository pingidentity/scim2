/*
 * Copyright 2015-2016 UnboundID Corp.
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

import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.server.annotations.ResourceType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A per resource life cycle Resource Endpoint implementation.
 */
@ResourceType(
    description = "Custom User Account",
    name = "Custom User",
    schema = UserResource.class)
@Path("/CustomContent")
public class CustomContentEndpoint
{
  /**
   * Test case endpoint method for testing custom media types.
   *
   * @param userResource user resource.
   *
   * @return user resource with a location set
   * @throws URISyntaxException error condition
   */
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public UserResource postResource(UserResource userResource) throws URISyntaxException
  {
    userResource.setMeta(new Meta());
    userResource.getMeta().setLocation(new URI("CustomContent"));
    return userResource;
  }


  /**
   * Test case endpoint method for testing custom media types.
   *
   * @param userResource user resource.
   *
   * @return user resource with a location set
   * @throws URISyntaxException error condition
   */
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @PUT
  public UserResource putResource(UserResource userResource)
  {
    return userResource;
  }

  /**
   * Test case endpoint method for testing custom media types.
   *
   * @param patchRequest user resource.
   * @throws URISyntaxException error condition
   */
  @Consumes(MediaType.APPLICATION_JSON)
  @PATCH
  public void patchResource(PatchRequest patchRequest)
  {
    return;
  }


  /**
   * Test case endpoint method for testing custom media types.
   *
   * @param id string id, not used.
   * @return a byte array
   * @throws URISyntaxException error condition
   */
  @Path("{id}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @GET
  public byte[] getTextResource(@PathParam("id") String id)
      throws URISyntaxException
  {
    return new String("TextPlainReturn:" + id).getBytes();
  }

}
