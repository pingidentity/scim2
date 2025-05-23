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

package com.unboundid.scim2.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.Path;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.exceptions.BadRequestException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.EqualFilter;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.filters.FilterType;
import com.unboundid.scim2.common.utils.FilterEvaluator;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * This class represents a SCIM 2 PATCH operation. A patch operation is a
 * component of a {@link PatchRequest}, and it represents an individual update
 * to a SCIM resource. A patch operation must be one of the following types:
 * <ul>
 *   <li> add
 *   <li> remove
 *   <li> replace
 * </ul>
 *
 * An {@code add} operation will add new attribute data. A {@code remove}
 * operation will delete the existing value(s) on an attribute. A
 * {@code replace} operation will overwrite any existing values of an attribute.
 * <br><br>
 *
 * To create an {@code add} operation, use methods of the following form:
 * <pre>
 *   PatchOperation.addIntegerValues(path, 512);
 *   PatchOperation.addStringValues(path, "Kingdom Tears");
 *   PatchOperation.add(path, jsonNodeValue);
 * </pre>
 *
 * To create a {@code remove} operation, use the following method:
 * <pre>
 *   PatchOperation.remove(path);
 * </pre>
 *
 * To create a {@code replace} operation, use methods of the following form:
 * <pre>
 *   PatchOperation.replace(path, 512);
 *   PatchOperation.replace(path, "Kingdom Tears");
 *   PatchOperation.replace(path, true);
 *   PatchOperation.replace(path, jsonNodeValue);
 * </pre>
 *
 * To create a patch operation in an alternative way, use the {@link #create}
 * static method. This method is useful if the operation type is not known at
 * compile time.
 * <pre>
 *   PatchOperation.create(operationType, path, jsonNodeValue);
 * </pre>
 *
 * Note that many of the helper methods for {@code add} and {@code replace}
 * operations do not accept a {@code null} path because they are intended for
 * targeting an attribute value. For example, to replace a user's email, the
 * following method may be used:
 * <pre>
 *   PatchOperation.replace("emails", "muhammad.ali@example.com")
 * </pre>
 * If a {@code null} path is needed for an {@code add} or {@code replace}
 * operation, then use the {@link #add(JsonNode)} and
 * {@link #replace(ObjectNode)} methods.
 *
 * @see PatchRequest
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "op")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PatchOperation.AddOperation.class,
        name="add", names= {"add", "Add", "ADD"}),
    @JsonSubTypes.Type(value = PatchOperation.RemoveOperation.class,
        name="remove", names= {"remove", "Remove", "REMOVE"}),
    @JsonSubTypes.Type(value = PatchOperation.ReplaceOperation.class,
        name="replace", names= {"replace", "Replace", "REPLACE"})})
public abstract class PatchOperation
{
  // The attribute path that is targeted by the patch operation (i.e., the
  // attribute that should be updated). Note that this is an optional field for
  // add and replace operations, but it is mandatory for remove operations.
  @Nullable
  private final Path path;


  static final class AddOperation extends PatchOperation
  {
    @NotNull
    @JsonProperty
    private final JsonNode value;

