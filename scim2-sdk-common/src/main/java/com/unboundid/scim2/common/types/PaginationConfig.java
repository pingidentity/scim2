/*
 * Copyright 2025-2026 Ping Identity Corporation
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
 * Copyright 2025-2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.Nullable;

import java.util.Objects;


/**
 * This class represents a complex type that specifies pagination configuration
 * of a SCIM service, as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9865#section-4">RFC 9865
 * Section 4</a>.
 * <br><br>
 *
 * Pagination is a useful optimization used in many types of APIs, which breaks
 * down large sets of results into manageable pieces referred to as "pages".
 * Without pagination, many restrictions and limitations would need to be
 * considered when obtaining multiple resources. For example, there are limits
 * to how much data can be placed in a JSON payload, which can cause problems
 * with HTTP infrastructure rejecting messages that are too large. Furthermore,
 * generating massive amounts of data increases processing time, resulting in
 * latency that is often undesirable. Pagination allows services and clients to
 * easily handle large amounts of data efficiently.
 * <br><br>
 *
 * There are two types of pagination defined in the SCIM standard:
 * <ul>
 *   <li> Index-based pagination: Allows iterating over the result set by page
 *        number. The first page is identified by a value of {@code 1}.
 *   <li> Cursor-based pagination: Allows iterating over the result set with
 *        string identifiers. When a list response is returned, it will include
 *        a unique cursor corresponding to the next page. The list response may
 *        optionally include another cursor corresponding to the previous page.
 * </ul>
 * <br><br>
 *
 * Cursor-based pagination was added to the SCIM standard in RFC 9865. Note that
 * pagination is not a hard requirement of the SCIM protocol, so some SCIM
 * services may support one, both, or neither approaches.
 * <br><br>
 *
 * A single page of results is represented by a
 * {@link com.unboundid.scim2.common.messages.ListResponse ListResponse} object.
 * The {@code PaginationConfig} data is displayed on a SCIM service's
 * {@code /ServiceProviderConfig} endpoint as a way of clarifying service
 * behavior regarding paging, and is optional.
 * <br><br>
 *
 * An example representation of this class is shown below:
 * <pre>
 * {
 *     "schemas": [
 *         "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig"
 *      ],
 *     ...
 *     "pagination": {
 *         "cursor": true,
 *         "index": true,
 *         "defaultPaginationMethod": "cursor",
 *         "defaultPageSize": 100,
 *         "maxPageSize": 250,
 *         "cursorTimeout": 3600
 *     },
 *     ...
 * }
 * </pre>
 *
 * The above configuration describes a SCIM service provider that:
 * <ul>
 *   <li> Supports both cursor-based and index-based pagination.
 *   <li> Uses cursor-based pagination by default, unless a client explicitly
 *        requests an index-based page number.
 *   <li> Returns up to 100 resources/elements in a page by default.
 *   <li> Returns a maximum of 250 resources in a page.
 *   <li> Invalidates cursor strings after 3600 seconds (1 hour).
 * </ul>
 *
 * @see ServiceProviderConfigResource
 * @since 5.0.0
 */
