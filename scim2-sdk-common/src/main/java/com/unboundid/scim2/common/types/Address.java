/*
 * Copyright 2015-2021 Ping Identity Corporation
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
 * Address for the user.
 */
public class Address
{
  @Attribute(description = "The full mailing address, formatted for " +
      "display or use with a mailing label. This attribute MAY " +
      "contain newlines.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String formatted;

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

  @Attribute(description = "The city or locality component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String locality;

  @Attribute(description = "The state or region component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String region;

  @Attribute(description = "The zipcode or postal code component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String postalCode;

  @Attribute(description = "The country name component.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String country;

  @Attribute(description = "A label indicating the attribute's " +
      "function; e.g., 'work' or 'home'.",
      isRequired = false,
      isCaseExact = false,
      canonicalValues = { "work", "home", "other" },
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String type;

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
  public Address setFormatted(final String formatted)
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
  public Address setStreetAddress(final String streetAddress)
  {
    this.streetAddress = streetAddress;
    return this;
  }

  /**
   * Retrieves the city or locality component.
   *
   * @return The city or locality component.
   */
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
  public Address setLocality(final String locality)
  {
    this.locality = locality;
    return this;
  }

  /**
   * Specifies the state or region component.
   *
   * @return The state or region component.
   */
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
  public Address setRegion(final String region)
  {
    this.region = region;
    return this;
  }

  /**
   * Retrieves the zipcode or postal code component.
   *
   * @return The zipcode or postal code component.
   */
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
  public Address setPostalCode(final String postalCode)
  {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Retrieves the country name component.
   *
   * @return The country name component.
   */
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
  public Address setCountry(final String country)
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
  public Address setType(final String type)
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
  public Address setPrimary(final Boolean primary)
  {
    this.primary = primary;
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

    Address address = (Address) o;

    if (formatted != null ? !formatted.equals(address.formatted) :
        address.formatted != null)
    {
      return false;
    }
    if (streetAddress != null ? !streetAddress.equals(address.streetAddress) :
        address.streetAddress != null)
    {
      return false;
    }
    if (locality != null ? !locality.equals(address.locality) :
        address.locality != null)
    {
      return false;
    }
    if (region != null ? !region.equals(address.region) :
        address.region != null)
    {
      return false;
    }
    if (postalCode != null ? !postalCode.equals(address.postalCode) :
        address.postalCode != null)
    {
      return false;
    }
    if (country != null ? !country.equals(address.country) :
        address.country != null)
    {
      return false;
    }
    if (type != null ? !type.equals(address.type) : address.type != null)
    {
      return false;
    }
    return !(primary != null ? !primary.equals(address.primary) :
        address.primary != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = formatted != null ? formatted.hashCode() : 0;
    result = 31 * result + (streetAddress != null ?
        streetAddress.hashCode() : 0);
    result = 31 * result + (locality != null ? locality.hashCode() : 0);
    result = 31 * result + (region != null ? region.hashCode() : 0);
    result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
    result = 31 * result + (country != null ? country.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (primary != null ? primary.hashCode() : 0);
    return result;
  }
}
