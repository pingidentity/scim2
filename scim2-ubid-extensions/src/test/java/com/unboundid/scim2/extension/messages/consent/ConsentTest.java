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

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ConsentTest
{
  /**
   * Tests serialization of Consent objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String consentString =
        """
            {
               "client":{
                  "name":"appName",
                  "description":"appDesc",
                  "iconUrl":"http://localhost:12345/app",
                  "emailAddress":"email@address.com"
               },
               "scopes":[
                  {
                     "name":"name1",
                     "description":"description1",
                     "consent":"granted"
                  },
                  {
                     "name":"name2",
                     "description":"description2",
                     "consent":"denied"
                  },
                  {
                     "name":"name3",
                     "description":"description3",
                     "consent":"revoked"
                  }
               ],
               "meta":{
                  "lastModified":"2015-07-06T04:03:02.000Z"
               },
               "id":"ConsentId"
            }""";

    Consent consent = JsonUtils.getObjectReader().forType(Consent.class).
        readValue(consentString);
    String serializedConsent =
        JsonUtils.getObjectWriter().writeValueAsString(consent);
    Consent deserializedConsent = JsonUtils.getObjectReader().
        forType(Consent.class).readValue(serializedConsent);
    Assert.assertEquals(consent, deserializedConsent);
  }
}
