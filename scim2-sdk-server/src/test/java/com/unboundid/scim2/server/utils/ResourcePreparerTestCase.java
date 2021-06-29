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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test case for the Resource Preparer utility.
 */
public class ResourcePreparerTestCase
{

  private ResourceTypeDefinition resourceTypeDefinition;
  private GenericScimResource testResource;
  private ArrayList<PatchOperation> testPatch;
  private URI testBaseUri = URI.create("https://test/scim");

  /**
   * Setup a resource def with a test schema.
   *
   * @throws Exception If an error occurs.
   */
  @BeforeClass
  public void setUp() throws Exception
  {
    List<AttributeDefinition> attributeDefinitions =
        new ArrayList<AttributeDefinition>(4);

    AttributeDefinition.Builder builder = new AttributeDefinition.Builder();
    builder.setName("always");
    builder.setType(AttributeDefinition.Type.STRING);
    builder.setReturned(AttributeDefinition.Returned.ALWAYS);
    attributeDefinitions.add(builder.build());

    builder.setName("never");
    builder.setReturned(AttributeDefinition.Returned.NEVER);
    attributeDefinitions.add(builder.build());

    builder.setName("default");
    builder.setReturned(AttributeDefinition.Returned.DEFAULT);
    attributeDefinitions.add(builder.build());

    builder.setName("request");
    builder.setReturned(AttributeDefinition.Returned.REQUEST);
    attributeDefinitions.add(builder.build());

    AttributeDefinition[] attributeDefinitionsArray =
        attributeDefinitions.toArray(
            new AttributeDefinition[attributeDefinitions.size()]);
    List<AttributeDefinition> complexAttributeDefinitions =
        new ArrayList<AttributeDefinition>(4);
    builder = new AttributeDefinition.Builder();
    builder.setName("always");
    builder.setType(AttributeDefinition.Type.COMPLEX);
    builder.setReturned(AttributeDefinition.Returned.ALWAYS);
    builder.addSubAttributes(attributeDefinitionsArray);
    complexAttributeDefinitions.add(builder.build());

    builder.setName("never");
    builder.setReturned(AttributeDefinition.Returned.NEVER);
    complexAttributeDefinitions.add(builder.build());

    builder.setName("default");
    builder.setReturned(AttributeDefinition.Returned.DEFAULT);
    complexAttributeDefinitions.add(builder.build());

    builder.setName("request");
    builder.setReturned(AttributeDefinition.Returned.REQUEST);
    complexAttributeDefinitions.add(builder.build());


    SchemaResource schema =
        new SchemaResource("urn:test", "test", "test",
                           complexAttributeDefinitions);
    SchemaResource extensionSchema =
        new SchemaResource("urn:ext:1", "ext", "ext",
                           complexAttributeDefinitions);

    resourceTypeDefinition = new ResourceTypeDefinition.Builder(
        "test", "test").setCoreSchema(schema).addOptionalSchemaExtension(
        extensionSchema).build();

    ObjectNode node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{  \n" +
            "  \"id\":\"test\",\n" +
            "  \"always\":\n" +
            "  {\n" +
            "    \"always\":\"here\",\n" +
            "    \"neVEr\":\"here\",\n" +
            "    \"default\":\"here\",\n" +
            "    \"rEquest\":\"here\"\n" +
            "  },\n" +
            "  \"neVEr\":\n" +
            "  {\n" +
            "    \"always\":\"here\",\n" +
            "    \"neVEr\":\"here\",\n" +
            "    \"default\":\"here\",\n" +
            "    \"rEquest\":\"here\"\n" +
            "  },\n" +
            "  \"default\":\n" +
            "  {\n" +
            "    \"always\":\"here\",\n" +
            "    \"neVEr\":\"here\",\n" +
            "    \"default\":\"here\",\n" +
            "    \"notDeclared\":\"here\",\n" +
            "    \"rEquest\":\"here\"\n" +
            "  },\n" +
            "  \"rEquest\":\n" +
            "  {\n" +
            "    \"always\":\"here\",\n" +
            "    \"neVEr\":\"here\",\n" +
            "    \"default\":\"here\",\n" +
            "    \"rEquest\":\"here\"\n" +
            "  },\n" +
            "  \"urn:ext:1\":\n" +
            "  {\n" +
            "    \"always\":\n" +
            "    {\n" +
            "      \"always\":\"here\",\n" +
            "      \"neVEr\":\"here\",\n" +
            "      \"default\":\"here\",\n" +
            "      \"rEquest\":\"here\"\n" +
            "    },\n" +
            "    \"neVEr\":\n" +
            "    {\n" +
            "      \"always\":\"here\",\n" +
            "      \"neVEr\":\"here\",\n" +
            "      \"default\":\"here\",\n" +
            "      \"rEquest\":\"here\"\n" +
            "    },\n" +
            "    \"default\":\n" +
            "    {\n" +
            "      \"always\":\"here\",\n" +
            "      \"neVEr\":\"here\",\n" +
            "      \"default\":\"here\",\n" +
            "      \"rEquest\":\"here\"\n" +
            "    },\n" +
            "    \"rEquest\":\n" +
            "    {\n" +
            "      \"always\":\"here\",\n" +
            "      \"neVEr\":\"here\",\n" +
            "      \"default\":\"here\",\n" +
            "      \"rEquest\":\"here\"\n" +
            "    }\n" +
            "  }\n" +
            "}");
    testResource = new GenericScimResource(node);
    testPatch = new ArrayList<PatchOperation>(4);
    testPatch.add(PatchOperation.add(Path.fromString("alWays"),
        new TextNode("test")));
    testPatch.add(PatchOperation.add(Path.fromString("neveR"),
        new TextNode("test")));
    testPatch.add(PatchOperation.add(Path.fromString("defauLt"),
        new TextNode("test")));
    testPatch.add(PatchOperation.add(Path.fromString("Request"),
        new TextNode("test")));
  }

