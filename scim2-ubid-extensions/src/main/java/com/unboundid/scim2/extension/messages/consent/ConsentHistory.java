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

package com.unboundid.scim2.extension.messages.consent;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.Meta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Contains information about a consent history record.
 */
@Schema(description = "Consent History objects.  These represent the " +
    "history of a user giving consent for an application to access " +
    "identity data.",
    id = "urn:unboundid:schemas:broker:2.0:consentHistory",
    name = "ConsentHistory")
public final class ConsentHistory extends BaseScimResource
{
  public static class Builder
  {
    private Application application;
    private List<Scope> scopes;
    private Calendar created;
    private String id;

    /**
     * Sets the scopes for the consent history.
     *
     * @param scopes the scopes for the consent history.
     * @return this.
     */
    public Builder setScopes(final List<Scope> scopes)
    {
      this.scopes = scopes;
      return this;
    }

    /**
     * Sets the application for the consent history.
     *
     * @param application the application for the consent history.
     * @return this.
     */
    public Builder setApplication(final Application application)
    {
      this.application = application;
      return this;
    }

    /**
     * Sets the created attribute of the meta attribute of the consent history.
     *
     * @param created the created attribute of the meta attribute of the
     *                consent history.
     * @return this.
     */
    public Builder setCreated(final Calendar created)
    {
      this.created = created;
      return this;
    }

    /**
     * Sets the id for the consent history.
     *
     * @param id the id for the consent history.
     * @return this.
     */
    public Builder setId(final String id)
    {
      this.id = id;
      return this;
    }

    /**
     * Builds a new consent history from the attributes set in this builder.
     *
     * @return a new consent history object.
     */
    public ConsentHistory build()
    {
      if(created == null)
      {
        created = new GregorianCalendar();
      }
      return new ConsentHistory(this);
    }
  }

  @Attribute(description = "The application for this consent history entry.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final Application application;

  @Attribute(description = "The scopes for this consent history entry.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final List<Scope> scopes;

  // private no-arg constructor for Jackson
  private ConsentHistory()
  {
    this(new Builder());
  }

  private ConsentHistory(final Builder builder)
  {
    this.application = builder.application;
    this.setId(builder.id);
    Meta meta  = new Meta();
    meta.setCreated(builder.created);
    setMeta(meta);

    scopes = Collections.unmodifiableList(
        (builder.scopes == null) ? new ArrayList<Scope>() : builder.scopes);
  }

  /**
   * Gets the application for this consent history entry.
   *
   * @return the application for this consent history entry.
   */
  public Application getApplication()
  {
    return application;
  }

  /**
   * Gets the scopes for this consent history entry.
   *
   * @return the scopes for this consent history entry.
   */
  public List<Scope> getScopes()
  {
    return scopes;
  }
}

