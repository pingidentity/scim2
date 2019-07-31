/*
 * Copyright 2016-2019 Ping Identity Corporation
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

package com.unboundid.scim2.extension.messages.sessionmgmt;

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests Session objects.
 */
public class SessionTest
{
  /**
   * Test serialization to and from JSON.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String sessionString =
        "{  \n" +
            "   \"lastLoginMethods\": \n" +
            "    [ \"Method One\", \"Method Two\", \"Method Three\" ], \n" +
            "   \"lastSecondFactorMethods\": \n" +
            "    [ \"SF Method One\", \"SF Method Two\" ], \n" +
            "   \"lastLogin\":\"2015-07-06T04:03:02.000Z\", \n" +
            "   \"lastSecondFactor\":\"2015-07-07T04:03:02.000Z\", \n" +
            "   \"ipAddress\":\"192.168.10.34\", \n" +
            "   \"userAgentString\":\"User Agent String\", \n" +
            "   \"clients\": \n" +
            "    [ \n" +
            "    { \n" +
            "       \"name\":\"Client 1\", \n" +
            "       \"description\":\"Description 1\", \n" +
            "       \"url\":\"http://localhost:8080/myUrl1\", \n" +
            "       \"iconUrl\":\"http://localhost:8080/myUrl1/icon.png\", \n" +
            "       \"emailAddress\":\"someone1@somewhere.com\", \n" +
            "       \"lastAuthorization\":\"2015-07-08T04:03:02.000Z\" \n" +
            "    }, \n" +
            "    { \n" +
            "       \"name\":\"Client 2\", \n" +
            "       \"description\":\"Description 2\", \n" +
            "       \"url\":\"http://localhost:8080/myUrl2\", \n" +
            "       \"iconUrl\":\"http://localhost:8080/myUrl2/icon.png\", \n" +
            "       \"emailAddress\":\"someone2@somewhere.com\", \n" +
            "       \"lastAuthorization\":\"2015-07-09T04:03:02.000Z\" \n" +
            "    }, \n" +
            "    { \n" +
            "       \"name\":\"Client 3\", \n" +
            "       \"description\":\"Description 3\", \n" +
            "       \"url\":\"http://localhost:8080/myUrl3\", \n" +
            "       \"iconUrl\":\"http://localhost:8080/myUrl3/icon.png\", \n" +
            "       \"emailAddress\":\"someone3@somewhere.com\", \n" +
            "       \"lastAuthorization\":\"2015-07-10T04:03:02.000Z\" \n" +
            "    } \n" +
            "    ] \n" +
            "}";

    Session session = JsonUtils.getObjectReader().forType(Session.class).
        readValue(sessionString);
    String serializedSession =
        JsonUtils.getObjectWriter().writeValueAsString(session);
    Session deserializedSession = JsonUtils.getObjectReader().
        forType(Session.class).readValue(serializedSession);
    Assert.assertEquals(session, deserializedSession);
  }
}
