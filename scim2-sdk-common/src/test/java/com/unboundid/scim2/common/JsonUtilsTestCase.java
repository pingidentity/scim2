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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.utils.DateTimeUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.MapperFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Test coverage for the JsonUtil methods.
 */
public class JsonUtilsTestCase
{
  /**
   * Represents a JSON Object.
   */
  public static final class ArrayValue
  {
    private Map<String, Object> fields = new HashMap<String, Object>();

    /**
     * Set a field.
     * @param key The key for the field.
     * @param o The value.
     */
    @JsonAnySetter
    public void set(String key, Object o)
    {
      fields.put(key, o);
    }

    /**
     * Retrieve all fields.
     *
     * @return all fields.
     */
    @JsonAnyGetter
    public Map<String, Object> get()
    {
      return fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      ArrayValue that = (ArrayValue) o;

      if (!fields.equals(that.fields))
      {
        return false;
      }

      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
      return fields.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder("ArrayValue{");
      sb.append("fields=").append(fields);
      sb.append('}');
      return sb.toString();
    }
  }

  private ObjectNode getTestResource() throws IOException
  {

    return (ObjectNode) JsonUtils.getObjectReader().
        readTree("{  \n" +
            "  \"string\":\"string\",\n" +
            "  \"integer\":1,\n" +
            "  \"decimal\":1.582,\n" +
            "  \"boolean\":true,\n" +
            "  \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "  \"binary\":\"YmluYXJ5\",\n" +
            "  \"null\":null,\n" +
            "  \"empty\":[  \n" +
            "\n" +
            "  ],\n" +
            "  \"array\":[  \n" +
            "    {  \n" +
            "      \"id\":\"1\",\n" +
            "      \"string\":\"string\",\n" +
            "      \"integer\":1,\n" +
            "      \"decimal\":1.582,\n" +
            "      \"boolean\":true,\n" +
            "      \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "      \"binary\":\"YmluYXJ5\",\n" +
            "      \"null\":null,\n" +
            "      \"empty\":[  \n" +
            "\n" +
            "      ],\n" +
            "      \"array\":[  \n" +
            "        {  \n" +
            "          \"id\":\"1\",\n" +
            "          \"string\":\"string\",\n" +
            "          \"integer\":1,\n" +
            "          \"decimal\":1.582,\n" +
            "          \"boolean\":true,\n" +
            "          \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "          \"binary\":\"YmluYXJ5\",\n" +
            "          \"null\":null,\n" +
            "          \"empty\":[  \n" +
            "\n" +
            "          ]\n" +
            "        },\n" +
            "        {  \n" +
            "          \"id\":\"2\",\n" +
            "          \"string\":\"string\",\n" +
            "          \"integer\":1,\n" +
            "          \"decimal\":1.582,\n" +
            "          \"boolean\":true,\n" +
            "          \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "          \"binary\":\"YmluYXJ5\",\n" +
            "          \"null\":null,\n" +
            "          \"empty\":[  \n" +
            "\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"complex\":{  \n" +
            "        \"string\":\"string\",\n" +
            "        \"integer\":1,\n" +
            "        \"decimal\":1.582,\n" +
            "        \"boolean\":true,\n" +
            "        \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "        \"binary\":\"YmluYXJ5\",\n" +
            "        \"null\":null,\n" +
            "        \"empty\":[  \n" +
            "\n" +
            "        ],\n" +
            "        \"array\":[  \n" +
            "          {  \n" +
            "            \"id\":\"1\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          },\n" +
            "          {  \n" +
            "            \"id\":\"2\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    {  \n" +
            "      \"id\":\"2\",\n" +
            "      \"string\":\"string\",\n" +
            "      \"integer\":1,\n" +
            "      \"decimal\":1.582,\n" +
            "      \"boolean\":true,\n" +
            "      \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "      \"binary\":\"YmluYXJ5\",\n" +
            "      \"null\":null,\n" +
            "      \"empty\":[  \n" +
            "\n" +
            "      ],\n" +
            "      \"array\":[  \n" +
            "        {  \n" +
            "          \"id\":\"1\",\n" +
            "          \"string\":\"string\",\n" +
            "          \"integer\":1,\n" +
            "          \"decimal\":1.582,\n" +
            "          \"boolean\":true,\n" +
            "          \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "          \"binary\":\"YmluYXJ5\",\n" +
            "          \"null\":null,\n" +
            "          \"empty\":[  \n" +
            "\n" +
            "          ]\n" +
            "        },\n" +
            "        {  \n" +
            "          \"id\":\"2\",\n" +
            "          \"string\":\"string\",\n" +
            "          \"integer\":1,\n" +
            "          \"decimal\":1.582,\n" +
            "          \"boolean\":true,\n" +
            "          \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "          \"binary\":\"YmluYXJ5\",\n" +
            "          \"null\":null,\n" +
            "          \"empty\":[  \n" +
            "\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"complex\":{  \n" +
            "        \"string\":\"string\",\n" +
            "        \"integer\":1,\n" +
            "        \"decimal\":1.582,\n" +
            "        \"boolean\":true,\n" +
            "        \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "        \"binary\":\"YmluYXJ5\",\n" +
            "        \"null\":null,\n" +
            "        \"empty\":[  \n" +
            "\n" +
            "        ],\n" +
            "        \"array\":[  \n" +
            "          {  \n" +
            "            \"id\":\"1\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          },\n" +
            "          {  \n" +
            "            \"id\":\"2\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"complex\":{  \n" +
            "    \"string\":\"string\",\n" +
            "    \"integer\":1,\n" +
            "    \"decimal\":1.582,\n" +
            "    \"boolean\":true,\n" +
            "    \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "    \"binary\":\"YmluYXJ5\",\n" +
            "    \"null\":null,\n" +
            "    \"empty\":[  \n" +
            "\n" +
            "    ],\n" +
            "    \"array\":[  \n" +
            "      {  \n" +
            "        \"id\":\"1\",\n" +
            "        \"string\":\"string\",\n" +
            "        \"integer\":1,\n" +
            "        \"decimal\":1.582,\n" +
            "        \"boolean\":true,\n" +
            "        \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "        \"binary\":\"YmluYXJ5\",\n" +
            "        \"null\":null,\n" +
            "        \"empty\":[  \n" +
            "\n" +
            "        ],\n" +
            "        \"array\":[  \n" +
            "          {  \n" +
            "            \"id\":\"1\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          },\n" +
            "          {  \n" +
            "            \"id\":\"2\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          }\n" +
            "        ],\n" +
            "        \"complex\":{  \n" +
            "          \"string\":\"string\",\n" +
            "          \"integer\":1,\n" +
            "          \"decimal\":1.582,\n" +
            "          \"boolean\":true,\n" +
            "          \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "          \"binary\":\"YmluYXJ5\",\n" +
            "          \"null\":null,\n" +
            "          \"empty\":[  \n" +
            "\n" +
            "          ],\n" +
            "          \"array\":[  \n" +
            "            {  \n" +
            "              \"id\":\"1\",\n" +
            "              \"string\":\"string\",\n" +
            "              \"integer\":1,\n" +
            "              \"decimal\":1.582,\n" +
            "              \"boolean\":true,\n" +
            "              \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "              \"binary\":\"YmluYXJ5\",\n" +
            "              \"null\":null,\n" +
            "              \"empty\":[  \n" +
            "\n" +
            "              ]\n" +
            "            },\n" +
            "            {  \n" +
            "              \"id\":\"2\",\n" +
            "              \"string\":\"string\",\n" +
            "              \"integer\":1,\n" +
            "              \"decimal\":1.582,\n" +
            "              \"boolean\":true,\n" +
            "              \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "              \"binary\":\"YmluYXJ5\",\n" +
            "              \"null\":null,\n" +
            "              \"empty\":[  \n" +
            "\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {  \n" +
            "        \"id\":\"2\",\n" +
            "        \"string\":\"string\",\n" +
            "        \"integer\":1,\n" +
            "        \"decimal\":1.582,\n" +
            "        \"boolean\":true,\n" +
            "        \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "        \"binary\":\"YmluYXJ5\",\n" +
            "        \"null\":null,\n" +
            "        \"empty\":[  \n" +
            "\n" +
            "        ],\n" +
            "        \"array\":[  \n" +
            "          {  \n" +
            "            \"id\":\"1\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          },\n" +
            "          {  \n" +
            "            \"id\":\"2\",\n" +
            "            \"string\":\"string\",\n" +
            "            \"integer\":1,\n" +
            "            \"decimal\":1.582,\n" +
            "            \"boolean\":true,\n" +
            "            \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "            \"binary\":\"YmluYXJ5\",\n" +
            "            \"null\":null,\n" +
            "            \"empty\":[  \n" +
            "\n" +
            "            ]\n" +
            "          }\n" +
            "        ],\n" +
            "        \"complex\":{  \n" +
            "          \"string\":\"string\",\n" +
            "          \"integer\":1,\n" +
            "          \"decimal\":1.582,\n" +
            "          \"boolean\":true,\n" +
            "          \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "          \"binary\":\"YmluYXJ5\",\n" +
            "          \"null\":null,\n" +
            "          \"empty\":[  \n" +
            "\n" +
            "          ],\n" +
            "          \"array\":[  \n" +
            "            {  \n" +
            "              \"id\":\"1\",\n" +
            "              \"string\":\"string\",\n" +
            "              \"integer\":1,\n" +
            "              \"decimal\":1.582,\n" +
            "              \"boolean\":true,\n" +
            "              \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "              \"binary\":\"YmluYXJ5\",\n" +
            "              \"null\":null,\n" +
            "              \"empty\":[  \n" +
            "\n" +
            "              ]\n" +
            "            },\n" +
            "            {  \n" +
            "              \"id\":\"2\",\n" +
            "              \"string\":\"string\",\n" +
            "              \"integer\":1,\n" +
            "              \"decimal\":1.582,\n" +
            "              \"boolean\":true,\n" +
            "              \"date\":\"2015-02-27T11:28:39Z\",\n" +
            "              \"binary\":\"YmluYXJ5\",\n" +
            "              \"null\":null,\n" +
            "              \"empty\":[  \n" +
            "\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}");
  }

