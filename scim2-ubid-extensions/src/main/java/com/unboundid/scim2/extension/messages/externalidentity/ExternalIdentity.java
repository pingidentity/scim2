/*
 * Copyright 2015-2016 UnboundID Corp.
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
    id = "urn:unboundid:scim:api:messages:2.0:ExternalIdentity",
    name = "ExternalIdentity")
public final class ExternalIdentity extends BaseScimResource
{

  public static class Builder
  {
    private String providerName;
    private String accessToken;
    private String refreshToken;
    private String providerUserId;
    private String callbackUrl;
    private String flowState;
    private ObjectNode callbackParameters;

    /**
     * Constructs a new builder.
     */
    public Builder()
    {
    }

    /**
     * Creates an external identity link request from a provider user id.
     * Use this when the request is to create a link from a known provider
     * user id.
     *
     * @param providerUserId the provider user id to use.
     * @return the newly created external identity request.
     */
    public static ExternalIdentity fromProviderUserId(
        final String providerUserId)
    {
      Builder builder = new Builder();
      builder.providerUserId = providerUserId;
      return builder.build();
    }

    /**
     * Creates an external identity link request from a callback uri.
     * Use this when the request is to create a link interactively.  This
     * request will return information that can be use to authenticate the
     * user, and then an additional request is needed to complete the link.
     *
     * @param callbackUrl the callback uri that is used for the external
     *                    identity provider.
     * @return the newly created external identity request.
     */
    public static ExternalIdentity fromcallbackUrl(
        final String callbackUrl)
    {
      Builder builder = new Builder();
      builder.callbackUrl = callbackUrl;
      return builder.build();
    }

    /**
     * Creates an external identity link request from a callback uri.
     * Use this when the request is to complete creating a link
     * interactively.  This request must contain the flow state string
     * returned by the request that initiated the link creation, as well
     * as any parameters sent to the callback uri.
     *
     * @param callbackParameters the parameters sent to the callback uri by
     *                           the identity provider.
     * @param flowState the flow state string returned by the request
     *                        that started this request.
     * @return the newly created external identity request.
     */
    public static ExternalIdentity fromCallbackParameters(
        final ObjectNode callbackParameters, final String flowState)
    {
      Builder builder = new Builder();
      builder.callbackParameters = callbackParameters;
      builder.flowState = flowState;
      return builder.build();
    }

    /**
     * Sets the external identity provider.
     *
     * @param providerName the external identity provider name.
     * @return this;
     */
    public Builder setProviderName(final String providerName)
    {
      this.providerName = providerName;
      return this;
    }

    /**
     * Sets the access token for the external identity provider.
     *
     * @param accessToken the access token for the external identity provider.
     * @return this;
     */
    public Builder setAccessToken(final String accessToken)
    {
      this.accessToken = accessToken;
      return this;
    }

    /**
     * Sets the refresh token for the external identity provider.
     *
     * @param refreshToken the refresh token for the external identity provider.
     * @return this;
     */
    public Builder setRefreshToken(final String refreshToken)
    {
      this.refreshToken = refreshToken;
      return this;
    }

    /**
      * Builds a new external identity object from values in this builder.
      *
      * @return a new external identity object.
      */
    public ExternalIdentity build()
    {
      return new ExternalIdentity(this);
    }

  }

  @Attribute(description = "The external IDP.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final Provider provider;

  @Attribute(description = "The user ID at the provider. If not available, " +
      "the user is not linked to any external identities at the provider.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String providerUserId;

  @Attribute(description = "The access token issued by the provider that " +
      "may be used to retrieve additional data.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String accessToken;

  @Attribute(description = "The refresh token issued by the provider that " +
      "may be used to retrieve a new access token.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String refreshToken;

  @Attribute(description = "The provider redirect url can be used to obtain " +
      "an auth code for this external identity provider.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String providerRedirectUrl;

  @Attribute(description = "The state of the current operation.  This should " +
      "not need to be interpreted by clients",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String flowState;

  @Attribute(description = "The OAuth2 callback url",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String callbackUrl;

  @Attribute(description = "The parameters received during an OAuth2 callback",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final ObjectNode callbackParameters;

  /**
   * No-argument constructor.
   */
  public ExternalIdentity()
  {
    this(new Builder());
  }

  /**
   * Constructs a new external identity object from the provided builder.
   *
   * @param builder the builder to build the new external identity from.
   */
  private ExternalIdentity(final Builder builder)
  {
    this(new Provider.Builder().setName(builder.providerName).build(),
        builder.providerUserId, builder.accessToken, builder.refreshToken,
        builder.flowState, builder.callbackUrl, builder.callbackParameters);
  }

  /**
   * Constructs a new ExternalIdentity object.  This should only be needed
   * by the server.  Clients should instead construct ExternalIdentity objects
   * with the provided builder.
   *
   * @param provider the provider
   */
  public ExternalIdentity(final Provider provider)
  {
    this(provider, null, null, null, null, null, null);
  }

  /**
   * Constructs a new ExternalIdentity object.  This should only be needed
   * by the server.  Clients should instead construct ExternalIdentity objects
   * with the provided builder.
   *
   * @param provider the provider
   * @param providerUserId the provider user id.
   * @param accessToken the access token.
   * @param refreshToken the refresh token (optional.  May be null.).
   */
  public ExternalIdentity(final Provider provider, final String providerUserId,
      final String accessToken, final String refreshToken)
  {
    this(provider, providerUserId, accessToken, refreshToken,
        null, null, null);
  }

  /**
   * Constructs a new ExternalIdentity object.  This should only be needed
   * by the server.  Clients should instead construct ExternalIdentity objects
   * with the provided builder.
   *
   * @param provider the provider
   * @param providerUserId the provider user id.
   * @param accessToken the access token.
   * @param refreshToken the refresh token (optional.  May be null.).
   * @param flowState the flowState for external identity operations that
   *                  span multiple rest operations.
   * @param callbackUrl the OAuth2 callback url.
   * @param callbackParameters any parameters recieved from an OAuth2 callback.
   */
  public ExternalIdentity(final Provider provider, final String providerUserId,
      final String accessToken, final String refreshToken,
      final String flowState, final String callbackUrl,
      final ObjectNode callbackParameters)
  {
    this.provider = provider;
    this.providerUserId = providerUserId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.callbackUrl = callbackUrl;
    this.callbackParameters = callbackParameters;
    this.flowState = flowState;
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
   * Sets the provider redirect url in the external identity.  The provider
   * redirect url can be used to obtain an auth code for this external
   * identity provider.
   *
   * @param providerRedirectUrl the provider redirect url.
   */
  public void setProviderRedirectUrl(final String providerRedirectUrl)
  {
    this.providerRedirectUrl = providerRedirectUrl;
  }

  /**
   * Gets the flow state.  The flow state keeps track of the state
   * of external identity operations such as creating a link when
   * multiple calls to the REST endpoint are involved.  If a flow state
   * is provided, it should be passed back for subsequent calls to the
   * interface that are required to complete the operation.  The state
   * should not be altered.
   *
   * @return the flow state.
   */
  public String getFlowState()
  {
    return flowState;
  }

  /**
   * Sets the flow state.  The flow state keeps track of the state
   * of external identity operations such as creating a link when
   * multiple calls to the REST endpoint are involved.  If a flow state
   * is provided, it should be passed back for subsequent calls to the
   * interface that are required to complete the operation.  This method
   * is used by the server, but should not be called by clients.
   *
   * @param flowState the flow state.
   */
  public void setFlowState(final String flowState)
  {
    this.flowState = flowState;
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
    if (flowState != null ? !flowState.equals(that.flowState) :
        that.flowState != null)
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
    result = 31 * result + (flowState != null ? flowState.hashCode() : 0);
    result = 31 * result + (callbackUrl != null ? callbackUrl.hashCode() : 0);
    result = 31 * result +
        (callbackParameters != null ? callbackParameters.hashCode() : 0);
    return result;
  }
}
