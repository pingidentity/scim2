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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.schema.SchemaUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The base scim object.  Does not include other scim related fields such as
 * Meta, id, externalId, extensions, etc.  BaseScimResourceObject
 * can be used for that purpose.
 *
 * The primary use of this object will be to define extension schema objects
 * in annotated java classes.  If you would like to do that, just extend this
 * class from a java bean class, and use the scim2 annotations.
 */
public class BaseScimObject
{
  private Map<String, Object> unmappedValues =
      new HashMap<String, Object>();

  /**
   * This method will validate that this object conforms to the
   * schema.
   *
   * @throws Exception thrown if an error occurs.  Exceptions would
   * probably be due to reflection/introspection problems.
   */
  public void validateObject() throws Exception
  {
    Collection<PropertyDescriptor> propertyDescriptors =
        SchemaUtils.getPropertyDescriptors(this.getClass());
    for(PropertyDescriptor d : propertyDescriptors)
    {
      Method m = d.getReadMethod();
      if(m.isAnnotationPresent(SchemaProperty.class))
      {
        SchemaProperty schemaProperty = m.getAnnotation(SchemaProperty.class);
        if(schemaProperty.isRequired())
        {
          Object o = m.invoke(this);
          if(o == null)
          {
            throw new RuntimeException("Failed Validation:  " +
                "Property:  " + d.getName() + " cannot be null");

          }
        }
      }
    }
  }

  /**
   * This method is used during json deserialization.  It will be called
   * in the event that a value is given for an field that is not defined
   * in the class.
   *
   * @param key name of the field.
   * @param value value of the field.
   */
  @JsonAnySetter
  protected void setUnmappedValue(final String key, final Object value)
  {
    unmappedValues.put(key, value);
  }

  /**
   * Used to get values that were deserialized from json where there was
   * no matching field in the class.
   * @return the value of the field.
   */
  @JsonAnyGetter
  public Map<String, Object> getUnmappedValues()
  {
    return unmappedValues;
  }
}
