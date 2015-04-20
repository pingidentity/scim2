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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.common.annotations.SchemaInfo;
import com.unboundid.scim2.common.annotations.SchemaProperty;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tests for schema extensions.
 */
@Test
public class ExtensionsTest
{
  @SchemaInfo(description = "Class to represent users",
      id = "urn:junit:CoreClass_User:Users",
    name = "Users")
  private static class CoreClass_User extends BaseScimResource
  {
    @SchemaProperty(description = "A user's username")
    private String userName;

    @SchemaProperty(description = "The name of the user")
    private CoreClass_Name name;

    @SchemaProperty(description = "The user's password",
        mutability = AttributeDefinition.Mutability.WRITE_ONLY)
    private String password;

    /**
     * Gets the user name.
     * @return the user name.
     */
    public String getUserName()
    {
      return userName;
    }

    /**
     * Sets the username.
     * @param userName the username.
     */
    public void setUserName(String userName)
    {
      this.userName = userName;
    }

    /**
     * Gets the name.
     * @return the name.
     */
    public CoreClass_Name getName()
    {
      return name;
    }

    /**
     * Sets the name.
     * @param name the name.
     */
    public void setName(CoreClass_Name name)
    {
      this.name = name;
    }

    /**
     * Sets the password.
     * @param password the password.
     */
    public void setPassword(String password)
    {
      this.password = password;
    }
  }

  private static class CoreClass_Name
  {
    @SchemaProperty(description = "User's first name")
    public String first;

    @SchemaProperty(description = "User's middle name")
    public String middle;

    @SchemaProperty(description = "User's last name")
    public String last;

    /**
     * Gets the first name.
     * @return the first name.
     */
    public String getFirst()
    {
      return first;
    }

    /**
     * Sets the first name.
     * @param first the first name.
     */
    public void setFirst(String first)
    {
      this.first = first;
    }

    /**
     * Gets the middle name.
     * @return the middle name.
     */
    public String getMiddle()
    {
      return middle;
    }

    /**
     * Sets the middle name.
     * @param middle the middle name.
     */
    public void setMiddle(String middle)
    {
      this.middle = middle;
    }

    /**
     * Gets the last name.
     * @return the last name.
     */
    public String getLast()
    {
      return last;
    }

    /**
     * Sets the last name.
     * @param last the last name.
     */
    public void setLast(String last)
    {
      this.last = last;
    }
  }

  @SchemaInfo(description = "Class to represent a favorite color",
      id = "urn:unboundid:schemas:FavoriteColor",
      name = "FavoriteColor")
  private static class ExtensionClass
  {
    @SchemaProperty(description = "Favorite color")
    private String favoriteColor;

    /**
     * Gets the favorite color.
     * @return the favorite color.
     */
    public String getFavoriteColor()
    {
      return favoriteColor;
    }

    /**
     * Sets the favorite color.
     * @param favoriteColor the favorite color.
     */
    public void setFavoriteColor(String favoriteColor)
    {
      this.favoriteColor = favoriteColor;
    }
  }

  /**
   * Tests json generation of the schema annotation.
   * @throws Exception thrown if an error occurs.
   */
  @Test
  public void testJsonGeneration_annotations() throws Exception
  {
    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();

    CoreClass_User user = getBasicUser();
    String userString = mapper.writeValueAsString(user);

    JsonNode userNode = mapper.readTree(userString);

    // check some of the basic fields
    Assert.assertEquals(userNode.path("userName").asText(),
        user.getUserName());
    Assert.assertEquals(userNode.path("id").asText(),
        user.getId());
    Assert.assertEquals(userNode.path("externalId").asText(),
        user.getExternalId());

    // check the schemas
    Assert.assertEquals(mapper.treeToValue(userNode.path("schemas"),
            HashSet.class), user.getSchemaUrns());

    // check the extension values
    Assert.assertEquals(
        userNode.path("urn:unboundid:schemas:FavoriteColor").
            path("favoriteColor").asText(),
        user.getExtensionValue(Path.extension(ExtensionClass.class),
            ExtensionClass.class).getFavoriteColor());
  }

