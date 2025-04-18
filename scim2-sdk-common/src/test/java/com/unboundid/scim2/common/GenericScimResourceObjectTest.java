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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.DateTimeUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Tests generic scim objects.
 */
@Test
public class GenericScimResourceObjectTest
{
  private final DateFormat dateFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  /**
   * Constructor.  Sets up the dateFormat.
   */
  public GenericScimResourceObjectTest()
  {
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  /**
   * Tests parsing a json string into a GenericScimObject.
   *
   * @throws Exception in event of an error.
   */
  @Test
  public void testBasicParsing() throws Exception
  {
    ObjectNode node = (ObjectNode) JsonUtils.getObjectReader().
        readTree("""
            {
                "externalId": "user:externalId",
                "id": "user:id",
                "meta": {
                    "created": "2015-02-27T11:28:39.042Z",
                    "lastModified": "2015-02-27T11:29:39.042Z",
                    "locAtion": "https://here/user",
                    "resourceType": "some resource type",
                    "version": "1.0"
                },
                "name": {
                    "first": "name:first",
                    "last": "name:last",
                    "middle": "name:middle"
                },
                "shoeSize" : "12W",
                "password": "user:password",
                "Schemas": [
                    "urn:pingidentity:schemas:baseSchema",
                    "urn:pingidentity:schemas:favoriteColor"
                ],
                "urn:pingidentity:schemas:favoriteColor": {
                    "favoriteColor": "extension:favoritecolor"
                },
                "userName": "user:username"
            }""");

    GenericScimResource gso = new GenericScimResource(node);

    ScimResource cso = gso;

    Set<String> schemaSet = new HashSet<>();
    schemaSet.add("urn:pingidentity:schemas:baseSchema");
    schemaSet.add("urn:pingidentity:schemas:favoriteColor");

    Assert.assertTrue(cso.getSchemaUrns().containsAll(schemaSet));

    Assert.assertEquals(cso.getId(), "user:id");
    Assert.assertEquals(cso.getExternalId(), "user:externalId");
    Meta meta = cso.getMeta();
    Assert.assertNotNull(meta);

    Assert.assertEquals(
        dateFormat.format(meta.getCreated().getTime()),
        "2015-02-27T11:28:39.042Z");
    Assert.assertEquals(
        dateFormat.format(meta.getLastModified().getTime()),
        "2015-02-27T11:29:39.042Z");

    ObjectNode metaNode = (ObjectNode) JsonUtils.getObjectReader().readTree(
        JsonUtils.getObjectWriter().writeValueAsString(gso.getMeta()));
    Assert.assertEquals(
        metaNode.get("created").asText(),
        "2015-02-27T11:28:39.042Z");
    Assert.assertEquals(
        metaNode.get("lastModified").asText(),
        "2015-02-27T11:29:39.042Z");

    Assert.assertEquals(meta.getLocation().toString(),
                        "https://here/user");
    Assert.assertEquals(meta.getResourceType(), "some resource type");
    Assert.assertEquals(meta.getVersion(), "1.0");

    Assert.assertEquals(
        ((GenericScimResource) cso).getObjectNode().path("shoeSize").asText(),
        "12W");
  }

  /**
   * Test string methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testStringMethods() throws ScimException
  {
    String path1 = "path1";
    String value1 = "value1";
    String path2 = "path2";
    String value2 = "value2";
    String path3 = "path3";
    String arrayValue1 = "arrayValue1";
    String arrayValue2 = "arrayValue2";
    String path4 = "path4";
    String arrayValue3 = "arrayValue3";
    String arrayValue4 = "arrayValue4";

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getStringValue(Path.fromString(path1)), value1);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getStringValue(path2), value2);

    List<String> list1 = gsr.addStringValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getStringValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<String> list2 = gsr.addStringValues(Path.fromString(path4),
        List.of(arrayValue3, arrayValue4)).
        getStringValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    Assert.assertTrue(list2.contains(arrayValue3));
    Assert.assertTrue(list2.contains(arrayValue4));

    Assert.assertNull(gsr.getStringValue("bogusPath"));
    Assert.assertNull(gsr.getStringValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getStringValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getStringValueList(
        Path.fromString("bogusPath")).isEmpty());
  }


  /**
   * Test {@link GenericScimResource#setSchemaUrns}.
   */
  @Test
  public void testGenericScimResourceSchemaUrns()
  {
    GenericScimResource genericObject = new GenericScimResource();
    GenericScimResource genericObject2 = new GenericScimResource();

    // Set a single value.
    List<String> singleUrn = List.of("urn:pingidentity:specialObject");
    genericObject.setSchemaUrns(singleUrn);
    genericObject2.setSchemaUrns("urn:pingidentity:specialObject");
    assertThat(genericObject).isEqualTo(genericObject2);

    // Set two values.
    List<String> twoUrns = List.of(
        "urn:pingidentity:proprietaryObject",
        "urn:pingidentity:specialObject"
    );
    genericObject.setSchemaUrns(twoUrns);
    genericObject2.setSchemaUrns("urn:pingidentity:proprietaryObject",
        "urn:pingidentity:specialObject"
    );
    assertThat(genericObject).isEqualTo(genericObject2);

    // The first parameter of the method should not accept null.
    assertThatThrownBy(() ->
        genericObject.setSchemaUrns(null, "urn:pingidentity:specialObject"))
        .isInstanceOf(NullPointerException.class);

    // Null arguments in the varargs method should be ignored.
    genericObject.setSchemaUrns(
        "urn:pingidentity:proprietaryObject", null, null);
    assertThat(genericObject.getSchemaUrns().size()).isEqualTo(1);
  }

  /**
   * Test boolean methods.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testBooleanMethods() throws ScimException
  {
    GenericScimResource gsr = new GenericScimResource()
        .replaceValue("liftsWeights", Boolean.TRUE)
        .replaceValue("hasMuscle", Boolean.FALSE);

    // Ensure that fetching a non-existent value returns null.
    assertThat(gsr.getBooleanValue("bogusPath")).isNull();
    assertThat(gsr.getBooleanValue(Path.fromString("bogusPath"))).isNull();

    // Update an existing value from false to true.
    assertThat(gsr.getBooleanValue("hasMuscle")).isFalse();
    gsr.replaceValue("hasMuscle", true);
    assertThat(gsr.getBooleanValue("hasMuscle")).isTrue();

    // Update an existing value from true to false.
    assertThat(gsr.getBooleanValue("liftsWeights")).isTrue();
    gsr.replaceValue("liftsWeights", false);
    assertThat(gsr.getBooleanValue("liftsWeights")).isFalse();

    // Set a new 'true' value that was not on the resource before.
    assertThat(gsr.getBooleanValue("mfaEnabled")).isNull();
    gsr.replaceValue("mfaEnabled", true);
    assertThat(gsr.getBooleanValue("mfaEnabled")).isTrue();

    // Set a new 'false' value that was not on the resource before.
    assertThat(gsr.getBooleanValue("enabled")).isNull();
    gsr.replaceValue("enabled", false);
    assertThat(gsr.getBooleanValue("enabled")).isFalse();
  }

  /**
   * test double methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testDoubleMethods() throws ScimException
  {
    String path1 = "path1";
    double value1 = 3.5;
    String path2 = "path2";
    double value2 = 7.3;
    String path3 = "path3";
    Double arrayValue1 = 8.2;
    Double arrayValue2 = 12.3;
    String path4 = "path4";
    Double arrayValue3 = 2.9;
    Double arrayValue4 = 1.2;

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getDoubleValue(Path.fromString(path1)), value1, 0.01);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getDoubleValue(path2), value2, 0.01);

    List<Double> list1 = gsr.addDoubleValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getDoubleValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);

     List<Double> list2 = gsr.addDoubleValues(Path.fromString(path4),
         List.of(arrayValue3, arrayValue4)).
        getDoubleValueList(path4);
    Assert.assertEquals(list2.size(), 2);

    Assert.assertNull(gsr.getDoubleValue("bogusPath"));
    Assert.assertNull(gsr.getDoubleValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getDoubleValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getDoubleValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * test integer methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testIntegerMethods() throws ScimException
  {
    String path1 = "path1";
    Integer value1 = 3;
    String path2 = "path2";
    Integer value2 = 2;
    String path3 = "path3";
    Integer arrayValue1 = 89;
    Integer arrayValue2 = 34;
    String path4 = "path4";
    Integer arrayValue3 = 82;
    Integer arrayValue4 = 12;

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getIntegerValue(Path.fromString(path1)), value1);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getIntegerValue(path2), value2);

    List<Integer> list1 = gsr.addIntegerValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getIntegerValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<Integer> list2 = gsr.addIntegerValues(Path.fromString(path4),
        List.of(arrayValue3, arrayValue4)).
        getIntegerValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    Assert.assertTrue(list2.contains(arrayValue3));
    Assert.assertTrue(list2.contains(arrayValue4));

    Assert.assertNull(gsr.getIntegerValue("bogusPath"));
    Assert.assertNull(gsr.getIntegerValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getIntegerValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getIntegerValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * test long methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testLongMethods() throws ScimException
  {
    String path1 = "path1";
    Long value1 = 3L;
    String path2 = "path2";
    Long value2 = 2L;
    String path3 = "path3";
    Long arrayValue1 = 89L;
    Long arrayValue2 = 34L;
    String path4 = "path4";
    Long arrayValue3 = 82L;
    Long arrayValue4 = 12L;

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getLongValue(Path.fromString(path1)), value1);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getLongValue(path2), value2);

    List<Long> list1 = gsr.addLongValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getLongValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<Long> list2 = gsr.addLongValues(Path.fromString(path4),
        List.of(arrayValue3, arrayValue4)).
        getLongValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    Assert.assertTrue(list2.contains(arrayValue3));
    Assert.assertTrue(list2.contains(arrayValue4));

    Assert.assertNull(gsr.getLongValue("bogusPath"));
    Assert.assertNull(gsr.getLongValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getLongValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getLongValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * test date methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testDateMethods() throws ScimException
  {
    String path1 = "path1";
    Date value1 = new Date(9482087542L);
    String path2 = "path2";
    Date value2 = new Date(9482087523L);
    String path3 = "path3";
    Date arrayValue1 = new Date(9482087508L);
    Date arrayValue2 = new Date(9482087574L);
    String path4 = "path4";
    Date arrayValue3 = new Date(9482087526L);
    Date arrayValue4 = new Date(9482087579L);

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getDateValue(Path.fromString(path1)), value1);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getDateValue(path2), value2);

    Assert.assertEquals(gsr.getDateValue(path1), value1);
    Assert.assertEquals(GenericScimResource.getDateFromJsonNode(gsr.getValue(path1)),
        value1);
    Assert.assertEquals(gsr.getStringValue(path1), DateTimeUtils.format(value1));

    Assert.assertEquals(gsr.getDateValue(path2), value2);
    Assert.assertEquals(GenericScimResource.getDateFromJsonNode(gsr.getValue(path2)),
        value2);
    Assert.assertEquals(gsr.getStringValue(path2), DateTimeUtils.format(value2));

    List<Date> list1 = gsr.addDateValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getDateValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<Date> list2 = gsr.addDateValues(Path.fromString(path4),
        List.of(arrayValue3, arrayValue4)).
        getDateValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    Assert.assertTrue(list2.contains(arrayValue3));
    Assert.assertTrue(list2.contains(arrayValue4));

    Assert.assertNull(gsr.getDateValue("bogusPath"));
    Assert.assertNull(gsr.getDateValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getDateValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getDateValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * test uri methods.
   * @throws Exception if an error occurs.
   */
  @Test
  public void testURIMethods() throws Exception
  {
    String path1 = "path1";
    URI value1 = new URI("http://localhost/value1");
    String path2 = "path2";
    URI value2 = new URI("http://localhost/value2");
    String path3 = "path3";
    URI arrayValue1 = new URI("http://localhost/arrayValue1");
    URI arrayValue2 = new URI("http://localhost/arrayValue2");
    String path4 = "path4";
    URI arrayValue3 = new URI("http://localhost/arrayValue3");
    URI arrayValue4 = new URI("http://localhost/arrayValue4");
    String path5 = "path5";
    URI relativeUri1 = new URI("Users/1dd6d752-1744-47e5-a4a8-5f5670aa8905");

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getURIValue(Path.fromString(path1)), value1);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getURIValue(path2), value2);

