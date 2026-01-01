/*
 * Copyright 2015-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2015-2026 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

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
    @NotNull
    private final String key;

    CaseIgnoreKey(@NotNull final String key)
    {
      this.key = key;
    }

    @NotNull
    public String getKey()
    {
      return key;
    }

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
    @NotNull
    private final Set<CaseIgnoreKey> keys;

    KeySet(@NotNull final Set<CaseIgnoreKey> keys)
    {
      this.keys = keys;
    }

    @Override
    @NotNull
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
    @NotNull
    private final Iterator<CaseIgnoreKey> iterator;

    KeyIterator(@NotNull final Iterator<CaseIgnoreKey> iterator)
    {
      this.iterator = iterator;
    }

    public boolean hasNext()
    {
      return iterator.hasNext();
    }

    @NotNull
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
    @NotNull
    private final Set<Entry<CaseIgnoreKey, JsonNode>> entries;

    EntrySet(@NotNull final Set<Entry<CaseIgnoreKey, JsonNode>> entries)
    {
      this.entries = entries;
    }

    @Override
    @NotNull
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
    @NotNull
    private final Iterator<Entry<CaseIgnoreKey, JsonNode>> iterator;

    EntryIterator(
        @NotNull final Iterator<Entry<CaseIgnoreKey, JsonNode>> iterator)
    {
      this.iterator = iterator;
    }

    public boolean hasNext()
    {
      return iterator.hasNext();
    }

    @NotNull
    public Entry<String, JsonNode> next()
    {
      Entry<CaseIgnoreKey, JsonNode> entry = iterator.next();
      return new AbstractMap.SimpleEntry<>(
          entry.getKey().getKey(), entry.getValue());
    }

    public void remove()
    {
      iterator.remove();
    }
  }

  @NotNull
  private final LinkedHashMap<CaseIgnoreKey, JsonNode> attributes;

  /**
   * Create a new empty CaseIgnoreMap.
   */
  public CaseIgnoreMap()
  {
    attributes = new LinkedHashMap<>();
  }

  /**
   * Create a new CaseIgnoreMap from the contents of the provided map.
   *
   * @param map The map whose mappings are to the placed in this map.
   */
  public CaseIgnoreMap(@NotNull final Map<String, JsonNode> map)
  {
    attributes = new LinkedHashMap<>(map.size());
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
  public boolean containsKey(@NotNull final Object key)
  {
    return attributes.containsKey(new CaseIgnoreKey(key.toString()));
  }

  /**
   * {@inheritDoc}
   */
  public boolean containsValue(@Nullable final Object value)
  {
    return attributes.containsValue(value);
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public JsonNode get(@NotNull final Object key)
  {
    return attributes.get(new CaseIgnoreKey(key.toString()));
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public JsonNode put(@NotNull final String key,
                      @NotNull final JsonNode value)
  {
    return attributes.put(new CaseIgnoreKey(key), value);
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public JsonNode remove(@NotNull final Object key)
  {
    return attributes.remove(new CaseIgnoreKey(key.toString()));
  }

  /**
   * {@inheritDoc}
   */
  public void putAll(@NotNull final Map<? extends String, ? extends JsonNode> m)
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
  @NotNull
  public Set<String> keySet()
  {
    return new KeySet(attributes.keySet());
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Collection<JsonNode> values()
  {
    return attributes.values();
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  public Set<Entry<String, JsonNode>> entrySet()
  {
    return new EntrySet(attributes.entrySet());
  }

  /**
   * Indicates whether the provided object is equal to this map.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this map, or
   *            {@code false} if not.
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

    CaseIgnoreMap that = (CaseIgnoreMap) o;
    return attributes.equals(that.attributes);
  }

  /**
   * Retrieves a hash code for this map.
   *
   * @return  A hash code for this map.
   */
  @Override
  public int hashCode()
  {
    return attributes.hashCode();
  }

  /**
   * Retrieves a string representation of this map.
   *
   * @return  A string representation of this map.
   */
  @Override
  @NotNull
  public String toString()
  {
    return attributes.toString();
  }
}
