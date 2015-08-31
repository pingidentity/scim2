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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;



/**
 * A resource trimmer for include and exclude attribute obligations.
 */
public class ObligationResourceTrimmer extends ResourceTrimmer
{
  private final Set<Path> includeAttributes;
  private final Set<Path> excludeAttributes;



  /**
   * Create a new ObligationResourceTrimmer.
   *
   * @param resourceType       The resource type definition for resources to
   *                           trim.
   * @param includeAttributes  The set of include attribute obligations. May be
   *                           null or empty to indicate that there are no
   *                           include attribute obligations.
   * @param excludeAttributes  The set of exclude attribute obligations. May be
   *                           null or empty to indicate that there are no
   *                           exclude attribute obligations.
   * @throws BadRequestException  If any of the provided attributes is not a
   *                              valid attribute path.
   */
  public ObligationResourceTrimmer(final ResourceTypeDefinition resourceType,
                                   final Collection<String> includeAttributes,
                                   final Collection<String> excludeAttributes)
      throws BadRequestException
  {
    this.includeAttributes = new LinkedHashSet<Path>();
    this.excludeAttributes = new LinkedHashSet<Path>();

    if (includeAttributes != null)
    {
      for (String attribute : includeAttributes)
      {
        try
        {
          this.includeAttributes.add(
              resourceType.normalizePath(Path.fromString(attribute)));
        }
        catch (BadRequestException e)
        {
          throw BadRequestException.invalidValue(
              "'" + attribute +
              "' is not a valid value for the includeAttributes parameter: " +
              e.getMessage());
        }
      }
    }

    if (excludeAttributes != null)
    {
      for (String attribute : excludeAttributes)
      {
        try
        {
          this.excludeAttributes.add(
              resourceType.normalizePath(Path.fromString(attribute)));
        }
        catch (BadRequestException e)
        {
          throw BadRequestException.invalidValue(
              "'" + attribute +
              "' is not a valid value for the excludeAttributes parameter: " +
              e.getMessage());
        }
      }
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldReturn(final Path path)
  {
    if (excludeAttributes.contains(path))
    {
      return false;
    }
    else
    {
      if (!includeAttributes.isEmpty())
      {
        return includeAttributes.contains(path);
      }
    }

    return true;
  }
}
