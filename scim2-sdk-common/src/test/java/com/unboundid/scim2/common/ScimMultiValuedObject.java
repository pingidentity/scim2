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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.net.URI;

/**
 * This class is used to represent SCIM multi-valued objects.  To create
 * a SCIM multi-valued attribute, just define a java.util.List of this
 * type (or a class that extends this one).
 * @param <T> the type of the value of this object.
 */
public class ScimMultiValuedObject<T>
{
  /**
   * A string describing the type of this attribute's value.  This should
   * probably be one of the SCIM datatypes.
   */
  @Attribute(
      description = "The type for this multi-valued attribute's value.")
  String type;

  /**
   * A boolean that indicates if this is the primary value for this
   * multi-valued object.  Only one of the attribute values in a list
   * should have this boolean set to true.
   */
  @Attribute(description =
      "Boolean to indicate if this is the primary value.")
  boolean primary;

  /**
   * The display name for this value.  For example:  "Work phone number".
   */
  @Attribute(description = "The display name of the attribute attribute.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  String display;

  /**
   * The actual value of this occurrence of a multi-valued attribute.
   */
  @Attribute(description = "The actual value of the attribute attribute.")
  T value;

  /**
   * The URI of the value if it is a reference.  If the value is not a
   * reference, this should not be set.
   */
  @Attribute(description = "The URI of the value if it is a reference.")
  @JsonProperty("$ref")
  URI ref;

  /**
   * Gets the SCIM type of the object.
   *
   * @return the SCIM type of the object.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Sets the SCIM type of the object.
   *
   * @param type the SCIM type of the object.
   */
  public void setType(final String type)
  {
    this.type = type;
  }

  /**
   * Boolean set to true if this is the primary value of a multi-valued
   * attribute.
   *
   * @return true if this is the primary value for the multi-valued attribute,
   * or false if not.
   */
  public boolean isPrimary()
  {
    return primary;
  }

  /**
   * Sets a boolean indicated if this is the primary value of the multi-valued
   * attribute.
   * @param primary true if this is the primary value for the multi-valued
   *                attribute, or false if not.
   */
  public void setPrimary(final boolean primary)
  {
    this.primary = primary;
  }

  /**
   * Gets the display name for this value of the multi-valued attribute.
   *
   * @return the display name for this value of the multi-valued attribute.
   */
  public String getDisplay()
  {
    return display;
  }

  /**
   * Sets the display name for this value of the multi-valued attribute.
   * @param display the display name for this value of the multi-valued
   *                attribute.
   */
  public void setDisplay(final String display)
  {
    this.display = display;
  }

  /**
   * Get's the value of this occurrence of the multi-valued attribute.
   * @return the value of this occurrence of the multi-valued attribute.
   */
  public T getValue()
  {
    return value;
  }

  /**
   * Sets the value of this occurrence of the multi-valued attribute.
   * @param value the value of this occurrence of the multi-valued attribute.
   */
  public void setValue(final T value)
  {
    this.value = value;
  }

  /**
   * Gets the URI for this value of the multi-valued attribute if the value
   * is a reference.
   *
   * @return the URI for this value of the multi-valued attribute if the value
   * is a reference.
   */
  public URI getRef()
  {
    return ref;
  }

  /**
   * Sets the URI for this value of the multi-valued attribute if the value
   * is a reference.
   * @param ref the URI for this value of the multi-valued attribute if the
   *            value is a reference.
   */
  public void setRef(final URI ref)
  {
    this.ref = ref;
  }
}
