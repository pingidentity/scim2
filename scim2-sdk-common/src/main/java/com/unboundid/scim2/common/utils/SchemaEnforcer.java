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

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.types.SchemaResource;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.filters.FilterEvaluator;
import com.unboundid.scim2.common.messages.PatchOperation;

import java.net.URI;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Utility class used to validate and enforce schema constraints on
 * SCIM resources.
 */
public class SchemaEnforcer
{
  private final SchemaResource coreSchema;
  private final Map<SchemaResource, Boolean> schemaExtensions;
  private final Collection<AttributeDefinition> commonAndCoreAttributes;

  /**
   * Schema checking results.
   */
  public static class Results
  {
    private final List<String> syntaxIssues = new LinkedList<String>();
    private final List<String> mutabilityIssues = new LinkedList<String>();
    private final List<String> pathIssues = new LinkedList<String>();

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
  }

  /**
   * Create a new instance that may be used to validate and enforce schema
   * constraints for a resource type using the provided core schema and
   * schema extensions.
   *
   * @param coreSchema The core schema for the resource type.
   * @param schemaExtensions A map of schema extensions to whether it is
   *                         required for the resource type.
   */
  public SchemaEnforcer(final SchemaResource coreSchema,
                        final Map<SchemaResource, Boolean> schemaExtensions)
  {
    this.coreSchema = coreSchema;
    this.schemaExtensions = schemaExtensions;

    commonAndCoreAttributes = new ArrayList<AttributeDefinition>(
        coreSchema.getAttributes().size() + 4);
    commonAndCoreAttributes.addAll(Arrays.asList(
        SchemaUtils.SCHEMAS_ATTRIBUTE_DEFINITION,
        SchemaUtils.ID_ATTRIBUTE_DEFINITION,
        SchemaUtils.EXTERNAL_ID_ATTRIBUTE_DEFINITION,
        SchemaUtils.META_ATTRIBUTE_DEFINITION));
    commonAndCoreAttributes.addAll(coreSchema.getAttributes());
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
   * @param objectNode The SCIM resource that will be created.
   * @return Schema checking results.
   * @throws ScimException If an error occured while checking the schema.
   */
  public Results checkCreate(final ObjectNode objectNode) throws ScimException
  {
    ObjectNode copyNode = objectNode.deepCopy();
    Results results = new Results();
    checkResource("", copyNode, results, null);
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
   *                          {@code null} if not available.
   * @return Schema checking results.
   * @throws ScimException If an error occured while checking the schema.
   */
  public Results checkModify(final Iterable<PatchOperation> patchOperations,
                             final ObjectNode currentObjectNode)
      throws ScimException
  {
    ObjectNode copyCurrentNode =
        currentObjectNode == null ? null : currentObjectNode.deepCopy();
    ObjectNode appliedNode =
        currentObjectNode == null ? null : currentObjectNode.deepCopy();
    Results results = new Results();

    int i = 0;
    String prefix;
    for(PatchOperation patchOp : patchOperations)
    {
      prefix = "Patch op[" + i + "]: ";
      Path path = patchOp.getPath();
      JsonNode value = patchOp.getJsonNode();
      AttributeDefinition attribute =
          path == null ? null : getAttributeDefinition(prefix, path, results);
      Filter valueFilter =
          path == null ? null :
              path.getElement(path.size() - 1).getValueFilter();
      if(path != null && attribute == null)
      {
        // Can't find the attirbute defintion for attribute in path.
        continue;
      }
      switch (patchOp.getOpType())
      {
        case REMOVE:
          if(attribute == null)
          {
            continue;
          }
          checkAttributeMutability(prefix, null, path, attribute, results,
              currentObjectNode, false, false);
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
                currentObjectNode, true, false);
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
                currentObjectNode, false, true);
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
          appliedNode, results, copyCurrentNode);
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
   * @throws ScimException If an error occured while checking the schema.
   */
  public Results checkReplace(final ObjectNode replacementObjectNode,
                              final ObjectNode currentObjectNode)
      throws ScimException
  {
    ObjectNode copyReplacementNode = replacementObjectNode.deepCopy();
    ObjectNode copyCurrentNode =
        currentObjectNode == null ? null : currentObjectNode.deepCopy();
    Results results = new Results();
    checkResource("", copyReplacementNode, results, copyCurrentNode);
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
   * @return The new SCIM resource with the read-only attributes (if any)
   *         removed.
   */
  public ObjectNode removeReadOnlyAttributes(final ObjectNode objectNode)
  {
    ObjectNode copyNode = objectNode.deepCopy();
    for(SchemaResource schemaExtension : schemaExtensions.keySet())
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
   * Retrieve the attribute definition for the attribute in the path.
   *
   * @param prefix The issue prefix.
   * @param path The attribute path.
   * @param results The schema check results.
   * @return The attribute definition for {@code null}.
   */
  private AttributeDefinition getAttributeDefinition(final String prefix,
                                                     final Path path,
                                                     final Results results)
  {
    int elementIndex = 0;
    Iterable<AttributeDefinition> attributes =
        commonAndCoreAttributes;
    if(path.getExtensionSchema() != null)
    {
      elementIndex = 1;
      boolean found = false;
      for(SchemaResource schema : schemaExtensions.keySet())
      {
        if(schema.getId().equals(path.getExtensionSchema()))
        {
          attributes = schema.getAttributes();
          found = true;
          break;
        }
      }
      if(!found)
      {
        results.pathIssues.add(prefix + "Schema extension " +
            path.getExtensionSchema() +" in path is undefined for this " +
            "resource type");
      }
    }

    for(; elementIndex < path.size(); elementIndex++)
    {
      for(AttributeDefinition attribute : attributes)
      {
        if(attribute.getName().equals(
            path.getElement(elementIndex).getAttribute()))
        {
          if(elementIndex >= path.size() - 1)
          {
            return attribute;
          }
          attributes = attribute.getSubAttributes();
        }
      }
      if(attributes == null)
      {
        break;
      }
    }

    if(path.getExtensionSchema() == null && elementIndex == 1)
    {
      results.pathIssues.add(prefix + "Core attribute " +
          path.getElement(elementIndex - 1).getAttribute() + " in path is " +
          "undefined for schema " + coreSchema.getId());
    }
    else if(path.getExtensionSchema() != null && elementIndex == 2)
    {
      results.pathIssues.add(prefix + "Extended attribute " +
          path.getElement(elementIndex - 1).getAttribute() + " in path is " +
          "undefined for schema " +
          path.getExtensionSchema());
    }
    else
    {
      results.pathIssues.add(prefix + "Sub-attribute " +
          path.getElement(elementIndex - 1).getAttribute() + " in path is " +
          "undefined for attribute " + path.parent());
    }
    return null;
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
          for (SchemaResource schemaExtension : schemaExtensions.keySet())
          {
            if (schemaExtension.getId().equals(field.getKey()))
            {
              checkObjectNode(prefix, Path.root(field.getKey()),
                  schemaExtension.getAttributes(),
                  (ObjectNode) field.getValue(), results, currentObjectNode,
                  isPartialReplace, isPartialAdd);
              found = true;
              break;
            }
          }
          if(!found)
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
        isPartialReplace, isPartialAdd);
  }

  /**
   * Internal method to check a SCIM resource.
   *
   * @param prefix The issue prefix.
   * @param objectNode The partial resource.
   * @param results The schema check results.
   * @param currentObjectNode The current resource.
   * @throws ScimException If an error occurs.
   */
  private void checkResource(final String prefix,
                             final ObjectNode objectNode,
                             final Results results,
                             final ObjectNode currentObjectNode)
      throws ScimException
  {
    // Iterate through the schemas
    JsonNode schemas = objectNode.get("schemas");
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
          extensionNode =
              SchemaUtils.createSCIMCompatibleMapper().createObjectNode();
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
        if (schema.textValue().equals(coreSchema.getId()))
        {
          // Skip the core schema.
          coreFound = true;
          continue;
        } else
        {
          for (Map.Entry<SchemaResource, Boolean> schemaExtension :
              schemaExtensions.entrySet())
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
            false, false);
      }

      if (!coreFound)
      {
        // Make sure core schemas was included.
        results.syntaxIssues.add(prefix + "Value for attribute schemas must " +
            " contain schema URI " + coreSchema.getId() +
            " because it is the core schema for this resource type");
      }

      // Make sure all required extension schemas were included.
      for (Map.Entry<SchemaResource, Boolean> schemaExtension :
          schemaExtensions.entrySet())
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
    // Remove any additional undefined schema extensions.
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      String fieldName = i.next().getKey();
      if(SchemaUtils.isUrn(fieldName))
      {
        results.syntaxIssues.add(prefix + "Undefined extended attributes " +
            "namespace " + fieldName);
        i.remove();
      }
    }

    // Check common and core schema
    checkObjectNode(prefix, Path.root(), commonAndCoreAttributes,
        objectNode, results, currentObjectNode,
        false, false);
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
   * @throws ScimException If an error occurs.
   */
  private void checkAttributeMutability(final String prefix,
                                        final JsonNode node,
                                        final Path path,
                                        final AttributeDefinition attribute,
                                        final Results results,
                                        final ObjectNode currentObjectNode,
                                        final boolean isPartialReplace,
                                        final boolean isPartialAdd)
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
      if(isPartialReplace)
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
            JsonUtils.getValues(path, currentObjectNode);
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
          TextNode.valueOf(coreSchema.getId())))
      {
        results.syntaxIssues.add(prefix + "Attribute value(s) " + path +
            " may not be removed or replaced because the core schema " +
            coreSchema.getId() + " is required for this resource type");
      }
      for (Map.Entry<SchemaResource, Boolean> schemaExtension :
          schemaExtensions.entrySet())
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
        Path parentPath = path.parent();
        if(parentPath == null)
        {
          throw new NullPointerException(
              "Path should always point to an attribute");
        }
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
      case BINARY:
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
          ISO8601Utils.parse(node.textValue(), new ParsePosition(0));
        }
        catch (Exception e)
        {
          Debug.debug(Level.INFO, DebugType.EXCEPTION,
              "Invalid ISO8601 string during schema checking", e);
          results.syntaxIssues.add(prefix + "Value for attribute " + path +
              " is not a valid ISO8601 formatted string");
        }
        break;
      case BINARY:
        try
        {
          Base64Variants.getDefaultVariant().decode(node.textValue());
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
            isPartialReplace, isPartialAdd);
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
                canonicalValue.equalsIgnoreCase(node.textValue()))
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
      for (SchemaResource schemaExtension : schemaExtensions.keySet())
      {
        if (node.textValue().equals(schemaExtension.getId()))
        {
          found = true;
          break;
        }
      }
      if(!found)
      {
        found = node.textValue().equals(coreSchema.getId());
      }
      if(!found)
      {
        results.syntaxIssues.add(prefix + "Value " + node.textValue() +
            " is not valid for attribute " + path + " because it is " +
            "an undefined schema for this resource type");
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
      final boolean isPartialAdd) throws ScimException
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
            currentObjectNode, isPartialReplace, isPartialAdd);
        checkAttributeValues(prefix, node, path, attribute, results,
            currentObjectNode, isPartialReplace, isPartialAdd);
      }
    }

    // All defined attributes should be removed. Remove any additional
    // undefined attributes.
    Iterator<Map.Entry<String, JsonNode>> i = objectNode.fields();
    while(i.hasNext())
    {
      if(parentPath.size() == 0)
      {
        results.syntaxIssues.add(prefix + "Core attribute " +
            i.next().getKey() + " is undefined for schema " +
            coreSchema.getId());
      }
      else if(parentPath.isRoot() &&
          parentPath.getExtensionSchema() != null)
      {
        results.syntaxIssues.add(prefix + "Extended attribute " +
            i.next().getKey() + " is undefined for schema " +
            parentPath.getExtensionSchema());
      }
      else
      {
        results.syntaxIssues.add(prefix + "Sub-attribute " + i.next().getKey() +
            " is undefined for attribute " + parentPath);
      }
      i.remove();
    }
  }
}
