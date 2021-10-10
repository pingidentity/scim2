/*
 * Copyright 2015-2021 Ping Identity Corporation
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

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.common.collect.Lists;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.PatchOpType;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.utils.JsonUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for patch operation.
 */
public class PatchOpTestCase
{
  /**
   * Test patch request.
   *
   * @throws IOException If an error occurs.
   * @throws ScimException If an error occurs.
   */
  @Test
  public void getTestPatch() throws IOException, ScimException
  {
    PatchRequest patchOp = JsonUtils.getObjectReader().
        forType(PatchRequest.class).
        readValue("{  \n" +
            "  \"schemas\":[  \n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "  ],\n" +
            "  \"Operations\":[  \n" +
            "    {  \n" +
            "      \"op\":\"add\",\n" +
            "      \"value\":{  \n" +
            "        \"emails\":[  \n" +
            "          {  \n" +
            "            \"value\":\"babs@jensen.org\",\n" +
            "            \"type\":\"home\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"nickname\":\"Babs\"\n" +
            "      }\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"remove\",\n" +
            "      \"path\":\"emails[type eq \\\"work\\\" and " +
            "value ew \\\"example.com\\\"]\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"remove\",\n" +
            "      \"path\":\"meta\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"add\",\n" +
            "      \"path\":\"members\",\n" +
            "      \"value\":[  \n" +
            "        {  \n" +
            "          \"display\":\"Babs Jensen\",\n" +
            "          \"$ref\":\"https://example.com/v2/Users/2819c223..." +
            "413861904646\",\n" +
            "          \"value\":\"2819c223-7f76-453a-919d-413861904646\"\n" +
            "        },\n" +
            "        {  \n" +
            "          \"display\":\"James Smith\",\n" +
            "          \"$ref\":\"https://example.com/v2/Users/08e1d05d..." +
            "473d93df9210\",\n" +
            "          \"value\":\"08e1d05d-121c-4561-8b96-473d93df9210\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"replace\",\n" +
            "      \"path\":\"members2\",\n" +
            "      \"value\":[  \n" +
            "        {  \n" +
            "          \"display\":\"Babs Jensen\",\n" +
            "          \"$ref\":\"https://example.com/v2/Users/2819c223..." +
            "413861904646\",\n" +
            "          \"value\":\"2819c223...413861904646\"\n" +
            "        },\n" +
            "        {  \n" +
            "          \"display\":\"James Smith\",\n" +
            "          \"$ref\":\"https://example.com/v2/Users/08e1d05d..." +
            "473d93df9210\",\n" +
            "          \"value\":\"08e1d05d...473d93df9210\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"replace\",\n" +
            "      \"path\":\"addresses[type eq \\\"work\\\"]\",\n" +
            "      \"value\":{  \n" +
            "        \"type\":\"work\",\n" +
            "        \"streetAddress\":\"911 Universal City Plaza\",\n" +
            "        \"locality\":\"Hollywood\",\n" +
            "        \"region\":\"CA\",\n" +
            "        \"postalCode\":\"91608\",\n" +
            "        \"country\":\"US\",\n" +
            "        \"formatted\":\"911 Universal City Plaza\\nHollywood, " +
            "CA 91608 US\",\n" +
            "        \"primary\":true\n" +
            "      }\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"replace\",\n" +
            "      \"path\":\"addresses[type eq \\\"home\\\"]." +
            "streetAddress\",\n" +
            "      \"value\":\"1010 Broadway Ave\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"replace\",\n" +
            "      \"value\":{  \n" +
            "        \"emails2\":[  \n" +
            "          {  \n" +
            "            \"value\":\"bjensen@example.com\",\n" +
            "            \"type\":\"work\",\n" +
            "            \"primary\":true\n" +
            "          },\n" +
            "          {  \n" +
            "            \"value\":\"babs@jensen.org\",\n" +
            "            \"type\":\"home\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"nickname2\":\"Babs\"\n" +
            "      }\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"remove\",\n" +
            "      \"path\":\"schemas[" +
            "value eq \\\"urn:ubid:custom:schema1\\\"]\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"op\":\"replace\",\n" +
            "      \"path\":\"schemas[" +
            "value eq \\\"urn:ubid:custom:schema2\\\"]\",\n" +
            "      \"value\":\"urn:ubid:custom:schema3\"\n" +
            "    }," +
            "    {  \n" +
            "      \"op\":\"add\",\n" +
            "      \"path\":\"urn:ubid:custom:schema4:attr\",\n" +
            "      \"value\":\"somevalue\"\n" +
            "    }" +
            "  ]\n" +
            "}");

