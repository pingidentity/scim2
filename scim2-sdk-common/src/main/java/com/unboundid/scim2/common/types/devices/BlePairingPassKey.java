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

import java.util.Objects;


/**
 * This class represents a {@link BlePairingMethod} subtype corresponding to the
 * Bluetooth Low Energy "Pass Key" method. The passkey pairing method requires a
 * six-digit key to pair devices. For more background on how this object
 * interacts with a device extension, see {@link BleDeviceExtension}.
 * <br><br>
 *
 * The following JSON represents the form for this pairing method as it would
 * appear inside a {@link BleDeviceExtension}. This contains a mandatory key
 * that takes a numeric value up to six digits.
 * <pre>
 *   "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device": {
 *     "key": 123456
 *   }
 * </pre>
 * <br><br>
 *
 * This JSON value can be created with the following Java code:
 * <pre><code>
 *   bleDeviceExtension.setPairingExtension(new BlePairingPassKey(123456));
 * </code></pre>
 * <br><br>
 *
 * In JSON form, this integer value will not have six digits if the integer
 * value is less than {@code 100,000}, as leading zeroes are not allowed in JSON
 * integer values. If a six-digit string value is required, use
 * {@link #getKeyAsString()}.
 */
@Schema(id = "urn:ietf:params:scim:schemas:extension:pairingPassKey:2.0:Device",
    name = "Passkey Pairing for BLE",
    description = "BLE Pass Key pairing method")
public class BlePairingPassKey extends BlePairingMethod
{
  @Attribute(description =
      "The six-digit passkey (000000-999999) used for BLE pairing.",
      isRequired = true)
  private final int key;

  /**
   * Creates a new BLE Passkey pairing method.
   *
   * @param key The six-digit passkey (000000–999999).
   *
   * @throws IllegalArgumentException  If the key is not a valid 6 digit value.
   */
  @JsonCreator
  public BlePairingPassKey(
      @JsonProperty(value = "key", required = true) final int key)
          throws IllegalArgumentException
  {
    if (key < 0 || key > 999_999)
    {
      throw new IllegalArgumentException(
          "The provided key was not a six digit value: " + key);
    }

    this.key = key;
  }

  /**
   * Alternate constructor that accepts a string input.
   *
   * @param key The six-digit passkey as a string.
   *
   * @throws IllegalArgumentException  If the key is not a valid 6 digit value.
   */
  public BlePairingPassKey(@NotNull final String key)
      throws IllegalArgumentException
  {
    this(Integer.parseInt(Objects.requireNonNull(key)));
  }

  /**
   * Retrieves the passkey as an integer.
   *
   * @return The six-digit passkey.
   */
  public int getKey()
  {
    return key;
  }

  /**
   * Retrieves the passkey as a six-digit string value.
   *
   * @return The six-digit passkey.
   */
  @NotNull
  @JsonIgnore
  public String getKeyAsString()
  {
    return "%06d".formatted(key);
  }


  /**
   * Indicates whether the provided object is equal to this BLE Passkey object.
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

    return o instanceof BlePairingPassKey that && key == that.key;
  }

  /**
   * Retrieves a hash code for this BLE Passkey object.
   *
   * @return  A hash code for this object.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(key);
  }
}
