/*
 * Copyright 2015-2023 Ping Identity Corporation
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

/**
 * Various constants used by the SCIM API.
 */
public class ApiConstants
{
  /**
   * An HTTP GET to this endpoint will return a JSON structure that
   * describes the SCIM specification features available on a service
   * provider.
   */
  public static final String SERVICE_PROVIDER_CONFIG_ENDPOINT =
      "ServiceProviderConfig";

  /**
   * An HTTP GET to this endpoint is used to discover the types of
   * resources available on a SCIM service provider (for example, Users and
   * Groups).
   */
  public static final String RESOURCE_TYPES_ENDPOINT = "ResourceTypes";

  /**
   * An HTTP GET to this endpoint is used to retrieve information about
   * resource schemas supported by a SCIM service provider.
   */
  public static final String SCHEMAS_ENDPOINT = "Schemas";

  /**
   * The "{@code /Me}" authenticated subject URI alias for the User or other resource
   * associated with the currently authenticated subject for any SCIM operation.
   */
  public static final String ME_ENDPOINT = "Me";

  /**
   * An HTTP POST to this endpoint is used to retrieve information about
   * resource schemas supported by a SCIM service provider.
   */
  public static final String SEARCH_WITH_POST_PATH_EXTENSION = ".search";

  /**
   * The SCIM media type string.
   */
  public static final String MEDIA_TYPE_SCIM = "application/scim+json";

  /**
   * The HTTP query parameter used in a URI to exclude specific SCIM attributes.
   */
  public static final String QUERY_PARAMETER_EXCLUDED_ATTRIBUTES =
      "excludedAttributes";

  /**
   * The HTTP query parameter used in a URI to include specific SCIM attributes.
   */
  public static final String QUERY_PARAMETER_ATTRIBUTES = "attributes";

  /**
   * The HTTP query parameter used in a URI to provide a filter expression.
   */
  public static final String QUERY_PARAMETER_FILTER = "filter";

  /**
   * The HTTP query parameter used in a URI to sort by a SCIM attribute.
   */
  public static final String QUERY_PARAMETER_SORT_BY = "sortBy";

  /**
   * The HTTP query parameter used in a URI to specify the sort order.
   */
  public static final String QUERY_PARAMETER_SORT_ORDER = "sortOrder";

  /**
   * The HTTP query parameter used in a URI to specify the starting index
   * for page results.
   */
  public static final String QUERY_PARAMETER_PAGE_START_INDEX = "startIndex";

  /**
   * The HTTP query parameter used in a URI to specify the maximum size of
   * a page of results.
   */
  public static final String QUERY_PARAMETER_PAGE_SIZE = "count";
}

