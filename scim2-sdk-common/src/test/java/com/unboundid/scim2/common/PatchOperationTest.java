/*
 * Copyright 2023 Ping Identity Corporation
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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.messages.PatchOpType;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertThrows;


/**
 * Tests for the {@link PatchOperation} class.
 */
public class PatchOperationTest
{
  private static final ArrayNode EMPTY_ARRAY =
          JsonUtils.getJsonNodeFactory().arrayNode();


  /**
   * Validates empty values passed into the PatchOperation constructors. Empty
   * arrays are permitted (e.g., a 'replace' operation for an existing
   * multi-valued attribute).
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testEmptyValues() throws Exception
  {
    // Null or empty values should not be permitted.
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.add("attr", null));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.replace("attr", (String) null));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.ADD, "attr", null));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.REPLACE, "attr", null));

    ObjectNode emptyNode = JsonUtils.getJsonNodeFactory().objectNode();
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.add("attr", emptyNode));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.replace("attr", emptyNode));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.ADD, "attr", emptyNode));
    assertThrows(IllegalArgumentException.class,
            () -> PatchOperation.create(PatchOpType.REPLACE, "attr", emptyNode));


    // Empty array values should be accepted.
    PatchOperation.add("myArray", EMPTY_ARRAY);
    PatchOperation.replace("myArray", EMPTY_ARRAY);
    PatchOperation.create(PatchOpType.ADD, "myArray", EMPTY_ARRAY);
    PatchOperation.create(PatchOpType.REPLACE, "myArray", EMPTY_ARRAY);
  }
}
