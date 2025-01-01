/*
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.extension.messages.consent;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OAuth2ClientTest
{
  /**
   * Tests serialization of Client objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String name = "testName";
    String description = "testDescription";
    String url = "https://localhost:12345/test/app/url";
    String iconUrl = "https://localhost:12345/test/icon/url";
    String emailAddress = "unit@test.com";

    ObjectNode objectNode = JsonUtils.getJsonNodeFactory().objectNode();
    objectNode.put("name", name);
    objectNode.put("description", description);
    objectNode.put("iconUrl", iconUrl);
    objectNode.put("url", url);
    objectNode.put("emailAddress", emailAddress);

    OAuth2Client app1 = JsonUtils.getObjectReader().forType(OAuth2Client.class).
        readValue(objectNode.toString());
    Assert.assertEquals(app1.getName(), name);
    Assert.assertEquals(app1.getDescription(), description);
    Assert.assertEquals(app1.getIconUrl(), iconUrl);
    Assert.assertEquals(app1.getEmailAddress(), emailAddress);

    OAuth2Client app2 =
        JsonUtils.getObjectReader().forType(OAuth2Client.class).readValue(
            JsonUtils.getObjectWriter().writeValueAsString(app1));
    Assert.assertEquals(app1, app2);
  }
}
