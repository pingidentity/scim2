/*
 * Copyright 2024-2026 Ping Identity Corporation
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
 * Copyright 2024-2026 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.MapperFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;


/**
 * This class contains tests that validate customization of the
 * {@link MapperFactory} and its object mapper.
 */
public class MapperFactoryTest
{
  /**
   * Reset the mapper factory configuration to the default settings.
   */
  @AfterMethod
  public void tearDown()
  {
    JsonUtils.setCustomMapperFactory(new MapperFactory());
  }

  /**
   * Tests updating the SCIM SDK mapper configuration by appending updates to
   * the builder provided in {@link MapperFactory#createBuilder()}.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testCustomJsonMapperBuilder()
  {
    // A SCIM resource with the attributes (except 'schema') sorted
    // alphabetically.
    final String rawJSONString = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "displayName": "Kendrick Lamar",
          "emails": [{ "value": "NLU@example.com" }],
          "userName": "K.Dot"
        }""";

    // Reformat the string in a standardized form.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(rawJSONString).toString();

    UserResource user = new UserResource()
        .setUserName("K.Dot")
        .setEmails(new Email().setValue("NLU@example.com"))
        .setDisplayName("Kendrick Lamar");

    // In the SCIM SDK's default configuration, the 'userName' field appears
    // before fields like 'email'. Before making any changes, verify that the
    // serialized user resource does not list attributes in alphabetical order.
    String userJSON = JsonUtils.getObjectWriter().writeValueAsString(user);
    assertThat(userJSON).isNotEqualTo(expectedJSON);

    // Update the object mapper to sort the elements of a SCIM resource.
    JsonMapper.Builder newConfig = JsonUtils.getInitialMapperConfig()
        .enable(SORT_PROPERTIES_ALPHABETICALLY);
    JsonUtils.setCustomMapperFactory(new MapperFactory().setConfig(newConfig));

    // Serialize the user resource again. This time, the object mapper should
    // sort the fields alphabetically.
    userJSON = JsonUtils.getObjectWriter().writeValueAsString(user);
    assertThat(userJSON).isEqualTo(expectedJSON);
  }

  /**
   * Tests support for overriding the {@link MapperFactory#createObjectMapper()}
   * method.
   * <br><br>
   *
   * In some cases, a client application may require more specific
   * customizations, such as setting custom serializers/deserializers for
   * better integration with a SCIM service provider that provides
   * non-standardized SCIM responses. For those cases, we should ensure that
   * it's possible for client applications to extend the MapperFactory class and
   * implement their own object mapper settings.
   */
  @Test
  public void testOverrideMapperFactoryClass()
  {
    // Override the object mapper configuration to force case-sensitivity.
    class CustomFactory extends MapperFactory
    {
      @NotNull
      @Override
      public JsonMapper createObjectMapper()
      {
        return new JsonMapper();
      }
    }

    // A UserResource JSON with capitalized attribute names.
    String json = """
        {
          "SCHEMAS": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "USERNAME": "MF DOOM"
        }
        """;

    // First validate the SCIM SDK's default behavior. It should be possible to
    // fetch an attribute value regardless of the casing.
    final ObjectNode user = JsonUtils.getObjectReader()
        .forType(ObjectNode.class).readValue(json);
    assertThat(user.path("userName").asString()).isEqualTo("MF DOOM");

    // Update the SCIM SDK's object mapper with the custom factory, which
    // will require matching casing.
    MapperFactory factory = new CustomFactory();
    JsonUtils.setCustomMapperFactory(factory);

    // Attempt the same conversion again to verify the change in behavior. This
    // should now require that the casing of the attribute exactly matches the
    // field in the JSON.
    final ObjectNode user2 = JsonUtils.getObjectReader()
        .forType(ObjectNode.class).readValue(json);
    assertThat(user2.get("userName")).isNull();
    assertThat(user2.get("USERNAME").asString()).isEqualTo("MF DOOM");
  }
}
