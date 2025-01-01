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

package com.unboundid.scim2.client.requests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.client.SearchResultHandler;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.messages.ListResponse;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A builder for ListResponses that is also a SearchResultHandler
 * implementation.
 */
public class ListResponseBuilder<T>
    implements SearchResultHandler<T>
{
  @Nullable
  private Integer totalResults;

  @NotNull
  private List<T> resources = new LinkedList<T>();

  @Nullable
  private Integer startIndex;

  @Nullable
  private Integer itemsPerPage;

  /**
   * {@inheritDoc}
   */
  public void startIndex(final int startIndex)
  {
    this.startIndex = startIndex;
  }

  /**
   * {@inheritDoc}
   */
  public void itemsPerPage(final int itemsPerPage)
  {
    this.itemsPerPage = itemsPerPage;
  }

  /**
   * {@inheritDoc}
   */
  public void totalResults(final int totalResults)
  {
    this.totalResults = totalResults;
  }

  /**
   * {@inheritDoc}
   */
  public boolean resource(@NotNull final T scimResource)
  {
    this.resources.add(scimResource);
    return true;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method currently does not perform any action and should not be used.
   */
  public void extension(@NotNull final String urn,
                        @NotNull final ObjectNode extensionObjectNode)
  {
    // TODO: do nothing for now
  }

  /**
   * Builds a List Response.
   *
   * @return generated ListResponse.
   */
  @NotNull
  public ListResponse<T> build()
  {
    final Map<String, Object> properties = new LinkedHashMap<String, Object>();
    properties.put("totalResults", totalResults == null ?
      resources.size() : totalResults);
    properties.put("resources", resources);
    properties.put("startIndex", startIndex);
    properties.put("itemsPerPage", itemsPerPage);
    return new ListResponse<T>(properties);
  }
}
