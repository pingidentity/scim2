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

package com.unboundid.scim2.extension.messages.consent;

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


@Test
public class ConsentHistoryTest
{
  /**
   * Tests serialization of ConsentHistory objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String consentHistoryString =
        "{  \n" +
            "   \"client\":{  \n" +
            "      \"name\":\"appName\",\n" +
            "      \"description\":\"appDesc\",\n" +
            "      \"iconUrl\":\"http://localhost:12345/app\",\n" +
            "      \"emailAddress\":\"email@address.com\"\n" +
            "   },\n" +
            "   \"scopes\":[  \n" +
            "      {  \n" +
            "         \"name\":\"name1\",\n" +
            "         \"description\":\"description1\",\n" +
            "         \"consent\":\"granted\"\n" +
            "      },\n" +
            "      {  \n" +
            "         \"name\":\"name2\",\n" +
            "         \"description\":\"description2\",\n" +
            "         \"consent\":\"denied\"\n" +
            "      },\n" +
            "      {  \n" +
            "         \"name\":\"name3\",\n" +
            "         \"description\":\"description3\",\n" +
            "         \"consent\":\"revoked\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"meta\":{  \n" +
            "      \"lastModified\":\"2015-07-04T00:00:00.000Z\"\n" +
            "   },\n" +
            "   \"id\":\"ConsentHistoryId\"\n" +
            "}";

    ConsentHistory consentHistory = JsonUtils.getObjectReader().forType(
        ConsentHistory.class).readValue(consentHistoryString);
    String serializedConsentHistory = JsonUtils.getObjectWriter().
        writeValueAsString(consentHistory);
    ConsentHistory deserializedConsentHistory = JsonUtils.getObjectReader().
        forType(ConsentHistory.class).readValue(serializedConsentHistory);
    Assert.assertEquals(consentHistory, deserializedConsentHistory);
  }
}
