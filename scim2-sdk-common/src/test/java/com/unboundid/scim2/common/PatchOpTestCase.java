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

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.BulkOpType;
import com.unboundid.scim2.common.messages.BulkOperation;
import com.unboundid.scim2.common.messages.BulkOperationResult;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.messages.PatchOpType;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.Address;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.Photo;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

/**
 * Test cases for patch operation.
 */
public class PatchOpTestCase
{
  private static final ArrayNode EMPTY_ARRAY =
          JsonUtils.getJsonNodeFactory().arrayNode();

  private static final ObjectNode EMPTY_OBJECT =
          JsonUtils.getJsonNodeFactory().objectNode();

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
        readValue("""
            {
              "schemas":[ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations":[
                {
                  "op":"add",
                  "value":{
                    "emails":[
                      {
                        "value":"babs@jensen.org",
                        "type":"home"
                      }
                    ],
                    "nickname":"Babs"
                  }
                },
                {
                  "op":"remove",
                  "path": \
                  "emails[type eq \\"work\\" and value ew \\"example.com\\"]"
                },
                {
                  "op":"remove",
                  "path":"meta"
                },
                {
                  "op":"add",
                  "path":"members",
                  "value":[
                    {
                      "display":"Babs Jensen",
                      "$ref": \
                      "https://example.com/v2/Users/2819c223...413861904646",
                      "value":"2819c223-7f76-453a-919d-413861904646"
                    },
                    {
                      "display":"James Smith",
                      "$ref": \
                      "https://example.com/v2/Users/08e1d05d...473d93df9210",
                      "value":"08e1d05d-121c-4561-8b96-473d93df9210"
                    }
                  ]
                },
                {
                  "op":"replace",
                  "path":"members2",
                  "value":[
                    {
                      "display":"Babs Jensen",
                      "$ref": \
                      "https://example.com/v2/Users/2819c223...413861904646",
                      "value":"2819c223...413861904646"
                    },
                    {
                      "display":"James Smith",
                      "$ref": \
                      "https://example.com/v2/Users/08e1d05d...473d93df9210",
                      "value":"08e1d05d...473d93df9210"
                    }
                  ]
                },
                {
                  "op":"replace",
                  "path":"addresses[type eq \\"work\\"]",
                  "value":{
                    "type":"work",
                    "streetAddress":"911 Universal City Plaza",
                    "locality":"Hollywood",
                    "region":"CA",
                    "postalCode":"91608",
                    "country":"US",
                    "formatted": \
                    "911 Universal City Plaza\\nHollywood, CA 91608 US",
                    "primary":true
                  }
                },
                {
                  "op":"replace",
                  "path":"addresses[type eq \\"home\\"].streetAddress",
                  "value":"1010 Broadway Ave"
                },
                {
                  "op":"replace",
                  "value":{
                    "emails2":[
                      {
                        "value":"bjensen@example.com",
                        "type":"work",
                        "primary":true
                      },
                      {
                        "value":"babs@jensen.org",
                        "type":"home"
                      }
                    ],
                    "nickname2":"Babs"
                  }
                },
                {
                  "op":"remove",
                  "path":"schemas[value eq \\"urn:ubid:custom:schema1\\"]"
                },
                {
                  "op":"replace",
                  "path":"schemas[value eq \\"urn:ubid:custom:schema2\\"]",
                  "value":"urn:ubid:custom:schema3"
                },
                {
                  "op":"add",
                  "path":"urn:ubid:custom:schema4:attr",
                  "value":"somevalue"
                }
              ]
            }""");

