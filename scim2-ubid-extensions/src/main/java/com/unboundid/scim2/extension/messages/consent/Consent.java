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

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.Meta;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a consent object provided by a user.
 */
@Schema(description = "Consent objects.  These represent a user giving" +
    " consent for a client to access identity data.",
    id = "urn:pingidentity:scim:api:messages:2.0:consent",
    name = "Consent")
public final class Consent extends BaseScimResource
{
  @Nullable
  @Attribute(description = "The client for this consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final OAuth2Client client;

  @Nullable
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
   * @param client client for this consent.
   * @param scopes scopes for this consent.
   */
  public Consent(@Nullable final OAuth2Client client,
                 @Nullable final List<Scope> scopes)
  {
    this.setMeta(new Meta());
    this.client = client;
    this.scopes = scopes;
  }

  /**
   * Gets the client for this consent.
   *
   * @return the client for this consent.
   */
  @Nullable
  public OAuth2Client getClient()
  {
    return client;
  }

  /**
   * Gets the scopes for this consent.
   *
   * @return the scopes for this consent.
   */
  @Nullable
  public List<Scope> getScopes()
  {
    return scopes;
  }

  /**
   * Indicates whether the provided object is equal to this consent object.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this consent
   *            object, or {@code false} if not.
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

    if (!super.equals(o))
    {
      return false;
    }

    Consent consent = (Consent) o;
    if (!Objects.equals(client, consent.client))
    {
      return false;
    }
    return Objects.equals(scopes, consent.scopes);
  }

  /**
   * Retrieves a hash code for this consent.
   *
   * @return  A hash code for this consent.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), client, scopes);
  }
}

