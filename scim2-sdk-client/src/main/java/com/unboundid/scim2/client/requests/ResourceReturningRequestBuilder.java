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

package com.unboundid.scim2.client.requests;

import com.unboundid.scim2.common.utils.ApiConstants;
import com.unboundid.scim2.common.utils.StaticUtils;

import jakarta.ws.rs.client.WebTarget;
import java.util.Set;

/**
 * Abstract SCIM request builder for resource returning requests.
 */
public abstract class ResourceReturningRequestBuilder
    <T extends ResourceReturningRequestBuilder> extends RequestBuilder<T>
{
  /**
   * Whether the attribute list is for excluded attributes.
   */
  protected boolean excluded;

  /**
   * The attribute list of include or exclude.
   */
  protected Set<String> attributes;

  /**
   * Create a new SCIM request builder.
   *
   * @param target The WebTarget to send the request.
   */
  ResourceReturningRequestBuilder(final WebTarget target)
  {
    super(target);
  }

  /**
   * Build the WebTarget for the request.
   *
   * @return The WebTarget for the request.
   */
  WebTarget buildTarget()
  {
    if(attributes != null && attributes.size() > 0)
    {
      if(!excluded)
      {
        return super.buildTarget().queryParam(
            ApiConstants.QUERY_PARAMETER_ATTRIBUTES,
            StaticUtils.collectionToString(attributes, ","));
      }
      else
      {
        return super.buildTarget().queryParam(
            ApiConstants.QUERY_PARAMETER_EXCLUDED_ATTRIBUTES,
            StaticUtils.collectionToString(attributes, ","));
      }
    }
    return super.buildTarget();
  }

  /**
   * Specifies a multi-valued list of strings indicating the names of
   * resource attributes to return in the response overriding the set
   * of attributes that would be returned by default. Any existing excluded
   * attributes will be removed.
   *
   * @param attributes the names of resource attributes to return
   * @return This builder.
   */
  @SuppressWarnings("unchecked")
  public T attributes(final String... attributes)
  {
    this.attributes = StaticUtils.arrayToSet(attributes);
    return (T) this;
  }

  /**
   * Specifies a multi-valued list of strings indicating the names of resource
   * attributes to be removed from the default set of attributes to return. Any
   * existing attributes to return will be removed.
   *
   * @param excludedAttributes names of resource attributes to be removed from
   *                           the default set of attributes to return.
   * @return This builder.
   */
  @SuppressWarnings("unchecked")
  public T excludedAttributes(final String... excludedAttributes)
  {
    this.attributes = StaticUtils.arrayToSet(excludedAttributes);
    this.excluded = true;
    return (T) this;
  }

}
