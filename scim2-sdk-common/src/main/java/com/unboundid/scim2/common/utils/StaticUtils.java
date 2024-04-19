/*
 * Copyright 2015-2024 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class provides a number of static utility functions.
 */
public final class StaticUtils
{
  @NotNull
  private static final Pattern SEPARATOR = Pattern.compile("\\s*,\\s*");

  /**
   * Prevent this class from being instantiated.
   */
  private StaticUtils()
  {
    // No implementation is required.
  }



  /**
   * Retrieves an all-lowercase version of the provided string.
   *
   * @param  s  The string for which to retrieve the lowercase version.
   *
   * @return  An all-lowercase version of the provided string.
   */
  @Nullable
  public static String toLowerCase(@Nullable final String s)
  {
    if (s == null)
    {
      return null;
    }

    final int length = s.length();
    final char[] charArray = s.toCharArray();
    for (int i=0; i < length; i++)
    {
      switch (charArray[i])
      {
        case 'A':
          charArray[i] = 'a';
          break;
        case 'B':
          charArray[i] = 'b';
          break;
        case 'C':
          charArray[i] = 'c';
          break;
        case 'D':
          charArray[i] = 'd';
          break;
        case 'E':
          charArray[i] = 'e';
          break;
        case 'F':
          charArray[i] = 'f';
          break;
        case 'G':
          charArray[i] = 'g';
          break;
        case 'H':
          charArray[i] = 'h';
          break;
        case 'I':
          charArray[i] = 'i';
          break;
        case 'J':
          charArray[i] = 'j';
          break;
        case 'K':
          charArray[i] = 'k';
          break;
        case 'L':
          charArray[i] = 'l';
          break;
        case 'M':
          charArray[i] = 'm';
          break;
        case 'N':
          charArray[i] = 'n';
          break;
        case 'O':
          charArray[i] = 'o';
          break;
        case 'P':
          charArray[i] = 'p';
          break;
        case 'Q':
          charArray[i] = 'q';
          break;
        case 'R':
          charArray[i] = 'r';
          break;
        case 'S':
          charArray[i] = 's';
          break;
        case 'T':
          charArray[i] = 't';
          break;
        case 'U':
          charArray[i] = 'u';
          break;
        case 'V':
          charArray[i] = 'v';
          break;
        case 'W':
          charArray[i] = 'w';
          break;
        case 'X':
          charArray[i] = 'x';
          break;
        case 'Y':
          charArray[i] = 'y';
          break;
        case 'Z':
          charArray[i] = 'z';
          break;
        default:
          if (charArray[i] > 0x7F)
          {
            return s.toLowerCase();
          }
          break;
      }
    }

    return new String(charArray);
  }



  /**
   * Creates a string representation of the elements in the
   * list separated by {@code separator}.
   *
   * @param list the list to print
   * @param separator to use between elements
   *
   * @return String representing the list
   */
  @NotNull
  public static String listToString(@NotNull final List<?> list,
                                    @Nullable final String separator)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      sb.append(list.get(i));
      if (i < list.size() - 1) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }



  /**
   * Creates a string representation of the elements in the
   * collection separated by {@code separator}.
   *
   * @param collection to print
   * @param separator to use between elements
   *
   * @return String representing the collection
   */
  @NotNull
  public static String collectionToString(
      @NotNull final Collection<?> collection,
      @Nullable final String separator)
  {
    StringBuilder sb = new StringBuilder();
    for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
      sb.append(iter.next());
      if (iter.hasNext()) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }



  /**
   * Convert an array to a linked hash set.
   *
   * @param i The items in the array to put into the set.
   * @param <T> The type of items in the array.
   * @return The set.
   */
  @NotNull
  public static <T> Set<T> arrayToSet(@NotNull final T... i)
  {
    Set<T> set = new LinkedHashSet<T>(i.length);
    Collections.addAll(set, i);
    return set;
  }



  /**
   * Split a comma separated string. White space characters around the
   * commas will be removed.
   *
   * @param str The comma separated string to split.
   * @return The array of strings computed by splitting this string around
   * commas.
   */
  @NotNull
  public static String[] splitCommaSeparatedString(@NotNull final String str)
  {
    return SEPARATOR.split(str.trim());
  }


  /**
   * Appends a single-line string representation of the stack trace for the
   * provided {@code Throwable} to the given buffer.  It will include the
   * unqualified name of the {@code Throwable} class, a list of source files and
   * line numbers (if available) for the stack trace, and will also include the
   * stack trace for the cause (if present).
   *
   * @param  t       The {@code Throwable} for which to retrieve the stack
   *                 trace.
   * @param  buffer  The buffer to which the information should be appended.
   */
  public static void getStackTrace(@NotNull final Throwable t,
                                   @NotNull final StringBuilder buffer)
  {
    buffer.append(t.getClass().getSimpleName());
    buffer.append('(');

    final String message = t.getMessage();
    if (message != null)
    {
      buffer.append("message='");
      buffer.append(message);
      buffer.append("', ");
    }

    buffer.append("trace='");
    getStackTrace(t.getStackTrace(), buffer);
    buffer.append('\'');

    final Throwable cause = t.getCause();
    if (cause != null)
    {
      buffer.append(", cause=");
      getStackTrace(cause, buffer);
    }
    buffer.append(", revision=");
    buffer.append(Version.REVISION_NUMBER);
    buffer.append(')');
  }


  /**
   * Appends a single-line string representation of the stack trace to the given
   * buffer.  It will include a list of source files and line numbers
   * (if available) for the stack trace.
   *
   * @param  elements  The stack trace.
   * @param  buffer  The buffer to which the information should be appended.
   */
  public static void getStackTrace(@NotNull final StackTraceElement[] elements,
                                   @NotNull final StringBuilder buffer)
  {
    for (int i=0; i < elements.length; i++)
    {
      if (i > 0)
      {
        buffer.append(" / ");
      }

      buffer.append(elements[i].getMethodName());
      buffer.append('(');
      buffer.append(elements[i].getFileName());

      final int lineNumber = elements[i].getLineNumber();
      if (lineNumber > 0)
      {
        buffer.append(':');
        buffer.append(lineNumber);
      }
      buffer.append(')');
    }
  }


  /**
   * Converts an array of objects into a List form. This method is primarily
   * used by the SDK for converting arrays into lists used by multi-valued
   * parameters.
   *
   * @param <T>           The Java type of the elements.
   * @param firstElement  The initial element in the array. This field is
   *                      guaranteed to be first in the list returned by this
   *                      method. This must not be {@code null}.
   * @param elements      An optional array of additional objects to be included
   *                      in the list. Any {@code null} values will be ignored.
   *
   * @return  A list of the elements. All entries in this list will be non-null.
   */
  @NotNull
  public static <T> List<T> toList(@NotNull final T firstElement,
                                   @Nullable final T[] elements)
  {
    Objects.requireNonNull(firstElement);

    List<T> list = new ArrayList<>();
    list.add(firstElement);
    if (elements != null)
    {
      for (T element : elements)
      {
        if (element != null)
        {
          list.add(element);
        }
      }
    }

    return list;
  }
}
