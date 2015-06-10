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

package com.unboundid.scim2.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a path to one or more JSON values that are the targets
 * of a SCIM PATCH operation. A path may also be used to describe which JSON
 * values to get or set when manipulating SCIM resources using the
 * GenericScimResourceObject class.
 */
public final class Path implements Iterable<Path.Element>
{
  private static Path ROOT = new Path(Collections.<Element>emptyList());

  /**
   * This class represents an element of the path.
   */
  public static final class Element
  {
    private final String attribute;
    private final Filter valueFilter;

    /**
     * Create a new path element.
     *
     * @param attribute The attribute referenced by this path element.
     * @param valueFilter The optional value filter.
     */
    private Element(final String attribute, final Filter valueFilter)
    {
      this.attribute = attribute;
      this.valueFilter = valueFilter;
    }

    /**
     * Retrieves the attribute referenced by this path element.
     *
     * @return The attribute referenced by this path element.
     */
    public String getAttribute()
    {
      return attribute;
    }

    /**
     * Retrieves the value filter that may be used to narrow down the values
     * of the attribute referenced by this path element.
     *
     * @return The value filter that may be used to narrow down the values of
     * the attribute referenced by this path element or {@code null} if all
     * values are referened by this path element.
     */
    public Filter getValueFilter()
    {
      return valueFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      Element element = (Element) o;

      if (!attribute.equals(element.attribute))
      {
        return false;
      }
      if (valueFilter != null ? !valueFilter.equals(element.valueFilter) :
          element.valueFilter != null)
      {
        return false;
      }

      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
      int result = attribute.hashCode();
      result = 31 * result + (valueFilter != null ? valueFilter.hashCode() : 0);
      return result;
    }

    /**
     * Append the string representation of the path element to the provided
     * buffer.
     *
     * @param builder  The buffer to which the string representation of the
     *                 path element is to be appended.
     */
    public void toString(final StringBuilder builder)
    {
      builder.append(attribute);
      if(valueFilter != null)
      {
        builder.append("[");
        builder.append(valueFilter);
        builder.append("]");
      }
    }
  }

  private final List<Element> elements;

  /**
   * Create a new path with the provided elements.
   *
   * @param elements The path elements.
   */
  private Path(final List<Element> elements)
  {
    this.elements = Collections.unmodifiableList(elements);
  }

  /**
   * Create a new path to a sub-attribute of the attribute referenced by this
   * path.
   *
   * @param attribute The name of the sub-attribute.
   *
   * @return A new path to a sub-attribute of the attribute referenced by this
   * path.
   */
  public Path attribute(final String attribute)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.add(new Element(attribute, null));
    return new Path(newElements);
  }

  /**
   * Create a new path to a sub-set of values of a sub-attribute of the
   * attribute referenced by this path.
   *
   * @param attribute The name of the sub-attribute.
   * @param valueFilter The value filter.
   *
   * @return A new path to a sub-attribute of the attribute referenced by this
   * path.
   */
  public Path attribute(final String attribute, final Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.add(new Element(attribute, valueFilter));
    return new Path(newElements);
  }

  /**
   * Creates a new path to the parent of the attribute referenced by this path.
   *
   * @return A new path to the parent of the attribute referenced by this path
   *         or {@code null} if this path is a root path.
   */
  public Path parent()
  {
    if(isRoot())
    {
      return null;
    }
    return new Path(elements.subList(0, elements.size() - 1));
  }

  /**
   * {@inheritDoc}
   */
  public Iterator<Element> iterator()
  {
    return elements.iterator();
  }

  /**
   * Retrieves the path element at the specified index.
   *
   * @param index The index of the path element to retrieve.
   * @return The path element at the index.
   * @throws IndexOutOfBoundsException if the index is out of range
   *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
   */
  public Element getElement(final int index) throws IndexOutOfBoundsException
  {
    return elements.get(index);
  }

  /**
   * Whether this path targets the root of the JSON object that represents the
   * SCIM resource or an schema extension.
   *
   * @return {@code true} if this path targets the root of the JSON object that
   * represents the SCIM resource or an schema extension or {@code false}
   * otherwise.
   */
  public boolean isRoot()
  {
    return elements.isEmpty() || elements.size() == 1 &&
        SchemaUtils.isUrn(elements.get(0).getAttribute());
  }

  /**
   * Retrieves the number of elements in this path.
   *
   * @return the number of elements in this path.
   */
  public int size()
  {
    return elements.size();
  }

  /**
   * Parse a path from its string representation.
   *
   * @param pathString The string representation of the path.
   * @return The parsed path.
   * @throws BadRequestException if the path string could not be parsed.
   */
  @JsonCreator
  public static Path fromString(final String pathString)
      throws BadRequestException
  {
    return Parser.parsePath(pathString);
  }

  /**
   * Creates a path to the root of the JSON object that represents the
   * SCIM resource.
   *
   * @return The path to the root of the JSON object that represents the
   * SCIM resource.
   */
  public static Path root()
  {
    return ROOT;
  }

  /**
   * Creates a path to the root of the JSON object that contains all the
   * extension attributes of an extension schema.
   *
   * @param extensionSchema The the extension schema URN.
   *
   * @return The path to the root of the JSON object that contains all the
   * extension attributes of an extension schema.
   */
  public static Path root(final String extensionSchema)
  {
    if(!SchemaUtils.isUrn(extensionSchema))
    {
      throw new IllegalArgumentException(
          String.format("Invalid extension schema URN: %s",
              extensionSchema));
    }
    return new Path(Collections.singletonList(
        new Element(extensionSchema, null)));
  }

  /**
   * Creates a path to the root of the JSON object that contains all the
   * extension attributes of an extension schema defined by the provided class.
   *
   * @param extensionClass The the extension class that defines the extension
   *                       schema.
   * @param <T> The generic type parameter of the Java class used to represent
   *            the extension.
   *
   * @return The path to the root of the JSON object that contains all the
   * extension attributes of an extension schema.
   */
  public static <T> Path root(final Class<T> extensionClass)
  {
    return root(SchemaUtils.getSchemaUrn(extensionClass));
  }

  /**
   * Retrieves the extension schema URN of the extension attribute referenced by
   * this path.
   *
   * @return The extension schema URN of the extension attribute referenced by
   * this path or {@code null} if this path references a core attribute.
   */
  public String getExtensionSchema()
  {
    if(!elements.isEmpty() && SchemaUtils.isUrn(elements.get(0).getAttribute()))
    {
      return elements.get(0).getAttribute();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    Path path = (Path) o;

    if (!elements.equals(path.elements))
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    return elements.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @JsonValue
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    toString(builder);
    return builder.toString();
  }

  /**
   * Append the string representation of the attribute path to the provided
   * buffer.
   *
   * @param builder  The buffer to which the string representation of the
   *                 attribute path is to be appended.
   */
  public void toString(final StringBuilder builder)
  {
    Iterator<Element> i = elements.iterator();

    Element element;

    // If the first element has an attribute that starts with "urn:", it is a
    // schema extension URN.
    if(i.hasNext())
    {
      element = i.next();
      if(SchemaUtils.isUrn(element.getAttribute()))
      {
        element.toString(builder);
        if(i.hasNext())
        {
          builder.append(':');
        }
      }
      else
      {
        element.toString(builder);
        if(i.hasNext())
        {
          builder.append(".");
        }
      }
    }

    while(i.hasNext())
    {
      i.next().toString(builder);
      if(i.hasNext())
      {
        builder.append(".");
      }
    }
  }
}
