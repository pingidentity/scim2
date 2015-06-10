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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.Entitlement;
import com.unboundid.scim2.common.types.InstantMessagingAddress;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.PhoneNumber;
import com.unboundid.scim2.common.types.Photo;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Test the SCIM resource diff utility.
 */
public class DiffTestCase
{
  private final ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
  /**
   * Test comparison of single-valued attributes.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDiffSingularAttribute() throws Exception
  {
    // *** singular ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    // - unchanged
    source.put("userName", "bjensen");
    target.put("userName", "bjensen");
    // - added
    target.put("nickName", "bjj3");
    // - removed
    source.put("title", "hot shot");
    target.putNull("title");
    // - updated
    source.put("userType", "employee");
    target.put("userType", "manager");
    // - non-asserted
    source.put("displayName", "don't touch");

    List<PatchOperation> d = JsonUtils.diff(source, target, false);

    assertEquals(d.size(), 3);

    assertTrue(d.contains(PatchOperation.add(null,
        mapper.getNodeFactory().objectNode().put("nickName","bjj3"))));
    assertTrue(d.contains(PatchOperation.remove("title")));
    ObjectNode replaceValue = mapper.createObjectNode();
    replaceValue.put("userType", "manager");
    assertTrue(d.contains(PatchOperation.replace(null, replaceValue)));

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);
  }

  /**
   * Test comparison of single-valued complex attributes.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDiffSingularComplexAttribute() throws Exception
  {
    // *** singular complex ***
    // - unchanged
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    ObjectNode name = mapper.valueToTree(new Name().
        setFormatted("Ms. Barbara J Jensen III").
        setFamilyName("Jensen").
        setMiddleName("J").
        setGivenName("Barbara").
        setHonorificPrefix("Ms.").
        setHonorificSuffix("III"));

    source.set("name", name);
    target.set("name", name);

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 0);

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - added
    source = mapper.getNodeFactory().objectNode();
    target = mapper.getNodeFactory().objectNode();

    target.set("name", name);

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    assertTrue(d.contains(PatchOperation.add(null,
        mapper.getNodeFactory().objectNode().set("name",name))));

    d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - removed
    source = mapper.getNodeFactory().objectNode();
    target = mapper.getNodeFactory().objectNode();

    source.set("name", name);
    target.putNull("name");

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    assertTrue(d.contains(PatchOperation.remove("name")));

    d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - removed a sub-attribute
    source = mapper.getNodeFactory().objectNode();
    target = mapper.getNodeFactory().objectNode();

    source.set("name", name);
    target.set("name", name.deepCopy().putNull("honorificSuffix"));

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    assertTrue(d.contains(PatchOperation.remove("name.honorificSuffix")));

    d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - updated
    source = mapper.getNodeFactory().objectNode();
    target = mapper.getNodeFactory().objectNode();

    source.set("name", name);
    target.set("name", name.deepCopy().put("familyName", "Johnson"));

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    ObjectNode replaceValue = mapper.createObjectNode();
    replaceValue.putObject("name").put("familyName", "Johnson");
    assertTrue(d.contains(PatchOperation.replace(null, replaceValue)));

    d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - non-asserted
    source = mapper.getNodeFactory().objectNode();
    target = mapper.getNodeFactory().objectNode();

    ObjectNode nameCopy = name.deepCopy();
    nameCopy.remove("middleName");
    source.set("name", name);
    target.set("name", nameCopy);

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 0);

    d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);
  }

  /**
   * Test comparison of multi-valued complex attributes.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDiffMultiValuedAttribute() throws Exception
  {
    // *** multi-valued ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    // - unchanged
    String email1 = "bjensen@example.com";
    String email2 = "babs@jensen.org";

    source.putArray("emails").add(email1).add(email2);
    target.putArray("emails").add(email1).add(email2);

    // - added
    String phone1 = "1234567890";
    String phone2 = "0987654321";

    target.putArray("phones").add(phone1).add(phone2);

    // - removed
    String im1 = "babs";
    String im2 = "bjensen";

    source.putArray("ims").add(im1).add(im2);
    target.putArray("ims");

    // - updated
    // -- unchanged
    String photo0 = "http://photo0";
    String photo1 = "http://photo1";
    // -- add a new value
    String photo3 = "http://photo3";
    // -- remove a value
    String thumbnail = "http://thumbnail1";

    source.putArray("photos").add(photo0).add(photo1).add(thumbnail);
    target.putArray("photos").add(photo0).add(photo1).add(photo3);

    // -- updated with all new values
    String entitlement1 = "admin";
    String entitlement2 = "user";
    String entitlement3 = "inactive";
    source.putArray("entitlements").add(entitlement1).add(entitlement2);
    target.putArray("entitlements").add(entitlement3);

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 4);

    assertTrue(d.contains(PatchOperation.remove("ims")));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("photos",
            Filter.eq("value", thumbnail)).toString())));
    ObjectNode replaceValue = mapper.createObjectNode();
    replaceValue.putArray("entitlements").add(entitlement3);
    assertTrue(d.contains(PatchOperation.replace(null, replaceValue)));
    ObjectNode addValue = mapper.createObjectNode();
    addValue.putArray("phones").add(phone1).add(phone2);
    addValue.putArray("photos").add(photo3);
    assertTrue(d.contains(PatchOperation.add(null, addValue)));

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);

    // Have to compare photos explicitly since ordering of array values doesn't
    // matter.
    JsonNode sourcePhotos = source.remove("photos");
    JsonNode targetPhotos = target.remove("photos");
    assertEquals(sourcePhotos.size(), targetPhotos.size());
    for(JsonNode sourceValue : sourcePhotos)
    {
      boolean found = false;
      for(JsonNode targetValue : targetPhotos)
      {
        if(sourceValue.equals(targetValue))
        {
          found = true;
          break;
        }
      }
      if(!found)
      {
        fail("Source photo value " + sourceValue +
            " not in target photo array " + targetPhotos);
      }
    }
    assertEquals(source, target);
  }

  /**
   * Test comparison of multi-valued complex attributes.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDiffMultiValuedComplexAttribute() throws Exception
  {
    // *** multi-valued ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    // - unchanged
    ObjectNode email1 = mapper.valueToTree(new Email().
        setValue("bjensen@example.com").
        setType("work").
        setPrimary(true));
    ObjectNode email2 = mapper.valueToTree(new Email().
        setValue("babs@jensen.org").
        setType("home").
        setPrimary(false));

    source.putArray("emails").add(email1).add(email2);
    target.putArray("emails").add(email1).add(email2);

    // - added
    ObjectNode phone1 = mapper.valueToTree(new PhoneNumber().
        setValue("1234567890").
        setType("work").
        setPrimary(true));
    ObjectNode phone2 = mapper.valueToTree(new PhoneNumber().
        setValue("0987654321").
        setType("home").
        setPrimary(false));

    target.putArray("phones").add(phone1).add(phone2);

    // - removed
    ObjectNode im1 = mapper.valueToTree(new InstantMessagingAddress().
        setValue("babs").
        setType("aim").
        setPrimary(true));
    ObjectNode im2 = mapper.valueToTree(new InstantMessagingAddress().
        setValue("bjensen").
        setType("gtalk").
        setPrimary(false));

    source.putArray("ims").add(im1).add(im2);
    target.putArray("ims");

    // - updated
    // -- unchanged
    ObjectNode photo0 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo0")).
        setType("photo0").
        setPrimary(false));
    ObjectNode photo1 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo1")).
        setType("photo1").
        setPrimary(false));
    // -- non-asserted
    ObjectNode photo2 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo2")).
        setType("photo2").
        setPrimary(false));
    ObjectNode photo2a = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo2")).
        setType("photo2"));
    // -- add a new value
    ObjectNode photo3 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo3")).
        setType("photo3").
        setPrimary(true));
    // -- update an existing value
    ObjectNode photo4 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo4")).
        setType("photo4").
        setPrimary(true));
    ObjectNode photo4a = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo4")).
        setType("photo4").
        setPrimary(false));
    // -- add a new value
    ObjectNode photo5 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo5")).
        setType("photo5").
        setPrimary(false));
    ObjectNode photo5a = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo5")).
        setType("photo5").
        setPrimary(false).
        setDisplay("Photo 5"));
    // -- remove an existing value
    ObjectNode photo6 = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo6")).
        setType("photo6").
        setPrimary(false).
        setDisplay("Photo 6"));
    ObjectNode photo6a = mapper.valueToTree(new Photo().
        setValue(new URL("http://photo6")).
        setType("photo6").
        setPrimary(false));
    photo6a.putNull("display");
    // -- remove a value
    ObjectNode thumbnail = mapper.valueToTree(new Photo().
        setValue(new URL("http://thumbnail1")).
        setType("thumbnail").
        setPrimary(true));

    source.putArray("photos").add(photo0).add(photo1).add(photo2).
        add(photo4).add(photo5).add(photo6).add(thumbnail);
    target.putArray("photos").add(photo0).add(photo1).add(photo2a).
        add(photo4a).add(photo5a).add(photo6a).add(photo3);

    // -- updated with all new values
    ObjectNode entitlement1 = mapper.valueToTree(new Entitlement().
        setValue("admin").
        setPrimary(false));
    ObjectNode entitlement2 = mapper.valueToTree(new Entitlement().
        setValue("user").
        setPrimary(false));
    ObjectNode entitlement3 = mapper.valueToTree(new Entitlement().
        setValue("inactive").
        setPrimary(true));
    source.putArray("entitlements").add(entitlement1).add(entitlement2);
    target.putArray("entitlements").add(entitlement3);

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 7);

    assertTrue(d.contains(PatchOperation.remove("ims")));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://photo6\" and " +
                "display eq \"Photo 6\" and " +
                "type eq \"photo6\" and " +
                "primary eq false")).attribute("display").toString())));
    assertTrue(d.contains(PatchOperation.replace(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://photo4\" and " +
                "type eq \"photo4\" and " +
                "primary eq true")).toString(),
        mapper.createObjectNode().put("primary", false))));
    assertTrue(d.contains(PatchOperation.replace(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://photo5\" and " +
                "type eq \"photo5\" and " +
                "primary eq false")).toString(),
        mapper.createObjectNode().put("display", "Photo 5"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://thumbnail1\" and " +
                "type eq \"thumbnail\" and " +
                "primary eq true")).toString())));
    ObjectNode replaceValue = mapper.createObjectNode();
    replaceValue.putArray("entitlements").add(entitlement3);
    assertTrue(d.contains(PatchOperation.replace(null, replaceValue)));
    ObjectNode addValue = mapper.createObjectNode();
    addValue.putArray("phones").add(phone1).add(phone2);
    addValue.putArray("photos").add(photo3);
    assertTrue(d.contains(PatchOperation.add(null, addValue)));

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);
  }

  /**
   * Test comparison against 1st null object.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffNullObject1() throws Exception
  {
    // *** singular ***
    ObjectNode source = null;
    ObjectNode target = mapper.getNodeFactory().objectNode();

    // - unchanged
    target.put("userName", "bjensen");
    target.put("nickName", "bjj3");
    target.put("title", "hot shot");
    target.put("userType", "employee");

    try
    {
      List<PatchOperation> d = JsonUtils.diff(source, target, false);
      fail("Expected NullPointerException");
    } catch (NullPointerException e)
    {
      // pass
    }
  }

  /**
   * Test comparison against 2nd null object.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffNullObject2() throws Exception
  {
    // *** singular ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = null;

    // - unchanged
    source.put("userName", "bjensen");
    source.put("nickName", "bjj3");
    source.put("title", "hot shot");
    source.put("userType", "employee");

    try
    {
      List<PatchOperation> d = JsonUtils.diff(source, target, false);
      fail("Expected NullPointerException");
    } catch (NullPointerException e)
    {
      // pass
    }
  }

  /**
   * Test comparison of null objects.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffNullObjects() throws Exception
  {
    // *** singular ***
    ObjectNode source = null;
    ObjectNode target = null;

    try
    {
      List<PatchOperation> d = JsonUtils.diff(source, target, false);
      fail("Expected NullPointerException");
    } catch (NullPointerException e)
    {
      // pass
    }

    try
    {
      List<PatchOperation> d = JsonUtils.diff(source, target, true);
      fail("Expected NullPointerException");
    } catch (NullPointerException e)
    {
      // pass
    }
  }

  /**
   * Test comparison of same object with itself.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffSameObject() throws Exception
  {
    // *** singular ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = source;

    // - unchanged
    source.put("userName", "bjensen");
    source.put("nickName", "bjj3");
    source.put("title", "hot shot");
    source.put("userType", "employee");

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 0);
  }

  /**
   * Test comparison of empty objects.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffEmptyObjects() throws Exception
  {
    // *** singular ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 0);
  }

  /**
   * Test comparison of objects with equal attributes.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffNoChanges() throws Exception
  {
    // *** singular ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    source.put("userName", "bjensen");
    target.put("userName", "bjensen");
    source.put("nickName", "bjj3");
    target.put("nickName", "bjj3");
    source.put("title", "hot shot");
    target.put("title", "hot shot");
    source.put("userType", "employee");
    target.put("userType", "employee");

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 0);
  }

  /**
   * Test comparison of objects removing all attributes.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testRemoveAll() throws Exception
  {
    // *** singular ***
    ObjectNode source = mapper.getNodeFactory().objectNode();
    ObjectNode target = mapper.getNodeFactory().objectNode();

    // - unchanged
    source.put("userName", "bjensen");
    target.putNull("userName");
    source.put("nickName", "bjj3");
    target.putNull("nickName");
    source.put("title", "hot shot");
    target.putNull("title");
    source.put("userType", "employee");
    target.putArray("userType");

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 4);
    assertTrue(d.contains(PatchOperation.remove("userName")));
    assertTrue(d.contains(PatchOperation.remove("nickName")));
    assertTrue(d.contains(PatchOperation.remove("title")));
    assertTrue(d.contains(PatchOperation.remove("userType")));

    target = mapper.getNodeFactory().objectNode();
    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for(PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);
  }

  private void removeNullNodes(JsonNode object)
  {
    Iterator<JsonNode> i = object.elements();
    while(i.hasNext())
    {
      JsonNode field = i.next();
      if(field.isNull() ||
          (field.isArray() && field.size() == 0))
      {
        i.remove();
      }
      else
      {
        removeNullNodes(field);
      }
    }
  }
}
