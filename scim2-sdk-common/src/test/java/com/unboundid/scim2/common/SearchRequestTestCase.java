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

import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for search requests.
 */
public class SearchRequestTestCase
{
  /**
   * Verifies that serialization and deserialization of search request objects
   * result in an expected structure.
   */
  @Test
  public void testSerialization() throws Exception
  {
    // This JSON comes from RFC 7644.
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:SearchRequest" ],
          "attributes": [ "displayName", "userName" ],
          "filter": "displayName sw \\"smith\\"",
          "startIndex": 1,
          "count": 10
        }""";
    SearchRequest request = JsonUtils.getObjectReader()
        .forType(SearchRequest.class).readValue(json);

    assertThat(request.getSchemaUrns()).containsOnly(
        "urn:ietf:params:scim:api:messages:2.0:SearchRequest");
    assertThat(request.getAttributes()).containsOnly("displayName", "userName");
    assertThat(request.getFilter())
        .isEqualTo(Filter.sw("displayName", "smith").toString());
    assertThat(request.getStartIndex()).isEqualTo(1);
    assertThat(request.getCount()).isEqualTo(10);
    assertThat(request.getExcludedAttributes()).isNull();
    assertThat(request.getSortBy()).isNull();
    assertThat(request.getSortOrder()).isNull();
    assertThat(request.getCursor()).isNull();

    // Reformat the JSON into a standardized form. When the Java object is
    // serialized into a string, it should match the exact structure.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(json).toString();
    String serialized = JsonUtils.getObjectWriter().writeValueAsString(request);
    assertThat(serialized).isEqualTo(expectedJSON);

    // The same JSON with different casing for attribute names should be
    // considered equivalent.
    SearchRequest requestWithCasing = JsonUtils.getObjectReader()
        .forType(SearchRequest.class).readValue("""
        {
          "SCHEMAS": [ "urn:ietf:params:scim:api:messages:2.0:SearchRequest" ],
          "attRibutes": [ "displayName", "userName" ],
          "Filter": "displayName sw \\"smith\\"",
          "startIndex": 1,
          "counT": 10
        }""");
    assertThat(request).isEqualTo(requestWithCasing);

    // Use another JSON object with cursor values described from RFC 9865.
    String cursorJSON = """
        {
          "schemas": [ "urn:ietf:params:scim:api:messages:2.0:SearchRequest" ],
          "attributes": [ "id", "addresses", "active" ],
          "excludedAttributes": [ "meta" ],
          "filter": "displayName sw \\"smith\\"",
          "sortBy": "id",
          "sortOrder": "descending",
          "cursor": "YkU3OF86Pz0rGv",
          "count": 5
        }""";
    SearchRequest cursorRequest = JsonUtils.getObjectReader()
        .forType(SearchRequest.class).readValue(cursorJSON);

    assertThat(cursorRequest.getSchemaUrns()).containsOnly(
        "urn:ietf:params:scim:api:messages:2.0:SearchRequest");
    assertThat(cursorRequest.getAttributes())
        .containsOnly("id", "addresses", "active");
    assertThat(cursorRequest.getExcludedAttributes()).containsOnly("meta");
    assertThat(cursorRequest.getFilter())
        .isEqualTo(Filter.sw("displayName", "smith").toString());
    assertThat(cursorRequest.getSortBy()).isEqualTo("id");
    assertThat(cursorRequest.getSortOrder()).isEqualTo(SortOrder.DESCENDING);
    assertThat(cursorRequest.getStartIndex()).isNull();
    assertThat(cursorRequest.getCursor()).isEqualTo("YkU3OF86Pz0rGv");
    assertThat(cursorRequest.getCount()).isEqualTo(5);

    // Ensure that serializing the object results in the expected JSON string.
    final String expectedCursorJSON = JsonUtils.getObjectReader()
        .readTree(json).toString();
    String s = JsonUtils.getObjectWriter().writeValueAsString(request);
    assertThat(s).isEqualTo(expectedCursorJSON);
  }

  /**
   * Tests for {@code equals()}.
   */
  @SuppressWarnings("all")
  @Test
  public void testEquals() throws Exception
  {
    final String filter = Filter.eq("userName", "alice").toString();
    SearchRequest request = new SearchRequest(
        null, null, filter, null, null, null, null, 10);
    assertThat(request).isEqualTo(request);
    assertThat(request).isNotEqualTo(null);
    assertThat(request).isNotEqualTo(new UserResource());

    SearchRequest unequalSuperclass = new SearchRequest(
        null, null, filter, null, null, null, null, 10);
    unequalSuperclass.setId("undefined");
    assertThat(request).isNotEqualTo(unequalSuperclass);

    var attributesValue = new SearchRequest(
        Set.of("title"), null, filter, null, null, null, null, 10);
    assertThat(request).isNotEqualTo(attributesValue);
    assertThat(request.hashCode()).isNotEqualTo(attributesValue.hashCode());
    var excludedAttributesValue = new SearchRequest(
        null, Set.of("meta"), filter, null, null, null, null, 10);
    assertThat(request).isNotEqualTo(excludedAttributesValue);
    assertThat(request.hashCode()).isNotEqualTo(excludedAttributesValue.hashCode());
    var filterValue = new SearchRequest(
        null, null, Filter.ne("userName", "alice").toString(),
        null, null, null, null, 10);
    assertThat(request).isNotEqualTo(filterValue);
    assertThat(request.hashCode()).isNotEqualTo(filterValue.hashCode());
    var sortByValue = new SearchRequest(
        null, null, filter, "userName", null, null, null, 10);
    assertThat(request).isNotEqualTo(sortByValue);
    assertThat(request.hashCode()).isNotEqualTo(sortByValue.hashCode());
    var sortOrderValue = new SearchRequest(
        null, null, filter, null, SortOrder.ASCENDING, null, null, 10);
    assertThat(request).isNotEqualTo(sortOrderValue);
    assertThat(request.hashCode()).isNotEqualTo(sortOrderValue.hashCode());
    var startIndexValue = new SearchRequest(
        null, null, filter, null, null, 1, null, 10);
    assertThat(request).isNotEqualTo(startIndexValue);
    assertThat(request.hashCode()).isNotEqualTo(startIndexValue.hashCode());
    var cursorValue = new SearchRequest(
        null, null, filter, null, null, null, "nextValue", 10);
    assertThat(request).isNotEqualTo(cursorValue);
    assertThat(request.hashCode()).isNotEqualTo(cursorValue.hashCode());
    var countValue = new SearchRequest(
        null, null, filter, null, null, null, null, 10_000);
    assertThat(request).isNotEqualTo(countValue);
    assertThat(request.hashCode()).isNotEqualTo(countValue.hashCode());
  }
}
