/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema.testobjects;

import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.model.BaseScimResourceObject;
import com.unboundid.scim2.schema.AttributeDefinition;

/**
 * Test class.
 */
@SchemaInfo(id="urn:id:TestObject1",
    description = "description:TestObject1", name = "name:TestObject1")
public class TestObject2 extends BaseScimResourceObject
{
  @SchemaProperty(description = "description:stringField",
      isCaseExact = true, isRequired = true)
  private String stringField;

  @SchemaProperty(description = "description:booleanObjectField",
      isCaseExact = true, isRequired = false,
      returned = AttributeDefinition.Returned.REQUEST)
  private Boolean booleanObjectField;

  @SchemaProperty(description = "description:booleanField",
      isCaseExact = false, isRequired = false,
      returned = AttributeDefinition.Returned.NEVER)
  private boolean booleanField;

  @SchemaProperty(description = "description:integerObjectField",
      isCaseExact = false, isRequired = true,
      returned = AttributeDefinition.Returned.DEFAULT)
  private Integer integerObjectField;

  @SchemaProperty(description = "description:integerField",
      isCaseExact = true, isRequired = true,
      returned = AttributeDefinition.Returned.ALWAYS)
  private int integerField;

  @SchemaProperty(description = "description:mutabilityImmutable",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String mutabilityImmutable;

  @SchemaProperty(description = "description:mutabilityReadWrite",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private String mutabilityReadWrite;

  @SchemaProperty(description = "description:mutabilityWriteOnly",
      mutability = AttributeDefinition.Mutability.WRITE_ONLY)
  private String mutabilityWriteOnly;

  @SchemaProperty(description = "description:mutabilityReadOnly",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String mutabilityReadOnly;

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public String getStringField()
  {
    return stringField;
  }

  /**
   * Setter for attribute in test class.
   * @param stringField  attribute value.
   */
  public void setStringField(String stringField)
  {
    this.stringField = stringField;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public Boolean getBooleanObjectField()
  {
    return booleanObjectField;
  }

  /**
   * Setter for attribute in test class.
   * @param booleanObjectField  attribute value.
   */
  public void setBooleanObjectField(Boolean booleanObjectField)
  {
    this.booleanObjectField = booleanObjectField;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public boolean isBooleanField()
  {
    return booleanField;
  }

  /**
   * Setter for attribute in test class.
   * @param booleanField  attribute value.
   */
  public void setBooleanField(boolean booleanField)
  {
    this.booleanField = booleanField;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public Integer getIntegerObjectField()
  {
    return integerObjectField;
  }

  /**
   * Setter for attribute in test class.
   * @param integerObjectField  attribute value.
   */
  public void setIntegerObjectField(Integer integerObjectField)
  {
    this.integerObjectField = integerObjectField;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public int getIntegerField()
  {
    return integerField;
  }

  /**
   * Setter for attribute in test class.
   * @param integerField  attribute value.
   */
  public void setIntegerField(int integerField)
  {
    this.integerField = integerField;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public String getMutabilityImmutable()
  {
    return mutabilityImmutable;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public String getMutabilityReadOnly()
  {
    return mutabilityReadOnly;
  }

  /**
   * Setter for attribute in test class.
   * @param mutabilityWriteOnly  attribute value.
   */
  public void setMutabilityWriteOnly(String mutabilityWriteOnly)
  {
    this.mutabilityWriteOnly = mutabilityWriteOnly;
  }

  /**
   * Getter for attribute in test class.
   * @return attribute value.
   */
  public String getMutabilityReadWrite()
  {
    return mutabilityReadWrite;
  }

  /**
   * Setter for attribute in test class.
   * @param mutabilityReadWrite  attribute value.
   */
  public void setMutabilityReadWrite(String mutabilityReadWrite)
  {
    this.mutabilityReadWrite = mutabilityReadWrite;
  }
}
