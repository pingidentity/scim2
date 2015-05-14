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

package com.unboundid.scim2.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.unboundid.scim2.common.annotations.SchemaInfo;
import com.unboundid.scim2.common.annotations.SchemaProperty;
import com.unboundid.scim2.common.AttributeDefinition;
import com.unboundid.scim2.common.SchemaResource;

import javax.lang.model.type.NullType;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Stack;

/**
 * Utility class with static methods for common schema operations.
 */
public class SchemaUtils
{

  /**
   * Gets property descriptors for the given class.
   *
   * @param cls The class to get the property descriptors for.
   * @return a collection of property values.
   * @throws java.beans.IntrospectionException throw if there are any
   * introspection errors.
   */
  public static Collection<PropertyDescriptor>
  getPropertyDescriptors(final Class cls)
      throws IntrospectionException
  {
    BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
    PropertyDescriptor[] propertyDescriptors =
        beanInfo.getPropertyDescriptors();
    return Arrays.asList(propertyDescriptors);
  }

  /**
   * Gets schema attributes for the given class.
   * @param cls Class to get the schema attributes for.
   * @return a collection of attributes.
   * @throws IntrospectionException thrown if an introspection error occurs.
   */
  @Transient
  public static Collection<AttributeDefinition> getAttributes(
      final Class cls)
      throws IntrospectionException
  {
    Stack<String> classesProcessed = new Stack<String>();
    return getAttributes(classesProcessed, cls);
  }

  /**
   * Gets SCIM schema attributes for a class.
   *
   * @param classesProcessed a stack containing the classes processed prior
   *                         to this class.  This is used for cycle detection.
   * @param cls the class to get the attributes for.
   * @return a collection of SCIM schema attributes for the class.
   * @throws IntrospectionException thrown if an error occurs during
   *    Introspection.
   */
  private static Collection<AttributeDefinition> getAttributes(
      final Stack<String> classesProcessed, final Class<?> cls)
      throws IntrospectionException
  {
    String className = cls.getCanonicalName();
    if(classesProcessed.contains(className))
    {
      throw new RuntimeException("Cycles detected in Schema");
    }

    Collection<PropertyDescriptor> propertyDescriptors =
        getPropertyDescriptors(cls);
    Collection<AttributeDefinition> attributes =
        new ArrayList<AttributeDefinition>();

    for(PropertyDescriptor propertyDescriptor : propertyDescriptors)
    {
      AttributeDefinition.Builder attributeBuilder =
          new AttributeDefinition.Builder();

      Field field = findField(cls, propertyDescriptor.getName());

      SchemaProperty schemaProperty = null;
      if((field != null)
          && (field.isAnnotationPresent(SchemaProperty.class)))
      {
        schemaProperty = field.getAnnotation(SchemaProperty.class);
      }

      // Only generate schema for annotated fields.
      if(schemaProperty == null)
      {
        continue;
      }

      addName(attributeBuilder, propertyDescriptor);
      addDescription(attributeBuilder, schemaProperty);
      addCaseExact(attributeBuilder, schemaProperty);
      addRequired(attributeBuilder, schemaProperty);
      addReturned(attributeBuilder, schemaProperty);
      addUniqueness(attributeBuilder, schemaProperty);
      addReferenceTypes(attributeBuilder, schemaProperty);
      addMutability(attributeBuilder, schemaProperty);
      addMultiValued(attributeBuilder, propertyDescriptor, schemaProperty);
      addCanonicalValues(attributeBuilder, schemaProperty);

      Class propertyCls = propertyDescriptor.getPropertyType();

      // if this is a multivalued attribute the real sub attribute class is the
      // the one specified in the annotation, not the list, set, array, etc.
      if((schemaProperty.multiValueClass() != NullType.class))
      {
        propertyCls = schemaProperty.multiValueClass();
      }

      AttributeDefinition.Type type = getAttributeType(propertyCls);
      attributeBuilder.setType(type);

      if(type == AttributeDefinition.Type.COMPLEX)
      {
        // Add this class to the list to allow cycle detection
        classesProcessed.push(cls.getCanonicalName());
        Collection<AttributeDefinition> subAttributes =
            getAttributes(classesProcessed, propertyCls);
        attributeBuilder.addSubAttributes(subAttributes.toArray(
            new AttributeDefinition[subAttributes.size()]));
        classesProcessed.pop();
      }

      attributes.add(attributeBuilder.build());
    }

    return attributes;
  }

