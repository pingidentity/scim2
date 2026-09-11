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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.StaticUtils;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;


/**
 * This class represents the Bluetooth Low Energy (BLE) device extension defined
 * by <a href="https://datatracker.ietf.org/doc/html/rfc9944#section-7.1">
 * RFC 9944 Section 7.1</a>. For more background on devices, see the
 * {@link DeviceResource} class.
 * <br><br>
 *
 * The following fields are defined on this extension:
 * <ul>
 *   <li> {@code deviceMacAddress}: A string value that represents a public MAC
 *                                  address assigned by the manufacturer.
 *   <li> {@code irk}:              The Identity Resolving Key unique to each
 *                                  device and used to resolve a random address
 *                                  received from a peer device.
 *   <li> {@code isRandom}:         Indicates whether the device's MAC address
 *                                  is public (false) or random (true). A random
 *                                  address is either a private address (if an
 *                                  IRK is present) or a static random address.
 *   <li> {@code mobility}:         Indicates BLE device mobility. If set to
 *                                  {@code true}, the device can be expected to
 *                                  move within a network of Access Points.
 *   <li> {@code versionSupport}:   A set of strings that specifies the BLE
 *                                  versions supported by the device.
 *   <li> {@code pairingMethods}:   A set of strings that specifies pairing
 *                                  methods associated with the BLE device.
 *   <li> {@code separateBroadcastAddress}: Represents an address used for
 *        broadcasts. This value MUST NOT be set when an IRK is provided.
 * </ul>
 * <br><br>
 *
 * The following JSON represents a device with a BLE extension:
 * <pre>
 * {
 *   "schemas": [
 *       "urn:ietf:params:scim:schemas:core:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:ble:2.0:Device"
 *   ],
 *   "id": "e9e30dba",
 *   "meta": {
 *     "resourceType": "Device",
 *     "location": "https://example.com/v2/Devices/e9e30dba"
 *   },
 *   "displayName": "BLE Example Monitor",
 *   "active": true,
 *   "urn:ietf:params:scim:schemas:extension:ble:2.0:Device": {
 *     "versionSupport": [ "5.4" ],
 *     "deviceMacAddress": "2C:54:91:88:C9:E2",
 *     "isRandom": false,
 *     "mobility": true,
 *     "separateBroadcastAddress": [ "AA:BB:88:77:22:11", "AA:BB:88:77:22:12" ],
 *     "pairingMethods": [
 *         "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device"
 *     ],
 *     "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device": {
 *       "key": 123456
 *     }
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * This device can be created with the following Java code:
 * <pre><code>
 *   // Create the device extension.
 *   BleDeviceExtension ble =
 *       new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
 *           .setIsRandom(false)
 *           .setMobility(true)
 *           .setPairingExtension(new BlePairingPassKey(123456));
 *
 *   // Create the device.
 *   DeviceResource device = new DeviceResource()
 *       .setDisplayName("BLE Example Monitor")
 *       .setActive(true)
 *       .setDeviceExtension(ble);
 *   device.setId("e9e30dba");
 *   device.setMeta(new Meta()
 *       .setResourceType("Device")
 *       .setLocationString("https://example.com/v2/Devices/e9e30dba"));
 * </code></pre>
 * <br><br>
 *
 * Bluetooth Low Energy SCIM data is noteworthy in that it contains a nested
 * extension schema value. These nested values represent the type of "pairing
 * method" that is compatible with the device. For example, some Bluetooth
 * devices display a numeric code during the connection process. Pairing methods
 * are defined as subclasses of {@link BlePairingMethod}, and include:
 * <ul>
 *   <li> {@link BlePairingJustWorks}
 *   <li> {@link BlePairingNull}
 *   <li> {@link BlePairingOutOfBand}
 *   <li> {@link BlePairingPassKey}
 * </ul>
 *
 * When these classes are passed to {@link #setPairingExtension}, the value of
 * the {@code pairingMethods} property will automatically be updated, so that
 * array value does not need to be handled manually.
 */
@Schema(id = "urn:ietf:params:scim:schemas:extension:ble:2.0:Device",
    name = "BLE Extension",
    description = "BLE extension for a Device resource")
public class BleDeviceExtension extends DeviceExtension
{
  @NotNull
  @Attribute(description = "The BLE versions supported by this device.",
      isRequired = true,
      isCaseExact = false,
      multiValueClass = String.class)
  private final List<String> versionSupport;

  @NotNull
  @Attribute(description = "A string value that represents a public MAC"
      + " address assigned by the manufacturer.",
      isRequired = true,
      isCaseExact = false,
      uniqueness = AttributeDefinition.Uniqueness.MANUFACTURER,
      pattern = MAC_PATTERN)
  private final String deviceMacAddress;

  @Attribute(description =
      "Indicates whether the device MAC address is a random address.",
      isRequired = false)
  private boolean isRandom = false;

