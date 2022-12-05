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

package com.unboundid.scim2.client;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.unboundid.scim2.client.requests.CreateRequestBuilder;
import com.unboundid.scim2.client.requests.DeleteRequestBuilder;
import com.unboundid.scim2.client.requests.ModifyRequestBuilder;
import com.unboundid.scim2.client.requests.ReplaceRequestBuilder;
import com.unboundid.scim2.client.requests.RetrieveRequestBuilder;
import com.unboundid.scim2.client.requests.SearchRequestBuilder;
import com.unboundid.scim2.common.ScimResource;
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
import static com.unboundid.scim2.common.utils.ApiConstants.
    RESOURCE_TYPES_ENDPOINT;
import static com.unboundid.scim2.common.utils.ApiConstants.SCHEMAS_ENDPOINT;
import static com.unboundid.scim2.common.utils.ApiConstants.
    SERVICE_PROVIDER_CONFIG_ENDPOINT;

/**
 * The main entry point to the client API used to access a SCIM 2 service
 * provider.
 */
public class ScimService implements ScimInterface
{
  /**
   * The authenticated subject alias.
   */
  public static final URI ME_URI = URI.create(ME_ENDPOINT);

  /**
   * The SCIM media type.
   */
  public static final MediaType MEDIA_TYPE_SCIM_TYPE =
      MediaType.valueOf(MEDIA_TYPE_SCIM);

  private final WebTarget baseTarget;
  private volatile ServiceProviderConfigResource serviceProviderConfig;

  /**
   * Create a new client instance to the SCIM 2 service provider at the
   * provided WebTarget. The path of the WebTarget should be the base URI
   * SCIM 2 service (ie. http://host/scim/v2).
   *
   * @param baseTarget The web target for the base URI of the SCIM 2 service
   *                   provider.
   */
  public ScimService(final WebTarget baseTarget)
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
  public ResourceTypeResource getResourceType(final String name)
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
  public SchemaResource getSchema(final String id)
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
  public <T extends ScimResource> T create(
      final String endpoint, final T resource) throws ScimException
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
  public <T extends ScimResource> T retrieve(
      final String endpoint, final String id, final Class<T> cls)
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
  public <T extends ScimResource> T retrieve(final URI url, final Class<T> cls)
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
  public <T extends ScimResource> T retrieve(final T resource)
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
  public <T extends ScimResource> T replace(
      final T resource) throws ScimException
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
  public void delete(final String endpoint, final String id)
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
  public void delete(final URI url) throws ScimException
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
  public <T extends ScimResource> void delete(final T resource)
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
  public <T extends ScimResource> CreateRequestBuilder<T> createRequest(
      final String endpoint, final T resource)
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
  public RetrieveRequestBuilder.Typed retrieveRequest(
      final String endpoint, final String id)
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
  public RetrieveRequestBuilder.Typed retrieveRequest(final URI url)
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
  public <T extends ScimResource> RetrieveRequestBuilder.Generic<T>
      retrieveRequest(final T resource)
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
  public SearchRequestBuilder searchRequest(final String endpoint)
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
  public <T extends ScimResource> ReplaceRequestBuilder<T> replaceRequest(
      final URI uri, final T resource)
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
  public <T extends ScimResource> ReplaceRequestBuilder<T> replaceRequest(
      final T resource)
  {
    return new ReplaceRequestBuilder<T>(
        resolveWebTarget(checkAndGetLocation(resource)), resource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends ScimResource> T modify(final String endpoint,
      final String id, final PatchRequest patchRequest, final Class<T> clazz)
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
   * of operations to "{@code add}", "{@code remove}", or "{@code replace}" values. The service provider
   * configuration maybe used to discover service provider support for PATCH.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  public ModifyRequestBuilder.Typed modifyRequest(
      final String endpoint, final String id)
  {
    return new ModifyRequestBuilder.Typed(
        baseTarget.path(endpoint).path(id));
  }


  /**
   * Modify a SCIM resource by updating one or more attributes using a sequence
   * of operations to "{@code add}", "{@code remove}", or "{@code replace}" values. The service provider
   * configuration maybe used to discover service provider support for PATCH.
   *
   * @param url The URL of the resource to modify.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  public  ModifyRequestBuilder.Typed modifyRequest(
      final URI url)
  {
    return new ModifyRequestBuilder.Typed(resolveWebTarget(url));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends ScimResource> T modify(
      final T resource, final PatchRequest patchRequest) throws ScimException
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
   * of operations to "{@code add}", "{@code remove}", or "{@code replace}" values. The service provider
   * configuration maybe used to discover service provider support for PATCH.
   *
   * @param resource The resource to modify.
   * @param <T> The Java type of the resource.
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   */
  public <T extends ScimResource> ModifyRequestBuilder.Generic<T> modifyRequest(
      final T resource)
  {
    return new ModifyRequestBuilder.Generic<T>(
        resolveWebTarget(checkAndGetLocation(resource)), resource);
  }

  /**
   * Build a request to delete a SCIM resource at the service provider.
   *
   * @param endpoint The resource endpoint such as: "{@code Users}" or "{@code Groups}" as
   *                 defined by the associated resource type.
   * @param id The resource identifier (for example the value of the "{@code id}"
   *           attribute).
   * @return The request builder that may be used to specify additional request
   * parameters and to invoke the request.
   * @throws ScimException if an error occurs.
   */
  public DeleteRequestBuilder deleteRequest(
      final String endpoint, final String id)
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
  public DeleteRequestBuilder deleteRequest(final URI url) throws ScimException
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
  public <T extends ScimResource> DeleteRequestBuilder deleteRequest(
      final T resource)
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
  private WebTarget resolveWebTarget(final URI url)
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
  private URI checkAndGetLocation(final ScimResource resource)
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
  public <T extends ScimResource> ListResponse<T> search(final String endpoint,
      final String filter, final Class<T> clazz) throws ScimException
  {
    return searchRequest(endpoint).filter(filter).invoke(clazz);
  }
}
