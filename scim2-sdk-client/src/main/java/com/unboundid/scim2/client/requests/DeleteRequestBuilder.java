/*
 * Copyright 2015-2016 UnboundID Corp.
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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.exceptions.ScimException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * A builder for SCIM delete requests.
 */
public class DeleteRequestBuilder extends RequestBuilder<DeleteRequestBuilder>
{
  /**
   * Create a new DeleteRequestBuilder.
   *
   * @param target The WebTarget to DELETE.
   */
  public DeleteRequestBuilder(final WebTarget target)
  {
    super(target);
  }

  /**
   * Invoke the SCIM delete request.
   *
   * @throws ScimException If an error occurred.
   */
  public void invoke() throws ScimException
  {
    Response response = buildRequest().delete();
    if(response.getStatusInfo().getFamily() !=
        Response.Status.Family.SUCCESSFUL)
    {
      throw toScimException(response);
    }
  }
}
