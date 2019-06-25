/*
 * Copyright 2015-2019 Ping Identity Corporation
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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

import java.text.FieldPosition;
import java.util.Date;



/**
 * Like ISO8601DateFormat except this format includes milliseconds when
 * serializing.
 *
 * @deprecated This class relies on deprecated APIs from the Jackson library
 * and will be removed in a future version of the SCIM 2 SDK.
 * For general parsing and formatting of SCIM 2 DateTime values, see
 * {@link DateTimeUtils}. For usages with a Jackson
 * {@link com.fasterxml.jackson.databind.ObjectMapper}, see the SCIM 2 SDK's
 * serializers and deserializers for {@link Date} and {@link java.util.Calendar}.
 *
 * @see DateTimeUtils
 * @see DateSerializer
 * @see DateDeserializer
 * @see CalendarSerializer
 * @see CalendarDeserializer
 */
@Deprecated // since 2.2.2
public class ScimDateFormat extends ISO8601DateFormat
{
  /**
   * {@inheritDoc}
   */
  @Override
  public StringBuffer format(final Date date,
                             final StringBuffer toAppendTo,
                             final FieldPosition fieldPosition)
  {
    final String value = ISO8601Utils.format(date, true);
    toAppendTo.append(value);
    return toAppendTo;
  }
}
