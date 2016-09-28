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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.Collections;
import java.util.List;

/**
 * SCIM provides a schema for representing the service provider's configuration
 * identified using the following schema URI:
 * "{@code urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig}"
 *
 * The Service Provider configuration resource enables a Service
 * Provider to discover SCIM specification features in a standardized
 * form as well as provide additional implementation details to clients.
 * All attributes have a mutability of "{@code readOnly}".  Unlike other core
 * resources, the "{@code id}" attribute is not required for the Service
 * Provider configuration resource.
 **/
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig",
    name="Service Provider Config",
    description = "SCIM 2.0 Service Provider Config Resource")
public class ServiceProviderConfigResource extends BaseScimResource
{
  @Attribute(description = "An HTTP addressable URI pointing to the " +
      "service provider's human consumable help documentation.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String documentationUri;

  @Attribute(description = "A complex type that specifies PATCH " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final PatchConfig patch;

  @Attribute(description = "A complex type that specifies Bulk " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final BulkConfig bulk;

  @Attribute(description = "A complex type that specifies FILTER options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final FilterConfig filter;

  @Attribute(description = "A complex type that specifies Change " +
      "Password configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final ChangePasswordConfig changePassword;

  @Attribute(description = "A complex type that specifies Sort " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final SortConfig sort;

  @Attribute(description = "A complex type that specifies Etag " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final ETagConfig etag;

  @Attribute(description = "A complex type that specifies supported " +
      "Authentication Scheme properties.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true,
      multiValueClass = AuthenticationScheme.class)
  private final List<AuthenticationScheme> authenticationSchemes;

  /**
   * Create a new ServiceProviderConfig.
   *
   * @param documentationUri An HTTP addressable URI pointing to the service
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
      @JsonProperty(value = "documentationUri") final String documentationUri,
      @JsonProperty(value = "patch", required = true) final PatchConfig patch,
      @JsonProperty(value = "bulk", required = true) final BulkConfig bulk,
      @JsonProperty(value = "filter", required = true)
      final FilterConfig filter,
      @JsonProperty(value = "changePassword", required = true)
      final ChangePasswordConfig changePassword,
      @JsonProperty(value = "sort", required = true) final SortConfig sort,
      @JsonProperty(value = "etag", required = true) final ETagConfig etag,
      @JsonProperty(value = "authenticationSchemes", required = true)
      final List<AuthenticationScheme> authenticationSchemes)
  {
    this.documentationUri = documentationUri;
    this.patch = patch;
    this.bulk = bulk;
    this.filter = filter;
    this.changePassword = changePassword;
    this.sort = sort;
    this.etag = etag;
    this.authenticationSchemes = authenticationSchemes == null ?
        Collections.<AuthenticationScheme>emptyList() :
        Collections.unmodifiableList(authenticationSchemes);
  }

  /**
   * Retrieves the HTTP addressable URI pointing to the service provider's
   * human consumable help documentation.
   *
   * @return The HTTP addressable URI pointing to the service provider's
   * human consumable help documentation.
   */
  public String getDocumentationUri()
  {
    return documentationUri;
  }

  /**
   * Retrieves the complex type that specifies PATCH configuration options.
   *
   * @return The complex type that specifies PATCH configuration options.
   */
  public PatchConfig getPatch()
  {
    return patch;
  }

  /**
   * Retrieves the complex type that specifies Bulk configuration options.
   *
   * @return The complex type that specifies Bulk configuration options.
   */
  public BulkConfig getBulk()
  {
    return bulk;
  }

  /**
   * Retrieves the complex type that specifies FILTER options.
   *
   * @return The complex type that specifies FILTER options.
   */
  public FilterConfig getFilter()
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
  public ChangePasswordConfig getChangePassword()
  {
    return changePassword;
  }

  /**
   * Retrieves the complex type that specifies Sort configuration options.
   *
   * @return The complex type that specifies Sort configuration options.
   */
  public SortConfig getSort()
  {
    return sort;
  }

  /**
   * Retrieves the complex type that specifies Etag configuration options.
   *
   * @return The complex type that specifies Etag configuration options.
   */
  public ETagConfig getEtag()
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
    if (documentationUri != null ? !documentationUri.equals(
        that.documentationUri) : that.documentationUri != null)
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
    result = 31 * result + (documentationUri != null ?
        documentationUri.hashCode() : 0);
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
