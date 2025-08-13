/*
 * Copyright 2015-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toLowerCase;

/**
 * This class represents an attribute path to JSON data. A path is described as
 * a collection of {@link Element} instances, where each element contains an
 * attribute name and an optional filter.
 * <br><br>
 *
 * The simplest example of a path is an attribute name such as {@code userName},
 * which points to simple single-valued data. However, paths can also target
 * specific fields for data stored in other forms, namely complex and
 * multi-valued attributes. In essence, a path describes one or more JSON values
 * stored within a resource. Paths are used for operations like fetches (via
 * filtering) and modifies (via SCIM PATCH requests).
 * <br><br>
 *
 * As an example, consider the following JSON representing a SCIM resource:
 * <pre>
 * {
 *   "schemas": [
 *     "urn:ietf:params:scim:schemas:core:2.0:User",
 *     "urn:ietf:ext:example:2.0:User"
 *   ],
 *   "userName": "ClarkK",
 *   "addresses": [
 *     {
 *       "type": "work",
 *       "streetAddress": "911 Universal City Plaza",
 *       "locality": "Hollywood",
 *       "region": "CA",
 *       "postalCode": "91608",
 *       "country": "US"
 *     },
 *     {
 *       "type": "home",
 *       "streetAddress": "1000 Small Farm Road",
 *       "postalCode": "54321",
 *       "country": "US"
 *     }
 *   ],
 *   "name": {
 *     "givenName": "Clark",
 *     "familyName": "K"
 *   },
 *   "urn:ietf:ext:example:2.0:User": {
 *     "employeeNumber": "1938",
 *     "manager": {
 *       "name": "Mr. Manager"
 *     }
 *   }
 * }
 * </pre>
 *
 * The following strings represent some examples of valid paths that target a
 * field on this resource:
 * <ul>
 *   <li> {@code userName}: Targets the "userName" attribute. This is a path
 *        with a single element and no filter.
 *   <li> {@code addresses[postalCode eq "54321"]}: Targets an address with a
 *        specific postal code value. This is a path with a single element that
 *        also contains a filter value. For this example, the filter only
 *        matches the last address in the array.
 *   <li> {@code name.givenName}: Targets the "givenName" sub-attribute that is
 *        nested beneath the complex "name" attribute. This is a path with two
 *        elements and no filter values.
 *   <li> {@code urn:ietf:ext:example:2.0:User.manager.name}: Targets the "name"
 *        sub-attribute stored within the schema extension. Note that schema
 *        extensions are not accounted for path sizes, so this is a path with
 *        two elements ({@code manager} and {@code name}).
 * </ul>
 * <br><br>
 *
 * To create paths, the following methods may be used:
 * <ul>
 *   <li> {@link #root()}: Creates a "root" object to begin construction of an
 *        attribute path.
 *   <li> {@link #root(String)}: Constructs an attribute path that targets the
 *        base of a schema extension.
 *   <li> {@link #attribute}: Appends an attribute and an optional filter to the
 *        path object.
 *   <li> {@link #fromString(String)}: Creates a path object from a string.
 *   <li> {@link #of(String)}: Equal to {@code Path.root().attribute(String)}.
 *        This should only be used to target simple top-level attributes (e.g.,
 *        "userName", but not "name.givenName" or "addresses[...]").
 * </ul>
 * <br><br>
 *
 * The following Java code represents ways to target fields in the example
 * resource shown above:
 * <pre><code>
 *  // Primary way to create a path. This targets the 'userName' attribute.
 *  Path.root().attribute("userName");
 *
 *  // An alternate way to target the 'userName' attribute. This method is
 *  // mostly useful for hard-coded string inputs.
 *  Path.of("userName");
 *
 *  // Target values within the 'addresses' array with postal code values that
 *  // equal "54321".
 *  Path.root().attribute("addresses", Filter.eq("postalCode", "54321"));
 *
 *  // Target the 'givenName' sub-attribute that is nested within the complex
 *  // 'name' attribute.
 *  Path.root("name").attribute("givenName");
 *
 *  // Target a schema extension object. Schema extension URNs are considered
 *  // part of the root.
 *  Path.root("urn:ietf:example:2.0:User");
 *
 *  // Target fields within a schema extension.
 *  Path.root("urn:ietf:example:2.0:User").attribute("manager");
 *  Path.root("urn:ietf:example:2.0:User")
 *      .attribute("manager").attribute("name");
 *
 *  // For general string conversions, use fromString().
 *  try
 *  {
 *    String orig = "attribute.nested.veryNestedArray[value eq \"matryoshka\"]";
 *    Path convertedPath = Path.fromString(orig);
 *  }
 *  catch (BadRequestException e)
 *  {
 *    // The fromString() method requires exception handling, but this handling
 *    // defends against malformed client paths.
 *  }
 * </code></pre>
 *
 * The following utility functions are helpful when working with paths:
 * <ul>
 *   <li> {@link #getElement(int)}: Fetches a specific element within the path.
 *   <li> {@link #getLastElement()}: Fetches the last element within the path.
 *   <li> {@link #replace}: Updates a specific element within the path.
 *   <li> {@link #subPath}: Removes elements at and after the provided index.
 *   <li> {@link #withoutFilters()}: Returns the path with all filters removed.
 * </ul>
 */
