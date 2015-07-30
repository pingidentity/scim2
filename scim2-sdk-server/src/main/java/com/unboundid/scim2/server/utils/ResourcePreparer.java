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

package com.unboundid.scim2.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.DebugType;
import com.unboundid.scim2.common.utils.StaticUtils;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static com.unboundid.scim2.common.utils.ApiConstants.*;

/**
 * Utility to prepare a resource to return to the client. This includes:
 *
 * <ul>
 *   <li>
 *     Returning the attributes based on the returned constraint of the
 *     attribute definition in the schema.
 *   </li>
 *   <li>
 *     Returning the attributes requested by the client using the request
 *     resource as well as the attributes or excludedAttributes query parameter.
 *   </li>
 *   <li>
 *     Setting the meta.resourceType and meta.location attributes if not
 *     already set.
 *   </li>
 * </ul>
 */
public class ResourcePreparer<T extends ScimResource>
{
  private final ResourceTypeDefinition resourceType;
  private final URI baseUri;
  private final Set<Path> queryAttributes;
  private final boolean excluded;

  /**
   * Create a new ResourcePreparer for preparing returned resources for a
   * SCIM operation.
   *
   * @param resourceType The resource type definition for resources to prepare.
   * @param requestUriInfo The UriInfo for the request.
   * @throws BadRequestException If an attribute path specified by attributes
   * and excludedAttributes is invalid.
   */
  public ResourcePreparer(final ResourceTypeDefinition resourceType,
                          final UriInfo requestUriInfo)
      throws BadRequestException
  {
    String attributesString = null;
    String excludedAttributesString = null;

    // https://tools.ietf.org/html/draft-ietf-scim-api-19#section-4 says
    // query params should be ignored for these endpoints.
    if(!resourceType.getEndpoint().equals(SERVICE_PROVIDER_CONFIG_ENDPOINT) &&
        !resourceType.getEndpoint().equals(RESOURCE_TYPES_ENDPOINT) &&
        !resourceType.getEndpoint().equals(SCHEMAS_ENDPOINT))
    {
      attributesString = requestUriInfo.getQueryParameters().getFirst(
          QUERY_PARAMETER_ATTRIBUTES);
      excludedAttributesString = requestUriInfo.getQueryParameters().getFirst(
          QUERY_PARAMETER_EXCLUDED_ATTRIBUTES);
    }
    if(attributesString != null && !attributesString.isEmpty())
    {
      Set<String> attributeSet = StaticUtils.arrayToSet(
          StaticUtils.splitCommaSeperatedString(attributesString));
      this.queryAttributes = new LinkedHashSet<Path>(attributeSet.size());
      for(String attribute : attributeSet)
      {
        try
        {
          this.queryAttributes.add(Path.fromString(attribute));
        }
        catch (BadRequestException e)
        {
          throw BadRequestException.invalidValue("'" + attribute +
              "' is not a valid value for the attributes parameter: " +
              e.getMessage());
        }
      }
      this.excluded = false;
    }
    else if(excludedAttributesString != null &&
        !excludedAttributesString.isEmpty())
    {
      Set<String> attributeSet = StaticUtils.arrayToSet(
          StaticUtils.splitCommaSeperatedString(excludedAttributesString));
      this.queryAttributes = new LinkedHashSet<Path>(attributeSet.size());
      for(String attribute : attributeSet)
      {
        try
        {
          this.queryAttributes.add(Path.fromString(attribute));
        }
        catch (BadRequestException e)
        {
          throw BadRequestException.invalidValue("'" + attribute +
              "' is not a valid value for the excludedAttributes parameter: " +
              e.getMessage());
        }
      }
      this.excluded = true;
    }
    else
    {
      this.queryAttributes = Collections.emptySet();
      this.excluded = true;
    }
    this.resourceType = resourceType;
    this.baseUri = requestUriInfo.getBaseUriBuilder().
        path(resourceType.getEndpoint().getPath()).
        buildFromMap(requestUriInfo.getPathParameters());
  }

  /**
   * Private constructor used by unit-test.
   *
   * @param resourceType The resource type definition for resources to prepare.
   * @param baseUri The resource type base URI.
   * @param queryAttributes The attributes to return or exclude.
   * @param excluded Whether the queryAttributes should be excluded.
   */
  ResourcePreparer(final ResourceTypeDefinition resourceType,
                   final URI baseUri,
                   final Set<Path> queryAttributes,
                   final boolean excluded)
  {
    this.resourceType = resourceType;
    this.baseUri = baseUri;
    this.queryAttributes = queryAttributes;
    this.excluded = excluded;
  }

  /**
   * Trim attributes of the resources returned from a search or retrieve
   * operation based on schema and the request parameters.
   *
   * @param returnedResource The resource to return.
   * @return The trimmed resource ready to return to the client.
   */
  public GenericScimResource trimRetrievedResource(final T returnedResource)
  {
    return trimReturned(returnedResource, null, null);
  }

