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

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_CREATED;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_NO_CONTENT;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_OK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link BulkResponse} class.
 */
public class BulkResponseTest
{
  /**
   * Validation for BulkResponse constructors and general usage.
   */
  @Test
  public void testBasic()
  {
    // The list constructor should treat null as an empty list.
    assertThat(new BulkResponse(null).getOperations())
        .isNotNull()
        .isEmpty();

    BulkOperationResult result = new BulkOperationResult(BulkOpType.POST,
        HTTP_STATUS_CREATED,
        "https://example.com/v2/Users/92b725cd",
        null,
        "qwerty",
        "W/\"4weymrEsh5O6cAEK\"");

    BulkOperationResult result2 = new BulkOperationResult(BulkOpType.POST,
        HTTP_STATUS_CREATED,
        "https://example.com/v2/Groups/e9e30dba",
        null,
        "ytrewq",
        "W/\"lha5bbazU3fNvfe5\"");

    // The list constructor should accept single-valued lists.
    assertThat(new BulkResponse(List.of(result)).getOperations())
        .hasSize(1)
        .first().isEqualTo(result);

    // The list constructor should filter null values.
    assertThat(new BulkResponse(Arrays.asList(result, null)).getOperations())
        .hasSize(1)
        .first().isEqualTo(result);

    // Create a general bulk response with two results. The returned list should
    // always be immutable.
    BulkResponse response = new BulkResponse(List.of(result, result2));
    assertThat(response.getOperations())
        .isUnmodifiable()
        .hasSize(2)
        .containsExactly(result, result2);

    // The alternate constructor should be able to create an equivalent bulk
    // response in a succinct manner.
    assertThat(new BulkResponse(result, result2)).isEqualTo(response);

    // It should be possible to iterate over the bulk response in an enhanced
    // for-loop.
    for (BulkOperationResult r : response)
    {
      assertThat(r.getMethod()).isEqualTo(BulkOpType.POST);
    }
  }

  /**
   * Ensures bulk responses can be serialized and deserialized successfully.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String rawJSONString = """
        {
          "schemas": ["urn:ietf:params:scim:api:messages:2.0:BulkResponse"],
          "Operations": [
            {
              "location": "https://example.com/v2/Users/92b725cd",
              "method": "POST",
              "bulkId": "qwerty",
              "version": "W/\\"4weymrEsh5O6cAEK\\"",
              "status": "201"
            },
            {
              "location": "https://example.com/v2/Groups/e9e30dba",
              "method": "POST",
              "bulkId": "ytrewq",
              "version": "W/\\"lha5bbazU3fNvfe5\\"",
              "status": "201"
            }
          ]
        }""";

    // Reformat the string in a standardized form.
    final String jsonResponse = JsonUtils.getObjectReader()
        .readTree(rawJSONString).toString();

    // Create the same object as a POJO.
    BulkResponse pojoResponse = new BulkResponse(
        new BulkOperationResult(BulkOpType.POST,
            HTTP_STATUS_CREATED,
            "https://example.com/v2/Users/92b725cd",
            null,
            "qwerty",
            "W/\"4weymrEsh5O6cAEK\""),
        new BulkOperationResult(BulkOpType.POST,
            HTTP_STATUS_CREATED,
            "https://example.com/v2/Groups/e9e30dba",
            null,
            "ytrewq",
            "W/\"lha5bbazU3fNvfe5\"")
    );

    // Ensure serializing an object into JSON matches the expected form.
    var serial = JsonUtils.getObjectWriter().writeValueAsString(pojoResponse);
    assertThat(serial).isEqualTo(jsonResponse);

    // Ensure deserializing a string into an object is successful.
    BulkResponse deserialized = JsonUtils.getObjectReader()
        .forType(BulkResponse.class).readValue(jsonResponse);
    assertThat(deserialized).isEqualTo(pojoResponse);
  }

  /**
   * Test {@link BulkResponse#equals(Object)}.
   */
  @SuppressWarnings("all")
  @Test
  public void testEquals()
  {
    BulkOperationResult result1 = new BulkOperationResult(BulkOpType.DELETE,
        HTTP_STATUS_NO_CONTENT,
        "https://example.com/v2/Users/fa1afe1",
        null,
        null,
        "W/\"4weymrEsh5O6cAEK\"");
    BulkOperationResult result2 = new BulkOperationResult(BulkOpType.PUT,
        HTTP_STATUS_OK,
        "https://example.com/v2/Users/5ca1ab1e",
        null,
        null,
        null);

    // Bulk responses should be equal to themselves, as well as other response
    // objects with the same results.
    BulkResponse response = new BulkResponse(result1, result2);
    assertThat(response == response).isTrue();
    assertThat(response.equals(response)).isTrue();
    BulkResponse response2 = new BulkResponse(result1, result2);
    assertThat(response == response2).isFalse();
    assertThat(response.equals(response2)).isTrue();
    assertThat(response2.equals(response)).isTrue();

    // Null references should never be equivalent.
    assertThat(response.equals(null)).isFalse();

    // Bulk responses should not be equivalent if the operations do not match.
    // The hash code should also be different.
    BulkResponse emptyResponse = new BulkResponse(result1);
    assertThat(response.equals(emptyResponse)).isFalse();
    assertThat(response.hashCode()).isNotEqualTo(emptyResponse.hashCode());

    // Validate the order of results within a bulk response.
    BulkResponse differentOrder = new BulkResponse(result2, result1);
    assertThat(response.equals(differentOrder)).isFalse();
    assertThat(response.hashCode()).isNotEqualTo(differentOrder.hashCode());
  }
}
