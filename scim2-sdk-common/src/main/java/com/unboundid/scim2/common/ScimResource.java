/*
 * Copyright 2015-2023 Ping Identity Corporation
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

package com.unboundid.scim2.common;

import com.unboundid.scim2.common.types.Meta;

import java.util.Collection;

/**
 * This class represents the core interface for all SCIM objects. This interface
 * helps ensure that all SCIM resources contain the following properties:
 * <ul>
 *   <li> A {@code schemas} field containing a list of URIs that represent the
 *        type of the SCIM resource.
 *   <li> An {@code id} field representing a unique identifier for the resource.
 *        This is typically in {@link java.util.UUID} form.
 *   <li> A {@link Meta} attribute that stores metadata relating to the SCIM
 *        resource, such as the time the resource was created and last updated.
 *   <li> An {@code externalID} field. This is an optional field whose meaning
 *        is determined by a SCIM client. This is particularly useful for
 *        provisioning use cases, where a client is importing users from another
 *        system but still wants to store a resource's old unique ID in the new
 *        system. The {@code externalID} field can later be used by the client
 *        to search for resources on the new SCIM endpoints by referencing their
 *        old IDs.
 * </ul>
 *
 * The SCIM SDK provides two core implementations of ScimResource. These are the
 * {@link BaseScimResource} and {@link GenericScimResource} classes. In general,
 * a SCIM resource should be represented as a BaseScimResource if a well-defined
 * schema is known ahead of time, and a dedicated Java object is desired.
 * Alternatively, a SCIM resource should be represented as a GenericScimResource
 * if it is easier to work with a class that is a wrapper for a JSON object.
 * See the class-level documentation of these subclasses for more information.
 */
public interface ScimResource
{
  /**
   * Gets metadata about the object.
   *
   * @return {@code Meta} containing metadata about the object.
   */
  Meta getMeta();

  /**
   * Sets metadata for the object.
   *
   * @param meta {@code Meta} containing metadata for the object.
   */
  void setMeta(Meta meta);

  /**
   * Gets the id of the object.
   *
   * @return the id of the object.
   */
  String getId();


  /**
   * Sets the id of the object.
   *
   * @param id The object's id.
   */
  void setId(String id);

  /**
   * Gets the objects external id.
   *
   * @return The external id of the object.
   */
  String getExternalId();

  /**
   * Sets the object's external id.
   *
   * @param externalId The external id of the object.
   */
  void setExternalId(String externalId);

  /**
   * Gets the schema urns for this object.  This includes the one for the
   * class that extends this class (taken from the annotation), as well as
   * any that are present in the extensions.
   *
   * @return the schema urns for this object.
   */
  Collection<String> getSchemaUrns();

  /**
   * Sets the schema urns for this object.  This set should contain
   * all schema urns including the one for this object and all
   * extensions.  The value must not be {@code null}.
   *
   * @param schemaUrns A Collection containing the schema urns for this object.
   */
  void setSchemaUrns(Collection<String> schemaUrns);

  /**
   * An alternate version of {@link #setSchemaUrns(Collection)}.
   *
   * @param schemaUrn  A schema URN that will be listed first. This must not be
   *                   {@code null}.
   * @param schemaUrns An optional parameter for additional schema URNs. Any
   *                   {@code null} values will be ignored.
   */
  void setSchemaUrns(String schemaUrn, String... schemaUrns);

  /**
   * Returns the GenericScimResource representation of this ScimResource. If
   * this ScimResource is already a GenericScimResource, this same instance will
   * be returned.
   *
   * @return The GenericScimResource representation of this ScimResource.
   */
  GenericScimResource asGenericScimResource();
}
