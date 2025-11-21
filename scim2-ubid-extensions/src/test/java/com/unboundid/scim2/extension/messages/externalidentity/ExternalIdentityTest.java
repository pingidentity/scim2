/*
 * Copyright 2015-2025 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    String externalIdString = """
            {
               "provider":{
                  "name":"testName",
                  "description":"testDescription",
                  "iconUrl":"http://localhost:3020/test/url",
                  "type":"testType"
               },
               "providerUserId":"testUserId",
               "accessToken":"testAccessToken",
               "refreshToken":"testRefreshToken",
               "callbackUrl":"testCallbackUrl",
               "callbackParameters":
               {
                   "testParamOne":"testValueOne",
                   "testParamTwo":"testValueTwo",
                   "testParamThree":"testValueThree"
               },
               "meta":{
                  "created":"2015-07-04T00:00:00.000Z",
                  "lastModified":"2015-07-06T04:03:02.000Z"
               }
            }""";

    ExternalIdentity externalId = JsonUtils.getObjectReader().
        forType(ExternalIdentity.class).readValue(externalIdString);
    String serializedExternalId = JsonUtils.getObjectWriter().
        writeValueAsString(externalId);
    ExternalIdentity deserializedObject = JsonUtils.getObjectReader().
        forType(ExternalIdentity.class).readValue(serializedExternalId);
    Assert.assertEquals(externalId, deserializedObject);
  }
}
