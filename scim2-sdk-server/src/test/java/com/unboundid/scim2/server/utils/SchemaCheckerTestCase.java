/*
 * Copyright 2015 UnboundID Corp.
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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for the SchemaEnforcer.
 */
public class SchemaCheckerTestCase
{
  private ObjectMapper mapper;
  private SchemaResource coreSchema;
  private SchemaResource typeTestSchema;

  /**
   * Setup some basic schemas.
   *
   * @throws Exception If an error occurs.
   */
  @BeforeClass
  public void setUp() throws Exception
  {
    coreSchema = SchemaUtils.getSchema(UserResource.class);
    mapper = SchemaUtils.createSCIMCompatibleMapper();

    List<AttributeDefinition> attributeDefinitions =
        new ArrayList<AttributeDefinition>();

    // String
    AttributeDefinition.Builder builder = new AttributeDefinition.Builder();
    builder.setName("string");
    builder.setType(AttributeDefinition.Type.STRING);
    AttributeDefinition string = builder.build();
    attributeDefinitions.add(string);

    // String with canonical values
    builder = new AttributeDefinition.Builder();
    builder.setName("stringCanonical");
    builder.setType(AttributeDefinition.Type.STRING);
    builder.addCanonicalValues("value1", "value2");
    AttributeDefinition stringCanonical = builder.build();
    attributeDefinitions.add(stringCanonical);

    // Datetime
    builder = new AttributeDefinition.Builder();
    builder.setName("datetime");
    builder.setType(AttributeDefinition.Type.DATETIME);
    AttributeDefinition datetime = builder.build();
    attributeDefinitions.add(datetime);

    // Binary
    builder = new AttributeDefinition.Builder();
    builder.setName("binary");
    builder.setType(AttributeDefinition.Type.BINARY);
    AttributeDefinition binary = builder.build();
    attributeDefinitions.add(binary);

    // Reference
    builder = new AttributeDefinition.Builder();
    builder.setName("reference");
    builder.setType(AttributeDefinition.Type.REFERENCE);
    AttributeDefinition reference = builder.build();
    attributeDefinitions.add(reference);

    // Boolean
    builder = new AttributeDefinition.Builder();
    builder.setName("boolean");
    builder.setType(AttributeDefinition.Type.BOOLEAN);
    AttributeDefinition bool = builder.build();
    attributeDefinitions.add(bool);

    // Decimal
    builder = new AttributeDefinition.Builder();
    builder.setName("decimal");
    builder.setType(AttributeDefinition.Type.DECIMAL);
    AttributeDefinition decimal = builder.build();
    attributeDefinitions.add(decimal);

    // Integer
    builder = new AttributeDefinition.Builder();
    builder.setName("integer");
    builder.setType(AttributeDefinition.Type.INTEGER);
    AttributeDefinition integer = builder.build();
    attributeDefinitions.add(integer);

    // Complex
    builder = new AttributeDefinition.Builder();
    builder.setName("complex");
    builder.setType(AttributeDefinition.Type.COMPLEX);
    builder.addSubAttributes(string);
    builder.addSubAttributes(stringCanonical);
    builder.addSubAttributes(datetime);
    builder.addSubAttributes(binary);
    builder.addSubAttributes(reference);
    builder.addSubAttributes(bool);
    builder.addSubAttributes(decimal);
    builder.addSubAttributes(integer);
    attributeDefinitions.add(builder.build());

    // Multi-valued String
    builder = new AttributeDefinition.Builder();
    builder.setName("mvstring");
    builder.setType(AttributeDefinition.Type.STRING);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued String with canonical values
    builder = new AttributeDefinition.Builder();
    builder.setName("mvstringCanonical");
    builder.setType(AttributeDefinition.Type.STRING);
    builder.addCanonicalValues("value1", "value2");
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Datetime
    builder = new AttributeDefinition.Builder();
    builder.setName("mvdatetime");
    builder.setType(AttributeDefinition.Type.DATETIME);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Binary
    builder = new AttributeDefinition.Builder();
    builder.setName("mvbinary");
    builder.setType(AttributeDefinition.Type.BINARY);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Reference
    builder = new AttributeDefinition.Builder();
    builder.setName("mvreference");
    builder.setType(AttributeDefinition.Type.REFERENCE);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Boolean
    builder = new AttributeDefinition.Builder();
    builder.setName("mvboolean");
    builder.setType(AttributeDefinition.Type.BOOLEAN);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Decimal
    builder = new AttributeDefinition.Builder();
    builder.setName("mvdecimal");
    builder.setType(AttributeDefinition.Type.DECIMAL);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Integer
    builder = new AttributeDefinition.Builder();
    builder.setName("mvinteger");
    builder.setType(AttributeDefinition.Type.INTEGER);
    builder.setMultiValued(true);
    attributeDefinitions.add(builder.build());

    // Multi-valued Complex
    builder = new AttributeDefinition.Builder();
    builder.setName("mvcomplex");
    builder.setType(AttributeDefinition.Type.COMPLEX);
    builder.setMultiValued(true);
    builder.addSubAttributes(string);
    builder.addSubAttributes(stringCanonical);
    builder.addSubAttributes(datetime);
    builder.addSubAttributes(binary);
    builder.addSubAttributes(reference);
    builder.addSubAttributes(bool);
    builder.addSubAttributes(decimal);
    builder.addSubAttributes(integer);
    attributeDefinitions.add(builder.build());
    typeTestSchema = new SchemaResource(
        "urn:id:test", "test", "", attributeDefinitions);
  }

