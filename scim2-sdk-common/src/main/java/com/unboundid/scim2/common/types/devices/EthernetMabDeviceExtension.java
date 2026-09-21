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

import java.util.Objects;


/**
 * This class represents the Ethernet MAC Authentication Bypass (MAB) extension
 * per <a href="https://datatracker.ietf.org/doc/html/rfc9944#section-7.3">
 * RFC 9944 Section 7.3</a>. This extension enables a legacy means of weak
 * authentication that is supported in many wired Ethernet solutions. For more
 * background on devices, see the {@link DeviceResource} class.
 * <br><br>
 *
 * The following JSON represents this extension as it appears on a device
 * resource. This extension contains a single value, {@code deviceMacAddress},
 * which represents the Ethernet address to be provisioned onto the network.
 * <pre>
 * {
 *   "schemas": [
 *       "urn:ietf:params:scim:schemas:core:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:ethernet-mab:2.0:Device"
 *   ],
 *   "id": "e9e30dba",
 *   "meta": {
 *     "resourceType": "Device",
 *     "created": "1970-01-23T04:56:22Z",
 *     "lastModified": "1970-05-13T04:42:34Z",
 *     "location": "https://example.com/v2/Devices/e9e30dba"
 *   },
 *   "displayName": "Example Ethernet Device",
 *   "active": true,
 *   "urn:ietf:params:scim:schemas:extension:ethernet-mab:2.0:Device": {
 *     "deviceMacAddress": "2C:54:91:88:C9:E2"
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * This extension can be added to a device with the following Java code:
 * <pre><code>
 *   DeviceResource device = new DeviceResource()
 *       .setDisplayName("Example Ethernet Device")
 *       .setActive(true)
 *       .setDeviceExtension(new EthernetMabDeviceExtension("2C:54:91:88:C9:E2")
 *   );
 *   device.setId("e9e30dba");
 *   device.setMeta(new Meta()
 *       .setResourceType("Device")
 *       .setCreatedMillis(1918582000L)
 *       .setLastModifiedMillis(11421754000L)
 *       .setLocationString("https://example.com/v2/Devices/e9e30dba"));
 * </code></pre>
 */
@Schema(
    id = "urn:ietf:params:scim:schemas:extension:ethernet-mab:2.0:Device",
    name = "Ethernet MAB",
    description = "Ethernet MAB extension for a Device resource")
public class EthernetMabDeviceExtension extends DeviceExtension
{
  @NotNull
  @Attribute(description = "The Ethernet MAC address of this device.",
      isRequired = true,
      isCaseExact = false,
      pattern = MAC_PATTERN)
  private final String deviceMacAddress;

  /**
   * Creates a new Ethernet MAC Authentication Bypass (MAB) extension.
   *
   * @param deviceMacAddress  The Ethernet MAC address of this device.
   */
  @JsonCreator
  public EthernetMabDeviceExtension(
      @NotNull @JsonProperty(value = "deviceMacAddress", required = true)
      final String deviceMacAddress)
  {
    this.deviceMacAddress = Objects.requireNonNull(deviceMacAddress);
  }

  /**
   * Retrieves the Ethernet MAC address of this device.
   *
   * @return The MAC address.
   */
  @NotNull
  public String getDeviceMacAddress()
  {
    return deviceMacAddress;
  }

  /**
   * Indicates whether the provided object is equal to this Ethernet MAB
   * extension.
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

    return o instanceof EthernetMabDeviceExtension that
        && Objects.equals(deviceMacAddress, that.deviceMacAddress);
  }

  /**
   * Retrieves a hash code for this Ethernet MAB extension.
   *
   * @return  A hash code for this Ethernet MAB extension.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(deviceMacAddress);
  }
}
