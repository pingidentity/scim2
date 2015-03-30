/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.schema.SchemaUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A generic SCIM object.  This object can be used if you have no
 * java object representing the SCIM object being returned.
 *
 * This object can be used when the exact structure of the SCIM object
 * that will be recieved as JSON text is not known.  This will provide
 * methods that can read attributes from those objects without needing
 * to know the schema ahead of time.  Another way to work with SCIM
 * objects is when you know ahead of time what the schema will be.  In
 * that case you could still use this object, but BaseScimResourceObject
 * might be a better choice.
 *
 * If you have a BaseScimResourceObject derived object, you can always get a
 * GenericScimResourceObject by serializing The BaseScimResourceObject
 * derived object into a JSON string, and deserializing back to a
 * GenericScimResourceObject.  You could also go the other way.
 *
 * {@link com.unboundid.scim2.model.BaseScimResourceObject}
 */
@JsonDeserialize(using = GenericScimObjectDeserializer.class)
@JsonSerialize(using = GenericScimObjectSerializer.class)
public final class GenericScimResourceObject implements ScimResource
{
  private JsonNode jsonNode;
  private ThreadLocal<ObjectMapper> objectMapperThreadLocal =
      new ThreadLocal<ObjectMapper>();

  /**
   * Sets the the <code>JsonNode</code> that backs this object.
   * @param jsonNode a <code>JsonNode</code>
   */
  public void setJsonNode(final JsonNode jsonNode)
  {
    this.jsonNode = jsonNode;
  }

  /**
   * Gets the <code>JsonNode</code> that backs this object.
   * @return a <code>JsonNode</code>.
   */
  public JsonNode getJsonNode()
  {
    return this.jsonNode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Meta getMeta()
  {
    return getObjectFromJsonNode("meta", Meta.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMeta(final Meta meta)
  {
    setObjectFieldInJsonNode("meta", meta);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getId()
  {
    return getObjectFromJsonNode("id", String.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final String id)
  {
    setObjectFieldInJsonNode("id", id);
  }

  /**
   * {@inheritDoc}
   */
  public Set<String> getSchemaUrns()
  {
    return getObjectFromJsonNode("schemas", HashSet.class);
  }

  /**
   * {@inheritDoc}
   */
  public void setSchemaUrns(final Set<String> schemaUrns)
  {
    setObjectFieldInJsonNode("schemas", schemaUrns);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getExtension(final Class<T> cl) throws Exception
  {
    return getObjectFromJsonNode(
        SchemaUtils.getSchemaIdFromAnnotation(cl), cl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GenericScimResourceObject getExtension(final String schemaId)
      throws Exception
  {
    return getObjectFromJsonNode(schemaId, GenericScimResourceObject.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getExternalId()
  {
    return getObjectFromJsonNode("externalId", String.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setExternalId(final String externalId)
  {
    setObjectFieldInJsonNode("externalId", externalId);
  }

  /**
   * Gets the ObjectMapper from a thread local.  Will construct
   * one if needed.
   *
   * @return An object mapper for this thread.
   */
  private ObjectMapper getObjectMapper()
  {
    ObjectMapper objectMapper = objectMapperThreadLocal.get();
    if(objectMapper == null)
    {
      objectMapper = new ObjectMapper();
      objectMapperThreadLocal.set(objectMapper);
    }

    return objectMapper;
  }

  /**
   * Gets an object from a json node by name.  The ObjectMapper will
   * be used to deserialize the value of the field with the given name
   * into an object of the class provided.
   *
   * @param fieldName name of the field to deserialize.
   * @param cls class to use to construct the object.
   * @param <T> the type of the class.  This will be the returned type.
   * @return an instance of the Class specified with the value of the field.
   */
  private <T> T getObjectFromJsonNode(final String fieldName,
                                      final Class<T> cls)
  {
    return getObjectFromJsonNode(this.jsonNode, fieldName, cls);
  }

  /**
   * Gets an object node from a json node and returns it as the java
   * class passed in.
   *
   * @param jsonNode the JsonNode to retrieve the value from.
   * @param fieldName the name of the field holding the value.
   * @param cls the java class object used to determine the type to return.
   * @param <T> the generic type parameter of the java Class used to
   *           determine the type to return.
   * @return the object value for the field in the JsonNode.
   */
  private <T> T getObjectFromJsonNode(final JsonNode jsonNode,
      final String fieldName, final Class<T> cls)
  {
    T object = null;
    JsonNode fieldNode = jsonNode.get(fieldName);
    if (fieldNode != null)
    {
      try
      {
        object = getObjectMapper().treeToValue(fieldNode, cls);
//        object = getObjectMapper ().readValue(fieldNode.traverse(), cls);
      }
      catch (IOException e)
      {
        throw new RuntimeException("Unable to parse " + fieldName + " node", e);
      }
    }

    return object;

  }

  /**
   * Sets the field in the json node with the given name to the value of the
   * object given.
   *
   * @param fieldName name of the field to set or replace the value of.
   * @param object object representing the new value.
   */
  private void setObjectFieldInJsonNode(final String fieldName,
      final Object object)
  {
    setObjectFieldInJsonNode(this.jsonNode, fieldName, object);
  }

  /**
   * Creates and sets a new ObjectNode in a JSONNode.  The JSONNode
   * should be an ObjectNode.
   *
   * @param jsonNode the object node to create a new object node inside of.
   * @param fieldName the name of the newly created object node.
   * @param object the value of the newly created object node.
   */
  private void setObjectFieldInJsonNode(final JsonNode jsonNode,
      final String fieldName, final Object object)
  {
    if(jsonNode.isObject())
    {
      ObjectNode objectNode = (ObjectNode)jsonNode;
      JsonNode newObjectNode = getObjectMapper().valueToTree(object);
      objectNode.put(fieldName, newObjectNode);
    }
    else
    {
      throw new RuntimeException(
          "Cannot add " + fieldName + "Meta to a simple type (non object)");
    }
  }
}
