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

package com.unboundid.scim2.pwdmgmt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Serializes classes that are beans, but the underlying structure holding
 * the attribute values is a JsonReference object.  This will preserve those
 * values, so that if a value was explicitly set to null, we will know that,
 * but if not, we will know it was never set.  During serialization, we will
 * serialize null values, but not serialize never set values.
 */
public class JsonRefBeanSerializer extends JsonSerializer<Object>
{
  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(final Object value, final JsonGenerator gen,
                        final SerializerProvider serializers)
      throws IOException
  {
    Class <?> clazz = value.getClass();
    try
    {
      gen.writeStartObject();
      Collection<PropertyDescriptor> propertyDescriptors =
          SchemaUtils.getPropertyDescriptors(clazz);
      for(PropertyDescriptor propertyDescriptor : propertyDescriptors)
      {
        Field field = SchemaUtils.findField(
            clazz, propertyDescriptor.getName());
        field.setAccessible(true);
        Object obj = field.get(value);
        if(obj instanceof JsonReference)
        {
          JsonReference<?> reference = (JsonReference<?>)obj;
          if(reference.isSet())
          {
            gen.writeFieldName(field.getName());
            serializers.defaultSerializeValue(reference.getObj(), gen);
          }
        }
      }
      gen.writeEndObject();
    }
    catch (IntrospectionException e)
    {
      e.printStackTrace();
    } catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }

  }
}
