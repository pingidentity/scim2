/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.runtime.ScimDeserializeException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ObjectNode;


/**
 * Deserializer for {@link GenericScimResource}.
 */
public class GenericScimObjectDeserializer
    extends ValueDeserializer<GenericScimResource>
{
  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public GenericScimResource deserialize(
      @NotNull final JsonParser jp,
      @Nullable final DeserializationContext ctxt)
  {
    try
    {
      ObjectNode node = JsonUtils.getObjectReader()
          .forType(ObjectNode.class).readValue(jp);
      return new GenericScimResource(node);
    }
    catch (JacksonException e)
    {
      throw new ScimDeserializeException(
          "Cannot convert a non-object JSON to a GenericScimResource.", e);
    }
  }
}
