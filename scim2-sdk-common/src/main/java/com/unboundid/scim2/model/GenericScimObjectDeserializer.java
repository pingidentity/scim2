/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Deserializer for the GenericScimObject.
 */
public class GenericScimObjectDeserializer
    extends JsonDeserializer<GenericScimObject>
{
  /**
   * {@inheritDoc}
   */
  @Override
  public GenericScimObject deserialize(final JsonParser jp,
      final DeserializationContext ctxt) throws IOException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(jp);
    GenericScimObject newObject = new GenericScimObject();
    newObject.setJsonNode(jsonNode);
    return newObject;
  }
}
