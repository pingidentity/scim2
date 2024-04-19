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

package com.unboundid.scim2.client;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.unboundid.scim2.client.requests.CreateRequestBuilder;
import com.unboundid.scim2.client.requests.DeleteRequestBuilder;
import com.unboundid.scim2.client.requests.ModifyRequestBuilder;
import com.unboundid.scim2.client.requests.ReplaceRequestBuilder;
import com.unboundid.scim2.client.requests.RetrieveRequestBuilder;
import com.unboundid.scim2.client.requests.SearchRequestBuilder;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;
import com.unboundid.scim2.common.utils.JsonUtils;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import java.net.URI;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.common.utils.ApiConstants.ME_ENDPOINT;
import static com.unboundid.scim2.common.utils.ApiConstants.RESOURCE_TYPES_ENDPOINT;
import static com.unboundid.scim2.common.utils.ApiConstants.SCHEMAS_ENDPOINT;
import static com.unboundid.scim2.common.utils.ApiConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT;

/**
 * The main entry point to the client API used to access a SCIM 2 service
 * provider.
 */
public class ScimService implements ScimInterface
{
  /**
   * The authenticated subject alias.
   */
  @NotNull
  public static final URI ME_URI = URI.create(ME_ENDPOINT);

  /**
   * The SCIM media type.
   */
  @NotNull
  public static final MediaType MEDIA_TYPE_SCIM_TYPE =
      MediaType.valueOf(MEDIA_TYPE_SCIM);

  @NotNull
  private final WebTarget baseTarget;

  @Nullable
  private volatile ServiceProviderConfigResource serviceProviderConfig;

