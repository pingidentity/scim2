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

package com.unboundid.scim2.server;

import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.server.resources.AbstractResourceEndpoint;

import javax.ws.rs.Path;
import java.io.IOException;

/**
 * A per resource life cycle Resource Endpoint implementation.
 */
@ResourceType(
    description = "User Account",
    name = "User",
    schema = UserResource.class)
@Path("/Users")
public class TestResourceEndpoint extends AbstractResourceEndpoint<UserResource>
{
  private static UserResource USER;
  static
  {
    USER = new UserResource().setUserName("test");
    USER.setId("123");
  }

  @Override
  public ListResponseStreamingOutput<UserResource> search(
      final SearchRequest searchRequest) throws ScimException
  {
    return new ListResponseStreamingOutput<UserResource>()
    {
      @Override
      public void write(final ListResponseWriter<UserResource> os)
          throws IOException
      {
        os.resource(USER);
      }
    };
  }

  @Override
  public UserResource get(final String id) throws ScimException
  {
    if(id.equals(USER.getId()))
    {
      return USER;
    }
    return null;
  }
}
