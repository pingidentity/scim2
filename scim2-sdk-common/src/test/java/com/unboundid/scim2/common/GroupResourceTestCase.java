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
package com.unboundid.scim2.common;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Member;
import com.unboundid.scim2.common.utils.JsonUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Some basic tests for serialization and de-serialization of the core group
 * resource.
 */
public class GroupResourceTestCase
{
  private String fullRepresentation;

  /**
   * Initializes the environment before each test method.
   */
  @BeforeEach
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
    }

    assertEquals(groupResource.getMembers().get(0).getDisplay(), "Babs Jensen");
    assertEquals(groupResource.getMembers().get(1).getDisplay(), "Mandy Pepperidge");
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
}
