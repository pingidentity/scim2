/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Copyright 2015-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test cases for {@link com.unboundid.scim2.common.messages.ErrorResponse}.
 */
public class ErrorResponseTest
{
  private static final String SCHEMA =
      SchemaUtils.getSchemaIdFromAnnotation(ErrorResponse.class);
  private static final int STATUS = 400;
  private static final String SCIM_TYPE = BadRequestException.MUTABILITY;
  private static final String DETAIL = "Attribute 'id' is readOnly";

  /**
   * Confirms that an ErrorResponse can be deserialized when the status field
   * is a JSON string.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDeserializationAndSerializationWithStringStatus()
      throws Exception
  {
    final ObjectNode node = (ObjectNode) JsonUtils.getObjectReader().readTree(
        """
            {
              "schemas": ["%s"],
              "scimType":"%s",
              "detail":"%s",
              "status": "%d"
            }""".formatted(SCHEMA, SCIM_TYPE, DETAIL, STATUS)
    );

    final ErrorResponse errorResponse =
        JsonUtils.nodeToValue(node, ErrorResponse.class);
    assertEquals(errorResponse.getStatus(), Integer.valueOf(STATUS));
    assertEquals(errorResponse.getScimType(), SCIM_TYPE);
    assertEquals(errorResponse.getDetail(), DETAIL);
    assertEquals(errorResponse.getSchemaUrns().size(), 1);
    assertEquals(errorResponse.getSchemaUrns().iterator().next(), SCHEMA);

    final String serializedString =
        JsonUtils.getObjectWriter().writeValueAsString(errorResponse);
    final ErrorResponse deserializedErrorResponse =
        JsonUtils.getObjectReader().forType(ErrorResponse.class).
            readValue(serializedString);
    assertEquals(errorResponse, deserializedErrorResponse);
  }

  /**
   * Confirms that an ErrorResponse can be deserialized when the status field
   * is a JSON number.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDeserializationAndSerializationWithNumberStatus()
      throws Exception
  {
    final ObjectNode node = (ObjectNode) JsonUtils.getObjectReader().readTree(
        "{\n" +
        "  \"schemas\": [\"" + SCHEMA + "\"],\n" +
        "  \"scimType\":\"" + SCIM_TYPE + "\",\n" +
        "  \"detail\":\"" + DETAIL + "\",\n" +
        "  \"status\": " + STATUS + "\n" +
        "}"
    );

    final ErrorResponse errorResponse =
        JsonUtils.nodeToValue(node, ErrorResponse.class);
    assertEquals(errorResponse.getStatus(), Integer.valueOf(STATUS));
    assertEquals(errorResponse.getScimType(), SCIM_TYPE);
    assertEquals(errorResponse.getDetail(), DETAIL);
    assertEquals(errorResponse.getSchemaUrns().size(), 1);
    assertEquals(errorResponse.getSchemaUrns().iterator().next(), SCHEMA);

    final String serializedString =
        JsonUtils.getObjectWriter().writeValueAsString(errorResponse);
    final ErrorResponse deserializedErrorResponse =
        JsonUtils.getObjectReader().forType(ErrorResponse.class).
            readValue(serializedString);
    assertEquals(errorResponse, deserializedErrorResponse);
  }
}