public class PaginationConfig
{
  @Attribute(description = "A boolean value specifying support of cursor-based"
      + " pagination.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean cursor;

  @Attribute(description = "A boolean value specifying support of index-based"
      + " pagination.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final boolean index;

  @Nullable
  @Attribute(description = "A value specifying the default type of pagination"
      + " for the SCIM service. Possible values are \"cursor\" and \"index\".")
  private final String defaultPaginationMethod;

  @Nullable
  @Attribute(description = "An integer value specifying the default number of"
      + " results returned by the SCIM service in a page.")
  private final Integer defaultPageSize;

  @Nullable
  @Attribute(description = "An integer value specifying the maximum number of"
      + " results that can be returned by the SCIM service in a page.")
  private final Integer maxPageSize;

  @Nullable
  @Attribute(description = """
      A value specifying the minimum number of seconds that a cursor is valid \
      between page requests. No value being specified may mean that there is \
      no cursor timeout, or the cursor timeout is not a static duration.""",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final Integer cursorTimeout;


  /**
   * Create a new complex type that defines pagination behavior for a SCIM
   * service.
   *
   * @param cursor          If cursor-based pagination is supported.
   * @param index           If index-based pagination is supported.
   * @param defaultMethod   The default pagination method. This should be
   *                        "cursor", "index", or {@code null}.
   * @param defaultPageSize The default number of resources returned in a page.
   * @param maxPageSize     The maximum number of resources returned in a page.
   * @param cursorTimeout   The maximum number of seconds a cursor is valid, if
   *                        the SCIM service sets expiration times for cursors.
   */
  public PaginationConfig(
      @JsonProperty(value = "cursor", required = true)
      final boolean cursor,
      @JsonProperty(value = "index", required = true)
      final boolean index,
      @Nullable @JsonProperty(value = "defaultPaginationMethod")
      final String defaultMethod,
      @Nullable @JsonProperty(value = "defaultPageSize")
      final Integer defaultPageSize,
      @Nullable @JsonProperty(value = "maxPageSize")
      final Integer maxPageSize,
      @Nullable @JsonProperty(value = "cursorTimeout")
      final Integer cursorTimeout)
  {
    this.cursor = cursor;
    this.index = index;
    this.defaultPaginationMethod = defaultMethod;
    this.defaultPageSize = defaultPageSize;
    this.maxPageSize = maxPageSize;
    this.cursorTimeout = cursorTimeout;
  }

  /**
   * Indicates whether the SCIM service supports cursor-based pagination as
   * defined by RFC 9865.
   *
   * @return  {@code true} if cursor-based pagination is supported, or
   *          {@code false} if not.
   */
  @JsonProperty("cursor")
  public boolean supportsCursorPagination()
  {
    return cursor;
  }

  /**
   * Indicates whether the SCIM service supports index-based pagination.
   *
   * @return  {@code true} if index-based pagination is supported, or
   *          {@code false} if not.
   */
  @JsonProperty("index")
  public boolean supportsIndexPagination()
  {
    return index;
  }

  /**
   * Indicates the default pagination method. Valid values are {@code cursor}
   * and {@code index}.
   *
   * @return  The default pagination method, or {@code null} if this is not
   *          defined.
   */
  @Nullable
  public String getDefaultPaginationMethod()
  {
    return defaultPaginationMethod;
  }

  /**
   * Indicates the default page size returned for
   * {@link com.unboundid.scim2.common.messages.ListResponse} objects.
   *
   * @return  The default page size, or {@code null} if this is not defined.
   */
  @Nullable
  public Integer getDefaultPageSize()
  {
    return defaultPageSize;
  }

  /**
   * Indicates the maximum page size that will be returned for
   * {@link com.unboundid.scim2.common.messages.ListResponse} objects.
   *
   * @return  The maximum page size, or {@code null} if this is not defined.
   */
  @Nullable
  public Integer getMaxPageSize()
  {
    return maxPageSize;
  }

  /**
   * Indicates the time (in seconds) that a cursor generated by the SCIM service
   * will be valid.
   *
   * @return  The time-to-live for a cursor, or {@code null} if this is not
   *          defined.
   */
  @Nullable
  public Integer getCursorTimeout()
  {
    return cursorTimeout;
  }

  /**
   * Indicates whether the provided object is equal to this pagination
   * configuration.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this pagination
   *            configuration, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof PaginationConfig that))
    {
      return false;
    }

    if (cursor != that.cursor)
    {
      return false;
    }
    if (index != that.index)
    {
      return false;
    }
    if (!Objects.equals(defaultPaginationMethod, that.defaultPaginationMethod))
    {
      return false;
    }
    if (!Objects.equals(defaultPageSize, that.defaultPageSize))
    {
      return false;
    }
    if (!Objects.equals(maxPageSize, that.maxPageSize))
    {
      return false;
    }
    return Objects.equals(cursorTimeout, that.cursorTimeout);
  }

  /**
   * Retrieves a hash code for this pagination configuration object.
   *
   * @return  A hash code for this pagination configuration object.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(cursor, index, defaultPaginationMethod, defaultPageSize,
        maxPageSize, cursorTimeout);
  }
}
