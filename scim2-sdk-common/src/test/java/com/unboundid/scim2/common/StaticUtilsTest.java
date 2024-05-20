/*
 * Copyright 2024 Ping Identity Corporation
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

import org.testng.annotations.Test;

import static com.unboundid.scim2.common.utils.StaticUtils.splitCommaSeparatedString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StaticUtilsTest
{
  /**
   * Unit test for the {@code splitCommaSeparatedString()} method.
   */
  @Test
  public void testSplitCommaSeparatedString()
  {
    // Ensure the validity of the @NotNull parameter.
    assertThatThrownBy(() -> splitCommaSeparatedString(null))
        .isInstanceOf(NullPointerException.class);

    assertThat(splitCommaSeparatedString("value"))
        .containsExactly("value");

    assertThat(splitCommaSeparatedString(" value"))
        .containsExactly("value");

    assertThat(splitCommaSeparatedString("value "))
        .containsExactly("value");

    assertThat(splitCommaSeparatedString(" value "))
        .containsExactly("value");

    assertThat(splitCommaSeparatedString("  value  "))
        .containsExactly("value");

    assertThat(splitCommaSeparatedString("\tvalue\t"))
        .containsExactly("value");

    assertThat(splitCommaSeparatedString("value1,value2"))
        .containsExactly("value1", "value2");

    assertThat(splitCommaSeparatedString(" value1,value2"))
        .containsExactly("value1", "value2");

    assertThat(splitCommaSeparatedString("value1 ,value2"))
        .containsExactly("value1", "value2");

    assertThat(splitCommaSeparatedString("value1, value2"))
        .containsExactly("value1", "value2");

    assertThat(splitCommaSeparatedString("value1,value2 "))
        .containsExactly("value1", "value2");

    assertThat(splitCommaSeparatedString(" value1 , value2 "))
        .containsExactly("value1", "value2");

    assertThat(splitCommaSeparatedString("value1 , value2 , value3"))
        .containsExactly("value1", "value2", "value3");

    assertThat(splitCommaSeparatedString("   value1 , value2 , value3  "))
        .containsExactly("value1", "value2", "value3");

    assertThat(splitCommaSeparatedString(","))
        .isEmpty();

    assertThat(splitCommaSeparatedString(",,"))
        .isEmpty();

    assertThat(splitCommaSeparatedString("   value1 ,    , value3  "))
        .containsExactly("value1", "", "value3");
  }
}
