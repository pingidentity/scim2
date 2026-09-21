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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.types.devices.BleDeviceExtension;
import com.unboundid.scim2.common.types.devices.BlePairingNull;
import com.unboundid.scim2.common.types.devices.BlePairingOutOfBand;
import com.unboundid.scim2.common.types.devices.DeviceResource;
import com.unboundid.scim2.common.types.devices.DppDeviceExtension;
import com.unboundid.scim2.common.types.devices.EndpointAppResource;
import com.unboundid.scim2.common.types.devices.EndpointAppDeviceExtension;
import com.unboundid.scim2.common.types.devices.FdoDeviceExtension;
import com.unboundid.scim2.common.types.devices.ZigbeeDeviceExtension;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Validate schema constraints of device resources.
 */
@Test
public class Rfc9944SchemaCheckerTestCase
{
  private ResourceTypeDefinition deviceResourceTypeDef;
  private ResourceTypeDefinition endpointAppResourceTypeDef;

  /**
   * Set up resource type definitions backed by generated schemas.
   */
  @BeforeClass
  public void setUp() throws Exception
  {
    SchemaResource deviceSchema = SchemaUtils.getSchema(DeviceResource.class);
    SchemaResource bleSchema = SchemaUtils.getSchema(BleDeviceExtension.class);
    SchemaResource dppSchema = SchemaUtils.getSchema(DppDeviceExtension.class);
    SchemaResource fdoSchema = SchemaUtils.getSchema(FdoDeviceExtension.class);
    SchemaResource endpointAppsSchema =
        SchemaUtils.getSchema(EndpointAppDeviceExtension.class);
    SchemaResource zigbeeSchema =
        SchemaUtils.getSchema(ZigbeeDeviceExtension.class);

    deviceResourceTypeDef = new ResourceTypeDefinition.Builder(
        "Device", "/Devices")
        .setCoreSchema(deviceSchema)
        .addOptionalSchemaExtension(bleSchema)
        .addOptionalSchemaExtension(dppSchema)
        .addOptionalSchemaExtension(fdoSchema)
        .addOptionalSchemaExtension(endpointAppsSchema)
        .addOptionalSchemaExtension(zigbeeSchema)
        .build();

    SchemaResource appSchema =
        SchemaUtils.getSchema(EndpointAppResource.class);
    endpointAppResourceTypeDef = new ResourceTypeDefinition.Builder(
        "EndpointApp", "/EndpointApps")
        .setCoreSchema(appSchema)
        .build();
  }

  /**
   * Valid MAC addresses must pass SchemaChecker.
   *
   * @param mac A valid MAC address string.
   */
  @Test(dataProvider = "validMacAddresses")
  public void testValidMacAddressPassesSchemaChecker(final String mac)
      throws Exception
  {
    SchemaChecker checker = new SchemaChecker(deviceResourceTypeDef);
    BleDeviceExtension ble = new BleDeviceExtension(mac, "5.0")
        .setPairingExtension(new BlePairingNull());
    DeviceResource device = new DeviceResource()
        .setActive(true)
        .setDeviceExtension(ble);

    ObjectNode resource = device.asGenericScimResource().getObjectNode();
    SchemaChecker.Results results = checker.checkCreate(resource);
    assertThat(results.getSyntaxIssues()).isEmpty();
  }

  /**
   * Invalid MAC addresses must produce a syntax issue.
   *
   * @param mac An invalid MAC address string.
   */
  @Test(dataProvider = "invalidMacAddresses")
  public void testInvalidMacAddressFailsSchemaChecker(final String mac)
      throws Exception
  {
    SchemaChecker checker = new SchemaChecker(deviceResourceTypeDef);
    BleDeviceExtension ble = new BleDeviceExtension(mac, "5.0")
        .setPairingExtension(new BlePairingNull());
    DeviceResource device = new DeviceResource()
        .setActive(true)
        .setDeviceExtension(ble);

    ObjectNode resource = device.asGenericScimResource().getObjectNode();
    SchemaChecker.Results results = checker.checkCreate(resource);
    assertThat(results.getSyntaxIssues()).isNotEmpty();
  }