  /**
   * Check the full Spec User representation against the Spec schemas. Shouldn't
   * be any issues.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void sanityCheck() throws Exception
  {
    String USER = "{  \n" +
        "  \"schemas\":[  \n" +
        "    \"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
        "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"\n" +
        "  ],\n" +
        "  \"id\":\"2819c223-7f76-453a-919d-413861904646\",\n" +
        "  \"externalId\":\"701984\",\n" +
        "  \"userName\":\"bjensen@example.com\",\n" +
        "  \"name\":{  \n" +
        "    \"formatted\":\"Ms. Barbara J Jensen III\",\n" +
        "    \"familyName\":\"Jensen\",\n" +
        "    \"givenName\":\"Barbara\",\n" +
        "    \"middleName\":\"Jane\",\n" +
        "    \"honorificPrefix\":\"Ms.\",\n" +
        "    \"honorificSuffix\":\"III\"\n" +
        "  },\n" +
        "  \"displayName\":\"Babs Jensen\",\n" +
        "  \"nickName\":\"Babs\",\n" +
        "  \"profileUrl\":\"https://login.example.com/bjensen\",\n" +
        "  \"emails\":[  \n" +
        "    {  \n" +
        "      \"value\":\"bjensen@example.com\",\n" +
        "      \"type\":\"work\",\n" +
        "      \"primary\":true\n" +
        "    },\n" +
        "    {  \n" +
        "      \"value\":\"babs@jensen.org\",\n" +
        "      \"type\":\"home\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"addresses\":[  \n" +
        "    {  \n" +
        "      \"streetAddress\":\"100 Universal City Plaza\",\n" +
        "      \"locality\":\"Hollywood\",\n" +
        "      \"region\":\"CA\",\n" +
        "      \"postalCode\":\"91608\",\n" +
        "      \"country\":\"USA\",\n" +
        "      \"formatted\":\"100 Universal City Plaza\\nHollywood, " +
        "CA 91608 USA\",\n" +
        "      \"type\":\"work\",\n" +
        "      \"primary\":true\n" +
        "    },\n" +
        "    {  \n" +
        "      \"streetAddress\":\"456 Hollywood Blvd\",\n" +
        "      \"locality\":\"Hollywood\",\n" +
        "      \"region\":\"CA\",\n" +
        "      \"postalCode\":\"91608\",\n" +
        "      \"country\":\"USA\",\n" +
        "      \"formatted\":\"456 Hollywood Blvd\\nHollywood, " +
        "CA 91608 USA\",\n" +
        "      \"type\":\"home\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"phoneNumbers\":[  \n" +
        "    {  \n" +
        "      \"value\":\"555-555-5555\",\n" +
        "      \"type\":\"work\"\n" +
        "    },\n" +
        "    {  \n" +
        "      \"value\":\"555-555-4444\",\n" +
        "      \"type\":\"mobile\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"ims\":[  \n" +
        "    {  \n" +
        "      \"value\":\"someaimhandle\",\n" +
        "      \"type\":\"aim\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"photos\":[  \n" +
        "    {  \n" +
        "      \"value\":\"https://photos.example.com/profilephoto/" +
        "72930000000Ccne/F\",\n" +
        "      \"type\":\"photo\"\n" +
        "    },\n" +
        "    {  \n" +
        "      \"value\":\"https://photos.example.com/profilephoto/" +
        "72930000000Ccne/T\",\n" +
        "      \"type\":\"thumbnail\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"userType\":\"Employee\",\n" +
        "  \"title\":\"Tour Guide\",\n" +
        "  \"preferredLanguage\":\"en-US\",\n" +
        "  \"locale\":\"en-US\",\n" +
        "  \"timezone\":\"America/Los_Angeles\",\n" +
        "  \"active\":true,\n" +
        "  \"password\":\"t1meMa$heen\",\n" +
        "  \"groups\":[  \n" +
        "    {  \n" +
        "      \"value\":\"e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
        "      \"$ref\":\"../Groups/e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
        "      \"display\":\"Tour Guides\"\n" +
        "    },\n" +
        "    {  \n" +
        "      \"value\":\"fc348aa8-3835-40eb-a20b-c726e15c55b5\",\n" +
        "      \"$ref\":\"../Groups/fc348aa8-3835-40eb-a20b-c726e15c55b5\",\n" +
        "      \"display\":\"Employees\"\n" +
        "    },\n" +
        "    {  \n" +
        "      \"value\":\"71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7\",\n" +
        "      \"$ref\":\"../Groups/71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7\",\n" +
        "      \"display\":\"US Employees\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"x509Certificates\":[  \n" +
        "    {  \n" +
        "      \"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTj" +
        "ELMAkGA1UEBhMCVVMx" +
        "        EzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29t" +
        "MRQwEgYD" +
        "        VQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQw" +
        "NjI0MzFa" +
        "        MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYD" +
        "VQQKDAtl" +
        "        eGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4g" +
        "SUlJMSIw" +
        "        IAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkq" +
        "hkiG9w0B" +
        "        AQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis" +
        "56AK02bc" +
        "        1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5v" +
        "APKpzz5i" +
        "        PSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT" +
        "5WFTVfFZ" +
        "        zidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT" +
        "51ikSQG3" +
        "        DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ" +
        "4zJQBmDr" +
        "        SGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sw" +
        "eTAJBgNV" +
        "        HRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZ" +
        "XJ0aWZp" +
        "        Y2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jB" +
        "BgwFoAU" +
        "        dGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81Ss" +
        "FnOdYJt" +
        "        Ng5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pK" +
        "fTiDz1R" +
        "        C4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAto" +
        "RUeDov1" +
        "        +GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\":{\n" +
        "    \"employeeNumber\":\"701984\",\n" +
        "    \"costCenter\":\"4130\",\n" +
        "    \"organization\":\"Universal Studios\",\n" +
        "    \"division\":\"Theme Park\",\n" +
        "    \"department\":\"Tour Operations\",\n" +
        "    \"manager\":{  \n" +
        "      \"value\":\"26118915-6090-4610-87e4-49d8ca9f808d\",\n" +
        "      \"$ref\":\"../Users/26118915-6090-4610-87e4-49d8ca9f808d\",\n" +
        "      \"displayName\":\"John Smith\"\n" +
        "    }\n" +
        "  },\n" +
        "  \"meta\":{  \n" +
        "    \"resourceType\":\"User\",\n" +
        "    \"created\":\"2010-01-23T04:56:22Z\",\n" +
        "    \"lastModified\":\"2011-05-13T04:42:34Z\",\n" +
        "    \"version\":\"W\\/\\\"3694e05e9dff591\\\"\",\n" +
        "    \"location\":\"https://example.com/v2/Users/" +
        "2819c223-7f76-453a-919d-413861904646\"\n" +
        "  }\n" +
        "}";
    ObjectNode userResource =
        SchemaUtils.createSCIMCompatibleMapper().readValue(
            USER, ObjectNode.class);

    SchemaResource coreSchema = SchemaUtils.getSchema(UserResource.class);
    SchemaResource enterpriseExtension =
        SchemaUtils.getSchema(EnterpriseUserExtension.class);

    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(coreSchema).
            addOptionalSchemaExtension(enterpriseExtension).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);

    // Remove any read only attributes since they are suppose to be ignored
    // on create and replace.
    userResource = checker.removeReadOnlyAttributes(userResource);
    JsonNode copyUserResource = userResource.deepCopy();

    // Check create
    SchemaChecker.Results results = checker.checkCreate(userResource);

    // Make sure there are no issues.
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());
    assertTrue(results.getPathIssues().isEmpty(),
        results.getPathIssues().toString());
    assertTrue(results.getSyntaxIssues().isEmpty(),
        results.getSyntaxIssues().toString());

    // Make sure the ObjectNode wasn't modified during the check.
    assertEquals(userResource, copyUserResource);

    // Check replace
    results = checker.checkReplace(userResource, null);

    // Make sure there are no issues.
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());
    assertTrue(results.getPathIssues().isEmpty(),
        results.getPathIssues().toString());
    assertTrue(results.getSyntaxIssues().isEmpty(),
        results.getSyntaxIssues().toString());

    // Make sure the ObjectNode wasn't modified during the check.
    assertEquals(userResource, copyUserResource);

    // Check modify
    String patchRequestStr =
        "{  \n" +
            "  \"op\":\"add\",\n" +
            "  \"value\":{  \n" +
            "    \"password\":\"password\",\n" +
            "    \"name\":{  \n" +
            "      \"givenName\":\"Barbara\",\n" +
            "      \"familyName\":\"Jensen\",\n" +
            "      \"formatted\":\"Barbara Ann Jensen\"\n" +
            "    },\n" +
            "    \"emails\":[  \n" +
            "      {  \n" +
            "        \"value\":\"bjensen@example.com\",\n" +
            "        \"type\":\"work\"\n" +
            "      },\n" +
            "      {  \n" +
            "        \"value\":\"babs@jensen.org\",\n" +
            "        \"type\":\"home\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"urn:ietf:params:scim:schemas:extension:" +
            "enterprise:2.0:User\":{  \n" +
            "      \"employeeNumber\":\"701984\"\n" +
            "    },\n" +
            "    \"addresses\":[  \n" +
            "      {  \n" +
            "        \"type\":\"work\",\n" +
            "        \"streetAddress\":\"13809 Research Blvd\",\n" +
            "        \"locality\":\"Austin\",\n" +
            "        \"region\":\"TX\",\n" +
            "        \"postalCode\":\"78750\",\n" +
            "        \"country\":\"USA\",\n" +
            "        \"formatted\":\"13809 Research Blvd\\n" +
            "Austin, TX 78750 USA\",\n" +
            "        \"primary\":true\n" +
            "      },\n" +
            "      {  \n" +
            "        \"type\":\"home\",\n" +
            "        \"streetAddress\":\"456 Hollywood Blvd\",\n" +
            "        \"locality\":\"Hollywood\",\n" +
            "        \"region\":\"CA\",\n" +
            "        \"postalCode\":\"91608\",\n" +
            "        \"country\":\"USA\",\n" +
            "        \"formatted\":\"456 Hollywood Blvd\\n" +
            "Hollywood, CA 91608 USA\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    PatchOperation operation =
        mapper.readValue(patchRequestStr, PatchOperation.class);

    results = checker.checkModify(
        Collections.singleton(operation), userResource);

    // Make sure there are no issues.
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());
    assertTrue(results.getPathIssues().isEmpty(),
        results.getPathIssues().toString());
    assertTrue(results.getSyntaxIssues().isEmpty(),
        results.getSyntaxIssues().toString());

    // Make sure the patch operation wasn't modified during the check.
    assertEquals(operation,
        mapper.readValue(patchRequestStr, PatchOperation.class));

    // Make sure the ObjectNode wasn't modified during the check.
    assertEquals(userResource, copyUserResource);
  }

  /**
   * Provider for testSchemaExtension.
   *
   * @return The test data.
   */
  @DataProvider
  public Object[][] schemaExtensionProvider()
  {
    // Create one schema extension with a required attribute.
    AttributeDefinition reqAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(true).
        build();
    SchemaResource extWithReqAttr =
        new SchemaResource("urn:id:testExt", "testExt", "",
            Collections.singleton(reqAttr));

    // Create one schema extension with a required attribute.
    AttributeDefinition optAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        build();
    SchemaResource extWithOptAttr =
        new SchemaResource("urn:id:testExt", "testExt", "",
            Collections.singleton(optAttr));

    ObjectNode extNotIn = mapper.createObjectNode();
    extNotIn.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User");
    extNotIn.put("userName", "test");

    ObjectNode extInSchemas = mapper.createObjectNode();
    extInSchemas.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:id:testExt");
    extInSchemas.put("userName", "test");

    ObjectNode extNotInSchemas = mapper.createObjectNode();
    extNotInSchemas.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User");
    extNotInSchemas.put("userName", "test");
    extNotInSchemas.putObject("urn:id:testExt").put("test", "test");

    ObjectNode extIn = mapper.createObjectNode();
    extIn.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:id:testExt");
    extIn.put("userName", "test");
    extIn.putObject("urn:id:testExt").put("test", "test");

    ObjectNode undefinedInSchemas = mapper.createObjectNode();
    undefinedInSchemas.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:id:undefined");
    undefinedInSchemas.put("userName", "test");

    ObjectNode undefinedNotInSchemas = mapper.createObjectNode();
    undefinedNotInSchemas.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User");
    undefinedNotInSchemas.put("userName", "test");
    undefinedNotInSchemas.putObject("urn:id:undefined").put("test", "test");

    ObjectNode undefinedIn = mapper.createObjectNode();
    undefinedIn.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:id:undefined");
    undefinedIn.put("userName", "test");
    undefinedIn.putObject("urn:id:undefined").put("test", "test");

    ObjectNode notObject = mapper.createObjectNode();
    notObject.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:id:testExt");
    notObject.put("userName", "test");
    notObject.putArray("urn:id:testExt").addObject().put("test", "test");

    return new Object[][] {
        new Object[] { extWithReqAttr, true, extNotIn, 1, 0 },
        new Object[] { extWithReqAttr, true, extInSchemas, 1, 0 },
        new Object[] { extWithReqAttr, true, extNotInSchemas, 2, 0 },
        new Object[] { extWithReqAttr, true, extIn, 0, 0 },
        new Object[] { extWithReqAttr, false, extNotIn, 0, 0 },
        new Object[] { extWithReqAttr, false, extInSchemas, 1, 0 },
        new Object[] { extWithReqAttr, false, extNotInSchemas, 1, 0 },
        new Object[] { extWithReqAttr, false, extIn, 0, 0 },

        new Object[] { extWithReqAttr, false, undefinedInSchemas, 1, 1 },
        new Object[] { extWithReqAttr, false, undefinedNotInSchemas, 1, 1 },
        new Object[] { extWithReqAttr, false, undefinedIn, 1, 2 },
        new Object[] { extWithReqAttr, false, notObject, 1, 1 },

        new Object[] { extWithOptAttr, true, extNotIn, 1, 0 },
        new Object[] { extWithOptAttr, true, extInSchemas, 0, 0 },
        new Object[] { extWithOptAttr, true, extNotInSchemas, 2, 0 },
        new Object[] { extWithOptAttr, true, extIn, 0, 0 },
        new Object[] { extWithOptAttr, false, extNotIn, 0, 0 },
        new Object[] { extWithOptAttr, false, extInSchemas, 0, 0 },
        new Object[] { extWithOptAttr, false, extNotInSchemas, 1, 0 },
        new Object[] { extWithOptAttr, false, extIn, 0, 0 },

        new Object[] { extWithOptAttr, false, undefinedInSchemas, 1, 1 },
        new Object[] { extWithOptAttr, false, undefinedNotInSchemas, 1, 1 },
        new Object[] { extWithOptAttr, false, undefinedIn, 1, 2 },
        new Object[] { extWithOptAttr, false, notObject, 1, 1 },
    };
  }

