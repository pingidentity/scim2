/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2;

import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.model.ScimResource;
import com.unboundid.scim2.model.GenericScimResourceObject;
import com.unboundid.scim2.model.Meta;
import com.unboundid.scim2.schema.SchemaUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * Tests generic scim objects.
 */
@Test
public class GenericScimResourceObjectTest
{
  private DateFormat dateFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  /**
   * Constructor.  Sets up the dateFormat.
   */
  public GenericScimResourceObjectTest()
  {
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  /**
   * Tests parsing a json string into a GenericScimObject.
   *
   * @throws Exception in event of an error.
   */
  @Test
  public void testBasicParsing() throws Exception
  {
    JsonNode node = SchemaUtils.createSCIMCompatibleMapper().
        readTree("{\n" +
            "    \"externalId\": \"user:externalId\",\n" +
            "    \"id\": \"user:id\",\n" +
            "    \"meta\": {\n" +
            "        \"created\": \"2015-02-27T11:28:39Z\",\n" +
            "        \"lastModified\": \"2015-02-27T11:29:39Z\",\n" +
            "        \"location\": \"http://here/user\",\n" +
            "        \"resourceType\": \"some resource type\",\n" +
            "        \"version\": \"1.0\"\n" +
            "    },\n" +
            "    \"name\": {\n" +
            "        \"first\": \"name:first\",\n" +
            "        \"last\": \"name:last\",\n" +
            "        \"middle\": \"name:middle\"\n" +
            "    },\n" +
            "    \"shoeSize\" : \"12W\",\n" +
            "    \"password\": \"user:password\",\n" +
            "    \"schemas\": [" +
            "    \"urn:unboundid:schemas:baseSchema\", " +
            "    \"urn:unboundid:schemas:favoriteColor\"" +
            "    ],\n" +
            "    \"urn:unboundid:schemas:favoriteColor\": {\n" +
            "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
            "    },\n" +
            "    \"userName\": \"user:username\"\n" +
            "}");

    GenericScimResourceObject gso = new GenericScimResourceObject();
    gso.setJsonNode(node);

    ScimResource cso = gso;

    Set<String> schemaSet = new HashSet<String>();
    schemaSet.add("urn:unboundid:schemas:baseSchema");
    schemaSet.add("urn:unboundid:schemas:favoriteColor");

    Assert.assertEquals(schemaSet, cso.getSchemaUrns());
    Assert.assertEquals("user:id", cso.getId());
    Assert.assertEquals("user:externalId", cso.getExternalId());
    Meta meta = cso.getMeta();

    Assert.assertEquals("2015-02-27T11:28:39Z",
        dateFormat.format(meta.getCreated().getTime()));
    Assert.assertEquals("2015-02-27T11:29:39Z",
        dateFormat.format(meta.getLastModified().getTime()));
    Assert.assertEquals("http://here/user".toString(),
        meta.getLocation().toString());
    Assert.assertEquals("some resource type", meta.getResourceType());
    Assert.assertEquals("1.0", meta.getVersion());

    Assert.assertEquals("12W",
        ((GenericScimResourceObject)cso).getJsonNode().path("shoeSize")
            .asText());
  }
}
