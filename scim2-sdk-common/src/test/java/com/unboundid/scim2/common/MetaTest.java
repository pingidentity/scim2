/*
 * Copyright 2025-2026 Ping Identity Corporation
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
 * Copyright 2025-2026 Ping Identity Corporation
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

import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Duration;
import java.util.Calendar;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for the {@link Meta} class.
 */
public class MetaTest
{
  /**
   * This method provides basic validation for Meta objects.
   */
  @Test
  public void testBasic() throws Exception
  {
    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.clear();
    calendar.set(1970, Calendar.JANUARY, 1, 11, 0);

    // The class-level Javadoc of the Meta class contains an example JSON.
    // These values are based on the ones from that example.
    Meta meta = new Meta()
        .setResourceType("User")
        .setCreatedMillis(0L)
        .setLastModified(calendar)
        .setLocation(new URI("https://example.com/v2/Users/fa1afe1"))
        .setVersion("e180ee84f0671b1");

    assertThat(meta.getResourceType()).isEqualTo("User");
    assertThat(meta.getCreated()).isNotNull();
    assertThat(meta.getCreated().getTimeInMillis()).isEqualTo(0L);
    assertThat(meta.getLastModified()).isEqualTo(calendar);
    assertThat(meta.getLocationString())
        .isEqualTo("https://example.com/v2/Users/fa1afe1");
    assertThat(meta.getVersion()).isEqualTo("e180ee84f0671b1");

    // Ensure that the JSON results in an expected form.
    String metaJson = JsonUtils.getObjectWriter().writeValueAsString(meta);
    String expected = """
        {
          "resourceType": "User",
          "created": "1970-01-01T00:00:00Z",
          "lastModified": "1970-01-01T11:00:00Z",
          "location": "https://example.com/v2/Users/fa1afe1",
          "version": "e180ee84f0671b1"
        }""";
    String expectedJson = JsonUtils.getObjectReader().readTree(expected)
        .toString();
    assertThat(metaJson).isEqualTo(expectedJson);

    // Ensure the lastModified value can also be set with millisecond values.
    long unixTimestamp = Duration.ofHours(11).toMillis();
    Meta metaMillis = new Meta()
        .setResourceType("User")
        .setCreatedMillis(0L)
        .setLastModifiedMillis(unixTimestamp)
        .setLocationString("https://example.com/v2/Users/fa1afe1")
        .setVersion("e180ee84f0671b1");
    assertThat(metaMillis).isEqualTo(meta);
  }

  /**
   * Ensures that it is possible to extend the Meta class.
   */
  @Test
  public void testExtendable()
  {
    // Define a new subclass.
    class CustomMeta extends Meta
    {
      public String customField;
    }

    // Fields defined on the subclass and parent class should be accessible.
    CustomMeta object = new CustomMeta();
    object.setResourceType("Custom");
    object.customField = "present";

    // Ensure the values were set properly.
    assertThat(object.getResourceType()).isEqualTo("Custom");
    assertThat(object.customField).isEqualTo("present");
  }

  /**
   * Tests for {@code equals()}.
   */
  @Test
  @SuppressWarnings("all")
  public void testEquals()
  {
    Meta first = new Meta()
        .setResourceType("User")
        .setCreatedMillis(0L)
        .setLastModifiedMillis(10L)
        .setLocationString("https://example.com/v2/Users/fa1afe1");
    Meta second = new Meta()
        .setResourceType("User")
        .setCreatedMillis(0L)
        .setLastModifiedMillis(10L)
        .setLocationString("https://example.com/v2/Users/fa1afe1");

    assertThat(first.equals(second)).isTrue();
    assertThat(first.equals(first)).isTrue();
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
    assertThat(first.equals(null)).isFalse();
    assertThat(first.equals(new Object())).isFalse();

    assertThat(second.setCreatedMillis(10L)).isNotEqualTo(first);
    assertThat(second.hashCode()).isNotEqualTo(first.hashCode());
    second.setCreatedMillis(first.getCreated().getTimeInMillis());

    assertThat(second.setLastModifiedMillis(1000L)).isNotEqualTo(first);
    second.setLastModified(first.getLastModified());

    assertThat(second.setLocationString(null)).isNotEqualTo(first);
    assertThat(second.getLocationString()).isNull();
    second.setLocationString(first.getLocationString());

    assertThat(second.setResourceType("Other")).isNotEqualTo(first);
    second.setResourceType("User");

    assertThat(second.setVersion("213")).isNotEqualTo(first);
    second.setVersion(null);
  }
}