  /**
   * Create a new client instance to the SCIM 2 service provider at the
   * provided WebTarget. The path of the WebTarget should be the base URI
   * SCIM 2 service (i.e., {@code https://host/scim/v2}).
   *
   * @param baseTarget The web target for the base URI of the SCIM 2 service
   *                   provider.
   */
  public ScimService(@NotNull final WebTarget baseTarget)
  {
    this.baseTarget = baseTarget.register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper(),
            JacksonJsonProvider.BASIC_ANNOTATIONS));
  }

  /**
   * Retrieve the service provider configuration.
   *
   * @return the service provider configuration.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException
  {
    if(serviceProviderConfig == null)
    {
      serviceProviderConfig = retrieve(
          baseTarget.path(SERVICE_PROVIDER_CONFIG_ENDPOINT).getUri(),
          ServiceProviderConfigResource.class);
    }
    return serviceProviderConfig;
  }

  /**
   * Retrieve the resource types supported by the service provider.
   *
   * @return The list of resource types supported by the service provider.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public ListResponse<ResourceTypeResource> getResourceTypes()
      throws ScimException
  {
    return searchRequest(RESOURCE_TYPES_ENDPOINT).
        invoke(ResourceTypeResource.class);
  }

  /**
   * Retrieve a known resource type supported by the service provider.
   *
   * @param name The name of the resource type.
   * @return The resource type with the provided name.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public ResourceTypeResource getResourceType(@NotNull final String name)
      throws ScimException
  {
    return retrieve(RESOURCE_TYPES_ENDPOINT, name, ResourceTypeResource.class);
  }

  /**
   * Retrieve the schemas supported by the service provider.
   *
   * @return The list of schemas supported by the service provider.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public ListResponse<SchemaResource> getSchemas()
      throws ScimException
  {
    return searchRequest(SCHEMAS_ENDPOINT).invoke(SchemaResource.class);
  }

  /**
   * Retrieve a known schema supported by the service provider.
   *
   * @param id The schema URN.
   * @return The resource type with the provided URN.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public SchemaResource getSchema(@NotNull final String id)
      throws ScimException
  {
    return retrieve(SCHEMAS_ENDPOINT, id, SchemaResource.class);
  }

  /**
   * Create the provided new SCIM resource at the service provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "Groups" as
   *                 defined by the associated resource type.
   * @param resource The new resource to create.
   * @param <T> The Java type of the resource.
   * @return The successfully create SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public <T extends ScimResource> T create(@NotNull final String endpoint,
                                           @NotNull final T resource)
      throws ScimException
  {
    return createRequest(endpoint, resource).invoke();
  }

  /**
   * Retrieve a known SCIM resource from the service provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The Java type of the resource.
   * @return The successfully retrieved SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public <T extends ScimResource> T retrieve(@NotNull final String endpoint,
                                             @NotNull final String id,
                                             @NotNull final Class<T> cls)
      throws ScimException
  {
    return retrieveRequest(endpoint, id).invoke(cls);
  }

  /**
   * Retrieve a known SCIM resource from the service provider.
   *
   * @param url The URL of the resource to retrieve.
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The Java type of the resource.
   * @return The successfully retrieved SCIM resource.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public <T extends ScimResource> T retrieve(@NotNull final URI url,
                                             @NotNull final Class<T> cls)
      throws ScimException
  {
    return retrieveRequest(url).invoke(cls);
  }

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
  public <T extends ScimResource> T retrieve(@NotNull final T resource)
      throws ScimException
  {
    RetrieveRequestBuilder.Generic<T> builder = retrieveRequest(resource);
    return builder.invoke();
  }

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
  public <T extends ScimResource> T replace(@NotNull final T resource)
      throws ScimException
  {
    ReplaceRequestBuilder<T> builder = replaceRequest(resource);
    return builder.invoke();
  }

  /**
   * Delete a SCIM resource at the service provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @throws ScimException if an error occurs.
   */
  public void delete(@NotNull final String endpoint, @NotNull final String id)
      throws ScimException
  {
    deleteRequest(endpoint, id).invoke();
  }

  /**
   * Delete a SCIM resource at the service provider.
   *
   * @param url The URL of the resource to delete.
   * @throws ScimException if an error occurs.
   */
  public void delete(@NotNull final URI url)
      throws ScimException
  {
    deleteRequest(url).invoke();
  }

  /**
   * Delete a SCIM resource at the service provider.
   *
   * @param resource The resource to delete.
   * @param <T> The Java type of the resource.
   * @throws ScimException if an error occurs.
   */
  public <T extends ScimResource> void delete(@NotNull final T resource)
      throws ScimException
  {
    DeleteRequestBuilder builder = deleteRequest(resource);
    builder.invoke();
  }

  /**
   * Build a request to create the provided new SCIM resource at the service
   * provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @param resource The new resource to create.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public <T extends ScimResource> CreateRequestBuilder<T> createRequest(
      @NotNull final String endpoint,
      @NotNull final T resource)
  {
    return new CreateRequestBuilder<T>(baseTarget.path(endpoint), resource);
  }

  /**
   * Build a request to retrieve a known SCIM resource from the service
   * provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public RetrieveRequestBuilder.Typed retrieveRequest(
      @NotNull final String endpoint,
      @NotNull final String id)
  {
    return new RetrieveRequestBuilder.Typed(baseTarget.path(endpoint).path(id));
  }

  /**
   * Build a request to retrieve a known SCIM resource from the service
   * provider.
   *
   * @param url The URL of the resource to retrieve.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public RetrieveRequestBuilder.Typed retrieveRequest(@NotNull final URI url)
  {
    return new RetrieveRequestBuilder.Typed(resolveWebTarget(url));
  }

  /**
   * Build a request to retrieve a known SCIM resource from the service
   * provider.
   *
   * @param resource The resource to retrieve.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public <T extends ScimResource> RetrieveRequestBuilder.Generic<T>
      retrieveRequest(@NotNull final T resource)
  {
    return new RetrieveRequestBuilder.Generic<T>(
        resolveWebTarget(checkAndGetLocation(resource)), resource);
  }

  /**
   * Build a request to query and retrieve resources of a single resource type
   * from the service provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public SearchRequestBuilder searchRequest(@NotNull final String endpoint)
  {
    return new SearchRequestBuilder(baseTarget.path(endpoint));
  }

  /**
   * Build a request to modify a SCIM resource by replacing the resource's
   * attributes at the service provider.
   *
   * @param uri The URL of the resource to modify.
   * @param resource The resource to replace.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public <T extends ScimResource> ReplaceRequestBuilder<T> replaceRequest(
      @NotNull final URI uri,
      @NotNull final T resource)
  {
    return new ReplaceRequestBuilder<T>(resolveWebTarget(uri), resource);
  }

  /**
   * Build a request to modify a SCIM resource by replacing the resource's
   * attributes at the service provider.
   *
   * @param resource The previously retrieved and revised resource.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public <T extends ScimResource> ReplaceRequestBuilder<T> replaceRequest(
      @NotNull final T resource)
  {
    return new ReplaceRequestBuilder<T>(
        resolveWebTarget(checkAndGetLocation(resource)), resource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public <T extends ScimResource> T modify(
      @NotNull final String endpoint,
      @NotNull final String id,
      @NotNull final PatchRequest patchRequest,
      @NotNull final Class<T> clazz)
          throws ScimException
  {
    ModifyRequestBuilder.Typed requestBuilder = new ModifyRequestBuilder.Typed(
        baseTarget.path(endpoint).path(id));
    for(PatchOperation op : patchRequest.getOperations())
    {
      requestBuilder.addOperation(op);
    }
    return requestBuilder.invoke(clazz);
  }

  /**
   * Modify a SCIM resource by updating one or more attributes using a sequence
   * of operations to "{@code add}", "{@code remove}", or "{@code replace}"
   * values. The service provider configuration may be used to discover service
   * provider support for PATCH.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or
   *                 "{@code Groups}" as defined by the associated resource
   *                 type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public ModifyRequestBuilder.Typed modifyRequest(
      @NotNull final String endpoint,
      @NotNull final String id)
  {
    return new ModifyRequestBuilder.Typed(
        baseTarget.path(endpoint).path(id));
  }


  /**
   * Modify a SCIM resource by updating one or more attributes using a sequence
   * of operations to "{@code add}", "{@code remove}", or "{@code replace}"
   * values. The service provider configuration may be used to discover service
   * provider support for PATCH.
   *
   * @param url The URL of the resource to modify.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public  ModifyRequestBuilder.Typed modifyRequest(@NotNull final URI url)
  {
    return new ModifyRequestBuilder.Typed(resolveWebTarget(url));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public <T extends ScimResource> T modify(
      @NotNull final T resource,
      @NotNull final PatchRequest patchRequest)
          throws ScimException
  {
    ModifyRequestBuilder.Generic<T> requestBuilder =
        new ModifyRequestBuilder.Generic<T>(resolveWebTarget(
            checkAndGetLocation(resource)), resource);

    for(PatchOperation op : patchRequest.getOperations())
    {
      requestBuilder.addOperation(op);
    }
    return requestBuilder.invoke();
 }

  /**
   * Modify a SCIM resource by updating one or more attributes using a sequence
   * of operations to "{@code add}", "{@code remove}", or "{@code replace}"
   * values. The service provider configuration may be used to discover service
   * provider support for PATCH.
   *
   * @param resource The resource to modify.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  @NotNull
  public <T extends ScimResource> ModifyRequestBuilder.Generic<T> modifyRequest(
      @NotNull final T resource)
  {
    return new ModifyRequestBuilder.Generic<T>(
        resolveWebTarget(checkAndGetLocation(resource)), resource);
  }

  /**
   * Build a request to delete a SCIM resource at the service provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or
   *                 "{@code Groups}" as defined by the associated resource
   *                 type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public DeleteRequestBuilder deleteRequest(@NotNull final String endpoint,
                                            @NotNull final String id)
      throws ScimException
  {
    return new DeleteRequestBuilder(baseTarget.path(endpoint).path(id));
  }

  /**
   * Build a request to delete a SCIM resource at the service provider.
   *
   * @param url The URL of the resource to delete.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public DeleteRequestBuilder deleteRequest(@NotNull final URI url)
      throws ScimException
  {
    return new DeleteRequestBuilder(resolveWebTarget(url));
  }

  /**
   * Build a request to delete a SCIM resource at the service provider.
   *
   * @param resource The resource to delete.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public <T extends ScimResource> DeleteRequestBuilder deleteRequest(
      @NotNull final T resource)
          throws ScimException
  {
    return deleteRequest(checkAndGetLocation(resource));
  }

  /**
   * Resolve a URL (relative or absolute) to a web target.
   *
   * @param url The URL to resolve.
   * @return The WebTarget.
   */
  @NotNull
  private WebTarget resolveWebTarget(@NotNull final URI url)
  {
    URI relativePath;
    if(url.isAbsolute())
    {
      relativePath = baseTarget.getUri().relativize(url);
      if (relativePath.equals(url))
      {
        // The given resource's location is from another service provider
        throw new IllegalArgumentException("Given resource's location " +
            url + " is not under this service's " +
            "base path " + baseTarget.getUri());
      }
    }
    else
    {
      relativePath = url;
    }

    return baseTarget.path(relativePath.getRawPath());
  }

  /**
   * Get the meta.location attribute value of the SCIM resource.
   *
   * @param resource The SCIM resource.
   * @return The meta.location attribute value.
   * @throws IllegalArgumentException if the resource does not contain the
   * meta.location attribute value.
   */
  @NotNull
  private URI checkAndGetLocation(@NotNull final ScimResource resource)
      throws IllegalArgumentException
  {
    Meta meta = resource.getMeta();
    if(meta == null || meta.getLocation() == null)
    {
      throw new IllegalArgumentException(
          "Resource URI must be specified by meta.location");
    }
    return meta.getLocation();
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public <T extends ScimResource> ListResponse<T> search(
      @NotNull final String endpoint,
      @Nullable final String filter,
      @NotNull final Class<T> clazz)
          throws ScimException
  {
    return searchRequest(endpoint).filter(filter).invoke(clazz);
  }
}
