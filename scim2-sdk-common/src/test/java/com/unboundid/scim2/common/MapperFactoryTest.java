/*
 * Copyright 2024-2025 Ping Identity Corporation
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.filters.FilterType;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.MapperFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
   * Tests a custom {@link com.fasterxml.jackson.databind.MapperFeature} setting
   * on a mapper factory.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testCustomMapperFeatures() throws Exception
  {
    // A SCIM resource with the attributes (except 'schema') sorted
    // alphabetically.
    final String rawJSONString = """
        {
          "schemas" : [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "displayName" : "Kendrick Lamar",
          "emails" : [{ "value" : "NLU@example.com" }],
          "userName" : "K.Dot"
        }""";

    // Reformat the string in a standardized form.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(rawJSONString).toString();

    UserResource user = new UserResource()
        .setUserName("K.Dot")
        .setEmails(new Email().setValue("NLU@example.com"))
        .setDisplayName("Kendrick Lamar");

    // By default, the 'userName' field appears before fields like 'email'.
    // Verify that the serialized user resource does not list attributes in
    // alphabetical order.
    String userJSON = JsonUtils.getObjectWriter().writeValueAsString(user);
    assertThat(userJSON).isNotEqualTo(expectedJSON);

    // Update the object mapper to sort the elements of a SCIM resource.
    MapperFactory factory = new MapperFactory().setMapperCustomFeatures(
        Map.of(SORT_PROPERTIES_ALPHABETICALLY, true)
    );
    JsonUtils.setCustomMapperFactory(factory);

    // Serialize the user resource again. This time, the object mapper should
    // sort the fields alphabetically.
    userJSON = JsonUtils.getObjectWriter().writeValueAsString(user);
    assertThat(userJSON).isEqualTo(expectedJSON);
  }

  /**
   * Tests a custom deserialization setting on a mapper factory.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testCustomDeserializationFeatures() throws Exception
  {
    // The JSON representing the 'name' field for a UserResource. The
    // 'stageName' field is not established by the SCIM standard.
    final String rawJSONString = """
        {
            "familyName": "Duckworth",
            "givenName": "Kendrick",
            "middleName": "Lamar",
            "formatted": "Kendrick Lamar Duckworth",
            "stageName": "K.Dot"
        }""";

    Name expectedPOJO = new Name().setFamilyName("Duckworth")
        .setGivenName("Kendrick")
        .setMiddleName("Lamar")
        .setFormatted("Kendrick Lamar Duckworth");

    // The default configuration should not allow the unknown field.
    assertThatThrownBy(() ->
        JsonUtils.getObjectReader().forType(Name.class).readValue(rawJSONString)
    ).isInstanceOf(JsonProcessingException.class);

    // Update the mapper factory to ignore unknown fields.
    var factory = new MapperFactory().setDeserializationCustomFeatures(
        Map.of(FAIL_ON_UNKNOWN_PROPERTIES, false)
    );
    JsonUtils.setCustomMapperFactory(factory);

    // Attempt to deserialize the data to a Name object again. This time, it
    // should not throw an exception, and the unknown field should be ignored.
    Name javaObject = JsonUtils.getObjectReader().forType(Name.class)
        .readValue(rawJSONString);
    assertThat(javaObject).isEqualTo(expectedPOJO);
    assertThat(javaObject.toString()).doesNotContain("stageName");
  }

  /**
   * Tests a custom serialization setting on a mapper factory.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testCustomSerialization() throws Exception
  {
    // A SCIM resource with a 'schemas' field set to a string instead of an
    // array.
    final String rawJSONString = """
        {
            "schemas": "urn:ietf:params:scim:schemas:core:2.0:User",
            "userName": "kendrick.lamar"
        }""";

    // Reformat the string in a standardized form.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(rawJSONString).toString();

    UserResource user = new UserResource().setUserName("kendrick.lamar");

    // Convert the user resource to a standardized JSON string and ensure the
    // representation does not match 'expectedJSON'. By default, this member
    // variable should be converted to an array.
    String userJSON = JsonUtils.getObjectWriter().writeValueAsString(user);
    assertThat(userJSON).isNotEqualTo(expectedJSON);
    assertThat(userJSON).contains("[", "]");

    // Update the object mapper to convert string values into single-valued
    // arrays for array attributes.
    MapperFactory factory = new MapperFactory().setSerializationCustomFeatures(
        Map.of(WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true)
    );
    JsonUtils.setCustomMapperFactory(factory);

    // Convert the resource to a string again. This time, the converted string
    // should be equivalent to the expected JSON.
    userJSON = JsonUtils.getObjectWriter().writeValueAsString(user);
    assertThat(userJSON).isEqualTo(expectedJSON);
    assertThat(userJSON).doesNotContain("[", "]");
  }

  /**
   * Validates the behavior of setting a custom JSON parser feature.
   * <br><br>
   *
   * Note that the {@link com.unboundid.scim2.common.utils.Parser} class (used
   * for processing string filters) leverages Jackson JSON Parsers, so this unit
   * test validates that the behavior of the filter parser can be updated.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testCustomJSONParser() throws Exception
  {
    // Ensure that single quotes are not permitted for filter values by default.
    assertThatThrownBy(() -> Filter.fromString("userName eq 'kendrick'"))
        .isInstanceOf(BadRequestException.class);

    // Permit single quotes.
    MapperFactory factory = new MapperFactory().setJsonParserCustomFeatures(
        Map.of(ALLOW_SINGLE_QUOTES, true)
    );
    JsonUtils.setCustomMapperFactory(factory);

    // The conversion should now be permitted.
    Filter equalFilter = Filter.fromString("userName eq 'kendrick'");
    assertThat(equalFilter.getFilterType()).isEqualTo(FilterType.EQUAL);
    assertThat(equalFilter.getAttributePath())
        .isNotNull()
        .matches(path -> path.toString().equals("userName"));
    assertThat(equalFilter.getComparisonValue())
        .isEqualTo(TextNode.valueOf("kendrick"));
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
    // Define a class that inherits from the SCIM SDK's MapperFactory and
    // overrides the object mapper configuration to explicitly print all null
    // values.
    class CustomFactory extends MapperFactory
    {
      @NotNull
      @Override
      public ObjectMapper createObjectMapper()
      {
        ObjectMapper mapper = super.createObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);
        return mapper;
      }
    }

    // Validate the SCIM SDK's default behavior.
    final UserResource user = new UserResource().setUserName("kendrick.lamar");
    assertThat(user.toString()).doesNotContain("null");

    // Update the SCIM SDK's object mapper with the custom factory, which
    // prints null values.
    MapperFactory factory = new CustomFactory();
    JsonUtils.setCustomMapperFactory(factory);

    // Convert the resource to a string again and verify the change in behavior.
    assertThat(user.toString()).contains("null");
  }
}
