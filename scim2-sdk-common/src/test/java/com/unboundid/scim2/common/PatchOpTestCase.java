/*
 * Copyright 2015 UnboundID Corp.
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

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
    PatchRequest patchOp = SchemaUtils.createSCIMCompatibleMapper().
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
            "    }\n" +
            "  ]\n" +
            "}", PatchRequest.class);

    JsonNode prePatchResource = SchemaUtils.createSCIMCompatibleMapper().
        readTree("{  \n" +
            "  \"schemas\":[  \n" +
            "    \"urn:ietf:params:scim:schemas:core:2.0:User\"\n" +
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

    JsonNode postPatchResource = SchemaUtils.createSCIMCompatibleMapper().
        readTree("{  \n" +
            "  \"schemas\":[  \n" +
            "    \"urn:ietf:params:scim:schemas:core:2.0:User\"\n" +
            "  ],\n" +
            "  \"id\":\"2819c223-7f76-453a-919d-413861904646\",\n" +
            "  \"userName\":\"bjensen@example.com\",\n" +
            "  \"nickname2\":\"Babs\",\n" +
            "  \"emails\":[  \n" +
            "    {  \n" +
            "      \"value\":\"babs@jensen.org\",\n" +
            "      \"type\":\"home\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"emails2\":[  \n" +
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
            "  \"members2\":[  \n" +
            "    {  \n" +
            "      \"display\":\"Babs Jensen\",\n" +
            "      \"$ref\":\"https://example.com/v2/Users/2819c223..." +
            "413861904646\",\n" +
            "      \"value\":\"2819c223...413861904646\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"display\":\"James Smith\",\n" +
            "      \"$ref\":\"https://example.com/v2/Users/08e1d05d..." +
            "473d93df9210\",\n" +
            "      \"value\":\"08e1d05d...473d93df9210\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"addresses\":[  \n" +
            "    {  \n" +
            "      \"type\":\"work\",\n" +
            "      \"streetAddress\":\"911 Universal City Plaza\",\n" +
            "      \"locality\":\"Hollywood\",\n" +
            "      \"region\":\"CA\",\n" +
            "      \"postalCode\":\"91608\",\n" +
            "      \"country\":\"US\",\n" +
            "      \"formatted\":\"911 Universal City Plaza\\nHollywood, " +
            "CA 91608 US\",\n" +
            "      \"primary\":true\n" +
            "    },\n" +
            "    {  \n" +
            "      \"type\":\"home\",\n" +
            "      \"streetAddress\":\"1010 Broadway Ave\",\n" +
            "      \"locality\":\"Hollywood\",\n" +
            "      \"region\":\"CA\",\n" +
            "      \"postalCode\":\"91608\",\n" +
            "      \"country\":\"USA\",\n" +
            "      \"formatted\":\"456 Hollywood Blvd\\nHollywood, " +
            "CA 91608 USA\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"nickname\":\"Babs\",\n" +
            "  \"members\":[  \n" +
            "    {  \n" +
            "      \"display\":\"Babs Jensen\",\n" +
            "      \"$ref\":\"https://example.com/v2/Users/2819c223..." +
            "413861904646\",\n" +
            "      \"value\":\"2819c223-7f76-453a-919d-413861904646\"\n" +
            "    },\n" +
            "    {  \n" +
            "      \"display\":\"James Smith\",\n" +
            "      \"$ref\":\"https://example.com/v2/Users/08e1d05d..." +
            "473d93df9210\",\n" +
            "      \"value\":\"08e1d05d-121c-4561-8b96-473d93df9210\"\n" +
            "    }\n" +
            "  ]\n" +
            "}");

    GenericScimResource scimResource =
        new GenericScimResource((ObjectNode)prePatchResource);
    patchOp.apply(scimResource);
    assertEquals(scimResource.getObjectNode(), postPatchResource);

    PatchRequest constructed = new PatchRequest(patchOp.getOperations());

    assertEquals(constructed, patchOp);

    String serialized = SchemaUtils.createSCIMCompatibleMapper().
        writeValueAsString(constructed);
    assertEquals(SchemaUtils.createSCIMCompatibleMapper().readValue(
        serialized, PatchRequest.class), constructed);

  }

}
