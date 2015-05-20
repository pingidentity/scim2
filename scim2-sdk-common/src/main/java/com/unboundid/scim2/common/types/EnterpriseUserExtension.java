/*
 * Copyright 2015 UnboundID Corp.
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

import com.unboundid.scim2.common.AttributeDefinition;
import com.unboundid.scim2.common.annotations.SchemaInfo;
import com.unboundid.scim2.common.annotations.SchemaProperty;

/**
 * SCIM extension commonly used in representing users that belong to, or act
 * on behalf of a business or enterprise.
 */
@SchemaInfo(id="urn:ietf:params:scim:schemas:extension:enterprise:2.0:User",
    name="EnterpriseUser", description = "Enterprise User")
public class EnterpriseUserExtension
{
  @SchemaProperty(description = "Numeric or alphanumeric identifier assigned " +
      "to a person, typically based on order of hire or association with " +
      "an organization.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String employeeNumber;

  @SchemaProperty(description = "Identifies the name of a cost center.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String costCenter;

  @SchemaProperty(description = "Identifies the name of an organization.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String organization;

  @SchemaProperty(description = "Identifies the name of a division.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String division;

  @SchemaProperty(description = "Identifies the name of a department.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String department;

  @SchemaProperty(description = "The User's manager.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT)
  private Manager manager;

  /**
   * Retrieves the numeric or alphanumeric identifier assigned to a person.
   *
   * @return The numeric or alphanumeric identifier assigned to a person.
   */
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
  public EnterpriseUserExtension setEmployeeNumber(final String employeeNumber)
  {
    this.employeeNumber = employeeNumber;
    return this;
  }

  /**
   * Retrieves the name of a cost center.
   *
   * @return The name of a cost center.
   */
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
  public EnterpriseUserExtension setCostCenter(final String costCenter)
  {
    this.costCenter = costCenter;
    return this;
  }

  /**
   * Retrieves the name of an organization.
   *
   * @return The name of an organization.
   */
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
  public EnterpriseUserExtension setOrganization(final String organization)
  {
    this.organization = organization;
    return this;
  }

  /**
   * Retrieves the name of a division.
   *
   * @return The name of a division.
   */
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
  public EnterpriseUserExtension setDivision(final String division)
  {
    this.division = division;
    return this;
  }

  /**
   * Retrieves the name of a department.
   *
   * @return The name of a department.
   */
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
  public EnterpriseUserExtension setDepartment(final String department)
  {
    this.department = department;
    return this;
  }

  /**
   * Retrieves the User's manager.
   *
   * @return The User's manager.
   */
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
  public EnterpriseUserExtension setManager(final Manager manager)
  {
    this.manager = manager;
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
   * {@inheritDoc}
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