    List<URI> list1 = gsr.addURIValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getURIValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<URI> list2 = gsr.addURIValues(Path.fromString(path4),
        List.of(arrayValue3, arrayValue4)).
        getURIValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    Assert.assertTrue(list2.contains(arrayValue3));
    Assert.assertTrue(list2.contains(arrayValue4));

    Assert.assertEquals(gsr.replaceValue(path5, relativeUri1).
        getURIValue(path5), relativeUri1);

    Assert.assertNull(gsr.getURIValue("bogusPath"));
    Assert.assertNull(gsr.getURIValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getURIValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getURIValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * test binary methods.
   * @throws Exception if an error occurs.
   */
  @Test
  public void testBinaryMethods() throws Exception
  {
    String path1 = "path1";
    byte[] value1 = new byte[] { 0x02, 0x32, 0x33, 0x2e };
    String path2 = "path2";
    byte[] value2 = new byte[] { 0x0f, 0x34, 0x33, 0x1e };
    String path3 = "path3";
    byte[] arrayValue1 = new byte[] { 0x0e, 0x30, 0x1a, 0x1e };
    byte[] arrayValue2 = new byte[] { 0x0f, 0x34, 0x44, 0x0e };
    String path4 = "path4";
    byte[] arrayValue3 = new byte[] { 0x0f, 0x74, 0x33, 0x0e };
    byte[] arrayValue4 = new byte[] { 0x10, 0x34, 0x11, 0x1e };

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getBinaryValue(Path.fromString(path1)), value1);

    // Set BinaryNode directly
    Assert.assertEquals(gsr.replaceValue(path1,
        JsonUtils.getJsonNodeFactory().binaryNode(value1)).
        getBinaryValue(Path.fromString(path1)), value1);

    // Set TextNode directly
    Assert.assertEquals(gsr.replaceValue(path1,
        JsonUtils.getJsonNodeFactory().textNode(
            Base64Variants.getDefaultVariant().encode(value1))).
        getBinaryValue(Path.fromString(path1)), value1);

    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getBinaryValue(path2), value2);

