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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ResourceConflictException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.StatusDeserializer;
import com.unboundid.scim2.common.utils.StatusSerializer;

/**
 * This class represents a SCIM API error response. An error response represents
 * a JSON body with an error message from a SCIM service provider. It has a
 * schema URI of {@code urn:ietf:params:scim:api:messages:2.0:Error}.
 * <br><br>
 * An error response has the following fields:
 * <ul>
 *   <li> {@code schemas}: A required parameter that contains the schema of the
 *        SCIM error response object.
 *   <li> {@code scimType}: An optional SCIM "detail error keyword"
 *        that succinctly describes the reason for the error (e.g.,
 *        {@code uniqueness}, {@code tooMany}). This is typically used for
 *        {@link BadRequestException} and {@link ResourceConflictException}
 *        errors.
 *   <li> {@code detail}: An optional parameter containing a descriptive message
 *        that describes the reason for the error.
 *   <li> {@code status}: A required parameter that contains the HTTP status code
 *        for the error (e.g., 401, 500).
 * </ul>
 * <br><br>
 * An example error response is shown below:
 * <pre>
 *   {
 *      "schemas": [ "urn:ietf:params:scim:api:messages:2.0:Error" ],
 *      "scimType": "mutability",
 *      "detail": "The 'id' attribute is read-only and cannot be modified.",
 *      "status": "400"
 *   }
 * </pre>
 *
 * To create a SCIM error response as an exception, use the
 * {@link ScimException} class, which contains an ErrorResponse.
 */
@Schema(id="urn:ietf:params:scim:api:messages:2.0:Error",
    name="Error Response", description = "SCIM 2.0 Error Response")
public final class ErrorResponse extends BaseScimResource
{
  @Nullable
  @Attribute(description = "A SCIM detailed error keyword.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String scimType;

  @Nullable
  @Attribute(description = "A detailed, human readable message.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private String detail;

  @Attribute(description = "The HTTP status code.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      isRequired = true)
  @JsonSerialize(using = StatusSerializer.class)
  @JsonDeserialize(using = StatusDeserializer.class)
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
  @Nullable
  public String getScimType()
  {
    return scimType;
  }

  /**
   * Sets the type of the SCIM error.
   *
   * @param scimType the type of the SCIM error.
   */
  public void setScimType(@Nullable final String scimType)
  {
    this.scimType = scimType;
  }

  /**
   * Gets the summary of the SCIM error.
   *
   * @return the summary of the SCIM error.
   */
  @Nullable
  public String getDetail()
  {
    return detail;
  }

  /**
   * Sets the summary of the SCIM error.
   *
   * @param detail the summary of the SCIM error.
   */
  public void setDetail(@Nullable final String detail)
  {
    this.detail = detail;
  }

  /**
   * Gets the HTTP status of the SCIM error.
   *
   * @return the HTTP status of the SCIM error.
   */
  @NotNull
  public Integer getStatus()
  {
    return status;
  }

  /**
   * Indicates whether the provided object is equal to this error response.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this error
   *            response, or {@code false} if not.
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
   * Retrieves a hash code for this error response.
   *
   * @return  A hash code for this error response.
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
