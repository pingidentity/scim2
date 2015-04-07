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



import com.unboundid.scim2.client.security.HttpBasicAuthSecurityHandler;
import com.unboundid.scim2.client.security.OAuthSecurityHandler;
import com.unboundid.scim2.client.security.OAuthToken;
import com.unboundid.scim2.exceptions.ScimException;
import com.unboundid.scim2.exceptions.ScimErrorResource;
import com.unboundid.scim2.utils.Debug;
import com.unboundid.scim2.utils.StaticUtils;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.RedirectException;
import org.apache.wink.client.ClientAuthenticationException;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientConfigException;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.httpclient.ApacheHttpClientConfig;

import java.io.IOException;
import java.net.URI;

/**
 * SCIM 2 Client class.
 */
public class SCIM2Client
{
  private final RestClient restClient;
  private final URI baseURL;

  /**
   * Constructs a new SCIM 2 Client object.
   *
   * @param baseUrl URI containing the base url for this client.
   * @param clientConfig client configuration object.
   */
  public SCIM2Client(final URI baseUrl, final ClientConfig clientConfig)
  {
    this.baseURL = baseUrl;
    this.restClient = new RestClient(clientConfig);
  }

  /**
   * Constructs a new SCIM 2 Client object.
   *
   * @param baseUrl URI containing the base url for this client.
   */
  public SCIM2Client(final URI baseUrl)
  {
    this(baseUrl, createDefaultClientConfig());
  }

  /**
   * Constructs a new SCIM 2 Client object.
   *
   * @param baseUrl URI containing the base url for this client.
   * @param oAuthToken oAuth token.
   */
  public SCIM2Client(final URI baseUrl, final OAuthToken oAuthToken)
  {
    this(baseUrl, createDefaultClientConfig().handlers(new OAuthSecurityHandler
        (oAuthToken)));
  }

  /**
   * Constructs a new SCIM 2 Client object.
   *
   * @param baseUrl URI containing the base url for this client.
   * @param username username used to authenticate.
   * @param password password used to authenticate.
   */
  public SCIM2Client(final URI baseUrl, final String username,
                     final String password)
  {
    this(baseUrl, createDefaultClientConfig().handlers(
        new HttpBasicAuthSecurityHandler(username, password)));
  }

  /**
   * Create a new ClientConfig with the default settings.
   *
   * @return A new ClientConfig with the default settings.
   */
  private static ClientConfig createDefaultClientConfig() {
    ApacheHttpClientConfig config = new ApacheHttpClientConfig();
    config.setMaxPooledConnections(100);
    return config;
  }

  /**
   * Gets the base url.
   * @return a URI containing the base url.
   */
  public URI getBaseURL()
  {
    return baseURL;
  }

  /**
   * Tries to deduce the most appropriate HTTP response code from the given
   * exception. This method expects most exceptions to be one of 3 or 4
   * expected runtime exceptions that are common to Wink and the Apache Http
   * Client library.
   * <p>
   * Note this method can return -1 for the special case in which the
   * service provider could not be reached at all.
   * <p>>
   *
   * @param t the Exception instance to analyze
   * @return the most appropriate HTTP status code
   */
  public int getStatusCode(final Throwable t)
  {
    Throwable rootCause = t;
    if(rootCause instanceof ClientRuntimeException)
    {
      //Pull the underlying cause out of the ClientRuntimeException
      rootCause = StaticUtils.getRootCause(t);
    }

    if(rootCause instanceof HttpResponseException)
    {
      HttpResponseException hre = (HttpResponseException) rootCause;
      return hre.getStatusCode();
    }
    else if(rootCause instanceof HttpException)
    {
      if(rootCause instanceof RedirectException)
      {
        return 300;
      }
      else if(rootCause instanceof AuthenticationException)
      {
        return 401;
      }
      else if(rootCause instanceof MethodNotSupportedException)
      {
        return 501;
      }
      else if(rootCause instanceof UnsupportedHttpVersionException)
      {
        return 505;
      }
    }
    else if(rootCause instanceof IOException)
    {
      if(rootCause instanceof NoHttpResponseException)
      {
        return 503;
      }
      else if(rootCause instanceof ConnectionClosedException)
      {
        return 503;
      }
      else
      {
        return -1;
      }
    }

    if(t instanceof ClientWebException)
    {
      ClientWebException cwe = (ClientWebException) t;
      return cwe.getResponse().getStatusCode();
    }
    else if(t instanceof ClientAuthenticationException)
    {
      return 401;
    }
    else if(t instanceof ClientConfigException)
    {
      return 400;
    }
    else
    {
      return 500;
    }
  }

  /**
   * Extracts the exception message from the root cause of the exception if
   * possible.
   *
   * @param t the original Throwable that was caught. This may be null.
   * @return the exception message from the root cause of the exception, or
   *         null if the specified Throwable is null or the message cannot be
   *         determined.
   */
  public String getExceptionMessage(final Throwable t)
  {
    if(t == null)
    {
      return null;
    }

    Throwable rootCause = StaticUtils.getRootCause(t);
    return rootCause.getMessage();
  }

  /**
   * Returns a SCIM exception representing the error response.
   *
   * @param response  The client response.
   *
   * @return  The SCIM exception representing the error response.
   */
  public ScimException createErrorResponseException(
      final ClientResponse response)
  {
    ScimErrorResource scimErrorResource = null;

    try
    {
      scimErrorResource = response.getEntity(ScimErrorResource.class);
    }
    catch (Exception e)
    {
      // The response content could not be parsed as a SCIM error
      // response, which is the case if the response is a more general
      // HTTP error. It is better to just provide the HTTP response
      // details in this case.
      Debug.debugException(e);
    }

    if(scimErrorResource == null)
    {
      return ScimException.createException(
          response.getStatusCode(), response.getMessage());
    }

    return ScimException.createException(scimErrorResource, null);
  }

  /**
   * Gets the rest client used by this object.
   *
   * @return the rest client used by this object.
   */
  public RestClient getRestClient()
  {
    return restClient;
  }
}

