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

package com.unboundid.scim2.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

/**
 * An interface for handling the search result response. Methods will be called
 * in the order they are received.
 */
public interface SearchResultHandler<T>
{
  /**
   * Handle the startIndex in the search response.
   *
   * @param startIndex The startIndex.
   */
  void startIndex(final int startIndex);

  /**
   * Handle the itemsPerPage in the search response.
   *
   * @param itemsPerPage The itemsPerPage.
   */
  void itemsPerPage(final int itemsPerPage);

  /**
   * Handle the previousCursor in the search response as defined by
   * <a href="https://datatracker.ietf.org/doc/html/rfc9865">RFC 9865</a>.
   *
   * @param previousCursor The previousCursor.
   *
   * @since 5.0.0
   */
  void previousCursor(@Nullable final String previousCursor);

  /**
   * Handle the nextCursor in the search response as defined by
   * <a href="https://datatracker.ietf.org/doc/html/rfc9865">RFC 9865</a>.
   *
   * @param nextCursor The nextCursor.
   *
   * @since 5.0.0
   */
  void nextCursor(@Nullable final String nextCursor);

  /**
   * Handle the totalResults in the search response.
   *
   * @param totalResults The totalResults.
   */
  void totalResults(final int totalResults);

  /**
   * Handle a search result resource.
   *
   * @param scimResource A search result resource.
   * @return {@code true} to continue processing the search result response or
   *         {@code false} to immediate stop further processing of the response.
   */
  boolean resource(@NotNull final T scimResource);

  /**
   * Handle a schema extension in the search response.
   *
   * @param urn The URN of the extension schema.
   * @param extensionObjectNode The ObjectNode representing the extension
   *                            schema.
   */
  void extension(@NotNull final String urn,
                 @NotNull final ObjectNode extensionObjectNode);
}
