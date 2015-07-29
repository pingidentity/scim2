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

import com.unboundid.scim2.common.types.AuthenticationScheme;
import com.unboundid.scim2.common.types.BulkConfig;
import com.unboundid.scim2.common.types.ChangePasswordConfig;
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.types.FilterConfig;
import com.unboundid.scim2.common.types.PatchConfig;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.SortConfig;
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
   * {@inheritDoc}
   */
  @Override
  public ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException
  {
    return create();
  }

  public static ServiceProviderConfigResource create()
  {
    return new ServiceProviderConfigResource("https://doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        Collections.singletonList(
            new AuthenticationScheme(
                "Basic", "HTTP BASIC", null, null, "httpbasic", true)));
  }
}
