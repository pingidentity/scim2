/*
 * Copyright 2019-2025 Ping Identity Corporation
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
 * Copyright 2019-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;

import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * This class represents a group object as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc7643#section-4.2">
 * RFC 7643 section 4.2</a>. A group resource, or "group", is a collection of
 * other resources, which helps organize user accounts. Groups often contain
 * {@link UserResource} objects, but can contain other resource types, including
 * other group resources. Groups contained within other groups are referred to
 * as "nested" groups.
 * <br><br>
 *
 * Groups help enable role-based access control, such as allowing a group of
 * users to view a set of documents. In this example, an administrator can
 * modify user access to all the documents by adding or removing users from a
 * single group. Note that the SCIM standard itself does not define any specific
 * rules for this type of authorization or authentication, so this is largely
 * defined by SCIM services.
 * <br><br>
 *
 * Group resources have the following attributes defined:
 * <ul>
 *   <li> {@code id}: The unique identifier for the group resource.
 *   <li> {@code displayName}: Represents the human-readable name for the group.
 *   <li> {@code members}: A multi-valued attribute that indicates all user
 *        accounts that are considered a part of this group.
 * </ul>
 *
 * The following represents an example of a group resource:
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:Group" ],
 *     "id": "8e4d749e-6dde-420a-8d71-00faf8d57510",
 *     "displayName": "Example Group With One Member",
 *     "members": [{
 *       "value": "cab1e",
 *       "type": "DIRECT"
 *     }]
 *   }
 * </pre>
 *
 * This group can be created with the following Java code:
 * <pre><code>
 *   GroupResource group = new GroupResource()
 *       .setDisplayName("Example Group With One Member")
 *       .setMembers(new Member().setValue("cab1e").setType("DIRECT"));
 *   group.setId("8e4d749e-6dde-420a-8d71-00faf8d57510");
 * </code></pre>
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:Group",
    name="Group", description = "Group")
public class GroupResource extends BaseScimResource
{
  @NotNull
  @Attribute(description = "A human-readable name for the Group.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String displayName;

  @Nullable
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
  @NotNull
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Specifies the name of the Group, suitable for display to end-users.
   *
   * @param displayName The name of the Group, suitable for display to
   *                    end-users.
   * @return This object.
   */
  @NotNull
  public GroupResource setDisplayName(@NotNull final String displayName)
  {
    this.displayName = displayName;
    return this;
  }

  /**
   * Retrieves the list of group members.
   *
   * @return The list of group members.
   */
  @Nullable
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
  @NotNull
  public GroupResource setMembers(@Nullable final List<Member> members)
  {
    this.members = members;
    return this;
  }

  /**
   * Alternate version of {@link #setMembers(List)} that accepts individual
   * Member objects that are not contained in a list.
   *
   * @param member    The first member to add. This must not be {@code null}.
   * @param members   An optional set of additional arguments. Any {@code null}
   *                  values will be ignored.
   * @return This object.
   *
   * @since 4.1.0
   */
  @NotNull
  public GroupResource setMembers(@NotNull final Member member,
                                  @Nullable final Member... members)
  {
    return setMembers(toList(member, members));
  }

  /**
   * Indicates whether the provided object is equal to this group resource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this group
   *            resource, or {@code false} if not.
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
    if (!super.equals(o))
    {
      return false;
    }
    final GroupResource that = (GroupResource) o;
    return Objects.equals(displayName, that.displayName) &&
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
