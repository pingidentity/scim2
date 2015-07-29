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

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A container for the attributes specified by the SCIM attributes and
 * excludedAttributes parameters. This set is always immutable.
 */
public final class AttributeSet extends AbstractSet<String>
{
  private static final Pattern SEPARATOR = Pattern.compile("\\s*,\\s*");
  private final Set<String> attributes;

  private AttributeSet(final Set<String> attributes)
  {
    this.attributes = Collections.unmodifiableSet(attributes);
  }

  /**
   * Create an attribute set from a comma delimited string.
   *
   * @param attributes The comma delimited string.
   * @return The created attribute set.
   */
  public static AttributeSet fromString(final String attributes)
  {
    return fromStrings(SEPARATOR.split(attributes.trim()));
  }

  /**
   * Create an attribute set from attributes names.
   *
   * @param attributes The attributes names.
   * @return The created attribute set.
   */
  public static AttributeSet fromStrings(final String... attributes)
  {
    Set<String> attributeSet = new LinkedHashSet<String>(attributes.length);
    Collections.addAll(attributeSet, attributes);
    return new AttributeSet(attributeSet);
  }

  /**
   * Create an attribute set from a collection of attribute names.
   *
   * @param attributes The attributes names.
   * @return The created attribute set.
   */
  @JsonCreator
  public static AttributeSet fromCollection(final Collection<String> attributes)
  {
    Set<String> attributeSet = new LinkedHashSet<String>(attributes);
    return new AttributeSet(attributeSet);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return StaticUtils.collectionToString(attributes, ", ");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
  {
    return o == this || attributes.equals(o);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    return attributes.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<String> iterator()
  {
    return attributes.iterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size()
  {
    return attributes.size();
  }
}