    JsonNode prePatchResource = JsonUtils.getObjectReader().
        readTree("{  \n" +
            "  \"schemas\":[  \n" +
            "    \"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"urn:ubid:custom:schema1\",\n" +
            "    \"urn:ubid:custom:schema2\"\n" +
            "  ],\n" +
            "  \"id\":\"2819c223-7f76-453a-919d-413861904646\",\n" +
            "  \"userName\":\"bjensen@example.com\",\n" +
            "  \"nickname2\":\"nickname\",\n" +
            "  \"emails\":[  \n" +
            "    {  \n" +
            "      \"value\":\"bjensen@example.com\",\n" +
            "      \"type\":\"work\",\n" +
            "      \"primary\":true\n" +
            "    },\n" +
            "    {  \n" +
            "      \"value\":\"babs@jensen.org\",\n" +
            "      \"type\":\"home\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"emails2\":[  \n" +
            "    {  \n" +
            "      \"value\":\"someone@somewhere.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"members2\":[  \n" +
            "    {  \n" +
            "      \"value\":\"e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
            "      \"$ref\":\"https://example.com/v2/Groups/" +
            "e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
            "      \"display\":\"Tour Guides\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"value\":\"fc348aa8-3835-40eb-a20b-c726e15c55b5\",\n" +
            "      \"$ref\":\"https://example.com/v2/Groups/" +
            "fc348aa8-3835-40eb-a20b-c726e15c55b5\",\n" +
            "      \"display\":\"Employees\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"addresses\":[  \n" +
            "    {  \n" +
            "      \"type\":\"work\",\n" +
            "      \"streetAddress\":\"13809 Research Blvd\",\n" +
            "      \"locality\":\"Austin\",\n" +
            "      \"region\":\"TX\",\n" +
            "      \"postalCode\":\"78750\",\n" +
            "      \"country\":\"USA\",\n" +
            "      \"formatted\":\"13809 Research Blvd\\nAustin, " +
            "TX 78750 USA\",\n" +
            "      \"primary\":true\n" +
            "    },\n" +
            "    {  \n" +
            "      \"type\":\"home\",\n" +
            "      \"streetAddress\":\"456 Hollywood Blvd\",\n" +
            "      \"locality\":\"Hollywood\",\n" +
            "      \"region\":\"CA\",\n" +
            "      \"postalCode\":\"91608\",\n" +
            "      \"country\":\"USA\",\n" +
            "      \"formatted\":\"456 Hollywood Blvd\\nHollywood, " +
            "CA 91608 USA\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"meta\":{  \n" +
            "    \"resourceType\":\"User\",\n" +
            "    \"created\":\"2010-01-23T04:56:22Z\",\n" +
            "    \"lastModified\":\"2011-05-13T04:42:34Z\",\n" +
            "    \"version\":\"W\\/\\\"3694e05e9dff590\\\"\",\n" +
            "    \"location\":\"https://example.com/v2/Users/" +
            "2819c223-7f76-453a-919d-413861904646\"\n" +
            "  }\n" +
            "}");

