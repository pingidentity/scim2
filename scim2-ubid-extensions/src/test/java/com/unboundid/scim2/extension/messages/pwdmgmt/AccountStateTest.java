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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class AccountStateTest
{
  /**
   * Tests serialization of AccountState objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testAccountState() throws Exception
  {
    String accountStateString =
        "{\n" +
            "  \"accountDisabled\": false,\n" +
            "  \"accountExpirationTime\": \"2015-07-06T04:03:02.000Z\",\n" +
            "  \"secondsUntilAccountExpiration\": 10,\n" +
            "  \"passwordChangedTime\": \"2015-07-06T04:04:02.000Z\",\n" +
            "  \"passwordExpirationWarnedTime\": \"2015-07-06T04:05:02.000Z\",\n" +
            "  \"secondsUntilPasswordExpiration\": 11,\n" +
            "  \"secondsUntilPasswordExpirationWarning\": 12,\n" +
            "  \"authenticationFailureTimes\": [\n" +
            "    \"2015-07-06T04:10:02.000Z\",\n" +
            "    \"2015-07-06T04:11:02.000Z\",\n" +
            "    \"2015-07-06T04:12:02.000Z\"\n" +
            "  ],\n" +
            "  \"secondsUntilAuthenticationFailureUnlock\": 13,\n" +
            "  \"remainingAuthenticationFailureCount\": 14,\n" +
            "  \"lastLoginTime\": \"2015-07-06T04:06:02.000Z\",\n" +
            "  \"secondsUntilIdleLockout\": 15,\n" +
            "  \"mustChangePassword\": true,\n" +
            "  \"graceLoginTimes\": [\n" +
            "    \"2015-07-06T04:20:02.000Z\",\n" +
            "    \"2015-07-06T04:21:02.000Z\",\n" +
            "    \"2015-07-06T04:22:02.000Z\"\n" +
            "  ],\n" +
            "  \"remainingGraceLoginCount\": 17,\n" +
            "  \"passwordChangedByRequiredTime\": \"2015-07-06T04:07:02.000Z\",\n" +
            "  \"passwordHistory\": [\n" +
            "    \"pw_one\",\n" +
            "    \"pw_two\",\n" +
            "    \"pw_three\"\n" +
            "  ],\n" +
            "  \"retiredPassword\": {\n" +
            "    \"passwordRetiredTime\": \"2015-07-06T04:20:02.000Z\",\n" +
            "    \"passwordExpirationTime\": \"2015-07-06T04:20:02.000Z\"\n" +
            "  },\n" +
            "  \"accountActivationTime\": \"2015-07-06T04:07:02.000Z\",\n" +
            "  \"secondsUntilAccountActivation\": 18,\n" +
            "  \"accountUsabilityNotices\": [\n" +
            "    {\n" +
            "      \"name\": \"test1\",\n" +
            "      \"message\": \"test is a test\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"accountUsabilityWarnings\": [\n" +
            "    {\n" +
            "      \"name\": \"test1\",\n" +
            "      \"message\": \"test is a test\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"accountUsabilityErrors\": [\n" +
            "    {\n" +
            "      \"name\": \"test1\",\n" +
            "      \"message\": \"test is a test\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    AccountState accountState =
        JsonUtils.getObjectReader().forType(AccountState.class).
        readValue(accountStateString);
    String serializedConsent =
        JsonUtils.getObjectWriter().writeValueAsString(accountState);
    AccountState deserializedAccountState = JsonUtils.getObjectReader().
        forType(AccountState.class).readValue(serializedConsent);
    Assert.assertEquals(accountState, deserializedAccountState);
  }
}
