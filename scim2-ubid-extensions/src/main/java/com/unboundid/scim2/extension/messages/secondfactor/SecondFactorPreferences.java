/*
 * Copyright 2016 UnboundID Corp.
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

package com.unboundid.scim2.extension.messages.secondfactor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.util.List;

/**
 * Object used to manage second factor authentication preferences.
 */
@Schema(id="urn:unboundid:schemas:2.0:SecondFactorPreferences",
    name="SecondFactorPreferences",
    description = "Management of second factor authentication preferences")
public class SecondFactorPreferences extends BaseScimResource
{
  @Attribute(description = "Whether second factor authentication is enabled.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private boolean enabled;

  @Attribute(description = "The authenticator's specific preferences",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AuthenticatorPreferences.class)
  private final List<AuthenticatorPreferences> authenticators;

  /**
   * Creates a new SecondFactorPreferences.
   *
   * @param enabled Whether the second factor authentication is enabled.
   * @param authenticators The authenticator preferences.
   */
  @JsonCreator
  public SecondFactorPreferences(
      @JsonProperty(value = "enabled") final boolean enabled,
      @JsonProperty(value = "authenticators")
      final List<AuthenticatorPreferences> authenticators)
  {
    this.enabled = enabled;
    this.authenticators = authenticators;
  }

  /**
   * Creates a new SecondFactorPreferences.
   *
   * @param enabled Whether the second factor authentication is enabled.
   */
  public SecondFactorPreferences(final boolean enabled)
  {
    this.enabled = enabled;
    this.authenticators = null;
  }

  /**
   * Retrieves whether second factor authentication is enabled.
   *
   * @return {@code true} if second factor authentication is enabled or
   *         {@code false} otherwise.
   */
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * Sets whether second factor authentication is enabled.
   *
   * @param enabled Whether second factor is enabled.
   * @return This object.
   */
  public SecondFactorPreferences setEnabled(final boolean enabled)
  {
    this.enabled = enabled;
    return this;
  }

  /**
   * Retrieves the list of authenticators and their preferences.
   *
   * @return The list of authenticators and their preferences.
   */
  public List<AuthenticatorPreferences> getAuthenticators()
  {
    return authenticators;
  }
}
