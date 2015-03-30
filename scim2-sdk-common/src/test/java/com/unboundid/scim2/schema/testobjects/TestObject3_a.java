/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema.testobjects;

import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.model.BaseScimResourceObject;

/**
 * Class used by unit tests.
 */
public class TestObject3_a extends BaseScimResourceObject
{
  @SchemaProperty(description = "description:stringField_3a")
  private String stringField_3a;

  /**
   * Getter for attribute in test class.
   * @return  attribute value.
   */
  public String getStringField_3a()
  {
    return stringField_3a;
  }

  /**
   * Setter for attribute in test class.
   * @param stringField_3a attribute value.
   */
  public void setStringField_3a(String stringField_3a)
  {
    this.stringField_3a = stringField_3a;
  }

}
