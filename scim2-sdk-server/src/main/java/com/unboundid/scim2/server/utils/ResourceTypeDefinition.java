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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.server.annotations.ResourceType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Declaration of a resource type including all schemas.
 */
public final class ResourceTypeDefinition
{
  private final String id;
  private final String name;
  private final String description;
  private final String endpoint;
  private final SchemaResource coreSchema;
  private final Map<SchemaResource, Boolean> schemaExtensions;
  private final Map<Path, AttributeDefinition> attributeNotationMap;
  private final boolean discoverable;

  /**
   * Builder for creating a ResourceTypeDefinition.
   */
  public static class Builder
  {
    private final String name;
    private final String endpoint;
    private String id;
    private String description;
    private SchemaResource coreSchema;
    private Set<SchemaResource> requiredSchemaExtensions =
        new HashSet<SchemaResource>();
    private Set<SchemaResource> optionalSchemaExtensions =
        new HashSet<SchemaResource>();
    private boolean discoverable = true;

    /**
     * Create a new builder.
     *
     * @param name The name of the resource type.
     * @param endpoint The endpoint of the resource type.
     */
    public Builder(final String name, final String endpoint)
    {
      if(name == null)
      {
        throw new IllegalArgumentException("name must not be null");
      }
      if(endpoint == null)
      {
        throw new IllegalArgumentException("endpoint must not be null");
      }
      this.name = name;
      this.endpoint = endpoint;
    }

    /**
     * Sets the ID of the resource type.
     *
     * @param id the ID of the resource type.
     * @return this builder.
     */
    public Builder setId(final String id)
    {
      this.id = id;
      return this;
    }

    /**
     * Sets the description of the resource type.
     *
     * @param description the description of the resource type.
     * @return this builder.
     */
    public Builder setDescription(final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets the core schema of the resource type.
     *
     * @param coreSchema the core schema of the resource type.
     * @return this builder.
     */
    public Builder setCoreSchema(final SchemaResource coreSchema)
    {
      this.coreSchema = coreSchema;
      return this;
    }

    /**
     * Adds a required schema extension for a resource type.
     *
     * @param schemaExtension the required schema extension for the resource
     *                        type.
     * @return this builder.
     */
    public Builder addRequiredSchemaExtension(
        final SchemaResource schemaExtension)
    {
      this.requiredSchemaExtensions.add(schemaExtension);
      return this;
    }

    /**
     * Adds a operation schema extension for a resource type.
     *
     * @param schemaExtension the operation schema extension for the resource
     *                        type.
     * @return this builder.
     */
    public Builder addOptionalSchemaExtension(
        final SchemaResource schemaExtension)
    {
      this.optionalSchemaExtensions.add(schemaExtension);
      return this;
    }

    /**
     * Sets whether this resource type is discoverable over the /ResourceTypes
     * endpoint.
     *
     * @param discoverable {@code true} this resource type is discoverable over
     *                     the /ResourceTypes endpoint or {@code false}
     *                     otherwise.
     * @return this builder.
     */
    public Builder setDiscoverable(
        final boolean discoverable)
    {
      this.discoverable = discoverable;
      return this;
    }

    /**
     * Build the ResourceTypeDefinition.
     *
     * @return The newly created ResourceTypeDefinition.
     */
    public ResourceTypeDefinition build()
    {
      Map<SchemaResource, Boolean> schemaExtensions =
          new HashMap<SchemaResource, Boolean>(requiredSchemaExtensions.size() +
              optionalSchemaExtensions.size());
      for(SchemaResource schema : requiredSchemaExtensions)
      {
        schemaExtensions.put(schema, true);
      }
      for(SchemaResource schema : optionalSchemaExtensions)
      {
        schemaExtensions.put(schema, false);
      }
      return new ResourceTypeDefinition(id, name, description, endpoint,
          coreSchema, schemaExtensions, discoverable);
    }
  }

  /**
   * Create a new ResourceType.
   *
   * @param coreSchema The core schema for the resource type.
   * @param schemaExtensions A map of schema extensions to whether it is
   *                         required for the resource type.
   */
  private ResourceTypeDefinition(
      final String id, final String name, final String description,
      final String endpoint,
      final SchemaResource coreSchema,
      final Map<SchemaResource, Boolean> schemaExtensions,
      final boolean discoverable)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.endpoint = endpoint;
    this.coreSchema = coreSchema;
    this.schemaExtensions = Collections.unmodifiableMap(schemaExtensions);
    this.discoverable = discoverable;
    this.attributeNotationMap = new HashMap<Path, AttributeDefinition>();

    // Add the common attributes
    buildAttributeNotationMap(Path.root(),
        SchemaUtils.COMMON_ATTRIBUTE_DEFINITIONS);

    // Add the core attributes
    if(coreSchema != null)
    {
      buildAttributeNotationMap(Path.root(), coreSchema.getAttributes());
    }

    // Add the extension attributes
    for(SchemaResource schemaExtension : schemaExtensions.keySet())
    {
      buildAttributeNotationMap(Path.root(schemaExtension.getId()),
          schemaExtension.getAttributes());
    }
  }

