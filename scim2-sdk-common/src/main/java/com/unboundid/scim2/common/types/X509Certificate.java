/*
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * A public key certificate in {@code X.509} form that can be assigned to a
 * {@link UserResource}.
 */
public class X509Certificate
{
  @Nullable
  @Attribute(description = "The value of a X509 certificate.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private byte[] value;

  @Nullable
  @Attribute(description = "A human readable name, primarily used for " +
      "display purposes.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String display;

  @Nullable
  @Attribute(description = "A label indicating the attribute's " +
      "function.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String type;

  @Nullable
  @Attribute(description = "A Boolean value indicating the 'primary' " +
      "or preferred attribute value for this attribute. The primary " +
      "attribute value 'true' MUST appear no more than once.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT)
  private Boolean primary;

  /**
   * Retrieves the value of a X509 certificate.
   *
   * @return The value of a X509 certificate.
   */
  @Nullable
  public byte[] getValue()
  {
    return value;
  }

  /**
   * Specifies the value of a X509 certificate.
   *
   * @param value The value of a X509 certificate.
   * @return This object.
   */
  @NotNull
  public X509Certificate setValue(@Nullable final byte[] value)
  {
    this.value = value;
    return this;
  }

  /**
   * Specifies the display name, primarily used for display purposes.
   *
   * @return The display name.
   */
  @Nullable
  public String getDisplay()
  {
    return display;
  }

  /**
   * Specifies the display name, primarily used for display purposes.
   *
   * @param display The display name.
   * @return This object.
   */
  @NotNull
  public X509Certificate setDisplay(@Nullable final String display)
  {
    this.display = display;
    return this;
  }

  /**
   * Retrieves the label indicating the attribute's function.
   *
   * @return The label indicating the attribute's function.
   */
  @Nullable
  public String getType()
  {
    return type;
  }

  /**
   * Specifies the label indicating the attribute's function.
   *
   * @param type The label indicating the attribute's function.
   * @return This object.
   */
  @NotNull
  public X509Certificate setType(@Nullable final String type)
  {
    this.type = type;
    return this;
  }

  /**
   * Retrieves the Boolean value indicating the 'primary' or preferred
   * attribute value for this attribute.
   *
   * @return The Boolean value indicating the 'primary' or preferred
   * attribute value for this attribute.
   */
  @Nullable
  public Boolean getPrimary()
  {
    return primary;
  }

  /**
   * Specifies the Boolean value indicating the 'primary' or preferred
   * attribute value for this attribute.
   *
   * @param primary The Boolean value indicating the 'primary' or preferred
   * attribute value for this attribute.
   * @return This object.
   */
  @NotNull
  public X509Certificate setPrimary(@Nullable final Boolean primary)
  {
    this.primary = primary;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this X.509 public
   * certificate.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this
   *            certificate, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    X509Certificate that = (X509Certificate) o;
    if (!Objects.equals(display, that.display))
    {
      return false;
    }
    if (!Objects.equals(type, that.type))
    {
      return false;
    }
    if (!Objects.equals(primary, that.primary))
    {
      return false;
    }
    return Arrays.equals(value, that.value);
  }

  /**
   * Retrieves a hash code for this X.509 certificate.
   *
   * @return  A hash code for this X.509 certificate.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(display, type, primary, Arrays.hashCode(value));
  }
}
