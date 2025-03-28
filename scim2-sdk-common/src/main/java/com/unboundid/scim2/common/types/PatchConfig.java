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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Nullable;

/**
 * A complex type that specifies PATCH configuration options.
 */
public class PatchConfig
{
  @Attribute(description = "Boolean value specifying whether the " +
      "operation is supported.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean supported;

  /**
   * Create a new complex type that specifies PATCH configuration options.
   *
   * @param supported Boolean value specifying whether the operation is
   *                  supported.
   */
  @JsonCreator
  public PatchConfig(@JsonProperty(value = "supported", required = true)
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
   * Indicates whether the provided object is equal to this PATCH configuration.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this PATCH
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

    PatchConfig that = (PatchConfig) o;
    return supported == that.supported;
  }

  /**
   * Retrieves a hash code for this PATCH configuration.
   *
   * @return  A hash code for this PATCH configuration.
   */
  @Override
  public int hashCode()
  {
    return (supported ? 1 : 0);
  }
}
