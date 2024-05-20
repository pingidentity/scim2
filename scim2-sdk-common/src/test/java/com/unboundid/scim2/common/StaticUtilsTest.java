package com.unboundid.scim2.common;

import org.testng.annotations.Test;

import static com.unboundid.scim2.common.utils.StaticUtils.splitCommaSeparatedString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StaticUtilsTest
{
  @Test
  public void testSplitCommaSeparatedString()
  {
    // Ensure that the @NotNull parameter is not permitted.
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
