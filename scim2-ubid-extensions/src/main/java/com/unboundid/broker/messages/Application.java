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
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.net.URL;

public final class Application extends BaseScimResource
{
  public static class Builder
  {
    private String name;
    private String description;
    private URL url;
    private URL iconUrl;
    private String emailAddress;

    /**
     * Sets the name of the application.
     *
     * @param name the name of the application.
     * @return this.
     */
    public Builder setName(final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the description of the application.
     *
     * @param description the description of the application.
     * @return this.
     */
    public Builder setDescription(final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets the url of the application.
     *
     * @param url the url of the application.
     * @return this.
     */
    public Builder setUrl(final URL url)
    {
      this.url = url;
      return this;
    }

    /**
     * Sets the icon url of the application.
     *
     * @param iconUrl the icon url of the application.
     * @return this.
     */
    public Builder setIconUrl(final URL iconUrl)
    {
      this.iconUrl = iconUrl;
      return this;
    }

    /**
     * Sets the email address of the application.
     *
     * @param emailAddress the email address of the application.
     * @return this.
     */
    public Builder setEmailAddress(final String emailAddress)
    {
      this.emailAddress = emailAddress;
      return this;
    }

    /**
     * Builds an application with the parameters that have been
     * set in the builder.
     *
     * @return a newly created application.
     */
    public Application build()
    {
      return new Application(this);
    }
  }

  @Attribute(description = "The name of the application authorized " +
      "by the consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String name;

  @Attribute(description = "The description of the application authorized " +
      "by the consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String description;

  @Attribute(description = "The reference to the page of the application " +
      "authorized by the consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final URL url;

  @Attribute(description = "The reference to icon of the application " +
      "authorized by the consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final URL iconUrl;

  @Attribute(description = "The name of the application authorized " +
      "by the consent.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private final String emailAddress;

  // private no-arg constructor for Jackson
  private Application()
  {
    this(new Builder());
  }

  private Application(final Builder builder)
  {
    this.name = builder.name;
    this.description = builder.description;
    this.url = builder.url;
    this.iconUrl = builder.iconUrl;
    this.emailAddress = builder.emailAddress;
  }

  /**
   * Gets the name of the application.
   *
   * @return the name of the application.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of the application.
   *
   * @return the description of the application.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the url of the application.
   *
   * @return the url of the application.
   */
  public URL getUrl()
  {
    return url;
  }

  /**
   * Gets the icon url of the application.
   *
   * @return the icon url of the application.
   */
  public URL getIconUrl()
  {
    return iconUrl;
  }

  /**
   * Gets the email address for the application.
   *
   * @return the email address for the application.
   */
  public String getEmailAddress()
  {
    return emailAddress;
  }
}
