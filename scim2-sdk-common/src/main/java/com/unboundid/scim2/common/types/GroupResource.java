/*
 * Copyright 2019-2024 Ping Identity Corporation
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

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Schema;

import java.util.List;
import java.util.Objects;

/**
 * SCIM provides a resource type for "{@code Group}" resources.  The core schema
 * for "{@code Group}" is identified using the URI:
 * "{@code urn:ietf:params:scim:schemas:core:2.0:Group}".
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:Group",
    name="Group", description = "Group")
public class GroupResource extends BaseScimResource
{
  @Attribute(description = "A human-readable name for the Group.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String displayName;

  @Attribute(description = "A list of members of the Group.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Member.class)
  private List<Member> members;

  /**
   * Retrieves the name of the Group, suitable for display to end-users.
   *
   * @return The name of the Group, suitable for display to end-users.
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Specifies the name of the Group, suitable for display to end-users.
   *
   * @param displayName The name of the Group, suitable for display to end-users.
   * @return This object.
   */
  public GroupResource setDisplayName(final String displayName)
  {
    this.displayName = displayName;
    return this;
  }

  /**
   * Retrieves the list of group members.
   *
   * @return The list of group members.
   */
  public List<Member> getMembers()
  {
    return members;
  }

  /**
   * Specifies the list of group members.
   *
   * @param members The list of group members.
   * @return This object.
   */
  public GroupResource setMembers(final List<Member> members)
  {
    this.members = members;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this group resource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this group
   *            resource, or {@code false} if not.
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
    if (!super.equals(o))
    {
      return false;
    }
    final GroupResource that = (GroupResource) o;
    return displayName.equals(that.displayName) &&
        Objects.equals(members, that.members);
  }

  /**
   * Retrieves a hash code for this group resource.
   *
   * @return  A hash code for this group resource.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), displayName, members);
  }
}
