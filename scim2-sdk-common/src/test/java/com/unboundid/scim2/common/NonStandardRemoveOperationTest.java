/*
 * Copyright 2025-2026 Ping Identity Corporation
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
 * Copyright 2025-2026 Ping Identity Corporation
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Member;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * This class contains tests for {@code remove} PATCH operations that set a
 * {@code value} field in the JSON. This is typically used for group membership
 * updates that involve removing a user from a group resource. See
 * {@link PatchOperation#setRemoveOpValue(JsonNode)} for more details.
 * <br><br>
 *
 * The canonical, correct way to remove a member from a group leverages a value
 * filter of the following form:
 * <pre>
 *   {
 *      "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
 *      "Operations": [{
 *        "op": "remove",
 *        "path": "members[value eq \"2819c223-7f76-453a-919d-413861904646\"]"
 *      }]
 *    }
 * </pre>
 *
 * However, some SCIM services require providing the user/member ID in a
 * {@code value} field instead of the filter:
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
 *     "Operations": [{
 *       "op": "remove",
 *       "path": "members",
 *       "value": {
 *         "members": [{
 *           "value": "2819c223-7f76-453a-919d-413861904646"
 *         }]
 *       }
 *     }]
 *   }
 * </pre>
 *
 * Other service providers also require a {@code value} field, but structure the
 * data differently:
 * <pre>
 *   {
 *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
 *     "Operations": [{
 *       "op": "remove",
 *       "path": "members",
 *       "value": [{
 *         "$ref": null,
 *         "value": "2819c223-7f76-453a-919d-413861904646"
 *       }]
 *     }]
 *   }
 * </pre>
 *
 * Note that for both cases above, the {@code value} stored directly under the
 * {@code Operations} field refers to the patch operation value, and the nested
 * {@code value} field refers to the {@link Member#getValue} property.
 * <br><br>
 *
 * The SCIM SDK supports these operations by deserializing them properly.
 * Whenever {@link PatchOperation#apply} is invoked to process the update, the
 * SCIM SDK will convert the request to use a value filter (in the form shown in
 * the first example) before attempting to modify the resource.
 */
