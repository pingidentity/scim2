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


import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;

/**
 * The request for changing a password for a user.
 */
@Schema(description = "Password change request",
    id = "urn:pingidentity:scim:api:messages:2.0:PasswordUpdateRequest",
    name = "PasswordUpdateRequest")
public final class PasswordUpdateRequest extends BaseScimResource
{
  @Nullable
  @Attribute(description = "The current password.",
      mutability = AttributeDefinition.Mutability.WRITE_ONLY)
  private String currentPassword;

  @Nullable
  @Attribute(description = "The new password.",
      mutability = AttributeDefinition.Mutability.WRITE_ONLY)
  private String newPassword;

  @Nullable
  @Attribute(
      description = "Contains a generated password" +
          " if a password was generated.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String generatedPassword;

  /**
   * Builder for a password update request object.
   */
  public static class PasswordUpdateRequestBuilder
  {
    @Nullable
    private String currentPassword;

    @Nullable
    private String newPassword;

    @Nullable
    private String generatedPassword;

    /**
     * Sets the current password for the password update request.
     *
     * @param currentPassword  the current password.
     * @return this.
     */
    @NotNull
    public PasswordUpdateRequestBuilder setCurrentPassword(
        @Nullable final String currentPassword)
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
    @NotNull
    public PasswordUpdateRequestBuilder setNewPassword(
        @Nullable final String newPassword)
    {
      this.newPassword = newPassword;
      return this;
    }

    /**
     * Sets the server generated password.
     *
     * @param generatedPassword the generated password.
     * @return this object.
     */
    @NotNull
    public PasswordUpdateRequestBuilder setGeneratedPassword(
        @Nullable final String generatedPassword)
    {
      this.generatedPassword = generatedPassword;
      return this;
    }

    /**
     * Call build to build a password reset token request with the parameters
     * that have been supplied to the builder.
     *
     * @return a password reset token built from the parameters supplied
     * to the builder.
     */
    @NotNull
    public PasswordUpdateRequest build()
    {
      PasswordUpdateRequest request = new PasswordUpdateRequest();
      request.currentPassword = this.currentPassword;
      request.newPassword = this.newPassword;
      request.generatedPassword = this.generatedPassword;
      return request;
    }
  }

  // Use the builder instead of constructing directly.
  private PasswordUpdateRequest()
  {

  }

  /**
   * Create a new PasswordUpdateRequest.
   *
   * @param currentPassword  the current password.
   * @param newPassword  the new password.
   */
  public PasswordUpdateRequest(@Nullable final String currentPassword,
                               @Nullable final String newPassword)
  {
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  /**
   * Sets the current password for the password update request.
   *
   * @param currentPassword  the current password.
   * @return this object.
   */
  @NotNull
  public PasswordUpdateRequest setCurrentPassword(
      @Nullable final String currentPassword)
  {
    this.currentPassword = currentPassword;
    return this;
  }

  /**
   * Sets the new password for the password update request.
   *
   * @param newPassword  the new password.
   * @return this object.
   */
  @NotNull
  public PasswordUpdateRequest setNewPassword(
      @Nullable final String newPassword)
  {
    this.newPassword = newPassword;
    return this;
  }

  /**
   * Gets the current password.
   *
   * @return the current password.
   */
  @Nullable
  public String getCurrentPassword()
  {
    return currentPassword;
  }

  /**
   * Gets the new password.
   *
   * @return the new password.
   */
  @Nullable
  public String getNewPassword()
  {
    return newPassword;
  }

  /**
   * Gets the server generated password.
   *
   * @return the generated password.
   */
  @Nullable
  public String getGeneratedPassword()
  {
    return generatedPassword;
  }
}
