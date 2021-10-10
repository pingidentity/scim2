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

import static com.unboundid.scim2.common.filters.Filter.eq;
import static com.unboundid.scim2.common.filters.Filter.pr;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;

/**
 * Tests for parsing SCIM 2 paths.
 */
public class PathParsingTestCase {
    /**
     * Retrieves a set of valid path strings.
     *
     * @return A set of valid path strings.
     * @ throws ScimException if the path string is invalid.
     */
    private static Stream<Arguments> testValidPathStrings()  throws ScimException {
        return Stream.of(
                Arguments.of("attr",
                        Path.root()
                                .attribute("attr")),
                Arguments.of("urn:extension:attr",
                        Path.root("urn:extension")
                                .attribute("attr")),
                Arguments.of("attr.subAttr",
                        Path.root()
                                .attribute("attr")
                                .attribute("subAttr")),
                Arguments.of("attr[subAttr eq \"78750\"].subAttr",
                        Path.root()
                                .attribute("attr", eq("subAttr", "78750")).
                                attribute("subAttr")),
                Arguments.of("attr.$ref",
                        Path.root()
                                .attribute("attr")
                                .attribute("$ref")),
                Arguments.of("attr[$ref eq \"/Users/xxx\"]",
                        Path.root()
                                .attribute("attr", eq("$ref", "/Users/xxx"))),
                Arguments.of("urn:extension:attr[subAttr eq \"78750\"].subAttr",
                        Path.root("urn:extension")
                                .attribute("attr",
                                        eq("subAttr", "78750"))
                                .attribute("subAttr")),
                Arguments.of("urn:ietf:params:scim:schemas:extension:" +
                                "enterprise:2.0:User:employeeNumber",
                        Path.root("urn:ietf:params:scim:schemas:extension:" +
                                        "enterprise:2.0:User")
                                .attribute("employeeNumber")),
                Arguments.of("urn:pingidentity:schemas:sample:profile:1.0:" +
                                "topicPreferences[(id eq " +
                                "\"urn:X-UnboundID:topic:clothing:shoes\" and " +
                                "strength eq 10 and timeStamp eq " +
                                "\"2015-10-12T14:57:36.494Z\")]",
                        Path.root("urn:pingidentity:schemas:sample:profile:1.0").
                                attribute("topicPreferences",
                                        Filter.and(eq("id",
                                                        "urn:X-UnboundID:topic:clothing:shoes"),
                                                eq("strength", 10),
                                                eq("timeStamp",
                                                        "2015-10-12T14:57:36.494Z")))),

                // The following does not technically conform to the SCIM spec
                // but our path impl is lenient so it may be used with any JSON
                // object.
                Arguments.of("", Path.root()),
                Arguments.of("urn:extension:", Path.root("urn:extension")),
                Arguments.of("urn:extension:attr[subAttr eq \"78750\"]." +
                                "subAttr[subSub pr].this.is.crazy[type eq \"good\"].deep",
                        Path.root("urn:extension").
                                attribute("attr", eq("subAttr", "78750")).
                                attribute("subAttr", pr("subSub")).
                                attribute("this").
                                attribute("is").
                                attribute("crazy", eq("type", "good")).
                                attribute("deep"))
        );
    }


    /**
     * Retrieves a set of invalid path strings.
     *
     * @return A set of invalid path strings.
     */
    private static Stream<Arguments> testInvalidPathStrings() {
        return Stream.of(
                Arguments.of("."),
                Arguments.of("attr."),
                Arguments.of("urn:attr"),
                Arguments.of("attr[].subAttr"),
                Arguments.of(".attr"),
                Arguments.of("urn:extension:."),
                Arguments.of("urn:extension:.attr"),
                Arguments.of("attr[subAttr eq 123].")
        );
    }

    /**
     * Tests the {@code fromString} method with a valid path string.
     *
     * @param pathString   The string representation of the path to fromString.
     * @param expectedPath The expected parsed path instance.
     * @throws Exception If the test fails.
     */
    @ParameterizedTest(name = "{index} tryinig to find pathString {0} in jsonNode {1}")
    @MethodSource("testValidPathStrings")
    public void testParseValidPath(final String pathString,
            final Path expectedPath)
            throws Exception {
        final Path parsedPath = Path.fromString(pathString);
        assertEquals(parsedPath, expectedPath);
    }


    /**
     * Tests the {@code fromString} method with an invalid path string.
     *
     * @param pathString The string representation of the path to fromString.
     * @throws Exception If the test fails.
     */
    @MethodSource("testInvalidPathStrings")
    @ParameterizedTest(name = "{index} {0} is valid pahtString argument")
    public void testParseInvalidPath(final String pathString)
            throws Exception {
        try {
            Path.fromString(pathString);
            fail("Unexpected successful fromString of invalid path: " + pathString);
        }
        catch (BadRequestException e) {
            assertEquals(e.getScimError()
                            .getScimType(),
                    BadRequestException.INVALID_PATH);

        }
    }
}
