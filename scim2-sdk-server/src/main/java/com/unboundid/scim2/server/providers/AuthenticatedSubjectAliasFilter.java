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

package com.unboundid.scim2.server.providers;

import com.unboundid.scim2.common.exceptions.NotImplementedException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.ApiConstants;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * A ContainerRequestFilter implementation to resolve the /Me alias to the
 * path of the resource that represents the authenticated subject. This
 * implementation will use the user principal within the SecurityContext
 * as the resource ID and assumes the resource is part of the /Users resource
 * type.
 */
@Provider
@PreMatching
public class AuthenticatedSubjectAliasFilter implements ContainerRequestFilter
{
  /**
   * {@inheritDoc}
   */
  public void filter(final ContainerRequestContext requestContext)
      throws IOException
  {
    String requestPath = requestContext.getUriInfo().getPath();
    for(String alias : getAliases())
    {
      if(requestPath.startsWith(alias + "/") || requestPath.equals(alias))
      {
        String authSubjectPath;
        try
        {
          authSubjectPath = getAuthenticatedSubjectPath(
              requestContext.getSecurityContext());
          UriBuilder newRequestUri =
              requestContext.getUriInfo().getBaseUriBuilder();
          newRequestUri.path(authSubjectPath +
              requestPath.substring(alias.length()));
          requestContext.setRequestUri(newRequestUri.build());
        }
        catch (ScimException e)
        {
          requestContext.abortWith(Response.status(
              e.getScimError().getStatus()).entity(
              e.getScimError()).type(
              ApiConstants.MEDIA_TYPE_SCIM).build());
        }
        break;
      }
    }
  }

  /**
   * Get the path of the resource the represents the authenticated subject.
   *
   * @param securityContext The request's security context.
   * @return The path relative to the base URI.
   * @throws ScimException if an error occurs while resolving the path.
   */
  protected String getAuthenticatedSubjectPath(
      final SecurityContext securityContext)
      throws ScimException
  {
    if(securityContext == null || securityContext.getUserPrincipal() == null)
    {
      throw new NotImplementedException("/Me not supported");
    }

    return "Users/"+ securityContext.getUserPrincipal().toString();
  }

  /**
   * Get the aliases for the authenticated subject.
   *
   * @return The aliases for the authenticated subject.
   */
  protected Collection<String> getAliases()
  {
    return Collections.singleton(ApiConstants.ME_ENDPOINT);
  }
}
