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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.types.Group;
import com.unboundid.scim2.common.utils.StaticUtils;

import java.util.List;
import java.util.Objects;


/**
 * This class represents an "endpoint application" resource type as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc9944#section-6">RFC 9944
 * Section 6</a>. An EndpointApp resource represents an external application
 * that interacts with a device. The following types of applications are
 * defined in SCIM:
 * <ul>
 *   <li> deviceControl: Sends commands to control a device.
 *   <li> telemetry: Receives data from a device, generally for observability.
 * </ul>
 * <br><br>
 *
 * An endpoint application resource contains the following fields:
 * <ul>
 *   <li> {@code applicationType}: The type of application. Must be either
 *                                 {@code deviceControl} or {@code telemetry}.
 *   <li> {@code applicationName}: A human-readable name for the application.
 *   <li> {@code clientToken}:     A read-only token used by the endpoint
 *                                 application to authenticate to the service.
 *   <li> {@link CertificateInfo}: Certificate info for the endpoint app.
 *   <li> {@code groups}:          A list of groups to which the endpoint
 *                                 application belongs.
 * </ul>
 * <br><br>
 *
 * The following example JSON object represents an EndpointApp resource:
 * <pre>
 * {
 *   "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:EndpointApp" ],
 *   "id": "e9e30dba",
 *   "meta": {
 *     "resourceType": "EndpointApp",
 *     "location": "https://example.com/v2/EndpointApps/e9e30dba"
 *   },
 *   "applicationType": "deviceControl",
 *   "applicationName": "Device Control App 1",
 *   "certificateInfo": {
 *       "rootCA": "d2hhdHNVcA==",
 *       "subjectName": "CN=EX1,O=Example,C=US"
 *   }
 * }
 * </pre>
 * <br><br>
 *
 * This JSON value can be created with the following Java code:
 * <pre><code>
 *   EndpointAppResource app = new EndpointAppResource()
 *       .setApplicationType("deviceControl")
 *       .setApplicationName("Device Control App 1")
 *       .setCertificateInfo(new CertificateInfo()
 *           .setRootCA("d2hhdHNVcA==")
 *           .setSubjectName("CN=EX1,O=Example,C=US"));
 *   app.setId("e9e30dba");
 *   app.setMeta(new Meta()
 *       .setResourceType("EndpointApp")
 *       .setLocationString("https://example.com/v2/EndpointApps/e9e30dba"));
 * </code></pre>
 * <br><br>
 *
 * See {@link CertificateInfo} for more details on how SCIM services should
 * handle client certificate data.
 *
 * @since 6.1.0
 */
@Schema(id = "urn:ietf:params:scim:schemas:core:2.0:EndpointApp",
    name = "Endpoint Application",
    description = "Endpoint Application")
public class EndpointAppResource extends BaseScimResource
{
  /**
   * The maximum length of a client token as defined by RFC 9944 Section 6.2.
   */
  public static final int MAX_TOKEN_LENGTH = 500;

