/*
 * Copyright 2023-2025 Ping Identity Corporation
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
 * Copyright 2023-2025 Ping Identity Corporation
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


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Tests for {@link BaseScimResource}. Note that additional test coverage for
 * schema extensions is included in the {@link ExtensionsTest}.
 */
public class BaseScimResourceTest
{
  /**
   * Resets the customizable property toggled by this test class.
   */
  @AfterMethod
  public void resetProperty()
  {
    BaseScimResource.IGNORE_UNKNOWN_FIELDS = false;
  }

  /**
   * Test {@link BaseScimResource#setSchemaUrns}.
   */
  @Test
  public void testBaseScimResourceSchemaUrns()
  {
    BaseScimResource scimObject = new UserResource();
    BaseScimResource scimObject2 = new UserResource();

    // Set a single value.
    List<String> singleUrn = List.of("urn:pingidentity:specialObject");
    scimObject.setSchemaUrns(singleUrn);
    scimObject2.setSchemaUrns("urn:pingidentity:specialObject");
    assertThat(scimObject).isEqualTo(scimObject2);

    // Set two values.
    List<String> schemaArray = List.of(
            "urn:pingidentity:proprietaryObject",
            "urn:pingidentity:specialObject"
    );
    scimObject.setSchemaUrns(schemaArray);
    scimObject2.setSchemaUrns("urn:pingidentity:proprietaryObject",
            "urn:pingidentity:specialObject"
    );
    assertThat(scimObject).isEqualTo(scimObject2);

    // On a BaseScimResource, the objects should be considered equivalent
    // regardless of the order of the parameters.
    scimObject2.setSchemaUrns("urn:pingidentity:specialObject",
            "urn:pingidentity:proprietaryObject");
    assertThat(scimObject).isEqualTo(scimObject2);

    // Setting schema URNs to null should not be allowed.
    assertThatThrownBy(() -> scimObject.setSchemaUrns(null))
        .isInstanceOf(NullPointerException.class);

    // The first parameter of the method should not accept null.
    assertThatThrownBy(() ->
        scimObject.setSchemaUrns(null, "urn:pingidentity:specialObject"))
        .isInstanceOf(NullPointerException.class);

    // Null arguments in the varargs method should be ignored.
    scimObject.setSchemaUrns(
            "urn:pingidentity:proprietaryObject", null, null);
    assertThat(scimObject.getSchemaUrns().size()).isEqualTo(1);
  }

  /**
   * Ensure that the requested order is preserved when an ordered Collection is
   * used to initialize the schema URN set of a BaseScimResource.
   */
  @Test
  public void testSchemaUrnOrder()
  {
    final String urn0 = "urn:ietf:params:scim:schemas:core:2.0:User";
    final String urn1 = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";
    final String urn2 = "urn:pingidentity:proprietaryObject";
    final String urn3 = "urn:pingidentity:specialObject";
    final String urn4 = "urn:pingidentity:veryParticularObject";
    final String urn5 = "urn:pingidentity:aVeryVeryParticularObject";
    final List<String> urns = List.of(urn0, urn1, urn2, urn3, urn4, urn5);
    BaseScimResource resource = new UserResource();
    resource.setSchemaUrns(urns);

    assertThat(resource.getSchemaUrns())
        .hasSize(6)
        .containsExactly(urn0, urn1, urn2, urn3, urn4, urn5);

    // Re-order the urns and ensure the ordering is still preserved.
    resource.setSchemaUrns(urn5, urn4, urn3, urn2, urn1, urn0);
    assertThat(resource.getSchemaUrns())
        .hasSize(6)
        .containsExactly(urn5, urn4, urn3, urn2, urn1, urn0);
  }

  /**
   * Tests that extensions are properly parsed when deserialized from JSON.
   */
  @Test
  public void testSerializingExtension() throws Exception
  {
    BaseScimResource user = JsonUtils.getObjectReader()
        .forType(UserResource.class).readValue("""
            {
                "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
                "userName": "specialName",
                "urn:pingidentity:customExtension": {
                    "id": "fa1afe1"
                }
            }"""
        );

    ObjectNode extension = user.getExtensionObjectNode();
    assertThat(extension.size()).isEqualTo(1);

    assertThat(extension.path("urn:pingidentity:customExtension")
        .path("id").asText()).isEqualTo("fa1afe1");
  }

