/*
 * Copyright 2015-2024 Ping Identity Corporation
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
import com.unboundid.scim2.common.types.UserResource;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to record useful properties about attributes on SCIM
 * resources. This helps display attribute definitions directly on member
 * variables of a class, where the class represents a SCIM resource type. For
 * example, if an attribute must be provided when a resource type is created,
 * the {@link #isRequired} field should be {@code true}.
 * <br><br>
 * The {@link UserResource} class uses this annotation to highlight attribute
 * definitions that are defined in RFC 7643.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Attribute
{
  /**
   * Determines if the attribute value is case-sensitive.
   *
   * @return A flag indicating the attribute value's case sensitivity.
   */
  boolean isCaseExact() default true;

  /**
   * Determines if the attribute value is required.
   *
   * @return A flag indicating if the attribute value is required.
   */
  boolean isRequired() default false;

  /**
   * The description of the attribute.
   *
   * @return The description of the attribute.
   */
  @NotNull String description();

  /**
   * The canonical values that may appear in an attribute.
   *
   * @return The canonical values that may appear in an attribute.
   */
  @NotNull String[] canonicalValues() default {};

  /**
   * The return constraint for the attribute.
   *
   * @return The return constraint for the attribute.
   */
  @NotNull
  AttributeDefinition.Returned returned()
      default AttributeDefinition.Returned.DEFAULT;

  /**
   * The uniqueness constraint for the attribute.
   *
   * @return The uniqueness constraint for the attribute.
   */
  @NotNull
  AttributeDefinition.Uniqueness uniqueness()
      default AttributeDefinition.Uniqueness.NONE;

  /**
   * The reference types for the attribute.
   *
   * @return The reference types for the attribute.
   */
  @NotNull String[] referenceTypes() default {};

  /**
   * The mutability constraint for the attribute.
   *
   * @return The mutability constraint for the attribute.
   */
  @NotNull
  AttributeDefinition.Mutability mutability()
      default AttributeDefinition.Mutability.READ_WRITE;

  /**
   * If the attribute is multi-value, this holds the type of the
   * child object.
   *
   * @return For a multi-valued attribute, the type of the child object.
   */
  @NotNull Class<?> multiValueClass() default NullType.class;
}
