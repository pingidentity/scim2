/*
 * Copyright 2015-2019 Ping Identity Corporation
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
import com.unboundid.scim2.common.types.AttributeDefinition;

/**
 * Contains information about scopes.
 */
public final class Scope
{
  /**
   * Consent string constant used to show that the user has granted consent.
   */
  public static final String CONSENT_GRANTED = "granted";

  /**
   * Consent string constant used to show that the user has denied consent.
   */
  public static final String CONSENT_DENIED = "denied";

  /**
   * Consent string constant used to show that the consent was revoked.
   */
  public static final String CONSENT_REVOKED = "revoked";

  public static class Builder
  {
    private String name;
    private String description;
    private String consentPromptText;
    private String consent;

    /**
     * Sets the name of this scope.
     *
     * @param name the name of this scope.
     * @return this.
     */
    public Builder setName(final String name)
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
    public Builder setDescription(final String description)
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
    public Builder setConsentPromptText(final String consentPromptText)
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
    public Builder setConsent(final String consent)
    {
      this.consent = consent;
      return this;
    }

    /**
     * Builds a new Scope object from the attributes of this builder.
     *
     * @return a new Scope object.
     */
    public Scope build()
    {
      return new Scope(this);
    }
  }

  @Attribute(description = "The name of the scope.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String name;

  @Attribute(description = "The description of the scope.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String description;

  @Attribute(description = "The consent prompt text of the scope.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String consentPromptText;

  @Attribute(description = "Consent (Granted, Denied, Revoked).",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String consent;

  // private no-arg constructor for Jackson
  private Scope()
  {
    this(new Builder());
  }

  private Scope(final Builder builder)
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
  public String getName()
  {
    return name;
  }

  /**
   * Returns the description of this scope.
   *
   * @return the description of this scope.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the consent action for this scope.
   *
   * @return the consent action for this scope.
   */
  public String getConsent()
  {
    return consent;
  }

  /**
   * Returns the consent prompt text for this scope.
   *
   * @return the consent prompt text for this scope.
   */
  public String getConsentPromptText()
  {
    return consentPromptText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
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

    if (name != null ? !name.equals(scope.name) :
        scope.name != null)
    {
      return false;
    }

    if (description != null ? !description.equals(scope.description) :
        scope.description != null)
    {
      return false;
    }

    return !(consent != null ? !consent.equals(scope.consent) :
        scope.consent != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (consent != null ? consent.hashCode() : 0);
    return result;
  }

}
