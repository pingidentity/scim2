/*
 * Copyright 2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.schema;

import java.util.Collection;
import java.util.Set;

/**
 * An attribute in a schema for a SCIM Object.
 */
public class AttributeDefinition
{

  /**
   * An enumeration of the data types for values.
   */
  public enum Type
  {
    /**
     * String datatype.
     */
    STRING("string"),

    /**
     * Boolean datatype.
     */
    BOOLEAN("boolean"),

    /**
     * Decimal datatype.
     */
    DECIMAL("decimal"),

    /**
     * Integer datatype.
     */
    INTEGER("integer"),

    /**
     * Datetime datatype.
     */
    DATETIME("datetime"),

    /**
     * Binary datatype.
     */
    BINARY("binary"),

    /**
     * Reference datatype.
     */
    REFERENCE("reference"),

    /**
     * Complex datatype.
     */
    COMPLEX("complex");

    private String name;

    /**
     * Constructs an attribute type object.
     * @param name the name (used in SCIM schemas) of the object.
     */
    private Type(final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the type.
     *
     * @return the name of the type.
     */
    public String getName()
    {
      return name;
    }

    /**
     * Gets the Type matching the given name.  Throws a runtime
     * exception if the constraint cannot be found because an invalid
     * name was given.
     *
     * @param name the name of the type.
     * @return the type matching the given name.
     */
    public static Type fromName(final String name)
    {
      for(Type type : Type.values())
      {
        if(type.getName().equals(name))
        {
          return type;
        }
      }

      throw new RuntimeException("Unknown SCIM datatype");
    }
  };

  /**
   * This enum is used to describe the mutability of an attribute.
   */
  public enum Mutability
  {
    /**
     * The attribute can be read, but not written.
     */
    READ_ONLY("readOnly"),

    /**
     * The attribute can be read, and written.
     */
    READ_WRITE("readWrite"),

    /**
     * The attribute can be read, and cannot be set after
     * object creation.  It can be set during object creation.
     */
    IMMUTABLE("immutable"),

    /**
     * The attribute can only be written, and not read.  This
     * might be used for password hashes for example.
     */
    WRITE_ONLY("writeOnly");

    /**
     * The SCIM name for this enum.
     */
    private String name;

    /**
     * Mutability enum private constructor.
     *
     * @param name the name of the mutability constraint.
     */
    private Mutability(final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the mutability constraint.
     *
     * @return the name of the mutability constraint.
     */
    public String getName()
    {
      return name;
    }

    /**
     * finds the mutability constraint by name.  Throws a runtime
     * exception if the constraint cannot be found because an invalid
     * name was given.
     *
     * @param name the name of the mutability constraint.
     * @return the enum value for the given name.
     */
    public static Mutability fromName(final String name)
    {
      for(Mutability mutability : Mutability.values())
      {
        if(mutability.getName().equals(name))
        {
          return mutability;
        }
      }

      throw new RuntimeException("Unknown SCIM mutability constraint");
    }

  }

  /**
   * This enum is used to describe the when an attribute is returned
   * from scim methods.
   */
  public enum Returned
  {
    /**
     * Indicates that the attribute is always returned.
     */
    ALWAYS("always"),

    /**
     * Indicates that the attribute is never returned.
     */
    NEVER("never"),

    /**
     * Indicates that the attribute is returned by default.
     */
    DEFAULT("default"),

    /**
     * Indicates that the attribute is only returned if requested.
     */
    REQUEST("request");

    /**
     * The SCIM name for this enum.
     */
    private String name;

    /**
     * Returned enum private constructor.
     *
     * @param name the name of the return constraint.
     */
    private Returned(final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the return constraint.
     *
     * @return the name of the return constraint.
     */
    public String getName()
    {
      return name;
    }

    /**
     * finds the return constraint by name.  Throws a runtime
     * exception if the constraint cannot be found because an invalid
     * name was given.
     *
     * @param name the name of the return constraint.
     * @return the enum value for the given name.
     */
    public static Returned fromName(final String name)
    {
      for(Returned returned : Returned.values())
      {
        if(returned.getName().equals(name))
        {
          return returned;
        }
      }

      throw new RuntimeException("Unknown SCIM return constraint");
    }

  }

  public enum Uniqueness
  {
    /**
     * Indicates that this attribute's value need not be unique.
     */
    NONE("none"),

    /**
     * Indicates that this attribute's value must be unique for a given server.
     */
    SERVER("server"),

    /**
     * Indicates that this attribute's value must be globally unique.
     */
    GLOBAL("global");

    private String name;

