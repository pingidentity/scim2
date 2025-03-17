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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

/**
 * A complex type that optionally allows Service Providers to represent
 * organizational hierarchy by referencing the 'id' attribute of another User.
 */
public class Manager
{
  @Nullable
  @Attribute(description = "The id of the SCIM resource representing " +
      "the User's manager.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

  @Nullable
  @Attribute(description = "The URI of the SCIM resource representing " +
      "the User's manager.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      referenceTypes = {"User"} )
  @JsonProperty("$ref")
  private URI ref;

  @Nullable
  @Attribute(description = "The displayName of the User's manager.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String displayName;

  /**
   * Retrieves the id of the SCIM resource representing the User's manager.
   *
   * @return The id of the SCIM resource representing the User's manager.
   */
  @Nullable
  public String getValue()
  {
    return value;
  }

  /**
   * Specifies the id of the SCIM resource representing the User's manager.
   *
   * @param value The id of the SCIM resource representing the User's manager.
   * @return This object.
   */
  @NotNull
  public Manager setValue(@Nullable final String value)
  {
    this.value = value;
    return this;
  }

  /**
   * Retrieves the URI of the SCIM resource representing the User's manager.
   *
   * @return The URI of the SCIM resource representing the User's manager.
   */
  @Nullable
  public URI getRef()
  {
    return ref;
  }

  /**
   * Specifies the URI of the SCIM resource representing the User's manager.
   *
   * @param ref The URI of the SCIM resource representing the User's manager.
   * @return This object.
   */
  @NotNull
  public Manager setRef(@Nullable final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Retrieves the displayName of the User's manager.
   *
   * @return The displayName of the User's manager.
   */
  @Nullable
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Specifies the displayName of the User's manager.
   *
   * @param displayName The displayName of the User's manager.
   * @return This object.
   */
  @NotNull
  public Manager setDisplayName(@Nullable final String displayName)
  {
    this.displayName = displayName;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this manager (i.e., an
   * employee's manager).
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this manager, or
   *            {@code false} if not.
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

    Manager manager = (Manager) o;
    if (!Objects.equals(value, manager.value))
    {
      return false;
    }
    if (!Objects.equals(ref, manager.ref))
    {
      return false;
    }
    return Objects.equals(displayName, manager.displayName);
  }

  /**
   * Retrieves a hash code for this manager.
   *
   * @return  A hash code for this manager.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(value, ref, displayName);
  }
}
