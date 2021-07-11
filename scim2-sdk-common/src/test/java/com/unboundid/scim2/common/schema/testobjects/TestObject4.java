/*
 * Copyright 2015-2018 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.Schema;

@Schema(id="urn:com.unboundid:schemas:TestObject4",
        description = "description:TestObject4", name = "TestObject4")
public class TestObject4 {

    @Attribute(description = "description:value")
    private TestEnumObject value;

    /**
     * Getter for attribute in test class.
     * @param value attribute value.
     */
    public void setValue(final TestEnumObject value) {
        this.value = value;
    }

    /**
     * Getter for attribute in test class.
     * @return attribute value.
     */
    public TestEnumObject getValue() {
        return value;
    }
}
