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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.JsonUtils;
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
    ObjectNode node = (ObjectNode) JsonUtils.getObjectReader().
        readTree("{\n" +
            "    \"externalId\": \"user:externalId\",\n" +
            "    \"id\": \"user:id\",\n" +
            "    \"meta\": {\n" +
            "        \"created\": \"2015-02-27T11:28:39Z\",\n" +
            "        \"lastModified\": \"2015-02-27T11:29:39Z\",\n" +
            "        \"locAtion\": \"http://here/user\",\n" +
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
            "    \"Schemas\": [" +
            "    \"urn:unboundid:schemas:baseSchema\", " +
            "    \"urn:unboundid:schemas:favoriteColor\"" +
            "    ],\n" +
            "    \"urn:unboundid:schemas:favoriteColor\": {\n" +
            "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
            "    },\n" +
            "    \"userName\": \"user:username\"\n" +
            "}");

    GenericScimResource gso = new GenericScimResource(node);

    ScimResource cso = gso;

    Set<String> schemaSet = new HashSet<String>();
    schemaSet.add("urn:unboundid:schemas:baseSchema");
    schemaSet.add("urn:unboundid:schemas:favoriteColor");

    Assert.assertTrue(cso.getSchemaUrns().containsAll(schemaSet));

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
        ((GenericScimResource)cso).getObjectNode().path("shoeSize")
            .asText());
  }
}