  @Nullable
  @Attribute(description = "The type of the endpoint application.",
      isRequired = true,
      isCaseExact = false,
      canonicalValues = {"deviceControl", "telemetry"},
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String applicationType;

  @Nullable
  @Attribute(description = "The human-readable name of the endpoint "
      + "application.",
      isRequired = true,
      isCaseExact = false)
  private String applicationName;

  @Nullable
  @Attribute(description = """
      A read-only token used by the endpoint application to authenticate to the
      service. The token is generated by the server and MUST NOT exceed 500
      characters in length (RFC 9944 Section 6.2).""",
      isCaseExact = true,
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String clientToken;

  @Nullable
  @Attribute(description = "Certificate info for the endpoint application.")
  private CertificateInfo certificateInfo;

  @NotNull
  @Attribute(description =
      "A list of groups to which the endpoint application belongs.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = Group.class)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Group> groups = List.of();

  /**
   * Retrieves the application type of this endpoint application.
   *
   * @return The application type.
   */
  @Nullable
  public String getApplicationType()
  {
    return applicationType;
  }

  /**
   * Specifies the application type of this endpoint application. This
   * attribute is immutable after creation.
   *
   * @param type The application type.
   * @return This endpoint application resource.
   */
  @NotNull
  public EndpointAppResource setApplicationType(@Nullable final String type)
  {
    applicationType = type;
    return this;
  }

  /**
   * Retrieves the human-readable name of this endpoint application.
   *
   * @return The application name.
   */
  @Nullable
  public String getApplicationName()
  {
    return applicationName;
  }

  /**
   * Specifies the human-readable name of this endpoint application.
   *
   * @param name The application name.
   * @return This endpoint application resource.
   */
  @NotNull
  public EndpointAppResource setApplicationName(@Nullable final String name)
  {
    applicationName = name;
    return this;
  }

  /**
   * Retrieves the client token for this endpoint application.
   *
   * @return The client token.
   */
  @Nullable
  public String getClientToken()
  {
    return clientToken;
  }


  /**
   * Specifies the client token for this endpoint application.
   *
   * @param clientToken The client token.
   * @return This endpoint application resource.
   *
   * @throws IllegalArgumentException  If the provided token is longer than the
   *                                   {@link #MAX_TOKEN_LENGTH}.
   */
  @NotNull
  public EndpointAppResource setClientToken(@Nullable final String clientToken)
      throws IllegalArgumentException
  {
    if (clientToken != null && clientToken.length() > MAX_TOKEN_LENGTH)
    {
      throw new IllegalArgumentException(
          "Client token lengths cannot be greater than " + MAX_TOKEN_LENGTH);
    }

    this.clientToken = clientToken;
    return this;
  }

  /**
   * Retrieves the certificate information for this endpoint application.
   *
   * @return The certificate information.
   */
  @Nullable
  public CertificateInfo getCertificateInfo()
  {
    return certificateInfo;
  }

  /**
   * Specifies the certificate information for this endpoint application.
   *
   * @param certificateInfo The certificate information.
   * @return This endpoint application resource.
   */
  @NotNull
  public EndpointAppResource setCertificateInfo(
      @Nullable final CertificateInfo certificateInfo)
  {
    this.certificateInfo = certificateInfo;
    return this;
  }

  /**
   * Retrieves the list of groups to which this endpoint application belongs.
   *
   * @return The list of groups.
   */
  @NotNull
  public List<Group> getGroups()
  {
    return groups;
  }

  /**
   * Specifies the list of groups to which this endpoint application belongs.
   *
   * @param groups The list of groups.
   * @return This endpoint application resource.
   */
  @NotNull
  public EndpointAppResource setGroups(@Nullable final List<Group> groups)
  {
    this.groups = (groups == null) ? List.of() : groups;
    return this;
  }

  /**
   * Alternate version of {@link #setGroups(List)}.
   *
   * @param group   The first group object.
   * @param groups  An optional set of additional arguments.
   *
   * @return This endpoint application resource.
   */
  @NotNull
  public EndpointAppResource setGroups(@NotNull final Group group,
                                       @Nullable final Group... groups)
  {
    return setGroups(StaticUtils.toList(group, groups));
  }

  /**
   * Verifies whether the {@code certificateInfo} field is {@code null} or
   * contains no data. This can be used by SCIM applications to check if a
   * certificateInfo has fields that should be validated.
   *
   * @return  {@code true} if the certificateInfo is {@code null} or empty.
   */
  public boolean hasEmptyCertificateInfo()
  {
    return certificateInfo == null || (certificateInfo.getRootCA() == null
        && certificateInfo.getSubjectName() == null);
  }

  /**
   * Indicates whether the provided object is equal to this endpoint app
   * resource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this endpoint
   *            app resource, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    return o instanceof EndpointAppResource that
        && super.equals(o)
        && Objects.equals(applicationType, that.applicationType)
        && Objects.equals(applicationName, that.applicationName)
        && Objects.equals(clientToken, that.clientToken)
        && Objects.equals(certificateInfo, that.certificateInfo)
        && Objects.equals(groups, that.groups);
  }

  /**
   * Retrieves a hash code for this endpoint app resource.
   *
   * @return  A hash code for this endpoint app resource.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), applicationType, applicationName,
        clientToken, certificateInfo, groups);
  }
}
