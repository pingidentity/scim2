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

package com.unboundid.scim2.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a number of static utility functions.
 */
public final class StaticUtils
{
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
  public static String toLowerCase(final String s)
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
   * <code>list</code> separated by <code>separator</code>.
   *
   * @param list the list to print
   * @param separator to use between elements
   *
   * @return String representing the list
   */
  public static String listToString(final List<?> list, final String separator)
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
   * <code>collection</code> separated by <code>separator</code>.
   *
   * @param collection to print
   * @param separator to use between elements
   *
   * @return String representing the collection
   */
  public static String collectionToString(final Collection<?> collection,
                                          final String separator)
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
   * Retrieves a UTF-8 byte representation of the provided string.
   *
   * @param  s  The string for which to retrieve the UTF-8 byte representation.
   *
   * @return  The UTF-8 byte representation for the provided string.
   */
  public static byte[] getUTF8Bytes(final String s)
  {
    final int length;
    if((s == null) || ((length = s.length()) == 0))
    {
      return new byte[0];
    }

    final byte[] b = new byte[length];
    for(int i = 0; i < length; i++)
    {
      final char c = s.charAt(i);
      if(c <= 0x7F)
      {
        b[i] = (byte) (c & 0x7F);
      }
      else
      {
        try
        {
          return s.getBytes("UTF-8");
        }
        catch(Exception e)
        {
          // This should never happen.
          Debug.debugException(e);
          return s.getBytes();
        }
      }
    }
    return b;
  }



  /**
   * Retrieves a single-line string representation of the stack trace for the
   * provided {@code Throwable}.  It will include the unqualified name of the
   * {@code Throwable} class, a list of source files and line numbers (if
   * available) for the stack trace, and will also include the stack trace for
   * the cause (if present).
   *
   * @param  t  The {@code Throwable} for which to retrieve the stack trace.
   *
   * @return  A single-line string representation of the stack trace for the
   *          provided {@code Throwable}.
   */
  public static String getStackTrace(final Throwable t)
  {
    final StringBuilder buffer = new StringBuilder();
    getStackTrace(t, buffer);
    return buffer.toString();
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
  public static void getStackTrace(final Throwable t,
                                   final StringBuilder buffer)
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
   * Returns a single-line string representation of the stack trace.  It will
   * include a list of source files and line numbers (if available) for the
   * stack trace.
   *
   * @param  elements  The stack trace.
   *
   * @return  A single-line string representation of the stack trace.
   */
  public static String getStackTrace(final StackTraceElement[] elements)
  {
    final StringBuilder buffer = new StringBuilder();
    getStackTrace(elements, buffer);
    return buffer.toString();
  }



  /**
   * Appends a single-line string representation of the stack trace to the given
   * buffer.  It will include a list of source files and line numbers
   * (if available) for the stack trace.
   *
   * @param  elements  The stack trace.
   * @param  buffer  The buffer to which the information should be appended.
   */
  public static void getStackTrace(final StackTraceElement[] elements,
                                   final StringBuilder buffer)
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
   * Inspects the Throwable to obtain the root cause. This method walks through
   * the exception chain to the last element (the "root" of the tree) and
   * returns that exception.
   * <p>
   * This method handles recursive <code>cause</code> structures that
   * might otherwise cause infinite loops. If the Throwable parameter has a
   * <code>cause</code> of itself, then a reference to itself will be returned.
   * If the <code>cause</code> chain loops, the last element in the chain before
   * it loops is returned.
   *
   * @param t the Throwable to get the root cause for. This may be null.
   * @return the root cause of the Throwable, or null if none is found or the
   *         input is null.
   */
  public static Throwable getRootCause(final Throwable t)
  {
    if(t == null)
    {
      return null;
    }

    List<Throwable> chain = new ArrayList<Throwable>(10);
    Throwable current = t;
    while(current != null && !chain.contains(current))
    {
      chain.add(current);
      current = current.getCause();
    }

    if(chain.size() < 2)
    {
      return t;
    }
    else
    {
      return chain.get(chain.size() - 1);
    }
  }
}
