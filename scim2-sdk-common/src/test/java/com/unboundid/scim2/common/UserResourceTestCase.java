/*
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.common;

import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Some basic tests for serializing and de-serializing of the core user
 * resource.
 */
public class UserResourceTestCase
{
  private String fullRepresentation;

  /**
   * Initializes the environment before each test method.
   */
  @BeforeMethod
  public void init()
  {
    fullRepresentation = """
        {
          "schemas":[
            "urn:ietf:params:scim:schemas:core:2.0:User",
            "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"
          ],
          "id":"2819c223-7f76-453a-919d-413861904646",
          "externalId":"701984",
          "userName":"bjensen@example.com",
          "Name":{
            "formatted":"Ms. Barbara J Jensen III",
            "familyName":"Jensen",
            "GivenName":"Barbara",
            "middleName":"Jane",
            "honorificPrefix":"Ms.",
            "honorificSuffix":"III"
          },
          "displayName":"Babs Jensen",
          "nickName":"Babs",
          "profileUrl":"https://login.example.com/bjensen",
          "emails":[
            {
              "Value":"bjensen@example.com",
              "type":"work",
              "primary":true
            },
            {
              "value":"babs@jensen.org",
              "type":"home"
            }
          ],
          "addresses":[
            {
              "streetAddress":"100 Universal City Plaza",
              "locality":"Hollywood",
              "region":"CA",
              "postalCode":"91608",
              "country":"USA",
              "formatted":"100 Universal City Plaza\\nHollywood, CA 91608 USA",
              "type":"work",
              "primary":true
            },
            {
              "streetAddress":"456 Hollywood Blvd",
              "locality":"Hollywood",
              "region":"CA",
              "postalCode":"91608",
              "country":"USA",
              "formatted":"456 Hollywood Blvd\\nHollywood, CA 91608 USA",
              "type":"home"
            }
          ],
          "phoneNumbers":[
            {
              "value":"555-555-5555",
              "type":"work"
            },
            {
              "value":"555-555-4444",
              "type":"mobile"
            }
          ],
          "ims":[
            {
              "value":"someaimhandle",
              "type":"aim"
            }
          ],
          "photos":[
            {
              "value":
        "https://photos.example.com/profilephoto/72930000000Ccne/F",
              "type":"photo"
            },
            {
              "value":
        "https://photos.example.com/profilephoto/72930000000Ccne/T",
              "type":"thumbnail"
            }
          ],
          "userType":"Employee",
          "title":"Tour Guide",
          "preferredLanguage":"en-US",
          "locale":"en-US",
          "timezone":"America/Los_Angeles",
          "active":true,
          "password":"t1meMa$heen",
          "groups":[
            {
              "value":"e9e30dba-f08f-4109-8486-d5c6a331660a",
              "$ref":"../Groups/e9e30dba-f08f-4109-8486-d5c6a331660a",
              "display":"Tour Guides"
            },
            {
              "value":"fc348aa8-3835-40eb-a20b-c726e15c55b5",
              "$ref":"../Groups/fc348aa8-3835-40eb-a20b-c726e15c55b5",
              "display":"Employees"
            },
            {
              "value":"71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7",
              "$ref":"../Groups/71ddacd2-a8e7-49b8-a5db-ae50d0a5bfd7",
              "display":"US Employees"
            }
          ],
          "x509Certificates":[
            {
              "value":\
        "MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBg\
        NVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtc\
        GxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFaMH8xCzAJBgNVBAYTAlVT\
        MRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAww\
        YTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW\
        1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJ\
        FcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhV\
        bXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkb\
        T5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQ\
        G3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMY\
        n4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwG\
        CWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQ\
        U8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt\
        0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVko\
        MZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTV\
        vxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo="
            }
          ],
          "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User":{
            "employeeNumber":"701984",
            "costCenter":"4130",
            "Organization":"Universal Studios",
            "division":"Theme Park",
            "department":"Tour Operations",
            "manager":{
              "value":"26118915-6090-4610-87e4-49d8ca9f808d",
              "$ref":"../Users/26118915-6090-4610-87e4-49d8ca9f808d",
              "displayName":"John Smith"
            }
          },
          "meta":{
            "resourceType":"User",
            "created":"2010-01-23T04:56:22Z",
            "lastModified":"2011-05-13T04:42:34Z",
            "version":"W\\/\\"3694e05e9dff591\\"",
            "location":"https://example.com/v2/Users/\
        2819c223-7f76-453a-919d-413861904646"
          }
        }""";
  }

