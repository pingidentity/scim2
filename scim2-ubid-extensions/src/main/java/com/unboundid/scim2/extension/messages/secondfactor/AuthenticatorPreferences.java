/*
 * Copyright 2016 UnboundID Corp.
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

package com.unboundid.scim2.extension.messages.secondfactor;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Attribute;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.common.utils.SchemaUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Authenticator preferences information.
 */
public final class AuthenticatorPreferences extends BaseScimResource
{
  @Attribute(description = "The type of the authenticator.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private final String type;

  @Attribute(description = "Flow state token",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final String flowState;

  @Attribute(description = "Whether the authenticator is ready to " +
      "authenticate the user",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private final Boolean ready;

  private final ObjectNode authenticatorParameters;

  /**
   * Create a new AuthenticatorPreferences.
   *
   * @param name The name of the authenticator.
   * @param type The type of the authenticator.
   * @param flowState The flow state token.
   * @param ready Whether the authenticator is ready to authenticate the user.
   */
  @JsonCreator
  public AuthenticatorPreferences(
      @JsonProperty(value = "name") final String name,
      @JsonProperty(value = "type") final String type,
      @JsonProperty(value = "flowState") final String flowState,
      @JsonProperty(value = "ready") final Boolean ready)
  {
    super(name);
    this.type = type;
    this.flowState = flowState;
    this.authenticatorParameters = JsonUtils.getJsonNodeFactory().objectNode();
    this.ready = ready;
  }

  /**
   * Create a new AuthenticatorPreferences.
   *
   * @param flowState The flow state token.
   */
  public AuthenticatorPreferences(final String flowState)
  {
    super();
    this.type = null;
    this.flowState = flowState;
    this.authenticatorParameters = JsonUtils.getJsonNodeFactory().objectNode();
    this.ready = null;
  }

  /**
   * Retrieves the type of this authenticator.
   *
   * @return The type of this authenticator.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Retrieves the flow state token.
   *
   * @return The flow state token.
   */
  public String getFlowState()
  {
    return flowState;
  }

  /**
   * Retrieves whether the authenticator is ready to authenticate the user.
   *
   * @return Whether the authenticator is ready to authenticate the user.
   */
  public Boolean getReady()
  {
    return ready;
  }

  /**
   * Sets an authenticator parameter.
   *
   * @param parameterName The name of the authenticator parameter.
   * @param value The value fot he authenticator parameter.
   * @return This object.
   */
  @JsonIgnore
  public AuthenticatorPreferences setAuthenticatorParameter(
      final String parameterName, final JsonNode value)
  {
    authenticatorParameters.set(parameterName, value);
    return this;
  }

  /**
   * Sets authenticator parameters.
   *
   * @param parameters The ObjectNode containing authenticator parameters
   *                   to set.
   * @return This object.
   */
  @JsonIgnore
  public AuthenticatorPreferences setAuthenticatorParameters(
      final ObjectNode parameters)
  {
    authenticatorParameters.setAll(parameters);
    return this;
  }

  /**
   * Retrieves an authenticator parameter.
   *
   * @param parameterName The name of the authenticator parameter.
   * @return The JsonNode associated with the parameter or {@code null} if
   *         the parameter is not found.
   */
  @JsonIgnore
  public JsonNode getAuthenticatorParameter(final String parameterName)
  {
    return authenticatorParameters.get(parameterName);
  }

  /**
   * Retrieves all authenticator parameters.
   *
   * @return The ObjectNode containing all authenticator parameters.
   */
  @JsonIgnore
  public ObjectNode getAuthenticatorParameters()
  {
    return authenticatorParameters.deepCopy();
  }

  /**
   * {@inheritDoc}
   */
  @JsonAnySetter
  protected void setAny(final String key,
                        final JsonNode value)
      throws ScimException
  {
    if(SchemaUtils.isUrn(key) && value.isObject())
     {
       getExtensionObjectNode().set(key, value);
     }
     else
     {
       authenticatorParameters.set(key, value);
     }
  }

  /**
   * {@inheritDoc}
   */
  @JsonAnyGetter
  protected Map<String, Object> getAny()
  {
    HashMap<String, Object> map = new HashMap<String, Object>(
        authenticatorParameters.size() + getExtensionObjectNode().size());
    Iterator<Map.Entry<String, JsonNode>> i = authenticatorParameters.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      map.put(field.getKey(), field.getValue());
    }
    i = getExtensionObjectNode().fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      map.put(field.getKey(), field.getValue());
    }
    return map;
  }
}
