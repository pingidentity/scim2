/*
 * Copyright 2023 Ping Identity Corporation
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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.NullabilityAnnotationTest;
import org.testng.annotations.Test;


/**
 * This class provides a version of the {@link NullabilityAnnotationTest} that
 * evaluates the {@code server} module.
 */
public class ServerNullabilityAnnotationTest
    extends NullabilityAnnotationTest
{
  /**
   * Evaluate the fields of public methods for the provided class.
   *
   * @param c The class within this module to evaluate.
   */
  @Test(dataProvider = "sdkClasses", enabled = false)
  public void serverNullabilityTest(Class<?> c)
  {
    super.testNullability(c);
  }
}
