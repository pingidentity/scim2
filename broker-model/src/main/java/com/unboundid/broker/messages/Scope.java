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

package com.unboundid.broker.messages;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.exceptions.ServerErrorException;
import com.unboundid.scim2.common.types.AttributeDefinition;

/**
 * Contains information about scopes.
 */
@Schema(description = "Scope of identity data.",
    id = "urn:unboundid:schemas:broker:2.0:scope",
    name = "Scope")
public final class Scope extends BaseScimResource
{
  public enum ConsentAction
  {
    /**
     * Consent has been granted by the user.
     */
    Granted(1),

    /**
     * Consent has been denied by the user.
     */
    Denied(0),

    // TODO DAN:  check this value is correct
    /**
     * Consent has been revoked.
     */
    Revoked(-1);

    private final Integer id;

    ConsentAction(final Integer id)
    {
      this.id = id;
    }

    /**
     * Returns the id (numeric value) of this consent action.
     *
     * @return the id (numeric value) of this consent action.
     */
    public Integer getId()
    {
      return id;
    }

    /**
     * Gets the enum value for the specified id (numeric value).
     *
     * @param id the numeric value of the consent action.
     * @return the consent action corresponding to the id provided.
     * @throws ServerErrorException if the id provided does not exist.
     */
    public static ConsentAction getForInteger(final Integer id)
        throws ServerErrorException
    {
      for(ConsentAction consentAction : ConsentAction.values())
      {
        if(consentAction.getId() == id)
        {
          return consentAction;
        }
      }

      throw new ServerErrorException(
          "Unknown consent action id found: '" + id + "'");
    }
  }

  public static class Builder
  {
    private String name;
    private String description;
    private ConsentAction consent;

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
     * @param description the description of this scope.
     * @return this.
     */
    public Builder setDescription(final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets the consent action for this scope.
     *
     * @param consent the consent action for this scope.
     * @return this.
     */
    public Builder setConsent(final ConsentAction consent)
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
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String name;

  @Attribute(description = "The description of the scope.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String description;

  // TODO: should change this to an enum ... at least in the builder
  @Attribute(description = "Consent (Granted, Denied, Revoked).",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final ConsentAction consent;

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
  public ConsentAction getConsent()
  {
    return consent;
  }
}
