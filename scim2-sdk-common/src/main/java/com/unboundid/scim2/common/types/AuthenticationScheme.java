/*
 * Copyright 2015-2020 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A complex type that specifies supported Authentication Scheme properties.
 */
public class AuthenticationScheme
{
  @Attribute(description = "The common authentication scheme name; " +
      "e.g., HTTP Basic.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final String name;

  @Attribute(description = "A description of the Authentication Scheme.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final String description;

  @Attribute(description = "An HTTP addressable URI pointing to the " +
      "Authentication Scheme's specification.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final URI specUri;

  @Attribute(description = "An HTTP addressable URI pointing to the " +
      "Authentication Scheme's usage documentation.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final URI documentationUri;

  @Attribute(description = "A label indicating the authentication " +
      "scheme type; e.g., \"oauth\" or \"oauth2\".",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true,
      canonicalValues = { "oauth", "oauth2", "oauthbearertoken", "httpbasic",
          "httpdigest" } )
  private final String type;

  @Attribute(description = "A Boolean value indicating whether this " +
      "authentication scheme is preferred.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean primary;
  /**
   * Create a new complex type that specifies supported Authentication Scheme
   * properties.
   *
   * @param name The common authentication scheme name.
   * @param description A description of the Authentication Scheme.
   * @param specUri An HTTP addressable URI pointing to the Authentication
   *                Scheme's specification.
   * @param documentationUri An HTTP addressable URI pointing to the
   *                         Authentication Scheme's usage documentation.
   * @param type A label indicating the authentication scheme type.
   * @param primary  Boolean value indicating whether this authentication
   *                 scheme is preferred.
   */
  public AuthenticationScheme(
      @JsonProperty(value = "name", required = true) final String name,
      @JsonProperty(value = "description", required = true)
      final String description,
      @JsonProperty(value = "specUri") final URI specUri,
      @JsonProperty(value = "documentationUri")
      final URI documentationUri,
      @JsonProperty(value = "type") final String type,
      @JsonProperty(value = "primary", defaultValue = "false")
      final boolean primary)
  {
    this.name = name;
    this.description = description;
    this.specUri = specUri;
    this.documentationUri = documentationUri;
    this.type = type;
    this.primary = primary;
  }

  /**
   * Retrieves the common authentication scheme name.
   *
   * @return The common authentication scheme name.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Retrieves the description of the Authentication Scheme.
   *
   * @return The description of the Authentication Scheme.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Retrieves the HTTP addressable URI pointing to the Authentication
   * Scheme's specification.
   *
   * @return The HTTP addressable URI pointing to the Authentication
   * Scheme's specification.
   */
  public URI getSpecUri()
  {
    return specUri;
  }

  /**
   * Retrieves the HTTP addressable URI pointing to the Authentication
   * Scheme's usage documentation.
   *
   * @return The HTTP addressable URI pointing to the Authentication
   * Scheme's usage documentation.
   */
  public URI getDocumentationUri()
  {
    return documentationUri;
  }

  /**
   * Retrieves the label indicating the authentication scheme type.
   *
   * @return The label indicating the authentication scheme type.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Retrieves the Boolean value indicating whether this authentication
   * scheme is preferred.
   *
   * @return The Boolean value indicating whether this authentication
   * scheme is preferred.
   */
  public boolean isPrimary()
  {
    return primary;
  }

  /**
   * {@inheritDoc}
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

    AuthenticationScheme that = (AuthenticationScheme) o;

    if (primary != that.primary)
    {
      return false;
    }
    if (description != null ? !description.equals(that.description) :
        that.description != null)
    {
      return false;
    }
    if (documentationUri != null ? !documentationUri.equals(
        that.documentationUri) : that.documentationUri != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (specUri != null ? !specUri.equals(that.specUri) :
        that.specUri != null)
    {
      return false;
    }
    if (type != null ? !type.equals(that.type) : that.type != null)
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (specUri != null ? specUri.hashCode() : 0);
    result = 31 * result + (documentationUri != null ?
        documentationUri.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (primary ? 1 : 0);
    return result;
  }

  /**
   * Convenience method that creates a new AuthenticationScheme instances for
   * HTTP BASIC.
   *
   * @param primary Whether this authentication scheme is primary
   *
   * @return A new AuthenticationScheme instances for HTTP BASIC.
   */
  public static AuthenticationScheme createHttpBasic(final boolean primary)
  {
    try
    {
      return new AuthenticationScheme(
          "HTTP Basic",
          "The HTTP Basic Access Authentication scheme. This scheme is not " +
              "considered to be a secure method of user authentication " +
              "(unless used in conjunction with some external secure system " +
              "such as SSL), as the user name and password are passed over " +
              "the network as cleartext.",
          new URI("http://www.ietf.org/rfc/rfc2617.txt"),
          null,
          "httpbasic", primary);
    }
    catch (URISyntaxException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Convenience method that creates a new AuthenticationScheme instances for
   * OAuth 2 bearer token.
   *
   * @param primary Whether this authentication scheme is primary
   *
   * @return A new AuthenticationScheme instances for OAuth 2 bearer token.
   */
  public static AuthenticationScheme createOAuth2BearerToken(
      final boolean primary)
  {
    try
    {
      return new AuthenticationScheme(
          "OAuth 2.0 Bearer Token",
          "The OAuth 2.0 Bearer Token Authentication scheme. OAuth enables " +
              "clients to access protected resources by obtaining an access " +
              "token, which is defined in RFC 6750 as \"a string " +
              "representing an access authorization issued to the client\", " +
              "rather than using the resource owner's credentials directly.",
          new URI("http://tools.ietf.org/html/rfc6750"),
          null,
          "oauthbearertoken", primary);
    }
    catch (URISyntaxException e)
    {
      throw new RuntimeException(e);
    }
  }
}
