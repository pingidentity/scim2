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

import java.util.List;

@Schema(description = "Consent objects.  These represent a user giving" +
    "consent for an application to access identity data.",
    id = "urn:unboundid:schemas:broker:2.0:consent",
    name = "Consent")
public final class Consent extends BaseScimResource
{
  @Attribute(description = "The application for this consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final Application application;

  @Attribute(description = "The scopes for this consent.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Scope.class)
  private final List<Scope> scopes;

  // private no-arg constructor for Jackson.  Jackson will construct
  // this object and set all fields (even private final ones).
  private Consent()
  {
    this(null, null);
  }

  /**
   * Construct a new Consent object.  This should only be needed by the
   * server.
   *
   * @param application application for this consent.
   * @param scopes scopes for this consent.
   */
  public Consent(final Application application, final List<Scope> scopes)
  {
    this.setMeta(new Meta());
    this.application = application;
    this.scopes = scopes;
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

