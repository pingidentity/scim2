/*
 * Copyright 2015-2024 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Nullable;

/**
 * This class represents a configuration object that helps a SCIM service
 * provider declare support for SCIM entity tags (ETags). This class is used to
 * help assemble this field on the {@code /ServiceProviderConfig} endpoint. For
 * more information, see {@link ServiceProviderConfigResource}.
 * <br><br>
 *
 * <h2>What is an ETag?</h2>
 * A SCIM ETag is akin to an HTTP ETag, which is a piece of metadata that
 * represents a specific version of a resource. ETags help HTTP clients keep
 * track of changes to resources. For example, if a resource is created, a
 * service provider can generate a unique ETag identifier, which represents the
 * initial version of the SCIM resource. If the resource is ever updated with
 * new data, a new ETag corresponding to the updated resource will be generated.
 * These ETag values can be used to determine if the resource is updated at a
 * later time. For example, if a client creates a resource, records the ETag,
 * and fetches the resource again at a later time, it can conclusively determine
 * whether the resource was updated by other clients by comparing the current
 * ETag value with the one that was previously recorded. If the ETag remains
 * unchanged, then the resource was not updated.
 * <br><br>
 * If a SCIM service provider supports ETag versioning, an ETag corresponding to
 * a resource will be specified in the {@code meta.version} attribute.
 * <pre>
 *   "meta": {
 *     "resourceType": "User",
 *     "created": "2022-10-21T18:58:07.878Z",
 *     "lastModified": "2023-06-06T19:17:49.884Z",
 *     "location": "https://example.com/v2/Users/2819c223-7f76-453a-919d-413861904646",
 *     "version":"W\/\"e180ee84f0671b1\""
 *   }
 * </pre>
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class ETagConfig
{
  @Attribute(description = "Boolean value specifying whether the " +
      "operation is supported.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean supported;

  /**
   * Create a new complex type that specifies Etag configuration options.
   *
   * @param supported Boolean value specifying whether the operation is
   *                  supported.
   */
  @JsonCreator
  public ETagConfig(@JsonProperty(value = "supported", required = true)
                    final boolean supported)
  {
    this.supported = supported;
  }

  /**
   * Retrieves the boolean value specifying whether the operation is
   * supported.
   *
   * @return {@code true} if the operation is supported or {@code false}
   * otherwise.
   */
  public boolean isSupported()
  {
    return supported;
  }

  /**
   * Indicates whether the provided object is equal to this ETag configuration.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this ETag
   *            configuration, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    ETagConfig that = (ETagConfig) o;

    if (supported != that.supported)
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this ETag configuration.
   *
   * @return  A hash code for this ETag configuration.
   */
  @Override
  public int hashCode()
  {
    return (supported ? 1 : 0);
  }
}
