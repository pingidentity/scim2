/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.schema.testobjects.TestObject1;
import com.unboundid.scim2.schema.testobjects.TestObject2;
import com.unboundid.scim2.schema.testobjects.TestObject3;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Tests cases for SCIM schema generation.
 */
@Test
public class SchemaGenerationTest
{
  private enum Required
  {
    REQUIRED,
    NOT_REQUIRED,
    DEFAULT
  }

  private enum CaseExact
  {
    CASE_EXACT,
    NOT_CASE_EXACT,
    DEFAULT
  }

  private enum Multivalued
  {
    MULTIVALUED,
    NOT_MULTIVALUED,
    DEFAULT
  }

  private ObjectMapper mapper = new ObjectMapper();

  /**
   * Tests SchemaInfo annotation with no additional properties.
   * @throws Exception in the event an error occurs.
   */
  @Test
  public void testCase1() throws Exception
  {
    SchemaDefinition schemaDefinition =
        SchemaUtils.getSchema(TestObject1.class);
    String schemaJsonString = mapper.writeValueAsString(schemaDefinition);
  }

  /**
   * Tests schema property annotations for some of the basic types
   * and attributes.
   * @throws Exception in the event an error occurs.
   */
  @Test
  public void testCase2() throws Exception
  {
    SchemaDefinition schemaDefinition =
        SchemaUtils.getSchema(TestObject2.class);

    tc2_checkSchema(schemaDefinition);

    List<String> expectedAttributes = makeModifiableList("stringField",
        "booleanObjectField", "booleanField", "integerObjectField",
        "integerField", "mutabilityReadWrite", "mutabilityReadOnly",
        "mutabilityWriteOnly", "mutabilityImmutable");
    addBaseResourceObjectFields(expectedAttributes);

    for(AttributeDefinition attribute : schemaDefinition.getAttributes())
    {
      if(attribute.getName().equals("stringField" ))
      {
        //    @SchemaProperty(description = "description:stringField",
        //        isCaseExact = true, isRequired = true)
        checkAttribute(attribute, "description:stringField",
            Required.REQUIRED, CaseExact.CASE_EXACT, Multivalued.DEFAULT,
            AttributeDefinition.Type.STRING, null,
            null, null, null, null);
      }
      else if(attribute.getName().equals("booleanObjectField" ))
      {
        //    @SchemaProperty(description = "description:booleanObjectField",
        //        isCaseExact = true, isRequired = false,
        //        returned = SCIM2Attribute.Returned.REQUEST)
        checkAttribute(attribute, "description:booleanObjectField",
            Required.NOT_REQUIRED, CaseExact.CASE_EXACT,
            Multivalued.DEFAULT, AttributeDefinition.Type.BOOLEAN,
            AttributeDefinition.Returned.REQUEST,
            null, null, null, null);
      }
      else if(attribute.getName().equals("booleanField" ))
      {
        //    @SchemaProperty(description = "description:booleanField",
        //        isCaseExact = false, isRequired = false,
        //        returned = SCIM2Attribute.Returned.NEVER)

        checkAttribute(attribute, "description:booleanField",
            Required.NOT_REQUIRED, CaseExact.NOT_CASE_EXACT,
            Multivalued.DEFAULT, AttributeDefinition.Type.BOOLEAN,
            AttributeDefinition.Returned.NEVER,
            null, null, null, null);
      }
      else if(attribute.getName().equals("integerObjectField" ))
      {
        //    @SchemaProperty(description = "description:integerObjectField",
        //        isCaseExact = false, isRequired = true,
        //        returned = SCIM2Attribute.Returned.DEFAULT)

        checkAttribute(attribute, "description:integerObjectField",
            Required.REQUIRED, CaseExact.NOT_CASE_EXACT,
            Multivalued.DEFAULT, AttributeDefinition.Type.INTEGER,
            AttributeDefinition.Returned.DEFAULT,
            null, null, null, null);
      }
      else if(attribute.getName().equals("integerField" ))
      {
        //    @SchemaProperty(description = "description:integerField",
        //        isCaseExact = true, isRequired = true,
        //        returned = SCIM2Attribute.Returned.ALWAYS)
        checkAttribute(attribute, "description:integerField",
            Required.REQUIRED, CaseExact.CASE_EXACT,
            Multivalued.DEFAULT, AttributeDefinition.Type.INTEGER,
            AttributeDefinition.Returned.ALWAYS,
            null, null, null, null);
      }
      else if(attribute.getName().equals("mutabilityReadWrite" ))
      {
        //    @SchemaProperty(description = "description:mutabilityReadWrite",
        //        mutability = SCIM2Attribute.Mutability.READ_WRITE)
        checkAttribute(attribute, "description:mutabilityReadWrite",
            Required.DEFAULT, CaseExact.DEFAULT,
            Multivalued.DEFAULT, AttributeDefinition.Type.STRING,
            null, AttributeDefinition.Mutability.READ_WRITE, null, null, null);
      }
      else if(attribute.getName().equals("mutabilityReadOnly" ))
      {
        //    @SchemaProperty(description = "description:mutabilityReadOnly",
        //        mutability = SCIM2Attribute.Mutability.READ_ONLY)
        checkAttribute(attribute, "description:mutabilityReadOnly",
            Required.DEFAULT, CaseExact.DEFAULT,
            Multivalued.DEFAULT, AttributeDefinition.Type.STRING,
            null, AttributeDefinition.Mutability.READ_ONLY, null, null, null);
      }
      else if(attribute.getName().equals("mutabilityWriteOnly" ))
      {
        //    @SchemaProperty(description = "description:mutabilityWriteOnly",
        //        mutability = SCIM2Attribute.Mutability.WRITE_ONLY)
        checkAttribute(attribute, "description:mutabilityWriteOnly",
            Required.DEFAULT, CaseExact.DEFAULT, Multivalued.DEFAULT,
            AttributeDefinition.Type.STRING, null,
            AttributeDefinition.Mutability.WRITE_ONLY,
            null, null, null);
      }
      else if(attribute.getName().equals("mutabilityImmutable" ))
      {
        //    @SchemaProperty(description = "description:mutabilityImmutable",
        //        mutability = SCIM2Attribute.Mutability.IMMUTABLE)
        checkAttribute(attribute, "description:mutabilityImmutable",
            Required.DEFAULT, CaseExact.DEFAULT, Multivalued.DEFAULT,
            AttributeDefinition.Type.STRING, null,
            AttributeDefinition.Mutability.IMMUTABLE,
            null, null, null);
      }

      markAttributeFound(expectedAttributes, attribute);
    }

    checkAllAttributesFound(expectedAttributes);
    String schemaJsonString = mapper.writeValueAsString(schemaDefinition);
  }

