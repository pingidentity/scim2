/*
 * Copyright 2023-2024 Ping Identity Corporation
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

import com.unboundid.scim2.common.types.Address;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.Entitlement;
import com.unboundid.scim2.common.types.Group;
import com.unboundid.scim2.common.types.InstantMessagingAddress;
import com.unboundid.scim2.common.types.PhoneNumber;
import com.unboundid.scim2.common.types.Photo;
import com.unboundid.scim2.common.types.Role;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.types.X509Certificate;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertThrows;

/**
 * Tests for the varargs methods in the {@link UserResource} class.
 */
public class UserResourceVarargsTestCase
{
  /**
   * Test the varargs methods on the {@link UserResource} class.
   */
  @Test
  public void testEmailVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Kratos");
    user2.setUserName("Kratos");

    // Set a single email.
    user1.setEmails(Collections.singletonList(new Email().setValue("kratos@example.com")));
    user2.setEmails(new Email().setValue("kratos@example.com"));
    assertEquals(user1, user2);

    // Set two values.
    user1.setEmails(Arrays.asList(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com")
    ));
    user2.setEmails(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com")
    );
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setEmails(Arrays.asList(
            new Email().setValue("kratos2@example.com"),
            new Email().setValue("kratos@example.com")
    ));
    user2.setEmails(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com")
    );
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setEmails(Arrays.asList(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com"),
            new Email().setValue("kratos3@example.com")
    ));
    user2.setEmails(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com"),
            new Email().setValue("kratos3@example.com")
    );
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setEmails(Arrays.asList(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com"),
            new Email().setValue("kratos3@example.com")
    ));
    user2.setEmails(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos3@example.com"),
            new Email().setValue("kratos2@example.com")
    );
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setEmails((Email) null));

    // Null values in the varargs should be filtered out.
    user2.setEmails(new Email().setValue("kratos@example.com"),
                    null,
                    new Email().setValue("kratos2#example.com"),
                    null);
    assertEquals(user2.getEmails().size(), 2);

    user1.setEmails(Arrays.asList(
            new Email().setValue("kratos@example.com"),
            new Email().setValue("kratos2@example.com")
    ));
    user2.setEmails(new Email().setValue("kratos@example.com"),
            null, null, new Email().setValue("kratos2@example.com"), null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setPhoneNumbers} varargs method.
   */
  @Test
  public void testPhoneNumberVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Atreus");
    user2.setUserName("Atreus");
    final PhoneNumber pi = new PhoneNumber().setValue("+1 314-159-2653");
    final PhoneNumber euler = new PhoneNumber().setValue("+2 718-281-828");
    final PhoneNumber one = new PhoneNumber().setValue("+3 111-111-1111");

    // Set a single value.
    user1.setPhoneNumbers(Collections.singletonList(pi));
    user2.setPhoneNumbers(pi);
    assertEquals(user1, user2);

    // Set two values.
    user1.setPhoneNumbers(Arrays.asList(pi, euler));
    user2.setPhoneNumbers(pi, euler);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setPhoneNumbers(Arrays.asList(euler, pi));
    user2.setPhoneNumbers(pi, euler);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setPhoneNumbers(Arrays.asList(pi, euler, one));
    user2.setPhoneNumbers(pi, euler, one);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setPhoneNumbers(Arrays.asList(pi, euler, one));
    user2.setPhoneNumbers(pi, one, euler);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setPhoneNumbers((PhoneNumber) null));

    // Null values in the varargs should be filtered out.
    user2.setPhoneNumbers(pi, null, one, null);
    assertEquals(user2.getPhoneNumbers().size(), 2);

    user1.setPhoneNumbers(Arrays.asList(pi, euler));
    user2.setPhoneNumbers(pi, null, null, euler, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setIms}} varargs method.
   */
  @Test
  public void testImsVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Mimir");
    user2.setUserName("Mimir");
    final InstantMessagingAddress ims1 =
            new InstantMessagingAddress().setValue("@brother");
    final InstantMessagingAddress ims2 =
            new InstantMessagingAddress().setValue("@treebeard");
    final InstantMessagingAddress ims3 =
            new InstantMessagingAddress().setValue("@head");

    // Set a single value.
    user1.setIms(Collections.singletonList(ims1));
    user2.setIms(ims1);
    assertEquals(user1, user2);

    // Set two values.
    user1.setIms(Arrays.asList(ims1, ims2));
    user2.setIms(ims1, ims2);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setIms(Arrays.asList(ims2, ims1));
    user2.setIms(ims1, ims2);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setIms(Arrays.asList(ims1, ims2, ims3));
    user2.setIms(ims1, ims2, ims3);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setIms(Arrays.asList(ims1, ims2, ims3));
    user2.setIms(ims1, ims3, ims2);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setIms((InstantMessagingAddress) null));

    // Null values in the varargs should be filtered out.
    user2.setIms(ims1, null, ims2, null);
    assertEquals(user2.getIms().size(), 2);

    user1.setIms(Arrays.asList(ims1, ims2));
    user2.setIms(ims1, null, null, ims2, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setPhotos}} varargs method.
   */
  @Test
  public void testPhotosVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Freya");
    user2.setUserName("Freya");
    final Photo photo1 =
            new Photo().setValue(URI.create("https://example.com/1.png"));
    final Photo photo2 =
            new Photo().setValue(URI.create("https://example.com/2.png"));
    final Photo photo3 =
            new Photo().setValue(URI.create("https://example.com/3.png"));

    // Set a single value.
    user1.setPhotos(Collections.singletonList(photo1));
    user2.setPhotos(photo1);
    assertEquals(user1, user2);

    // Set two values.
    user1.setPhotos(Arrays.asList(photo1, photo2));
    user2.setPhotos(photo1, photo2);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setPhotos(Arrays.asList(photo2, photo1));
    user2.setPhotos(photo1, photo2);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setPhotos(Arrays.asList(photo1, photo2, photo3));
    user2.setPhotos(photo1, photo2, photo3);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setPhotos(Arrays.asList(photo1, photo2, photo3));
    user2.setPhotos(photo1, photo3, photo2);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setPhotos((Photo) null));

    // Null values in the varargs should be filtered out.
    user2.setPhotos(photo1, null, photo2, null);
    assertEquals(user2.getPhotos().size(), 2);

    user1.setPhotos(Arrays.asList(photo1, photo2));
    user2.setPhotos(photo1, null, null, photo2, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setAddresses} varargs method.
   */
  @Test
  public void testAddressesVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Brok");
    user2.setUserName("Brok");
    final Address address1 =
            new Address().setStreetAddress("10001 Tyr's Temple");
    final Address address2 =
            new Address().setStreetAddress("100 Yggdrasil Plaza");
    final Address address3 =
            new Address().setStreetAddress("1234 Svartelfheim Ave.");

    // Set a single value.
    user1.setAddresses(Collections.singletonList(address1));
    user2.setAddresses(address1);
    assertEquals(user1, user2);

    // Set two values.
    user1.setAddresses(Arrays.asList(address1, address2));
    user2.setAddresses(address1, address2);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setAddresses(Arrays.asList(address2, address1));
    user2.setAddresses(address1, address2);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setAddresses(Arrays.asList(address1, address2, address3));
    user2.setAddresses(address1, address2, address3);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setAddresses(Arrays.asList(address1, address2, address3));
    user2.setAddresses(address1, address3, address2);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setAddresses((Address) null));

    // Null values in the varargs should be filtered out.
    user2.setAddresses(address1, null, address2, null);
    assertEquals(user2.getAddresses().size(), 2);

    user1.setAddresses(Arrays.asList(address1, address2));
    user2.setAddresses(address1, null, null, address2, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setGroups} varargs method.
   */
  @Test
  public void testGroupsVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Sindri");
    user2.setUserName("Sindri");
    final Group dwarves = new Group().setValue("Dwarves");
    final Group giants = new Group().setValue("Giants");
    final Group elves = new Group().setValue("Elves");

    // Set a single value.
    user1.setGroups(Collections.singletonList(dwarves));
    user2.setGroups(dwarves);
    assertEquals(user1, user2);

    // Set two values.
    user1.setGroups(Arrays.asList(dwarves, giants));
    user2.setGroups(dwarves, giants);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setGroups(Arrays.asList(giants, dwarves));
    user2.setGroups(dwarves, giants);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setGroups(Arrays.asList(dwarves, giants, elves));
    user2.setGroups(dwarves, giants, elves);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setGroups(Arrays.asList(dwarves, giants, elves));
    user2.setGroups(dwarves, elves, giants);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setGroups((Group) null));

    // Null values in the varargs should be filtered out.
    user2.setGroups(dwarves, null, elves, null);
    assertEquals(user2.getGroups().size(), 2);

    user1.setGroups(Arrays.asList(dwarves, giants));
    user2.setGroups(dwarves, null, null, giants, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setEntitlements} varargs method.
   */
  @Test
  public void testEntitlementsVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Faye");
    user2.setUserName("Faye");
    final Entitlement magic = new Entitlement().setValue("Magic");
    final Entitlement prophecy = new Entitlement().setValue("Prophetic");
    final Entitlement healing = new Entitlement().setValue("Healing");

    // Set a single value.
    user1.setEntitlements(Collections.singletonList(magic));
    user2.setEntitlements(magic);
    assertEquals(user1, user2);

    // Set two values.
    user1.setEntitlements(Arrays.asList(magic, prophecy));
    user2.setEntitlements(magic, prophecy);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setEntitlements(Arrays.asList(prophecy, magic));
    user2.setEntitlements(magic, prophecy);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setEntitlements(Arrays.asList(magic, prophecy, healing));
    user2.setEntitlements(magic, prophecy, healing);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setEntitlements(Arrays.asList(magic, prophecy, healing));
    user2.setEntitlements(magic, healing, prophecy);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setEntitlements((Entitlement) null));

    // Null values in the varargs should be filtered out.
    user2.setEntitlements(magic, null, prophecy, null);
    assertEquals(user2.getEntitlements().size(), 2);

    user1.setEntitlements(Arrays.asList(magic, prophecy));
    user2.setEntitlements(magic, null, null, prophecy, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setRoles} varargs method.
   */
  @Test
  public void testRolesVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Odin");
    user2.setUserName("Odin");
    final Role allFather = new Role().setValue("Father");
    final Role leader = new Role().setValue("Leadership");
    final Role seeker = new Role().setValue("Knowledge seeker");

    // Set a single value.
    user1.setRoles(Collections.singletonList(allFather));
    user2.setRoles(allFather);
    assertEquals(user1, user2);

    // Set two values.
    user1.setRoles(Arrays.asList(allFather, leader));
    user2.setRoles(allFather, leader);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setRoles(Arrays.asList(leader, allFather));
    user2.setRoles(allFather, leader);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setRoles(Arrays.asList(allFather, leader, seeker));
    user2.setRoles(allFather, leader, seeker);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setRoles(Arrays.asList(allFather, leader, seeker));
    user2.setRoles(allFather, seeker, leader);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setRoles((Role) null));

    // Null values in the varargs should be filtered out.
    user2.setRoles(allFather, null, seeker, null);
    assertEquals(user2.getRoles().size(), 2);

    user1.setRoles(Arrays.asList(allFather, leader));
    user2.setRoles(allFather, null, null, leader, null);
    assertEquals(user1, user2);
  }


  /**
   * Tests the {@link UserResource#setX509Certificates} varargs method.
   */
  @Test
  public void testX509CertificatesVarArgs()
  {
    final UserResource user1 = new UserResource();
    final UserResource user2 = new UserResource();
    user1.setUserName("Thor");
    user2.setUserName("Thor");
    final X509Certificate cert1 =
            new X509Certificate().setValue("cert1".getBytes());
    final X509Certificate cert2 =
            new X509Certificate().setValue("cert2".getBytes());
    final X509Certificate cert3 =
            new X509Certificate().setValue("cert3".getBytes());

    // Set a single value.
    user1.setX509Certificates(Collections.singletonList(cert1));
    user2.setX509Certificates(cert1);
    assertEquals(user1, user2);

    // Set two values.
    user1.setX509Certificates(Arrays.asList(cert1, cert2));
    user2.setX509Certificates(cert1, cert2);
    assertEquals(user1, user2);

    // Set two values in a different order.
    user1.setX509Certificates(Arrays.asList(cert2, cert1));
    user2.setX509Certificates(cert1, cert2);
    assertNotEquals(user1, user2);

    // Set three values.
    user1.setX509Certificates(Arrays.asList(cert1, cert2, cert3));
    user2.setX509Certificates(cert1, cert2, cert3);
    assertEquals(user1, user2);

    // Set three values in a different order.
    user1.setX509Certificates(Arrays.asList(cert1, cert2, cert3));
    user2.setX509Certificates(cert1, cert3, cert2);
    assertNotEquals(user1, user2);

    // The method should not accept 'null' as a parameter.
    assertThrows(NullPointerException.class,
            () -> user2.setX509Certificates((X509Certificate) null));

    // Null values in the varargs should be filtered out.
    user2.setX509Certificates(cert1, null, cert2, null);
    assertEquals(user2.getX509Certificates().size(), 2);

    user1.setX509Certificates(Arrays.asList(cert1, cert2));
    user2.setX509Certificates(cert1, null, null, cert2, null);
    assertEquals(user1, user2);
  }
}
