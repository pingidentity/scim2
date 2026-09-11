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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;


/**
 * A reference to an {@link EndpointAppResource} within an
 * {@link EndpointAppDeviceExtension}, as defined by RFC 9944. For more
 * background on devices, see the {@link DeviceResource} class.
 */
public class EndpointAppReference
{
  @Nullable
  @Attribute(description = "The identifier of the EndpointApp resource.",
      isRequired = true,
      isCaseExact = false)
  private String value;

  @Nullable
  @Attribute(description = "The URI of the EndpointApp resource.",
      isRequired = false,
      isCaseExact = true,
      referenceTypes = { "EndpointApp" },
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  @JsonProperty("$ref")
  private URI ref;

  /**
   * Retrieves the identifier of the EndpointApp resource.
   *
   * @return The EndpointApp identifier.
   */
  @Nullable
  public String getValue()
  {
    return value;
  }

  /**
   * Specifies the identifier of the EndpointApp resource. This should generally
   * be a {@link UUID}.
   *
   * @param value The EndpointApp identifier.
   * @return This object.
   */
  @NotNull
  public EndpointAppReference setValue(@Nullable final String value)
  {
    this.value = value;
    return this;
  }

  /**
   * Alternate method that accepts a UUID object as a value.
   *
   * @param value The EndpointApp identifier.
   * @return This object.
   */
  @NotNull
  public EndpointAppReference setValue(@Nullable final UUID value)
  {
    return setValue(value == null ? null : value.toString());
  }

  /**
   * Retrieves the URI of the EndpointApp resource.
   *
   * @return The URI.
   */
  @Nullable
  public URI getRef()
  {
    return ref;
  }

  /**
   * Retrieves the URI of the EndpointApp resource.
   *
   * @return The URI.
   */
  @Nullable
  @JsonIgnore
  public String getRefString()
  {
    return (getRef() == null) ? null : getRef().toString();
  }

  /**
   * Specifies the URI of the EndpointApp resource.
   *
   * @param ref The URI.
   * @return This object.
   */
  @NotNull
  public EndpointAppReference setRef(@Nullable final URI ref)
  {
    this.ref = ref;
    return this;
  }

  /**
   * Specifies a string URI of the EndpointApp resource.
   *
   * @param ref The URI.
   * @return This object.
   *
   * @throws IllegalArgumentException  If the provided string was not a URI.
   */
  @NotNull
  public EndpointAppReference setRef(@Nullable final String ref)
      throws IllegalArgumentException
  {
    return setRef((ref == null) ? null : URI.create(ref));
  }

  /**
   * Indicates whether the provided object is equal to this application entry.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this entry,
   *            or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }

    return o instanceof EndpointAppReference that
        && Objects.equals(value, that.value)
        && Objects.equals(ref, that.ref);
  }

  /**
   * Retrieves a string representation of this application entry.
   *
   * @return  A string representation of this application entry.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.getObjectWriter().withDefaultPrettyPrinter()
        .writeValueAsString(this);
  }

  /**
   * Retrieves a hash code for this application entry.
   *
   * @return  A hash code for this application entry.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(value, ref);
  }
}