  /**
   * Test de-serializing the full core user representation copied from
   * draft-ietf-scim-core-schema-20.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSpecFullRepresentation() throws Exception
  {
    UserResource userResource =
        JsonUtils.getObjectReader().forType(UserResource.class).readValue(
            fullRepresentation);

      assertNotNull(userResource.getName());
      assertNotNull(userResource.getName().getGivenName());
      assertNotNull(userResource.getEmails());
      assertNotNull(userResource.getEmails().get(0).getValue());
      assertNotNull(userResource.getEmails().get(1).getValue());

    EnterpriseUserExtension enterpriseUserExtension =
        JsonUtils.nodeToValue(userResource.getExtensionValues(
                Path.root(EnterpriseUserExtension.class)).get(0),
            EnterpriseUserExtension.class);

    assertNotNull(enterpriseUserExtension);

    String serializedString =
        JsonUtils.getObjectWriter().writeValueAsString(
            userResource);

    assertEquals(
        JsonUtils.getObjectReader().forType(UserResource.class).readValue(
            serializedString),
        userResource);
  }

  /**
   * Test operations with POJO extension objects.
   * @throws Exception if an error occurs.
   */
  @Test
  public void testPOJOExtensions() throws Exception
  {
    String euExtensionSchema =
        "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";
    Path extensionPath = Path.root(EnterpriseUserExtension.class);
    UserResource userResource =
        JsonUtils.getObjectReader().forType(UserResource.class).readValue(
            fullRepresentation);

    EnterpriseUserExtension nodeEUExtension =
        JsonUtils.nodeToValue(
            userResource.getExtensionValues(extensionPath).get(0),
            EnterpriseUserExtension.class);

    EnterpriseUserExtension pojoEUExtension =
                userResource.getExtension(EnterpriseUserExtension.class);

    assertNotNull(pojoEUExtension);
    assertEquals(pojoEUExtension, nodeEUExtension);
    assertTrue(userResource.getSchemaUrns().contains(euExtensionSchema));

    nodeEUExtension.setCostCenter("1111");
    nodeEUExtension.setOrganization("New Organization 1");
    nodeEUExtension.setDivision("New Division 1");
    nodeEUExtension.setDepartment("New Department 1");

    // replace with a path object
    userResource.setExtension(nodeEUExtension);
    pojoEUExtension = userResource.getExtension(EnterpriseUserExtension.class);

    assertNotNull(pojoEUExtension);
    assertEquals(pojoEUExtension, nodeEUExtension);
    assertTrue(userResource.getSchemaUrns().contains(euExtensionSchema));

    // remove with path object
    Assert.assertTrue(
        userResource.removeExtension(EnterpriseUserExtension.class));
    Assert.assertNull(userResource.getExtension(EnterpriseUserExtension.class));
    assertFalse(userResource.getSchemaUrns().contains(euExtensionSchema));

    // now recreate the extension, and make sure it is present
    userResource.setExtension(nodeEUExtension);
    pojoEUExtension = userResource.getExtension(EnterpriseUserExtension.class);

    assertNotNull(pojoEUExtension);
    assertEquals(pojoEUExtension, nodeEUExtension);
    assertTrue(userResource.getSchemaUrns().contains(euExtensionSchema));
  }

  /**
   * Test conversion to GenericScimResource.
   *
   * @throws IOException indicates a test failure.
   */
  @Test
  public void testAsGenericScimResource() throws IOException
  {
    UserResource userResource1 =
        JsonUtils.getObjectReader().forType(UserResource.class).readValue(
            fullRepresentation);

    GenericScimResource gsr = userResource1.asGenericScimResource();

    UserResource userResource2 = JsonUtils.nodeToValue(gsr.getObjectNode(),
        UserResource.class);
    Assert.assertEquals(userResource1, userResource2);
  }

}