public final class Path implements Iterable<Path.Element>
{
  @Nullable
  private final String schemaUrn;

  @NotNull
  private final List<Element> elements;

  /**
   * This class represents an element of the path.
   */
  public static final class Element
  {
    @NotNull
    private final String attribute;

    @Nullable
    private final Filter valueFilter;

    /**
     * Create a new path element.
     *
     * @param attribute The attribute referenced by this path element.
     * @param valueFilter The optional value filter.
     */
    private Element(@NotNull final String attribute,
                    @Nullable final Filter valueFilter)
    {
      this.attribute = attribute;
      this.valueFilter = valueFilter;
    }

    /**
     * Retrieves the attribute referenced by this path element.
     *
     * @return The attribute referenced by this path element.
     */
    @NotNull
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
     * values are referenced by this path element.
     */
    @Nullable
    public Filter getValueFilter()
    {
      return valueFilter;
    }

    /**
     * Indicates whether the provided object is equal to this path element.
     *
     * @param o   The object to compare.
     * @return    {@code true} if the provided object is equal to this path
     *            element, or {@code false} if not.
     */
    @Override
    public boolean equals(@Nullable final Object o)
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

      if (!toLowerCase(attribute).equals(toLowerCase(element.attribute)))
      {
        return false;
      }
      return Objects.equals(valueFilter, element.valueFilter);
    }

    /**
     * Retrieves a hash code for this path element.
     *
     * @return  A hash code for this path element.
     */
    @Override
    public int hashCode()
    {
      return Objects.hash(toLowerCase(attribute), valueFilter);
    }

