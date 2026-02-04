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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.exceptions.ForbiddenException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.exceptions.ServerErrorException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.unboundid.scim2.common.bulk.BulkOpType.*;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_CREATED;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_INTERNAL_ERROR;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_NOT_FOUND;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_NO_CONTENT;
import static com.unboundid.scim2.common.bulk.BulkOperationResult.HTTP_STATUS_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Tests for {@link BulkOperationResult}.
 */
public class BulkOperationResultTest
{
  // An example location URI for test use.
  private static final String location =
      "https://example.com/v2/Users/exampleResourceID";

  /**
   * Ensure {@link BulkOperationResult} objects can be successfully created from
   * JSON strings.
   */
  @Test
  public void testDeserialization() throws Exception
  {
    final ObjectReader reader = JsonUtils.getObjectReader()
        .forType(BulkOperationResult.class);

    String postJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "POST",
          "bulkId": "qwerty",
          "version": "W/\\"4weymrEsh5O6cAEK\\"",
          "status": "201"
        }""";
    BulkOperationResult result = reader.readValue(postJson);
    assertThat(result.getLocation())
        .isEqualTo("https://example.com/v2/Users/92b...87a");
    assertThat(result.getMethod()).isEqualTo(POST);
    assertThat(result.getBulkId()).isEqualTo("qwerty");
    assertThat(result.getVersion()).contains("4weymrEsh5O6cAEK");
    assertThat(result.getResponse()).isNull();
    assertThat(result.getResponseAsScimResource()).isNull();
    assertThat(result.getStatus()).isEqualTo("201");

    // The associated "statusInt" value should not be printed in the JSON.
    assertThat(result.toString()).doesNotContain("statusInt");

    // Construct the same result via code and ensure it serializes into the same
    // JSON.
    BulkOperationResult created = new BulkOperationResult(
        POST,
        HTTP_STATUS_CREATED,
        "https://example.com/v2/Users/92b...87a",
        null,
        "qwerty",
        "W/\"4weymrEsh5O6cAEK\""
    );
    String reformattedJSON = reader.readTree(postJson).toPrettyString();
    assertThat(created.toString()).isEqualTo(reformattedJSON);

    // Test a JSON with a "status.code" field. This appears in the RFC in one
    // location, so the SCIM SDK should parse this gracefully in case a client
    // uses it.
    String nestedStatusJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "PUT",
          "status": {
              "code": "200"
          }
        }""";
    result = reader.readValue(nestedStatusJson);
    assertThat(result.getStatus()).isEqualTo("200");
    assertThat(result.getMethod()).isEqualTo(PUT);
    assertThat(result.getLocation())
        .isEqualTo("https://example.com/v2/Users/92b...87a");
    assertThat(result.getBulkId()).isNull();
    assertThat(result.getVersion()).isNull();
    assertThat(result.getResponse()).isNull();
    assertThat(result.getResponseAsScimResource()).isNull();

    // Other forms for "status" should not be permitted.
    String invalidStatusJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "POST",
          "status": {
              "customCode": "201"
        }""";
    assertThatThrownBy(() -> reader.readValue(invalidStatusJson))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Could not parse the 'status' field");

    // The "status" field must be a numeric value.
    String notANumber = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "PUT",
          "status": "OK"
        }""";
    assertThatThrownBy(() -> reader.readValue(notANumber))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Could not convert 'OK' to an integer");

