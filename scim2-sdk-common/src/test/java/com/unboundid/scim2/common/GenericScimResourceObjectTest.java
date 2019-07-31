/*
 * Copyright 2015-2019 Ping Identity Corporation
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.DateTimeUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * Tests generic scim objects.
 */
@Test
public class GenericScimResourceObjectTest
{
  private DateFormat dateFormat =
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
        readTree("{\n" +
            "    \"externalId\": \"user:externalId\",\n" +
            "    \"id\": \"user:id\",\n" +
            "    \"meta\": {\n" +
            "        \"created\": \"2015-02-27T11:28:39.042Z\",\n" +
            "        \"lastModified\": \"2015-02-27T11:29:39.042Z\",\n" +
            "        \"locAtion\": \"http://here/user\",\n" +
            "        \"resourceType\": \"some resource type\",\n" +
            "        \"version\": \"1.0\"\n" +
            "    },\n" +
            "    \"name\": {\n" +
            "        \"first\": \"name:first\",\n" +
            "        \"last\": \"name:last\",\n" +
            "        \"middle\": \"name:middle\"\n" +
            "    },\n" +
            "    \"shoeSize\" : \"12W\",\n" +
            "    \"password\": \"user:password\",\n" +
            "    \"Schemas\": [" +
            "    \"urn:pingidentity:schemas:baseSchema\", " +
            "    \"urn:pingidentity:schemas:favoriteColor\"" +
            "    ],\n" +
            "    \"urn:pingidentity:schemas:favoriteColor\": {\n" +
            "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
            "    },\n" +
            "    \"userName\": \"user:username\"\n" +
            "}");

    GenericScimResource gso = new GenericScimResource(node);

    ScimResource cso = gso;

    Set<String> schemaSet = new HashSet<String>();
    schemaSet.add("urn:pingidentity:schemas:baseSchema");
    schemaSet.add("urn:pingidentity:schemas:favoriteColor");

    Assert.assertTrue(cso.getSchemaUrns().containsAll(schemaSet));

    Assert.assertEquals(cso.getId(), "user:id");
    Assert.assertEquals(cso.getExternalId(), "user:externalId");
    Meta meta = cso.getMeta();

    Assert.assertEquals(
        dateFormat.format(meta.getCreated().getTime()),
        "2015-02-27T11:28:39.042Z");
    Assert.assertEquals(
        dateFormat.format(meta.getLastModified().getTime()),
        "2015-02-27T11:29:39.042Z");

    ObjectNode metaNode = (ObjectNode)JsonUtils.getObjectReader().readTree(
        JsonUtils.getObjectWriter().writeValueAsString(gso.getMeta()));
    Assert.assertEquals(
        metaNode.get("created").asText(),
        "2015-02-27T11:28:39.042Z");
    Assert.assertEquals(
        metaNode.get("lastModified").asText(),
        "2015-02-27T11:29:39.042Z");

    Assert.assertEquals(meta.getLocation().toString(),
                        "http://here/user");
    Assert.assertEquals(meta.getResourceType(), "some resource type");
    Assert.assertEquals(meta.getVersion(), "1.0");

