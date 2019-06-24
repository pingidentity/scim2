/*
 * Copyright 2015-2019 Ping Identity Corporation
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.utils.Debug;
import com.unboundid.scim2.common.utils.DebugType;
import com.unboundid.scim2.common.utils.FilterEvaluator;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.common.utils.StaticUtils;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Utility class used to validate and enforce the schema constraints of a
 * Resource Type on JSON objects representing SCIM resources.
 */
public class SchemaChecker
{
  /**
   * Schema checking results.
   */
  public static class Results
  {
    private final List<String> syntaxIssues = new LinkedList<String>();
    private final List<String> mutabilityIssues = new LinkedList<String>();
    private final List<String> pathIssues = new LinkedList<String>();
    private final List<String> filterIssues = new LinkedList<String>();

    void addFilterIssue(final String issue)
    {
      filterIssues.add(issue);
    }

    /**
     * Retrieve any syntax issues found during schema checking.
     *
     * @return syntax issues found during schema checking.
     */
    public List<String> getSyntaxIssues()
    {
      return Collections.unmodifiableList(syntaxIssues);
    }

    /**
     * Retrieve any mutability issues found during schema checking.
     *
     * @return mutability issues found during schema checking.
     */
    public List<String> getMutabilityIssues()
    {
      return Collections.unmodifiableList(mutabilityIssues);
    }

    /**
     * Retrieve any path issues found during schema checking.
     *
     * @return path issues found during schema checking.
     */
    public List<String> getPathIssues()
    {
      return Collections.unmodifiableList(pathIssues);
    }

    /**
     * Retrieve any filter issues found during schema checking.
     *
     * @return filter issues found during schema checking.
     */
    public List<String> getFilterIssues()
    {
      return Collections.unmodifiableList(filterIssues);
    }

    /**
     * Throws an exception if there are schema validation errors.  The exception
     * will contain all of the syntax errors, mutability errors or path issues
     * (in that order of precedence).  The exception message will be the content
     * of baseExceptionMessage followed by a space delimited list of all of the
     * issues of the type (syntax, mutability, or path) being reported.
     *
     * @throws BadRequestException if issues are found during schema checking.
     */
    public void throwSchemaExceptions()
      throws BadRequestException
    {
      if(syntaxIssues.size() > 0)
      {
        throw BadRequestException.invalidSyntax(getErrorString(syntaxIssues));
      }

      if(mutabilityIssues.size() > 0)
      {
        throw BadRequestException.mutability(getErrorString(mutabilityIssues));
      }

      if(pathIssues.size() > 0)
      {
        throw BadRequestException.invalidPath(getErrorString(pathIssues));
      }

      if(filterIssues.size() > 0)
      {
        throw BadRequestException.invalidFilter(getErrorString(filterIssues));
      }
    }

    private String getErrorString(final List<String> issues)
    {
      if ((issues == null) || issues.isEmpty())
      {
        return null;
      }

      return StaticUtils.collectionToString(issues, ", ");
    }
  }

  /**
   * Enumeration that defines options affecting the way schema checking is
   * performed. These options may be enabled and disabled before using the
   * schema checker.
   */
  public enum Option
  {
    /**
     * Relax SCIM 2 standard schema requirements by allowing core or extended
     * attributes in the resource that are not defined by any schema in the
     * resource type definition.
     */
    ALLOW_UNDEFINED_ATTRIBUTES,

    /**
     * Relax SCIM 2 standard schema requirements by allowing sub-attributes
     * that are not defined by the definition of the parent attribute.
     */
    ALLOW_UNDEFINED_SUB_ATTRIBUTES;
  }

  private final ResourceTypeDefinition resourceType;
  private final Collection<AttributeDefinition> commonAndCoreAttributes;
  private final Set<Option> enabledOptions;

  /**
   * Create a new instance that may be used to validate and enforce schema
   * constraints for a resource type.
   *
   * @param resourceType The resource type whose schema(s) to enforce.
   */
  public SchemaChecker(final ResourceTypeDefinition resourceType)
  {
    this.resourceType = resourceType;
    this.commonAndCoreAttributes = new LinkedHashSet<AttributeDefinition>(
        resourceType.getCoreSchema().getAttributes().size() + 4);
    this.commonAndCoreAttributes.addAll(
        SchemaUtils.COMMON_ATTRIBUTE_DEFINITIONS);
    this.commonAndCoreAttributes.addAll(
        resourceType.getCoreSchema().getAttributes());
    this.enabledOptions = new HashSet<Option>();
  }

