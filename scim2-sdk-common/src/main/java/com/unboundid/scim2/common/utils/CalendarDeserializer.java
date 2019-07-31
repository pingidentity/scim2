/*
 * Copyright 2019 Ping Identity Corporation
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.util.Calendar;

/**
 * Deserializes SCIM 2 DateTime values to {@link Calendar} objects.
 */
public class CalendarDeserializer extends JsonDeserializer<Calendar>
{
  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar deserialize(final JsonParser jp,
                              final DeserializationContext ctxt)
      throws IOException, JsonProcessingException
  {
    String dateStr = jp.getText();
    try
    {
      return DateTimeUtils.parse(dateStr);
    }
    catch (IllegalArgumentException e)
    {
      throw new InvalidFormatException(jp, "unable to deserialize value",
          dateStr, Calendar.class);
    }
  }
}
