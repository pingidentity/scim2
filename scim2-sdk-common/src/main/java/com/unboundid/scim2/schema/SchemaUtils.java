/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.model.BaseScimObject;

import javax.lang.model.type.NullType;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Stack;
import java.util.TimeZone;

/**
 * Created by dvernon on 3/27/15.
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
   * @param schema the name of the parent schema.  This will be set in all
   *               attributes.
   * @return a collection of attributes.
   * @throws IntrospectionException thrown if an introspection error occurs.
   */
  @Transient
  public static Collection<AttributeDefinition> getAttributes(
      final Class cls, final String schema)
      throws IntrospectionException
  {
    Stack<String> classesProcessed = new Stack<String>();
    return getAttributes(classesProcessed, cls, schema);
  }

  /**
   * Gets SCIM schema attributes for a class.
   *
   * @param classesProcessed a stack containing the classes processed prior
   *                         to this class.  This is used for cycle detection.
   * @param cls the class to get the attributes for.
   * @param schemaUrn the urn that will be applied to the attributes.
   * @return a collection of SCIM schema attributes for the class.
   * @throws IntrospectionException thrown if an error occurs during
   *    Introspection.
   */
  private static Collection<AttributeDefinition> getAttributes(
      final Stack<String> classesProcessed, final Class<?> cls,
      final String schemaUrn)
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
      addReferenceType(attributeBuilder, schemaProperty);
      addMutability(attributeBuilder, schemaProperty);
      addMultiValued(attributeBuilder, propertyDescriptor, schemaProperty);
      addCanonicalValues(attributeBuilder, schemaProperty);

      Class propertyCls = propertyDescriptor.getPropertyType();

      // if this is a multivalued attribute the real sub attribute class is the
      // the one specified in the annotation, not the list, set, array, etc.
      if((schemaProperty != null) &&
          (schemaProperty.multiValueClass() != NullType.class))
      {
        propertyCls = schemaProperty.multiValueClass();
      }

      Collection<AttributeDefinition> subAttributes = null;
      if(BaseScimObject.class.isAssignableFrom(propertyCls))
      {
        // Add this class to the list to allow cycle detection
        classesProcessed.push(cls.getCanonicalName());
        subAttributes = getAttributes(classesProcessed, propertyCls, schemaUrn);
        classesProcessed.pop();
      }

      attributeBuilder.
          setType(getAttributeType(propertyCls)).
          setSubAttributes(subAttributes);

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
      String[] canonicalValues = schemaProperty.canonicalValues();
      if(canonicalValues.length == 0)
      {
        attributeBuilder.setCanonicalValues(null);
      }
      else
      {
        attributeBuilder.setCanonicalValues(
            new HashSet<String>(Arrays.asList(canonicalValues)));
      }
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
   * This method will find the reference type for the attribute, and add
   * it to the builder.
   *
   * @param attributeBuilder builder for a scim attribute.
   * @param schemaProperty the schema property annotation for the field
   *                       to build an attribute for.
   * @return this.
   */
  private static AttributeDefinition.Builder addReferenceType(
      final AttributeDefinition.Builder attributeBuilder,
      final SchemaProperty schemaProperty)
  {
    if(schemaProperty != null)
    {
      String referenceType = schemaProperty.referenceType();
      if(referenceType.isEmpty())
      {
        attributeBuilder.setReferenceType(null);
      }
      else
      {
        attributeBuilder.setReferenceType(schemaProperty.referenceType());
      }
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
  public static SchemaDefinition getSchema(final Class<?> cls)
      throws IntrospectionException
  {
    SchemaInfo schemaAnnotation =
        (SchemaInfo)cls.getAnnotation(SchemaInfo.class);

    // Only generate schema for annotated classes.
    if(schemaAnnotation == null)
    {
      return null;
    }

    SchemaDefinition schema = new SchemaDefinition();
    if(schemaAnnotation != null)
    {
      schema.setName(schemaAnnotation.name());
      schema.setId(schemaAnnotation.id());
      schema.setDescription(schemaAnnotation.description());
    }
    schema.setAttributes(getAttributes(cls, schema.getId()));
    return schema;
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
  private static Field findField(final Class<?> cls,
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
    if(cls.isArray() || Collection.class.isAssignableFrom(cls))
    {
      return true;
    }

    return false;
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
    mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    return mapper;
  }

  /**
   * Gets the id of the schema from the annotation of the class
   * passed in.
   *
   * @param cls class to find the schema name property of the annotation from.
   * @return the name of the schema.
   */
  public static String getSchemaIdFromAnnotation(final Class<?> cls)
  {
    SchemaInfo schema = (SchemaInfo)cls.getAnnotation(SchemaInfo.class);
    return SchemaUtils.getSchemaIdFromAnnotation(schema);
  }

  /**
   * Gets the name property from schema annotation.  If the the name
   * attribute was null, a schema name is generated.
   *
   * @param schemaAnnotation the SCIM SchemaInfo annotation.
   * @return the name of the schema or a generated name.
   */
  private static String getSchemaIdFromAnnotation(
      final SchemaInfo schemaAnnotation)
  {
    if(schemaAnnotation != null)
    {
      return schemaAnnotation.id();
    }
    else
    {
      return null;
    }
  }
}
