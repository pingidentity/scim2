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
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will contain information about password quality
 * requirements.
 */
@Schema(id="urn:pingidentity:schemas:2.0:PasswordQualityRequirement",
    description = "The password quality requirements for a particular user.",
    name = "PasswordQualityRequirement")
public class PasswordQualityRequirementResponse extends BaseScimResource
{
  @Nullable
  @Attribute(description =
      "True if the user's password is required for password updates.")
  private Boolean currentPasswordRequired;

  @Nullable
  @Attribute(description = "True if the user's password must be changed.")
  private Boolean mustChangePassword;

  @Nullable
  @Attribute(
      description = "The number of seconds before the password will expire.")
  private Integer secondsUntilPasswordExpiration;

  @Nullable
  @Attribute(
      description = "A list of password requirements that must be met.",
      multiValueClass = PasswordRequirementResult.class)
  private final List<PasswordRequirementResult> passwordRequirements =
      new ArrayList<>();

  /**
   * Returns a boolean that is true if the current password is required,
   * or false if not.
   *
   * @return true if the current password is required, or false
   * if not.
   */
  @Nullable
  public Boolean isCurrentPasswordRequired()
  {
    return currentPasswordRequired;
  }

  /**
   * Sets a boolean that is true if the current password is required,
   * or false if not.
   *
   * @param currentPasswordRequired a boolean that is true if the
   * current password is required, or false if not.
   */
  public void setCurrentPasswordRequired(
      @Nullable final Boolean currentPasswordRequired)
  {
    this.currentPasswordRequired = currentPasswordRequired;
  }

  /**
   * Returns a boolean that is true if the user's password must change.
   *
   * @return true if the user's password must change, or false if not.
   */
  @Nullable
  public Boolean isMustChangePassword()
  {
    return mustChangePassword;
  }

  /**
   * Sets a boolean that is true if the user's password must change.
   *
   * @param mustChangePassword a boolean value that is true if the user's
   * password must change, or false if not.
   */
  public void setMustChangePassword(@Nullable final Boolean mustChangePassword)
  {
    this.mustChangePassword = mustChangePassword;
  }

  /**
   * Gets the number of seconds until the user's password expires.
   *
   * @return the number of seconds until the user's password expires.
   */
  @Nullable
  public Integer getSecondsUntilPasswordExpiration()
  {
    return secondsUntilPasswordExpiration;
  }

  /**
   * Sets the number of seconds until the user's password expires.
   *
   * @param secondsUntilPasswordExpiration the number of seconds until
   * the user's password expires.
   */
  public void setSecondsUntilPasswordExpiration(
      @Nullable final Integer secondsUntilPasswordExpiration)
  {
    this.secondsUntilPasswordExpiration = secondsUntilPasswordExpiration;
  }

  /**
   * Gets the list of password requirements.
   *
   * @return the list of password requirements.
   */
  @Nullable
  public List<PasswordRequirementResult> getPasswordRequirements()
  {
    return passwordRequirements;
  }
}
