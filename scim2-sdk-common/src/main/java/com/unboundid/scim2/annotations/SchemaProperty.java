/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.annotations;


import com.unboundid.scim2.schema.SCIM2Attribute;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for "getter" methods of a scim object.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface SchemaProperty
{
  /**
   * Determines if the attribute value is case sensitive.
   */
  boolean isCaseExact() default true;

  /**
   * Determines if the attribute value is required.
   */
  boolean isRequired() default false;

  /**
   * The description of the attribute.
   */
  String description();

  /**
   * The canonical values that may appear in an attribute.
   */
  String[] canonicalValues() default {};

  /**
   * The return constraint for the attribute.
   */
  SCIM2Attribute.Returned returned() default SCIM2Attribute.Returned.DEFAULT;

  /**
   * The uniqueness constraint for the attribute.
   */
  SCIM2Attribute.Uniqueness uniqueness() default SCIM2Attribute.Uniqueness.NONE;

  /**
   * The reference type for the attribute.
   */
  String referenceType() default "";

  /**
   * The mutability constraint for the attribute.
   */
  SCIM2Attribute.Mutability mutability() default
      SCIM2Attribute.Mutability.READ_WRITE;

  /**
   * If the attribute is multi-value, this holds the type of the
   * child object.
   */
  Class multiValueClass() default NullType.class;
}
