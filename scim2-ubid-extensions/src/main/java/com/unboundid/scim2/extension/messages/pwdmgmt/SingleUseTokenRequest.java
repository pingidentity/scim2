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

import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * A single use token request object.  This can be built with the
 * builder provided (inner class).  All of the fields are immutable.
 */
@Schema(description = "This object can be used to request a single use " +
    "token (OTP)",
    id = "urn:unboundid:schemas:2.0:SingleUseTokenRequest",
    name = "SingleUseTokenRequest")
public final class SingleUseTokenRequest extends BaseScimResource
{
  /**
   * Builder for a single use token request object.
   */
  public static class SingleUseTokenRequestBuilder
  {
    private List<DeliveryMechanism> preferredDeliveryMechanisms =
        new ArrayList<DeliveryMechanism>();

    private Integer secondsUntilExpiration;

    private TokenText fullText;

    private TokenText compactText;

    private boolean deliverIfPasswordExpired;

    private boolean deliverIfAccountLocked;

    private boolean deliverIfAccountDisabled;

    private boolean deliverIfAccountExpired;

    private String messageSubject;

    /**
     * Gets the preferred delivery mechanisms list (which can be
     * used to add delivery mechanisms).
     *
     * @return the preferred delivery mechanisms list.
     */
    public List<DeliveryMechanism> getPreferredDeliveryMechanisms()
    {
      return preferredDeliveryMechanisms;
    }

    /**
     * Sets the before and after text for the single use token message for
     * delivery mechanisms that do not place significant constraints on
     * the messages delivered.
     *
     * @param fullText the before and after text.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setFullText(
        final TokenText fullText)
    {
      this.fullText = fullText;
      return this;
    }

    /**
     * Sets the before and after text for the single use token message for
     * delivery mechanisms that place significant constraints on the messages
     * delivered.
     *
     * @param compactText the before and after text.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setCompactText(
        final TokenText compactText)
    {
      this.compactText = compactText;
      return this;
    }

    /**
     * Sets whether or not the password should be delivered if the password for
     * the account is expired.
     *
     * @param deliverIfPasswordExpired whether or not the password should be
     *                                 delivered if the password for the account
     *                                 is expired.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setDeliverIfPasswordExpired(
        final boolean deliverIfPasswordExpired)
    {
      this.deliverIfPasswordExpired = deliverIfPasswordExpired;
      return this;
    }

    /**
     * Sets whether or not the password should be delivered if the account is
     * locked.
     *
     * @param deliverIfAccountLocked whether or not the password should be
     *                               delivered if the account is locked.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setDeliverIfAccountLocked(
        final boolean deliverIfAccountLocked)
    {
      this.deliverIfAccountLocked = deliverIfAccountLocked;
      return this;
    }

    /**
     * Sets whether or not the password should be delivered if the account is
     * disabled.
     *
     * @param deliverIfAccountDisabled whether or not the password should be
     *                                 delivered if the account is disabled.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setDeliverIfAccountDisabled(
        final boolean deliverIfAccountDisabled)
    {
      this.deliverIfAccountDisabled = deliverIfAccountDisabled;
      return this;
    }

    /**
     * Sets whether or not the password should be delivered if the password for
     * the account is expired.
     *
     * @param deliverIfAccountExpired whether or not the password should be
     *                                delivered if the password for the account
     *                                is expired.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setDeliverIfAccountExpired(
        final boolean deliverIfAccountExpired)
    {
      this.deliverIfAccountExpired = deliverIfAccountExpired;
      return this;
    }

    /**
     * Sets the subject of the single use token message.
     *
     * @param messageSubject subject of the single use token message.
     * @return this.
     */
    public SingleUseTokenRequestBuilder setMessageSubject(
        final String messageSubject)
    {
      this.messageSubject = messageSubject;
      return this;
    }

    /**
     * Sets the seconds until the singlue use token expires.
     *
     * @param secondsUntilExpiration the number of seconds until the
     *                               single use token expires.
     */
    public void setSecondsUntilExpiration(final Integer secondsUntilExpiration)
    {
      this.secondsUntilExpiration = secondsUntilExpiration;
    }

