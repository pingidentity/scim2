package com.unboundid.scim2.common.exceptions;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ThrowableNotThrown")
public class PayloadTooLargeExceptionTest
{
  @Test
  public void testBasic()
  {
    ScimException e = new PayloadTooLargeException(
        "The size of the bulk operation exceeds the maxPayloadSize (1048576).");
    assertThat(e.getMessage())
        .contains("The size of the bulk operation exceeds the maxPayloadSize");
    System.out.println(e.getScimError());
  }
}
