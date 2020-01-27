/*
 * Copyright 2015-2020 Ping Identity Corporation
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

import java.net.URI;

/**
 * A complex type that optionally allows Service Providers to represent
 * organizational hierarchy by referencing the 'id' attribute of another User.
 */
public class Manager
{
  @Attribute(description = "The id of the SCIM resource representing " +
      "the User's manager.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

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
  public Manager setValue(final String value)
  {
    this.value = value;
    return this;
  }

  /**
   * Retrieves the URI of the SCIM resource representing the User's manager.
   *
   * @return The URI of the SCIM resource representing the User's manager.
   */
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
  public Manager setRef(final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Retrieves the displayName of the User's manager.
   *
   * @return The displayName of the User's manager.
   */
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
  public Manager setDisplayName(final String displayName)
  {
    this.displayName = displayName;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
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

    if (value != null ? !value.equals(manager.value) : manager.value != null)
    {
      return false;
    }
    if (ref != null ? !ref.equals(manager.ref) : manager.ref != null)
    {
      return false;
    }
    return !(displayName != null ? !displayName.equals(manager.displayName) :
        manager.displayName != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (ref != null ? ref.hashCode() : 0);
    result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
    return result;
  }
}
