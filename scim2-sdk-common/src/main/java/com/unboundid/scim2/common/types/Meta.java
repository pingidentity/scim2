/*
 * Copyright 2015-2024 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.Nullable;

import java.net.URI;
import java.util.Calendar;

/**
 * Stores metadata about a SCIM object.
 */
public final class Meta
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
   */
  public void setCreated(@Nullable final Calendar created)
  {
    this.created = created;
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
   */
  public void setLastModified(@Nullable final Calendar lastModified)
  {
    this.lastModified = lastModified;
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
   */
  public void setLocation(@Nullable final URI location)
  {
    this.location = location;
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
   */
  public void setVersion(@Nullable final String version)
  {
    this.version = version;
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
   */
  public void setResourceType(@Nullable final String resourceType)
  {
    this.resourceType = resourceType;
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

    if (created != null ? !created.equals(meta.created) : meta.created != null)
    {
      return false;
    }
    if (lastModified != null ? !lastModified.equals(meta.lastModified) :
        meta.lastModified != null)
    {
      return false;
    }
    if (location != null ? !location.equals(meta.location) :
        meta.location != null)
    {
      return false;
    }
    if (resourceType != null ? !resourceType.equals(meta.resourceType) :
        meta.resourceType != null)
    {
      return false;
    }
    if (version != null ? !version.equals(meta.version) : meta.version != null)
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this SCIM metadata.
   *
   * @return  A hash code for this SCIM metadata.
   */
  @Override
  public int hashCode()
  {
    int result = resourceType != null ? resourceType.hashCode() : 0;
    result = 31 * result + (created != null ? created.hashCode() : 0);
    result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
    result = 31 * result + (location != null ? location.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    return result;
  }
}
