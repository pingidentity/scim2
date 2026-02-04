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

package com.unboundid.scim2.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.jakarta.rs.cfg.JakartaRSFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.unboundid.scim2.client.ScimInterface;
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.client.ScimServiceException;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.bulk.BulkOpType;
import com.unboundid.scim2.common.bulk.BulkOperation;
import com.unboundid.scim2.common.bulk.BulkOperationResult;
import com.unboundid.scim2.common.bulk.BulkResponse;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.MethodNotAllowedException;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.messages.SortOrder;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.PhoneNumber;
import com.unboundid.scim2.common.types.ResourceTypeResource;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.ApiConstants;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.server.providers.DefaultContentTypeFilter;
import com.unboundid.scim2.server.providers.DotSearchFilter;
import com.unboundid.scim2.server.providers.JsonProcessingExceptionMapper;
import com.unboundid.scim2.server.providers.RuntimeExceptionMapper;
import com.unboundid.scim2.server.providers.ScimExceptionMapper;
import com.unboundid.scim2.server.resources.ResourceTypesEndpoint;
import com.unboundid.scim2.server.resources.SchemasEndpoint;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.utils.ServerUtils;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Test for the various endpoints included in the server module.
 */
public class EndpointTestCase extends JerseyTestNg.ContainerPerClassTest
{
  private SchemaResource userSchema;
  private SchemaResource enterpriseSchema;
  private ResourceTypeResource resourceType;
  private ResourceTypeResource singletonResourceType;
  private ServiceProviderConfigResource serviceProviderConfig;
  private TestRequestFilter requestFilter;

