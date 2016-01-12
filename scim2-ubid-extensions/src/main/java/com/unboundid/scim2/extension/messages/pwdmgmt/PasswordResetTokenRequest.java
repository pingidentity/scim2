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
 * Class used to request password token reset via the SCIM
 * REST api.
 */
@Schema(description = "Request used when invoking the SCIM reset "
    + "password token REST method.",
    id = "urn:unboundid:schemas:2.0:PasswordResetTokenRequest",
    name = "PasswordResetTokenRequest")
public final class PasswordResetTokenRequest extends BaseScimResource
{
  @Attribute(description = "The delivery mechanisms to use in order " +
      "of preference.", multiValueClass = DeliveryMechanism.class)
  private List<DeliveryMechanism> preferredDeliveryMechanisms =
      new ArrayList<DeliveryMechanism>();

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

  /**
   * Builder for a password reset token request object.
   */
  public static class PasswordResetTokenRequestBuilder
  {
    private List<DeliveryMechanism> preferredDeliveryMechanisms =
        new ArrayList<DeliveryMechanism>();

    private TokenText fullText;

    private TokenText compactText;

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
     * Sets the before and after text for the password reset token message for
     * delivery mechanisms that do not place significant constraints on
     * the messages delivered.
     *
     * @param fullText the before and after text.
     * @return this.
     */
    public PasswordResetTokenRequestBuilder setFullText(
        final TokenText fullText)
    {
      this.fullText = fullText;
      return this;
    }

    /**
     * Sets the before and after text for the password reset token message for
     * delivery mechanisms that place significant constraints on the messages
     * delivered.
     *
     * @param compactText the before and after text.
     * @return this.
     */
    public PasswordResetTokenRequestBuilder setCompactText(
        final TokenText compactText)
    {
      this.compactText = compactText;
      return this;
    }

    /**
     * Sets the subject of the password reset token message.
     *
     * @param messageSubject subject of the password reset token message.
     * @return this.
     */
    public PasswordResetTokenRequestBuilder setMessageSubject(
        final String messageSubject)
    {
      this.messageSubject = messageSubject;
      return this;
    }

    /**
     * Call build to build a password reset token request with the parameters
     * that have been supplied to the builder.
     *
     * @return a password reset token built from the parameters supplied
     * to the builder.
     */
    public PasswordResetTokenRequest build()
    {
      PasswordResetTokenRequest request = new PasswordResetTokenRequest();
      request.getPreferredDeliveryMechanisms().
          addAll(this.preferredDeliveryMechanisms);
      request.fullText = this.fullText;
      request.compactText = this.compactText;
      request.messageSubject = this.messageSubject;
      return request;
    }
  }

  // Use the builder instead of constructing directly.
  private PasswordResetTokenRequest()
  {

  }

    /**
   * Gets the preferred delivery mechanisms to use (in order of preference).
   *
   * @return the preferred delivery mechanisms to use (in order of preference).
   */
  public List<DeliveryMechanism> getPreferredDeliveryMechanisms()
  {
    return preferredDeliveryMechanisms;
  }

  /**
   * Gets the before and after text for the password reset token message for
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
   * Gets the before and after text for the password reset token message for
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
   * Gets the subject of the password reset token message.
   *
   * @return subject of the password reset token message.
   */
  public String getMessageSubject()
  {
    return messageSubject;
  }
}
