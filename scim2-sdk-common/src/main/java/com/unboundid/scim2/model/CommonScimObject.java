/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.model;

import java.util.Set;

/**
 * Interface that can be used to access data from all scim objects.
 */
public interface CommonScimObject
{

  /**
   * Gets metadata about the object.
   * @return <code>Meta</code> containing metadata about the object.
   */
  Meta getMeta();

  /**
   * Sets metadata for the object.
   * @param meta <code>Meta</code> containing metadata for the object.
   */
  void setMeta(Meta meta);

  /**
   * Gets the id of the object.
   * @return the id of the object.
   */
  String getId();


  /**
   * Sets the id of the object.
   * @param id The object's id.
   */
  void setId(String id);

  /**
   * Gets the objects external id.
   * @return The external id of the object.
   */
  String getExternalId();

  /**
   * Sets the object's external id.
   * @param externalId The external id of the object.
   */
  void setExternalId(String externalId);

  /**
   * Gets the schema urns for this object.  This includes the one for the
   * class that extens this class (taken from the annotation), as well as
   * any that are present in the extensions.
   *
   * @return the schema urns for this object.
   */
  Set<String> getSchemaUrns();

  /**
   * Sets the schema urns for this object.  This set should contain
   * all schema urns including the one for this object and all
   * extensions.
   * @param schemaUrns a set containing the schema urns for this object.
   */
  void setSchemaUrns(Set<String> schemaUrns);

  /**
   * Gets an extension of the type of the class passed in.  The class
   * passed in must be annotated with the schema's urn.
   *
   * @param cl a java class used to determine the type of the extension
   *           that is returned.
   * @param <T> the generic parameter of the java Class used to determine
   *           the type of the extension that is returned.
   * @return the extension if it exists.
   * @throws Exception thrown if an error is encountered while locating
   * the extension or parsing it.
   */
  <T> T getExtension(final Class<T> cl)
      throws Exception;

  /**
   * Gets an extension with the schema urn passed in and returns it
   * as a generic SCIM object.
   *
   * @param schemaId the urn of the schema of the extension.
   * @return a generic SCIM object containing the extension values.
   * @throws Exception thrown if an error is encountered while locating
   * the extension or parsing it.
   */
  GenericScimObject getExtension(final String schemaId)
      throws Exception;
}
