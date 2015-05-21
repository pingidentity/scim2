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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
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

    return (ObjectNode)SchemaUtils.createSCIMCompatibleMapper().
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
   * Test the getValues method.
   *
   * @throws Exception If an error occurred.
   */
  @Test
  public void testGet()
      throws Exception
  {
    GenericScimResource gso =
        new GenericScimResource(getTestResource());

    List<String> stringResult = gso.getValues(
        "string",
        String.class);
    assertEquals(stringResult.size(), 1);
    assertEquals(stringResult.get(0), "string");

    List<Integer> intResult = gso.getValues(
        "integer",
        Integer.class);
    assertEquals(intResult.size(), 1);
    assertEquals(intResult.get(0), Integer.valueOf(1));

    List<Double> decimalResult = gso.getValues(
        "decimal",
        Double.class);
    assertEquals(decimalResult.size(), 1);
    assertEquals(decimalResult.get(0), Double.valueOf(1.582));

    List<Boolean> booleanResult = gso.getValues(
        "boolean",
        Boolean.class);
    assertEquals(booleanResult.size(), 1);
    assertEquals(booleanResult.get(0), Boolean.valueOf(true));

    List<Date> dateResult = gso.getValues(
        "date",
        Date.class);
    assertEquals(dateResult.size(), 1);
    assertEquals(dateResult.get(0),
        ISO8601Utils.parse("2015-02-27T11:28:39Z", new ParsePosition(0)));


    List<byte[]> binaryResult = gso.getValues(
        "binary",
        byte[].class);
    assertEquals(binaryResult.size(), 1);
    assertEquals(binaryResult.get(0), "binary".getBytes());
  }

  /**
   * Test the getValues method.
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

    List<String> stringResult = gso.getValues(
        "array.complex.array.string",
        String.class);
    assertEquals(stringResult.size(), 4);
    assertEquals(stringResult.get(0), "string");

    stringResult = gso.getValues(
        "array[id eq \"1\"].complex.array.string",
        String.class);
    assertEquals(stringResult.size(), 2);
    assertEquals(stringResult.get(0), "string");

    stringResult = gso.getValues(
        "array.complex.array[id eq \"1\"].string",
        String.class);
    assertEquals(stringResult.size(), 2);
    assertEquals(stringResult.get(0), "string");

    stringResult = gso.getValues(
        "array[id eq \"1\"].complex.array[id eq \"1\"].string",
        String.class);
    assertEquals(stringResult.size(), 1);
    assertEquals(stringResult.get(0), "string");

    stringResult = gso.getValues(
        "complex.array.array.string",
        String.class);
    assertEquals(stringResult.size(), 4);
    assertEquals(stringResult.get(0), "string");

    List<Map> mapResult = gso.getValues(
        "array.complex",
        Map.class);
    assertEquals(mapResult.size(), 2);
    assertEquals(mapResult.get(0).size(), 9);

  }

  /**
   * Test the getValues method.
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

    List<Map> mapResult = gso.getValues(
        "array.complex",
        Map.class);
    assertEquals(mapResult.size(), 2);
    assertEquals(mapResult.get(0).size(), 9);

    mapResult = gso.getValues(
        "array[id eq \"2\"].complex",
        Map.class);
    assertEquals(mapResult.size(), 1);
    assertEquals(mapResult.get(0).size(), 9);
  }

  /**
   * Test the getValues method.
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

    List<Map> mapResult = gso.getValues(
        "array",
        Map.class);
    assertEquals(mapResult.size(), 2);
    assertEquals(mapResult.get(0).size(), 11);

    mapResult = gso.getValues(
        "complex.array[id eq \"2\"]",
        Map.class);
    assertEquals(mapResult.size(), 1);
    assertEquals(mapResult.get(0).size(), 11);
  }


  /**
   * Test the getValues method.
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

    int removed = gso.removeValues("array.complex.array.string");
    assertEquals(removed, 4);

    List<String> stringResult = gso.getValues(
        "array.complex.array.string",
        String.class);
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("array[id eq \"1\"].complex.array.string");
    assertEquals(removed, 2);

    stringResult = gso.getValues(
        "array[id eq \"1\"].complex.array.string",
        String.class);
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("array.complex.array[id eq \"1\"].string");
    assertEquals(removed, 2);

    stringResult = gso.getValues(
        "array.complex.array[id eq \"1\"].string",
        String.class);
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues(
        "array[id eq \"1\"].complex.array[id eq \"1\"].string");
    assertEquals(removed, 1);

    stringResult = gso.getValues(
        "array[id eq \"1\"].complex.array[id eq \"1\"].string",
        String.class);
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("complex.array.array.string");
    assertEquals(removed, 4);

    stringResult = gso.getValues(
        "complex.array.array.string",
        String.class);
    assertEquals(stringResult.size(), 0);

    gso = new GenericScimResource(getTestResource());

    removed = gso.removeValues("array.complex");
    assertEquals(removed, 2);

    List<Map> mapResult = gso.getValues(
        "array.complex",
        Map.class);
    assertEquals(mapResult.size(), 0);

    removed = gso.removeValues("array");
    assertEquals(removed, 2);

    mapResult = gso.getValues("array", Map.class);
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
    ObjectNode resource = (ObjectNode)SchemaUtils.createSCIMCompatibleMapper().
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

    JsonNode value = SchemaUtils.createSCIMCompatibleMapper().
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

    JsonNode expectedAddResult = SchemaUtils.createSCIMCompatibleMapper().
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

    JsonNode expectedReplaceResult = SchemaUtils.createSCIMCompatibleMapper().
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

    gso.setValue("string", "new");

    assertEquals(gso.getValues("string", String.class).get(0), "new");

    gso.setValue("array.string", "new");
    gso.setValue("complex.array.array[id eq \"2\"].string", "new");

    assertEquals(gso.getValues("array.string", String.class).get(0), "new");
    assertEquals(gso.getValues("array.string", String.class).get(1), "new");
    assertEquals(gso.getValues("complex.array.array.string",
            String.class).get(0), "string");
    assertEquals(gso.getValues("complex.array.array.string",
            String.class).get(1), "new");
    assertEquals(gso.getValues("complex.array.array.string",
        String.class).get(2), "string");
    assertEquals(gso.getValues("complex.array.array.string",
            String.class).get(3), "new");

    gso.setValue(Path.attribute("urn:some:extension", "attribute"),
        "extensionValue");
    assertEquals(gso.getValue(Path.attribute("urn:some:extension", "attribute"),
        String.class), "extensionValue");
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

    ArrayValue value0 = gso.getValues("array.complex", ArrayValue.class).get(0);
    value0.set("version", "new");
    ArrayValue value1 = gso.getValues("array.complex", ArrayValue.class).get(1);
    value1.set("version", "new");

    ArrayValue meta = new ArrayValue();
    meta.set("version", "new");
    gso.setValue("array.complex", meta);

    // Sub-attributes should be merged.
    assertEquals(gso.getValues("array.complex", ArrayValue.class).get(0),
        value0);
    assertEquals(gso.getValues("array.complex", ArrayValue.class).get(1),
        value1);

    gso.setValue("complex.array[id eq \"2\"].complex", meta);

    assertNotEquals(gso.getValues("complex.array.complex",
        ArrayValue.class).get(0), value0);
    assertEquals(gso.getValues("complex.array.complex",
            ArrayValue.class).get(1), value1);

    gso.setValue(Path.extension("urn:some:extension"), meta);
    assertEquals(gso.getValue(Path.extension("urn:some:extension"),
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
    List<ArrayValue> values = new ArrayList<ArrayValue>(2);
    values.add(meta1);
    values.add(meta2);

    gso.setValues("array", meta1, meta2);
    assertEquals(gso.getValues("array", ArrayValue.class), values);

    ArrayValue value1 = gso.getValue("complex.array[id eq \"2\"]",
        ArrayValue.class);
    value1.set("version", "1");

    gso.setValue("complex.array[id eq \"2\"]", meta1);

    // The sub-attributes of the second value where id eq 2 should have been
    // merged
    assertNotEquals(gso.getValues("complex.array", ArrayValue.class).get(0),
        value1);
    assertNotEquals(gso.getValues("complex.array", ArrayValue.class).get(0),
        value1);
    assertEquals(gso.getValues("complex.array", ArrayValue.class).get(1),
        value1);

    gso.setValues(Path.attribute("urn:some:extension", "attribute"),
        meta1, meta2);
    assertEquals(gso.getValues(Path.attribute("urn:some:extension",
        "attribute"), ArrayValue.class), values);
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
    List<ArrayValue> metas = new ArrayList<ArrayValue>();
    metas.add(meta1);
    metas.add(meta2);
    gso.addValues("array", metas);

    assertNotEquals(gso.getValues("array", ArrayValue.class).get(0), meta1);
    assertNotEquals(gso.getValues("array", ArrayValue.class).get(1), meta2);
    assertEquals(gso.getValues("array", ArrayValue.class).get(2), meta1);
    assertEquals(gso.getValues("array", ArrayValue.class).get(3), meta2);

    gso.addValues("complex.array[id eq \"2\"]", metas);

    // There should now be 4 values. The original values where id is 1 and 2 as
    // well as the two new meta values.
    assertEquals(gso.getValues("complex.array", ArrayValue.class).size(), 4);
    assertNotEquals(gso.getValues("complex.array", ArrayValue.class).get(0),
        meta1);
    assertNotEquals(gso.getValues("complex.array", ArrayValue.class).get(1),
        meta2);
    assertEquals(gso.getValues("complex.array", ArrayValue.class).get(2),
        meta1);
    assertEquals(gso.getValues("complex.array", ArrayValue.class).get(3),
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
    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
    GenericScimResource resource =
        mapper.readValue(jsonString, GenericScimResource.class);
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
            "{}", Path.attribute("simpleString"), false
        },
        {
            jsonString, Path.attribute("simpleString"), true
        },
        {
            jsonString, Path.attribute("nullValue"), true
        },
        {
            jsonString,
            Path.attribute("singleComplex").sub("address").sub("l1"), true
        },
        {
            jsonString,
            Path.attribute("singleComplex").sub("address").sub("l3"), false
        },
        {
            jsonString,
            Path.attribute("missing"), false
        },
        {
            jsonString,
            Path.attribute("singleComplex").sub("address").sub("nullValue"),
            true
        },
        {
            jsonString,
            Path.attribute("list", Filter.eq("id", 2)), true
        },
        {
            jsonString,
            Path.attribute("list", Filter.eq("id", "5")), false
        },
        {
            jsonString,
            Path.attribute("list", Filter.eq("id", 2)).sub("address").sub("l2"),
            true
        },
        {
            jsonString,
            Path.attribute("list", Filter.eq("id", 3)).sub("address").sub("l3"),
            false
        },
        {
            jsonString,
            Path.attribute(
                "list", Filter.eq("id", 3)).sub("address").sub("nullValue"),
            true
        }
    };
  }
}