    /**
     * Call build to build a single use token with the parameters
     * that have been supplied to the builder.
     *
     * @return a singlue use token built from the parameters supplied
     * to the builder.
     */
    public SingleUseTokenRequest build()
    {
      SingleUseTokenRequest request = new SingleUseTokenRequest();
      request.getPreferredDeliveryMechanisms().
          addAll(this.preferredDeliveryMechanisms);
      request.secondsUntilExpiration = this.secondsUntilExpiration;
      request.fullText = this.fullText;
      request.compactText = this.compactText;
      request.deliverIfPasswordExpired = this.deliverIfPasswordExpired;
      request.deliverIfAccountLocked = this.deliverIfAccountLocked;
      request.deliverIfAccountDisabled = this.deliverIfAccountDisabled;
      request.deliverIfAccountExpired = this.deliverIfAccountExpired;
      request.messageSubject = this.messageSubject;
      request.secondsUntilExpiration = this.secondsUntilExpiration;
      return request;
    }

  }

  private SingleUseTokenRequest()
  {

  }

  @Attribute(description = "The delivery mechanisms to use in order " +
      "of preference.", multiValueClass = DeliveryMechanism.class)
  private List<DeliveryMechanism> preferredDeliveryMechanisms =
      new ArrayList<DeliveryMechanism>();

  @Attribute(description = "The maximum length of time in seconds " +
      "that the generated token should be considered valid.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private Integer secondsUntilExpiration;

  @Attribute(description = "The text that should appear in the " +
      "message delivered to the user via a delivery mechanism that " +
      "does not impose significant constraints on the message size.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private TokenText fullText;

  @Attribute(description = "The text that should appear in the " +
      "message delivered to the user via a delivery mechanism that " +
      "imposes significant constraints on the message size.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private TokenText compactText;

  @Attribute(description = "The text (if any) that should be used " +
      "as the message subject if the delivery mechanism accepts a subject.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String messageSubject;

  @Attribute(description = "Whether to generate and deliver a token " +
      "if the target user's password is expired. Default is false.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private boolean deliverIfPasswordExpired;

  @Attribute(description = "Whether to generate and deliver a token if " +
      "the target user's account is locked. Default is false.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private boolean deliverIfAccountLocked;

  @Attribute(description = "Whether to generate and deliver a token if " +
      "the target user's account is disabled administratively. Default is " +
      "false.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private boolean deliverIfAccountDisabled;

  @Attribute(description = "Whether to generate and deliver a token if " +
      "the target user's account has expired. Default is false.",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private boolean deliverIfAccountExpired;

  /**
   * Gets the preferred delivery mechanisms list.
   *
   * @return the preferred delivery mechanisms list.
   */
  public List<DeliveryMechanism> getPreferredDeliveryMechanisms()
  {
    return preferredDeliveryMechanisms;
  }

  /**
   * Gets the seconds until the token should expire.
   *
   * @return the seconds until the token should expire.
   */
  public Integer getSecondsUntilExpiration()
  {
    return secondsUntilExpiration;
  }

  /**
   * Gets the before and after text for the single use token message for
   * delivery mechanisms that do not place significant constraints on
   * the messages delivered.
   *
   * @return the before and after text.
   */
  public TokenText getFullText()
  {
    return fullText;
  }

  /**
   * Gets the before and after text for the single use token message for
   * delivery mechanisms that do place significant constraints on the
   * messages delivered.
   *
   * @return the before and after text.
   */
  public TokenText getCompactText()
  {
    return compactText;
  }

  /**
   * A boolean indicating whether or not the password should be delivered
   * if the password for the account is expired.
   *
   * @return true if the password should be delivered or false if not.
   */
  public boolean isDeliverIfPasswordExpired()
  {
    return deliverIfPasswordExpired;
  }

  /**
   * A boolean indicating whether or not the password should be delivered
   * if the account is locked.
   *
   * @return true if the password should be delivered or false if not.
   */
  public boolean isDeliverIfAccountLocked()
  {
    return deliverIfAccountLocked;
  }

  /**
   * A boolean indicating whether or not the password should be delivered
   * if the account is disabled.
   *
   * @return  true if the password should be delivered or false if not.
   */
  public boolean isDeliverIfAccountDisabled()
  {
    return deliverIfAccountDisabled;
  }

  /**
   * A boolean indicating whether or not the password should be delivered
   * if the account is expired.
   *
   * @return  true if the password should be delivered or false if not.
   */
  public boolean isDeliverIfAccountExpired()
  {
    return deliverIfAccountExpired;
  }

  /**
   * Sets the subject of the single use token message.
   *
   * @return subject of the single use token message.
   */
  public String getMessageSubject()
  {
    return messageSubject;
  }
}

