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

/**
 * This class provides an ObjectNode implementation that treats field/property
 * names as case-insensitive. This is aligned with the SCIM standard's treatment
 * of attribute names.
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
   * @param nc        The JsonNodeFactory.
   * @param children  The fields to put in this CaseIgnoreObjectNode.
   */
  public CaseIgnoreObjectNode(@NotNull final JsonNodeFactory nc,
                              @NotNull final Map<String, JsonNode> children)
  {
    super(nc, new CaseIgnoreMap(children));
  }

  /**
   * Similar to {@link #findValue}, but returns multiple values.
   * External callers should use {@link #findValues(String)}.
   *
   * @param propertyName  The name of the JSON field/attribute.
   * @param foundSoFar    An optional argument for recursive calls by Jackson.
   *
   * @return  The list of values.
   */
  @Override
  @NotNull
  public List<JsonNode> findValues(@NotNull final String propertyName,
                                   @Nullable final List<JsonNode> foundSoFar)
  {
    List<JsonNode> parsed = findParents(propertyName, null)
        .stream().map(node -> node.path(propertyName)).toList();
    return mutableList(foundSoFar, parsed);
  }

  /**
   * Similar to {@link #findValues(String, List)}, but returns string values.
   *
   * @param propertyName The name of the JSON field/attribute.
   * @param initial      An optional argument for recursive calls by Jackson.
   *
   * @return  The list of values.
   */
  @Override
  @NotNull
  public List<String> findValuesAsText(@NotNull final String propertyName,
                                      @Nullable final List<String> initial)
  {
    List<String> parsed = findParents(propertyName, null)
        .stream().map(node -> node.path(propertyName).asText()).toList();
    return mutableList(initial, parsed);
  }

  /**
   * Obtains a JSON object that contains a specified field, within this node or
   * its descendants. External callers should use {@link #findParents(String)}.
   *
   * @param propertyName  The name of the JSON field/attribute.
   * @param foundSoFar    An optional argument for recursive calls.
   *
   * @return  A list containing all matching nodes.
   */
  @Override
  @NotNull
  // CHECKSTYLE:OFF
  public List<JsonNode> findParents(@NotNull final String propertyName,
                                    @Nullable List<JsonNode> foundSoFar)
  // CHECKSTYLE:ON
  {
    foundSoFar = (foundSoFar == null) ? new ArrayList<>() : foundSoFar;
    for (Map.Entry<String, JsonNode> entry : _children.entrySet())
    {
      // Ensure case-insensitive comparison for CaseIgnoreObjectNode.
      if (propertyName.equalsIgnoreCase(entry.getKey()))
      {
        foundSoFar.add(this);
      }
      else
      {
        foundSoFar = entry.getValue().findParents(propertyName, foundSoFar);
      }
    }

    return foundSoFar;
  }

  @SafeVarargs
  @NotNull
  private static <T> List<T> mutableList(@NotNull final List<T>... lists)
  {
    List<T> list = new ArrayList<>();
    for (List<T> l : lists)
    {
      if (l != null)
      {
        list.addAll(l);
      }
    }

    return list;
  }
}
