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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.exceptions.BadRequestException;

/**
 * The order in which the sortBy parameter is applied.
 */
public enum SortOrder
{
  /**
   * The ascending sort order.
   */
  ASCENDING("ascending"),


  /**
   * The descending sort order.
   */
  DESCENDING("descending");


  /**
   * The lower case string value for this sort order.
   */
  @NotNull
  private String name;


  /**
   * SortOrder enum private constructor.
   *
   * @param name the name of the sort order.
   */
  SortOrder(@NotNull final String name)
  {
    this.name = name;
  }

  /**
   * Gets the name of the sort order.
   *
   * @return the name of the sort order.
   */
  @NotNull
  @JsonValue
  public String getName()
  {
    return name;
  }

  /**
   * Finds the sort order by name.
   *
   * @param name the name of the mutability constraint.
   * @return the enum value for the given name.
   * @throws BadRequestException if the name of the sort order is invalid.
   */
  @NotNull
  @JsonCreator
  public static SortOrder fromName(@NotNull final String name)
      throws BadRequestException
  {
    for (SortOrder sortOrder : SortOrder.values())
    {
      if (sortOrder.getName().equals(name))
      {
        return sortOrder;
      }
    }

    throw BadRequestException.invalidSyntax(
        "Unknown sort order value " + name);
  }
}
