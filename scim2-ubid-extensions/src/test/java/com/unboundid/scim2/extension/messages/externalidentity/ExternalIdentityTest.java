/*
 * Copyright 2015-2019 Ping Identity Corporation
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

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


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
    String externalIdString =
        "{  \n" +
            "   \"provider\":{  \n" +
            "      \"name\":\"testName\",\n" +
            "      \"description\":\"testDescription\",\n" +
            "      \"iconUrl\":\"http://localhost:3020/test/url\",\n" +
            "      \"type\":\"testType\"\n" +
            "   },\n" +
            "   \"providerUserId\":\"testUserId\",\n" +
            "   \"accessToken\":\"testAccessToken\",\n" +
            "   \"refreshToken\":\"testRefreshToken\",\n" +
            "   \"callbackUrl\":\"testCallbackUrl\",\n" +
            "   \"callbackParameters\":\n" +
            "   {\n" +
            "       \"testParamOne\":\"testValueOne\",\n" +
            "       \"testParamTwo\":\"testValueTwo\",\n" +
            "       \"testParamThree\":\"testValueThree\"\n" +
            "   },\n" +
            "   \"meta\":{  \n" +
            "      \"created\":\"2015-07-04T00:00:00.000Z\",\n" +
            "      \"lastModified\":\"2015-07-06T04:03:02.000Z\"\n" +
            "   }\n" +
            "}";

    ExternalIdentity externalId = JsonUtils.getObjectReader().
        forType(ExternalIdentity.class).readValue(externalIdString);
    String serializedExternalId = JsonUtils.getObjectWriter().
        writeValueAsString(externalId);
    ExternalIdentity deserializedObject = JsonUtils.getObjectReader().
        forType(ExternalIdentity.class).readValue(serializedExternalId);
    Assert.assertEquals(externalId, deserializedObject);
  }
}
