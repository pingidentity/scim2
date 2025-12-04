/*
 * Copyright 2025 Ping Identity Corporation
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
 * Copyright 2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for {@link ServiceProviderConfigResource}.
 */
public class ServiceProviderConfigTest
{
  private final ServiceProviderConfigResource serviceProviderConfig =
      new ServiceProviderConfigResource(
          "https://example.com/doc",
          new PatchConfig(true),
          new BulkConfig(true, 100, 1000),
          new FilterConfig(true, 200),
          new ChangePasswordConfig(true),
          new SortConfig(true),
          new ETagConfig(false),
          new PaginationConfig(true, false, "cursor", 200, 200, null),
          new AuthenticationScheme(
              "Basic", "HTTP BASIC", null, null, "httpbasic", true));

  /**
   * Basic validation for service provider configuration resources.
   */
  @Test
  public void testBasic()
  {
    assertThat(serviceProviderConfig.getDocumentationUri())
        .isEqualTo("https://example.com/doc");
    assertThat(serviceProviderConfig.getPatch().isSupported()).isTrue();

    BulkConfig bulkConfig = serviceProviderConfig.getBulk();
    assertThat(bulkConfig.isSupported()).isTrue();
    assertThat(bulkConfig.getMaxOperations()).isEqualTo(100);
    assertThat(bulkConfig.getMaxPayloadSize()).isEqualTo(1000);

    FilterConfig filterConfig = serviceProviderConfig.getFilter();
    assertThat(filterConfig.isSupported()).isTrue();
    assertThat(filterConfig.getMaxResults()).isEqualTo(200);

    assertThat(serviceProviderConfig.getChangePassword().isSupported())
        .isTrue();
    assertThat(serviceProviderConfig.getSort().isSupported()).isTrue();
    assertThat(serviceProviderConfig.getEtag().isSupported()).isFalse();

    PaginationConfig paginationConfig = serviceProviderConfig.getPagination();
    assertThat(paginationConfig).isNotNull();
    assertThat(paginationConfig.supportsCursorPagination()).isTrue();
    assertThat(paginationConfig.supportsIndexPagination()).isFalse();
    assertThat(paginationConfig.getDefaultPaginationMethod())
        .isEqualTo("cursor");
    assertThat(paginationConfig.getDefaultPageSize()).isEqualTo(200);
    assertThat(paginationConfig.getMaxPageSize()).isEqualTo(200);
    assertThat(paginationConfig.getCursorTimeout()).isNull();

    assertThat(serviceProviderConfig.getAuthenticationSchemes()).hasSize(1);
    AuthenticationScheme authConfig =
        serviceProviderConfig.getAuthenticationSchemes().get(0);
    assertThat(authConfig.getName()).isEqualTo("Basic");
    assertThat(authConfig.getDescription()).isEqualTo("HTTP BASIC");
    assertThat(authConfig.getSpecUri()).isNull();
    assertThat(authConfig.getDocumentationUri()).isNull();
    assertThat(authConfig.getType()).isEqualTo("httpbasic");
    assertThat(authConfig.isPrimary()).isTrue();
  }

  /**
   * Ensures that serialization and deserialization attain the expected forms.
   */
  @Test
  public void testSerialization() throws Exception
  {
    // The expected JSON for the "serviceProviderConfig" test variable.
    String json = """
        {
          "schemas": [
              "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig"
          ],
          "documentationUri": "https://example.com/doc",
          "patch": {
            "supported": true
          },
          "bulk": {
            "supported": true,
            "maxOperations": 100,
            "maxPayloadSize": 1000
          },
          "filter": {
            "supported": true,
            "maxResults": 200
          },
          "changePassword": {
            "supported": true
          },
          "sort": {
            "supported": true
          },
          "etag": {
            "supported": false
          },
          "pagination": {
            "cursor": true,
            "index": false,
            "defaultPaginationMethod": "cursor",
            "defaultPageSize": 200,
            "maxPageSize": 200
          },
          "authenticationSchemes": [ {
            "name": "Basic",
            "description": "HTTP BASIC",
            "type": "httpbasic",
            "primary": true
          } ]
        }""";

    // Reformat the JSON into a standardized form, and ensure the serialized
    // object matches the result.
    final String expectedJSON = JsonUtils.getObjectReader()
        .readTree(json).toString();
    String serialized = JsonUtils.getObjectWriter()
        .writeValueAsString(serviceProviderConfig);
    assertThat(serialized).isEqualTo(expectedJSON);

    // Ensure the deserialized value is considered equivalent to the original.
    ServiceProviderConfigResource deserialized = JsonUtils.getObjectReader()
        .forType(ServiceProviderConfigResource.class).readValue(json);
    assertThat(deserialized).isEqualTo(serviceProviderConfig);
    assertThat(deserialized.hashCode())
        .isEqualTo(serviceProviderConfig.hashCode());
  }

  /**
   * Ensures that all attributes defined on the ServiceProviderConfig resource
   * have a mutability of "read-only".
   */
  @Test
  public void testAttributesReadOnly() throws Exception
  {
    Collection<AttributeDefinition> spcSchema =
        SchemaUtils.getAttributes(ServiceProviderConfigResource.class);
    assertThat(spcSchema).allMatch(attr ->
      attr.getMutability() == AttributeDefinition.Mutability.READ_ONLY);
  }

  /**
   * Tests for {@code equals()}.
   */
  @SuppressWarnings("all")
  @Test
  public void testEquals()
  {
    assertThat(serviceProviderConfig).isEqualTo(serviceProviderConfig);
    assertThat(serviceProviderConfig).isNotEqualTo(null);
    assertThat(serviceProviderConfig).isNotEqualTo(new UserResource());

    var docValue = new ServiceProviderConfigResource(
        "https://example.com/newURL.txt",
        new PatchConfig(false),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(docValue);
    var patchValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(false),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(patchValue);
    var bulkValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(false, 0, 0),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(bulkValue);
    var filterValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(false, 0),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(filterValue);
    var changePasswordValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(false),
        new SortConfig(true),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(changePasswordValue);
    var sortValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(false),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(sortValue);
    var etagValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(true),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(etagValue);
    var paginationValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        null,
        new AuthenticationScheme(
            "Basic", "HTTP BASIC", null, null, "httpbasic", true));
    assertThat(serviceProviderConfig).isNotEqualTo(paginationValue);
    var authValue = new ServiceProviderConfigResource(
        "https://example.com/doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        new PaginationConfig(true, false, "cursor", 200, 200, null),
        new AuthenticationScheme(
            "OAuth 2.0", "OAuth 2.0", null, null, "OAuth 2.0", true));
    assertThat(serviceProviderConfig).isNotEqualTo(authValue);
  }
}
