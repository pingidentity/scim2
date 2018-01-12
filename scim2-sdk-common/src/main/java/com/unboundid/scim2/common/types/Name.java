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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.annotations.Attribute;

/**
 * The components of the user's full name.
 */
public class Name
{
  @Attribute(description = "The full name, including all middle " +
      "names, titles, and suffixes as appropriate, formatted for display " +
      "(for example, Ms. Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String formatted;

  @Attribute(description = "The family name of the User, or Last " +
      "Name in most Western languages (for example, Jensen given the full name Ms. " +
      "Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String familyName;

  @Attribute(description = "The given name of the User, or First Name " +
      "in most Western languages (for example, Barbara given the full name Ms. " +
      "Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String givenName;

  @Attribute(description = "The middle name(s) of the User (for example, " +
      "Robert given the full name Ms. Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String middleName;

  @Attribute(description = "The honorific prefix(es) of the User, or " +
      "Title in most Western languages (for example, Ms. given the full name " +
      "Ms. Barbara J Jensen, III.).",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String honorificPrefix;

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
  public Name setFormatted(final String formatted)
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
  public Name setFamilyName(final String familyName)
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
  public Name setGivenName(final String givenName)
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
  public Name setMiddleName(final String middleName)
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
  public Name setHonorificPrefix(final String honorificPrefix)
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
  public Name setHonorificSuffix(final String honorificSuffix)
  {
    this.honorificSuffix = honorificSuffix;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
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

    if (formatted != null ? !formatted.equals(name.formatted) :
        name.formatted != null)
    {
      return false;
    }
    if (familyName != null ? !familyName.equals(name.familyName) :
        name.familyName != null)
    {
      return false;
    }
    if (givenName != null ? !givenName.equals(name.givenName) :
        name.givenName != null)
    {
      return false;
    }
    if (middleName != null ? !middleName.equals(name.middleName) :
        name.middleName != null)
    {
      return false;
    }
    if (honorificPrefix != null ? !honorificPrefix.equals(
        name.honorificPrefix) : name.honorificPrefix != null)
    {
      return false;
    }
    return !(honorificSuffix != null ? !honorificSuffix.equals(
        name.honorificSuffix) : name.honorificSuffix != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = formatted != null ? formatted.hashCode() : 0;
    result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
    result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
    result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
    result = 31 * result + (honorificPrefix != null ?
        honorificPrefix.hashCode() : 0);
    result = 31 * result + (honorificSuffix != null ?
        honorificSuffix.hashCode() : 0);
    return result;
  }
}
