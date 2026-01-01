/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.annotations.NotNull;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

/**
 * Utility methods for server side use.
 */
public class ServerUtils
{
  /**
   * The SCIM media type.
   */
  @NotNull
  public static final MediaType MEDIA_TYPE_SCIM_TYPE =
      MediaType.valueOf(MEDIA_TYPE_SCIM);

  /**
   * Sets the appropriate response content type while taking the accept header
   * into account.
   *
   * @param response The response builder.
   * @param acceptableTypes The list of acceptable types from
   *                        Request.getAcceptableMediaTypes.
   * @return The response builder.
   */
  @NotNull
  public static Response.ResponseBuilder setAcceptableType(
      @NotNull final Response.ResponseBuilder response,
      @NotNull final List<MediaType> acceptableTypes)
  {
    MediaType responseType = null;
    for (MediaType mediaType : acceptableTypes)
    {
      if (mediaType.isCompatible(MEDIA_TYPE_SCIM_TYPE))
      {
        responseType = MEDIA_TYPE_SCIM_TYPE;
        break;
      }
      else if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE))
      {
        responseType = MediaType.APPLICATION_JSON_TYPE;
        break;
      }
    }
    response.type(responseType == null ? MEDIA_TYPE_SCIM_TYPE : responseType);

    return response;
  }

  /**
   * Encodes a string with template parameters name present, specifically the
   * characters '{' and '}' will be percent-encoded.
   *
   * @param s the string with zero or more template parameter names
   * @return the string with encoded template parameter names.
   */
  @NotNull
  public static String encodeTemplateNames(@NotNull final String s)
  {
    String s1 = s;
    int i = s1.indexOf('{');
    if (i != -1)
    {
      s1 = s1.replace("{", "%7B");
    }
    i = s1.indexOf('}');
    if (i != -1)
    {
      s1 = s1.replace("}", "%7D");
    }

    return s1;
  }
}
