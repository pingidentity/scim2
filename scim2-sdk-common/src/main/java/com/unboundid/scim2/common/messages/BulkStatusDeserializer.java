/*
 * Copyright 2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.io.IOException;

/**
 * Deserializer for the {@code status} field of a bulk operation contained
 * within a bulk response. The primary form is:
 * <pre>
 *   {
 *     "status": "200"
 *   }
 * </pre>
 *
 * However, another location in the RFC displays the following form:
 * <pre>
 *   {
 *     "status": {
 *       "code": "200"
 *     }
 *   }
 * </pre>
 *
 * To ensure broader compatibility, this class looks for both forms when parsing
 * the {@code status} value. Note that when the SCIM SDK serializes objects into
 * JSON, it always prints strings of the first form.
 */
public class BulkStatusDeserializer extends JsonDeserializer<String>
{
  /**
   * Implementation of the bulk status deserializer.
   * <br><br>
   *
   * {@inheritDoc}
   */
  @Override
  public String deserialize(@NotNull final JsonParser p,
                            @Nullable final DeserializationContext ctxt)
      throws IOException
  {
    final JsonNode statusNode = JsonUtils.getObjectReader().readTree(p);

    // Check for { "status": "200" }.
    if (statusNode.isTextual())
    {
      return statusNode.asText();
    }

    // Check for the "status.code" sub-attribute.
    JsonNode nested = statusNode.path("code");
    if (nested.isTextual())
    {
      return nested.asText();
    }

    throw new IOException(
        "Could not parse the 'status' field of the bulk operation response."
    );
  }
}
