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

package com.unboundid.scim2.server;

import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.utils.StaticUtils;
import com.unboundid.scim2.server.utils.ServerUtils;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A {@link ContainerRequestFilter} implementation that is used to assert that
 * a request contains expected sets of headers or query parameters. If the
 * request does not contain the expected headers or query parameters, then the
 * request will be terminated with an HTTP 400 status response. By default,
 * all validation is skipped.
 */
class TestRequestFilter implements ContainerRequestFilter
{
  private MultivaluedMap<String, String> expectedHeaders =
      new MultivaluedHashMap<String, String>();
  private MultivaluedMap<String, String> expectedQueryParams =
      new MultivaluedHashMap<String, String>();

  /** {@inheritDoc} */
  public void filter(ContainerRequestContext requestContext) throws IOException
  {
    MultivaluedMap<String, String> headers =
        requestContext.getHeaders();
    validateRequest(requestContext, headers, expectedHeaders, true);

    MultivaluedMap<String, String> queryParams =
        requestContext.getUriInfo().getQueryParameters();
    validateRequest(requestContext, queryParams, expectedQueryParams, false);
  }

  /**
   * Adds an expected header and value.
   *
   * @param header The expected header name.
   * @param value The expected header value.
   */
  void addExpectedHeader(String header, String value)
  {
    expectedHeaders.add(header, value);
  }

  /**
   * Adds an expected query parameter and value.
   *
   * @param queryParameter The expected query parameter name.
   * @param value The expected query parameter value.
   */
  void addExpectedQueryParam(String queryParameter, String value)
  {
    expectedQueryParams.add(queryParameter, value);
  }

  /**
   * Resets the expected headers and query parameters.
   */
  void reset()
  {
    expectedHeaders.clear();
    expectedQueryParams.clear();
  }

  /**
   * Validates that a request contains the given set of expected headers or
   * query parameters and values.
   *
   * @param requestContext The request context.
   * @param actual The actual set of headers or query parameters.
   * @param expected The expected set of headers or query parameters.
   * @param isCommaDelimited Whether a value may be comma-delimited.
   */
  private void validateRequest(ContainerRequestContext requestContext,
                               MultivaluedMap<String, String> actual,
                               MultivaluedMap<String, String> expected,
                               boolean isCommaDelimited)
  {
    if(!expected.isEmpty())
    {
      for(Map.Entry<String, List<String>> expectedEntry : expected.entrySet())
      {
        List<String> actualEntry = actual.get(expectedEntry.getKey());
        if(actualEntry != null && !actualEntry.isEmpty())
        {
          List<String> actualValues;
          // Checking a header.
          if(isCommaDelimited)
          {
            actualValues =
                Arrays.asList(StaticUtils.splitCommaSeparatedString(
                    actual.get(expectedEntry.getKey()).get(0)));
          }
          // Checking a query parameter.
          else
          {
            actualValues = actual.get(expectedEntry.getKey());
          }
          if(actualValues.containsAll(expectedEntry.getValue()))
          {
            continue;
          }
        }
        ErrorResponse errorResponse = errorResponse();
        requestContext.abortWith(ServerUtils.setAcceptableType(
            Response.status(errorResponse.getStatus())
                .entity(errorResponse),
            requestContext.getAcceptableMediaTypes()).build());
      }
    }
  }

  private ErrorResponse errorResponse()
  {
    return new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode());
  }
}
