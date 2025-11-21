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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

  /**
   * Test expected values of null fields, whether initialized or explicitly set.
   */
  @Test
  public void testNullFields()
  {
    // Newly-created account fields should all evaluate to null.
    AccountState accountState = new AccountState();
    assertAllFieldsNull(accountState);

    // Set the public fields to a JsonReference containing null.
    accountState.setAccountDisabled(null);
    accountState.setAccountExpirationTime(null);
    accountState.setPasswordChangedTime(null);
    accountState.setPasswordExpirationWarnedTime(null);
    accountState.setLastLoginTime(null);
    accountState.setMustChangePassword(null);
    accountState.setPasswordChangedByRequiredTime(null);
    accountState.setAuthenticationFailureTimes(null);
    accountState.setGraceLoginTimes(null);
    accountState.clearPasswordHistory();
    accountState.purgeRetiredPassword();
    accountState.setAccountActivationTime(null);

    // The fields should still effectively be null.
    assertAllFieldsNull(accountState);
  }

  private void assertAllFieldsNull(AccountState accountState)
  {
    assertThat(accountState.isAccountDisabled()).isNull();
    assertThat(accountState.getAccountExpirationTime()).isNull();
    assertThat(accountState.getSecondsUntilAccountExpiration()).isNull();
    assertThat(accountState.getPasswordChangedTime()).isNull();
    assertThat(accountState.getPasswordExpirationWarnedTime()).isNull();
    assertThat(accountState.getSecondsUntilPasswordExpiration()).isNull();
    assertThat(accountState.getSecondsUntilPasswordExpirationWarning()).isNull();
    assertThat(accountState.getSecondsUntilAuthenticationFailureUnlock()).isNull();
    assertThat(accountState.getRemainingAuthenticationFailureCount()).isNull();
    assertThat(accountState.getLastLoginTime()).isNull();
    assertThat(accountState.getSecondsUntilIdleLockout()).isNull();
    assertThat(accountState.isMustChangePassword()).isNull();
    assertThat(accountState.getSecondsUntilPasswordResetLockout()).isNull();
    assertThat(accountState.getRemainingGraceLoginCount()).isNull();
    assertThat(accountState.getPasswordChangedByRequiredTime()).isNull();
    assertThat(accountState.getSecondsUntilRequiredChangeTime()).isNull();
    assertThat(accountState.getAuthenticationFailureTimes()).isNull();
    assertThat(accountState.getGraceLoginTimes()).isNull();
    assertThat(accountState.getPasswordHistory()).isNull();
    assertThat(accountState.getRetiredPassword()).isNull();
    assertThat(accountState.getAccountActivationTime()).isNull();
    assertThat(accountState.getSecondsUntilAccountActivation()).isNull();
    assertThat(accountState.getAccountUsabilityNotices()).isNull();
    assertThat(accountState.getAccountUsabilityWarnings()).isNull();
    assertThat(accountState.getAccountUsabilityErrors()).isNull();
  }
}
