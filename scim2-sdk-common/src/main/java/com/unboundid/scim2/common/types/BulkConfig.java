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
 * A complex type that specifies Bulk configuration options.
 */
public class BulkConfig
{
  @Attribute(description = "Boolean value specifying whether the " +
      "operation is supported.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean supported;

  @Attribute(description = "An integer value specifying the maximum " +
      "number of operations.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final int maxOperations;

  @Attribute(description = "An integer value specifying the maximum " +
      "payload size in bytes.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final int maxPayloadSize;

  /**
   * Create a new complex type that specifies Bulk configuration options.
   *
   * @param supported Boolean value specifying whether the operation is
   *                  supported.
   * @param maxOperations An integer value specifying the maximum number of
   *                      operations.
   * @param maxPayloadSize An integer value specifying the maximum payload
   *                       size in bytes
   */
  @JsonCreator
  public BulkConfig(@JsonProperty(value = "supported", required = true)
                    final boolean supported,
                    @JsonProperty(value = "maxOperations", required = true)
                    final int maxOperations,
                    @JsonProperty(value = "maxPayloadSize", required = true)
                    final int maxPayloadSize)
  {
    this.supported = supported;
    this.maxOperations = maxOperations;
    this.maxPayloadSize = maxPayloadSize;
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
   * Retrieves the integer value specifying the maximum number of operations.
   *
   * @return The integer value specifying the maximum number of operations.
   */
  public int getMaxOperations()
  {
    return maxOperations;
  }

  /**
   * Retrieves the integer value specifying the maximum payload size in bytes.
   *
   * @return the integer value specifying the maximum payload size in bytes.
   */
  public int getMaxPayloadSize()
  {
    return maxPayloadSize;
  }

  /**
   * Indicates whether the provided object is equal to this bulk configuration.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this bulk
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

    BulkConfig that = (BulkConfig) o;

    if (maxOperations != that.maxOperations)
    {
      return false;
    }
    if (maxPayloadSize != that.maxPayloadSize)
    {
      return false;
    }
    if (supported != that.supported)
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this bulk configuration.
   *
   * @return  A hash code for this bulk configuration.
   */
  @Override
  public int hashCode()
  {
    int result = (supported ? 1 : 0);
    result = 31 * result + maxOperations;
    result = 31 * result + maxPayloadSize;
    return result;
  }
}