  /**
   * Trim attributes of the resources returned from a create operation based on
   * schema as well as the request resource and request parameters.
   *
   * @param returnedResource The resource to return.
   * @param requestResource The resource in the create request or
   *                        {@code null} if not available.
   * @return The trimmed resource ready to return to the client.
   */
  public GenericScimResource trimCreatedResource(final T returnedResource,
                                                 final T requestResource)
  {
    return trimReturned(returnedResource, requestResource, null);
  }

  /**
   * Trim attributes of the resources returned from a replace operation based on
   * schema as well as the request resource and request parameters.
   *
   * @param returnedResource The resource to return.
   * @param requestResource The resource in the replace request or
   *                        {@code null} if not available.
   * @return The trimmed resource ready to return to the client.
   */
  public GenericScimResource trimReplacedResource(final T returnedResource,
                                                  final T requestResource)
  {
    return trimReturned(returnedResource, requestResource, null);
  }

  /**
   * Trim attributes of the resources returned from a modify operation based on
   * schema as well as the patch request and request parameters.
   *
   * @param returnedResource The resource to return.
   * @param patchOperations The operations in the patch request or
   *                        {@code null} if not available.
   * @return The trimmed resource ready to return to the client.
   */
  public GenericScimResource trimModifiedResource(
      final T returnedResource, final Iterable<PatchOperation> patchOperations)
  {
    return trimReturned(returnedResource, null, patchOperations);
  }

  /**
   * Sets the meta.resourceType and meta.location metadata attribute values.
   *
   * @param returnedResource The resource to set the attributes.
   */
  public void setResourceTypeAndLocation(final T returnedResource)
  {
    Meta meta = returnedResource.getMeta();

    boolean metaUpdated = false;
    if(meta == null)
    {
      meta = new Meta();
    }

    if(meta.getResourceType() == null)
    {
      meta.setResourceType(resourceType.getName());
      metaUpdated = true;
    }

    if(meta.getLocation() == null)
    {
      String id = returnedResource.getId();
      if (id != null)
      {
        UriBuilder locationBuilder = UriBuilder.fromUri(baseUri);
        locationBuilder.path(id);
        meta.setLocation(locationBuilder.build());
      }
      else
      {
        meta.setLocation(baseUri);
      }
      metaUpdated = true;
    }

    if(metaUpdated)
    {
      returnedResource.setMeta(meta);
    }
  }

  /**
   * Trim attributes of the resources to return based on schema and the client
   * request.
   *
   * @param returnedResource The resource to return.
   * @param requestResource The resource in the PUT or POST request or
   *                        {@code null} for other requests.
   * @param patchOperations The patch operations in the PATCH request or
   *                        {@code null} for other requests.
   * @return The trimmed resource ready to return to the client.
   */
  @SuppressWarnings("unchecked")
  private GenericScimResource trimReturned(
      final T returnedResource, final T requestResource,
      final Iterable<PatchOperation> patchOperations)
  {
    Set<Path> requestAttributes = Collections.emptySet();
    if(requestResource != null)
    {
      ObjectNode requestObject =
          requestResource.asGenericScimResource().getObjectNode();
      requestAttributes = new LinkedHashSet<Path>();
      collectAttributes(Path.root(), requestAttributes, requestObject);
    }

    if(patchOperations != null)
    {
      requestAttributes = new LinkedHashSet<Path>();
      collectAttributes(requestAttributes, patchOperations);
    }

    ObjectNode returnedObject =
        returnedResource.asGenericScimResource().getObjectNode();
    GenericScimResource preparedResource =
        new GenericScimResource(
            trimObjectNode(returnedObject, requestAttributes, Path.root()));
    setResourceTypeAndLocation((T) preparedResource);
    return preparedResource;
  }