  /**
   * Tests the behavior when a source JSON with an extra field is serialized
   * into a subclass of BaseScimResource. If the {@code IGNORE_UNKNOWN_FIELDS}
   * property is set to true, the extra fields should be ignored.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testSerializingUnknownProperty() throws Exception
  {
    final ObjectReader reader =
        JsonUtils.getObjectReader().forType(UserResource.class);
    final String jsonWithNonStandardField = """
        {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "userName": "creative3",
            "userNameButCaps": "CREATIVE3"
        }""";

    // Update the SDK to ignore unknown fields.
    BaseScimResource.IGNORE_UNKNOWN_FIELDS = true;

    // Convert the JSON with the extra non-standard field. This should not
    // result in an exception.
    UserResource user = reader.readValue(jsonWithNonStandardField);
    assertThat(user.getUserName()).isEqualTo("creative3");

    // The ignored field and value should not be present anywhere on the user.
    // Use the string representation of the resource to confirm this.
    assertThat(user.toString())
        .doesNotContain("userNameButCaps")
        .doesNotContain("CREATIVE3");

    // Try the same thing with a JSON that also has a schema extension. The
    // extension data should still be parsed properly and not ignored.
    final String jsonWithExtension = """
        {
            "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
            "userName": "creative3",
            "userNameButCaps": "CREATIVE3",
            "urn:pingidentity:customExtension": {
                "id": "fa1afe1"
            }
        }""";
    user = reader.readValue(jsonWithExtension);
    assertThat(user.getUserName()).isEqualTo("creative3");
    List<JsonNode> extensionValues =
        user.getExtensionValues("urn:pingidentity:customExtension");
    assertThat(extensionValues).hasSize(1);
    assertThat(extensionValues.get(0).path("id").asText()).isEqualTo("fa1afe1");

    // Once again, the ignored field and value should not be present.
    assertThat(user.toString())
        .doesNotContain("userNameButCaps")
        .doesNotContain("CREATIVE3");

    // Attempt adding a new value by calling setAny() directly. This is not
    // available for public use, but should still be ignored.
    user.setAny("otherField", TextNode.valueOf("ignored"));
    assertThat(user.toString())
        .doesNotContain("otherField")
        .doesNotContain("ignored");

    // Reset the property to the default value.
    BaseScimResource.IGNORE_UNKNOWN_FIELDS = false;

    // Re-attempt the same conversions. These should now fail. Note that if this
    // exception type from Jackson changes, the documentation for the
    // IGNORE_UNKNOWN_FIELDS parameter should be updated.
    assertThatThrownBy(() -> reader.readValue(jsonWithNonStandardField))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Core attribute userNameButCaps is undefined");
    assertThatThrownBy(() -> reader.readValue(jsonWithExtension))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Core attribute userNameButCaps is undefined");

    // Directly calling the setAny() method with an invalid field should result
    // in a BadRequestException by default.
    assertThatThrownBy(() ->
        new UserResource().setAny("unknownField", TextNode.valueOf("ignored")))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Core attribute unknownField is undefined");
  }

  /**
   * Basic test to ensure that {@link BaseScimResource#getAny} will return
   * extension data.
   */
  @Test
  public void testGetAny() throws Exception
  {
    // A resource without any extension data should not return null.
    assertThat(new GroupResource().getAny()).isNotNull().isEmpty();

    // Deserialize the JSON into a group resource object.
    final String groupString = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:Group" ],
          "displayName": "newGroup",
          "urn:pingidentity:groupExt": {
            "id": "beef"
          }
        }""";
    GroupResource group = JsonUtils.getObjectReader()
        .forType(GroupResource.class).readValue(groupString);

    // Ensure that getAny() returns a map with the appropriate result.
    assertThat(group.getAny()).hasSize(1);
    assertThat(group.getAny().get("urn:pingidentity:groupExt"))
        .isInstanceOfSatisfying(ObjectNode.class, groupExtValue ->
            assertThat(groupExtValue.path("id").asText()).isEqualTo("beef"));
  }

  /**
   * Tests the behavior of the extension methods. These include:
   * <ul>
   *   <li> {@link BaseScimResource#getExtensionValues}
   *   <li> {@link BaseScimResource#replaceExtensionValue}
   *   <li> {@link BaseScimResource#removeExtensionValues}
   * </ul>
   */
  @Test
  public void testExtensionValueMethods() throws Exception
  {
    final String extName = "urn:pingidentity:errorDetail";
    final String customErrorResponseJSON = """
        {
          "schemas" : [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
          "status" : "418",
          "urn:pingidentity:errorDetail" : {
            "statusName" : "I_AM_A_TEAPOT"
          }
        }""";
    ErrorResponse response = JsonUtils.getObjectReader()
        .forType(ErrorResponse.class)
        .readValue(customErrorResponseJSON);

    // getExtensionValues() should return the schema extension data as a
    // single-element list with an ObjectNode.
    assertThat(response.getExtensionValues(extName))
        .hasSize(1).first()
        .isInstanceOfSatisfying(ObjectNode.class, node ->
            assertThat(node.path("statusName").asText())
                .isEqualTo("I_AM_A_TEAPOT"));

    // Test that replaceExtensionValue() can recreate the value from scratch.
    final ErrorResponse newResponse = new ErrorResponse(418);
    newResponse.replaceExtensionValue(extName,
        createObjectNode("statusName", "I_AM_A_TEAPOT"));
    assertThat(newResponse).isEqualTo(response);

    // Test that replaceExtensionValue() can overwrite the value of an existing
    // schema extension.
    response.replaceExtensionValue(extName,
        createObjectNode("statusName", "i_am_a_teapot"));
    assertThat(response.getExtensionValues(extName))
        .hasSize(1).first()
        .isInstanceOfSatisfying(ObjectNode.class, node ->
            assertThat(node.path("statusName").asText())
                .isEqualTo("i_am_a_teapot"));

    // Test that removeExtensionValue() can remove the schema extension data.
    response.removeExtensionValues(extName);
    assertThat(response.getExtensionValues(extName)).isEmpty();
    assertThat(response.toString()).doesNotContain(extName);
  }

  /**
   * Tests for {@code equals()}.
   */
  @SuppressWarnings("all")
  @Test
  public void testEquals() throws Exception
  {
    // Create two resources and set equivalent values for fields that are
    // defined on the BaseScimResource class.
    UserResource a = generateBasicUser();
    UserResource b = generateBasicUser();
    assertThat(a.equals(a)).isTrue();
    assertThat(a).isEqualTo(b);
    assertThat(a).isNotEqualTo(null);
    assertThat(a).isNotEqualTo(new GroupResource());

    b.setSchemaUrns("urn:pingidentity:newUrn");
    assertThat(a).isNotEqualTo(b);

    b = generateBasicUser();
    assertThat(a).isEqualTo(b);
    b.setId("uuidValue");
    assertThat(a).isNotEqualTo(b);

    b = generateBasicUser();
    assertThat(a).isEqualTo(b);
    b.setExternalId("newExternalID");
    assertThat(a).isNotEqualTo(b);

    b = generateBasicUser();
    assertThat(a).isEqualTo(b);
    b.setMeta(new Meta());
    assertThat(a).isNotEqualTo(b);

    b = generateBasicUser();
    assertThat(a).isEqualTo(b);
    b.replaceExtensionValue("urn:pingidentity:otherExt",
        createObjectNode("isOther", "true"));
    assertThat(a).isNotEqualTo(b);
  }

  @SuppressWarnings("SameParameterValue")
  private ObjectNode createObjectNode(String key, String value)
  {
    ObjectNode node = JsonUtils.getJsonNodeFactory().objectNode();
    node.set(key, TextNode.valueOf(value));
    return node;
  }

  private UserResource generateBasicUser() throws Exception
  {
    final UserResource user = new UserResource();
    user.setId("fa1afe1");
    user.setExternalId("ext-fa1afe1");
    user.replaceExtensionValue("urn:pingidentity:present",
        createObjectNode("isPresent", "true"));

    Meta meta = new Meta();
    meta.setResourceType("user");
    user.setMeta(meta);

    return user;
  }
}
