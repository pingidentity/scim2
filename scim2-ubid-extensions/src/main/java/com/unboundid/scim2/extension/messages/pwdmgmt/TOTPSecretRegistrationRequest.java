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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.net.URI;

/**
 * This class provides the request and response model for the TOTP shared secret
 * SCIM sub-resource handler that may be used to register a new randomly
 * generated TOTP shared secret for a user.
 * <br><br>
 * To generate a new TOTP shared secret, create a new, empty request using POST.
 * The response will include the generated shared secret, the corresponding
 * otpauth URI which may be used to generate a QR code to be scanned by the
 * TOTP device. The shared secret is not yet saved to the user resource at this
 * time. To prove that the generated shared secret is correctly added to the
 * TOTP device, a valid TOTP generated by the TOTP device must then be sent in
 * a PUT request along with the internal verify state value received during the
 * previous POST response. If the TOTP is successfully validated, the generated
 * shared secret will then be saved to the user resource and ready for use.
 */
@Schema(description = "TOTP Secret Registration Request",
    id = "urn:pingidentity:scim:api:messages:2.0:TOTPSecretRegistrationRequest",
    name = "TOTPSecretRegistrationRequest")
public class TOTPSecretRegistrationRequest extends BaseScimResource
{
  @Nullable
  @Attribute(description = "The generated shared secret encoded in Base32.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String sharedSecret;

  @Nullable
  @Attribute(description = "The URI containing the generated shared secret " +
      "that can be encoded in a QR code and scanned by the TOTP device.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private URI otpAuthUri;

  @Nullable
  @Attribute(description = "Whether the shared secret is registered.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Boolean registered;

  @Nullable
  @Attribute(description = "A TOTP generated from the shared secret used " +
      "to verify that the shared secret was successfully registered with the " +
      "device.",
      mutability = AttributeDefinition.Mutability.WRITE_ONLY,
      returned = AttributeDefinition.Returned.NEVER)
  private String verifyTotp;

  @Nullable
  @Attribute(description = "Internal state used during the shared secret " +
      "registration process. Must be returned with verifyTotp.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private String verifyState;

  /**
   * Retrieves the generated shared secret encoded in Base32.
   *
   * @return The generated shared secret.
   */
  @Nullable
  public String getSharedSecret()
  {
    return sharedSecret;
  }

  /**
   * Sets the generated shared secret encoded in Base32.
   *
   * @param sharedSecret The generated shared secret.
   * @return This object.
   */
  @NotNull
  public TOTPSecretRegistrationRequest setSharedSecret(
      @Nullable final String sharedSecret)
  {
    this.sharedSecret = sharedSecret;
    return this;
  }

  /**
   * Retrieves the URI containing the generated shared secret.
   *
   * @return The URI containing the generated shared secret.
   */
  @Nullable
  public URI getOtpAuthUri()
  {
    return otpAuthUri;
  }

  /**
   * Sets the URI containing the generated shared secret.
   *
   * @param otpAuthUri The URI containing the generated shared secret.
   * @return This object.
   */
  @NotNull
  public TOTPSecretRegistrationRequest setOtpAuthUri(
      @Nullable final URI otpAuthUri)
  {
    this.otpAuthUri = otpAuthUri;
    return this;
  }

  /**
   * Retrieves whether the shared secret is registered.
   *
   * @return Whether the shared secret is registered.
   */
  @Nullable
  public Boolean getRegistered()
  {
    return registered;
  }

  /**
   * Sets whether the shared secret is registered.
   *
   * @param registered Whether the shared secret is registered.
   * @return This object.
   */
  @NotNull
  public TOTPSecretRegistrationRequest setRegistered(
      @Nullable final Boolean registered)
  {
    this.registered = registered;
    return this;
  }

  /**
   * Retrieves the TOTP generated from the shared secret.
   *
   * @return The TOTP generated from the shared secret.
   */
  @Nullable
  public String getVerifyTotp()
  {
    return verifyTotp;
  }

  /**
   * Sets the TOTP generated from the shared secret.
   *
   * @param verifyTotp The TOTP generated from the shared secret.
   * @return This object.
   */
  @NotNull
  public TOTPSecretRegistrationRequest setVerifyTotp(
      @Nullable final String verifyTotp)
  {
    this.verifyTotp = verifyTotp;
    return this;
  }

  /**
   * Retrieves the internal state used during the shared secret registration
   * process. Must be returned with verifyTotp.
   *
   * @return The internal state used during the shared secret registration
   * process.
   */
  @Nullable
  public String getVerifyState()
  {
    return verifyState;
  }

  /**
   * Sets the internal state used during the shared secret registration
   * process. Must be returned with verifyTotp.
   *
   * @param verifyState The internal state used during the shared secret
   *                    registration process.
   * @return  This object.
   */
  @NotNull
  public TOTPSecretRegistrationRequest setVerifyState(
      @Nullable final String verifyState)
  {
    this.verifyState = verifyState;
    return this;
  }
}
