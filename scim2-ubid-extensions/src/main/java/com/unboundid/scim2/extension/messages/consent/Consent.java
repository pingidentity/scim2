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

@Schema(description = "Consent objects.  These represent a user giving" +
    "consent for an application to access identity data.",
    id = "urn:unboundid:schemas:broker:2.0:consent",
    name = "Consent")
public final class Consent extends BaseScimResource
{

  public static class Builder
  {
    private Application application;
    private List<Scope> scopes;
    private Calendar lastModified;
    private String id;

    /**
     * Sets the scopes for this consent.
     *
     * @param scopes the scopes for this consent.
     * @return this
     */
    public Builder setScopes(final List<Scope> scopes)
    {
      this.scopes = scopes;
      return this;
    }

    /**
     * Sets the application for this consent.
     *
     * @param application the application for this consent.
     * @return this
     */
    public Builder setApplication(final Application application)
    {
      this.application = application;
      return this;
    }

    /**
     * Sets the lastModified attribute of the meta attribute
     *                     for this consent.
     *
     * @param lastModified the lastModified attribute of the meta attribute
     *                     for this consent.
     * @return this
     */
    public Builder setLastModified(final Calendar lastModified)
    {
      this.lastModified = lastModified;
      return this;
    }

    /**
     * Sets the id of the consent.
     *
     * @param id the id of the consent (this should be set to the
     *           application name).
     */
    public void setId(final String id)
    {
      this.id = id;
    }

    /**
     * Builds a new Consent object.
     *
     * @return a new consent object created from the attributes set on this
     * builder.
     */
    public Consent build()
    {
      if(lastModified == null)
      {
        lastModified = new GregorianCalendar();
      }
      return new Consent(this);
    }
  }

  @Attribute(description = "The application for this consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final Application application;

  @Attribute(description = "The scopes for this consent.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Scope.class)
  private final List<Scope> scopes;

  // private no-arg constructor for Jackson
  private Consent()
  {
    this(new Builder());
  }

  private Consent(final Builder builder)
  {
    this.application = builder.application;
    Meta meta  = new Meta();
    meta.setLastModified(builder.lastModified);
    setMeta(meta);
    setId(builder.id);

    scopes = Collections.unmodifiableList(
        (builder.scopes == null) ? new ArrayList<Scope>() : builder.scopes);
  }

  /**
   * Gets the application for this consent.
   *
   * @return the application for this consent.
   */
  public Application getApplication()
  {
    return application;
  }

  /**
   * Gets the scopes for this consent.
   *
   * @return the scopes for this consent.
   */
  public List<Scope> getScopes()
  {
    return scopes;
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

    if (!super.equals(o))
    {
      return false;
    }

    Consent consent = (Consent) o;

    if (application != null ? !application.equals(consent.application) :
        consent.application != null)
    {
      return false;
    }

    return !(scopes != null ? !scopes.equals(consent.scopes) :
        consent.scopes != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (application != null ? application.hashCode() : 0);
    result = 31 * result + (scopes != null ? scopes.hashCode() : 0);
    return result;
  }

}

