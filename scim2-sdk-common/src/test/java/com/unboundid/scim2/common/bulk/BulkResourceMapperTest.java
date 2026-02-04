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

import com.fasterxml.jackson.databind.node.NullNode;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.Set;

import static com.unboundid.scim2.common.bulk.BulkResourceMapper.SCHEMAS_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * TODO: Add a test in EndpointTestCase that maps a JSON into a response that
 *   a client would use. This will test the resource ampping logic in a more
 *   ideal way.
 */
public class BulkResourceMapperTest
{
  /**
   * Reset the bulk resource mapper to its default settings.
   */
  @AfterTest
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

    // A class that is not annotated with @Schema cannot be usable since there
    // is no information to fetch.
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

  @Schema(id = "urn:pingidentity:example", description = "", name = "")
  private static class ClassWithAnnotation extends BaseScimResource
  {

  }

  private static class NoAnnotation extends BaseScimResource
  {

  }
}