  /**
   * Enable an option.
   *
   * @param option The option to enable.
   */
  public void enable(final Option option)
  {
    enabledOptions.add(option);
  }

  /**
   * Disable an option.
   *
   * @param option The option to disable.
   */
  public void disable(final Option option)
  {
    enabledOptions.remove(option);
  }

  /**
   * Check a new SCIM resource against the schema.
   *
   * The following checks will be performed:
   * <ul>
   *   <li>
   *     All schema URIs in the schemas attribute are defined.
   *   </li>
   *   <li>
   *     All required schema extensions are present.
   *   </li>
   *   <li>
   *     All required attributes are present.
   *   </li>
   *   <li>
   *     All attributes are defined in schema.
   *   </li>
   *   <li>
   *     All attribute values match the types defined in schema.
   *   </li>
   *   <li>
   *     All canonical type values match one of the values defined in the
   *     schema.
   *   </li>
   *   <li>
   *     No attributes with values are read-only.
   *   </li>
   * </ul>
   *
   * @param objectNode The SCIM resource that will be created. Any read-only
   *                   attributes should be removed first using
   *                   {@link #removeReadOnlyAttributes(ObjectNode)}.
   * @return Schema checking results.
   * @throws ScimException If an error occurred while checking the schema.
   */
  public Results checkCreate(final ObjectNode objectNode) throws ScimException
  {
    ObjectNode copyNode = objectNode.deepCopy();
    Results results = new Results();
    checkResource("", copyNode, results, null, false);
    return results;
  }

  /**
   * Check a set of modify patch operations against the schema. The current
   * state of the SCIM resource may be provided to enable additional checks
   * for attributes that are immutable or required.
   *
   * The following checks will be performed:
   * <ul>
   *   <li>
   *     Undefined schema URIs are not added to the schemas attribute.
   *   </li>
   *   <li>
   *     Required schema extensions are not removed.
   *   </li>
   *   <li>
   *     Required attributes are not removed.
   *   </li>
   *   <li>
   *     Undefined attributes are not added.
   *   </li>
   *   <li>
   *     New attribute values match the types defined in the schema.
   *   </li>
   *   <li>
   *     New canonical values match one of the values defined in the schema.
   *   </li>
   *   <li>
   *     Read-only attribute are not modified.
   *   </li>
   * </ul>
   *
   * Additional checks if the current state of the SCIM resource is provided:
   * <ul>
   *   <li>
   *     The last value from a required multi-valued attribute is not removed.
   *   </li>
   *   <li>
   *     Immutable attribute values are not modified if they already have a
   *     value.
   *   </li>
   * </ul>
   *
   * @param patchOperations The set of modify patch operations to check.
   * @param currentObjectNode The current state of the SCIM resource or
   *                          {@code null} if not available. Any read-only
   *                          attributes should be removed first using
   *                          {@link #removeReadOnlyAttributes(ObjectNode)}.
   * @return Schema checking results.
   * @throws ScimException If an error occurred while checking the schema.
   */
  public Results checkModify(final Iterable<PatchOperation> patchOperations,
                             final ObjectNode currentObjectNode)
      throws ScimException
  {
    ObjectNode copyCurrentNode =
        currentObjectNode == null ? null : currentObjectNode.deepCopy();
    ObjectNode appliedNode =
        currentObjectNode == null ? null :
            removeReadOnlyAttributes(currentObjectNode.deepCopy());
    Results results = new Results();

    int i = 0;
    String prefix;
    for(PatchOperation patchOp : patchOperations)
    {
      prefix = "Patch op[" + i + "]: ";
      Path path = patchOp.getPath();
      JsonNode value = patchOp.getJsonNode();
      Filter valueFilter =
          path == null ? null :
              path.getElement(path.size() - 1).getValueFilter();
      AttributeDefinition attribute = path == null ? null :
          resourceType.getAttributeDefinition(path);
      if(path != null && attribute == null)
      {
        // Can't find the attribute definition for attribute in path.
        addMessageForUndefinedAttr(path, prefix, results.pathIssues);
        continue;
      }
      if(valueFilter != null && attribute != null && !attribute.isMultiValued())
      {
        results.pathIssues.add(prefix +
            "Attribute " + path.getElement(0)+ " in path " +
            path.toString() + " must not have a value selection filter " +
            "because it is not multi-valued");
      }
      if(valueFilter != null && attribute != null)
      {
        SchemaCheckFilterVisitor.checkValueFilter(
            path.withoutFilters(), valueFilter, resourceType, this,
            enabledOptions, results);
      }
      switch (patchOp.getOpType())
      {
        case REMOVE:
          if(attribute == null)
          {
            continue;
          }
          checkAttributeMutability(prefix, null, path, attribute, results,
              currentObjectNode, false, false, false);
          if(valueFilter == null)
          {
            checkAttributeRequired(prefix, path, attribute, results);
          }
          break;
        case REPLACE:
          if(attribute == null)
          {
            checkPartialResource(prefix, (ObjectNode) value, results,
                copyCurrentNode, true, false);
          }
          else
          {
            checkAttributeMutability(prefix, value, path, attribute, results,
                currentObjectNode, true, false, false);
            if(valueFilter != null)
            {
              checkAttributeValue(prefix, value, path, attribute, results,
                  currentObjectNode, true, false);
            }
            else
            {
              checkAttributeValues(prefix, value, path, attribute, results,
                  copyCurrentNode, true, false);
            }
          }
          break;
        case ADD:
          if(attribute == null)
          {
            checkPartialResource(prefix, (ObjectNode) value, results,
                copyCurrentNode, false, true);
          }
          else
          {
            checkAttributeMutability(prefix, value, path, attribute, results,
                currentObjectNode, false, true, false);
            if(valueFilter != null)
            {
              checkAttributeValue(prefix, value, path, attribute, results,
                  currentObjectNode, false, true);
            }
            else
            {
              checkAttributeValues(prefix, value, path, attribute, results,
                  copyCurrentNode, false, true);
            }
          }
          break;
      }

      if(appliedNode != null)
      {
        // Apply the patch so we can later ensure these set of operations
        // wont' be removing the all the values from a
        // required multi-valued attribute.
        try
        {
          patchOp.apply(appliedNode);
        }
        catch(BadRequestException e)
        {
          // No target exceptions are operational errors and not related
          // to the schema. Just ignore.
          if(!e.getScimError().getScimType().equals(
              BadRequestException.NO_TARGET))
          {
            throw e;
          }
        }
      }

      i++;
    }

    if(appliedNode != null)
    {
      checkResource("Applying patch ops results in an invalid resource: ",
          appliedNode, results, copyCurrentNode, false);
    }

    return results;
  }

