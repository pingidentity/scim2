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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Nullable;

/**
 * Model class for an account usability issues.
 */
public class AccountUsabilityIssue
{
  @Nullable
  private final String name;

  @Nullable
  private final String message;

  /**
   * Create a new account usability issue instance.
   *
   * @param name The name for this account usability issue.
   * @param message The human-readable message that provides specific details
   *                about this account usability issue.
   */
  @JsonCreator
  public AccountUsabilityIssue(
      @Nullable @JsonProperty(value = "name") final String name,
      @Nullable @JsonProperty(value = "message") final String message)
  {
    this.name = name;
    this.message = message;
  }

  /**
   * Retrieves the name for this account usability issue.
   *
   * @return  The name for this account usability issue.
   */
  @Nullable
  public String getName()
  {
    return name;
  }

  /**
   * Retrieves a human-readable message that provides specific details about
   * this account usability issue.
   *
   * @return  A human-readable message that provides specific details about this
   *          account usability issue, or {@code null} if no message is
   *          available.
   */
  @Nullable
  public String getMessage()
  {
    return message;
  }

  /**
   * Indicates whether the provided object is equal to this account usability
   * issue.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this account
   *            usability issue, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    AccountUsabilityIssue that = (AccountUsabilityIssue) o;

    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    return !(message != null ? !message.equals(that.message) :
        that.message != null);

  }

  /**
   * Retrieves a hash code for this account usability issue.
   *
   * @return  A hash code for this account usability issue.
   */
  @Override
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (message != null ? message.hashCode() : 0);
    return result;
  }
}
