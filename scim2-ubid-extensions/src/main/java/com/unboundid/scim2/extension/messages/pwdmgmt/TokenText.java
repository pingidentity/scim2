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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.annotations.Attribute;

/**
 * This class is used to represent the before and after text for single
 * use tokens.
 */
public class TokenText
{
  @Attribute(description = "The text that should appear before the token",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String before;

  @Attribute(description = "The text that should appear after the token",
      mutability = AttributeDefinition.Mutability.IMMUTABLE)
  private String after;

  /**
   * Private constructor for Jackson.
   */
  public TokenText()
  {

  }

  /**
   * Constructs the TokenText object.
   *
   * @param before the text that will appear before the token for single
   *               use token delivery.
   * @param after the text that will appear after the token for single
   *               use token delivery.
   */
  public TokenText(final String before, final String after)
  {
    this.before = before;
    this.after = after;
  }

  /**
   * Gets the text that will appear before the token for single
   *               use token delivery.
   * @return the text that will appear before the token for single
   *               use token delivery.
   */
  public String getBefore()
  {
    return before;
  }

  /**
   * Gets the text that will appear after the token for single
   *               use token delivery.
   * @return the text that will appear after the token for single
   *               use token delivery.
   */
  public String getAfter()
  {
    return after;
  }

  /**
   * Utility method to get the before text, but return null
   * if the token text passed in was null.
   *
   * @param tokenText the token text to get the before text
   *                  from.  This may be null.
   * @return the before text from the TokenText object, or
   * null if the TokenText object was null.
   */
  public static String getBeforeText(final TokenText tokenText)
  {
    return (tokenText == null) ? null : tokenText.getBefore();
  }

  /**
   * Utility method to get the after text, but return null
   * if the token text passed in was null.
   *
   * @param tokenText the token text to get the after text
   *                  from.  This may be null.
   * @return the after text from the TokenText object, or
   * null if the TokenText object was null.
   */
  public static String getAfterText(final TokenText tokenText)
  {
    return (tokenText == null) ? null : tokenText.getAfter();
  }

}

