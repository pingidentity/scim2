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

package com.unboundid.scim2.common;

import com.unboundid.scim2.common.types.Meta;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for the {@link Meta} class.
 */
public class MetaTest
{
  /**
   * Tests that a Meta object can be created with the builder pattern.
   */
  @Test
  public void testBuilderPattern() throws Exception
  {
    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(1970, Calendar.JANUARY, 1, 11, 0);

    // The class-level Javadoc of the Meta class contains an example JSON.
    // These values are based on the ones from that example.
    Meta meta = new Meta()
        .setResourceType("User")
        .setCreated(calendar)
        .setLastModified(calendar)
        .setLocation(new URI("https://example.com/v2/Users/fa1afe1"))
        .setVersion("W/\"e180ee84f0671b1\"");

    assertThat(meta.getResourceType()).isEqualTo("User");
    assertThat(meta.getCreated()).isEqualTo(calendar);
    assertThat(meta.getLastModified()).isEqualTo(calendar);
    assertThat(meta.getLocation()).isNotNull();
    assertThat(meta.getLocation().toString())
        .isEqualTo("https://example.com/v2/Users/fa1afe1");
    assertThat(meta.getVersion()).isEqualTo("W/\"e180ee84f0671b1\"");
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
}
