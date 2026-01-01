/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.utils.DateTimeUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
    private final Map<String, Object> fields = new HashMap<>();

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
     * Indicates whether the provided object is equal to this array value.
     *
     * @param o   The object to compare.
     * @return    {@code true} if the provided object is equal to this array
     *            value, or {@code false} if not.
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
      return fields.equals(that.fields);
    }

    /**
     * Retrieves a hash code for this array value.
     *
     * @return  A hash code for this array value.
     */
    @Override
    public int hashCode()
    {
      return fields.hashCode();
    }

    /**
     * Retrieves a string representation of this array value.
     *
     * @return  A string representation of this array value.
     */
    @Override
    public String toString()
    {
      return "ArrayValue{fields=%s}".formatted(fields);
    }
  }

  private ObjectNode getTestResource() throws IOException
  {
    return (ObjectNode) JsonUtils.getObjectReader().readTree("""
        {
          "string":"string",
          "integer":1,
          "decimal":1.582,
          "boolean":true,
          "date":"2015-02-27T11:28:39Z",
          "binary":"YmluYXJ5",
          "null":null,
          "empty":[],
          "array":[
            {
              "id":"1",
              "string":"string",
              "integer":1,
              "decimal":1.582,
              "boolean":true,
              "date":"2015-02-27T11:28:39Z",
              "binary":"YmluYXJ5",
              "null":null,
              "empty":[],
              "array":[
                {
                  "id":"1",
                  "string":"string",
                  "integer":1,
                  "decimal":1.582,
                  "boolean":true,
                  "date":"2015-02-27T11:28:39Z",
                  "binary":"YmluYXJ5",
                  "null":null,
                  "empty":[]
                },
                {
                  "id":"2",
                  "string":"string",
                  "integer":1,
                  "decimal":1.582,
                  "boolean":true,
                  "date":"2015-02-27T11:28:39Z",
                  "binary":"YmluYXJ5",
                  "null":null,
                  "empty":[]
                }
              ],
              "complex":{
                "string":"string",
                "integer":1,
                "decimal":1.582,
                "boolean":true,
                "date":"2015-02-27T11:28:39Z",
                "binary":"YmluYXJ5",
                "null":null,
                "empty":[],
                "array":[
                  {
                    "id":"1",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  },
                  {
                    "id":"2",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  }
                ]
              }
            },
            {
              "id":"2",
              "string":"string",
              "integer":1,
              "decimal":1.582,
              "boolean":true,
              "date":"2015-02-27T11:28:39Z",
              "binary":"YmluYXJ5",
              "null":null,
              "empty":[],
              "array":[
                {
                  "id":"1",
                  "string":"string",
                  "integer":1,
                  "decimal":1.582,
                  "boolean":true,
                  "date":"2015-02-27T11:28:39Z",
                  "binary":"YmluYXJ5",
                  "null":null,
                  "empty":[]
                },
                {
                  "id":"2",
                  "string":"string",
                  "integer":1,
                  "decimal":1.582,
                  "boolean":true,
                  "date":"2015-02-27T11:28:39Z",
                  "binary":"YmluYXJ5",
                  "null":null,
                  "empty":[]
                }
              ],
              "complex":{
                "string":"string",
                "integer":1,
                "decimal":1.582,
                "boolean":true,
                "date":"2015-02-27T11:28:39Z",
                "binary":"YmluYXJ5",
                "null":null,
                "empty":[],
                "array":[
                  {
                    "id":"1",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  },
                  {
                    "id":"2",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  }
                ]
              }
            }
          ],
          "complex":{
            "string":"string",
            "integer":1,
            "decimal":1.582,
            "boolean":true,
            "date":"2015-02-27T11:28:39Z",
            "binary":"YmluYXJ5",
            "null":null,
            "empty":[],
            "array":[
              {
                "id":"1",
                "string":"string",
                "integer":1,
                "decimal":1.582,
                "boolean":true,
                "date":"2015-02-27T11:28:39Z",
                "binary":"YmluYXJ5",
                "null":null,
                "empty":[],
                "array":[
                  {
                    "id":"1",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  },
                  {
                    "id":"2",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  }
                ],
                "complex":{
                  "string":"string",
                  "integer":1,
                  "decimal":1.582,
                  "boolean":true,
                  "date":"2015-02-27T11:28:39Z",
                  "binary":"YmluYXJ5",
                  "null":null,
                  "empty":[],
                  "array":[
                    {
                      "id":"1",
                      "string":"string",
                      "integer":1,
                      "decimal":1.582,
                      "boolean":true,
                      "date":"2015-02-27T11:28:39Z",
                      "binary":"YmluYXJ5",
                      "null":null,
                      "empty":[]
                    },
                    {
                      "id":"2",
                      "string":"string",
                      "integer":1,
                      "decimal":1.582,
                      "boolean":true,
                      "date":"2015-02-27T11:28:39Z",
                      "binary":"YmluYXJ5",
                      "null":null,
                      "empty":[]
                    }
                  ]
                }
              },
              {
                "id":"2",
                "string":"string",
                "integer":1,
                "decimal":1.582,
                "boolean":true,
                "date":"2015-02-27T11:28:39Z",
                "binary":"YmluYXJ5",
                "null":null,
                "empty":[],
                "array":[
                  {
                    "id":"1",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  },
                  {
                    "id":"2",
                    "string":"string",
                    "integer":1,
                    "decimal":1.582,
                    "boolean":true,
                    "date":"2015-02-27T11:28:39Z",
                    "binary":"YmluYXJ5",
                    "null":null,
                    "empty":[]
                  }
                ],
                "complex":{
                  "string":"string",
                  "integer":1,
                  "decimal":1.582,
                  "boolean":true,
                  "date":"2015-02-27T11:28:39Z",
                  "binary":"YmluYXJ5",
                  "null":null,
                  "empty":[],
                  "array":[
                    {
                      "id":"1",
                      "string":"string",
                      "integer":1,
                      "decimal":1.582,
                      "boolean":true,
                      "date":"2015-02-27T11:28:39Z",
                      "binary":"YmluYXJ5",
                      "null":null,
                      "empty":[]
                    },
                    {
                      "id":"2",
                      "string":"string",
                      "integer":1,
                      "decimal":1.582,
                      "boolean":true,
                      "date":"2015-02-27T11:28:39Z",
                      "binary":"YmluYXJ5",
                      "null":null,
                      "empty":[]
                    }
                  ]
                }
              }
            ]
          }
        }""");

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
        readTree("""
            {
                "old" : "old",
                "existing" : "existing",
                "null" : null,
                "empty" : [],
                "array": [
                  {
                    "value": 1
                  }
                ],
                "complex": {
                  "old" : "old",
                  "existing" : "existing",
                  "null" : null,
                  "empty" : [],
                  "array": [
                    {
                      "value": 1
                    },
                    {
                      "value": 2
                    }
                  ]
                }
            }""");

    JsonNode value = JsonUtils.getObjectReader().
        readTree("""
            {
                "new" : "new",
                "existing" : "newValue",
                "null" : "newValue",
                "empty" : "newValue",
                "array": [
                  {
                    "value": 2
                  }
                ],
                "complex": {
                  "new" : "new",
                  "existing" : "newValue",
                  "null" : "newValue",
                  "empty" : "newValue",
                  "array": [
                    {
                      "value": 3
                    }
                  ]
                }
            }""");

    JsonNode expectedAddResult = JsonUtils.getObjectReader().
        readTree("""
            {
                "old" : "old",
                "new" : "new",
                "existing" : "newValue",
                "null" : "newValue",
                "empty" : "newValue",
                "array": [
                  {
                    "value": 1
                  },
                  {
                    "value": 2
                  }
                ],
                "complex": {
                  "old" : "old",
                  "new" : "new",
                  "existing" : "newValue",
                  "null" : "newValue",
                  "empty" : "newValue",
                  "array": [
                    {
                      "value": 1
                    },
                    {
                      "value": 2
                    },
                    {
                      "value": 3
                    }
                  ]
                }
            }""");

    JsonUtils.addValue(Path.root(), resource, value);

    assertThat(resource).isEqualTo(expectedAddResult);

    JsonNode expectedReplaceResult = JsonUtils.getObjectReader().
        readTree("""
            {
                "old" : "old",
                "new" : "new",
                "existing" : "newValue",
                "null" : "newValue",
                "empty" : "newValue",
                "array": [
                  {
                    "value": 2
                  }
                ],
                "complex": {
                  "old" : "old",
                  "new" : "new",
                  "existing" : "newValue",
                  "null" : "newValue",
                  "empty" : "newValue",
                  "array": [
                    {
                      "value": 3
                    }
                  ]
                }
            }""");

    JsonUtils.replaceValue(Path.root(), resource, value);

    assertThat(resource).isEqualTo(expectedReplaceResult);
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
    gso.addValues("array", JsonUtils.valueToNode(metas));

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

    gso.addValues("complex.array[id eq \"2\"]", JsonUtils.valueToNode(metas));

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
        """
            {
               "list":[
                  {
                     "id":1,
                     "address":{
                        "l1":"Id 1, Line 1",
                        "l2":"Id 1, Line 2",
                        "l3":"Id 1, Line 3"
                     }
                  },
                  {
                     "id":2,
                     "address":{
                        "l1":"Id 2, Line 1",
                        "l2":"Id 2, Line 2",
                        "l3":"Id 2, Line 3"
                     }
                  },
                  {
                     "id":3,
                     "address":{
                        "l1":"Id 3, Line 1",
                        "l2":"Id 3, Line 2",
                        "nullValue":null
                     }
                  }
               ],
               "simpleString":"present",
               "nullValue":null,
               "singleComplex":{
                     "id":3,
                     "address":{
                        "l1":"Id 3, Line 1",
                        "l2":"Id 3, Line 2",
                        "nullValue":null
                     }
                  }
            }""";

    return new Object[][] {
        {
            "{}", Path.of("simpleString"), false
        },
        {
            jsonString, Path.of("simpleString"), true
        },
        {
            jsonString, Path.of("nullValue"), true
        },
        {
            jsonString,
            Path.of("singleComplex").attribute("address").attribute("l1"), true
        },
        {
            jsonString,
            Path.of("singleComplex").attribute("address").attribute("l3"), false
        },
        {
            jsonString, Path.of("missing"), false
        },
        {
            jsonString,
            Path.of("singleComplex").attribute("address").
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

  /**
   * Test that a SCIM 2 SDK ObjectMapper can be copied without an exception
   * thrown.
   * <p>
   * This test does not check if the copy is equivalent to the original because
   * the ObjectMapper class does not have an {@code equals} method.
   */
  @Test
  public void testScimObjectMapperCopy()
  {
    ObjectMapper mapper = JsonUtils.createObjectMapper();

    // Copying the object mapper should not cause an exception.
    ObjectMapper copy = mapper.copy();

    // The copy should be a different object.
    assertThat(mapper == copy).isFalse();
  }
}
