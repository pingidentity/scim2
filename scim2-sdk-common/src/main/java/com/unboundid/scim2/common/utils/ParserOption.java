/*
 * Copyright 2021 Ping Identity Corporation
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

package com.unboundid.scim2.common.utils;

/**
 * This enumeration defines special options for parsing SCIM filter expressions.
 *
 * <p>NOTE: SCIM server implementations are not guaranteed to support a given option.</p>
 */
public enum ParserOption
{
  /**
   * Allow attribute names to include semicolons (in addition to underscores,
   * dashes and colons).
   */
  ALLOW_SEMICOLONS_IN_ATTRIBUTE_NAMES;
}