    /**
     * Uniqueness enum private constructor.
     *
     * @param name the name of the uniqueness constraint.
     */
    private Uniqueness(final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the uniqueness constraint.
     *
     * @return the name of the uniqueness constraint.
     */
    public String getName()
    {
      return name;
    }

    /**
     * finds the uniqueness constraint by name.  Throws a runtime
     * exception if the constraint cannot be found because an invalid
     * name was given.
     *
     * @param name the name of the uniqueness constraint.
     * @return the enum value for the given name.
     */
    public static Uniqueness fromName(final String name)
    {
      for(Uniqueness uniqueness : Uniqueness.values())
      {
        if(uniqueness.getName().equals(name))
        {
          return uniqueness;
        }
      }

      throw new RuntimeException("Unknown SCIM uniquenessConstraint");
    }

  }


  /**
   * The attribute's name.
   */
  private String name;

  /**
   * The attribute's type.  For the possible values, see:
   * {@link AttributeDefinition.Type}
   */
  private String type;

  /**
   * The sub-attributes of this attribute.
   */
  private Collection<AttributeDefinition> subAttributes;

  /**
   * A boolean value indicating whether or not this attribute can have
   * multiple values.
   */
  private boolean multiValued;

  /**
   * The description of this attribute.
   */
  private String description;

  /**
   * A boolean indicating whether or not this attribute is required
   * to be present.
   */
  private boolean required;

  /**
   * A Set of canonical values that this attribute may contain.
   */
  private Set<String> canonicalValues;

  /**
   * A boolean indicated whether or not searches for this object will
   * be case exact.  If true, then this attribute will only be matched
   * if the case of the value exactly matches the search string's case.
   */
  private boolean caseExact;

  /**
   * This value indicates the mutability constraints of this attribute.
   * See {@link AttributeDefinition.Mutability}
   */
  private String mutability;

  /**
   * Indicates the when this attribute will be returned as part of
   * a scim object.
   * See {@link AttributeDefinition.Returned}
   */
  private String returned;

  /**
   * This field represents the uniqueness constraints of this
   * attribute.
   * See {@link AttributeDefinition.Uniqueness}
   */
  private String uniqueness;

  /**
   * The reference type of this attribute.
   */
  private String referenceType;

  /**
   * Builder class to build SCIM2Attributes.
   */
  public static class Builder
  {
    /**
     * The name of the attribute.
     */
    private String name;

    /**
     * The type of the attribute.  For the possible values, see:
     * {@link AttributeDefinition.Type}
     */
    private Type type;

    /**
     * The sub-attributes of this attribute.
     */
    private Collection<AttributeDefinition> subAttributes;

    /**
     * A boolean value indicating whether or not this attribute can have
     * multiple values.
     */
    private boolean multiValued;

    /**
     * The description of this attribute.
     */
    private String description;

    /**
     * A boolean indicating whether or not this attribute is required
     * to be present.
     */
    private boolean required;

    /**
     * A Set of canonical values that this attribute may contain.
     */
    private Set<String> canonicalValues;

    /**
     * A boolean indicated whether or not searches for this object will
     * be case exact.  If true, then this attribute will only be matched
     * if the case of the value exactly matches the search string's case.
     */
    private boolean caseExact;

    /**
     * This value indicates the mutability constraints of this attribute.
     * See {@link AttributeDefinition.Mutability}
     */
    private Mutability mutability;

    /**
     * Indicates the when this attribute will be returned as part of
     * a scim object.
     * See {@link AttributeDefinition.Returned}
     */
    private Returned returned;

    /**
     * This field represents the uniqueness constraints of this
     * attribute.
     * See {@link AttributeDefinition.Uniqueness}
     */
    private Uniqueness uniqueness;

    /**
     * The reference type of this attribute.
     */
    private String referenceType;

