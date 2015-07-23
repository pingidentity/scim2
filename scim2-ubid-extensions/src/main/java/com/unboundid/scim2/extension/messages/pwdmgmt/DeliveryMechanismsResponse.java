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

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the response returned from a call to get
 * the delivery mechanisms available for a user.
 */
@Schema(description = "Response returned by the SCIM request for supported" +
    "delivery mechanisms.",
    id = "urn:unboundid:schemas:2.0:DeliveryMechanismsResponse",
    name = "DeliveryMechanismsResponse")
public class DeliveryMechanismsResponse extends BaseScimResource
{
  @Attribute(description = "The delivery mechanisms available.",
      multiValueClass = DeliveryMechanism.class)
  private List<DeliveryMechanism> deliveryMechanisms =
      new ArrayList<DeliveryMechanism>();

  /**
   * Gets the delivery mechanisms.
   *
   * @return the list of delivery mechanisms that was returned.
   */
  public List<DeliveryMechanism> getDeliveryMechanisms()
  {
    return deliveryMechanisms;
  }
}

