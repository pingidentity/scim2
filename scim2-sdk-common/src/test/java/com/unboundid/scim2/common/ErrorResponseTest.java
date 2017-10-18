/*
 * Copyright 2015-2017 UnboundID Corp.
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test cases for {@link com.unboundid.scim2.common.messages.ErrorResponse}.
 */
public class ErrorResponseTest
{
  /**
   * Confirms that an ErrorResponse can be deserialized when the status field
   * is a JSON string.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDeserializeStringStatus() throws Exception
  {
    final ObjectNode node = (ObjectNode) JsonUtils.getObjectReader().readTree(
        "{\n" +
        "  \"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:Error\"],\n" +
        "  \"scimType\":\"mutability\",\n" +
        "  \"detail\":\"Attribute 'id' is readOnly\",\n" +
        "  \"status\": \"400\"\n" +
        "}"
    );

    final ErrorResponse errorResponse =
        JsonUtils.nodeToValue(node, ErrorResponse.class);
    assertEquals(errorResponse.getStatus(), Integer.valueOf(400));
    assertEquals(errorResponse.getScimType(), BadRequestException.MUTABILITY);
    assertNotNull(errorResponse.getDetail());
    assertEquals(errorResponse.getSchemaUrns().size(), 1);
    assertEquals(errorResponse.getSchemaUrns().iterator().next(),
        "urn:ietf:params:scim:api:messages:2.0:Error");
  }

  /**
   * Confirms that an ErrorResponse can be deserialized when the status field
   * is a JSON number.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDeserializeNumberStatus() throws Exception
  {
    final ObjectNode node = (ObjectNode) JsonUtils.getObjectReader().readTree(
        "{\n" +
        "  \"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:Error\"],\n" +
        "  \"scimType\":\"mutability\",\n" +
        "  \"detail\":\"Attribute 'id' is readOnly\",\n" +
        "  \"status\": 400\n" +
        "}"
    );

    final ErrorResponse errorResponse =
        JsonUtils.nodeToValue(node, ErrorResponse.class);
    assertEquals(errorResponse.getStatus(), Integer.valueOf(400));
    assertEquals(errorResponse.getScimType(), BadRequestException.MUTABILITY);
    assertNotNull(errorResponse.getDetail());
    assertEquals(errorResponse.getSchemaUrns().size(), 1);
    assertEquals(errorResponse.getSchemaUrns().iterator().next(),
        "urn:ietf:params:scim:api:messages:2.0:Error");
  }
}