  /**
   * Check a replacement SCIM resource against the schema. The current
   * state of the SCIM resource may be provided to enable additional checks
   * for attributes that are immutable.
   *
   * The following checks will be performed:
   * <ul>
   *   <li>
   *     All schema URIs in the schemas attribute are defined.
   *   </li>
   *   <li>
   *     All required schema extensions are present.
   *   </li>
   *   <li>
   *     All attributes are defined in schema.
   *   </li>
   *   <li>
   *     All attribute values match the types defined in schema.
   *   </li>
   *   <li>
   *     All canonical type values match one of the values defined in the
   *     schema.
   *   </li>
   *   <li>
   *     No attributes with values are read-only.
   *   </li>
   * </ul>
   *
   * Additional checks if the current state of the SCIM resource is provided:
   * <ul>
   *   <li>
   *     Immutable attribute values are not replaced if they already have a
   *     value.
   *   </li>
   * </ul>
   *
   * @param replacementObjectNode The replacement SCIM resource to check.
   * @param currentObjectNode The current state of the SCIM resource or
   *                          {@code null} if not available.
   * @return Schema checking results.
   * @throws ScimException If an error occurred while checking the schema.
   */
  public Results checkReplace(final ObjectNode replacementObjectNode,
                              final ObjectNode currentObjectNode)
      throws ScimException
  {
    ObjectNode copyReplacementNode = replacementObjectNode.deepCopy();
    ObjectNode copyCurrentNode =
        currentObjectNode == null ? null : currentObjectNode.deepCopy();
    Results results = new Results();
    checkResource("", copyReplacementNode, results, copyCurrentNode, true);
    return results;
  }

  /**
   * Remove any read-only attributes and/or sub-attributes that are present in
   * the provided SCIM resource. This should be performed on new and
   * replacement SCIM resources before schema checking since read-only
   * attributes should be ignored by the service provider on create with POST
   * and modify with PUT operations.
   *
   * @param objectNode The SCIM resource to remove read-only attributes from.
   *                   This method will not alter the provided resource.
   * @return A copy of the SCIM resource with the read-only attributes (if any)
   *         removed.
   */
  public ObjectNode removeReadOnlyAttributes(final ObjectNode objectNode)
  {
    ObjectNode copyNode = objectNode.deepCopy();
    for(SchemaResource schemaExtension :
        resourceType.getSchemaExtensions().keySet())
    {
      JsonNode extension = copyNode.get(schemaExtension.getId());
      if(extension != null && extension.isObject())
      {
        removeReadOnlyAttributes(schemaExtension.getAttributes(),
            (ObjectNode) extension);
      }
    }
    removeReadOnlyAttributes(commonAndCoreAttributes, copyNode);
    return copyNode;
  }



