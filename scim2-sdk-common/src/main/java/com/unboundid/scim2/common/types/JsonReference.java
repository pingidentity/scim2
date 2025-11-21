/*
 * Copyright 2015-2025 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.annotations.Nullable;

import java.util.Objects;

/**
 * This class can be used in a bean that is converted to json.  If
 * used with the JsonReferenceBeanSerializer, the json that is created
 * will not contain a field of this type if it has never been set.  If
 * however it has been set explicitly to {@code null}, then {@code null} will
 * be serialized.
 *
 * @param <T> The type of object referred to.
 */
public class JsonReference<T>
{
  private boolean set;

  @Nullable
  private T obj;

  /**
   * Constructs a JsonReference that has not been set.
   */
  public JsonReference()
  {
    set = false;
    obj = null;
  }

  /**
   * Constructs a JsonReference that is set to the object passed in.
   *
   * @param obj the object that this JsonReference refers to.
   */
  public JsonReference(@Nullable final T obj)
  {
    set = true;
    this.obj = obj;
  }

  /**
   * Returns true if this reference is set.
   *
   * @return true if this reference is set, or false if it is not.
   */
  public boolean isSet()
  {
    return set;
  }

  /**
   * Returns the object (if it was set), or throws a runtime exception
   * if the object was never set.
   *
   * @return the object that this JsonReference refers to.
   */
  @Nullable
  public T getObjIfSet()
  {
    if (set)
    {
      return obj;
    }

    throw new RuntimeException("Invalid reference");
  }

  /**
   * Returns the object that this instance references if it has been set, or
   * {@code null} if the reference was never set.
   * <br><br>
   * If the JsonReference instance itself might be null, then use the static
   * {@link #getObject(JsonReference)} method.
   *
   * @return The object referred to, or {@code null}.
   */
  @Nullable
  public T getObj()
  {
    return set ? obj : null;
  }

  /**
   * Static version of {@link #getObj} that accepts a reference which is
   * possibly {@code null}. This method is a null-safe way to obtain the value
   * of {@link #getObj}, and is a shortcut for:
   *
   * <pre><code>
   *   var object = (jsonReference == null) ? null : jsonReference.getObj();
   * </code></pre>
   *
   * @param <T>             The Java type of the referenced object.
   * @param jsonReference   The JsonReference instance.
   * @return                The object referenced by {@code jsonReference}. If
   *                        the reference is {@code null}, the object is unset,
   *                        or the object is set to {@code null}, then this
   *                        method will return {@code null}.
   */
  @Nullable
  public static <T> T getObject(@Nullable final JsonReference<T> jsonReference)
  {
    return (jsonReference == null) ? null : jsonReference.getObj();
  }

  /**
   * Indicates whether the provided object is equal to this JSON reference.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this JSON
   *            reference, or {@code false} if not.
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

    JsonReference<?> that = (JsonReference<?>) o;
    if (set != that.set)
    {
      return false;
    }
    return Objects.equals(obj, that.obj);
  }

  /**
   * Retrieves a hash code for this JSON reference.
   *
   * @return  A hash code for this JSON reference.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(set, obj);
  }
}