  /**
   * Valid EUI-64 addresses must pass SchemaChecker.
   *
   * @param eui64 A valid EUI-64 address string.
   */
  @Test(dataProvider = "validEui64Addresses")
  public void testValidEui64PassesSchemaChecker(final String eui64)
      throws Exception
  {
    SchemaChecker checker = new SchemaChecker(deviceResourceTypeDef);
    ZigbeeDeviceExtension zigbee = new ZigbeeDeviceExtension(eui64, "3.0");
    DeviceResource device = new DeviceResource()
        .setActive(true)
        .setDeviceExtension(zigbee);

    ObjectNode resource = device.asGenericScimResource().getObjectNode();
    SchemaChecker.Results results = checker.checkCreate(resource);
    assertThat(results.getSyntaxIssues()).isEmpty();
  }

  /**
   * Invalid EUI-64 addresses must produce a syntax issue.
   *
   * @param eui64 An invalid EUI-64 address string.
   */
  @Test(dataProvider = "invalidEui64Addresses")
  public void testInvalidEui64FailsSchemaChecker(final String eui64)
      throws Exception
  {
    SchemaChecker checker = new SchemaChecker(deviceResourceTypeDef);
    ZigbeeDeviceExtension zigbee = new ZigbeeDeviceExtension(eui64, "3.0");
    DeviceResource device = new DeviceResource()
        .setActive(true)
        .setDeviceExtension(zigbee);

    ObjectNode resource = device.asGenericScimResource().getObjectNode();
    SchemaChecker.Results results = checker.checkCreate(resource);
    assertThat(results.getSyntaxIssues()).isNotEmpty();
  }

  /**
   * Ensure a nested schema extension does not cause an error during
   * SchemaChecker analysis of a device resource.
   */
  @Test
  public void testNestedPairingPassKeyExtensionAccepted() throws Exception
  {
    SchemaChecker checker = new SchemaChecker(deviceResourceTypeDef);
    ObjectNode resource = JsonUtils.getObjectReader()
        .forType(ObjectNode.class).readValue("""
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:ble:2.0:Device"
          ],
          "active": true,
          "urn:ietf:params:scim:schemas:extension:ble:2.0:Device": {
            "versionSupport": ["5.4"],
            "deviceMacAddress": "2C:54:91:88:C9:E2",
            "pairingMethods": [
              "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device"
            ],
            "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device": {
              "key": 123456
            }
          }
        }""");
    SchemaChecker.Results results = checker.checkCreate(resource);
    assertThat(results.getSyntaxIssues()).isEmpty();
  }

