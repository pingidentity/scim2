package com.unboundid.scim2.common.messages;

import com.unboundid.scim2.common.annotations.NotNull;

/**
 * An enum representing possible bulk operation types. Note that
 * {@code HTTP GET} methods are not permitted within bulk requests, as bulk
 * requests are designed for write operations. Thus, there is not an enum
 * defined for that here.
 */
public enum BulkOpType
{
  POST("POST"),
  PUT("PUT"),
  PATCH("PATCH"),
  DELETE("DELETE"),
  ;

  @NotNull
  private final String stringValue;

  BulkOpType(@NotNull String stringValue)
  {
    this.stringValue = stringValue;
  }

  @Override
  @NotNull
  public String toString()
  {
    return stringValue;
  }
}
