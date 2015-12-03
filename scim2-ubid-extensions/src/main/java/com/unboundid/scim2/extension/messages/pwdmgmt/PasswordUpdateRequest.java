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
 * The request for changing a password for a user.
 */
@Schema(description = "Password change request",
    id = "urn:unboundid:scim:api:messages:2.0:PasswordUpdateRequest",
    name = "PasswordUpdateRequest")
public final class PasswordUpdateRequest extends BaseScimResource
{
  private String currentPassword;
  private String newPassword;

  /**
   * Builder for a password update request object.
   */
  public static class PasswordUpdateRequestBuilder
  {
    private String currentPassword;
    private String newPassword;

    /**
     * Sets the current password for the password update request.
     *
     * @param currentPassword  the current password.
     * @return this.
     */
    public PasswordUpdateRequestBuilder setCurrentPassword(
        final String currentPassword)
    {
      this.currentPassword = currentPassword;
      return this;
    }

    /**
     * Sets the new password for the password update request.
     *
     * @param newPassword  the new password.
     * @return this.
     */
    public PasswordUpdateRequestBuilder setNewPassword(
        final String newPassword)
    {
      this.newPassword = newPassword;
      return this;
    }

    /**
     * Call build to build a password reset token request with the parameters
     * that have been supplied to the builder.
     *
     * @return a password reset token built from the parameters supplied
     * to the builder.
     */
    public PasswordUpdateRequest build()
    {
      PasswordUpdateRequest request = new PasswordUpdateRequest();
      request.currentPassword = this.currentPassword;
      request.newPassword = this.newPassword;
      return request;
    }
  }

  // Use the builder instead of constructing directly.
  private PasswordUpdateRequest()
  {

  }

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
   * Gets the new password.
   *
   * @return the new password.
   */
  public String getNewPassword()
  {
    return newPassword;
  }
}
