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
public class Consent extends BaseScimResource
{
  public static class Builder
  {
    private Application application;
    private List<Scope> scopes;
    private Calendar lastModified;

    public Builder setScopes(List<Scope> scopes)
    {
      this.scopes = scopes;
      return this;
    }

    public Builder setApplication(Application application)
    {
      this.application = application;
      return this;
    }

    public void setLastModified(Calendar lastModified)
    {
      this.lastModified = lastModified;
    }

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
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final List<Scope> scopes;

  // private no-arg constructor for Jackson
  private Consent()
  {
    this(new Builder());
  }

  private Consent(Builder builder)
  {
    this.application = builder.application;
    Meta meta  = new Meta();
    meta.setLastModified(builder.lastModified);
    setMeta(meta);

    scopes = Collections.unmodifiableList(
        (builder.scopes == null) ? new ArrayList<Scope>() : builder.scopes);
  }

  public Application getApplication()
  {
    return application;
  }

  public List<Scope> getScopes()
  {
    return scopes;
  }
}

