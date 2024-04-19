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
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
  // attribute that should be updated. Note that this is an optional field for
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
      if(value.isArray())
      {
        throw new IllegalArgumentException("Patch operation contains " +
            "multiple values");
      }
      return JsonUtils.getObjectReader().treeToValue(
          value, cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public <T> List<T> getValues(@NotNull final Class<T> cls)
        throws JsonProcessingException, ScimException
    {
      ArrayList<T> objects = new ArrayList<T>(value.size());
      for(JsonNode node : value)
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
        validateAddOpWithFilter(path, value);
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
     * This method processes an add operation whose attribute path contains a
     * value selection filter. This operation takes the form of:
     * <pre>
     *   {
     *     "op": "add",
     *     "path": "addresses[type eq \"work\"].streetAddress",
     *     "value": "100 Tricky Ghost Avenue"
     *   }
     * </pre>
     *
     * When this patch operation is applied by a SCIM service provider, it
     * should result in both the {@code streetAddress} and the {@code type}
     * fields being appended to the {@code addresses} attribute.
     * <pre>
     *   "addresses": [
     *       {
     *         "streetAddress": "100 Tricky Ghost Avenue",
     *         "type": "work"
     *       }
     *   ]
     * </pre>
     *
     * While RFC 7644 does not dictate or describe this use case, this
     * convention is nevertheless used by some SCIM service providers to specify
     * additional data for a multi-valued parameter, such as a work address or a
     * home email.
     * <br><br>
     * Note that filters in attribute paths are treated differently for other
     * types of patch operations. For example, a {@code remove} operation with a
     * path of {@code addresses[type eq "work"]} would only delete address
     * values that contain a {@code "type": "work"} field. In other words, these
     * filters are normally used to modify a subset of multi-valued attributes,
     * but the use case for add operations is unique.
     *
     * @param path              The attribute path that contains a value filter.
     *                          This value filter will be added as part of the
     *                          new attribute value.
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
            throws BadRequestException
    {
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
        // There are no existing values for the attribute, so we should add this
        // value ourselves.
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

      // Construct the new attribute value that should be added to the resource.
      ObjectNode newValue = JsonUtils.getJsonNodeFactory().objectNode();
      newValue.set(subAttributeName, value);
      newValue.set(filterAttributeName, filterValue);

      attribute.add(newValue);
      existingResource.replace(attributeName, attribute);
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
      if (getPath() != null ? !getPath().equals(that.getPath()) :
          that.getPath() != null)
      {
        return false;
      }
      if (!value.equals(that.value))
      {
        return false;
      }

      return true;
    }

    /**
     * Retrieves a hash code for this add operation.
     *
     * @return  A hash code for this add operation.
     */
    @Override
    public int hashCode()
    {
      int result = getPath() != null ? getPath().hashCode() : 0;
      result = 31 * result + value.hashCode();
      return result;
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
      if(path == null)
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

      if (getPath() != null ? !getPath().equals(that.getPath()) :
          that.getPath() != null)
      {
        return false;
      }

      return true;
    }

    /**
     * Retrieves a hash code for this remove operation.
     *
     * @return  A hash code for this remove operation.
     */
    @Override
    public int hashCode()
    {
      return getPath() != null ? getPath().hashCode() : 0;
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
      if(value.isArray())
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
      ArrayList<T> objects = new ArrayList<T>(value.size());
      for(JsonNode node : value)
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

      if (getPath() != null ? !getPath().equals(that.getPath()) :
          that.getPath() != null)
      {
        return false;
      }
      if (!value.equals(that.value))
      {
        return false;
      }

      return true;
    }

    /**
     * Retrieves a hash code for this replace operation.
     *
     * @return  A hash code for this replace operation.
     */
    @Override
    public int hashCode()
    {
      int result = getPath() != null ? getPath().hashCode() : 0;
      result = 31 * result + value.hashCode();
      return result;
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
    if(path != null)
    {
      if(path.size() > 2)
      {
        throw BadRequestException.invalidPath(
            "Path cannot target sub-attributes more than one level deep");
      }

      if(path.size() == 2)
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
    if(schemasNode.isArray())
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
      else if(getPath().getSchemaUrn() != null)
      {
        addSchemaUrnIfMissing(schemas, getPath().getSchemaUrn());
      }
    }
  }

  private void addSchemaUrnIfMissing(@NotNull final ArrayNode schemas,
                                     @NotNull final String schemaUrn)
  {
    for(JsonNode node : schemas)
    {
      if(node.isTextual() && node.textValue().equalsIgnoreCase(schemaUrn))
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
    for(String value : values)
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
    for(Double value : values)
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
    for(Integer value : values)
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
    for(Long value : values)
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
    for(Date value : values)
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
    for(byte[] value : values)
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
    for(URI value : values)
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
    switch (opType)
    {
      case ADD:
        return add(path, value);
      case REPLACE:
        return replace(path, value);
      case REMOVE:
        return remove(path);
      default:
        throw new IllegalArgumentException("Unknown patch op type " + opType);
    }
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
            (value.isObject() && value.size() == 0))
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
}
