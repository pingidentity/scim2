/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

package com.unboundid.scim2.extension.messages.consent;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ScopeTest
{
  /**
   * Tests serialization of Scope objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    String name = "testName";
    String description = "testDescription";
    String consent = Scope.CONSENT_GRANTED;

    ObjectNode objectNode = JsonUtils.getJsonNodeFactory().objectNode();
    objectNode.put("name", name);
    objectNode.put("description", description);
    objectNode.put("consent", consent);

    Scope scope1 = JsonUtils.getObjectReader().forType(Scope.class).readValue(
        objectNode.toString());
    Assert.assertEquals(name, scope1.getName());
    Assert.assertEquals(description, scope1.getDescription());
    Assert.assertEquals(consent, scope1.getConsent());

    Scope scope2 =
        JsonUtils.getObjectReader().forType(Scope.class).readValue(
            JsonUtils.getObjectWriter().writeValueAsString(scope1));
    Assert.assertEquals(scope1, scope2);
  }
}
