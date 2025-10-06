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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOpType;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.Entitlement;
import com.unboundid.scim2.common.types.InstantMessagingAddress;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.PhoneNumber;
import com.unboundid.scim2.common.types.Photo;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Test the SCIM resource diff utility.
 */
public class DiffTestCase
{
  /**
   * Test comparison of single-valued attributes.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testDiffSingularAttribute() throws Exception
  {
    // *** singular ***
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

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

    assertEquals(d.size(), 2);

    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("title"))));
    ObjectNode replaceValue = JsonUtils.getJsonNodeFactory().objectNode();
    replaceValue.put("userType", "manager");
    replaceValue.put("nickName", "bjj3");
    assertTrue(d.contains(PatchOperation.replace(replaceValue)));

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertThat(source).isEqualTo(target);
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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

    ObjectNode name = JsonUtils.valueToNode(new Name().
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
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - added
    source = JsonUtils.getJsonNodeFactory().objectNode();
    target = JsonUtils.getJsonNodeFactory().objectNode();

    target.set("name", name);

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    assertTrue(d.contains(PatchOperation.replace(
        JsonUtils.getJsonNodeFactory().objectNode().set("name", name))));

    d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - removed
    source = JsonUtils.getJsonNodeFactory().objectNode();
    target = JsonUtils.getJsonNodeFactory().objectNode();

    source.set("name", name);
    target.putNull("name");

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("name"))));

    d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - removed a sub-attribute
    source = JsonUtils.getJsonNodeFactory().objectNode();
    target = JsonUtils.getJsonNodeFactory().objectNode();

    source.set("name", name);
    target.set("name", name.deepCopy().putNull("honorificSuffix"));

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("name").attribute("honorificSuffix"))));

    d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - updated
    source = JsonUtils.getJsonNodeFactory().objectNode();
    target = JsonUtils.getJsonNodeFactory().objectNode();

    source.set("name", name);
    target.set("name", name.deepCopy().put("familyName", "Johnson"));

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 1);
    ObjectNode replaceValue = JsonUtils.getJsonNodeFactory().objectNode();
    replaceValue.putObject("name").put("familyName", "Johnson");
    assertTrue(d.contains(PatchOperation.replace(replaceValue)));

    d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);

    // - non-asserted
    source = JsonUtils.getJsonNodeFactory().objectNode();
    target = JsonUtils.getJsonNodeFactory().objectNode();

    ObjectNode nameCopy = name.deepCopy();
    nameCopy.remove("middleName");
    source.set("name", name);
    target.set("name", nameCopy);

    d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 0);

    d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

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

    assertTrue(d.contains(PatchOperation.remove(Path.root().attribute("ims"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("photos",
            Filter.eq("value", thumbnail)))));
    ObjectNode replaceValue = JsonUtils.getJsonNodeFactory().objectNode();
    replaceValue.putArray("entitlements").add(entitlement3);
    replaceValue.putArray("phones").add(phone1).add(phone2);
    assertTrue(d.contains(PatchOperation.replace(replaceValue)));
    ObjectNode addValue = JsonUtils.getJsonNodeFactory().objectNode();
    addValue.putArray("photos").add(photo3);
    assertTrue(d.contains(PatchOperation.add(addValue)));

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);

    // Have to compare photos explicitly since ordering of array values doesn't
    // matter.
    JsonNode sourcePhotos = source.remove("photos");
    JsonNode targetPhotos = target.remove("photos");
    assertEquals(sourcePhotos.size(), targetPhotos.size());
    for (JsonNode sourceValue : sourcePhotos)
    {
      boolean found = false;
      for (JsonNode targetValue : targetPhotos)
      {
        if (sourceValue.equals(targetValue))
        {
          found = true;
          break;
        }
      }
      if (!found)
      {
        fail("Source photo value " + sourceValue +
            " not in target photo array " + targetPhotos);
      }
    }
    assertThat(source).isEqualTo(target);
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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

    // - unchanged
    ObjectNode email1 = JsonUtils.valueToNode(new Email().
        setValue("bjensen@example.com").
        setType("work").
        setPrimary(true));
    ObjectNode email2 = JsonUtils.valueToNode(new Email().
        setValue("babs@jensen.org").
        setType("home").
        setPrimary(false));

    source.putArray("emails").add(email1).add(email2);
    target.putArray("emails").add(email1).add(email2);

    // - added
    ObjectNode phone1 = JsonUtils.valueToNode(new PhoneNumber().
        setValue("1234567890").
        setType("work").
        setPrimary(true));
    ObjectNode phone2 = JsonUtils.valueToNode(new PhoneNumber().
        setValue("0987654321").
        setType("home").
        setPrimary(false));

    target.putArray("phones").add(phone1).add(phone2);

    // - removed
    ObjectNode im1 = JsonUtils.valueToNode(new InstantMessagingAddress().
        setValue("babs").
        setType("aim").
        setPrimary(true));
    ObjectNode im2 = JsonUtils.valueToNode(new InstantMessagingAddress().
        setValue("bjensen").
        setType("gtalk").
        setPrimary(false));

    source.putArray("ims").add(im1).add(im2);
    target.putArray("ims");

    // - updated
    // -- unchanged
    ObjectNode photo0 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo0")).
        setType("photo0").
        setPrimary(false));
    ObjectNode photo1 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo1")).
        setType("photo1").
        setPrimary(false));
    // -- non-asserted
    ObjectNode photo2 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo2")).
        setType("photo2").
        setPrimary(false));
    ObjectNode photo2a = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo2")).
        setType("photo2"));
    // -- add a new value
    ObjectNode photo3 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo3")).
        setType("photo3").
        setPrimary(true));
    // -- update an existing value
    ObjectNode photo4 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo4")).
        setType("photo4").
        setPrimary(true));
    ObjectNode photo4a = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo4")).
        setType("photo4").
        setPrimary(false));
    // -- add a new value
    ObjectNode photo5 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo5")).
        setType("photo5").
        setPrimary(false));
    ObjectNode photo5a = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo5")).
        setType("photo5").
        setPrimary(false).
        setDisplay("Photo 5"));
    // -- remove an existing value
    ObjectNode photo6 = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo6")).
        setType("photo6").
        setPrimary(false).
        setDisplay("Photo 6"));
    ObjectNode photo6a = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://photo6")).
        setType("photo6").
        setPrimary(false));
    photo6a.putNull("display");
    // -- remove a value
    ObjectNode thumbnail = JsonUtils.valueToNode(new Photo().
        setValue(new URI("http://thumbnail1")).
        setType("thumbnail").
        setPrimary(true));

    source.putArray("photos").add(photo0).add(photo1).add(photo2).
        add(photo4).add(photo5).add(photo6).add(thumbnail);
    target.putArray("photos").add(photo0).add(photo1).add(photo2a).
        add(photo4a).add(photo5a).add(photo6a).add(photo3);

    // -- updated with all new values
    ObjectNode entitlement1 = JsonUtils.valueToNode(new Entitlement().
        setValue("admin").
        setPrimary(false));
    ObjectNode entitlement2 = JsonUtils.valueToNode(new Entitlement().
        setValue("user").
        setPrimary(false));
    ObjectNode entitlement3 = JsonUtils.valueToNode(new Entitlement().
        setValue("inactive").
        setPrimary(true));
    source.putArray("entitlements").add(entitlement1).add(entitlement2);
    target.putArray("entitlements").add(entitlement3);

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 7);

    assertTrue(d.contains(PatchOperation.remove(Path.root().attribute("ims"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://photo6\" and " +
                "display eq \"Photo 6\" and " +
                "type eq \"photo6\" and " +
                "primary eq false")).attribute("display"))));
    assertTrue(d.contains(PatchOperation.replace(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://photo4\" and " +
                "type eq \"photo4\" and " +
                "primary eq true")),
        JsonUtils.getJsonNodeFactory().objectNode().put("primary", false))));
    assertTrue(d.contains(PatchOperation.replace(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://photo5\" and " +
                "type eq \"photo5\" and " +
                "primary eq false")),
        JsonUtils.getJsonNodeFactory().objectNode().put(
            "display", "Photo 5"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("photos",
            Filter.fromString("value eq \"http://thumbnail1\" and " +
                "type eq \"thumbnail\" and " +
                "primary eq true")))));
    ObjectNode replaceValue = JsonUtils.getJsonNodeFactory().objectNode();
    replaceValue.putArray("entitlements").add(entitlement3);
    replaceValue.putArray("phones").add(phone1).add(phone2);
    assertTrue(d.contains(PatchOperation.replace(replaceValue)));
    ObjectNode addValue = JsonUtils.getJsonNodeFactory().objectNode();
    addValue.putArray("photos").add(photo3);
    assertTrue(d.contains(PatchOperation.add(addValue)));

    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertThat(source).isEqualTo(target);
  }

  /**
   * Test comparison against 1st {@code null} object.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffNullObject1() throws Exception
  {
    // *** singular ***
    ObjectNode source = null;
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

    // - unchanged
    target.put("userName", "bjensen");
    target.put("nickName", "bjj3");
    target.put("title", "hot shot");
    target.put("userType", "employee");

    assertThatThrownBy(() -> JsonUtils.diff(source, target, false))
        .isInstanceOf(NullPointerException.class);
  }

  /**
   * Test comparison against 2nd {@code null} object.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void testDiffNullObject2() throws Exception
  {
    // *** singular ***
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = null;

    // - unchanged
    source.put("userName", "bjensen");
    source.put("nickName", "bjj3");
    source.put("title", "hot shot");
    source.put("userType", "employee");

    assertThatThrownBy(() -> JsonUtils.diff(source, target, false))
        .isInstanceOf(NullPointerException.class);
  }

  /**
   * Test comparison of {@code null} objects.
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

    assertThatThrownBy(() -> JsonUtils.diff(source, target, false))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> JsonUtils.diff(source, target, true))
    .isInstanceOf(NullPointerException.class);
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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

    source.put("userName", "bjensen");
    target.put("userName", "bjensen");
    source.put("nickName", "bjj3");
    target.put("nickName", "bjj3");
    source.put("title", "hot shot");
    target.put("title", "hot shot");
    source.put("userType", 1);
    target.put("userType", new BigDecimal(1));
    source.put("password", "cGFzc3dvcmQ=");
    target.put("password", "password".getBytes());

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
    ObjectNode source = JsonUtils.getJsonNodeFactory().objectNode();
    ObjectNode target = JsonUtils.getJsonNodeFactory().objectNode();

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
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("userName"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("nickName"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("title"))));
    assertTrue(d.contains(PatchOperation.remove(
        Path.root().attribute("userType"))));

    target = JsonUtils.getJsonNodeFactory().objectNode();
    List<PatchOperation> d2 = JsonUtils.diff(source, target, true);
    for (PatchOperation op : d2)
    {
      op.apply(source);
    }
    removeNullNodes(target);
    assertEquals(source, target);
  }

  /**
   * Sanity test with source and target objects containing multiple attribute
   * types but with only a small difference.
   *
   * @throws Exception
   *           if an error occurs.
   */
  @Test
  public void sanityTest() throws Exception
  {
    ObjectNode source = (ObjectNode) JsonUtils.getObjectReader().readTree(
        """
            {
              "addresses": [
                {
                  "streetAddress": "60804 Ridge Street",
                  "locality": "Indianapolis",
                  "region": "HI",
                  "postalCode": "92756",
                  "country": "US",
                  "primary": true,
                  "type": "home"
                }
              ],
              "someObject": [
                {
                  "someField": "A"
                }
              ],
              "urn:pingidentity:schemas:sample:profile:1.0": {
                "birthdayDayMonth": {
                  "day": 24,
                  "month": 12
                },
                "communicationOpts": [
                  {
                    "id": "urn:X-UnboundID:Opt:SMSMarketing",
                    "destination": "+1 921 433 6722",
                    "destinationType": "sms",
                    "polarityOpt": "out",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0",
                    "frequency": "daily;5:33"
                  },
                  {
                    "id": "urn:X-UnboundID:Opt:EmailMarketing",
                    "destination": "user.13@example.com",
                    "destinationType": "email",
                    "polarityOpt": "in",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0",
                    "frequency": "daily;5:33"
                  }
                ],
                "contentOpts": [
                  {
                    "id": "urn:X-UnboundID:Opt:Coupons",
                    "polarityOpt": "out",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0"
                  },
                  {
                    "id": "urn:X-UnboundID:Opt:Newsletters",
                    "polarityOpt": "in",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0"
                  },
                  {
                    "id": "urn:X-UnboundID:Opt:Notification",
                    "polarityOpt": "in",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0",
                    "timeStamp": "2015-10-11T22:58:08.608Z"
                  }
                ],
                "postalCode": "92756",
                "termsOfService": [
                  {
                    "id": "urn:X-UnboundID:ToS:StandardUser:1.0",
                    "timeStamp": "2013-11-13T12:40:57Z",
                    "collector": "urn:X-UnboundID:App:Mobile:1.0"
                  }
                ],
                "topicPreferences": [
                  {
                    "id": "urn:X-UnboundID:topic:finance:renting",
                    "strength": -10,
                    "timeStamp": "2014-12-20T17:54:25Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:auto:maintenance",
                    "strength": -8,
                    "timeStamp": "2013-11-25T12:45:21Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:health:heart",
                    "strength": -5,
                    "timeStamp": "2013-10-30T13:32:39Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:shoes",
                    "strength": 10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:workout",
                    "strength": 10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:casual",
                    "strength": 10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:accessories",
                    "strength": -10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:impress",
                    "strength": -10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  }
                ]
              },
              "displayName": "Jacquelynn Ellis",
              "emails": [
                {
                  "value": "user.13@example.com",
                  "verified": false,
                  "primary": false,
                  "type": "work"
                },
                {
                  "value": "Jacquelynn.Ellis@gmail.com",
                  "verified": true,
                  "primary": true,
                  "type": "home"
                }
              ],
              "meta": {
                "lastModified": "2015-10-13T16:54:59.157Z",
                "resourceType": "Users"
              },
              "name": {
                "familyName": "Ellis",
                "formatted": "Chad Sanford",
                "givenName": "Jacquelynn",
                "middleName": "Krystle"
              },
              "phoneNumbers": [
                {
                  "value": "+1 909 234 2568",
                  "type": "work",
                  "verified": false,
                  "primary": false
                },
                {
                  "value": "+1 191 623 7660",
                  "type": "home",
                  "verified": false,
                  "primary": false
                },
                {
                  "value": "+1 490 020 8366",
                  "type": "mobile",
                  "verified": true,
                  "primary": true
                }
              ],
              "userName": "user.0",
              "id": "ad55a34a-763f-358f-93f9-da86f9ecd9e4",
              "schemas": [
                "urn:pingidentity:schemas:User:1.0",
                "urn:pingidentity:schemas:sample:profile:1.0"
              ]
            }""");

    ObjectNode target = (ObjectNode) JsonUtils.getObjectReader().readTree(
        """
            {
              "schemas": [
                "urn:pingidentity:schemas:User:1.0",
                "urn:pingidentity:schemas:sample:profile:1.0"
              ],
              "addresses": [
                {
                  "streetAddress": "60804 Ridge Street",
                  "locality": "Indianapolis",
                  "region": "HI",
                  "postalCode": "92756",
                  "country": "US",
                  "primary": true,
                  "type": "home"
                }
              ],
              "someObject": [
                {
                  "someField": "B"
                }
              ],
              "displayName": "Jacquelynn Ellis",
              "emails": [
                {
                  "value": "user.13@example.com",
                  "verified": false,
                  "primary": false,
                  "type": "work"
                },
                {
                  "value": "Jacquelynn.Ellis@gmail.com",
                  "verified": true,
                  "primary": true,
                  "type": "home"
                }
              ],
              "name": {
                "familyName": "Ellis",
                "formatted": "Chad Sanford",
                "givenName": "Jacquelynn",
                "middleName": "Krystle"
              },
              "phoneNumbers": [
                {
                  "value": "+1 909 234 2568",
                  "type": "work",
                  "verified": false,
                  "primary": false
                },
                {
                  "value": "+1 191 623 7660",
                  "type": "home",
                  "verified": false,
                  "primary": false
                },
                {
                  "value": "+1 490 020 8366",
                  "type": "mobile",
                  "verified": true,
                  "primary": true
                }
              ],
              "urn:pingidentity:schemas:sample:profile:1.0": {
                "birthdayDayMonth": {
                  "day": 24,
                  "month": 12
                },
                "communicationOpts": [
                  {
                    "id": "urn:X-UnboundID:Opt:SMSMarketing",
                    "destination": "+1 921 433 6722",
                    "destinationType": "sms",
                    "polarityOpt": "out",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0",
                    "frequency": "daily;5:33"
                  },
                  {
                    "id": "urn:X-UnboundID:Opt:EmailMarketing",
                    "destination": "user.13@example.com",
                    "destinationType": "email",
                    "polarityOpt": "in",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0",
                    "frequency": "daily;5:33"
                  }
                ],
                "contentOpts": [
                  {
                    "id": "urn:X-UnboundID:Opt:Coupons",
                    "polarityOpt": "out",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0"
                  },
                  {
                    "id": "urn:X-UnboundID:Opt:Newsletters",
                    "polarityOpt": "in",
                    "timeStamp": "2015-10-11T22:58:08.608Z",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0"
                  },
                  {
                    "id": "urn:X-UnboundID:Opt:Notification",
                    "polarityOpt": "in",
                    "collector": "urn:X-UnboundID:App:Profile-Manager:1.0",
                    "timeStamp": "2015-10-11T22:58:08.608Z"
                  }
                ],
                "postalCode": "92756",
                "termsOfService": [
                  {
                    "id": "urn:X-UnboundID:ToS:StandardUser:1.0",
                    "timeStamp": "2013-11-13T12:40:57Z",
                    "collector": "urn:X-UnboundID:App:Mobile:1.0"
                  }
                ],
                "topicPreferences": [
                  {
                    "id": "urn:X-UnboundID:topic:finance:renting",
                    "strength": -10,
                    "timeStamp": "2014-12-20T17:54:25Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:auto:maintenance",
                    "strength": -8,
                    "timeStamp": "2013-11-25T12:45:21Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:health:heart",
                    "strength": -5,
                    "timeStamp": "2013-10-30T13:32:39Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:shoes",
                    "strength": 10,
                    "timeStamp": "2015-10-13T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:workout",
                    "strength": 10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:casual",
                    "strength": 10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:accessories",
                    "strength": -10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  },
                  {
                    "id": "urn:X-UnboundID:topic:clothing:impress",
                    "strength": -10,
                    "timeStamp": "2015-10-12T14:57:36.494Z"
                  }
                ]
              },
              "userName": "user.0",
              "photoURLs": []
            }""");

    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    assertEquals(d.size(), 2);
    assertEquals(d.get(0).getOpType(), PatchOpType.REPLACE);
    assertEquals(d.get(0).getPath().toString(),
        "urn:pingidentity:schemas:sample:profile:1.0:topicPreferences[" +
            "(id eq \"urn:X-UnboundID:topic:clothing:shoes\" and" +
            " strength eq 10 and timeStamp eq \"2015-10-12T14:57:36.494Z\")]");
    assertEquals(d.get(0).getJsonNode().path("timeStamp").textValue(),
        "2015-10-13T14:57:36.494Z");
  }

