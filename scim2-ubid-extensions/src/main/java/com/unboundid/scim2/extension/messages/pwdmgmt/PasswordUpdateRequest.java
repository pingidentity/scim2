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


import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;

/**
 * The request for validating a password for a user.
 */
@Schema(description = "Password change request",
    id = "urn:unboundid:scim:api:messages:2.0:PasswordUpdateRequest",
    name = "PasswordUpdateRequest")
public class PasswordUpdateRequest extends BaseScimResource
{
  private String currentPassword;
  private String newPassword;

  /**
   * Gets the current password.
   *
   * @return the current password.
   */
  public String getCurrentPassword()
  {
    return currentPassword;
  }

  /**
   * Sets the current password.
   *
   * @param currentPassword the current password.
   */
  public void setCurrentPassword(final String currentPassword)
  {
    this.currentPassword = currentPassword;
  }

  /**
   * Gets the new password.
   *
   * @return the new password.
   */
  public String getNewPassword()
  {
    return newPassword;
  }

  /**
   * Sets the new password.
   *
   * @param newPassword the new password.
   */
  public void setNewPassword(final String newPassword)
  {
    this.newPassword = newPassword;
  }

}
