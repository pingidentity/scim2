/*
 * Copyright 2015-2020 Ping Identity Corporation
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

import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.Date;

/**
 * This class represents information about retired passwords.
 */
public class RetiredPassword
{
  @Attribute(description = "The time that the password was retired",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private Date passwordRetiredTime;

  @Attribute(description = "The expiration time of the password.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private Date passwordExpirationTime;

  /**
   * Gets the retired password expiration time.
   *
   * @return the retired password expiration.
   */
  public Date getPasswordExpirationTime()
  {
    return passwordExpirationTime;
  }

  /**
   * Sets the retired password expiration time.
   *
   * @param passwordExpirationTime the retired password expiration.
   */
  public void setPasswordExpirationTime(final Date passwordExpirationTime)
  {
    this.passwordExpirationTime = passwordExpirationTime;
  }

  /**
   * Gets the retired time.
   *
   * @return the retired time.
   */
  public Date getPasswordRetiredTime()
  {
    return passwordRetiredTime;
  }

  /**
   * Sets the retired time.
   *
   * @param passwordRetiredTime the retired time.
   */
  public void setPasswordRetiredTime(final Date passwordRetiredTime)
  {
    this.passwordRetiredTime = passwordRetiredTime;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }

    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    RetiredPassword that = (RetiredPassword) o;

    if (passwordRetiredTime != null ?
        !passwordRetiredTime.equals(that.passwordRetiredTime) :
        that.passwordRetiredTime != null)
    {
      return false;
    }

    return !(passwordExpirationTime != null ?
        !passwordExpirationTime.equals(that.passwordExpirationTime) :
        that.passwordExpirationTime != null);

  }

  @Override
  public int hashCode()
  {
    int result =
        passwordRetiredTime != null ? passwordRetiredTime.hashCode() : 0;
    result = 31 * result + (passwordExpirationTime != null ?
        passwordExpirationTime.hashCode() : 0);
    return result;
  }
}
