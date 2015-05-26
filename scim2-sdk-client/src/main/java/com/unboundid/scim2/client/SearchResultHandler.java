/*
 * Copyright 2015 UnboundID Corp.
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
  boolean resource(final T scimResource);

  /**
   * Handle an schema extension in the search response.
   *
   * @param urn The URN of the extension schema.
   * @param extensionObjectNode The ObjectNode representing the extension
   *                            schema.
   */
  void extension(final String urn, final ObjectNode extensionObjectNode);
}
