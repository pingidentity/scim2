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
        "{  \n" +
        "  \"accountDisabled\":false," + "\n" +
        "  \"accountExpirationTime\":\"2015-07-06T04:03:02Z\"," + "\n" +
        "  \"secondsUntilAccountExpiration\":10," + "\n" +
        "  \"passwordChangedTime\":\"2015-07-06T04:04:02Z\"," + "\n" +
        "  \"passwordExpirationWarnedTime\":\"2015-07-06T04:05:02Z\"," + "\n" +
        "  \"secondsUntilPasswordExpiration\":11," + "\n" +
        "  \"secondsUntilPasswordExpirationWarning\":12," + "\n" +
        "  \"authenticationFailureTimes\":[\"2015-07-06T04:10:02Z\", " +
            "\"2015-07-06T04:11:02Z\",\"2015-07-06T04:12:02Z\"]," + "\n" +
        "  \"secondsUntilAuthenticationFailureUnlock\":13," + "\n" +
        "  \"remainingAuthenticationFailureCount\":14," + "\n" +
        "  \"lastLoginTime\":\"2015-07-06T04:06:02Z\"," + "\n" +
        "  \"secondsUntilIdleLockout\":15," + "\n" +
        "  \"mustChangePassword\":true," + "\n" +
        "  \"graceLoginTimes\":[\"2015-07-06T04:20:02Z\", " +
            "\"2015-07-06T04:21:02Z\",\"2015-07-06T04:22:02Z\"]," + "\n" +
        "  \"remainingGraceLoginCount\":17," + "\n" +
        "  \"passwordChangedByRequiredTime\":\"2015-07-06T04:07:02Z\"," + "\n" +
        "  \"passwordHistory\":[\"pw_one\",\"pw_two\",\"pw_three\"]," + "\n" +
        "  \"retiredPassword\":" + "\n" +
        "    {" + "\n" +
        "      \"passwordRetiredTime\":\"2015-07-06T04:20:02Z\"," + "\n" +
        "      \"passwordExpirationTime\":\"2015-07-06T04:20:02Z\"" + "\n" +
        "    }," + "\n" +
        "  \"accountActivationTime\":\"2015-07-06T04:07:02Z\"," + "\n" +
        "  \"secondsUntilAccountActivation\":18," + "\n" +
        "  \"lastLoginIpAddress\":\"lastLoginIp\"" + "\n" +
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
