/*
 * Copyright 2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.types.devices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Objects;


/**
 * This class represents a FIDO Device Onboard (FDO) device extension defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944">RFC 9944</a>.
 * FIDO Device Onboard is an automated service that detects devices connected to
 * a network, and a device management service establishes secure connections to
 * these devices by leveraging public key credentials. For more background on
 * devices, see the {@link DeviceResource} class.
 * <br><br>
 *
 * This object contains a single field, {@code fdoVoucher}. This is a
 * cryptographic ownership voucher that is stored as a PEM-encoded object. It is
 * declared as write-only and must never appear in SCIM service responses.
 * Furthermore, the SCIM service MUST know how to process the voucher, either
 * directly or by forwarding it along to an owner process.
 * <br><br>
 *
 * The following JSON represents a device with an FDO extension:
 * <pre>
 * {
 *   "schemas": [
 *     "urn:ietf:params:scim:schemas:core:2.0:Device",
 *     "urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device"
 *   ],
 *   "id": "e9e30dba",
 *   "meta": {
 *     "resourceType": "Device",
 *     "location": "https://example.com/v2/Devices/e9e30dba"
 *   },
 *   "displayName": "Custom FDO Device",
 *   "active": true,
 *   "urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device": {
 *     "fdoVoucher": "pemEncodedVoucherValue"
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * This device can be created with the following Java code:
 * <pre><code>
 *   String voucher = getPemEncodedVoucher();
 *
 *   DeviceResource device = new DeviceResource()
 *       .setDisplayName("Custom FDO Device")
 *       .setActive(true)
 *       .setDeviceExtension(new FdoDeviceExtension(voucher));
 *   device.setId("e9e30dba");
 *   device.setMeta(new Meta()
 *       .setResourceType("Device")
 *       .setLocationString("https://example.com/v2/Devices/e9e30dba"));
 * </code></pre>
 */
@Schema(
    id="urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device",
    name = "FIDO Device Onboard",
    description = "FIDO Device Onboard extension for a Device resource")
public class FdoDeviceExtension extends DeviceExtension
{
  @NotNull
  @Attribute(description = "The FDO ownership voucher for this device. This is"
      + " a cryptographic secret that should never be returned in responses.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.WRITE_ONLY,
      returned = AttributeDefinition.Returned.NEVER,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String fdoVoucher = "";


  /**
   * Creates a FIDO Device Onboard (FDO) extension object.
   *
   * @param fdoVoucher  The PEM-encoded voucher.
   */
  @JsonCreator
  public FdoDeviceExtension(
      @NotNull @JsonProperty(value = "fdoVoucher", required = true)
      final String fdoVoucher)
  {
    setFdoVoucher(fdoVoucher);
  }

  /**
   * Retrieves the FDO ownership voucher.
   *
   * @return The FDO voucher.
   */
  @NotNull
  public String getFdoVoucher()
  {
    return fdoVoucher;
  }

  /**
   * Specifies the PEM-encoded FDO ownership voucher.
   *
   * @param voucher The FDO voucher.
   * @return This FDO extension.
   */
  @NotNull
  public FdoDeviceExtension setFdoVoucher(@NotNull final String voucher)
  {
    this.fdoVoucher = Objects.requireNonNull(voucher);
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this FDO extension.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this extension,
   *            or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    return o instanceof FdoDeviceExtension that
        && Objects.equals(fdoVoucher, that.fdoVoucher);
  }

  /**
   * Retrieves a hash code for this FDO extension.
   *
   * @return  A hash code for this FDO extension.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(fdoVoucher);
  }

  /**
   * Retrieves a string representation of this FDO extension, with the
   * {@code fdoVoucher} value redacted.
   *
   * @return  A string representation of this FDO extension.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.toRedactedString(this, "fdoVoucher");
  }
}
