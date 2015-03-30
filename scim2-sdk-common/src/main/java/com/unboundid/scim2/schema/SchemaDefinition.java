/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema;

import com.unboundid.scim2.model.BaseScimResourceObject;

import java.util.Collection;

/**
 * This represents a SCIM schema.
 */
public class SchemaDefinition extends BaseScimResourceObject
{
  /**
   * The unique URI of the schema.  When applicable service providers
   * MUST specify the URI specified in the core schema specification;
   * e.g., "urn:ietf:params:scim:schemas:core:2.0:User".  Unlike most
   * other schemas, which use some sort of a GUID for the "id", the
   * schema "id" is a URI so that it can be registered and is portable
   * between different service providers and clients.
   */
  private String id;

  /**
   * The schema's human readable name.  When applicable service
   * providers MUST specify the name specified in the core schema
   * specification; e.g., "User" or "Group".  OPTIONAL.
   */
  private String name;

  /**
   * The schema's human readable description.  When applicable service
   * providers MUST specify the description specified in the core
   * schema specification.  OPTIONAL.
   */
  private String description;

  /**
   * Attributes of the object described by this schema.
   */
  private Collection<AttributeDefinition> attributes;

  /**
   * Gets the object's name.
   * @return objects name.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name of the SCIM object.
   * @param name the name of the SCIM object.
   */
  public void setName(final String name)
  {
    this.name = name;
  }

  /**
   * Gets the name of the SCIM object from the schema.
   * @return the name of the SCIM object.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the description of the SCIM object in the schema.
   *
   * @param description the description of the SCIM object.
   */
  public void setDescription(final String description)
  {
    this.description = description;
  }

  /**
   * Gets the attributes of the SCIM object from the schema.
   *
   * @return the attributes of the SCIM object.
   */
  public Collection<AttributeDefinition> getAttributes()
  {
    return attributes;
  }

  /**
   * Sets the attributes of the SCIM object in the schema.
   * @param attributes attributes of the SCIM object in the schema.
   */
  public void setAttributes(final Collection<AttributeDefinition> attributes)
  {
    this.attributes = attributes;
  }

  /**
   * Sets the id of the SCIM object in the schema.
   * @return the id of the SCIM object.
   */
  public String getId()
  {
    return id;
  }

  /**
   * Gets the id of the SCIM object from the schema.
   * @param id the id of the SCIM object.
   */
  public void setId(final String id)
  {
    this.id = id;
  }

//  @Override
//  public Map<String, JsonNode> getExtensions()
//  {
//    return null;
//  }
//
}