    /**
     * Create a new add patch operation.
     *
     * @param path The path targeted by this patch operation.
     * @param value The value(s) to add.
     * @throws ScimException If a value is not valid.
     */
    @JsonCreator
    private AddOperation(
        @Nullable @JsonProperty(value = "path")
        final Path path,
        @NotNull @JsonProperty(value = "value", required = true)
        final JsonNode value)
            throws ScimException
    {
      super(path);
      validateOperationValue(path, value, getOpType());
      this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public PatchOpType getOpType()
    {
      return PatchOpType.ADD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public JsonNode getJsonNode()
    {
      return value.deepCopy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public <T> T getValue(@NotNull final Class<T> cls)
        throws JsonProcessingException, ScimException, IllegalArgumentException
    {
      if (value.isArray())
      {
        throw new IllegalArgumentException("Patch operation contains " +
            "multiple values");
      }
      return JsonUtils.getObjectReader().treeToValue(value, cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public <T> List<T> getValues(@NotNull final Class<T> cls)
        throws JsonProcessingException, ScimException
    {
      ArrayList<T> objects = new ArrayList<>(value.size());
      for (JsonNode node : value)
      {
        objects.add(JsonUtils.getObjectReader().treeToValue(node, cls));
      }
      return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(@NotNull final ObjectNode node) throws ScimException
    {
      Path path = (getPath() == null) ? Path.root() : getPath();
      if (hasValueFilter(path))
      {
        applyAddWithValueFilter(path, node, value);
      }
      else
      {
        JsonUtils.addValue(path, node, value);
      }

      addMissingSchemaUrns(node);
    }

    /**
     * Indicates whether the provided attribute path has a value filter in the
     * first element.
     */
    private boolean hasValueFilter(@Nullable final Path path)
    {
      return path != null
          && path.size() > 0
          && path.getElement(0) != null
          && path.getElement(0).getValueFilter() != null;
    }

    /**
     * Validates an add operation with a value selection filter. For more
     * information, see {@link #applyAddWithValueFilter}.
     * <p>
     * This method imposes the following constraints:
     * <ul>
     *   <li> The value filter must be in the first element in the path.
     *   <li> The value filter must be an {@link EqualFilter}. Paths such as
     *        {@code addresses[type ne "home"]} are ambiguous and do not specify
     *        the value that should be assigned to the {@code type} field.
     *   <li> The attribute path must contain more than one element. In other
     *        words, it must be of the form
     *        {@code addresses[type eq "work"].streetAddress} and cannot be
     *        {@code addresses[type eq "work"]}.
     * </ul>
     *
     * @param path  The attribute path.
     * @param value The value that will be assigned to the sub-attribute in the
     *              path (e.g., {@code streetAddress}).
     *
     * @throws BadRequestException  If the value selection filter was in an
     *                              invalid form for the add operation.
     */
    private void validateAddOpWithFilter(@Nullable final Path path,
                                         @NotNull final JsonNode value)
        throws BadRequestException
    {
      Filter filter;
      if (path == null || !path.iterator().hasNext())
      {
        throw BadRequestException.invalidSyntax(
            "The patch add operation was expected to contain a non-empty path");
      }
      if (value.isArray())
      {
        throw BadRequestException.invalidSyntax(
            "Patch add operations with a filter cannot set the 'value' field to"
                + " an array");
      }

      Iterator<Path.Element> it = path.iterator();
      Path.Element firstElement = it.next();
      filter = firstElement.getValueFilter();

      FilterType filterType = (filter == null) ? null : filter.getFilterType();
      if (filterType == null)
      {
        throw BadRequestException.invalidPath(
            "The add operation contained an empty filter"
        );
      }
      if (filterType != FilterType.EQUAL)
      {
        throw BadRequestException.invalidPath(String.format(
            "The add operation contained a value selection filter of type '%s',"
                + " which is not an equality filter",
            filterType));
      }
      if (!it.hasNext())
      {
        // A path with a filter must have a second element to specify the
        // other value. It may not be 'emails[type eq "work"]', as that would
        // add only the 'type' field:
        // "emails": {
        //     "type": "work"
        // }
        //
        // Since the filter value would be unused, this request is invalid.
        throw BadRequestException.invalidPath(
            "A patch operation's attribute path was of the form 'attribute[filter]', but"
                + " needs to be 'attribute[filter].subAttribute'"
        );
      }

      // Ensure there are no other filters in the request.
      while (it.hasNext())
      {
        Path.Element element = it.next();
        if (element.getValueFilter() != null)
        {
          throw BadRequestException.invalidPath(String.format(
              "Patch add operations are only allowed to contain a single value"
                  + " selection filter in the top-level attribute name. '%s' is"
                  + " invalid.",
              element.getValueFilter()
          ));
        }
      }
    }

    /**
     * This method processes an add operation that aims to update a value within
     * an array. For example, the following patch operation attempts to update
     * a user's work address by adding a new street address value. Addresses
     * other than the work address will not be updated.
     * <pre>
     *   {
     *     "op": "add",
     *     "path": "addresses[type eq \"work\"].streetAddress",
     *     "value": "100 Tricky Ghost Avenue"
     *   }
     * </pre>
     *
     * By default, the SCIM SDK will look through the {@code addresses} on the
     * target resource and add the new value for any address that matches. For
     * example, consider an existing user resource that has:
     * <pre>
     *   "addresses": [
     *       {
     *         "formatted": "Formatted Ghost Avenue",
     *         "type": "work"
     *       },
     *       {
     *         "formatted": "Unrelated Address",
     *         "type": "home"
     *       }
     *   ]
     * </pre>
     *
     * If the above patch operation is applied to this resource, it will result
     * in:
     * <pre>
     *   "addresses": [
     *       {
     *         "formatted": "Formatted Ghost Avenue",
     *         "streetAddress": "100 Tricky Ghost Avenue",
     *         "type": "work"
     *       },
     *       {
     *         "formatted": "Unrelated Address",
     *         "type": "home"
     *       }
     *   ]
     * </pre>
     *
     * If the patch operation is applied to a resource that has no work address,
     * then a new value will be added to the multi-valued attribute.
     * <pre>
     *   "addresses": [
     *       {
     *         "streetAddress": "100 Tricky Ghost Avenue",
     *         "type": "work"
     *       }
     *    ]
     * </pre>
     *
     * @param path              The attribute path that contains a value filter.
     *                          This value filter will specify which values
     *                          within the array that should be updated.
     * @param existingResource  The most recent copy of the resource.
     * @param value             The new sub-attribute value that should be added
     *                          to the existing resource.
     *
     * @throws BadRequestException  If the operation targets an invalid
     *                              attribute.
     */
    private void applyAddWithValueFilter(
        @NotNull final Path path,
        @NotNull final ObjectNode existingResource,
        @NotNull final JsonNode value)
            throws ScimException
    {
      validateAddOpWithFilter(path, value);

      Filter valueFilter = path.getElement(0).getValueFilter();
      String filterAttributeName = valueFilter.getAttributePath().toString();
      ValueNode filterValue = valueFilter.getComparisonValue();

      // For an attribute path of the form 'emails[...].value', fetch the
      // attribute (emails) and the sub-attribute (value).
      String attributeName = path.getElement(0).getAttribute();
      String subAttributeName = path.getElement(1).getAttribute();

      JsonNode jsonAttribute = existingResource.get(attributeName);
      if (jsonAttribute == null)
      {
        // There are no existing values for the attribute, so we should
        // prepare to add this value ourselves.
        jsonAttribute = JsonUtils.getJsonNodeFactory().arrayNode(1);
      }
      if (!jsonAttribute.isArray())
      {
        throw BadRequestException.invalidSyntax(
            "The patch operation could not be processed because a complex"
                + " value selection filter was provided, but '" + attributeName
                + "' is single-valued"
        );
      }
      ArrayNode attribute = (ArrayNode) jsonAttribute;

      // When operations with a value filter add data, we can either append
      // the data to a new value in the multi-valued attribute, or we can update
      // an existing value.
      //
      // If the APPEND_NEW_PATCH_VALUES_PROPERTY property is enabled, the
      // provided data should be added as a new value regardless of the existing
      // resource's state (so we pretend that there are no matched values).
      // Otherwise, any new data should update the existing value, if it is
      // present.
      ObjectNode matchedValue = null;
      if (!APPEND_NEW_PATCH_VALUES_PROPERTY)
      {
        matchedValue = fetchExistingValue(attribute, valueFilter, attributeName);
      }

      // If there are no existing values that match the filter, or if no values
      // were fetched, then we should add the two attribute values to the array.
      // In the former case, this indicates that something like a home address
      // does not yet exist on the resource, so a new one should be added.
      if (matchedValue == null)
      {
        ObjectNode newValue = JsonUtils.getJsonNodeFactory().objectNode();
        newValue.set(subAttributeName, value);
        newValue.set(filterAttributeName, filterValue);

        attribute.add(newValue);
        existingResource.replace(attributeName, attribute);
        return;
      }

      // Ensure that the data to be added is not already populated on the
      // resource. For example, adding the "locality" sub-attribute to a home
      // address should not be allowed if the home address already has a
      // locality defined.
      if (FilterEvaluator.evaluate(Filter.pr(subAttributeName), matchedValue))
      {
        throw BadRequestException.invalidValue(String.format(
            "The add operation attempted to add a new '%s' field, but the"
                + " specified path already has a '%s' defined.",
            subAttributeName,
            subAttributeName
        ));
      }

      matchedValue.set(subAttributeName, value);
    }

    /**
     * Checks a multi-valued attribute for an existing value and returns a value
     * based on the following conditions:
     * <ul>
     *   <li> If a single existing value is present, it will be returned.
     *   <li> If no existing values are present, {@code null} will be returned.
     *   <li> If multiple existing values are present, a
     *        {@link BadRequestException} will be thrown, since it is unclear
     *        which value should be targeted.
     * </ul>
     *
     * @param attribute      The multi-valued attribute.
     * @param valueFilter    The value selection filter provided with the patch
     *                       add operation.
     * @param attributeName  The name of {@code attribute}.
     *
     * @return  An ObjectNode representing the single value that matched the
     *          criteria of the {@code valueFilter}, or {@code null} if no
     *          attributes matched the filter.
     *
     * @throws ScimException  If there was an error processing the filter, or
     *                        if multiple values were matched.
     */
    @Nullable
    private static ObjectNode fetchExistingValue(
        @NotNull final ArrayNode attribute,
        @NotNull final Filter valueFilter,
        @NotNull final String attributeName)
          throws ScimException
    {
      ObjectNode matchedValue = null;

      for (var arrayVal : attribute)
      {
        if (FilterEvaluator.evaluate(valueFilter, arrayVal))
        {
          if (matchedValue != null)
          {
            throw BadRequestException.noTarget(
                "The operation could not be applied on the resource because the"
                    + " value filter matched more than one element in the '"
                    + attributeName + "' array of the resource.");
          }
          matchedValue = (ObjectNode) arrayVal;
        }
      }

      return matchedValue;
    }

    /**
     * Indicates whether the provided object is equal to this add operation.
     *
     * @param o   The object to compare.
     * @return    {@code true} if the provided object is equal to this
     *            operation, or {@code false} if not.
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

      AddOperation that = (AddOperation) o;
      if (!Objects.equals(getPath(), that.getPath()))
      {
        return false;
      }
      return value.equals(that.value);
    }

    /**
     * Retrieves a hash code for this add operation.
     *
     * @return  A hash code for this add operation.
     */
    @Override
    public int hashCode()
    {
      return Objects.hash(getPath(), value);
    }

  }

  static final class RemoveOperation extends PatchOperation
  {
    /**
     * Create a new remove patch operation.
     *
     * @param path The path targeted by this patch operation.
     * @throws ScimException If a path is null.
     */
    @JsonCreator
    private RemoveOperation(
        @NotNull @JsonProperty(value = "path", required = true) final Path path)
            throws ScimException
    {
      super(path);
      if (path == null)
      {
        throw BadRequestException.noTarget(
            "path field must not be null for remove operations");
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public PatchOpType getOpType()
    {
      return PatchOpType.REMOVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(@NotNull final ObjectNode node) throws ScimException
    {
      JsonUtils.removeValues(getPath(), node);
    }

    /**
     * Indicates whether the provided object is equal to this remove operation.
     *
     * @param o   The object to compare.
     * @return    {@code true} if the provided object is equal to this
     *            operation, or {@code false} if not.
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

      RemoveOperation that = (RemoveOperation) o;
      return Objects.equals(getPath(), that.getPath());
    }

    /**
     * Retrieves a hash code for this remove operation.
     *
     * @return  A hash code for this remove operation.
     */
    @Override
    public int hashCode()
    {
      return Objects.hash(getPath());
    }
  }

  static final class ReplaceOperation extends PatchOperation
  {
    @NotNull
    @JsonProperty
    private final JsonNode value;

    /**
     * Create a new replace patch operation.
     *
     * @param path The path targeted by this patch operation.
     * @param value The value(s) to replace.
     * @throws ScimException If a value is not valid.
     */
    @JsonCreator
    private ReplaceOperation(
        @Nullable @JsonProperty(value = "path")
        final Path path,
        @NotNull @JsonProperty(value = "value", required = true)
        final JsonNode value)
            throws ScimException
    {
      super(path);
      validateOperationValue(path, value, getOpType());
      this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public PatchOpType getOpType()
    {
      return PatchOpType.REPLACE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public JsonNode getJsonNode()
    {
      return value.deepCopy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public <T> T getValue(@NotNull final Class<T> cls)
        throws JsonProcessingException, ScimException, IllegalArgumentException
    {
      if (value.isArray())
      {
        throw new IllegalArgumentException("Patch operation contains " +
            "multiple values");
      }
      return JsonUtils.getObjectReader().treeToValue(value, cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public <T> List<T> getValues(@NotNull final Class<T> cls)
        throws JsonProcessingException, ScimException
    {
      ArrayList<T> objects = new ArrayList<>(value.size());
      for (JsonNode node : value)
      {
        objects.add(JsonUtils.getObjectReader().treeToValue(node, cls));
      }
      return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(@NotNull final ObjectNode node) throws ScimException
    {
      Path path = (getPath() == null) ? Path.root() : getPath();
      JsonUtils.replaceValue(path, node, value);
      addMissingSchemaUrns(node);
    }

    /**
     * Indicates whether the provided object is equal to this replace operation.
     *
     * @param o   The object to compare.
     * @return    {@code true} if the provided object is equal to this
     *            operation, or {@code false} if not.
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

      ReplaceOperation that = (ReplaceOperation) o;
      if (!Objects.equals(getPath(), that.getPath()))
      {
        return false;
      }
      return value.equals(that.value);
    }

    /**
     * Retrieves a hash code for this replace operation.
     *
     * @return  A hash code for this replace operation.
     */
    @Override
    public int hashCode()
    {
      return Objects.hash(getPath(), value);
    }
  }



  /**
   * Create a new patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @throws ScimException If a value is not valid.
   */
  PatchOperation(@Nullable final Path path) throws ScimException
  {
    if (path != null)
    {
      if (path.size() > 2)
      {
        throw BadRequestException.invalidPath(
            "Path cannot target sub-attributes more than one level deep");
      }

      if (path.size() == 2)
      {
        Filter valueFilter = path.getElement(1).getValueFilter();
        // Allow use of the special case "value" path to reference the value itself.
        // Any other value filter is for a sub-attribute, which is not permitted.
        if (valueFilter != null &&
            !valueFilter.getAttributePath().getElement(0).getAttribute().equals("value"))
        {
          throw BadRequestException.invalidPath(
              "Path cannot include a value filter on sub-attributes");
        }
      }
    }
    this.path = path;
  }

  /**
   * Retrieves the operation type.
   *
   * @return The operation type.
   */
  @JsonIgnore
  @NotNull
  public abstract PatchOpType getOpType();

  /**
   * Retrieves the path targeted by this operation.
   *
   * @return The path targeted by this operation.
   */
  @Nullable
  public Path getPath()
  {
    return path;
  }

  /**
   * Retrieve the value or values of the patch operation as a JsonNode. The
   * returned JsonNode is a copy so it may be altered without altering this
   * operation.
   *
   * @return  The value or values of the patch operation, or {@code null}
   *          if this operation is a remove operation.
   */
  @Nullable
  @JsonIgnore
  public JsonNode getJsonNode()
  {
    return null;
  }

  /**
   * Retrieve the value of the patch operation.
   *
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The generic type parameter of the Java class used to determine
   *            the type to return.
   * @return The value of the patch operation.
   * @throws JsonProcessingException If the value can not be parsed to the
   *         type specified by the Java class object.
   * @throws ScimException If the path is invalid.
   * @throws IllegalArgumentException If the operation contains more than one
   *         value, in which case, the getValues method should be used to
   *         retrieve all values.
   */
  @Nullable
  public <T> T getValue(@NotNull final Class<T> cls)
      throws JsonProcessingException, ScimException, IllegalArgumentException
  {
    return null;
  }

  /**
   * Retrieve all values of the patch operation.
   *
   * @param cls The Java class object used to determine the type to return.
   * @param <T> The generic type parameter of the Java class used to determine
   *            the type to return.
   * @return The values of the patch operation.
   * @throws JsonProcessingException If the value can not be parsed to the
   *         type specified by the Java class object.
   * @throws ScimException If the path is invalid.
   */
  @Nullable
  public <T> List<T> getValues(@NotNull final Class<T> cls)
      throws JsonProcessingException, ScimException
  {
    return null;
  }

  /**
   * Apply this patch operation to an ObjectNode.
   *
   * @param node The ObjectNode to apply this patch operation to.
   *
   * @throws ScimException If the patch operation is invalid.
   */
  public abstract void apply(@NotNull final ObjectNode node)
      throws ScimException;

  /**
   * Retrieves a string representation of this patch operation.
   *
   * @return  A string representation of this patch operation.
   */
  @Override
  @NotNull
  public String toString()
  {
    try
    {
      return JsonUtils.getObjectWriter().withDefaultPrettyPrinter().
          writeValueAsString(this);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Implicitly add any schema URNs of any extended attributes that are missing
   * from the schemas attribute.
   *
   * @param node The ObjectNode to apply this patch operation to.
   */
  protected void addMissingSchemaUrns(@NotNull final ObjectNode node)
  {
    // Implicitly add the schema URN of any extended attributes to the
    // schemas attribute.
    JsonNode schemasNode =
        node.path(SchemaUtils.SCHEMAS_ATTRIBUTE_DEFINITION.getName());
    if (schemasNode.isArray())
    {
      ArrayNode schemas = (ArrayNode) schemasNode;
      if (getPath() == null)
      {
        Iterator<String> i = getJsonNode().fieldNames();
        while (i.hasNext())
        {
          String field = i.next();
          if (SchemaUtils.isUrn(field))
          {
            addSchemaUrnIfMissing(schemas, field);
          }
        }
      }
      else if (getPath().getSchemaUrn() != null)
      {
        addSchemaUrnIfMissing(schemas, getPath().getSchemaUrn());
      }
    }
  }

  private void addSchemaUrnIfMissing(@NotNull final ArrayNode schemas,
                                     @NotNull final String schemaUrn)
  {
    for (JsonNode node : schemas)
    {
      if (node.isTextual() && node.textValue().equalsIgnoreCase(schemaUrn))
      {
        return;
      }
    }

    schemas.add(schemaUrn);
  }

  /**
   * Create a new add patch operation.
   *
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation add(@NotNull final JsonNode value)
  {
    return add((Path) null, value);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation add(@Nullable final String path,
                                   @NotNull final JsonNode value)
      throws ScimException
  {
    return add(Path.fromString(path), value);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation add(@Nullable final Path path,
                                   @NotNull final JsonNode value)
  {
    try
    {
      return new AddOperation(path, value);
    }
    catch (ScimException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addStringValues(
      @NotNull final String path,
      @NotNull final List<String> values)
          throws ScimException
  {
    return addStringValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addStringValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addStringValues(@NotNull final String path,
                                               @NotNull final String value1,
                                               @Nullable final String... values)
      throws ScimException
  {
    return addStringValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation addStringValues(
      @NotNull final Path path,
      @NotNull final List<String> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (String value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addStringValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   */
  @NotNull
  public static PatchOperation addStringValues(@Nullable final Path path,
                                               @NotNull final String value1,
                                               @Nullable final String... values)
  {
    return addStringValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final String value)
      throws ScimException
  {
    return replace(path, TextNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation. The {@code path} must not be
   * {@code null} since this method is used to target an attribute on a resource
   * (as opposed to targeting the resource itself). Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final String value)
  {
    return replace(path, TextNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final Boolean value)
      throws ScimException
  {
    return replace(path, BooleanNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final Boolean value)
  {
    return replace(path, BooleanNode.valueOf(value));
  }


  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addDoubleValues(
      @NotNull final String path,
      @NotNull final List<Double> values)
          throws ScimException
  {
    return addDoubleValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addDoubleValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addDoubleValues(@NotNull final String path,
                                               @NotNull final Double value1,
                                               @Nullable final Double... values)
      throws ScimException
  {
    return addDoubleValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation addDoubleValues(
      @NotNull final Path path,
      @NotNull final List<Double> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Double value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addDoubleValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   */
  @NotNull
  public static PatchOperation addDoubleValues(@NotNull final Path path,
                                               @NotNull final Double value1,
                                               @Nullable final Double... values)
  {
    return addDoubleValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final Double value)
      throws ScimException
  {
    return replace(path, DoubleNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final Double value)
  {
    return replace(path, DoubleNode.valueOf(value));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addIntegerValues(
      @NotNull final String path,
      @NotNull final List<Integer> values)
          throws ScimException
  {
    return addIntegerValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addIntegerValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addIntegerValues(
      @NotNull final String path,
      @NotNull final Integer value1,
      @Nullable final Integer... values)
          throws ScimException
  {
    return addIntegerValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation addIntegerValues(
      @NotNull final Path path,
      @NotNull final List<Integer> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Integer value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addIntegerValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   */
  @NotNull
  public static PatchOperation addIntegerValues(
      @NotNull final Path path,
      @NotNull final Integer value1,
      @Nullable final Integer... values)
  {
    return addIntegerValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final Integer value)
      throws ScimException
  {
    return replace(path, IntNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final Integer value)
  {
    return replace(path, IntNode.valueOf(value));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addLongValues(@NotNull final String path,
                                             @NotNull final List<Long> values)
      throws ScimException
  {
    return addLongValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addLongValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addLongValues(@NotNull final String path,
                                             @NotNull final Long value1,
                                             @Nullable final Long... values)
      throws ScimException
  {
    return addLongValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation addLongValues(@NotNull final Path path,
                                             @NotNull final List<Long> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Long value : values)
    {
      arrayNode.add(value);
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addLongValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   */
  @NotNull
  public static PatchOperation addLongValues(@NotNull final Path path,
                                             @NotNull final Long value1,
                                             @Nullable final Long... values)
  {
    return addLongValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final Long value)
      throws ScimException
  {
    return replace(path, LongNode.valueOf(value));
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final Long value)
  {
    return replace(path, LongNode.valueOf(value));
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addDateValues(@NotNull final String path,
                                             @NotNull final List<Date> values)
      throws ScimException
  {
    return addDateValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addDateValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addDateValues(@NotNull final String path,
                                             @NotNull final Date value1,
                                             @Nullable final Date... values)
      throws ScimException
  {
    return addDateValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public static PatchOperation addDateValues(@NotNull final Path path,
                                             @NotNull final List<Date> values)
      throws ScimException
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (Date value : values)
    {
      arrayNode.add(GenericScimResource.getDateJsonNode(value).textValue());
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addDateValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addDateValues(@NotNull final Path path,
                                             @NotNull final Date value1,
                                             @Nullable final Date... values)
      throws ScimException
  {
    return addDateValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final Date value)
      throws ScimException
  {
    String valueString =
        GenericScimResource.getDateJsonNode(value).textValue();
    return replace(path, valueString);
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException if an error occurs.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final Date value)
      throws ScimException
  {
    String valueString =
        GenericScimResource.getDateJsonNode(value).textValue();
    return replace(path, valueString);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The value(s) to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addBinaryValues(
      @NotNull final String path,
      @NotNull final List<byte[]> values)
          throws ScimException
  {
    return addBinaryValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addBinaryValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addBinaryValues(@NotNull final String path,
                                               @NotNull final byte[] value1,
                                               @Nullable final byte[]... values)
      throws ScimException
  {
    return addBinaryValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation addBinaryValues(
      @NotNull final Path path,
      @NotNull final List<byte[]> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (byte[] value : values)
    {
      arrayNode.add(Base64Variants.getDefaultVariant().encode(value));
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addBinaryValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   */
  @NotNull
  public static PatchOperation addBinaryValues(@NotNull final Path path,
                                               @NotNull final byte[] value1,
                                               @Nullable final byte[]... values)
  {
    return addBinaryValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final byte[] value)
      throws ScimException
  {
    String valueString = Base64Variants.getDefaultVariant().encode(value);
    return replace(path, valueString);
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final byte[] value)
  {
    String valueString = Base64Variants.getDefaultVariant().encode(value);
    return replace(path, valueString);
  }

  /**
   * Create a new add patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation addURIValues(@NotNull final String path,
                                            @NotNull final List<URI> values)
      throws ScimException
  {
    return addURIValues(Path.fromString(path), values);
  }

  /**
   * Alternate version of {@link #addURIValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   *
   * @throws ScimException  If the path is invalid.
   */
  @NotNull
  public static PatchOperation addURIValues(@NotNull final String path,
                                            @NotNull final URI value1,
                                            @Nullable final URI... values)
      throws ScimException
  {
    return addURIValues(path, toList(value1, values));
  }

  /**
   * Create a new add patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   *
   * @param path The path targeted by this patch operation.
   * @param values The values to add.
   *
   * @return The new add patch operation.
   */
  @NotNull
  public static PatchOperation addURIValues(@NotNull final Path path,
                                            @NotNull final List<URI> values)
  {
    ArrayNode arrayNode = JsonUtils.getJsonNodeFactory().arrayNode();
    for (URI value : values)
    {
      arrayNode.add(value.toString());
    }
    return add(path, arrayNode);
  }

  /**
   * Alternate version of {@link #addURIValues(String, List)}.
   *
   * @param path    The attribute path targeted by this patch operation.
   * @param value1  The first value.
   * @param values  An optional field for additional values. Any {@code null}
   *                values will be ignored.
   * @return        A new PatchOperation with an opType of {@code "add"}.
   */
  @NotNull
  public static PatchOperation addURIValues(@NotNull final Path path,
                                            @NotNull final URI value1,
                                            @Nullable final URI... values)
  {
    return addURIValues(path, toList(value1, values));
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final URI value)
      throws ScimException
  {
    return replace(path, value.toString());
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final URI value)
  {
    return replace(path, value.toString());
  }

  /**
   * Create a new replace patch operation.
   *
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final ObjectNode value)
  {
    return replace((Path) null, value);
  }

  /**
   * Create a new replace patch operation.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final String path,
                                       @NotNull final JsonNode value)
      throws ScimException
  {
    return replace(Path.fromString(path), value);
  }

  /**
   * Create a new replace patch operation. Example paths include:
   * <ul>
   *   <li> {@code userName}
   *   <li> {@code emails[type eq "work"]}
   * </ul>
   *
   * If a {@code null} path is desired, use the {@link #replace(ObjectNode)}
   * method, as this is more concise and will also ensure that the JSON value
   * is an ObjectNode.
   *
   * @param path The path targeted by this patch operation.
   * @param value The value(s) to replace.
   *
   * @return The new replace patch operation.
   */
  @NotNull
  public static PatchOperation replace(@NotNull final Path path,
                                       @NotNull final JsonNode value)
  {
    try
    {
      return new ReplaceOperation(path, value);
    }
    catch (ScimException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Create a new remove patch operation.
   *
   * @param path The path targeted by this patch operation.
   *
   * @return The new delete patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation remove(@NotNull final String path)
      throws ScimException
  {
    return remove(Path.fromString(path));
  }

  /**
   * Create a new remove patch operation.
   *
   * @param path The path targeted by this patch operation.
   *
   * @return The new delete patch operation.
   */
  @NotNull
  public static PatchOperation remove(@NotNull final Path path)
  {
    try
    {
      return new RemoveOperation(path);
    }
    catch (ScimException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Create a new patch operation based on the parameters provided.
   *
   * @param opType The operation type.
   * @param path   The path targeted by this patch operation. This may not be
   *               {@code null}. If a {@code null} path is desired, see the
   *               class-level Javadoc for more details.
   * @param value  The value(s). This field will be ignored for {@code remove}
   *               operations.
   *
   * @return The new patch operation.
   * @throws ScimException If the path is invalid.
   */
  @NotNull
  public static PatchOperation create(@NotNull final PatchOpType opType,
                                      @NotNull final String path,
                                      @NotNull final JsonNode value)
      throws ScimException
  {
    return create(opType, Path.fromString(path), value);
  }

  /**
   * Create a new patch operation based on the parameters provided.
   *
   * @param opType The operation type.
   * @param path   The path targeted by this patch operation. This may not be
   *               {@code null}. If a {@code null} path is desired, see the
   *               class-level Javadoc for more details.
   * @param value  The value(s). This field will be ignored for {@code remove}
   *               operations.
   *
   * @return The new patch operation.
   */
  @NotNull
  public static PatchOperation create(@NotNull final PatchOpType opType,
                                      @NotNull final Path path,
                                      @NotNull final JsonNode value)
  {
    return switch (opType)
    {
      case ADD -> add(path, value);
      case REPLACE -> replace(path, value);
      case REMOVE -> remove(path);
    };
  }


  /**
   * Validates the {@code value} of a patch operation when the operation is
   * constructed.
   *
   * @param path    The attribute path.
   * @param value   The node containing the attribute value that will be
   *                analyzed.
   * @param type    The type of patch operation.
   *
   * @throws ScimException  If the provided value is {@code null} or invalid.
   */
  private static void validateOperationValue(@Nullable final Path path,
                                             @Nullable final JsonNode value,
                                             @NotNull final PatchOpType type)
      throws ScimException
  {
    if (value == null || value.isNull() ||
            (value.isObject() && value.isEmpty()))
    {
      throw BadRequestException.invalidSyntax(
          "The patch operation value must not be null or an empty object");
    }

    if (path == null && !value.isObject())
    {
      throw BadRequestException.invalidSyntax(
          "value field must be a JSON object containing the"
                  + " attributes to " + type);
    }
  }

  /**
   * This field represents a property that can customize behavior when
   * processing {@code add} PATCH operations with a value filter. This is used
   * for multi-valued attributes such as {@code emails}, and is not used for
   * {@code remove} or {@code replace} operations. For example, consider the
   * following patch request:
   * <pre>
   *   {
   *     "schemas": [ "urn:ietf:params:scim:api:messages:2.0:PatchOp" ],
   *     "Operations": [
   *       {
   *         "op": "add",
   *         "path": "emails[type eq \"work\"].display",
   *         "value": "apollo.j@example.com"
   *       }
   *     ]
   *   }
   * </pre>
   *
   * When this property is enabled and the above patch request is applied, the
   * following JSON will be appended to the {@code emails} of the user
   * resource, regardless of the current contents of the user:
   * <pre>
   *   {
   *     "type": "work",
   *     "display": "apollo.j@example.com"
   *   }
   * </pre>
   * Thus, enabling this property for PATCH requests has the potential to result
   * in multiple emails containing a {@code type} of {@code work}.
   * <br><br>
   *
   * If this property is <em>disabled</em>, then in the above example, the
   * {@code display} field will be added to the resource's existing work email,
   * if it exists. If the work email does not exist, then a new value will be
   * appended. If there is already more than one existing work email on the
   * resource, a {@link BadRequestException} will be thrown.
   *
   * @since 3.2.0
   */
  public static boolean APPEND_NEW_PATCH_VALUES_PROPERTY = false;
}
