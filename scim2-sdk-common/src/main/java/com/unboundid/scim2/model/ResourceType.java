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

package com.unboundid.scim2.model;

import com.unboundid.scim2.annotations.SchemaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * The "ResourceType" schema specifies the meta-data about a resource
 * type.  Resource type resources are READ-ONLY and identified using the
 * following schema URI:
 * "urn:ietf:params:scim:schemas:core:2.0:ResourceType".  Unlike other
 * core resources, all attributes are REQUIRED unless otherwise
 * specified.  The "id" attribute is not required for the resource type
 * resource.
 */
@SchemaInfo(id="urn:ietf:params:scim:schemas:core:2.0:ResourceType",
    name="Resource Type", description = "SCIM 2.0 Resource Type Resource")
public class ResourceType extends BaseScimResourceObject
{
  /**
   * name
   * The resource type name.  When applicable service providers MUST
   * specify the name specified in the core schema specification; e.g.,
   * "User" or "Group".  This name is referenced by the
   * "meta.resourceType" attribute in all resources.
   */
  private String name;

  /**
   * description
   * The resource type's human readable description.  When applicable
   *  service providers MUST specify the description specified in the
   * core schema specification.
   */
  private String description;

  /**
   *
   * endpoint
   * The resource type's HTTP addressable endpoint relative to the Base
   * URL; e.g., "/Users".
   */
  private String endpoint;

  /**
   * schema
   * The resource type's primary/base schema URI; e.g.,
   * "urn:ietf:params:scim:schemas:core:2.0:User".  This MUST be equal
   * to the "id" attribute of the associated "Schema" resource.
   */
  private String schema;

  /**
   * schemaExtensions
   * A list of URIs of the resource type's schema extensions.
   * OPTIONAL.
   */
  private List<SchemaExtension> schemaExtensions;

  /**
   * Constructs a new ResourceType with no SchemaExtensions.
   *
   * @param id id of the resource type.
   * @param name name of the resource type.
   * @param description description of the resource type.
   * @param endpoint the endpoint for the resource type.
   * @param schema the schema for the resource type.
   */
  public ResourceType(final String id, final String name,
                      final String description, final String endpoint,
                      final String schema)
  {
    this(id, name, description, endpoint, schema,
        new ArrayList<SchemaExtension>());
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
  public ResourceType(final String id, final String name,
                      final String description, final String endpoint,
                      final String schema,
                      final List<SchemaExtension> schemaExtensions)
  {
    this.name = name;
    this.description = description;
    this.endpoint = endpoint;
    this.schema = schema;
    this.schemaExtensions = schemaExtensions;
    setId(id);
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
  public String getSchema()
  {
    return schema;
  }

  /**
   * Gets the resource type's schema extensions.
   *
   * @return the schema extensions for the resource type.
   */
  public List<SchemaExtension> getSchemaExtensions()
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
    /**
     * schema  The URI of an extended schema; e.g., "urn:edu:2.0:Staff".
     * This MUST be equal to the "id" attribute of a "Schema"
     * resource.  REQUIRED.
     */
    private String schema;

    /**
     * required  A Boolean value that specifies whether the schema
     * extension is required for the resource type.  If true, a
     * resource of this type MUST include this schema extension and
     * include any attributes declared as required in this schema
     */
    private boolean required;

    /**
     * Constructs a new schema extension.
     *
     * @param schema the schema urn for the extension.
     * @param required a boolean indicating if this extension schema is
     *                 required or not.
     */
    SchemaExtension(final String schema, final boolean required)
    {
      this.schema = schema;
      this.required = required;
    }

    /**
     * Gets the extension schema's urn.
     *
     * @return urn for the schema extension.
     */
    public String getSchema()
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
  }
}
