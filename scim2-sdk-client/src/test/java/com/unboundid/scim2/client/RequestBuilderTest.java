/*
 * Copyright 2025 Ping Identity Corporation
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

package com.unboundid.scim2.client;

import com.unboundid.scim2.client.requests.CreateRequestBuilder;
import com.unboundid.scim2.client.requests.DeleteRequestBuilder;
import com.unboundid.scim2.client.requests.ReplaceRequestBuilder;
import com.unboundid.scim2.client.requests.SearchRequestBuilder;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.types.UserResource;
import jakarta.ws.rs.client.WebTarget;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RequestBuilderTest
{
  /**
   * Ensures that builder classes are not {@code final} and can be extended.
   */
  @Test
  public void testExtendable()
  {
    final WebTarget target = null;
    final UserResource resource = new UserResource();

    // Test CreateRequestBuilder.
    class CustomCreate<T extends ScimResource>
        extends CreateRequestBuilder<T>
    {
      public String otherField;

      public CustomCreate(WebTarget target, T resource)
      {
        super(target, resource);
      }
    }
    var createInstance = new CustomCreate<>(target, resource);
    createInstance.otherField = "present";
    assertThat(createInstance.otherField).isEqualTo("present");

    // Test ReplaceRequestBuilder.
    class CustomReplace<T extends ScimResource>
        extends ReplaceRequestBuilder<T>
    {
      public String otherField;

      public CustomReplace(WebTarget target, T resource)
      {
        super(target, resource);
      }
    }
    var replaceInstance = new CustomReplace<>(target, resource);
    replaceInstance.otherField = "present";
    assertThat(replaceInstance.otherField).isEqualTo("present");

    // Test DeleteRequestBuilder.
    class CustomDelete extends DeleteRequestBuilder
    {
      public String otherField;

      public CustomDelete(WebTarget target)
      {
        super(target);
      }
    }
    var deleteInstance = new CustomDelete(target);
    deleteInstance.otherField = "present";
    assertThat(deleteInstance.otherField).isEqualTo("present");

    // Test SearchRequestBuilder.
    class CustomSearch extends SearchRequestBuilder
    {
      public String otherField;

      public CustomSearch(WebTarget target)
      {
        super(target);
      }
    }
    var searchInstance = new CustomSearch(target);
    searchInstance.otherField = "present";
    assertThat(searchInstance.otherField).isEqualTo("present");
  }
}
