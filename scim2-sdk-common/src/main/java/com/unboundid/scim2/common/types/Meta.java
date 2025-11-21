/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.net.URI;
import java.util.Calendar;
import java.util.Objects;

/**
 * This class represents the {@code meta} attribute that stores additional
 * metadata about SCIM resources. For example, the following JSON represents a
 * minimal user resource with metadata:
 * <pre>
 *  {
 *    "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
 *    "id": "fa1afe1",
 *    "meta": {
 *      "resourceType": "User",
 *      "created": "1970-01-01T11:00:00.00Z",
 *      "lastModified": "1970-01-01T11:00:00.00Z",
 *      "location": "https://example.com/v2/Users/fa1afe1",
 *      "version": "W/\"e180ee84f0671b1\""
 *    }
 *  }
 * </pre>
 *
 * Here, the user with ID {@code fa1afe1} provides data on:
 * <ul>
 *   <li> Its resource type.
 *   <li> The timestamp indicating when it was created.
 *   <li> The timestamp indicating the last time it was updated.
 *   <li> The URI representing its location.
 *   <li> The ETag indicating the resource version.
 * </ul>
 * <br><br>
 *
 * To create a resource with the above data, use the {@code setMeta()} method of
 * any {@link com.unboundid.scim2.common.ScimResource} object:
 * <pre><code>
 *  // Represents January 1st at 11:00 AM.
 *  final Calendar calendar = Calendar.getInstance();
 *  calendar.set(getCurrentYear(), Calendar.JANUARY, 1, 11, 0);
 *
 *  UserResource user = new UserResource();
 *  user.setId("fa1afe1");
 *  user.setMeta(new Meta()
 *      .setResourceType("User")
 *      .setCreated(calendar)
 *      .setLastModified(calendar)
 *      .setLocation(new URI("https://example.com/v2/Users/fa1afe1"))
 *      .setVersion("W/\"e180ee84f0671b1\"")
 *  );
 * </code></pre>
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class Meta
{
  @Nullable
  @Attribute(description = "The resource Type",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.ALWAYS)
  private String resourceType;

  @Nullable
  @Attribute(description = "Date and time the resource was created",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.ALWAYS)
  private Calendar created;

  @Nullable
  @Attribute(description = "Date and time the resource was last modified",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.ALWAYS)
  private Calendar lastModified;

  @Nullable
  @Attribute(description = "The location (URI) of the resource",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.ALWAYS)
  private URI location;

  @Nullable
  @Attribute(description = "The version of the resource",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.ALWAYS)
  private String version;

  /**
   * Gets the timestamp of when the SCIM object was created.
   *
   * @return the date and time the SCIM object was created.
   */
  @Nullable
  public Calendar getCreated()
  {
    return created;
  }

  /**
   * Sets the timestamp of when the SCIM object was created.
   *
   * @param created the date and time the SCIM object was created.
   * @return  This {@code Meta} object.
   */
  @NotNull
  public Meta setCreated(@Nullable final Calendar created)
  {
    this.created = created;
    return this;
  }

  /**
   * Gets the timestamp for the last modification.
   *
   * @return the timestamp of the last modification.
   */
  @Nullable
  public Calendar getLastModified()
  {
    return lastModified;
  }

  /**
   * Sets the timestamp of the last modification.
   *
   * @param lastModified the timestamp of the last modification.
   * @return  This {@code Meta} object.
   */
  @NotNull
  public Meta setLastModified(@Nullable final Calendar lastModified)
  {
    this.lastModified = lastModified;
    return this;
  }

  /**
   * Gets the location URI of the SCIM object.
   *
   * @return the location URI of the SCIM object.
   */
  @Nullable
  public URI getLocation()
  {
    return location;
  }

  /**
   * Sets the location URI of the SCIM object.
   *
   * @param location the location URI of the SCIM object.
   * @return  This {@code Meta} object.
   */
  @NotNull
  public Meta setLocation(@Nullable final URI location)
  {
    this.location = location;
    return this;
  }

  /**
   * Gets the version of the SCIM object.
   *
   * @return the version of the SCIM object.
   */
  @Nullable
  public String getVersion()
  {
    return version;
  }

  /**
   * Sets the version of the SCIM object.
   *
   * @param version the version of the SCIM object.
   * @return  This {@code Meta} object.
   */
  @NotNull
  public Meta setVersion(@Nullable final String version)
  {
    this.version = version;
    return this;
  }

  /**
   * Gets the resource type of the SCIM object.
   *
   * @return the resource type of the SCIM object.
   */
  @Nullable
  public String getResourceType()
  {
    return resourceType;
  }

  /**
   * Sets the resource type of the SCIM object.
   *
   * @param resourceType the resource type of the SCIM object.
   * @return  This {@code Meta} object.
   */
  @NotNull
  public Meta setResourceType(@Nullable final String resourceType)
  {
    this.resourceType = resourceType;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this SCIM metadata.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this metadata,
   *            or {@code false} if not.
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

    Meta meta = (Meta) o;
    if (!Objects.equals(created, meta.created))
    {
      return false;
    }
    if (!Objects.equals(lastModified, meta.lastModified))
    {
      return false;
    }
    if (!Objects.equals(location, meta.location))
    {
      return false;
    }
    if (!Objects.equals(resourceType, meta.resourceType))
    {
      return false;
    }
    return Objects.equals(version, meta.version);
  }

  /**
   * Retrieves a hash code for this SCIM metadata.
   *
   * @return  A hash code for this SCIM metadata.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(created, lastModified, location, resourceType, version);
  }
}
