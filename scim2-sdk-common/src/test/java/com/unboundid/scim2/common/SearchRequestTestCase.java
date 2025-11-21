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

import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.StaticUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Test for search requests.
 */
public class SearchRequestTestCase
{
  /**
   * Test search request.
   *
   * @throws java.io.IOException If an error occurs.
   * @throws ScimException If an error occurs.
   */
  @Test
  public void testSearchRequest() throws Exception
  {
    // Test case insensitivity
    SearchRequest searchRequest = JsonUtils.getObjectReader().
        forType(SearchRequest.class).
        readValue("""
            {
                 "schemas": [
                     "urn:ietf:params:scim:api:messages:2.0:SearchRequest"
                 ],
                 "attributes": ["displayName", "userName"],
                 "Filter":
                   "displayName sw \\"smith\\"",
                 "startIndex": 1,
                 "counT": 10
            }""");

    assertEquals(searchRequest.getAttributes(),
        StaticUtils.arrayToSet("displayName", "userName"));
    assertNull(searchRequest.getExcludedAttributes());
    assertEquals(searchRequest.getFilter(), "displayName sw \"smith\"");
    assertNull(searchRequest.getSortBy());
    assertNull(searchRequest.getSortOrder());
    assertEquals(searchRequest.getStartIndex(), Integer.valueOf(1));
    assertEquals(searchRequest.getCount(), Integer.valueOf(10));

    searchRequest =
        JsonUtils.getObjectReader().forType(SearchRequest.class).readValue("""
            {
                "schemas": [
                     "urn:ietf:params:scim:api:messages:2.0:SearchRequest"
                 ],
                 "excludedAttributes": ["displayName", "userName"],
                 "sortBy": "name.lastName",
                 "sortOrder": "descending"
            }""");

    assertNull(searchRequest.getAttributes());
    assertEquals(searchRequest.getExcludedAttributes(),
        StaticUtils.arrayToSet("displayName", "userName"));
    assertNull(searchRequest.getFilter());
    assertEquals(searchRequest.getSortBy(), "name.lastName");
    assertEquals(searchRequest.getSortOrder(), SortOrder.DESCENDING);
    assertNull(searchRequest.getStartIndex());
    assertNull(searchRequest.getCount());

    searchRequest = new SearchRequest(
        StaticUtils.arrayToSet("displayName", "userName"),
        StaticUtils.arrayToSet("addresses"),
        "userName eq \"test\"", "name.lastName",
        SortOrder.ASCENDING, 5, 100);

    String serialized = JsonUtils.getObjectWriter().
        writeValueAsString(searchRequest);
    assertEquals(JsonUtils.getObjectReader().forType(SearchRequest.class).
        readValue(serialized), searchRequest);
  }
}
