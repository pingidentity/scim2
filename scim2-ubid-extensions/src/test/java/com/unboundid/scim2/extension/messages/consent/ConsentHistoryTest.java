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

package com.unboundid.scim2.extension.messages.consent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.extension.messages.JsonObjectStringBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class ConsentHistoryTest
{
  private final ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();

  /**
   * Tests serialization of ConsentHistory objects.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void testSerialization() throws Exception
  {
    Application application = new Application.Builder().
        setName("appName").setDescription("appDesc").
        setEmailAddress("email@address.com").
        setIconUrl(new URL("http://localhost:12345/app/icon")).
        setIconUrl(new URL("http://localhost:12345/app")).build();

    Scope scope1 = new Scope.Builder().setConsent(Scope.CONSENT_GRANTED).
        setName("name1").setDescription("description1").build();
    Scope scope2 = new Scope.Builder().setConsent(Scope.CONSENT_DENIED).
        setName("name2").setDescription("description2").build();
    Scope scope3 = new Scope.Builder().setConsent(Scope.CONSENT_REVOKED).
        setName("name3").setDescription("description3").build();

    String meta_created = "2015-07-04T00:00:00Z";
    JsonObjectStringBuilder meta_jsob = new JsonObjectStringBuilder();
    meta_jsob.appendProperty("lastModified", meta_created);
    Meta meta = mapper.readValue(meta_jsob.toString(), Meta.class);
    String id = "ConsentHistoryId";


    JsonObjectStringBuilder jsob = new JsonObjectStringBuilder();
    jsob.appendProperty("application", mapper, application);
    jsob.appendListProperty("scope", mapper, scope1, scope2, scope3);
    jsob.appendProperty("meta", mapper, meta);
    jsob.appendProperty("id", id);

    ObjectMapper mapper = SchemaUtils.createSCIMCompatibleMapper();
    ConsentHistory consentHistory1 = mapper.readValue(jsob.toString(),
        ConsentHistory.class);
    Assert.assertEquals(application, consentHistory1.getApplication());
    Assert.assertEquals(id, consentHistory1.getId());

    for(Scope scope : consentHistory1.getScopes())
    {
      if(scope.getName().equals(scope1.getName()))
      {
        Assert.assertEquals(scope1, scope);
      }
      else if(scope.getName().equals(scope2.getName()))
      {
        Assert.assertEquals(scope2, scope);
      }
      else if(scope.getName().equals(scope3.getName()))
      {
        Assert.assertEquals(scope3, scope);
      }
      else
      {
        Assert.fail("Unknown scope found - name = " + scope.getName());
      }
    }

    ConsentHistory consentHistory2 =
        mapper.readValue(mapper.writeValueAsString(consentHistory1),
            ConsentHistory.class);
    Assert.assertEquals(consentHistory1, consentHistory2);
  }
}