  /**
   * Check the provided filter against the schema.
   *
   * @param filter   The filter to check.
   * @return Schema checking results.
   * @throws ScimException If an error occurred while checking the schema.
   */
  public Results checkSearch(final Filter filter)
      throws ScimException
  {
    Results results = new Results();
    SchemaCheckFilterVisitor.checkFilter(
        filter, resourceType, this, enabledOptions, results);
    return results;
  }



  /**
   * Generate an appropriate error message(s) for an undefined attribute, or
   * no message if the enabled options allow for the undefined attribute.
   *
   * @param  path           The path referencing an undefined attribute.
   * @param  messagePrefix  A prefix for the generated message, or empty string
   *                        if no prefix is needed.
   * @param  messages       The generated messages are to be added to this list.
   */
  void addMessageForUndefinedAttr(final Path path,
                                  final String messagePrefix,
                                  final List<String> messages)
  {
    if(path.size() > 1)
    {
      // This is a path to a sub-attribute. See if the parent attribute is
      // defined.
      if(resourceType.getAttributeDefinition(path.subPath(1)) == null)
      {
        // The parent attribute is also undefined.
        if(!enabledOptions.contains(Option.ALLOW_UNDEFINED_ATTRIBUTES))
        {
          messages.add(messagePrefix +
              "Attribute " + path.getElement(0)+ " in path " +
              path.toString() + " is undefined");
        }
      }
      else
      {
        // The parent attribute is defined but the sub-attribute is
        // undefined.
        if(!enabledOptions.contains(Option.ALLOW_UNDEFINED_SUB_ATTRIBUTES))
        {
          messages.add(messagePrefix +
              "Sub-attribute " + path.getElement(1)+ " in path " +
              path.toString() + " is undefined");
        }
      }
    }
    else if(!enabledOptions.contains(Option.ALLOW_UNDEFINED_ATTRIBUTES))
    {
      messages.add(messagePrefix +
          "Attribute " + path.getElement(0)+ " in path " +
          path.toString() + " is undefined");
    }
  }



  /**
   * Internal method to remove read-only attributes.
   *
   * @param attributes The collection of attribute definitions.
   * @param objectNode The ObjectNode to remove from.
   */
  private void removeReadOnlyAttributes(
      final Collection<AttributeDefinition> attributes,
      final ObjectNode objectNode)
  {
    for(AttributeDefinition attribute : attributes)
    {
      if(attribute.getMutability() == AttributeDefinition.Mutability.READ_ONLY)
      {
        objectNode.remove(attribute.getName());
        continue;
      }
      if(attribute.getSubAttributes() != null)
      {
        JsonNode node = objectNode.path(attribute.getName());
        if (node.isObject())
        {
          removeReadOnlyAttributes(attribute.getSubAttributes(),
              (ObjectNode) node);
        } else if (node.isArray())
        {
          for (JsonNode value : node)
          {
            if (value.isObject())
            {
              removeReadOnlyAttributes(attribute.getSubAttributes(),
                  (ObjectNode) value);
            }
          }
        }
      }
    }
  }

  /**
   * Check a partial resource that is part of the patch operation with no
   * path.
   *
   * @param prefix The issue prefix.
   * @param objectNode The partial resource.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @param isPartialReplace Whether this is a partial replace.
   * @param isPartialAdd Whether this is a partial add.
   * @throws ScimException If an error occurs.
   */
  private void checkPartialResource(final String prefix,
                                    final ObjectNode objectNode,
                                    final Results results,
                                    final ObjectNode currentObjectNode,
                                    final boolean isPartialReplace,
                                    final boolean isPartialAdd)
      throws ScimException
  {

    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      if(SchemaUtils.isUrn(field.getKey()))
      {
        if(!field.getValue().isObject())
        {
          // Bail if the extension namespace is not valid
          results.syntaxIssues.add(prefix + "Extended attributes namespace " +
              field.getKey() + " must be a JSON object");
        }
        else
        {
          boolean found = false;
          for (SchemaResource schemaExtension :
              resourceType.getSchemaExtensions().keySet())
          {
            if (schemaExtension.getId().equals(field.getKey()))
            {
              checkObjectNode(prefix, Path.root(field.getKey()),
                  schemaExtension.getAttributes(),
                  (ObjectNode) field.getValue(), results, currentObjectNode,
                  isPartialReplace, isPartialAdd, false);
              found = true;
              break;
            }
          }
          if(!found &&
              !enabledOptions.contains(Option.ALLOW_UNDEFINED_ATTRIBUTES))
          {
            results.syntaxIssues.add(prefix + "Undefined extended attributes " +
                "namespace " + field);
          }
        }
        i.remove();
      }
    }

