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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * This represents a SCIM schema.
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:Schema",
    name="Schema", description = "SCIM 2.0 Schema Resource")
public class SchemaResource extends BaseScimResource
{
  @Nullable
  @Attribute(description =
      "The schema's human readable name.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String name;

  @Nullable
  @Attribute(description =
      "The schema's human readable description.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String description;

  @NotNull
  @Attribute(description =
      "Attributes of the object described by this schema.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AttributeDefinition.class)
  private final Collection<AttributeDefinition> attributes;

  /**
   * Create a new Schema resource.
   *
   * @param id The schema's ID.
   * @param name The schema's display name.
   * @param description The schema's human readable description.
   * @param attributes The schema's attributes.
   */
  @JsonCreator
  public SchemaResource(@NotNull @JsonProperty(value = "id", required = true)
                        final String id,
                        @Nullable @JsonProperty(value = "name")
                        final String name,
                        @Nullable @JsonProperty(value = "description")
                        final String description,
                        @NotNull @JsonProperty(value = "attributes", required = true)
                        final Collection<AttributeDefinition> attributes)
  {
    super(id);
    this.name = name;
    this.description = description;
    this.attributes = Collections.unmodifiableList(
        new ArrayList<AttributeDefinition>(attributes));
  }

  /**
   * Gets the object's name.
   *
   * @return objects name.
   */
  @Nullable
  public String getName()
  {
    return name;
  }

  /**
   * Gets the name of the SCIM object from the schema.
   *
   * @return the name of the SCIM object.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the attributes of the SCIM object from the schema.
   *
   * @return the attributes of the SCIM object.
   */
  @NotNull
  public Collection<AttributeDefinition> getAttributes()
  {
    return attributes;
  }

  /**
   * Indicates whether the provided object is equal to this SCIM schema object.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this schema
   *            resource, or {@code false} if not.
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

    SchemaResource that = (SchemaResource) o;
    if (!Objects.equals(name, that.name))
    {
      return false;
    }
    if (!Objects.equals(description, that.description))
    {
      return false;
    }
    return Objects.equals(attributes, that.attributes);
  }

  /**
   * Retrieves a hash code for this SCIM schema object.
   *
   * @return  A hash code for this SCIM schema object.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(name, description, attributes);
  }
}
