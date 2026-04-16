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

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Set;

import static com.unboundid.scim2.common.bulk.BulkResourceMapper.SCHEMAS_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Unit tests for the {@link BulkResourceMapper} class. This functionality is
 * also exercised in {@code EndpointTestCase#testBulkRequestJsonProcessing}.
 */
public class BulkResourceMapperTest
{
  /**
   * Reset the bulk resource mapper to its default settings.
   */
  @AfterMethod
  public void tearDown()
  {
    BulkResourceMapper.initialize();
  }

  /**
   * Basic validation for the resource mapper.
   */
  @Test
  public void testBasic()
  {
    // Clear the map to start.
    BulkResourceMapper.clear();

    // Call the add() method and ensure it is registered appropriately for a
    // class defined with the @Schema annotation.
    BulkResourceMapper.add(ClassWithAnnotation.class);
    assertThat(SCHEMAS_MAP).hasSize(1);

    // Now that the value is present in the map, querying the map for a schema
    // list of "urn:pingidentity:example" should return the correct class.
    Class<ScimResource> clazz =
        BulkResourceMapper.get(Set.of("urn:pingidentity:example"));
    assertThat(clazz).isEqualTo(ClassWithAnnotation.class);

    // Query again with a JSON node.
    var arrayNode = JsonUtils.getJsonNodeFactory().arrayNode()
        .add("urn:pingidentity:example");
    clazz = BulkResourceMapper.get(arrayNode);
    assertThat(clazz).isEqualTo(ClassWithAnnotation.class);

    // A class that is not annotated with @Schema is not compatible with add()
    // since there is no information to fetch.
    assertThatThrownBy(() -> BulkResourceMapper.add(NoAnnotation.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Requested schema for the")
        .hasMessageContaining("NoAnnotation class, which does not have a valid")
        .hasMessageContaining("@Schema annotation");

    // It should be possible to register the un-annotated class with the put()
    // method.
    BulkResourceMapper.clear();
    BulkResourceMapper.put(Set.of("urn:pingidentity:put"), NoAnnotation.class);
    assertThat(SCHEMAS_MAP).hasSize(1);

    arrayNode.removeAll();
    arrayNode.add("urn:pingidentity:put");
    Class<ScimResource> clazzNoAnnotation = BulkResourceMapper.get(arrayNode);
    assertThat(clazzNoAnnotation).isEqualTo(NoAnnotation.class);

    // Trying to fetch an unregistered or invalid JsonNode should just return
    // the default GenericScimResource value.
    arrayNode.removeAll();
    arrayNode.add("urn:notFound");
    assertThat(BulkResourceMapper.get(arrayNode))
        .isEqualTo(GenericScimResource.class);
    assertThat(BulkResourceMapper.get(NullNode.getInstance()))
        .isEqualTo(GenericScimResource.class);
  }

  /**
   * Ensures the mapper returns expected objects when a JsonNode is provided to
   * the {@link BulkResourceMapper#asScimResource} method. This is the primary
   * way to interface with the BulkResourceMapper.
   */
  @Test
  public void testJsonNodeConversion() throws Exception
  {
    final var reader = JsonUtils.getObjectReader().forType(ObjectNode.class);

    // A user JSON should result in a UserResource.
    String userJson = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "userName": "simpleUser"
        }""";
    ObjectNode userNode = reader.readValue(userJson);
    ScimResource resource = BulkResourceMapper.asScimResource(userNode);
    assertThat(resource).isInstanceOfSatisfying(UserResource.class,
        user -> assertThat(user.getUserName()).isEqualTo("simpleUser"));

    // Group JSON objects should result in a GroupResource.
    String groupJson = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:Group" ],
          "displayName": "simpleGroup"
        }""";
    ObjectNode groupNode = reader.readValue(groupJson);
    ScimResource groupResource = BulkResourceMapper.asScimResource(groupNode);
    assertThat(groupResource).isInstanceOfSatisfying(GroupResource.class,
        group -> assertThat(group.getDisplayName()).isEqualTo("simpleGroup"));

    // An unregistered schema should be returned as a GenericScimResource.
    String customJson = """
        {
          "schemas": [
              "urn:ietf:params:scim:schemas:core:2.0:User",
              "urn:example:customExtension"
          ],
          "userName": "customName"
        }""";
    var gen = BulkResourceMapper.asScimResource(reader.readValue(customJson));
    assertThat(gen).isInstanceOfSatisfying(GenericScimResource.class, gsr -> {
      assertThat(gsr.getObjectNode().get("userName").asString())
          .isEqualTo("customName");
    });

    // Register the custom class that was just attempted.
    BulkResourceMapper.put(
        Set.of("urn:ietf:params:scim:schemas:core:2.0:User",
            "urn:example:customExtension"),
        UserSubClass.class
    );

    // Attempt reading the value again.
    ObjectNode customUser = reader.readValue(customJson);
    ScimResource customResource = BulkResourceMapper.asScimResource(customUser);
    assertThat(customResource).isInstanceOfSatisfying(UserSubClass.class,
        user -> assertThat(user.getUserName()).isEqualTo("customName"));
    assertThat(resource).isNotInstanceOf(GenericScimResource.class);

    // The "schemas" value should be treated as a set. Ensure the mapping still
    // works when the values are out of order.
    String outOfOrderJson = """
        {
          "schemas": [
              "urn:example:customExtension",
              "urn:ietf:params:scim:schemas:core:2.0:User"
          ],
          "userName": "customName"
        }""";
    ObjectNode outOfOrderUser = reader.readValue(outOfOrderJson);
    var outOfOrderResource = BulkResourceMapper.asScimResource(outOfOrderUser);
    assertThat(outOfOrderResource).isInstanceOfSatisfying(UserSubClass.class,
        user -> assertThat(user.getUserName()).isEqualTo("customName"));
    assertThat(outOfOrderResource).isEqualTo(customResource);
  }

  /**
   * A custom class definition with a {@code @Schema} annotation.
   */
  @Schema(id = "urn:pingidentity:example", description = "", name = "")
  private static class ClassWithAnnotation extends BaseScimResource {}

  /**
   * A custom class definition without a {@code @Schema} annotation.
   */
  private static class NoAnnotation extends BaseScimResource {}

  private static class UserSubClass extends UserResource
  {
    UserSubClass()
    {
      super.setSchemaUrns("urn:ietf:params:scim:schemas:core:2.0:User",
          "urn:example:customExtension");
    }
  }
}
