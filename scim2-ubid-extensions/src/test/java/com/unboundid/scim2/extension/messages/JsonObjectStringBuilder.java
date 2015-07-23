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

package com.unboundid.scim2.extension.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Convenience class to help build up json strings in tests.
 */
public class JsonObjectStringBuilder
{
  private final StringBuilder sb = new StringBuilder();
  private boolean needsComma = false;

  /**
   * Create a new object.
   */
  public JsonObjectStringBuilder()
  {
    sb.append("{");
  }

  /**
   * {@inheritDoc}
   */
  public String toString()
  {
    return sb.toString() + "}";
  }

  /**
   * Adds a property to the json string.
   *
   * @param name property name
   * @param value property value
   * @return this
   */
  public JsonObjectStringBuilder appendProperty(String name, String value)
  {
    appendCommaIfNeeded();
    appendPropertyName(name);
    appendPropertySeparator();
    appendPropertyValue(value);
    return this;
  }

  /**
   * Adds a list property to the json string.
   *
   * @param name property name
   * @param values list property value
   * @return this
   */
  public JsonObjectStringBuilder appendListProperty(String name,
      String ... values)
  {
    appendCommaIfNeeded();
    appendPropertyName(name);
    appendPropertySeparator();
    appendStartArray();

    if(values.length == 0)
    {
      appendPropertyValue((String)null);
    }
    else if (values.length == 1)
    {
      appendPropertyValue(values[0]);
    }
    else
    {
      boolean needsListComma = false;
      for(String value : values)
      {
        if(needsListComma)
        {
          sb.append(", ");
        }
        else
        {
          needsListComma = true;
        }
        appendPropertyValue(value);
      }
    }

    appendEndArray();
    return this;
  }

  /**
   * Adds a property to the json string.
   *
   * @param name property name
   * @param mapper mapper to convert value to string with.
   * @param value property value
   * @return this
   * @throws JsonProcessingException if an error occurs generating json string.
   */
  public JsonObjectStringBuilder appendProperty(String name,
      ObjectMapper mapper, Object value) throws JsonProcessingException
  {
    appendCommaIfNeeded();
    appendPropertyName(name);
    appendPropertySeparator();
    appendPropertyValue(mapper, value);
    return this;
  }

  /**
   * Adds a list property to the json string.
   *
   * @param name property name.
   * @param mapper mapper to use to convert the value to string.
   * @param values list property value.
   * @return this
   * @throws JsonProcessingException if an error occurs generating json string.
   */
  public JsonObjectStringBuilder appendListProperty(String name,
      ObjectMapper mapper, Object ... values) throws JsonProcessingException
  {
    appendCommaIfNeeded();
    appendPropertyName(name);
    appendPropertySeparator();
    appendStartArray();

    if(values.length == 0)
    {
      appendPropertyValue((String)null);
    }
    else if (values.length == 1)
    {
      appendPropertyValue(mapper, values[0]);
    }
    else
    {
      boolean needsListComma = false;
      for(Object value : values)
      {
        if(needsListComma)
        {
          sb.append(", ");
        }
        else
        {
          needsListComma = true;
        }
        appendPropertyValue(mapper, value);
      }
    }

    appendEndArray();
    return this;
  }

  /**
   * Adds a property to the json string.
   *
   * @param name property name
   * @param value property value
   * @return this
   */
  public JsonObjectStringBuilder appendProperty(String name,
      JsonObjectStringBuilder value)
  {
    appendCommaIfNeeded();
    appendPropertyName(name);
    appendPropertySeparator();
    appendPropertyValue(value);
    return this;
  }

  /**
   * Adds a list property to the json string.
   *
   * @param name property name
   * @param values list property value
   * @return this
   */
  public JsonObjectStringBuilder appendListProperty(String name,
    JsonObjectStringBuilder ... values)
  {
    appendCommaIfNeeded();
    appendPropertyName(name);
    appendPropertySeparator();
    appendStartArray();

    if(values.length == 0)
    {
      appendPropertyValue((String)null);
    }
    else if (values.length == 1)
    {
      appendPropertyValue(values[0]);
    }
    else
    {
      boolean needsListComma = false;
      for(JsonObjectStringBuilder value : values)
      {
        if(needsListComma)
        {
          sb.append(", ");
        }
        else
        {
          needsListComma = true;
        }
        appendPropertyValue(value);
      }
    }

    appendEndArray();

    return this;
  }

  private void appendStartArray()
  {
    sb.append("[");
  }

  private void appendEndArray()
  {
    sb.append("]");
  }

  private void appendCommaIfNeeded()
  {
    if (needsComma)
    {
      sb.append(", ");
    }
    else
    {
      needsComma = true;
    }
  }

  private void appendPropertyName(String name)
  {
    sb.append("\"" + name + "\"");
  }

  private void appendPropertySeparator()
  {
    sb.append(":");
  }

  private void appendPropertyValue(String value)
  {
    sb.append("\"" + value + "\"");
  }

  private void appendPropertyValue(ObjectMapper mapper, Object value)
      throws JsonProcessingException
  {
    sb.append(mapper.writeValueAsString(value));
  }

  private void appendPropertyValue(JsonObjectStringBuilder value)
  {
    sb.append(value.toString());
  }
}