  /**
   * Test to ensure schema extensions are checked correctly.
   *
   * @param extension The schema extension.
   * @param required Whether it is required.
   * @param node The object node to check.
   * @param expectedErrorOnCreate Expected errors on create.
   * @param expectedErrorOnPatch Expected errors on patch.
   * @throws Exception if an error occurs.
   */
  @Test(dataProvider = "schemaExtensionProvider")
  public void testSchemaExtension(SchemaResource extension,
                                  boolean required,
                                  ObjectNode node,
                                  int expectedErrorOnCreate,
                                  int expectedErrorOnPatch)
      throws Exception
  {
    ResourceTypeDefinition resourceTypeDefinition = required ?
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(coreSchema).
            addRequiredSchemaExtension(extension).build() :
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(coreSchema).
            addOptionalSchemaExtension(extension).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);
    SchemaChecker.Results results = checker.checkCreate(node);

    assertEquals(results.getSyntaxIssues().size(), expectedErrorOnCreate,
        results.getSyntaxIssues().toString());

    // Partial patch
    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(null, node)), null);
    assertEquals(results.getSyntaxIssues().size(), expectedErrorOnPatch,
        results.getSyntaxIssues().toString());

    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(null, node)), null);
    assertEquals(results.getSyntaxIssues().size(), expectedErrorOnPatch,
        results.getSyntaxIssues().toString());
  }

  /**
   * Test to ensure not including the core schema in the schemas attribute
   * should result in syntax error.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testCoreSchema() throws Exception
  {
    // Create one schema extension with a required attribute.
    AttributeDefinition reqAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(true).
        build();
    SchemaResource extWithReqAttr =
        new SchemaResource("urn:id:testExt", "testExt", "",
            Collections.singleton(reqAttr));

    // Not including the core schema should be an error.
    ObjectNode resource = mapper.createObjectNode();
    resource.putArray("schemas").
        add("urn:id:testExt");
    resource.put("userName", "test");
    resource.putObject("urn:id:testExt").put("test", "test");

    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(coreSchema).
            addRequiredSchemaExtension(extWithReqAttr).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);
    SchemaChecker.Results results = checker.checkCreate(resource);

    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
  }

  /**
   * Test to ensure modifications using patch operations on the schemas
   * attribute are checked correctly.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSchemasModify() throws Exception
  {
    // Create one schema extension with a required attribute.
    AttributeDefinition reqAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(true).
        build();
    SchemaResource extWithReqAttr =
        new SchemaResource("urn:id:extWithReqAttr", "extWithReqAttr", "",
            Collections.singleton(reqAttr));

    // Create one schema extension with a required attribute.
    AttributeDefinition optAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        build();
    SchemaResource extWithOptAttr =
        new SchemaResource("urn:id:extWithOptAttr", "extWithOptAttr", "",
            Collections.singleton(optAttr));

    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(coreSchema).
            addRequiredSchemaExtension(extWithReqAttr).
            addOptionalSchemaExtension(extWithOptAttr).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);

    ObjectNode resource = mapper.createObjectNode();
    resource.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:id:extWithReqAttr");
    resource.put("userName", "test");
    resource.putObject("urn:id:extWithReqAttr").put("test", "test");

    // Shouldn't be able to remove the core schema
    List<PatchOperation> patchOps = new LinkedList<PatchOperation>();
    patchOps.add(PatchOperation.remove(Path.root().attribute("schemas",
        Filter.eq("value", "urn:ietf:params:scim:schemas:core:2.0:User"))));

    SchemaChecker.Results results = checker.checkModify(patchOps, resource);
    assertEquals(results.getSyntaxIssues().size(), 2,
        results.getSyntaxIssues().toString());

    // Shouldn't be able to remove a required schema extension
    patchOps = new LinkedList<PatchOperation>();
    patchOps.add(PatchOperation.remove(Path.root().attribute("schemas",
        Filter.eq("value", "urn:id:extWithReqAttr"))));

    results = checker.checkModify(patchOps, resource);
    assertEquals(results.getSyntaxIssues().size(), 3,
        results.getSyntaxIssues().toString());

        // Shouldn't be able to replace the core schema
    patchOps = new LinkedList<PatchOperation>();
    patchOps.add(PatchOperation.replace(Path.root().attribute("schemas",
            Filter.eq("value", "urn:ietf:params:scim:schemas:core:2.0:User")),
        TextNode.valueOf("urn:id:extWithOptAttr")));

    results = checker.checkModify(patchOps, resource);
    assertEquals(results.getSyntaxIssues().size(), 2,
        results.getSyntaxIssues().toString());

    // Shouldn't be able to replace a required schema extension
    patchOps = new LinkedList<PatchOperation>();
    patchOps.add(PatchOperation.replace(Path.root().attribute("schemas",
        Filter.eq("value", "urn:id:extWithReqAttr")),
        TextNode.valueOf("urn:id:extWithOptAttr")));

    results = checker.checkModify(patchOps, resource);
    assertEquals(results.getSyntaxIssues().size(), 3,
        results.getSyntaxIssues().toString());

    // Shouldn't be able to add an undefined schema extension
    patchOps = new LinkedList<PatchOperation>();
    patchOps.add(PatchOperation.add(Path.root().attribute("schemas"),
        mapper.createArrayNode().add("urn:id:undefined")));

    results = checker.checkModify(patchOps, resource);
    assertEquals(results.getSyntaxIssues().size(), 2,
        results.getSyntaxIssues().toString());
  }

  /**
   * Provider for testRequiredAttributes.
   *
   * @return The test data.
   */
  @DataProvider
  public Object[][] requiredAttributesProvider()
  {
    // Optional attribute
    AttributeDefinition optAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(false).
        build();

    // Required attribute
    AttributeDefinition reqAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(true).
        build();

    // Optional attribute, optional sub-attribute
    AttributeDefinition optAttrOptSub = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.COMPLEX).
        setRequired(false).
        addSubAttributes(optAttr).
        build();

    // Optional attribute, required sub-attribute
    AttributeDefinition optAttrReqSub = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.COMPLEX).
        setRequired(false).
        addSubAttributes(reqAttr).
        build();

    // Required attribute, required sub-attribute
    AttributeDefinition reqAttrReqSub = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.COMPLEX).
        setRequired(true).
        addSubAttributes(reqAttr).
        build();

    // Optional multi-valeud attribute
    AttributeDefinition optMVAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(false).
        setMultiValued(true).
        build();

    // Required multi-valued attribute
    AttributeDefinition reqMVAttr = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.STRING).
        setRequired(true).
        setMultiValued(true).
        build();

    // Optional multi-valued attribute, optional sub-attribute
    AttributeDefinition optMVAttrOptSub = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.COMPLEX).
        setRequired(false).
        addSubAttributes(optAttr).
        setMultiValued(true).
        build();

    // Optional multi-valued attribute, required sub-attribute
    AttributeDefinition optMVAttrReqSub = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.COMPLEX).
        setRequired(false).
        addSubAttributes(reqAttr).
        setMultiValued(true).
        build();

    // Required multi-valued attribute, required sub-attribute
    AttributeDefinition reqMVAttrReqSub = new AttributeDefinition.Builder().
        setName("test").
        setType(AttributeDefinition.Type.COMPLEX).
        setRequired(true).
        addSubAttributes(reqAttr).
        setMultiValued(true).
        build();


    // Attribute not present
    ObjectNode notPresent = mapper.createObjectNode();
    notPresent.putArray("schemas").
        add("urn:id:test");

    // Attribute with null value
    ObjectNode nullValue = mapper.createObjectNode();
    nullValue.putArray("schemas").
        add("urn:id:test");
    nullValue.putNull("test");

    // Attribute with not present sub-attribute
    ObjectNode subNotPresent = mapper.createObjectNode();
    subNotPresent.putArray("schemas").
        add("urn:id:test");
    subNotPresent.putObject("test");

    // Attribute with null value sub-attribute
    ObjectNode subNullValue = mapper.createObjectNode();
    subNullValue.putArray("schemas").
        add("urn:id:test");
    subNullValue.putObject("test").putNull("test");

    // Attribute with empty array
    ObjectNode emptyArray = mapper.createObjectNode();
    emptyArray.putArray("schemas").
        add("urn:id:test");
    emptyArray.putArray("test");

    // Attribute with one element not present sub-attribute
    ObjectNode arrayNotPresent = mapper.createObjectNode();
    arrayNotPresent.putArray("schemas").
        add("urn:id:test");
    arrayNotPresent.putArray("test").addObject();

    // Attribute with one element null value sub-attribute
    ObjectNode arrayNullValue = mapper.createObjectNode();
    arrayNullValue.putArray("schemas").
        add("urn:id:test");
    arrayNullValue.putArray("test").addObject().putNull("test");

    return new Object[][] {
        new Object[] {optAttr, notPresent, 0},
        new Object[] {optAttr, nullValue, 0},
        new Object[] {reqAttr, notPresent, 1},
        new Object[] {reqAttr, nullValue, 1},
        new Object[] {optAttrOptSub, notPresent, 0},
        new Object[] {optAttrOptSub, nullValue, 0},
        new Object[] {optAttrOptSub, subNotPresent, 0},
        new Object[] {optAttrOptSub, subNullValue, 0},
        new Object[] {optAttrReqSub, notPresent, 0},
        new Object[] {optAttrReqSub, nullValue, 0},
        new Object[] {optAttrReqSub, subNotPresent, 1},
        new Object[] {optAttrReqSub, subNullValue, 1},
        new Object[] {reqAttrReqSub, notPresent, 1},
        new Object[] {reqAttrReqSub, nullValue, 1},
        new Object[] {reqAttrReqSub, subNotPresent, 1},
        new Object[] {reqAttrReqSub, subNullValue, 1},
        new Object[] {optMVAttr, notPresent, 0},
        new Object[] {optMVAttr, emptyArray, 0},
        new Object[] {reqMVAttr, notPresent, 1},
        new Object[] {reqMVAttr, emptyArray, 1},
        new Object[] {optMVAttrOptSub, notPresent, 0},
        new Object[] {optMVAttrOptSub, emptyArray, 0},
        new Object[] {optMVAttrOptSub, arrayNotPresent, 0},
        new Object[] {optMVAttrOptSub, arrayNullValue, 0},
        new Object[] {optMVAttrReqSub, notPresent, 0},
        new Object[] {optMVAttrReqSub, emptyArray, 0},
        new Object[] {optMVAttrReqSub, arrayNotPresent, 1},
        new Object[] {optMVAttrReqSub, arrayNullValue, 1},
        new Object[] {reqMVAttrReqSub, notPresent, 1},
        new Object[] {reqMVAttrReqSub, emptyArray, 1},
        new Object[] {reqMVAttrReqSub, arrayNotPresent, 1},
        new Object[] {reqMVAttrReqSub, arrayNullValue, 1},
    };
  }

  /**
   * Test to ensure required attributes are checked correctly.
   *
   * @param attributeDefinition The attribute definition.
   * @param node The object node to check.
   * @param expectError Whether an error is expected.
   * @throws Exception if an error occurs.
   */
  @Test(dataProvider = "requiredAttributesProvider")
  public void testRequiredAttributes(AttributeDefinition attributeDefinition,
                                     ObjectNode node,
                                     int expectError) throws Exception
  {
    SchemaResource schema = new SchemaResource("urn:id:test", "test", "",
        Collections.singleton(attributeDefinition));
    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(schema).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);
    SchemaChecker.Results results = checker.checkCreate(node);

    assertEquals(results.getSyntaxIssues().size(), expectError,
        results.getSyntaxIssues().toString());

    // Can't remove required attributes in patch
    if(attributeDefinition.isRequired())
    {
      results = checker.checkModify(Collections.singleton(
          PatchOperation.remove(Path.root().attribute("test"))), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
    }
    if(attributeDefinition.getSubAttributes() != null &&
        attributeDefinition.getSubAttributes().iterator().next().isRequired())
    {
      results = checker.checkModify(Collections.singleton(
          PatchOperation.remove(
              Path.root().attribute("test").attribute("test"))), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
    }
  }

  /**
   * Test that undefined attributes are detected correctly.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testUndefinedAttributes()
      throws Exception
  {
    SchemaResource coreSchema = SchemaUtils.getSchema(UserResource.class);
    SchemaResource enterpriseExtension =
        SchemaUtils.getSchema(EnterpriseUserExtension.class);

    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(coreSchema).
            addOptionalSchemaExtension(enterpriseExtension).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);

    // Core attribute is undefined
    ObjectNode coreUndefined = mapper.createObjectNode();
    coreUndefined.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
    coreUndefined.put("userName", "test");
    coreUndefined.put("undefined", "value");

    SchemaChecker.Results results = checker.checkCreate(coreUndefined);
    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
    assertTrue(containsIssueWith(results.getSyntaxIssues(),
        "is undefined for schema"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root().attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for core schema"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root().attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for core schema"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.remove(Path.root().attribute("undefined"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for core schema"));

    // Core sub-attribute is undefined
    ObjectNode coreSubUndefined = mapper.createObjectNode();
    coreSubUndefined.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
    coreSubUndefined.put("userName", "test");
    coreSubUndefined.putObject("name").put("undefined", "value");

    results = checker.checkCreate(coreSubUndefined);
    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
    assertTrue(containsIssueWith(results.getSyntaxIssues(),
        "is undefined for attribute"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root().attribute("name").attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for attribute"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(
            Path.root().attribute("name").attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for attribute"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.remove(
            Path.root().attribute("name").attribute("undefined"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for attribute"));

    // Extended attribute is undefined
    ObjectNode extendedUndefined = mapper.createObjectNode();
    extendedUndefined.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
    extendedUndefined.put("userName", "test");
    extendedUndefined.putObject(
        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
        put("undefined", "value");

    results = checker.checkCreate(extendedUndefined);
    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
    assertTrue(containsIssueWith(results.getSyntaxIssues(),
        "is undefined for schema"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root(
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
                attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for schema"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root(
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
                attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for schema"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.remove(Path.root(
            "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
            attribute("undefined"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for schema"));

    // Extended sub-attribute is undefined
    ObjectNode extendedSubUndefined = mapper.createObjectNode();
    extendedSubUndefined.putArray("schemas").
        add("urn:ietf:params:scim:schemas:core:2.0:User").
        add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
    extendedSubUndefined.put("userName", "test");
    extendedSubUndefined.putObject(
        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
        putObject("manager").
        put("$ref", "https://value").
        put("value", "value").
        put("undefined", "value");

    results = checker.checkCreate(extendedSubUndefined);
    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
    assertTrue(containsIssueWith(results.getSyntaxIssues(),
        "is undefined for attribute"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root(
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
                attribute("manager").attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for attribute"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root(
                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
                attribute("manager").attribute("undefined"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for attribute"));

    results = checker.checkModify(Collections.singleton(
        PatchOperation.remove(Path.root(
            "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User").
            attribute("manager").attribute("undefined"))), null);
    assertEquals(results.getPathIssues().size(), 1,
        results.getPathIssues().toString());
    assertTrue(containsIssueWith(results.getPathIssues(),
        "is undefined for attribute"));
  }

  /**
   * Provider for testAttributeValueType.
   *
   * @return The test data.
   */
  @DataProvider
  public Object[][] attributeValueTypeProvider()
  {
    return new Object[][] {
        // Wrong attribute value types
        new Object[] {"string", mapper.getNodeFactory().numberNode(1)},
        new Object[] {"string", mapper.getNodeFactory().booleanNode(true)},
        new Object[] {"string", mapper.getNodeFactory().objectNode()},
        new Object[] {"string", mapper.getNodeFactory().arrayNode()},
        new Object[] {"stringCanonical",
            mapper.getNodeFactory().textNode("value3")},
        new Object[] {"datetime",
            mapper.getNodeFactory().textNode("notdatetime")},
        new Object[] {"datetime", mapper.getNodeFactory().numberNode(1)},
        new Object[] {"datetime", mapper.getNodeFactory().booleanNode(true)},
        new Object[] {"datetime", mapper.getNodeFactory().objectNode()},
        new Object[] {"datetime", mapper.getNodeFactory().arrayNode()},
        new Object[] {"binary", mapper.getNodeFactory().textNode("()$#@_@")},
        new Object[] {"binary", mapper.getNodeFactory().numberNode(1)},
        new Object[] {"binary", mapper.getNodeFactory().booleanNode(true)},
        new Object[] {"binary", mapper.getNodeFactory().objectNode()},
        new Object[] {"binary", mapper.getNodeFactory().arrayNode()},
        new Object[] {"reference", mapper.getNodeFactory().textNode("rtp:\\")},
        new Object[] {"reference", mapper.getNodeFactory().numberNode(1)},
        new Object[] {"reference", mapper.getNodeFactory().booleanNode(true)},
        new Object[] {"reference", mapper.getNodeFactory().objectNode()},
        new Object[] {"reference", mapper.getNodeFactory().arrayNode()},
        new Object[] {"boolean", mapper.getNodeFactory().textNode("string")},
        new Object[] {"boolean", mapper.getNodeFactory().numberNode(1)},
        new Object[] {"boolean", mapper.getNodeFactory().objectNode()},
        new Object[] {"boolean", mapper.getNodeFactory().arrayNode()},
        new Object[] {"decimal", mapper.getNodeFactory().textNode("string")},
        new Object[] {"decimal", mapper.getNodeFactory().booleanNode(true)},
        new Object[] {"decimal", mapper.getNodeFactory().objectNode()},
        new Object[] {"decimal", mapper.getNodeFactory().arrayNode()},
        new Object[] {"integer", mapper.getNodeFactory().textNode("string")},
        new Object[] {"integer", mapper.getNodeFactory().booleanNode(true)},
        new Object[] {"integer", mapper.getNodeFactory().objectNode()},
        new Object[] {"integer", mapper.getNodeFactory().arrayNode()},
        new Object[] {"integer", mapper.getNodeFactory().numberNode(1.1)},
        new Object[] {"complex", mapper.getNodeFactory().textNode("string")},
        new Object[] {"complex", mapper.getNodeFactory().numberNode(1)},
        new Object[] {"complex", mapper.getNodeFactory().booleanNode(true)},
    };
  }

  /**
   * Test to ensure attribute values are checked for the correct type.
   *
   * @param field The attribute name to test.
   * @param node The value node to test.
   * @throws Exception if an error occurs.
   */
  @Test(dataProvider = "attributeValueTypeProvider")
  public void testAttributeValueType(String field, JsonNode node)
      throws Exception
  {
    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(typeTestSchema).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);

    // First test as an attribute
    ObjectNode o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.set(field, node);
    SchemaChecker.Results results = checker.checkCreate(o);
    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
    assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

    if(!((node.isArray() || node.isObject()) && node.size() == 0))
    {
      // Partial patch
      results = checker.checkModify(Collections.singleton(
          PatchOperation.add(null, o)), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      results = checker.checkModify(Collections.singleton(
          PatchOperation.replace(null, o)), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      // Path'ed patch
      results = checker.checkModify(Collections.singleton(
          PatchOperation.add(Path.root().attribute(field), node)), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      results = checker.checkModify(Collections.singleton(
          PatchOperation.replace(Path.root().attribute(field), node)), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));
    }

    // Then test a an sub-attribute
    if(!field.equals("complex"))
    {
      o = mapper.createObjectNode();
      o.putArray("schemas").add("urn:id:test");
      o.putObject("complex").set(field, node);
      results = checker.checkCreate(o);
      assertFalse(results.getSyntaxIssues().isEmpty(),
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      if(!((node.isArray() || node.isObject()) && node.size() == 0))
      {
        // Partial patch
        results = checker.checkModify(Collections.singleton(
            PatchOperation.add(null, o)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        results = checker.checkModify(Collections.singleton(
            PatchOperation.replace(null, o)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        // Path'ed patch
        results = checker.checkModify(Collections.singleton(
            PatchOperation.add(Path.root().
                attribute("complex").attribute(field), node)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        results = checker.checkModify(Collections.singleton(
            PatchOperation.replace(Path.root().
                attribute("complex").attribute(field), node)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));
      }
    }

    // Then test as a single-value for multi-valued attributes
    if(!node.isArray())
    {
      o = mapper.createObjectNode();
      o.putArray("schemas").add("urn:id:test");
      o.set("mv" + field, node);
      results = checker.checkCreate(o);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      if(!((node.isArray() || node.isObject()) && node.size() == 0))
      {
        // Partial patch
        results = checker.checkModify(Collections.singleton(
            PatchOperation.add(null, o)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        results = checker.checkModify(Collections.singleton(
            PatchOperation.replace(null, o)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        // Path'ed patch
        results = checker.checkModify(Collections.singleton(
            PatchOperation.add(Path.root().attribute("mv"+field), node)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        results = checker.checkModify(Collections.singleton(
            PatchOperation.replace(
                Path.root().attribute("mv"+field), node)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));
      }
    }

    // Then test as a multi-valued attribute
    o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.putArray("mv" + field).add(node);
    results = checker.checkCreate(o);
    assertEquals(results.getSyntaxIssues().size(), 1,
        results.getSyntaxIssues().toString());
    assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

    if(!((node.isArray() || node.isObject()) && node.size() == 0))
    {
      // Partial patch
      results = checker.checkModify(Collections.singleton(
          PatchOperation.add(null, o)), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      results = checker.checkModify(Collections.singleton(
          PatchOperation.replace(null, o)), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      // Path'ed patch
      results = checker.checkModify(Collections.singleton(
          PatchOperation.add(Path.root().attribute("mv"+field),
              mapper.createArrayNode().add(node))), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      results = checker.checkModify(Collections.singleton(
          PatchOperation.replace(
              Path.root().attribute("mv"+field),
              mapper.createArrayNode().add(node))), null);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));
    }

    // Finally test as a sub-attribute of a multi-valued attribute
    if(!field.equals("complex"))
    {
      o = mapper.createObjectNode();
      o.putArray("schemas").add("urn:id:test");
      o.putArray("mvcomplex").addObject().set(field, node);
      results = checker.checkCreate(o);
      assertEquals(results.getSyntaxIssues().size(), 1,
          results.getSyntaxIssues().toString());
      assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

      if(!((node.isArray() || node.isObject()) && node.size() == 0))
      {
        // Partial patch
        results = checker.checkModify(Collections.singleton(
            PatchOperation.add(null, o)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        results = checker.checkModify(Collections.singleton(
            PatchOperation.replace(null, o)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        // Path'ed patch
        results = checker.checkModify(Collections.singleton(
            PatchOperation.add(Path.root().
                attribute("mvcomplex").attribute(field), node)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));

        results = checker.checkModify(Collections.singleton(
            PatchOperation.replace(Path.root().
                attribute("mvcomplex").attribute(field), node)), null);
        assertEquals(results.getSyntaxIssues().size(), 1,
            results.getSyntaxIssues().toString());
        assertTrue(containsIssueWith(results.getSyntaxIssues(), "Value"));
      }
    }

  }

  /**
   * Test the attribute mutability constratins are checked correctly.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testAttributeMutability() throws Exception
  {
    List<AttributeDefinition> attributeDefinitions =
        new ArrayList<AttributeDefinition>();

    // Read-only attribute
    AttributeDefinition readOnly = new AttributeDefinition.Builder().
        setName("readOnly").
        setType(AttributeDefinition.Type.STRING).
        setMutability(AttributeDefinition.Mutability.READ_ONLY).
        build();

    // Immutable attribute
    AttributeDefinition immutable = new AttributeDefinition.Builder().
        setName("immutable").
        setType(AttributeDefinition.Type.STRING).
        setMutability(AttributeDefinition.Mutability.IMMUTABLE).
        build();

    attributeDefinitions.add(readOnly);
    attributeDefinitions.add(immutable);

    SchemaResource schema = new SchemaResource("urn:id:test", "test", "",
        attributeDefinitions);
    ResourceTypeDefinition resourceTypeDefinition =
        new ResourceTypeDefinition.Builder("test", "/test").
            setCoreSchema(schema).build();
    SchemaChecker checker = new SchemaChecker(resourceTypeDefinition);

    // Can not create read-only
    ObjectNode o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.put("readOnly", "value");
    SchemaChecker.Results results = checker.checkCreate(o);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "read-only"));

    // Can not replace read-only
    results = checker.checkReplace(o, null);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "read-only"));

    // Can not add read-only in patch
    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root().attribute("readOnly"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "read-only"));

    // Can not remove read-only in patch
    results = checker.checkModify(Collections.singleton(
        PatchOperation.remove(Path.root().attribute("readOnly"))), null);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "read-only"));

    // Can not replace read-only in patch
    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root().attribute("readOnly"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "read-only"));

    // Can create immutable
    o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.put("immutable", "value");
    results = checker.checkCreate(o);
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());

    // Can replace immutable if not already present
    o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.put("immutable", "value");
    results = checker.checkReplace(o, null);
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());

    // Can replace if it is the same
    o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.put("immutable", "value");
    results = checker.checkReplace(o, o);
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());

    // Can not replace if value already present and different
    o = mapper.createObjectNode();
    o.putArray("schemas").add("urn:id:test");
    o.put("immutable", "value");
    results = checker.checkReplace(
        o.deepCopy().put("immutable", "newValue"), o);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));

    // Can add immutable in patch if not already present
    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root().attribute("immutable"),
            TextNode.valueOf("value"))), null);
    assertTrue(results.getMutabilityIssues().isEmpty(),
        results.getMutabilityIssues().toString());

    // Can not replace immutable in patch if not already present
    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root().attribute("immutable"),
            TextNode.valueOf("value"))), null);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));

    // Can not add immutable in patch if it is the same
    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root().attribute("immutable"),
            TextNode.valueOf("value"))), o);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));

    // Can not replace immutable in patch if it is the same
    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root().attribute("immutable"),
            TextNode.valueOf("value"))), o);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));

    // Can not add immutable in patch if already present and different
    results = checker.checkModify(Collections.singleton(
        PatchOperation.add(Path.root().attribute("immutable"),
            TextNode.valueOf("newValue"))), o);
    assertEquals(results.getMutabilityIssues().size(), 2,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));

    // Can not replace immutable in patch if already present and different
    results = checker.checkModify(Collections.singleton(
        PatchOperation.replace(Path.root().attribute("immutable"),
            TextNode.valueOf("newValue"))), o);
    assertEquals(results.getMutabilityIssues().size(), 2,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));

    // Can not remove immutable
    results = checker.checkModify(Collections.singleton(
        PatchOperation.remove(Path.root().attribute("immutable"))), null);
    assertEquals(results.getMutabilityIssues().size(), 1,
        results.getMutabilityIssues().toString());
    assertTrue(containsIssueWith(results.getMutabilityIssues(), "immutable"));
  }

  /**
   * Test that the proper exceptions are thrown if errors are found during
   * schema checking.  This test uses a data provider for its data, and uses
   * reflection to set private fields of the results to make the test simpler.
   *
   * @param baseMsg The base message that will be used.
   * @param expectedMsg The expected exception message.
   * @param syntaxIssues A list of syntax issues.
   * @param mutabilityIssues A list of mutability issues.
   * @param pathIssues A list of path issues.
   * @throws Exception throw in case of error.
   */
  @Test(dataProvider="schemaResultsProvider")
  public void testSchemaResultExceptions(String baseMsg, String expectedMsg,
    List<String> syntaxIssues, List<String> mutabilityIssues,
    List<String> pathIssues) throws Exception
  {
    BadRequestException caughtException = null;

    SchemaChecker.Results results =
        getResults(syntaxIssues, mutabilityIssues, pathIssues);

    try
    {
      results.throwSchemaExceptions(baseMsg);
    }
    catch(BadRequestException ex)
    {
      caughtException = ex;
      Assert.assertEquals(caughtException.getMessage(), expectedMsg);
    }

    if (!syntaxIssues.isEmpty())
    {
      Assert.assertNotNull(caughtException);
      Assert.assertEquals(caughtException.getScimError().getScimType(),
          BadRequestException.INVALID_SYNTAX);
    } else if (!mutabilityIssues.isEmpty())
    {
      Assert.assertNotNull(caughtException);
      Assert.assertEquals(caughtException.getScimError().getScimType(),
          BadRequestException.MUTABILITY);
    }
    else if (!pathIssues.isEmpty())
    {
      Assert.assertNotNull(caughtException);
      Assert.assertEquals(caughtException.getScimError().getScimType(),
          BadRequestException.INVALID_PATH);
    } else
    {
      Assert.assertNull(caughtException, "Bad exception thrown");
    }
  }

  @DataProvider(name="schemaResultsProvider")
  private Object[][] getResultData()
  {
    return new Object[][] {
        {"TestMessage", "TestMessage syntaxIssueOne syntaxIssueTwo",
            Arrays.asList("syntaxIssueOne", "syntaxIssueTwo"),
            Collections.emptyList(), Collections.emptyList()},
        {"TestMessage", "TestMessage mutabilityIssueOne mutabilityIssueTwo",
            Collections.emptyList(),
            Arrays.asList("mutabilityIssueOne", "mutabilityIssueTwo"),
            Collections.emptyList()},
        {"TestMessage", "TestMessage pathIssueOne pathIssueTwo",
            Collections.emptyList(), Collections.emptyList(),
            Arrays.asList("pathIssueOne", "pathIssueTwo")},
        {"TestMessage", "TestMessage syntaxIssueOne syntaxIssueTwo",
            Arrays.asList("syntaxIssueOne", "syntaxIssueTwo"),
            Arrays.asList("mutabilityIssueOne", "mutabilityIssueTwo"),
            Arrays.asList("pathIssueOne", "pathIssueTwo")},
        {"TestMessage", "TestMessage mutabilityIssueOne",
            Collections.emptyList(),
            Arrays.asList("mutabilityIssueOne"),
            Arrays.asList("pathIssueOne", "pathIssueTwo")},
        {"TestMessage", null,
            Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()}
    };
  }

  private SchemaChecker.Results getResults(List<String> syntaxIssues,
      List<String> mutabilityIssues, List<String> pathIssues) throws Exception
  {
    SchemaChecker.Results results = new SchemaChecker.Results();

    Field syntaxField =
        SchemaChecker.Results.class.getDeclaredField("syntaxIssues");
    syntaxField.setAccessible(true);
    syntaxField.set(results, syntaxIssues);

    Field pathField =
        SchemaChecker.Results.class.getDeclaredField("pathIssues");
    pathField.setAccessible(true);
    pathField.set(results, pathIssues);

    Field mutabilityField =
        SchemaChecker.Results.class.getDeclaredField("mutabilityIssues");
    mutabilityField.setAccessible(true);
    mutabilityField.set(results, mutabilityIssues);

    return results;
  }


  private boolean containsIssueWith(Collection<String> issues, String issueText)
  {
    for(String issue: issues)
    {
      if(issue.contains(issueText))
      {
        return true;
      }
    }
    return false;
  }
}
