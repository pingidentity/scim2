/*
 * Copyright 2015-2020 Ping Identity Corporation
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

@Test
public class PasswordQualityRequirementResponseTest
{
  /**
   * Tests serialization of Provider objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String pqrString = "{  \n" +
        "   \"currentPasswordRequired\":true," + "\n" +
        "   \"mustChangePassword\":true," + "\n" +
        "   \"secondsUntilPasswordExpiration\":12," + "\n" +
        "   \"passwordRequirements\":" + "\n" +
        "   [" + "\n" +
        "     {" + "\n" +
        "       \"type\":\"Type One\"," + "\n" +
        "       \"requirementSatisfied\":true," + "\n" +
        "       \"description\":\"Description One\"," + "\n" +
        "       \"additionalInfo\":\"Additional Info One\"" + "\n" +
        "     }," + "\n" +
        "     {" + "\n" +
        "       \"type\":\"Type Two\"," + "\n" +
        "       \"requirementSatisfied\":true," + "\n" +
        "       \"description\":\"Description Two\"," + "\n" +
        "       \"additionalInfo\":\"Additional Info Two\"" + "\n" +
        "     }" + "\n" +
        "   ]" + "\n" +
        "}";

    PasswordQualityRequirementResponse pqrResponse =
        JsonUtils.getObjectReader().forType(
            PasswordQualityRequirementResponse.class).readValue(pqrString);
    String serializedPqrResponse = JsonUtils.getObjectWriter().
        writeValueAsString(pqrResponse);
    PasswordQualityRequirementResponse deserializedPqrResponse =
        JsonUtils.getObjectReader().forType(
            PasswordQualityRequirementResponse.class).
            readValue(serializedPqrResponse);
    Assert.assertEquals(pqrResponse, deserializedPqrResponse);
  }
}
