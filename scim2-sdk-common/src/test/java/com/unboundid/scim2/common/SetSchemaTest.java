/*
 * Copyright 2023-2025 Ping Identity Corporation
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


import com.unboundid.scim2.common.types.UserResource;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;


/**
 * Ensures that setting schema URNs on a SCIM object behaves identically,
 * regardless of the version of {@link ScimResource#setSchemaUrns} that is used.
 */
public class SetSchemaTest
{
  /**
   * Test {@link BaseScimResource#setSchemaUrns}.
   */
  @Test
  public void testBaseScimResourceSchemaUrns()
  {
    BaseScimResource scimObject = new UserResource();
    BaseScimResource scimObject2 = new UserResource();

    // Set a single value.
    List<String> singleUrn =
            Collections.singletonList("urn:pingidentity:specialObject");
    scimObject.setSchemaUrns(singleUrn);
    scimObject2.setSchemaUrns("urn:pingidentity:specialObject");
    assertEquals(scimObject, scimObject2);

    // Set two values.
    List<String> schemaArray = Arrays.asList(
            "urn:pingidentity:proprietaryObject",
            "urn:pingidentity:specialObject"
    );
    scimObject.setSchemaUrns(schemaArray);
    scimObject2.setSchemaUrns("urn:pingidentity:proprietaryObject",
            "urn:pingidentity:specialObject"
    );
    assertEquals(scimObject, scimObject2);

    // On a BaseScimResource, the objects should be considered equivalent
    // regardless of the order of the parameters.
    scimObject2.setSchemaUrns("urn:pingidentity:specialObject",
            "urn:pingidentity:proprietaryObject");
    assertEquals(scimObject, scimObject2);

    // Setting schema URNs to null should not be allowed.
    assertThrows(NullPointerException.class, () -> scimObject.setSchemaUrns(null));

    // The first parameter of the method should not accept null.
    assertThrows(NullPointerException.class,
            () -> scimObject.setSchemaUrns(null, "urn:pingidentity:specialObject"));

    // Null arguments in the varargs method should be ignored.
    scimObject.setSchemaUrns(
            "urn:pingidentity:proprietaryObject", null, null);
    assertEquals(scimObject.getSchemaUrns().size(), 1);
  }


  /**
   * Test {@link GenericScimResource#setSchemaUrns}.
   */
  @Test
  public void testGenericScimResourceSchemaUrns()
  {
    GenericScimResource genericObject = new GenericScimResource();
    GenericScimResource genericObject2 = new GenericScimResource();

    // Set a single value.
    List<String> singleUrn =
            Collections.singletonList("urn:pingidentity:specialObject");
    genericObject.setSchemaUrns(singleUrn);
    genericObject2.setSchemaUrns("urn:pingidentity:specialObject");
    assertEquals(genericObject, genericObject2);

    // Set two values.
    List<String> twoUrns = Arrays.asList(
            "urn:pingidentity:proprietaryObject",
            "urn:pingidentity:specialObject"
    );
    genericObject.setSchemaUrns(twoUrns);
    genericObject2.setSchemaUrns("urn:pingidentity:proprietaryObject",
            "urn:pingidentity:specialObject"
    );
    assertEquals(genericObject, genericObject2);

    // The first parameter of the method should not accept null.
    assertThrows(NullPointerException.class,
            () -> genericObject.setSchemaUrns(null, "urn:pingidentity:specialObject"));

    // Null arguments in the varargs method should be ignored.
    genericObject.setSchemaUrns(
            "urn:pingidentity:proprietaryObject", null, null);
    assertEquals(genericObject.getSchemaUrns().size(), 1);
  }


  /**
   * Ensure that the requested order is preserved when an ordered Collection is
   * used to initialize the schema URN set of a BaseScimResource.
   */
  @Test
  public void testSchemaUrnOrder()
  {
    final String urn0 = "urn:ietf:params:scim:schemas:core:2.0:User";
    final String urn1 = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";
    final String urn2 = "urn:pingidentity:proprietaryObject";
    final String urn3 = "urn:pingidentity:specialObject";
    final String urn4 = "urn:pingidentity:veryParticularObject";
    final String urn5 = "urn:pingidentity:aVeryVeryParticularObject";
    final List<String> urns = Arrays.asList(urn0, urn1, urn2, urn3, urn4, urn5);
    BaseScimResource resource = new UserResource();
    resource.setSchemaUrns(urns);

    String[] resourceSchemaUrns = resource.getSchemaUrns().toArray(new String[0]);
    assertEquals(resourceSchemaUrns.length, 6);

    assertEquals(resourceSchemaUrns[0], urn0);
    assertEquals(resourceSchemaUrns[1], urn1);
    assertEquals(resourceSchemaUrns[2], urn2);
    assertEquals(resourceSchemaUrns[3], urn3);
    assertEquals(resourceSchemaUrns[4], urn4);
    assertEquals(resourceSchemaUrns[5], urn5);
  }
}
