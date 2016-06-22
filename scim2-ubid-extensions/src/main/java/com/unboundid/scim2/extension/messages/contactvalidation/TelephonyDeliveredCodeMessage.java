/*
 * Copyright 2016 UnboundID Corp.
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

package com.unboundid.scim2.extension.messages.contactvalidation;

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.types.AttributeDefinition;

/**
 * Model for TelephonyDeliveredCode message.
 */
public class TelephonyDeliveredCodeMessage
{
  @Attribute(description = "The message to be delivered",
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      isRequired = true,
      returned = AttributeDefinition.Returned.NEVER)
  private String message;

  @Attribute(description = "The language and locale of the message.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE,
      returned = AttributeDefinition.Returned.NEVER)
  private String language;

  private TelephonyDeliveredCodeMessage()
  {
    // For Jackson.
  }

  /**
   * Create a new TelephonyDeliveredCodeMessage.
   *
   * @param message The message to be delivered.
   */
  public TelephonyDeliveredCodeMessage(final String message)
  {
    this.message = message;
  }

  /**
   * Retrieve the message to be delivered.
   * @return  The message to be delivered.
   */
  public String getMessage()
  {
    return message;
  }



  /**
   * Specify the message to be delivered.
   * @param message  The message to be delivered.
   */
  public void setMessage(final String message)
  {
    this.message = message;
  }



  /**
   * Retrieve the language and locale of the message.
   * @return  The language and locale of the message.
   */
  public String getLanguage()
  {
    return language;
  }



  /**
   * Specify the language and locale of the message.
   * @param language  The language and locale of the message.
   */
  public void setLanguage(final String language)
  {
    this.language = language;
  }
}
