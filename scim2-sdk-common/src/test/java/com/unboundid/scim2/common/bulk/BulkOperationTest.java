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

import com.unboundid.scim2.common.exceptions.runtime.BulkRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * A collection of tests for the {@link BulkOperation} class.
 */
public class BulkOperationTest
{
  private static final ObjectReader reader = JsonUtils.getObjectReader();

  /**
   * Tests the behavior of bulk delete operations.
   */
  @Test
  public void testDelete()
  {
    // Instantiate a bulk delete operation and verify the result.
    BulkOperation operation = BulkOperation.delete("/Users/customResource")
        .setVersion("versionValue");
    assertThat(operation.getMethod()).isEqualTo(BulkOpType.DELETE);
    assertThat(operation.getPath()).isEqualTo("/Users/customResource");
    assertThat(operation.getVersion()).isEqualTo("versionValue");
    assertThat(operation.getBulkId()).isNull();
    assertThat(operation.getData()).isNull();

    // Validate serialization of the operation by ensuring the serialized string
    // is equivalent to the expected JSON output.
    String expectedString = """
        {
          "method": "DELETE",
          "path": "/Users/customResource",
          "version": "versionValue"
        }""";
    String normalizedJSON = reader.readTree(expectedString).toPrettyString();
    assertThat(operation.toString()).isEqualTo(normalizedJSON);

    // Deserialize the JSON data and verify that the resulting object is
    // equivalent to the initial operation.
    BulkOperation deserialized = reader.forType(BulkOperation.class)
        .readValue(expectedString);
    assertThat(operation).isEqualTo(deserialized);

    // Setting the bulk ID should not be permitted for a delete operation.
    assertThatThrownBy(() -> operation.setBulkId("invalid"))
        .isInstanceOf(BulkRequestException.class)
        .hasMessageContaining("Bulk IDs may only be set for POST requests")
        .hasMessageContaining("Invalid HTTP method: DELETE");
  }

  /**
   * Tests the behavior of bulk POST operations.
   */
  @Test
  public void testPost()
  {
    // Instantiate a post operation.
    UserResource newUser = new UserResource().setUserName("kendrick.lamar");
    BulkOperation operation = BulkOperation.post("/Users", newUser);

    assertThat(operation.getMethod()).isEqualTo(BulkOpType.POST);
    assertThat(operation.getPath()).isEqualTo("/Users");
    assertThat(operation.getBulkId()).isNull();
    assertThat(operation.getVersion()).isNull();
    assertThat(operation.getData())
        .isNotNull()
        .isEqualTo(newUser.asGenericScimResource().getObjectNode());

    // Ensure that it is possible to set the bulk ID.
    operation.setBulkId("qwerty2");
    assertThat(operation.getBulkId()).isEqualTo("qwerty2");

    // Validate serialization of the operation.
    String expectedString = """
        {
          "method": "POST",
          "path": "/Users",
          "bulkId": "qwerty2",
          "data": {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "userName": "kendrick.lamar"
          }
        }""";
    String normalizedJSON = reader.readTree(expectedString).toPrettyString();
    assertThat(operation.toString()).isEqualTo(normalizedJSON);

    // Validate deserialization of the JSON.
    BulkOperation deserialized = reader.forType(BulkOperation.class)
        .readValue(expectedString);
    assertThat(operation).isEqualTo(deserialized);

    // Ensure that the version field cannot be set for bulk posts.
    assertThatThrownBy(() -> operation.setVersion("nonsensical"))
        .isInstanceOf(BulkRequestException.class)
        .hasMessageContaining("Cannot set the 'version' field")
        .hasMessageContaining("of a bulk POST operation");
  }

