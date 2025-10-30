/*
 * Copyright 2025 Ping Identity Corporation
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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.utils.DateTimeUtils;
import com.unboundid.scim2.common.utils.JsonUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Validates the {@link DateTimeUtils} class.
 */
public class DateTimeUtilsTest
{
  private static final TimeZone GMT = TimeZone.getTimeZone("GMT+00:00");

  /**
   * Validate {@link DateTimeUtils#format(Date, TimeZone)}.
   *
   * @param expected   The string value that should be generated.
   * @param dateObject The date object.
   * @param timezone   The timezone.
   */
  @Test(dataProvider = "timestampTestCases")
  public void testConversionToString(final String expected,
                                     final Date dateObject,
                                     final TimeZone timezone)
  {
    // Serialize the object timestamp to a string. The result should match the
    // provided string representation.
    String computedTimestamp = DateTimeUtils.format(dateObject, timezone);
    assertThat(computedTimestamp).isEqualTo(expected);
  }

  /**
   * Validate {@link DateTimeUtils#parse}.
   *
   * @param stringTimestamp   The string timestamp that will be used to generate
   *                          the calendar POJO.
   * @param expectedDate      The date value of the expected calendar object.
   * @param expectedTimezone  The timezone of the expected calendar object.
   */
  @Test(dataProvider = "timestampTestCases")
  public void testConversionToObject(final String stringTimestamp,
                                     final Date expectedDate,
                                     final TimeZone expectedTimezone)
  {
    // Obtain the expected Calendar object.
    Calendar expectedValue = Calendar.getInstance(expectedTimezone);
    expectedValue.setTime(expectedDate);

    // Fetch the real value computed by the utility class and ensure it is
    // equivalent.
    Calendar parsedValue = DateTimeUtils.parse(stringTimestamp);
    assertThat(parsedValue).isEqualTo(expectedValue);
  }

  /**
   * Similar to {@link #testConversionToObject}, but for invalid values.
   *
   * @param invalidString   The timestamp to evaluate.
   */
  @Test(dataProvider = "invalidTimestamps")
  public void testInvalidTimestamps(final String invalidString)
  {
    assertThatThrownBy(() -> DateTimeUtils.parse(invalidString))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("could not be parsed");
  }

  /**
   * Validate the {@link DateTimeUtils#format(Date)} method. This test will only
   * be run if the timezone value is UTC.
   *
   * @param expected    The string value that should be generated.
   * @param dateObject  The date object.
   * @param timezone    The timezone.
   */
  @Test(dataProvider = "timestampTestCases")
  public void testUTC(final String expected,
                      final Date dateObject,
                      final TimeZone timezone)
  {
    if (timezone.getRawOffset() == 0)
    {
      String computedTimestamp = DateTimeUtils.format(dateObject);
      assertThat(computedTimestamp).isEqualTo(expected);
    }
  }

