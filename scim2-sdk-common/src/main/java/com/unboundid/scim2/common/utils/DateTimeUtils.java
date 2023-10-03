/*
 * Copyright 2019-2023 Ping Identity Corporation
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

import jakarta.xml.bind.DatatypeConverter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility methods for handling SCIM 2 DateTime values. The SCIM 2 DateTime
 * type is defined as a valid xsd:dateTime in RFC 7643, section 2.3.5.
 */
public final class DateTimeUtils
{
  private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

  /**
   * Formats a {@link Date} value as a SCIM 2 DateTime string.
   *
   * @param date A Date value.
   * @return The value as a SCIM 2 DateTime string.
   */
  public static String format(final Date date)
  {
    return format(date, DEFAULT_TIME_ZONE);
  }

  /**
   * Formats a {@link Date} value as a SCIM 2 DateTime string.
   *
   * @param date A Date value.
   * @param timeZone The time zone.
   * @return The value as a SCIM 2 DateTime string.
   */
  public static String format(final Date date, final TimeZone timeZone)
  {
    Calendar calendar = Calendar.getInstance(timeZone);
    calendar.setTime(date);
    return format(calendar);
  }

  /**
   * Formats a {@link Calendar} value as a SCIM 2 DateTime string.
   *
   * @param calendar A Calendar value.
   * @return The value as a SCIM 2 DateTime string.
   */
  public static String format(final Calendar calendar)
  {
    return DatatypeConverter.printDateTime(calendar);
  }

  /**
   * Converts a SCIM 2 DateTime string to a {@link Calendar}.
   *
   * @param dateStr A SCIM 2 DateTime string.
   * @return The DateTime string as a Calendar value.
   * @throws IllegalArgumentException if the string cannot be parsed as an
   * xsd:dateTime value.
   */
  public static Calendar parse(final String dateStr)
      throws IllegalArgumentException
  {
    return DatatypeConverter.parseDateTime(dateStr);
  }
}