  /**
   * Tests the behavior of bulk PUT operations.
   */
  @Test
  public void testPut()
  {
    // Instantiate a bulk put operation.
    UserResource newUserData = new UserResource()
        .setUserName("kendrick.lamar")
        .setEmails(new Email().setValue("NLU@example.com"));
    BulkOperation operation = BulkOperation.put("/Users/resource", newUserData)
        .setVersion("versionBeforeUpdate");

    assertThat(operation.getMethod()).isEqualTo(BulkOpType.PUT);
    assertThat(operation.getPath()).isEqualTo("/Users/resource");
    assertThat(operation.getBulkId()).isNull();
    assertThat(operation.getVersion()).isEqualTo("versionBeforeUpdate");
    assertThat(operation.getData())
        .isNotNull()
        .isEqualTo(newUserData.asGenericScimResource().getObjectNode());

    // Validate serialization of the operation.
    String expectedString = """
        {
          "method": "PUT",
          "path": "/Users/resource",
          "version": "versionBeforeUpdate",
          "data": {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "userName": "kendrick.lamar",
            "emails": [ {
              "value": "NLU@example.com"
            } ]
          }
        }""";
    String normalizedJSON = reader.readTree(expectedString).toPrettyString();
    assertThat(operation.toString()).isEqualTo(normalizedJSON);

    // Validate deserialization of the JSON.
    BulkOperation deserialized = reader.forType(BulkOperation.class)
        .readValue(expectedString);
    assertThat(operation).isEqualTo(deserialized);

    // Ensure that it is not possible to set the bulkID.
    assertThatThrownBy(() -> operation.setBulkId("invalid"))
        .isInstanceOf(BulkRequestException.class)
        .hasMessageContaining("Bulk IDs may only be set for POST requests")
        .hasMessageContaining("Invalid HTTP method: PUT");
  }

  /**
   * Tests the behavior of bulk patch operations.
   */
  @Test
  public void testPatch() throws Exception
  {
    // Instantiate a bulk patch operation.
    var patchOp = PatchOperation.replace("displayName", "New Value");
    var bulkOperation = BulkOperation.patch("/Groups/resource", patchOp)
        .setVersion("newVersion");

    assertThat(bulkOperation.getMethod()).isEqualTo(BulkOpType.PATCH);
    assertThat(bulkOperation.getPath()).isEqualTo("/Groups/resource");
    assertThat(bulkOperation.getBulkId()).isNull();
    assertThat(bulkOperation.getVersion()).isEqualTo("newVersion");
    assertThat(bulkOperation.getData()).isNotNull();

    // Validate serialization of a standard bulk patch operation as defined in
    // the errata of RFC 7644. This states that the 'data' field should be the
    // same JSON that represents a PatchRequest.
    String expectedString = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "version": "newVersion",
          "data": {
            "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
            "Operations": [ {
              "op": "replace",
              "path": "displayName",
              "value": "New Value"
            } ]
          }
        }""";
    String normalizedJSON = reader.readTree(expectedString).toPrettyString();
    assertThat(bulkOperation.toString()).isEqualTo(normalizedJSON);

    // Test deserializing the bulk operation directly. This should be equivalent
    // to the constructed 'bulkOperation' object.
    BulkOperation deserialized = reader.forType(BulkOperation.class)
        .readValue(expectedString);
    assertThat(bulkOperation).isEqualTo(deserialized);

    // Validate the contents of the patch operation list.
    assertThat(deserialized.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class, patchRequest ->
            assertThat(patchRequest.getOperations()).containsExactly(patchOp));

    // Validate the previous JSON, this time with no 'schemas' attribute. The
    // SCIM SDK should still accept this for broad compatibility.
    String noSchemasJson = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "version": "newVersion",
          "data": {
            "Operations": [ {
              "op": "replace",
              "path": "displayName",
              "value": "New Value"
            } ]
          }
        }""";
    BulkOperation noSchemas = reader.forType(BulkOperation.class)
        .readValue(noSchemasJson);
    assertThat(noSchemas).isEqualTo(bulkOperation);

