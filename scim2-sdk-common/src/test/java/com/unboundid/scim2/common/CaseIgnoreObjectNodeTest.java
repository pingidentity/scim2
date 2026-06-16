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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.utils.CaseIgnoreObjectNode;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for {@link CaseIgnoreObjectNode}.
 */
public class CaseIgnoreObjectNodeTest
{
  /**
   * This test method validates basic compatibility. In particular, the test
   * checks that standard ObjectNode behavior is preserved, along with the
   * treatment of all fields as case-insensitive.
   * <br><br>
   *
   * Note that some additional parent methods are tested alongside methods
   * explicitly defined in the subclass. This ensures that a method override is
   * not necessary.
   */
  @Test
  public void testBasic() throws Exception
  {
    String nestedJson = """
        {
          "Weird": {
            "Not": "Here"
          },
          "I'm": [ "Zombie", "Body", "Train", "Track" ],
          "Dirty": "Rotten",
          "Colors": "Flat",
          "Sad": {
            "Shell": {
              "Woman": {
                "Maggots": "brains"
              }
            }
          },
          "Just": {
            "Happens": {
              "When": {
                "Baby": "Away",
                "He": "Away"
              }
            }
          }
        }""";

    // Initialize the object node with the above JSON data.
    CaseIgnoreObjectNode node = JsonUtils.getObjectReader()
        .forType(CaseIgnoreObjectNode.class)
        .readValue(nestedJson);

    // Test findValue() using different casing for the properties.
    assertThat(node.findValue("dirty").asText()).isEqualTo("Rotten");
    assertThat(node.findValue("COLORS").asText()).isEqualTo("Flat");

    // Test findValues().
    assertThat(node.findValues("he")).containsOnly(TextNode.valueOf("Away"));
    assertThat(node.findValues("NOT")).containsOnly(TextNode.valueOf("Here"));
    assertThat(node.findValues("maggots", List.of(TextNode.valueOf("for"))))
        .containsExactly(TextNode.valueOf("for"), TextNode.valueOf("brains"));

    // Test findValuesAsString().
    assertThat(node.findValuesAsText("colors")).containsOnly("Flat");
    assertThat(node.findValuesAsText("DIRTY")).containsOnly("Rotten");
    assertThat(node.findValuesAsText("baby", List.of("goes")))
        .containsExactly("goes", "Away");

    // Test findParent(), which obtains the reference to a nested JsonNode
    // object when it is given a string property name.
    assertThat(node.findParent("baby"))
        .isEqualTo(objectNode().put("Baby", "Away").put("He", "Away"));

    // Test findParents().
    assertThat(node.findParents("NOT"))
        .containsOnly(objectNode().put("Not", "Here"));

    // Test deepCopy().
    assertThat(node.deepCopy())
        .isInstanceOf(CaseIgnoreObjectNode.class)
        .isEqualTo(node);
  }

  private ObjectNode objectNode()
  {
    return JsonUtils.getJsonNodeFactory().objectNode();
  }
}
