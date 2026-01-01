/*
 * Copyright 2019-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2019-2026 Ping Identity Corporation
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
 * A member of a Group resource.
 */
public class Member
{
  @NotNull
  @Attribute(description = "The identifier of a group member.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

  @Nullable
  @Attribute(description = "A label indicating the type of resource, e.g.,"
          + " 'User' or 'Group'",
      canonicalValues = { "User", "Group" },
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  @JsonProperty("type")
  private String type;

  @Nullable
  @Attribute(description = "The URI of the member resource.",
      isRequired = false,
      referenceTypes = { "User", "Group" },
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  @JsonProperty("$ref")
  private URI ref;

  @Nullable
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
  @NotNull
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
  @NotNull
  public Member setValue(@NotNull final String value)
  {
    this.value = value;
    return this;
  }

  /**
   * Retrieves the type of the group member.
   *
   * @return The type of the group member.
   */
  @Nullable
  public String getType()
  {
    return type;
  }

  /**
   * Specifies the type of the group member.
   *
   * @param type The type of the group member.
   * @return This object.
   */
  @NotNull
  public Member setType(@NotNull final String type)
  {
    this.type = type;
    return this;
  }

  /**
   * Retrieves the URI of the SCIM resource corresponding to this group member.
   *
   * @return The URI of the SCIM resource corresponding to this group member.
   */
  @Nullable
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
  @NotNull
  public Member setRef(@Nullable final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Retrieves the display name, primarily used for display purposes.
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
  public Member setDisplay(@Nullable final String display)
  {
    this.display = display;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this group member.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this member, or
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
    final Member member = (Member) o;
    return value.equals(member.value) &&
        Objects.equals(ref, member.ref) &&
        Objects.equals(type, member.type) &&
        Objects.equals(display, member.display);
  }

  /**
   * Retrieves a hash code for this group member.
   *
   * @return  A hash code for this group member.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(value, ref, type, display);
  }
}
