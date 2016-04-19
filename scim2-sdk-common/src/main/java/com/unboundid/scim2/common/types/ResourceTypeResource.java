/*
 * Copyright 2015-2016 UnboundID Corp.
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
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The "ResourceType" schema specifies the meta-data about a resource
 * type.  Resource type resources are READ-ONLY and identified using the
 * following schema URI:
 * "urn:ietf:params:scim:schemas:core:2.0:ResourceType".  Unlike other
 * core resources, all attributes are REQUIRED unless otherwise
 * specified.  The "id" attribute is not required for the resource type
 * resource.
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:ResourceType",
    name="Resource Type", description = "SCIM 2.0 Resource Type Resource")
public class ResourceTypeResource extends BaseScimResource
{
  @Attribute(description = "The resource type name.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String name;

  @Attribute(description =
      "The resource type's human readable description.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String description;

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

  @Attribute(description =
      "The resource types primary/base schema URI.",
      referenceTypes = {"uri"},
      isRequired = true,
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final URI schema;

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
  public ResourceTypeResource(final String name,
                              final String description,
                              final URI endpoint,
                              final URI schema)
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
  public ResourceTypeResource(@JsonProperty("id") final String id,
                              @JsonProperty(value = "name", required = true)
                              final String name,
                              @JsonProperty(value = "description")
                              final String description,
                              @JsonProperty(value = "endpoint", required = true)
                              final URI endpoint,
                              @JsonProperty(value = "schema", required = true)
                              final URI schema,
                              @JsonProperty("schemaExtensions")
                              final Collection<SchemaExtension>
                                  schemaExtensions)
  {
    super(id);
    this.name = name;
    this.description = description;
    this.endpoint = endpoint;
    this.schema = schema;
    this.schemaExtensions = schemaExtensions == null ?
        null : Collections.unmodifiableList(
        new ArrayList<SchemaExtension>(schemaExtensions));
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
  public URI getEndpoint()
  {
    return endpoint;
  }

  /**
   * Gets the resource type's schema.
   *
   * @return the schema for the resource type.
   */
  public URI getSchema()
  {
    return schema;
  }

  /**
   * Gets the resource type's schema extensions.
   *
   * @return the schema extensions for the resource type.
   */
  public Collection<SchemaExtension> getSchemaExtensions()
  {
    return schemaExtensions;
  }

  /**
   * This class holds information about schema extensions for resource
   * types.  It contains a urn and a boolean indicating if it's required
   * or optional.
   */
  public static class SchemaExtension
  {
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
    public SchemaExtension(@JsonProperty(value = "schema", required = true)
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
    public URI getSchema()
    {
      return schema;
    }

    /**
     * Gets the boolean indicating if the schema is required
     * for this schema extension (for a the resource type this
     * schema extension is part of).
     *
     * @return boolean boolean indicating if the schema is required
     * for this schema extension.
     */
    public boolean isRequired()
    {
      return required;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o)
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
      if (schema != null ? !schema.equals(that.schema) : that.schema != null)
      {
        return false;
      }

      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
      int result = schema != null ? schema.hashCode() : 0;
      result = 31 * result + (required ? 1 : 0);
      return result;
    }
  }

  @Override
  public boolean equals(final Object o)
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

    if (description != null ? !description.equals(that.description) :
        that.description != null)
    {
      return false;
    }
    if (endpoint != null ? !endpoint.equals(that.endpoint) :
        that.endpoint != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (schema != null ? !schema.equals(that.schema) : that.schema != null)
    {
      return false;
    }
    if (schemaExtensions != null ?
        !schemaExtensions.equals(that.schemaExtensions) :
        that.schemaExtensions != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (endpoint != null ? endpoint.hashCode() : 0);
    result = 31 * result + (schema != null ? schema.hashCode() : 0);
    result = 31 * result + (schemaExtensions != null ?
        schemaExtensions.hashCode() : 0);
    return result;
  }
}