  /**
   * Tests complex types and multi-valued objects.
   * @throws Exception in the event an error occurs.
   */
  @Test
  public void testCase3() throws Exception
  {
    SchemaDefinition schemaDefinition =
        SchemaUtils.getSchema(TestObject3.class);

    List<String> expectedAttributes = makeModifiableList("complexObject",
        "multiValuedString", "multiValuedComplex",
        "multiValuedField_missingType");
    addBaseResourceObjectFields(expectedAttributes);

    for (AttributeDefinition attribute : schemaDefinition.getAttributes())
    {
      if (attribute.getName().equals("complexObject"))
      {
        tc3_checkComplexObject(attribute);
      }
      else if(attribute.getName().equals("multiValuedString"))
      {
        tc3_checkMultiValuedString(attribute);
      }

      markAttributeFound(expectedAttributes, attribute);
    }

    checkAllAttributesFound(expectedAttributes);
  }

  private void tc2_checkSchema(SchemaDefinition schemaDefinition)
  {
//    @SchemaInfo(id="urn:id:TestObject1",
//        description = "description:TestObject1", name = "name:TestObject1")
    Assert.assertEquals(schemaDefinition.getId(), "urn:id:TestObject1");
    Assert.assertEquals(schemaDefinition.getDescription(),
        "description:TestObject1");
    Assert.assertEquals(schemaDefinition.getName(), "name:TestObject1");
  }

  private void tc3_checkComplexObject(AttributeDefinition attribute)
  {
    checkAttribute(attribute, "description:complexObject",
        Required.DEFAULT, CaseExact.DEFAULT, Multivalued.DEFAULT,
        AttributeDefinition.Type.COMPLEX, null,
        null, null, null, null);

    Collection<AttributeDefinition> subAttributes =
        attribute.getSubAttributes();
    Assert.assertTrue(subAttributes != null);
    Assert.assertTrue(subAttributes.size() > 0);
    List<String> expectedAttributes = makeModifiableList("stringField_3a");
    addBaseResourceObjectFields(expectedAttributes);

    for(AttributeDefinition subAttribute : subAttributes)
    {
      if(subAttribute.getName().equals("stringField_3a"))
      {
        //   @SchemaProperty(description = "description:stringField_3a")
        checkAttribute(subAttribute, "description:stringField_3a",
            Required.DEFAULT, CaseExact.DEFAULT, Multivalued.DEFAULT,
            AttributeDefinition.Type.STRING, null,
            null, null, null, null);
      }


      markAttributeFound(expectedAttributes, subAttribute);
    }

    checkAllAttributesFound(expectedAttributes);
  }