    JsonNode prePatchResource = JsonUtils.getObjectReader().readTree(
            """
            {
              "schemas":[
                "urn:ietf:params:scim:schemas:core:2.0:User",
                "urn:ubid:custom:schema1",
                "urn:ubid:custom:schema2"
              ],
              "id":"2819c223-7f76-453a-919d-413861904646",
              "userName":"bjensen@example.com",
              "nickname2":"nickname",
              "emails":[
                {
                  "value":"bjensen@example.com",
                  "type":"work",
                  "primary":true
                },
                {
                  "value":"babs@jensen.org",
                  "type":"home"
                }
              ],
              "emails2":[
                {
                  "value":"someone@somewhere.com"
                }
              ],
              "members2":[
                {
                  "value":"e9e30dba-f08f-4109-8486-d5c6a331660a",
                  "$ref":
            "https://example.com/v2/Groups/e9e30dba-f08f-4109-8486-d5c6a331660a",
                  "display":"Tour Guides"
                },
                {
                  "value":"fc348aa8-3835-40eb-a20b-c726e15c55b5",
                  "$ref":
            "https://example.com/v2/Groups/fc348aa8-3835-40eb-a20b-c726e15c55b5",
                  "display":"Employees"
                }
              ],
              "addresses":[
                {
                  "type":"work",
                  "streetAddress":"13809 Research Blvd",
                  "locality":"Austin",
                  "region":"TX",
                  "postalCode":"78750",
                  "country":"USA",
                  "formatted":"13809 Research Blvd\\nAustin, TX 78750 USA",
                  "primary":true
                },
                {
                  "type":"home",
                  "streetAddress":"456 Hollywood Blvd",
                  "locality":"Hollywood",
                  "region":"CA",
                  "postalCode":"91608",
                  "country":"USA",
                  "formatted":"456 Hollywood Blvd\\nHollywood, CA 91608 USA"
                }
              ],
              "meta":{
                "resourceType":"User",
                "created":"2010-01-23T04:56:22Z",
                "lastModified":"2011-05-13T04:42:34Z",
                "version":"W\\/\\"3694e05e9dff590\\"",
                "location":
            "https://example.com/v2/Users/2819c223-7f76-453a-919d-413861904646"
              }
            }""");

    JsonNode postPatchResource = JsonUtils.getObjectReader().
        readTree("""
            {
              "schemas":[
                "urn:ietf:params:scim:schemas:core:2.0:User",
                "urn:ubid:custom:schema3",
                "urn:ubid:custom:schema4"
              ],
              "id":"2819c223-7f76-453a-919d-413861904646",
              "userName":"bjensen@example.com",
              "nickname2":"Babs",
              "emails":[
                {
                  "value":"babs@jensen.org",
                  "type":"home"
                }
              ],
              "emails2":[
                {
                  "value":"bjensen@example.com",
                  "type":"work",
                  "primary":true
                },
                {
                  "value":"babs@jensen.org",
                  "type":"home"
                }
              ],
              "members2":[
                {
                  "display":"Babs Jensen",
                  "$ref":"https://example.com/v2/Users/2819c223...413861904646",
                  "value":"2819c223...413861904646"
                },
                {
                  "display":"James Smith",
                  "$ref":"https://example.com/v2/Users/08e1d05d...473d93df9210",
                  "value":"08e1d05d...473d93df9210"
                }
              ],
              "addresses":[
                {
                  "type":"work",
                  "streetAddress":"911 Universal City Plaza",
                  "locality":"Hollywood",
                  "region":"CA",
                  "postalCode":"91608",
                  "country":"US",
                  "formatted":
            "911 Universal City Plaza\\nHollywood, CA 91608 US",
                  "primary":true
                },
                {
                  "type":"home",
                  "streetAddress":"1010 Broadway Ave",
                  "locality":"Hollywood",
                  "region":"CA",
                  "postalCode":"91608",
                  "country":"USA",
                  "formatted":
            "456 Hollywood Blvd\\nHollywood, CA 91608 USA"
                }
              ],
              "nickname":"Babs",
              "members":[
                {
                  "display":"Babs Jensen",
                  "$ref":"https://example.com/v2/Users/2819c223...413861904646",
                  "value":"2819c223-7f76-453a-919d-413861904646"
                },
                {
                  "display":"James Smith",
                  "$ref":"https://example.com/v2/Users/08e1d05d...473d93df9210",
                  "value":"08e1d05d-121c-4561-8b96-473d93df9210"
                }
              ],
              "urn:ubid:custom:schema4":{
                "attr": "somevalue"
              }
            }""");