    // Ensure case-insensitive subfields. Use a lowercase 'o' for Operations.
    String differentCasingJson = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "version": "newVersion",
          "data": {
            "oPeRaTiOnS": [ {
              "Op": "replace",
              "Path": "displayName",
              "Value": "New Value"
            } ]
          }
        }""";
    BulkOperation differentCasing = reader.forType(BulkOperation.class)
        .readValue(differentCasingJson);
    assertThat(differentCasing).isEqualTo(bulkOperation);

    // Serialize the new object. The casing should be normalized.
    assertThat(differentCasing.toString()).isEqualTo(bulkOperation.toString());

    // Validate a list with multiple elements.
    String multipleOpsJson = """
        {
          "method": "PATCH",
          "path": "/Users/resource",
          "data": {
            "Operations": [ {
              "op": "add",
              "path": "emails[type eq \\"work\\"].value",
              "value": "name@example.com"
            }, {
              "op": "remove",
              "path": "locale"
            }, {
              "op": "replace",
              "path": "active",
              "value": true
            } ]
          }
        }""";
    BulkOperation multipleOps = reader.forType(BulkOperation.class)
        .readValue(multipleOpsJson);
    String addPath = "emails[type eq \"work\"].value";
    assertThat(multipleOps.getDataAsScimResource())
        .isInstanceOf(PatchRequest.class)
        .isEqualTo(new PatchRequest(
            PatchOperation.add(addPath, StringNode.valueOf("name@example.com")),
            PatchOperation.remove("locale"),
            PatchOperation.replace("active", true)
        ));

    // When 'multipleOps' is serialized again, it should print the 'schemas'
    // field even though it was not originally present in the source JSON.
    assertThat(multipleOps.toString())
        .contains("schemas")
        .contains("urn:ietf:params:scim:api:messages:2.0:PatchOp");
    assertThat(multipleOps.getData()).isInstanceOfSatisfying(ObjectNode.class,
        o -> assertThat(o.get("schemas")).containsExactly(StringNode.valueOf(
            "urn:ietf:params:scim:api:messages:2.0:PatchOp")));

    // Validate a list with no elements.
    String emptyJson = """
        {
          "method": "PATCH",
          "path": "/Users/resource",
          "data": {
            "Operations": []
          }
        }""";
    BulkOperation empty = reader.forType(BulkOperation.class)
        .readValue(emptyJson);
    assertThat(empty.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class,
            patchRequest -> assertThat(patchRequest.getOperations()).isEmpty());

    // Validate an empty data value.
    String emptyDataJson = """
        {
          "method": "PATCH",
          "path": "/Users/resource",
          "data": { }
        }""";
    BulkOperation emptyData = reader.forType(BulkOperation.class)
        .readValue(emptyDataJson);
    assertThat(emptyData.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class,
            patchRequest -> assertThat(patchRequest.getOperations()).isEmpty());

    // Validate a bulk operation with a null operations field.
    String nullOperationsJson = """
        {
          "method": "PATCH",
          "path": "/Users/resource",
          "data": {
            "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ]
          }
        }""";
    BulkOperation nullOperations = reader.forType(BulkOperation.class)
        .readValue(nullOperationsJson);
    assertThat(nullOperations.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class,
            patchRequest -> assertThat(patchRequest.getOperations()).isEmpty());
  }

  /**
   * Validates attempts to deserialize invalid bulk operations.
   */
  @Test
  public void testImproperlyFormattedPatch()
  {
    // Ensure that other resource types are not silently accepted.
    String wrongType = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "data": {
            "otherResourceType": true
          }
        }""";
    assertThatThrownBy(() ->
        reader.forType(BulkOperation.class).readValue(wrongType))
        .isInstanceOf(JacksonException.class)
        .hasMessageContaining("Could not parse the patch operation list")
        .hasMessageContaining("because the value of 'Operations' is absent");

    // Ensure that other resource types with a schema are not silently accepted.
    String wrongSchema = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "data": {
            "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
            "detail": "message"
          }
        }""";
    assertThatThrownBy(() ->
        reader.forType(BulkOperation.class).readValue(wrongSchema))
        .isInstanceOf(JacksonException.class)
        .hasMessageContaining("Could not parse the patch operation list")
        .hasMessageContaining("because the value of 'Operations' is absent");

    String operationsNotArray = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "data": {
            "Operations": {
              "op": "replace",
              "path": "displayName",
              "value": "New Value Attempt"
            }
          }
        }""";
    assertThatThrownBy(() ->
        reader.forType(BulkOperation.class).readValue(operationsNotArray))
        .isInstanceOf(JacksonException.class)
        .hasMessageContaining("Could not parse the patch operation list")
        .hasMessageContaining("because the 'Operations' field is not an array");

    // Attempt an invalid PatchOperation that is missing the required 'op' field.
    String badOperationFormat = """
        {
          "method": "PATCH",
          "path": "/Groups/resource",
          "data": {
            "Operations": [
              {
                "path": "myAttribute",
                "value": "value without operation type"
              }
            ]
          }
        }""";
    assertThatThrownBy(() ->
        reader.forType(BulkOperation.class).readValue(badOperationFormat))
        .isInstanceOf(JacksonException.class)
        .hasMessageContaining("Failed to convert a malformed patch operation");
  }

  /**
   * Validate fetching PatchRequest objects from bulk operations.
   */
  @Test
  public void testPatchOpList() throws Exception
  {
    BulkOperation emptyBulkPatch =
        BulkOperation.patch("/Users/userID", (List<PatchOperation>) null);
    assertThat(emptyBulkPatch.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class,
            patchRequest -> assertThat(patchRequest.getOperations()).isEmpty());

    List<PatchOperation> patchList = List.of(
        PatchOperation.addStringValues("customPhoneNumbers", "512-111-1111"),
        PatchOperation.remove("addresses"),
        PatchOperation.replace("userName", "myUserName")
    );
    BulkOperation bulkPatch = BulkOperation.patch("/Users/userID", patchList);
    assertThat(bulkPatch.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class, patchRequest ->
          assertThat(patchRequest.getOperations())
              .isEqualTo(patchList).isNotSameAs(patchList)
        );

    // Repeat the previous test case with the source data as an ObjectNode.
    ObjectNode patchNode = new PatchRequest(patchList)
        .asGenericScimResource().getObjectNode();
    BulkOperation bulkPatch2 = BulkOperation.patch("/Users/userID", patchNode);
    assertThat(bulkPatch2.getDataAsScimResource())
        .isInstanceOfSatisfying(PatchRequest.class, patchRequest2 ->
          assertThat(patchRequest2.getOperations())
              .isEqualTo(patchList).isNotSameAs(patchList)
        );

    // Only patch operation types should return a PatchRequest.
    ObjectNode node = JsonUtils.getJsonNodeFactory().objectNode();
    assertThat(BulkOperation.patch("/Users", node).getDataAsScimResource())
        .isInstanceOf(PatchRequest.class);
    assertThat(BulkOperation.post("/Users", node).getDataAsScimResource())
        .isNotInstanceOf(PatchRequest.class);
    assertThat(BulkOperation.put("/Users/userID", node).getDataAsScimResource())
        .isNotInstanceOf(PatchRequest.class);
    assertThat(BulkOperation.delete("/Users/userID").getDataAsScimResource())
        .satisfies(d -> assertThat(d instanceof PatchRequest).isFalse());
  }

  /**
   * Test the {@link BulkOperation#getDataAsScimResource()} method.
   */
  @Test
  public void testFetchingData() throws Exception
  {
    UserResource dataSource = new UserResource().setUserName("premonition")
        .setDisplayName("Premo");
    BulkOperation op = BulkOperation.post("/Users", dataSource);
    assertThat(op.getDataAsScimResource())
        .isInstanceOf(UserResource.class)
        .isEqualTo(dataSource);

    // A bulk patch operation should be returned as a PatchRequest.
    BulkOperation patch = BulkOperation.patch("/Users/fa1afe1",
        PatchOperation.add("userName", StringNode.valueOf("newUser")),
        PatchOperation.replace("title", "newHire"));
    assertThat(patch.getDataAsScimResource()).isInstanceOf(PatchRequest.class);
    PatchRequest patchRequest = (PatchRequest) patch.getDataAsScimResource();
    assertThat(patchRequest.getOperations())
        .hasSize(2)
        .containsExactly(
            PatchOperation.add("userName", StringNode.valueOf("newUser")),
            PatchOperation.replace("title", "newHire"));

    // Delete operations do not have data. Ensure a null value is returned.
    BulkOperation delete = BulkOperation.delete("/Users/fa1afe1");
    assertThat(delete.getDataAsScimResource()).isNull();

    // Create an invalid user resource.
    ObjectNode invalidNode = op.getData();
    assertThat(invalidNode).isNotNull();
    invalidNode.set("emails", StringNode.valueOf("invalidSingleValue"));
    BulkOperation opWithInvalidData = BulkOperation.post("/Users", invalidNode);
    assertThatThrownBy(opWithInvalidData::getDataAsScimResource)
        .isInstanceOf(BulkRequestException.class)
        .hasMessageContaining("Failed to convert a malformed JSON into")
        .hasMessageContaining("a SCIM resource.");
  }

  /**
   * Ensures that copying a bulk operation produces a new, equivalent object.
   */
  @Test
  public void testCopy() throws Exception
  {
    List<BulkOperation> operations = List.of(
        BulkOperation.post("/Groups", new GroupResource())
            .setBulkId("qwerty"),
        BulkOperation.put("/Groups/id", new GroupResource())
            .setVersion("version"),
        BulkOperation.patch("/Groups/id", PatchOperation.remove("emails"))
            .setVersion("version"),
        BulkOperation.delete("/Groups/id").setVersion("version")
    );

    for (BulkOperation operation : operations)
    {
      BulkOperation copy = operation.copy();
      assertThat(operation.equals(copy)).isTrue();
      assertThat(operation == copy).isFalse();
    }
  }

  /**
   * Test {@link BulkOperation#equals(Object)}.
   */
  @Test
  @SuppressWarnings("all")
  public void testEquals()
  {
    var objectNode = new UserResource().asGenericScimResource().getObjectNode();
    BulkOperation put = BulkOperation.put("/Users/userID", objectNode);

    assertThat(put.equals(put)).isTrue();
    assertThat(put.equals(null)).isFalse();
    assertThat(put.equals(objectNode)).isFalse();

    // Operations of different types should not be equal.
    assertThat(put.equals(BulkOperation.delete("/Users/userID"))).isFalse();

    // Operations of the same type that reference a different path should not be
    // equal.
    assertThat(put.equals(BulkOperation.put("/Users/otherID", objectNode)))
        .isFalse();

    // Operations of the same type that do not share the same data should not be
    // equal.
    var emptyNode = JsonUtils.getJsonNodeFactory().objectNode();
    assertThat(put.equals(BulkOperation.put("/Users/userID", emptyNode)))
        .isFalse();

    final BulkOperation other = BulkOperation.put("/Users/userID", objectNode);

    // Operations with different bulk IDs should not be equal.
    BulkOperation postOp1 = BulkOperation.post("/Users", objectNode)
        .setBulkId("first");
    BulkOperation postOp2 = BulkOperation.post("/Users", objectNode)
        .setBulkId("second");
    assertThat(postOp1.equals(postOp2)).isFalse();
    assertThat(postOp1.hashCode()).isNotEqualTo(postOp2.hashCode());

    // Operations with different ETag values should not be equal.
    put.setVersion("versionValue");
    other.setVersion("otherVersion");
    assertThat(put.equals(other)).isFalse();
    other.setVersion("versionValue");
  }
}
