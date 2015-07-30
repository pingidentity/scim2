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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    SchemaResource schema =
        new SchemaResource("test", "test", "test", attributeDefinitions);

    resourceTypeDefinition = new ResourceTypeDefinition.Builder(
        "test", "test").setCoreSchema(schema).build();

    ObjectNode node =
        (ObjectNode) SchemaUtils.createSCIMCompatibleMapper().readTree(
            "{  \n" +
            "  \"id\":\"test\",\n" +
            "  \"always\":\"here\",\n" +
            "  \"never\":\"here\",\n" +
            "  \"default\":\"here\",\n" +
            "  \"request\":\"here\"\n" +
            "}");
    testResource = new GenericScimResource(node);
    testPatch = new ArrayList<PatchOperation>(4);
    testPatch.add(PatchOperation.add(Path.fromString("always"),
        new TextNode("test")));
    testPatch.add(PatchOperation.add(Path.fromString("never"),
        new TextNode("test")));
    testPatch.add(PatchOperation.add(Path.fromString("default"),
        new TextNode("test")));
    testPatch.add(PatchOperation.add(Path.fromString("request"),
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
            new Object[] { null, true },
            new Object[] { AttributeDefinition.Returned.ALWAYS, true },
            new Object[] { AttributeDefinition.Returned.NEVER, true },
            new Object[] { AttributeDefinition.Returned.DEFAULT, true },
            new Object[] { AttributeDefinition.Returned.REQUEST, true },
            new Object[] { AttributeDefinition.Returned.ALWAYS, false },
            new Object[] { AttributeDefinition.Returned.NEVER, false },
            new Object[] { AttributeDefinition.Returned.DEFAULT, false },
            new Object[] { AttributeDefinition.Returned.REQUEST, false },
        };
  }

  /**
   * Test trim on retrieve.
   *
   * @param attribute The attribute to test as attributes/excludedAttributes.
   * @param excluded Whether the attribute is one of the excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testRetrieve(AttributeDefinition.Returned attribute,
                           boolean excluded)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            testBaseUri, attribute == null ?
            Collections.<Path>emptySet() :
            Collections.singleton(Path.fromString(attribute.getName())),
            excluded);

    GenericScimResource prepared = preparer.trimRetrievedResource(testResource);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    if(attribute != null &&
        attribute == AttributeDefinition.Returned.REQUEST && !excluded)
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }
  }



  /**
   * Test trim on create.
   *
   * @param attribute The attribute to test as attributes/excludedAttributes.
   * @param excluded Whether the attribute is one of the excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testCreate(AttributeDefinition.Returned attribute,
                         boolean excluded)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            testBaseUri, attribute == null ?
            Collections.<Path>emptySet() :
            Collections.singleton(Path.fromString(attribute.getName())),
            excluded);

    GenericScimResource prepared =
        preparer.trimCreatedResource(testResource, null);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    if(attribute != null &&
        attribute == AttributeDefinition.Returned.REQUEST && !excluded)
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    prepared = preparer.trimCreatedResource(testResource, testResource);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    assertTrue(prepared.getObjectNode().has("request"));
  }

  /**
   * Test trim on replace.
   *
   * @param attribute The attribute to test as attributes/excludedAttributes.
   * @param excluded Whether the attribute is one of the excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testReplace(AttributeDefinition.Returned attribute,
                          boolean excluded)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            testBaseUri, attribute == null ?
            Collections.<Path>emptySet() :
            Collections.singleton(Path.fromString(attribute.getName())),
            excluded);

    GenericScimResource prepared =
        preparer.trimReplacedResource(testResource, null);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    if(attribute != null &&
        attribute == AttributeDefinition.Returned.REQUEST && !excluded)
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    prepared = preparer.trimReplacedResource(testResource, testResource);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    assertTrue(prepared.getObjectNode().has("request"));
  }



  /**
   * Test trim on modify.
   *
   * @param attribute The attribute to test as attributes/excludedAttributes.
   * @param excluded Whether the attribute is one of the excludedAttributes.
   * @throws BadRequestException If an error occurs.
   */
  @Test(dataProvider = "dataProvider")
  public void testModify(AttributeDefinition.Returned attribute,
                         boolean excluded)
      throws BadRequestException
  {
    ResourcePreparer<ScimResource> preparer =
        new ResourcePreparer<ScimResource>(resourceTypeDefinition,
            testBaseUri, attribute == null ?
            Collections.<Path>emptySet() :
            Collections.singleton(Path.fromString(attribute.getName())),
            excluded);

    GenericScimResource prepared =
        preparer.trimModifiedResource(testResource, null);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    if(attribute != null &&
        attribute == AttributeDefinition.Returned.REQUEST && !excluded)
    {
      assertTrue(prepared.getObjectNode().has("request"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("request"));
    }

    prepared = preparer.trimModifiedResource(testResource, testPatch);
    assertTrue(prepared.getObjectNode().has("always"));
    assertFalse(prepared.getObjectNode().has("never"));
    if(attribute == null ||
        (excluded && attribute != AttributeDefinition.Returned.DEFAULT) ||
        (!excluded && attribute == AttributeDefinition.Returned.DEFAULT))
    {
      assertTrue(prepared.getObjectNode().has("default"));
    }
    else
    {
      assertFalse(prepared.getObjectNode().has("default"));
    }
    assertTrue(prepared.getObjectNode().has("request"));
  }
}
