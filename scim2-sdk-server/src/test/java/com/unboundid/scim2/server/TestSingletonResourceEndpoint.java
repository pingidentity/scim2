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

import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.resources.AbstractResourceEndpoint;

import javax.ws.rs.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by boli on 5/12/15.
 */
@ResourceType(
    description = "Singleton User Account",
    name = "Singleton User",
    schema = UserResource.class,
    requiredSchemaExtensions = EnterpriseUserExtension.class)
@Path("/SingletonUsers")
public class TestSingletonResourceEndpoint
    extends AbstractResourceEndpoint<UserResource>
{
  private final Map<String, UserResource> users =
      new HashMap<String, UserResource>();

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
        for(UserResource user : users.values())
        {
          os.resource(user);
        }
        os.itemsPerPage(users.size());
        os.startIndex(1);
      }
    };
  }

  @Override
  public UserResource get(final String id) throws ScimException
  {
    UserResource found = users.get(id);
    if(found == null)
    {
      throw new ResourceNotFoundException("No resource with ID " + id);
    }
    return found;
  }

  @Override
  public UserResource create(final UserResource resource) throws ScimException
  {
    resource.setId(String.valueOf(resource.hashCode()));
    users.put(resource.getId(), resource);
    return resource;
  }

  @Override
  public void delete(final String id) throws ScimException
  {
    UserResource found = users.remove(id);
    if(found == null)
    {
      throw new ResourceNotFoundException("No resource with ID " + id);
    }
  }
}
