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
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.StaticUtils;

import java.net.URI;
import java.util.List;
import java.util.Objects;


/**
 * This class represents an endpoint application extension for a device per
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944#section-7.6">
 * RFC 9944 Section 7.6</a>.
 * <br><br>
 *
 * Some device types (e.g., Bluetooth and Zigbee) require an application gateway
 * interface to manage them. This class represents the list of applications that
 * connect to an external system. Ultimately, this extension links a device to
 * the {@link EndpointAppResource} instances that manage it. For more background
 * on devices, see the {@link DeviceResource} class.
 * <br><br>
 *
 * The following fields are defined on this extension:
 * <ul>
 *   <li> {@code deviceControlEnterpriseEndpoint}: The URL of the enterprise
 *         endpoint used to reach the enterprise gateway for device control.
 *   <li> {@code telemetryEnterpriseEndpoint}: The URL of the enterprise
 *        endpoint used to reach the enterprise gateway for telemetry.
 *   <li> {@code applications}: A list of {@link EndpointAppReference}
 *        entries, each identifying an associated {@link EndpointAppResource}
 *        via a resource ID value and a {@code $ref} URL.
 * </ul>
 * <br><br>
 *
 * The following JSON represents a device with a BLE extension and an
 * endpoint applications extension:
 * <pre>
 * {
 *   "schemas": [
 *       "urn:ietf:params:scim:schemas:core:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:ble:2.0:Device",
 *       "urn:ietf:params:scim:schemas:extension:endpointAppsExt:2.0:Device"
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
 *     "mobility": false,
 *     "separateBroadcastAddress": [ "AA:BB:88:77:22:11", "AA:BB:88:77:22:12" ],
 *     "pairingMethods": [
 *         "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device"
 *     ],
 *     "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device": {
 *       "key": 123456
 *     }
 *   },
 *   "urn:ietf:params:scim:schemas:extension:endpointAppsExt:2.0:Device": {
 *     "deviceControlEnterpriseEndpoint": "https://example.com/ctrl_endpoint",
 *     "telemetryEnterpriseEndpoint": "mqtts://example.com/telementry",
 *     "applications": [ {
 *         "value": "a1b2c3d4",
 *         "$ref": "https://example.com/v2/EndpointApps/a1b2c3d4"
 *       }, {
 *         "value": "a1b2c3d5",
 *         "$ref": "https://example.com/v2/EndpointApps/a1b2c3d5"
 *       }
 *     ]
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * To create the EndpointAppReference entries embedded within the response
 * above, use the following Java code:
 * <pre><code>
 *   // Create the endpoint applications extension.
 *   EndpointAppReference app1 = new EndpointAppReference()
 *       .setValue("a1b2c3d4")
 *       .setRef("https://example.com/v2/EndpointApps/a1b2c3d4");
 *   EndpointAppReference app2 = new EndpointAppReference()
 *       .setValue("a1b2c3d5")
 *       .setRef("https://example.com/v2/EndpointApps/a1b2c3d5");
 *
 *   EndpointAppDeviceExtension apps = new EndpointAppDeviceExtension()
 *     .setApplications(app1, app2)
 *     .setDeviceControlEnterpriseEndpoint("https://example.com/ctrl_endpoint")
 *     .setTelemetryEnterpriseEndpoint("mqtts://example.com/telementry");
 * </code></pre>
 */
@Schema(
    id = "urn:ietf:params:scim:schemas:extension:endpointAppsExt:2.0:Device",
    name = "Application Endpoint Extension",
    description = "Endpoint applications extension for a Device resource")
public class EndpointAppDeviceExtension extends DeviceExtension
{
  // Like $ref fields, this is marked as not required since clients will not set
  // this value in requests they send to SCIM services.
  @Nullable
  @Attribute(description =
      "The URI of the enterprise device-control endpoint application.",
      isRequired = false,
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      uniqueness = AttributeDefinition.Uniqueness.ENTERPRISE,
      referenceTypes = "external")
  private URI deviceControlEnterpriseEndpoint;

  @Nullable
  @Attribute(description =
      "The URI of the enterprise telemetry endpoint application.",
      isRequired = false,
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      uniqueness = AttributeDefinition.Uniqueness.ENTERPRISE,
      referenceTypes = "external")
  private URI telemetryEnterpriseEndpoint;

  @NotNull
  @Attribute(description =
      "The list of EndpointApp resources associated with this device.",
      isRequired = true,
      multiValueClass = EndpointAppReference.class)
  private List<EndpointAppReference> applications = List.of();

  /**
   * Retrieves the URI of the enterprise device-control endpoint.
   *
   * @return The device-control endpoint URI.
   */
  @Nullable
  public URI getDeviceControlEnterpriseEndpoint()
  {
    return deviceControlEnterpriseEndpoint;
  }

