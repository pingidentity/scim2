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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The "ResourceType" schema specifies the meta-data about a resource
 * type.  Resource type resources are READ-ONLY and identified using the
 * following schema URI:
 * "{@code urn:ietf:params:scim:schemas:core:2.0:ResourceType}".  Unlike other
 * core resources, all attributes are REQUIRED unless otherwise
 * specified.  The "{@code id}" attribute is not required for the resource type
 * resource.
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:ResourceType",
    name="Resource Type", description = "SCIM 2.0 Resource Type Resource")
public class ResourceTypeResource extends BaseScimResource
{
  @Nullable
  @Attribute(description = "The resource type name.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String name;

  @Nullable
  @Attribute(description =
      "The resource type's human readable description.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String description;

  @Nullable
  @Attribute(description =
      "The resource type's HTTP addressable endpoint relative to the Base " +
          "URL; e.g., \"/Users\".",
      referenceTypes = {"uri"},
      isRequired = true,
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final URI endpoint;

  @Nullable
  @Attribute(description =
      "The resource types primary/base schema URI.",
      referenceTypes = {"uri"},
      isRequired = true,
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final URI schema;

  @Nullable
  @Attribute(description =
      "A list of URIs of the resource type's schema extensions.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = SchemaExtension.class)
  private final Collection<SchemaExtension> schemaExtensions;

  /**
   * Constructs a new ResourceType with no SchemaExtensions.
   *
   * @param name name of the resource type.
   * @param description description of the resource type.
   * @param endpoint the endpoint for the resource type.
   * @param schema the schema for the resource type.
   */
  public ResourceTypeResource(@Nullable final String name,
                              @Nullable final String description,
                              @Nullable final URI endpoint,
                              @Nullable final URI schema)
  {
    this(name, name, description, endpoint, schema, null);
  }

  /**
   * Constructs a new ResourceType with SchemaExtensions.
   *
   * @param id id of the resource type.
   * @param name name of the resource type.
   * @param description description of the resource type.
   * @param endpoint the endpoint for the resource type.
   * @param schema the schema for the resource type.
   * @param schemaExtensions a list of schema extensions for this resource
   *                         type.
   */
  @JsonCreator
  public ResourceTypeResource(
      @Nullable @JsonProperty("id")
      final String id,
      @Nullable @JsonProperty(value = "name", required = true)
      final String name,
      @Nullable @JsonProperty(value = "description")
      final String description,
      @Nullable @JsonProperty(value = "endpoint", required = true)
      final URI endpoint,
      @Nullable @JsonProperty(value = "schema", required = true)
      final URI schema,
      @Nullable @JsonProperty("schemaExtensions")
      final Collection<SchemaExtension> schemaExtensions)
  {
    super(id);
    this.name = name;
    this.description = description;
    this.endpoint = endpoint;
    this.schema = schema;
    this.schemaExtensions = schemaExtensions == null ?
        null : List.copyOf(schemaExtensions);
  }

  /**
   * Gets the resource type name.
   *
   * @return the name of the resource type.
   */
  @Nullable
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of the resource type.
   *
   * @return the description of the resource type.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the resource type's endpoint.
   *
   * @return the endpoint for the resource type.
   */
  @Nullable
  public URI getEndpoint()
  {
    return endpoint;
  }

  /**
   * Gets the resource type's schema.
   *
   * @return the schema for the resource type.
   */
  @Nullable
  public URI getSchema()
  {
    return schema;
  }

  /**
   * Gets the resource type's schema extensions.
   *
   * @return the schema extensions for the resource type.
   */
  @Nullable
  public Collection<SchemaExtension> getSchemaExtensions()
  {
    return schemaExtensions;
  }

  /**
   * This class holds information about schema extensions for resource
   * types.  It contains an urn and a boolean indicating if it's required
   * or optional.
   */
  public static class SchemaExtension
  {
    @NotNull
    @Attribute(description =
        "The URI of a schema extension.",
        isRequired = true,
        isCaseExact = true,
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        returned = AttributeDefinition.Returned.DEFAULT,
        uniqueness = AttributeDefinition.Uniqueness.NONE,
        referenceTypes = {"uri"} )
    private URI schema;

    @Attribute(description =
        "A Boolean value that specifies whether the schema extension is " +
            "required for the resource type. If true, a resource of this " +
            "type MUST include this schema extension and include any " +
            "attributes declared as required in this schema extension. " +
            "If false, a resource of this type MAY omit this schema extension",
        isRequired = true,
        mutability = AttributeDefinition.Mutability.READ_ONLY,
        returned = AttributeDefinition.Returned.DEFAULT)
    private boolean required;

    /**
     * Constructs a new schema extension.
     *
     * @param schema the schema urn for the extension.
     * @param required a boolean indicating if this extension schema is
     *                 required or not.
     */
    @JsonCreator
    public SchemaExtension(@NotNull @JsonProperty(value = "schema", required = true)
                           final URI schema,
                           @JsonProperty(value = "required", required = true)
                           final boolean required)
    {
      this.schema = schema;
      this.required = required;
    }

    /**
     * Gets the extension schema's urn.
     *
     * @return urn for the schema extension.
     */
    @NotNull
    public URI getSchema()
    {
      return schema;
    }

    /**
     * Indicates whether the schema is required for this schema extension
     * (for the resource type this schema extension is part of).
     *
     * @return Whether the schema is required for this schema extension.
     */
    public boolean isRequired()
    {
      return required;
    }

    /**
     * Indicates whether the provided object is equal to this schema extension.
     *
     * @param o   The object to compare.
     * @return    {@code true} if the provided object is equal to this schema
     *            extension, or {@code false} if not.
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

      SchemaExtension that = (SchemaExtension) o;

      if (required != that.required)
      {
        return false;
      }
      return Objects.equals(schema, that.schema);
    }

    /**
     * Retrieves a hash code for this schema extension.
     *
     * @return  A hash code for this schema extension.
     */
    @Override
    public int hashCode()
    {
      return Objects.hash(required, schema);
    }
  }

  /**
   * Indicates whether the provided object is equal to this resource type
   * definition.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this resource
   *            type definition, or {@code false} if not.
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

    ResourceTypeResource that = (ResourceTypeResource) o;
    if (!Objects.equals(name, that.name))
    {
      return false;
    }
    if (!Objects.equals(description, that.description))
    {
      return false;
    }
    if (!Objects.equals(endpoint, that.endpoint))
    {
      return false;
    }
    if (!Objects.equals(schema, that.schema))
    {
      return false;
    }
    return Objects.equals(schemaExtensions, that.schemaExtensions);
  }

  /**
   * Retrieves a hash code for this resource type definition.
   *
   * @return  A hash code for this resource type definition.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(name, description, endpoint, schema, schemaExtensions);
  }
}
