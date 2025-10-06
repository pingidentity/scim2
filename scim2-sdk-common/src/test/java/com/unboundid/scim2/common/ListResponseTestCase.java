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
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;

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
   * Ensures that it is possible to deserialize a JSON string properly. This
   * test ensures that the objects contained within the {@code Resources} array
   * are also of the expected Java type.
   */
  @Test
  public void testDeserialization() throws Exception
  {
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 2,
          "itemsPerPage": 2,
          "Resources": [ {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "657460d3-04cc-4900-bf50-321a61fc87b7",
            "userName": "Frieren"
          }, {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "fd6fb34c-b8d0-4e10-9ba7-4ae44f11b6c3",
            "userName": "Fern"
          } ]
        }""";

    // Convert the string to a Java object as described by ListResponse's
    // class-level Javadoc.
    ListResponse<UserResource> response = JsonUtils.getObjectReader()
        .forType(new TypeReference<ListResponse<UserResource>>(){})
        .readValue(json);

    // Ensure the fields were converted correctly.
    assertThat(response.getTotalResults()).isEqualTo(2);
    assertThat(response.getItemsPerPage()).isEqualTo(2);
    assertThat(response.getStartIndex()).isNull();

    // The use of a TypeReference object must result in UserResource objects
    // in the Resources list. Otherwise, attempts to use these objects will
    // result in a ClassCastException.
    assertThat(response.getResources())
        .hasSize(2)
        .hasOnlyElementsOfType(UserResource.class);

    // The elements should have been deserialized in the correct order.
    UserResource frieren = response.getResources().get(0);
    UserResource fern = response.getResources().get(1);
    assertThat(frieren.getId())
        .isEqualTo("657460d3-04cc-4900-bf50-321a61fc87b7");
    assertThat(frieren.getUserName()).isEqualTo("Frieren");
    assertThat(fern.getId()).isEqualTo("fd6fb34c-b8d0-4e10-9ba7-4ae44f11b6c3");
    assertThat(fern.getUserName()).isEqualTo("Fern");

    // Same JSON as before, but with different casing for attribute names.
    String differentCaseJson = """
        {
          "schemaS": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "TotalResults": 2,
          "iTeMsPeRpAgE": 2,
          "resources": [ {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "657460d3-04cc-4900-bf50-321a61fc87b7",
            "userName": "Frieren"
          }, {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "iD": "fd6fb34c-b8d0-4e10-9ba7-4ae44f11b6c3",
            "USERNAME": "Fern"
          } ]
        }""";

    ListResponse<UserResource> differentCasing = JsonUtils.getObjectReader()
        .forType(new TypeReference<ListResponse<UserResource>>(){})
        .readValue(differentCaseJson);

    // Ensure the newly-deserialized value is identical to the expected result,
    // as well as the previous object.
    var expectedResult = new ListResponse<>(2, List.of(frieren, fern), null, 2);
    assertThat(differentCasing).isEqualTo(expectedResult);
    assertThat(differentCasing).isEqualTo(response);

    // Attempt converting a JSON that has an invalid UserResource definition
    // nested within it.
    String invalidUser = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 1,
          "itemsPerPage": 1,
          "Resources": [ {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "657460d3-04cc-4900-bf50-321a61fc87b7",
            "userName": "Frieren",
            "hobby": "magic"
          }]
        }""";

    ObjectReader reader = JsonUtils.getObjectReader()
        .forType(new TypeReference<ListResponse<UserResource>>(){});
    assertThatThrownBy(() -> reader.readValue(invalidUser))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Core attribute hobby is undefined for schema")
        .hasMessageContaining("urn:ietf:params:scim:schemas:core:2.0:User");

    // A JSON string must have the required 'totalResults' field.
    String missingTotalResults = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "itemsPerPage": 1,
          "Resources": [ {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "657460d3-04cc-4900-bf50-321a61fc87b7",
            "userName": "Frieren"
          }]
        }""";

    String expectedError = "Missing required creator property 'totalResults'";
    assertThatThrownBy(() -> reader.readValue(missingTotalResults))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining(expectedError);
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
    final ObjectReader reader = JsonUtils.getObjectReader()
        .forType(ListResponse.class);

    // When 'totalResults' is 0, a missing "Resources" property should be
    // permitted and translated into an empty list.
    String noResource = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 0,
          "itemsPerPage": 0
        }""";
    ListResponse<?> object = reader.readValue(noResource);
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
    ListResponse<?> small = reader.readValue(smallResponse);
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
    ListResponse<?> response = reader.readValue(emptyArray);
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
    ListResponse<?> response2 = reader.readValue(newObj);
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
    ListResponse<?> response3 = reader.readValue(itemsJSON);
    assertThat(response3.getTotalResults()).isEqualTo(100);
    assertThat(response3.getItemsPerPage()).isEqualTo(0);
    assertThat(response3.getStartIndex()).isNull();
    assertThat(response3.getResources())
        .isNotNull()
        .isEmpty();
  }
}
