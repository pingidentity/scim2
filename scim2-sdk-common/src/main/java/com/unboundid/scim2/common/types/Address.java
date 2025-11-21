/*
 * Copyright 2015-2025 Ping Identity Corporation
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
 * Address for the user.
 */
public class Address
{
  @Nullable
  @Attribute(description = "The full mailing address, formatted for " +
      "display or use with a mailing label. This attribute MAY " +
      "contain newlines.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String formatted;

  @Nullable
  @Attribute(description = "The full street address component, which " +
      "may include house number, street name, PO BOX, and multi-line " +
      "extended street address information. This attribute MAY contain " +
      "newlines.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String streetAddress;

  @Nullable
  @Attribute(description = "The city or locality component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String locality;

  @Nullable
  @Attribute(description = "The state or region component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String region;

  @Nullable
  @Attribute(description = "The zipcode or postal code component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String postalCode;

  @Nullable
  @Attribute(description = "The country name component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String country;

  @Nullable
  @Attribute(description = "A label indicating the attribute's " +
      "function; e.g., 'work' or 'home'.",
      isRequired = false,
      isCaseExact = false,
      canonicalValues = { "work", "home", "other" },
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String type;

  @Nullable
  @Attribute(description = "A Boolean value indicating the 'primary' " +
      "or preferred attribute value for this attribute, e.g., the " +
      "preferred address. The primary attribute value 'true' MUST appear " +
      "no more than once.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT)
  private Boolean primary;

  /**
   * Retrieves the full mailing address, formatted for display or use with a
   * mailing label. This attribute MAY contain newlines.
   *
   * @return The full mailing address
   */
  @Nullable
  public String getFormatted()
  {
    return formatted;
  }

  /**
   * Specifies the full mailing address, formatted for display or use with a
   * mailing label. This attribute MAY contain newlines.
   *
   * @param formatted The full mailing address
   * @return This object.
   */
  @NotNull
  public Address setFormatted(@Nullable final String formatted)
  {
    this.formatted = formatted;
    return this;
  }

  /**
   * Retrieves the full street address component, which may include house
   * number, street name, PO BOX, and multi-line extended street address
   * information. This attribute MAY contain newlines.
   *
   * @return the full street address component.
   */
  @Nullable
  public String getStreetAddress()
  {
    return streetAddress;
  }

  /**
   * Specifies the full street address component, which may include house
   * number, street name, PO BOX, and multi-line extended street address
   * information. This attribute MAY contain newlines.
   *
   * @param streetAddress the full street address component.
   * @return This object.
   */
  @NotNull
  public Address setStreetAddress(@Nullable final String streetAddress)
  {
    this.streetAddress = streetAddress;
    return this;
  }

  /**
   * Retrieves the city or locality component.
   *
   * @return The city or locality component.
   */
  @Nullable
  public String getLocality()
  {
    return locality;
  }

  /**
   * Specifies the city or locality component.
   *
   * @param locality The city or locality component.
   * @return This object.
   */
  @NotNull
  public Address setLocality(@Nullable final String locality)
  {
    this.locality = locality;
    return this;
  }

  /**
   * Specifies the state or region component.
   *
   * @return The state or region component.
   */
  @Nullable
  public String getRegion()
  {
    return region;
  }

  /**
   * Retrieves the state or region component.
   *
   * @param region The state or region component.
   * @return This object.
   */
  @NotNull
  public Address setRegion(@Nullable final String region)
  {
    this.region = region;
    return this;
  }

  /**
   * Retrieves the zipcode or postal code component.
   *
   * @return The zipcode or postal code component.
   */
  @Nullable
  public String getPostalCode()
  {
    return postalCode;
  }

  /**
   * Specifies the zipcode or postal code component.
   *
   * @param postalCode The zipcode or postal code component.
   * @return This object.
   */
  @NotNull
  public Address setPostalCode(@Nullable final String postalCode)
  {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Retrieves the country name component.
   *
   * @return The country name component.
   */
  @Nullable
  public String getCountry()
  {
    return country;
  }

  /**
   * Specifies the country name component.
   *
   * @param country The country name component.
   * @return This object.
   */
  @NotNull
  public Address setCountry(@Nullable final String country)
  {
    this.country = country;
    return this;
  }

  /**
   * Retrieves the label indicating the attribute's function;
   * for example, 'work' or 'home'.
   *
   * @return A label indicating the attribute's function; for example, 'work' or
   * 'home'.
   */
  @Nullable
  public String getType()
  {
    return type;
  }

  /**
   * Specifies the label indicating the attribute's function;
   * for example, 'work' or 'home'.
   *
   * @param type a label indicating the attribute's function.
   * @return This object.
   */
  @NotNull
  public Address setType(@Nullable final String type)
  {
    this.type = type;
    return this;
  }

  /**
   * Retrieves the Boolean value indicating the 'primary' or preferred attribute
   * value for this attribute, for example, the preferred address. The primary
   * attribute value 'true' MUST appear no more than once.
   *
   * @return The Boolean value indicating the 'primary' or preferred attribute
   * value for this attribute
   */
  @Nullable
  public Boolean getPrimary()
  {
    return primary;
  }

  /**
   * Specifies the Boolean value indicating the 'primary' or preferred attribute
   * value for this attribute, for example, the preferred address. The primary
   * attribute value 'true' MUST appear no more than once.
   *
   * @param primary The Boolean value indicating the 'primary' or preferred
   *                attribute value for this attribute
   * @return This object.
   */
  @NotNull
  public Address setPrimary(@Nullable final Boolean primary)
  {
    this.primary = primary;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this address.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this address, or
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

    Address address = (Address) o;
    if (!Objects.equals(formatted, address.formatted))
    {
      return false;
    }
    if (!Objects.equals(streetAddress, address.streetAddress))
    {
      return false;
    }
    if (!Objects.equals(locality, address.locality))
    {
      return false;
    }
    if (!Objects.equals(region, address.region))
    {
      return false;
    }
    if (!Objects.equals(postalCode, address.postalCode))
    {
      return false;
    }
    if (!Objects.equals(country, address.country))
    {
      return false;
    }
    if (!Objects.equals(type, address.type))
    {
      return false;
    }
    return Objects.equals(primary, address.primary);
  }

  /**
   * Retrieves a hash code for this address.
   *
   * @return  A hash code for this address.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(formatted, streetAddress, locality, region, postalCode,
        country, type, primary);
  }
}
