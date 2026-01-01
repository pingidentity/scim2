/*
 * Copyright 2015-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * This class represents a service provider's configuration.
 * <br><br>
 *
 * All SCIM services should have an endpoint similar to
 * {@code https://example.com/v2/ServiceProviderConfig}, which indicates
 * information about the behavior of the SCIM service and what it supports. An
 * example response is shown below:
 * <pre>
 * {
 *   "schemas": ["urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig"],
 *   "documentationUri": "https://example.com/help/scim.html",
 *   "patch": {
 *     "supported": true
 *   },
 *   "bulk": {
 *     "supported": true,
 *     "maxOperations": 1000,
 *     "maxPayloadSize": 1048576
 *   },
 *   "filter": {
 *     "supported": true,
 *     "maxResults": 200
 *   },
 *   "changePassword": {
 *     "supported": true
 *   },
 *   "sort": {
 *     "supported": true
 *   },
 *   "etag": {
 *     "supported": true
 *   },
 *   "pagination": {
 *       "cursor": true,
 *       "index": false
 *   },
 *   "authenticationSchemes": [
 *     {
 *       "name": "OAuth Bearer Token",
 *       "description": "Authentication scheme using the OAuth Standard",
 *       "specUri": "https://datatracker.ietf.org/doc/html/rfc6750",
 *       "documentationUri": "https://example.com/help/oauth.html",
 *       "type": "oauthbearertoken",
 *       "primary": true
 *     }
 *   ],
 *   "meta": {
 *     "location": "https://example.com/v2/ServiceProviderConfig",
 *     "resourceType": "ServiceProviderConfig",
 *     "created": "2015-09-25T00:00:00Z",
 *     "lastModified": "2025-10-09T00:00:00Z"
 *   }
 * }
 * </pre>
 *
 * The above JSON response indicates that this SCIM service:
 * <ul>
 *   <li> Supports SCIM PATCH requests.
 *   <li> Supports SCIM bulk requests with up to 1000 operations in a request.
 *   <li> Supports SCIM filtering and will return a maximum of 200 results.
 *   <li> Supports password change API requests.
 *   <li> Supports sorting the result set when multiple resources are returned.
 *   <li> Supports ETag versioning. For more details, see {@link ETagConfig}.
 *   <li> Supports paging through results with cursors, but not by page numbers.
 *   <li> Supports only OAuth 2.0 bearer tokens for authenticating clients.
 * </ul>
 * <br><br>
 *
 * This endpoint provides a summary for the SCIM service's behavior. All
 * attributes on this resource type have a mutability of {@code readOnly}.
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig",
    name="Service Provider Config",
    description = "SCIM 2.0 Service Provider Config Resource")
public class ServiceProviderConfigResource extends BaseScimResource
{
  @Nullable
  @Attribute(description = "An HTTP addressable URI pointing to the " +
      "service provider's human consumable help documentation.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String documentationUri;

  @NotNull
  @Attribute(description = "A complex type that specifies PATCH " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final PatchConfig patch;

  @NotNull
  @Attribute(description = "A complex type that specifies Bulk " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final BulkConfig bulk;

  @NotNull
  @Attribute(description = "A complex type that specifies FILTER options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final FilterConfig filter;

  @NotNull
  @Attribute(description = "A complex type that specifies Change " +
      "Password configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final ChangePasswordConfig changePassword;

  @NotNull
  @Attribute(description = "A complex type that specifies Sort " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final SortConfig sort;

  @NotNull
  @Attribute(description = "A complex type that specifies Etag " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final ETagConfig etag;

  @Nullable
  @Attribute(description = "A complex type that specifies pagination " +
      "configuration options.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final PaginationConfig pagination;

  @NotNull
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
   * @param etag A complex type that specifies ETag configuration options.
   * @param pagination A complex type that specifies pagination configuration
   *                   options.
   * @param authenticationSchemes A complex type that specifies supported
   *                              Authentication Scheme properties.
   *
   * @since 5.0.0
   */
  @JsonCreator
  public ServiceProviderConfigResource(
      @Nullable @JsonProperty(value = "documentationUri")
      final String documentationUri,
      @NotNull @JsonProperty(value = "patch", required = true)
      final PatchConfig patch,
      @NotNull @JsonProperty(value = "bulk", required = true)
      final BulkConfig bulk,
      @NotNull @JsonProperty(value = "filter", required = true)
      final FilterConfig filter,
      @NotNull @JsonProperty(value = "changePassword", required = true)
      final ChangePasswordConfig changePassword,
      @NotNull @JsonProperty(value = "sort", required = true)
      final SortConfig sort,
      @NotNull @JsonProperty(value = "etag", required = true)
      final ETagConfig etag,
      @Nullable @JsonProperty(value = "pagination")
      final PaginationConfig pagination,
      @NotNull @JsonProperty(value = "authenticationSchemes", required = true)
      final List<AuthenticationScheme> authenticationSchemes)
  {
    this.documentationUri = documentationUri;
    this.patch = patch;
    this.bulk = bulk;
    this.filter = filter;
    this.changePassword = changePassword;
    this.sort = sort;
    this.etag = etag;
    this.pagination = pagination;
    this.authenticationSchemes = authenticationSchemes == null ? null :
        Collections.unmodifiableList(authenticationSchemes);
  }

  /**
   * Alternate constructor that allows specifying authentication schemes
   * directly.
   *
   * @param documentationUri  An HTTP addressable URI pointing to the service
   *                          provider's human consumable help documentation.
   * @param patch             A complex type indicating PATCH configuration
   *                          options.
   * @param bulk              A complex type indicating Bulk configuration
   *                          options.
   * @param filter            A complex type indicating filter options.
   * @param changePassword    A complex type indicating password changing
   *                          configuration options.
   * @param sort              A complex type indicating Sort configuration
   *                          options.
   * @param etag              A complex type indicating ETag configuration
   *                          options.
   * @param pagination        A complex type indicating pagination configuration
   *                          options.
   * @param authenticationSchemes  A complex type indicating supported
   *                               Authentication Scheme properties.
   *
   * @since 5.0.0
   */
  public ServiceProviderConfigResource(
      @Nullable final String documentationUri,
      @NotNull final PatchConfig patch,
      @NotNull final BulkConfig bulk,
      @NotNull final FilterConfig filter,
      @NotNull final ChangePasswordConfig changePassword,
      @NotNull final SortConfig sort,
      @NotNull final ETagConfig etag,
      @Nullable final PaginationConfig pagination,
      @NotNull final AuthenticationScheme... authenticationSchemes)
  {
    this(documentationUri, patch, bulk, filter, changePassword, sort, etag,
        pagination, List.of(authenticationSchemes));
  }

  /**
   * Create a new ServiceProviderConfig resource. This constructor primarily
   * exists for backward compatibility, and using the primary constructor
   * ({@link #ServiceProviderConfigResource(String, PatchConfig, BulkConfig,
   *          FilterConfig, ChangePasswordConfig, SortConfig, ETagConfig,
   *          PaginationConfig, List) ServiceProviderConfigResource()}
   * )
   * is encouraged. The primary constructor supports information regarding
   * pagination.
   *
   * @param documentationUri An HTTP addressable URI pointing to the service
   *                         provider's human consumable help documentation.
   * @param patch A complex type that specifies PATCH configuration options.
   * @param bulk A complex type that specifies Bulk configuration options.
   * @param filter A complex type that specifies FILTER options.
   * @param changePassword A complex type that specifies Change Password
   *                       configuration options.
   * @param sort A complex type that specifies Sort configuration options.
   * @param etag A complex type that specifies ETag configuration options.
   * @param authenticationSchemes A complex type that specifies supported
   *                              Authentication Scheme properties.
   */
  public ServiceProviderConfigResource(
      @Nullable final String documentationUri,
      @NotNull final PatchConfig patch,
      @NotNull final BulkConfig bulk,
      @NotNull final FilterConfig filter,
      @NotNull final ChangePasswordConfig changePassword,
      @NotNull final SortConfig sort,
      @NotNull final ETagConfig etag,
      @NotNull final List<AuthenticationScheme> authenticationSchemes)
  {
    this(documentationUri, patch, bulk, filter, changePassword, sort, etag,
        null, authenticationSchemes);
  }

  /**
   * Retrieves the HTTP addressable URI pointing to the service provider's
   * human consumable help documentation.
   *
   * @return The HTTP addressable URI pointing to the service provider's
   * human consumable help documentation.
   */
  @Nullable
  public String getDocumentationUri()
  {
    return documentationUri;
  }

  /**
   * Retrieves the complex type that specifies PATCH configuration options.
   *
   * @return The complex type that specifies PATCH configuration options.
   */
  @NotNull
  public PatchConfig getPatch()
  {
    return patch;
  }

  /**
   * Retrieves the complex type that specifies Bulk configuration options.
   *
   * @return The complex type that specifies Bulk configuration options.
   */
  @NotNull
  public BulkConfig getBulk()
  {
    return bulk;
  }

  /**
   * Retrieves the complex type that specifies FILTER options.
   *
   * @return The complex type that specifies FILTER options.
   */
  @NotNull
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
  @NotNull
  public ChangePasswordConfig getChangePassword()
  {
    return changePassword;
  }

  /**
   * Retrieves the complex type that specifies Sort configuration options.
   *
   * @return The complex type that specifies Sort configuration options.
   */
  @NotNull
  public SortConfig getSort()
  {
    return sort;
  }

  /**
   * Retrieves the complex type that specifies ETag configuration options.
   *
   * @return The complex type that specifies ETag configuration options.
   */
  @NotNull
  public ETagConfig getEtag()
  {
    return etag;
  }

  /**
   * Retrieves the complex type that specifies pagination configuration options.
   * This may be {@code null} for SCIM services that do not explicitly support
   * <a href="https://datatracker.ietf.org/doc/html/rfc9865">RFC 9865</a>.
   *
   * @return The complex type that specifies pagination configuration options.
   */
  @Nullable
  public PaginationConfig getPagination()
  {
    return pagination;
  }

  /**
   * Retrieves the complex type that specifies supported Authentication Scheme
   * properties.
   *
   * @return The complex type that specifies supported Authentication Scheme
   * properties.
   */
  @NotNull
  public List<AuthenticationScheme> getAuthenticationSchemes()
  {
    return authenticationSchemes;
  }

  /**
   * Indicates whether the provided object is equal to this service provider
   * configuration definition.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this
   *            ServiceProviderConfig, or {@code false} if not.
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
    if (!super.equals(o))
    {
      return false;
    }

    ServiceProviderConfigResource that = (ServiceProviderConfigResource) o;
    if (!Objects.equals(authenticationSchemes, that.authenticationSchemes))
    {
      return false;
    }
    if (!Objects.equals(bulk, that.bulk))
    {
      return false;
    }
    if (!Objects.equals(changePassword, that.changePassword))
    {
      return false;
    }
    if (!Objects.equals(documentationUri, that.documentationUri))
    {
      return false;
    }
    if (!Objects.equals(etag, that.etag))
    {
      return false;
    }
    if (!Objects.equals(filter, that.filter))
    {
      return false;
    }
    if (!Objects.equals(patch, that.patch))
    {
      return false;
    }
    if (!Objects.equals(pagination, that.pagination))
    {
      return false;
    }
    return Objects.equals(sort, that.sort);
  }

  /**
   * Retrieves a hash code for this service provider configuration definition.
   *
   * @return  A hash code for this service provider configuration definition.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), documentationUri, patch, bulk, filter,
        changePassword, sort, etag, pagination, authenticationSchemes);
  }
}
