/*
 * Copyright 2015-2023 Ping Identity Corporation
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;

import javax.lang.model.type.NullType;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

/**
 * Utility class with static methods for common schema operations.
 */
public class SchemaUtils
{
  /**
   * The attribute definition for the SCIM 2 standard schemas attribute.
   */
  public static final AttributeDefinition SCHEMAS_ATTRIBUTE_DEFINITION;

  /**
   * The attribute definition for the SCIM 2 standard id attribute.
   */
  public static final AttributeDefinition ID_ATTRIBUTE_DEFINITION;

  /**
   * The attribute definition for the SCIM 2 standard externalId attribute.
   */
  public static final AttributeDefinition EXTERNAL_ID_ATTRIBUTE_DEFINITION;

  /**
   * The attribute definition for the SCIM 2 standard meta attribute.
   */
  public static final AttributeDefinition META_ATTRIBUTE_DEFINITION;

  /**
   * The collection of attribute definitions for SCIM 2 standard common
   * attributes: schemas, id, externalId, and meta.
   */
  public static final Collection<AttributeDefinition>
      COMMON_ATTRIBUTE_DEFINITIONS;

  static
  {
    AttributeDefinition.Builder builder = new AttributeDefinition.Builder();
    builder.setType(AttributeDefinition.Type.STRING);
    builder.setName("schemas");
    builder.setRequired(true);
    builder.setCaseExact(true);
    builder.setMultiValued(true);
    builder.setMutability(AttributeDefinition.Mutability.READ_WRITE);
    builder.setReturned(AttributeDefinition.Returned.ALWAYS);
    SCHEMAS_ATTRIBUTE_DEFINITION = builder.build();

    builder = new AttributeDefinition.Builder();
    builder.setType(AttributeDefinition.Type.STRING);
    builder.setName("id");
    builder.setCaseExact(true);
    builder.setMutability(AttributeDefinition.Mutability.READ_ONLY);
    builder.setReturned(AttributeDefinition.Returned.ALWAYS);
    ID_ATTRIBUTE_DEFINITION = builder.build();

    builder = new AttributeDefinition.Builder();
    builder.setType(AttributeDefinition.Type.STRING);
    builder.setName("externalId");
    builder.setCaseExact(true);
    builder.setMutability(AttributeDefinition.Mutability.READ_WRITE);
    EXTERNAL_ID_ATTRIBUTE_DEFINITION = builder.build();

    builder = new AttributeDefinition.Builder();
    builder.setType(AttributeDefinition.Type.COMPLEX);
    builder.setName("meta");
    builder.setMutability(AttributeDefinition.Mutability.READ_ONLY);
    try
    {
      Collection<AttributeDefinition> subAttributes = getAttributes(Meta.class);
      builder.addSubAttributes(subAttributes.toArray(
          new AttributeDefinition[subAttributes.size()]));
    }
    catch (IntrospectionException e)
    {
      throw new RuntimeException(e);
    }
    META_ATTRIBUTE_DEFINITION = builder.build();

    COMMON_ATTRIBUTE_DEFINITIONS =
        Collections.unmodifiableCollection(Arrays.asList(
            SCHEMAS_ATTRIBUTE_DEFINITION, ID_ATTRIBUTE_DEFINITION,
            EXTERNAL_ID_ATTRIBUTE_DEFINITION, META_ATTRIBUTE_DEFINITION));
  }

  /**
   * Gets property descriptors for the given class.
   *
   * @param cls The class to get the property descriptors for.
   * @return a collection of property values.
   * @throws java.beans.IntrospectionException throw if there are any
   * introspection errors.
   */
  public static Collection<PropertyDescriptor>
  getPropertyDescriptors(final Class<?> cls)
      throws IntrospectionException
  {
    BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
    PropertyDescriptor[] propertyDescriptors =
        beanInfo.getPropertyDescriptors();
    return Arrays.asList(propertyDescriptors);
  }