  /**
   * BlePairingOutOfBand should be able to generate its schema like any other
   * class, even though it exposes a {@code long} for the {@code randomNumber}
   * field's getter method.
   */
  @Test
  public void testBlePairingOutOfBandSchemaGeneration() throws Exception
  {
    SchemaResource schema = SchemaUtils.getSchema(BlePairingOutOfBand.class);
    assertThat(schema).isNotNull();

    AttributeDefinition randomNumber = schema.getAttributes().stream()
        .filter(attribute -> "randomNumber".equals(attribute.getName()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(
            "Expected a randomNumber attribute in the generated schema."));
    assertThat(randomNumber.getType())
        .isEqualTo(AttributeDefinition.Type.INTEGER);
  }

  /**
   * Cryptographic secret fields such as {@code irk} should be stripped from
   * responses that are handled by {@code ResourcePreparer}.
   */
  @Test
  public void testSecretFieldsAreStrippedFromResponse() throws Exception
  {
    GenericScimResource resource = new GenericScimResource(
        JsonUtils.getObjectReader()
            .forType(ObjectNode.class).readValue("""
            {
              "schemas": [
                "urn:ietf:params:scim:schemas:core:2.0:Device",
                "urn:ietf:params:scim:schemas:extension:ble:2.0:Device",
                "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device",
                "urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device"
              ],
              "id": "dev-1",
              "active": true,
              "urn:ietf:params:scim:schemas:extension:ble:2.0:Device": {
                "deviceMacAddress": "AA:BB:CC:DD:EE:FF",
                "irk": "secret-irk",
                "versionSupport": ["5.0"],
                "pairingMethods": ["urn:ietf:params:scim:schemas:extension:pairingNull:2.0:Device"]
              },
              "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device": {
                "dppVersion": 2,
                "bootstrapKey": "secret-bootstrap-key"
              },
              "urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device": {
                "fdoVoucher": "secret-fdo-voucher"
              }
            }"""));

    ResourcePreparer<GenericScimResource> preparer =
        new ResourcePreparer<>(deviceResourceTypeDef,
            null, null, URI.create("https://example.com/scim"));
    GenericScimResource trimmed = preparer.trimRetrievedResource(resource);

    ObjectNode trimmedNode = trimmed.getObjectNode();

    String extName = "urn:ietf:params:scim:schemas:extension:ble:2.0:Device";
    assertThat(containsField(trimmedNode, extName, "irk")).isFalse();

    extName = "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device";
    assertThat(containsField(trimmedNode, extName, "bootstrapKey")).isFalse();

    extName = "urn:ietf:params:scim:schemas:extension"
        + ":fido-device-onboard:2.0:Device";
    assertThat(containsField(trimmedNode, extName, "fdoVoucher")).isFalse();
  }

  /**
   * Test an endpointApp's field that must be settable on create.
   */
  @Test
  public void testApplicationTypeAllowedOnCreate() throws Exception
  {
    SchemaChecker checker = new SchemaChecker(endpointAppResourceTypeDef);
    ObjectNode resource = JsonUtils.getObjectReader()
        .forType(ObjectNode.class).readValue("""
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:EndpointApp" ],
          "applicationType": "deviceControl",
          "applicationName": "My App"
        }""");
    SchemaChecker.Results results = checker.checkCreate(resource);
    assertThat(results.getMutabilityIssues()).isEmpty();

    // Updating the field must be rejected on a replace operation.
    PatchOperation op = PatchOperation.replace(Path.of("applicationType"),
        StringNode.valueOf("telemetry"));
    results = checker.checkModify(List.of(op), resource);
    assertThat(results.getMutabilityIssues()).isNotEmpty();
  }

  /**
   * Valid MAC address strings.
   *
   * @return Test data.
   */
  @DataProvider
  public Object[][] validMacAddresses()
  {
    return new Object[][] {
        { "AA:BB:CC:DD:EE:FF" },
        { "aa:bb:cc:dd:ee:ff" },
        { "00:11:22:33:44:55" },
        { "0A:1B:2C:3D:4E:5F" },
    };
  }

  /**
   * Invalid MAC address strings.
   *
   * @return Test data.
   */
  @DataProvider
  public Object[][] invalidMacAddresses()
  {
    return new Object[][] {
        { "AA:BB:CC:DD:EE" },
        { "AA:BB:CC:DD:EE:GG" },
        { "AABBCCDDEEFF" },
        { "AA:BB:CC:DD:EE:FF:00" },
        { "" },
    };
  }

  /**
   * Valid EUI-64 address strings.
   *
   * @return Test data.
   */
  @DataProvider
  public Object[][] validEui64Addresses()
  {
    return new Object[][] {
        { "00:11:22:33:44:55:66:77" },
        { "AA:BB:CC:DD:EE:FF:00:11" },
        { "0a:1b:2c:3d:4e:5f:6a:7b" },
    };
  }

  /**
   * Invalid EUI-64 address strings.
   *
   * @return Test data.
   */
  @DataProvider
  public Object[][] invalidEui64Addresses()
  {
    return new Object[][] {
        { "00:11:22:33:44:55:66" },
        { "00:11:22:33:44:55:66:GG" },
        { "0011223344556677" },
        { "00:11:22:33:44:55:66:77:88" },
        { "" },
    };
  }

  private boolean containsField(final ObjectNode root,
                                final String namespace,
                                final String field)
  {
    if (root == null)
    {
      return false;
    }
    else if (root.has(namespace) && root.get(namespace).has(field))
    {
      return true;
    }

    return root.has(field);
  }
}
