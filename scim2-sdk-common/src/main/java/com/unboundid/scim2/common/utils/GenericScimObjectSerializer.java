/*
 * Copyright 2015-2025 Ping Identity Corporation
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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.io.IOException;

/**
 * Serializer for {@link GenericScimResource}.
 */
public class GenericScimObjectSerializer
    extends JsonSerializer<GenericScimResource>
{
  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NotNull final GenericScimResource value,
      @NotNull final JsonGenerator jgen,
      @Nullable final SerializerProvider provider)
          throws IOException
  {
    JsonUtils.getObjectWriter().writeValue(jgen, value.getObjectNode());
  }
}
