/*
 * Copyright 2019-2023 Ping Identity Corporation
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
package com.unboundid.scim2.common;

import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.AttributeDefinition.Mutability;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Member;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Some basic tests for serialization and de-serialization of the core group
 * resource.
 */
public class GroupResourceTestCase
{
  // An example representation of a group defined in RFC 7643 section 8.4.
  private String fullRepresentation;

  // A representation of a group in which members have the 'type' field defined
  // for each group member.
  private String membersWithType;

  /**
   * Initializes the environment before each test method.
   */
  @BeforeMethod
  public void init()
  {
    fullRepresentation = "{\n" +
        "  \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
        "  \"id\": \"e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
        "  \"displayName\": \"Tour Guides\",\n" +
        "  \"members\": [\n" +
        "    {\n" +
        "      \"value\": \"2819c223-7f76-453a-919d-413861904646\",\n" +
        "      \"$ref\":\n" +
        "\"https://example.com/v2/Users/2819c223-7f76-453a-919d-413861904646\",\n" +
        "      \"display\": \"Babs Jensen\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"value\": \"902c246b-6245-4190-8e05-00816be7344a\",\n" +
        "      \"$ref\":\n" +
        "\"https://example.com/v2/Users/902c246b-6245-4190-8e05-00816be7344a\",\n" +
        "      \"display\": \"Mandy Pepperidge\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"meta\": {\n" +
        "    \"resourceType\": \"Group\",\n" +
        "    \"created\": \"2010-01-23T04:56:22Z\",\n" +
        "    \"lastModified\": \"2011-05-13T04:42:34Z\",\n" +
        "    \"version\": \"W\\/\\\"3694e05e9dff592\\\"\",\n" +
        "    \"location\":\n" +
        "\"https://example.com/v2/Groups/e9e30dba-f08f-4109-8486-d5c6a331660a\"\n" +
        "  }\n" +
        "}";

    membersWithType = "{" +
        "  \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"]," +
        "  \"id\": \"c215de78-5c6a-407b-bea3-9a2a8f0f1202\"," +
        "  \"displayName\": \"Not Basketball Players\"," +
        "  \"members\": [" +
        "    {" +
        "      \"value\": \"03b5ae49-fa74-4f5c-8af4-12b363100a2b\"," +
        "      \"type\": \"User\"," +
        "      \"$ref\":\"https://example.com/v2/Users/" +
                      "03b5ae49-fa74-4f5c-8af4-12b363100a2b\"," +
        "      \"display\": \"Michael B. Jordan\"" +
        "    }," +
        "    {" +
        "      \"value\": \"f9b62a62-abe8-430e-b802-9e84f0e06baa\"," +
        "      \"type\": \"Group\"," +
        "      \"$ref\": \"https://example.com/v2/Groups/" +
                      "f9b62a62-abe8-430e-b802-9e84f0e06baa\"," +
        "      \"display\": \"Wizards (of the Coast)\"" +
        "    }" +
        "  ]" +
        "}";
  }

  /**
   * Test de-serializing the full core user representation copied from
   * RFC 7643.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSpecFullRepresentation() throws Exception
  {
    GroupResource groupResource =
        JsonUtils.getObjectReader().forType(GroupResource.class).readValue(
            fullRepresentation);

    assertNotNull(groupResource.getSchemaUrns());
    assertNotNull(groupResource.getId());
    assertNotNull(groupResource.getMeta());
    assertNotNull(groupResource.getDisplayName());
    assertNotNull(groupResource.getMembers());
    assertEquals(groupResource.getMembers().size(), 2);

    for (Member member : groupResource.getMembers())
    {
      assertNotNull(member.getValue());
      assertNotNull(member.getRef());
      assertNotNull(member.getDisplay());
      assertNull(member.getType());
    }

    assertEquals(groupResource.getMembers().get(0).getDisplay(), "Babs Jensen");
    assertEquals(groupResource.getMembers().get(1).getDisplay(), "Mandy Pepperidge");
  }

  /**
   * Ensures that a JSON representation of a group will correctly serialize
   * members when a member includes the {@code type} attribute.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testMembersWithTypeFieldDefined() throws Exception
  {
    GroupResource groupResource = JsonUtils.getObjectReader()
            .forType(GroupResource.class).readValue(membersWithType);

    for (Member member : groupResource.getMembers())
    {
      assertThat(member.getType()).isNotNull();
      assertThat(member.getType()).as("Member should be a canonical type")
              .isIn("User", "Group");
    }
  }

  /**
   * Test conversion to GenericScimResource.
   *
   * @throws IOException indicates a test failure.
   */
  @Test
  public void testAsGenericScimResource() throws IOException
  {
    GroupResource groupResource1 =
        JsonUtils.getObjectReader().forType(GroupResource.class).readValue(
            fullRepresentation);

    GenericScimResource gsr = groupResource1.asGenericScimResource();

    GroupResource groupResource2 = JsonUtils.nodeToValue(gsr.getObjectNode(),
        GroupResource.class);
    assertEquals(groupResource1, groupResource2);
  }


  /**
   * Ensures that the {@code members} field of a {@link GroupResource} contains
   * immutable sub-attributes.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testMembersImmutable() throws Exception
  {
    Collection<AttributeDefinition> groupSchema =
            SchemaUtils.getAttributes(GroupResource.class);
    List<AttributeDefinition> memberDefinition =
            groupSchema.stream().filter(
                    attribute -> attribute.getName().equalsIgnoreCase("members")
            ).collect(Collectors.toList());
    assertThat(memberDefinition).hasSize(1);

    for (AttributeDefinition a : memberDefinition.get(0).getSubAttributes())
    {
      assertThat(a.getMutability()).isEqualTo(Mutability.IMMUTABLE);
    }
  }
}
