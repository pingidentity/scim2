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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class holds any errors encountered during a password update.
 */
public class PasswordRequirementResult
{
  @Nullable
  @Attribute(description = "The type of password requirement.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String type;

  @Nullable
  @Attribute(description = "True if this requirement was satisfied.  " +
      "False if not.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
  private Boolean requirementSatisfied;

  @Nullable
  @Attribute(description = "The description associated with this " +
      "password validator.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String description;

  @Nullable
  @Attribute(description = "Additional information about the request, " +
      "such as the message associated with a password validation failure.",
        mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String additionalInfo;

  @NotNull
  private Map<String, JsonNode> properties =
      new LinkedHashMap<>();

  /**
   * Gets the type of password requirement.
   *
   * @return the type of password requirement.
   */
  @Nullable
  public String getType()
  {
    return type;
  }

  /**
   * Sets the type of password requirement.
   *
   * @param type the type of password requirement.
   */
  public void setType(@Nullable final String type)
  {
    this.type = type;
  }

  /**
   * Used to determine if the requirement is satisfied.
   *
   * @return true if the requirement is satisfied, or false if not.
   */
  @Nullable
  public Boolean isRequirementSatisfied()
  {
    return requirementSatisfied;
  }

  /**
   * Sets whether or not the password requirement is satisfied.
   *
   * @param requirementSatisfied boolean indicating if the password requirement
   *                             is satisfied or not.
   */
  public void setRequirementSatisfied(
      @Nullable final Boolean requirementSatisfied)
  {
    this.requirementSatisfied = requirementSatisfied;
  }

  /**
   * Gets the additonal information for this password update error.
   *
   * @return the additional information, such as the failure message
   * for this password update error.
   */
  @Nullable
  public String getAdditionalInfo()
  {
    return additionalInfo;
  }

  /**
   * Sets the additional information for this password update error.
   *
   * @param additionalInfo additional information, such as the failure message
   *   for this password update error.
   */
  public void setAdditionalInfo(@Nullable final String additionalInfo)
  {
    this.additionalInfo = additionalInfo;
  }

  /**
   * Gets the description for this password requirement.
   *
   * @return the description for this password requirement.
   */
  @Nullable
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the description for this password requirement.
   *
   * @param description the description for this password requirement.
   */
  public void setDescription(@Nullable final String description)
  {
    this.description = description;
  }

  /**
   * Gets the properties for this password requirement.
   *
   * @return the properties for this password requirement.
   */
  @NotNull
  @JsonAnyGetter
  public Map<String, JsonNode> getProperties()
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
  public void putProperty(@NotNull final String propertyName,
                          @NotNull final JsonNode propertyValue)
  {
    properties.put(propertyName, propertyValue);
  }
}