  /**
   * This test validates the deserialization of UNIX timestamps as date values
   * for backward compatibility. Consider the following scenario.
   * <br><br>
   *
   * A SCIM server/service application processes incoming requests. A separate
   * Java client application sends a request to the service. However, the Java
   * client uses an ObjectMapper with default settings, which includes using the
   * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} feature. Thus, the
   * JSON sent by the client after serialization looks like:
   * <pre>
   *   {
   *     "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
   *     "userName": "name",
   *     "meta": {
   *       "created": 1426901922
   *     }
   *   }
   * </pre>
   *
   * The timestamp value above represents milliseconds after the epoch instead
   * of the expected ISO 8601 string, "2015-03-20T20:38:42-05:00".
   * <br><br>
   *
   * Previous releases of the SCIM SDK supported client requests that sent a
   * UNIX timestamp as a value. Thus, in the example above, the SCIM service
   * must be able to support these client requests for consistency with older
   * releases. This test simulates the behavior of such requests and ensures
   * they can be deserialized properly.
   *
   * @param ignored     The timestamp from the data provider. This is not used.
   * @param dateObject  The date object from the data provider.
   * @param ignoredZone The timezone from the data provider. This is not used.
   */
  @Test(dataProvider = "timestampTestCases")
  public void testDeserialize(final String ignored,
                              final Date dateObject,
                              final TimeZone ignoredZone)
      throws Exception
  {
    long timestamp = dateObject.getTime();
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "userName": "name",
          "meta": {
            "created": %d
          }
        }""".formatted(timestamp);

    // Deserialize the JSON into a user resource.
    UserResource user = JsonUtils.getObjectReader().forType(UserResource.class)
        .readValue(json);

    // Fetch the deserialized value of 'meta.created'.
    assertThat(user.getMeta()).isNotNull();
    Calendar createdTimestamp = user.getMeta().getCreated();

    // The timestamp should have been deserialized into a Calendar object set
    // to the correct time.
    assertThat(createdTimestamp).isNotNull();
    assertThat(createdTimestamp.getTime()).isEqualTo(dateObject);

    // Since the value was extracted from a timestamp, the timezone must be UTC.
    assertThat(createdTimestamp.getTimeZone().getRawOffset()).isEqualTo(0);
  }

  /**
   * Ensures an exception is thrown when a malformed timestamp is deserialized.
   */
  @Test
  public void testBadDeserialization()
  {
    String json = """
        {
          "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
          "userName": "name",
          "meta": {
            "created": "notATimestamp"
          }
        }""";

    var reader = JsonUtils.getObjectReader().forType(UserResource.class);
    assertThatThrownBy(() -> reader.readValue(json))
        .isInstanceOf(InvalidFormatException.class)
        .hasMessageStartingWith("SCIM SDK: unable to deserialize value");
  }

  /**
   * This method is shorthand for creating a test case for the
   * {@code timestampTestCases} data provider.
   *
   * @param stringTimestamp  The string form of the timestamp. This is equal
   *                         to the combination of 'epochMillis' and 'timezone'.
   * @param epochMillis      The timestamp represented as the number of
   *                         milliseconds since January 1, 1970.
   * @param timezone         The timezone that this timestamp represents.
   *
   * @return  An object array representing a test case for a data provider.
   */
  private static Object[] testcase(final String stringTimestamp,
                                   final long epochMillis,
                                   final TimeZone timezone)
  {
    return new Object[] { stringTimestamp, new Date(epochMillis), timezone };
  }

  /**
   * Creates a TimeZone object based on an hour and minute offset. If the zone
   * offset is negative, the minutes value must be negative as well.
   */
  private TimeZone zone(int zoneOffset, int offsetMinutes)
  {
    var offset = ZoneOffset.ofHoursMinutes(zoneOffset, offsetMinutes);
    return TimeZone.getTimeZone(offset);
  }

  private TimeZone zone(int zoneOffset)
  {
    return zone(zoneOffset, 0);
  }

  /**
   * A data provider representing string timestamps that are not expected to be
   * parsed correctly.
   *
   * @return A set of invalid timestamp strings.
   */
  @DataProvider(name = "invalidTimestamps")
  public Object[][] invalidTimestamps()
  {
    return new Object[][]
        {
            new String[] { "1989-03-14T17:56:47+09:999" },
            new String[] { "1989-03-14T17:56:47+99:00" },
            new String[] { "1989-03-14T17:56:61+02:00" },
            new String[] { "1989-03-14T17:61:47+02:00" },
            new String[] { "1989-03-14T25:56:47+02:00" },
            new String[] { "1989-03-32T17:56:47+02:00" },
            new String[] { "1989-13-14T17:56:47+02:00" },
        };
  }

  /**
   * A data provider for test cases that each describe:
   * <ul>
   *   <li> A string timestamp.
   *   <li> An equivalent UNIX timestamp and an associated TimeZone value.
   * </ul>
   *
   * It should be possible to convert these two values both ways.
   *
   * @return  A set of valid timestamp object and string pairs.
   */
  @DataProvider(name = "timestampTestCases")
  public Object[][] timestampTestCases()
  {
    return new Object[][]
        {
            // Basic test cases.
            testcase("1970-01-01T00:00:00Z", 0L, GMT),
            testcase("1970-01-01T00:00:01Z", 1000L, GMT),
            testcase("1970-01-01T01:00:00Z", 3600000L, GMT),
            testcase("2079-10-18T08:53:50+01:00", 3464841230000L, zone(1)),
            testcase("2098-06-17T20:13:43-01:00", 4053878023000L, zone(-1)),
            testcase("2076-12-31T22:37:36Z", 3376679856000L, GMT),
            testcase("2021-04-30T11:09:27Z", 1619780967000L, GMT),
            testcase("1974-04-09T06:41:08Z", 134721668000L, GMT),
            testcase("2061-12-11T01:53:11.013Z", 2901491591013L, GMT),
            testcase("2061-12-11T01:53:11.100Z", 2901491591100L, GMT),
            testcase("2061-12-11T01:53:11.120Z", 2901491591120L, GMT),

            // This timestamp value comes from RFC 7643.
            testcase("2008-01-23T04:56:22Z", 1201064182000L, GMT),

            // Timestamps representing values beyond the 21st century.
            testcase("2151-01-22T09:00:00Z", 5713664400000L, GMT),
            testcase("2451-01-22T09:00:00Z", 15180771600000L, GMT),
            testcase("3000-01-22T09:00:00Z", 32505526800000L, GMT),

            // A timestamp representing a value before the epoch.
            testcase("1955-01-22T09:00:00Z", -471538800000L, GMT),

            // These represent values before and after Daylight Savings Time
            // started in the US Central Time Zone in 2025. This occurred at
            // 2:00 AM local time.
            testcase("2025-03-09T01:59:45-06:00", 1741507185000L, zone(-6)),
            testcase("2025-03-09T03:04:21-05:00", 1741507461000L, zone(-5)),

            // This timestamp is technically not valid, since it points to a
            // value in between the Daylight Savings jump. However, it can still
            // point to a definite number of milliseconds after the epoch.
            testcase("2025-03-09T02:30:11-06:00", 1741509011000L, zone(-6)),

            // Test a non-standard value for a timezone.
            testcase("2079-10-18T08:53:50-11:07", 3464884850000L, zone(-11, -7)),

            // Additional test cases.
            testcase("2079-10-18T08:53:50-08:00", 3464873630000L, zone(-8)),
            testcase("2098-06-17T20:13:43Z", 4053874423000L, GMT),
            testcase("2076-12-31T22:37:36.141+05:30", 3376660056141L, zone(5, 30)),
            testcase("2021-04-30T11:09:27+10:00", 1619744967000L, zone(10)),
            testcase("1974-04-09T06:41:08Z", 134721668000L, GMT),
            testcase("2093-02-23T19:17:56-09:30", 3886289276000L, zone(-9, -30)),
            testcase("2030-11-02T23:43:07-02:00", 1919900587000L, zone(-2)),
            testcase("2077-04-04T01:01:07+04:30", 3384707467000L, zone(4, 30)),
            testcase("2061-04-27T04:05:45+09:00", 2881767945000L, zone(9)),
            testcase("1978-12-09T04:19:28+14:00", 281974768000L, zone(14)),
            testcase("2036-04-11T17:34:11-11:00", 2091587651000L, zone(-11)),
            testcase("2093-05-01T13:49:13.002-03:30", 3892036753002L, zone(-3, -30)),
            testcase("2065-02-03T22:36:54+03:30", 3000913614000L, zone(3, 30)),
            testcase("2048-10-02T05:48:27+08:00", 2485201707000L, zone(8)),
            testcase("2000-03-12T03:18:16+13:00", 952784296000L, zone(13)),
            testcase("2060-03-05T22:42:28Z", 2845752148000L, GMT),
            testcase("2034-05-20T21:08:56-05:00", 2031790136000L, zone(-5)),
            testcase("2058-05-04T15:06:54+02:00", 2787743214000L, zone(2)),
            testcase("2027-07-12T06:04:43+06:30", 1815348883000L, zone(6, 30)),
            testcase("2016-01-04T11:25:27+12:00", 1451863527000L, zone(12)),
            testcase("1990-07-05T21:43:13Z", 647214193000L, GMT),
            testcase("1986-03-29T03:51:45-07:00", 512477505000L, zone(-7)),
            testcase("2009-04-04T06:21:56Z", 1238826116000L, GMT),
            testcase("1982-05-03T17:49:57.941+05:45", 389275497941L, zone(5, 45)),
            testcase("2007-04-29T13:26:22+10:30", 1177815382000L, zone(10, 30)),
            testcase("2077-01-08T18:28:11Z", 3377356091000L, GMT),
            testcase("2075-03-07T06:25:59-09:00", 3319197959000L, zone(-9)),
            testcase("2016-08-16T10:51:47-02:00", 1471351907000L, zone(-2)),
            testcase("1993-11-30T17:56:47+05:00", 754664207000L, zone(5)),
            testcase("2042-04-27T03:48:45+09:30", 2282149125000L, zone(9, 30)),
            testcase("2088-06-16T23:14:34Z", 3738266074000L, GMT),
            testcase("1970-03-31T02:40:25-10:00", 7735225000L, zone(-10)),
            testcase("2011-11-06T18:06:14-03:00", 1320613574000L, zone(-3)),
            testcase("2049-07-24T21:40:43+04:00", 2510761243000L, zone(4)),
            testcase("1990-08-23T08:22:17+04:30", 651383537000L, zone(4, 30)),
            testcase("2091-04-17T00:29:39+08:45", 3827576679000L, zone(8, 45)),
            testcase("1975-11-12T09:25:46+14:00", 184965946000L, zone(14)),
            testcase("1972-05-22T14:26:24-11:00", 75432384000L, zone(-11)),
            testcase("1980-05-01T20:36:11-04:00", 326075771000L, zone(-4)),
            testcase("1981-08-21T05:36:43+03:30", 367207603000L, zone(3, 30)),
            testcase("2039-08-07T16:15:52+07:00", 2196321352000L, zone(7)),
            testcase("1978-04-25T07:29:47+12:45", 262291487000L, zone(12, 45)),
            testcase("2046-06-09T03:51:29Z", 2412129089000L, GMT),
            testcase("1996-03-09T02:26:40-06:00", 826360000000L, zone(-6)),
            testcase("1983-01-10T12:03:21+02:00", 411041001000L, zone(2)),
            testcase("2084-05-04T17:02:12+06:00", 3608276532000L, zone(6)),
            testcase("1995-09-16T20:03:15+11:00", 811242195000L, zone(11)),
            testcase("1983-04-22T18:02:51Z", 419882571000L, GMT),
            testcase("2070-10-02T00:31:16-08:00", 3179464276000L, zone(-8)),
            testcase("2078-03-08T02:50:24Z", 3413933424000L, GMT),
            testcase("1993-05-29T00:46:46.523+05:45", 738615706523L, zone(5, 45)),
            testcase("2066-12-02T09:29:02+10:00", 3058471742000L, zone(10)),
            testcase("2030-07-23T06:32:51Z", 1911018771000L, GMT),
            testcase("2048-03-17T05:02:02-09:30", 2468068322000L, zone(-9, -30)),
            testcase("1976-02-10T11:43:33-02:00", 192807813000L, zone(-2)),
            testcase("1987-03-12T13:51:45+05:00", 542537505000L, zone(5)),
            testcase("2030-05-30T15:18:38+09:00", 1906352318000L, zone(9)),
            testcase("2054-10-23T13:54:15Z", 2676376455000L, GMT),
            testcase("2020-06-02T22:32:57-11:00", 1591176777000L, zone(-11)),
            testcase("1973-08-02T19:59:07-03:30", 113182147000L, zone(-3, -30)),
            testcase("2086-10-02T05:54:43+04:00", 3684362083000L, zone(4)),
            testcase("2050-10-13T12:41:00+08:00", 2549248860000L, zone(8)),
            testcase("2012-10-04T03:42:04+13:00", 1349275324000L, zone(13)),
            testcase("2058-09-12T06:40:47-12:00", 2799081647000L, zone(-12)),
            testcase("2068-04-02T13:27:00-05:00", 3100616820000L, zone(-5)),
            testcase("2066-10-14T16:56:23+03:00", 3054290183000L, zone(3)),
            testcase("2095-01-23T20:13:54+06:30", 3946628634000L, zone(6, 30)),
            testcase("2047-02-18T09:36:33+12:00", 2434052193000L, zone(12)),
            testcase("2055-05-15T20:40:48Z", 2694026448000L, GMT),
            testcase("2079-11-09T23:17:50-07:00", 3466822670000L, zone(-7)),
            testcase("2064-11-05T03:07:27-06:00", 2993101647000L, zone(-6)),
            testcase("2095-08-05T15:13:38+01:00", 3963392018000L, zone(1)),
            testcase("2082-12-01T04:56:39+06:00", 3563304999000L, zone(6)),
            testcase("2006-08-23T20:36:32+11:00", 1156325792000L, zone(11)),
            testcase("2079-12-19T19:16:27Z", 3470238987000L, GMT),
            testcase("2039-02-06T10:16:01-08:00", 2180628961000L, zone(-8)),
            testcase("2068-08-10T08:11:27-01:00", 3111815487000L, zone(-1)),
            testcase("1975-03-15T20:31:50+05:30", 164127710000L, zone(5, 30)),
            testcase("2024-08-20T22:47:09+10:00", 1724158029000L, zone(10)),
            testcase("2035-06-16T14:53:12Z", 2065618392000L, GMT),
            testcase("2014-10-08T08:41:01-09:30", 1412791861000L, zone(-9, -30)),
            testcase("2030-09-03T10:15:14-02:00", 1914668114000L, zone(-2)),
            testcase("2008-12-29T11:54:51+04:30", 1230535491000L, zone(4, 30)),
            testcase("2087-12-17T22:17:13+09:00", 3722505433000L, zone(9)),
            testcase("2044-10-16T16:45:45+14:00", 2360198745000L, zone(14)),
            testcase("2013-08-02T20:31:53-11:00", 1375515113000L, zone(-11)),
            testcase("2003-02-01T12:06:27.208-03:30", 1044113787208L, zone(-3, -30)),
            testcase("1975-03-20T11:01:09+03:30", 164532669000L, zone(3, 30)),
            testcase("2084-06-11T01:46:57+08:00", 3611497617000L, zone(8)),
            testcase("1984-04-27T08:51:42+12:45", 451858002000L, zone(12, 45)),
            testcase("2015-07-12T01:02:05Z", 1436662925000L, GMT),
            testcase("2024-08-21T22:35:55-05:00", 1724297755000L, zone(-5)),
            testcase("1986-06-16T08:31:08+02:00", 519287468000L, zone(2)),
            testcase("1975-12-13T21:41:37+06:30", 187715497000L, zone(6, 30)),
            testcase("2024-10-30T03:46:02+12:00", 1730216762000L, zone(12)),
            testcase("2004-03-20T07:15:19Z", 1079766919000L, GMT),
            testcase("2090-01-18T20:11:46-07:00", 3788478706000L, zone(-7)),
            testcase("1981-06-30T09:24:33Z", 362741073000L, GMT),
            testcase("2004-01-18T00:44:55+05:45", 1074365995000L, zone(5, 45)),
            testcase("2066-06-08T19:13:54+10:30", 3043212234000L, zone(10, 30)),
            testcase("1986-10-12T00:36:42Z", 529461402000L, GMT),
            testcase("2046-06-18T09:49:07-09:00", 2412960547000L, zone(-9)),
            testcase("2022-09-14T05:44:15-02:00", 1663141455000L, zone(-2)),
            testcase("2083-01-09T01:14:06+05:00", 3566664846000L, zone(5)),
            testcase("1990-12-16T12:41:53+09:30", 661317113000L, zone(9, 30)),
            testcase("2078-10-20T13:45:34Z", 3433499134000L, GMT),
            testcase("2019-05-15T21:01:27-10:00", 1557990087000L, zone(-10)),
            testcase("2068-06-27T22:27:16-10:00", 3108097636000L, zone(-10)),
            testcase("2028-08-27T04:32:27-03:00", 1850974347000L, zone(-3)),
            testcase("2067-01-07T07:40:27+04:30", 3061595427000L, zone(4, 30)),
            testcase("2016-04-05T02:27:37+08:45", 1459791757000L, zone(8, 45)),
            testcase("2061-11-19T02:03:54+14:00", 2899541034000L, zone(14)),
            testcase("2048-10-18T04:05:27-11:00", 2486646327000L, zone(-11)),
            testcase("1975-03-09T20:51:27-04:00", 163644687000L, zone(-4)),
            testcase("2027-05-18T11:09:59+03:30", 1810625999000L, zone(3, 30)),
            testcase("1982-11-27T10:06:43+07:00", 407214403000L, zone(7)),
            testcase("2098-08-26T01:55:32+12:45", 4059810632000L, zone(12, 45)),
            testcase("2052-08-09T12:02:07Z", 2606817727000L, GMT),
            testcase("1996-02-20T09:10:36-06:00", 824829036000L, zone(-6)),
            testcase("2092-05-18T14:38:50+02:00", 3861952730000L, zone(2)),
            testcase("2019-07-01T03:38:37+06:00", 1561930717000L, zone(6)),
            testcase("1974-10-02T06:17:41+11:00", 149887061000L, zone(11)),
            testcase("2022-06-17T08:36:37Z", 1655454997000L, GMT),
            testcase("2090-09-11T10:04:41-08:00", 3808836281000L, zone(-8)),
            testcase("1972-11-02T20:22:48Z", 89583768000L, GMT),
            testcase("1982-08-14T15:01:21+05:45", 398164581000L, zone(5, 45)),
            testcase("2053-11-26T13:15:53+10:00", 2647739753000L, zone(10)),
            testcase("2022-06-13T03:27:35Z", 1655090855000L, GMT),
            testcase("2012-08-20T03:32:24-09:30", 1345467744000L, zone(-9, -30)),
            testcase("1986-09-14T17:49:27-02:00", 527111367000L, zone(-2)),
            testcase("2052-04-08T02:48:19+05:00", 2596139299000L, zone(5)),
            testcase("2081-07-01T19:43:31+09:00", 3518592211000L, zone(9)),
            testcase("2013-03-24T21:13:34Z", 1364159614000L, GMT),
            testcase("2090-10-21T05:11:41-11:00", 3812285501000L, zone(-11)),
            testcase("2085-03-13T16:20:16-03:30", 3635351416000L, zone(-3, -30)),
            testcase("2036-12-18T14:01:30+04:00", 2113207290000L, zone(4)),
            testcase("1983-06-04T11:58:34+08:00", 423547114000L, zone(8)),
            testcase("2036-07-28T12:36:34+13:00", 2100814594000L, zone(13)),
            testcase("1987-06-05T14:21:08Z", 549901268000L, GMT),
            testcase("2068-11-15T09:57:24-05:00", 3120217044000L, zone(-5)),
            testcase("2039-01-15T21:55:56+03:00", 2178730556000L, zone(3)),
            testcase("1979-05-23T13:22:30+06:30", 296290350000L, zone(6, 30)),
            testcase("2098-10-23T06:44:12+12:00", 4064841852000L, zone(12)),
            testcase("1972-03-14T04:31:08+12:45", 69349568000L, zone(12, 45)),
            testcase("2062-10-14T08:16:39Z", 2928039399000L, GMT),
            testcase("2043-06-10T07:09:09-06:00", 2317554549000L, zone(-6)),
            testcase("2065-02-18T21:48:12+01:00", 3002215692000L, zone(1)),
            testcase("1987-12-19T02:22:03+06:00", 566857323000L, zone(6)),
            testcase("2026-02-25T21:30:39+11:00", 1772015439000L, zone(11)),
            testcase("2095-07-22T12:23:56Z", 3962175836000L, GMT),
            testcase("2016-07-19T06:33:33-08:00", 1468938813000L, zone(-8)),
            testcase("2008-12-07T13:51:55-01:00", 1228661515000L, zone(-1)),
            testcase("2010-07-21T11:37:42+05:30", 1279692462000L, zone(5, 30)),
            testcase("2030-02-05T04:12:24+10:00", 1896459144000L, zone(10)),
            testcase("1980-03-30T06:17:11Z", 323245031000L, GMT),
            testcase("2002-03-20T00:59:25-09:30", 1016620165000L, zone(-9, -30)),
            testcase("1977-06-30T21:21:30.044-02:00", 236560890044L, zone(-2)),
            testcase("1988-10-24T03:27:57+04:30", 593650677000L, zone(4, 30)),
            testcase("2089-01-27T11:24:34+09:00", 3757631074000L, zone(9)),
            testcase("2000-12-07T11:56:54+14:00", 976139814000L, zone(14)),
            testcase("2075-03-08T21:27:13-11:00", 3319345633000L, zone(-11)),
            testcase("2036-07-05T05:46:31-03:30", 2098862191000L, zone(-3, -30)),
            testcase("2075-10-26T21:58:46+03:30", 3339340126000L, zone(3, 30)),
            testcase("2077-10-02T18:13:23+08:00", 3400395203000L, zone(8)),
            testcase("2065-04-12T00:47:35+12:45", 3006676955000L, zone(12, 45)),
            testcase("2073-08-24T01:36:48Z", 3270764208000L, GMT),
            testcase("2015-02-25T21:13:27-05:00", 1424916807000L, zone(-5)),
            testcase("2007-10-24T22:55:23+02:00", 1193259323000L, zone(2)),
            testcase("1986-01-29T21:13:52+06:30", 507393832000L, zone(6, 30)),
            testcase("2058-05-02T15:42:40+11:00", 2787540160000L, zone(11)),
            testcase("1988-12-15T00:31:24Z", 598149084000L, GMT),
            testcase("2064-02-14T11:00:24-07:00", 2970237624000L, zone(-7)),
            testcase("2008-10-26T08:48:57Z", 1225010937000L, GMT),
            testcase("2084-02-04T05:19:38+05:45", 3600459278000L, zone(5, 45)),
            testcase("2093-04-03T16:51:12+10:30", 3889578072000L, zone(10, 30)),
            testcase("2020-08-03T23:51:01.001Z", 1596498661001L, GMT),
            testcase("1977-06-11T18:55:13-09:00", 234935713000L, zone(-9)),
            testcase("1976-08-13T17:38:50-02:00", 208813130000L, zone(-2)),
            testcase("2077-01-24T16:43:25+05:00", 3378714205000L, zone(5)),
            testcase("2063-06-02T02:35:06+05:30", 2947957506000L, zone(5, 30)),
            testcase("2074-03-14T10:26:54+09:30", 3288214614000L, zone(9, 30)),
            testcase("2029-03-18T13:03:18Z", 1868533398000L, GMT),
            testcase("1975-07-22T06:29:36-10:00", 175278576000L, zone(-10)),
            testcase("2077-06-11T09:24:08-03:00", 3390639848000L, zone(-3)),
            testcase("2054-06-24T12:44:28+04:30", 2665901668000L, zone(4, 30)),
            testcase("2068-09-23T04:46:30+08:45", 3115569690000L, zone(8, 45)),
            testcase("2044-06-24T10:30:36+14:00", 2350326636000L, zone(14)),
            testcase("1976-07-06T06:25:01-11:00", 205521901000L, zone(-11)),
            testcase("2036-10-27T04:59:37-04:00", 2108710777000L, zone(-4)),
            testcase("2082-04-06T22:31:34+03:30", 3542727694000L, zone(3, 30)),
            testcase("2035-06-05T05:44:30+07:00", 2064609870000L, zone(7)),
            testcase("2067-05-16T16:35:12+12:45", 3072743412000L, zone(12, 45)),
            testcase("2024-10-27T22:59:15Z", 1730069955000L, GMT),
            testcase("2067-08-24T03:44:20-06:00", 3081404660000L, zone(-6)),
            testcase("1971-06-13T06:37:28+02:00", 45635848000L, zone(2)),
            testcase("2062-06-20T15:41:31+06:00", 2918022091000L, zone(6)),
            testcase("2070-10-14T16:27:41+11:00", 3180490061000L, zone(11)),
            testcase("2087-01-10T13:22:50Z", 3693043370000L, GMT),
            testcase("2049-09-11T21:43:34-08:00", 2515038214000L, zone(-8)),
            testcase("2070-12-02T16:39:48Z", 3184763988000L, GMT),
            testcase("2047-10-13T12:32:53+05:30", 2454562973000L, zone(5, 30)),
            testcase("1988-10-30T15:44:30+10:00", 594193470000L, zone(10)),
            testcase("1978-07-28T15:34:04Z", 270488044000L, GMT),
            testcase("1976-06-22T09:01:39-09:30", 204316299000L, zone(-9, -30)),
            testcase("1984-07-31T01:30:36-02:00", 460092636000L, zone(-2)),
            testcase("2028-01-14T16:35:09+05:00", 1831462509000L, zone(5)),
            testcase("2068-06-23T02:05:34+09:00", 3107610334000L, zone(9)),
            testcase("1984-09-05T06:03:19Z", 463212199000L, GMT),
            testcase("2030-11-15T22:49:17-11:00", 1921052957000L, zone(-11)),
            testcase("1975-01-13T02:37:13-03:30", 158825233000L, zone(-3, -30)),
            testcase("2042-05-05T22:28:40+04:00", 2282927320000L, zone(4)),
            testcase("2059-07-10T17:07:51+08:00", 2825053671000L, zone(8)),
            testcase("1981-06-02T22:13:01+13:00", 360321181000L, zone(13)),
            testcase("2000-02-20T17:07:40Z", 951066460000L, GMT),
            testcase("2050-02-12T09:14:41-05:00", 2528288081000L, zone(-5)),
            testcase("1973-08-30T03:47:32-04:00", 115544852000L, zone(-4)),
            testcase("1991-04-04T13:17:27+03:00", 670760247000L, zone(3)),
            testcase("2078-12-05T22:48:14+07:00", 3437480894000L, zone(7)),
            testcase("2014-09-06T07:11:56+12:00", 1409944316000L, zone(12)),
            testcase("2013-02-15T04:28:27Z", 1360902507000L, GMT),
            testcase("1980-02-10T21:01:04-06:00", 319086064000L, zone(-6)),
            testcase("2053-12-17T07:28:06+01:00", 2649565686000L, zone(1)),
            testcase("1972-09-23T05:41:37+06:00", 86053297000L, zone(6)),
            testcase("1988-03-02T18:29:36+11:00", 573290976000L, zone(11)),
            testcase("1997-10-03T03:37:16Z", 875849836000L, GMT),
            testcase("1997-06-15T05:17:44-08:00", 866380664000L, zone(-8)),
            testcase("2065-12-01T01:40:33-01:00", 3026860833000L, zone(-1)),
            testcase("2032-01-15T17:08:42+05:30", 1957779522000L, zone(5, 30)),
            testcase("2014-05-08T21:46:08+10:00", 1399549568000L, zone(10)),
            testcase("1971-04-22T10:22:55Z", 41163775000L, GMT),
            testcase("2040-07-19T16:07:52-09:30", 2226361072000L, zone(-9, -30)),
            testcase("2041-01-10T14:51:26-03:00", 2241453086000L, zone(-3)),
            testcase("2058-10-04T18:54:28+04:30", 2800967068000L, zone(4, 30)),
            testcase("2066-08-12T23:58:37+09:00", 3048850717000L, zone(9)),
            testcase("1996-03-17T14:34:52+14:00", 827022892000L, zone(14)),
            testcase("2037-09-21T12:34:48-11:00", 2137188888000L, zone(-11)),
            testcase("2035-10-25T00:42:05-03:30", 2076898325000L, zone(-3, -30)),
            testcase("1991-02-20T14:34:19+03:30", 667047859000L, zone(3, 30)),
            testcase("1970-04-17T04:36:08+08:00", 9146168000L, zone(8)),
            testcase("2067-09-13T16:27:44+12:45", 3083110964000L, zone(12, 45)),
            testcase("1985-02-12T12:18:27Z", 477058707000L, GMT),
            testcase("2043-09-21T03:34:44-05:00", 2326437284000L, zone(-5)),
            testcase("2047-09-24T13:17:48+02:00", 2452936668000L, zone(2)),
            testcase("2016-06-15T09:25:12+06:30", 1465959312000L, zone(6, 30)),
            testcase("1987-09-19T10:45:47+11:00", 559007147000L, zone(11)),
            testcase("2032-08-10T13:47:19Z", 1975758439000L, GMT),
            testcase("2082-07-24T01:01:08-07:00", 3552105668000L, zone(-7)),
            testcase("2036-12-09T20:35:18Z", 2112467718000L, GMT),
            testcase("2045-11-27T18:03:10+05:45", 2395397890000L, zone(5, 45)),
            testcase("2079-05-10T17:17:44+10:00", 3450928664000L, zone(10)),
            testcase("2064-03-31T06:54:59Z", 2974172099000L, GMT),
            testcase("2088-04-19T06:44:49Z", 3733195489000L, GMT),
            testcase("2085-02-19T01:11:06-09:00", 3633415866000L, zone(-9)),
            testcase("2073-08-09T06:16:00.329-01:00", 3269488560329L, zone(-1)),
            testcase("1993-04-24T23:37:45+05:30", 735674865000L, zone(5, 30)),
            testcase("2039-10-22T14:27:33+09:30", 2202872253000L, zone(9, 30)),
            testcase("1988-03-13T22:07:37Z", 574294057000L, GMT),
            testcase("1988-01-19T18:14:23-10:00", 569650463000L, zone(-10)),
            testcase("2034-10-22T07:24:58-03:00", 2045125498000L, zone(-3)),
            testcase("2064-06-07T23:00:18+04:30", 2980089018000L, zone(4, 30)),
            testcase("2030-03-05T16:37:29+08:45", 1898927549000L, zone(8, 45)),
            testcase("2048-03-27T19:35:02+14:00", 2468900102000L, zone(14)),
            testcase("2049-11-16T09:37:03-12:00", 2520711423000L, zone(-12)),
            testcase("1971-05-23T08:21:20-04:00", 43849280000L, zone(-4)),
            testcase("2055-10-15T08:27:26+03:30", 2707189046000L, zone(3, 30)),
            testcase("2057-06-14T20:37:18+07:00", 2759751438000L, zone(7)),
            testcase("2082-10-16T01:10:46+12:45", 3559292746000L, zone(12, 45)),
            testcase("1994-10-04T03:55:17Z", 781242917000L, GMT),
            testcase("2050-02-15T15:32:26-06:00", 2528573546000L, zone(-6)),
            testcase("2049-12-28T02:18:33+02:00", 2524263513000L, zone(2)),
            testcase("1989-03-09T19:16:05+06:00", 605452565000L, zone(6)),
            testcase("2049-12-22T16:27:16+11:00", 2523763636000L, zone(11)),
            testcase("2036-02-25T11:39:59Z", 2087552399000L, GMT),
            testcase("1979-06-13T23:51:56-08:00", 298194716000L, zone(-8)),
            testcase("1988-06-19T11:59:29Z", 582724769000L, GMT),
            testcase("2044-04-16T06:16:51+05:30", 2344380411000L, zone(5, 30)),
            testcase("2028-09-06T11:11:31+10:00", 1851815491000L, zone(10)),
            testcase("2093-11-24T04:16:20Z", 3909874580000L, GMT),
            testcase("2084-12-21T08:43:31-09:30", 3628260811000L, zone(-9, -30)),
            testcase("1970-01-15T01:55:59-02:00", 1223759000L, zone(-2)),
            testcase("1994-08-17T17:12:32+04:30", 777127352000L, zone(4, 30)),
            testcase("2011-07-02T13:41:43+09:00", 1309581703000L, zone(9)),
            testcase("2088-10-12T20:34:55Z", 3748451695000L, GMT),
            testcase("2060-09-10T22:06:22-11:00", 2862119182000L, zone(-11)),
            testcase("2006-04-25T10:53:12-03:30", 1145974992000L, zone(-3, -30)),
            testcase("2003-10-20T17:12:36+04:00", 1066655556000L, zone(4)),
            testcase("2048-02-18T11:35:00+08:00", 2465609700000L, zone(8)),
            testcase("2037-12-17T21:58:25+08:45", 2144668405000L, zone(8, 45)),
            testcase("2053-08-23T14:36:36+13:00", 2639525796000L, zone(13)),
            testcase("2078-05-07T00:56:36-12:00", 3419153796000L, zone(-12)),
            testcase("2026-02-21T10:12:44-04:00", 1771683164000L, zone(-4)),
            testcase("1973-08-30T01:12:45+03:00", 115510365000L, zone(3)),
            testcase("2016-10-05T15:12:57+07:00", 1475655177000L, zone(7)),
            testcase("1989-02-08T10:18:45+12:00", 602893125000L, zone(12)),
            testcase("2063-07-28T13:18:34Z", 2952854314000L, GMT),
            testcase("1991-02-21T03:28:25-06:00", 667128505000L, zone(-6)),
            testcase("2070-05-19T13:16:54+01:00", 3167727414000L, zone(1)),
            testcase("2003-05-21T03:23:38+06:00", 1053465818000L, zone(6)),
            testcase("2075-03-20T21:56:57+11:00", 3320305017000L, zone(11)),
            testcase("1988-06-04T20:44:24Z", 581460264000L, GMT),
        };
  }
}
