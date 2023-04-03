/*
 * Copyright 2015-2023 Ping Identity Corporation
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
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.utils.JsonUtils;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A builder for SCIM modify requests.
 */
public abstract class ModifyRequestBuilder<T extends ModifyRequestBuilder<T>>
    extends ResourceReturningRequestBuilder<T>
{
  /**
   * The list of patch operations to include in the request.
   */
  protected final List<PatchOperation> operations;

  /**
   * The version to match.
   */
  protected String version;

  /**
   * Create a new ModifyRequestBuilder.
   *
   * @param target The WebTarget to PATCH.
   */
  private ModifyRequestBuilder(final WebTarget target)
  {
    super(target);
    this.operations = new LinkedList<PatchOperation>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  Invocation.Builder buildRequest()
  {
    Invocation.Builder request = super.buildRequest();
    if(version != null)
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
    private final T resource;

    /**
     * Create a new generic modify request builder.
     *
     * @param target The WebTarget to PATCH.
     * @param resource The SCIM resource to retrieve.
     */
    public Generic(final WebTarget target, final T resource)
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
    public Generic<T> ifMatch()
    {
      this.version = getResourceVersion(resource);
      return this;
    }

    /**
     * Invoke the SCIM modify request.
     *
     * @return The successfully modified SCIM resource.
     * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service provider responded with an error.
     */
    @SuppressWarnings("unchecked")
    public T invoke() throws ScimException
    {
      return (T) invoke(resource.getClass());
    }

    /**
     * Invoke the SCIM modify request.
     *
     * @param <C> The type of object to return.
     * @param cls The Java class object used to determine the type to return.
     * @return The successfully modified SCIM resource.
     * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service provider responded with an error.
     */
    public <C> C invoke(final Class<C> cls) throws ScimException
    {
      PatchRequest patchRequest = new PatchRequest(operations);
      Response response = buildRequest().method("PATCH",
          Entity.entity(patchRequest, getContentType()));
      try
      {
        if (response.getStatusInfo().getFamily() ==
            Response.Status.Family.SUCCESSFUL)
        {
          return response.readEntity(cls);
        } else
        {
          throw toScimException(response);
        }
      }
      finally
      {
        response.close();
      }
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
    public Typed(final WebTarget target)
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
    public Typed ifMatch(final String version)
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
     * @throws jakarta.ws.rs.ProcessingException If a JAX-RS runtime exception occurred.
     * @throws ScimException If the SCIM service provider responded with an error.
     */
    public <T> T invoke(final Class<T> cls) throws ScimException
    {
      PatchRequest patchRequest = new PatchRequest(operations);
      Response response = buildRequest().method("PATCH",
          Entity.entity(patchRequest, getContentType()));
      try
      {
        if(response.getStatusInfo().getFamily() ==
            Response.Status.Family.SUCCESSFUL)
        {
          return response.readEntity(cls);
        }
        else
        {
          throw toScimException(response);
        }
      }
      finally
      {
        response.close();
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
  public T replaceValue(final String path, final Object object)
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
  public T replaceValue(final Path path, final Object object)
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
  public T replaceValues(final String path, final Collection<Object> objects)
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
  public T replaceValues(final Path path, final Collection<Object> objects)
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
  public T replaceValues(final String path, final Object... objects)
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
  public T replaceValues(final Path path, final Object... objects)
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
  public T addValues(final String path, final Collection<?> objects)
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
  public T addValues(final Path path, final Collection<?> objects)
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
  public T addValues(final String path, final Object... objects)
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
  public T addValues(final Path path, final Object... objects)
  {
    JsonNode newObjectNode = JsonUtils.valueToNode(objects);
    return addOperation(PatchOperation.add(path, newObjectNode));
  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.

   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  public T removeValues(final String path)
      throws ScimException
  {
    return removeValues(Path.fromString(path));

  }

  /**
   * Remove all values of the attribute specified by the path.
   *
   * @param path The path to the attribute whose value to remove.

   * @return This patch operation request.
   * @throws ScimException If the path is invalid.
   */
  public T removeValues(final Path path)
      throws ScimException
  {
    return addOperation(PatchOperation.remove(path));
  }

  /**
   * Add a new patch operation this this patch request.
   *
   * @param op The patch operation to add.
   *
   * @return This patch operation request.
   */
  @SuppressWarnings("unchecked")
  public T addOperation(final PatchOperation op)
  {
    operations.add(op);
    return (T) this;
  }
}
