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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * SCIM provides a schema for representing the service provider's configuration
 * identified using the following schema URI:
 * "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig"
 *
 * The Service Provider configuration resource enables a Service
 * Provider to discover SCIM specification features in a standardized
 * form as well as provide additional implementation details to clients.
 * All attributes have a mutability of "readOnly".  Unlike other core
 * resources, the "id" attribute is not required for the Service
 * Provider configuration resource.
 **/
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig",
    name="Service Provider Config",
    description = "SCIM 2.0 Service Provider Config Resource")
public class ServiceProviderConfigResource extends BaseScimResource
{
  /**
   * A complex type that specifies PATCH configuration options.
   */
  public static class Patch
  {
    @Attribute(description = "Boolean value specifying whether the " +
        "operation is supported.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final boolean supported;

    /**
     * Create a new complex type that specifies PATCH configuration options.
     * @param supported Boolean value specifying whether the operation is
     *                  supported.
     */
    @JsonCreator
    public Patch(@JsonProperty(value = "supported", required = true)
                 final boolean supported)
    {
      this.supported = supported;
    }

    /**
     * Retrieves the boolean value specifying whether the operation is
     * supported.
     *
     * @return {@code true} if the operation is supported or {@code false}
     * otherwise.
     */
    public boolean isSupported()
    {
      return supported;
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

      Patch that = (Patch) o;

      if (supported != that.supported)
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
      return (supported ? 1 : 0);
    }
  }

