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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests serialization and deserialization of AuthenticationScheme objects.
 */
@Test
public class AuthenticationSchemeTest
{
  private static final String AS_NAME = "name";
  private static final String AS_DESC = "description";
  private static final String AS_SPEC_URI = "http://localhost/specUri";
  private static final String AS_DOC_URI = "http://localhost/documentationUri";
  private static final String AS_TYPE = "type";
  private static final String AS_PRIMARY = "true";

  /**
   * Test the normal serialization case.
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerializationAndDeserialization() throws Exception
  {
    String authSchemeString = "{\n" +
        "      \"name\" : \"" + AS_NAME + "\",\n" +
        "      \"description\" : \"" + AS_DESC + "\",\n" +
        "      \"specUri\" : \"" + AS_SPEC_URI + "\",\n" +
        "      \"documentationUri\" : \"" + AS_DOC_URI + "\",\n" +
        "      \"type\" : \"" + AS_TYPE + "\",\n" +
        "      \"primary\" : " + AS_PRIMARY + "\n" +
        "    }";

    AuthenticationScheme as = JsonUtils.getObjectReader().forType(
        AuthenticationScheme.class).readValue(authSchemeString);
    Assert.assertEquals(as.getName(), AS_NAME);
    Assert.assertEquals(as.getDescription(), AS_DESC);
    Assert.assertEquals(as.getSpecUri().toString(), AS_SPEC_URI);
    Assert.assertEquals(as.getDocumentationUri().toString(), AS_DOC_URI);
    Assert.assertEquals(as.getType(), AS_TYPE);
    Assert.assertEquals(as.isPrimary(), true);

    String serializedString =
        JsonUtils.getObjectWriter().writeValueAsString(as);
    AuthenticationScheme deserializedScheme = JsonUtils.getObjectReader().
        forType(AuthenticationScheme.class).readValue(serializedString);
    Assert.assertEquals(as, deserializedScheme);
  }
}
