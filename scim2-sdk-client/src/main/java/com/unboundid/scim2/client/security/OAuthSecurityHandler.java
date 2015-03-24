/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.client.security;

import org.apache.wink.client.ClientRequest;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.handlers.ClientHandler;
import org.apache.wink.client.handlers.HandlerContext;

/**
 * This class provides OAuth Authentication handling.
 */
public class OAuthSecurityHandler implements ClientHandler
{
  /**
   * The stringified OAuth token authorization header.
   */
  private volatile String authorizationHeader;

  /**
   * Constructs a fully initialized OAuthSecurityHandler handler.
   * @param token Fully constructed OAuth Token
   */
  public OAuthSecurityHandler(final OAuthToken token)
  {
    this.authorizationHeader = token.getFormattedValue();
  }

  /**
   * Attempts to authenticate a Consumer via OAuth tokens.
   *
   * @param request  The Client Resource request.
   * @param context The provided handler chain.
   * @return Client Response that may indicate success or failure.
   * @throws Exception Thrown if error handling authentication.
   */
  public ClientResponse handle(final ClientRequest request,
                               final HandlerContext context) throws Exception
  {
    request.getHeaders().putSingle("Authorization", this.authorizationHeader);
    return context.doChain(request);
  }
}
