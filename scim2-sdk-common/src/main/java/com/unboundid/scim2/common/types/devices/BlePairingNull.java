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

import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;


/**
 * This class represents a {@link BlePairingMethod} subtype corresponding to the
 * Bluetooth Low Energy "Null" method. For more background on how this object
 * interacts with a device extension, see {@link BleDeviceExtension}.
 * <br><br>
 *
 * This pairing method JSON object is unique in that it is empty:
 * <pre>
 *   "urn:ietf:params:scim:schemas:extension:pairingNull:2.0:Device": {}
 * </pre>
 *
 * Instead, the presence of this value in the {@code pairingMethods} list of a
 * {@link BleDeviceExtension} indicates that no pairing is required. This value
 * can be set on a device extension with the following Java code:
 * <pre><code>
 *   bleDeviceExtension.setPairingExtension(new BlePairingNull());
 * </code></pre>
 */
@Schema(
    id = "urn:ietf:params:scim:schemas:extension:pairingNull:2.0:Device",
    name = "Pairing Null",
    description = "BLE Null pairing method — no pairing required")
public class BlePairingNull extends BlePairingMethod
{
  /**
   * Indicates whether the provided object is equal to this BLE pairing null
   * bean.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this bean,
   *            or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    return o instanceof BlePairingNull;
  }

  /**
   * Retrieves a hash code for this BLE pairing null bean.
   *
   * @return  A hash code for this bean.
   */
  @Override
  public int hashCode()
  {
    return getClass().hashCode();
  }
}
