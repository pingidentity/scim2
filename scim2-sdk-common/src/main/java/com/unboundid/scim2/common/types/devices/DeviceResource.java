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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.Group;
import com.unboundid.scim2.common.utils.StaticUtils;

import java.net.URI;
import java.util.List;
import java.util.Objects;


/**
 * This class represents the {@code device} resource type as described by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944">RFC 9944</a>.
 * Management of IoT (Internet of Things) devices was added to SCIM to handle
 * device provisioning for authentication use cases. These use cases include
 * both user-based authentication and OAuth/certificate authentication.
 * <br><br>
 *
 * In SCIM, a device resource can represent many types of devices that are
 * managed by users. These include Bluetooth, Wi-Fi Easy Connect (DPP), Zigbee
 * low-power devices, and more. The following fields are defined on a device:
 * <ul>
 *   <li> {@code displayName}: The human-readable name of the device.
 *   <li> {@code active}:      Indicates whether the device is in use.
 *   <li> {@code mudUrl}:      An optional Manufacturer Usage Description URL
 *                             for this device as defined by RFC 8520.
 *   <li> {@code groups}:      An optional list of groups that this device
 *                             belongs to. Membership may be given directly,
 *                             through a nested group, or dynamically computed.
 * </ul>
 * <br><br>
 *
 * In addition to the above fields, a device resource generally contains more
 * standard-specific data that is stored in a schema extension. For example,
 * consider the following Zigbee device resource in JSON form:
 * <pre>
 *   {
 *     "schemas": [
 *       "urn:ietf:params:scim:schemas:core:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device"
 *     ],
 *     "id": "e9e30dba",
 *     "meta": {
 *       "resourceType": "Device",
 *       "created": "1970-01-23T04:56:22Z",
 *       "lastModified": "1970-05-13T04:42:34Z",
 *       "location": "https://example.com/v2/Devices/e9e30dba"
 *     },
 *     "displayName": "Zigbee Example Monitor",
 *     "active": true,
 *     "mudUrl": "https://example.com/lightbulbs/colour/v1",
 *     "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device": {
 *       "versionSupport": [ "3.0" ],
 *       "deviceEui64Address": "50:32:5F:FF:FE:E7:67:28"
 *     }
 *   }
 * </pre>
 *
 * This device resource may be created with the following Java code. Values for
 * metadata use constants below, but will generally come from a database.
 * <pre><code>
 *   // Create the device extension.
 *   ZigbeeDeviceExtension zigbee = new ZigbeeDeviceExtension(
 *       "50:32:5F:FF:FE:E7:67:28", "3.0");
 *
 *   // Create the device.
 *   DeviceResource device = new DeviceResource()
 *       .setDisplayName("Zigbee Example Monitor")
 *       .setActive(true)
 *       .setDeviceExtension(zigbee);
 *   device.setId("e9e30dba");
 *   device.setMeta(new Meta()
 *       .setResourceType("Device")
 *       .setCreatedMillis(1918582000L)
 *       .setLastModifiedMillis(11421754000L)
 *       .setLocationString("https://example.com/v2/Devices/e9e30dba"));
 * </code></pre>
 * <br><br>
 *
 * Data specific to the Zigbee device is stored under the relevant extension
 * schema. This structure organizes standard-specific data, since Zigbee
 * information (e.g., the unique EUI 64 address) is not applicable to other
 * device types. It also allows consumers to differentiate between device types,
 * which each have their own URN identifier listed below. Note that all
 * extension classes descend from the {@link DeviceExtension} class.
 * <ul>
 *   <li> {@link BleDeviceExtension}:
 *        urn:ietf:params:scim:schemas:extension:ble:2.0:Device
 *   <li> {@link DppDeviceExtension}:
 *        urn:ietf:params:scim:schemas:extension:dpp:2.0:Device
 *   <li> {@link EndpointAppDeviceExtension}:
 *        urn:ietf:params:scim:schemas:extension:endpointAppsExt:2.0:Device
 *   <li> {@link EthernetMabDeviceExtension}:
 *        urn:ietf:params:scim:schemas:extension:ethernet-mab:2.0:Device
 *   <li> {@link FdoDeviceExtension}:
 *        urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device
 *   <li> {@link ZigbeeDeviceExtension}:
 *        urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device
 * </ul>
 * <br><br>
 *
 * To fetch the device extension(s) stored on a device resource, use the
 * {@link #getDeviceExtensions()} method and the {@code instanceof} keyword:
 * <pre><code>
 *   for (DeviceExtension extension : device.getDeviceExtensions())
 *   {
 *     if (extension instanceof BleDeviceExtension ble)
 *     {
 *       exampleBluetoothHandle(device, ble);
 *     }
 *     else if (extension instanceof DppDeviceExtension wifi)
 *     {
 *       exampleWifiHandle(device, wifi);
 *     }
 *     // ...
 *   }
 * </code></pre>
 * <br><br>
 *
 * <h2>Cryptographic Secrets</h2>
 * By their nature, devices sometimes rely on cryptography to establish trusted
 * connections and verification. As a result, device extension JSON objects from
 * a client may contain secret values that should not be exposed, particularly:
 * <ul>
 *   <li> The Identity Resolving Key (IRK) of a {@link BleDeviceExtension}.
 *   <li> The bootstrap key of a {@link DppDeviceExtension}.
 *   <li> The voucher of an {@link FdoDeviceExtension}.
 * </ul>
 *
 * When these extension objects are converted to JSON, the SCIM SDK prints such
 * secret values if they are present. This behavior is necessary when a client
 * application sends this information to a SCIM service. However, SCIM services
 * should ensure that they are not returned to a client under any circumstances.
 * The scim2-sdk-server's {@code ResourcePreparer} class may be used to handle
 * these values.
 * <br><br>
 *
 * Although some device extensions use strong encryption, the use of a device
 * extension does not guarantee this. For example, MAC Authenticated Bypass used
 * by {@link EthernetMabDeviceExtension} is specifically considered a legacy
 * form of weak authentication, so it is important to be aware of different
 * authentication types.
 *
 * @since 6.1.0
 */