    String notANumber2 = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "PUT",
          "status": {
              "code": "OK"
          }
        }""";
    assertThatThrownBy(() -> reader.readValue(notANumber2))
        .isInstanceOf(JsonMappingException.class)
        .hasMessageContaining("Could not convert 'OK' to an integer");

    // Test the case where unknown properties are present in the JSON. This
    // should not cause the request to fail.
    String unknownPropertyJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "DELETE",
          "status": "204",
          "path": "pathIsOnlyDefinedForBulkOperations",
          "unknownNonStandardField": "shouldNotCauseException"
        }""";
    result = reader.readValue(unknownPropertyJson);
    assertThat(result.getMethod()).isEqualTo(DELETE);
    assertThat(result.getLocation())
        .isEqualTo("https://example.com/v2/Users/92b...87a");
    assertThat(result.getBulkId()).isNull();
    assertThat(result.getVersion()).isNull();
    assertThat(result.getResponse()).isNull();
    assertThat(result.getResponseAsScimResource()).isNull();
    assertThat(result.getStatus()).isEqualTo("204");
  }

  /**
   * Validate construction of new bulk operation results.
   */
  @Test
  public void testConstructors() throws Exception
  {
    // Create a BulkOperationResult from the contents of a bulk operation.
    BulkOperation sourceOp = BulkOperation.post("/Users", new UserResource())
        .setBulkId("qwerty");
    BulkOperationResult result = new BulkOperationResult(sourceOp,
            "200",
            "https://example.com/v2/Users/" + UUID.randomUUID()
    );

    // Validate the fields.
    assertThat(result.getMethod()).isEqualTo(POST);
    assertThat(result.getStatus()).isEqualTo("200");
    assertThat(result.getLocation()).startsWith("https://example.com/v2/Users");
    assertThat(result.getResponse()).isNull();
    assertThat(result.getResponseAsScimResource()).isNull();
    assertThat(result.getBulkId()).isEqualTo("qwerty");
    assertThat(result.getVersion()).isNull();

    // Test the basic constructor.
    result = new BulkOperationResult(
        DELETE,
        HTTP_STATUS_NO_CONTENT,
        location,
        null,
        null,
        null
    );
    assertThat(result.getMethod()).isEqualTo(DELETE);
    assertThat(result.getStatus()).isEqualTo("204");
    assertThat(result.getLocation()).isEqualTo(location);
    assertThat(result.getResponse()).isNull();
    assertThat(result.getResponseAsScimResource()).isNull();
    assertThat(result.getBulkId()).isNull();
    assertThat(result.getVersion()).isNull();

    // Ensure that the bulk ID is always null for non-POST operations.
    // TODO: Considering removing this if there's not a way to set it via the
    //       SDK. Is it possible to set via serialization? Or should this be
    //       enforced in the other constructor? I'm leaning toward this.



    // Ensure that locations may not be null for non-POST requests.
    List<BulkOperation> operationList = List.of(
        BulkOperation.put(location, new UserResource()),
        BulkOperation.patch(location, PatchOperation.remove("userName")),
        BulkOperation.delete(location)
    );
    for (BulkOperation op : operationList)
    {
      assertThatThrownBy(() -> new BulkOperationResult(
          op, HTTP_STATUS_INTERNAL_ERROR, null))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("The 'location' of a bulk response")
              .hasMessageContaining("must be defined");
    }

    // Ensure a failed POST operation is permitted to set a null location.
    new BulkOperationResult(
        BulkOperation.post("https://example.com/v2/Groups", new GroupResource()),
        HTTP_STATUS_INTERNAL_ERROR,
        null
    );
  }

  /**
   * When client applications are reading the contents of a bulk operation
   * result, the {@code response} data in the JSON payload is most useful when
   * it can easily be fetched as a POJO. This is supported by the
   * {@link BulkOperationResult#getResponseAsScimResource()} method, which is
   * validated by this test.
   */
  @Test
  public void testGettingResponse()
  {
    // TODO
  }

  /**
   * Validate the setter methods.
   */
  @Test
  public void testSetters()
  {
    BulkOperationResult result = new BulkOperationResult(
        BulkOpType.POST, HTTP_STATUS_OK, location, null, null, null);

    // Overwrite the location.
    result.setLocation("asdf");
    assertThat(result.getLocation()).isEqualTo("asdf");

    // Set all remaining fields.
    result.setBulkId("qwerty")
        .setVersion("newVersion")
        .setStatus(HTTP_STATUS_NOT_FOUND)
        .setResponse(JsonUtils.getJsonNodeFactory().objectNode());
    assertThat(result.getBulkId()).isEqualTo("qwerty");
    assertThat(result.getVersion()).isEqualTo("newVersion");
    assertThat(result.getStatus()).isEqualTo("404");

    // An empty object node should be returned as an empty GenericScimResource.
    assertThat(result.getResponse()).isNotNull().isEmpty();
    assertThat(result.getResponseAsScimResource())
        .isEqualTo(new GenericScimResource());

    // Use the other setter variants that accept other object types.
    result.setResponse(new UserResource())
        .setStatus(418);
    assertThat(result.getResponse()).isNotNull().isNotEmpty();
    assertThat(result.getStatus()).isEqualTo("418");

    // Passing a null UserResource should behave identically to the other
    // variant.
    UserResource resource = null;
    result.setResponse(resource);
    assertThat(result.getResponse()).isNull();
    assertThat(result.getResponseAsScimResource()).isNull();
  }

  /**
   * Validate passing {@code null} arguments to the constructors and methods.
   */
  @SuppressWarnings("DataFlowIssue")
  @Test
  public void testSettingNull()
  {
    // Check the constructors for null operations and statuses.
    assertThatThrownBy(() ->
        new BulkOperationResult((BulkOperation) null, "500", location))
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() ->
        new BulkOperationResult(BulkOperation.delete("/val"), null, location))
        .isInstanceOf(NullPointerException.class);

    // Create a result.
    BulkOperationResult result = new BulkOperationResult(
        PUT, "200", location, null, null, null);

    // Setting the status should never accept null.
    assertThatThrownBy(() -> result.setStatus((String) null))
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> result.setStatus((Integer) null))
        .isInstanceOf(NullPointerException.class);

    // setLocation() should not accept null for non-POST requests.
    for (var opType : List.of(PUT, PATCH, DELETE))
    {
      BulkOperationResult locationResult = new BulkOperationResult(
          opType, "200", location, null, null, null);
      assertThatThrownBy(() -> locationResult.setLocation(null))
          .isInstanceOf(NullPointerException.class);
    }

    // Ensure a null location is permissible for a POST.
    new BulkOperationResult(POST, "429", location, null, null, null)
        .setLocation(null);
  }

  /**
   * Validate the {@link BulkOperationResult#error} methods.
   */
  @Test
  public void testErrors() throws Exception
  {
    // Validate the method that accepts an operation type.
    BulkOperationResult error = BulkOperationResult.error(
        DELETE, new ServerErrorException("Internal Server Error"), location);
    assertThat(error.getMethod()).isEqualTo(DELETE);
    assertThat(error.getLocation()).isEqualTo(location);
    assertThat(error.getBulkId()).isNull();
    assertThat(error.getVersion()).isNull();
    assertThat(error.getStatus()).isEqualTo("500");
    assertThat(error.getStatusInt()).isEqualTo(500);
    assertThat(error.getResponse()).isNotNull();

    // Use the ScimResource-based object for validation since it is easier to
    // work with.
    assertThat(error.getResponseAsScimResource())
        .isInstanceOf(ErrorResponse.class)
        .satisfies(e -> {
          var response = (ErrorResponse) e;
          assertThat(response.getStatus()).isEqualTo(500);
          assertThat(response.getDetail()).isEqualTo("Internal Server Error");
        });

    // Validate the method that accepts a bulk operation.
    error = BulkOperationResult.error(
        BulkOperation.patch(location, PatchOperation.replace("userName", "a")),
        new ForbiddenException("User is unauthorized."),
        location);
    assertThat(error.getMethod()).isEqualTo(PATCH);
    assertThat(error.getLocation()).isEqualTo(location);
    assertThat(error.getBulkId()).isNull();
    assertThat(error.getVersion()).isNull();
    assertThat(error.getStatus()).isEqualTo("403");
    assertThat(error.getStatusInt()).isEqualTo(403);
    assertThat(error.getResponse()).isNotNull();
    assertThat(error.getResponseAsScimResource())
        .isInstanceOf(ErrorResponse.class)
        .satisfies(e -> {
          var response = (ErrorResponse) e;
          assertThat(response.getStatus()).isEqualTo(403);
          assertThat(response.getDetail()).isEqualTo("User is unauthorized.");
        });

    // Ensure that a bulk POST operation retains the bulk ID.
    String usersURI = "https://example.com/v2/Users";
    error = BulkOperationResult.error(
        BulkOperation.post(usersURI, new UserResource()).setBulkId("Bulkley"),
        new ScimException(503, "Service is unavailable."),
        location);
    assertThat(error.getBulkId()).isEqualTo("Bulkley");

    // Even though a location value was provided, the SCIM SDK should ensure
    // that the result location should be null. This is because a failed POST
    // means that the resource was not created, so it cannot have a location.
    assertThat(error.getLocation()).isNull();

    // Validate all remaining fields.
    assertThat(error.getMethod()).isEqualTo(POST);
    assertThat(error.getVersion()).isNull();
    assertThat(error.getStatus()).isEqualTo("503");
    assertThat(error.getStatusInt()).isEqualTo(503);
    assertThat(error.getResponse()).isNotNull();
    assertThat(error.getResponseAsScimResource())
        .isInstanceOf(ErrorResponse.class)
        .satisfies(e -> {
          var response = (ErrorResponse) e;
          assertThat(response.getStatus()).isEqualTo(503);
          assertThat(response.getDetail()).isEqualTo("Service is unavailable.");
        });
  }

  /**
   * Test {@link BulkOperationResult#equals(Object)}.
   */
  @Test
  @SuppressWarnings("all")
  public void testEquals()
  {
    BulkOperationResult first = new BulkOperationResult(
        PATCH,
        HTTP_STATUS_OK,
        "https://example.com/v2/Users/userID",
        JsonUtils.getJsonNodeFactory().objectNode(),
        null,
        "versionTag"
    );
    BulkOperationResult second = first.copy();

    assertThat(first.equals(second)).isTrue();
    assertThat(first.equals(first)).isTrue();
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
    assertThat(first.equals(null)).isFalse();
    assertThat(first.equals(BulkOperation.delete("/Users/userID"))).isFalse();

    second.setLocation("other");
    assertThat(first.equals(second)).isFalse();
    second = first.copy();
    second.setVersion("otherVersion");
    assertThat(first.equals(second)).isFalse();
    second = first.copy();

    second.setStatus("418");
    assertThat(first.equals(second)).isFalse();
    second = first.copy();

    var node = JsonUtils.getJsonNodeFactory().objectNode();
    node.put("id", "value");
    second.setResponse(node);
    assertThat(first.equals(second)).isFalse();
    second = first.copy();

    // Result objects of different types should not be equal.
    BulkOperationResult other = new BulkOperationResult(
        PUT,
        HTTP_STATUS_OK,
        "https://example.com/v2/Users/userID",
        JsonUtils.getJsonNodeFactory().objectNode(),
        "qwerty",
        "versionTag"
    );
    assertThat(first.equals(other)).isFalse();

    // Test the bulk ID. This is only relevant to a POST request.
    BulkOperationResult post = new BulkOperationResult(
        POST,
        HTTP_STATUS_CREATED,
        "https://example.com/v2/Users/userID",
        JsonUtils.getJsonNodeFactory().objectNode(),
        "qwerty",
        null
    );
    BulkOperationResult postCopy = post.copy();
    assertThat(post.equals(postCopy)).isTrue();
    postCopy.setBulkId("otherID");
    assertThat(post.equals(postCopy)).isFalse();
  }

  /**
   * Ensures that copying a bulk operation result produces a new, equivalent
   * object.
   */
  @Test
  public void testCopy()
  {
    BulkOperationResult original = new BulkOperationResult(
        PUT,
        HTTP_STATUS_OK,
        "https://example.com/v2/Users/userID",
        JsonUtils.getJsonNodeFactory().objectNode(),
        "qwerty",
        "versionTag"
    );
    BulkOperationResult copy = original.copy();

    // The references should not match, but the objects should be equal.
    assertThat(original == copy).isFalse();
    assertThat(original).isEqualTo(copy);

    // The copied response ObjectNode should be a separate object.
    assertThat(original.getResponse() == copy.getResponse()).isFalse();
    assertThat(original.getResponse()).isEqualTo(copy.getResponse());

    // Null response ObjectNodes should be handled properly when copied.
    BulkOperationResult delete = new BulkOperationResult(
        DELETE,
        HTTP_STATUS_NO_CONTENT,
        "https://example.com/v2/Users/userID",
        null,
        "qwerty",
        "versionTag"
    );
    assertThat(delete.copy()).isEqualTo(delete);
    assertThat(delete.copy() == delete).isFalse();
  }

  /**
   * Test {@link BulkOperationResult#setAny}.
   */
  @Test
  public void testSetAny()
  {
    // Bulk operations should always ignore unknown fields when deserializing,
    // so the @JsonAnySetter method should effectively be a no-op. Ensure this
    // is the case by showing that valid keys/fields make no update.
    BulkOperationResult result = new BulkOperationResult(
        POST,
        "201",
        "https://example.com/Users/userID",
        null,
        null,
        null
    );
    var result2 = result.copy();
    result.setAny("method", TextNode.valueOf("fieldDoesNotExist"));
    assertThat(result).isEqualTo(result2);

    result.setAny("status", TextNode.valueOf("200"));
    assertThat(result.getStatus())
        .isNotEqualTo("200")
        .isEqualTo("201");
    assertThat(result).isEqualTo(result2);
  }
}
