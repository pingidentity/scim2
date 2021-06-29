/*
 * Copyright 2015-2021 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.util.Calendar;

public final class OAuth2Client
{

  public static class Builder
  {
    private String name;
    private String description;
    private String url;
    private String iconUrl;
    private String emailAddress;

    /**
     * Sets the name of the client.
     *
     * @param name the name of the client.
     * @return this.
     */
    public Builder setName(final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the description of the client.
     *
     * @param description the description of the client.
     * @return this.
     */
    public Builder setDescription(final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets the url of the client.
     *
     * @param url the url of the client.
     * @return this.
     */
    public Builder setUrl(final String url)
    {
      this.url = url;
      return this;
    }

    /**
     * Sets the icon url of the client.
     *
     * @param iconUrl the icon url of the client.
     * @return this.
     */
    public Builder setIconUrl(final String iconUrl)
    {
      this.iconUrl = iconUrl;
      return this;
    }

    /**
     * Sets the email address of the client.
     *
     * @param emailAddress the email address of the client.
     * @return this.
     */
    public Builder setEmailAddress(final String emailAddress)
    {
      this.emailAddress = emailAddress;
      return this;
    }

    /**
     * Builds an client with the parameters that have been
     * set in the builder.
     *
     * @return a newly created client.
     */
    public OAuth2Client build()
    {
      return new OAuth2Client(this);
    }
  }

  @Attribute(description = "The name of the client authorized " +
      "by the consent.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String name;

  @Attribute(description = "The description of the client authorized " +
      "by the consent.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String description;

  @Attribute(description = "The reference to the page of the client " +
      "authorized by the consent.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String url;

  @Attribute(description = "The reference to icon of the client " +
      "authorized by the consent.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String iconUrl;

  @Attribute(description = "The name of the client authorized " +
      "by the consent.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String emailAddress;

  @Attribute(description = "The last time this client obtained a token using " +
      "this session.", mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Calendar lastAuthorization;

  // private no-arg constructor for Jackson
  private OAuth2Client()
  {
    this(new Builder());
  }

  private OAuth2Client(final Builder builder)
  {
    this.name = builder.name;
    this.description = builder.description;
    this.url = builder.url;
    this.iconUrl = builder.iconUrl;
    this.emailAddress = builder.emailAddress;
  }

  /**
   * Gets the name of the client.
   *
   * @return the name of the client.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of the client.
   *
   * @return the description of the client.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the url of the client.
   *
   * @return the url of the client.
   */
  public String getUrl()
  {
    return url;
  }

  /**
   * Gets the icon url of the client.
   *
   * @return the icon url of the client.
   */
  public String getIconUrl()
  {
    return iconUrl;
  }

  /**
   * Gets the email address for the client.
   *
   * @return the email address for the client.
   */
  public String getEmailAddress()
  {
    return emailAddress;
  }

  /**
   * Gets the date of the last authorization.
   *
   * @return the date of the last authorization.
   */
  public Calendar getLastAuthorization()
  {
    return lastAuthorization;
  }

  /**
   * Sets the date of the last authorization.
   *
   * @param lastAuthorization the date of the last authorization.
   */
  public void setLastAuthorization(final Calendar lastAuthorization)
  {
    this.lastAuthorization = lastAuthorization;
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

    OAuth2Client that = (OAuth2Client) o;

    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (description != null ? !description.equals(that.description) :
        that.description != null)
    {
      return false;
    }
    if (url != null ? !url.equals(that.url) : that.url != null)
    {
      return false;
    }
    if (iconUrl != null ? !iconUrl.equals(that.iconUrl) : that.iconUrl != null)
    {
      return false;
    }
    if (emailAddress != null ? !emailAddress.equals(that.emailAddress) :
        that.emailAddress != null)
    {
      return false;
    }
    return lastAuthorization != null ?
        lastAuthorization.equals(that.lastAuthorization) :
        that.lastAuthorization == null;
  }

  @Override
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
    result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
    result = 31 * result +
        (lastAuthorization != null ? lastAuthorization.hashCode() : 0);
    return result;
  }
}
