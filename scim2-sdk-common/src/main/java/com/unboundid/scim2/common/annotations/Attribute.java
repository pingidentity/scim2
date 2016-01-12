/*
 * Copyright 2015-2016 UnboundID Corp.
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

package com.unboundid.scim2.common.annotations;


import com.unboundid.scim2.common.types.AttributeDefinition;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for "getter" methods of a SCIM object.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Attribute
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
  AttributeDefinition.Returned returned()
      default AttributeDefinition.Returned.DEFAULT;

  /**
   * The uniqueness constraint for the attribute.
   */
  AttributeDefinition.Uniqueness uniqueness()
      default AttributeDefinition.Uniqueness.NONE;

  /**
   * The reference types for the attribute.
   */
  String[] referenceTypes() default {};

  /**
   * The mutability constraint for the attribute.
   */
  AttributeDefinition.Mutability mutability() default
      AttributeDefinition.Mutability.READ_WRITE;

  /**
   * If the attribute is multi-value, this holds the type of the
   * child object.
   */
  Class multiValueClass() default NullType.class;
}
