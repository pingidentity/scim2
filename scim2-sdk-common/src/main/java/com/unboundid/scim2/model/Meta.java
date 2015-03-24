/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.schema.SCIM2Attribute;

import java.net.URI;
import java.util.Calendar;

/**
 * Stores metadata about a SCIM object.
 */
public class Meta extends BaseScimObject
{
  @SchemaProperty(description = "The resource Type",
      mutability = SCIM2Attribute.Mutability.READ_ONLY)
  private String resourceType;

  @SchemaProperty(description = "Date and time the resource was created",
      mutability = SCIM2Attribute.Mutability.READ_ONLY)
  private Calendar created;

  @SchemaProperty(description = "Date and time the resource was last modified",
      mutability = SCIM2Attribute.Mutability.READ_ONLY)
  private Calendar lastModified;

  @SchemaProperty(description = "The location (URI) of the resource",
      mutability = SCIM2Attribute.Mutability.READ_ONLY)
  private URI location;

  @SchemaProperty(description = "The version of the resource",
      mutability = SCIM2Attribute.Mutability.READ_ONLY)
  private String version;

  /**
   * Gets the timestamp of when the SCIM object was created.
   * @return the date and time the SCIM object was created.
   */
  public Calendar getCreated()
  {
    return created;
  }

  /**
   * Sets the timestamp of when the SCIM object was created.
   *
   * @param created the date and time the SCIM object was created.
   */
  public void setCreated(final Calendar created)
  {
    this.created = created;
  }

  /**
   * Gets the timestamp for the last modification.
   * @return the timestamp of the last modification.
   */
  public Calendar getLastModified()
  {
    return lastModified;
  }

  /**
   * Sets the timestamp of the last modification.
   *
   * @param lastModified the timestamp of the last modification.
   */
  public void setLastModified(final Calendar lastModified)
  {
    this.lastModified = lastModified;
  }

  /**
   * Gets the location URI of the SCIM object.
   *
   * @return the location URI of the SCIM object.
   */
  public URI getLocation()
  {
    return location;
  }

  /**
   * Sets the location URI of the SCIM object.
   *
   * @param location the location URI of the SCIM object.
   */
  public void setLocation(final URI location)
  {
    this.location = location;
  }

  /**
   * Gets the version of the SCIM object.
   *
   * @return the version of the SCIM object.
   */
  public String getVersion()
  {
    return version;
  }

  /**
   * Sets the version of the SCIM object.
   *
   * @param version the version of the SCIM object.
   */
  public void setVersion(final String version)
  {
    this.version = version;
  }

  /**
   * Gets the resource type of the SCIM object.
   *
   * @return the resource type of the SCIM object.
   */
  public String getResourceType()
  {
    return resourceType;
  }

  /**
   * Sets the resource type of the SCIM object.
   *
   * @param resourceType the resource type of the SCIM object.
   */
  public void setResourceType(final String resourceType)
  {
    this.resourceType = resourceType;
  }
}
