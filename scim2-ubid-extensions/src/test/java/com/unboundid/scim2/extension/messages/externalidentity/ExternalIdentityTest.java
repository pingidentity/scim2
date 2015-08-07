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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class ExternalIdentityTest
{
  /**
   * Tests serialization of ExternalIdentity objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    Provider provider = new Provider.Builder().setName("testName").
        setDescription("testDescription").
        setIconUrl(new URL("http://localhost:3020/test/url")).
        setType("testType").build();


    String meta_created = "2015-07-04T00:00:00Z";
    String meta_lastModified = "2015-07-06T04:03:02Z";
    ObjectNode metaNode = JsonUtils.getJsonNodeFactory().objectNode();
    metaNode.put("created", meta_created);
    metaNode.put("lastModified", meta_lastModified);
    Meta meta = JsonUtils.getObjectReader().forType(Meta.class).readValue(
        metaNode.toString());

    String providerUserId = "testUserId";
    String accessToken = "testAccessToken";
    String refreshToken = "testRefreshToken";

    ObjectNode objectNode = JsonUtils.getJsonNodeFactory().objectNode();
    objectNode.putPOJO("provider", provider);
    objectNode.put("providerUserId", providerUserId);
    objectNode.put("accessToken", accessToken);
    objectNode.put("refreshToken", refreshToken);
    objectNode.putObject("meta").put("created", meta_created).put(
        "lastModified", meta_lastModified);

    ExternalIdentity extId1 =
        JsonUtils.getObjectReader().forType(ExternalIdentity.class).readValue(
            JsonUtils.getObjectWriter().writeValueAsString(objectNode));


    Assert.assertEquals(provider, extId1.getProvider());
    Assert.assertEquals(providerUserId, extId1.getProviderUserId());
    Assert.assertEquals(accessToken, extId1.getAccessToken());
    Assert.assertEquals(refreshToken, extId1.getRefreshToken());
    Assert.assertEquals(meta, extId1.getMeta());

    ExternalIdentity extId2 = JsonUtils.getObjectReader().forType(
        ExternalIdentity.class).readValue(
        JsonUtils.getObjectWriter().writeValueAsString(extId1));
    Assert.assertEquals(extId1, extId2);
  }
}