    /**
     * Sets the attribute name.
     *
     * @param name the attribute name.
     * @return this
     */
    public Builder setName(final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the type of the attribute.
     *
     * @param type the type of the attribute.
     * @return this.
     */
    public Builder setType(final Type type)
    {
      this.type = type;
      return this;
    }

    /**
     * Sets the sub-attributes of the attribute.
     *
     * @param subAttributes the sub-attributes of the attribute.
     * @return this.
     */
    public Builder setSubAttributes(
        final Collection<AttributeDefinition> subAttributes)
    {
      this.subAttributes = subAttributes;
      return this;
    }

    /**
     * Sets a boolean indicating if the attribute is multi-valued.
     *
     * @param multiValued a boolean indicating if the attribute is multi-valued.
     * @return this.
     */
    public Builder setMultiValued(final boolean multiValued)
    {
      this.multiValued = multiValued;
      return this;
    }

    /**
     * Sets the description of the attribute.
     *
     * @param description the description of the attribute.
     * @return this.
     */
    public Builder setDescription(final String description)
    {
      this.description = description;
      return this;
    }

    /**
     * Sets a boolean indicating if the attribute is required.
     *
     * @param required a boolean indicating if the attribute is required.
     * @return this.
     */
    public Builder setRequired(final boolean required)
    {
      this.required = required;
      return this;
    }

    /**
     * Sets the possible cononical values for this attribute.  This
     * is only relevant for multi-valued attributes.
     *
     * @param canonicalValues the possible cononical values for this attribute.
     * @return this.
     */
    public Builder setCanonicalValues(final Set<String> canonicalValues)
    {
      this.canonicalValues = canonicalValues;
      return this;
    }

    /**
     * Sets a boolean indicating if the value of the attribute should be
     * treated as case sensitive.  If true, the attribute's value should be
     * treated as case sensitive.
     *
     * @param caseExact a boolean indicating if the value of the attribute
     *     should be treated as case sensitive.
     * @return this.
     */
    public Builder setCaseExact(final boolean caseExact)
    {
      this.caseExact = caseExact;
      return this;
    }

    /**
     * Sets the mutablity constraint for the attribute.
     *
     * @param mutability the mutablity constraint for the attribute.
     * @return this.
     */
    public Builder setMutability(final Mutability mutability)
    {
      this.mutability = mutability;
      return this;
    }

    /**
     * Sets the return constraint for the attribute.
     *
     * @param returned the return constraint for the attribute.
     * @return this.
     */
    public Builder setReturned(final Returned returned)
    {
      this.returned = returned;
      return this;
    }

    /**
     * Sets the uniqueness constraint of the attribute.
     *
     * @param uniqueness the uniqueness constraint of the attribute.
     * @return this.
     */
    public Builder setUniqueness(final Uniqueness uniqueness)
    {
      this.uniqueness = uniqueness;
      return this;
    }

    /**
     * Sets the reference type for the attribute.
     *
     * @param referenceType the reference type for the attribute.
     * @return this.
     */
    public Builder setReferenceType(final String referenceType)
    {
      this.referenceType = referenceType;
      return this;
    }

    /**
     * Clear's all values in this builder, so that it could be used again.
     *
     * @return this.
     */
    public Builder clear()
    {
      this.name = null;
      this.type = null;
      this.subAttributes = null;
      this.multiValued = false;
      this.description = null;
      this.required = false;
      this.canonicalValues = null;
      this.caseExact = false;
      this.mutability = null;
      this.returned = null;
      this.uniqueness = null;
      this.referenceType = null;
      return this;
    }

    /**
     * Builds a new SCIM2Attribute.
     *
     * @return a new SCIM2Attribute.
     */
    public AttributeDefinition build()
    {
      AttributeDefinition attribute = new AttributeDefinition();
      attribute.setName(name);
      attribute.setType((type == null) ? null : type.getName());
      attribute.setSubAttributes(subAttributes);
      attribute.setMultiValued(multiValued);
      attribute.setDescription(description);
      attribute.setRequired(required);
      attribute.setCanonicalValues(canonicalValues);
      attribute.setCaseExact(caseExact);
      attribute.setMutability(
          (mutability == null) ? null : mutability.getName());
      attribute.setReturned((returned == null) ? null : returned.getName());
      attribute.setUniqueness(
          (uniqueness == null) ? null : uniqueness.getName());
      attribute.setReferenceType(referenceType);
      return attribute;
    }
  }

  /**
   * Default constructor.  Used mainly for deserialization from JSON
   */
  public AttributeDefinition()
  {

  }

  /**
   * Determines if the attribute allows multiple values.
   *
   * @return true if the attribute is multivalues, or false
   *    if it is not.
   */
  public boolean isMultiValued()
  {
    return multiValued;
  }

  /**
   * Sets whether or not the attribute is multivalued.
   *
   * @param multiValued true if the attribute is multivalues, or false
   *    if it is not.
   */
  private void setMultiValued(final boolean multiValued)
  {
    this.multiValued = multiValued;
  }

  /**
   * Gets the type of the value for this attribute.
   *
   * @return type of the value for this attribute.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Sets the type of the value for this attribute.
   *
   * @param type the type of the value for this attribute.
   */
  private void setType(final String type)
  {
    this.type = type;
  }

  /**
   * Is the attribute required.
   *
   * @return true if the attribute is required, false if it is not.
   */
  public boolean isRequired()
  {
    return required;
  }