  private void buildAttributeNotationMap(
      final Path parentPath,
      final Collection<AttributeDefinition> attributes)
  {
    for(AttributeDefinition attribute : attributes)
    {
      Path path = parentPath.attribute(attribute.getName());
      attributeNotationMap.put(path, attribute);
      if(attribute.getSubAttributes() != null)
      {
        buildAttributeNotationMap(path, attribute.getSubAttributes());
      }
    }
  }

  /**
   * Gets the resource type name.
   *
   * @return the name of the resource type.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of the resource type.
   *
   * @return the description of the resource type.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the resource type's endpoint.
   *
   * @return the endpoint for the resource type.
   */
  public String getEndpoint()
  {
    return endpoint;
  }

  /**
   * Gets the resource type's schema.
   *
   * @return the schema for the resource type.
   */
  public SchemaResource getCoreSchema()
  {
    return coreSchema;
  }

  /**
   * Gets the resource type's schema extensions.
   *
   * @return the schema extensions for the resource type.
   */
  public Map<SchemaResource, Boolean> getSchemaExtensions()
  {
    return schemaExtensions;
  }

  /**
   * Whether this resource type and its associated schemas should be
   * discoverable using the SCIM 2 standard /resourceTypes and /schemas
   * endpoints.
   *
   * @return {@code true} if discoverable or {@code false} otherwise.
   */
  public boolean isDiscoverable()
  {
    return discoverable;
  }

  /**
   * Retrieve the attribute definition for the attribute in the path.
   *
   * @param path The attribute path.
   * @return The attribute definition or {@code null} if there is no attribute
   * defined for the path.
   */
  public AttributeDefinition getAttributeDefinition(final Path path)
  {
    return attributeNotationMap.get(normalizePath(path).withoutFilters());
  }

  /**
   * Normalize a path by removing the schema URN for core attributes.
   *
   * @param path The path to normalize.
   * @return The normalized path.
   */
  public Path normalizePath(final Path path)
  {
    if(path.getSchemaUrn() != null && coreSchema != null &&
        path.getSchemaUrn().equalsIgnoreCase(coreSchema.getId()))
    {
      return Path.root().attribute(path);
    }
    return path;
  }

  /**
   * Retrieve the ResourceType SCIM resource that represents this definition.
   *
   * @return The ResourceType SCIM resource that represents this definition.
   */
  public ResourceTypeResource toScimResource()
  {
    try
    {
      URI coreSchemaUri = null;
      if(coreSchema != null)
      {
        coreSchemaUri = new URI(coreSchema.getId());
      }
      List<ResourceTypeResource.SchemaExtension> schemaExtensionList = null;
      if (schemaExtensions.size() > 0)
      {
        schemaExtensionList =
            new ArrayList<ResourceTypeResource.SchemaExtension>(
                schemaExtensions.size());

        for(Map.Entry<SchemaResource, Boolean> schemaExtension :
            schemaExtensions.entrySet())
        {
          schemaExtensionList.add(new ResourceTypeResource.SchemaExtension(
              URI.create(schemaExtension.getKey().getId()),
              schemaExtension.getValue()));
        }
      }

      return new ResourceTypeResource(id == null ? name : id, name, description,
          URI.create(endpoint), coreSchemaUri, schemaExtensionList);
    }
    catch(URISyntaxException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create a new instance representing the resource type implemented by a
   * root JAX-RS resource class.
   *
   * @param resource a root resource whose
   *                 {@link com.unboundid.scim2.server.annotations.ResourceType}
   *                 and {@link jakarta.ws.rs.Path} values will be used to
   *                 initialize the ResourceTypeDefinition.
   * @return a new ResourceTypeDefinition or {@code null} if resource is not
   * annotated with {@link com.unboundid.scim2.server.annotations.ResourceType}
   * and {@link jakarta.ws.rs.Path}.
   */
  public static ResourceTypeDefinition fromJaxRsResource(
      final Class<?> resource)
  {
    Class<?> c = resource;
    ResourceType resourceType;
    do
    {
      resourceType = c.getAnnotation(ResourceType.class);
      c = c.getSuperclass();
    }
    while(c != null && resourceType == null);

    c = resource;
    jakarta.ws.rs.Path path;
    do
    {
      path = c.getAnnotation(jakarta.ws.rs.Path.class);
      c = c.getSuperclass();
    }
    while(c != null && path == null);

    if(resourceType == null || path == null)
    {
      return null;
    }

    try
    {
      ResourceTypeDefinition.Builder builder =
          new Builder(resourceType.name(), path.value());
      builder.setDescription(resourceType.description());
      builder.setCoreSchema(SchemaUtils.getSchema(resourceType.schema()));
      builder.setDiscoverable(
          resourceType.discoverable());

      for (Class<?> optionalSchemaExtension :
          resourceType.optionalSchemaExtensions())
      {
        builder.addOptionalSchemaExtension(
            SchemaUtils.getSchema(optionalSchemaExtension));
      }

      for (Class<?> requiredSchemaExtension :
          resourceType.requiredSchemaExtensions())
      {
        builder.addRequiredSchemaExtension(
            SchemaUtils.getSchema(requiredSchemaExtension));
      }

      return builder.build();
    }
    catch(Exception e)
    {
      throw new IllegalArgumentException(e);
    }
  }
}
