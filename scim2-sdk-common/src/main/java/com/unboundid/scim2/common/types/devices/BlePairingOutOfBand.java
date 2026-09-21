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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.utils.JsonUtils;
import tools.jackson.databind.node.NumericNode;

import java.util.Objects;


/**
 * This class represents a {@link BlePairingMethod} subtype corresponding to the
 * Bluetooth Low Energy "Out-of-Band" method. For more background on how this
 * object interacts with a device extension, see {@link BleDeviceExtension}.
 * <br><br>
 *
 * The following fields are defined:
 * <ul>
 *   <li> {@code key}:                A value received from out-of-band sources,
 *                                    such as Near Field Communication (NFC).
 *   <li> {@code randomNumber}:       A number that represents a cryptographic
 *                                    "number-used-once" added to the key.
 *   <li> {@code confirmationNumber}: An optional number that some solutions
 *                                    require in a RESTful message exchange.
 * </ul>
 *
 * The following JSON represents the form for this pairing method as it would
 * appear inside a {@link BleDeviceExtension}:
 * <pre>
 *   "urn:ietf:params:scim:schemas:extension:pairingOOB:2.0:Device": {
 *     "key": "retrievedKey",
 *     "randomNumber": 238796813516896
 *   }
 * </pre>
 *
 * This value can be set on a device extension with the following Java code:
 * <pre><code>
 *   bleDeviceExtension.setPairingExtension(
 *       new BlePairingOutOfBand("retrievedKey", 238796813516896L));
 * </code></pre>
 */
@Schema(id = "urn:ietf:params:scim:schemas:extension:pairingOOB:2.0:Device",
    name = "Out-of-Band Pairing for BLE",
    description = "BLE Out-of-Band pairing method")
public class BlePairingOutOfBand extends BlePairingMethod
{
  @NotNull
  @Attribute(description = "The OOB pairing key.",
      isRequired = true,
      isCaseExact = true)
  private final String key;

  // Stored as a JsonNode for flexible equivalency evaluation.
  @NotNull
  @Attribute(description = "The random number used in OOB pairing.",
      isRequired = true)
  @JsonProperty("randomNumber")
  private final NumericNode randomNumber;

  @Nullable
  @Attribute(description = "The confirmation number used in OOB pairing.")
  private Integer confirmationNumber;

  /**
   * Creates a Bluetooth Low Energy out-of-band pairing method.
   *
   * @param key                 The key.
   * @param randomNumber        The cryptographic number-used-once value.
   * @param confirmationNumber  The optional confirmation number.
   */
  @JsonCreator
  public BlePairingOutOfBand(
      @NotNull @JsonProperty(value = "key", required = true)
      final String key,
      @JsonProperty(value = "randomNumber", required = true)
      final long randomNumber,
      @Nullable @JsonProperty(value = "confirmationNumber")
      final Integer confirmationNumber)
  {
    this.key = Objects.requireNonNull(key);
    this.randomNumber = JsonUtils.asNumericNode(randomNumber);
    this.confirmationNumber = confirmationNumber;
  }

  /**
   * Creates a Bluetooth Low Energy out-of-band pairing method.
   *
   * @param key           The key.
   * @param randomNumber  The cryptographic number-used-once value.
   */
  public BlePairingOutOfBand(@NotNull final String key, final long randomNumber)
  {
    this(key, randomNumber, null);
  }

  /**
   * Fetches the OOB pairing key.
   *
   * @return The OOB pairing key.
   */
  @NotNull
  public String getKey()
  {
    return key;
  }

  /**
   * Fetches the random number used in OOB pairing.
   *
   * @return The random number.
   */
  @JsonIgnore
  public long getRandomNumber()
  {
    return randomNumber.longValue();
  }

  /**
   * Fetches the confirmation number used in OOB pairing.
   *
   * @return The confirmation number.
   */
  @Nullable
  public Integer getConfirmationNumber()
  {
    return confirmationNumber;
  }

  /**
   * Specifies the confirmation number used in OOB pairing.
   *
   * @param confirmationNumber The confirmation number.
   * @return This object.
   */
  @NotNull
  public BlePairingOutOfBand setConfirmationNumber(
      @Nullable final Integer confirmationNumber)
  {
    this.confirmationNumber = confirmationNumber;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this BLE pairing object.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this object,
   *            or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    return o instanceof BlePairingOutOfBand that
        && randomNumber.equals(that.randomNumber)
        && Objects.equals(key, that.key)
        && Objects.equals(confirmationNumber, that.confirmationNumber);
  }

  /**
   * Retrieves a hash code for this BLE OOB pairing object.
   *
   * @return  A hash code for this object.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(key, randomNumber, confirmationNumber);
  }
}
