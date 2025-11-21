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

package com.unboundid.scim2.extension.messages.consent;

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.util.Objects;

/**
 * Contains information about scopes.
 */
public final class Scope
{
  /**
   * Consent string constant used to show that the user has granted consent.
   */
  @NotNull
  public static final String CONSENT_GRANTED = "granted";

  /**
   * Consent string constant used to show that the user has denied consent.
   */
  @NotNull
  public static final String CONSENT_DENIED = "denied";

  /**
   * Consent string constant used to show that the consent was revoked.
   */
  @NotNull
  public static final String CONSENT_REVOKED = "revoked";

  /**
   * Builder for creating a Scope.
   */
  public static class Builder
  {
    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    private String consentPromptText;

    @Nullable
    private String consent;

    /**
     * Sets the name of this scope.
     *
     * @param name the name of this scope.
     * @return this.
     */
    @NotNull
    public Builder setName(@Nullable final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the description of this scope.
     *
     * @param description a description of this scope that is intended
     *                    for end-users.
     * @return this.
     */
    @NotNull
    public Builder setDescription(@Nullable final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets the consent prompt text of this scope.
     *
     * @param consentPromptText the consent prompt text for this scope.
     * @return this.
     */
    @NotNull
    public Builder setConsentPromptText(
        @Nullable final String consentPromptText)
    {
      this.consentPromptText = consentPromptText;
      return this;
    }

    /**
     * Sets the consent action for this scope.
     *
     * @param consent the consent action for this scope.
     * @return this.
     */
    @NotNull
    public Builder setConsent(@Nullable final String consent)
    {
      this.consent = consent;
      return this;
    }

    /**
     * Builds a new Scope object from the attributes of this builder.
     *
     * @return a new Scope object.
     */
    @NotNull
    public Scope build()
    {
      return new Scope(this);
    }
  }

  @Nullable
  @Attribute(description = "The name of the scope.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String name;

  @Nullable
  @Attribute(description = "The description of the scope.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String description;

  @Nullable
  @Attribute(description = "The consent prompt text of the scope.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String consentPromptText;

  @Nullable
  @Attribute(description = "Consent (Granted, Denied, Revoked).",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String consent;

  // private no-arg constructor for Jackson
  private Scope()
  {
    this(new Builder());
  }

  private Scope(@NotNull final Builder builder)
  {
    this.name = builder.name;
    this.description = builder.description;
    this.consent = builder.consent;
    this.consentPromptText = builder.consentPromptText;
  }

  /**
   * Returns the name of this scope.
   *
   * @return the name of this scope.
   */
  @Nullable
  public String getName()
  {
    return name;
  }

  /**
   * Returns the description of this scope.
   *
   * @return the description of this scope.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the consent action for this scope.
   *
   * @return the consent action for this scope.
   */
  @Nullable
  public String getConsent()
  {
    return consent;
  }

  /**
   * Returns the consent prompt text for this scope.
   *
   * @return the consent prompt text for this scope.
   */
  @Nullable
  public String getConsentPromptText()
  {
    return consentPromptText;
  }

  /**
   * Indicates whether the provided object is equal to this scope object.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this scope, or
   *            {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    Scope scope = (Scope) o;
    if (!Objects.equals(name, scope.name))
    {
      return false;
    }
    if (!Objects.equals(description, scope.description))
    {
      return false;
    }
    return Objects.equals(consent, scope.consent);
  }

  /**
   * Retrieves a hash code for this scope.
   *
   * @return  A hash code for this scope.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(name, description, consent);
  }
}
