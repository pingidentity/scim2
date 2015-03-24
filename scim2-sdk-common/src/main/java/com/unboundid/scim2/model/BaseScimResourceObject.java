/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.annotations.SchemaProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The base SCIM object.  This object contains all of the
 * attributes required of SCIM objects.
 */
public class BaseScimResourceObject extends BaseScimObject
    implements CommonScimObject
{
  @SchemaProperty(description = "Meta information about the SCIM object")
  private Meta meta;

  @SchemaProperty(description = "The id of the SCIM object")
  private String id;

  @SchemaProperty(description = "The external id of the SCIM object")
  private String externalId;

  @JsonProperty("schemas")
  private Set<String> schemaUrns = new HashSet<String>();

  private Map<String, JsonNode> extensions;

  /**
   * Constructs a new BaseScimResource object, and sets the urn if
   * the class extending this one is annotated.
   */
  public BaseScimResourceObject()
  {
    addMyUrn();
  }

  /**
   * Constructs a base scim resource object with the given
   * schema urn.
   *
   * @param schemaUrn urn of the schema for this object.
   */
  public BaseScimResourceObject(final String schemaUrn)
  {
    getSchemaUrns().add(schemaUrn);
  }

  /**
   * Gets metadata about the object.
   * @return <code>Meta</code> containing metadata about the object.
   */
  @Override
  public Meta getMeta()
  {
    return meta;
  }

  /**
   * Sets metadata for the object.
   * @param meta <code>Meta</code> containing metadata for the object.
   */
  @Override
  public void setMeta(final Meta meta)
  {
    this.meta = meta;
  }

  /**
   * Gets the id of the object.
   * @return the id of the object.
   */
  @Override
  public String getId()
  {
    return id;
  }

  /**
   * Sets the id of the object.
   * @param id The object's id.
   */
  @Override
  public void setId(final String id)
  {
    this.id = id;
  }

  /**
   * Gets the objects external id.
   * @return The external id of the object.
   */
  @Override
  public String getExternalId()
  {
    return externalId;
  }

  /**
   * Sets the object's external id.
   * @param externalId The external id of the object.
   */
  @Override
  public void setExternalId(final String externalId)
  {
    this.externalId = externalId;
  }

  /**
   * {@inheritDoc}
   */
  public Set<String> getSchemaUrns()
  {
    if(schemaUrns == null)
    {
      schemaUrns = new HashSet<String>();
    }

    return schemaUrns;
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(final Set<String> schemaUrns)
  {
    this.schemaUrns = schemaUrns;
  }

  /**
   * Gets an extension object.  In SCIM terms "this" is the CORE
   * schema object, and the class passed in is the object with the
   * extension schema.
   *
   * @return an object representing the extension.
   */
  @JsonAnyGetter
  public Map<String, JsonNode> getExtensions()
  {
    if(extensions == null)
    {
      extensions = new HashMap<String, JsonNode>();
    }
    return extensions;
  }

  /**
   * Adds an extension object.  In SCIM terms "this" is the CORE
   * schema object, and the class passed in is the object with the
   * extension schema.
   *
   * @param extension Object representing the SCIM object that extends
   *                  this object.
   */
  public void addExtension(final BaseScimObject extension)
  {
    // check to make sure it's a valid extension object
    // somewhere around here.
    String extensionSchemaName = getExtensionId(extension.getClass());
    if(extensionSchemaName != null)
    {
      getSchemaUrns().add(extensionSchemaName);
    }

    ObjectMapper mapper = BaseScimObject.createSCIMCompatibleMapper();
    getExtensions().put(extensionSchemaName, mapper.valueToTree(extension));
  }

  /**
   * Sets all SCIM extensions for this SCIM object.
   *
   * @param extensions the SCIM objects to extend this object with.
   */
  public void setExtensions(final Map<String, JsonNode> extensions)
  {
    // set the extensions
    this.extensions = extensions;

    // fix up the set of schema urns.  clear them, add this classes schema
    // urn, then all extension schema urns.
    getSchemaUrns().clear();
    addMyUrn();
    getSchemaUrns().addAll(this.extensions.keySet());
  }

  /**
   * Adds the urn of this class to the list of schemas for this object.
   * This is taken from the schema annotation of a class that extends
   * this class.  If the class has no schema annotation, no schema urn
   * will be added.
   */
  private void addMyUrn()
  {
    String mySchema = getSchemaIdFromAnnotation(this.getClass());
    if((mySchema != null) && (!mySchema.isEmpty()))
    {
      getSchemaUrns().add(mySchema);
    }
  }

  @Override
  protected void setUnmappedValue(final String key, final Object value)
  {
    if(key.startsWith("urn:"))
    {
      ObjectMapper mapper = BaseScimObject.createSCIMCompatibleMapper();
      getExtensions().put(key, mapper.valueToTree(value));
    }
    else
    {
      super.setUnmappedValue(key, value);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> T getExtension(final Class<T> cl) throws JsonProcessingException
  {
    String extensionSchemaName = getExtensionId(cl);
    ObjectMapper mapper = BaseScimObject.createSCIMCompatibleMapper();
    JsonNode jsonNode = getExtensions().get(extensionSchemaName);
    return mapper.treeToValue(jsonNode, cl);
  }

  /**
   * {@inheritDoc}
   */
  public GenericScimObject getExtension(final String schemaId)
      throws JsonProcessingException
  {
    JsonNode jsonNode = getExtensions().get(schemaId);
    ObjectMapper mapper = BaseScimObject.createSCIMCompatibleMapper();
    return mapper.treeToValue(jsonNode, GenericScimObject.class);
  }

  /**
   * Gets the id of an extension from a java <code>Class</code>.
   * @param extensionClass <code>Class</code> of the extension object.
   * @return The name of the extension.
   */
  private String getExtensionId(final Class extensionClass)
  {
    String extensionSchemaId = getSchemaIdFromAnnotation(extensionClass);
    if((extensionSchemaId == null) || (extensionSchemaId.isEmpty()))
    {
      extensionSchemaId = extensionClass.getCanonicalName();
    }

    return extensionSchemaId;
  }
}
