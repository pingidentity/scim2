package com.unboundid.scim2;

import com.unboundid.scim2.filters.Filter;
import com.unboundid.scim2.utils.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by boli on 3/26/15.
 */
public final class Path
{
  private static Path ROOT = new Path(Collections.<Element>emptyList());

  public final static class Element
  {
    private final String attribute;
    private final Filter valueFilter;

    private Element(String attribute, Filter valueFilter)
    {
      this.attribute = attribute;
      this.valueFilter = valueFilter;
    }

    public String getAttribute()
    {
      return attribute;
    }

    public Filter getValueFilter()
    {
      return valueFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o)
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

  private Path(List<Element> elements)
  {
    this.elements = Collections.unmodifiableList(elements);
  }

  public Path sub(String attribute)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.add(new Element(attribute, null));
    return new Path(newElements);
  }

  public Path sub(String attribute, Filter valueFilter)
  {
    List<Element> newElements = new ArrayList<Element>(this.elements);
    newElements.add(new Element(attribute, valueFilter));
    return new Path(newElements);
  }

  public List<Element> getElements()
  {
    return elements;
  }

  public static Path fromString(String path)
  {
    return Parser.parsePath(path);
  }

  public static Path root()
  {
    return ROOT;
  }

  public static Path root(String extensionSchemaUrn)
  {
    if(!extensionSchemaUrn.startsWith("urn:") ||
        extensionSchemaUrn.length() <= 4)
    {
      throw new IllegalArgumentException(
          String.format("Invalid extension schema URN: %s",
              extensionSchemaUrn));
    }
    return new Path(Collections.singletonList(
        new Element(extensionSchemaUrn, null)));
  }

  public static Path fromAttribute(String attribute)
  {
    return fromAttribute(null, attribute, null);
  }

  public static Path fromAttribute(String schemaUrn, String attribute)
  {
    return fromAttribute(schemaUrn, attribute, null);
  }

  public static Path fromAttribute(String attribute, Filter valueFilter)
  {
    return fromAttribute(null, attribute, valueFilter);
  }

  public static Path fromAttribute(String schemaUrn, String attribute,
                                   Filter valueFilter)
  {
    if(schemaUrn != null &&
        (!schemaUrn.startsWith("urn:") || schemaUrn.length() <= 4))
    {
      throw new IllegalArgumentException(
          String.format("Invalid schema URN: %s", schemaUrn));
    }
    ArrayList<Element> elements = new ArrayList<Element>(2);
    if(schemaUrn != null)
    {
      elements.add(new Element(schemaUrn, null));
    }
    elements.add(new Element(attribute, valueFilter));
    return new Path(elements);
  }

  public String getSchemaUrn()
  {
    if(elements.isEmpty() && elements.get(0).getAttribute().startsWith("urn:"))
    {
      return elements.get(0).getAttribute();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o)
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

    // If the first element has a colon, it is a schema extension URN.
    if(i.hasNext())
    {
      element = i.next();
      if(element.getAttribute().startsWith("urn:"))
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
