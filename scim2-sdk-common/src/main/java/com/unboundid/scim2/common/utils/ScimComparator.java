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

package com.unboundid.scim2.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.messages.SortOrder;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A comparator implementation that could be used to compare POJOs representing
 * SCIM resources using the SCIM sorting parameters.
 */
public class ScimComparator<T> implements Comparator<T>
{
  private final Path sortBy;
  private final SortOrder sortOrder;

  /**
   * Create a new ScimComparator that will sort in ascending order.
   *
   * @param sortBy The path to the attribute to sort by.
   */
  public ScimComparator(final Path sortBy)
  {
    this(sortBy, SortOrder.ASCENDING);
  }

  /**
   * Create a new ScimComparator.
   *
   * @param sortBy The path to the attribute to sort by.
   * @param sortOrder The sort order.
   */
  public ScimComparator(final Path sortBy, final SortOrder sortOrder)
  {
    this.sortBy = sortBy;
    this.sortOrder = sortOrder;
  }

  /**
   * {@inheritDoc}
   */
  public int compare(final T o1, final T o2)
  {
    ObjectNode n1;
    ObjectNode n2;

    if(o1 instanceof GenericScimResource)
    {
      n1 = ((GenericScimResource) o1).getObjectNode();
    }
    else
    {
      n1 = SchemaUtils.createSCIMCompatibleMapper().valueToTree(o1);
    }

    if(o2 instanceof GenericScimResource)
    {
      n2 = ((GenericScimResource) o2).getObjectNode();
    }
    else
    {
      n2 = SchemaUtils.createSCIMCompatibleMapper().valueToTree(o1);
    }

    JsonNode v1 = null;
    JsonNode v2 = null;

    try
    {
      List<JsonNode> v1s = JsonUtils.getValues(sortBy, n1);
      if(!v1s.isEmpty())
      {
        // Always just use the primary or first value of the first found node.
        v1 = getPrimaryOrFirst(v1s.get(0));
      }
    }
    catch (ScimException e)
    {
      Debug.debugException(e);
    }

    try
    {
      List<JsonNode> v2s = JsonUtils.getValues(sortBy, n2);
      if(!v2s.isEmpty())
      {
        // Always just use the primary or first value of the first found node.
        v2 = getPrimaryOrFirst(v2s.get(0));
      }
    }
    catch (ScimException e)
    {
      Debug.debugException(e);
    }

    if(v1 == null && v2 == null)
    {
      return 0;
    }
    // or all attribute types, if there is no data for the specified "sortBy"
    // value they are sorted via the "sortOrder" parameter; i.e., they are
    // ordered last if ascending and first if descending.
    else if(v1 == null)
    {
      return sortOrder == SortOrder.ASCENDING ? 1 : -1;
    }
    else if(v2 == null)
    {
      return sortOrder == SortOrder.ASCENDING ? -1 : 1;
    }
    else
    {
      return sortOrder == SortOrder.ASCENDING ?
          JsonUtils.compareTo(v1, v2) : JsonUtils.compareTo(v2, v1);
    }
  }

  /**
   * Retrieve the value of a complex multi-valued attribute that is marked as
   * primary or the first value in the list. If the provided node is not an
   * array node, then just return the provided node.
   *
   * @param node The JsonNode to retrieve from.
   * @return The primary or first value or {@code null} if the provided array
   * node is empty.
   */
  private JsonNode getPrimaryOrFirst(final JsonNode node)
  {
    // if it's a multi-valued attribute (see Section 2.4
    // [I-D.ietf - scim - core - schema]), if any, or else the first value in
    // the list, if any.

    if(!node.isArray())
    {
      return node;
    }

    if(node.size() == 0)
    {
      return null;
    }

    Iterator<JsonNode> i = node.elements();
    while(i.hasNext())
    {
      JsonNode value = i.next();
      JsonNode primary = value.get("primary");
      if(primary != null && primary.booleanValue())
      {
        return value;
      }
    }
    return node.get(0);
  }

}
