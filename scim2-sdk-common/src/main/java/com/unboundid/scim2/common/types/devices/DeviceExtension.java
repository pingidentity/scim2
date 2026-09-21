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
 * This class represents a supertype for all device extension objects described
 * in {@link DeviceResource}. This structure allows device extension objects to
 * be nested within a {@link DeviceResource} without printing a {@code schemas}
 * value of their own.
 */
@JsonIgnoreProperties("schemas")
public abstract class DeviceExtension extends BaseScimResource
{
  /**
   * The regex matching MAC addresses as defined by RFC 9944.
   */
  @NotNull
  public static final String MAC_PATTERN =
      "^[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}$";

  /**
   * Creates a new device extension.
   */
  protected DeviceExtension()
  {
    super.setSchemaUrns(List.of());
  }

  @Override
  public void setSchemaUrns(@NotNull final Collection<String> schemaUrns)
      throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException(
        "Cannot set the 'schemas' value of a device extension.");
  }
}
