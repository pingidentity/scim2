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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.net.URI;

/**
 * Group membership for the user.
 */
public class Group
{
  @Nullable
  @Attribute(description = "The identifier of the User's group.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

  @Nullable
  @Attribute(description = "The URI of the corresponding Group " +
      "resource to which the user belongs",
      isRequired = false,
      referenceTypes = { "User", "Group" },
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  @JsonProperty("$ref")
  private URI ref;

  @Nullable
  @Attribute(description = "A human readable name, primarily used for " +
      "display purposes.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String display;

  @Nullable
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
  @Nullable
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
  @NotNull
  public Group setValue(@Nullable final String value)
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
  @Nullable
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
  @NotNull
  public Group setRef(@Nullable final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Specifies the display name, primarily used for display purposes.
   *
   * @return The display name.
   */
  @Nullable
  public String getDisplay()
  {
    return display;
  }

  /**
   * Specifies the display name, primarily used for display purposes.
   *
   * @param display The display name.
   * @return This object.
   */
  @NotNull
  public Group setDisplay(@Nullable final String display)
  {
    this.display = display;
    return this;
  }

  /**
   * Retrieves the label indicating the attribute's function.
   *
   * @return The label indicating the attribute's function.
   */
  @Nullable
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
  @NotNull
  public Group setType(@Nullable final String type)
  {
    this.type = type;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this group membership
   * identifier.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this group
   *            membership identifier, or {@code false} if not.
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
   * Retrieves a hash code for this group membership identifier.
   *
   * @return  A hash code for this group membership identifier.
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
