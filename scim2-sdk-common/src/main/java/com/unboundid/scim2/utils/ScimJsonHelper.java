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

package com.unboundid.scim2.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;

/**
 * This class provides some helper methods for working with Scim
 * JSON objects as JsonNodes.
 */
public class ScimJsonHelper
{
  private JsonNode jsonNode;

  /**
   * Constructs a ScimJsonHelper object from a json node.
   * @param jsonNode the json node to use with this helper class.
   */
  public ScimJsonHelper(final JsonNode jsonNode)
  {
    this.jsonNode = jsonNode;
  }

  /**
   * This just delegates to the underlying jsonNode object, but
   * allows for multiple paths to be specified.  It is mainly for
   * convenience, as the objects that are returned are helper
   * objects that can be used for things like getCanonicalType.
   *
   * @param paths the paths to search.
   * @return as ScimJsonHelper wrapping the node at the path, or a MissingNode.
   */
  public ScimJsonHelper path(final String ... paths)
  {
    JsonNode returnJsonNode = jsonNode;

    for(String path : paths)
    {
      returnJsonNode = returnJsonNode.path(path);
    }

    return new ScimJsonHelper(returnJsonNode);
  }

  /**
   * Get's the json node representing the scim object with the canonical
   * type specified.  This means an object with a field called 'type' that
   * has the given value.
   *
   * @param typeValue the value to search for in for the value of the field.
   * @return a ScimJsonHelper wrapping the found node, or a MissingNode if
   * not found.
   */
  public ScimJsonHelper getCanonicalType(final String typeValue)
  {
    return getCanonicalType("type", typeValue);
  }

  /**
   * Get's the json node representing the scim object with the canonical
   * type specified.  Normally in SCIM this means an object with a field
   * called 'type' that has the given value., but in this method, the field
   * name can be specified.
   *
   * @param typeFieldName the field name to use as the type field.
   * @param typeValue the value to search for in for the value of the field.
   * @return a ScimJsonHelper wrapping the found node, or a MissingNode if
   * not found.
   */
  public ScimJsonHelper getCanonicalType(final String typeFieldName,
                                         final String typeValue)
  {
    if(this.jsonNode.isArray())
    {
      for(JsonNode searchNode : ((ArrayNode)this.jsonNode))
      {
        JsonNode typeNode = searchNode.get(typeFieldName);
        if(typeNode == null)
        {
          continue;
        }

        if(typeValue.equals(typeNode.asText()))
        {
          return new ScimJsonHelper(searchNode);
        }
      }
    }

    return new ScimJsonHelper(MissingNode.getInstance());
  }

  /**
   * Gets the underlying json node.
   *
   * @return the udnerlying json node.
   */
  public JsonNode getJsonNode()
  {
    return jsonNode;
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the value, or the default value
   */
  public String textValue()
  {
    return jsonNode.textValue();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the value, or the default value
   */
  public String asText()
  {
    return jsonNode.asText();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the value, or the default value
   */
  public int intValue()
  {
    return jsonNode.intValue();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the converted value, or the default value
   */
  public int asInt()
  {
    return jsonNode.asInt();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @param defaultValue returned if the value cannot be converted
   * @return the converted value, or the default value
   */
  public int asInt(final int defaultValue)
  {
    return jsonNode.asInt(defaultValue);
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the value, or the default value
   */
  public long longValue()
  {
    return jsonNode.longValue();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the converted value, or the default value
   */
  public long asLong()
  {
    return jsonNode.asLong();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @param defaultValue returned if the value cannot be converted
   * @return the converted value, or the default value
   */
  public long asLong(final long defaultValue)
  {
    return jsonNode.asLong(defaultValue);
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the value, or the default value
   */
  public boolean booleanValue()
  {
    return jsonNode.booleanValue();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the converted value, or the default value
   */
  public boolean asBoolean()
  {
    return jsonNode.asBoolean();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @param defaultValue returned if the value cannot be converted
   * @return the converted value, or the default value
   */
  public boolean asBoolean(final boolean defaultValue)
  {
    return jsonNode.asBoolean(defaultValue);
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the value, or the default value
   */
  public double doubleValue()
  {
    return jsonNode.doubleValue();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @return the converted value, or the default value
   */
  public double asDouble()
  {
    return jsonNode.asDouble();
  }

  /**
   * This method just delegates to the jsonNode object.  It just
   * a convenience, as it makes it a little easier to chain methods
   * together like path().path().getXXX().
   *
   * @param defaultValue returned if the value cannot be converted
   * @return the converted value, or the default value
   */
  public double asDouble(final double defaultValue)
  {
    return jsonNode.asDouble(defaultValue);
  }
}