  /**
   * A complex type that specifies Bulk configuration options.
   */
  public static class Bulk
  {
    @Attribute(description = "Boolean value specifying whether the " +
        "operation is supported.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final boolean supported;

    @Attribute(description = "An integer value specifying the maximum " +
        "number of operations.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final int maxOperations;

    @Attribute(description = "An integer value specifying the maximum " +
        "payload size in bytes.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final int maxPayloadSize;

    /**
     * Create a new complex type that specifies Bulk configuration options.
     *
     * @param supported Boolean value specifying whether the operation is
     *                  supported.
     * @param maxOperations An integer value specifying the maximum number of
     *                      operations.
     * @param maxPayloadSize An integer value specifying the maximum payload
     *                       size in bytes
     */
    @JsonCreator
    public Bulk(@JsonProperty(value = "supported", required = true)
                final boolean supported,
                @JsonProperty(value = "maxOperations", required = true)
                final int maxOperations,
                @JsonProperty(value = "maxPayloadSize", required = true)
                final int maxPayloadSize)
    {
      this.supported = supported;
      this.maxOperations = maxOperations;
      this.maxPayloadSize = maxPayloadSize;
    }

    /**
     * Retrieves the boolean value specifying whether the operation is
     * supported.
     *
     * @return {@code true} if the operation is supported or {@code false}
     * otherwise.
     */
    public boolean isSupported()
    {
      return supported;
    }

    /**
     * Retrieves the integer value specifying the maximum number of operations.
     *
     * @return The integer value specifying the maximum number of operations.
     */
    public int getMaxOperations()
    {
      return maxOperations;
    }

    /**
     * Retrieves the integer value specifying the maximum payload size in bytes.
     *
     * @return the integer value specifying the maximum payload size in bytes.
     */
    public int getMaxPayloadSize()
    {
      return maxPayloadSize;
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

      Bulk that = (Bulk) o;

      if (maxOperations != that.maxOperations)
      {
        return false;
      }
      if (maxPayloadSize != that.maxPayloadSize)
      {
        return false;
      }
      if (supported != that.supported)
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
      int result = (supported ? 1 : 0);
      result = 31 * result + maxOperations;
      result = 31 * result + maxPayloadSize;
      return result;
    }
  }

  /**
   * A complex type that specifies FILTER options.
   */
  public static class Filter
  {
    @Attribute(description = "Boolean value specifying whether the " +
        "operation is supported.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final boolean supported;

    @Attribute(description = "Integer value specifying the maximum " +
        "number of resources returned in a response.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final int maxResults;

    /**
     * Create a new complex type that specifies FILTER options.
     *
     * @param supported Boolean value specifying whether the operation is
     *                  supported.
     * @param maxResults Integer value specifying the maximum number of
     *                   resources returned in a response.
     */
    public Filter(@JsonProperty(value = "supported", required = true)
                  final boolean supported,
                  @JsonProperty(value = "maxResults", required = true)
                  final int maxResults)
    {
      this.supported = supported;
      this.maxResults = maxResults;
    }

    /**
     * Retrieves the boolean value specifying whether the operation is
     * supported.
     *
     * @return {@code true} if the operation is supported or {@code false}
     * otherwise.
     */
    public boolean isSupported()
    {
      return supported;
    }

    /**
     * Retrieves the integer value specifying the maximum number of resources
     * returned in a response.
     *
     * @return The integer value specifying the maximum number of resources
     * returned in a response.
     */
    public int getMaxResults()
    {
      return maxResults;
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

      Filter that = (Filter) o;

      if (maxResults != that.maxResults)
      {
        return false;
      }
      if (supported != that.supported)
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
      int result = (supported ? 1 : 0);
      result = 31 * result + maxResults;
      return result;
    }
  }

  /**
   * A complex type that specifies Change Password configuration options.
   */
  public static class ChangePassword
  {
    @Attribute(description = "Boolean value specifying whether the " +
        "operation is supported.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final boolean supported;

    /**
     * Create a new complex type that specifies Change Password configuration
     * options.
     *
     * @param supported Boolean value specifying whether the operation is
     *                  supported.
     */
    @JsonCreator
    public ChangePassword(@JsonProperty(value = "supported", required = true)
                          final boolean supported)
    {
      this.supported = supported;
    }

    /**
     * Retrieves the boolean value specifying whether the operation is
     * supported.
     *
     * @return {@code true} if the operation is supported or {@code false}
     * otherwise.
     */
    public boolean isSupported()
    {
      return supported;
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

      ChangePassword that = (ChangePassword) o;

      if (supported != that.supported)
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
      return (supported ? 1 : 0);
    }
  }

  /**
   * A complex type that specifies Sort configuration options.
   */
  public static class Sort
  {
    @Attribute(description = "Boolean value specifying whether the " +
        "operation is supported.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final boolean supported;

    /**
     * Create a new complex type that specifies Sort configuration options.
     *
     * @param supported Boolean value specifying whether the operation is
     *                  supported
     */
    @JsonCreator
    public Sort(@JsonProperty(value = "supported", required = true)
                final boolean supported)
    {
      this.supported = supported;
    }

    /**
     * Retrieves the boolean value specifying whether the operation is
     * supported.
     *
     * @return {@code true} if the operation is supported or {@code false}
     * otherwise.
     */
    public boolean isSupported()
    {
      return supported;
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

      Sort that = (Sort) o;

      if (supported != that.supported)
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
      return (supported ? 1 : 0);
    }
  }

  /**
   * A complex type that specifies Etag configuration options.
   */
  public static class ETag
  {
    @Attribute(description = "Boolean value specifying whether the " +
        "operation is supported.",
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        isRequired = true)
    private final boolean supported;

    /**
     * Create a new complex type that specifies Etag configuration options.
     *
     * @param supported Boolean value specifying whether the operation is
     *                  supported.
     */
    @JsonCreator
    public ETag(@JsonProperty(value = "supported", required = true)
                final boolean supported)
    {
      this.supported = supported;
    }

    /**
     * Retrieves the boolean value specifying whether the operation is
     * supported.
     *
     * @return {@code true} if the operation is supported or {@code false}
     * otherwise.
     */
    public boolean isSupported()
    {
      return supported;
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

      ETag that = (ETag) o;

      if (supported != that.supported)
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
      return (supported ? 1 : 0);
    }
  }

  /**
   * A complex type that specifies supported Authentication Scheme properties.
   */
  public static class AuthenticationScheme
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

    @Attribute(description = "An HTTP addressable URL pointing to the " +
        "Authentication Scheme's specification.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
    private final URL specUrl;

    @Attribute(description = "An HTTP addressable URL pointing to the " +
        "Authentication Scheme's usage documentation.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
    private final URL documentationUrl;

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
     * @param specUrl An HTTP addressable URL pointing to the Authentication
     *                Scheme's specification.
     * @param documentationUrl An HTTP addressable URL pointing to the
     *                         Authentication Scheme's usage documentation.
     * @param type A label indicating the authentication scheme type.
     * @param primary  Boolean value indicating whether this authentication
     *                 scheme is preferred.
     */
    @JsonCreator
    public AuthenticationScheme(
        @JsonProperty(value = "name", required = true) final String name,
        @JsonProperty(value = "description", required = true)
        final String description,
        @JsonProperty(value = "specUrl") final URL specUrl,
        @JsonProperty(value = "documentationUrl")
        final URL documentationUrl,
        @JsonProperty(value = "type") final String type,
        @JsonProperty(value = "primary") final boolean primary)
    {
      this.name = name;
      this.description = description;
      this.specUrl = specUrl;
      this.documentationUrl = documentationUrl;
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
     * Retrieves the HTTP addressable URL pointing to the Authentication
     * Scheme's specification.
     *
     * @return The HTTP addressable URL pointing to the Authentication
     * Scheme's specification.
     */
    public URL getSpecUrl()
    {
      return specUrl;
    }

    /**
     * Retrieves the HTTP addressable URL pointing to the Authentication
     * Scheme's usage documentation.
     *
     * @return The HTTP addressable URL pointing to the Authentication
     * Scheme's usage documentation.
     */
    public URL getDocumentationUrl()
    {
      return documentationUrl;
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
      if (documentationUrl != null ? !documentationUrl.equals(
          that.documentationUrl) : that.documentationUrl != null)
      {
        return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null)
      {
        return false;
      }
      if (specUrl != null ? !specUrl.equals(that.specUrl) :
          that.specUrl != null)
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
      result = 31 * result + (specUrl != null ? specUrl.hashCode() : 0);
      result = 31 * result + (documentationUrl != null ?
          documentationUrl.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (primary ? 1 : 0);
      return result;
    }
  }

  @Attribute(description = "An HTTP addressable URL pointing to the " +
      "service provider's human consumable help documentation.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String documentationUrl;

  @Attribute(description = "A complex type that specifies PATCH " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final Patch patch;

  @Attribute(description = "A complex type that specifies Bulk " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final Bulk bulk;

  @Attribute(description = "A complex type that specifies FILTER options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final Filter filter;

  @Attribute(description = "A complex type that specifies Change " +
      "Password configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final ChangePassword changePassword;

  @Attribute(description = "A complex type that specifies Sort " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final Sort sort;

  @Attribute(description = "A complex type that specifies Etag " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final ETag etag;

  @Attribute(description = "A complex type that specifies supported " +
      "Authentication Scheme properties.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final List<AuthenticationScheme> authenticationSchemes;

  /**
   * Create a new ServiceProviderConfig.
   *
   * @param documentationUrl An HTTP addressable URL pointing to the service
   *                         provider's human consumable help documentation.
   * @param patch A complex type that specifies PATCH configuration options.
   * @param bulk A complex type that specifies Bulk configuration options.
   * @param filter A complex type that specifies FILTER options.
   * @param changePassword A complex type that specifies Change Password
   *                       configuration options.
   * @param sort A complex type that specifies Sort configuration options.
   * @param etag A complex type that specifies Etag configuration options.
   * @param authenticationSchemes A complex type that specifies supported
   *                              Authentication Scheme properties.
   */
  @JsonCreator
  public ServiceProviderConfigResource(
      @JsonProperty(value = "documentationUrl") final String documentationUrl,
      @JsonProperty(value = "patch", required = true) final Patch patch,
      @JsonProperty(value = "bulk", required = true) final Bulk bulk,
      @JsonProperty(value = "filter", required = true) final Filter filter,
      @JsonProperty(value = "changePassword", required = true)
      final ChangePassword changePassword,
      @JsonProperty(value = "sort", required = true) final Sort sort,
      @JsonProperty(value = "etag", required = true) final ETag etag,
      @JsonProperty(value = "authenticationSchemes", required = true)
      final List<AuthenticationScheme> authenticationSchemes)
  {
    this.documentationUrl = documentationUrl;
    this.patch = patch;
    this.bulk = bulk;
    this.filter = filter;
    this.changePassword = changePassword;
    this.sort = sort;
    this.etag = etag;
    this.authenticationSchemes = authenticationSchemes == null ? null :
        Collections.unmodifiableList(authenticationSchemes);
  }

  /**
   * Retrieves the HTTP addressable URL pointing to the service provider's
   * human consumable help documentation.
   *
   * @return The HTTP addressable URL pointing to the service provider's
   * human consumable help documentation.
   */
  public String getDocumentationUrl()
  {
    return documentationUrl;
  }

  /**
   * Retrieves the complex type that specifies PATCH configuration options.
   *
   * @return The complex type that specifies PATCH configuration options.
   */
  public Patch getPatch()
  {
    return patch;
  }

  /**
   * Retrieves the complex type that specifies Bulk configuration options.
   *
   * @return The complex type that specifies Bulk configuration options.
   */
  public Bulk getBulk()
  {
    return bulk;
  }

  /**
   * Retrieves the complex type that specifies FILTER options.
   *
   * @return The complex type that specifies FILTER options.
   */
  public Filter getFilter()
  {
    return filter;
  }

  /**
   * Retrieves the complex type that specifies Change Password configuration
   * options.
   *
   * @return The complex type that specifies Change Password configuration
   * options.
   */
  public ChangePassword getChangePassword()
  {
    return changePassword;
  }

  /**
   * Retrieves the complex type that specifies Sort configuration options.
   *
   * @return The complex type that specifies Sort configuration options.
   */
  public Sort getSort()
  {
    return sort;
  }

  /**
   * Retrieves the complex type that specifies Etag configuration options.
   *
   * @return The complex type that specifies Etag configuration options.
   */
  public ETag getEtag()
  {
    return etag;
  }

  /**
   * Retrieves the complex type that specifies supported Authentication Scheme
   * properties.
   *
   * @return The complex type that specifies supported Authentication Scheme
   * properties.
   */
  public List<AuthenticationScheme> getAuthenticationSchemes()
  {
    return authenticationSchemes;
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
    if (!super.equals(o))
    {
      return false;
    }

    ServiceProviderConfigResource that = (ServiceProviderConfigResource) o;

    if (authenticationSchemes != null ? !authenticationSchemes.equals(
        that.authenticationSchemes) : that.authenticationSchemes != null)
    {
      return false;
    }
    if (bulk != null ? !bulk.equals(that.bulk) : that.bulk != null)
    {
      return false;
    }
    if (changePassword != null ? !changePassword.equals(that.changePassword) :
        that.changePassword != null)
    {
      return false;
    }
    if (documentationUrl != null ? !documentationUrl.equals(
        that.documentationUrl) : that.documentationUrl != null)
    {
      return false;
    }
    if (etag != null ? !etag.equals(that.etag) : that.etag != null)
    {
      return false;
    }
    if (filter != null ? !filter.equals(that.filter) : that.filter != null)
    {
      return false;
    }
    if (patch != null ? !patch.equals(that.patch) : that.patch != null)
    {
      return false;
    }
    if (sort != null ? !sort.equals(that.sort) : that.sort != null)
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
    int result = super.hashCode();
    result = 31 * result + (documentationUrl != null ?
        documentationUrl.hashCode() : 0);
    result = 31 * result + (patch != null ? patch.hashCode() : 0);
    result = 31 * result + (bulk != null ? bulk.hashCode() : 0);
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (changePassword != null ?
        changePassword.hashCode() : 0);
    result = 31 * result + (sort != null ? sort.hashCode() : 0);
    result = 31 * result + (etag != null ? etag.hashCode() : 0);
    result = 31 * result + (authenticationSchemes != null ?
        authenticationSchemes.hashCode() : 0);
    return result;
  }
}
