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

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.DebugType;
import com.unboundid.scim2.common.utils.FilterEvaluator;

import java.util.logging.Level;

/**
 * A schema aware filter evaluator that respects case sensitivity.
 */
public class SchemaAwareFilterEvaluator extends FilterEvaluator
{
  private final ResourceTypeDefinition resourceType;

  /**
   * Create a new schema aware filter evaluator.
   *
   * @param resourceType The resource type definition.
   */
  public SchemaAwareFilterEvaluator(final ResourceTypeDefinition resourceType)
  {
    this.resourceType = resourceType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AttributeDefinition getAttributeDefinition(final Path path)
  {
    try
    {
      return resourceType.getAttributeDefinition(path);
    }
    catch (BadRequestException e)
    {
      Debug.debug(Level.WARNING, DebugType.EXCEPTION,
          "Error retrieving attribute definition for " + path.toString(), e);
      return null;
    }
  }
}