    JsonNode postPatchResource = JsonUtils.getObjectReader().
        readTree("{  \n" +
            "   \"schemas\":[  \n" +
            "      \"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "      \"urn:ubid:custom:schema3\",\n" +
            "      \"urn:ubid:custom:schema4\"\n" +
            "   ],\n" +
            "   \"id\":\"2819c223-7f76-453a-919d-413861904646\",\n" +
            "   \"userName\":\"bjensen@example.com\",\n" +
            "   \"nickname2\":\"Babs\",\n" +
            "   \"emails\":[  \n" +
            "      {  \n" +
            "         \"value\":\"babs@jensen.org\",\n" +
            "         \"type\":\"home\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"emails2\":[  \n" +
            "      {  \n" +
            "         \"value\":\"bjensen@example.com\",\n" +
            "         \"type\":\"work\",\n" +
            "         \"primary\":true\n" +
            "      },\n" +
            "      {  \n" +
            "         \"value\":\"babs@jensen.org\",\n" +
            "         \"type\":\"home\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"members2\":[  \n" +
            "      {  \n" +
            "         \"display\":\"Babs Jensen\",\n" +
            "         \"$ref\":" +
            "\"https://example.com/v2/Users/2819c223...413861904646\",\n" +
            "         \"value\":\"2819c223...413861904646\"\n" +
            "      },\n" +
            "      {  \n" +
            "         \"display\":\"James Smith\",\n" +
            "         \"$ref\":" +
            "\"https://example.com/v2/Users/08e1d05d...473d93df9210\",\n" +
            "         \"value\":\"08e1d05d...473d93df9210\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"addresses\":[  \n" +
            "      {  \n" +
            "         \"type\":\"work\",\n" +
            "         \"streetAddress\":\"911 Universal City Plaza\",\n" +
            "         \"locality\":\"Hollywood\",\n" +
            "         \"region\":\"CA\",\n" +
            "         \"postalCode\":\"91608\",\n" +
            "         \"country\":\"US\",\n" +
            "         \"formatted\":" +
            "\"911 Universal City Plaza\\nHollywood, CA 91608 US\",\n" +
            "         \"primary\":true\n" +
            "      },\n" +
            "      {  \n" +
            "         \"type\":\"home\",\n" +
            "         \"streetAddress\":\"1010 Broadway Ave\",\n" +
            "         \"locality\":\"Hollywood\",\n" +
            "         \"region\":\"CA\",\n" +
            "         \"postalCode\":\"91608\",\n" +
            "         \"country\":\"USA\",\n" +
            "         \"formatted\":" +
            "\"456 Hollywood Blvd\\nHollywood, CA 91608 USA\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"nickname\":\"Babs\",\n" +
            "   \"members\":[  \n" +
            "      {  \n" +
            "         \"display\":\"Babs Jensen\",\n" +
            "         \"$ref\":" +
            "\"https://example.com/v2/Users/2819c223...413861904646\",\n" +
            "         \"value\":\"2819c223-7f76-453a-919d-413861904646\"\n" +
            "      },\n" +
            "      {  \n" +
            "         \"display\":\"James Smith\",\n" +
            "         \"$ref\":" +
            "\"https://example.com/v2/Users/08e1d05d...473d93df9210\",\n" +
            "         \"value\":\"08e1d05d-121c-4561-8b96-473d93df9210\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"urn:ubid:custom:schema4\":{  \n" +
            "      \"attr\":\"somevalue\"\n" +
            "   }\n" +
            "}");

    GenericScimResource scimResource =
        new GenericScimResource((ObjectNode)prePatchResource);
    patchOp.apply(scimResource);
    assertEquals(scimResource.getObjectNode(), postPatchResource);

    PatchRequest constructed = new PatchRequest(patchOp.getOperations());

    assertEquals(constructed, patchOp);

