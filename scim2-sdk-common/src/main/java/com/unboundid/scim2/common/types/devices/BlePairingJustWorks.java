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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;


/**
 * This class represents a {@link BlePairingMethod} subtype corresponding to the
 * Bluetooth Low Energy "Just Works" method. This pairing method does not
 * require a key to pair devices, and its pairing key is always set to
 * {@code null}. For more background on how this object interacts with a device
 * extension, see {@link BleDeviceExtension}.
 * <br><br>
 *
 * The following JSON represents the form for this pairing method as it would
 * appear inside a {@link BleDeviceExtension}. This pairing method contains only
 * the {@code key} field, which is always {@code null}.
 * <pre>
 *   "urn:ietf:params:scim:schemas:extension:pairingJustWorks:2.0:Device": {
 *     "key": null
 *   }
 * </pre>
 *
 * This value can be set on a device extension with the following Java code:
 * <pre><code>
 *   bleDeviceExtension.setPairingExtension(new BlePairingJustWorks());
 * </code></pre>
 */
@Schema(
    id = "urn:ietf:params:scim:schemas:extension:pairingJustWorks:2.0:Device",
    name = "Just Works Auth BLE",
    description = "BLE Just Works pairing method")
public class BlePairingJustWorks extends BlePairingMethod
{
  @Nullable
  @Attribute(description = """
      The pairing key for the "Just Works" method. The value is always null.""",
      isRequired = true,
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  @JsonInclude(JsonInclude.Include.ALWAYS)
  private final Integer key = null;

  /**
   * Obtains the pairing key of a BLE Just Works extension. This method will
   * always return {@code null} and primarily exists for Jackson serialization.
   *
   * @return  The pairing key's value, which is always {@code null}.
   */
  @Nullable
  public Integer getKey()
  {
    return key;
  }

  /**
   * Indicates whether the provided object is equal to this BLE Just Works
   * pairing method.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this object,
   *            or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    return o instanceof BlePairingJustWorks;
  }

  /**
   * Retrieves a hash code for this BLE Just Works pairing method.
   *
   * @return  A hash code for this object.
   */
  @Override
  public int hashCode()
  {
    return getClass().hashCode();
  }
}