  @Nullable
  @Attribute(description = """
      The Identity Resolving Key (IRK) for this BLE device. This is a \
      cryptographic secret that is never returned in responses.""",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.WRITE_ONLY,
      returned = AttributeDefinition.Returned.NEVER,
      uniqueness = AttributeDefinition.Uniqueness.MANUFACTURER)
  private String irk;

  @Nullable
  @Attribute(description = "Indicates whether a device supports BLE mobility.",
      isRequired = false)
  private Boolean mobility;

  @NotNull
  @Attribute(description =
      "One or more separate broadcast addresses used by this BLE device.",
      isRequired = false,
      isCaseExact = false,
      multiValueClass = String.class,
      pattern = MAC_PATTERN)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> separateBroadcastAddress = List.of();

  @NotNull
  @Attribute(description =
      "BLE pairing methods supported by this device, expressed as schema URNs.",
      isRequired = true,
      isCaseExact = true,
      multiValueClass = String.class)
  private final TreeSet<String> pairingMethods = new TreeSet<>();

  /**
   * Creates a new Bluetooth Low Energy device extension.
   *
   * @param versionSupport    The BLE versions supported by this device.
   * @param deviceMacAddress  The device's MAC address.
   */
  @JsonCreator
  public BleDeviceExtension(
      @NotNull @JsonProperty(value = "versionSupport", required = true)
      final List<String> versionSupport,
      @NotNull @JsonProperty(value = "deviceMacAddress", required = true)
      final String deviceMacAddress)
  {
    this.versionSupport = List.copyOf(Objects.requireNonNull(versionSupport));
    this.deviceMacAddress = Objects.requireNonNull(deviceMacAddress);
  }

  /**
   * Creates a new Bluetooth Low Energy device extension.
   *
   * @param deviceMacAddress  The device's MAC address.
   * @param versionSupport    A non-null BLE version supported by this device.
   * @param versions          An optional argument for additional versions.
   */
  public BleDeviceExtension(@NotNull final String deviceMacAddress,
                            @NotNull final String versionSupport,
                            @Nullable final String... versions)
  {
    this(StaticUtils.toList(versionSupport, versions), deviceMacAddress);
  }

  /**
   * Fetches the Bluetooth device MAC address.
   *
   * @return The MAC address.
   */
  @NotNull
  public String getDeviceMacAddress()
  {
    return deviceMacAddress;
  }

  /**
   * Fetches whether the device MAC address is a random address.
   *
   * @return {@code true} if random, or {@code false} if not.
   */
  public boolean getIsRandom()
  {
    return isRandom;
  }

  /**
   * Specifies whether the device MAC address is a random address.
   *
   * @param isRandom The boolean value indicating if the MAC address is random.
   * @return This Bluetooth Low Energy extension.
   */
  @NotNull
  public BleDeviceExtension setIsRandom(final boolean isRandom)
  {
    this.isRandom = isRandom;
    return this;
  }

  /**
   * Fetches the list of separate broadcast addresses for this BLE device.
   *
   * @return The list of separate broadcast addresses.
   */
  @NotNull
  public List<String> getSeparateBroadcastAddress()
  {
    return separateBroadcastAddress;
  }

  /**
   * Specifies the list of separate broadcast addresses for this BLE device.
   *
   * @param separateBroadcastAddress The list of separate broadcast addresses.
   * @return This Bluetooth Low Energy extension.
   * @throws IllegalStateException If the IRK is also set.
   */
  @NotNull
  public BleDeviceExtension setSeparateBroadcastAddress(
      @Nullable final List<String> separateBroadcastAddress)
      throws IllegalStateException
  {
    validate(this.irk, separateBroadcastAddress);
    this.separateBroadcastAddress = (separateBroadcastAddress == null)
        ? List.of() : List.copyOf(separateBroadcastAddress);
    return this;
  }

  /**
   * Alternate version of {@link #setSeparateBroadcastAddress(List)}.
   *
   * @param address   A non-null address.
   * @param addresses An optional set of additional arguments. Any
   *                  {@code null} values will be ignored.
   * @return This Bluetooth Low Energy extension.
   * @throws IllegalStateException If the IRK is also set.
   */
  @NotNull
  public BleDeviceExtension setSeparateBroadcastAddress(
      @NotNull final String address,
      @Nullable final String... addresses)
          throws IllegalStateException
  {
    return setSeparateBroadcastAddress(StaticUtils.toList(address, addresses));
  }

  /**
   * Fetches the Identity Resolving Key for this BLE device.
   *
   * @return The IRK.
   */
  @Nullable
  public String getIrk()
  {
    return irk;
  }

  /**
   * Specifies the Identity Resolving Key for this BLE device.
   *
   * @param irk The IRK.
   * @return This Bluetooth Low Energy extension.
   * @throws IllegalStateException If the separateBroadcastAddress is also set.
   */
  @NotNull
  public BleDeviceExtension setIrk(@Nullable final String irk)
      throws IllegalStateException
  {
    validate(irk, this.separateBroadcastAddress);
    this.irk = irk;
    return this;
  }