  /**
   * Tests parsing json into a scim resource.
   * @throws Exception thrown if an error occurs.
   */
  @Test
  public void testJsonParsing_annotations() throws Exception
  {
    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();

    String jsonString = "{\n" +
        "    \"externalId\": \"user:externalId\",\n" +
        "    \"id\": \"user:id\",\n" +
        "    \"meta\": {\n" +
        "        \"created\": \"2015-02-27T14:00:04Z\",\n" +
        "        \"lastModified\": \"2015-02-27T14:00:04Z\",\n" +
        "        \"location\": \"http://here/user\",\n" +
        "        \"resourceType\": \"some resource type\",\n" +
        "        \"version\": \"1.0\"\n" +
        "    },\n" +
        "    \"name\": {\n" +
        "        \"first\": \"name:first\",\n" +
        "        \"last\": \"name:last\",\n" +
        "        \"middle\": \"name:middle\"\n" +
        "    },\n" +
        "    \"password\": \"user:password\",\n" +
        "    \"schemas\": [\n" +
        "        \"urn:unboundid:schemas:FavoriteColor\",\n" +
        "        \"urn:junit:CoreClass_User:Users\"\n" +
        "    ],\n" +
        "    \"urn:unboundid:schemas:FavoriteColor\": {\n" +
        "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
        "    },\n" +
        "    \"userName\": \"user:username\"\n" +
        "}";

    CoreClass_User user = mapper.readValue(jsonString, CoreClass_User.class);

    // check some of the basic fields
    Assert.assertEquals(user.getUserName(), "user:username");
    Assert.assertEquals(user.getId(), "user:id");
    Assert.assertEquals(user.getExternalId(), "user:externalId");

    // check the schemas
    Set<String> schemaSet = new HashSet<String>();
    schemaSet.add("urn:unboundid:schemas:FavoriteColor");
    schemaSet.add("urn:junit:CoreClass_User:Users");

    Assert.assertEquals(user.getSchemaUrns(),
        schemaSet);

    // check the extension values
    Assert.assertEquals(
        user.getExtensionValue(Path.extension(ExtensionClass.class),
            ExtensionClass.class).getFavoriteColor(),
        "extension:favoritecolor");
  }

  /**
   * Tests getting extensions from a generic scim object.
   * @throws Exception thrown if an error occurs.
   */
  @Test
  public void testGetExtensionAsGenericScimObject() throws Exception
  {
    GenericScimResource commonScimObject = getGenericUser();

    ExtensionClass extensionClass =
        commonScimObject.getValue(
            Path.extension(ExtensionClass.class), ExtensionClass.class);
    Map extensionAttrs =
        commonScimObject.getValue("urn:unboundid:schemas:FavoriteColor:",
            Map.class);

    Assert.assertEquals(extensionClass.getFavoriteColor(),
        "extension:favoritecolor");

    Assert.assertEquals(extensionAttrs.get("favoriteColor"),
        extensionClass.getFavoriteColor());
  }

  /**
   * Create a user (test class) with an extension.
   * @return a user for tests.
   * @throws URISyntaxException thrown if an error occurs.
   */
  private CoreClass_User getBasicUser() throws URISyntaxException, ScimException
  {
    CoreClass_Name name = new CoreClass_Name();
    name.setFirst("name:first");
    name.setMiddle("name:middle");
    name.setLast("name:last");

    Meta meta = new Meta();
    meta.setCreated(new GregorianCalendar());
    meta.setLastModified(new GregorianCalendar());
    meta.setLocation(new URI("http://here/user"));
    meta.setResourceType("some resource type");
    meta.setVersion("1.0");

    ExtensionClass extensionClass = new ExtensionClass();
    extensionClass.setFavoriteColor("extension:favoritecolor");

    CoreClass_User user = new CoreClass_User();
    user.setPassword("user:password");
    user.setUserName("user:username");
    user.setName(name);
    user.setId("user:id");
    user.setExternalId("user:externalId");
    user.setMeta(meta);
    user.setExtensionValue(Path.extension(extensionClass.getClass()),
        extensionClass);

    return user;
  }

  /**
   * Create a GenericScimObject that represents a user (test class)
   * with an extension.  This should convertible to a user scim object.
   * @return a GenericScimObject user for tests.
   * @throws Exception thrown if an error occurs.
   */
  private GenericScimResource getGenericUser() throws Exception
  {
    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();

    String jsonString = "{\n" +
        "    \"externalId\": \"user:externalId\",\n" +
        "    \"id\": \"user:id\",\n" +
        "    \"meta\": {\n" +
        "        \"created\": \"2015-02-27T14:00:04Z\",\n" +
        "        \"lastModified\": \"2015-02-27T14:00:04Z\",\n" +
        "        \"location\": \"http://here/user\",\n" +
        "        \"resourceType\": \"some resource type\",\n" +
        "        \"version\": \"1.0\"\n" +
        "    },\n" +
        "    \"name\": {\n" +
        "        \"first\": \"name:first\",\n" +
        "        \"last\": \"name:last\",\n" +
        "        \"middle\": \"name:middle\"\n" +
        "    },\n" +
        "    \"password\": \"user:password\",\n" +
        "    \"schemas\": [\n" +
        "        \"urn:unboundid:schemas:FavoriteColor\",\n" +
        "        \"urn:junit:CoreClass_User:Users\"\n" +
        "    ],\n" +
        "    \"urn:unboundid:schemas:FavoriteColor\": {\n" +
        "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
        "    },\n" +
        "    \"userName\": \"user:username\"\n" +
        "}";

    GenericScimResource user =
        mapper.readValue(jsonString, GenericScimResource.class);
    return user;
  }
}