  /**
   * This method will find the name for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param propertyDescriptor property descriptor for the field to build
   *                           the attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addName(
      final AttributeDefinition.Builder attributeBuilder,
      final PropertyDescriptor propertyDescriptor)
  {
    attributeBuilder.setName(propertyDescriptor.getName());

    return attributeBuilder;
  }

  /**
   * This method will determine if this attribute can have multieple
   * values, and set that in the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param propertyDescriptor property descriptor for the field to build
   *                           the attribute for.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addMultiValued(
      final AttributeDefinition.Builder attributeBuilder,
      final PropertyDescriptor propertyDescriptor,
      final SchemaProperty schemaProperty)
  {
    Class<?> multiValuedClass = schemaProperty.multiValueClass();
    boolean multiValued = !multiValuedClass.equals(NullType.class);
    boolean collectionOrArray =
        isCollectionOrArray(propertyDescriptor.getPropertyType());

    // if the multiValuedClass attribute is present in the annotation,
    // make sure this is a collection or array.
    if(multiValued && !collectionOrArray)
    {
      throw new RuntimeException("Property named " +
          propertyDescriptor.getName() +
          " is annotated with a multiValuedClass, " +
          "but is not a Collection or an array");
    }

    if(!multiValued && collectionOrArray)
    {
      throw new RuntimeException("Property named " +
          propertyDescriptor.getName() +
          " is not annotated with a multiValuedClass, " +
          "but is a Collection or an array");
    }

    attributeBuilder.setMultiValued(multiValued);

    return attributeBuilder;
  }

  /**
   * This method will find the description for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addDescription(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setDescription(schemaProperty.description());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the case exact boolean for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addCaseExact(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setCaseExact(schemaProperty.isCaseExact());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the required boolean for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addRequired(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setRequired(schemaProperty.isRequired());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the canonical values for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addCanonicalValues(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setCanonicalValues(schemaProperty.canonicalValues());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the returned constraint for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addReturned(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setReturned(schemaProperty.returned());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the uniqueness constraint for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addUniqueness(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setUniqueness(schemaProperty.uniqueness());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the reference types for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addReferenceTypes(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.addReferenceTypes(schemaProperty.referenceTypes());
    }

    return attributeBuilder;
  }

  /**
   * This method will find the mutability constraint for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addMutability(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.setMutability(schemaProperty.mutability());
    }
    else
    {
      attributeBuilder.setMutability(AttributeDefinition.Mutability.READ_WRITE);
    }

    return attributeBuilder;
  }

  /**
   * Gets the attribute type for a given property descriptor.  This method
   * will attempt to decide what SCIM attribute type should be in the schema
   * based on the java class of the attribute.
   *
   * @param cls java Class for an attribute of a SCIM object.
   * @return an attribute type.
   */
  private static AttributeDefinition.Type getAttributeType(
      final Class cls)
  {
    if((cls == Integer.class) ||
        (cls == int.class))
    {
      return AttributeDefinition.Type.INTEGER;
    }
    else if ((cls == Boolean.class) ||
        (cls == boolean.class))
    {
      return AttributeDefinition.Type.BOOLEAN;
    }
    else if ((cls == Double.class) ||
        (cls == double.class) ||
        (cls == Float.class) ||
        (cls == float.class))
    {
      return AttributeDefinition.Type.DECIMAL;
    }
    else if ((cls == String.class) ||
        (cls == boolean.class))
    {
      return AttributeDefinition.Type.STRING;
    }
    else if((cls == URI.class) || (cls == URL.class))
    {
      return AttributeDefinition.Type.REFERENCE;
    }
    else if((cls == Date.class) || (cls == Calendar.class))
    {
      return AttributeDefinition.Type.DATETIME;
    }
//    else if(What?)
//    {
//      return Attribute.Type.BINARY;
//    }
    else
    {
      return AttributeDefinition.Type.COMPLEX;
    }
  }

  /**
   * Gets the schema for a class.  This will walk the inheritance tree looking
   * for information about the SCIM schema of the objects represented. This
   * information comes from annotations and introspection.
   *
   * @param cls the class to get the schema for.
   * @return the schema.
   * @throws IntrospectionException if an exception occurs during introspection.
   */
  public static SchemaResource getSchema(final Class<?> cls)
      throws IntrospectionException
  {
    SchemaInfo schemaAnnotation = cls.getAnnotation(SchemaInfo.class);

    // Only generate schema for annotated classes.
    if(schemaAnnotation == null)
    {
      return null;
    }

    return new SchemaResource(schemaAnnotation.id(),
        schemaAnnotation.name(), schemaAnnotation.description(),
        getAttributes(cls));
  }

