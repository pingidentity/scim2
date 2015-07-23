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

package com.unboundid.scim2.extension.messages.externalidentity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.extension.messages.JsonObjectStringBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class ProviderTest
{
  private final ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();

  /**
   * Tests serialization of Provider objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String name = "testName";
    String description = "testDescription";
    String iconUrl = "https://localhost:12345/test/url";
    String type = "testType";

    JsonObjectStringBuilder jsob = new JsonObjectStringBuilder();
    jsob.appendProperty("name", name);
    jsob.appendProperty("description", description);
    jsob.appendProperty("iconUrl", iconUrl);
    jsob.appendProperty("type", type);

    Provider provider1 = mapper.readValue(jsob.toString(), Provider.class);
    Assert.assertEquals(name, provider1.getName());
    Assert.assertEquals(description, provider1.getDescription());
    Assert.assertEquals(type, provider1.getType());
    Assert.assertEquals(new URL(iconUrl), provider1.getIconUrl());

    Provider provider2 =
        mapper.readValue(mapper.writeValueAsString(provider1), Provider.class);
    Assert.assertEquals(provider1, provider2);
  }
}