    /**
     * Retrieves a string representation of this path element.
     *
     * @return  A string representation of this path element.
     */
    @Override
    @NotNull
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
    public void toString(@NotNull final StringBuilder builder)
    {
      builder.append(attribute);
      if (valueFilter != null)
      {
        builder.append("[");
        builder.append(valueFilter);
        builder.append("]");
      }
    }
  }

  /**
   * Create a new path with the provided elements.
   *
   * @param elements The path elements.
   */
  private Path(@Nullable final String schemaUrn,
               @NotNull final List<Element> elements)
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
  @NotNull
  public Path attribute(@NotNull final String attribute)
  {
    List<Element> newElements = new ArrayList<>(this.elements);
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
  @NotNull
  public Path attribute(@NotNull final String attribute,
                        @Nullable final Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<>(this.elements);
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
  @NotNull
  public Path attribute(@NotNull final Path path)
  {
    List<Element> newElements = new ArrayList<>(elements.size() + path.size());
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
  @NotNull
  public Path replace(final int index,
                      @NotNull final String attribute,
                      @Nullable final Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<>(this.elements);
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
  @NotNull
  public Path replace(final int index, @NotNull final String attribute)
  {
    List<Element> newElements = new ArrayList<>(this.elements);
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
  @NotNull
  public Path replace(final int index, @Nullable final Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<>(this.elements);
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
  @NotNull
  public Path subPath(final int index) throws IndexOutOfBoundsException
  {
    return new Path(schemaUrn, elements.subList(0, index));
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
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
  @NotNull
  public Element getElement(final int index) throws IndexOutOfBoundsException
  {
    return elements.get(index);
  }

  /**
   * Retrieves the path element of the last attribute.
   *
   * @return  The path element at the last index in the list of elements.
   * @throws IndexOutOfBoundsException  If this path is the root attribute
   *                                    (i.e., the element list size is 0).
   *
   * @since 4.0.1
   */
  @NotNull
  public Element getLastElement() throws IndexOutOfBoundsException
  {
    return getElement(elements.size() - 1);
  }

  /**
   * Whether this path targets the root of the JSON object that represents the
   * SCIM resource or a schema extension.
   *
   * @return {@code true} if this path targets the root of the JSON object that
   * represents the SCIM resource or a schema extension or {@code false}
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
  @NotNull
  public Path withoutFilters()
  {
    ArrayList<Element> newElements = new ArrayList<>(elements.size());
    for (Element element : elements)
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
  @NotNull
  public static Path fromString(@Nullable final String pathString)
      throws BadRequestException
  {
    return Parser.parsePath(pathString);
  }

  /**
   * Create a new top-level attribute. This is typically used for hard-coded
   * strings, since this method is shorthand for:
   * <pre><code>
   *   Path.root().attribute("attributeName");
   * </code></pre>
   *
   * For all other uses, {@link #fromString} is encouraged, especially in cases
   * where the path is provided by an external client. See the class-level
   * Javadoc for more details. As an example, consider the following resource:
   * <pre>
   *   {
   *     "schemas": [ "urn:ietf:params:scim:schemas:core:2.0:User" ],
   *     "id": "5ca1e",
   *     "name": {
   *       "givenName": "Clark"
   *     }
   *   }
   * </pre>
   *
   * This method may be used to construct the following paths. Note that
   * {@code name.givenName} is not permitted as an input.
   * <pre><code>
   *   // Target the top-level attributes.
   *   Path schemas = Path.of("schemas");
   *   Path id = Path.of("id");
   *   Path name = Path.of("name");
   * </code></pre>
   *
   * @param attribute The name of the top-level attribute. It should not be a
   *                  sub-attribute or extension, and it may not use a filter.
   * @return  The path representation of the simple attribute.
   *
   * @throws IllegalArgumentException  If the attribute name is a sub-attribute
   * (e.g., "name.familyName"), schema extension, or contains a filter.
   *
   * @since 4.0.1
   */
  @NotNull
  public static Path of(@NotNull final String attribute)
      throws IllegalArgumentException
  {
    if (attribute.contains(".") || attribute.contains(":")
        || attribute.contains("[") || attribute.contains("]"))
    {
      throw new IllegalArgumentException("Attempted creating a top-level Path"
          + " with invalid '.', ':', or '[]' characters.");
    }

    return Path.root().attribute(attribute);
  }

  /**
   * Creates a path to the root of the JSON object that represents the
   * SCIM resource.
   *
   * @return The path to the root of the JSON object that represents the
   * SCIM resource.
   */
  @NotNull
  public static Path root()
  {
    return new Path(null, Collections.emptyList());
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
  @NotNull
  public static Path root(@Nullable final String schemaUrn)
  {
    if (schemaUrn != null && !SchemaUtils.isUrn(schemaUrn))
    {
      throw new IllegalArgumentException(
          String.format("Invalid extension schema URN: %s",
              schemaUrn));
    }
    return new Path(schemaUrn, Collections.emptyList());
  }

  /**
   * Creates a path to the root of the JSON object that contains all the
   * extension attributes of an extension schema defined by the provided class.
   *
   * @param extensionClass The extension class that defines the extension
   *                       schema.
   * @param <T> The generic type parameter of the Java class used to represent
   *            the extension.
   *
   * @return The path to the root of the JSON object that contains all the
   * extension attributes of an extension schema.
   */
  @NotNull
  public static <T> Path root(@NotNull final Class<T> extensionClass)
  {
    return root(SchemaUtils.getSchemaUrn(extensionClass));
  }

  /**
   * Retrieves the schema URN of the attribute referenced by this path.
   *
   * @return The schema URN of the attribute referenced by this path or
   * {@code null} if not specified.
   */
  @Nullable
  public String getSchemaUrn()
  {
    return schemaUrn;
  }

  /**
   * Indicates whether the provided object is equal to this attribute path.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this attribute
   *            path, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
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

  /**
   * Retrieves a hash code for this Path.
   *
   * @return  A hash code for this Path.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(toLowerCase(schemaUrn), elements);
  }

  /**
   * Retrieves a string representation of this Path.
   *
   * @return  A string representation of this Path.
   */
  @Override
  @NotNull
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
  public void toString(@NotNull final StringBuilder builder)
  {
    if (schemaUrn != null)
    {
      builder.append(schemaUrn);
      builder.append(":");
    }
    Iterator<Element> i = elements.iterator();
    while (i.hasNext())
    {
      i.next().toString(builder);
      if (i.hasNext())
      {
        builder.append(".");
      }
    }
  }
}
