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

package com.unboundid.scim2.common.schema.testobjects;

import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.ScimMultiValuedObject;

import java.util.List;

@Schema(id="urn:com.unboundid:schemas:TestObject3",
    description = "description:TestObject3", name = "TestObject3")
public class TestObject3 extends BaseScimResource
{
  @Attribute(description = "description:complexObject")
  private TestObject3_a complexObject;

  @Attribute(description = "description:multiValuedString",
      multiValueClass = TestObject3_b.class,
      canonicalValues = {"one", "two", "three"})
  private List<ScimMultiValuedObject<String>> multiValuedString;

  @Attribute(description = "description:multiValuedComplex",
      multiValueClass = TestObject3_c.class,
      canonicalValues = {"a", "b", "c"})
  private List<TestObject3_c> multiValuedComplex;

  @Attribute(description = "description:multiValuedField_missingType",
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
