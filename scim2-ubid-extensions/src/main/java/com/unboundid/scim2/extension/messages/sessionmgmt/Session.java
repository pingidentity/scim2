/*
 * Copyright 2016-2025 Ping Identity Corporation
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

package com.unboundid.scim2.extension.messages.sessionmgmt;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.extension.messages.consent.OAuth2Client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a login/authentication session.
 */
@Schema(description = "Session objects",
    id = "urn:pingidentity:scim:api:messages:2.0:session",
    name = "Session")
public class Session extends BaseScimResource
{
  @NotNull
  @Attribute(description = "Details about the authentication methods " +
      "successfully used during the last login event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = String.class)
  private List<String> lastLoginMethods;

  @NotNull
  @Attribute(description = "Details about the authentication methods " +
      "successfully used during the last second factor event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = String.class)
  private List<String> lastSecondFactorMethods;

  @Nullable
  @Attribute(description = "The last time of a successful login event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Calendar lastLogin;

  @Nullable
  @Attribute(description = "The last time of a successful second factor event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Calendar lastSecondFactor;

  @Nullable
  @Attribute(description = "The IP address of the user agent the was " +
      "used to perform the authentication.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String ipAddress;

  @Nullable
  @Attribute(description = "The user agent string presented by the user " +
      "agent that was used to perform the authentication.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String userAgentString;

  @NotNull
  @Attribute(description = "A list of all clients with access tokens or " +
      "offline tokens obtained using this session.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = OAuth2Client.class)
  private List<OAuth2Client> clients;

  /**
   * Constructor for session objects.
   */
  public Session()
  {
    this.clients = new ArrayList<>();
    this.lastLoginMethods = new ArrayList<>();
    this.lastSecondFactorMethods = new ArrayList<>();
  }

  /**
   * Gets Details about the authentication methods successfully used
   * during the last login event. To add login methods, get the list, and
   * call the add() method on the list.
   *
   * @return details about the authentication methods successfully used
   * during the last login event.
   */
  @NotNull
  public List<String> getLastLoginMethods()
  {
    return lastLoginMethods;
  }

  /**
   * Gets Details about the authentication methods successfully used
   * during the last second factor event.  To add second factor methods,
   * get the list, and call the add() method on the list.
   *
   * @return details about the authentication methods successfully used
   * during the last second factor event.
   */
  @NotNull
  public List<String> getLastSecondFactorMethods()
  {
    return lastSecondFactorMethods;
  }

  /**
   * Gets the last time of a successful login event.
   *
   * @return the last time of a successful login event.
   */
  @Nullable
  public Calendar getLastLogin()
  {
    return lastLogin;
  }

  /**
   * Sets the last time of a successful login event.
   *
   * @param lastLogin the last time of a successful login event.
   */
  public void setLastLogin(@Nullable final Calendar lastLogin)
  {
    this.lastLogin = lastLogin;
  }

  /**
   * Gets the last time of a successful second factor event.
   *
   * @return the last time of a successful second factor event.
   */
  @Nullable
  public Calendar getLastSecondFactor()
  {
    return lastSecondFactor;
  }

  /**
   * Sets the last time of a successful second factor event.
   *
   * @param lastSecondFactor the last time of a successful second factor event.
   */
  public void setLastSecondFactor(@Nullable final Calendar lastSecondFactor)
  {
    this.lastSecondFactor = lastSecondFactor;
  }

  /**
   * Gets the IP address of the user agent the was used to perform
   * the authentication.
   *
   * @return the IP address of the user agent the was used to perform
   * the authentication.
   */
  @Nullable
  public String getIpAddress()
  {
    return ipAddress;
  }

  /**
   * Sets the IP address of the user agent the was used to perform
   * the authentication.
   *
   * @param ipAddress the IP address of the user agent the was used to perform
   * the authentication.
   */
  public void setIpAddress(@Nullable final String ipAddress)
  {
    this.ipAddress = ipAddress;
  }

  /**
   * Gets the user agent string presented by the user agent that was used
   * to perform the authentication.
   *
   * @return the user agent string presented by the user agent that was used
   * to perform the authentication.
   */
  @Nullable
  public String getUserAgentString()
  {
    return userAgentString;
  }

  /**
   * Sets the user agent string presented by the user agent that was used
   * to perform the authentication.
   *
   * @param userAgentString the user agent string presented by the user
   * agent that was used to perform the authentication.
   */
  public void setUserAgentString(@Nullable final String userAgentString)
  {
    this.userAgentString = userAgentString;
  }

  /**
   * Gets list of all clients with access tokens or offline tokens
   * obtained using this session.  To add clients, get the list, and
   * call the add() method on the list.
   *
   * @return a list of all clients with access tokens or offline tokens
   * obtained using this session.
   */
  @NotNull
  public List<OAuth2Client> getClients()
  {
    return clients;
  }

  /**
   * Indicates whether the provided object is equal to this session.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this session, or
   *            {@code false} if not.
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
    if (!super.equals(o))
    {
      return false;
    }

    Session session = (Session) o;
    if (!Objects.equals(lastLoginMethods, session.lastLoginMethods))
    {
      return false;
    }
    if (!Objects.equals(lastSecondFactorMethods,
        session.lastSecondFactorMethods))
    {
      return false;
    }
    if (!Objects.equals(lastLogin, session.lastLogin))
    {
      return false;
    }
    if (!Objects.equals(lastSecondFactor, session.lastSecondFactor))
    {
      return false;
    }
    if (!Objects.equals(ipAddress, session.ipAddress))
    {
      return false;
    }
    if (!Objects.equals(userAgentString, session.userAgentString))
    {
      return false;
    }
    return Objects.equals(clients, session.clients);
  }

  /**
   * Retrieves a hash code for this session.
   *
   * @return  A hash code for this session.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(lastLoginMethods, lastSecondFactorMethods, lastLogin,
        lastSecondFactor, ipAddress, userAgentString, clients);
  }
}

