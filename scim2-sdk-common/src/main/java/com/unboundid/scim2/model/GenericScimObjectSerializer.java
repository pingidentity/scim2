/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serializer for generic scim objects.
 */
public class GenericScimObjectSerializer
    extends JsonSerializer<GenericScimObject>
{

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(final GenericScimObject value, final JsonGenerator jgen,
      final SerializerProvider provider) throws IOException
  {
    ObjectMapper om = new ObjectMapper();
    om.writeTree(jgen, value.getJsonNode());
  }
}
