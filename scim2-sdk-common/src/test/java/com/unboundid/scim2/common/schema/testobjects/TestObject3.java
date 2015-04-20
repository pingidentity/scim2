/*
 * Copyright 2015 UnboundID Corp.
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

package com.unboundid.scim2.common.schema.testobjects;

import com.unboundid.scim2.common.annotations.SchemaInfo;
import com.unboundid.scim2.common.annotations.SchemaProperty;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.ScimMultiValuedObject;

import java.util.List;

@SchemaInfo(id="urn:com.unboundid:schemas:TestObject3",
    description = "description:TestObject3", name = "TestObject3")
public class TestObject3 extends BaseScimResource
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