@Schema(id = "urn:ietf:params:scim:schemas:core:2.0:Device",
    name = "Core Device Schema",
    description = "Device")
public class DeviceResource extends BaseScimResource
{
  @Nullable
  @Attribute(description = "A human-readable name for the device, suitable"
      + " for display to end-users.",
      isCaseExact = false)
  private String displayName;

  @Attribute(description = "A Boolean value indicating whether the device"
      + " is currently active.",
      isRequired = true)
  private boolean active;

  @Nullable
  @Attribute(description = "A URI pointing to the Manufacturer Usage"
      + " Description (MUD) file for this device, as defined by RFC 8520.",
      isCaseExact = true,
      referenceTypes = "external")
  private URI mudUrl;

  @NotNull
  @Attribute(description = "A list of groups to which the device belongs.",
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = Group.class)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Group> groups = List.of();

  /**
   * Retrieves the human-readable display name of this device.
   *
   * @return The display name of this device.
   */
  @Nullable
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Specifies the human-readable display name of this device.
   *
   * @param displayName The display name of this device.
   * @return This device resource.
   */
  @NotNull
  public DeviceResource setDisplayName(@Nullable final String displayName)
  {
    this.displayName = displayName;
    return this;
  }

  /**
   * Retrieves whether this device is currently active.
   *
   * @return The device's active status.
   */
  public boolean getActive()
  {
    return active;
  }

  /**
   * Specifies whether this device is currently active.
   *
   * @param active The device's active status.
   * @return This device resource.
   */
  @NotNull
  public DeviceResource setActive(final boolean active)
  {
    this.active = active;
    return this;
  }

  /**
   * Retrieves the Manufacturer Usage Description URL for this device.
   *
   * @return The MUD URL for this device.
   */
  @Nullable
  public URI getMudUrl()
  {
    return mudUrl;
  }

  /**
   * Retrieves the Manufacturer Usage Description URL as a string.
   *
   * @return The MUD URL for this device.
   */
  @Nullable
  @JsonIgnore
  public String getMudUrlString()
  {
    return (mudUrl == null) ? null : mudUrl.toString();
  }

  /**
   * Specifies the Manufacturer Usage Description URL for this device.
   *
   * @param mudUrl The MUD URL for this device.
   * @return This device resource.
   */
  @NotNull
  public DeviceResource setMudUrl(@Nullable final URI mudUrl)
  {
    this.mudUrl = mudUrl;
    return this;
  }

  /**
   * Alternate version of {@link #setMudUrl(URI)} that accepts a string.
   *
   * @param mudUrl The MUD URL for this device.
   * @return This device resource.
   *
   * @throws IllegalArgumentException  If the string was not a valid URI.
   */
  @NotNull
  public DeviceResource setMudUrl(@Nullable final String mudUrl)
      throws IllegalArgumentException
  {
    return setMudUrl((mudUrl == null) ? null : URI.create(mudUrl));
  }

  /**
   * Retrieves the list of groups to which this device belongs.
   *
   * @return The list of groups to which this device belongs.
   */
  @NotNull
  public List<Group> getGroups()
  {
    return groups;
  }

  /**
   * Specifies the list of groups to which this device belongs.
   *
   * @param groups The list of groups to which this device belongs.
   * @return This device resource.
   */
  @NotNull
  public DeviceResource setGroups(@Nullable final List<Group> groups)
  {
    this.groups = (groups == null) ? List.of() : groups;
    return this;
  }

  /**
   * Alternate version of {@link #setGroups(List)}.
   *
   * @param group  A non-null group.
   * @param groups  An optional set of additional arguments. Any
   *                {@code null} values will be ignored.
   * @return This device resource.
   */
  @NotNull
  public DeviceResource setGroups(@NotNull final Group group,
                                  @Nullable final Group... groups)
  {
    return setGroups(StaticUtils.toList(group, groups));
  }

  /**
   * Fetches the device extensions attached to this device resource. This can
   * contain multiple values, particularly if the device contains an
   * {@link EndpointAppDeviceExtension}.
   *
   * @return The device extensions.
   */
  @NotNull
  @JsonIgnore
  public List<DeviceExtension> getDeviceExtensions()
  {
    List<Class<? extends DeviceExtension>> extensionClasses = List.of(
        BleDeviceExtension.class,
        ZigbeeDeviceExtension.class,
        DppDeviceExtension.class,
        FdoDeviceExtension.class,
        EthernetMabDeviceExtension.class,
        EndpointAppDeviceExtension.class);

    return getClassesFromExtension(extensionClasses);
  }

  /**
   * Attaches a device extension (e.g., {@link ZigbeeDeviceExtension}) to this
   * resource. This is equivalent to calling {@link #setExtension(Object)},
   * though it returns the DeviceResource for builder pattern calls.
   *
   * @param ext The device extension to attach.
   * @return This device resource.
   */
  @NotNull
  public DeviceResource setDeviceExtension(@NotNull final DeviceExtension ext)
  {
    setExtension(ext);
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this device resource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this device
   *            resource, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    return o instanceof DeviceResource that
        && active == that.active
        && Objects.equals(displayName, that.displayName)
        && Objects.equals(mudUrl, that.mudUrl)
        && Objects.equals(groups, that.groups)
        && super.equals(o);
  }

  /**
   * Retrieves a hash code for this device resource.
   *
   * @return  A hash code for this device resource.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), active, displayName, mudUrl, groups);
  }
}
