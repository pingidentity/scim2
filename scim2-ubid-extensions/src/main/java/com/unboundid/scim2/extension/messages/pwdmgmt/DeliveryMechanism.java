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

package com.unboundid.scim2.extension.messages.pwdmgmt;


import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.annotations.Attribute;

/**
 * This class contains information (such as the name and recipient) about
 * the delivery mechanism used to deliver password reset tokens.  In the
 * context of a request this class represents the preferred delivery mechanism.
 * In the context of a response, it represents the actual mechanism used.
 */
public class DeliveryMechanism
{
  /**
   * The name of the delivery mechanism.
   */
  @Attribute(description = "The delivery mechanism requested or used " +
      "(depending on the context).",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String name;

  /**
   * The recipient address (email address, phone number, etc).
   */
  @Attribute(description = "The recipient address (email address, phone " +
      "number, etc.) requested or used (depending on the context).",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String recipient;

  @Attribute(description = "True if the delivery mechanism is supported " +
      "or false if it is not.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private Boolean supported;

  /**
   * Private no-arg constructor for jackson.
   */
  private DeliveryMechanism()
  {
  }

  /**
   * Constructs a delivery mechanism.
   *
   * @param name the delivery mechanism name to use.
   * @param recipient the recipient to use.
   */
  public DeliveryMechanism(final String name, final String recipient)
  {
    this(name, recipient, null);
  }

  /**
   * Constructs a delivery mechanism.
   * @param name the name of the delivery mechanism.
   * @param recipient the recipient id.
   * @param supported true if the delivery mechanism is supported, or false
   *                  if not.  This parameter really only makes sense if
   *                  this is a response, and will be ignored if this is
   *                  part of a request.
   */
  public DeliveryMechanism(final String name,
                           final String recipient, final Boolean supported)
  {
    this.name = name;
    this.recipient = recipient;
    this.supported = supported;
  }

  /**
   * Gets the name.
   *
   * @return the name.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the recipient.
   *
   * @return the recipient.
   */
  public String getRecipient()
  {
    return recipient;
  }

  /**
   * Returns true if the delivery mechanism is supported, or false if not.
   *
   * @return true if the delivery mechanism is supported, or false if not.
   */
  public Boolean isSupported()
  {
    return supported;
  }
}
