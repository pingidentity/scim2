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
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.extension.messages.JsonObjectStringBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class ExternalIdentityTest
{
  private final ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();

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
    JsonObjectStringBuilder meta_jsob = new JsonObjectStringBuilder();
    meta_jsob.appendProperty("created", meta_created);
    meta_jsob.appendProperty("lastModified", meta_lastModified);
    Meta meta = mapper.readValue(meta_jsob.toString(), Meta.class);

    String providerUserId = "testUserId";
    String accessToken = "testAccessToken";
    String refreshToken = "testRefreshToken";

    JsonObjectStringBuilder jsob = new JsonObjectStringBuilder();
    jsob.appendProperty("provider", mapper, provider);
    jsob.appendProperty("providerUserId", providerUserId);
    jsob.appendProperty("accessToken", accessToken);
    jsob.appendProperty("refreshToken", refreshToken);
    jsob.appendProperty("meta", meta_jsob);

    ExternalIdentity extId1 =
        mapper.readValue(jsob.toString(), ExternalIdentity.class);


    Assert.assertEquals(provider, extId1.getProvider());
    Assert.assertEquals(providerUserId, extId1.getProviderUserId());
    Assert.assertEquals(accessToken, extId1.getAccessToken());
    Assert.assertEquals(refreshToken, extId1.getRefreshToken());
    Assert.assertEquals(meta, extId1.getMeta());

    ExternalIdentity extId2 = mapper.readValue(
        mapper.writeValueAsString(extId1), ExternalIdentity.class);
    Assert.assertEquals(extId1, extId2);
  }
}