    List<byte[]> list1 = gsr.addBinaryValues(path3,
        List.of(arrayValue1, arrayValue2)).
        getBinaryValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    assertByteArrayListContainsBytes(list1, arrayValue1);
    assertByteArrayListContainsBytes(list1, arrayValue2);

    // Set BinaryNode directly
    list1 = gsr.replaceValue(path3,
        JsonUtils.getJsonNodeFactory().arrayNode().
            add(JsonUtils.getJsonNodeFactory().binaryNode(arrayValue1)).
            add(JsonUtils.getJsonNodeFactory().binaryNode(arrayValue2))).
        getBinaryValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    assertByteArrayListContainsBytes(list1, arrayValue1);
    assertByteArrayListContainsBytes(list1, arrayValue2);

    // Set TextNode directly
    list1 = gsr.replaceValue(path3,
        JsonUtils.getJsonNodeFactory().arrayNode().
            add(JsonUtils.getJsonNodeFactory().textNode(
                Base64Variants.getDefaultVariant().encode(arrayValue1))).
            add(JsonUtils.getJsonNodeFactory().textNode(
                Base64Variants.getDefaultVariant().encode(arrayValue2)))).
        getBinaryValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    assertByteArrayListContainsBytes(list1, arrayValue1);
    assertByteArrayListContainsBytes(list1, arrayValue2);

