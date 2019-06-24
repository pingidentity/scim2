/*
 * Copyright 2015-2019 Ping Identity Corporation
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
import com.unboundid.scim2.common.utils.StaticUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a path to one or more JSON values that are the targets
 * of a SCIM PATCH operation. A path may also be used to describe which JSON
 * values to get or set when manipulating SCIM resources using the
 * {@link GenericScimResource} class.
 */
public final class Path implements Iterable<Path.Element>
{
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

      if (!StaticUtils.toLowerCase(attribute).equals(
          StaticUtils.toLowerCase(element.attribute)))
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
      int result = StaticUtils.toLowerCase(attribute).hashCode();
      result = 31 * result + (valueFilter != null ? valueFilter.hashCode() : 0);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
      final StringBuilder builder = new StringBuilder();
      toString(builder);
      return builder.toString();
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

  private final String schemaUrn;
  private final List<Element> elements;

  /**
   * Create a new path with the provided elements.
   *
   * @param elements The path elements.
   */
  private Path(final String schemaUrn, final List<Element> elements)
  {
    this.schemaUrn = schemaUrn;
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
    return new Path(schemaUrn, newElements);
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
    return new Path(schemaUrn, newElements);
  }

  /**
   * Create a new path to the sub-attribute path of the attribute referenced by
   * this path.
   *
   * @param path The path of the sub-attribute.
   *
   * @return A new path to a sub-attribute of the attribute referenced by this
   * path.
   */
  public Path attribute(final Path path)
  {
    List<Element> newElements = new ArrayList<Element>(
        this.elements.size() + path.size());
    newElements.addAll(this.elements);
    newElements.addAll(path.elements);
    return new Path(schemaUrn, newElements);
  }

  /**
   * Create a new path where the attribute and filter at the specified index
   * is replaced with those provided.
   *
   * @param index The index of the element to replace.
   * @param attribute The replacement attribute.
   * @param valueFilter The replacement value filter.
   * @return The new path.
   */
  public Path replace(final int index,
                      final String attribute,
                      final Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.set(index, new Element(attribute, valueFilter));
    return new Path(schemaUrn, newElements);
  }

  /**
   * Create a new path where the attribute at the specified index is replaced
   * with the one provided.
   *
   * @param index The index of the element to replace.
   * @param attribute The replacement attribute.
   * @return The new path.
   */
  public Path replace(final int index, final String attribute)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.set(index,
        new Element(attribute, this.elements.get(index).getValueFilter()));
    return new Path(schemaUrn, newElements);
  }

  /**
   * Create a new path where the filter at the specified index is replaced with
   * the one provided.
   *
   * @param index The index of the element to replace.
   * @param valueFilter The replacement value filter.
   * @return The new path.
   */
  public Path replace(final int index, final Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.set(index,
        new Element(this.elements.get(index).getAttribute(), valueFilter));
    return new Path(schemaUrn, newElements);
  }

  /**
   * Creates a new path from beginning portion of this path to the specified
   * index (exclusive). The last element in the newly created path will be
   * the provided index - 1.
   *
   * @param index The exclusive index of the endpoint path element.
   * @return A new path to a beginning portion of this path.
   * @throws IndexOutOfBoundsException if the index is out of range
   *         ({@code index < 0 || index > size()})
   */
  public Path subPath(final int index) throws IndexOutOfBoundsException
  {
    return new Path(schemaUrn, elements.subList(0, index));
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
   *         ({@code index < 0 || index >= size()})
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
    return elements.isEmpty();
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
   * Create a new path from this path with any value filters removed.
   *
   * @return A new path from this path with any value filters removed.
   */
  public Path withoutFilters()
  {
    ArrayList<Element> newElements = new ArrayList<Element>(elements.size());
    for(Element element : elements)
    {
      newElements.add(new Element(element.getAttribute(), null));
    }
    return new Path(schemaUrn, newElements);
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
    return new Path(null, Collections.<Element>emptyList());
  }

  /**
   * Creates a path to the root of the JSON object that contains all the
   * attributes of a schema.
   *
   * @param schemaUrn The schema URN or {@code null}.
   *
   * @return The path to the root of the JSON object that contains all the
   * attributes of an extension URN.
   */
  public static Path root(final String schemaUrn)
  {
    if(schemaUrn != null && !SchemaUtils.isUrn(schemaUrn))
    {
      throw new IllegalArgumentException(
          String.format("Invalid extension schema URN: %s",
              schemaUrn));
    }
    return new Path(schemaUrn, Collections.<Element>emptyList());
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
   * Retrieves the schema URN of the attribute referenced by this path.
   *
   * @return The schema URN of the attribute referenced by this path or
   * {@code null} if not specified.
   */
  public String getSchemaUrn()
  {
    return schemaUrn;
  }

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

    if (schemaUrn != null ? !schemaUrn.equalsIgnoreCase(path.schemaUrn) :
        path.schemaUrn != null)
    {
      return false;
    }
    return elements.equals(path.elements);

  }

  @Override
  public int hashCode()
  {
    int result = schemaUrn != null ?
        StaticUtils.toLowerCase(schemaUrn).hashCode() : 0;
    result = 31 * result + elements.hashCode();
    return result;
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
    if(schemaUrn != null)
    {
      builder.append(schemaUrn);
      builder.append(":");
    }
    Iterator<Element> i = elements.iterator();
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
