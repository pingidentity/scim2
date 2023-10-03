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

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.exceptions.NotModifiedException;
import com.unboundid.scim2.common.exceptions.ScimException;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

@Path("/etag/test")
public class ETagTestEndpoint
{
  /**
   * Contains information about the etags that were present for the last
   * REST call.  Useful for delete since delete doesn't expect a return
   * value.
   */
  public static GenericScimResource lastResult;

  /**
   * Test endpoint method.
   * @param headers http headers.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @GET
  public GenericScimResource etagGetTest(@Context HttpHeaders headers)
      throws ScimException
  {
    return getEtagDocument(headers);
  }


  /**
   * Test endpoint method.
   * @param headers http headers.
   * @param id id parameter.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @GET
  @Path("{id}")
  public GenericScimResource etagGetTest(@Context HttpHeaders headers,
      @PathParam("id") String id) throws ScimException
  {
    return getEtagDocument(headers);
  }


  /**
   * Test endpoint method.
   * @return nothing.  Always throws exception.
   * @throws ScimException NotModifiedException is thrown.
   */
  @GET
  @Path("exception/{noneMatch}")
  public GenericScimResource etagGetNoneMatchExceptionTest() throws ScimException
  {
    throw new NotModifiedException("Test Case None Match Exception");
  }


  /**
   * Test endpoint method.
   * @param headers http headers.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @POST
  public GenericScimResource etagPostTest(@Context HttpHeaders headers)
      throws ScimException
  {
    return getEtagDocument(headers);
  }


  /**
   * Test endpoint method.
   * @param headers http headers.
   * @param id id parameter.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @POST
  @Path("{id}")
  public GenericScimResource etagPostTest(@Context HttpHeaders headers,
      @PathParam("id") String id) throws ScimException
  {
    return getEtagDocument(headers);
  }

  /**
   * Test endpoint method.
   * @param headers http headers.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @PUT
  public GenericScimResource etagPutTest(@Context HttpHeaders headers)
      throws ScimException
  {
    return getEtagDocument(headers);
  }

  /**
   * Test endpoint method.
   * @param headers http headers.
   * @param id id parameter.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @PUT
  @Path("{id}")
  public GenericScimResource etagPutTest(@Context HttpHeaders headers,
      @PathParam("id") String id) throws ScimException
  {
    return getEtagDocument(headers);
  }

  /**
   * Test endpoint method.
   * @param headers http headers.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @PATCH
  public GenericScimResource etagPatchTest(@Context HttpHeaders headers)
      throws ScimException
  {
    return getEtagDocument(headers);
  }


  /**
   * Test endpoint method.
   * @param headers http headers.
   * @param id id parameter.
   * @return etag information.
   * @throws ScimException in case of error.
   */
  @PATCH
  @Path("{id}")
  public GenericScimResource etagPatchTest(@Context HttpHeaders headers,
      @PathParam("id") String id) throws ScimException
  {
    return getEtagDocument(headers);
  }


  /**
   * Test endpoint method.
   * @param headers http headers.
   * @throws ScimException in case of error.
   */
  @DELETE
  public void etagDeleteTest(@Context HttpHeaders headers) throws ScimException
  {
    getEtagDocument(headers);
  }


  /**
   * Test endpoint method.
   * @param headers http headers.
   * @param id id parameter.
   * @throws ScimException in case of error.
   */
  @DELETE
  @Path("{id}")
  public void etagDeleteTest(@Context HttpHeaders headers,
      @PathParam("id") String id) throws ScimException
  {
    getEtagDocument(headers);
  }

  private GenericScimResource getEtagDocument(HttpHeaders headers)
      throws ScimException
  {
    GenericScimResource gsr = new GenericScimResource();
    if(headers.getRequestHeaders().containsKey(HttpHeaders.IF_NONE_MATCH))
    {
      gsr.addStringValues(HttpHeaders.IF_NONE_MATCH,
          headers.getRequestHeader(HttpHeaders.IF_NONE_MATCH));
    }

    if(headers.getRequestHeaders().containsKey(HttpHeaders.IF_MATCH))
    {
      gsr.addStringValues(HttpHeaders.IF_MATCH,
          headers.getRequestHeader(HttpHeaders.IF_MATCH));
    }

    lastResult = gsr;
    return gsr;
  }
}
