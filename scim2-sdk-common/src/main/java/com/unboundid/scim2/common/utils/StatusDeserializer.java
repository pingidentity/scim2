/*
 * Copyright 2015-2024 Ping Identity Corporation
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.io.IOException;

/**
 * Deserializes the status field of
 * {@link com.unboundid.scim2.common.messages.ErrorResponse} to an Integer.
 */
public class StatusDeserializer extends JsonDeserializer<Integer>
{
  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public Integer deserialize(@NotNull final JsonParser jp,
                             @Nullable final DeserializationContext ctxt)
      throws IOException, JsonProcessingException
  {
    return jp.readValueAs(Integer.class);
  }
}
