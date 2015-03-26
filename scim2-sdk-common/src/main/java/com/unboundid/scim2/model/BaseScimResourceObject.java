/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.schema.SchemaUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * The base SCIM object.  This object contains all of the
 * attributes required of SCIM objects.
 *
 *
 * BaseScimResourceObject is used when the schema is known ahead of
 * time.  In that case a developer can derive a class from
 * BaseScimResourceObject and annotate the class.  The class should
 * be a java bean.  This will make it easier to work with the SCIM
 * object since you will just have plain old getters and setters.
 *
 *
 * If you have a BaseScimResourceObject derived object, you can always get a
 * GenericScimResourceObject by serializing The BaseScimResourceObject
 * derived object into a JSON string, and deserializing back to a
 * GenericScimResourceObject.  You could also go the other way.
 *
 * @See GenericResourceObject
 */
public class BaseScimResourceObject extends BaseScimObject
    implements ScimResource
{
  @SchemaProperty(description = "Meta information about the SCIM object")
  private Meta meta;

  @SchemaProperty(description = "The id of the SCIM object")
  private String id;

  @SchemaProperty(description = "The external id of the SCIM object")
  private String externalId;

  @JsonProperty("schemas")
  private Set<String> schemaUrns = new HashSet<String>();

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

    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
    getUnmappedValues().put(extensionSchemaName, mapper.valueToTree(extension));
  }

  /**
   * Adds the urn of this class to the list of schemas for this object.
   * This is taken from the schema annotation of a class that extends
   * this class.  If the class has no schema annotation, no schema urn
   * will be added.
   */
  private void addMyUrn()
  {
    String mySchema = SchemaUtils.getSchemaIdFromAnnotation(this.getClass());
    if((mySchema != null) && (!mySchema.isEmpty()))
    {
      getSchemaUrns().add(mySchema);
    }
  }

  @Override
  protected void setUnmappedValue(final String key, final Object value)
  {
    if(isUrn(key))
    {
      ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
      getUnmappedValues().put(key, mapper.valueToTree(value));
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
    return getExtension(extensionSchemaName, cl);
  }

  /**
   * {@inheritDoc}
   */
  public GenericScimResourceObject getExtension(final String schemaId)
      throws JsonProcessingException
  {
    return getExtension(schemaId, GenericScimResourceObject.class);
  }

  /**
   * Used internally to share code between the other two getExtensions
   * methods.  This method will get an extension from unmapped values.
   * The extension must have been stored with a urn for a key, and
   * the value must be a json node.  This will be set properly if the
   * addExtension method was used to add the extension.
   *
   * @param schemaId the urn of the extension schema.
   * @param cl the class to return.
   * @param <T> the type of the class to return.
   * @return a deserialized SCIM object.
   * @throws JsonProcessingException in the case of an error.
   */
  private <T> T getExtension(final String schemaId , final Class<T> cl)
      throws JsonProcessingException
  {
    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
    Object unmappedValue = getUnmappedValues().get(forceToBeUrn(schemaId));
    if(unmappedValue instanceof JsonNode)
    {
      JsonNode jsonNode = (JsonNode)unmappedValue;
      return mapper.treeToValue(jsonNode, cl);
    }

    return null;
  }

  /**
   * Gets the id of an extension from a java <code>Class</code>.
   * @param extensionClass <code>Class</code> of the extension object.
   * @return The name of the extension.
   */
  private String getExtensionId(final Class extensionClass)
  {
    String extensionSchemaId =
        SchemaUtils.getSchemaIdFromAnnotation(extensionClass);
    if((extensionSchemaId == null) || (extensionSchemaId.isEmpty()))
    {
      extensionSchemaId = extensionClass.getCanonicalName();
    }

    // if this doesn't appear to be a urn, stick the "urn:" prefix
    // on it, and use it as a urn anyway.
    return forceToBeUrn(extensionSchemaId);
  }

  /**
   * Returns true if the string passed in appears to be a urn.
   * That determination is made by looking to see if the string
   * starts with "urn:".
   *
   * @param string the string to check.
   * @return true if it's a urn, or false if not.
   */
  private boolean isUrn(final String string)
  {
    return string.startsWith("urn:");
  }

  /**
   * Will force the string passed in to look like a urn.  If the
   * string starts with "urn:" it will be returned as is, however
   * if the string starts with anything else, this method will
   * prepend "urn:".  This is mainly so that if we have a class that
   * will be used as an extension schema, we will ensure that it's
   * schema will be a urn and distinguishable from all other unmmapped
   * values.
   *
   * @param string the string to force to be a urn.
   * @return the urn.
   */
  private String forceToBeUrn(final String string)
  {
    if(isUrn(string))
    {
      return string;
    }

    return "urn:" + string;
  }
}