  /**
   * Sets a boolean indicating if this attribute requires a value or not.
   *
   * @param required a boolean indicating if this attribute requires a
   *                 value or not.
   */
  private void setRequired(final boolean required)
  {
    this.required = required;
  }

  /**
   * Is the attribute's value case sensitive.
   *
   * @return true if the attributes value is case sensitive, or false
   *          if it is not.
   */
  public boolean isCaseExact()
  {
    return caseExact;
  }


  /**
   * Sets a boolean indicating whether or not the attribute's value should
   * be treated as case sensitive.
   *
   * @param caseExact a boolean indicating whether or not the attribute's
   *                  value should be treated as case sensitive.
   */
  private void setCaseExact(final boolean caseExact)
  {
    this.caseExact = caseExact;
  }

  /**
   * Gets the name of the attribute.
   *
   * @return the name of the attribute.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name of the attribute.
   *
   * @param name the name of the attribute.
   */
  private void setName(final String name)
  {
    this.name = name;
  }

  /**
   * Gets the description of the attribute.
   *
   * @return the description of the attribute.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the description of this attribute.
   *
   * @param description the discription of this attribute.
   */
  private void setDescription(final String description)
  {
    this.description = description;
  }

  /**
   * Gets the subattributes of the attribute.
   *
   * @return the subattributes of the attribute.
   */
  public Collection<AttributeDefinition> getSubAttributes()
  {
    return subAttributes;
  }

  /**
   * Sets the sub-attributes of this attribute.
   *
   * @param subAttributes the sub-attributes of this attribute.
   */
  private void setSubAttributes(
      final Collection<AttributeDefinition> subAttributes)
  {
    this.subAttributes = subAttributes;
  }

  /**
   * Gets the canonical values of the attribute.
   *
   * @return the canonical values of the attribute.
   */
  public Set<String> getCanonicalValues()
  {
    return canonicalValues;
  }

  /**
   * Sets the canonical values of the attribute.
   *
   * @param canonicalValues a set containing the possible
   *                        canonical values.
   */
  private void setCanonicalValues(final Set<String> canonicalValues)
  {
    this.canonicalValues = canonicalValues;
  }

  /**
   * Gets the mutability constraint for this attribute.
   *
   * @return the mutability constraint for this attribute.
   */
  public String getMutability()
  {
    return mutability;
  }

  /**
   * Sets the mutability constraint for this attribute.
   *
   * @param mutability the mutability constraint for this attribute.
   */
  private void setMutability(final String mutability)
  {
    this.mutability = mutability;
  }

  /**
   * Gets the return constraint for this attribute.
   *
   * @return the return constraint for this attribute.
   */
  public String getReturned()
  {
    return returned;
  }

  /**
   * Sets the return constraint for this attribute.
   *
   * @param returned the return constraint for this attribute.
   */
  private void setReturned(final String returned)
  {
    this.returned = returned;
  }

  /**
   * Gets the Uniqueness constraint fo this attribute.
   *
   * @return the Uniqueness constraint fo this attribute.
   */
  public String getUniqueness()
  {
    return uniqueness;
  }

  /**
   * Sets the Uniqueness constraint fo this attribute.
   *
   * @param uniqueness the Uniqueness constraint fo this attribute.
   */
  private void setUniqueness(final String uniqueness)
  {
    this.uniqueness = uniqueness;
  }

  /**
   * Gets the reference type for this attribute.
   *
   * @return the reference type for this attribute.
   */
  public String getReferenceType()
  {
    return referenceType;
  }

  /**
   * Sets the reference type for this attribute.
   *
   * @param referenceType the reference type for this attribute.
   */
  private void setReferenceType(final String referenceType)
  {
    this.referenceType = referenceType;
  }

  /**
   * Gets a string representation of the attribute.
   *
   * @return a string representation of the attribute.
   */
  @Override
  public String toString()
  {
    return toIndentedString("");
  }

  /**
   * Called by toString.  This is used to format the output of the object
   * a little to help readability.
   *
   * @param indent the string to use for each indent increment.  For example,
   *               one might use "  " for a 2 space indent.
   * @return a string representation of this attribute.
   */
  private String toIndentedString(final String indent)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(indent);
    builder.append("Name: ");
    builder.append(getName());
    builder.append(" Description: ");
    builder.append(getDescription());
    builder.append(" isReadOnly: ");
    builder.append(" isRequired: ");
    builder.append(isRequired());
    builder.append(" isCaseExact: ");
    builder.append(isCaseExact());
    builder.append(System.lineSeparator());
    for(AttributeDefinition a : getSubAttributes())
    {
      builder.append(a.toIndentedString(indent + "  "));
    }
    return builder.toString();
  }
}
