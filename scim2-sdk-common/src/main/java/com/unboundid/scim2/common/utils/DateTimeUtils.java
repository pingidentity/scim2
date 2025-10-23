/*
 * Copyright 2019-2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * Utility methods for handling SCIM 2 DateTime values. The SCIM 2 DateTime
 * type is defined as a valid {@code xsd:dateTime} in
 * <a href="https://datatracker.ietf.org/doc/html/rfc7643#section-2.3.5">
 * RFC 7643, section 2.3.5</a>.
 */
public final class DateTimeUtils
{
  /**
   * Represents the "UTC" timezone (Coordinated Universal Time).
   */
  @NotNull
  private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

  @NotNull
  private static final TimeZone GMT = TimeZone.getTimeZone("GMT+00:00");

  /**
   * Formats a {@link Date} value as a SCIM 2 DateTime string.
   * This will use UTC as the time zone.
   *
   * @param date A Date value.
   * @return The value as a SCIM 2 DateTime string.
   */
  @NotNull
  public static String format(@NotNull final Date date)
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
  @NotNull
  public static String format(@NotNull final Date date,
                              @NotNull final TimeZone timeZone)
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
  @NotNull
  public static String format(@NotNull final Calendar calendar)
  {
    OffsetDateTime time = calendar.toInstant()
        .atZone(calendar.getTimeZone().toZoneId())
        .toOffsetDateTime()
        .truncatedTo(ChronoUnit.MILLIS);

    if (time.getNano() > 0)
    {
      // Ensure millisecond values, when present, are always printed with three
      // digits. For example, return "12:00:00.100" instead of "12:00:00.1".
      return time.toString();
    }
    else
    {
      // Ensure that the seconds value, when zero, is always explicitly printed.
      // For example, return "12:00:00" instead of "12:00".
      return ISO_OFFSET_DATE_TIME.format(time);
    }
  }

  /**
   * Converts a SCIM 2 DateTime string to a {@link Calendar}.
   *
   * @param dateStr A SCIM 2 DateTime string.
   * @return The DateTime string as a Calendar value.
   * @throws IllegalArgumentException if the string cannot be parsed as an
   * xsd:dateTime value.
   */
  @NotNull
  public static Calendar parse(@NotNull final String dateStr)
      throws IllegalArgumentException
  {
    OffsetDateTime parsedTime;
    try
    {
      parsedTime = OffsetDateTime.parse(dateStr);
    }
    catch (DateTimeException e)
    {
      // Re-throw as an IllegalArgumentException for backward compatibility with
      // previous releases of the SCIM SDK.
      throw new IllegalArgumentException(e);
    }

    // Extract the timestamp and timezone. Timezones in the default region
    // should be considered as "GMT+00:00" for backward compatibility.
    Date timestamp = Date.from(parsedTime.toInstant());
    TimeZone zone = parsedTime.getOffset().equals(ZoneOffset.ofHours(0))
        ? GMT : TimeZone.getTimeZone(parsedTime.getOffset());

    Calendar calendar = Calendar.getInstance(zone);
    calendar.setTime(timestamp);
    return calendar;
  }
}
