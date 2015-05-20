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

import com.unboundid.scim2.common.ServiceProviderConfigResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.server.resources.
    AbstractServiceProviderConfigEndpoint;

import java.util.Collections;

/**
 * A test Service Provider Config endpoint implementation that just serves up
 * a canned config.
 */
public class TestServiceProviderConfigEndpoint
    extends AbstractServiceProviderConfigEndpoint
{
  /**
   * A static canned config.
   */
  public static ServiceProviderConfigResource CONFIG =
          new ServiceProviderConfigResource("https://doc",
              new ServiceProviderConfigResource.Patch(true),
              new ServiceProviderConfigResource.Bulk(true, 100, 1000),
              new ServiceProviderConfigResource.Filter(true, 200),
              new ServiceProviderConfigResource.ChangePassword(true),
              new ServiceProviderConfigResource.Sort(true),
              new ServiceProviderConfigResource.ETag(true),
              Collections.singletonList(
                  new ServiceProviderConfigResource.AuthenticationScheme(
                      "Basic", "HTTP BASIC", null, null, "httpbasic", true)));

  /**
   * {@inheritDoc}
   */
  @Override
  public ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException
  {
    return CONFIG;
  }
}