public class NonStandardRemoveOperationTest
{
  /**
   * Basic validation for non-standard remove patch operations. See the
   * class-level Javadoc for more information.
   */
  @Test
  public void testBasic() throws Exception
  {
    PatchOperation op;
    List<Member> member;

    GroupResource origGroup = new GroupResource()
        .setDisplayName("testGroup")
        .setMembers(new Member().setValue("ca11ab1e"),
                    new Member().setValue("c0a1e5ce"),
                    new Member().setValue("50f7ba11"),
                    new Member().setValue("e5ca1a7e"),
                    new Member().setValue("def1ec75"),
                    new Member().setValue("ba5eba11"));
    ObjectNode group = origGroup.asGenericScimResource().getObjectNode();

    int groupSize = 6;
    assertThat(group.get("members")).hasSize(groupSize);

    // Attempt removing an unrelated member.
    member = List.of(new Member().setValue("fa1afe1"));
    op = PatchOperation.remove("members").setRemoveOpValue(member, true);
    op.apply(group);
    assertThat(group.get("members")).hasSize(groupSize);

    // Using the library methods, remove a member by specifying the "value"
    // field in the JSON.
    String memberID = "ca11ab1e";
    member = List.of(new Member().setValue(memberID));
    op = PatchOperation.remove("members").setRemoveOpValue(member, true);
    groupSize--;
    op.apply(group);
    assertThat(group.get("members")).hasSize(groupSize);
    assertThat(group.toString()).doesNotContain(memberID);

    // Remove multiple members in a single request.
    op = PatchOperation.remove("members")
        .setRemoveOpValue(List.of(
            new Member().setValue("c0a1e5ce"),
            new Member().setValue("50f7ba11")
        ), true);
    groupSize -= 2;
    op.apply(group);
    assertThat(group.get("members")).hasSize(groupSize);
    assertThat(group.toString()).doesNotContain("c0a1e5ce", "50f7ba11");

    // Remove a member using the alternate JSON structure.
    memberID = "e5ca1a7e";
    member = List.of(new Member().setValue(memberID));
    op = PatchOperation.remove("members").setRemoveOpValue(member, false);
    groupSize--;
    op.apply(group);
    assertThat(group.get("members")).hasSize(groupSize);
    assertThat(group.toString()).doesNotContain(memberID);

    // Remove multiple members in a single request with the alternate JSON
    // structure (by providing "false").
    op = PatchOperation.remove("members")
        .setRemoveOpValue(List.of(
            new Member().setValue("def1ec75"),
            new Member().setValue("ba5eba11")
        ), false);
    groupSize -= 2;
    op.apply(group);
    assertThat(groupSize).isEqualTo(0);
    assertThat(group.get("members")).isNull();
    assertThat(group.toString()).doesNotContain("def1ec75", "ba5eba11");

    // Passing a non-initialized Member value to 'setRemoveOpValue()' should be
    // a no-op.
    PatchOperation initialRemove = PatchOperation.remove("members")
        .setRemoveOpValue(List.of(new Member().setValue("value")), true);
    JsonNode initialState = initialRemove.getJsonNode();

    initialRemove.setRemoveOpValue(null, true);
    assertThat(initialRemove.getJsonNode()).isEqualTo(initialState);
    initialRemove.setRemoveOpValue(List.of(), true);
    assertThat(initialRemove.getJsonNode()).isEqualTo(initialState);
    initialRemove.setRemoveOpValue(Arrays.asList(null, null), true);
    assertThat(initialRemove.getJsonNode()).isEqualTo(initialState);

    // setRemoveOpValue() should not be permitted for other operation types.
    var addOp = PatchOperation.add("userName", TextNode.valueOf("initialVal"));
    assertThatThrownBy(() -> addOp.setRemoveOpValue(TextNode.valueOf("v")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("The 'removeValue()' method may only be used for")
        .hasMessageContaining("remove operations.");
    var replaceOp = PatchOperation.replace("userName", "newValue");
    assertThatThrownBy(() -> replaceOp.setRemoveOpValue(TextNode.valueOf("v")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("The 'removeValue()' method may only be used for")
        .hasMessageContaining("remove operations.");
  }

  /**
   * Validate behavior for deserialized patch requests.
   */
  @Test
  public void testDeserialization() throws Exception
  {
    // Deserialize a JSON string.
    PatchRequest deserialized = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members",
                "value": {
                  "members": [{
                    "value": "2819c223"
                  }]
                }
              }]
            }
            """);

    // Construct an equivalent patch request and ensure it is equal.
    PatchRequest pojo = new PatchRequest(
        PatchOperation.remove("members")
            .setRemoveOpValue(List.of(new Member().setValue("2819c223")), true)
    );
    assertThat(deserialized).isEqualTo(pojo);

    // Repeat this for the other type of group membership removal request.
    deserialized = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                  "op": "remove",
                  "path": "members",
                  "value": [{
                      "value": "2819c223"
                  }]
              }]
            }
            """);
    pojo = new PatchRequest(
        PatchOperation.remove("members")
            .setRemoveOpValue(List.of(new Member().setValue("2819c223")), false)
    );
    assertThat(deserialized).isEqualTo(pojo);

    // Remove operations must always have a path, even for ones with a value.
    String jsonWithoutPath = """
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                  "op": "remove",
                  "value": [{
                      "value": "2819c223"
                  }]
              }]
            }
            """;
    assertThatThrownBy(() -> JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue(jsonWithoutPath))
        .isInstanceOf(JsonProcessingException.class)
        .hasMessageContaining("Missing required creator property 'path'");

    // Ensure explicit null values are deserialized correctly.
    PatchRequest explicitNullValue = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members",
                "value": null
              }]
            }
            """);
    PatchOperation operation = explicitNullValue.getOperations().get(0);
    assertThat(operation.getJsonNode())
        .isNotEqualTo(NullNode.getInstance())
        .isNull();
  }

  /**
   * Explicitly ensure that a patch with an empty member array does not modify
   * the target resource.
   */
  @Test
  public void testEmpty() throws Exception
  {
    GroupResource origGroup = new GroupResource().setDisplayName("testGroup");
    origGroup.setMembers(
        new Member().setValue("ca11ab1e"),
        new Member().setValue("c0a1e5ce"),
        new Member().setValue("50f7ba11")
    );
    GenericScimResource group = origGroup.asGenericScimResource();
    final int groupSize = 3;

    PatchRequest request = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members",
                "value": {
                  "members": []
                }
              }]
            }
            """);
    request.apply(group);

    // Ensure the resource is unmodified.
    assertThat(group.getValue("members"))
        .isInstanceOfSatisfying(ArrayNode.class,
            a -> assertThat(a).hasSize(groupSize));

    // Repeat for the alternate modification type.
    request = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members",
                "value": []
              }]
            }
            """);
    request.apply(group);
    assertThat(group.getValue("members"))
        .isInstanceOfSatisfying(ArrayNode.class,
            a -> assertThat(a).hasSize(groupSize));
  }

  /**
   * Test multiple flavors of these remove operations contained within a single
   * request.
   */
  @Test
  public void testMultiple() throws Exception
  {
    GroupResource origGroup = new GroupResource().setDisplayName("testGroup");
    origGroup.setMembers(
        new Member().setValue("ca11ab1e"),
        new Member().setValue("c0a1e5ce"),
        new Member().setValue("50f7ba11")
    );
    GenericScimResource group = origGroup.asGenericScimResource();

    PatchRequest request = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [
                {
                  "op": "remove",
                  "path": "members",
                  "value": {
                    "members": [{
                      "value": "ca11ab1e"
                    }]
                  }
                },
                {
                  "op": "remove",
                  "path": "members",
                  "value": {
                    "members": []
                  }
                },
                {
                  "op": "remove",
                  "path": "members",
                  "value": {
                    "members": [
                      {
                        "value": "c0a1e5ce"
                      },
                      {
                        "value": "50f7ba11"
                      }
                    ]
                  }
                }
              ]
            }
            """);
    request.apply(group);
    assertThat(group.getObjectNode().get("members")).isNull();

    // Repeat the same modifications for the other type of group membership
    // removal request.
    group = origGroup.asGenericScimResource();
    request = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [
                {
                  "op": "remove",
                  "path": "members",
                  "value": [{
                      "value": "ca11ab1e"
                  }]
                },
                {
                  "op": "remove",
                  "path": "members",
                  "value": []
                },
                {
                  "op": "remove",
                  "path": "members",
                  "value": [
                    {
                        "value": "c0a1e5ce"
                    },
                    {
                        "value": "50f7ba11"
                    }
                  ]
                }
              ]
            }
            """);
    request.apply(group);
    assertThat(group.getObjectNode().get("members")).isNull();
  }

  /**
   * Remove operation tests for {@link PatchOperation#getJsonNode()}.
   */
  @Test
  public void testGetJsonNode() throws Exception
  {
    // Fetch getJsonNode() for a standard remove operation, which should always
    // be null.
    PatchOperation op = JsonUtils.getObjectReader()
        .forType(PatchOperation.class).readValue("""
            {
              "op": "remove",
              "path": "members[value eq \\"2819c223\\"]"
            }
            """);
    assertThat(op.getJsonNode()).isNull();

    // For a remove operation with a value field, the stored JSON field should
    // be returned.
    PatchOperation opWithValue = JsonUtils.getObjectReader()
        .forType(PatchOperation.class).readValue("""
            {
              "op": "remove",
              "path": "members",
              "value": [{
                "value": "2819c223"
              }]
            }
            """);

    ArrayNode expected = JsonUtils.getJsonNodeFactory().arrayNode();
    expected.add(JsonUtils.getJsonNodeFactory().objectNode()
        .set("value", TextNode.valueOf("2819c223")));
    assertThat(opWithValue.getJsonNode()).isEqualTo(expected);
  }

  /**
   * This test ensures that appropriate error messages are returned when using
   * JSON values that are improperly formatted.
   */
  @Test
  public void testInvalidOperations() throws Exception
  {
    final var reader = JsonUtils.getObjectReader().forType(PatchRequest.class);
    GenericScimResource resource = new GenericScimResource();

    // Creating this operation is invalid because we only support targeting
    // "members".
    String invalidPath = """
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "emails",
                "value": {
                  "members": []
                }
              }]
            }
            """;
    assertThatThrownBy(() -> reader.readValue(invalidPath))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Cannot create the operation since it has a")
        .hasMessageContaining("value, but an invalid 'emails' path.");

    // Creating this operation is invalid because the path has a value filter.
    String operationWithFilter = """
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members[value sw \\"existingFilterValue\\"]",
                "value": {
                  "members": []
                }
              }]
            }
            """;
    assertThatThrownBy(() -> reader.readValue(operationWithFilter))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Cannot create the operation since it has a")
        .hasMessageContaining("value, but an invalid 'members[value sw");

    // Applying this operation is invalid because the Member field has other
    // attributes such as '$ref' and 'display', but it does not have 'value'.
    PatchRequest opWithBadValue = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members",
                "value": {
                  "members": [{
                    "$ref": "https://example.com/",
                    "display": "A Member Object Without A Value"
                  }]
                }
              }]
            }
            """);
    assertThatThrownBy(() -> opWithBadValue.apply(resource))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("The remove operation was formatted incorrectly")
        .hasMessageContaining("because it did not contain a Member object with")
        .hasMessageContaining("a 'value' subfield set.");
    PatchRequest otherOpWithBadValue = JsonUtils.getObjectReader()
        .forType(PatchRequest.class).readValue("""
            {
              "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
              "Operations": [{
                "op": "remove",
                "path": "members",
                "value": [{
                  "$ref": "https://example.com/",
                  "display": "Other Type Of Member Object Without A Value"
                }]
              }]
            }
            """);
    assertThatThrownBy(() -> otherOpWithBadValue.apply(resource))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("The remove operation was formatted incorrectly")
        .hasMessageContaining("because it did not contain a Member object with")
        .hasMessageContaining("a 'value' subfield set.");
  }

  /**
   * Tests for the {@link PatchOperation#getRemoveMemberList} method.
   */
  @Test
  public void testGetMemberList() throws Exception
  {
    final ObjectReader reader =
        JsonUtils.getObjectReader().forType(PatchOperation.class);

    // The method must only be callable for remove operations.
    PatchOperation invalid;
    invalid = PatchOperation.add("path", TextNode.valueOf("value"));
    assertThatThrownBy(invalid::getRemoveMemberList)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Fetching members is only supported for")
        .hasMessageContaining("'remove' operations");
    invalid = PatchOperation.replace("path", TextNode.valueOf("value"));
    assertThatThrownBy(invalid::getRemoveMemberList)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Fetching members is only supported for")
        .hasMessageContaining("'remove' operations");

    // A standard-compliant remove operation should return an empty list.
    PatchOperation standard = reader.readValue("""
            {
              "op": "remove",
              "path": "members[value eq \\"2819c223\\"]"
            }""");
    assertThat(standard.getRemoveMemberList()).isNotNull().isEmpty();

    // Ensure a nested member array is properly converted.
    PatchOperation nestedType = reader.readValue("""
            {
              "op": "remove",
              "path": "members",
              "value": {
                "members": [{
                  "value": "2819c223",
                  "type": "nested"
                }]
              }
            }""");

    assertThat(nestedType.getRemoveMemberList())
        .hasSize(1)
        .containsExactly(new Member().setValue("2819c223").setType("nested"));

    // Repeat this for the un-nested group membership removal request type.
    PatchOperation notNestedType = reader.readValue("""
            {
              "op": "remove",
              "path": "members",
              "value": [
                {
                  "value": "2819c223",
                  "type": "first"
                },
                {
                  "value": "2819c223",
                  "type": "second"
                }
              ]
            }""");
    assertThat(notNestedType.getRemoveMemberList())
        .hasSize(2)
        .containsExactly(
            new Member().setValue("2819c223").setType("first"),
            new Member().setValue("2819c223").setType("second")
        );

    // A JSON with invalid array data should throw an exception. This patch
    // operation does not have a "value.members" array.
    PatchOperation invalidValue = reader.readValue("""
            {
              "op": "remove",
              "path": "members",
              "value": {
                "otherData": "extraneous"
              }
            }""");
    assertThatThrownBy(invalidValue::getRemoveMemberList)
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Could not extract a Member object list from the")
        .hasMessageContaining("patch operation");

    // A JSON with an invalid Member object should throw an exception.
    PatchOperation invalidMember = reader.readValue("""
            {
              "op": "remove",
              "path": "members",
              "value": {
                "members": [{
                  "userName": "notAMemberObject",
                  "displayName": "Actually A UserResource",
                  "timezone": "America/Los_Angeles"
                }]
              }
            }""");
    assertThatThrownBy(invalidMember::getRemoveMemberList)
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("The provided JSON contained an invalid Member")
        .hasMessageContaining("representation.");
  }
}
