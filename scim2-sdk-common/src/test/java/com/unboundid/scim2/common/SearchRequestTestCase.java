/*
 * Copyright 2015-2021 Ping Identity Corporation
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
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
  public void testSearchRequest() throws IOException, ScimException
  {
    SearchRequest searchRequest = JsonUtils.getObjectReader().
        forType(SearchRequest.class).
        readValue("{\n" +
            "     \"schemas\": [" +
            "\"urn:ietf:params:scim:api:messages:2.0:SearchRequest\"],\n" +
            "     \"attributes\": [\"displayName\", \"userName\"],\n" +
            // Test case insensitivity
            "     \"Filter\":\n" +
            "       \"displayName sw \\\"smith\\\"\",\n" +
            "     \"startIndex\": 1,\n" +
            "     \"counT\": 10\n" +
            "}");

    assertEquals(searchRequest.getAttributes(),
        StaticUtils.arrayToSet("displayName", "userName"));
    assertEquals(searchRequest.getExcludedAttributes(), null);
    assertEquals(searchRequest.getFilter(), "displayName sw \"smith\"");
    assertEquals(searchRequest.getSortBy(), null);
    assertEquals(searchRequest.getSortOrder(), null);
    assertEquals(searchRequest.getStartIndex(), new Integer(1));
    assertEquals(searchRequest.getCount(), new Integer(10));

    searchRequest = JsonUtils.getObjectReader().
        forType(SearchRequest.class).
        readValue("{\n" +
            "     \"schemas\": [" +
            "\"urn:ietf:params:scim:api:messages:2.0:SearchRequest\"],\n" +
            "     \"excludedAttributes\": [\"displayName\", \"userName\"],\n" +
            "     \"sortBy\": \"name.lastName\",\n" +
            "     \"sortOrder\": \"descending\"\n" +
            "}");

    assertEquals(searchRequest.getAttributes(), null);
    assertEquals(searchRequest.getExcludedAttributes(),
        StaticUtils.arrayToSet("displayName", "userName"));
    assertEquals(searchRequest.getFilter(), null);
    assertEquals(searchRequest.getSortBy(), "name.lastName");
    assertEquals(searchRequest.getSortOrder(), SortOrder.DESCENDING);
    assertEquals(searchRequest.getStartIndex(), null);
    assertEquals(searchRequest.getCount(), null);

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
