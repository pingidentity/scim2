/*
 * Copyright 2015-2024 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;

/**
 * Provider information.  This class will contain information about
 * external identity providers.
 */
@Schema(description = "External identity provider information.",
    id = "urn:pingidentity:scim:api:messages:2.0:provider",
    name = "Provider")
public final class Provider
{

  public static class Builder
  {
    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    private String iconUrl;

    @Nullable
    private String type;

    @Nullable
    private String samlResponseBinding;

    /**
     * Sets the name of this provider.
     *
     * @param name the name of this provider.
     * @return this
     */
    @NotNull
    public Builder setName(@Nullable final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the description of this provider.
     *
     * @param description the description of this provider.
     * @return this
     */
    @NotNull
    public Builder setDescription(@Nullable final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets the icon url of this provider.
     *
     * @param iconUrl the icon url of this provider.
     * @return this
     */
    @NotNull
    public Builder setIconUrl(@Nullable final String iconUrl)
    {
      this.iconUrl = iconUrl;
      return this;
    }

    /**
     * Sets the type of this provider.
     *
     * @param type the type of this provider.
     * @return this
     */
    @NotNull
    public Builder setType(@Nullable final String type)
    {
      this.type = type;
      return this;
    }

    /**
     * Sets the SAML response binding. Only applicable to providers of
     * type "{@code saml}".
     *
     * @param samlResponseBinding The SAML response binding, either
     *                            "{@code artifact}" or "{@code post}".
     *
     * @return this
     */
    @NotNull
    public Builder setSamlResponseBinding(
        @Nullable final String samlResponseBinding)
    {
      this.samlResponseBinding = samlResponseBinding;
      return this;
    }



    /**
     * Builds a new provider object from the attributes in this builder.
     *
     * @return a new provider object.
     */
    @NotNull
    public Provider build()
    {
      return new Provider(this);
    }
  }

  @Nullable
  @Attribute(description = "The name of the IDP.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      isRequired = true)
  private final String name;

  @Nullable
  @Attribute(description = "The description of the IDP.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String description;

  @Nullable
  @Attribute(description = "The reference to the icon of the IDP.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String iconUrl;

  @Nullable
  @Attribute(description = "The IDP type.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String type;

  @Nullable
  @Attribute(description = "The SAML response binding, either 'artifact' " +
                           "or 'post'. Only applicable to providers of " +
                           "type 'saml'.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String samlResponseBinding;


  // private no-arg constructor for Jackson
  private Provider()
  {
    this(new Builder());
  }

  private Provider(@NotNull final Builder builder)
  {
    this.name = builder.name;
    this.description = builder.description;
    this.iconUrl = builder.iconUrl;
    this.type = builder.type;
    this.samlResponseBinding = builder.samlResponseBinding;
  }

  /**
   * Gets the name of this provider.
   *
   * @return the name of this provider.
   */
  @Nullable
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of this provider.
   *
   * @return the description of this provider.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the type of this provider.
   *
   * @return the type of this provider.
   */
  @Nullable
  public String getType()
  {
    return type;
  }

  /**
   * Gets the icon url of this provider.
   *
   * @return the icon url of this provider.
   */
  @Nullable
  public String getIconUrl()
  {
    return iconUrl;
  }

  /**
   * Gets the SAML response binding, either "{@code artifact}" or "{@code post}".
   * Only applicable to providers of type "{@code saml}".
   *
   * @return The SAML response binding, either "{@code artifact}" or "{@code post}".
   */
  @Nullable
  public String getSamlResponseBinding()
  {
    return samlResponseBinding;
  }

  /**
   * Indicates whether the provided object is equal to this Provider.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this Provider
   *            or {@code false} if not.
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

    final Provider provider = (Provider) o;

    if (name != null ? !name.equals(provider.name) : provider.name != null)
    {
      return false;
    }
    if (description != null ? !description.equals(provider.description) :
        provider.description != null)
    {
      return false;
    }
    if (iconUrl != null ? !iconUrl.equals(provider.iconUrl) :
        provider.iconUrl != null)
    {
      return false;
    }
    if (type != null ? !type.equals(provider.type) : provider.type != null)
    {
      return false;
    }
    return samlResponseBinding != null ? samlResponseBinding.equals(
        provider.samlResponseBinding) : provider.samlResponseBinding == null;

  }



  /**
   * Retrieves a hash code for this Provider.
   *
   * @return  A hash code for this Provider.
   */
  @Override
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (samlResponseBinding != null ?
                            samlResponseBinding.hashCode() : 0);
    return result;
  }
}
