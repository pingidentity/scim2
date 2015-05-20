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

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.unboundid.scim2.common.utils.SchemaUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * A JacksonJsonProvider specialized for SCIM.
 */
@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class ScimJacksonJsonProvider extends JacksonJsonProvider
{
  /**
   * Create a new ScimJacksonJsonProvider.
   */
  public ScimJacksonJsonProvider()
  {
    super(SchemaUtils.createSCIMCompatibleMapper(), BASIC_ANNOTATIONS);
  }
}
