/*
 * Copyright 2026 Ping Identity Corporation
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
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.bulk;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class BulkRequestTest
{
  /**
   * Resets a customizable property.
   */
  @AfterMethod
  public void tearDown()
  {
    BaseScimResource.IGNORE_UNKNOWN_FIELDS = false;
  }

  /**
   * Performs basic validation on the BulkRequest class.
   */
  @Test
  public void testBasic()
  {
    //noinspection DataFlowIssue
    assertThatThrownBy(() -> new BulkRequest((Integer) null, null))
        .isInstanceOf(IllegalArgumentException.class);

    // Passing in null to BulkRequest(List) should be handled safely.
    BulkRequest request = new BulkRequest(null);
    assertThat(request.getOperations()).isEmpty();
    assertThat(request.getFailOnErrors()).isNull();

    // For the BulkRequest(BulkOperation, BulkOperation...) variant, null values
    // should also be handled.
    request = new BulkRequest(
        BulkOperation.delete("value"), (BulkOperation[]) null);
    assertThat(request.getFailOnErrors()).isNull();
    assertThat(request.getOperations()).hasSize(1);

    request = new BulkRequest(
        BulkOperation.delete("value"),
        null,
        BulkOperation.delete("value2"));
    assertThat(request.getFailOnErrors()).isNull();
    assertThat(request.getOperations()).hasSize(2);

    // It should be possible to iterate over the bulk request in an enhanced
    // for-loop.
    for (BulkOperation op : request)
    {
      assertThat(op.getOperationType()).isEqualTo(BulkOpType.DELETE);
    }

    // forEach() may also be used, but as the documentation states, this is
    // generally discouraged since it causes difficulties with debugging and
    // following stack traces.
    request.forEach(op ->
        assertThat(op.getOperationType()).isEqualTo(BulkOpType.DELETE));
  }

  /**
   * Validates the JSON form when bulk requests are serialized into strings.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
          "failOnErrors": 2,
          "Operations": [ {
            "method": "POST",
            "path": "/Users",
            "data": {
              "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
              "userName": "Alice"
            }
          } ]
        }""";

    // Reformat the string in a standardized form.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(json).toPrettyString();

    // Ensure all constructors can create the request.
    BulkRequest request;
    final BulkOperation operation = BulkOperation.post(
        "/Users", new UserResource().setUserName("Alice"));

    request = new BulkRequest(2, List.of(operation));
    assertThat(request.toString()).isEqualTo(expectedJSON);

    request = new BulkRequest(List.of(operation)).setFailOnErrors(2);
    assertThat(request.toString()).isEqualTo(expectedJSON);

    request = new BulkRequest(operation).setFailOnErrors(2);
    assertThat(request.toString()).isEqualTo(expectedJSON);
  }

  /**
   * Validates deserialization of a bulk request.
   */
  @Test
  public void testDeserialization() throws Exception
  {
    final var reader = JsonUtils.getObjectReader().forType(BulkRequest.class);

    // Example JSON bulk request from RFC 7644. This was slightly revised to
    // include the "Operations" attribute within the bulk patch request (the
    // third bulk operation) so that it's a valid JSON with key-value pairs.
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
          "failOnErrors": 1,
          "Operations": [
            {
              "method": "POST",
              "path": "/Users",
              "bulkId": "qwerty",
              "data": {
                "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
                "userName": "Alice"
              }
            },
            {
              "method": "PUT",
              "path": "/Users/b7c14771-226c-4d05-8860-134711653041",
              "version": "W/\\"3694e05e9dff591\\"",
              "data": {
                "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
                "id": "b7c14771-226c-4d05-8860-134711653041",
                "userName": "Bob"
              }
            },
            {
              "method": "PATCH",
              "path": "/Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc",
              "version": "W/\\"edac3253e2c0ef2\\"",
              "data": {
                "Operations": [
                  {
                    "op": "remove",
                    "path": "nickName"
                  },
                  {
                    "op": "replace",
                    "path": "userName",
                    "value": "Dave"
                  }
                ]
              }
            },
            {
              "method": "DELETE",
              "path": "/Users/e9025315-6bea-44e1-899c-1e07454e468b",
              "version": "W/\\"0ee8add0a938e1a\\""
            }
          ]
        }""";
    BulkRequest request = reader.readValue(json);
    assertThat(request.getFailOnErrors()).isEqualTo(1);

    // Validate the operations list and its contents.
    final List<BulkOperation> operations = request.getOperations();
    assertThat(operations).hasSize(4);

    UserResource alice = new UserResource().setUserName("Alice");
    assertThat(operations.get(0)).satisfies(op -> {
      assertThat(op.getMethod()).isEqualTo(BulkOpType.POST);
      assertThat(op.getPath()).isEqualTo("/Users");
      assertThat(op.getBulkId()).isEqualTo("qwerty");
      assertThat(op.getData()).isNotNull();
      assertThat(op.getData().toPrettyString()).isEqualTo(alice.toString());
      assertThat(op.getVersion()).isNull();
    });

    UserResource bob = new UserResource().setUserName("Bob");
    bob.setId("b7c14771-226c-4d05-8860-134711653041");
    assertThat(operations.get(1)).satisfies(op -> {
      assertThat(op.getMethod()).isEqualTo(BulkOpType.PUT);
      assertThat(op.getPath())
          .isEqualTo("/Users/b7c14771-226c-4d05-8860-134711653041");
      assertThat(op.getBulkId()).isNull();
      assertThat(op.getData()).isNotNull();
      assertThat(op.getData().toPrettyString()).isEqualTo(bob.toString());
      assertThat(op.getVersion()).isEqualTo("W/\"3694e05e9dff591\"");
    });

    List<JsonNode> expectedPatchOperations = List.of(
        JsonUtils.valueToNode(PatchOperation.remove("nickName")),
        JsonUtils.valueToNode(PatchOperation.replace("userName", "Dave")));
    assertThat(operations.get(2)).satisfies(op -> {
      assertThat(op.getMethod()).isEqualTo(BulkOpType.PATCH);
      assertThat(op.getPath())
          .isEqualTo("/Users/5d8d29d3-342c-4b5f-8683-a3cb6763ffcc");
      assertThat(op.getBulkId()).isNull();
      assertThat(op.getData()).isNotNull();
      assertThat(op.getData().get("Operations"))
          .hasSize(2)
          .containsExactlyInAnyOrderElementsOf(expectedPatchOperations);
      assertThat(op.getVersion()).isEqualTo("W/\"edac3253e2c0ef2\"");
    });

    assertThat(operations.get(3)).satisfies(op -> {
      assertThat(op.getMethod()).isEqualTo(BulkOpType.DELETE);
      assertThat(op.getPath())
          .isEqualTo("/Users/e9025315-6bea-44e1-899c-1e07454e468b");
      assertThat(op.getBulkId()).isNull();
      assertThat(op.getData()).isNull();
      assertThat(op.getVersion()).isEqualTo("W/\"0ee8add0a938e1a\"");
    });

    // Test empty bulk requests.
    String emptyOpsJson = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
          "failOnErrors": 10,
          "Operations": []
        }""";
    request = reader.readValue(emptyOpsJson);
    assertThat(request.getFailOnErrors()).isEqualTo(10);
    assertThat(request.getOperations()).isEmpty();

    // The SCIM SDK treats ListResponse objects with special handling when the
    // "Resources" array is not present in the JSON, as this behavior is
    // mandated by the RFC. For consistent behavior, treat a null value for
    // "Operations" as an empty array.
    String missingOpsJson = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
          "failOnErrors": 200
        }""";
    request = reader.readValue(missingOpsJson);
    assertThat(request.getFailOnErrors()).isEqualTo(200);
    assertThat(request.getOperations()).isEmpty();

    // A missing value for failOnErrors should be treated as null.
    String missingFailOnErrors = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ],
          "Operations": []
        }""";
    request = reader.readValue(missingFailOnErrors);
    assertThat(request.getFailOnErrors()).isNull();
    assertThat(request.getOperations()).isEmpty();

    // Attempt deserializing an incorrect object into a bulk request.
    String invalidJson = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "userName": "muhammad.ali",
          "title": "Champ",
          "emails": [{
            "value": "ali@example.com",
            "primary": true
          }]
        }""";

    // By default, we should see a JsonMappingException.
    assertThatThrownBy(() -> reader.readValue(invalidJson))
        .isInstanceOf(JsonMappingException.class);

    // If the configuration setting is set to allow unknown attributes without
    // failing, then we should still have an exception to indicate that no data
    // was successfully copied. In this test case, a user resource should appear
    // to deserialize successfully into a BulkRequest.
    //
    // The method throws an IllegalArgumentException, but Jackson re-throws this
    // as a JsonMappingException.
    BaseScimResource.IGNORE_UNKNOWN_FIELDS = true;
    assertThatThrownBy(() -> reader.readValue(invalidJson))
        .isInstanceOf(JsonMappingException.class);

    // Explicitly null values for both attributes should fail, as this is the
    // signal we use that the source JSON was most likely not an actual bulk
    // request.
    String veryEmpty = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:BulkRequest" ]
        }""";
    assertThatThrownBy(() -> reader.readValue(veryEmpty))
        .isInstanceOf(JsonMappingException.class);
  }

  /**
   * Validate {@link BulkRequest#equals(Object)}.
   */
  @Test
  @SuppressWarnings("all")
  public void testEquals()
  {
    BulkRequest first = new BulkRequest(BulkOperation.delete("value"))
        .setFailOnErrors(1);
    BulkRequest second = new BulkRequest(BulkOperation.delete("value"))
        .setFailOnErrors(1);

    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    assertThat(first.equals(new UserResource())).isFalse();
    assertThat(first.equals(second)).isTrue();

    // Before the next phase, ensure the hash codes are identical.
    assertThat(first.hashCode()).isEqualTo(second.hashCode());

    // Clear the second value request's failure count so that the requests are
    // unequal. Now, the hash codes should be different.
    second.setFailOnErrors(null);
    assertThat(first.equals(second)).isFalse();
    assertThat(first.hashCode()).isNotEqualTo(second.hashCode());

    // Create another operation with the same failure count but a different
    // request.
    BulkRequest newOperation = new BulkRequest(
        BulkOperation.post("/Users", new UserResource().setUserName("Alice")));
    newOperation.setFailOnErrors(2);
    assertThat(first.equals(newOperation)).isFalse();
  }
}
