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

package com.unboundid.scim2.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for SCIM resource classes.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ResourceType
{
  /**
   * The description for the object.
   */
  String description();

  /**
   * The name for the object.  This is a human readable
   * name.
   */
  String name();

  /**
   * The primary/base resource class.
   */
  Class<?> schema();

  /**
   * The required schema extension resource classes.
   */
  Class<?>[] requiredSchemaExtensions() default {};

  /**
   * The optional schema extension resource classes.
   */
  Class<?>[] optionalSchemaExtensions() default {};
}
