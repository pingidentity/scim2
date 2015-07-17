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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
   * The authenticated subject alias.
   */
  private static final String ME_URI_ALIAS = "Me";

  /**
   * {@inheritDoc}
   */
  public void filter(final ContainerRequestContext requestContext)
      throws IOException
  {
    List<PathSegment> pathSegments =
        requestContext.getUriInfo().getPathSegments();
    if(pathSegments.size() >= 1)
    {
      for(String alias : getAliases())
      {
        if(pathSegments.get(0).getPath().equals(alias))
        {
          String authSubjectPath = getAuthenticatedSubjectPath(requestContext);
          if(authSubjectPath != null)
          {
            UriBuilder newRequestUri =
                requestContext.getUriInfo().getRequestUriBuilder();
            newRequestUri.replacePath(requestContext.getUriInfo().getPath().
                replaceFirst(alias, authSubjectPath));
            requestContext.setRequestUri(newRequestUri.build());
          }
          break;
        }
      }
    }
  }

  /**
   * Get the path of the resource the represents the authenticated subject.
   *
   * @param requestContext The request context.
   * @return The path relative to the base URI.
   */
  protected String getAuthenticatedSubjectPath(
      final ContainerRequestContext requestContext)
  {
    if(requestContext.getSecurityContext() == null)
    {
      return null;
    }

    if(requestContext.getSecurityContext().getUserPrincipal() == null)
    {
      return null;
    }

    return "Users/"+
        requestContext.getSecurityContext().getUserPrincipal().toString();
  }

  /**
   * Get the aliases for the authenticated subject.
   *
   * @return The aliases for the authenticated subject.
   */
  protected Collection<String> getAliases()
  {
    return Collections.singleton(ME_URI_ALIAS);
  }
}
