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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.jakarta.rs.cfg.JakartaRSFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.google.common.collect.Lists;
import com.unboundid.scim2.client.ScimInterface;
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.client.ScimServiceException;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.MethodNotAllowedException;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
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
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.ws.rs.WebApplicationException;
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

    JacksonJsonProvider provider =
        new JacksonJsonProvider(JsonUtils.createObjectMapper());
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
    config.connectorProvider(new ApacheConnectorProvider());
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
    try
    {
      ScimService scimService = new ScimService(target());
      scimService.modifyRequest(
          scimService.getServiceProviderConfig()).invoke();
    }
    catch (WebApplicationException e)
    {
      assertEquals(e.getResponse().getStatus(), 501);
      ErrorResponse errorResponse =
          e.getResponse().readEntity(ErrorResponse.class);
      assertEquals(errorResponse.getStatus(), Integer.valueOf(501));
    }
    catch (ScimException e)
    {
      e.printStackTrace();
    }
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
    catch(ScimException e)
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

    assertEquals(returnedSchemas.getTotalResults(), 2);
    assertTrue(contains(returnedSchemas, userSchema));
    assertTrue(contains(returnedSchemas, enterpriseSchema));

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

    assertEquals(returnedResourceTypes.getTotalResults(), 3);
    assertTrue(contains(returnedResourceTypes, resourceType));
    assertTrue(contains(returnedResourceTypes, singletonResourceType));

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
   * Test an resource endpoint implementation registered as a class.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testGetUsers() throws ScimException
  {
    final ScimService service = new ScimService(target());
    final ListResponse<UserResource> returnedUsers =
        service.searchRequest("Users").
            filter("meta.resourceType eq \"User\"").
            page(1, 10).
            sort("id", SortOrder.ASCENDING).
            attributes("id", "name", "Meta").
            invoke(UserResource.class);

    assertEquals(returnedUsers.getTotalResults(), 1);
    assertEquals(returnedUsers.getStartIndex(), Integer.valueOf(1));
    assertEquals(returnedUsers.getItemsPerPage(), Integer.valueOf(1));

    final UserResource r = returnedUsers.getResources().get(0);
    service.retrieve(r);
  }

  /**
   * Test an resource endpoint implementation registered as a class.
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

    Set<String> excludedAttributes = new HashSet<String>();
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
    try
    {
      scimService.retrieve(subResourceUri.build(), UserResource.class);
      fail("Sub-resource should not exist");
    }
    catch(ScimException e)
    {
      // Expected.
    }
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
        attributes("nickName","Meta").invoke(UserResource.class);
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

    try
    {
      scimService.retrieve(createdUser);
      fail("Resource should have been deleted");
    }
    catch(ResourceNotFoundException e)
    {
      // expected
    }

    // Now with no accept header
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path("SingletonUsers").path("deleteUser").
        request().delete();
    assertEquals(response.getStatus(), 404);
    assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);

    // With invalid accept type (DS-14520)
    target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));
    response = target.path("SingletonUsers").path("deleteUser").
        request().accept(MediaType.APPLICATION_ATOM_XML_TYPE).delete();
    assertEquals(response.getStatus(), 404);
    assertEquals(response.getMediaType(), ServerUtils.MEDIA_TYPE_SCIM_TYPE);
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

    List<PatchOperation> patchOperations = new ArrayList<PatchOperation>();
    patchOperations.add(PatchOperation.remove("displayName"));
    patchOperations.add(PatchOperation.replace("name.middleName", "the"));
    patchOperations.add(PatchOperation.replace(
        "emails[type eq \"work\"].value", "bobNew@tester.com"));
    patchOperations.add(PatchOperation.add("phoneNumbers",
        JsonUtils.valueToNode(Lists.newArrayList(phone1, phone2))));

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

    List<PatchOperation> patchOperations = new ArrayList<PatchOperation>();
    patchOperations.add(PatchOperation.remove("displayName"));
    patchOperations.add(PatchOperation.replace("name.middleName", "the"));
    patchOperations.add(PatchOperation.replace(
        "emails[type eq \"work\"].value", "bobNew@tester.com"));
    patchOperations.add(PatchOperation.add("phoneNumbers",
        JsonUtils.valueToNode(Lists.newArrayList(phone1, phone2))));

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
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testInvalidJson() throws ScimException
  {
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path("SingletonUsers").request().
        accept(MEDIA_TYPE_SCIM).post(
        Entity.entity("{badJson}", MEDIA_TYPE_SCIM));
    assertEquals(response.getStatus(), 400);
    assertEquals(response.getMediaType(), MediaType.valueOf(MEDIA_TYPE_SCIM));
    ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
    assertEquals(errorResponse.getStatus(), Integer.valueOf(400));
    assertEquals(errorResponse.getScimType(), "invalidSyntax");
    assertNotNull(errorResponse.getDetail());

    // Now with application/json
    response = target.path("SingletonUsers").request().
        accept(MediaType.APPLICATION_JSON_TYPE,
            ServerUtils.MEDIA_TYPE_SCIM_TYPE).post(
        Entity.entity("{badJson}", MediaType.APPLICATION_JSON_TYPE));
    assertEquals(response.getStatus(), 400);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Test error response when an invalid standard SCIM message is submitted and
   * a Jackson binding error occurs.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testInvalidMessage() throws ScimException
  {
    WebTarget target = target().register(
        new JacksonJsonProvider(JsonUtils.createObjectMapper()));

    Response response = target.path("SingletonUsers").path(".search").
        request().accept(MEDIA_TYPE_SCIM).post(Entity.entity(
        "{\"undefinedField\": \"value\"}", MEDIA_TYPE_SCIM));
    assertEquals(response.getStatus(), 400);
    assertEquals(response.getMediaType(), MediaType.valueOf(MEDIA_TYPE_SCIM));
    ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
    assertEquals(errorResponse.getStatus(), Integer.valueOf(400));
    assertEquals(errorResponse.getScimType(), "invalidSyntax");
    assertNotNull(errorResponse.getDetail());

    // Now with application/json
    response = target.path("SingletonUsers").path(".search").
        request().accept(MediaType.APPLICATION_JSON_TYPE,
        ServerUtils.MEDIA_TYPE_SCIM_TYPE).post(Entity.entity(
        "{\"undefinedField\": \"value\"}", MediaType.APPLICATION_JSON_TYPE));
    assertEquals(response.getStatus(), 400);
    assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
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
      try
      {
        service.retrieveRequest(ScimService.ME_URI).
            queryParam(expectedKey, "unexpectedValue").
            invoke(UserResource.class);
        fail("Expected BadRequestException");
      }
      catch(BadRequestException e)
      {
        // Expected.
      }

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
      try
      {
        service.retrieveRequest(ScimService.ME_URI).
            header(expectedKey, "unexpectedValue").
            invoke(UserResource.class);
        fail("Expected BadRequestException");
      }
      catch(BadRequestException e)
      {
        // Expected.
      }

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
          filter("meta.resourceType eq \"User\"").
          header(expectedKey, expectedValue).
          invoke(UserResource.class);

      service.searchRequest("Users").
          filter("meta.resourceType eq \"User\"").
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
    catch(ScimServiceException ex)
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
    catch(ScimServiceException ex)
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
   * Test that MethodNotAllowedExceptions are thrown properly.
   *
   * @throws ScimException uncaught exceptions indicate an error.
   */
  @Test
  public void testMethodNotAllowed() throws ScimException
  {
    final ScimService service = new ScimService(target());

    try
    {
      service.create("Users/schema", new GenericScimResource());
      Assert.fail("Expecting a MethodNotFoundException");
    }
    catch(MethodNotAllowedException ex)
    {
      ErrorResponse response = ex.getScimError();
      Assert.assertEquals(response.getStatus(), Integer.valueOf(405));
      Assert.assertTrue(response.getDetail().contains("Method Not Allowed"));
    }
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
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testAcceptExceptions() throws ScimException
  {
    ScimService scimService = new ScimService(target());

    // Create a new user.
    try
    {
      String returnString = new String(scimService.retrieveRequest(
          "CustomContent", "123").accept(null).
          invoke(byte[].class));
      Assert.fail("Expected illegal argument exception");
    }
    catch(IllegalArgumentException ex)
    {
      // Expected.  Ignore.
    }
    try
    {
      String returnString = new String(scimService.retrieveRequest(
          "CustomContent", "123").accept().
          invoke(byte[].class));
      Assert.fail("Expected illegal argument exception");
    }
    catch(IllegalArgumentException ex)
    {
      // Expected.  Ignore.
    }
  }


  private void setMeta(Class<?> resourceClass, ScimResource scimResource)
  {
    ResourceTypeResource resourceType =
        ResourceTypeDefinition.fromJaxRsResource(
            resourceClass).toScimResource();
    UriBuilder locationBuilder =
        UriBuilder.fromUri(getBaseUri()).path(
            resourceType.getEndpoint().getPath());
    if(scimResource.getId() != null)
    {
      locationBuilder.path(scimResource.getId());
    }

    Meta meta = scimResource.getMeta();
    if(meta == null)
    {
      meta = new Meta();
      scimResource.setMeta(meta);
    }
    meta.setLocation(locationBuilder.build());
    meta.setResourceType(resourceType.getName());
  }

  private <T> boolean contains(Iterable<T> list, T resource)
  {
    for(T r : list)
    {
      if(r.equals(resource))
      {
        return true;
      }
    }
    return false;
  }
}
