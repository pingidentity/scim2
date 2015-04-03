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

package com.unboundid.scim2.exceptions;

import com.unboundid.scim2.annotations.SchemaInfo;
import com.unboundid.scim2.annotations.SchemaProperty;
import com.unboundid.scim2.model.BaseScimResourceObject;

/**
 * This object is returned whenever by SCIM when an error occurs.
 */
@SchemaInfo(id="urn:ietf:params:scim:api:messages:2.0:ScimError", name="Error",
  description = "Contains errors")
public class ScimErrorResource extends BaseScimResourceObject
{
  @SchemaProperty(description = "Type of the SCIM error.")
  private String scimType = "";

  @SchemaProperty(description = "Summary of the SCIM error.")
  private String detail = "";

  @SchemaProperty(description = "HTTP Status of the SCIM error.")
  private Integer status = 200;


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
   * Sets the HTTP status of the SCIM error.
   * @param status the HTTP status of the SCIM error.
   */
  public void setStatus(final Integer status)
  {
    this.status = status;
  }
}