  /**
   * Retrieves the URI of the enterprise device-control endpoint as a string.
   *
   * @return The device-control endpoint URI.
   */
  @Nullable
  @JsonIgnore
  public String getDeviceControlEnterpriseEndpointString()
  {
    URI controlEndpoint = getDeviceControlEnterpriseEndpoint();
    return (controlEndpoint == null) ? null : controlEndpoint.toString();
  }

  /**
   * Specifies the URI of the enterprise device-control endpoint.
   *
   * @param deviceControlEnterpriseEndpoint The device-control endpoint URI.
   * @return This endpoint application extension.
   */
  @NotNull
  public EndpointAppDeviceExtension setDeviceControlEnterpriseEndpoint(
      @Nullable final URI deviceControlEnterpriseEndpoint)
  {
    this.deviceControlEnterpriseEndpoint = deviceControlEnterpriseEndpoint;
    return this;
  }

  /**
   * Alternate version of {@link #setDeviceControlEnterpriseEndpoint(URI)}
   * that accepts a string.
   *
   * @param endpoint The device-control endpoint URI.
   * @return This endpoint application extension.
   *
   * @throws IllegalArgumentException  If the string was not a valid URI.
   */
  @NotNull
  public EndpointAppDeviceExtension setDeviceControlEnterpriseEndpoint(
      @Nullable final String endpoint)
          throws IllegalArgumentException
  {
    return setDeviceControlEnterpriseEndpoint(
        (endpoint == null) ? null : URI.create(endpoint));
  }

  /**
   * Retrieves the URI of the enterprise telemetry endpoint.
   *
   * @return The telemetry endpoint URI.
   */
  @Nullable
  public URI getTelemetryEnterpriseEndpoint()
  {
    return telemetryEnterpriseEndpoint;
  }

  /**
   * Retrieves the URI of the enterprise telemetry endpoint as a string.
   *
   * @return The telemetry endpoint URI.
   */
  @Nullable
  @JsonIgnore
  public String getTelemetryEnterpriseEndpointString()
  {
    URI telemetryEndpoint = getTelemetryEnterpriseEndpoint();
    return (telemetryEndpoint == null) ? null : telemetryEndpoint.toString();
  }

  /**
   * Specifies the URI of the enterprise telemetry endpoint.
   *
   * @param telemetryEnterpriseEndpoint The telemetry endpoint URI.
   * @return This endpoint application extension.
   */
  @NotNull
  public EndpointAppDeviceExtension setTelemetryEnterpriseEndpoint(
      @Nullable final URI telemetryEnterpriseEndpoint)
  {
    this.telemetryEnterpriseEndpoint = telemetryEnterpriseEndpoint;
    return this;
  }

  /**
   * Alternate version of {@link #setTelemetryEnterpriseEndpoint(URI)} that
   * accepts a string.
   *
   * @param endpoint The telemetry endpoint URI.
   * @return This endpoint application extension.
   *
   * @throws IllegalArgumentException  If the string was not a valid URI.
   */
  @NotNull
  public EndpointAppDeviceExtension setTelemetryEnterpriseEndpoint(
      @Nullable final String endpoint)
          throws IllegalArgumentException
  {
    return setTelemetryEnterpriseEndpoint(
        (endpoint == null) ? null : URI.create(endpoint));
  }

  /**
   * Retrieves the list of EndpointApp resources associated with this device.
   *
   * @return The list of endpoint application references.
   */
  @NotNull
  public List<EndpointAppReference> getApplications()
  {
    return applications;
  }

  /**
   * Specifies the list of EndpointApp resources associated with this device.
   *
   * @param applications The list of endpoint application references.
   * @return This endpoint application extension.
   */
  @NotNull
  public EndpointAppDeviceExtension setApplications(
      @Nullable final List<EndpointAppReference> applications)
  {
    this.applications = (applications == null) ? List.of() : applications;
    return this;
  }

  /**
   * Alternate version of {@link #setApplications(List)}.
   *
   * @param app    A non-null application reference.
   * @param apps   An optional set of additional arguments.
   *
   * @return This endpoint application extension.
   */
  @NotNull
  public EndpointAppDeviceExtension setApplications(
      @NotNull final EndpointAppReference app,
      @Nullable final EndpointAppReference... apps)
  {
    return setApplications(StaticUtils.toList(app, apps));
  }

  /**
   * Indicates whether the provided object is equal to this endpoint application
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

    return o instanceof EndpointAppDeviceExtension that
        && Objects.equals(deviceControlEnterpriseEndpoint,
            that.deviceControlEnterpriseEndpoint)
        && Objects.equals(telemetryEnterpriseEndpoint,
            that.telemetryEnterpriseEndpoint)
        && Objects.equals(applications, that.applications);
  }

  /**
   * Retrieves a hash code for this endpoint application extension.
   *
   * @return  A hash code for this endpoint extension.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(deviceControlEnterpriseEndpoint,
        telemetryEnterpriseEndpoint, applications);
  }
}
