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
        """
            {
              "accountDisabled": false,
              "accountExpirationTime": "2015-07-06T04:03:02.000Z",
              "secondsUntilAccountExpiration": 10,
              "passwordChangedTime": "2015-07-06T04:04:02.000Z",
              "passwordExpirationWarnedTime": "2015-07-06T04:05:02.000Z",
              "secondsUntilPasswordExpiration": 11,
              "secondsUntilPasswordExpirationWarning": 12,
              "authenticationFailureTimes": [
                "2015-07-06T04:10:02.000Z",
                "2015-07-06T04:11:02.000Z",
                "2015-07-06T04:12:02.000Z"
              ],
              "secondsUntilAuthenticationFailureUnlock": 13,
              "remainingAuthenticationFailureCount": 14,
              "lastLoginTime": "2015-07-06T04:06:02.000Z",
              "secondsUntilIdleLockout": 15,
              "mustChangePassword": true,
              "graceLoginTimes": [
                "2015-07-06T04:20:02.000Z",
                "2015-07-06T04:21:02.000Z",
                "2015-07-06T04:22:02.000Z"
              ],
              "remainingGraceLoginCount": 17,
              "passwordChangedByRequiredTime": "2015-07-06T04:07:02.000Z",
              "passwordHistory": [
                "pw_one",
                "pw_two",
                "pw_three"
              ],
              "retiredPassword": {
                "passwordRetiredTime": "2015-07-06T04:20:02.000Z",
                "passwordExpirationTime": "2015-07-06T04:20:02.000Z"
              },
              "accountActivationTime": "2015-07-06T04:07:02.000Z",
              "secondsUntilAccountActivation": 18,
              "accountUsabilityNotices": [
                {
                  "name": "test1",
                  "message": "test is a test"
                }
              ],
              "accountUsabilityWarnings": [
                {
                  "name": "test1",
                  "message": "test is a test"
                }
              ],
              "accountUsabilityErrors": [
                {
                  "name": "test1",
                  "message": "test is a test"
                }
              ]
            }""";

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