  /**
   * Trim attributes of the object node to return based on schema and the client
   * request.
   *
   * @param objectNode The object node to return.
   * @param requestAttributes The attributes in the request object or
   *                          {@code null} for other requests.
   * @return The trimmed object node ready to return to the client.
   */
  private ObjectNode trimObjectNode(final ObjectNode objectNode,
                                    final Set<Path> requestAttributes,
                                    final Path parentPath)
  {
    ObjectNode objectToReturn = JsonNodeFactory.instance.objectNode();
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      Path path = parentPath.attribute(field.getKey());
      if(shouldReturn(path, requestAttributes,
          queryAttributes, excluded))
      {
        if (field.getValue().isArray())
        {
          objectToReturn.set(field.getKey(), trimArrayNode(
              (ArrayNode) field.getValue(), requestAttributes, path));
        }
        else if (field.getValue().isObject())
        {
          objectToReturn.set(field.getKey(), trimObjectNode(
              (ObjectNode) field.getValue(), requestAttributes, path));
        }
        else
        {
          objectToReturn.set(field.getKey(), field.getValue());
        }
      }
    }
    return objectToReturn;
  }

  /**
   * Trim attributes of the values in the array node to return based on
   * schema and the client request.
   *
   * @param arrayNode The array node to return.
   * @param requestAttributes The attributes in the request object or
   *                          {@code null} for other requests.
   * @return The trimmed object node ready to return to the client.
   */
  private ArrayNode trimArrayNode(final ArrayNode arrayNode,
                                  final Set<Path> requestAttributes,
                                  final Path parentPath)
  {
    ArrayNode arrayToReturn = JsonNodeFactory.instance.arrayNode();
    for(JsonNode value : arrayNode)
    {
      if(value.isArray())
      {
        arrayToReturn.add(trimArrayNode((ArrayNode) value,
            requestAttributes, parentPath));
      }
      else if(value.isObject())
      {
        arrayToReturn.add(trimObjectNode((ObjectNode) value,
            requestAttributes, parentPath));
      }
      else
      {
        arrayToReturn.add(value);
      }
    }
    return arrayToReturn;
  }

  /**
   * Determine if the attribute specified by the path should be returned.
   *
   * @param path The path for the attribute.
   * @param requestAttributes The attributes in the request object or
   *                          {@code null} for other requests.
   * @param queryAttributes The attributes to return or exclude.
   * @param excluded Whether the queryAttributes should be excluded.
   * @return {@code true} to return the attribute or {@code false} to remove the
   * attribute from the returned resource..
   */
  private boolean shouldReturn(final Path path,
                               final Set<Path> requestAttributes,
                               final Set<Path> queryAttributes,
                               final boolean excluded)
  {
    AttributeDefinition attributeDefinition = null;
    try
    {
      attributeDefinition = resourceType == null ? null :
          resourceType.getAttributeDefinition(path);
    }
    catch (BadRequestException e)
    {
      Debug.debug(Level.WARNING, DebugType.EXCEPTION,
          "Error retrieving attribute definition for " + path.toString(), e);
    }
    AttributeDefinition.Returned returned = attributeDefinition == null ?
        AttributeDefinition.Returned.DEFAULT :
        attributeDefinition.getReturned();

    switch(returned)
    {
      case ALWAYS:
        return true;
      case NEVER:
        return false;
      case REQUEST:
        // Return only if it was one of the request attributes or if there are
        // no request attributes, then only if it was one of the override query
        // attributes.
        return requestAttributes.contains(path) ||
            (requestAttributes.isEmpty() && !excluded &&
                queryAttributes.contains(path));
      default:
        // Return if it is not one of the excluded query attributes and no
        // override query attributes are provided. If override query attributes
        // are provided, only return if it is one of them.
        if(excluded)
        {
          return !queryAttributes.contains(path);
        }
        else
        {
          return queryAttributes.isEmpty() || queryAttributes.contains(path);
        }
    }
  }

  /**
   * Collect a list of attributes in the object node.
   *
   * @param parentPath The parent path of attributes in the object.
   * @param paths The set of paths to add to.
   * @param objectNode The object node to collect from.
   */
  private void collectAttributes(final Path parentPath,
                                 final Set<Path> paths,
                                 final ObjectNode objectNode)
  {
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      Path path = parentPath.attribute(field.getKey());
      paths.add(path);
      if (field.getValue().isArray())
      {
        collectAttributes(path, paths, (ArrayNode) field.getValue());
      }
      else if (field.getValue().isObject())
      {
        collectAttributes(path, paths, (ObjectNode) field.getValue());
      }
    }
  }

  /**
   * Collect a list of attributes in the array node.
   *
   * @param parentPath The parent path of attributes in the array.
   * @param paths The set of paths to add to.
   * @param arrayNode The array node to collect from.
   */
  private void collectAttributes(final Path parentPath,
                                 final Set<Path> paths,
                                 final ArrayNode arrayNode)
  {
    for(JsonNode value : arrayNode)
    {
      if(value.isArray())
      {
        collectAttributes(parentPath, paths, (ArrayNode) value);
      }
      else if(value.isObject())
      {
        collectAttributes(parentPath, paths, (ObjectNode) value);
      }
    }
  }

  /**
   * Collect a list of attributes in the patch operation.
   *
   * @param paths The set of paths to add to.
   * @param patchOperations The patch operation to collect attributes from.
   */
  private void collectAttributes(
      final Set<Path> paths, final Iterable<PatchOperation> patchOperations)

  {
    for(PatchOperation patchOperation : patchOperations)
    {
      Path path = Path.root();
      if(patchOperation.getPath() != null)
      {
        path = patchOperation.getPath();
        paths.add(path);
      }
      if(patchOperation.getJsonNode() != null)
      {
        if(patchOperation.getJsonNode().isArray())
        {
          collectAttributes(
              path, paths, (ArrayNode) patchOperation.getJsonNode());
        }
        else if(patchOperation.getJsonNode().isObject())
        {
          collectAttributes(
              path, paths, (ObjectNode) patchOperation.getJsonNode());
        }
      }
    }
  }
}
