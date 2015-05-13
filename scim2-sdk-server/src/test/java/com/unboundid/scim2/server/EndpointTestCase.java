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

package com.unboundid.scim2.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.unboundid.scim2.common.ResourceTypeResource;
import com.unboundid.scim2.common.SchemaResource;
import com.unboundid.scim2.common.ServiceProviderConfigResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.server.providers.ScimExceptionMapper;
import com.unboundid.scim2.server.providers.ScimJacksonJsonProvider;
import com.unboundid.scim2.server.providers.WebApplicationExceptionMapper;
import com.unboundid.scim2.server.resources.ResourceTypesEndpoint;
import com.unboundid.scim2.server.resources.SchemasEndpoint;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.annotations.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;

import java.net.URI;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected Application configure()
  {
    try
    {
      userSchema = SchemaUtils.getSchema(UserResource.class);
      enterpriseSchema = SchemaUtils.getSchema(EnterpriseUserExtension.class);

      resourceType = new ResourceTypeResource("User", "User Account",
          new URI("/Users"),
          new URI(userSchema.getId()));
      singletonResourceType = new ResourceTypeResource(
          "Singleton User", "Singleton User", "Singleton User Account",
          new URI("/SingletonUsers"), new URI(userSchema.getId()),
          Collections.singletonList(new ResourceTypeResource.SchemaExtension(
              new URI(enterpriseSchema.getId()), true)));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }


    ResourceConfig config = new ResourceConfig();
    config.register(ScimExceptionMapper.class);
    config.register(WebApplicationExceptionMapper.class);
    config.register(ScimJacksonJsonProvider.class);
    config.register(ResourceTypesEndpoint.class);
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
    config.register(
        new JacksonJsonProvider(SchemaUtils.createSCIMCompatibleMapper()));
  }

  /**
   * Test the service provider config can be retrieved.
   */
  @Test
  public void testGetServiceProviderConfig()
  {
    final ServiceProviderConfigResource returnedServiceProviderConfig =
        target("ServiceProviderConfig").request().get(
            ServiceProviderConfigResource.class);

    assertEquals(returnedServiceProviderConfig,
        TestServiceProviderConfigEndpoint.CONFIG);
  }

  /**
   * Test that modifying the service provider config results in error.
   */
  @Test
  public void testPutServiceProviderConfig()
  {
    try
    {
      target("ServiceProviderConfig").request().put(
          Entity.json(TestServiceProviderConfigEndpoint.CONFIG),
          ServiceProviderConfigResource.class);
    }
    catch (WebApplicationException e)
    {
      assertEquals(e.getResponse().getStatus(), 501);
      ErrorResponse errorResponse =
          e.getResponse().readEntity(ErrorResponse.class);
      assertEquals(errorResponse.getStatus(), new Integer(501));
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
      target("badPath").request().get(
          ServiceProviderConfigResource.class);
      fail();
    }
    catch(NotFoundException nfe)
    {
      ErrorResponse errorResponse =
          nfe.getResponse().readEntity(ErrorResponse.class);
      assertEquals(errorResponse.getStatus(), new Integer(404));
    }
  }

  /**
   * Test all schemas can be retrieved.
   */
  @Test
  public void testGetSchemas()
  {
    final ListResponse<SchemaResource> returnedSchemas =
            target("Schemas").request().get(
                new GenericType<ListResponse<SchemaResource>>() {});

    assertEquals(returnedSchemas.getTotalResults(), 2);
    assertTrue(contains(returnedSchemas, userSchema));
    assertTrue(contains(returnedSchemas, enterpriseSchema));
  }

  /**
   * Test an individual schema can be retrieved.
   */
  @Test
  public void testGetSchema()
  {
    SchemaResource returnedSchema =
            target("Schemas").path(userSchema.getId()).request().get(
                SchemaResource.class);

    assertEquals(returnedSchema, userSchema);

    returnedSchema =
            target("Schemas").path(enterpriseSchema.getId()).request().get(
                SchemaResource.class);

    assertEquals(returnedSchema, enterpriseSchema);
  }

  /**
   * Test all resource types can be retrieved.
   */
  @Test
  public void testGetResourceTypes()
  {
    final ListResponse<ResourceTypeResource> returnedResourceTypes =
            target("ResourceTypes").request().get(
                new GenericType<ListResponse<ResourceTypeResource>>() {});

    assertEquals(returnedResourceTypes.getTotalResults(), 2);
    assertTrue(contains(returnedResourceTypes, resourceType));
    assertTrue(contains(returnedResourceTypes, singletonResourceType));
  }

  /**
   * Test an individual resource type can be retrieved.
   */
  @Test
  public void testGetResourceType()
  {
    final ResourceTypeResource returnedResourceTypeById =
            target("ResourceTypes").path(resourceType.getId()).request().get(
                ResourceTypeResource.class);

    assertEquals(returnedResourceTypeById, resourceType);

    final ResourceTypeResource returnedResourceTypeByName =
            target("ResourceTypes").path(
                singletonResourceType.getName()).request().get(
                ResourceTypeResource.class);

    assertEquals(returnedResourceTypeByName, singletonResourceType);

    final SchemaResource returnedSchema =
            target("Schemas").path(
                returnedResourceTypeById.getSchema().toString()).request().get(
                SchemaResource.class);

    assertEquals(returnedSchema, userSchema);


  }

  /**
   * Test an resource endpoint implementation registered as a class.
   */
  @Test
  public void testGetUsers()
  {
    final ListResponse<UserResource> returnedUsers =
            target("Users").request().get(
                new GenericType<ListResponse<UserResource>>() {});

    assertEquals(returnedUsers.getTotalResults(), 1);
  }

  /**
   * Test create operation.
   *
   * @throws ScimException if an error occurs.
   */
  @Test
  public void testCreate() throws ScimException
  {
    WebTarget target = target("SingletonUsers");

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("createUser");
    EnterpriseUserExtension extension =
        new EnterpriseUserExtension().setEmployeeNumber("1234");
    newUser.setExtensionValue(
        SchemaUtils.getSchemaUrn(EnterpriseUserExtension.class), extension);

    UserResource createdUser = target.request().post(
        Entity.entity(newUser, ApiConstants.MEDIA_TYPE_SCIM_TYPE),
        UserResource.class);
    assertNotNull(createdUser.getId());
    assertEquals(createdUser.getUserName(), newUser.getUserName());
    try
    {
      assertEquals(createdUser.getExtensionValue(
          SchemaUtils.getSchemaUrn(EnterpriseUserExtension.class),
          EnterpriseUserExtension.class), extension);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e);
    }

    UserResource retrievedUser =
        target.path(createdUser.getId()).request().get(UserResource.class);

    assertEquals(createdUser, retrievedUser);
  }

  /**
   * Test delete operation.
   */
  @Test
  public void testDelete()
  {
    WebTarget target = target("SingletonUsers");

    // Create a new user.
    UserResource newUser = new UserResource().setUserName("deleteUser");
    UserResource createdUser = target.request().post(
        Entity.entity(newUser, ApiConstants.MEDIA_TYPE_SCIM_TYPE),
        UserResource.class);
    assertNotNull(createdUser.getId());
    assertEquals(createdUser.getUserName(), newUser.getUserName());

    target.path(createdUser.getId()).request().delete();

    try
    {
      target.path(createdUser.getId()).request().get(UserResource.class);
      fail("Resource should have been deleted");
    }
    catch(NotFoundException e)
    {
      // expected
    }
  }

  private <T> boolean contains(ListResponse<T> list, T resource)
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
