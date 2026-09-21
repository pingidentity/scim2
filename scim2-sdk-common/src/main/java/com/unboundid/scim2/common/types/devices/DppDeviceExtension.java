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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.StaticUtils;

import java.util.List;
import java.util.Objects;


/**
 * This class represents a Device Provisioning Protocol extension for a device,
 * which allows enabling Wi-Fi Easy Connect in SCIM as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944">RFC 9944</a>. For
 * more background on devices, see the {@link DeviceResource} class.
 * <br><br>
 *
 * The following fields are defined on this extension:
 * <ul>
 *   <li> {@code dppVersion}:          An integer that represents the version of
 *                                     DPP the device supports.
 *   <li> {@code bootstrapKey}:        An Elliptic Curve Diffie-Hellman (ECDH)
 *                                     public key used for bootstrapping.
 *   <li> {@code deviceMacAddress}:    A MAC address stored as a string.
 *   <li> {@code serialNumber}:        An alphanumeric serial number that may
 *                                     also be passed as bootstrap information.
 *   <li> {@code classChannel}:        Values representing the global operating
 *                                     class and channel for bootstrap info.
 *   <li> {@code bootstrappingMethod}: Values representing the bootstrap methods
 *                                     available on the enrollee device.
 * </ul>
 * <br><br>
 *
 * The following JSON represents a device with a DPP extension:
 * <pre>
 * {
 *   "schemas": [
 *       "urn:ietf:params:scim:schemas:core:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device"
 *   ],
 *   "id": "e9e30dba",
 *   "meta": {
 *     "resourceType": "Device",
 *     "created": "1970-01-23T04:56:22Z",
 *     "lastModified": "1970-05-13T04:42:34Z",
 *     "location": "https://example.com/v2/Devices/e9e30dba"
 *   },
 *   "displayName": "Wi-Fi Example Monitor",
 *   "active": true,
 *   "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device": {
 *     "dppVersion": 2,
 *     "bootstrappingMethod": [ "QR" ],
 *     "bootstrapKey": "MDkwEwYHKoZIzj0CAQ...",
 *     "deviceMacAddress": "2C:54:91:88:C9:F2",
 *     "classChannel": [ "81/1", "115/36" ],
 *     "serialNumber": "4774LH2b4044"
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * This JSON value can be created with the following Java code:
 * <pre><code>
 *   // Create the device extension.
 *   DppDeviceExtension dpp = new DppDeviceExtension(2, "MDkwEwYHKoZIzj0CAQ...")
 *       .setBootstrappingMethod("QR")
 *       .setDeviceMacAddress("2C:54:91:88:C9:F2")
 *       .setClassChannel("81/1", "115/36")
 *       .setSerialNumber("4774LH2b4044");
 *
 *   // Create the device.
 *   DeviceResource device = new DeviceResource()
 *       .setDisplayName("Wi-Fi Example Monitor")
 *       .setActive(true)
 *       .setDeviceExtension(dpp);
 *   device.setId("e9e30dba");
 *   device.setMeta(new Meta()
 *       .setResourceType("Device")
 *       .setCreatedMillis(1918582000L)
 *       .setLastModifiedMillis(11421754000L)
 *       .setLocationString("https://example.com/v2/Devices/e9e30dba"));
 * </code></pre>
 */
@Schema(id = "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device",
    name = "Wi-Fi Easy Connect",
    description = "DPP extension for a Device resource")
@JsonPropertyOrder({"dppVersion", "bootstrappingMethod", "bootstrapKey"})
public class DppDeviceExtension extends DeviceExtension
{
  @Attribute(description = "The DPP protocol version supported by this"
      + " device.",
      isRequired = true)
  private final int dppVersion;

  @NotNull
  @Attribute(description =
      "The bootstrapping methods supported by this DPP device.",
      isCaseExact = false,
      multiValueClass = String.class)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> bootstrappingMethod = List.of();

