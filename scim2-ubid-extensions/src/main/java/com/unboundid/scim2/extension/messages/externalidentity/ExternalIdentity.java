/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

package com.unboundid.scim2.extension.messages.externalidentity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.util.Objects;

/**
 * External identity provider information.
 */
@Schema(description = "Contains information about external identities.",
    id = "urn:pingidentity:scim:api:messages:2.0:ExternalIdentity",
    name = "ExternalIdentity")
public final class ExternalIdentity extends BaseScimResource
{
  @Nullable
  @Attribute(description = "The external IDP.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      isRequired = true)
  private final Provider provider;

  @Nullable
  @Attribute(description = "The user ID at the provider. If not available, " +
      "the user is not linked to any external identities at the provider.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String providerUserId;

  @Nullable
  @Attribute(description = "The access token issued by the provider that " +
      "may be used to retrieve additional data.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String accessToken;

  @Nullable
  @Attribute(description = "The refresh token issued by the provider that " +
      "may be used to retrieve a new access token.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String refreshToken;

  @Nullable
  @Attribute(description = "The provider redirect url can be used to obtain " +
      "an auth code for this external identity provider.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String providerRedirectUrl;

  @Nullable
  @Attribute(description = "The OAuth2 callback url",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String callbackUrl;

  @Nullable
  @Attribute(description = "The parameters received during an OAuth2 callback",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private ObjectNode callbackParameters;

  // private no-arg constructor for Jackson.  Jackson will construct
  // this object and set all fields (even private final ones).
  private ExternalIdentity()
  {
    this(null);
  }

  /**
   * Creates an external identity link request from a callback uri.
   * Use this when the request is to create a link interactively.  This
   * request will return information that can be used to authenticate the
   * user, and then an additional replace request is needed to complete the
   * link.
   *
   * @param providerName the name of the external identity provider.
   * @param callbackUrl the callback uri that is used for the external
   *                    identity provider.
   */
  public ExternalIdentity(@Nullable final String providerName,
                          @Nullable final String callbackUrl)
  {
    this.provider = new Provider.Builder().setName(providerName).build();
    this.providerUserId = null;
    this.accessToken = null;
    this.refreshToken = null;
    this.providerRedirectUrl = null;
    this.callbackUrl = callbackUrl;
  }

  /**
   * Constructs a new ExternalIdentity object.  This should only be needed
   * by the server.
   *
   * @param provider the provider
   */
  public ExternalIdentity(@Nullable final Provider provider)
  {
    this.provider = provider;
    this.providerUserId = null;
    this.accessToken = null;
    this.refreshToken = null;
    this.providerRedirectUrl = null;
    this.callbackUrl = null;
  }

  /**
   * Constructs a new ExternalIdentity object.  This should only be needed
   * by the server.
   *
   * @param provider the provider
   * @param providerRedirectUrl the provider redirect url from the external
   *                            identity.
   */
  public ExternalIdentity(@Nullable final Provider provider,
                          @Nullable final String providerRedirectUrl)
  {
    this.provider = provider;
    this.providerUserId = null;
    this.accessToken = null;
    this.refreshToken = null;
    this.providerRedirectUrl = providerRedirectUrl;
    this.callbackUrl = null;
  }

  /**
   * Constructs a new ExternalIdentity object.  This should only be needed
   * by the server.
   *
   * @param provider the provider
   * @param providerUserId the provider user id.
   * @param accessToken the access token.
   * @param refreshToken the refresh token (optional.  May be null.).
   */
  public ExternalIdentity(@Nullable final Provider provider,
                          @Nullable final String providerUserId,
                          @Nullable final String accessToken,
                          @Nullable final String refreshToken)
  {
    this.provider = provider;
    this.providerUserId = providerUserId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.providerRedirectUrl = null;
    this.callbackUrl = null;
  }

  /**
   * Gets the external identity provider.
   *
   * @return the external identity provider.
   */
  @Nullable
  public Provider getProvider()
  {
    return provider;
  }

  /**
   * Gets the user id for the external identity provider.
   *
   * @return the user id for the external identity provider.
   */
  @Nullable
  public String getProviderUserId()
  {
    return providerUserId;
  }

  /**
   * Gets the access token for the external identity provider.
   *
   * @return the access token for the external identity provider.
   */
  @Nullable
  public String getAccessToken()
  {
    return accessToken;
  }

  /**
   * Gets the refresh token for the external identity provider.
   *
   * @return the refresh token for the external identity provider.
   */
  @Nullable
  public String getRefreshToken()
  {
    return refreshToken;
  }

  /**
   * Gets the provider redirect url from the external identity.  The provider
   * redirect url can be used to obtain an auth code for this external
   * identity provider.
   *
   * @return the provider redirect url.
   */
  @Nullable
  public String getProviderRedirectUrl()
  {
    return providerRedirectUrl;
  }

  /**
   * Gets the callback uri.
   *
   * @return the callback uri.
   */
  @Nullable
  public String getcallbackUrl()
  {
    return callbackUrl;
  }

  /**
   * Gets the callback parameters.
   *
   * @return the callback parameters.
   */
  @Nullable
  public ObjectNode getCallbackParameters()
  {
    return callbackParameters;
  }

  /**
   * Sets the callback parameters.
   *
   * @param callbackParameters the callback parameters.
   */
  public void setCallbackParameters(
      @Nullable final ObjectNode callbackParameters)
  {
    this.callbackParameters = callbackParameters;
  }

  /**
   * Indicates whether the provided object is equal to this external identity
   * provider.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this external
   *            identity provider, or {@code false} if not.
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

    ExternalIdentity that = (ExternalIdentity) o;
    if (!Objects.equals(provider, that.provider))
    {
      return false;
    }
    if (!Objects.equals(providerUserId, that.providerUserId))
    {
      return false;
    }
    if (!Objects.equals(accessToken, that.accessToken))
    {
      return false;
    }
    if (!Objects.equals(refreshToken, that.refreshToken))
    {
      return false;
    }
    if (!Objects.equals(providerRedirectUrl, that.providerRedirectUrl))
    {
      return false;
    }
    if (!Objects.equals(callbackUrl, that.callbackUrl))
    {
      return false;
    }
    return Objects.equals(callbackParameters, that.callbackParameters);
  }

  /**
   * Retrieves a hash code for this external identity provider.
   *
   * @return  A hash code for this external identity provider.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(provider, providerUserId, accessToken, refreshToken,
        providerRedirectUrl, callbackUrl, callbackParameters);
  }
}
