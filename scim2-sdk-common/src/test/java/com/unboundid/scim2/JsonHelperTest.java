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

package com.unboundid.scim2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.schema.SchemaUtils;
import com.unboundid.scim2.utils.ScimJsonHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests JsonHelper class.
 */
@Test
public class JsonHelperTest
{
  /**
   * Tests the path and canonical type helpers.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testJsonHelperPath() throws Exception
  {
    String testString = "{ \"sv1\" : \"stringValue1\",\n" +
        "  \"nv1\" : 22,\n" +
        "  \"nv2\" : 12.30,\n" +
        "  \"av1\" : [ \"value1\", \"value2\", \"value3\" ],\n" +
        "  \"av2\" : [\n" +
        "    {\n" +
        "        \"address\" : \"home address\",\n" +
        "        \"type\" : \"home\"\n" +
        "    }, \n" +
        "    {\n" +
        "        \"address\" : \"office address\",\n" +
        "        \"type\" : \"office\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"address\" : \"other address\",\n" +
        "        \"type\" : \"other\"\n" +
        "    }   \n" +
        "],\n" +
        "  \"ov1\" :  {\n" +
        "        \"thing1\" : \"thing1.value\",\n" +
        "        \"thing2\" : \"thing2.value\"\n" +
        "    }   \n" +
        "}";

    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
    ScimJsonHelper helper = new ScimJsonHelper(mapper.readTree(testString));

    Assert.assertEquals(22, helper.path("nv1").intValue());
    Assert.assertEquals("thing1.value", helper.path("ov1").
        path("thing1").textValue());
    Assert.assertEquals(
        helper.path("av2").getCanonicalType("other")
            .path("address").textValue(), "other address");
  }
}
