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

package com.unboundid.scim2.common.bulk;

import com.unboundid.scim2.common.annotations.NotNull;

/**
 * An enum representing possible bulk operation types. Note that
 * {@code HTTP GET} methods are not permitted within bulk requests, as bulk
 * requests are designed for write operations. Thus, there is not an enum
 * defined for that here.
 */
public enum BulkOpType
{
  /**
   * The "POST" bulk operation type used to create a resource.
   */
  POST("POST"),

  /**
   * The "PUT" bulk operation type used to update a resource.
   */
  PUT("PUT"),

  /**
   * The "PATCH" bulk operation type used to update a resource.
   */
  PATCH("PATCH"),

  /**
   * The "DELETE" bulk operation type used to delete a resource.
   */
  DELETE("DELETE"),
  ;

  @NotNull
  private final String stringValue;

  BulkOpType(@NotNull final String stringValue)
  {
    this.stringValue = stringValue;
  }

  @Override
  @NotNull
  public String toString()
  {
    return stringValue;
  }
}
