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

package com.unboundid.scim2.common.utils;

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.types.Manager;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;


/**
 * This is a custom deserializer for the {@code manager} field of the
 * {@link com.unboundid.scim2.common.types.EnterpriseUserExtension} class. This
 * ensures that known malformed representations of a Manager are gracefully
 * handled with the intended structure.
 */
public class EnterpriseManagerDeserializer extends ValueDeserializer<Manager>
{
  /**
   * Converts a JSON field into a {@link Manager}.
   */
  @Override
  @NotNull
  public Manager deserialize(@NotNull final JsonParser p,
                             @NotNull final DeserializationContext ctxt)
  {
    try
    {
      return ctxt.readValue(p, Manager.class);
    }
    catch (JacksonException e)
    {
      // Some implementations set "manager" to a string that should actually
      // represent the "value". If a string is present, handle it gracefully.
      JsonNode node = p.readValueAsTree();
      if (node.isString())
      {
        return new Manager().setValue(node.asString());
      }

      throw e;
    }
  }
}
