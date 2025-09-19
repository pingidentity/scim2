package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.unboundid.scim2.common.messages.BulkOpType.*;
import static com.unboundid.scim2.common.messages.BulkOperationResult.HTTP_STATUS_CREATED;
import static com.unboundid.scim2.common.messages.BulkOperationResult.HTTP_STATUS_INTERNAL_ERROR;
import static com.unboundid.scim2.common.messages.BulkOperationResult.HTTP_STATUS_NOT_FOUND;
import static com.unboundid.scim2.common.messages.BulkOperationResult.HTTP_STATUS_NO_CONTENT;
import static com.unboundid.scim2.common.messages.BulkOperationResult.HTTP_STATUS_OK;
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

  @Test
  public void testSerialization() throws Exception
  {
    final ObjectReader reader = JsonUtils.getObjectReader();

    String postJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "POST",
          "bulkId": "qwerty",
          "version": "W\\/\\"4weymrEsh5O6cAEK\\"",
          "status": "201"
        }""";
    BulkOperationResult result = reader.forType(BulkOperationResult.class)
        .readValue(postJson);
    assertThat(result.getLocation())
        .isEqualTo("https://example.com/v2/Users/92b...87a");
    assertThat(result.getMethod()).isEqualTo(POST);
    assertThat(result.getBulkId()).isEqualTo("qwerty");
    assertThat(result.getVersion()).contains("4weymrEsh5O6cAEK");
    assertThat(result.getResponse()).isNull();
    assertThat(result.getStatus()).isEqualTo("201");

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
    // location, so the SCIM SDK should parse this gracefully.
    // TODO: Make it configurable to serialize into this
    String nestedStatusJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "PUT",
          "status": {
              "code": "201"
          }
        }""";
    result = reader.forType(BulkOperationResult.class)
        .readValue(nestedStatusJson);
    assertThat(result.getStatus()).isEqualTo("201");
    assertThat(result.getMethod()).isEqualTo(PUT);
    assertThat(result.getLocation())
        .isEqualTo("https://example.com/v2/Users/92b...87a");
    assertThat(result.getBulkId()).isNull();
    assertThat(result.getVersion()).isNull();
    assertThat(result.getResponse()).isNull();

    // Other forms for "status" should not be permitted.
    String invalidStatusJson = """
        {
          "location": "https://example.com/v2/Users/92b...87a",
          "method": "POST",
          "status": {
              "customCode": "201"
        }""";
    assertThatThrownBy(() -> reader.forType(BulkOperationResult.class)
        .readValue(invalidStatusJson))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("Could not parse the 'status' field");

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
    result = reader.forType(BulkOperationResult.class)
        .readValue(unknownPropertyJson);
    assertThat(result.getMethod()).isEqualTo(DELETE);
    assertThat(result.getLocation())
        .isEqualTo("https://example.com/v2/Users/92b...87a");
    assertThat(result.getBulkId()).isNull();
    assertThat(result.getVersion()).isNull();
    assertThat(result.getResponse()).isNull();
    assertThat(result.getStatus()).isEqualTo("204");
  }

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
    assertThat(result.getBulkId()).isNull();
    assertThat(result.getVersion()).isNull();

    // Check the NotNull fields.
    assertThatThrownBy(() ->
        new BulkOperationResult(null, "500", location))
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() ->
        new BulkOperationResult(sourceOp, null, location))
        .isInstanceOf(NullPointerException.class);

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



//    assertThat(result.getBulkId()).isNull();
  }

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
    assertThat(result.getResponse()).isNotNull().isEmpty();

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
  }

  @Test
  public void testSettingNull()
  {
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

  @Test
  public void testErrors() throws Exception
  {
    // Validate the method that accepts an operation type.
    BulkOperationResult error = BulkOperationResult.error(
        DELETE, new ErrorResponse(500), location);
    assertThat(error.getMethod()).isEqualTo(DELETE);
    assertThat(error.getLocation()).isEqualTo(location);
    assertThat(error.getBulkId()).isNull();
    assertThat(error.getVersion()).isNull();
    assertThat(error.getStatus()).isEqualTo("500");
    assertThat(error.getResponse()).isNotNull();
    assertThat(error.getResponse().path("schemas").path(0).asText())
        .isEqualTo("urn:ietf:params:scim:api:messages:2.0:Error");
    assertThat(error.getResponse().path("status").asText()).isEqualTo("500");

//    assertThat(JsonUtils.error.getResponse())


    // Validate the method that accepts a bulk operation.
    error = BulkOperationResult.error(
        BulkOperation.patch(location, PatchOperation.replace("userName", "a")),
        new ErrorResponse(403),
        location);
    assertThat(error.getMethod()).isEqualTo(PATCH);
    assertThat(error.getLocation()).isEqualTo(location);
    assertThat(error.getBulkId()).isNull();
    assertThat(error.getVersion()).isNull();
    assertThat(error.getStatus()).isEqualTo("403");
    assertThat(error.getResponse()).isNotNull();
    assertThat(error.getResponse().path("schemas").path(0).asText())
        .isEqualTo("urn:ietf:params:scim:api:messages:2.0:Error");
    assertThat(error.getResponse().path("status").asText()).isEqualTo("403");


    // Ensure that a bulk POST operation retains the bulk ID.
    String usersURI = "https://example.com/v2/Users";
    error = BulkOperationResult.error(
        BulkOperation.post(usersURI, new UserResource()).setBulkId("Bulkley"),
        new ErrorResponse(503),
        location);
    assertThat(error.getBulkId()).isEqualTo("Bulkley");

    // Even though a location value was passed in, the result should have a
    // null location. A failed POST means that the resource was not created and
    // does not exist, so it cannot have a location.
    assertThat(error.getLocation()).isNull();

    // Validate all remaining fields.
    assertThat(error.getMethod()).isEqualTo(POST);
    assertThat(error.getVersion()).isNull();
    assertThat(error.getStatus()).isEqualTo("503");
    assertThat(error.getResponse()).isNotNull();
    assertThat(error.getResponse().path("schemas").path(0).asText())
        .isEqualTo("urn:ietf:params:scim:api:messages:2.0:Error");
    assertThat(error.getResponse().path("status").asText()).isEqualTo("503");
  }

  @Test
  @SuppressWarnings("all")
  public void testEquals()
  {
    BulkOperationResult first = new BulkOperationResult(
        PATCH,
        HTTP_STATUS_OK,
        "https://example.com/v2/Users/userID",
        JsonUtils.getJsonNodeFactory().objectNode(),
        "qwerty",
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

    second.setBulkId("otherID");
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
  }

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
}
