/*
 * Copyright 2016-2017 UnboundID Corp.
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
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.extension.messages.consent.OAuth2Client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Schema(description = "Session objects",
    id = "urn:pingidentity:scim:api:messages:2.0:session",
    name = "Session")
public class Session extends BaseScimResource
{
  @Attribute(description = "Details about the authentication methods " +
      "successfully used during the last login event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = String.class)
  private List<String> lastLoginMethods;

  @Attribute(description = "Details about the authentication methods " +
      "successfully used during the last second factor event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = String.class)
  private List<String> lastSecondFactorMethods;

  @Attribute(description = "The last time of a successful login event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Calendar lastLogin;

  @Attribute(description = "The last time of a successful second factor event.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Calendar lastSecondFactor;

  @Attribute(description = "The IP address of the user agent the was " +
      "used to perform the authentication.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String ipAddress;

  @Attribute(description = "The user agent string presented by the user " +
      "agent that was used to perform the authentication.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String userAgentString;

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
    this.clients = new ArrayList<OAuth2Client>();
    this.lastLoginMethods = new ArrayList<String>();
    this.lastSecondFactorMethods = new ArrayList<String>();
  }

  /**
   * Gets Details about the authentication methods successfully used
   * during the last login event. To add login methods, get the list, and
   * call the add() method on the list.
   *
   * @return details about the authentication methods successfully used
   * during the last login event.
   */
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
  public List<String> getLastSecondFactorMethods()
  {
    return lastSecondFactorMethods;
  }

  /**
   * Gets the last time of a successful login event.
   *
   * @return the last time of a successful login event.
   */
  public Calendar getLastLogin()
  {
    return lastLogin;
  }

  /**
   * Sets the last time of a successful login event.
   *
   * @param lastLogin the last time of a successful login event.
   */
  public void setLastLogin(final Calendar lastLogin)
  {
    this.lastLogin = lastLogin;
  }

  /**
   * Gets the last time of a successful second factor event.
   *
   * @return the last time of a successful second factor event.
   */
  public Calendar getLastSecondFactor()
  {
    return lastSecondFactor;
  }

  /**
   * Sets the last time of a successful second factor event.
   *
   * @param lastSecondFactor the last time of a successful second factor event.
   */
  public void setLastSecondFactor(final Calendar lastSecondFactor)
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
  public void setIpAddress(final String ipAddress)
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
  public void setUserAgentString(final String userAgentString)
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
  public List<OAuth2Client> getClients()
  {
    return clients;
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
    if (!super.equals(o))
    {
      return false;
    }

    Session session = (Session) o;

    if (lastLoginMethods != null ?
        !lastLoginMethods.equals(session.lastLoginMethods)
        : session.lastLoginMethods != null)
    {
      return false;
    }
    if (lastSecondFactorMethods != null
        ? !lastSecondFactorMethods.equals(session.lastSecondFactorMethods)
        : session.lastSecondFactorMethods != null)
    {
      return false;
    }
    if (lastLogin != null ? !lastLogin.equals(session.lastLogin)
        : session.lastLogin != null)
    {
      return false;
    }
    if (lastSecondFactor != null
        ? !lastSecondFactor.equals(session.lastSecondFactor)
        : session.lastSecondFactor != null)
    {
      return false;
    }
    if (ipAddress != null ? !ipAddress.equals(session.ipAddress)
        : session.ipAddress != null)
    {
      return false;
    }
    if (userAgentString != null ?
        !userAgentString.equals(session.userAgentString)
        : session.userAgentString != null)
    {
      return false;
    }
    return clients != null ? clients.equals(session.clients)
        : session.clients == null;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (lastLoginMethods != null
        ? lastLoginMethods.hashCode() : 0);
    result = 31 * result + (lastSecondFactorMethods != null
        ? lastSecondFactorMethods.hashCode() : 0);
    result = 31 * result + (lastLogin != null ? lastLogin.hashCode() : 0);
    result = 31 * result + (lastSecondFactor != null
        ? lastSecondFactor.hashCode() : 0);
    result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
    result = 31 * result + (userAgentString != null
        ? userAgentString.hashCode() : 0);
    result = 31 * result + (clients != null ? clients.hashCode() : 0);
    return result;
  }
}