  /**
   * Test the case of creating a patch for an object with multivalued
   * attributes that have no values, and supplying the same object to
   * diff with.  We should return no patch operations, however in previous
   * iterations of this code, we would try and remove the attribute.
   *
   * @throws IOException if an error occurs
   */
  @Test
  public void testMultiValuedAttributesWithEmptyValues() throws IOException
  {
    String jsonString = """
        {
          "schemas" : [ "urn:unboundid:configuration:2.0" ],
          "id" : "userRoot2",
          "meta" : {
            "resourceType" : "LocalDbBackend",
            "location" : "http://localhost:5033/config/v2/Backends/userRoot2"
          },
          "urn:unboundid:configuration:2.0" : {
            "type" : "LocalDbBackend"
          },
          "backendId" : "userRoot2",
          "backgroundPrime" : "false",
          "backupFilePermissions" : "700",
          "baseDn" : [ "dc=example2,dc=com" ],
          "checkpointOnCloseCount" : "2",
          "cleanerThreadWaitTime" : "120000",
          "compactCommonParentDn" : [ ],
          "jeProperty" : [ ]
        }""";
    ObjectNode source = JsonUtils.getObjectReader().
        forType(ObjectNode.class).readValue(jsonString);
    ObjectNode target = JsonUtils.getObjectReader().
        forType(ObjectNode.class).readValue(jsonString);
    List<PatchOperation> d = JsonUtils.diff(source, target, false);
    Assert.assertEquals(d.size(), 0);
  }

  private void removeNullNodes(JsonNode object)
  {
    Iterator<JsonNode> i = object.iterator();
    while (i.hasNext())
    {
      JsonNode field = i.next();
      if (field.isNull() || (field.isArray() && field.isEmpty()))
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