    String serialized = JsonUtils.getObjectWriter().
        writeValueAsString(constructed);
    assertEquals(JsonUtils.getObjectReader().forType(PatchRequest.class).
        readValue(serialized), constructed);

  }

  /**
   * Test bad patch requests.
   *
   * @throws IOException If an error occurs.
   * @throws ScimException If an error occurs.
   */
  @Test
  public void getTestBadPatch() throws IOException, ScimException
  {
    try
    {
      JsonUtils.getObjectReader().
          forType(PatchRequest.class).
          readValue("{  \n" +
              "  \"schemas\":[  \n" +
              "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
              "  ],\n" +
              "  \"Operations\":[  \n" +
              "    {  \n" +
              "      \"op\":\"remove\",\n" +
              "      \"path\":\"emails[type eq \\\"work\\\" and " +
              "value ew \\\"example.com\\\"].too.deep\"\n" +
              "    }\n" +
              "  ]\n" +
              "}");
    }
    catch(JsonMappingException e)
    {
      assertEquals(
          ((BadRequestException) e.getCause()).getScimError().getScimType(),
          BadRequestException.INVALID_PATH);
    }

    try
    {
    JsonUtils.getObjectReader().
        forType(PatchRequest.class).
        readValue("{  \n" +
            "  \"schemas\":[  \n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "  ],\n" +
            "  \"Operations\":[  \n" +
            "    {  \n" +
            "      \"op\":\"remove\",\n" +
            "      \"path\":\"emails[type eq \\\"work\\\" and " +
            "value ew \\\"example.com\\\"].sub[something eq 2]\"\n" +
            "    }\n" +
            "  ]\n" +
            "}");
    }
    catch(JsonMappingException e)
    {
      assertEquals(
          ((BadRequestException) e.getCause()).getScimError().getScimType(),
          BadRequestException.INVALID_PATH);
    }

    try
    {
    JsonUtils.getObjectReader().
        forType(PatchRequest.class).
        readValue("{  \n" +
            "  \"schemas\":[  \n" +
            "    \"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
            "  ],\n" +
            "  \"Operations\":[  \n" +
            "    {  \n" +
            "      \"op\":\"add\",\n" +
            "      \"path\":\"emails[type eq \\\"work\\\" and " +
            "value ew \\\"example.com\\\"],\"\n" +
            "      \"value\":\"value\"\n" +
            "    }\n" +
            "  ]\n" +
            "}");
    }
    catch(JsonMappingException e)
    {
      assertEquals(
          ((BadRequestException) e.getCause()).getScimError().getScimType(),
          BadRequestException.INVALID_PATH);
    }
  }

  /**
   * test string methods.
   * @throws Exception error
   */
  @Test
  public void testStringPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addStringValues("path1",
        Lists.newArrayList("value1", "value2"));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).textValue(), "value1");
    assertEquals(jsonNode.get(1).textValue(), "value2");
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addStringValues(Path.fromString("path1"),
        Lists.newArrayList("value1", "value2"));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).textValue(), "value1");
    assertEquals(jsonNode.get(1).textValue(), "value2");
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", "value1");
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(String.class), "value1");
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(Path.fromString("path1"), "value1");
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(String.class), "value1");
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test boolean methods.
   * @throws Exception error
   */
  @Test
  public void testBooleanPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addBooleanValues("path1",
        Lists.newArrayList(Boolean.TRUE, Boolean.FALSE));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(
        jsonNode.get(0).booleanValue(), Boolean.TRUE.booleanValue());
    assertEquals(
        jsonNode.get(1).booleanValue(), Boolean.FALSE.booleanValue());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addBooleanValues(Path.fromString("path1"),
        Lists.newArrayList(Boolean.FALSE, Boolean.TRUE));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(
        jsonNode.get(0).booleanValue(), Boolean.FALSE.booleanValue());
    assertEquals(
        jsonNode.get(1).booleanValue(), Boolean.TRUE.booleanValue());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), Boolean.TRUE);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Boolean.class), Boolean.TRUE);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), Boolean.FALSE);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Boolean.class), Boolean.FALSE);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test double methods.
   * @throws Exception error
   */
  @Test
  public void testDoublePatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addDoubleValues("path1",
        Lists.newArrayList(1.1, 1.2));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).doubleValue(), 1.1, 0.01);
    assertEquals(jsonNode.get(1).doubleValue(), 1.2, 0.01);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addDoubleValues(Path.fromString("path1"),
        Lists.newArrayList(2.1, 2.2));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).doubleValue(), 2.1, 0.01);
    assertEquals(jsonNode.get(1).doubleValue(), 2.2, 0.01);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", 734.2);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Double.class), 734.2, 0.01);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), 0.3);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Double.class), 0.3, 0.01);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test integer methods.
   * @throws Exception error
   */
  @Test
  public void testIntegerPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addIntegerValues("path1",
        Lists.newArrayList(1, 2));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).intValue(), 1);
    assertEquals(jsonNode.get(1).intValue(), 2);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addIntegerValues(Path.fromString("path1"),
        Lists.newArrayList(3, 4));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).intValue(), 3);
    assertEquals(jsonNode.get(1).intValue(), 4);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", 5);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Integer.class).intValue(), 5);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), 7);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Integer.class).intValue(), 7);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test long methods.
   * @throws Exception error
   */
  @Test
  public void testLongPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addLongValues("path1",
        Lists.newArrayList(1L, 2L));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).longValue(), 1);
    assertEquals(jsonNode.get(1).longValue(), 2);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addIntegerValues(Path.fromString("path1"),
        Lists.newArrayList(3, 4));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).longValue(), 3);
    assertEquals(jsonNode.get(1).longValue(), 4);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", 5L);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Long.class).longValue(), 5L);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), 7L);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(Long.class).longValue(), 7L);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test date methods.
   * @throws Exception error
   */
  @Test
  public void testDatePatchOps() throws Exception
  {
    Date d1 = new Date(89233675234L);
    Date d2 = new Date(89233675235L);
    PatchOperation patchOp = PatchOperation.addDateValues(
        "path1", Lists.newArrayList(d1, d2));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(0)), d1);
    assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(1)), d2);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    Date d3 = new Date(89233675236L);
    Date d4 = new Date(89233675237L);
    patchOp = PatchOperation.addDateValues(
        Path.fromString("path1"), Lists.newArrayList(d3, d4));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(0)), d3);
    assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(1)), d4);
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    Date d5 = new Date(89233675238L);
    patchOp = PatchOperation.replace("path1", d5);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(String.class),
        GenericScimResource.getDateJsonNode(d5).textValue());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    Date d6 = new Date(89233675240L);
    patchOp = PatchOperation.replace(
        Path.fromString("path1"), d6);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(String.class),
        GenericScimResource.getDateJsonNode(d6).textValue());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test binary methods.
   * @throws Exception error
   */
  @Test
  public void testBinaryPatchOps() throws Exception
  {
    byte[] ba1 = new byte[] {0x00, 0x01, 0x02};
    byte[] ba2 = new byte[] {0x02, 0x01, 0x00};
    PatchOperation patchOp = PatchOperation.addBinaryValues(
        "path1", Lists.newArrayList(ba1, ba2));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(0).textValue()), ba1));
    assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(1).textValue()), ba2));
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    byte[] ba3 = new byte[] {0x03, 0x04, 0x05};
    byte[] ba4 = new byte[] {0x05, 0x04, 0x03};
    patchOp = PatchOperation.addBinaryValues(
        Path.fromString("path1"), Lists.newArrayList(ba3, ba4));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(0).textValue()), ba3));
    assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(1).textValue()), ba4));
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    byte[] ba5 = new byte[] {0x06, 0x07, 0x08};
    patchOp = PatchOperation.replace("path1", ba5);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertTrue(Arrays.equals(patchOp.getValue(byte[].class), ba5));
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    byte[] ba6 = new byte[] {0x09, 0x0a, 0x0b};
    patchOp = PatchOperation.replace(
        Path.fromString("path1"), ba6);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertTrue(Arrays.equals(patchOp.getValue(byte[].class), ba6));
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test URI methods.
   * @throws Exception error
   */
  @Test
  public void testURIPatchOps() throws Exception
  {
    URI uri1 = new URI("http://localhost:8080/apps/app1");
    URI uri2 = new URI("Users/1dd6d752-1744-47e5-a4a8-5f5670aa8905");
    PatchOperation patchOp = PatchOperation.addURIValues("path1",
        Lists.newArrayList(uri1, uri2));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).textValue(), uri1.toString());
    assertEquals(jsonNode.get(1).textValue(), uri2.toString());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    URI uri3 = new URI("http://localhost:8080/apps/app2");
    URI uri4 = new URI("Users/1dd6d752-1744-47e5-a4a8-5f5670aa8998");
    patchOp = PatchOperation.addURIValues(Path.fromString("path1"),
        Lists.newArrayList(uri3, uri4));
    assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    assertTrue(jsonNode.isArray());
    assertEquals(jsonNode.size(), 2);
    assertEquals(jsonNode.get(0).textValue(), uri3.toString());
    assertEquals(jsonNode.get(1).textValue(), uri4.toString());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    URI uri5 = new URI("http://localhost:8080/apps/app3");
    patchOp = PatchOperation.replace("path1", uri5);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(String.class), uri5.toString());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));

    URI uri6 = new URI("http://localhost:8080/apps/app4");
    patchOp = PatchOperation.replace(Path.fromString("path1"), uri6);
    assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    assertEquals(patchOp.getValue(String.class), uri6.toString());
    assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }
}
