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

import com.unboundid.scim2.common.types.Group;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * This class contains tests for {@link DeviceResource}, device extensions such
 * as {@link BleDeviceExtension}, and more of the {@code devices} package.
 */
@Test
public class DevicesTest
{
  /**
   * Tests the base {@link DeviceResource} class.
   */
  @Test
  public void testDeviceResource()
  {
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:Device" ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316111",
          "displayName": "BLE Example Monitor",
          "active": true
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    DeviceResource device = new DeviceResource()
        .setDisplayName("BLE Example Monitor")
        .setActive(true);
    device.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");

    assertThat(device.getId())
        .isEqualTo("e9e30dba-f08f-4109-8486-d5c6a3316111");
    assertThat(device.getDisplayName()).isEqualTo("BLE Example Monitor");
    assertThat(device.getActive()).isTrue();
    assertThat(device.getMudUrlString()).isNull();
    assertThat(device.getGroups()).isNotNull().isEmpty();
    assertThat(device.getActive()).isTrue();
    assertThat(device.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(device);
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(serialized);
    assertThat(device).isEqualTo(deserialized);

    Group group = new Group()
        .setValue("beef")
        .setRef("https://example.com/Groups/beef")
        .setDisplay("Admins")
        .setType("direct");

    DeviceResource device2 = new DeviceResource()
        .setDisplayName("BLE Example Monitor")
        .setActive(false)
        .setMudUrl("https://example.com/mud/device.json")
        .setGroups(group);
    device2.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");

    assertThat(device2.getDisplayName()).isEqualTo("BLE Example Monitor");
    assertThat(device2.getActive()).isFalse();
    assertThat(device2.getMudUrl())
        .isEqualTo(URI.create("https://example.com/mud/device.json"));
    assertThat(device2.getMudUrlString())
        .isEqualTo("https://example.com/mud/device.json");
    assertThat(device2.getGroups()).containsExactly(group);
    assertThat(device2.toString()).contains("https://example.com/Groups/beef");
    assertThat(group.toString()).contains("https://example.com/Groups/beef");
  }

  /**
   * Tests for {@link EndpointAppResource}.
   */
  @Test
  public void testEndpointAppResource()
  {
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:EndpointApp" ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316212",
          "applicationType": "deviceControl",
          "applicationName": "Device Control App 1",
          "certificateInfo": {
            "rootCA": "SGV5U3RvcERlY29kaW5nTWU=",
            "subjectName": "www.example.com"
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    CertificateInfo cert = new CertificateInfo("www.example.com")
        .setRootCA("SGV5U3RvcERlY29kaW5nTWU=");

    EndpointAppResource app = new EndpointAppResource()
        .setApplicationType("deviceControl")
        .setApplicationName("Device Control App 1")
        .setCertificateInfo(cert);
    app.setId("e9e30dba-f08f-4109-8486-d5c6a3316212");

    assertThat(app.getId()).isEqualTo("e9e30dba-f08f-4109-8486-d5c6a3316212");
    assertThat(app.getApplicationType()).isEqualTo("deviceControl");
    assertThat(app.getApplicationName()).isEqualTo("Device Control App 1");
    assertThat(app.getCertificateInfo()).isEqualTo(cert);
    assertThat(app.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(app);
    EndpointAppResource deserialized = JsonUtils.getObjectReader()
        .forType(EndpointAppResource.class).readValue(serialized);
    assertThat(app).isEqualTo(deserialized);

    Group group = new Group().setValue("group1").setType("direct");
    EndpointAppResource rich = new EndpointAppResource()
        .setApplicationType("telemetry")
        .setApplicationName("Telemetry App")
        .setClientToken("tok-secret")
        .setCertificateInfo(cert)
        .setGroups(group);
    rich.setId("e9e30dba-f08f-4109-8486-d5c6a3316212");

    assertThat(rich.getClientToken()).isEqualTo("tok-secret");
    assertThat(rich.getGroups()).containsExactly(group);

    String richSerialized = JsonUtils.getObjectWriter().writeValueAsString(rich);
    EndpointAppResource richDeserialized = JsonUtils.getObjectReader()
        .forType(EndpointAppResource.class).readValue(richSerialized);
    assertThat(rich).isEqualTo(richDeserialized);

    // Ensure max length restrictions on client tokens.
    assertThatThrownBy(() -> new EndpointAppResource()
        .setClientToken("a".repeat(501)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Tests for {@link CertificateInfo}.
   */
  @Test
  public void testCertificateInfo()
  {
    // The subjectName DN is printed in the form typically seen in certs.
    String json = """
        {
          "rootCA": "SGV5U3RvcERlY29kaW5nTWU=",
          "subjectName": "CN=EX1, O=Example, C=US"
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    CertificateInfo cert = new CertificateInfo("CN=EX1, O=Example, C=US")
        .setRootCA("SGV5U3RvcERlY29kaW5nTWU=");

    assertThat(cert.getRootCA()).isEqualTo("SGV5U3RvcERlY29kaW5nTWU=");
    assertThat(cert.getSubjectName()).isEqualTo("CN=EX1, O=Example, C=US");
    assertThat(cert.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(cert);
    CertificateInfo deserialized = JsonUtils.getObjectReader()
        .forType(CertificateInfo.class).readValue(serialized);
    assertThat(cert).isEqualTo(deserialized);
  }

  /**
   * Tests for {@link BleDeviceExtension}.
   */
  @Test
  public void testBleDeviceExtension()
  {
    String json = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:ble:2.0:Device"
          ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316111",
          "displayName": "Bluetooth Low Energy Sample Device",
          "active": true,
          "urn:ietf:params:scim:schemas:extension:ble:2.0:Device": {
            "versionSupport": [ "5.4" ],
            "deviceMacAddress": "2C:54:91:88:C9:E2",
            "isRandom": false,
            "mobility": true,
            "separateBroadcastAddress": [
              "AA:BB:88:77:22:11",
              "AA:BB:88:77:22:12"
            ],
            "pairingMethods": [
              "urn:ietf:params:scim:schemas:extension:pairingJustWorks:2.0:Device",
              "urn:ietf:params:scim:schemas:extension:pairingNull:2.0:Device",
              "urn:ietf:params:scim:schemas:extension:pairingOOB:2.0:Device",
              "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device"
            ],
            "urn:ietf:params:scim:schemas:extension:pairingJustWorks:2.0:Device": {
              "key": null
            },
            "urn:ietf:params:scim:schemas:extension:pairingNull:2.0:Device": { },
            "urn:ietf:params:scim:schemas:extension:pairingOOB:2.0:Device": {
              "key": "OOB_Key",
              "randomNumber": 1828,
              "confirmationNumber": 3
            },
            "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device": {
              "key": 12
            }
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    // Obtain the device and its nested extension in object form.
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(expectedJson);
    BleDeviceExtension extension =
        deserialized.getExtension(BleDeviceExtension.class);
    assertThat(extension).isNotNull();

    // Evaluate the individual fields of the extension first.
    assertThat(extension.getDeviceMacAddress()).isEqualTo("2C:54:91:88:C9:E2");
    assertThat(extension.getIrk()).isNull();
    assertThat(extension.getIsRandom()).isFalse();
    assertThat(extension.getSeparateBroadcastAddress())
        .containsExactly("AA:BB:88:77:22:11", "AA:BB:88:77:22:12");
    assertThat(extension.getMobility()).isTrue();
    assertThat(extension.getVersionSupport()).containsExactly("5.4");
    assertThat(extension.getPairingMethods()).containsExactly(
        "urn:ietf:params:scim:schemas:extension:pairingJustWorks:2.0:Device",
        "urn:ietf:params:scim:schemas:extension:pairingNull:2.0:Device",
        "urn:ietf:params:scim:schemas:extension:pairingOOB:2.0:Device",
        "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device"
    );

    // Fetching the elements in object form should result in a list in the same
    // order.
    assertThat(extension.getPairingMethodExtensions()).containsExactly(
        new BlePairingJustWorks(),
        new BlePairingNull(),
        new BlePairingOutOfBand("OOB_Key", 1828).setConfirmationNumber(3),
        new BlePairingPassKey("000012"));

    // Instantiate an object equivalent to the JSON.
    BleDeviceExtension ble =
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
            .setIsRandom(false)
            .setSeparateBroadcastAddress(
                "AA:BB:88:77:22:11", "AA:BB:88:77:22:12")
            .setMobility(true)
            .setPairingExtension(new BlePairingPassKey(12))
            .setPairingExtension(new BlePairingOutOfBand("OOB_Key", 1828, 3))
            .setPairingExtension(new BlePairingNull())
            .setPairingExtension(new BlePairingJustWorks());

    DeviceResource device = new DeviceResource()
        .setDisplayName("Bluetooth Low Energy Sample Device")
        .setActive(true)
        .setDeviceExtension(ble);
    device.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");
    assertThat(device).isEqualTo(deserialized);

    // Check the overall JSON form of the object when it is serialized.
    assertThat(device.toString()).isEqualTo(expectedJson);

    // Check the order for pairing method URNs and objects. These were added in
    // non-alphabetical order, but should still return a consistent order.
    assertThat(ble.getPairingMethods())
        .containsExactlyElementsOf(extension.getPairingMethods());
    assertThat(ble.getPairingMethodExtensions())
        .containsExactlyElementsOf(extension.getPairingMethodExtensions());

    // Try obtaining a field stored on a nested BlePairingMethod object.
    assertThat(ble.getPairingMethodExtensions())
        .filteredOn(p -> p instanceof BlePairingOutOfBand)
        .hasSize(1)
        .first()
        .isInstanceOfSatisfying(BlePairingOutOfBand.class,
            oob -> assertThat(oob.getRandomNumber()).isEqualTo(1828));

    // Test removing the pairing method from the device.
    int originalSize = ble.getPairingMethodExtensions().size();
    assertThat(ble.getPairingMethodExtensions()).hasSize(originalSize);
    assertThat(ble.getPairingMethods()).hasSize(originalSize);
    assertThat(ble).isEqualTo(deserialized.getExtension(BleDeviceExtension.class));

    ble = ble.removePairingExtension(BlePairingOutOfBand.class);
    assertThat(ble.getPairingMethodExtensions())
        .doesNotHaveAnyElementsOfTypes(BlePairingOutOfBand.class);
    assertThat(ble.getPairingMethodExtensions()).hasSize(originalSize - 1);
    assertThat(ble.getPairingMethods()).hasSize(originalSize - 1);
    assertThat(ble)
        .isNotEqualTo(deserialized.getExtension(BleDeviceExtension.class));

    // Try the same call again. This tests removal of a pairing method object
    // that is not present on a BLE extension. This should be a no-op.
    var list = List.copyOf(ble.getPairingMethodExtensions());
    ble.removePairingExtension(BlePairingOutOfBand.class);
    assertThat(ble.getPairingMethodExtensions()).isEqualTo(list);
    assertThat(ble.getPairingMethods()).hasSameSizeAs(list);

    // Test manually setting the pairing methods.
    ble.setPairingMethods(List.of("urn:customValue"));
    assertThat(ble.getPairingMethods()).containsOnly("urn:customValue");

    // Ensure the irk field can be set.
    BleDeviceExtension withIrk =
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
            .setIsRandom(false)
            .setIrk("resolvingKey")
            .setMobility(true);

    assertThat(withIrk.getIrk()).isEqualTo("resolvingKey");

    // Calling toString() should not print the IRK to avoid leaking the
    // value in log messages.
    String serialized = JsonUtils.getObjectWriter().writeValueAsString(withIrk);
    assertThat(serialized).contains(withIrk.getIrk());
    assertThat(withIrk.toString()).contains("--REDACTED--")
        .doesNotContain(withIrk.getIrk());
  }

  /**
   * Tests for {@link BlePairingJustWorks}.
   */
  @Test
  public void testBlePairingJustWorks()
  {
    // The SCIM SDK generally does not print null values. However, printing null
    // is explicitly mandated for the Just Works pairing method.
    String json = """
        {
          "key": null
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    String serialized = new BlePairingJustWorks().toString();
    assertThat(serialized).isEqualTo(expectedJson);

    BlePairingJustWorks deserialized = JsonUtils.getObjectReader()
        .forType(BlePairingJustWorks.class).readValue(json);
    assertThat(deserialized).isEqualTo(new BlePairingJustWorks());
  }

  /**
   * Tests for {@link BlePairingNull}.
   */
  @Test
  public void testBlePairingNull()
  {
    String json = "{}";
    BlePairingNull object = new BlePairingNull();

    String serialized = JsonUtils.getObjectWriter().withDefaultPrettyPrinter()
        .writeValueAsString(object);
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();
    assertThat(serialized).isEqualTo(expectedJson);

    BlePairingNull deserialized = JsonUtils.getObjectReader()
        .forType(BlePairingNull.class).readValue(serialized);
    assertThat(object).isEqualTo(deserialized);
  }

  /**
   * Tests for {@link BlePairingOutOfBand}.
   */
  @Test
  public void testBlePairingOOB()
  {
    String json = """
        {
          "key": "retrievedKey",
          "randomNumber": 987654
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    BlePairingOutOfBand figure6 = new BlePairingOutOfBand(
        "retrievedKey", 987654);

    assertThat(figure6.getKey()).isEqualTo("retrievedKey");
    assertThat(figure6.getRandomNumber()).isEqualTo(987654);
    assertThat(figure6.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(figure6);
    BlePairingOutOfBand deserialized = JsonUtils.getObjectReader()
        .forType(BlePairingOutOfBand.class).readValue(serialized);
    assertThat(figure6).isEqualTo(deserialized);

    BlePairingOutOfBand withConfirmation = new BlePairingOutOfBand(
        "retrievedKey", 987654)
        .setConfirmationNumber(111222);

    assertThat(withConfirmation.getConfirmationNumber()).isEqualTo(111222);

    String fullSerialized =
        JsonUtils.getObjectWriter().writeValueAsString(withConfirmation);
    BlePairingOutOfBand fullDeserialized = JsonUtils.getObjectReader()
        .forType(BlePairingOutOfBand.class).readValue(fullSerialized);
    assertThat(withConfirmation).isEqualTo(fullDeserialized);
  }

  /**
   * Tests for {@link BlePairingPassKey}.
   */
  @Test
  public void testBlePairingPassKey()
  {
    // Leading zeroes are not permitted in JSON integers. This passkey's value
    // is actually "003456".
    String json = """
        {
          "key": 3456
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    BlePairingPassKey passKey = new BlePairingPassKey(3456);

    assertThat(passKey.getKey()).isEqualTo(3456);
    assertThat(passKey.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(passKey);
    BlePairingPassKey deserialized = JsonUtils.getObjectReader()
        .forType(BlePairingPassKey.class).readValue(serialized);
    assertThat(passKey).isEqualTo(deserialized);

    assertThat(passKey.getKeyAsString()).isEqualTo("003456");

    // Attempt using values that are out of bounds.
    assertThatThrownBy(() -> new BlePairingPassKey(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The provided key was not a six digit value");
    assertThatThrownBy(() -> new BlePairingPassKey(1_000_000))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The provided key was not a six digit value");
  }

  /**
   * Tests for {@link DppDeviceExtension}.
   */
  @Test
  public void testDppDeviceExtension()
  {
    String json = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device"
          ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316111",
          "displayName": "WiFi Example Monitor",
          "active": true,
          "urn:ietf:params:scim:schemas:extension:dpp:2.0:Device": {
            "dppVersion": 2,
            "bootstrappingMethod": [ "QR" ],
            "bootstrapKey": "V2F5IGJleW9uZCBjcmF6eSwgU2hhZHkgZ29uZQ==",
            "deviceMacAddress": "2C:54:91:88:C9:F2",
            "classChannel": [ "81/1", "115/36" ],
            "serialNumber": "4774LH2b4044"
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    DppDeviceExtension dpp =
        new DppDeviceExtension(2, "V2F5IGJleW9uZCBjcmF6eSwgU2hhZHkgZ29uZQ==")
            .setDeviceMacAddress("2C:54:91:88:C9:F2")
            .setSerialNumber("4774LH2b4044")
            .setBootstrappingMethod("QR")
            .setClassChannel("81/1", "115/36");

    DeviceResource device = new DeviceResource()
        .setDisplayName("WiFi Example Monitor")
        .setActive(true)
        .setDeviceExtension(dpp);
    device.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");

    assertThat(device.getDeviceExtensions()).containsOnly(dpp);
    assertThat(dpp.getDppVersion()).isEqualTo(2);
    assertThat(dpp.getBootstrapKey())
        .isEqualTo("V2F5IGJleW9uZCBjcmF6eSwgU2hhZHkgZ29uZQ==");
    assertThat(dpp.getDeviceMacAddress()).isEqualTo("2C:54:91:88:C9:F2");
    assertThat(dpp.getSerialNumber()).isEqualTo("4774LH2b4044");
    assertThat(dpp.getBootstrappingMethod()).containsExactly("QR");
    assertThat(dpp.getClassChannel()).containsExactly("81/1", "115/36");
    assertThat(device.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(device);
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(serialized);
    assertThat(device).isEqualTo(deserialized);
    assertThat(deserialized.getDeviceExtensions()).containsOnly(dpp);

    // Calling toString() should not print the secret key to avoid leaking the
    // value in log messages.
    assertThat(serialized).contains(dpp.getBootstrapKey());
    assertThat(dpp.toString()).contains("--REDACTED--")
        .doesNotContain(dpp.getBootstrapKey());
  }

  /**
   * Tests for {@link EthernetMabDeviceExtension}.
   */
  @Test
  public void testEthernetMabDeviceExtension()
  {
    String json = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:ethernet-mab:2.0:Device"
          ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316111",
          "displayName": "Example Ethernet Device",
          "active": true,
          "urn:ietf:params:scim:schemas:extension:ethernet-mab:2.0:Device": {
            "deviceMacAddress": "2C:54:91:88:C9:E2"
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    EthernetMabDeviceExtension mab =
        new EthernetMabDeviceExtension("2C:54:91:88:C9:E2");

    DeviceResource device = new DeviceResource()
        .setDisplayName("Example Ethernet Device")
        .setActive(true)
        .setDeviceExtension(mab);
    device.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");

    assertThat(device.getDeviceExtensions()).containsOnly(mab);
    assertThat(mab.getDeviceMacAddress()).isEqualTo("2C:54:91:88:C9:E2");
    assertThat(device.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(device);
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(serialized);
    assertThat(device).isEqualTo(deserialized);
    assertThat(deserialized.getDeviceExtensions()).containsOnly(mab);
  }

  /**
   * Tests for {@link FdoDeviceExtension}.
   */
  @Test
  public void testFidoDeviceOnboardExtension()
  {
    // fdoVoucher, like other secret fields, can be present in client requests,
    // but will be removed in server responses by scim2-sdk-server's
    // ResourcePreparer, or by other alternative means.
    String json = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device"
          ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316111",
          "displayName": "Example Ethernet Device",
          "active": true,
          "urn:ietf:params:scim:schemas:extension:fido-device-onboard:2.0:Device": {
            "fdoVoucher": "voucher"
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    FdoDeviceExtension fdo = new FdoDeviceExtension("voucher");

    DeviceResource device = new DeviceResource()
        .setDisplayName("Example Ethernet Device")
        .setActive(true)
        .setDeviceExtension(fdo);
    device.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");

    assertThat(device.getDeviceExtensions()).containsOnly(fdo);
    assertThat(fdo.getFdoVoucher()).isEqualTo("voucher");
    assertThat(device.toString()).isEqualTo(expectedJson);

    fdo = fdo.setFdoVoucher("newVoucher");
    assertThat(fdo.getFdoVoucher()).isEqualTo("newVoucher");

    // Reset the voucher to the original value for the next phase of the test.
    fdo = fdo.setFdoVoucher("voucher");
    assertThat(fdo.getFdoVoucher()).isEqualTo("voucher");

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(device);
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(serialized);
    assertThat(device).isEqualTo(deserialized);
    assertThat(deserialized.getDeviceExtensions()).containsOnly(fdo);

    // Calling toString() should not print the secret key to avoid leaking the
    // value in log messages.
    assertThat(serialized).contains(fdo.getFdoVoucher());
    assertThat(fdo.toString()).contains("--REDACTED--")
        .doesNotContain(fdo.getFdoVoucher());
  }

  /**
   * Tests for {@link ZigbeeDeviceExtension}.
   */
  @Test
  public void testZigbeeDeviceExtension()
  {
    String json = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device"
          ],
          "id": "e9e30dba",
          "meta": {
            "resourceType": "Device",
            "created": "1970-01-23T04:56:22Z",
            "lastModified": "1970-05-13T04:42:34Z",
            "location": "https://example.com/v2/Devices/e9e30dba"
          },
          "displayName": "Zigbee Example Monitor",
          "active": true,
          "mudUrl": "https://example.com/lightbulbs/colour/v1",
          "urn:ietf:params:scim:schemas:extension:zigbee:2.0:Device": {
            "versionSupport": [ "3.0" ],
            "deviceEui64Address": "50:32:5F:FF:FE:E7:67:28"
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    ZigbeeDeviceExtension zigbee =
        new ZigbeeDeviceExtension("50:32:5F:FF:FE:E7:67:28", "3.0");

    DeviceResource device = new DeviceResource()
        .setDisplayName("Zigbee Example Monitor")
        .setActive(true)
        .setMudUrl("https://example.com/lightbulbs/colour/v1")
        .setDeviceExtension(zigbee);
    device.setId("e9e30dba");
    device.setMeta(new Meta()
        .setResourceType("Device")
        .setCreatedMillis(1918582000L)
        .setLastModifiedMillis(11421754000L)
        .setLocationString("https://example.com/v2/Devices/e9e30dba"));

    assertThat(device.getDeviceExtensions()).containsOnly(zigbee);
    assertThat(zigbee.getDeviceEui64Address()).isEqualTo("50:32:5F:FF:FE:E7:67:28");
    assertThat(zigbee.getVersionSupport()).containsExactly("3.0");
    assertThat(device.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(device);
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(serialized);
    assertThat(device).isEqualTo(deserialized);
    assertThat(deserialized.getDeviceExtensions()).containsOnly(zigbee);
  }

  /**
   * Ensure that device extensions are explicitly forbidden from having a value
   * for {@code schemas}.
   */
  @Test
  public void testNoSchemasForDeviceExtensions()
  {
    EthernetMabDeviceExtension mab = new EthernetMabDeviceExtension("address");
    assertThatThrownBy(() -> mab.setSchemaUrns("urn:invalidUrn"))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Cannot set the 'schemas' value of a device extension.");

    // Any 'schemas' JSON value should be dropped, as it is invalid.
    String wifiJson = """
        {
          "schemas": [ "urn:this:should:be:unused" ],
          "dppVersion": 3,
          "bootstrappingMethod": [ "QR" ],
          "bootstrapKey": "secretKey",
          "deviceMacAddress": "00:11:22:33:44:55"
        }""";

    DppDeviceExtension wifiExtension = JsonUtils.getObjectReader()
        .forType(DppDeviceExtension.class).readValue(wifiJson);
    assertThat(wifiExtension.getSchemaUrns()).isEmpty();
  }

  /**
   * Tests for {@link EndpointAppReference}.
   */
  @Test
  public void testEndpointAppReference()
  {
    String json = """
        {
          "value": "e9e30dba",
          "$ref": "https://example.com/v2/EndpointApps/e9e30dba"
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    EndpointAppReference app = new EndpointAppReference()
        .setValue("e9e30dba")
        .setRef("https://example.com/v2/EndpointApps/e9e30dba");

    assertThat(app.getValue())
        .isEqualTo("e9e30dba");
    assertThat(app.getRefString())
        .isEqualTo("https://example.com/v2/EndpointApps/e9e30dba");

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(app);
    assertThat(app.toString()).isEqualTo(expectedJson);

    EndpointAppReference deserialized = JsonUtils.getObjectReader()
        .forType(EndpointAppReference.class).readValue(serialized);
    assertThat(app).isEqualTo(deserialized);
  }

  /**
   * Tests for {@link EndpointAppDeviceExtension}.
   */
  @Test
  public void testEndpointAppsDeviceExtension()
  {
    String json = """
        {
          "schemas": [
            "urn:ietf:params:scim:schemas:core:2.0:Device",
            "urn:ietf:params:scim:schemas:extension:endpointAppsExt:2.0:Device"
          ],
          "id": "e9e30dba-f08f-4109-8486-d5c6a3316111",
          "displayName": "BLE Example Monitor",
          "active": true,
          "urn:ietf:params:scim:schemas:extension:endpointAppsExt:2.0:Device": {
            "deviceControlEnterpriseEndpoint":
              "https://example.com/device_control_app_endpoint/",
            "telemetryEnterpriseEndpoint":
              "mqtts://example.com/telemetry_app_endpoint/",
            "applications": [
              {
                "value": "e9e30dba-f08f-4109-8486-d5c6a3316212",
                "$ref": "https://example.com/v2/EndpointApps/e9e30dba-f08f-4109-8486-d5c6a3316212"
              },
              {
                "value": "e9e30dba-f08f-4109-8486-d5c6a3316333",
                "$ref": "https://example.com/v2/EndpointApps/e9e30dba-f08f-4109-8486-d5c6a3316333"
              }
            ]
          }
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(json)
        .toPrettyString();

    EndpointAppReference app1 = new EndpointAppReference()
        .setValue(UUID.fromString("e9e30dba-f08f-4109-8486-d5c6a3316212"))
        .setRef("https://example.com/v2/EndpointApps/"
                + "e9e30dba-f08f-4109-8486-d5c6a3316212");
    EndpointAppReference app2 = new EndpointAppReference()
        .setValue("e9e30dba-f08f-4109-8486-d5c6a3316333")
        .setRef("https://example.com/v2/EndpointApps/"
                + "e9e30dba-f08f-4109-8486-d5c6a3316333");

    EndpointAppDeviceExtension ext = new EndpointAppDeviceExtension()
        .setDeviceControlEnterpriseEndpoint(
            "https://example.com/device_control_app_endpoint/")
        .setTelemetryEnterpriseEndpoint(
            "mqtts://example.com/telemetry_app_endpoint/")
        .setApplications(app1, app2);

    DeviceResource device = new DeviceResource()
        .setDisplayName("BLE Example Monitor")
        .setActive(true)
        .setDeviceExtension(ext);
    device.setId("e9e30dba-f08f-4109-8486-d5c6a3316111");

    assertThat(device.getDeviceExtensions()).containsOnly(ext);
    assertThat(ext.getDeviceControlEnterpriseEndpointString())
        .isEqualTo("https://example.com/device_control_app_endpoint/");
    assertThat(ext.getTelemetryEnterpriseEndpointString())
        .isEqualTo("mqtts://example.com/telemetry_app_endpoint/");
    assertThat(ext.getApplications()).containsExactly(app1, app2);
    assertThat(device.toString()).isEqualTo(expectedJson);

    String serialized = JsonUtils.getObjectWriter().writeValueAsString(device);
    DeviceResource deserialized = JsonUtils.getObjectReader()
        .forType(DeviceResource.class).readValue(serialized);
    assertThat(device).isEqualTo(deserialized);
    assertThat(deserialized.getDeviceExtensions()).containsOnly(ext);
  }

  /**
   * Tests RFC 9944 Section 7.1.1 mutual-exclusion constraint between
   * {@code irk} and {@code separateBroadcastAddress}.
   */
  @Test
  public void testBleIrkAndSeparateBroadcastAddressMutualExclusion()
  {
    // Setting only irk is accepted.
    BleDeviceExtension onlyIrk =
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
            .setIrk("secretIRK");
    assertThat(onlyIrk.getIrk()).isEqualTo("secretIRK");

    // Setting only separateBroadcastAddress is accepted.
    BleDeviceExtension onlySba =
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
            .setSeparateBroadcastAddress("AA:BB:88:77:22:11");
    assertThat(onlySba.getSeparateBroadcastAddress())
        .containsExactly("AA:BB:88:77:22:11");

    // Setting irk when separateBroadcastAddress is already set must throw.
    assertThatThrownBy(() ->
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
            .setSeparateBroadcastAddress("AA:BB:88:77:22:11")
            .setIrk("secretIRK"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("fields cannot both be set");

    // Setting separateBroadcastAddress when irk is already set must throw.
    assertThatThrownBy(() ->
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4")
            .setIrk("secretIRK")
            .setSeparateBroadcastAddress("AA:BB:88:77:22:11"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("fields cannot both be set");
  }

  /**
   * Test valid {@code hashCode()} implementations in the devices package by
   * ensuring the classes do not have conflicting values.
   */
  @Test
  public void testHashCode()
  {
    List<Object> instances = List.of(
        new DeviceResource(),
        new EndpointAppResource(),
        new CertificateInfo("OU=People , DC=Example, DC=Com"),
        new BleDeviceExtension("2C:54:91:88:C9:E2", "5.4"),
        new BlePairingJustWorks(),
        new BlePairingNull(),
        new BlePairingOutOfBand("key", 1),
        new BlePairingPassKey(1),
        new DppDeviceExtension(2, "key"),
        new EthernetMabDeviceExtension("2C:54:91:88:C9:E2"),
        new FdoDeviceExtension("voucher"),
        new ZigbeeDeviceExtension("50:32:5F:FF:FE:E7:67:28", "3.0"),
        new EndpointAppReference(),
        new EndpointAppDeviceExtension());

    Set<Object> set = new HashSet<>(instances);
    assertThat(set).hasSameSizeAs(instances);
  }
}
