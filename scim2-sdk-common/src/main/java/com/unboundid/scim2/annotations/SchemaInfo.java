/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class level annotation indicating the schema of a SCIM object.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SchemaInfo
{
  /**
   * The id for the object.
   */
  String id() default "urn:unboundid:schemas:broker:2.0";

  /**
   * The description for the object.
   */
  String description();

  /**
   * The name for the object.
   */
  String name();
}
