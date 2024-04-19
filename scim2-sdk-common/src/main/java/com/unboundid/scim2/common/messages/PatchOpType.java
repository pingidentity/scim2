/*
 * Copyright 2015-2024 Ping Identity Corporation
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

package com.unboundid.scim2.common.messages;


import com.unboundid.scim2.common.annotations.NotNull;

/**
 * This enumeration defines the set of possible operation types that may
 * be used for SCIM 2 patch operations.
 */
public enum PatchOpType
{
  /**
   * The add operation type.
   */
  ADD("add"),


  /**
   * The replace operation type.
   */
  REPLACE("replace"),


  /**
   * The remove operation type.
   */
  REMOVE("remove");


  /**
   * The lower case string value for this operation type.
   */
  @NotNull
  private String stringValue;


  /**
   * Creates a new operation type with the provided string value.
   *
   * @param stringValue The lower case string value for this operation type.
   */
  PatchOpType(@NotNull final String stringValue)
  {
    this.stringValue = stringValue;
  }


  /**
   * Retrieves the lower case string value for this operation type.
   *
   * @return The lower case string value for this operation type.
   */
  @NotNull
  public String getStringValue()
  {
    return stringValue;
  }


  /**
   * Retrieves a string representation of this operation type.
   *
   * @return A string representation of this operation type.
   */
  @NotNull
  public String toString()
  {
    return getStringValue();
  }
}