  private void tc3_checkMultiValuedString(AttributeDefinition attribute)
  {
//    @SchemaProperty(description = "description:multiValuedString",
//        canonicalValues = {"one", "two", "three"})

    checkAttribute(attribute, "description:multiValuedString",
        Required.DEFAULT, CaseExact.DEFAULT, Multivalued.MULTIVALUED,
        AttributeDefinition.Type.COMPLEX, null, null, null,
        new HashSet(makeModifiableList("one", "two", "three")), null);

    Collection<AttributeDefinition> subAttributes =
        attribute.getSubAttributes();
    Assert.assertTrue(subAttributes != null);
    Assert.assertTrue(subAttributes.size() > 0);
    List<String> expectedAttributes = new LinkedList<String>();
    addSubAttributeFields(expectedAttributes);
    addBaseResourceObjectFields(expectedAttributes);

    for(AttributeDefinition subAttribute : subAttributes)
    {
      markAttributeFound(expectedAttributes, subAttribute);
    }

    checkAllAttributesFound(expectedAttributes);
  }

  private void checkAttribute(AttributeDefinition attribute,
      String description, Required required, CaseExact caseExact,
      Multivalued multivalued, AttributeDefinition.Type type,
      AttributeDefinition.Returned returned,
      AttributeDefinition.Mutability mutability,
      AttributeDefinition.Uniqueness uniqueness, Set canonicalValues,
      String referenceType)
  {
    if (mutability == null)
    {
      mutability = AttributeDefinition.Mutability.READ_WRITE;
    }

    if(returned  == null)
    {
      returned = AttributeDefinition.Returned.DEFAULT;
    }

    if(uniqueness == null)
    {
      uniqueness = AttributeDefinition.Uniqueness.NONE;
    }

    Assert.assertEquals(attribute.getDescription(), description);
    Assert.assertEquals(attribute.getMutability(), mutability.getName());
    Assert.assertEquals(attribute.getReturned(), returned.getName());
    Assert.assertEquals(attribute.getUniqueness(), uniqueness.getName());

    switch(multivalued)
    {
      case MULTIVALUED:
        Assert.assertTrue(attribute.isMultiValued());
        break;

      case NOT_MULTIVALUED:
      default:
        Assert.assertFalse(attribute.isMultiValued());
        break;
    }

    switch(required)
    {
      case REQUIRED:
        Assert.assertTrue(attribute.isRequired());
        break;

      case NOT_REQUIRED:
      default:
        Assert.assertFalse(attribute.isRequired());
        break;
    }

    switch(caseExact)
    {
      case NOT_CASE_EXACT:
        Assert.assertFalse(attribute.isCaseExact());
        break;

      case CASE_EXACT:
      default:
        Assert.assertTrue(attribute.isCaseExact());
        break;
    }

    Assert.assertEquals(attribute.getCanonicalValues(), canonicalValues);
    Assert.assertEquals(attribute.getReferenceType(), referenceType);
    Assert.assertEquals(attribute.getType(), type.getName());

    attribute.getReferenceType();
    attribute.getType();
  }

  private void checkAllAttributesFound(List<String> expectedAttributes)
  {
    if(expectedAttributes.size() > 0)
    {
      Assert.fail("Did not find all attributes : " + expectedAttributes);
    }
  }

  private void markAttributeFound(List<String> expectedAttributes,
                                  AttributeDefinition attribute)
  {
    if(expectedAttributes.contains(attribute.getName()))
    {
      expectedAttributes.remove(attribute.getName());
    }
    else
    {
      Assert.fail("Found an unexpected attribute : " +
          attribute.getName());
    }
  }

  private List<String> makeModifiableList(String ... values)
  {
    return new LinkedList<String>(Arrays.asList(values));
  }

  private void addBaseResourceObjectFields(List<String> expectedAttributes)
  {
    expectedAttributes.addAll(Arrays.asList(
        "id", "externalId", "meta"));
  }


  private void addSubAttributeFields(List<String> expectedAttributes)
  {
    expectedAttributes.addAll(Arrays.asList(
        "type", "primary", "display", "value", "ref"));
  }
}