    Assert.assertEquals(
        ((GenericScimResource)cso).getObjectNode().path("shoeSize").asText(),
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
        Lists.<String>newArrayList(arrayValue1, arrayValue2)).
        getStringValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<String> list2 = gsr.addStringValues(Path.fromString(path4),
        Lists.<String>newArrayList(arrayValue3, arrayValue4)).
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
   * Test boolean methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testBooleanMethods() throws ScimException
  {
    String path1 = "path1";
    Boolean value1 = Boolean.TRUE;
    String path2 = "path2";
    Boolean value2 = Boolean.FALSE;
    String path3 = "path3";
    Boolean arrayValue1 = Boolean.TRUE;
    Boolean arrayValue2 = Boolean.FALSE;
    String path4 = "path4";
    Boolean arrayValue3 = Boolean.FALSE;
    Boolean arrayValue4 = Boolean.TRUE;

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getBooleanValue(Path.fromString(path1)), value1);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getBooleanValue(path2), value2);

    List<Boolean> list1 = gsr.addBooleanValues(path3,
        Lists.<Boolean>newArrayList(arrayValue1, arrayValue2)).
        getBooleanValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

     List<Boolean> list2 = gsr.addBooleanValues(Path.fromString(path4),
         Lists.<Boolean>newArrayList(arrayValue3, arrayValue4)).
        getBooleanValueList(path4);
    Assert.assertEquals(list2.size(), 2);
    Assert.assertTrue(list2.contains(arrayValue3));
    Assert.assertTrue(list2.contains(arrayValue4));

    Assert.assertNull(gsr.getBooleanValue("bogusPath"));
    Assert.assertNull(gsr.getBooleanValue(Path.fromString("bogusPath")));
    Assert.assertTrue(gsr.getBooleanValueList("bogusPath").isEmpty());
    Assert.assertTrue(gsr.getBooleanValueList(
        Path.fromString("bogusPath")).isEmpty());
  }

  /**
   * test double methods.
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testDoubleMethods() throws ScimException
  {
    String path1 = "path1";
    Double value1 = new Double(3.5);
    String path2 = "path2";
    Double value2 = new Double(7.3);
    String path3 = "path3";
    Double arrayValue1 = new Double(8.2);
    Double arrayValue2 = new Double(12.3);
    String path4 = "path4";
    Double arrayValue3 = new Double(2.9);
    Double arrayValue4 = new Double(1.2);

    GenericScimResource gsr = new GenericScimResource();
    Assert.assertEquals(gsr.replaceValue(path1, value1).
        getDoubleValue(Path.fromString(path1)), value1, 0.01);
    Assert.assertEquals(gsr.replaceValue(Path.fromString(path2), value2).
        getDoubleValue(path2), value2, 0.01);

    List<Double> list1 = gsr.addDoubleValues(path3,
        Lists.<Double>newArrayList(arrayValue1, arrayValue2)).
        getDoubleValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);

     List<Double> list2 = gsr.addDoubleValues(Path.fromString(path4),
         Lists.<Double>newArrayList(arrayValue3, arrayValue4)).
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
        Lists.<Integer>newArrayList(arrayValue1, arrayValue2)).
        getIntegerValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<Integer> list2 = gsr.addIntegerValues(Path.fromString(path4),
        Lists.<Integer>newArrayList(arrayValue3, arrayValue4)).
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
        Lists.<Long>newArrayList(arrayValue1, arrayValue2)).
        getLongValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<Long> list2 = gsr.addLongValues(Path.fromString(path4),
        Lists.<Long>newArrayList(arrayValue3, arrayValue4)).
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
        Lists.<Date>newArrayList(arrayValue1, arrayValue2)).
        getDateValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<Date> list2 = gsr.addDateValues(Path.fromString(path4),
        Lists.<Date>newArrayList(arrayValue3, arrayValue4)).
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
        Lists.<URI>newArrayList(arrayValue1, arrayValue2)).
        getURIValueList(Path.fromString(path3));
    Assert.assertEquals(list1.size(), 2);
    Assert.assertTrue(list1.contains(arrayValue1));
    Assert.assertTrue(list1.contains(arrayValue2));

    List<URI> list2 = gsr.addURIValues(Path.fromString(path4),
        Lists.<URI>newArrayList(arrayValue3, arrayValue4)).
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
        Lists.<byte[]>newArrayList(arrayValue1, arrayValue2)).
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
        Lists.<byte[]>newArrayList(arrayValue3, arrayValue4)).
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

  private void assertByteArrayListContainsBytes(
      List<byte[]> byteArrayList, byte[] bytes)
  {
    boolean found = false;
    for(byte[] bytesFromList : byteArrayList)
    {
      if(Arrays.equals(bytesFromList, bytes))
      {
        found = true;
        break;
      }
    }

    Assert.assertTrue(found);
  }

}
