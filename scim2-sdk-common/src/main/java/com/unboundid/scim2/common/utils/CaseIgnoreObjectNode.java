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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class provides an ObjectNode implementation that treats property names
 * as case-insensitive. Although the {@code ACCEPT_CASE_INSENSITIVE_PROPERTIES}
 * property is enabled by the SCIM SDK, this is only used for deserialization,
 * so it does not apply to the Jackson tree model once objects are instantiated.
 * Thus, this class aligns with the SCIM standard's handling of attribute names.
 * <br><br>
 *
 * To create an instance, use the {@link JsonUtils} class or a saved JsonMapper:
 * <pre><code>
 *   ObjectNode caseIgnoreNode = JsonUtils.getJsonNodeFactory().objectNode();
 *
 *   final JsonMapper mapper = JsonUtils.createJsonMapper();
 *   ObjectNode otherCaseIgnoreNode = mapper.createObjectNode();
 * </code></pre>
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
   * Obtains values of a named JSON property within a nested ObjectNode.
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
   * Obtains string values of a named JSON property within a nested ObjectNode.
   * External callers should use {@link #findValuesAsString(String)}.
   *
   * @param propertyName The name of the JSON field/attribute.
   * @param foundSoFar   An optional argument for recursive calls by Jackson.
   *
   * @return  The list of values.
   */
  @Override
  @NotNull
  public List<String> findValuesAsString(
      @NotNull final String propertyName,
      @Nullable final List<String> foundSoFar)
  {
    List<String> parsed = findParents(propertyName, null)
        .stream().map(node -> node.path(propertyName).asString()).toList();
    return mutableList(foundSoFar, parsed);
  }

  /**
   * Obtains JSON objects within this node that match the specified field.
   * External callers should use {@link #findParents(String)}.
   *
   * @param propertyName  The name of the JSON field/attribute.
   * @param _found        An optional argument for recursive calls.
   *
   * @return  A list containing all matching nodes.
   */
  @Override
  @NotNull
  public List<JsonNode> findParents(@NotNull final String propertyName,
                                    @Nullable final List<JsonNode> _found)
  {
    // Use a ternary operator to avoid creating a list on every recursive call.
    List<JsonNode> foundSoFar = (_found == null) ? new ArrayList<>() : _found;

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
    List<T> returnList = new ArrayList<>();
    for (List<T> list : lists)
    {
      if (list != null)
      {
        returnList.addAll(list);
      }
    }

    return returnList;
  }
}
