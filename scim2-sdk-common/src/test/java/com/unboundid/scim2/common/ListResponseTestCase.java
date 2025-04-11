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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Test for list responses.
 */
public class ListResponseTestCase
{
  // An example ListResponse in JSON form.
  private static final String SINGLE_ELEMENT_LIST_RESPONSE = """
      {
        "schemas": [
          "urn:ietf:params:scim:api:messages:2.0:ListResponse"
        ],
        "totalResults": 2,
        "itemsPerPage": 1,
        "startIndex": 1,
        "Resources": [
          "stringValue"
        ]
      }""";


  /**
   * Test list response.
   *
   * @throws Exception If an error occurs.
   */
  @Test
  public void testListResponse()
      throws Exception
  {
    // Test required property case-insensitivity
    ListResponse<ObjectNode> listResponse =
        JsonUtils.getObjectReader().forType(
            new TypeReference<ListResponse<ObjectNode>>() {}).readValue(
            """
                {
                  "schemas":[
                    "urn:ietf:params:scim:api:messages:2.0:ListResponse"
                  ],
                  "totalresults":2,
                  "startIndex":1,
                  "ItemsPerPage":3,
                  "Resources":[
                    {
                      "userName":"bjensen"
                    },
                    {
                      "userName":"jsmith"
                    }
                  ]
                }""");

    try
    {
      // Test missing required property: totalResults
      // Test case-insensitivity
      listResponse =
        JsonUtils.getObjectReader().forType(
          new TypeReference<ListResponse<ObjectNode>>() {}).readValue(
            """
                {
                  "schemas":[
                    "urn:ietf:params:scim:api:messages:2.0:ListResponse"
                  ],
                  "startIndex":1,
                  "ItemsPerPage":3,
                  "Resources":[
                    {
                      "userName":"bjensen"
                    },
                    {
                      "userName":"jsmith"
                    }
                  ]
                }""");
      fail("Expected failure for missing required property 'totalResults'");
    }
    catch (final JsonMappingException je)
    {
      assertTrue(je.getMessage().contains("Missing required creator property"),
        je.getMessage());
    }

    assertEquals(listResponse.getTotalResults(), 2);
    assertEquals(listResponse.getStartIndex(), Integer.valueOf(1));
    assertEquals(listResponse.getItemsPerPage(), Integer.valueOf(3));
    assertEquals(listResponse.getResources().size(), 2);

    ArrayList<ResourceTypeResource> resourceTypeList = new ArrayList<>();
    resourceTypeList.add(
        new ResourceTypeResource("urn:test", "test", "test", new URI("/test"),
            new URI("urn:test"),
            Collections.emptyList()));
    resourceTypeList.add(
        new ResourceTypeResource("urn:test2", "test2", "test2",
            new URI("/test2"), new URI("urn:test2"),
            Collections.emptyList()));
    ListResponse<ResourceTypeResource> response =
        new ListResponse<>(100, resourceTypeList, 1, 10);

    String serialized = JsonUtils.getObjectWriter().
        writeValueAsString(response);
    assertEquals(JsonUtils.getObjectReader().forType(
            new TypeReference<ListResponse<ResourceTypeResource>>() {}).
            readValue(serialized),
        response);
  }


  /**
   * Tests the format of a {@link ListResponse} object when it is serialized
   * into JSON form. In particular, this ensures that objects follow the form
   * described in the examples of RFC 7644, where the {@code Resources} array is
   * the final element in the JSON body.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testListResponseFormat() throws Exception
  {
    // Reformat the expected JSON to a standardized form.
    String expectedJSON =
            JsonUtils.getObjectReader().readTree(SINGLE_ELEMENT_LIST_RESPONSE)
                    .toString();

    List<String> resources = Collections.singletonList("stringValue");
    ListResponse<String> listResponse = new ListResponse<>(2, resources, 1, 1);
    String listResponseJSON =
            JsonUtils.getObjectWriter().writeValueAsString(listResponse);

    assertEquals(listResponseJSON, expectedJSON);
  }


  /**
   * Ensures {@code null} resources are permitted during deserialization when
   * {@code totalResults} or {@code itemsPerPage} are zero.
   */
  @Test
  public void testDeserializingNullResourcesArray() throws Exception
  {
    // The object reader that will be used to serialize JSON strings into
    // ListResponse objects.
    final ObjectReader reader = JsonUtils.getObjectReader();

    // When 'totalResults' is 0, a missing "Resources" property should be
    // permitted and translated into an empty list.
    String noResource = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 0,
          "itemsPerPage": 0
        }""";
    ListResponse<?> object = reader.readValue(noResource, ListResponse.class);
    assertThat(object.getTotalResults()).isEqualTo(0);
    assertThat(object.getItemsPerPage()).isEqualTo(0);
    assertThat(object.getStartIndex()).isNull();
    assertThat(object.getResources())
        .isNotNull()
        .isEmpty();

    // Test the emptiest possible valid ListResponse object. There should be no
    // problems with setting the other fields to null or empty.
    String smallResponse = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 0
        }""";
    ListResponse<?> small = reader.readValue(smallResponse, ListResponse.class);
    assertThat(small.getTotalResults()).isEqualTo(0);
    assertThat(small.getItemsPerPage()).isNull();
    assertThat(small.getStartIndex()).isNull();
    assertThat(small.getResources())
        .isNotNull()
        .isEmpty();

    // An explicit empty array should still be permitted if totalResults == 0.
    String emptyArray = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 0,
          "Resources": []
        }""";
    ListResponse<?> response = reader.readValue(emptyArray, ListResponse.class);
    assertThat(response.getTotalResults()).isEqualTo(0);
    assertThat(response.getItemsPerPage()).isNull();
    assertThat(response.getStartIndex()).isNull();
    assertThat(response.getResources())
        .isNotNull()
        .isEmpty();

    // This is illegal since the Resources array is missing when totalResults is
    // non-zero.
    String invalidJSON = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 1
        }""";
    assertThatThrownBy(() -> reader.readValue(invalidJSON, ListResponse.class))
        .isInstanceOf(JsonProcessingException.class)
        .hasMessageContaining("Failed to create the ListResponse since it is")
        .hasMessageContaining("missing the 'Resources' property");

    // The following is a valid list response since itemsPerPage restricts the
    // resource list size to 0.
    String newObj = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 100,
          "itemsPerPage": 0,
          "Resources": []
        }""";
    ListResponse<?> response2 = reader.readValue(newObj, ListResponse.class);
    assertThat(response2.getTotalResults()).isEqualTo(100);
    assertThat(response2.getItemsPerPage()).isEqualTo(0);
    assertThat(response2.getStartIndex()).isNull();
    assertThat(response2.getResources())
        .isNotNull()
        .isEmpty();

    // This response is technically invalid since it should use an empty array.
    // However, if a SCIM service is willing to return null when totalResults is
    // 0, they might do the same for itemsPerPage. The SCIM SDK permits this
    // operation in order to make it easier to work with these SCIM services.
    String itemsJSON = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 100,
          "itemsPerPage": 0
        }""";
    ListResponse<?> response3 = reader.readValue(itemsJSON, ListResponse.class);
    assertThat(response3.getTotalResults()).isEqualTo(100);
    assertThat(response3.getItemsPerPage()).isEqualTo(0);
    assertThat(response3.getStartIndex()).isNull();
    assertThat(response3.getResources())
        .isNotNull()
        .isEmpty();
  }
}
