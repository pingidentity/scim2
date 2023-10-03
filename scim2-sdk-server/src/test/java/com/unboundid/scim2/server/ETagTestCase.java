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

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.exceptions.NotModifiedException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.AuthenticationScheme;
import com.unboundid.scim2.common.types.BulkConfig;
import com.unboundid.scim2.common.types.ChangePasswordConfig;
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.types.FilterConfig;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.PatchConfig;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;
import com.unboundid.scim2.common.types.SortConfig;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.server.providers.JsonProcessingExceptionMapper;
import com.unboundid.scim2.server.providers.RuntimeExceptionMapper;
import com.unboundid.scim2.server.providers.ScimExceptionMapper;
import com.unboundid.scim2.server.resources.
    AbstractServiceProviderConfigEndpoint;
import com.unboundid.scim2.server.resources.ResourceTypesEndpoint;
import com.unboundid.scim2.server.resources.SchemasEndpoint;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.Assert;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class ETagTestCase extends JerseyTestNg.ContainerPerClassTest
{
  private static final URI ETAG_URI = URI.create("etag/test");

  @Override
  protected Application configure()
  {
    ResourceConfig config = new ResourceConfig();
    // Exception Mappers
    config.register(ScimExceptionMapper.class);
    config.register(RuntimeExceptionMapper.class);
    config.register(JsonProcessingExceptionMapper.class);
    config.register(new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    // Standard endpoints
    config.register(ResourceTypesEndpoint.class);
    config.register(SchemasEndpoint.class);
    registerAdditionalConfigItems(config);

    config.register(ETagTestEndpoint.class);
    config.register(new TestSingletonResourceEndpoint());

    return config;
  }

  /**
   * Register additional config items.  This is here mainly to allow a test
   * to register a custom service provider config (or not register any service
   * provider config).
   * @param config the resource config object.
   */
  protected void registerAdditionalConfigItems(ResourceConfig config)
  {
    config.register(new AbstractServiceProviderConfigEndpoint()
    {
      @Override
      public ServiceProviderConfigResource getServiceProviderConfig()
          throws ScimException
      {
        return new ServiceProviderConfigResource("https://doc",
            new PatchConfig(true),
            new BulkConfig(true, 100, 1000),
            new FilterConfig(true, 200),
            new ChangePasswordConfig(true),
            new SortConfig(true),
            new ETagConfig(true),
            Collections.singletonList(
                new AuthenticationScheme(
                    "Basic", "HTTP BASIC", null, null, "httpbasic", true)));
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureClient(final ClientConfig config)
  {
    config.property(ClientProperties.REQUEST_ENTITY_PROCESSING,
        RequestEntityProcessing.BUFFERED);
    config.connectorProvider(new ApacheConnectorProvider());
  }

  /**
   * Tests that etags work properly for retrievals.
   * @throws ScimException in case of error.
   */
  @Test
  public void testIfNoneMatch_retrieve() throws ScimException
  {
    final ScimService scimService = new ScimService(target());
    GenericScimResource gsr = new GenericScimResource();
    Meta meta = new Meta();
    meta.setLocation(UriBuilder.fromUri(ETAG_URI).path("/123").build());
    meta.setVersion("123");
    gsr.setMeta(meta);

    // uri
    verifyEtagHeaders(
        scimService.retrieveRequest(ETAG_URI).
            invoke(GenericScimResource.class), false, false);
    verifyEtagHeaders(
        scimService.retrieveRequest(ETAG_URI).ifNoneMatch("123").
            invoke(GenericScimResource.class), false, true);

    // uri + id
    verifyEtagHeaders(
        scimService.retrieveRequest(ETAG_URI.toString(), "123").
            invoke(GenericScimResource.class), false, false);
    verifyEtagHeaders(
        scimService.retrieveRequest(ETAG_URI.toString(), "123").
            ifNoneMatch("123").invoke(GenericScimResource.class), false, true);

    // uri + resource
    verifyEtagHeaders(scimService.retrieveRequest(gsr).invoke(
        GenericScimResource.class), false, false);
    verifyEtagHeaders(scimService.retrieveRequest(gsr).ifNoneMatch().invoke(
        GenericScimResource.class), false, true);


    // uri + Class
    verifyEtagHeaders(
        scimService.retrieve(ETAG_URI, GenericScimResource.class),
        false, false);

    // uri + id + Class
    verifyEtagHeaders(
        scimService.retrieve(ETAG_URI.toString(), "123",
            GenericScimResource.class), false, false);

    // resource
    verifyEtagHeaders(scimService.retrieve(gsr), false, false);

  }

  /**
   * Tests that etags work properly for retrievals when a not modified
   * exception is thrown.
   * @throws ScimException in case of error.
   */
  @Test(expectedExceptions = NotModifiedException.class)
  public void testIfNoneMatch_retrieveException() throws ScimException
  {
    final ScimService scimService = new ScimService(target());
    GenericScimResource gsr = new GenericScimResource();
    Meta meta = new Meta();
    meta.setLocation(UriBuilder.fromUri(ETAG_URI).path("/123").build());
    meta.setVersion("123");
    gsr.setMeta(meta);

    scimService.retrieveRequest(
      UriBuilder.fromUri(ETAG_URI).path("/exception/notModified").build()).
        ifNoneMatch("123").invoke(GenericScimResource.class);
  }


  /**
   * Tests that etags work properly for modify.
   * @throws ScimException in case of error.
   */
  @Test
  public void testIfMatch_modify() throws ScimException
  {
    final ScimService scimService = new ScimService(target());
    GenericScimResource gsr = new GenericScimResource();
    Meta meta = new Meta();
    meta.setLocation(UriBuilder.fromUri(ETAG_URI).path("/123").build());
    meta.setVersion("123");
    gsr.setMeta(meta);

    // uri
    verifyEtagHeaders(
        scimService.modifyRequest(ETAG_URI).
            invoke(GenericScimResource.class), false, false);
    verifyEtagHeaders(
        scimService.modifyRequest(ETAG_URI).ifMatch("123").
            invoke(GenericScimResource.class), true, false);

    // uri + id
    verifyEtagHeaders(
        scimService.modifyRequest(ETAG_URI.toString(), "123").
            invoke(GenericScimResource.class), false, false);
    verifyEtagHeaders(
        scimService.modifyRequest(ETAG_URI.toString(), "123").
            ifMatch("123").invoke(GenericScimResource.class), true, false);

    // uri + resource
    verifyEtagHeaders(scimService.modifyRequest(gsr).invoke(
        GenericScimResource.class), false, false);
    verifyEtagHeaders(scimService.modifyRequest(gsr).ifMatch().invoke(
        GenericScimResource.class), true, false);

    // endpoint + id + patchrequest
    PatchRequest patchRequest =
        new PatchRequest(Collections.<PatchOperation>emptyList());
    verifyEtagHeaders(
        scimService.modify(ETAG_URI.toString(), "123", patchRequest,
            GenericScimResource.class), false, false);

    // uri + id + Class
    verifyEtagHeaders(
        scimService.modify(gsr, patchRequest), false, false);
  }

  /**
   * Tests that etags work properly for replace.
   * @throws ScimException in case of error.
   */
  @Test
  public void testIfMatch_replace() throws ScimException
  {
    final ScimService scimService = new ScimService(target());
    GenericScimResource gsr = new GenericScimResource();
    Meta meta = new Meta();
    meta.setLocation(UriBuilder.fromUri(ETAG_URI).path("/123").build());
    meta.setVersion("123");
    gsr.setMeta(meta);

    // uri + resource
    verifyEtagHeaders(
        scimService.replaceRequest(ETAG_URI, gsr).
            invoke(GenericScimResource.class), false, false);
    verifyEtagHeaders(
        scimService.replaceRequest(ETAG_URI, gsr).ifMatch().
            invoke(GenericScimResource.class), true, false);

    // resource
    verifyEtagHeaders(
        scimService.replaceRequest(gsr).
            invoke(GenericScimResource.class), false, false);
    verifyEtagHeaders(
        scimService.replaceRequest(gsr).
            ifMatch().invoke(GenericScimResource.class), true, false);


    // resource
    verifyEtagHeaders(
        scimService.replace(gsr), false, false);
  }

  /**
   * Tests that etags work properly for delete.
   * @throws ScimException in case of error.
   */
  @Test
  public void testIfMatch_delete() throws ScimException
  {
    final ScimService scimService = new ScimService(target());
    GenericScimResource gsr = new GenericScimResource();
    Meta meta = new Meta();
    meta.setLocation(UriBuilder.fromUri(ETAG_URI).path("/123").build());
    meta.setVersion("123");
    gsr.setMeta(meta);

    // uri
    scimService.deleteRequest(ETAG_URI).invoke();
    verifyEtagHeaders(ETagTestEndpoint.lastResult, false, false);
    scimService.deleteRequest(ETAG_URI).ifMatch("123").invoke();
    verifyEtagHeaders(ETagTestEndpoint.lastResult, true, false);

    // uri + id
    scimService.deleteRequest(ETAG_URI.toString(), "123").invoke();
    verifyEtagHeaders(ETagTestEndpoint.lastResult, false, false);
    scimService.deleteRequest(ETAG_URI.toString(), "123").
        ifMatch("123").invoke();
    verifyEtagHeaders(ETagTestEndpoint.lastResult, true, false);

    // resource
    scimService.deleteRequest(gsr).invoke();
    verifyEtagHeaders(ETagTestEndpoint.lastResult, false, false);
    scimService.deleteRequest(gsr).ifMatch("123").invoke();
    verifyEtagHeaders(ETagTestEndpoint.lastResult, true, false);

    // uri
    scimService.delete(ETAG_URI);
    verifyEtagHeaders(ETagTestEndpoint.lastResult, false, false);

    // uri + id
    scimService.delete(ETAG_URI.toString(), "123");
    verifyEtagHeaders(ETagTestEndpoint.lastResult, false, false);

    // resource
    scimService.delete(gsr);
    verifyEtagHeaders(ETagTestEndpoint.lastResult, false, false);
  }

  private void verifyEtagHeaders(GenericScimResource resource,
      boolean shouldContainIfMatch, boolean shouldContainIfNoneMatch)
      throws ScimException
  {
    if(shouldContainIfMatch)
    {
      List<String> ifMatchValues =
          resource.getStringValueList(HttpHeaders.IF_MATCH);
      Assert.assertNotNull(ifMatchValues);
      Assert.assertFalse(ifMatchValues.isEmpty());
    }
    else
    {
      Assert.assertTrue(resource.getObjectNode().
          path(HttpHeaders.IF_MATCH).isMissingNode());
    }

    if(shouldContainIfNoneMatch)
    {
      List<String> ifNoneMatchValues =
          resource.getStringValueList(HttpHeaders.IF_NONE_MATCH);
      Assert.assertNotNull(ifNoneMatchValues);
      Assert.assertFalse(ifNoneMatchValues.isEmpty());
    }
    else
    {
      Assert.assertTrue(resource.getObjectNode().
          path(HttpHeaders.IF_NONE_MATCH).isMissingNode());
    }

  }

}