    // Check common and core schema
    checkObjectNode(prefix, Path.root(), commonAndCoreAttributes,
        objectNode, results, currentObjectNode,
        isPartialReplace, isPartialAdd, false);
  }

  /**
   * Internal method to check a SCIM resource.
   *
   * @param prefix The issue prefix.
   * @param objectNode The partial resource.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @param isReplace Whether this is a replace.
   * @throws ScimException If an error occurs.
   */
  private void checkResource(final String prefix,
                             final ObjectNode objectNode,
                             final Results results,
                             final ObjectNode currentObjectNode,
                             final boolean isReplace)
      throws ScimException
  {
    // Iterate through the schemas
    JsonNode schemas = objectNode.get(
        SchemaUtils.SCHEMAS_ATTRIBUTE_DEFINITION.getName());
    if(schemas != null && schemas.isArray())
    {
      boolean coreFound = false;
      for (JsonNode schema : schemas)
      {
        if (!schema.isTextual())
        {
          // Go to the next one if the schema URI is not valid. We will report
          // this issue later when we check the values for the schemas
          // attribute.
          continue;
        }

        // Get the extension namespace object node.
        JsonNode extensionNode = objectNode.remove(schema.textValue());
        if (extensionNode == null)
        {
          // Extension listed in schemas but no namespace in resource. Treat it
          // as an empty namesapce to check for required attributes.
          extensionNode = JsonUtils.getJsonNodeFactory().objectNode();
        }
        if (!extensionNode.isObject())
        {
          // Go to the next one if the extension namespace is not valid
          results.syntaxIssues.add(prefix + "Extended attributes namespace " +
              schema.textValue() + " must be a JSON object");
          continue;
        }

        // Find the schema definition.
        Map.Entry<SchemaResource, Boolean> extensionDefinition = null;
        if (schema.textValue().equals(resourceType.getCoreSchema().getId()))
        {
          // Skip the core schema.
          coreFound = true;
          continue;
        } else
        {
          for (Map.Entry<SchemaResource, Boolean> schemaExtension :
              resourceType.getSchemaExtensions().entrySet())
          {
            if (schema.textValue().equals(schemaExtension.getKey().getId()))
            {
              extensionDefinition = schemaExtension;
              break;
            }
          }
        }

        if (extensionDefinition == null)
        {
          // Bail if we can't find the schema definition. We will report this
          // issue later when we check the values for the schemas attribute.
          continue;
        }

        checkObjectNode(prefix, Path.root(schema.textValue()),
            extensionDefinition.getKey().getAttributes(),
            (ObjectNode) extensionNode, results, currentObjectNode,
                        isReplace, false, isReplace);
      }

      if (!coreFound)
      {
        // Make sure core schemas was included.
        results.syntaxIssues.add(prefix + "Value for attribute schemas must " +
            " contain schema URI " + resourceType.getCoreSchema().getId() +
            " because it is the core schema for this resource type");
      }

      // Make sure all required extension schemas were included.
      for (Map.Entry<SchemaResource, Boolean> schemaExtension :
          resourceType.getSchemaExtensions().entrySet())
      {
        if (schemaExtension.getValue())
        {
          boolean found = false;
          for (JsonNode schema : schemas)
          {
            if (schema.textValue().equals(schemaExtension.getKey().getId()))
            {
              found = true;
              break;
            }
          }
          if (!found)
          {
            results.syntaxIssues.add(prefix + "Value for attribute schemas " +
                "must contain schema URI " + schemaExtension.getKey().getId() +
                " because it is a required schema extension for this " +
                "resource type");
          }
        }
      }
    }

    // All defined schema extensions should be removed.
    // Remove any additional extended attribute namespaces not included in
    // the schemas attribute.
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      String fieldName = i.next().getKey();
      if(SchemaUtils.isUrn(fieldName))
      {
        results.syntaxIssues.add(prefix + "Extended attributes namespace "
            + fieldName + " must be included in the schemas attribute");
        i.remove();
      }
    }

    // Check common and core schema
    checkObjectNode(prefix, Path.root(), commonAndCoreAttributes,
        objectNode, results, currentObjectNode,
                    isReplace, false, isReplace);
  }

  /**
   * Check the attribute to see if it violated any mutability constraints.
   *
   * @param prefix The issue prefix.
   * @param node The attribute value.
   * @param path The attribute path.
   * @param attribute The attribute definition.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @param isPartialReplace Whether this is a partial replace.
   * @param isPartialAdd Whether this is a partial add.
   * @param isReplace Whether this is a replace.
   * @throws ScimException If an error occurs.
   */
  private void checkAttributeMutability(final String prefix,
                                        final JsonNode node,
                                        final Path path,
                                        final AttributeDefinition attribute,
                                        final Results results,
                                        final ObjectNode currentObjectNode,
                                        final boolean isPartialReplace,
                                        final boolean isPartialAdd,
                                        final boolean isReplace)
      throws ScimException
  {
    if(attribute.getMutability() ==
        AttributeDefinition.Mutability.READ_ONLY)
    {
      results.mutabilityIssues.add(prefix + "Attribute " + path +
          " is read-only");
    }
    if(attribute.getMutability() ==
        AttributeDefinition.Mutability.IMMUTABLE )
    {
      if(node == null)
      {
        results.mutabilityIssues.add(prefix + "Attribute " + path +
            " is immutable and value(s) may not be removed");
      }
      if(isPartialReplace && !isReplace)
      {
        results.mutabilityIssues.add(prefix + "Attribute " + path +
            " is immutable and value(s) may not be replaced");
      }
      else if(isPartialAdd && currentObjectNode != null &&
          JsonUtils.pathExists(path, currentObjectNode))
      {
        results.mutabilityIssues.add(prefix + "Attribute " + path +
            " is immutable and value(s) may not be added");
      }
      else if(currentObjectNode != null)
      {
        List<JsonNode> currentValues =
            JsonUtils.findMatchingPaths(path, currentObjectNode);
        if(currentValues.size() > 1 ||
            (currentValues.size() == 1 && !currentValues.get(0).equals(node)))
        {
          results.mutabilityIssues.add(prefix + "Attribute " + path +
              " is immutable and it already has a value");
        }
      }
    }

    Filter valueFilter = path.getElement(path.size() - 1).getValueFilter();
    if(attribute.equals(SchemaUtils.SCHEMAS_ATTRIBUTE_DEFINITION) &&
        valueFilter != null)
    {
      // Make sure the core schema and/or required schemas extensions are
      // not removed.
      if (FilterEvaluator.evaluate(valueFilter,
          TextNode.valueOf(resourceType.getCoreSchema().getId())))
      {
        results.syntaxIssues.add(prefix + "Attribute value(s) " + path +
            " may not be removed or replaced because the core schema " +
            resourceType.getCoreSchema().getId() +
            " is required for this resource type");
      }
      for (Map.Entry<SchemaResource, Boolean> schemaExtension :
          resourceType.getSchemaExtensions().entrySet())
      {
        if (schemaExtension.getValue() &&
            FilterEvaluator.evaluate(valueFilter,
                TextNode.valueOf(schemaExtension.getKey().getId())))
        {
          results.syntaxIssues.add(prefix + "Attribute value(s) " +
              path + " may not be removed or replaced because the schema " +
              "extension " + schemaExtension.getKey().getId() +
              " is required for this resource type");
        }
      }
    }
  }

  /**
   * Check the attribute to see if it violated any requirement constraints.
   *
   * @param prefix The issue prefix.
   * @param path The attribute path.
   * @param attribute The attribute definition.
   * @param results The schema check results.
   */
  private void checkAttributeRequired(final String prefix,
                                      final Path path,
                                      final AttributeDefinition attribute,
                                      final Results results)
  {
    // Check required attributes are all present.
    if(attribute.isRequired())
    {
      results.syntaxIssues.add(prefix + "Attribute " + path +
          " is required and must have a value");
    }
  }

  /**
   * Check the attribute values to see if it has the right type.
   *
   * @param prefix The issue prefix.
   * @param node The attribute value.
   * @param path The attribute path.
   * @param attribute The attribute definition.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @param isPartialReplace Whether this is a partial replace.
   * @param isPartialAdd Whether this is a partial add.
   * @throws ScimException If an error occurs.
   */
  private void checkAttributeValues(final String prefix,
                                    final JsonNode node,
                                    final Path path,
                                    final AttributeDefinition attribute,
                                    final Results results,
                                    final ObjectNode currentObjectNode,
                                    final boolean isPartialReplace,
                                    final boolean isPartialAdd)
      throws ScimException
  {
    if(attribute.isMultiValued() && !node.isArray())
    {
      results.syntaxIssues.add(prefix + "Value for multi-valued attribute " +
          path + " must be a JSON array");
      return;
    }
    if(!attribute.isMultiValued() && node.isArray())
    {
      results.syntaxIssues.add(prefix + "Value for single-valued attribute " +
          path + " must not be a JSON array");
      return;
    }

    if(node.isArray())
    {
      int i = 0;
      for (JsonNode value : node)
      {
        // Use a special notation attr[index] to refer to a value of an JSON
        // array.
        if(path.isRoot())
        {
          throw new NullPointerException(
              "Path should always point to an attribute");
        }
        Path parentPath = path.subPath(path.size() - 1);
        Path valuePath = parentPath.attribute(
            path.getElement(path.size() - 1).getAttribute() + "[" + i + "]");
        checkAttributeValue(prefix, value, valuePath, attribute, results,
            currentObjectNode, isPartialReplace, isPartialAdd);
        i++;
      }
    }
    else
    {
      checkAttributeValue(prefix, node, path, attribute, results,
          currentObjectNode, isPartialReplace, isPartialAdd);
    }
  }

  /**
   * Check an attribute value to see if it has the right type.
   *
   * @param prefix The issue prefix.
   * @param node The attribute value.
   * @param path The attribute path.
   * @param attribute The attribute definition.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @param isPartialReplace Whether this is a partial replace.
   * @param isPartialAdd Whether this is a partial add.
   * @throws ScimException If an error occurs.
   */
  private void checkAttributeValue(final String prefix,
                                   final JsonNode node,
                                   final Path path,
                                   final AttributeDefinition attribute,
                                   final Results results,
                                   final ObjectNode currentObjectNode,
                                   final boolean isPartialReplace,
                                   final boolean isPartialAdd)
      throws ScimException
  {
    if(node.isNull())
    {
      return;
    }

    // Check the node type.
    switch(attribute.getType())
    {
      case STRING:
      case DATETIME:
      case REFERENCE:
        if (!node.isTextual())
        {
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " must be a JSON string");
          return;
        }
        break;
      case BOOLEAN:
        if (!node.isBoolean())
        {
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " must be a JSON boolean");
          return;
        }
        break;
      case DECIMAL:
      case INTEGER:
        if (!node.isNumber())
        {
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " must be a JSON number");
          return;
        }
        break;
      case COMPLEX:
        if (!node.isObject())
        {
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " must be a JSON object");
          return;
        }
        break;
      case BINARY:
        if (!node.isTextual() && !node.isBinary())
        {
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " must be a JSON string");
          return;
        }
        break;
      default:
        throw new RuntimeException(
            "Unexpected attribute type " + attribute.getType());
    }

    // If the node type checks out, check the actual value.
    switch(attribute.getType())
    {
      case DATETIME:
        try
        {
          JsonUtils.nodeToDateValue(node);
        }
        catch (Exception e)
        {
          Debug.debug(Level.INFO, DebugType.EXCEPTION,
              "Invalid xsd:dateTime string during schema checking", e);
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " is not a valid xsd:dateTime formatted string");
        }
        break;
      case BINARY:
        try
        {
          node.binaryValue();
        }
        catch (Exception e)
        {
          Debug.debug(Level.INFO, DebugType.EXCEPTION,
              "Invalid base64 string during schema checking", e);
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " is not a valid base64 encoded string");
        }
        break;
      case REFERENCE:
        try
        {
          new URI(node.textValue());
        }
        catch (Exception e)
        {
          Debug.debug(Level.INFO, DebugType.EXCEPTION,
              "Invalid URI string during schema checking", e);
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " is not a valid URI string");
        }
        break;
      case INTEGER:
        if(!node.isIntegralNumber())
        {
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " is not an integral number");
        }
        break;
      case COMPLEX:
        checkObjectNode(prefix, path, attribute.getSubAttributes(),
            (ObjectNode) node, results, currentObjectNode,
            isPartialReplace, isPartialAdd, false);
        break;
      case STRING:
        // Check for canonical values
        if (attribute.getCanonicalValues() != null)
        {
          boolean found = false;
          for (String canonicalValue : attribute.getCanonicalValues())
          {
            if (attribute.isCaseExact() ?
                canonicalValue.equals(node.textValue()) :
                StaticUtils.toLowerCase(canonicalValue).equals(
                    StaticUtils.toLowerCase(node.textValue())))
            {
              found = true;
              break;
            }
          }
          if (!found)
          {
            results.syntaxIssues.add(prefix + "Value " + node.textValue() +
                " is not valid for attribute " + path + " because it " +
                "is not one of the canonical types: " +
                StaticUtils.collectionToString(
                    attribute.getCanonicalValues(), ", "));
          }
        }
    }

    // Special checking of the schemas attribute to ensure that
    // no undefined schemas are listed.
    if (attribute.equals(SchemaUtils.SCHEMAS_ATTRIBUTE_DEFINITION) &&
        path.size() == 1)
    {
      boolean found = false;
      for (SchemaResource schemaExtension :
          resourceType.getSchemaExtensions().keySet())
      {
        if (node.textValue().equals(schemaExtension.getId()))
        {
          found = true;
          break;
        }
      }
      if(!found)
      {
        found = node.textValue().equals(resourceType.getCoreSchema().getId());
      }
      if(!found && !enabledOptions.contains(Option.ALLOW_UNDEFINED_ATTRIBUTES))
      {
        results.syntaxIssues.add(prefix + "Schema URI " + node.textValue() +
            " is not a valid value for attribute " + path + " because it is " +
            "undefined as a core or schema extension for this resource type");
      }
    }
  }

  /**
   * Check an ObjectNode containing the core attributes or extended attributes.
   *
   * @param prefix The issue prefix.
   * @param parentPath The path of the parent node.
   * @param attributes The attribute definitions.
   * @param objectNode The ObjectNode to check.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @param isPartialReplace Whether this is a partial replace.
   * @param isPartialAdd Whether this is a partial add.
   * @param isReplace Whether this is a replace.
   * @throws ScimException If an error occurs.
   */
  private void checkObjectNode(
      final String prefix,
      final Path parentPath,
      final Collection<AttributeDefinition> attributes,
      final ObjectNode objectNode,
      final Results results,
      final ObjectNode currentObjectNode,
      final boolean isPartialReplace,
      final boolean isPartialAdd,
      final boolean isReplace) throws ScimException
  {
    if(attributes == null)
    {
      return;
    }

    for(AttributeDefinition attribute : attributes)
    {
      JsonNode node = objectNode.remove(attribute.getName());
      Path path = parentPath.attribute((attribute.getName()));

      if(node == null || node.isNull() || (node.isArray() && node.size() == 0))
      {
        // From SCIM's perspective, these are the same thing.
        if (!isPartialAdd && !isPartialReplace)
        {
          checkAttributeRequired(prefix, path, attribute, results);
        }
      }
      if(node != null)
      {
        // Additional checks for when the field is present
        checkAttributeMutability(prefix, node, path, attribute, results,
            currentObjectNode, isPartialReplace, isPartialAdd, isReplace);
        checkAttributeValues(prefix, node, path, attribute, results,
            currentObjectNode, isPartialReplace, isPartialAdd);
      }
    }

    // All defined attributes should be removed. Remove any additional
    // undefined attributes.
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      String undefinedAttribute = i.next().getKey();
      if(parentPath.size() == 0)
      {
        if(!enabledOptions.contains(Option.ALLOW_UNDEFINED_ATTRIBUTES))
        {
          results.syntaxIssues.add(prefix + "Core attribute " +
              undefinedAttribute + " is undefined for schema " +
              resourceType.getCoreSchema().getId());
        }
      }
      else if(parentPath.isRoot() &&
          parentPath.getSchemaUrn() != null)
      {
        if(!enabledOptions.contains(Option.ALLOW_UNDEFINED_ATTRIBUTES))
        {
          results.syntaxIssues.add(prefix + "Extended attribute " +
              undefinedAttribute + " is undefined for schema " +
              parentPath.getSchemaUrn());
        }
      }
      else
      {
        if(!enabledOptions.contains(Option.ALLOW_UNDEFINED_SUB_ATTRIBUTES))
        {
          results.syntaxIssues.add(prefix + "Sub-attribute " +
              undefinedAttribute + " is undefined for attribute " + parentPath);
        }
      }
      i.remove();
    }
  }
}
