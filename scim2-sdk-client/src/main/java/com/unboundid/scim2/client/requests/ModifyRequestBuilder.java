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

package com.unboundid.scim2.client.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.utils.JsonUtils;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * A builder for SCIM PATCH requests.
 */
public abstract class ModifyRequestBuilder<T extends ModifyRequestBuilder<T>>
    extends ResourceReturningRequestBuilder<T>
{
  /**
   * The list of patch operations to include in the request.
   */
  @NotNull
  protected final List<PatchOperation> operations;

  /**
   * The version to match.
   */
  @Nullable
  protected String version;

  /**
   * Create a new ModifyRequestBuilder.
   *
   * @param target The WebTarget to PATCH.
   */
  private ModifyRequestBuilder(@NotNull final WebTarget target)
  {
    super(target);
    this.operations = new LinkedList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  protected Invocation.Builder buildRequest()
  {
    Invocation.Builder request = super.buildRequest();
    if (version != null)
    {
      request.header(HttpHeaders.IF_MATCH, version);
    }
    return request;
  }

  /**
   * A builder for SCIM modify requests for where the returned resource POJO
   * type will be the same as the original.
   */
  public static final class Generic<T extends ScimResource>
      extends ModifyRequestBuilder<Generic<T>>
  {
    @NotNull
    private final T resource;

    /**
     * Create a new generic modify request builder.
     *
     * @param target The WebTarget to PATCH.
     * @param resource The SCIM resource to retrieve.
     */
    public Generic(@NotNull final WebTarget target, @NotNull final T resource)
    {
      super(target);
      this.resource = resource;
    }


    /**
     * Modify the resource only if the resource has not been modified from the
     * resource provided.
     *
     * @return This builder.
     */
    @NotNull
    public Generic<T> ifMatch()
    {
      this.version = getResourceVersion(resource);
      return this;
    }

    /**
     * Invoke the SCIM PATCH request.
     *
     * @return The successfully modified SCIM resource.
     * @throws ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service responded with an error.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public T invoke() throws ScimException
    {
      return (T) invoke(resource.getClass());
    }

    /**
     * Invoke the SCIM PATCH request.
     *
     * @param <C> The type of object to return.
     * @param cls The Java class object used to determine the type to return.
     * @return The successfully modified SCIM resource.
     * @throws ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service responded with an error.
     */
    @NotNull
    public <C> C invoke(@NotNull final Class<C> cls) throws ScimException
    {
      return invokeInternal(cls);
    }
  }


  /**
   * A builder for SCIM modify requests for where the returned resource POJO
   * type will be provided.
   */
  public static final class Typed extends ModifyRequestBuilder<Typed>
  {
    /**
     * Create a new generic modify request builder.
     *
     * @param target The WebTarget to PATCH.
     */
    public Typed(@NotNull final WebTarget target)
    {
      super(target);
    }

    /**
     * Modify the resource only if the resource has not been modified since the
     * provided version.
     *
     * @param version The version of the resource to compare.
     * @return This builder.
     */
    @NotNull
    public Typed ifMatch(@Nullable final String version)
    {
      this.version = version;
      return this;
    }

    /**
     * Invoke the SCIM modify request.
     *
     * @param <T> The type of object to return.
     * @param cls The Java class object used to determine the type to return.
     * @return The successfully modified SCIM resource.
     * @throws ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service responded with an error.
     */
    @NotNull
    public <T> T invoke(@NotNull final Class<T> cls) throws ScimException
    {
      return invokeInternal(cls);
    }
  }

  /**
   * Core method for invoking a SCIM PATCH request and returning the response as
   * the provided class.
   *
   * @param <G> The Java tpe that should be returned.
   * @param cls The Java class object used to determine the type to return.
   * @return The successfully modified SCIM resource.
   * @throws ProcessingException If a JAX-RS runtime exception occurred.
   * @throws ScimException If the SCIM service responded with an error.
   */
  @NotNull
  protected <G> G invokeInternal(@NotNull final Class<G> cls)
      throws ScimException
  {
    PatchRequest patchRequest = new PatchRequest(operations);
    var entity = Entity.entity(generify(patchRequest), getContentType());
    try (Response response = buildRequest().method(HttpMethod.PATCH, entity))
    {
      if (response.getStatusInfo().getFamily() == SUCCESSFUL)
      {
        return response.readEntity(cls);
      }
      else
      {
        throw toScimException(response);
      }
    }
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T replaceValue(@NotNull final String path,
                        @Nullable final Object object)
      throws ScimException
  {
    return replaceValue(Path.fromString(path), object);
  }

  /**
   * Set value of the attribute specified by the path, replacing any existing
   * value(s).
   *
   * @param path The path to the attribute whose value to set.
   * @param object The value to set.
   *
   * @return This patch operation request.
   */
  @NotNull
  public T replaceValue(@NotNull final Path path, @Nullable final Object object)
  {
    JsonNode newObjectNode = JsonUtils.valueToNode(object);
    return addOperation(PatchOperation.replace(path, newObjectNode));
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T replaceValues(@NotNull final String path,
                         @Nullable final Collection<Object> objects)
      throws ScimException
  {
    return replaceValues(Path.fromString(path), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   */
  @NotNull
  public T replaceValues(@NotNull final Path path,
                         @Nullable final Collection<Object> objects)
  {
    JsonNode newObjectNode = JsonUtils.valueToNode(objects);
    return addOperation(PatchOperation.replace(path, newObjectNode));
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T replaceValues(@NotNull final String path,
                         @NotNull final Object... objects)
      throws ScimException
  {
    return replaceValues(Path.fromString(path), objects);
  }

  /**
   * Set values of the attribute specified by the path, replacing any existing
   * values.
   *
   * @param path The path to the attribute whose value to set.
   * @param objects The value(s) to set.
   *
   * @return This patch operation request.
   */
  @NotNull
  public T replaceValues(@NotNull final Path path,
                         @NotNull final Object... objects)
  {
    JsonNode newObjectNode = JsonUtils.valueToNode(objects);
    return addOperation(PatchOperation.replace(path, newObjectNode));
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T addValues(@NotNull final String path,
                     @NotNull final Collection<?> objects)
      throws ScimException
  {
    return addValues(Path.fromString(path), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   */
  @NotNull
  public T addValues(@NotNull final Path path,
                     @NotNull final Collection<?> objects)
  {
    JsonNode newObjectNode = JsonUtils.valueToNode(objects);
    return addOperation(PatchOperation.add(path, newObjectNode));
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T addValues(@NotNull final String path,
                     @NotNull final Object... objects)
      throws ScimException
  {
    return addValues(Path.fromString(path), objects);
  }

  /**
   * Add values to the multi-valued attribute specified by the path.
   *
   * @param path The path to the multi-valued attribute.
   * @param objects The values to add.
   *
   * @return This patch operation request.
   */
  @NotNull
  public T addValues(@NotNull final Path path,
                     @NotNull final Object... objects)
  {
    JsonNode newObjectNode = JsonUtils.valueToNode(objects);
    return addOperation(PatchOperation.add(path, newObjectNode));
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T removeValues(@NotNull final String path)
      throws ScimException
  {
    return removeValues(Path.fromString(path));
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.
   *
   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public T removeValues(@NotNull final Path path)
      throws ScimException
  {
    return addOperation(PatchOperation.remove(path));
  }

  /**
   * Add a new patch operation to this patch request.
   *
   * @param op The patch operation to add.
   *
   * @return This patch operation request.
   */
  @NotNull
  @SuppressWarnings("unchecked")
  public T addOperation(@NotNull final PatchOperation op)
  {
    operations.add(op);
    return (T) this;
  }
}
