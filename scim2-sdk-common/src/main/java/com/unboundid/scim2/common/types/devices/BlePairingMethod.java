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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.NotNull;

import java.util.Collection;
import java.util.List;


/**
 * This class represents a supertype for "pairing methods" that define protocols
 * for establishing a Bluetooth connection. This class allows BLE pairing method
 * objects to be nested within a {@link BleDeviceExtension} with a common parent
 * class type.
 * <br><br>
 *
 * For more background on devices, see the {@link DeviceResource} class.
 */
@JsonIgnoreProperties("schemas")
public abstract class BlePairingMethod extends BaseScimResource
{
  /**
   * Create a new Bluetooth Low-Energy Pairing Method.
   */
  protected BlePairingMethod()
  {
    super.setSchemaUrns(List.of());
  }

  @Override
  public void setSchemaUrns(@NotNull final Collection<String> schemaUrns)
      throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException(
        "Cannot set the 'schemas' value of a BLE Pairing Method.");
  }
}
