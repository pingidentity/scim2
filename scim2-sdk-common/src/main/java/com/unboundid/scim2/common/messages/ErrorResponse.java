/*
 * Copyright 2015-2017 UnboundID Corp.
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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;

/**
 * This object is returned whenever by SCIM when an error occurs.
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:Error",
    name="Error Response", description = "SCIM 2.0 Error Response")
public final class ErrorResponse extends BaseScimResource
{
  @Attribute(description = "A SCIM detailed error keyword.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String scimType;

  @Attribute(description = "A detailed, human readable message.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String detail;

  @Attribute(description = "The HTTP status code.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  private final int status;

  /**
   * Create a new ScimError with the provided status.
   *
   * @param status The HTTP Status of the SCIM error.
   */
  @JsonCreator
  public ErrorResponse(
      @JsonProperty(value = "status", required = true) final int status)
  {
    this.status = status;
  }

  /**
   * Gets the type of the error.
   *
   * @return the type of the error.
   */
  public String getScimType()
  {
    return scimType;
  }

  /**
   * Sets the type of the SCIM error.
   *
   * @param scimType the type of the SCIM error.
   */
  public void setScimType(final String scimType)
  {
    this.scimType = scimType;
  }

  /**
   * Gets the summary of the SCIM error.
   * @return the summary of the SCIM error.
   */
  public String getDetail()
  {
    return detail;
  }

  /**
   * Sets the summary of the SCIM error.
   * @param detail the summary of the SCIM error.
   */
  public void setDetail(final String detail)
  {
    this.detail = detail;
  }

  /**
   * Gets the HTTP status of the SCIM error.
   * @return the HTTP status of the SCIM error.
   */
  public Integer getStatus()
  {
    return status;
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
    if (!super.equals(o))
    {
      return false;
    }

    ErrorResponse that = (ErrorResponse) o;

    if (status != that.status)
    {
      return false;
    }
    if (detail != null ? !detail.equals(that.detail) : that.detail != null)
    {
      return false;
    }
    if (scimType != null ? !scimType.equals(that.scimType) :
        that.scimType != null)
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (scimType != null ? scimType.hashCode() : 0);
    result = 31 * result + (detail != null ? detail.hashCode() : 0);
    result = 31 * result + status;
    return result;
  }
}
