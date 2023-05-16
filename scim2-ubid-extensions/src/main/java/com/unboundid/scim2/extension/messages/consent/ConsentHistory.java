/*
 * Copyright 2015-2023 Ping Identity Corporation
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

/**
 * Contains information about a consent history record.
 */
@Schema(description = "Consent History objects.  These represent the " +
    "history of a user giving consent for a client to access " +
    "identity data.",
    id = "urn:pingidentity:scim:api:messages:2.0:consentHistory",
    name = "ConsentHistory")
public final class ConsentHistory extends BaseScimResource
{

  @Attribute(description = "The client for this consent history entry.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final OAuth2Client client;

  @Attribute(description = "The scopes for this consent history entry.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Scope.class)
  private final List<Scope> scopes;

  // private no-arg constructor for Jackson
  private ConsentHistory()
  {
    this(null, null);
  }

  /**
   * Constructs a new ConsentHistory.  This should only be needed
   * by the server.
   *
   * @param client the client for this consent history.
   * @param scopes the scopes for this consent history.
   */
  public ConsentHistory(
      final OAuth2Client client, final List<Scope> scopes)
  {
    this.setMeta(new Meta());
    this.client = client;
    this.scopes = scopes;
  }

  /**
   * Gets the client for this consent history entry.
   *
   * @return the client for this consent history entry.
   */
  public OAuth2Client getClient()
  {
    return client;
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

    ConsentHistory that = (ConsentHistory) o;

    if (client != null ?
        !client.equals(that.client) : that.client != null)
    {
      return false;
    }
    return !(scopes != null ?
        !scopes.equals(that.scopes) : that.scopes != null);
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (client != null ? client.hashCode() : 0);
    result = 31 * result + (scopes != null ? scopes.hashCode() : 0);
    return result;
  }
}

