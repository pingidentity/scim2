/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.model.BaseScimObject;
import com.unboundid.scim2.model.BaseScimResourceObject;
import com.unboundid.scim2.model.CommonScimObject;
import com.unboundid.scim2.model.GenericScimObject;
import com.unboundid.scim2.model.Meta;
import com.unboundid.scim2.schema.SCIM2Attribute;
import com.unboundid.scim2.utils.ScimJsonHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for schema extensions.
 */
@Test
public class ExtensionsTest
{
  @SchemaInfo(description = "Class to represent users",
      id = "urn:junit:CoreClass_User",
    name = "Users")
  private static class CoreClass_User extends BaseScimResourceObject
  {
    @SchemaProperty(description = "A user's username")
    private String userName;

    @SchemaProperty(description = "The name of the user")
    private CoreClass_Name name;

    @SchemaProperty(description = "The user's password",
        mutability = SCIM2Attribute.Mutability.WRITE_ONLY)
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

  private static class CoreClass_Name extends BaseScimObject
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
      id = "urn:unboundid:schemas:favoriteColor", name = "FavoriteColor")
  private static class ExtensionClass extends BaseScimObject
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
    ObjectMapper mapper = BaseScimResourceObject.createSCIMCompatibleMapper();

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
    Assert.assertEquals(userNode.path("urn:unboundid:schemas:favoriteColor").
            path("favoriteColor").asText(),
        user.getExtension(ExtensionClass.class).getFavoriteColor());
  }

  /**
   * Tests parsing json into a scim resource.
   * @throws Exception thrown if an error occurs.
   */
  @Test
  public void testJsonParsing_annotations() throws Exception
  {
    ObjectMapper mapper = BaseScimResourceObject.createSCIMCompatibleMapper();

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
        "        \"urn:unboundid:schemas:favoriteColor\",\n" +
        "        \"urn:junit:CoreClass_User\"\n" +
        "    ],\n" +
        "    \"urn:unboundid:schemas:favoriteColor\": {\n" +
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
    schemaSet.add("urn:unboundid:schemas:favoriteColor");
    schemaSet.add("urn:junit:CoreClass_User");

    Assert.assertEquals(user.getSchemaUrns(),
        schemaSet);

    // check the extension values
    Assert.assertEquals(
        user.getExtension(ExtensionClass.class).getFavoriteColor(),
        "extension:favoritecolor");
  }

  /**
   * Tests getting extensions from a generic scim object.
   * @throws Exception thrown if an error occurs.
   */
  @Test
  public void testGetExtensionAsGenericScimObject() throws Exception
  {
    CommonScimObject commonScimObject = getGenericUser();

    ExtensionClass extensionClass =
        commonScimObject.getExtension(ExtensionClass.class);
    GenericScimObject genericScimObject =
        commonScimObject.getExtension("urn:unboundid:schemas:favoriteColor");

    Assert.assertEquals(extensionClass.getFavoriteColor(),
        "extension:favoritecolor");

    ScimJsonHelper helper = new ScimJsonHelper(genericScimObject.getJsonNode());
    Assert.assertEquals(helper.path("favoriteColor").asText(),
        extensionClass.getFavoriteColor());
  }

  /**
   * Create a user (test class) with an extension.
   * @return a user for tests.
   * @throws URISyntaxException thrown if an error occurs.
   */
  private CoreClass_User getBasicUser() throws URISyntaxException
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
    user.addExtension(extensionClass);

    return user;
  }

  /**
   * Create a GenericScimObject that represents a user (test class)
   * with an extension.  This should convertible to a user scim object.
   * @return a GenericScimObject user for tests.
   * @throws Exception thrown if an error occurs.
   */
  private GenericScimObject getGenericUser() throws Exception
  {
    ObjectMapper mapper = BaseScimResourceObject.createSCIMCompatibleMapper();

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
        "        \"urn:unboundid:schemas:favoriteColor\",\n" +
        "        \"urn:junit:CoreClass_User\"\n" +
        "    ],\n" +
        "    \"urn:unboundid:schemas:favoriteColor\": {\n" +
        "        \"favoriteColor\": \"extension:favoritecolor\"\n" +
        "    },\n" +
        "    \"userName\": \"user:username\"\n" +
        "}";

    GenericScimObject user =
        mapper.readValue(jsonString, GenericScimObject.class);
    return user;
  }
}