  @NotNull
  @Attribute(description = "The DPP bootstrapping public key for this"
      + " device. This is a cryptographic secret that is never returned.",
      isRequired = true,
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.WRITE_ONLY,
      returned = AttributeDefinition.Returned.NEVER,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String bootstrapKey;

  /**
   * Creates a new Device Provisioning Protocol (DPP) extension.
   *
   * @param dppVersion    The DPP version supported by this device.
   * @param bootstrapKey  The DPP bootstrapping public key for this device.
   */
  @JsonCreator
  public DppDeviceExtension(
      @NotNull @JsonProperty(value = "dppVersion", required = true)
      final Integer dppVersion,
      @NotNull @JsonProperty(value = "bootstrapKey", required = true)
      final String bootstrapKey)
  {
    this.dppVersion = Objects.requireNonNull(dppVersion);
    this.bootstrapKey = Objects.requireNonNull(bootstrapKey);
  }

  @Nullable
  @Attribute(description = "The MAC address of this DPP device.",
      isCaseExact = false,
      uniqueness = AttributeDefinition.Uniqueness.MANUFACTURER,
      pattern = MAC_PATTERN)
  private String deviceMacAddress;

  @NotNull
  @Attribute(description =
      "The class/channel pairs supported by this DPP device.",
      isCaseExact = false,
      multiValueClass = String.class)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> classChannel = List.of();

  @Nullable
  @Attribute(description = "The serial number of this DPP device.",
      isCaseExact = false)
  private String serialNumber;

  /**
   * Fetches the Device Provisioning Protocol version of this device.
   *
   * @return The DPP version.
   */
  public int getDppVersion()
  {
    return dppVersion;
  }

  /**
   * Fetches the DPP bootstrapping public key.
   *
   * @return The bootstrap key.
   */
  @NotNull
  public String getBootstrapKey()
  {
    return bootstrapKey;
  }

  /**
   * Fetches the MAC address of this DPP device.
   *
   * @return The MAC address.
   */
  @Nullable
  public String getDeviceMacAddress()
  {
    return deviceMacAddress;
  }

  /**
   * Specifies the MAC address of this DPP device.
   *
   * @param deviceMacAddress The MAC address.
   * @return This DPP extension.
   */
  @NotNull
  public DppDeviceExtension setDeviceMacAddress(
      @Nullable final String deviceMacAddress)
  {
    this.deviceMacAddress = deviceMacAddress;
    return this;
  }

  /**
   * Fetches the serial number of this DPP device.
   *
   * @return The serial number.
   */
  @Nullable
  public String getSerialNumber()
  {
    return serialNumber;
  }

  /**
   * Specifies the serial number of this DPP device.
   *
   * @param serialNumber The serial number.
   * @return This DPP extension.
   */
  @NotNull
  public DppDeviceExtension setSerialNumber(@Nullable final String serialNumber)
  {
    this.serialNumber = serialNumber;
    return this;
  }

  /**
   * Fetches the bootstrapping methods supported by this DPP device. The name of
   * this attribute is singular, but a DPP device may define multiple methods.
   *
   * @return The list of bootstrapping methods.
   */
  @NotNull
  public List<String> getBootstrappingMethod()
  {
    return bootstrappingMethod;
  }

  /**
   * Specifies the bootstrapping methods supported by this DPP device.
   *
   * @param bootstrappingMethod The list of bootstrapping methods.
   * @return This DPP extension.
   */
  @NotNull
  public DppDeviceExtension setBootstrappingMethod(
      @Nullable final List<String> bootstrappingMethod)
  {
    this.bootstrappingMethod =
        (bootstrappingMethod == null) ? List.of() : bootstrappingMethod;
    return this;
  }

  /**
   * Alternate version of {@link #setBootstrappingMethod(List)}.
   *
   * @param method    A non-null bootstrapping method.
   * @param methods   An optional set of additional arguments. Any
   *                  {@code null} values will be ignored.
   * @return This DPP extension.
   */
  @NotNull
  public DppDeviceExtension setBootstrappingMethod(
      @NotNull final String method,
      @Nullable final String... methods)
  {
    return setBootstrappingMethod(StaticUtils.toList(method, methods));
  }

  /**
   * Fetches the class/channel pairs supported by this DPP device.
   *
   * @return The list of class/channel pairs.
   */
  @NotNull
  public List<String> getClassChannel()
  {
    return classChannel;
  }

  /**
   * Specifies the class/channel pairs supported by this DPP device.
   *
   * @param classChannel The list of class/channel pairs.
   * @return This DPP extension.
   */
  @NotNull
  public DppDeviceExtension setClassChannel(
      @Nullable final List<String> classChannel)
  {
    this.classChannel = (classChannel == null) ? List.of() : classChannel;
    return this;
  }

  /**
   * Alternate version of {@link #setClassChannel(List)}.
   *
   * @param channel    A non-null class/channel string.
   * @param channels   An optional set of additional arguments. Any
   *                   {@code null} values will be ignored.
   * @return This DPP extension.
   */
  @NotNull
  public DppDeviceExtension setClassChannel(@NotNull final String channel,
                                            @Nullable final String... channels)
  {
    return setClassChannel(StaticUtils.toList(channel, channels));
  }

  /**
   * Indicates whether the provided object is equal to this DPP device
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

    return o instanceof DppDeviceExtension that
        && Objects.equals(dppVersion, that.dppVersion)
        && Objects.equals(bootstrapKey, that.bootstrapKey)
        && Objects.equals(deviceMacAddress, that.deviceMacAddress)
        && Objects.equals(serialNumber, that.serialNumber)
        && Objects.equals(bootstrappingMethod, that.bootstrappingMethod)
        && Objects.equals(classChannel, that.classChannel);
  }

  /**
   * Retrieves a hash code for this DPP device extension.
   *
   * @return  A hash code for this DPP device extension.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(dppVersion, bootstrapKey, deviceMacAddress,
        serialNumber, bootstrappingMethod, classChannel);
  }

  /**
   * Retrieves a string representation of this DPP device extension, with the
   * {@code bootstrapKey} value redacted.
   *
   * @return  A string representation of this DPP device extension.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.toRedactedString(this, "bootstrapKey");
  }
}
