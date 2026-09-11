/*
 * Copyright 2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.types.devices;

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Objects;

/**
 * This class represents a complex type for public key data as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944#section-6.3.1">
 * RFC 9944 Section 6.3.1</a>.
 * <br><br>
 *
 * In SCIM, a {@code CertificateInfo} is associated with an
 * {@link EndpointAppResource}. It contains values from an X.509 certificate:
 * <ul>
 *   <li> {@code rootCA}:      The root Certificate Authority (the top-level
 *                             trust anchor). This will be base64-encoded.
 *   <li> {@code subjectName}: This string represents either the subjectName or
 *                             the subjectAlternateName of the certificate.
 * </ul>
 *
 * For example JSON structure and Java code, see {@link EndpointAppResource}.
 * <br><br>
 *
 * The fields on {@code CertificateInfo} are optional, and the SCIM SDK stores
 * these string values without attempting to validate their format. As stated
 * above, {@code rootCA} will be a {@link java.util.Base64} value. For
 * {@code subjectName}, if the field represents a subjectAlternateName, it will
 * be a DNS name such as {@code www.example.com}. Otherwise, it will represent a
 * DN (distinguished name) such as {@code CN=EX1,O=Example,C=US}.
 * <br><br>
 *
 * SCIM applications should consider the following mandates with regard to a
 * certificateInfo object and client tokens on an EndpointAppResource:
 * <ul>
 *   <li> If the SCIM service accepts both a provided certificateInfo and a
 *        client token, then control/telemetry services MUST validate both.
 *   <li> If the SCIM service accepts a certificateInfo object, it MUST return
 *        that object in the response to the client.
 *   <li> If the SCIM service does not accept a certificateInfo and does not
 *        generate a clientToken, then an external authentication method, such
 *        as OAuth 2, MUST be pre-arranged.
 * </ul>
 */
public class CertificateInfo
{
  @Nullable
  @Attribute(description = "The certificate authority for the endpoint app.",
      isRequired = false)
  private String rootCA;

  @Nullable
  @Attribute(description =
      "The subject name for the endpoint application certificate.",
      isRequired = false)
  private String subjectName;

  /**
   * Retrieves the root certificate authority for the endpoint application.
   *
   * @return The root CA.
   */
  @Nullable
  public String getRootCA()
  {
    return rootCA;
  }

  /**
   * Specifies the root certificate authority for the endpoint application.
   *
   * @param rootCA The root CA.
   * @return This CertificateInfo.
   */
  @NotNull
  public CertificateInfo setRootCA(@Nullable final String rootCA)
  {
    this.rootCA = rootCA;
    return this;
  }

  /**
   * Retrieves the subject name for the endpoint application certificate.
   *
   * @return The subject name.
   */
  @Nullable
  public String getSubjectName()
  {
    return subjectName;
  }

  /**
   * Specifies the subject name for the endpoint application certificate.
   *
   * @param subjectName The subject name.
   * @return This CertificateInfo.
   */
  @NotNull
  public CertificateInfo setSubjectName(@Nullable final String subjectName)
  {
    this.subjectName = subjectName;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this certificate info.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this
   *            certificate info, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    return o instanceof CertificateInfo that
        && Objects.equals(rootCA, that.rootCA)
        && Objects.equals(subjectName, that.subjectName);
  }

  /**
   * Retrieves a string representation of this certificate info.
   *
   * @return  A string representation of this certificate info.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.getObjectWriter().withDefaultPrettyPrinter()
        .writeValueAsString(this);
  }

  /**
   * Retrieves a hash code for this certificate info.
   *
   * @return  A hash code for this certificate info.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(rootCA, subjectName);
  }
}
