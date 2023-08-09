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

package com.unboundid.scim2.extension.messages.externalidentity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;

/**
 * External identity provider information.
 */
@Schema(description = "Contains information about external identities.",
    id = "urn:pingidentity:scim:api:messages:2.0:ExternalIdentity",
    name = "ExternalIdentity")
public final class ExternalIdentity extends BaseScimResource
{
  @Attribute(description = "The external IDP.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      isRequired = true)
  private final Provider provider;

  @Attribute(description = "The user ID at the provider. If not available, " +
      "the user is not linked to any external identities at the provider.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String providerUserId;

  @Attribute(description = "The access token issued by the provider that " +
      "may be used to retrieve additional data.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String accessToken;

  @Attribute(description = "The refresh token issued by the provider that " +
      "may be used to retrieve a new access token.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String refreshToken;

  @Attribute(description = "The provider redirect url can be used to obtain " +
      "an auth code for this external identity provider.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String providerRedirectUrl;

  @Attribute(description = "The OAuth2 callback url",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String callbackUrl;

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
  public ExternalIdentity(final String providerName,
                          final String callbackUrl)
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
  public ExternalIdentity(final Provider provider)
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
  public ExternalIdentity(final Provider provider,
                          final String providerRedirectUrl)
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
  public ExternalIdentity(final Provider provider, final String providerUserId,
      final String accessToken, final String refreshToken)
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
   * @return the external idenity provider.
   */
  public Provider getProvider()
  {
    return provider;
  }

  /**
   * Gets the user id for the external identity provider.
   *
   * @return the user id for the external identity provider.
   */
  public String getProviderUserId()
  {
    return providerUserId;
  }

  /**
   * Gets the access token for the external identity provider.
   *
   * @return the access token for the external identity provider.
   */
  public String getAccessToken()
  {
    return accessToken;
  }

  /**
   * Gets the refresh token for the external identity provider.
   *
   * @return the refresh token for the external identity provider.
   */
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
  public String getProviderRedirectUrl()
  {
    return providerRedirectUrl;
  }

  /**
   * Gets the callback uri.
   *
   * @return the callback uri.
   */
  public String getcallbackUrl()
  {
    return callbackUrl;
  }

  /**
   * Gets the callback parameters.
   *
   * @return the callback parameters.
   */
  public ObjectNode getCallbackParameters()
  {
    return callbackParameters;
  }

  /**
   * Sets the callback parameters.
   *
   * @param callbackParameters the callback parameters.
   */
  public void setCallbackParameters(final ObjectNode callbackParameters)
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

    ExternalIdentity that = (ExternalIdentity) o;

    if (provider != null ? !provider.equals(that.provider) :
        that.provider != null)
    {
      return false;
    }
    if (providerUserId != null ? !providerUserId.equals(that.providerUserId) :
        that.providerUserId != null)
    {
      return false;
    }
    if (accessToken != null ? !accessToken.equals(that.accessToken) :
        that.accessToken != null)
    {
      return false;
    }
    if (refreshToken != null ? !refreshToken.equals(that.refreshToken) :
        that.refreshToken != null)
    {
      return false;
    }
    if (providerRedirectUrl != null ?
        !providerRedirectUrl.equals(that.providerRedirectUrl) :
        that.providerRedirectUrl != null)
    {
      return false;
    }
    if (callbackUrl != null ? !callbackUrl.equals(that.callbackUrl) :
        that.callbackUrl != null)
    {
      return false;
    }
    return callbackParameters != null ?
        callbackParameters.equals(that.callbackParameters) :
        that.callbackParameters == null;

  }

  /**
   * Retrieves a hash code for this external identity provider.
   *
   * @return  A hash code for this external identity provider.
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (provider != null ? provider.hashCode() : 0);
    result = 31 * result +
        (providerUserId != null ? providerUserId.hashCode() : 0);
    result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
    result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
    result = 31 * result +
        (providerRedirectUrl != null ? providerRedirectUrl.hashCode() : 0);
    result = 31 * result + (callbackUrl != null ? callbackUrl.hashCode() : 0);
    result = 31 * result +
        (callbackParameters != null ? callbackParameters.hashCode() : 0);
    return result;
  }
}
