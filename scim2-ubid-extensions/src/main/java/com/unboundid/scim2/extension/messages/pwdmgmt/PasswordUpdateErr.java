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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the errors that occurred while updating a password.
 */
@Schema(id="urn:pingidentity:scim:api:messages:2.0:PasswordUpdateError",
    description = "This class represents the errors that occurred while " +
        "updating a password.",
    name = "PasswordUpdateError")
public class PasswordUpdateErr
{
  @NotNull
  @Attribute(
      description = "A list of errors that occurred while attempting"
          + " to change the password.",
      multiValueClass = PasswordRequirementResult.class)
  private List<PasswordRequirementResult> passwordRequirements =
      new ArrayList<PasswordRequirementResult>();


  /**
   * Gets the password requirements returned from the attempted password change.
   *
   * @return the password requirements returned from the attempted
   * password change.
   */
  @NotNull
  public List<PasswordRequirementResult> getPasswordRequirements()
  {
    return passwordRequirements;
  }

  /**
   * Sets the password requirements returned from the attempted password change.
   *
   * @param passwordRequirementResults the password requirements returned from
   *                             the attempted password change.
   */
  public void setPasswordRequirements(
      @NotNull final List<PasswordRequirementResult> passwordRequirementResults)
  {
    this.passwordRequirements = passwordRequirementResults;
  }
}
