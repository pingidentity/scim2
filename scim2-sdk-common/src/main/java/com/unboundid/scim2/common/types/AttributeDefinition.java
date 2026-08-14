/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.messages.SearchRequest;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a SCIM 2 attribute definition as described by
 * <a href="https://datatracker.ietf.org/doc/html/rfc7643#section-7">
 * RFC 7643 Section 7</a>. An attribute is a field on a SCIM resource, and an
 * "attribute definition" details the characteristics and constraints of a
 * particular attribute. For example, the {@code userName} attribute, which is
 * stored on a {@link UserResource}, is defined as a mutable string value and
 * must always be present, among other properties.
 * <br><br>
 *
 * Attribute definitions contain the following parameters, which define the
 * purpose of the attribute and what type of data may be stored within it:
 * <ul>
 *   <li> {@code name}: The name of the attribute.
 *   <li> {@code type}: The data type of the attribute (e.g., string, boolean).
 *   <li> {@code description}:  A string summarizing/detailing the attribute.
 *   <li> {@code multiValued}:  Indicates whether the attribute may be set to a
 *                              single value or multiple values.
 *   <li> {@code required}:     Indicates whether the resource (e.g., a user)
 *                              must always have a value for this attribute.
 *   <li> {@code caseExact}:    Indicates whether searches for this attribute
 *                              value will use case-sensitive matching.
 *   <li> {@link Mutability mutability}: Indicates the circumstances in which
 *                                       the attribute value may be defined or
 *                                       redefined by a client.
 *   <li> {@link Returned returned}:     Indicates when attributes will be
 *                                       displayed in SCIM service responses.
 *   <li> {@link Uniqueness uniqueness}: Indicates whether the attribute value
 *                                       needs to be distinct at a given scope.
 *   <li> {@code canonicalValues}: Indicates whether the attribute may only be
 *                                 set to specific values (e.g., "direct" and
 *                                 "indirect" for group membership data).
 *   <li> {@code referenceTypes}:  Indicates the type of linked resource. See
 *                                 {@link #getReferenceTypes()} for more detail.
 *   <li> {@code subAttributes}:   For complex attributes, this represents a set
 *                                 of subordinate attributes stored beneath this
 *                                 attribute (e.g., {@code name.middleName}).
 * </ul>
 * <br><br>
 *
 * The following JSON represents a schema element with a single attribute
 * definition for {@code userName}, as described by RFC 7643:
 * <pre>
 * {
 *   "id": "urn:ietf:params:scim:schemas:core:2.0:User",
 *   "name": "User",
 *   "description": "User Account",
 *   "attributes": [ {
 *       "name": "userName",
 *       "type": "string",
 *       "multiValued": false,
 *       "description": "Unique identifier for the User, typically used by the
 *           user to directly authenticate to the service provider. Each User
 *           MUST include a non-empty userName value. This identifier MUST be
 *           unique across the service provider’s entire set of Users.",
 *       "required": true,
 *       "caseExact": false,
 *       "mutability": "readWrite",
 *       "returned": "default",
 *       "uniqueness": "server"
 *   } ]
 * }
 * </pre>
 *
 * This JSON shows that {@code userName}, defined on a {@link UserResource}, is
 * a single-valued {@link String} and must always be defined on all users.
 * Filters such as {@code userName eq "Alice"} will use case-insensitive
 * matching, and this value may be changed after the user is created. The
 * attribute will be returned in SCIM responses unless a {@link SearchRequest}
 * excludes it, and the value must be unique within a server or other subset of
 * users (see {@link Uniqueness#SERVER}).
 * <br><br>
 *
 * To create the attribute definition shown above, use the builder object:
 * <pre><code>
 *   AttributeDefinition userNameDefinition = new AttributeDefinition.Builder()
 *       .setName("userName")
 *       .setType(AttributeDefinition.Type.STRING)
 *       .setMultiValued(false)
 *       .setDescription("Unique identifier for the User, typically used...")
 *       .setRequired(true)
 *       .setCaseExact(false)
 *       .setMutability(AttributeDefinition.Mutability.READ_WRITE)
 *       .setReturned(AttributeDefinition.Returned.DEFAULT)
 *       .setUniqueness(AttributeDefinition.Uniqueness.SERVER)
 *       .build();
 * </code></pre>
 */
public class AttributeDefinition
{
  /**
   * This enumeration is used to describe an attribute's data type. RFC 7643
   * Section 2.2 states that the default value is {@code string}.
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
    private final String name;

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
     * Finds the Type matching the provided name. Throws a runtime exception if
     * the value cannot be found.
     *
     * @param name The name of the data type.
     * @return     The Type enum value.
     * @throws RuntimeException If the provided name is invalid.
     */
    @NotNull
    @JsonCreator
    public static Type fromName(@Nullable final String name)
    {
      for (Type type : Type.values())
      {
        if (type.getName().equalsIgnoreCase(name))
        {
          return type;
        }
      }

      throw new RuntimeException("Unknown SCIM datatype");
    }
  }

  /**
   * This enumeration is used to describe the mutability of an attribute. RFC
   * 7643 Section 2.2 states that the default value is {@code readWrite}.
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
    private final String name;

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
     * @param name The name of the mutability constraint.
     * @return     The mutability enum value.
     * @throws BadRequestException If the provided name is invalid.
     */
    @NotNull
    @JsonCreator
    public static Mutability fromName(@Nullable final String name)
        throws BadRequestException
    {
      for (Mutability mutability : Mutability.values())
      {
        if (mutability.getName().equalsIgnoreCase(name))
        {
          return mutability;
        }
      }

      throw BadRequestException.invalidSyntax(
          "Unknown SCIM mutability constraint");
    }
  }

  /**
   * This enumeration is used to describe the situations where an attribute is
   * present in SCIM service responses. This information is primarily related to
   * a {@link SearchRequest}, where the {@code attributes} and
   * {@code excludedAttributes} fields indicate the exact information that the
   * client wishes to obtain from the SCIM service.
   */
  public enum Returned
  {
    /**
     * Indicates that the attribute is returned in all circumstances. For
     * example, the {@code id} attribute is always returned to identify a SCIM
     * resource, even if it is set as "excluded" in a {@link SearchRequest}.
     */
    ALWAYS("always"),

    /**
     * Indicates that the attribute is never returned. For example, the
     * {@code password} attribute on user resources is never returned.
     */
    NEVER("never"),

    /**
     * Indicates that the attribute will generally be returned, unless it is
     * forbidden by a client's search criteria.
     */
    DEFAULT("default"),

    /**
     * Indicates that the attribute is only returned if it is explicitly
     * requested in the {@code attributes} parameter of a {@link SearchRequest}.
     */
    REQUEST("request");

    /**
     * The SCIM name for this enum.
     */
    @NotNull
    private final String name;

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
     * @param name The name of the return constraint.
     * @return     The Returned enum value.
     * @throws BadRequestException If the provided name is invalid.
     */
    @NotNull
    @JsonCreator
    public static Returned fromName(@Nullable final String name)
        throws BadRequestException
    {
      for (Returned returned : Returned.values())
      {
        if (returned.getName().equalsIgnoreCase(name))
        {
          return returned;
        }
      }

      throw BadRequestException.invalidSyntax("Unknown SCIM return constraint");
    }
  }

  /**
   * This enumeration is used to describe any uniqueness constraints on
   * attribute values. RFC 7643 Section 2.2 states that the default value is
   * {@code none}.
   */
  public enum Uniqueness
  {
    /**
     * Indicates that this attribute's value need not be unique.
     */
    NONE("none"),

    /**
     * Indicates that this attribute's value must be unique for a given server
     * or similar scope. For example, in deployments where a SCIM client can
     * directly query specific databases, the attribute value must be unique
     * amongst all resources that are stored within a single database instance.
     * <br><br>
     *
     * Alternatively, in cloud multi-tenant SCIM services, a tenant's data may
     * be available from a {@code /tenant/{tenantID}/v2/Users} endpoint. In this
     * case, this uniqueness level generally means that the value must be unique
     * within the tenant's data. For example, two companies hosted on this SCIM
     * service may each have a user with userName {@code Alice}, but one company
     * may not have two users who both have a userName of {@code Alice}.
     */
    SERVER("server"),

    /**
     * Indicates that this attribute's value must be globally unique.
     */
    GLOBAL("global");

    @NotNull
    private final String name;

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
     * Finds the uniqueness constraint by name.
     *
     * @param name The name of the uniqueness constraint.
     * @return     The uniqueness enum value.
     * @throws BadRequestException If the provided name is invalid.
     */
    @NotNull
    @JsonCreator
    public static Uniqueness fromName(@Nullable final String name)
        throws BadRequestException
    {
      for (Uniqueness uniqueness : Uniqueness.values())
      {
        if (uniqueness.getName().equalsIgnoreCase(name))
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
      isRequired = false,
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
      isRequired = false,
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
      "String attribute is case-sensitive.",
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
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final Mutability mutability;

  @NotNull
  @Attribute(description = "A single keyword that indicates when an " +
      "attribute and associated values are returned in response to a GET " +
      "request or in response to a PUT, POST, or PATCH request.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private final Returned returned;

  @NotNull
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
     * The type of the attribute (e.g., string, boolean, etc.).
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
     * A boolean indicating whether values of this attribute will be treated as
     * case-sensitive in search requests.
     */
    private boolean caseExact;

    /**
     * This field represents the mutability constraints of this attribute. If a
     * value is not set when this object is built, then the default value of
     * {@link Mutability#READ_WRITE} will be used.
     */
    @NotNull
    private Mutability mutability;

    /**
     * This field represents the situations when this attribute will be present
     * in SCIM responses. If a value is not set when this object is built, then
     * the default value of {@link Returned#DEFAULT} will be used.
     */
    @NotNull
    private Returned returned;

    /**
     * This field represents the uniqueness constraints of this attribute. If a
     * value is not set when this object is built, then the default value of
     * {@link Uniqueness#NONE} will be used.
     */
    @NotNull
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
      // Default values according to RFC 7643 Section 2.2.
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
     * @return This builder instance.
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
     * @return This builder instance.
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
     * @return This builder instance.
     */
    @NotNull
    public Builder addSubAttributes(
        @Nullable final AttributeDefinition... subAttributes)
    {
      if (subAttributes != null && subAttributes.length > 0)
      {
        if (this.subAttributes == null)
        {
          this.subAttributes = new LinkedList<>();
        }
        this.subAttributes.addAll(Arrays.asList(subAttributes));
      }
      return this;
    }

    /**
     * Sets a boolean indicating if the attribute is multi-valued.
     *
     * @param multiValued a boolean indicating if the attribute is multi-valued.
     * @return This builder instance.
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
     * @return This builder instance.
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
     * @return This builder instance.
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
     * @return This builder instance.
     */
    @NotNull
    public Builder addCanonicalValues(@Nullable final String... canonicalValues)
    {
      if (canonicalValues != null && canonicalValues.length > 0)
      {
        if (this.canonicalValues == null)
        {
          this.canonicalValues = new HashSet<>();
        }
        this.canonicalValues.addAll(Arrays.asList(canonicalValues));
      }
      return this;
    }

    /**
     * Sets a boolean indicating if the value of the attribute should be
     * treated as case-sensitive.
     *
     * @param caseExact A boolean indicating case-sensitivity.
     * @return This builder instance.
     */
    @NotNull
    public Builder setCaseExact(final boolean caseExact)
    {
      this.caseExact = caseExact;
      return this;
    }

    /**
     * Sets the mutability constraint for the attribute.
     *
     * @param mutability the mutability constraint for the attribute.
     * @return This builder instance.
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
     * @return This builder instance.
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
     * @return This builder instance.
     */
    @NotNull
    public Builder setUniqueness(@NotNull final Uniqueness uniqueness)
    {
      this.uniqueness = uniqueness;
      return this;
    }

    /**
     * Adds reference types for the attribute.
     *
     * @param referenceTypes the reference types for the attribute.
     * @return This builder instance.
     */
    @NotNull
    public Builder addReferenceTypes(@Nullable final String... referenceTypes)
    {
      if (referenceTypes != null && referenceTypes.length > 0)
      {
        if (this.referenceTypes == null)
        {
          this.referenceTypes = new HashSet<>();
        }
        this.referenceTypes.addAll(Arrays.asList(referenceTypes));
      }
      return this;
    }

    /**
     * Clears all values in this builder back to the default.
     *
     * @return This builder instance.
     */
    @NotNull
    public Builder clear()
    {
      type = Type.STRING;
      multiValued = false;
      required = false;
      caseExact = false;
      mutability = Mutability.READ_WRITE;
      returned = Returned.DEFAULT;
      uniqueness = Uniqueness.NONE;

      name = null;
      subAttributes = null;
      description = null;
      canonicalValues = null;
      referenceTypes = null;

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
          Objects.requireNonNull(name),
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
   * @param name        The attribute's name.
   * @param type        The attribute's data type.
   * @param subAttrs    The sub-attributes of the attribute.
   * @param multiValued Indicates whether the attribute is multi-valued.
   * @param description The description of this attribute.
   * @param required    Indicates whether this attribute must be present.
   * @param canonicals  A set of predefined values that this attribute may have.
   * @param caseExact   Indicates whether searches for this object will use
   *                    case-exact matching.
   * @param mutability  Indicates the mutability constraints of this attribute.
   *                    {@link Mutability#READ_WRITE} is the default value.
   * @param returned    Indicates when this attribute will be returned with a
   *                    resource. {@link Returned#DEFAULT} is the default value.
   * @param uniqueness  This field represents the uniqueness constraints of this
   *                    attribute. {@link Uniqueness#NONE} is the default value.
   * @param refTypes    The reference type of this attribute.
   */
  @JsonCreator
  AttributeDefinition(
      @NotNull @JsonProperty(value = "name", required = true)
      final String name,
      @Nullable @JsonProperty(value = "type")
      final Type type,
      @Nullable @JsonProperty(value = "subAttributes")
      final Collection<AttributeDefinition> subAttrs,
      @JsonProperty(value = "multiValued", required = true)
      final boolean multiValued,
      @Nullable @JsonProperty(value = "description")
      final String description,
      @JsonProperty(value = "required")
      final boolean required,
      @Nullable @JsonProperty(value = "canonicalValues")
      final Collection<String> canonicals,
      @JsonProperty(value = "caseExact")
      final boolean caseExact,
      @Nullable @JsonProperty(value = "mutability")
      final Mutability mutability,
      @Nullable @JsonProperty(value = "returned")
      final Returned returned,
      @Nullable @JsonProperty(value = "uniqueness")
      final Uniqueness uniqueness,
      @Nullable @JsonProperty(value = "referenceTypes")
      final Collection<String> refTypes)
  {
    this.name = name;
    this.subAttributes = subAttrs == null ? null : List.copyOf(subAttrs);
    this.multiValued = multiValued;
    this.description = description;
    this.required = required;
    this.canonicalValues = canonicals == null ? null : List.copyOf(canonicals);
    this.caseExact = caseExact;
    this.referenceTypes = refTypes == null ? null : List.copyOf(refTypes);

    // Default values as described by RFC 7643 Section 2.2.
    this.type = type == null ? Type.STRING : type;
    this.mutability = mutability == null ? Mutability.READ_WRITE : mutability;
    this.returned = returned == null ? Returned.DEFAULT : returned;
    this.uniqueness = uniqueness == null ? Uniqueness.NONE : uniqueness;
  }

  /**
   * Indicates whether the attribute allows multiple values.
   *
   * @return {@code true} if the attribute is multivalued.
   */
  public boolean isMultiValued()
  {
    return multiValued;
  }

  /**
   * Fetches the data type for values of this attribute.
   *
   * @return The attribute's data type.
   */
  @NotNull
  public Type getType()
  {
    return type;
  }

  /**
   * Indicates whether the attribute must always have a value on a resource.
   *
   * @return {@code true} if the attribute is required.
   */
  public boolean isRequired()
  {
    return required;
  }

  /**
   * Indicates whether values of the attribute will be treated as
   * case-sensitive.
   *
   * @return {@code true} if the attribute's values are case-sensitive.
   */
  public boolean isCaseExact()
  {
    return caseExact;
  }

  /**
   * Gets the name of the attribute.
   *
   * @return The name of the attribute.
   */
  @NotNull
  public String getName()
  {
    return name;
  }

  /**
   * Gets the description of the attribute.
   *
   * @return The description of the attribute.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Fetches the subordinate attributes of a complex attribute.
   *
   * @return The sub-attributes.
   */
  @Nullable
  public Collection<AttributeDefinition> getSubAttributes()
  {
    return subAttributes;
  }

  /**
   * Gets the canonical values of the attribute.
   *
   * @return The canonical values of the attribute.
   */
  @Nullable
  public Collection<String> getCanonicalValues()
  {
    return canonicalValues;
  }

  /**
   * Fetches the mutability constraint of this attribute.
   *
   * @return The mutability constraint.
   */
  @NotNull
  public Mutability getMutability()
  {
    return mutability;
  }

  /**
   * Fetches the return constraint of this attribute.
   *
   * @return The return constraint.
   */
  @NotNull
  public Returned getReturned()
  {
    return returned;
  }

  /**
   * Fetches the uniqueness constraint of this attribute.
   *
   * @return The uniqueness constraint.
   */
  @NotNull
  public Uniqueness getUniqueness()
  {
    return uniqueness;
  }

  /**
   * Fetches the reference types of this attribute, if any. This method is used
   * for SCIM attributes that act as a pointer (or reference) to other data. The
   * following types of references are defined:
   * <ul>
   *   <li> {@code external}: A reference to an external piece of data. For
   *                          example, the user {@code profileUrl} attribute is
   *                          a URL pointing to an external user profile.
   *   <li> {@code uri}:      A reference to an endpoint or a schema URN. For
   *                          example, {@link ResourceTypeResource} contains the
   *                          {@code endpoint} and {@code schema} fields which
   *                          are both a {@code uri} reference type.
   *   <li> A SCIM resource:  A reference to a type of SCIM resource, (e.g.,
   *                          {@code Group}. For example, a {@link Member} has a
   *                          {@code $ref} field that represents a user or group
   *                          resource.
   * </ul>
   *
   * @return The reference types of this attribute.
   */
  @Nullable
  public Collection<String> getReferenceTypes()
  {
    return referenceTypes;
  }

  /**
   * Retrieves a string representation of this attribute definition.
   *
   * @return A string representation of this attribute definition.
   */
  @Override
  @NotNull
  public String toString()
  {
    return JsonUtils.getObjectWriter().withDefaultPrettyPrinter()
        .writeValueAsString(this);
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

    return o instanceof AttributeDefinition that
        && caseExact == that.caseExact
        && multiValued == that.multiValued
        && required == that.required
        && Objects.equals(canonicalValues, that.canonicalValues)
        && Objects.equals(description, that.description)
        && Objects.equals(mutability, that.mutability)
        && Objects.equals(name, that.name)
        && Objects.equals(referenceTypes, that.referenceTypes)
        && Objects.equals(returned, that.returned)
        && Objects.equals(subAttributes, that.subAttributes)
        && Objects.equals(type, that.type)
        && Objects.equals(uniqueness, that.uniqueness);
  }

  /**
   * Retrieves a hash code for this attribute definition.
   *
   * @return  A hash code for this attribute definition.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(caseExact, multiValued, required, canonicalValues,
        description, mutability, name, referenceTypes, returned, subAttributes,
        type, uniqueness);
  }
}
