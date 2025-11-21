/*
 * Copyright 2015-2025 Ping Identity Corporation
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
/*
 * Portions Copyright ©2009-2015 FasterXML, LLC
 */

package com.unboundid.scim2.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unboundid.scim2.common.utils.StaticUtils.toLowerCase;

/**
 * An ObjectNode with case-insensitive field names.
 */
public class CaseIgnoreObjectNode extends ObjectNode
{
  /**
   * Create a new CaseIgnoreObjectNode.
   *
   * @param nc The JsonNodeFactory.
   */
  public CaseIgnoreObjectNode(@NotNull final JsonNodeFactory nc)
  {
    super(nc, new CaseIgnoreMap());
  }

  /**
   * Create a new CaseIgnoreObjectNode.
   *
   * @param nc   The JsonNodeFactory.
   * @param kids The fields to put in this CaseIgnoreObjectNode.
   */
  public CaseIgnoreObjectNode(@NotNull final JsonNodeFactory nc,
                              @NotNull final Map<String, JsonNode> kids)
  {
    super(nc, new CaseIgnoreMap(kids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public ObjectNode deepCopy()
  {
    CaseIgnoreObjectNode ret = new CaseIgnoreObjectNode(_nodeFactory);

    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      ret._children.put(entry.getKey(), entry.getValue().deepCopy());
    }

    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public JsonNode findValue(@NotNull final String fieldName)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      if (fieldName.equals(entry.getKey()))
      {
        return entry.getValue();
      }
      JsonNode value = entry.getValue().findValue(fieldName);
      if (value != null)
      {
        return value;
      }
    }
    return null;
  }

  /**
   * Similar to {@link #findValue}, but returns multiple values.
   *
   * @param fieldName   The name of the JSON field/attribute.
   * @param foundSoFar  An optional argument for recursive calls. External
   *                    callers should set this value to {@code null}.
   *
   * @return  The list of values.
   */
  @Override
  @NotNull
  public List<JsonNode> findValues(@NotNull final String fieldName,
                                   @Nullable final List<JsonNode> foundSoFar)
  {
    List<JsonNode> localFoundSoFar = foundSoFar;
    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      if (toLowerCase(fieldName).equals(toLowerCase(entry.getKey())))
      {
        if (localFoundSoFar == null)
        {
          localFoundSoFar = new ArrayList<>();
        }
        localFoundSoFar.add(entry.getValue());
      }
      else
      { // only add children if parent not added
        localFoundSoFar = entry.getValue().findValues(fieldName, foundSoFar);
      }
    }
    return localFoundSoFar;
  }

  /**
   * Similar to {@link #findValues}, but invokes {@link #asText()} on each
   * element.
   *
   * @param fieldName   The name of the JSON field/attribute.
   * @param foundSoFar  An optional argument to specify known values. External
   *                    calls generally should set this value to {@code null}.
   *
   * @return  The list of values.
   */
  @Override
  @NotNull
  public List<String> findValuesAsText(@NotNull final String fieldName,
                                       @Nullable final List<String> foundSoFar)
  {
    List<String> localFoundSoFar = foundSoFar;
    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      if (toLowerCase(fieldName).equals(toLowerCase(entry.getKey())))
      {
        if (localFoundSoFar == null)
        {
          localFoundSoFar = new ArrayList<>();
        }
        localFoundSoFar.add(entry.getValue().asText());
      }
      else
      { // only add children if parent not added
        localFoundSoFar = entry.getValue().findValuesAsText(fieldName,
            foundSoFar);
      }
    }
    return localFoundSoFar;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public ObjectNode findParent(@NotNull final String fieldName)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      if (toLowerCase(fieldName).equals(toLowerCase(entry.getKey())))
      {
        return this;
      }
      JsonNode value = entry.getValue().findParent(fieldName);
      if (value != null)
      {
        return (ObjectNode) value;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public List<JsonNode> findParents(@NotNull final String fieldName,
                                    @Nullable final List<JsonNode> foundSoFar)
  {
    List<JsonNode> localFoundSoFar = foundSoFar;
    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      if (toLowerCase(fieldName).equals(toLowerCase(entry.getKey())))
      {
        if (localFoundSoFar == null)
        {
          localFoundSoFar = new ArrayList<>();
        }
        localFoundSoFar.add(this);
      }
      else
      { // only add children if parent not added
        localFoundSoFar = entry.getValue()
            .findParents(fieldName, foundSoFar);
      }
    }
    return localFoundSoFar;
  }
}
