/*
 * Copyright 2017-2018 Ping Identity Corporation
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

import com.fasterxml.jackson.databind.JsonNode;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.utils.JsonDiff;
import com.unboundid.scim2.common.utils.JsonUtils;

/**
 * This class can be used to calculate the diffs between two SCIM
 * resources for the purpose of building a set of patch operations.
 * The comparison takes into account the SCIM schema of the resources
 * to be compared.
 */
public class ResourceDiff extends JsonDiff {

  private ResourceTypeDefinition resourceTypeDefinition;

  /**
   * Construct a ResourceDiff instance.
   * @param resourceTypeDefinition the ResourceTypeDefinition of the
   *                               resources to be compared.
   */
  public ResourceDiff(final ResourceTypeDefinition resourceTypeDefinition)
  {
    super();
    this.resourceTypeDefinition = resourceTypeDefinition;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected int compareTo(
      final Path path,
      final JsonNode sourceNode,
      final JsonNode targetNode)
  {
    return JsonUtils.compareTo(sourceNode, targetNode,
        resourceTypeDefinition.getAttributeDefinition(path));
  }
}
