/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.client;

import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;

/**
 * Interface providing a way to create, retrieve, update and delete
 * SCIM resources.
 */
public interface ScimInterface
{
  /**
   * Retrieve the service provider configuration.
   *
   * @return the service provider configuration.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException;

  /**
   * Retrieve the resource types supported by the service provider.
   *
   * @return The list of resource types supported by the service provider.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  ListResponse<ResourceTypeResource> getResourceTypes()
      throws ScimException;

  /**
   * Retrieve a known resource type supported by the service provider.
   *
   * @param name The name of the resource type.
   * @return The resource type with the provided name.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  ResourceTypeResource getResourceType(@NotNull String name)
      throws ScimException;

  /**
   * Retrieve the schemas supported by the service provider.
   *
   * @return The list of schemas supported by the service provider.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  ListResponse<SchemaResource> getSchemas()
      throws ScimException;

  /**
   * Retrieve a known schema supported by the service provider.
   *
   * @param id The schema URN.
   * @return The resource type with the provided URN.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  SchemaResource getSchema(@NotNull String id)
      throws ScimException;

  /**
   * Create the provided new SCIM resource at the service provider.
   *
   * @param endpoint The resource endpoint, such as {@code /Users} or
   *                 {@code /Groups}.
   * @param resource The new resource to create.
   * @param <T> The Java type of the resource.
   * @return The successfully create SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> T create(@NotNull String endpoint,
                                    @NotNull T resource)
      throws ScimException;

  /**
   * Retrieve a known SCIM resource from the service provider.
   *
   * @param endpoint The resource endpoint, such as {@code /Users} or
   *                 {@code /Groups}.
   * @param id       The resource identifier.
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The Java type of the resource.
   * @return The successfully retrieved SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> T retrieve(@NotNull String endpoint,
                                      @NotNull String id,
                                      @NotNull Class<T> cls)
      throws ScimException;

  /**
   * Retrieve a known SCIM resource from the service provider. If the
   * service provider supports resource versioning and the resource has not been
   * modified, the provided resource will be returned.
   *
   * @param resource The resource to retrieve.
   * @param <T> The Java type of the resource.
   * @return The successfully retrieved SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> T retrieve(@NotNull T resource)
      throws ScimException;

  /**
   * Modify a SCIM resource by replacing the resource's attributes at the
   * service provider. If the service provider supports resource versioning,
   * the resource will only be modified if it has not been modified since it
   * was retrieved.
   *
   * @param resource The previously retrieved and revised resource.
   * @param <T> The Java type of the resource.
   * @return The successfully replaced SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> T replace(@NotNull T resource)
      throws ScimException;

  /**
   * Modify a SCIM resource by updating one or more attributes using a sequence
   * of operations to {@code add}, {@code remove}, or {@code replace}
   * values. The service provider configuration may be used to discover service
   * provider support for PATCH.
   *
   * @param endpoint The resource endpoint, such as {@code /Users} or
   *                 {@code /Groups}.
   * @param id       The resource identifier.
   * @param patchRequest The patch request to use for the update.
   * @param clazz The class of the SCIM resource.
   * @param <T> The Java type of the resource.
   *
   * @return The modified resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> T modify(@NotNull String endpoint,
                                    @NotNull String id,
                                    @NotNull PatchRequest patchRequest,
                                    @NotNull Class<T> clazz)
      throws ScimException;

  /**
   * Modify a SCIM resource by updating one or more attributes using a sequence
   * of operations to {@code add}, {@code remove}, or {@code replace}
   * values. The service provider configuration may be used to discover service
   * provider support for PATCH.
   *
   * @param resource The resource to modify.
   * @param patchRequest the patch request to use for the update.
   * @param <T> The Java type of the resource.
   * @return The modified resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> T modify(@NotNull T resource,
                                    @NotNull PatchRequest patchRequest)
      throws ScimException;

  /**
   * Delete a SCIM resource at the service provider.
   *
   * @param endpoint The resource endpoint, such as {@code /Users} or
   *                 {@code /Groups}.
   * @param id       The resource identifier.
   * @throws ScimException if an error occurs.
   */
  void delete(@NotNull String endpoint, @NotNull String id)
      throws ScimException;

  /**
   * Delete a SCIM resource at the service provider.
   *
   * @param resource The resource to delete.
   * @param <T> The Java type of the resource.
   * @throws ScimException if an error occurs.
   */
  <T extends ScimResource> void delete(@NotNull T resource)
      throws ScimException;

  /**
   * Search for SCIM resources matching the SCIM filter provided.
   *
   * @param endpoint a SCIM resource type endpoint.
   * @param filter a SCIM filter string.
   * @param clazz the class representing the type of the SCIM resource.
   * @param <T> The SCIM resource type to return a list of.
   * @return a List of ScimResource objects matching the provided filter.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  <T extends ScimResource> ListResponse<T> search(@NotNull String endpoint,
                                                  @Nullable String filter,
                                                  @NotNull Class<T> clazz)
      throws ScimException;
}
