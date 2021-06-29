/*
 * Copyright 2019-2021 Ping Identity Corporation
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
import java.util.Objects;

/**
 * A member of a Group resource.
 */
public class Member
{
  @Attribute(description = "The identifier of a group member.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

  @Attribute(description = "The URI of the member resource.",
      isRequired = true,
      referenceTypes = { "User", "Group" },
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  @JsonProperty("$ref")
  private URI ref;

  @Attribute(description = "A human readable name, primarily used for " +
      "display purposes.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String display;

  /**
   * Retrieves the identifier of the group member.
   *
   * @return The identifier of the group member.
   */
  public String getValue()
  {
    return value;
  }

  /**
   * Specifies the identifier of the group member.
   *
   * @param value The identifier of the group member
   * @return This object.
   */
  public Member setValue(final String value)
  {
    this.value = value;
    return this;
  }

  /**
   * Retrieves the URI of the SCIM resource corresponding to this group member.
   *
   * @return The URI of the SCIM resource corresponding to this group member.
   */
  public URI getRef()
  {
    return ref;
  }

  /**
   * Specifies the URI of the SCIM resource corresponding to this group member.
   *
   * @param ref The URI of the SCIM resource corresponding to this group member
   * @return This object.
   */
  public Member setRef(final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Retrieves the display name, primarily used for display purposes.
   *
   * @return The display name.
   */
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
  public Member setDisplay(final String display)
  {
    this.display = display;
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
    final Member member = (Member) o;
    return value.equals(member.value) &&
        ref.equals(member.ref) &&
        Objects.equals(display, member.display);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(value, ref, display);
  }
}
