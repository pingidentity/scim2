/*
 * Copyright 2016-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2016-2026 Ping Identity Corporation
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.unboundid.scim2.common.annotations.NotNull;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES;

/**
 * This class may be used to customize the object mapper that is used by the
 * SCIM SDK.
 * <br><br>
 *
 * The SCIM SDK uses a Jackson {@link ObjectMapper} to convert SCIM resources
 * between JSON strings and Plain Old Java Objects such as
 * {@link com.unboundid.scim2.common.types.UserResource}. This object mapper is
 * configured with specific settings to benefit applications that use the SCIM
 * SDK. For example, when converting a Java object to a JSON string, the SCIM
 * SDK will ignore {@code null} fields from the object.
 * <br><br>
 *
 * The SCIM SDK provides access to the initial builder used for the shared
 * object mapper in the {@link JsonUtils#getInitialMapperConfig()} and
 * {@link #createBuilder()} methods. This may be useful if your application
 * needs to modify Jackson settings. For example, to disable the Jackson
 * {@link MapperFeature#ACCEPT_CASE_INSENSITIVE_PROPERTIES} property, use the
 * following Java code:
 * <pre><code>
 *   JsonMapper.Builder newConfig = JsonUtils.getInitialMapperConfig()
 *       .disable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
 *
 *   // Register a new MapperFactory with the desired settings.
 *   JsonUtils.setCustomMapperFactory(new MapperFactory().setConfig(newConfig));
 * </code></pre>
 * <br><br>
 *
 * If your desired customization is more complicated than enabling/disabling
 * Jackson features, an alternative is to create a custom {@code MapperFactory}
 * class that overrides the behavior of {@link #createObjectMapper}. When
 * overriding this method, the subclass <em>must</em> first fetch an object
 * mapper from the superclass to ensure that the SCIM SDK's original
 * configuration is preserved. For example:
 * <pre><code>
 *   public class CustomMapperFactory extends MapperFactory
 *   {
 *     &#064;Override
 *     public ObjectMapper createObjectMapper()
 *     {
 *       // Fetch the initial builder from the superclass, then add your
 *       // customizations. Do not instantiate a new builder.
 *       JsonMapper.Builder builder = super.createBuilder();
 *
 *       // Add the desired customizations.
 *       SimpleModule module = new SimpleModule();
 *       module.addSerializer(DesiredClass.class, new CustomSerializer());
 *       module.addDeserializer(DesiredClass.class, new CustomDeserializer());
 *       builder.addModule(module);
 *
 *       return builder.build();
 *     }
 *   }
 * </code></pre>
 *
 * When your application starts up, register your customer mapper factory with
 * the SCIM SDK to use the object mapper returned by the custom class:
 * <pre><code>
 *   JsonUtils.setCustomMapperFactory(new CustomMapperFactory());
 * </code></pre>
 */
public class MapperFactory
{
  /**
   * The JsonMapper builder with the appropriate configuration for creating new
   * object mappers.
   */
  @NotNull
  private JsonMapper.Builder mapperConfigBuilder = createBuilder();

  /**
   * Creates a SCIM compatible {@link JsonMapper} builder instance. This
   * contains the mapper configuration that is used by the SCIM SDK.
   *
   * @return  A builder object with the initial SCIM SDK mapper configuration.
   *
   * @since 5.1.0
   */
  @NotNull
  public JsonMapper.Builder createBuilder()
  {
    // Create a new object mapper with case-insensitive settings.
    JsonMapper.Builder builder = JsonMapper.builder(new ScimJsonFactory());

    // Do not care about case when de-serializing POJOs.
    builder.enable(ACCEPT_CASE_INSENSITIVE_PROPERTIES);

    // Don't serialize POJO nulls as JSON nulls.
    var nonNull = JsonInclude.Include.NON_NULL;
    builder.changeDefaultPropertyInclusion(v -> v.withValueInclusion(nonNull))
        .changeDefaultPropertyInclusion(v -> v.withContentInclusion(nonNull));

    // Preserve the form that we used to print JSON in previous releases. This
    // ensures that serialized SCIM resources have JSON consistent with the RFC.
    builder.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

    // Only use xsd:dateTime format for dates.
    SimpleModule dateTimeModule = new SimpleModule();
    dateTimeModule.addSerializer(Calendar.class, new CalendarSerializer());
    dateTimeModule.addDeserializer(Calendar.class, new CalendarDeserializer());
    dateTimeModule.addSerializer(Date.class, new DateSerializer());
    dateTimeModule.addDeserializer(Date.class, new DateDeserializer());
    builder.addModule(dateTimeModule);

    // Use case-insensitive JsonNodes.
    builder.nodeFactory(new ScimJsonNodeFactory());

    return builder;
  }

  /**
   * Sets the provided mapper configuration on this MapperFactory.
   *
   * @param builder  The builder containing the ObjectMapper configuration.
   * @return  This instance.
   *
   * @since 5.1.0
   */
  @NotNull
  public MapperFactory setConfig(@NotNull final JsonMapper.Builder builder)
  {
    mapperConfigBuilder = Objects.requireNonNull(builder);
    return this;
  }

  /**
   * Creates a custom SCIM compatible Jackson ObjectMapper. Creating new
   * ObjectMapper instances are expensive so instances should be shared if
   * possible. This can be used to set the factory used to build new instances
   * of the object mapper used by the SCIM 2 SDK.
   *
   * @return an Object Mapper with the correct options set for serializing
   *     and deserializing SCIM JSON objects.
   */
  @NotNull
  public JsonMapper createObjectMapper()
  {
    return mapperConfigBuilder.build();
  }
}