    GenericScimResource scimResource =
        new GenericScimResource((ObjectNode) prePatchResource);
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
   */
  @Test
  public void getTestBadPatch() throws IOException
  {
    try
    {
      JsonUtils.getObjectReader().forType(PatchRequest.class).readValue("""
              {
                "schemas":[ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
                "Operations":[
                  {
                    "op":"remove",
                    "path":"emails[type eq \\"work\\" and \
              value ew \\"example.com\\"].too.deep"
                  }
                ]
              }""");
    }
    catch (JsonMappingException e)
    {
      assertEquals(
          ((BadRequestException) e.getCause()).getScimError().getScimType(),
          BadRequestException.INVALID_PATH);
    }

    try
    {
    JsonUtils.getObjectReader().forType(PatchRequest.class).readValue("""
        {
          "schemas":[ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
          "Operations":[
            {
              "op":"remove",
              "path":"emails[type eq \\"work\\" and \
        value ew \\"example.com\\"].sub[something eq 2]"
            }
          ]
        }""");
    }
    catch (JsonMappingException e)
    {
      assertEquals(
          ((BadRequestException) e.getCause()).getScimError().getScimType(),
          BadRequestException.INVALID_PATH);
    }

    try
    {
    JsonUtils.getObjectReader().forType(PatchRequest.class).readValue("""
        {
          "schemas":[
            "urn:ietf:params:scim:api:messages:2.0:PatchOp"
          ],
          "Operations":[
            {
              "op":"add",
              "path":"emails[type eq \\"work\\" and \
        value ew \\"example.com\\"],"
              "value":"value"
            }
          ]
        }""");
    }
    catch (JsonMappingException e)
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
        List.of("value1", "value2"));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).textValue(), "value1");
    Assert.assertEquals(jsonNode.get(1).textValue(), "value2");
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addStringValues(Path.fromString("path1"),
        List.of("value1", "value2"));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).textValue(), "value1");
    Assert.assertEquals(jsonNode.get(1).textValue(), "value2");
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", "value1");
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(String.class), "value1");
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(Path.fromString("path1"), "value1");
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(String.class), "value1");
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test boolean methods.
   * @throws Exception error
   */
  @Test
  public void testBooleanPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.replace(
        Path.fromString("path1"), Boolean.TRUE);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Boolean.class), Boolean.TRUE);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), Boolean.FALSE);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Boolean.class), Boolean.FALSE);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test double methods.
   * @throws Exception error
   */
  @Test
  public void testDoublePatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addDoubleValues("path1",
        List.of(1.1, 1.2));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).doubleValue(), 1.1, 0.01);
    Assert.assertEquals(jsonNode.get(1).doubleValue(), 1.2, 0.01);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addDoubleValues(Path.fromString("path1"),
        List.of(2.1, 2.2));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).doubleValue(), 2.1, 0.01);
    Assert.assertEquals(jsonNode.get(1).doubleValue(), 2.2, 0.01);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", 734.2);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Double.class), 734.2, 0.01);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), 0.3);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Double.class), 0.3, 0.01);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test integer methods.
   * @throws Exception error
   */
  @Test
  public void testIntegerPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addIntegerValues("path1",
        List.of(1, 2));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).intValue(), 1);
    Assert.assertEquals(jsonNode.get(1).intValue(), 2);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addIntegerValues(Path.fromString("path1"),
        List.of(3, 4));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).intValue(), 3);
    Assert.assertEquals(jsonNode.get(1).intValue(), 4);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", 5);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Integer.class).intValue(), 5);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), 7);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Integer.class).intValue(), 7);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }

  /**
   * test long methods.
   * @throws Exception error
   */
  @Test
  public void testLongPatchOps() throws Exception
  {
    PatchOperation patchOp = PatchOperation.addLongValues("path1",
        List.of(1L, 2L));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).longValue(), 1);
    Assert.assertEquals(jsonNode.get(1).longValue(), 2);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.addIntegerValues(Path.fromString("path1"),
        List.of(3, 4));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).longValue(), 3);
    Assert.assertEquals(jsonNode.get(1).longValue(), 4);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace("path1", 5L);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Long.class).longValue(), 5L);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    patchOp = PatchOperation.replace(
        Path.fromString("path1"), 7L);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(Long.class).longValue(), 7L);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
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
        "path1", List.of(d1, d2));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(0)), d1);
    Assert.assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(1)), d2);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    Date d3 = new Date(89233675236L);
    Date d4 = new Date(89233675237L);
    patchOp = PatchOperation.addDateValues(
        Path.fromString("path1"), List.of(d3, d4));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(0)), d3);
    Assert.assertEquals(
        GenericScimResource.getDateFromJsonNode(jsonNode.get(1)), d4);
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    Date d5 = new Date(89233675238L);
    patchOp = PatchOperation.replace("path1", d5);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(String.class),
        GenericScimResource.getDateJsonNode(d5).textValue());
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    Date d6 = new Date(89233675240L);
    patchOp = PatchOperation.replace(
        Path.fromString("path1"), d6);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(String.class),
        GenericScimResource.getDateJsonNode(d6).textValue());
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
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
        "path1", List.of(ba1, ba2));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(0).textValue()), ba1));
    Assert.assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(1).textValue()), ba2));
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    byte[] ba3 = new byte[] {0x03, 0x04, 0x05};
    byte[] ba4 = new byte[] {0x05, 0x04, 0x03};
    patchOp = PatchOperation.addBinaryValues(
        Path.fromString("path1"), List.of(ba3, ba4));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(0).textValue()), ba3));
    Assert.assertTrue(Arrays.equals(Base64Variants.getDefaultVariant().
        decode(jsonNode.get(1).textValue()), ba4));
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    byte[] ba5 = new byte[] {0x06, 0x07, 0x08};
    patchOp = PatchOperation.replace("path1", ba5);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertTrue(Arrays.equals(patchOp.getValue(byte[].class), ba5));
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    byte[] ba6 = new byte[] {0x09, 0x0a, 0x0b};
    patchOp = PatchOperation.replace(
        Path.fromString("path1"), ba6);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertTrue(Arrays.equals(patchOp.getValue(byte[].class), ba6));
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
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
        List.of(uri1, uri2));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    JsonNode jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).textValue(), uri1.toString());
    Assert.assertEquals(jsonNode.get(1).textValue(), uri2.toString());
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    URI uri3 = new URI("http://localhost:8080/apps/app2");
    URI uri4 = new URI("Users/1dd6d752-1744-47e5-a4a8-5f5670aa8998");
    patchOp = PatchOperation.addURIValues(Path.fromString("path1"),
        List.of(uri3, uri4));
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.ADD);
    jsonNode = patchOp.getJsonNode();
    Assert.assertTrue(jsonNode.isArray());
    Assert.assertEquals(jsonNode.size(), 2);
    Assert.assertEquals(jsonNode.get(0).textValue(), uri3.toString());
    Assert.assertEquals(jsonNode.get(1).textValue(), uri4.toString());
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    URI uri5 = new URI("http://localhost:8080/apps/app3");
    patchOp = PatchOperation.replace("path1", uri5);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(String.class), uri5.toString());
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));

    URI uri6 = new URI("http://localhost:8080/apps/app4");
    patchOp = PatchOperation.replace(Path.fromString("path1"), uri6);
    Assert.assertEquals(patchOp.getOpType(), PatchOpType.REPLACE);
    Assert.assertEquals(patchOp.getValue(String.class), uri6.toString());
    Assert.assertEquals(patchOp.getPath(), Path.fromString("path1"));
  }


  /**
   * Validates empty values passed into the PatchOperation constructors. Empty
   * arrays are permitted (e.g., a 'replace' operation for an existing
   * multi-valued attribute). This test does not apply to remove operations, as
   * those do not contain a value.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testEmptyValues() throws Exception
  {
    // Null or empty values should not be permitted.
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.add("attr", null));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.replace("attr", (String) null));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.ADD, "attr", null));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.REPLACE, "attr", null));

    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.add("attr", EMPTY_OBJECT));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.replace("attr", EMPTY_OBJECT));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.ADD, "attr", EMPTY_OBJECT));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.REPLACE, "attr", EMPTY_OBJECT));

    JsonNode nullNode = NullNode.getInstance();
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.add("attr", nullNode));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.replace("attr", nullNode));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.ADD, "attr", nullNode));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.REPLACE, "attr", nullNode));

    // Empty array values should be accepted.
    PatchOperation.add("myArray", EMPTY_ARRAY);
    PatchOperation.replace("myArray", EMPTY_ARRAY);
    PatchOperation.create(PatchOpType.ADD, "myArray", EMPTY_ARRAY);
    PatchOperation.create(PatchOpType.REPLACE, "myArray", EMPTY_ARRAY);
  }

  /**
   * This test validates the behavior of patch operations when the value is an
   * empty array. When a patch operations sets the {@code value} field to an
   * empty array, this signifies that the relevant multi-valued attribute must
   * be cleared of all existing values.
   */
  @Test
  public void testApplyEmptyArray() throws Exception
  {
    PatchRequest request;
    UserResource user = new UserResource();
    user.setUserName("muhammad.ali");
    user.setEmails(new Email().setValue("muhammad.ali@example.com"));

    // "Add" an empty array. The resource should be unaffected.
    request = new PatchRequest(
        PatchOperation.add("emails", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getEmails()).hasSize(1);

    // Replace the attribute with an empty array. This should delete all email
    // values on the resource.
    request = new PatchRequest(
        PatchOperation.replace("emails", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getEmails()).isNull();

    // Apply an "empty" value to a resource that already does not have a value
    // for the requested attribute.
    assertThat(user.getAddresses()).isNull();
    request = new PatchRequest(
        PatchOperation.add("addresses", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getAddresses()).isNull();

    // Delete all attribute values when none previously exist.
    request = new PatchRequest(
        PatchOperation.replace("addresses", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getAddresses()).isNull();

    // If multiple values exist on a resource, the behavior of an 'add' should
    // be unchanged. All values should still be cleared when replaced with an
    // empty array.
    user.setPhotos(
        new Photo().setValue(URI.create("https://example.com/1.png")).setType("profile"),
        new Photo().setValue(URI.create("https://example.com/2.png")).setType("wallpaper"),
        new Photo().setValue(URI.create("https://example.com/3.png")).setType("mystery")
    );
    request = new PatchRequest(
        PatchOperation.add("photos", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getPhotos()).hasSize(3);
    request = new PatchRequest(
        PatchOperation.replace("photos", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getPhotos()).isNull();
  }


  /**
   * Similar to {@link #testApplyEmptyArray}, but involves replace operations
   * containing paths with a value selection filter. Validation for add
   * operations can be found in {@link AddOperationValueFilterTestCase}.
   */
  @Test
  public void testReplaceEmptyArrayAndValueFilter() throws Exception
  {
    PatchRequest request;
    UserResource user = new UserResource().setUserName("MuhammadAli").setEmails(
        new Email().setValue("fewCupsOfLove@example.com").setType("work"),
        new Email().setValue("oneTbspPatience@example.com").setType("work"),
        new Email().setValue("oneTspOfGenerosity@example.com").setType("home"),
        new Email().setValue("onePintOfKindness@example.com").setType("other")
    );

    // Delete the home email by replacing its value with an empty array.
    request = new PatchRequest(
        PatchOperation.replace("emails[type eq \"home\"]", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getEmails()).hasSize(3);
    assertThat(user.getEmails()).noneMatch(
        email -> "home".equalsIgnoreCase(email.getType())
    );

    // Delete both work emails.
    request = new PatchRequest(
        PatchOperation.replace("emails[type eq \"work\"]", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getEmails()).hasSize(1);
    assertThat(user.getEmails()).first().matches(
        email -> "other".equalsIgnoreCase(email.getType())
    );

    // Delete the last value. The emails attribute should be null.
    request = new PatchRequest(
        PatchOperation.replace("emails[type eq \"other\"]", EMPTY_ARRAY)
    );
    user = applyPatchRequest(user, request);
    assertThat(user.getEmails()).isNull();

    // Send a delete request that does not match any values on the resource.
    // This should result in an exception.
    UserResource newUser = new UserResource().setAddresses(
        new Address().setStreetAddress("1234 Tarrey Town Blvd.").setType("home"),
        new Address().setStreetAddress("0001 Hyrule Court").setType("castle")
    );
    PatchRequest unmatchedRequest = new PatchRequest(
        PatchOperation.replace("addresses[type eq \"work\"]", EMPTY_ARRAY)
    );
    assertThatThrownBy(() -> applyPatchRequest(newUser, unmatchedRequest))
        .isInstanceOf(BadRequestException.class)
        .satisfies(ex -> {
          var e = (BadRequestException) ex;
          assertThat(e.getMessage()).contains("does not have a value matching the filter");
        });

    // newUser should still have two addresses since nothing was removed.
    assertThat(newUser.getAddresses()).hasSize(2);

    // A BadRequestException should be thrown if a replace operation targets an
    // attribute that did not contain any initial values.
    UserResource emptyUser = new UserResource().setUserName("emptyUser");
    PatchRequest emailRequest = new PatchRequest(
        PatchOperation.replace("emails[type eq \"home\"]", EMPTY_ARRAY)
    );
    assertThatThrownBy(() -> applyPatchRequest(emptyUser, emailRequest))
        .isInstanceOf(BadRequestException.class)
        .satisfies(ex -> {
          var e = (BadRequestException) ex;
          assertThat(e.getMessage()).contains("does not have a value matching the filter");
        });
    assertThat(emptyUser.getEmails()).isNull();
  }


  /**
   * Tests methods that accept varargs on the PatchOperation class, such as
   * {@link PatchOperation#addIntegerValues(String, Integer, Integer...)}.
   * Methods with varargs parameters should behave identically to their
   * counterparts that accept a List as an input parameter.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testVarArgs() throws Exception
  {
    PatchOperation operation;
    PatchOperation operation2;

    operation = PatchOperation.addStringValues("backupEmails",
            Arrays.asList("shady@example.com", "rabbit@example.com"));
    operation2 = PatchOperation.addStringValues("backupEmails",
            "shady@example.com", "rabbit@example.com");
    assertEquals(operation, operation2);

    operation = PatchOperation.addDoubleValues("weightHistoryLbs",
            Arrays.asList(162.1, 165.0, 164.2));
    operation2 = PatchOperation.addDoubleValues("weightHistoryLbs",
            162.1, 165.0, 164.2);
    assertEquals(operation, operation2);

    operation = PatchOperation.addIntegerValues("siblingAges",
            Arrays.asList(13, 13, 24));
    operation2 = PatchOperation.addIntegerValues("siblingAges",
            13, 13, 24);
    assertEquals(operation, operation2);

    operation = PatchOperation.addLongValues("favoritePrimes",
            Arrays.asList(11L, 19L, 83L));
    operation2 = PatchOperation.addLongValues("favoritePrimes",
            11L, 19L, 83L);
    assertEquals(operation, operation2);

    operation = PatchOperation.addDateValues("commitTimestamps",
            Arrays.asList(new Date(1426901922000L), new Date(1440085206000L)));
    operation2 = PatchOperation.addDateValues("commitTimestamps",
            new Date(1426901922000L), new Date(1440085206000L));
    assertEquals(operation, operation2);

    operation = PatchOperation.addBinaryValues("easterEgg",
            Arrays.asList(
                    new byte[] { 69, 97, 115, 116, 101, 114 },
                    new byte[] { 101, 103, 103, 33 }
            ));
    operation2 = PatchOperation.addBinaryValues("easterEgg",
            new byte[] { 69, 97, 115, 116, 101, 114 },
            new byte[] { 101, 103, 103, 33 }
    );
    assertEquals(operation, operation2);

    operation = PatchOperation.addURIValues("personalSites",
            Arrays.asList(URI.create("https://example.com/"),
                          URI.create("https://example.com/cool")));
    operation2 = PatchOperation.addURIValues("personalSites",
            URI.create("https://example.com/"),
            URI.create("https://example.com/cool"));
    assertEquals(operation, operation2);
  }

  /**
   * This method applies a patch request to a UserResource object and returns
   * a new UserResource reflecting the modifications.
   */
  private static UserResource applyPatchRequest(UserResource userResource,
                                                PatchRequest request)
      throws JsonProcessingException, ScimException
  {
    GenericScimResource user = userResource.asGenericScimResource();
    request.apply(user);
    return JsonUtils.nodeToValue(user.getObjectNode(), UserResource.class);
  }

  @Test
  public void test()
  {
    var userResource = new UserResource().setUserName("name");
    var op = new BulkOperationResult(BulkOpType.POST,
        "200",
        "/Users/fa1afe1",
        userResource.asGenericScimResource().getObjectNode(),
        "originalBulkId",
        null);

    ErrorResponse response = BadRequestException.invalidSyntax(
        "Request is unparsable, syntactically incorrect, or violates the schema.")
        .getScimError();
    var error = BulkOperationResult.error(BulkOpType.POST,
        response, null);
    System.out.println(error);
  }
}