  /**
   * This method will find a java Field for with a particular name.  If
   * needed, this method will search through super classes.  The field
   * does not need to be public.
   *
   * @param cls the java Class to search.
   * @param fieldName the name of the field to find.
   * @return the java field.
   */
  public static Field findField(final Class<?> cls,
                                 final String fieldName)
  {
    Class<?> currentClass = cls;
    while(currentClass != null)
    {
      Field [] fields = currentClass.getDeclaredFields();
      for(Field field : fields)
      {
        if(field.getName().equals(fieldName))
        {
          return field;
        }
      }
      currentClass = currentClass.getSuperclass();
    }

    return null;
  }

  /**
   * Returns true if the supplied class is a collection or an array. This
   * is primarily used to determine if it's a multivalued attribute.
   *
   * @param cls the class to check.
   * @return true if the class is a collection or an array, or false if not.
   */
  private static boolean isCollectionOrArray(final Class<?> cls)
  {
    return (cls.isArray() && byte[].class != cls) ||
        Collection.class.isAssignableFrom(cls);

  }

  /**
   * Creates an object mapper suitable for serializing and deserializing
   * Scim objects derived from ether this object, or from GenericScimObject.
   *
   * @return an Object Mapper with the correct options set for seirializing
   *     and deserializing SCIM JSON objects.
   */
  public static ObjectMapper createSCIMCompatibleMapper()
  {
    ObjectMapper mapper = new ObjectMapper();

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    return mapper;
  }

  /**
   * Gets the id of the schema from the annotation of the class
   * passed in.
   *
   * @param cls class to find the schema id property of the annotation from.
   * @return the id of the schema, or null if it was not provided.
   */
  public static String getSchemaIdFromAnnotation(final Class<?> cls)
  {
    SchemaInfo schema = cls.getAnnotation(SchemaInfo.class);
    return SchemaUtils.getSchemaIdFromAnnotation(schema);
  }

  /**
   * Gets the id property from schema annotation.  If the the id
   * attribute was null, a schema id is generated.
   *
   * @param schemaAnnotation the SCIM SchemaInfo annotation.
   * @return the id of the schema, or null if it was not provided.
   */
  private static String getSchemaIdFromAnnotation(
      final SchemaInfo schemaAnnotation)
  {
    if(schemaAnnotation != null)
    {
      return schemaAnnotation.id();
    }

    return null;
  }

  /**
   * Gets the name property from the annotation of the class
   * passed in.
   *
   * @param cls class to find the schema name property of the annotation from.
   * @return the name of the schema.
   */
  public static String getNameFromSchemaAnnotation(final Class<?> cls)
  {
    SchemaInfo schema = (SchemaInfo)cls.getAnnotation(SchemaInfo.class);
    return SchemaUtils.getNameFromSchemaAnnotation(schema);
  }

  /**
   * Gets the name property from schema annotation.
   *
   * @param schemaAnnotation the SCIM SchemaInfo annotation.
   * @return the name of the schema or a generated name.
   */
  private static String getNameFromSchemaAnnotation(
      final SchemaInfo schemaAnnotation)
  {
    if(schemaAnnotation != null)
    {
      return schemaAnnotation.name();
    }

    return null;
  }

  /**
   * Gets the schema urn from a java <code>Class</code>.
   * @param cls <code>Class</code> of the object.
   * @return The schema urn for the object.
   */
  public static String getSchemaUrn(final Class cls)
  {
    // the schemaid is the urn.  Just make sure it
    // begins with urn: ... the field called name
    // is just a human friendly name for the object.
    String schemaId =
        SchemaUtils.getSchemaIdFromAnnotation(cls);

    if ((schemaId == null) || (schemaId.isEmpty()))
    {
      schemaId = cls.getCanonicalName();
    }

    // if this doesn't appear to be a urn, stick the "urn:" prefix
    // on it, and use it as a urn anyway.
    return forceToBeUrn(schemaId);
  }


  /**
   * Returns true if the string passed in appears to be a urn.
   * That determination is made by looking to see if the string
   * starts with "urn:".
   *
   * @param string the string to check.
   * @return true if it's a urn, or false if not.
   */
  public static boolean isUrn(final String string)
  {
    return string.toLowerCase().startsWith("urn:") &&
        string.length() > 4;
  }

  /**
   * Will force the string passed in to look like a urn.  If the
   * string starts with "urn:" it will be returned as is, however
   * if the string starts with anything else, this method will
   * prepend "urn:".  This is mainly so that if we have a class that
   * will be used as an extension schema, we will ensure that its
   * schema will be a urn and distinguishable from all other unmmapped
   * values.
   *
   * @param string the string to force to be a urn.
   * @return the urn.
   */
  public static String forceToBeUrn(final String string)
  {
    if(isUrn(string))
    {
      return string;
    }

    return "urn:" + string;
  }

}
