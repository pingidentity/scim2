/*
 * Copyright 2015-2025 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2015-2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * A builder for SCIM delete requests.
 */
public class DeleteRequestBuilder extends RequestBuilder<DeleteRequestBuilder>
{
  @Nullable
  private String version;

  /**
   * Create a new DeleteRequestBuilder.
   *
   * @param target The WebTarget to DELETE.
   */
  public DeleteRequestBuilder(@NotNull final WebTarget target)
  {
    super(target);
  }

  /**
   * Delete the resource only if the resource has not been modified since the
   * provided version.
   *
   * @param version The version of the resource to compare.
   * @return This builder.
   */
  @NotNull
  public DeleteRequestBuilder ifMatch(@Nullable final String version)
  {
    this.version = version;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  protected Invocation.Builder buildRequest()
  {
    Invocation.Builder request = super.buildRequest();
    if (version != null)
    {
      request.header(HttpHeaders.IF_MATCH, version);
    }
    return request;
  }

  /**
   * Invoke the SCIM delete request.
   *
   * @throws ProcessingException If a JAX-RS runtime exception occurred.
   * @throws ScimException If the SCIM service provider responded with an error.
   */
  public void invoke() throws ScimException
  {
    try (Response response = buildRequest().delete())
    {
      if (response.getStatusInfo().getFamily() != SUCCESSFUL)
      {
        throw toScimException(response);
      }
    }
  }
}
