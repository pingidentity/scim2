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

package com.unboundid.scim2.pwdmgmt;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class holds any errors encountered during a password update.
 */
public class PasswordRequirementResult
{
  @Attribute(description = "The human-readable description of the " +
      "password requirement",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String display;
  @Attribute(description = "The type of password requirement.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String type;
  @Attribute(description = "True if this requirement was satisfied.  " +
      "False if not.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Boolean requirementSatisfied;
  @Attribute(description = "The description associated with this " +
      "password validator.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String description;
  @Attribute(description = "Additional information about the request, " +
      "such as the message associated with a password validation failure.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String additionalInfo;
  private Map<String, String> properties = new LinkedHashMap<String, String>();

  /**
   * Gets the human-readable description of the password requirement.
   *
   * @return the human-readable description of the password requirement.
   */
  public String getDisplay()
  {
    return display;
  }

  /**
   * Sets the human-readable description of the password requirement.
   * @param display the the human-readable description of the password
   *                requirement.
   */
  public void setDisplay(final String display)
  {
    this.display = display;
  }

  /**
   * Gets the type of password requirement.
   *
   * @return the type of password requirement.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Sets the type of password requirement.
   *
   * @param type the type of password requirement.
   */
  public void setType(final String type)
  {
    this.type = type;
  }

  /**
   * Used to determine if the requirement is satisfied.
   * @return true if the requirement is satisfied, or false if not.
   */
  public Boolean isRequirementSatisfied()
  {
    return requirementSatisfied;
  }

  /**
   * Sets whether or not the password requirement is satisfied.
   * @param requirementSatisfied boolean indicating if the password requirement
   *                             is satisfied or not.
   */
  public void setRequirementSatisfied(final Boolean requirementSatisfied)
  {
    this.requirementSatisfied = requirementSatisfied;
  }

  /**
   * Gets the additonal information for this password update error.
   * @return the additional information, such as the failure message
   * for this password update error.
   */
  public String getAdditionalInfo()
  {
    return additionalInfo;
  }

  /**
   * Sets the additional information for this password update error.
   * @param additionalInfo additional information, such as the failure message
   *   for this password update error.
   */
  public void setAdditionalInfo(final String additionalInfo)
  {
    this.additionalInfo = additionalInfo;
  }

  /**
   * Gets the description for this password requirement.
   *
   * @return the description for this password requirement.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the description for this password requirement.
   *
   * @param description the description for this password requirement.
   */
  public void setDescription(final String description)
  {
    this.description = description;
  }

  /**
   * Gets the properties for this password requirement.
   *
   * @return the properties for this password requirement.
   */
  @JsonAnyGetter
  public Map<String, String> getProperties()
  {
    return properties;
  }

  /**
   * Sets the properties for this password requirement.
   *
   * @param propertyName  The name of the property.
   * @param propertyValue The value of the property.
   */
  @JsonAnySetter
  public void putProperty(final String propertyName, final String propertyValue)
  {
    properties.put(propertyName, propertyValue);
  }
}
