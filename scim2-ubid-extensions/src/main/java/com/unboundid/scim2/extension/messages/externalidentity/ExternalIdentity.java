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
import com.unboundid.scim2.common.types.Meta;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * External identity provider information.
 */
@Schema(description = "Contains information about external identities.",
    id = "urn:unboundid:schemas:broker:2.0:ExternalIdentity",
    name = "ExternalIdentity")
public final class ExternalIdentity extends BaseScimResource
{

  public static class Builder
  {
    private Provider provider;
    private String providerUserId;
    private String accessToken;
    private String refreshToken;
    private Calendar created;
    private Calendar lastModified;
    private String id;

    /**
     * Sets the external identity provider.
     *
     * @param provider the external identity provider.
     * @return this;
     */
    public Builder setProvider(final Provider provider)
    {
      this.provider = provider;
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
     * Sets the created time.
     *
     * @param created the created time.
     * @return this;
     */
    public Builder setCreated(final Calendar created)
    {
      this.created = created;
      return this;
    }

    /**
     * Sets the last modified time.
     *
     * @param lastModified the last modified time.
     * @return this;
     */
    public Builder setLastModified(final Calendar lastModified)
    {
      this.lastModified = lastModified;
      return this;
    }

    /**
     * Sets the id of this external identity.
     *
     * @param id the id of this external identity.  This should be the
     *           name of the external identity provider.
     * @return this;
     */
    public Builder setId(final String id)
    {
      this.id = id;
      return this;
    }

    /**
     * Builds a new external identity object from values in this builder.
     *
     * @return a new external identity object.
     */
    public ExternalIdentity build()
    {
      if(created == null)
      {
        created = new GregorianCalendar();
      }
      if(lastModified == null)
      {
        lastModified = new GregorianCalendar();
      }
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

  // private no-arg constructor for Jackson
  private ExternalIdentity()
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
    Meta meta  = new Meta();
    meta.setLastModified(builder.created);
    meta.setLastModified(builder.lastModified);
    setMeta(meta);
    this.provider = builder.provider;
    this.providerUserId = builder.providerUserId;
    this.accessToken = builder.accessToken;
    this.refreshToken = builder.refreshToken;
    this.setId(builder.id);
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
