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

package com.unboundid.broker.client;

import com.unboundid.broker.messages.Consent;
import com.unboundid.broker.messages.ConsentHistory;
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.client.requests.SearchRequestBuilder;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.ListResponse;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Client for working with Consent or Consent History objects.
 */
public class ConsentClient
{
  private URI baseUrl;
  private String username, password;
  private Configuration clientConfig;

  /**
   * Constructs a new ConsentClient.
   *
   * @param baseUrl The base url for the scim service.  For example,
   *                http://localhost:8443/scim/v2.
   * @param clientConfig Client configuration for this client.
   */
  public ConsentClient(final URI baseUrl, final Configuration clientConfig)
  {
    this.baseUrl = baseUrl;
    this.clientConfig = clientConfig;
  }

  /**
   * Gets the base url.
   *
   * @return the base url.
   */
  public URI getBaseURL()
  {
    return baseUrl;
  }

  /**
   * Gets (searches) consents for all consents with a particular user id.
   *
   * @param dataViewEndpoint the dataview to use for the search.
   * @param userId the userId to search consents for.
   * @return All consents for the user.
   * @throws Exception thrown if an error occurs.
   */
  public ListResponse<Consent> get(final String dataViewEndpoint,
      final String userId) throws Exception
  {
    return get(dataViewEndpoint, userId, (Filter) null);
  }

  /**
   * Gets (searches) consents for all consents with a particular user id.
   * This method will also take a filter to further narrow the results.
   *
   * @param dataViewEndpoint the dataview to use for the search.
   * @param userId the userId to search consents for.
   * @param filter the filter to apply for the search.
   * @return All consents for the user that match the filter
   * @throws Exception thrown if an error occurs.
   */
  public ListResponse<Consent> get(final String dataViewEndpoint,
      final String userId, final Filter filter) throws Exception
  {
    ScimService scimService;
    Client client;
    UriBuilder uriBuilder =
        UriBuilder.fromPath(dataViewEndpoint).path(userId).path("consents");

    URI uri = uriBuilder.build();

    client = getClient();
    scimService = new ScimService(client.target(baseUrl));

    SearchRequestBuilder searchRequestBuilder =
        scimService.searchRequest(uri.toString());
    if(filter != null)
    {
      searchRequestBuilder.filter(filter.toString());
    }
    return searchRequestBuilder.invoke(Consent.class);
  }

  /**
   * Gets the consent entry for a given application for the user specified.
   *
   * @param dataViewEndpoint the dataview to use for the search.
   * @param userId the userId to search consents for.
   * @param clientId the client id of the application to get the consent record
   *                 for.
   * @return The consent for the user for the specified application.
   * @throws Exception thrown if an error occurs.
   */
  public Consent get(final String dataViewEndpoint, final String userId,
      final String clientId) throws Exception
  {
    ScimService scimService;
    Client client;
    URI uri =
        UriBuilder.fromPath(dataViewEndpoint).
            path(userId).path("consents").build();

    client = getClient();
    scimService = new ScimService(client.target(baseUrl));

    return scimService.retrieveRequest(uri.toString(), clientId).
        invoke(Consent.class);
  }

  /**
   * Deletes a consent entry for an application for a given user.
   *
   * @param dataViewEndpoint the dataview to use for the delete.
   * @param userId the userId to delete a consent for.
   * @param clientId the client id of the consent record to delete for the user.
   * @throws Exception thrown if an error occurs.
   */
  public void delete(final String dataViewEndpoint, final String userId,
      final String clientId) throws Exception
  {
    ScimService scimService;
    Client client;
    URI uri =
        UriBuilder.fromPath(dataViewEndpoint).
            path(userId).path("consents").build();

    client = getClient();
    scimService = new ScimService(client.target(baseUrl));

    scimService.deleteRequest(uri.toString(), clientId).invoke();
  }

  /**
   * Gets (searches) consent history for all consent history entries
   * with a particular user id.
   *
   * @param dataViewEndpoint the dataview to use for the search.
   * @param userId the userId to search consent history for.
   * @return All consent history for the user.
   * @throws Exception thrown if an error occurs.
   */
  public ListResponse<ConsentHistory>
      getHistory(final String dataViewEndpoint, final String userId)
      throws Exception
  {
    return getHistory(dataViewEndpoint, userId, (Filter) null);
  }

  /**
   * Gets (searches) consent history for all consent history entries
   * with a particular user id.  Additionally the filter specified
   * will be applied to the search results.
   *
   * @param dataViewEndpoint the dataview to use for the search.
   * @param userId the userId to search consent history for.
   * @param filter the filter to apply for the search.
   * @return All consent history for the user.
   * @throws Exception thrown if an error occurs.
   */
  public ListResponse<ConsentHistory>
  getHistory(final String dataViewEndpoint, final String userId,
      final Filter filter) throws Exception
  {
    ScimService scimService;
    Client client;
    URI uri =
        UriBuilder.fromPath(dataViewEndpoint).path(userId).
            path("consentHistory").build();

    client = getClient();
    scimService = new ScimService(client.target(baseUrl));

    SearchRequestBuilder searchRequestBuilder =
        scimService.searchRequest(uri.toString());
    if(filter != null)
    {
      searchRequestBuilder.filter(filter.toString());
    }

    return searchRequestBuilder.invoke(ConsentHistory.class);
  }

  /**
   * Gets the consent history for the specified user.  Only the consent
   * history record that matches the event id specified will be returned.
   *
   * @param dataViewEndpoint the dataview to use for the search.
   * @param userId the userId to search consent history for.
   * @param eventId the eventId of the consent history.
   * @return All consent history for the user.
   * @throws Exception thrown if an error occurs.
   */
  public ConsentHistory getHistory(final String dataViewEndpoint,
      final String userId, final String eventId) throws Exception
  {
    ScimService scimService;
    Client client;
    URI uri =
        UriBuilder.fromPath(dataViewEndpoint).
            path(userId).path("consentHistory").build();

    client = getClient();
    scimService = new ScimService(client.target(baseUrl));

    return scimService.retrieveRequest(uri.toString(), eventId).
        invoke(ConsentHistory.class);
  }

  private Client getClient() throws Exception
  {
    return JerseyClientBuilder.createClient(clientConfig);
  }

}
