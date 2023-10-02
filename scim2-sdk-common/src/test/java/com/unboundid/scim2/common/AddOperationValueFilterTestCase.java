/*
 * Copyright 2023 Ping Identity Corporation
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.Address;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.PhoneNumber;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * This test class performs validation for ADD patch operations that contain
 * a value selection filter. An example request takes the following form:
 * <pre>
 *   {
 *     "op": "add",
 *     "path": "emails[type eq \"work\"].value",
 *     "value": "sissel@example.com"
 *   }
 * </pre>
 */
public class AddOperationValueFilterTestCase
{
  /**
   * Ensure that patch ADD operations with a value selection filter are not
   * permitted for filter types other than equality filters.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testAddOpValueFilterTypes() throws Exception
  {
    List<Filter> filterList = ImmutableList.of(
        Filter.ne("attr", "value"),
        Filter.co("attr", "value"),
        Filter.sw("attr", "value"),
        Filter.ew("attr", "value"),
        Filter.pr("attr"),
        Filter.gt("attr", "value"),
        Filter.ge("attr", "value"),
        Filter.lt("attr", "value"),
        Filter.le("attr", "value"),
        Filter.and("attr sw \"value\"", "attr eq \"value2\""),
        Filter.or("attr sw \"value\"", "attr eq \"value2\""),
        Filter.not("attr eq \"value\"")
    );

    for (Filter unsupportedFilter: filterList)
    {
      Path newPath = Path.fromString("roles[" + unsupportedFilter + "].value");
      PatchRequest request = createAddRequest(newPath, "newValue");
      assertThatThrownBy(() -> applyPatchRequest(request, new UserResource()))
          .isInstanceOf(BadRequestException.class);
    }
  }

  /**
   * Performs validation on add patch operations that contain a value selection
   * filter embedded in the path.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testBasic() throws Exception
  {
    Path path;
    PatchRequest request;
    UserResource resource = new UserResource();

    // Add a work email to a list of existing emails.
    resource.setEmails(
        new Email().setValue("existing@example.com").setType("home"),
        new Email().setValue("alternate@example.com").setType("personal")
    );
    assertThat(resource.getEmails()).hasSize(2);
    path = Path.fromString("emails[type eq \"work\"].value");
    request = createAddRequest(path, "sissel@example.com");
    resource = applyPatchRequest(request, resource);
    assertThat(resource.getEmails())
        .hasSize(3)
        .contains(new Email().setValue("sissel@example.com").setType("work"));

    // Set a value on a multi-valued attribute (addresses) when there are no
    // existing addresses on the resource.
    resource = new UserResource();
    assertThat(resource.getAddresses()).isNullOrEmpty();
    path = Path.fromString("addresses[type eq \"secret\"].streetAddress");
    request = createAddRequest(path, "The Batcave");
    resource = applyPatchRequest(request, resource);
    assertThat(resource.getAddresses())
        .hasSize(1)
        .containsOnly(
            new Address().setStreetAddress("The Batcave").setType("secret"));

    // Add a 'mobile' phone number to a user when an existing 'mobile' phone
    // number already exists. This should not be rejected.
    resource = new UserResource();
    resource.setPhoneNumbers(
        new PhoneNumber().setValue("+1 314-159-2653").setType("mobile")
    );
    path = Path.fromString("phoneNumbers[type eq \"mobile\"].value");
    request = createAddRequest(path, "+1 271-828-1828");
    resource = applyPatchRequest(request, resource);
    assertThat(resource.getPhoneNumbers())
        .hasSize(2)
        .containsExactly(
            new PhoneNumber().setValue("+1 314-159-2653").setType("mobile"),
            new PhoneNumber().setValue("+1 271-828-1828").setType("mobile"));

    // Add two photos with the same 'type' value within a single patch request.
    resource = new UserResource();
    path = Path.fromString("photos[type eq \"thumbnail\"].value");
    request = new PatchRequest(
        PatchOperation.add(path, TextNode.valueOf("https://example.com/1.png")),
        PatchOperation.add(path, TextNode.valueOf("https://example.com/2.png"))
    );
    resource = applyPatchRequest(request, resource);
    assertThat(resource.getPhotos())
        .filteredOn(photo -> photo.getType().equals("thumbnail"))
        .hasSize(2);

    // Only a single value selection filter should be permitted.
    Path multipleFilter = Path.fromString(
        "emails[type eq \"work\"].value[display eq \"Special Email\"]");
    assertThatThrownBy(() -> {
      PatchRequest req = createAddRequest(multipleFilter, "bob@example.com");
      applyPatchRequest(req, new UserResource());
    }).isInstanceOf(IllegalArgumentException.class)
      .hasCauseInstanceOf(BadRequestException.class);

    // Try multiple value selection filters again, but put 'value' in the filter
    // instead.
    Path otherFilter = Path.fromString(
        "emails[type eq \"work\"].display[value eq \"bob@example.com\"]");
    assertThatThrownBy(() -> {
      PatchRequest req = createAddRequest(otherFilter, "Special Email");
      applyPatchRequest(req, new UserResource());
    }).isInstanceOf(BadRequestException.class)
      .hasMessageContaining(
          "only allowed to contain a single value selection filter");

    // Attempt a path with a value selection filter, but without a second
    // element (i.e., path.size() == 1).
    Path singleElement = Path.fromString("ims[type eq \"skype\"]");
    PatchRequest singleElementRequest = createAddRequest(singleElement, "invalid");
    assertThatThrownBy(() -> applyPatchRequest(singleElementRequest, new UserResource()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("needs to be 'attribute[filter].subAttribute'");

    // Assemble an invalid patch request by placing the value in an array, as
    // opposed to providing it as a single string value.
    path = Path.fromString("emails[type eq \"home\"].value");
    PatchRequest improperFormat = new PatchRequest(
        PatchOperation.addStringValues(path, "home@example.com")
    );
    assertThatThrownBy(() -> applyPatchRequest(improperFormat, new UserResource()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("cannot set the 'value' field to an array");

    // Attempt using a value selection filter on an attribute that is
    // single-valued.
    UserResource userWithLanguage = new UserResource().setPreferredLanguage("nb-NO");
    Path singleValuedAttr =
        Path.fromString("preferredLanguage[type eq \"work\"].value");
    PatchRequest invalidAttr = createAddRequest(singleValuedAttr, "en-US");
    assertThatThrownBy(() -> applyPatchRequest(invalidAttr, userWithLanguage))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("could not be processed")
        .hasMessageContaining("value selection filter was provided");

    // Perform another add on a single-valued attribute when it does not have a
    // pre-existing value.
    PatchRequest invalidAttr2 = createAddRequest(singleValuedAttr, "en-US");
    assertThatThrownBy(() -> applyPatchRequest(invalidAttr2, new UserResource()))
        .isInstanceOf(JsonProcessingException.class);

    // Value filters must be the first element in the path.
    Path nestedFilter = Path.fromString("parent.examples[type eq \"best\"].value");
    assertThatThrownBy(() -> createAddRequest(nestedFilter, "value"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasCauseInstanceOf(BadRequestException.class)
        .hasMessageContaining("Path cannot target sub-attributes");

    // Providing an "empty" filter should not be allowed.
    assertThatThrownBy(() -> {
      Path emptyFilterPath = Path.fromString("entitlements[].value");
      PatchRequest emptyFilterReq = createAddRequest(emptyFilterPath, "ent1");
      applyPatchRequest(emptyFilterReq, new UserResource());
    }).isInstanceOf(BadRequestException.class);
  }

  /**
   * Test the appearance of a User resource after a patch request with a value
   * selection filter has updated the resource. In particular, this test
   * verifies that the attribute specified within the filter is listed second
   * in the JSON. For example, for an attribute path of
   * {@code emails[type eq "work"].value}, the {@code "type": "work"} field in
   * the JSON should be printed after the {@code value} field:
   * <pre>
   *   {
   *     "value": "sissel@example.com",
   *     "type": "work"
   *   }
   * </pre>
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testDeserializedObject() throws Exception
  {
    final String rawExpectedResult = "{"
        + "  \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\" ],"
        + "  \"emails\" : [ {"
        + "    \"value\" : \"sissel@example.com\","
        + "    \"type\" : \"work\""
        + "  }, {"
        + "    \"value\" : \"yomiel@example.com\","
        + "    \"type\" : \"home\""
        + "  } ]"
        + "}";

    String expected = JsonUtils.getObjectReader()
        .readTree(rawExpectedResult).toString();

    UserResource resource = new UserResource().setEmails(
        new Email().setType("work").setValue("sissel@example.com")
    );
    Path path = Path.fromString("emails[type eq \"home\"].value");
    PatchRequest request = createAddRequest(path, "yomiel@example.com");
    resource = applyPatchRequest(request, resource);

    // Deserialize the new resource into JSON.
    String resourceString = JsonUtils.valueToNode(resource).toString();
    assertThat(resourceString).isEqualTo(expected);
  }

  /**
   * This helper method is shorthand for a new patch request that contains a
   * single add operation with a string value.
   */
  private static PatchRequest createAddRequest(Path path, String value)
  {
    return new PatchRequest(PatchOperation.add(path, TextNode.valueOf(value)));
  }

  /**
   * This method applies a patch request to a UserResource object and returns
   * a new UserResource reflecting the modifications.
   */
  private static UserResource applyPatchRequest(PatchRequest request,
                                                UserResource userResource)
      throws JsonProcessingException, ScimException
  {
    GenericScimResource user = userResource.asGenericScimResource();
    request.apply(user);
    return JsonUtils.nodeToValue(user.getObjectNode(), UserResource.class);
  }
}
