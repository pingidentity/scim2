/*
 * Copyright 2015-2016 UnboundID Corp.
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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.unboundid.scim2.common.utils.StaticUtils.toLowerCase;

/**
 * A case-insensitive String to JsonNode map with insertion-order iteration.
 */
public class CaseIgnoreMap implements Map<String, JsonNode>
{
  /**
   * A wrapper around the standard String but compares and hashes them
   * in lower-case.
   */
  private static class CaseIgnoreKey
  {
    private final String key;

    public CaseIgnoreKey(final String key)
    {
      this.key = key;
    }

    public String getKey()
    {
      return key;
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

      CaseIgnoreKey that = (CaseIgnoreKey) o;

      return toLowerCase(key).equals(toLowerCase(that.key));

    }

    @Override
    public int hashCode()
    {
      return toLowerCase(key).hashCode();
    }
  }

  /**
   * Key set.
   */
  private static class KeySet extends AbstractSet<String>
  {
    private final Set<CaseIgnoreKey> keys;

    public KeySet(final Set<CaseIgnoreKey> keys)
    {
      this.keys = keys;
    }

    @Override
    public Iterator<String> iterator()
    {
      return new KeyIterator(keys.iterator());
    }

    @Override
    public int size()
    {
      return keys.size();
    }
  }

  /**
   * Iterator for the keys.
   */
  private static class KeyIterator implements Iterator<String>
  {
    private final Iterator<CaseIgnoreKey> iterator;

    public KeyIterator(final Iterator<CaseIgnoreKey> iterator)
    {
      this.iterator = iterator;
    }

    public boolean hasNext()
    {
      return iterator.hasNext();
    }

    public String next()
    {
      return iterator.next().getKey();
    }

    public void remove()
    {
      iterator.remove();
    }
  }

  /**
   * Entry set.
   */
  private static class EntrySet extends AbstractSet<Entry<String, JsonNode>>
  {
    private final Set<Entry<CaseIgnoreKey, JsonNode>> entries;

    public EntrySet(final Set<Entry<CaseIgnoreKey, JsonNode>> entries)
    {
      this.entries = entries;
    }

    @Override
    public Iterator<Entry<String, JsonNode>> iterator()
    {
      return new EntryIterator(entries.iterator());
    }

    @Override
    public int size()
    {
      return entries.size();
    }
  }

  /**
   * Iterator for map entries.
   */
  private static class EntryIterator
      implements Iterator<Entry<String, JsonNode>>
  {
    private final Iterator<Entry<CaseIgnoreKey, JsonNode>> iterator;

    public EntryIterator(
        final Iterator<Entry<CaseIgnoreKey, JsonNode>> iterator)
    {
      this.iterator = iterator;
    }

    public boolean hasNext()
    {
      return iterator.hasNext();
    }

    public Entry<String, JsonNode> next()
    {
      Entry<CaseIgnoreKey, JsonNode> entry = iterator.next();
      return new AbstractMap.SimpleEntry<String, JsonNode>(
          entry.getKey().getKey(), entry.getValue());
    }

    public void remove()
    {
      iterator.remove();
    }
  }

  private final LinkedHashMap<CaseIgnoreKey, JsonNode> attributes;

  /**
   * Create a new empty CaseIgnoreMap.
   */
  public CaseIgnoreMap()
  {
    attributes = new LinkedHashMap<CaseIgnoreKey, JsonNode>();
  }

  /**
   * Create a new CaseIgnoreMap from the contents of the provided map.
   *
   * @param map The map whose mappings are to the placed in this map.
   */
  public CaseIgnoreMap(final Map<String, JsonNode> map)
  {
    attributes = new LinkedHashMap<CaseIgnoreKey, JsonNode>(map.size());
    putAll(map);
  }

  /**
   * {@inheritDoc}
   */
  public int size()
  {
    return attributes.size();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isEmpty()
  {
    return attributes.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  public boolean containsKey(final Object key)
  {
    return attributes.containsKey(new CaseIgnoreKey(key.toString()));
  }

  /**
   * {@inheritDoc}
   */
  public boolean containsValue(final Object value)
  {
    return attributes.containsValue(value);
  }

  /**
   * {@inheritDoc}
   */
  public JsonNode get(final Object key)
  {
    return attributes.get(new CaseIgnoreKey(key.toString()));
  }

  /**
   * {@inheritDoc}
   */
  public JsonNode put(final String key, final JsonNode value)
  {
    return attributes.put(new CaseIgnoreKey(key), value);
  }

  /**
   * {@inheritDoc}
   */
  public JsonNode remove(final Object key)
  {
    return attributes.remove(new CaseIgnoreKey(key.toString()));
  }

  /**
   * {@inheritDoc}
   */
  public void putAll(final Map<? extends String, ? extends JsonNode> m)
  {
    for (Entry<? extends String, ? extends JsonNode> entry : m.entrySet())
    {
      attributes.put(new CaseIgnoreKey(entry.getKey()), entry.getValue());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void clear()
  {
    attributes.clear();
  }

  /**
   * {@inheritDoc}
   */
  public Set<String> keySet()
  {
    return new KeySet(attributes.keySet());
  }

  /**
   * {@inheritDoc}
   */
  public Collection<JsonNode> values()
  {
    return attributes.values();
  }

  /**
   * {@inheritDoc}
   */
  public Set<Entry<String, JsonNode>> entrySet()
  {
    return new EntrySet(attributes.entrySet());
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

    CaseIgnoreMap that = (CaseIgnoreMap) o;

    return attributes.equals(that.attributes);

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
  public String toString()
  {
    return attributes.toString();
  }
}
