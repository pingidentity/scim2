/*
 * Copyright 2015-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.types.GroupResource;
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
          {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "userName": "Frieren"
          }
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

    var list = List.of(new UserResource().setUserName("Frieren"));
    ListResponse<UserResource> listResponse = new ListResponse<>(2, list, 1, 1);
    String listResponseJSON =
            JsonUtils.getObjectWriter().writeValueAsString(listResponse);

    assertEquals(listResponseJSON, expectedJSON);
  }


  /**
   * Test the other utility constructors.
   */
  @Test
  public void testAlternateConstructors()
  {
    // This constructor should set totalResults and Resources, and leave all
    // other fields null.
    var group = new GroupResource().setDisplayName("Frieren's Party");
    ListResponse<GroupResource> collection = new ListResponse<>(List.of(group));

    assertThat(collection.getResources())
        .hasSize(1)
        .containsExactly(group);
    assertThat(collection.getTotalResults()).isEqualTo(1);
    assertThat(collection.getStartIndex()).isNull();
    assertThat(collection.getItemsPerPage()).isNull();
    assertThat(collection.getPreviousCursor()).isNull();
    assertThat(collection.getNextCursor()).isNull();

    // Use the original constructor from before RFC 9865 was supported.
    ListResponse<GroupResource> groupResponse =
        new ListResponse<>(1, List.of(group), null, null);
    assertThat(groupResponse).isEqualTo(collection);

    // This constructor is primarily used for SCIM services that only support
    // cursor-based pagination and do not need to return the previous cursor.
    ListResponse<GroupResource> cursorResponse =
        new ListResponse<>(2, "cursorValue", 1, List.of(group));

    assertThat(cursorResponse.getResources())
        .hasSize(1)
        .containsExactly(group);
    assertThat(cursorResponse.getTotalResults()).isEqualTo(2);
    assertThat(cursorResponse.getStartIndex()).isNull();
    assertThat(cursorResponse.getItemsPerPage()).isEqualTo(1);
    assertThat(cursorResponse.getPreviousCursor()).isNull();
    assertThat(cursorResponse.getNextCursor()).isEqualTo("cursorValue");

    // Create a "cursor" response that does not return a "nextCursor" value
    // since all results have been returned.
    ListResponse<GroupResource> allReturnedResponse =
        new ListResponse<>(1, null, null, List.of(group));

    assertThat(allReturnedResponse.getResources())
        .hasSize(1)
        .containsExactly(group);
    assertThat(allReturnedResponse.getTotalResults()).isEqualTo(1);
    assertThat(allReturnedResponse.getStartIndex()).isNull();
    assertThat(allReturnedResponse.getItemsPerPage()).isNull();
    assertThat(allReturnedResponse.getPreviousCursor()).isNull();
    assertThat(allReturnedResponse.getNextCursor()).isNull();
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
    assertThat(response.getPreviousCursor()).isNull();
    assertThat(response.getNextCursor()).isNull();

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
    assertThat(object.getPreviousCursor()).isNull();
    assertThat(object.getNextCursor()).isNull();
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
    assertThat(small.getPreviousCursor()).isNull();
    assertThat(small.getNextCursor()).isNull();
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
    assertThat(response.getPreviousCursor()).isNull();
    assertThat(response.getNextCursor()).isNull();
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
    assertThat(response2.getPreviousCursor()).isNull();
    assertThat(response2.getNextCursor()).isNull();
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
    assertThat(response3.getPreviousCursor()).isNull();
    assertThat(response3.getNextCursor()).isNull();
    assertThat(response3.getResources())
        .isNotNull()
        .isEmpty();
  }

  /**
   * Validates support for cursor-based pagination workflows as defined by
   * RFC 9865.
   */
  @Test
  public void testCursorPagination() throws Exception
  {
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:ListResponse" ],
          "totalResults": 2,
          "itemsPerPage": 2,
          "previousCursor": "ze7L30kMiiLX6x",
          "nextCursor": "YkU3OF86Pz0rGv",
          "Resources": [ {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "226537a7-90bb-4644-9bd0-e2c998e00d66",
            "userName": "Frieren"
          }, {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "id": "0fc9de62-645d-4655-9263-1b1a1d6dde13",
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
    assertThat(response.getPreviousCursor()).isEqualTo("ze7L30kMiiLX6x");
    assertThat(response.getNextCursor()).isEqualTo("YkU3OF86Pz0rGv");

    // Ensure it is possible to construct the same object.
    UserResource frieren = new UserResource().setUserName("Frieren");
    frieren.setId("226537a7-90bb-4644-9bd0-e2c998e00d66");
    UserResource fern = new UserResource().setUserName("Fern");
    fern.setId("0fc9de62-645d-4655-9263-1b1a1d6dde13");

    ListResponse<UserResource> constructed = new ListResponse<>(
        2, null, 2, "ze7L30kMiiLX6x", "YkU3OF86Pz0rGv", List.of(frieren, fern));
    assertThat(constructed).isEqualTo(response);

    // The constructed object should be serialized into a consistent form, with
    // an expected order for the attributes. First reformat the JSON into a
    // non-pretty string.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(json).toString();
    String serial = JsonUtils.getObjectWriter().writeValueAsString(response);
    assertThat(serial).isEqualTo(expectedJSON);
  }

  /**
   * Tests for {@code equals()}.
   */
  @SuppressWarnings("all")
  @Test
  public void testEquals()
  {
    ListResponse<UserResource> list =
        new ListResponse<>(List.of(new UserResource()));
    assertThat(list.equals(list)).isTrue();
    assertThat(list.equals(null)).isFalse();
    assertThat(list.equals(new UserResource())).isFalse();

    // Create a ListResponse with an unequal superclass value, even though list
    // responses do not have an "id".
    ListResponse<UserResource> unequalSuperclass =
        new ListResponse<>(List.of(new UserResource()));
    unequalSuperclass.setId("undefined");
    assertThat(list.equals(unequalSuperclass)).isFalse();

    var totalResultsValue = new ListResponse<>(
        1000, null, null, null, null, List.of(new UserResource()));
    assertThat(list.equals(totalResultsValue)).isFalse();
    assertThat(list.hashCode()).isNotEqualTo(totalResultsValue.hashCode());
    var startIndexValue = new ListResponse<>(
        1, 1, null, null, null, List.of(new UserResource()));
    assertThat(list.equals(startIndexValue)).isFalse();
    assertThat(list.hashCode()).isNotEqualTo(startIndexValue.hashCode());
    var itemsPerPageValue = new ListResponse<>(
        1, null, 1, null, null, List.of(new UserResource()));
    assertThat(list.equals(itemsPerPageValue)).isFalse();
    assertThat(list.hashCode()).isNotEqualTo(itemsPerPageValue.hashCode());
    var prevCursorValue = new ListResponse<>(
        1, null, null, "prev", null, List.of(new UserResource()));
    assertThat(list.equals(prevCursorValue)).isFalse();
    assertThat(list.hashCode()).isNotEqualTo(prevCursorValue.hashCode());
    var nextCursorValue = new ListResponse<>(
        1, null, null, null, "next", List.of(new UserResource()));
    assertThat(list.equals(nextCursorValue)).isFalse();
    assertThat(list.hashCode()).isNotEqualTo(nextCursorValue.hashCode());
    var resourcesValue = new ListResponse<>(
        1, null, null, null, null, List.of(new UserResource().setTitle("U.")));
    assertThat(list.equals(resourcesValue)).isFalse();
    assertThat(list.hashCode()).isNotEqualTo(resourcesValue.hashCode());
  }
}
