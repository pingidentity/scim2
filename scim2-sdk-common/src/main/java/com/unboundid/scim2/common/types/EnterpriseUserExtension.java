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

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

/**
 * SCIM extension commonly used in representing users that belong to, or act
 * on behalf of a business or enterprise.
 */
@Schema(id="urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
    name="EnterpriseUser", description = "Enterprise User")
public class EnterpriseUserExtension
{
  @Nullable
  @Attribute(description = "Numeric or alphanumeric identifier assigned " +
      "to a person, typically based on order of hire or association with " +
      "an organization.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String employeeNumber;

  @Nullable
  @Attribute(description = "Identifies the name of a cost center.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String costCenter;

  @Nullable
  @Attribute(description = "Identifies the name of an organization.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String organization;

  @Nullable
  @Attribute(description = "Identifies the name of a division.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String division;

  @Nullable
  @Attribute(description = "Identifies the name of a department.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String department;

  @Nullable
  @Attribute(description = "The User's manager.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT)
  private Manager manager;

  /**
   * Retrieves the numeric or alphanumeric identifier assigned to a person.
   *
   * @return The numeric or alphanumeric identifier assigned to a person.
   */
  @Nullable
  public String getEmployeeNumber()
  {
    return employeeNumber;
  }

  /**
   * Specifies the numeric or alphanumeric identifier assigned to a person.
   *
   * @param employeeNumber The numeric or alphanumeric identifier assigned to a
   *                       person.
   * @return This object.
   */
  @NotNull
  public EnterpriseUserExtension setEmployeeNumber(
      @Nullable final String employeeNumber)
  {
    this.employeeNumber = employeeNumber;
    return this;
  }

  /**
   * Retrieves the name of a cost center.
   *
   * @return The name of a cost center.
   */
  @Nullable
  public String getCostCenter()
  {
    return costCenter;
  }

  /**
   * Specifies the name of a cost center.
   *
   * @param costCenter The name of a cost center.
   * @return This object.
   */
  @NotNull
  public EnterpriseUserExtension setCostCenter(
      @Nullable final String costCenter)
  {
    this.costCenter = costCenter;
    return this;
  }

  /**
   * Retrieves the name of an organization.
   *
   * @return The name of an organization.
   */
  @Nullable
  public String getOrganization()
  {
    return organization;
  }

  /**
   * Specifies the name of an organization.
   *
   * @param organization The name of an organization.
   * @return This object.
   */
  @NotNull
  public EnterpriseUserExtension setOrganization(
      @Nullable final String organization)
  {
    this.organization = organization;
    return this;
  }

  /**
   * Retrieves the name of a division.
   *
   * @return The name of a division.
   */
  @Nullable
  public String getDivision()
  {
    return division;
  }

  /**
   * Specifies the name of a division.
   *
   * @param division The name of a division.
   * @return This object.
   */
  @NotNull
  public EnterpriseUserExtension setDivision(@Nullable final String division)
  {
    this.division = division;
    return this;
  }

  /**
   * Retrieves the name of a department.
   *
   * @return The name of a department.
   */
  @Nullable
  public String getDepartment()
  {
    return department;
  }

  /**
   * Specifies the name of a department.
   *
   * @param department the name of a department.
   * @return This object.
   */
  @NotNull
  public EnterpriseUserExtension setDepartment(
      @Nullable final String department)
  {
    this.department = department;
    return this;
  }

  /**
   * Retrieves the User's manager.
   *
   * @return The User's manager.
   */
  @Nullable
  public Manager getManager()
  {
    return manager;
  }

  /**
   * Specifies the User's manager.
   *
   * @param manager The User's manager.
   * @return This object.
   */
  @NotNull
  public EnterpriseUserExtension setManager(@Nullable final Manager manager)
  {
    this.manager = manager;
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this enterprise user
   * extension.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this enterprise
   *            user extension, or {@code false} if not.
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

    EnterpriseUserExtension that = (EnterpriseUserExtension) o;

    if (employeeNumber != null ? !employeeNumber.equals(that.employeeNumber) :
        that.employeeNumber != null)
    {
      return false;
    }
    if (costCenter != null ? !costCenter.equals(that.costCenter) :
        that.costCenter != null)
    {
      return false;
    }
    if (organization != null ? !organization.equals(that.organization) :
        that.organization != null)
    {
      return false;
    }
    if (division != null ? !division.equals(that.division) :
        that.division != null)
    {
      return false;
    }
    if (department != null ? !department.equals(that.department) :
        that.department != null)
    {
      return false;
    }
    return !(manager != null ? !manager.equals(that.manager) :
        that.manager != null);

  }

  /**
   * Retrieves a hash code for this enterprise user extension.
   *
   * @return  A hash code for this enterprise user extension.
   */
  @Override
  public int hashCode()
  {
    int result = employeeNumber != null ? employeeNumber.hashCode() : 0;
    result = 31 * result + (costCenter != null ? costCenter.hashCode() : 0);
    result = 31 * result + (organization != null ? organization.hashCode() : 0);
    result = 31 * result + (division != null ? division.hashCode() : 0);
    result = 31 * result + (department != null ? department.hashCode() : 0);
    result = 31 * result + (manager != null ? manager.hashCode() : 0);
    return result;
  }
}
