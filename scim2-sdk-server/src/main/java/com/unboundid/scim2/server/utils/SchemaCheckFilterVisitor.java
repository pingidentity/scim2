/*
 * Copyright 2017-2024 Ping Identity Corporation
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
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.AndFilter;
import com.unboundid.scim2.common.filters.ComparisonFilter;
import com.unboundid.scim2.common.filters.ComplexValueFilter;
import com.unboundid.scim2.common.filters.ContainsFilter;
import com.unboundid.scim2.common.filters.EndsWithFilter;
import com.unboundid.scim2.common.filters.EqualFilter;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.filters.FilterVisitor;
import com.unboundid.scim2.common.filters.GreaterThanFilter;
import com.unboundid.scim2.common.filters.GreaterThanOrEqualFilter;
import com.unboundid.scim2.common.filters.LessThanFilter;
import com.unboundid.scim2.common.filters.LessThanOrEqualFilter;
import com.unboundid.scim2.common.filters.NotEqualFilter;
import com.unboundid.scim2.common.filters.NotFilter;
import com.unboundid.scim2.common.filters.OrFilter;
import com.unboundid.scim2.common.filters.PresentFilter;
import com.unboundid.scim2.common.filters.StartsWithFilter;
import com.unboundid.scim2.common.types.AttributeDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.unboundid.scim2.server.utils.SchemaChecker.Option.ALLOW_UNDEFINED_ATTRIBUTES;
import static com.unboundid.scim2.server.utils.SchemaChecker.Option.ALLOW_UNDEFINED_SUB_ATTRIBUTES;



/**
 * Filter visitor to check attribute paths against the schema.
 */
public final class SchemaCheckFilterVisitor
    implements FilterVisitor<Filter, Object>
{
  private final Path parentPath;
  private final ResourceTypeDefinition resourceType;
  private final SchemaChecker schemaChecker;
  private final SchemaChecker.Results results;


  private SchemaCheckFilterVisitor(
      final Path parentPath,
      final ResourceTypeDefinition resourceType,
      final SchemaChecker schemaChecker,
      final SchemaChecker.Results results)
  {
    this.parentPath = parentPath;
    this.resourceType = resourceType;
    this.schemaChecker = schemaChecker;
    this.results = results;
  }



  /**
   * Check the provided filter against the schema.
   *
   * @param filter   The filter to check.
   * @param resourceTypeDefinition The schema to check the filter against.
   * @param schemaChecker The object that will enforce schema constraints.
   * @param enabledOptions  The schema checker enabled options.
   * @param results  The results of checking the filter.
   * @throws ScimException If an exception occurs during the operation.
   */
  static void checkFilter(
      final Filter filter,
      final ResourceTypeDefinition resourceTypeDefinition,
      final SchemaChecker schemaChecker,
      final Set<SchemaChecker.Option> enabledOptions,
      final SchemaChecker.Results results)
      throws ScimException
  {
    if (enabledOptions.contains(ALLOW_UNDEFINED_ATTRIBUTES) &&
        enabledOptions.contains(ALLOW_UNDEFINED_SUB_ATTRIBUTES))
    {
      // Nothing to check because all undefined attributes are allowed.
      return;
    }

    final SchemaCheckFilterVisitor visitor =
        new SchemaCheckFilterVisitor(
            null, resourceTypeDefinition, schemaChecker, results);
    filter.visit(visitor, null);
  }



  /**
   * Check the provided value filter in a patch path against the schema.
   *
   * @param parentPath  The parent attribute associated with the value filter.
   * @param filter   The value filter to check.
   * @param resourceTypeDefinition The schema to check the filter against.
   * @param schemaChecker The object that will enforce schema constraints.
   * @param enabledOptions  The schema checker enabled options.
   * @param results  The results of checking the filter.
   * @throws ScimException If an exception occurs during the operation.
   */
  static void checkValueFilter(
      final Path parentPath,
      final Filter filter,
      final ResourceTypeDefinition resourceTypeDefinition,
      final SchemaChecker schemaChecker,
      final Set<SchemaChecker.Option> enabledOptions,
      final SchemaChecker.Results results)
      throws ScimException
  {
    if (enabledOptions.contains(ALLOW_UNDEFINED_SUB_ATTRIBUTES))
    {
      // Nothing to check because all undefined sub-attributes are allowed.
      return;
    }

    final SchemaCheckFilterVisitor visitor =
        new SchemaCheckFilterVisitor(
            parentPath, resourceTypeDefinition, schemaChecker, results);
    filter.visit(visitor, null);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final EqualFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final NotEqualFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final ContainsFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final StartsWithFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final EndsWithFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final PresentFilter filter, final Object param)
      throws ScimException
  {
    checkAttributePath(filter.getAttributePath());
    return filter;
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final GreaterThanFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final GreaterThanOrEqualFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final LessThanFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final LessThanOrEqualFilter filter, final Object param)
      throws ScimException
  {
    return visitComparisonFilter(filter, param);
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final AndFilter filter, final Object param)
      throws ScimException
  {
    for (Filter f : filter.getCombinedFilters())
    {
      f.visit(this, param);
    }
    return filter;
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final OrFilter filter, final Object param)
      throws ScimException
  {
    for (Filter f : filter.getCombinedFilters())
    {
      f.visit(this, param);
    }
    return filter;
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final NotFilter filter, final Object param)
      throws ScimException
  {
    filter.getInvertedFilter().visit(this, param);
    return filter;
  }



  /**
   * {@inheritDoc}
   */
  public Filter visit(final ComplexValueFilter filter, final Object param)
      throws ScimException
  {
    checkAttributePath(filter.getAttributePath());
    return filter;
  }



  private Filter visitComparisonFilter(final ComparisonFilter filter,
                                       final Object param)
  {
    checkAttributePath(filter.getAttributePath());
    return filter;
  }



  private void checkAttributePath(final Path path)
  {
    if (this.parentPath != null)
    {
      final Path fullPath = parentPath.attribute(path);
      final AttributeDefinition attribute =
          resourceType.getAttributeDefinition(fullPath);

      // Simple, multi-valued attributes implicitly use "value" as the
      // name to access sub-attributes. Don't print the sub-attribute undefined
      // error in this case.
      if (path.getElement(0).getAttribute().equalsIgnoreCase("value"))
      {
        final AttributeDefinition parentAttr =
                resourceType.getAttributeDefinition(parentPath);
        if (parentAttr.isMultiValued() &&
                (parentAttr.getSubAttributes() == null))
        {
          return;
        }
      }

      if (attribute == null)
      {
        // Can't find the definition for the sub-attribute in a value filter.
        results.addFilterIssue(
            "Sub-attribute " + path.getElement(0) +
            " in value filter for path " + parentPath.toString() +
            " is undefined");
      }
    }
    else
    {
      final AttributeDefinition attribute =
          resourceType.getAttributeDefinition(path);
      if (attribute == null)
      {
        // Can't find the attribute definition for attribute in path.
        final List<String> messages = new ArrayList<String>();
        schemaChecker.addMessageForUndefinedAttr(path, "", messages);
        if (!messages.isEmpty())
        {
          for (String m : messages)
          {
            results.addFilterIssue(m);
          }
        }
      }
    }
  }
}
