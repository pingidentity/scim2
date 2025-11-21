/*
 * Copyright 2024-2025 Ping Identity Corporation
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
 * Copyright 2024-2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.utils.StaticUtils;
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
    //noinspection DataFlowIssue
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

  /**
   * Basic test for {@link StaticUtils#toLowerCase} that ensures it can accept
   * {@code null} inputs.
   */
  @Test
  public void testToLowercase()
  {
    // A null input should not cause an exception.
    //
    // noinspection ConstantValue
    assertThat(StaticUtils.toLowerCase(null)).isNull();

    // These test cases are not extensive since the method is a wrapper around
    // String#toLowerCase().
    assertThat(StaticUtils.toLowerCase("lowerstring")).isEqualTo("lowerstring");
    assertThat(StaticUtils.toLowerCase("sPoNgEbOb tExT"))
        .isEqualTo("spongebob text");
  }
}