  /**
   * Fetches whether this device supports BLE mobility.
   *
   * @return A boolean indicating whether mobility is supported, or {@code null}
   * if the value is not set.
   */
  @Nullable
  public Boolean getMobility()
  {
    return mobility;
  }

  /**
   * Specifies whether this device supports BLE mobility.
   *
   * @param mobility {@code true} if mobility is supported.
   * @return This Bluetooth Low Energy extension.
   */
  @NotNull
  public BleDeviceExtension setMobility(@Nullable final Boolean mobility)
  {
    this.mobility = mobility;
    return this;
  }

  /**
   * Fetches the BLE versions supported by this device.
   *
   * @return The list of supported BLE versions.
   */
  @NotNull
  public List<String> getVersionSupport()
  {
    return versionSupport;
  }

  /**
   * Fetches the BLE pairing methods supported by this device.
   *
   * @return The list of pairing method schema URNs.
   */
  @NotNull
  public List<String> getPairingMethods()
  {
    return List.copyOf(pairingMethods);
  }

  /**
   * Manually sets the BLE {@code pairingMethods} field.
   * <br><br>
   *
   * In general, methods like {@link #setPairingExtension(BlePairingMethod)}
   * should be used instead, which will automatically set the appropriate
   * fields.
   *
   * @param pairingMethods  The list of pairing methods.
   */
  public void setPairingMethods(@NotNull final List<String> pairingMethods)
  {
    this.pairingMethods.clear();
    this.pairingMethods.addAll(pairingMethods);
  }

  /**
   * Sets a BLE pairing method extension on this device extension, and adds
   * its schema URN to the {@code pairingMethods} field.
   *
   * @param <T>           The data type of the pairing method extension.
   * @param pairingMethod The pairing method extension to set.
   * @return This Bluetooth Low Energy extension.
   */
  @NotNull
  public <T extends BlePairingMethod> BleDeviceExtension setPairingExtension(
      @NotNull final T pairingMethod)
  {
    setExtension(pairingMethod);

    // setExtension() adds to the schema URNs, which should always be empty for
    // this class.
    getSchemaUrns().clear();

    String schema = SchemaUtils.getSchemaUrn(pairingMethod.getClass());
    pairingMethods.add(schema);
    return this;
  }

  /**
   * Removes a BLE pairing method extension from this device extension, and
   * removes its schema URN from the {@code pairingMethods} field.
   *
   * @param <T>          The data type of the pairing method extension.
   * @param pairingClass The class of the pairing method extension to remove.
   * @return This Bluetooth Low Energy extension.
   */
  @NotNull
  public <T extends BlePairingMethod> BleDeviceExtension removePairingExtension(
      @NotNull final Class<T> pairingClass)
  {
    removeExtension(pairingClass);
    String schema = SchemaUtils.getSchemaUrn(pairingClass);
    pairingMethods.remove(schema);
    return this;
  }

  /**
   * Obtains all compatible pairing method objects from this device extension.
   *
   * @return The list of pairing method objects.
   */
  @NotNull
  @JsonIgnore
  public List<BlePairingMethod> getPairingMethodExtensions()
  {
    List<Class<? extends BlePairingMethod>> pairingClasses = List.of(
        BlePairingJustWorks.class,
        BlePairingNull.class,
        BlePairingOutOfBand.class,
        BlePairingPassKey.class);

    return getClassesFromExtension(pairingClasses);
  }

  /**
   * Ensures that the {@code irk} and {@code separateBroadcastAddress} fields
   * are not both set.
   *
   * @throws IllegalStateException If both fields are set.
   */
  private void validate(@Nullable final String irkValue,
                        @Nullable final List<String> addresses)
  {
    if (irkValue != null && addresses != null && !addresses.isEmpty())
    {
      throw new IllegalStateException("The 'separateBroadcastAddress' and"
          + " 'irk' fields cannot both be set on a BleDeviceExtension.");
    }
  }

  /**
   * Indicates whether the provided object is equal to this BLE device
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

    return o instanceof BleDeviceExtension that
        && super.equals(o)
        && versionSupport.equals(that.versionSupport)
        && Objects.equals(deviceMacAddress, that.deviceMacAddress)
        && isRandom == that.isRandom
        && Objects.equals(irk, that.irk)
        && Objects.equals(mobility, that.mobility)
        && separateBroadcastAddress.equals(that.separateBroadcastAddress)
        && pairingMethods.equals(that.pairingMethods);
  }

  /**
   * Retrieves a hash code for this BLE device extension.
   *
   * @return A hash code for this BLE device extension.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), versionSupport, deviceMacAddress,
        isRandom, irk, mobility, separateBroadcastAddress, pairingMethods);
  }

  /**
   * Retrieves a string representation of this BLE device extension, with the
   * {@code irk} value redacted.
   *
   * @return  A string representation of this BLE device extension.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.toRedactedString(this, "irk");
  }
}
