/*
 * Copyright 2015-2024 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

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
    DATETIME("dateTime"),

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

    @NotNull
    private String name;

    /**
     * Constructs an attribute type object.
     *
     * @param name the name (used in SCIM schemas) of the object.
     */
    Type(@NotNull final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the type.
     *
     * @return the name of the type.
     */
    @NotNull
    @JsonValue
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
    @NotNull
    @JsonCreator
    public static Type fromName(@Nullable final String name)
    {
      for(Type type : Type.values())
      {
        if(type.getName().equalsIgnoreCase(name))
        {
          return type;
        }
      }

      throw new RuntimeException("Unknown SCIM datatype");
    }
  }

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
    @NotNull
    private String name;

    /**
     * Mutability enum private constructor.
     *
     * @param name the name of the mutability constraint.
     */
    Mutability(@NotNull final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the mutability constraint.
     *
     * @return the name of the mutability constraint.
     */
    @NotNull
    @JsonValue
    public String getName()
    {
      return name;
    }

    /**
     * Finds the mutability constraint by name.
     *
     * @param name the name of the mutability constraint.
     * @return the enum value for the given name.
     * @throws BadRequestException if the name of the mutability constraint is
     *                             invalid.
     */
    @NotNull
    @JsonCreator
    public static Mutability fromName(@Nullable final String name)
        throws BadRequestException
    {
      for(Mutability mutability : Mutability.values())
      {
        if(mutability.getName().equalsIgnoreCase(name))
        {
          return mutability;
        }
      }

      throw BadRequestException.invalidSyntax(
          "Unknown SCIM mutability constraint");
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
    @NotNull
    private String name;

    /**
     * Returned enum private constructor.
     *
     * @param name the name of the return constraint.
     */
    Returned(@NotNull final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the return constraint.
     *
     * @return the name of the return constraint.
     */
    @NotNull
    @JsonValue
    public String getName()
    {
      return name;
    }

    /**
     * Finds the return constraint by name.
     *
     * @param name the name of the return constraint.
     * @return the enum value for the given name.
     * @throws BadRequestException if the name of the return constraint is
     *                             invalid.
     */
    @NotNull
    @JsonCreator
    public static Returned fromName(@Nullable final String name)
        throws BadRequestException
    {
      for(Returned returned : Returned.values())
      {
        if(returned.getName().equalsIgnoreCase(name))
        {
          return returned;
        }
      }

      throw BadRequestException.invalidSyntax("Unknown SCIM return constraint");
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

    @NotNull
    private String name;

    /**
     * Uniqueness enum private constructor.
     *
     * @param name the name of the uniqueness constraint.
     */
    Uniqueness(@NotNull final String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the uniqueness constraint.
     *
     * @return the name of the uniqueness constraint.
     */
    @NotNull
    @JsonValue
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
     * @throws BadRequestException if the name of the uniqueness constraint is
     *                             invalid.
     */
    @NotNull
    @JsonCreator
    public static Uniqueness fromName(@Nullable final String name)
        throws BadRequestException
    {
      for(Uniqueness uniqueness : Uniqueness.values())
      {
        if(uniqueness.getName().equalsIgnoreCase(name))
        {
          return uniqueness;
        }
      }

      throw BadRequestException.invalidSyntax(
          "Unknown SCIM uniquenessConstraint");
    }
  }

  @NotNull
  @Attribute(description = "The attribute's name.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String name;

  @NotNull
  @Attribute(description = "The attribute's data type.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final Type type;

  @Nullable
  @Attribute(description = "When an attribute is of type \"complex\", " +
      "\"subAttributes\" defines set of sub-attributes.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = AttributeDefinition.class)
  private final Collection<AttributeDefinition> subAttributes;

  @Attribute(description = "Boolean value indicating the attribute's " +
      "plurality.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final boolean multiValued;

  @Nullable
  @Attribute(description = "The attribute's human readable description.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final String description;

  @Attribute(description = "A Boolean value that specifies if the " +
      "attribute is required.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final boolean required;

  @Nullable
  @Attribute(description = "A collection of suggested canonical values " +
      "that MAY be used.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = String.class)
  private final Collection<String> canonicalValues;

  @Attribute(description = "A Boolean value that specifies if the " +
      "String attribute is case sensitive.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final boolean caseExact;

  @NotNull
  @Attribute(description = "A single keyword indicating the " +
      "circumstances under which the value of the attribute can be " +
      "(re)defined.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final Mutability mutability;

  @NotNull
  @Attribute(description = "A single keyword that indicates when an " +
      "attribute and associated values are returned in response to a GET " +
      "request or in response to a PUT, POST, or PATCH request.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final Returned returned;

  @Nullable
  @Attribute(description = "A single keyword value that specifies how " +
      "the service provider enforces uniqueness of attribute values.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final Uniqueness uniqueness;

  @Nullable
  @Attribute(description = "A multi-valued array of JSON strings that " +
      "indicate the SCIM resource types that may be referenced.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = String.class)
  private final Collection<String> referenceTypes;

  /**
   * Builder class to build a SCIM attribute definition.
   */
  public static class Builder
  {
    /**
     * The name of the attribute.
     */
    @Nullable
    private String name;

    /**
     * The type of the attribute.  For the possible values, see:
     * {@link AttributeDefinition.Type}
     */
    @NotNull
    private Type type;

    /**
     * The sub-attributes of this attribute.
     */
    @Nullable
    private Collection<AttributeDefinition> subAttributes;

    /**
     * A boolean value indicating whether or not this attribute can have
     * multiple values.
     */
    private boolean multiValued;

    /**
     * The description of this attribute.
     */
    @Nullable
    private String description;

    /**
     * A boolean indicating whether or not this attribute is required
     * to be present.
     */
    private boolean required;

    /**
     * A Set of canonical values that this attribute may contain.
     */
    @Nullable
    private Collection<String> canonicalValues;

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
    @NotNull
    private Mutability mutability;

    /**
     * Indicates the when this attribute will be returned as part of
     * a scim object.
     * See {@link AttributeDefinition.Returned}
     */
    @NotNull
    private Returned returned;

    /**
     * This field represents the uniqueness constraints of this
     * attribute.
     *
     * @see AttributeDefinition.Uniqueness
     */
    @Nullable
    private Uniqueness uniqueness;

    /**
     * The reference types of this attribute.
     */
    @Nullable
    private Collection<String> referenceTypes;

    /**
     * Create a new builder.
     */
    public Builder()
    {
      // Defaults according to
      // https://tools.ietf.org/html/draft-ietf-scim-core-schema-20#section-2.2
      type = Type.STRING;
      caseExact = false;
      mutability = Mutability.READ_WRITE;
      returned = Returned.DEFAULT;
      uniqueness = Uniqueness.NONE;
    }

    /**
     * Sets the attribute name.
     *
     * @param name the attribute name.
     * @return this
     */
    @NotNull
    public Builder setName(@Nullable final String name)
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
    @NotNull
    public Builder setType(@NotNull final Type type)
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
    @NotNull
    public Builder addSubAttributes(
        @Nullable final AttributeDefinition... subAttributes)
    {
      if(subAttributes != null && subAttributes.length > 0)
      {
        if (this.subAttributes == null)
        {
          this.subAttributes = new LinkedList<AttributeDefinition>();
        }
        this.subAttributes.addAll(Arrays.asList(subAttributes));
      }
      return this;
    }

    /**
     * Sets a boolean indicating if the attribute is multi-valued.
     *
     * @param multiValued a boolean indicating if the attribute is multi-valued.
     * @return this.
     */
    @NotNull
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
    @NotNull
    public Builder setDescription(@Nullable final String description)
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
    @NotNull
    public Builder setRequired(final boolean required)
    {
      this.required = required;
      return this;
    }

    /**
     * Adds possible canonical values for this attribute. This is only relevant
     * for multi-valued attributes.
     *
     * @param canonicalValues the possible canonical values for this attribute.
     * @return this.
     */
    @NotNull
    public Builder addCanonicalValues(@Nullable final String... canonicalValues)
    {
      if(canonicalValues != null && canonicalValues.length > 0)
      {
        if (this.canonicalValues == null)
        {
          this.canonicalValues = new HashSet<String>();
        }
        this.canonicalValues.addAll(Arrays.asList(canonicalValues));
      }
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
    @NotNull
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
    @NotNull
    public Builder setMutability(@NotNull final Mutability mutability)
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
    @NotNull
    public Builder setReturned(@NotNull final Returned returned)
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
    @NotNull
    public Builder setUniqueness(@Nullable final Uniqueness uniqueness)
    {
      this.uniqueness = uniqueness;
      return this;
    }

    /**
     * Adds reference types for the attribute.
     *
     * @param referenceTypes the reference types for the attribute.
     * @return this.
     */
    @NotNull
    public Builder addReferenceTypes(@Nullable final String... referenceTypes)
    {
      if(referenceTypes != null && referenceTypes.length > 0)
      {
        if (this.referenceTypes == null)
        {
          this.referenceTypes = new HashSet<String>();
        }
        this.referenceTypes.addAll(Arrays.asList(referenceTypes));
      }
      return this;
    }

    /**
     * Clear's all values in this builder, so that it could be used again.
     *
     * @return this.
     */
    @NotNull
    public Builder clear()
    {
      this.name = null;
      this.type = Type.STRING;
      this.subAttributes = null;
      this.multiValued = false;
      this.description = null;
      this.required = false;
      this.canonicalValues = null;
      this.caseExact = false;
      this.mutability = Mutability.READ_WRITE;
      this.returned = Returned.DEFAULT;
      this.uniqueness = Uniqueness.NONE;
      this.referenceTypes = null;
      return this;
    }

    /**
     * Builds a new SCIM Attribute Definition.
     *
     * @return a new Attribute Definition.
     */
    @NotNull
    public AttributeDefinition build()
    {
      return new AttributeDefinition(
          name,
          type,
          subAttributes,
          multiValued,
          description,
          required,
          canonicalValues,
          caseExact,
          mutability,
          returned,
          uniqueness,
          referenceTypes);
    }
  }

  /**
   * Create a new Attribute Definition.
   *
   * @param name The attribute's name.
   * @param type The attribute's data type.
   * @param subAttributes The sub-attributes of the attribute.
   * @param multiValued A boolean indicating if the attribute is multi-valued.
   * @param description The description of this attribute.
   * @param required A boolean indicating whether or not this attribute is
   *                 required to be present.
   * @param canonicalValues A Set of canonical values that this attribute may
   *                        contain.
   * @param caseExact A boolean indicated whether or not searches for this
   *                  object will be case exact.
   * @param mutability This value indicates the mutability constraints of this
   *                   attribute.
   * @param returned Indicates the when this attribute will be returned as part
   *                 of a scim object.
   * @param uniqueness This field represents the uniqueness constraints of this
   *                   attribute.
   * @param referenceTypes The reference type of this attribute.
   */
  @JsonCreator
  AttributeDefinition(
      @NotNull @JsonProperty(value = "name", required = true)
      final String name,
      @NotNull @JsonProperty(value = "type", required = true)
      final Type type,
      @Nullable @JsonProperty(value = "subAttributes")
      final Collection<AttributeDefinition> subAttributes,
      @JsonProperty(value = "multiValued", required = true)
      final boolean multiValued,
      @Nullable @JsonProperty(value = "description")
      final String description,
      @JsonProperty(value = "required", required = true)
      final boolean required,
      @Nullable @JsonProperty(value = "canonicalValues")
      final Collection<String> canonicalValues,
      @JsonProperty(value = "caseExact")
      final boolean caseExact,
      @NotNull @JsonProperty(value = "mutability",  required = true)
      final Mutability mutability,
      @NotNull @JsonProperty(value = "returned", required = true)
      final Returned returned,
      @Nullable @JsonProperty(value = "uniqueness")
      final Uniqueness uniqueness,
      @Nullable @JsonProperty(value = "referenceTypes")
      final Collection<String> referenceTypes)
  {
    this.name = name;
    this.type = type;
    this.subAttributes = subAttributes == null ?
        null : Collections.unmodifiableList(
        new ArrayList<AttributeDefinition>(subAttributes));
    this.multiValued = multiValued;
    this.description = description;
    this.required = required;
    this.canonicalValues = canonicalValues == null ?
        null : Collections.unmodifiableList(
        new ArrayList<String>(canonicalValues));
    this.caseExact = caseExact;
    this.mutability = mutability;
    this.returned = returned;
    this.uniqueness = uniqueness;
    this.referenceTypes = referenceTypes == null ?
        null : Collections.unmodifiableList(
        new ArrayList<String>(referenceTypes));
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
   * Gets the type of the value for this attribute.
   *
   * @return type of the value for this attribute.
   */
  @NotNull
  public Type getType()
  {
    return type;
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
   * Gets the name of the attribute.
   *
   * @return the name of the attribute.
   */
  @NotNull
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of the attribute.
   *
   * @return the description of the attribute.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the sub-attributes of the attribute.
   *
   * @return the sub-attributes of the attribute.
   */
  @Nullable
  public Collection<AttributeDefinition> getSubAttributes()
  {
    return subAttributes;
  }

  /**
   * Gets the canonical values of the attribute.
   *
   * @return the canonical values of the attribute.
   */
  @Nullable
  public Collection<String> getCanonicalValues()
  {
    return canonicalValues;
  }

  /**
   * Gets the mutability constraint for this attribute.
   *
   * @return the mutability constraint for this attribute.
   */
  @NotNull
  public Mutability getMutability()
  {
    return mutability;
  }

  /**
   * Gets the return constraint for this attribute.
   *
   * @return the return constraint for this attribute.
   */
  @NotNull
  public Returned getReturned()
  {
    return returned;
  }

  /**
   * Gets the Uniqueness constraint fo this attribute.
   *
   * @return the Uniqueness constraint fo this attribute.
   */
  @Nullable
  public Uniqueness getUniqueness()
  {
    return uniqueness;
  }

  /**
   * Gets the reference types for this attribute.
   *
   * @return the reference types for this attribute.
   */
  @Nullable
  public Collection<String> getReferenceTypes()
  {
    return referenceTypes;
  }

  /**
   * Gets a string representation of the attribute.
   *
   * @return a string representation of the attribute.
   */
  @Override
  @NotNull
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
  @NotNull
  private String toIndentedString(@NotNull final String indent)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(indent);
    builder.append("Name: ");
    builder.append(getName());
    builder.append(" Description: ");
    builder.append(getDescription());
    builder.append(" Mutability: ");
    builder.append(getMutability());
    builder.append(" isRequired: ");
    builder.append(isRequired());
    builder.append(" isCaseExact: ");
    builder.append(isCaseExact());
    builder.append(System.lineSeparator());
    if (getSubAttributes() != null)
    {
      for (AttributeDefinition a : getSubAttributes())
      {
        builder.append(a.toIndentedString(indent + "  "));
      }
    }
    return builder.toString();
  }

  /**
   * Indicates whether the provided object is equal to this attribute
   * definition.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this attribute
   *            definition, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    AttributeDefinition that = (AttributeDefinition) o;

    if (caseExact != that.caseExact)
    {
      return false;
    }
    if (multiValued != that.multiValued)
    {
      return false;
    }
    if (required != that.required)
    {
      return false;
    }
    if (canonicalValues != null ?
        !canonicalValues.equals(that.canonicalValues) :
        that.canonicalValues != null)
    {
      return false;
    }
    if (description != null ? !description.equals(that.description) :
        that.description != null)
    {
      return false;
    }
    if (mutability != null ? !mutability.equals(that.mutability) :
        that.mutability != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (referenceTypes != null ? !referenceTypes.equals(that.referenceTypes) :
        that.referenceTypes != null)
    {
      return false;
    }
    if (returned != null ? !returned.equals(that.returned) :
        that.returned != null)
    {
      return false;
    }
    if (subAttributes != null ? !subAttributes.equals(that.subAttributes) :
        that.subAttributes != null)
    {
      return false;
    }
    if (type != null ? !type.equals(that.type) : that.type != null)
    {
      return false;
    }
    if (uniqueness != null ? !uniqueness.equals(that.uniqueness) :
        that.uniqueness != null)
    {
      return false;
    }

    return true;
  }

  /**
   * Retrieves a hash code for this attribute definition.
   *
   * @return  A hash code for this attribute definition.
   */
  @Override
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (subAttributes != null ?
        subAttributes.hashCode() : 0);
    result = 31 * result + (multiValued ? 1 : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (required ? 1 : 0);
    result = 31 * result + (canonicalValues != null ?
        canonicalValues.hashCode() : 0);
    result = 31 * result + (caseExact ? 1 : 0);
    result = 31 * result + (mutability != null ? mutability.hashCode() : 0);
    result = 31 * result + (returned != null ? returned.hashCode() : 0);
    result = 31 * result + (uniqueness != null ? uniqueness.hashCode() : 0);
    result = 31 * result + (referenceTypes != null ?
        referenceTypes.hashCode() : 0);
    return result;
  }
}
