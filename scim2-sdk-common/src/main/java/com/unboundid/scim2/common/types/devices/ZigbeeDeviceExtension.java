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
import com.unboundid.scim2.common.utils.StaticUtils;

import java.util.List;
import java.util.Objects;


/**
 * This class represents a Zigbee device extension for a device as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944">RFC 9944</a>. Zigbee
 * is a suite of protocols that are generally used for low-power hardware
 * devices. For more background on devices, see {@link DeviceResource}.
 * <br><br>
 *
 * The following fields are defined:
 * <ul>
 *   <li> {@code deviceEui64Address}: A 64-bit Extended Unique Identifier
 *                                    (EUI-64) device address.
 *   <li> {@code versionSupport}:     One or more strings of all the Zigbee
 *                                    versions supported by the device.
 * </ul>
 * <br><br>
 *
 * Consider the following device with a Zigbee extension:
 * <pre>
 * {
 *   "schemas": [
 *       "urn:ietf:params:scim:schemas:core:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device"
 *   ],
 *   "id": "e9e30dba",
 *   "meta": {
 *     "resourceType": "Device",
 *     "created": "1970-01-23T04:56:22Z",
 *     "lastModified": "1970-05-13T04:42:34Z",
 *     "location": "https://example.com/v2/Devices/e9e30dba"
 *   },
 *   "displayName": "Zigbee Smart Light",
 *   "active": true,
 *   "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device": {
 *     "versionSupport": [ "3.0" ],
 *     "deviceEui64Address": "50:32:5F:FF:FE:E7:67:28"
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * This JSON value can be created with the following Java code:
 * <pre><code>
 *   // Create the device extension.
 *   ZigbeeDeviceExtension zigbee = new ZigbeeDeviceExtension(
 *       "50:32:5F:FF:FE:E7:67:28", "3.0");
 *
 *   // Create the device.
 *   DeviceResource device = new DeviceResource()
 *       .setDisplayName("Zigbee Smart Light")
 *       .setActive(true)
 *       .setDeviceExtension(zigbee);
 *   device.setId("e9e30dba");
 *   device.setMeta(new Meta()
 *       .setResourceType("Device")
 *       .setCreatedMillis(1918582000L)
 *       .setLastModifiedMillis(11421754000L)
 *       .setLocationString("https://example.com/v2/Devices/e9e30dba"));
 * </code></pre>
 */
@Schema(id = "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device",
    name = "Zigbee",
    description = "Zigbee extension for a Device resource")
public class ZigbeeDeviceExtension extends DeviceExtension
{
  /**
   * The regex matching Extended Unique Identifiers as defined by RFC 9944.
   */
  @NotNull
  public static final String EUI64_PATTERN =
      "^[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){7}$";

  @NotNull
  @Attribute(description = "The Zigbee versions supported by this device.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = String.class)
  private final List<String> versionSupport;

  @NotNull
  @Attribute(description = "The IEEE EUI-64 address of this Zigbee device."
      + " Must match the pattern: " + EUI64_PATTERN,
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      pattern = EUI64_PATTERN)
  private final String deviceEui64Address;

  /**
   * Creates a new Zigbee device extension.
   *
   * @param versionSupport      The Zigbee versions supported by this device.
   * @param deviceEui64Address  The 64-bit Extended Unique Identifier of this
   *                            Zigbee device.
   */
  @JsonCreator
  public ZigbeeDeviceExtension(
      @NotNull @JsonProperty(value = "versionSupport", required = true)
      final List<String> versionSupport,
      @NotNull @JsonProperty(value = "deviceEui64Address", required = true)
      final String deviceEui64Address)
  {
    this.versionSupport = List.copyOf(versionSupport);
    this.deviceEui64Address = Objects.requireNonNull(deviceEui64Address);
  }

  /**
   * Creates a new Zigbee device extension.
   *
   * @param deviceEui64Address  The 64-bit Extended Unique Identifier.
   * @param version             A Zigbee version supported by this device.
   * @param versions            Additional versions supported by this device.
   */
  public ZigbeeDeviceExtension(@NotNull final String deviceEui64Address,
                               @NotNull final String version,
                               @Nullable final String... versions)
  {
    this(StaticUtils.toList(version, versions), deviceEui64Address);
  }

  /**
   * Fetches the 64-bit Extended Unique Identifier of this Zigbee device.
   *
   * @return The EUI-64 address.
   */
  @NotNull
  public String getDeviceEui64Address()
  {
    return deviceEui64Address;
  }

  /**
   * Fetches the Zigbee versions supported by this device.
   *
   * @return The list of supported Zigbee versions.
   */
  @NotNull
  public List<String> getVersionSupport()
  {
    return versionSupport;
  }

  /**
   * Indicates whether the provided object is equal to this Zigbee device
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

    return o instanceof ZigbeeDeviceExtension that
        && Objects.equals(deviceEui64Address, that.deviceEui64Address)
        && Objects.equals(versionSupport, that.versionSupport);
  }

  /**
   * Retrieves a hash code for this Zigbee device extension.
   *
   * @return  A hash code for this Zigbee device extension.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(deviceEui64Address, versionSupport);
  }
}
