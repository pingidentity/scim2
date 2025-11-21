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

import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.unboundid.scim2.common.filters.Filter.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;

/**
 * Tests for parsing SCIM 2 paths.
 */
public class PathParsingTestCase
{
  /**
   * Retrieves a set of valid path strings.
   *
   * @return  A set of valid path strings.
   * @throws BadRequestException if the path string is invalid.
   */
  @DataProvider(name = "testValidPathStrings")
  public Object[][] getTestValidPathStrings() throws BadRequestException
  {
    return new Object[][]
        {
            new Object[] { "attr",
                Path.root().attribute("attr") },
            new Object[] { "urn:extension:attr",
                Path.root("urn:extension").attribute("attr") },
            new Object[] { "attr.subAttr",
                Path.root().attribute("attr").attribute("subAttr") },
            new Object[] { "attr[subAttr eq \"78750\"].subAttr",
                Path.root().attribute("attr", eq("subAttr", "78750")).
                    attribute("subAttr") },
            new Object[] { "attr.$ref",
                Path.root().attribute("attr").attribute("$ref") },
            new Object[] { "attr[$ref eq \"/Users/xxx\"]",
                Path.root().attribute("attr", eq("$ref", "/Users/xxx")) },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"].subAttr",
                Path.root("urn:extension").attribute("attr",
                    eq("subAttr", "78750")).attribute("subAttr") },
            new Object[] { "urn:ietf:params:scim:schemas:extension:" +
                "enterprise:2.0:User:employeeNumber",
                Path.root("urn:ietf:params:scim:schemas:extension:" +
                    "enterprise:2.0:User").attribute("employeeNumber") },
            new Object[] { "urn:pingidentity:schemas:sample:profile:1.0:" +
                "topicPreferences[(id eq " +
                "\"urn:X-UnboundID:topic:clothing:shoes\" and " +
                "strength eq 10 and timeStamp eq " +
                "\"2015-10-12T14:57:36.494Z\")]",
                Path.root("urn:pingidentity:schemas:sample:profile:1.0").
                    attribute("topicPreferences",
                        Filter.and(Filter.eq("id",
                                "urn:X-UnboundID:topic:clothing:shoes"),
                                   Filter.eq("strength", 10),
                                   Filter.eq("timeStamp",
                                       "2015-10-12T14:57:36.494Z"))) },

            // The following does not technically conform to the SCIM spec
            // but our path impl is lenient so it may be used with any JSON
            // object.
            new Object[] { "", Path.root() },
            new Object[] { "urn:extension:", Path.root("urn:extension") },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"]." +
                "subAttr[subSub pr].this.is.crazy[type eq \"good\"].deep",
                Path.root("urn:extension").
                    attribute("attr", eq("subAttr", "78750")).
                    attribute("subAttr", pr("subSub")).
                    attribute("this").
                    attribute("is").
                    attribute("crazy", eq("type", "good")).
                    attribute("deep") },
        };
  }

  /**
   * Retrieves a set of invalid path strings.
   *
   * @return  A set of invalid path strings.
   */
  @DataProvider(name = "testInvalidPathStrings")
  public Object[][] getTestInvalidPathStrings()
  {
    return new Object[][]
        {
            new Object[] { "." },
            new Object[] { "attr." },
            new Object[] { "urn:attr" },
            new Object[] { "attr[].subAttr" },
            new Object[] { ".attr" },
            new Object[] { "urn:extension:." },
            new Object[] { "urn:extension:.attr" },
            new Object[] { "attr[subAttr eq 123]." },
        };
  }

  /**
   * Tests the {@code fromString} method with a valid path string.
   *
   * @param pathString  The string representation of the path to fromString.
   * @param expectedPath The expected parsed path instance.
   *
   * @throws Exception  If the test fails.
   */
  @Test(dataProvider = "testValidPathStrings")
  public void testParseValidPath(final String pathString,
                                 final Path expectedPath)
      throws Exception
  {
    final Path parsedPath = Path.fromString(pathString);
    assertEquals(parsedPath, expectedPath);
  }

  /**
   * Tests the {@code fromString} method with an invalid path string.
   *
   * @param  pathString  The string representation of the path to fromString.
   */
  @Test(dataProvider = "testInvalidPathStrings")
  public void testParseInvalidPath(final String pathString)
  {
    assertThatThrownBy(() -> Path.fromString(pathString))
        .isInstanceOfSatisfying(BadRequestException.class,
            bre -> assertThat(bre.getScimError().getScimType())
                .isEqualTo(BadRequestException.INVALID_PATH));
  }

  /**
   * Tests for {@link Path#of}.
   */
  @Test
  public void testPathOf()
  {
    for (String attr : List.of("id", "attr withSpace", ""))
    {
      // Ensure the path is a top-level attribute.
      final Path topLevelAttr = Path.of(attr);
      assertThat(topLevelAttr).isEqualTo(Path.root().attribute(attr));

      // There should be a single element with no schema URN.
      assertThat(topLevelAttr.size()).isEqualTo(1);
      assertThat(topLevelAttr.isRoot()).isFalse();
      assertThat(topLevelAttr.getSchemaUrn()).isNull();

      // The element should match the string value and have no value filter.
      Path.Element firstElement = Path.of(attr).getElement(0);
      assertThat(firstElement).isNotNull();
      assertThat(firstElement.getAttribute()).isEqualTo(attr);
      assertThat(firstElement.getValueFilter()).isNull();
    }

    // noinspection DataFlowIssue
    assertThatThrownBy(() -> Path.of(null))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> Path.of("name.familyName"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Attempted creating a top-level Path");

    assertThatThrownBy(() -> Path.of("addresses[postalCode eq \"12345\"]"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Attempted creating a top-level Path");

    assertThatThrownBy(() -> Path.of("emails["))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Attempted creating a top-level Path");

    assertThatThrownBy(() -> Path.of("eq true]"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Attempted creating a top-level Path");

    assertThatThrownBy(() -> Path.of("urn:ietf"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Attempted creating a top-level Path");
  }

  /**
   * Tests for {@link Path#getLastElement}.
   */
  @Test
  public void testLastElement() throws Exception
  {
    Path path;
    Path.Element element;

    assertThatThrownBy(() -> Path.root().getLastElement())
        .isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> Path.root("urn:extension:value").getLastElement())
        .isInstanceOf(IndexOutOfBoundsException.class);

    element = Path.of("userName").getLastElement();
    assertThat(element.getAttribute()).isEqualTo("userName");
    assertThat(element.getValueFilter()).isNull();

    path = Path.root().attribute("name").attribute("familyName");
    element = path.getLastElement();
    assertThat(element.getAttribute()).isEqualTo("familyName");
    assertThat(element.getValueFilter()).isNull();

    path = Path.root("urn:extension:value")
        .attribute("attribute")
        .attribute("nested")
        .attribute("veryNested", Filter.eq("value", "matryoshka"));
   element = path.getLastElement();
   assertThat(element.getAttribute()).isEqualTo("veryNested");
   assertThat(element.getValueFilter())
       .isEqualTo(Filter.eq("value", "matryoshka"));
  }
}
