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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.util.Objects;

/**
 * The components of the user's full name.
 */
public class Name
{
  @Nullable
  @Attribute(description = "The full name, including all middle " +
      "names, titles, and suffixes as appropriate, formatted for display " +
      "(for example, Ms. Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String formatted;

  @Nullable
  @Attribute(description = "The family name of the User, or Last " +
      "Name in most Western languages (for example, Jensen given the full name Ms. " +
      "Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String familyName;

  @Nullable
  @Attribute(description = "The given name of the User, or First Name " +
      "in most Western languages (for example, Barbara given the full name Ms. " +
      "Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String givenName;

  @Nullable
  @Attribute(description = "The middle name(s) of the User (for example, " +
      "Robert given the full name Ms. Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String middleName;

  @Nullable
  @Attribute(description = "The honorific prefix(es) of the User, or " +
      "Title in most Western languages (for example, Ms. given the full name " +
      "Ms. Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String honorificPrefix;

  @Nullable
  @Attribute(description = "The honorific suffix(es) of the User, or " +
      "Suffix in most Western languages (for example, III. given the full name " +
      "Ms. Barbara J Jensen, III.)",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String honorificSuffix;

  /**
   * Retrieves the full name, including all middle names, titles, and
   * suffixes as appropriate, formatted for display (for example, Ms. Barbara J
   * Jensen, III.).
   *
   * @return The full name, including all middle names, titles, and
   * suffixes as appropriate, formatted for display.
   */
  @Nullable
  public String getFormatted()
  {
    return formatted;
  }

  /**
   * Specifies the full name, including all middle names, titles, and
   * suffixes as appropriate, formatted for display (for example, Ms. Barbara J
   * Jensen, III.).
   *
   * @param formatted The full name, including all middle names, titles, and
   * suffixes as appropriate, formatted for display.
   * @return This object.
   */
  @NotNull
  public Name setFormatted(@Nullable final String formatted)
  {
    this.formatted = formatted;
    return this;
  }

  /**
   * Retrieves the family name of the User, or Last Name in most Western
   * languages (for example, Jensen given the full name Ms. Barbara J Jensen, III.).
   *
   * @return The family name of the User, or Last Name in most Western
   * languages.
   */
  @Nullable
  public String getFamilyName()
  {
    return familyName;
  }

  /**
   * Specifies the family name of the User, or Last Name in most Western
   * languages (for example, Jensen given the full name Ms. Barbara J Jensen, III.).
   *
   * @param familyName The family name of the User, or Last Name in most Western
   * languages.
   * @return This object.
   */
  @NotNull
  public Name setFamilyName(@Nullable final String familyName)
  {
    this.familyName = familyName;
    return this;
  }

  /**
   * Retrieves the given name of the User, or First Name in most Western
   * languages (for example, Barbara given the full name Ms. Barbara J Jensen, III.).
   *
   * @return The given name of the User, or First Name in most Western
   * languages.
   */
  @Nullable
  public String getGivenName()
  {
    return givenName;
  }

  /**
   * Specifies the given name of the User, or First Name in most Western
   * languages (for example, Barbara given the full name Ms. Barbara J Jensen, III.).
   *
   * @param givenName The given name of the User, or First Name in most Western
   * languages.
   * @return This object.
   */
  @NotNull
  public Name setGivenName(@Nullable final String givenName)
  {
    this.givenName = givenName;
    return this;
  }

  /**
   * Retrieves the middle name(s) of the User (for example, Robert given the full name
   * Ms. Barbara J Jensen, III.).
   *
   * @return the middle name(s) of the User.
   */
  @Nullable
  public String getMiddleName()
  {
    return middleName;
  }

  /**
   * Specifies the middle name(s) of the User (for example, Robert given the full name
   * Ms. Barbara J Jensen, III.).
   *
   * @param middleName The middle name(s) of the User.
   * @return This object.
   */
  @NotNull
  public Name setMiddleName(@Nullable final String middleName)
  {
    this.middleName = middleName;
    return this;
  }

  /**
   * Retrieves the honorific prefix(es) of the User, or Title in most Western
   * languages (for example, Ms. given the full name Ms. Barbara J Jensen, III.).
   *
   * @return The honorific prefix(es) of the User, or Title in most Western
   * languages.
   */
  @Nullable
  public String getHonorificPrefix()
  {
    return honorificPrefix;
  }

  /**
   * Specifies the honorific prefix(es) of the User, or Title in most Western
   * languages (for example, Ms. given the full name Ms. Barbara J Jensen, III.).
   *
   * @param honorificPrefix The honorific prefix(es) of the User, or Title in
   *                        most Western languages.
   * @return This object.
   */
  @NotNull
  public Name setHonorificPrefix(@Nullable final String honorificPrefix)
  {
    this.honorificPrefix = honorificPrefix;
    return this;
  }

  /**
   * Retrieves the honorific suffix(es) of the User, or Suffix in most Western
   * languages (for example, III. given the full name Ms. Barbara J Jensen, III.).
   *
   * @return the honorific suffix(es) of the User, or Suffix in most Western
   * languages.
   */
  @Nullable
  public String getHonorificSuffix()
  {
    return honorificSuffix;
  }

  /**
   * Specifies the honorific suffix(es) of the User, or Suffix in most Western
   * languages (for example, III. given the full name Ms. Barbara J Jensen, III.).
   *
   * @param honorificSuffix the honorific suffix(es) of the User, or Suffix in
   *                        most Western languages.
   * @return This object.
   */
  @NotNull
  public Name setHonorificSuffix(@Nullable final String honorificSuffix)
  {
    this.honorificSuffix = honorificSuffix;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this user's name.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this name, or
   *            {@code false} if not.
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

    Name name = (Name) o;
    if (!Objects.equals(formatted, name.formatted))
    {
      return false;
    }
    if (!Objects.equals(familyName, name.familyName))
    {
      return false;
    }
    if (!Objects.equals(givenName, name.givenName))
    {
      return false;
    }
    if (!Objects.equals(middleName, name.middleName))
    {
      return false;
    }
    if (!Objects.equals(honorificPrefix, name.honorificPrefix))
    {
      return false;
    }
    return Objects.equals(honorificSuffix, name.honorificSuffix);
  }

  /**
   * Retrieves a hash code for this name object.
   *
   * @return  A hash code for this name object.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(formatted, familyName, givenName, middleName,
        honorificPrefix, honorificSuffix);
  }
}
