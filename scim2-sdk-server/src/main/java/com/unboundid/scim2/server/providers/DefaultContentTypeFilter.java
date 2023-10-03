/*
 * Copyright 2016-2023 Ping Identity Corporation
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

import jakarta.annotation.Priority;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collections;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

/**
 * A ContainerRequestFilter implementation to set the content type header of
 * POST, PUT, and PATCH requests to "application/scim+json" if the header is
 * missing.
 */
@Provider
@PreMatching
@Priority(Priorities.HEADER_DECORATOR)
public class DefaultContentTypeFilter implements ContainerRequestFilter
{
  /**
   * {@inheritDoc}
   */
  public void filter(final ContainerRequestContext requestContext)
      throws IOException
  {
    if((requestContext.getMethod().equals(HttpMethod.POST) ||
        requestContext.getMethod().equals(HttpMethod.PUT) ||
        requestContext.getMethod().equals("PATCH")) &&
        requestContext.getMediaType() == null)
    {
      requestContext.getHeaders().put(HttpHeaders.CONTENT_TYPE,
          Collections.singletonList(MEDIA_TYPE_SCIM));
    }
  }
}
