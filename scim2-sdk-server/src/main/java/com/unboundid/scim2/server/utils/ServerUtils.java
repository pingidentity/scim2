/*
 * Copyright 2015-2023 Ping Identity Corporation
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
  public static Response.ResponseBuilder setAcceptableType(
      final Response.ResponseBuilder response,
      final List<MediaType> acceptableTypes)
  {
    MediaType responseType = null;
    for(MediaType mediaType : acceptableTypes)
    {
      if(mediaType.isCompatible(MEDIA_TYPE_SCIM_TYPE))
      {
        responseType = MEDIA_TYPE_SCIM_TYPE;
        break;
      }
      else if(mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE))
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
  public static String encodeTemplateNames(final String s)
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
