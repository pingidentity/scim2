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

/**
 * Instant messaging address for the user.
 */
public class InstantMessagingAddress
{
  @Nullable
  @Attribute(description = "Instant messaging address for the User.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String value;

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
      "function; e.g., 'aim', 'gtalk', 'mobile' etc.",
      isRequired = false,
      isCaseExact = false,
      canonicalValues = { "aim", "gtalk", "icq", "xmpp", "msn", "skype",
          "qq", "yahoo" },
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String type;

  @Nullable
  @Attribute(description = "A Boolean value indicating the 'primary' " +
      "or preferred attribute value for this attribute, e.g., the " +
      "preferred messenger or primary messenger. The primary attribute " +
      "value 'true' MUST appear no more than once.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT)
  private Boolean primary;

  /**
   * Retrieves the instant messaging address for the User.
   *
   * @return The instant messaging address for the User.
   */
  @Nullable
  public String getValue()
  {
    return value;
  }

  /**
   * Specifies the instant messaging address for the User.
   *
   * @param value The instant messaging address for the User.
   * @return This object.
   */
  @NotNull
  public InstantMessagingAddress setValue(@Nullable final String value)
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
  public InstantMessagingAddress setDisplay(@Nullable final String display)
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
  public InstantMessagingAddress setType(@Nullable final String type)
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
  public InstantMessagingAddress setPrimary(@Nullable final Boolean primary)
  {
    this.primary = primary;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this instant messaging
   * address.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this instant
   *            messaging address, or {@code false} if not.
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

    InstantMessagingAddress im = (InstantMessagingAddress) o;

    if (value != null ? !value.equals(im.value) : im.value != null)
    {
      return false;
    }
    if (display != null ? !display.equals(im.display) : im.display != null)
    {
      return false;
    }
    if (type != null ? !type.equals(im.type) : im.type != null)
    {
      return false;
    }
    return !(primary != null ? !primary.equals(im.primary) :
        im.primary != null);

  }

  /**
   * Retrieves a hash code for this instant messaging address.
   *
   * @return  A hash code for this instant messaging address.
   */
  @Override
  public int hashCode()
  {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (display != null ? display.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (primary != null ? primary.hashCode() : 0);
    return result;
  }
}
