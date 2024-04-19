/*
 * Copyright 2015-2024 Ping Identity Corporation
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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.FilterEvaluator;

/**
 * A schema aware filter evaluator that respects case sensitivity.
 */
public class SchemaAwareFilterEvaluator extends FilterEvaluator
{
  @NotNull
  private final ResourceTypeDefinition resourceType;

  /**
   * Create a new schema aware filter evaluator.
   *
   * @param resourceType The resource type definition.
   */
  public SchemaAwareFilterEvaluator(
      @NotNull final ResourceTypeDefinition resourceType)
  {
    this.resourceType = resourceType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  protected AttributeDefinition getAttributeDefinition(@NotNull final Path path)
  {
    return resourceType.getAttributeDefinition(path);
  }
}