  /**
   * {@inheritDoc}
   */
  @Override
  protected Application configure()
  {
    ResourceConfig config = new ResourceConfig();
    // Exception Mappers
    config.register(ScimExceptionMapper.class);
    config.register(RuntimeExceptionMapper.class);
    config.register(JsonProcessingExceptionMapper.class);

    var provider = new JacksonJsonProvider(JsonUtils.createObjectMapper());
    provider.configure(JakartaRSFeature.ALLOW_EMPTY_INPUT, false);
    config.register(provider);

    // Filters
    config.register(DotSearchFilter.class);
    config.register(TestAuthenticatedSubjectAliasFilter.class);
    config.register(DefaultContentTypeFilter.class);
    requestFilter = new TestRequestFilter();
    config.register(requestFilter);

    // Standard endpoints
    config.register(ResourceTypesEndpoint.class);
    config.register(CustomContentEndpoint.class);
    config.register(SchemasEndpoint.class);
    config.register(BulkEndpoint.class);
    config.register(TestServiceProviderConfigEndpoint.class);

    config.register(TestResourceEndpoint.class);
    config.register(new TestSingletonResourceEndpoint());

    return config;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureClient(final ClientConfig config)
  {
    config.connectorProvider(new Apache5ConnectorProvider());
  }

  @BeforeClass
  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    try
    {
      userSchema = SchemaUtils.getSchema(UserResource.class);
      setMeta(SchemasEndpoint.class, userSchema);

      enterpriseSchema = SchemaUtils.getSchema(EnterpriseUserExtension.class);
      setMeta(SchemasEndpoint.class, enterpriseSchema);

      resourceType = new ResourceTypeResource("User", "User Account",
          new URI("/Users"),
          new URI(userSchema.getId()));
      setMeta(ResourceTypesEndpoint.class, resourceType);

      singletonResourceType = new ResourceTypeResource(
          "Singleton User", "Singleton User", "Singleton User Account",
          new URI("/SingletonUsers"), new URI(userSchema.getId()),
          Collections.singletonList(new ResourceTypeResource.SchemaExtension(
              new URI(enterpriseSchema.getId()), true)));
      setMeta(ResourceTypesEndpoint.class, singletonResourceType);

      // TODO: Need a bulk one?

      serviceProviderConfig = TestServiceProviderConfigEndpoint.create();
      setMeta(TestServiceProviderConfigEndpoint.class, serviceProviderConfig);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Test the service provider config can be retrieved.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetServiceProviderConfig() throws ScimException
  {
    final ServiceProviderConfigResource returnedServiceProviderConfig =
        new ScimService(target()).getServiceProviderConfig();

    assertEquals(returnedServiceProviderConfig, serviceProviderConfig);
  }

  /**
   * Test that modifying the service provider config results in error.
   */
  @Test
  public void testPutServiceProviderConfig()
  {
    ScimService service = new ScimService(target());
    assertThatThrownBy(() ->
        service.modifyRequest(service.getServiceProviderConfig()).invoke())
        .isInstanceOf(MethodNotAllowedException.class)
        .satisfies(e -> {
          ErrorResponse r = ((MethodNotAllowedException) e).getScimError();
          assertThat(r.getStatus()).isEqualTo(405);
          assertThat(r.getDetail()).contains("Method Not Allowed");
        });
  }

  /**
   * Test that retrieving an invalid endpoint results in 404.
   */
  @Test
  public void testInvalidEndpoint()
  {
    try
    {
      new ScimService(target()).retrieve("badPath", "id", UserResource.class);
      fail();
    }
    catch (ScimException e)
    {
      assertTrue(e instanceof ResourceNotFoundException);
    }

    // Now with application/json
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path("badPath").path("id").request().accept(
        MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).get();
    assertEquals(response.getStatus(), 404);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Test all schemas can be retrieved.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetSchemas() throws ScimException
  {
    final ListResponse<SchemaResource> returnedSchemas =
        new ScimService(target()).getSchemas();

    assertThat(returnedSchemas.getTotalResults()).isEqualTo(3);
    assertThat(returnedSchemas)
        .contains(userSchema)
        .contains(enterpriseSchema);

    // Now with application/json
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path(ApiConstants.SCHEMAS_ENDPOINT).request().
        accept(MediaType.APPLICATION_JSON_TYPE,
            ServerUtils.MEDIA_TYPE_SCIM_TYPE).get();
    assertEquals(response.getStatus(), 200);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Test an individual schema can be retrieved.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetSchema() throws ScimException
  {
    SchemaResource returnedSchema =
        new ScimService(target()).getSchema(userSchema.getId());

    assertEquals(returnedSchema, userSchema);

    returnedSchema = new ScimService(target()).getSchema(
        enterpriseSchema.getId());

    assertEquals(returnedSchema, enterpriseSchema);

    // Now with application/json
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path(ApiConstants.SCHEMAS_ENDPOINT).
        path(userSchema.getId()).request().
        accept(MediaType.APPLICATION_JSON_TYPE,
            ServerUtils.MEDIA_TYPE_SCIM_TYPE).get();
    assertEquals(response.getStatus(), 200);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Test all resource types can be retrieved.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetResourceTypes() throws ScimException
  {
    final ListResponse<ResourceTypeResource> returnedResourceTypes =
        new ScimService(target()).getResourceTypes();

    assertThat(returnedResourceTypes.getTotalResults()).isEqualTo(4);
    assertThat(returnedResourceTypes)
        .contains(resourceType)
        .contains(singletonResourceType);

    // Now with application/json
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path(ApiConstants.RESOURCE_TYPES_ENDPOINT).
        request().accept(MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).get();
    assertEquals(response.getStatus(), 200);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Test an individual resource type can be retrieved.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetResourceType() throws ScimException
  {
    final ResourceTypeResource returnedResourceTypeById =
        new ScimService(target()).getResourceType(resourceType.getId());

    assertEquals(returnedResourceTypeById, resourceType);

    final ResourceTypeResource returnedResourceTypeByName =
        new ScimService(target()).getResourceType(
            singletonResourceType.getId());

    assertEquals(returnedResourceTypeByName, singletonResourceType);

    final SchemaResource returnedSchema =
        new ScimService(target()).getSchema(
            returnedResourceTypeById.getSchema().toString());

    assertEquals(returnedSchema, userSchema);

    // Now with application/json
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path(ApiConstants.RESOURCE_TYPES_ENDPOINT).
        path(resourceType.getId()).request().accept(
        MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).get();
    assertEquals(response.getStatus(), 200);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Test a call to a {@code /Users} endpoint with query parameters. The
   * response is defined in {@link TestResourceEndpoint#searchFourResults}.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetUsers() throws ScimException
  {
    final ScimService service = new ScimService(target());

    // First send a request to /Users with cursor-based pagination, requesting
    // two resources and a subset of attributes.
    final ListResponse<UserResource> returnedUsers =
        service.searchRequest("/Users/WithFourResults")
            .filter(Filter.eq("meta.resourceType", "User"))
            .page(1, 2)
            .sort("id", SortOrder.ASCENDING)
            .attributes("id", "name", "Meta")
            .invoke(UserResource.class);

    // There are a total of four users, but two should have been returned.
    assertThat(returnedUsers.getTotalResults()).isEqualTo(4);
    assertThat(returnedUsers.getItemsPerPage()).isEqualTo(2);

    // The response should indicate this is the first page.
    assertThat(returnedUsers.getStartIndex()).isEqualTo(1);

    // The users should have been sorted by ID.
    assertThat(returnedUsers.getResources().get(0).getId())
        .isEqualTo("286080c5");
    assertThat(returnedUsers.getResources().get(1).getId())
        .isEqualTo("69d17c9d");

    // The userName attribute was not requested, so this should be null in the
    // result even though it was present on the user.
    assertThat(returnedUsers).allMatch(user -> user.getUserName() == null);

    // Fetch a subset of responses and request a cursor. The SimpleSearchResults
    // implementation should return a string "2" cursor. This time, the
    // usernames should be provided since most attributes will be returned.
    ListResponse<UserResource> returnedUsersCursor =
        service.searchRequest("/Users/WithFourResults")
            .firstPageCursorWithCount(2)
            .invoke(UserResource.class);
    assertThat(returnedUsersCursor.getTotalResults()).isEqualTo(4);
    assertThat(returnedUsersCursor.getItemsPerPage()).isEqualTo(2);
    assertThat(returnedUsersCursor.getNextCursor()).isEqualTo("2");
    assertThat(returnedUsersCursor.getStartIndex()).isNull();
    assertThat(returnedUsersCursor.getResources()).hasSize(2);
    assertThat(returnedUsersCursor).allMatch(u -> u.getUserName() != null);

    // Ensure it is possible to use the returned cursor to fetch the next page.
    returnedUsersCursor = service.searchRequest("/Users/WithFourResults")
        .pageWithCursor("2", 2)
        .invoke(UserResource.class);
    assertThat(returnedUsersCursor.getTotalResults()).isEqualTo(4);
    assertThat(returnedUsersCursor.getItemsPerPage()).isEqualTo(2);
    assertThat(returnedUsersCursor.getNextCursor()).isEqualTo("3");
    assertThat(returnedUsersCursor.getStartIndex()).isNull();
    assertThat(returnedUsersCursor.getResources()).hasSize(2);

    // Request cursor pagination, but fetch all resources. A value should not be
    // returned for "nextCursor" since there are no more results to display.
    ListResponse<UserResource> allUsersWithCursor =
        service.searchRequest("/Users/WithFourResults")
            .firstPageCursorWithCount(100)
            .invoke(UserResource.class);
    assertThat(allUsersWithCursor.getTotalResults()).isEqualTo(4);
    assertThat(allUsersWithCursor.getItemsPerPage()).isEqualTo(4);
    assertThat(allUsersWithCursor.getNextCursor()).isNull();
    assertThat(allUsersWithCursor.getStartIndex()).isNull();
    assertThat(allUsersWithCursor.getResources()).hasSize(4);

    // Fetch all remaining resources starting at the second page. Again, a value
    // should not be returned for "nextCursor".
    ListResponse<UserResource> remainingUsersWithCursor =
        service.searchRequest("/Users/WithFourResults")
            .pageWithCursor("2", 100)
            .invoke(UserResource.class);
    assertThat(remainingUsersWithCursor.getTotalResults()).isEqualTo(4);
    assertThat(remainingUsersWithCursor.getItemsPerPage()).isEqualTo(3);
    assertThat(remainingUsersWithCursor.getNextCursor()).isNull();
    assertThat(remainingUsersWithCursor.getStartIndex()).isNull();
    assertThat(remainingUsersWithCursor.getResources()).hasSize(3);

    // Search for results with a filter that matches no resources. Request a
    // cursor, though a "nextCursor" value should not be returned since there
    // are no more results.
    ListResponse<UserResource> emptyLResults =
        service.searchRequest("/Users/WithFourResults")
            .filter(Filter.pr("x509Certificates"))
            .firstPageCursorWithCount(1)
            .invoke(UserResource.class);
    assertThat(emptyLResults.getTotalResults()).isEqualTo(0);
    assertThat(emptyLResults.getItemsPerPage()).isEqualTo(0);
    assertThat(emptyLResults.getNextCursor()).isNull();
    assertThat(emptyLResults.getStartIndex()).isNull();
    assertThat(emptyLResults.getResources()).hasSize(0);
  }

  /**
   * Test a resource endpoint implementation registered as a class. The response
   * is defined in {@link TestResourceEndpoint#searchOneResult}.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetUsersUsingPost() throws ScimException
  {
    final ListResponse<UserResource> returnedUsers =
        new ScimService(target()).searchRequest("Users").
            filter("meta.resourceType eq \"User\"").
            page(1, 10).
            sort("id", SortOrder.DESCENDING).
            excludedAttributes("addresses", "phoneNumbers").
            invokePost(UserResource.class);

    assertEquals(returnedUsers.getTotalResults(), 1);
    assertEquals(returnedUsers.getStartIndex(), Integer.valueOf(1));
    assertEquals(returnedUsers.getItemsPerPage(), Integer.valueOf(1));

    // Now with application/json
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Set<String> excludedAttributes = new HashSet<>();
    excludedAttributes.add("addresses");
    excludedAttributes.add("phoneNumbers");
    SearchRequest searchRequest = new SearchRequest(
        null,
        excludedAttributes,
        "meta.resourceType eq \"User\"",
        "id", SortOrder.DESCENDING, 1, 10);

    Response response = target.path("Users").path(".search").
        request().accept(
        MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).post(Entity.json(searchRequest));
    assertEquals(response.getStatus(), 200);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
    response.close();

    // Now with application/json; charset=UTF-8
    response = target.path("Users").path(".search").
        request().accept(
        MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).post(
        Entity.entity(searchRequest, "application/json; charset=UTF-8"));
    assertEquals(response.getStatus(), 200);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
    response.close();

    // Now with invalid MIME type
    response = target.path("Users").path(".search").
        request().accept(
        MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).post(
        Entity.text("bad"));
    assertEquals(response.getStatus(), 415);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
    response.close();

    // Now with invalid empty body
    response = target.path("Users").path(".search").
        request().accept(
        MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).post(
        Entity.json(null));
    assertEquals(response.getStatus(), 400);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
    response.close();
  }

  /**
   * Test the authentication subject alias filter.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetMe() throws ScimException
  {
    ScimService scimService = new ScimService(target());
    UserResource user =
        scimService.retrieve(ScimService.ME_URI, UserResource.class);
    assertEquals(user.getId(), "123");
    assertEquals(user.getMeta().getResourceType(), "User");

    UriBuilder subResourceUri =
        UriBuilder.fromUri(ScimService.ME_URI).path("something");

    assertThatThrownBy(() ->
      scimService.retrieve(subResourceUri.build(), UserResource.class))
        .isInstanceOf(ScimException.class);
  }

  /**
   * Tests the authentication subject alias filter with included and excluded
   * attributes.  These take the form of query parameters and were being
   * dropped.  This test will ensure that the query parameters remain intact.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetMe_includedAndExcludedAttributes() throws ScimException
  {
    ScimService scimService = new ScimService(target());
    UserResource user =
        scimService.retrieve(ScimService.ME_URI, UserResource.class);
    assertEquals(user.getId(), "123");
    assertEquals(user.getMeta().getResourceType(), "User");
    assertEquals(user.getDisplayName(), "UserDisplayName");
    assertEquals(user.getNickName(), "UserNickName");

    user = scimService.retrieveRequest(ScimService.ME_URI).
        excludedAttributes("nickName").invoke(UserResource.class);
    assertEquals(user.getId(), "123");
    assertEquals(user.getMeta().getResourceType(), "User");
    assertEquals(user.getDisplayName(), "UserDisplayName");
    assertNull(user.getNickName());

    user = scimService.retrieveRequest(ScimService.ME_URI).
        attributes("nickName", "Meta").invoke(UserResource.class);
    assertEquals(user.getId(), "123");
    assertEquals(user.getMeta().getResourceType(), "User");
    assertNull(user.getDisplayName());
    assertEquals(user.getNickName(), "UserNickName");
  }

  /**
   * Test create operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testCreate() throws ScimException
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("createUser");
    EnterpriseUserExtension extension =
        new EnterpriseUserExtension().setEmployeeNumber("1234");
    newUser.replaceExtensionValue(
        Path.root(EnterpriseUserExtension.class),
        JsonUtils.valueToNode(extension));

    UserResource createdUser =
        scimService.create("SingletonUsers", newUser);
    assertNotNull(createdUser.getId());
    assertEquals(createdUser.getUserName(), newUser.getUserName());
    try
    {
      assertEquals(JsonUtils.nodeToValue(createdUser.getExtensionValues(
          Path.root(EnterpriseUserExtension.class)).get(0),
          EnterpriseUserExtension.class), extension);

      assertEquals(createdUser.getExtensionValues(
          Path.root(EnterpriseUserExtension.class).attribute("employeeNumber"))
          .get(0).textValue(), "1234");
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e);
    }

    UserResource retrievedUser = scimService.retrieve(createdUser);

    assertEquals(createdUser, retrievedUser);
  }

  /**
   * Test delete operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testDelete() throws ScimException
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("deleteUser");
    UserResource createdUser =
        scimService.create("SingletonUsers", newUser);
    assertNotNull(createdUser.getId());
    assertEquals(createdUser.getUserName(), newUser.getUserName());

    scimService.delete(createdUser);

    assertThatThrownBy(() -> scimService.retrieve(createdUser))
        .isInstanceOf(ResourceNotFoundException.class);

    // Now with no accept header
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));
    try (Response response = target.path("SingletonUsers").path("deleteUser")
        .request().delete())
    {
      assertEquals(response.getStatus(), 404);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
    }

    // With invalid accept type (DS-14520)
    target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));
    try (Response response = target.path("SingletonUsers").path("deleteUser").
        request().accept(MediaType.APPLICATION_ATOM_XML_TYPE).delete())
    {
      assertEquals(response.getStatus(), 404);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
    }
  }

  /**
   * Test put operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testPut() throws ScimException
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("putUser");
    UserResource createdUser =
        scimService.create("SingletonUsers", newUser);

    assertNull(createdUser.getDisplayName());

    createdUser.setDisplayName("Bob");

    UserResource updatedUser = scimService.replace(createdUser);
    assertEquals(updatedUser.getDisplayName(), "Bob");
  }

  /**
   * Test patch operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testPatch() throws ScimException
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("patchUser");
    newUser.setDisplayName("removeMe");
    newUser.setName(new Name().setGivenName("Bob").setFamilyName("Tester"));
    newUser.setEmails(Collections.singletonList(
        new Email().setValue("bob@tester.com").setType("work")));
    UserResource createdUser =
        scimService.create("SingletonUsers", newUser);

    PhoneNumber phone1 = new PhoneNumber().
        setValue("1234567890").setType("home");
    PhoneNumber phone2 = new PhoneNumber().
        setValue("123123123").setType("work").setPrimary(true);

    UserResource updatedUser = scimService.modifyRequest(createdUser).
        removeValues("displayName").
        replaceValue("name.middleName", "the").
        replaceValue("emails[type eq \"work\"].value", "bobNew@tester.com").
        addValues("phoneNumbers", phone1, phone2).invoke();

    assertNull(updatedUser.getDisplayName());
    assertEquals(updatedUser.getName().getMiddleName(), "the");
    assertEquals(updatedUser.getEmails().get(0).getValue(),
        "bobNew@tester.com");
    assertEquals(updatedUser.getPhoneNumbers().size(), 2);
    assertTrue(contains(updatedUser.getPhoneNumbers(), phone1));
    assertTrue(contains(updatedUser.getPhoneNumbers(), phone2));
  }

  /**
   * Test patch operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testPatchThroughScimInterface_resource() throws ScimException
  {
    ScimInterface scimInterface = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("patchUser");
    newUser.setDisplayName("removeMe");
    newUser.setName(new Name().setGivenName("Bob").setFamilyName("Tester"));
    newUser.setEmails(Collections.singletonList(
        new Email().setValue("bob@tester.com").setType("work")));
    UserResource createdUser =
        scimInterface.create("SingletonUsers", newUser);

    PhoneNumber phone1 = new PhoneNumber().
        setValue("1234567890").setType("home");
    PhoneNumber phone2 = new PhoneNumber().
        setValue("123123123").setType("work").setPrimary(true);

    List<PatchOperation> patchOperations = new ArrayList<>();
    patchOperations.add(PatchOperation.remove("displayName"));
    patchOperations.add(PatchOperation.replace("name.middleName", "the"));
    patchOperations.add(PatchOperation.replace(
        "emails[type eq \"work\"].value", "bobNew@tester.com"));
    patchOperations.add(PatchOperation.add("phoneNumbers",
        JsonUtils.valueToNode(List.of(phone1, phone2))));

    UserResource updatedUser = scimInterface.modify(
        createdUser, new PatchRequest(patchOperations));

    assertNull(updatedUser.getDisplayName());
    assertEquals(updatedUser.getName().getMiddleName(), "the");
    assertEquals(updatedUser.getEmails().get(0).getValue(),
        "bobNew@tester.com");
    assertEquals(updatedUser.getPhoneNumbers().size(), 2);
    assertTrue(contains(updatedUser.getPhoneNumbers(), phone1));
    assertTrue(contains(updatedUser.getPhoneNumbers(), phone2));
  }

  /**
   * Test patch operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testPatchThroughScimInterface_typeId() throws ScimException
  {
    ScimInterface scimInterface = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("patchUser");
    newUser.setDisplayName("removeMe");
    newUser.setName(new Name().setGivenName("Bob").setFamilyName("Tester"));
    newUser.setEmails(Collections.singletonList(
        new Email().setValue("bob@tester.com").setType("work")));
    UserResource createdUser =
        scimInterface.create("SingletonUsers", newUser);

    PhoneNumber phone1 = new PhoneNumber().
        setValue("1234567890").setType("home");
    PhoneNumber phone2 = new PhoneNumber().
        setValue("123123123").setType("work").setPrimary(true);

    List<PatchOperation> patchOperations = new ArrayList<>();
    patchOperations.add(PatchOperation.remove("displayName"));
    patchOperations.add(PatchOperation.replace("name.middleName", "the"));
    patchOperations.add(PatchOperation.replace(
        "emails[type eq \"work\"].value", "bobNew@tester.com"));
    patchOperations.add(PatchOperation.add("phoneNumbers",
        JsonUtils.valueToNode(List.of(phone1, phone2))));

    UserResource updatedUser = scimInterface.modify(
        "SingletonUsers", createdUser.getId(),
        new PatchRequest(patchOperations), UserResource.class);

    assertNull(updatedUser.getDisplayName());
    assertEquals(updatedUser.getName().getMiddleName(), "the");
    assertEquals(updatedUser.getEmails().get(0).getValue(),
        "bobNew@tester.com");
    assertEquals(updatedUser.getPhoneNumbers().size(), 2);
    assertTrue(contains(updatedUser.getPhoneNumbers(), phone1));
    assertTrue(contains(updatedUser.getPhoneNumbers(), phone2));
  }

  /**
   * Test error response when invalid JSON is submitted.
   */
  @Test
  public void testInvalidJson()
  {
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    var request = target.path("SingletonUsers").request().
        accept(MEDIA_TYPE_SCIM);

    try (Response response = request.post(
        Entity.entity("{badJson}", MEDIA_TYPE_SCIM)))
    {
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), MediaType.valueOf(MEDIA_TYPE_SCIM));
      ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
      assertEquals(errorResponse.getStatus(), Integer.valueOf(400));
      assertEquals(errorResponse.getScimType(), "invalidSyntax");
      assertNotNull(errorResponse.getDetail());
    }

    // Now with application/json
    request = target.path("SingletonUsers").request().
        accept(MediaType.APPLICATION_JSON_TYPE,
            ServerUtils.MEDIA_TYPE_SCIM_TYPE);

    try (Response response = request.post(
        Entity.entity("{badJson}", MediaType.APPLICATION_JSON_TYPE)))
    {
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
    }
  }

  /**
   * Test error response when an invalid standard SCIM message is submitted and
   * a Jackson binding error occurs.
   */
  @Test
  public void testInvalidMessage()
  {
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    var request = target.path("SingletonUsers").path(".search").
        request().accept(MEDIA_TYPE_SCIM);

    try (Response response = request.post(Entity.entity(
        "{\"undefinedField\": \"value\"}", MEDIA_TYPE_SCIM)))
    {
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), MediaType.valueOf(MEDIA_TYPE_SCIM));
      ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
      assertEquals(errorResponse.getStatus(), Integer.valueOf(400));
      assertEquals(errorResponse.getScimType(), "invalidSyntax");
      assertNotNull(errorResponse.getDetail());
    }

    // Now with application/json
    request = target.path("SingletonUsers").path(".search").
        request().accept(MediaType.APPLICATION_JSON_TYPE,
            ServerUtils.MEDIA_TYPE_SCIM_TYPE);

    try (Response response = request.post(Entity.entity(
        "{\"undefinedField\": \"value\"}", MediaType.APPLICATION_JSON_TYPE)))
    {
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
    }
  }

  /**
   * Test that empty entity in POST, PUT, and PATCH requests are handled
   * correctly.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testEmptyEntity() throws ScimException
  {
    Client client = client();
    // Allow null request entities
    client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

    try
    {
      WebTarget target = client.target(getBaseUri()).register(
          new JacksonJsonProvider(JsonUtils.createObjectMapper()));

      // No content-type header and no entity
      Invocation.Builder b = target.path("SingletonUsers").
          request("application/scim+json");
      Response response = b.build("POST").invoke();
      assertEquals(response.getStatus(), 400);
      ErrorResponse error = response.readEntity(ErrorResponse.class);
      assertTrue(error.getDetail().contains("No content provided"));
      response.close();

      b = target.path("SingletonUsers").path("123").
          request("application/scim+json");
      response = b.build("PUT").invoke();
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
      error = response.readEntity(ErrorResponse.class);
      assertTrue(error.getDetail().contains("No content provided"));
      response.close();

      b = target.path("SingletonUsers").path("123").
          request("application/scim+json");
      response = b.build("PATCH").invoke();
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
      error = response.readEntity(ErrorResponse.class);
      assertTrue(error.getDetail().contains("No content provided"));
      response.close();

      // Content-type header set but no entity
      b = target.path("SingletonUsers").
          request("application/scim+json").
          header(HttpHeaders.CONTENT_TYPE, "application/scim+json");
      response = b.build("POST").invoke();
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
      error = response.readEntity(ErrorResponse.class);
      assertTrue(error.getDetail().contains("No content provided"));
      response.close();

      b = target.path("SingletonUsers").path("123").
          request("application/scim+json").
                    header(HttpHeaders.CONTENT_TYPE, "application/scim+json");
      response = b.build("PUT").invoke();
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
      error = response.readEntity(ErrorResponse.class);
      assertTrue(error.getDetail().contains("No content provided"));
      response.close();

      b = target.path("SingletonUsers").path("123").
          request("application/scim+json").
          header(HttpHeaders.CONTENT_TYPE, "application/scim+json");
      response = b.build("PATCH").invoke();
      assertEquals(response.getStatus(), 400);
      assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
      error = response.readEntity(ErrorResponse.class);
      assertTrue(error.getDetail().contains("No content provided"));
      response.close();
    }
    finally
    {
      client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, false);
    }
  }

  /**
   * Test ability of client to submit requests with arbitrary query parameters.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testQueryParams() throws ScimException
  {
    final ScimService service = new ScimService(target());
    final String expectedKey = "expectedKey";
    final String expectedValue = "expectedValue";

    requestFilter.addExpectedQueryParam(expectedKey, expectedValue);

    try
    {
      assertThatThrownBy(() ->
          service.retrieveRequest(ScimService.ME_URI)
              .queryParam(expectedKey, "unexpectedValue")
              .invoke(UserResource.class))
          .isInstanceOf(BadRequestException.class);

      service.retrieveRequest(ScimService.ME_URI).
          queryParam(expectedKey, expectedValue).
          invoke(UserResource.class);

      UserResource newUser = new UserResource().setUserName("queryParamUser");
      UserResource createdUser =
          service.createRequest("SingletonUsers", newUser).
              queryParam(expectedKey, expectedValue).
              invoke();

      createdUser.setDisplayName("Bob");
      UserResource updatedUser =
          service.replaceRequest(createdUser).
              queryParam(expectedKey, expectedValue).
              invoke();

      UserResource patchedUser =
          service.modifyRequest("SingletonUsers", updatedUser.getId()).
              addOperation(PatchOperation.replace("displayName",
                                                  TextNode.valueOf("Joe"))).
              queryParam(expectedKey, expectedValue).
              invoke(UserResource.class);

      service.deleteRequest("SingletonUsers", patchedUser.getId()).
          queryParam(expectedKey, expectedValue).
          invoke();

      // Confirm that query parameters set by other means are included.
      String filter = "meta.resourceType eq \"User\"";
      requestFilter.addExpectedQueryParam(ApiConstants.QUERY_PARAMETER_FILTER,
          filter);
      service.searchRequest("Users").
          filter(filter).
          queryParam(expectedKey, expectedValue).
          invoke(UserResource.class);

      // Test a request including a query parameter with multiple expected
      // values.
      requestFilter.reset();
      requestFilter.addExpectedQueryParam(expectedKey, "expectedValue1");
      requestFilter.addExpectedQueryParam(expectedKey, "expectedValue2");

      service.retrieveRequest(ScimService.ME_URI).
          queryParam(expectedKey, "expectedValue1").
          queryParam(expectedKey, "expectedValue2").
          invoke(UserResource.class);
    }
    finally
    {
      requestFilter.reset();
    }
  }

  /**
   * Test ability of client to submit requests with arbitrary headers.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testHeaders() throws ScimException
  {
    final ScimService service = new ScimService(target());
    final String expectedKey = "expectedKey";
    final String expectedValue = "expectedValue";

    requestFilter.addExpectedHeader(expectedKey, expectedValue);
    // Confirm that the default Accept header is preserved.
    requestFilter.addExpectedHeader(HttpHeaders.ACCEPT,
        ScimService.MEDIA_TYPE_SCIM_TYPE.
            toString());
    requestFilter.addExpectedHeader(HttpHeaders.ACCEPT,
        MediaType.APPLICATION_JSON_TYPE.
            toString());

    try
    {
      assertThatThrownBy(() ->
          service.retrieveRequest(ScimService.ME_URI)
              .header(expectedKey, "unexpectedValue")
              .invoke(UserResource.class))
          .isInstanceOf(BadRequestException.class);

      service.retrieveRequest(ScimService.ME_URI).
          header(expectedKey, expectedValue).
          invoke(UserResource.class);

      UserResource newUser = new UserResource().setUserName("queryParamUser");
      UserResource createdUser =
          service.createRequest("SingletonUsers", newUser).
              header(expectedKey, expectedValue).
              invoke();

      createdUser.setDisplayName("Bob");
      UserResource updatedUser =
          service.replaceRequest(createdUser).
              header(expectedKey, expectedValue).
              invoke();

      UserResource patchedUser =
          service.modifyRequest("SingletonUsers", updatedUser.getId()).
              addOperation(PatchOperation.replace("displayName",
                                                  TextNode.valueOf("Joe"))).
              header(expectedKey, expectedValue).
              invoke(UserResource.class);

      service.deleteRequest("SingletonUsers", patchedUser.getId()).
          header(expectedKey, expectedValue).
          invoke();

      service.searchRequest("Users").
          filter(Filter.eq("meta.resourceType", "User")).
          header(expectedKey, expectedValue).
          invoke(UserResource.class);

      service.searchRequest("Users").
          filter(Filter.eq("meta.resourceType", "User")).
          header(expectedKey, expectedValue).
          invokePost(UserResource.class);

      // Test a request including a header with multiple expected values.
      requestFilter.reset();
      requestFilter.addExpectedHeader(expectedKey, "expectedValue1");
      requestFilter.addExpectedHeader(expectedKey, "expectedValue2");

      service.retrieveRequest(ScimService.ME_URI).
          header(expectedKey, "expectedValue1").
          header(expectedKey, "expectedValue2").
          invoke(UserResource.class);
    }
    finally
    {
      requestFilter.reset();
    }
  }


  /**
   * Test for the proper return behavior for Meta.  This test is in response
   * to DS-15034.  If we are not careful about the order of populating location
   * and resource type with respect to trimming the results, we can end up
   * returning unwanted (or incorrect) information.
   * @throws Exception indicates a test failure.
   */
  @Test
  public void testMetaIsReturnedByDefaultOnly() throws Exception
  {
    ScimService scimService = new ScimService(target());
    UserResource user = scimService.retrieveRequest(ScimService.ME_URI).
        attributes("nickName").invoke(UserResource.class);
    assertNull(user.getMeta());

    user = scimService.retrieveRequest(ScimService.ME_URI).
        excludedAttributes("meta").invoke(UserResource.class);
    assertNull(user.getMeta());

    user = scimService.retrieveRequest(ScimService.ME_URI).
        invoke(UserResource.class);
    assertNotNull(user.getMeta());
    assertNotNull(user.getMeta().getLocation());
    assertNotNull(user.getMeta().getResourceType());
  }

  /**
   * Ensure that for a failed request, if the returned entity could not be
   * deserialized as an {@code ErrorResponse}, a {@code ScimServiceException}
   * is thrown which contains an appropriate error message and a fallback
   * {@code ErrorResponse} describing the request failure. The detail message
   * in the {@code ErrorResponse} should correspond to the actual SCIM issue,
   * NOT the deserialization failure within the provider - see DS-44767.
   *
   * @throws Exception in case of error.
   */
  @Test
  public void testBadErrorResult() throws Exception
  {
    final ScimService service = new ScimService(target());

    try
    {
      service.create("Users/badException",
          new GenericScimResource());
      Assert.fail("Expecting a ScimServiceException");
    }
    catch (ScimServiceException ex)
    {
      ErrorResponse response = ex.getScimError();
      Assert.assertNotNull(response);
      Assert.assertEquals(response.getStatus(), Integer.valueOf(409));
      Assert.assertNotNull(ex.getCause());
      Assert.assertEquals(ex.getMessage(), response.getDetail());
      Assert.assertEquals(response.getDetail(), "Conflict");
    }

    try
    {
      service.searchRequest(
          "Users/responseWithStatusUnauthorizedAndTypeOctetStreamAndBadEntity")
          .accept(MediaType.APPLICATION_OCTET_STREAM)
          .invoke(GenericScimResource.class);
      Assert.fail("Expecting a ScimServiceException");
    }
    catch (ScimServiceException ex)
    {
      ErrorResponse response = ex.getScimError();
      Assert.assertNotNull(response);
      Assert.assertEquals(response.getStatus(), Integer.valueOf(401));
      Assert.assertNotNull(ex.getCause());
      Assert.assertEquals(ex.getMessage(), response.getDetail());
      Assert.assertEquals(response.getDetail(), "Unauthorized");
    }
  }


  /**
   * This test validates the SDK's parsing of the ListResponse defined in
   * {@link TestResourceEndpoint#testListResponseCaseSensitivity}.
   */
  @Test
  public void testListResponseParsingCaseSensitivity() throws Exception
  {
    final ScimService service = new ScimService(target());
    ListResponse<UserResource> response =
        service.searchRequest("Users/testListResponseCaseSensitivity")
            .invoke(UserResource.class);

    // Even though the attribute casing is varied, all named fields should
    // have been successfully parsed.
    assertThat(response.getSchemaUrns())
        .hasSize(1)
        .containsOnly("urn:ietf:params:scim:api:messages:2.0:ListResponse");
    assertThat(response.getTotalResults()).isEqualTo(2);
    assertThat(response.getItemsPerPage()).isEqualTo(1);
    assertThat(response.getResources())
        .hasSize(1)
        .containsOnly(new UserResource().setUserName("k.dot"));

    // startIndex was not included, so it should not have a value.
    assertThat(response.getStartIndex()).isNull();
  }


  /**
   * Validate an edge case where a list response with an unknown attribute is
   * included at the very end of a response. In previous releases, this caused
   * parsing issues. The list response returned by the service is defined in
   * {@link TestResourceEndpoint#testLastFieldUnknown}.
   */
  @Test(timeOut = 5_000L)
  public void testLastFieldUnknown() throws Exception
  {
    final ScimService service = new ScimService(target());
    ListResponse<UserResource> response =
        service.searchRequest("/Users/testLastFieldUnknown")
            .invoke(UserResource.class);

    assertThat(response.getSchemaUrns())
        .hasSize(1)
        .containsOnly("urn:ietf:params:scim:api:messages:2.0:ListResponse");
    assertThat(response.getTotalResults()).isEqualTo(1);
    assertThat(response.getItemsPerPage()).isEqualTo(1);
    assertThat(response.getResources())
        .hasSize(1)
        .containsOnly(new UserResource().setUserName("GNX"));
    assertThat(response.toString())
        .doesNotContain("unknownAttribute")
        .doesNotContain("unknownValue");
  }


  /**
   * Test a response that includes {@code previousCursor}. The list response
   * returned by the service is defined in
   * {@link TestResourceEndpoint#testCursorPagination}.
   */
  @Test
  public void testCursorPagination() throws Exception
  {
    final ScimService service = new ScimService(target());
    ListResponse<UserResource> response =
        service.searchRequest("/Users/testCursorPagination")
            .invoke(UserResource.class);

    assertThat(response.getSchemaUrns())
        .hasSize(1)
        .containsOnly("urn:ietf:params:scim:api:messages:2.0:ListResponse");
    assertThat(response.getTotalResults()).isEqualTo(20);
    assertThat(response.getItemsPerPage()).isEqualTo(1);
    assertThat(response.getStartIndex()).isNull();
    assertThat(response.getPreviousCursor()).isEqualTo("ze7L30kMiiLX6x");
    assertThat(response.getNextCursor()).isEqualTo("YkU3OF86Pz0rGv");
    assertThat(response.getResources())
        .hasSize(1)
        .containsOnly(new UserResource().setUserName("reincarnated"));
  }


  /**
   * Test custom content-type.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testCustomContentType() throws ScimException
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("putUser");
    newUser.setDisplayName("removeMe");
    UserResource createdUser = scimService.createRequest("CustomContent", newUser).
        contentType(MediaType.APPLICATION_JSON).invoke();
    Assert.assertNotNull(createdUser);

    UserResource replacedUser = scimService.replaceRequest(createdUser).
        contentType(MediaType.APPLICATION_JSON).invoke();
    Assert.assertNotNull(replacedUser);

    scimService.modifyRequest(replacedUser).removeValues("displayName").
        contentType(MediaType.APPLICATION_JSON).invoke();

    String returnString = new String(scimService.retrieveRequest(
        "CustomContent", "123").accept(MediaType.APPLICATION_OCTET_STREAM).
        invoke(byte[].class));
    Assert.assertNotNull(returnString);
  }


  /**
   * Test accept(null) throws proper exception.
   */
  @Test
  public void testAcceptExceptions()
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    assertThatThrownBy(() ->
        new String(scimService.retrieveRequest("CustomContent", "123")
            .accept((String[]) null)
            .invoke(byte[].class)))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() ->
        new String(scimService.retrieveRequest("CustomContent", "123")
            .accept()
            .invoke(byte[].class)))
        .isInstanceOf(IllegalArgumentException.class);
  }


  /**
   * This test performs basic validation for bulk requests and responses in a
   * Jakarta-RS environment. Handling for these {@code /Bulk} calls come from
   * {@link BulkEndpoint#processBulkRequest}.
   */
  @Test
  public void bulkTest() throws Exception
  {
    ScimService scimService = new ScimService(target());
    final BulkOperation post = BulkOperation.post("/Users",
        new UserResource().setUserName("frieren"));
    final BulkOperation put = BulkOperation.put("/Users/fa1afe1",
        new UserResource().setUserName("frieren"));
    final BulkOperation patch = BulkOperation.patch("/Users/fa1afe1",
        PatchOperation.remove("nickName"));
    final BulkOperation delete = BulkOperation.delete("/Users/fa1afe1");

    // Validate a single delete operation.
    BulkResponse response = scimService.bulkRequest()
        .append(delete)
        .invoke();
    assertThat(response.getOperations()).hasSize(1);
    BulkOperationResult deleteResult = response.getOperations().get(0);
    assertThat(deleteResult.getMethod()).isEqualTo(BulkOpType.DELETE);
    assertThat(deleteResult.getStatus()).isEqualTo("204");
    assertThat(deleteResult.getLocation()).endsWith("/Users/fa1afe1");

    // A single create operation.
    BulkResponse createResponse = scimService.bulkRequest()
        .append(post)
        .invoke();
    assertThat(createResponse.getOperations()).hasSize(1);
    BulkOperationResult createResult = response.getOperations().get(0);
    assertThat(createResult.getMethod()).isEqualTo(BulkOpType.DELETE);
    assertThat(createResult.getStatus()).isEqualTo("204");
    assertThat(createResult.getLocation()).endsWith("/Users/fa1afe1");

    // A single PUT operation.
    BulkResponse replaceResponse = scimService.bulkRequest()
        .append(put)
        .invoke();
    assertThat(replaceResponse.getOperations()).hasSize(1);
    BulkOperationResult replaceResult = response.getOperations().get(0);
    assertThat(replaceResult.getMethod()).isEqualTo(BulkOpType.DELETE);
    assertThat(replaceResult.getStatus()).isEqualTo("204");
    assertThat(replaceResult.getLocation()).endsWith("/Users/fa1afe1");

    // A single PATCH operation.
    BulkResponse modifyResponse = scimService.bulkRequest()
        .append(patch)
        .invoke();
    assertThat(modifyResponse.getOperations()).hasSize(1);
    BulkOperationResult modifyResult = response.getOperations().get(0);
    assertThat(modifyResult.getMethod()).isEqualTo(BulkOpType.DELETE);
    assertThat(modifyResult.getStatus()).isEqualTo("204");
    assertThat(modifyResult.getLocation()).endsWith("/Users/fa1afe1");

    // Issue requests with no operations.
    BulkResponse noOpResponse = scimService.bulkRequest().invoke();
    assertThat(noOpResponse.getOperations()).hasSize(0);
    noOpResponse = scimService.bulkRequest()
        .append((List<BulkOperation>) null)
        .append((BulkOperation[]) null)
        .append(null, null)
        .invoke();
    assertThat(noOpResponse.getOperations()).hasSize(0);

    // Issue a request with multiple operations.
    BulkOperation postWithBulkID = post.copy().setBulkId("bulkley");
    BulkResponse multipleOpResponse = scimService.bulkRequest()
        .append(postWithBulkID, put, patch, delete)
        .invoke();
    assertThat(multipleOpResponse.getOperations()).hasSize(4);

    // Ensure the order of the requests is preserved.
    List<BulkOperationResult> expectedList = List.of(
        new BulkOperationResult(BulkOpType.POST, "201", "location")
            .setBulkId("bulkley"),
        new BulkOperationResult(BulkOpType.PUT, "200", "location"),
        new BulkOperationResult(BulkOpType.PATCH, "200", "location"),
        new BulkOperationResult(BulkOpType.DELETE, "204", "location"));
    for (int i = 0; i < expectedList.size(); i++)
    {
      BulkOperationResult expected = expectedList.get(i);
      BulkOperationResult actual = multipleOpResponse.getOperations().get(i);

      assertThat(actual.getMethod()).isEqualTo(expected.getMethod());
      assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
      assertThat(actual.getBulkId()).isEqualTo(expected.getBulkId());
    }

    // Re-run the previous test case with an explicit endpoint provided to the
    // bulk request.
    var uri = UriBuilder.fromUri(getBaseUri()).path("/Bulk").build();
    BulkResponse multipleOpResponse2 = scimService.bulkRequest(uri)
        .append(postWithBulkID, put, patch, delete)
        .invoke();
    assertThat(multipleOpResponse2).isEqualTo(multipleOpResponse);

    // Simulate a bulk error.
    var errorUri = UriBuilder.fromUri(getBaseUri())
        .path("/Bulk").path("/BulkError").build();
    assertThatThrownBy(() -> scimService.bulkRequest(errorUri).invoke())
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Simulated error for too many bulk operations.");
  }


  private void setMeta(Class<?> resourceClass, ScimResource scimResource)
  {
    ResourceTypeResource resourceType =
        ResourceTypeDefinition.fromJaxRsResource(
            resourceClass).toScimResource();
    UriBuilder locationBuilder =
        UriBuilder.fromUri(getBaseUri()).path(
            resourceType.getEndpoint().getPath());
    if (scimResource.getId() != null)
    {
      locationBuilder.path(scimResource.getId());
    }

    Meta meta = scimResource.getMeta();
    if (meta == null)
    {
      meta = new Meta();
      scimResource.setMeta(meta);
    }
    meta.setLocation(locationBuilder.build());
    meta.setResourceType(resourceType.getName());
  }

  private <T> boolean contains(Iterable<T> list, T resource)
  {
    for (T r : list)
    {
      if (r.equals(resource))
      {
        return true;
      }
    }
    return false;
  }
}
