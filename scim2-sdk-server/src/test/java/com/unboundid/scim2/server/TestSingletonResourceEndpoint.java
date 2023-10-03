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

package com.unboundid.scim2.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.exceptions.ServerErrorException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.server.utils.ResourcePreparer;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.utils.SimpleSearchResults;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

/**
 * Test JAX-RS SCIM resource endpoint that has a per request lifecycle.
 */
@ResourceType(
    description = "Singleton User Account",
    name = "Singleton User",
    schema = UserResource.class,
    requiredSchemaExtensions = EnterpriseUserExtension.class)
@Path("/SingletonUsers")
public class TestSingletonResourceEndpoint
{
  private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION =
      ResourceTypeDefinition.fromJaxRsResource(
          TestSingletonResourceEndpoint.class);
  private final Map<String, UserResource> users =
      new HashMap<String, UserResource>();

  /**
   * Test SCIM search.
   *
   * @param uriInfo The UriInfo.
   * @return The results.
   * @throws ScimException if an error occurs.
   */
  @GET
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public SimpleSearchResults<UserResource> search(
      @Context final UriInfo uriInfo) throws ScimException
  {
    SimpleSearchResults<UserResource> results =
        new SimpleSearchResults<UserResource>(
            RESOURCE_TYPE_DEFINITION, uriInfo);
    results.addAll(users.values());
    return results;
  }

  /**
   * Test SCIM retrieve by ID.
   *
   * @param id The ID of the resource to retrieve.
   * @param uriInfo The UriInfo.
   * @return The result.
   * @throws ScimException if an error occurs.
   */
  @Path("{id}")
  @GET
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public ScimResource retrieve(
      @PathParam("id") final String id, @Context final UriInfo uriInfo)
      throws ScimException
  {
    UserResource found = users.get(id);
    if(found == null)
    {
      throw new ResourceNotFoundException("No resource with ID " + id);
    }

    ResourcePreparer<UserResource> resourcePreparer =
        new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
    return resourcePreparer.trimRetrievedResource(found);
  }

  /**
   * Test SCIM create.
   *
   * @param resource The resource to create.
   * @param uriInfo The UriInfo.
   * @return The result.
   * @throws ScimException if an error occurs.
   */
  @POST
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public ScimResource create(
      final UserResource resource, @Context final UriInfo uriInfo)
      throws ScimException
  {
    resource.setId(String.valueOf(resource.hashCode()));
    users.put(resource.getId(), resource);

    ResourcePreparer<UserResource> resourcePreparer =
        new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
    return resourcePreparer.trimCreatedResource(resource, resource);
  }

  /**
   * Test SCIM delete.
   *
   * @param id The ID of the resource to delete.
   * @throws ScimException if an error occurs.
   */
  @Path("{id}")
  @DELETE
  public void delete(@PathParam("id") final String id) throws ScimException
  {
    UserResource found = users.remove(id);
    if(found == null)
    {
      throw new ResourceNotFoundException("No resource with ID " + id);
    }
  }

  /**
   * Test SCIM replace.
   *
   * @param id the ID of the resource to replace.
   * @param resource The resource to create.
   * @param uriInfo The UriInfo.
   * @return The result.
   * @throws ScimException if an error occurs.
   */
  @Path("{id}")
  @PUT
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public ScimResource replace(@PathParam("id") final String id,
                              final UserResource resource,
                              @Context final UriInfo uriInfo)
      throws ScimException
  {
    if(!users.containsKey(id))
    {
      throw new ResourceNotFoundException("No resource with ID " + id);
    }
    users.put(id, resource);
    ResourcePreparer<UserResource> resourcePreparer =
        new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
    return resourcePreparer.trimReplacedResource(resource, resource);
  }

  /**
   * Test SCIM modify.
   *
   * @param id The ID of the resource to modify.
   * @param patchRequest The patch request.
   * @param uriInfo The UriInfo.
   * @return The result.
   * @throws ScimException if an error occurs.
   */
  @Path("{id}")
  @PATCH
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public ScimResource modify(@PathParam("id") final String id,
                             final PatchRequest patchRequest,
                             @Context final UriInfo uriInfo)
      throws ScimException
  {
    UserResource found = users.get(id);
    if(found == null)
    {
      throw new ResourceNotFoundException("No resource with ID " + id);
    }
    ObjectNode node = JsonUtils.valueToNode(found);
    for(PatchOperation operation : patchRequest)
    {
      operation.apply(node);
    }
    UserResource patchedFound = null;
    try
    {
      patchedFound =
          JsonUtils.getObjectReader().treeToValue(node, UserResource.class);
    }
    catch (JsonProcessingException e)
    {
      throw new ServerErrorException(e.getMessage(), null, e);
    }
    users.put(id, patchedFound);
    ResourcePreparer<UserResource> resourcePreparer =
        new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
    return resourcePreparer.trimModifiedResource(patchedFound, patchRequest);
  }
}
