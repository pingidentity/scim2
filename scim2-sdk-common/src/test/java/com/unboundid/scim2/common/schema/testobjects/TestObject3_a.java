/*
 * Copyright 2015-2019 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;

/**
 * Class used by unit tests.
 */
public class TestObject3_a extends BaseScimResource
{
  @Attribute(description = "description:stringField_3a")
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
