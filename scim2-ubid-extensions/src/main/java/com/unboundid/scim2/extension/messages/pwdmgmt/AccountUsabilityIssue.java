/*
 * Copyright 2015-2016 UnboundID Corp.
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

/**
 * Model class for an account usability issues.
 */
public class AccountUsabilityIssue
{
  private final String name;
  private final String message;

  /**
   * Create a new account usability issue instance.
   *
   * @param name The name for this account usability issue.
   * @param message The human-readable message that provides specific details
   *                about this account usability issue.
   */
  public AccountUsabilityIssue(final String name, final String message)
  {
    this.name = name;
    this.message = message;
  }

  /**
   * Retrieves the name for this account usability issue.
   *
   * @return  The name for this account usability issue.
   */
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
  public String getMessage()
  {
    return message;
  }
}
