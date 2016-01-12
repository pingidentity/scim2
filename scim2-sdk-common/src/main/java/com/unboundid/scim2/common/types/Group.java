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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URI;

/**
 * Group membership for the user.
 */
public class Group
{
  @Attribute(description = "The identifier of the User's group.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

  @Attribute(description = "The URI of the corresponding Group " +
      "resource to which the user belongs",
      isRequired = false,
      referenceTypes = { "User", "Group" },
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  @JsonProperty("$ref")
  private URI ref;

  @Attribute(description = "A human readable name, primarily used for " +
      "display purposes.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String display;

  @Attribute(description = "A label indicating the attribute's " +
      "function; e.g., 'direct' or 'indirect'.",
      isRequired = false,
      isCaseExact = false,
      canonicalValues = { "direct", "indirect" },
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String type;

  /**
   * Retrieves the identifier of the User's group.
   *
   * @return The identifier of the User's group.
   */
  public String getValue()
  {
    return value;
  }

  /**
   * Specifies the identifier of the User's group.
   *
   * @param value The identifier of the User's group.
   * @return This object.
   */
  public Group setValue(final String value)
  {
    this.value = value;
    return this;
  }

  /**
   * Retrieves the URI of the corresponding Group resource to which the user
   * belongs.
   *
   * @return the URI of the corresponding Group resource to which the user
   * belongs.
   */
  public URI getRef()
  {
    return ref;
  }

  /**
   * Specifies the URI of the corresponding Group resource to which the user
   * belongs.
   *
   * @param ref The URI of the corresponding Group resource to which the user
   * belongs.
   * @return This object.
   */
  public Group setRef(final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Specifies the human readable name, primarily used for display purposes.
   *
   * @return The human readable name.
   */
  public String getDisplay()
  {
    return display;
  }

  /**
   * Specifies the human readable name, primarily used for display purposes.
   *
   * @param display The human readable name.
   * @return This object.
   */
  public Group setDisplay(final String display)
  {
    this.display = display;
    return this;
  }

  /**
   * Retrieves the label indicating the attribute's function.
   *
   * @return The label indicating the attribute's function.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Specifies the label indicating the attribute's function.
   *
   * @param type The label indicating the attribute's function.
   * @return This object.
   */
  public Group setType(final String type)
  {
    this.type = type;
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

    Group group = (Group) o;

    if (value != null ? !value.equals(group.value) : group.value != null)
    {
      return false;
    }
    if (ref != null ? !ref.equals(group.ref) : group.ref != null)
    {
      return false;
    }
    if (display != null ? !display.equals(group.display) :
        group.display != null)
    {
      return false;
    }
    return !(type != null ? !type.equals(group.type) : group.type != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (ref != null ? ref.hashCode() : 0);
    result = 31 * result + (display != null ? display.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }
}
