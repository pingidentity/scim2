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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

/**
 * Class used to deliver the response when a password token
 * reset is requested via the SCIM REST api.
 */
@Schema(description = "Response returned by the SCIM password reset token " +
    "REST methods.",
    id = "urn:unboundid:schemas:2.0:TokenDeliveryResponse",
    name = "TokenDeliveryResponse")
public class TokenDeliveryResponse extends BaseScimResource
{
  @Attribute(description = "The delivery mechanism used.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private DeliveryMechanism deliveryMechanism;

  @Attribute(description = "Detail message about the delivery.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String message;

  /**
   * private no-arg constructor for jackson.
   */
  private TokenDeliveryResponse()
  {

  }

  /**
   * Constructs a TokenDeliveryResponse.
   *
   * @param deliveryMechanism the delivery mechanism used to send the
   *                          reset token.
   * @param message additional information (if available).
   */
  public TokenDeliveryResponse(final DeliveryMechanism deliveryMechanism,
                               final String message)
  {
    this.deliveryMechanism = deliveryMechanism;
    this.message = message;
  }

  /**
   * Gets the delivery mechanism used.
   *
   * @return the delivery mechanism used.
   */
  public DeliveryMechanism getDeliveryMechanism()
  {
    return deliveryMechanism;
  }

  /**
   * Gets the detailed message about the delivery.
   *
   * @return the detailed message about the delivery
   */
  public String getMessage()
  {
    return message;
  }
}