  /**
   * Gets schema attributes for the given class.
   *
   * @param cls Class to get the schema attributes for.
   * @return a collection of attributes.
   * @throws IntrospectionException thrown if an introspection error occurs.
   */
  @Transient
  public static Collection<AttributeDefinition> getAttributes(
      final Class<?> cls)
      throws IntrospectionException
  {
    Stack<String> classesProcessed = new Stack<>();
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
    if(!cls.isAssignableFrom(AttributeDefinition.class) &&
        classesProcessed.contains(className))
    {
      throw new RuntimeException("Cycles detected in Schema");
    }

    Collection<PropertyDescriptor> propertyDescriptors =
        getPropertyDescriptors(cls);
    Collection<AttributeDefinition> attributes =
        new ArrayList<AttributeDefinition>();

    for(PropertyDescriptor propertyDescriptor : propertyDescriptors)
    {
      if(propertyDescriptor.getName().equals("subAttributes") &&
          cls.isAssignableFrom(AttributeDefinition.class) &&
          classesProcessed.contains(className))
      {
        // Skip second nesting of subAttributes the second time around
        // since there is no subAttributes of subAttributes in SCIM.
        continue;
      }
      AttributeDefinition.Builder attributeBuilder =
          new AttributeDefinition.Builder();

      Field field = findField(cls, propertyDescriptor.getName());

      if(field == null)
      {
        continue;
      }
      Attribute schemaProperty = null;
      JsonProperty jsonProperty = null;
      if(field.isAnnotationPresent(Attribute.class))
      {
        schemaProperty = field.getAnnotation(Attribute.class);
      }
      if(field.isAnnotationPresent(JsonProperty.class))
      {
        jsonProperty = field.getAnnotation(JsonProperty.class);
      }

      // Only generate schema for annotated fields.
      if(schemaProperty == null)
      {
        continue;
      }

      addName(attributeBuilder, propertyDescriptor, jsonProperty);
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
   * @param jsonProperty the Jackson JsonProperty annotation for the field.
   * @return this.
   */
  private static AttributeDefinition.Builder addName(
      final AttributeDefinition.Builder attributeBuilder,
      final PropertyDescriptor propertyDescriptor,
      final JsonProperty jsonProperty)
  {
    if(jsonProperty != null &&
        !jsonProperty.value().equals(JsonProperty.USE_DEFAULT_NAME))
    {
      attributeBuilder.setName(jsonProperty.value());
    }
    else
    {
      attributeBuilder.setName(propertyDescriptor.getName());
    }

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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
  {
    if(schemaProperty != null)
    {
      attributeBuilder.addCanonicalValues(schemaProperty.canonicalValues());
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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
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
      final Attribute schemaProperty)
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
        (cls == float.class) ||
        (cls == BigDecimal.class))
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
    else if((cls == byte[].class))
    {
      return AttributeDefinition.Type.BINARY;
    }
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
    Schema schemaAnnotation = cls.getAnnotation(Schema.class);

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
   * Gets the id of the schema from the annotation of the class
   * passed in.
   *
   * @param cls class to find the schema id property of the annotation from.
   * @return the id of the schema, or {@code null} if it was not provided.
   */
  public static String getSchemaIdFromAnnotation(final Class<?> cls)
  {
    Schema schema = cls.getAnnotation(Schema.class);
    return SchemaUtils.getSchemaIdFromAnnotation(schema);
  }

  /**
   * Gets the id property from schema annotation.  If the the id
   * attribute was {@code null}, a schema id is generated.
   *
   * @param schemaAnnotation the SCIM SchemaInfo annotation.
   * @return the id of the schema, or {@code null} if it was not provided.
   */
  private static String getSchemaIdFromAnnotation(
      final Schema schemaAnnotation)
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
    Schema schema = (Schema)cls.getAnnotation(Schema.class);
    return SchemaUtils.getNameFromSchemaAnnotation(schema);
  }

  /**
   * Gets the name property from schema annotation.
   *
   * @param schemaAnnotation the SCIM SchemaInfo annotation.
   * @return the name of the schema or a generated name.
   */
  private static String getNameFromSchemaAnnotation(
      final Schema schemaAnnotation)
  {
    if(schemaAnnotation != null)
    {
      return schemaAnnotation.name();
    }

    return null;
  }

  /**
   * Fetches the schema urn from a {@code Class}.
   *
   * @param cls The class of the object.
   * @return The schema urn for the object.
   */
  public static String getSchemaUrn(final Class<?> cls)
  {
    // The 'schemaId' is the URN. Make sure it begins with the "urn:" prefix.
    // The 'name' field is a human-friendly name for the object.
    String schemaId =
        SchemaUtils.getSchemaIdFromAnnotation(cls);

    if ((schemaId == null) || (schemaId.isEmpty()))
    {
      schemaId = cls.getCanonicalName();
    }

    // If the schema ID doesn't appear to be a valid URN, append the data to an
    // "urn:" prefix.
    return forceToBeUrn(schemaId);
  }


  /**
   * Returns true if the string passed in appears to be a urn.
   * That determination is made by looking to see if the string
   * starts with "{@code urn:}".
   *
   * @param string the string to check.
   * @return true if it's a urn, or false if not.
   */
  public static boolean isUrn(final String string)
  {
    return StaticUtils.toLowerCase(string).startsWith("urn:") &&
        string.length() > 4;
  }

  /**
   * Will force the string passed in to look like a urn.  If the
   * string starts with "{@code urn:}" it will be returned as is, however
   * if the string starts with anything else, this method will
   * prepend "{@code urn:}".  This is mainly so that if we have a class that
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