  /**
   * Test the findMatchingPaths method.
   *
   * @throws Exception If an error occurred.
   */
  @Test
  public void testGet()
      throws Exception
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    List<JsonNode> stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("String"), gso.getObjectNode());
    assertEquals(stringResult.size(), 1);
    assertEquals(stringResult.get(0).textValue(), "string");

    List<JsonNode> intResult = JsonUtils.findMatchingPaths(
        Path.fromString("integeR"), gso.getObjectNode());
    assertEquals(intResult.size(), 1);
    assertEquals(intResult.get(0).intValue(), 1);

    List<JsonNode> decimalResult = JsonUtils.findMatchingPaths(
        Path.fromString("deCimal"), gso.getObjectNode());
    assertEquals(decimalResult.size(), 1);
    assertEquals(decimalResult.get(0).doubleValue(), 1.582);

    List<JsonNode> booleanResult = JsonUtils.findMatchingPaths(
        Path.fromString("boolean"), gso.getObjectNode());
    assertEquals(booleanResult.size(), 1);
    assertEquals(booleanResult.get(0).booleanValue(), true);

    List<JsonNode> dateResult = JsonUtils.findMatchingPaths(
        Path.fromString("date"), gso.getObjectNode());
    assertEquals(dateResult.size(), 1);
    assertEquals(
        DateTimeUtils.parse(dateResult.get(0).textValue()),
        DateTimeUtils.parse("2015-02-27T11:28:39Z"));


    List<JsonNode> binaryResult = JsonUtils.findMatchingPaths(
        Path.fromString("binary"), gso.getObjectNode());
    assertEquals(binaryResult.size(), 1);
    assertEquals(binaryResult.get(0).binaryValue(), "binary".getBytes());
  }

  /**
   * Test the findMatchingPaths method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testGetDeepNested()
      throws ScimException, IOException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    List<JsonNode> stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex.array.string"), gso.getObjectNode());
    assertEquals(stringResult.size(), 4);
    assertEquals(stringResult.get(0).textValue(), "string");

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array[id eq \"1\"].complex.array.string"),
        gso.getObjectNode());
    assertEquals(stringResult.size(), 2);
    assertEquals(stringResult.get(0).textValue(), "string");

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex.array[id eq \"1\"].string"),
        gso.getObjectNode());
    assertEquals(stringResult.size(), 2);
    assertEquals(stringResult.get(0).textValue(), "string");

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array[id eq \"1\"].complex.arRay[id eq \"1\"].stRing"),
        gso.getObjectNode());
    assertEquals(stringResult.size(), 1);
    assertEquals(stringResult.get(0).textValue(), "string");

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.array.string"), gso.getObjectNode());
    assertEquals(stringResult.size(), 4);
    assertEquals(stringResult.get(0).textValue(), "string");

    List<JsonNode> mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode());
    assertEquals(mapResult.size(), 2);
    assertEquals(mapResult.get(0).size(), 9);

  }

  /**
   * Test the findMatchingPaths method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testGetComplex()
      throws ScimException, IOException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    List<JsonNode> mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode());
    assertEquals(mapResult.size(), 2);
    assertEquals(mapResult.get(0).size(), 9);

    mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("aRray[ID eq \"2\"].cOmplex"), gso.getObjectNode());
    assertEquals(mapResult.size(), 1);
    assertEquals(mapResult.get(0).size(), 9);
  }

  /**
   * Test the findMatchingPaths method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testGetArray()
      throws ScimException, IOException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    List<JsonNode> mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode());
    assertEquals(mapResult.size(), 1);
    assertEquals(mapResult.get(0).size(), 2);
    assertEquals(mapResult.get(0).get(0).size(), 11);

    mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("complex.array[id eq \"2\"]"), gso.getObjectNode());
    assertEquals(mapResult.size(), 1);
    assertEquals(mapResult.get(0).size(), 1);
    assertEquals(mapResult.get(0).get(0).size(), 11);
  }


  /**
   * Test the findMatchingPaths method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testRemoveDeepNested()
      throws ScimException, IOException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    boolean removed = gso.removeValues("array.complex.array.string");
    assertEquals(removed, true);

    List<JsonNode> stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex.array.string"), gso.getObjectNode());
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("array[id eq \"1\"].complex.array.string");
    assertEquals(removed, true);

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array[id eq \"1\"].complex.array.string"),
        gso.getObjectNode());
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("array.complex.array[id eq \"1\"].string");
    assertEquals(removed, true);

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex.array[id eq \"1\"].string"),
        gso.getObjectNode());
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues(
        "array[id eq \"1\"].complex.array[id eq \"1\"].string");
    assertEquals(removed, true);

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("array[id eq \"1\"].complex.array[id eq \"1\"].string"),
        gso.getObjectNode());
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("complex.array.array.string");
    assertEquals(removed, true);

    stringResult = JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.array.string"), gso.getObjectNode());
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("array.complex");
    assertEquals(removed, true);

    List<JsonNode> mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode());
    assertEquals(mapResult.size(), 0);

    removed = gso.removeValues("array");
    assertEquals(removed, true);

    mapResult = JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode());
    assertEquals(mapResult.size(), 0);
  }


  /**
   * Test the add and replace value method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testAddReplaceRoot() throws IOException, ScimException
  {
    ObjectNode resource = (ObjectNode) JsonUtils.getObjectReader().
        readTree("{\n" +
            "    \"old\" : \"old\",\n" +
            "    \"existing\" : \"existing\",\n" +
            "    \"null\" : null,\n" +
            "    \"empty\" : [],\n" +
            "    \"array\": [\n" +
            "      {\n" +
            "        \"value\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"complex\": {\n" +
            "      \"old\" : \"old\",\n" +
            "      \"existing\" : \"existing\",\n" +
            "      \"null\" : null,\n" +
            "      \"empty\" : [],\n" +
            "      \"array\": [\n" +
            "        {\n" +
            "          \"value\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"value\": 2\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "}");

    JsonNode value = JsonUtils.getObjectReader().
        readTree("{\n" +
            "    \"new\" : \"new\",\n" +
            "    \"existing\" : \"newValue\",\n" +
            "    \"null\" : \"newValue\",\n" +
            "    \"empty\" : \"newValue\",\n" +
            "    \"array\": [\n" +
            "      {\n" +
            "        \"value\": 2\n" +
            "      }\n" +
            "    ],\n" +
            "    \"complex\": {\n" +
            "      \"new\" : \"new\",\n" +
            "      \"existing\" : \"newValue\",\n" +
            "      \"null\" : \"newValue\",\n" +
            "      \"empty\" : \"newValue\",\n" +
            "      \"array\": [\n" +
            "        {\n" +
            "          \"value\": 3\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "}");

    JsonNode expectedAddResult = JsonUtils.getObjectReader().
        readTree("{\n" +
            "    \"old\" : \"old\",\n" +
            "    \"new\" : \"new\",\n" +
            "    \"existing\" : \"newValue\",\n" +
            "    \"null\" : \"newValue\",\n" +
            "    \"empty\" : \"newValue\",\n" +
            "    \"array\": [\n" +
            "      {\n" +
            "        \"value\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"value\": 2\n" +
            "      }\n" +
            "    ],\n" +
            "    \"complex\": {\n" +
            "      \"old\" : \"old\",\n" +
            "      \"new\" : \"new\",\n" +
            "      \"existing\" : \"newValue\",\n" +
            "      \"null\" : \"newValue\",\n" +
            "      \"empty\" : \"newValue\",\n" +
            "      \"array\": [\n" +
            "        {\n" +
            "          \"value\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"value\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"value\": 3\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "}");

    JsonUtils.addValue(Path.root(), resource, value);

    assertEquals(resource, expectedAddResult);

    JsonNode expectedReplaceResult = JsonUtils.getObjectReader().
        readTree("{\n" +
            "    \"old\" : \"old\",\n" +
            "    \"new\" : \"new\",\n" +
            "    \"existing\" : \"newValue\",\n" +
            "    \"null\" : \"newValue\",\n" +
            "    \"empty\" : \"newValue\",\n" +
            "    \"array\": [\n" +
            "      {\n" +
            "        \"value\": 2\n" +
            "      }\n" +
            "    ],\n" +
            "    \"complex\": {\n" +
            "      \"old\" : \"old\",\n" +
            "      \"new\" : \"new\",\n" +
            "      \"existing\" : \"newValue\",\n" +
            "      \"null\" : \"newValue\",\n" +
            "      \"empty\" : \"newValue\",\n" +
            "      \"array\": [\n" +
            "        {\n" +
            "          \"value\": 3\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "}");

    JsonUtils.replaceValue(Path.root(), resource, value);

    assertEquals(resource, expectedReplaceResult);
  }


  /**
   * Test the set value method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testSetSingle() throws IOException, ScimException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    gso.replaceValue("string", JsonUtils.valueToNode("new"));

    assertEquals(JsonUtils.nodeToValue(
        JsonUtils.findMatchingPaths(Path.fromString("string"),
            gso.getObjectNode()).get(0), String.class), "new");

    gso.replaceValue("array.string", JsonUtils.valueToNode("new"));
    gso.replaceValue("complex.array.array[id eq \"2\"].string",
        JsonUtils.valueToNode("new"));

    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array.string"), gso.getObjectNode()).get(0),
        String.class), "new");
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array.string"), gso.getObjectNode()).get(1),
        String.class), "new");
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.array.string"),
        gso.getObjectNode()).get(0), String.class), "string");
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.array.string"),
        gso.getObjectNode()).get(1), String.class), "new");
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.array.string"),
        gso.getObjectNode()).get(2), String.class), "string");
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.array.string"),
        gso.getObjectNode()).get(3), String.class), "new");

    gso.replaceValue(Path.root("urn:some:extension").attribute("attribute"),
        JsonUtils.valueToNode("extensionValue"));
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.root("urn:some:extension").attribute("attribute"),
        gso.getObjectNode()).get(0), String.class), "extensionValue");
  }


  /**
   * Test the set value method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testSetComplex() throws IOException, ScimException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    ArrayValue value0 = JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode()).get(0),
        ArrayValue.class);
    value0.set("version", "new");
    ArrayValue value1 = JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode()).get(1),
        ArrayValue.class);
    value1.set("version", "new");

    ArrayValue meta = new ArrayValue();
    meta.set("version", "new");
    gso.replaceValue("array.complex", JsonUtils.valueToNode(meta));

    // Sub-attributes should be merged.
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode()).get(0),
        ArrayValue.class), value0);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array.complex"), gso.getObjectNode()).get(1),
        ArrayValue.class), value1);

    gso.replaceValue("complex.array[id eq \"2\"].complex",
        JsonUtils.valueToNode(meta));

    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.complex"), gso.getObjectNode()).get(0),
        ArrayValue.class), value0);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array.complex"), gso.getObjectNode()).get(1),
        ArrayValue.class), value1);

    gso.replaceValue(Path.root("urn:some:extension"),
        JsonUtils.valueToNode(meta));
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.root("urn:some:extension"), gso.getObjectNode()).get(0),
        ArrayValue.class), meta);
  }


  /**
   * Test the set value method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testSetArray() throws IOException, ScimException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    ArrayValue meta1 = new ArrayValue();
    meta1.set("version", "1");
    ArrayValue meta2 = new ArrayValue();
    meta2.set("version", "2");
    ArrayValue[] values = new ArrayValue[] { meta1, meta2 };

    gso.replaceValue("array",
        JsonUtils.valueToNode(new ArrayValue[]{meta1, meta2}));
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode()).get(0),
        ArrayValue[].class), values);

    ArrayValue[] value1 = JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
            Path.fromString("complex.array[id eq \"2\"]"),
            gso.getObjectNode()).get(0), ArrayValue[].class);
    value1[0].set("version", "1");

    gso.replaceValue("complex.array[id eq \"2\"]",
        JsonUtils.valueToNode(meta1));

    // The sub-attributes of the second value where id eq 2 should have been
    // merged
    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[0], value1[0]);
    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[0], value1[0]);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[1], value1[0]);

    gso.replaceValue(Path.root("urn:some:extension").attribute("attribute"),
        JsonUtils.valueToNode(new ArrayValue[]{meta1, meta2}));
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
            Path.root("urn:some:extension").attribute("attribute"),
            gso.getObjectNode()).get(0), ArrayValue[].class), values);
  }


  /**
   * Test the add value method.
   *
   * @throws ScimException If an error occurred.
   * @throws IOException If an error occurred.
   */
  @Test
  public void testAddArray() throws IOException, ScimException
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    ArrayValue meta1 = new ArrayValue();
    meta1.set("version", "1");
    ArrayValue meta2 = new ArrayValue();
    meta2.set("version", "2");
    ArrayValue[] metas = new ArrayValue[] { meta1, meta2 };
    gso.addValues("array", (ArrayNode) JsonUtils.valueToNode(metas));

    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[0], meta1);
    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[1], meta2);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[2], meta1);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[3], meta2);

    gso.addValues("complex.array[id eq \"2\"]",
        (ArrayNode) JsonUtils.valueToNode(metas));

    // There should now be 4 values. The original values where id is 1 and 2 as
    // well as the two new meta values.
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class).length, 4);
    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[0], meta1);
    assertNotEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[1], meta2);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
        ArrayValue[].class)[2], meta1);
    assertEquals(JsonUtils.nodeToValue(JsonUtils.findMatchingPaths(
        Path.fromString("complex.array"), gso.getObjectNode()).get(0),
            ArrayValue[].class)[3],
        meta2);
  }

  /**
   * Tests that the pathExists method works in a variety of situations.
   *
   * @param jsonString the string representation of the json to check.
   * @param path the path to check the existence of.
   * @param shouldExist true if it should exist, false if not.
   * @throws Exception thrown in case of an error.
   */
  @Test(dataProvider = "pathExistsDataProvider")
  public void testPathExists(String jsonString, Path path, boolean shouldExist)
      throws Exception
  {
    GenericScimResource resource =
        JsonUtils.getObjectReader().forType(
            GenericScimResource.class).readValue(jsonString);
    Assert.assertEquals(
        JsonUtils.pathExists(path, resource.getObjectNode()), shouldExist);
  }

    /**
     * Data provider for pathExists tests.
     *
     * @return an array of array of Objects to use for the test.
     * @throws Exception can be thrown by Path/Filter classes
     */
  @DataProvider(name = "pathExistsDataProvider")
  public Object[][] getPathExistsParams() throws Exception
  {
    String jsonString =
        "{  \n" +
        "   \"list\":[  \n" +
        "      {  \n" +
        "         \"id\":1,\n" +
        "         \"address\":{  \n" +
        "            \"l1\":\"Id 1, Line 1\",\n" +
        "            \"l2\":\"Id 1, Line 2\",\n" +
        "            \"l3\":\"Id 1, Line 3\"\n" +
        "         }\n" +
        "      },\n" +
        "      {  \n" +
        "         \"id\":2,\n" +
        "         \"address\":{  \n" +
        "            \"l1\":\"Id 2, Line 1\",\n" +
        "            \"l2\":\"Id 2, Line 2\",\n" +
        "            \"l3\":\"Id 2, Line 3\"\n" +
        "         }\n" +
        "      },\n" +
        "      {  \n" +
        "         \"id\":3,\n" +
        "         \"address\":{  \n" +
        "            \"l1\":\"Id 3, Line 1\", \n" +
        "            \"l2\":\"Id 3, Line 2\", \n" +
        "            \"nullValue\":null \n " +
        "         }\n" +
        "      }\n" +
        "   ],\n" +
        "   \"simpleString\":\"present\", \n" +
        "   \"nullValue\":null, \n" +
        "   \"singleComplex\":{ \n" +
        "         \"id\":3,\n" +
        "         \"address\":{  \n" +
        "            \"l1\":\"Id 3, Line 1\", \n " +
        "            \"l2\":\"Id 3, Line 2\", \n " +
        "            \"nullValue\":null \n " +
        "         }\n" +
        "      }\n" +
        "}";

    return new Object[][] {
        {
            "{}", Path.root().attribute("simpleString"), false
        },
        {
            jsonString, Path.root().attribute("simpleString"), true
        },
        {
            jsonString, Path.root().attribute("nullValue"), true
        },
        {
            jsonString,
            Path.root().attribute("singleComplex").attribute("address").
                attribute("l1"), true
        },
        {
            jsonString,
            Path.root().attribute("singleComplex").attribute("address").
                attribute("l3"), false
        },
        {
            jsonString,
            Path.root().attribute("missing"), false
        },
        {
            jsonString,
            Path.root().attribute("singleComplex").attribute("address").
                attribute("nullValue"),
            true
        },
        {
            jsonString,
            Path.root().attribute("list", Filter.eq("id", 2)), true
        },
        {
            jsonString,
            Path.root().attribute("list", Filter.eq("id", "5")), false
        },
        {
            jsonString,
            Path.root().attribute("list", Filter.eq("id", 2)).
                attribute("address").attribute("l2"),
            true
        },
        {
            jsonString,
            Path.root().attribute("list", Filter.eq("id", 3)).
                attribute("address").attribute("l3"),
            false
        },
        {
            jsonString,
            Path.root().attribute(
                "list", Filter.eq("id", 3)).attribute("address").
                attribute("nullValue"),
            true
        }
    };
  }

  /**
   * Test that setting a custom object mapper factory allows for custom
   * options such as setting fail on unknown properties deserialization option
   * to false (defaults to true).
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testCustomMapper() throws Exception
  {
    MapperFactory mapperFactory = new MapperFactory();
    mapperFactory.setDeserializationCustomFeatures(
        ImmutableMap.<DeserializationFeature, Boolean>builder().
            put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE).build());
    JsonUtils.setCustomMapperFactory(mapperFactory);

    String jsonNameString =
        "{" +
        "\"familyName\":\"Smith\"," +
        "\"givenName\":\"Bob\"," +
        "\"middleName\":\"X\"," +
        "\"bogusField\":\"bogusValue\"" +
        "}";

    Name name = JsonUtils.getObjectReader().
        forType(Name.class).readValue(jsonNameString);

    Assert.assertEquals(name.getFamilyName(), "Smith");
    Assert.assertEquals(name.getGivenName(), "Bob");
    Assert.assertEquals(name.getMiddleName(), "X");
  }

  /**
   * Test that the SCIM 2 SDK ObjectMapper ignores null map values.
   */
  @Test
  public void testNullMapValue()
  {
    Map<String, String> map = new HashMap<>();
    map.put("hasValue", "value1");
    map.put("isNull", null);
    ObjectNode objectNode = JsonUtils.valueToNode(map);

    Assert.assertFalse(objectNode.path("hasValue").isMissingNode());
    Assert.assertEquals(objectNode.path("hasValue").textValue(), "value1");
    Assert.assertTrue(objectNode.path("isNull").isMissingNode());
  }
}
