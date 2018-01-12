/*
 * Copyright 2015-2018 Ping Identity Corporation
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
import com.fasterxml.jackson.databind.node.ValueNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.filters.FilterType;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Stack;



/**
 * A parser for SCIM filter expressions.
 */
public class Parser
{

  private static final class StringReader extends Reader
  {
    private final String string;
    private int pos;
    private int mark;

    /**
     * Create a new reader.
     *
     * @param string The string to read from.
     */
    private StringReader(final String string)
    {
      this.string = string;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read()
    {
      if(pos >= string.length())
      {
        return -1;
      }
      return string.charAt(pos++);
    }

    /**
     * Move the current read position back one character.
     */
    public void unread()
    {
      pos--;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ready()
    {
      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported()
    {
      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mark(final int readAheadLimit)
    {
      mark = pos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
      pos = mark;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(final long n)
    {
      long chars = Math.min(string.length() - pos, n);
      pos += chars;
      return chars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final char[] cbuf, final int off, final int len)
    {
      if(pos  >= string.length())
      {
        return -1;
      }
      int chars = Math.min(string.length() - pos, len);
      System.arraycopy(string.toCharArray(), pos, cbuf, off, chars);
      pos += chars;
      return chars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
      // do nothing.
    }
  }

  /**
   * Parse a filter string.
   *
   * @param filterString   The filter string to parse.
   *
   * @return A parsed SCIM filter.
   * @throws BadRequestException If the filter string could not be parsed.
   */
  public static Filter parseFilter(final String filterString)
      throws BadRequestException
  {
    return readFilter(new StringReader(filterString.trim()), false);
  }

  /**
   * Parse a path string.
   *
   * @param pathString   The path string to parse.
   *
   * @return A parsed SCIM path.
   * @throws BadRequestException If the path string could not be parsed.
   */
  public static Path parsePath(final String pathString)
      throws BadRequestException
  {
    if(pathString == null)
    {
      return Path.root();
    }

    final String trimmedPathString = pathString.trim();
    if(trimmedPathString.isEmpty())
    {
      return Path.root();
    }

    Path path = Path.root();
    StringReader reader = new StringReader(trimmedPathString);
    if(SchemaUtils.isUrn(trimmedPathString))
    {
      // The attribute name is prefixed with the schema URN.

      // Find the last ":" before any open brackets. Everything to the left is
      // the schema URN, everything on the right is the attribute name plus a
      // potential value filter.
      int j = trimmedPathString.indexOf('[');
      int i;
      if(j >= 0)
      {
        i = trimmedPathString.substring(0, j).lastIndexOf(':');
      }
      else
      {
        i = trimmedPathString.lastIndexOf(':');
      }
      String schemaUrn = trimmedPathString.substring(0, i++);
      String attributeName =
          trimmedPathString.substring(i, trimmedPathString.length());
      try
      {
        path = Path.root(schemaUrn);
      }
      catch(IllegalArgumentException e)
      {
        throw BadRequestException.invalidPath(e.getMessage());
      }
      if(attributeName.isEmpty())
      {
        // The trailing colon signifies that this is an extension root.
        return path;
      }
      reader = new StringReader(attributeName);
    }

    String token;

    while ((token = readPathToken(reader)) != null)
    {
      if (token.isEmpty())
      {
        // the only time this is allowed to occur is if the previous attribute
        // had a value filter, in which case, consume the token and move on.
        if(path.isRoot() ||
            path.getElement(path.size()-1).getValueFilter() == null)
        {
          final String msg = String.format(
              "Attribute name expected at position %d", reader.mark);
          throw BadRequestException.invalidPath(msg);
        }
      }
      else
      {
        String attributeName = token;
        Filter valueFilter = null;
        try
        {
          if (attributeName.endsWith("["))
          {
            // There is a value path.
            attributeName =
                attributeName.substring(0, attributeName.length() - 1);
            valueFilter = readFilter(reader, true);
          }

          path = path.attribute(attributeName, valueFilter);
        }
        catch(BadRequestException be)
        {
          Debug.debugException(be);
          final String msg = String.format(
              "Invalid value filter: %s", be.getMessage());
          throw BadRequestException.invalidPath(msg);
        }
        catch(Exception e)
        {
          Debug.debugException(e);
          final String msg = String.format(
              "Invalid attribute name starting at position %d: %s",
              reader.mark, e.getMessage());
          throw BadRequestException.invalidPath(msg);
        }
      }
    }

    return path;
  }

  /**
   * Read a path token. A token is either:
   * <ul>
   *   <li>
   *     An attribute name terminated by a period.
   *   </li>
   *   <li>
   *     An attribute name terminated by an opening brace.
   *   </li>
   *   <li>
   * </ul>
   *
   * @param reader The reader to read from.
   *
   * @return The token at the current position, or {@code null} if the end of
   *         the input has been reached.
   * @throws BadRequestException If the path string could not be parsed.
   */
  private static String readPathToken(final StringReader reader)
      throws BadRequestException
  {
    reader.mark(0);
    int c = reader.read();

    StringBuilder b = new StringBuilder();
    while(c > 0)
    {
      if (c == '.')
      {
        if(reader.pos >= reader.string.length())
        {
          // There is nothing after the period.
          throw BadRequestException.invalidPath(
              "Unexpected end of path string");
        }
        // Terminating period. Consume it and return token.
        return b.toString();
      }
      if (c == '[')
      {
        // Terminating opening brace. Consume it and return token.
        b.append((char)c);
        return b.toString();
      }
      if (c == '-' || c == '_' || c == '$' || Character.isLetterOrDigit(c))
      {
        b.append((char)c);
      }
      else
      {
        final String msg = String.format(
            "Unexpected character '%s' at position %d for token starting at %d",
            (char)c, reader.pos - 1, reader.mark);
        throw BadRequestException.invalidPath(msg);
      }
      c = reader.read();
    }

    if(b.length() > 0)
    {
      return b.toString();
    }
    return null;
  }

  /**
   * Read a filter token. A token is either:
   * <ul>
   *   <li>
   *     An attribute path terminated by a space or an opening parenthesis.
   *   </li>
   *   <li>
   *     An attribute path terminated by an opening brace.
   *   </li>
   *   <li>
   *     An operator terminated by a space or an opening parenthesis.
   *   </li>
   *   <li>
   *     An opening parenthesis.
   *   </li>
   *   <li>
   *     An closing parenthesis.
   *   </li>
   *   <li>
   *     An closing brace.
   *   </li>
   *   <li>
   *
   *   </li>
   * </ul>
   *
   * @param reader The reader to read from.
   * @param isValueFilter Whether to read the token for a value filter.
   *
   * @return The token at the current position, or {@code null} if the end of
   *         the input has been reached.
   * @throws BadRequestException If the filter string could not be parsed.
   */
  private static String readFilterToken(final StringReader reader,
                                        final boolean isValueFilter)
      throws BadRequestException
  {
    int c;
    do
    {
      // Skip over any leading spaces.
      reader.mark(0);
      c = reader.read();
    }
    while(c == ' ');

    StringBuilder b = new StringBuilder();
    while(c > 0)
    {
      if (c == ' ')
      {
        // Terminating space. Consume it and return token.
        return b.toString();
      }
      if (c == '(' || c == ')')
      {
        if(b.length() > 0)
        {
          // Do not consume the parenthesis.
          reader.unread();
        }
        else
        {
          b.append((char)c);
        }
        return b.toString();
      }
      if (!isValueFilter && c == '[')
      {
        // Terminating opening brace. Consume it and return token.
        b.append((char)c);
        return b.toString();
      }
      if (isValueFilter && c == ']')
      {
        if(b.length() > 0)
        {
          // Do not consume the closing brace.
          reader.unread();
        }
        else
        {
          b.append((char)c);
        }
        return b.toString();
      }
      if (c == '-' || c == '_' || c == '.' || c == ':' || c == '$' ||
          Character.isLetterOrDigit(c))
      {
        b.append((char)c);
      }
      else
      {
        final String msg = String.format(
            "Unexpected character '%s' at position %d for token starting at %d",
            (char)c, reader.pos - 1, reader.mark);
        throw BadRequestException.invalidFilter(msg);
      }
      c = reader.read();
    }

    if(b.length() > 0)
    {
      return b.toString();
    }
    return null;
  }

  /**
   * Read a filter from the reader.
   *
   * @param reader The reader to read the filter from.
   * @param isValueFilter Whether to read the filter as a value filter.
   * @return The parsed filter.
   * @throws BadRequestException If the filter string could not be parsed.
   */
  private static Filter readFilter(final StringReader reader,
                                   final boolean isValueFilter)
      throws BadRequestException
  {
    final Stack<Filter> outputStack = new Stack<Filter>();
    final Stack<String> precedenceStack = new Stack<String>();

    String token;
    String previousToken = null;

    while((token = readFilterToken(reader, isValueFilter)) != null)
    {
      if(token.equals("(") && expectsNewFilter(previousToken))
      {
        precedenceStack.push(token);
      }
      else if(token.equalsIgnoreCase(FilterType.NOT.getStringValue()) &&
          expectsNewFilter(previousToken))
      {
        // "not" should be followed by an (
        String nextToken = readFilterToken(reader, isValueFilter);
        if(nextToken == null)
        {
          throw BadRequestException.invalidFilter(
              "Unexpected end of filter string");
        }
        if(!nextToken.equals("("))
        {
          final String msg = String.format(
              "Expected '(' at position %d", reader.mark);
          throw BadRequestException.invalidFilter(msg);
        }
        precedenceStack.push(token);
      }
      else if(token.equals(")") && !expectsNewFilter(previousToken))
      {
        String operator = closeGrouping(precedenceStack, outputStack, false);
        if(operator == null)
        {
          final String msg =
              String.format("No opening parenthesis matching closing " +
                  "parenthesis at position %d", reader.mark);
          throw BadRequestException.invalidFilter(msg);
        }
        if (operator.equalsIgnoreCase(FilterType.NOT.getStringValue()))
        {
          // Treat "not" the same as "(" except wrap everything in a not filter.
          outputStack.push(Filter.not(outputStack.pop()));
        }
      }
      else if(token.equalsIgnoreCase(FilterType.AND.getStringValue()) &&
          !expectsNewFilter(previousToken))
      {
        // and has higher precedence than or.
        precedenceStack.push(token);
      }
      else if(token.equalsIgnoreCase(FilterType.OR.getStringValue()) &&
          !expectsNewFilter(previousToken))
      {
        // pop all the pending ands first before pushing or.
        LinkedList<Filter> andComponents = new LinkedList<Filter>();
        while (!precedenceStack.isEmpty())
        {
          if (precedenceStack.peek().equalsIgnoreCase(
              FilterType.AND.getStringValue()))
          {
            precedenceStack.pop();
            andComponents.addFirst(outputStack.pop());
          }
          else
          {
            break;
          }
          if(!andComponents.isEmpty())
          {
            andComponents.addFirst(outputStack.pop());
            outputStack.push(Filter.and(andComponents));
          }
        }

        precedenceStack.push(token);
      }
      else if(token.endsWith("[") && expectsNewFilter(previousToken))
      {
        // This is a complex value filter.
        final Path filterAttribute;
        try
        {
          filterAttribute = parsePath(
              token.substring(0, token.length() - 1));
        }
        catch (final BadRequestException e)
        {
          Debug.debugException(e);
          final String msg = String.format(
              "Invalid attribute path at position %d: %s",
              reader.mark, e.getMessage());
          throw BadRequestException.invalidFilter(msg);
        }

        if(filterAttribute.isRoot())
        {
          final String msg = String.format(
              "Attribute path expected at position %d", reader.mark);
          throw BadRequestException.invalidFilter(msg);
        }

        outputStack.push(Filter.hasComplexValue(
            filterAttribute, readFilter(reader, true)));
      }
      else if(isValueFilter && token.equals("]") &&
          !expectsNewFilter(previousToken))
      {
        break;
      }
      else if(expectsNewFilter(previousToken))
      {
        // This must be an attribute path followed by operator and maybe value.
        final Path filterAttribute;
        try
        {
          filterAttribute = parsePath(token);
        }
        catch (final BadRequestException e)
        {
          Debug.debugException(e);
          final String msg = String.format(
              "Invalid attribute path at position %d: %s",
              reader.mark, e.getMessage());
          throw BadRequestException.invalidFilter(msg);
        }

        if(filterAttribute.isRoot())
        {
          final String msg = String.format(
              "Attribute path expected at position %d", reader.mark);
          throw BadRequestException.invalidFilter(msg);
        }

        String op = readFilterToken(reader, isValueFilter);

        if(op == null)
        {
          throw BadRequestException.invalidFilter(
              "Unexpected end of filter string");
        }

        if (op.equalsIgnoreCase(FilterType.PRESENT.getStringValue()))
        {
          outputStack.push(Filter.pr(filterAttribute));
        }
        else
        {
          ValueNode valueNode;
          try
          {
            // Mark the beginning of the JSON value so we can later reset back
            // to this position and skip the actual chars that were consumed
            // by Jackson. The Jackson parser is buffered and reads everything
            // until the end of string.
            reader.mark(0);
            ScimJsonFactory scimJsonFactory = (ScimJsonFactory)
                JsonUtils.getObjectReader().getFactory();
            JsonParser parser = scimJsonFactory.createScimFilterParser(reader);
            // The object mapper will return a Java null for JSON null.
            // Have to distinguish between reading a JSON null and encountering
            // the end of string.
            if (parser.getCurrentToken() == null && parser.nextToken() == null)
            {
              // End of string.
              valueNode = null;
            }
            else
            {
              valueNode = parser.readValueAsTree();

              // This is actually a JSON null. Use NullNode.
              if(valueNode == null)
              {
                valueNode = JsonUtils.getJsonNodeFactory().nullNode();
              }
            }
            // Reset back to the beginning of the JSON value.
            reader.reset();
            // Skip the number of chars consumed by JSON parser.
            reader.skip(parser.getCurrentLocation().getCharOffset());
          }
          catch (IOException e)
          {
            final String msg = String.format(
                "Invalid comparison value at position %d: %s",
                reader.mark, e.getMessage());
            throw BadRequestException.invalidFilter(msg);
          }

          if (valueNode == null)
          {
            throw BadRequestException.invalidFilter(
                "Unexpected end of filter string");
          }

          if (op.equalsIgnoreCase(FilterType.EQUAL.getStringValue()))
          {
            outputStack.push(Filter.eq(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.NOT_EQUAL.getStringValue()))
          {
            outputStack.push(Filter.ne(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.CONTAINS.getStringValue()))
          {
            outputStack.push(Filter.co(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.STARTS_WITH.getStringValue()))
          {
            outputStack.push(Filter.sw(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.ENDS_WITH.getStringValue()))
          {
            outputStack.push(Filter.ew(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.GREATER_THAN.getStringValue()))
          {
            outputStack.push(Filter.gt(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.GREATER_OR_EQUAL.getStringValue()))
          {
            outputStack.push(Filter.ge(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.LESS_THAN.getStringValue()))
          {
            outputStack.push(Filter.lt(filterAttribute, valueNode));
          } else if (op.equalsIgnoreCase(
              FilterType.LESS_OR_EQUAL.getStringValue()))
          {
            outputStack.push(Filter.le(filterAttribute, valueNode));
          } else
          {
            final String msg = String.format(
                "Unrecognized attribute operator '%s' at position %d. " +
                    "Expected: eq,ne,co,sw,ew,pr,gt,ge,lt,le", op, reader.mark);
            throw BadRequestException.invalidFilter(msg);
          }
        }
      }
      else
      {
        final String msg = String.format(
            "Unexpected character '%s' at position %d", token,
            reader.mark);
        throw BadRequestException.invalidFilter(msg);
      }
      previousToken = token;
    }

    closeGrouping(precedenceStack, outputStack, true);

    if(outputStack.isEmpty())
    {
      throw BadRequestException.invalidFilter(
          "Unexpected end of filter string");
    }
    return outputStack.pop();
  }

  /**
   * Close a grouping of filters enclosed by parenthesis.
   *
   * @param operators The stack of operators tokens.
   * @param output The stack of output tokens.
   * @param isAtTheEnd Whether the end of the filter string was reached.
   * @return The last operator encountered that signaled the end of the group.
   * @throws BadRequestException If the filter string could not be parsed.
   */
  private static String closeGrouping(final Stack<String> operators,
                                      final Stack<Filter> output,
                                      final boolean isAtTheEnd)
      throws BadRequestException
  {
    String operator = null;
    String repeatingOperator = null;
    LinkedList<Filter> components = new LinkedList<Filter>();

    // Iterate over the logical operators on the stack until either there are
    // no more operators or an opening parenthesis or not is found.
    while (!operators.isEmpty())
    {
      operator = operators.pop();
      if(operator.equals("(") ||
          operator.equalsIgnoreCase(FilterType.NOT.getStringValue()))
      {
        if(isAtTheEnd)
        {
          throw BadRequestException.invalidFilter(
              "Unexpected end of filter string");
        }
        break;
      }
      if(repeatingOperator == null)
      {
        repeatingOperator = operator;
      }
      if(!operator.equals(repeatingOperator))
      {
        if(output.isEmpty())
        {
          throw BadRequestException.invalidFilter(
              "Unexpected end of filter string");
        }
        components.addFirst(output.pop());
        if(repeatingOperator.equalsIgnoreCase(FilterType.AND.getStringValue()))
        {
          output.push(Filter.and(components));
        }
        else
        {
          output.push(Filter.or(components));
        }
        components.clear();
        repeatingOperator = operator;
      }
      if(output.isEmpty())
      {
        throw BadRequestException.invalidFilter(
            "Unexpected end of filter string");
      }
      components.addFirst(output.pop());
    }

    if(repeatingOperator != null && !components.isEmpty())
    {
      if(output.isEmpty())
      {
        throw BadRequestException.invalidFilter(
            "Unexpected end of filter string");
      }
      components.addFirst(output.pop());
      if(repeatingOperator.equalsIgnoreCase(FilterType.AND.getStringValue()))
      {
        output.push(Filter.and(components));
      }
      else
      {
        output.push(Filter.or(components));
      }
    }

    return operator;
  }

  /**
   * Whether a new filter token is expected given the previous token.
   *
   * @param previousToken The previous filter token.
   * @return Whether a new filter token is expected.
   */
  private static boolean expectsNewFilter(final String previousToken)
  {
    return previousToken == null ||
        previousToken.equals("(") ||
        previousToken.equalsIgnoreCase(FilterType.NOT.getStringValue()) ||
        previousToken.equalsIgnoreCase(FilterType.AND.getStringValue()) ||
        previousToken.equalsIgnoreCase(FilterType.OR.getStringValue());
  }
}