    List<byte[]> list2 = gsr.addBinaryValues(Path.fromString(path4),
        List.of(arrayValue3, arrayValue4)).
        getBinaryValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    assertByteArrayListContainsBytes(list2, arrayValue3);
    assertByteArrayListContainsBytes(list2, arrayValue4);

    Assert.assertNull(gsr.getBinaryValue("bogusPath"));
    Assert.assertNull(gsr.getBinaryValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getBinaryValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getBinaryValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * Performs basic equivalency checks to validate
   * {@link GenericScimResource#equals(Object)}.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @SuppressWarnings("AssertBetweenInconvertibleTypes")
  @Test
  public void testEquals() throws Exception
  {
    GenericScimResource resource = new GenericScimResource();
    GenericScimResource resource2 = new GenericScimResource();

    // Basic test. Two objects with the same data should be equivalent. Check
    // equivalency twice to invoke both 'resource.equals(resource2)` and
    // 'resource2.equals(resource)'.
    resource.addStringValues("userName", Collections.singletonList("bob"));
    resource2.addStringValues("userName", Collections.singletonList("bob"));
    assertEquals(resource, resource2);
    assertEquals(resource2, resource);
    resource2.removeValues("userName");
    assertNotEquals(resource, resource2);
    assertNotEquals(resource2, resource);

    // An object should always be equal to itself.
    //noinspection EqualsWithItself
    assertEquals(resource, resource);
    //noinspection EqualsWithItself
    assertEquals(resource2, resource2);

    // Other object types should never be equivalent.
    assertNotEquals(new GenericScimResource(), new Object());
    ObjectNode newNode = JsonUtils.getJsonNodeFactory().objectNode();
    assertNotEquals(new GenericScimResource(), newNode);
    assertNotEquals(new GenericScimResource(), new UserResource());
  }

  /**
   * Tests methods that accept varargs on the GenericScimResource class, such as
   * {@link GenericScimResource#addStringValues(Path, String, String...)}.
   * Methods with varargs parameters should behave identically to their
   * counterparts that accept a List as an input parameter.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testVarArgMethods() throws Exception
  {
    GenericScimResource resource = new GenericScimResource();
    GenericScimResource resource2 = new GenericScimResource();

    resource.addStringValues("favoriteArtists", List.of("Beethoven", "Mozart"));
    resource2.addStringValues("favoriteArtists", "Beethoven", "Mozart");
    assertEquals(resource, resource2);

    resource.addDoubleValues("testScores", List.of(100.0, 98.0, 11.2));
    resource2.addDoubleValues("testScores", 100.0, 98.0, 11.2);
    assertEquals(resource, resource2);

    resource.addIntegerValues("pushUpCountHistory", List.of(11, 19, 83));
    resource2.addIntegerValues("pushUpCountHistory", 11, 19, 83);
    assertEquals(resource, resource2);

    resource.addLongValues("pullUpCountHistory", List.of(11L, 19L, 83L));
    resource2.addLongValues("pullUpCountHistory", 11L, 19L, 83L);
    assertEquals(resource, resource2);

    resource.addDateValues("flightDays", List.of(
            new Date(0L), new Date(1685499903710L)));
    resource2.addDateValues("flightDays",
            new Date(0L), new Date(1685499903710L));
    assertEquals(resource, resource2);

    resource.addBinaryValues("publicKeys",
            List.of(
                new byte[] { 87, 104, 121, 32, 100, 105, 100, 32, 121, 111, 117 },
                new byte[] { 100, 101, 99, 111, 100, 101, 32, 116, 104, 105, 115, 63 }
    ));
    resource2.addBinaryValues("publicKeys",
            new byte[] { 87, 104, 121, 32, 100, 105, 100, 32, 121, 111, 117 },
            new byte[] { 100, 101, 99, 111, 100, 101, 32, 116, 104, 105, 115, 63 }
    );
    assertEquals(resource, resource2);

    resource.addURIValues("permittedEndpoints",
            List.of(URI.create("/Users"), URI.create("/Hackers")));
    resource2.addURIValues("permittedEndpoints",
            URI.create("/Users"), URI.create("/Hackers"));
    assertEquals(resource, resource2);
  }

  /**
   * Tests the JsonNode-based methods that state the following text:
   * <pre>
   *   If the path matches multiple values (i.e., if the {@link Path} contains
   *   a filter), all paths that match will be updated.
   * </pre>
   */
  @Test
  public void testJsonMethodsWithFilter() throws Exception
  {
    // Initialize the JSON object as a GenericScimResource.
    String rawJSON = """
        {
          "emails": [
            {
              "type": "work",
              "value": "email1@example.com"
            },
            {
              "type": "work",
              "value": "email2@example.com"
            },
            {
              "value": "emailWithNoType@example.com"
            }
          ]
        }""";
    ObjectNode node = JsonUtils.getObjectReader()
        .readValue(rawJSON, ObjectNode.class);
    final GenericScimResource resource = new GenericScimResource(node);

    // Ensure the object does not yet contain the new email that will be set by
    // the 'replace' method.
    assertThat(resource.toString()).doesNotContain("newEmail@example.com");

    // Test the 'replace' method by updating the value of all emails that match
    // a filter.
    resource.replaceValue(Path.fromString("emails[type pr].value"),
        TextNode.valueOf("newEmail@example.com"));

    // Fetch the value from the ObjectNode and convert it to a list so that
    // it may be validated.
    List<?> emailList = getValueAsList(resource, "emails");

    // Iterate through each email and ensure there are two emails that have the
    // new value.
    int emailsWithExpectedValue = 0;
    for (var email : emailList)
    {
      // Convert the email into a JsonNode and fetch the value field.
      JsonNode valueNode = JsonUtils.valueToNode(email).get("value");
      assertThat(valueNode).isNotNull();
      assertThat(valueNode.isTextual()).isTrue();
      if ("newEmail@example.com".equals(valueNode.asText()))
      {
        emailsWithExpectedValue++;
      }
    }
    assertThat(emailsWithExpectedValue).isEqualTo(2);

    // Test the 'add' method by adding a new "customField" field on all paths
    // that match a filter.
    resource.addValues(Path.fromString("emails[type eq \"work\"].customField"),
        JsonUtils.getJsonNodeFactory().arrayNode().add("data"));
    emailList = getValueAsList(resource, "emails");

    int emailsWithCustomData = 0;
    for (var email : emailList)
    {
      JsonNode valueNode = JsonUtils.valueToNode(email).get("customField");
      if (valueNode != null)
      {
        emailsWithCustomData++;
      }
    }
    assertThat(emailsWithCustomData).isEqualTo(2);

    // Test the 'remove' method by removing any paths that match the filters.
    // This should leave only the "no type" email on the resource.
    resource.removeValues(Path.fromString("emails[customField pr]"));
    emailList = getValueAsList(resource, "emails");
    assertThat(emailList).hasSize(1);
    assertThat(emailList.get(0).toString())
        .contains("emailWithNoType@example.com");
  }

  /**
   * Validate the addition of new multi-valued attributes that do not yet exist
   * on a resource.
   */
  @Test
  public void testAddNewArrays() throws Exception
  {
    // Set a calendar object with a time zone for a consistent JSON output in
    // build pipelines.
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(1426883922000L);
    Date date = calendar.getTime();

    JsonNode expected = JsonUtils.getObjectReader().readTree("""
        {
          "emails": [ "email1@example.com", "email2@example.com" ],
          "aliases": [ "shady", "slim" ],
          "recentWeights": [ 104.0, 104.9 ],
          "recentHeights": [ 121.0, 120.9, 120.8 ],
          "fibonacciNumbers": [ 2, 3, 5, 8, 13 ],
          "timeTables": [ 0, 1, 2, 145 ],
          "wholeNumbers": [ 9223372036854775807 ],
          "negativeNumbers": [ -9223372036854775808 ],
          "projectInception": [ "2015-03-20T20:38:42Z" ],
          "innovationTime": [ "2015-03-20T20:38:42Z" ],
          "publicKeys": [ "aGV5" ],
          "code": [ "aGk=" ],
          "pictures": [ "https://example.com/photo.jpg" ],
          "links": [ "https://example.com" ]
        }"""
    );

    GenericScimResource gsr = new GenericScimResource()
        .addValues("emails", JsonUtils.getJsonNodeFactory().arrayNode()
            .add("email1@example.com")
            .add("email2@example.com"))
        .addStringValues("aliases", "shady", "slim")
        .addDoubleValues("recentWeights", 104.0, 104.9)
        .addDoubleValues(Path.fromString("recentHeights"), 121.0, 120.9, 120.8)
        .addIntegerValues("fibonacciNumbers", 2, 3, 5, 8, 13)
        .addIntegerValues(Path.fromString("timeTables"), 0, 1, 2, 145)
        .addLongValues("wholeNumbers", Long.MAX_VALUE)
        .addLongValues(Path.fromString("negativeNumbers"), Long.MIN_VALUE)
        .addDateValues("projectInception", date)
        .addDateValues(Path.fromString("innovationTime"), date)
        .addBinaryValues("publicKeys", new byte[] { 0x68, 0x65, 0x79 })
        .addBinaryValues(Path.fromString("code"), new byte[] { 104, 105 })
        .addURIValues("pictures", new URI("https://example.com/photo.jpg"))
        .addURIValues(Path.fromString("links"), new URI("https://example.com"));

    // Compare the serialized forms.
    assertThat(gsr.getObjectNode().toString()).isEqualTo(expected.toString());
  }

  private void assertByteArrayListContainsBytes(
      List<byte[]> byteArrayList, byte[] bytes)
  {
    boolean found = false;
    for (byte[] bytesFromList : byteArrayList)
    {
      if (Arrays.equals(bytesFromList, bytes))
      {
        found = true;
        break;
      }
    }

    Assert.assertTrue(found);
  }

  /**
   * Fetches the values of a multi-valued String attribute stored on a generic
   * SCIM resource. This method takes the array and converts it into a list
   * to simplify assertion validation.
   *
   * @param resource  The generic SCIM resource containing the attribute to
   *                  retrieve.
   * @param path      The path to the attribute.
   * @return          A list containing the values of the multi-valued
   *                  attribute.
   *
   * @throws Exception  If an error occurs while parsing the resource.
   */
  private List<?> getValueAsList(GenericScimResource resource, String path)
      throws Exception
  {
    JsonNode emailsJson = resource.getValue(path);
    List<?> emailList = JsonUtils.nodeToValue(emailsJson, List.class);
    assertThat(emailList).isNotNull();

    return emailList;
  }
}
