/*
 * Copyright 2015-2021 Ping Identity Corporation
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.unboundid.scim2.common.filters.Filter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test coverage for the {@code Filter} class.
 */
public class FilterParsingTestCase {
    /**
     * Retrieves a set of valid filter strings.
     *
     * @return A set of valid filter strings.
     * @ throws BadRequestException if the filter string is invalid.
     */

    private static Stream<Arguments> testValidFilterStrings()  throws BadRequestException {
      return Stream.of(
                Arguments.of("userName Eq \"john\"", eq("userName", "john")),
                Arguments.of("Username eq \"john\"", eq("Username", "john")),
                Arguments.of("userName eq \"bjensen\"", eq("userName", "bjensen")),
                Arguments.of("userName ne \"bjensen\"", ne("userName", "bjensen")),
                Arguments.of("userName co \"jensen\"", co("userName", "jensen")),
                Arguments.of("userName sw \"J\"", sw("userName", "J")),
                Arguments.of("userName ew \"sen\"", ew("userName", "sen")),
                Arguments.of("title pr", pr("title")),
                Arguments.of("meta.lastModified gt \"2011-05-13T04:42:34Z\"", gt("meta.lastModified", "2011-05-13T04:42:34Z")),
                Arguments.of("meta.lastModified ge \"2011-05-13T04:42:34Z\"", ge("meta.lastModified", "2011-05-13T04:42:34Z")),
                Arguments.of("meta.lastModified lt \"2011-05-13T04:42:34Z\"", lt("meta.lastModified", "2011-05-13T04:42:34Z")),
                Arguments.of("meta.lastModified le \"2011-05-13T04:42:34Z\"", le("meta.lastModified", "2011-05-13T04:42:34Z")),
                Arguments.of(" title  pr  and  userType  eq  \"Employee\" ", and(pr("title"), eq("userType", "Employee"))),
                Arguments.of("title pr or userType eq \"Intern\"", or(pr("title"), eq("userType", "Intern"))),
                Arguments.of("not(userName ew \"sen\")", not(ew("userName", "sen"))),
                Arguments.of("not ( userName ew \"sen\" ) ", not(ew("userName", "sen"))),
                Arguments.of("userType eq \"Employee\" and " +
                                "(email co \"example.com\" " +
                                "or email co \"example.org\")",
                        and(eq("userType", "Employee"),
                                or(co("email", "example.com"), co("email", "example.org")))),
                Arguments.of("userName co \"\\ufe00\\\"\\n\\t\\\\\"", co("userName", "\ufe00\"\n\t\\")),
                Arguments.of("urn:extension:members eq 25", eq("urn:extension:members", 25)),
                Arguments.of("urn:extension:members eq 25.52", eq("urn:extension:members", 25.52)),
                Arguments.of("urn:extension:isActive eq true", eq("urn:extension:isActive", true)),
                Arguments.of("urn:extension:isActive eq false", eq("urn:extension:isActive", false)),
                Arguments.of("addresses[zipcode eq 88283 and city ne \"Austin\"]",
                        hasComplexValue("addresses", and(eq("zipcode", 88283), ne("city", "Austin")))),
                Arguments.of("not(addresses[city ne \"Austin\"])", not(hasComplexValue("addresses", ne("city", "Austin")))),
                Arguments.of("title pr and email pr or userType pr", or(and(pr("title"), pr("email")), pr("userType"))),
                Arguments.of("(title pr and email pr) or userType pr", or(and(pr("title"), pr("email")), pr("userType"))),
                Arguments.of("title pr and email pr or not (userType pr)",
                        or(and(pr("title"), pr("email")), not(pr("userType")))),
                Arguments.of("title pr or email pr and userType pr", or(pr("title"), and(pr("email"), pr("userType")))),
                Arguments.of("title pr or (email pr and userType pr)", or(pr("title"), and(pr("email"), pr("userType")))),
                Arguments.of("title pr or email pr and not (userType pr)",
                        or(pr("title"), and(pr("email"), not(pr("userType"))))),
                Arguments.of("title pr and (email pr or userType pr)", and(pr("title"), or(pr("email"), pr("userType")))),
                Arguments.of("title pr and not (email pr or userType pr)",
                        and(pr("title"), not(or(pr("email"), pr("userType"))))),
                Arguments.of("(title pr or email pr) and userType pr", and(or(pr("title"), pr("email")), pr("userType"))),
                Arguments.of("not (title pr or email pr) and userType pr",
                        and(not(or(pr("title"), pr("email"))), pr("userType"))));


    }


    /**
     * Retrieves a set of invalid filter strings.
     *
     * @return A set of invalid filter strings.
     */
    private static List<String> testInvalidFilterStrings() {

        final Arguments of = Arguments.of("", "(",
                "(",
                ")",
                "()",
                "foo",
                "( title pr ) eq ",
                "username pr \"bjensen\"",
                "meta.lastModified lte \"2011-05-13T04:42:34Z\"",
                "username eq",
                "title pr and userType eq \"Employee\" eq",
                "title pr and userType eq true eq",
                "title pr and userType eq 12345.23 eq",
                "userName eq 'bjensen'",
                "userName eq \"bjensen",
                "userName eq \"bjensen\\",
                "userName eq \"\\a\"",
                "userName eq bjensen",
                "userName co \"\\ufe\" or userName co \"a\"",
                "userName bad \"john\"",
                "userName eq (\"john\")",
                "(userName eq \"john\"",
                "userName eq \"john\")",
                "userName eq \"john\" userName pr",
                "userName pr and",
                "and or",
                "not ( and )",
                "not userName pr",
                "userName pr ()",
                "() userName pr",
                "(userName pr)()",
                "()(userName pr)",
                "userName[])",
                "userName pr[)",
                "userName pr [bar pr])",
                "userName[userName pr)",
                "userName[userName[bar pr]])",
                "userName[userName pr]])",
                "[value eq \"false\"]");
        return Arrays.stream(of.get())
                .map(Object::toString)
                .collect(Collectors.toList());
    }


    /**
     * Tests the {@code fromString} method with a valid filter string.
     *
     * @param filterString   The string representation of the filter to
     *                       fromString.
     * @param expectedFilter The expected parsed filter instance.
     * @throws Exception If the test fails.
     */
    @ParameterizedTest(name = "{index} {0} is a valid filter {1}")
    @MethodSource("testValidFilterStrings")
    public void testParseValidFilter(final String filterString,
            final Filter expectedFilter)
            throws Exception {
        final Filter parsedFilter = Filter.fromString(filterString);
        assertEquals(parsedFilter, expectedFilter);
    }


    /**
     * Tests the {@code fromString} method with an invalid filter string.
     *
     * @param filterString The string representation of the filter to
     *                     fromString.
     * @throws Exception If the test fails.
     */
    @ParameterizedTest(name = "{index} {0} is not valid filter")
    @MethodSource("testInvalidFilterStrings")
    public void testParseInvalidFilter(final String filterString)
            throws Exception {
        try {
            Filter.fromString(filterString);
            fail("Unexpected successful fromString of invalid filter: " +
                    filterString);
        }
        catch (BadRequestException e) {
            assertEquals(e.getScimError()
                            .getScimType(),
                    BadRequestException.INVALID_FILTER);
        }
    }
}
