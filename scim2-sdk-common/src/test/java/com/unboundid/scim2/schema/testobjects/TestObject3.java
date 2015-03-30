/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema.testobjects;

import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.model.BaseScimResourceObject;
import com.unboundid.scim2.model.ScimMultiValuedObject;

import java.util.List;

@SchemaInfo(id="urn:com.unboundid:schemas:TestObject3",
    description = "description:TestObject3", name = "TestObject3")
public class TestObject3 extends BaseScimResourceObject
{
  @SchemaProperty(description = "description:complexObject")
  private TestObject3_a complexObject;

  @SchemaProperty(description = "description:multiValuedString",
      multiValueClass = TestObject3_b.class,
      canonicalValues = {"one", "two", "three"})
  private List<ScimMultiValuedObject<String>> multiValuedString;

  @SchemaProperty(description = "description:multiValuedComplex",
      multiValueClass = TestObject3_c.class,
      canonicalValues = {"a", "b", "c"})
  private List<TestObject3_c> multiValuedComplex;

  @SchemaProperty(description = "description:multiValuedField_missingType",
      multiValueClass = TestObject3_d.class,
      canonicalValues = {"one", "two", "three"})
  private List<TestObject3_d> multiValuedField_missingType;

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public TestObject3_a getComplexObject()
  {
    return complexObject;
  }

  /**
   * Setter for attribute in test class.
   * @param complexObject attribute value.
   */
  public void setComplexObject(TestObject3_a complexObject)
  {
    this.complexObject = complexObject;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public List<TestObject3_c> getMultiValuedComplex()
  {
    return multiValuedComplex;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public List<ScimMultiValuedObject<String>> getMultiValuedString()
  {
    return multiValuedString;
  }

  /**
   * Setter for attribute in test class.
   * @param multiValuedString attribute value.
   */
  public void setMultiValuedString(
      List<ScimMultiValuedObject<String>> multiValuedString)
  {
    this.multiValuedString = multiValuedString;
  }

  /**
   * Setter for attribute in test class.
   * @param multiValuedComplex attribute value.
   */
  public void setMultiValuedComplex(List<TestObject3_c> multiValuedComplex)
  {
    this.multiValuedComplex = multiValuedComplex;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public List<TestObject3_d> getMultiValuedField_missingType()
  {
    return multiValuedField_missingType;
  }

  /**
   * Setter for attribute in test class.
   * @param multiValuedField_missingType attribute value.
   */
  public void setMultiValuedField_missingType(
      List<TestObject3_d> multiValuedField_missingType)
  {
    this.multiValuedField_missingType = multiValuedField_missingType;
  }
}
