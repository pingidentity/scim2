/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema.testobjects;

import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.model.BaseScimObject;

@SchemaInfo(id="urn:id:TestObject1",
  description = "description:TestObject1", name = "name:TestObject1")
public class TestObject1 extends BaseScimObject
{

}
