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
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.runtime.ScimDeserializeException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Deserializer for the {@code status} field of a bulk operation contained
 * within a bulk response. The primary form is:
 * <pre>
 *   {
 *     "status": "200"
 *   }
 * </pre>
 *
 * However, a single reference in the RFC displays the following form, which is
 * likely an artifact from the SCIM 1.1 standard that is not used:
 * <pre>
 *   {
 *     "status": {
 *       "code": "200"
 *     }
 *   }
 * </pre>
 *
 * To ensure broader compatibility, this deserializer looks for both forms when
 * parsing the {@code status} value. Note that when the SCIM SDK serializes
 * objects into JSON, it always prints strings of the first form.
 */
public class BulkStatusDeserializer extends ValueDeserializer<String>
{
  /**
   * Implementation of the bulk status deserializer. See the class-level Javadoc
   * for more information.
   * <br><br>
   *
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public String deserialize(@NotNull final JsonParser p,
                            @Nullable final DeserializationContext ctxt)
  {
    // Check for the most common form: { "status": "200" }
    String standardValue = p.getValueAsString();
    if (standardValue != null)
    {
      return standardValue;
    }

    // Check for a nested view: { "status": { "code": "200" } }
    String nextField = p.nextName();
    if (!"code".equals(nextField))
    {
      throw new ScimDeserializeException(
          "Could not parse the 'status' field of the bulk operation response.");
    }

    String statusValue = p.nextStringValue();

    // The parser still points to the status value. Before returning, navigate
    // to the closing brace.
    JsonToken token;
    do
    {
      token = p.nextToken();
    }
    while (!p.isClosed() && token != JsonToken.END_OBJECT);
    return statusValue;
  }
}