  /**
   * Test combination data provider.
   *
   * @return The combinations of attributes/excludedAttributes to test.
   */
  @DataProvider
  public Object[][] dataProvider()
  {
    return new Object[][]
        {
            new Object[] { null, null },
            new Object[] { null, "always" },
            new Object[] { null, "always.default"},
            new Object[] { null, "never" },
            new Object[] { null, "urn:test:default" },
            new Object[] { null, "reQuest" },
            new Object[] { null, "default.DEFAULT"},
            new Object[] { "Always", null },
            new Object[] { "neveR", null },
            new Object[] { "urn:test:DEFAULT", null },
            new Object[] { "default.DEFAULT", null},
            new Object[] { "request", null },
            new Object[] { null, "urn:ext:1:always" },
            new Object[] { null, "urn:ext:1:never" },
            new Object[] { null, "urn:ext:1:default" },
            new Object[] { null, "urn:ext:1:reQuest" },
            new Object[] { "urn:ext:1:Always", null },
            new Object[] { "urn:ext:1:neveR", null },
            new Object[] { "urn:ext:1:DEFAULT", null },
            new Object[] { "urn:ext:1:request", null },
        };
  }

  /**
   * Test trim on retrieve.
   *
   * @param attributes The value to test as attributes
   * @param excludedAttributes the value to test as excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testRetrieve(String attributes, String excludedAttributes)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            attributes, excludedAttributes, testBaseUri);

    GenericScimResource prepared = preparer.trimRetrievedResource(testResource);
    assertTrue(prepared.getObjectNode().has("always"));
    ObjectNode innerNode = (ObjectNode)prepared.getObjectNode().get("always");
    assertTrue(innerNode.has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(innerNode.has("default"));
    }
    assertFalse(innerNode.has("never"));

    assertFalse(prepared.getObjectNode().has("never"));

    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
      {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }

    if(attributes != null && attributes.equalsIgnoreCase("request"))
    {
      assertTrue(prepared.getObjectNode().has("request"));
      innerNode = (ObjectNode)prepared.getObjectNode().get("request");
      assertTrue(innerNode.has("always"));
      assertFalse(innerNode.has("never"));
      assertTrue(innerNode.has("request"), prepared.toString());
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    assertTrue(prepared.getObjectNode().has("urn:ext:1"));
    ObjectNode extNode = (ObjectNode)prepared.getObjectNode().get("urn:ext:1");
    assertTrue(extNode.has("always"));
    assertFalse(extNode.has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:ext:1:default")) ||
        (attributes != null &&
         attributes.equalsIgnoreCase("urn:ext:1:default")))
    {
      assertTrue(extNode.has("default"));
    }
    else
    {
      assertFalse(extNode.has("default"));
    }
    if(attributes != null && attributes.equalsIgnoreCase("urn:ext:1:request"))
    {
      assertTrue(extNode.has("request"));
    }
    else
    {
      assertFalse(extNode.has("request"));
    }
  }



  /**
   * Test trim on create.
   *
   * @param attributes The value to test as attributes
   * @param excludedAttributes the value to test as excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testCreate(String attributes, String excludedAttributes)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            attributes, excludedAttributes, testBaseUri);

    GenericScimResource prepared =
        preparer.trimCreatedResource(testResource, null);
    assertTrue(prepared.getObjectNode().has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(prepared.getObjectNode().get("always").has("default"));
    }
    assertFalse(prepared.getObjectNode().has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
      {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }

    if(attributes != null && attributes.equalsIgnoreCase("request"))
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    prepared = preparer.trimCreatedResource(testResource, testResource);
    assertTrue(prepared.getObjectNode().has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(prepared.getObjectNode().get("always").has("default"));
    }
    assertFalse(prepared.getObjectNode().has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
      {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }
    assertTrue(prepared.getObjectNode().has("request"));
  }

  /**
   * Test trim on replace.
   *
   * @param attributes The value to test as attributes
   * @param excludedAttributes the value to test as excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testReplace(String attributes, String excludedAttributes)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            attributes, excludedAttributes, testBaseUri);

    GenericScimResource prepared =
        preparer.trimReplacedResource(testResource, null);
    assertTrue(prepared.getObjectNode().has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(prepared.getObjectNode().get("always").has("default"));
    }
    assertFalse(prepared.getObjectNode().has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
      {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }
    if(attributes != null && attributes.equalsIgnoreCase("request"))
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    prepared = preparer.trimReplacedResource(testResource, testResource);
    assertTrue(prepared.getObjectNode().has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(prepared.getObjectNode().get("always").has("default"));
    }
    assertFalse(prepared.getObjectNode().has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
        {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }
    assertTrue(prepared.getObjectNode().has("request"));
  }



  /**
   * Test trim on modify.
   *
   * @param attributes The value to test as attributes
   * @param excludedAttributes the value to test as excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testModify(String attributes, String excludedAttributes)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            attributes, excludedAttributes, testBaseUri);

    GenericScimResource prepared =
        preparer.trimModifiedResource(testResource, null);
    assertTrue(prepared.getObjectNode().has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(prepared.getObjectNode().get("always").has("default"));
    }
    assertFalse(prepared.getObjectNode().has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
      {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }
    if(attributes != null && attributes.equalsIgnoreCase("request"))
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    prepared = preparer.trimModifiedResource(testResource, testPatch);
    assertTrue(prepared.getObjectNode().has("always"));
    if(excludedAttributes != null &&
        excludedAttributes.equalsIgnoreCase("always.default"))
    {
      assertFalse(prepared.getObjectNode().get("always").has("default"));
    }
    assertFalse(prepared.getObjectNode().has("never"));
    if((attributes == null && excludedAttributes == null) ||
        (excludedAttributes != null &&
            !excludedAttributes.equalsIgnoreCase("urn:test:default") &&
            !excludedAttributes.equalsIgnoreCase("default.default")) ||
        (attributes != null &&
            (attributes.equalsIgnoreCase("urn:test:default") ||
                attributes.equalsIgnoreCase("default.default"))))
    {
      assertTrue(prepared.getObjectNode().has("default"));
      if(attributes != null && attributes.equalsIgnoreCase("default.default"))
      {
        // Only the requested default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("notDeclared"));
      }
      if(attributes != null && attributes.equalsIgnoreCase("urn:test:default"))
      {
        // All default returned sub-attribute should be included
        assertTrue(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
      }
    }
    else
    {
      if (excludedAttributes != null && excludedAttributes.equalsIgnoreCase("default.default"))
      {
        // default.default should be excluded but other subAttributes should be present
        assertTrue(prepared.getObjectNode().has("default"));
        assertFalse(prepared.getObjectNode().get("default").has("default"));
        assertTrue(prepared.getObjectNode().get("default").has("notDeclared"));
        assertTrue(prepared.getObjectNode().get("default").has("always"));
      }
      else
      {
        assertFalse(prepared.getObjectNode().has("default"));
      }
    }
    assertTrue(prepared.getObjectNode().has("request"));
  }

  /**
   * Test that empty containers are never included.
   *
   * @throws Exception If an error occurs.
   */
  @Test
  public void testEmptyContainer()
      throws Exception
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            "default", null, testBaseUri);

    ObjectNode node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{\n" +
                "  \"default\": {\n" +
                "    \"never\": \"here\"\n" +
                "  }\n" +
                "}");

    GenericScimResource prepared = preparer.trimRetrievedResource(
        new GenericScimResource(node));
    assertFalse(prepared.getObjectNode().has("default"));

    node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{\n" +
                "  \"default\": [\n" +
                "    {\n" +
                "      \"never\": \"here\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"always\": \"here\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

    prepared = preparer.trimRetrievedResource(
        new GenericScimResource(node));
    assertTrue(prepared.getObjectNode().has("default"));
    assertEquals(prepared.getObjectNode().get("default").size(), 1);


    node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{\n" +
                "  \"default\": [\n" +
                "    {\n" +
                "      \"never\": \"here\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"never\": \"here\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

    prepared = preparer.trimRetrievedResource(
        new GenericScimResource(node));
    assertFalse(prepared.getObjectNode().has("default"));

    node =
        (ObjectNode) JsonUtils.getObjectReader().readTree(
            "{\n" +
                "  \"urn:ext:1\": {\n" +
                "    \"never\": \"here\"\n" +
                "  }\n" +
                "}");

    prepared = preparer.trimRetrievedResource(
        new GenericScimResource(node));
    assertFalse(prepared.getObjectNode().has("urn:ext:1"));
  }
}
