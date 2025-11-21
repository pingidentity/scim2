/*
 * Copyright 2023-2025 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class PatchRequestTest
{
  /**
   * Test the {@link PatchRequest} constructors.
   *
   * @throws Exception  If an unexpected error occurs.
   */
  @Test
  public void testConstructors() throws Exception
  {
    PatchRequest listRequest = new PatchRequest(Collections.singletonList(
            PatchOperation.add("nickName", TextNode.valueOf("G"))
    ));
    PatchRequest argsRequest = new PatchRequest(
            PatchOperation.add("nickName", TextNode.valueOf("G"))
    );
    assertEquals(listRequest, argsRequest);

    listRequest = new PatchRequest(Arrays.asList(
            PatchOperation.add("displayName", TextNode.valueOf("myName")),
            PatchOperation.replace("displayName", TextNode.valueOf("yourName"))
    ));
    argsRequest = new PatchRequest(
            PatchOperation.add("displayName", TextNode.valueOf("myName")),
            PatchOperation.replace("displayName", TextNode.valueOf("yourName"))
    );
    assertEquals(listRequest, argsRequest);

    // Providing a null operations list should not be permitted.
    assertThrows(NullPointerException.class, () -> new PatchRequest(null));

    // The first parameter of the method should not accept null.
    assertThrows(NullPointerException.class,
            () -> new PatchRequest(null, PatchOperation.remove("invalid")));

    // Null arguments in the varargs method should be ignored.
    PatchRequest singleOperation = new PatchRequest(
            PatchOperation.remove("password"), null, null
    );
    assertEquals(singleOperation.getOperations().size(), 1);
  }
}
