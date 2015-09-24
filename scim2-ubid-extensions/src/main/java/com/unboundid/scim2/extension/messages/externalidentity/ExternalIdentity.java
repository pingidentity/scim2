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

package com.unboundid.scim2.extension.messages.externalidentity;

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

    /**
     * Constructs a new builder.
     */
    public Builder()
    {
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
     * Sets the user id from the external identity provider.
     *
     * @param providerUserId the user id from the external identity provider.
     * @return this;
     */
    public Builder setProviderUserId(final String providerUserId)
    {
      this.providerUserId = providerUserId;
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

  // TODO this should be READ_ONLY.  I changed it to immutable temporarily,
  // because at present it will get stored with null, and it's a key value.
  // that causes problems.  It can't be removed because we dont allow
  // filters to have null key values.
  @Attribute(description = "The user ID at the provider. If not available, " +
      "the user is not linked to any external identities at the provider.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String providerUserId;

  @Attribute(description = "The access token issued by the provider that " +
      "may be used to retrieve additional data.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String accessToken;

  @Attribute(description = "The refresh token issued by the provider that " +
      "may be used to retrieve a new access token.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String refreshToken;

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
        builder.providerUserId, builder.accessToken, builder.refreshToken);
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
    this.provider = provider;
    this.providerUserId = providerUserId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
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
    return !(refreshToken != null ? !refreshToken.equals(that.refreshToken) :
        that.refreshToken != null);
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (provider != null ? provider.hashCode() : 0);
    result = 31 * result +
        (providerUserId != null ? providerUserId.hashCode() : 0);
    result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
    result = 31 * result +
        (refreshToken != null ? refreshToken.hashCode() : 0);
    return result;
  }

}
