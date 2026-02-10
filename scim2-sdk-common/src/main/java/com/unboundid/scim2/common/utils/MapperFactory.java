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
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import com.unboundid.scim2.common.annotations.NotNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
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
 * If your project would benefit from enabling or disabling certain Jackson
 * features on the SCIM SDK's object mapper, use one of the following methods:
 * <ul>
 *   <li> {@link #setMapperCustomFeatures}
 *   <li> {@link #setDeserializationCustomFeatures}
 *   <li> {@link #setSerializationCustomFeatures}
 *   <li> {@link #setJsonParserCustomFeatures}
 *   <li> {@link #setJsonGeneratorCustomFeatures}
 * </ul>
 *
 * For example, to disable the
 * {@link MapperFeature#ACCEPT_CASE_INSENSITIVE_PROPERTIES} property, use the
 * following Java code:
 * <pre><code>
 *   MapperFactory newFactory = new MapperFactory();
 *   newFactory.setMapperCustomFeatures(
 *       Map.of(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false)
 *   );
 *
 *   // Register the new MapperFactory with the SCIM SDK.
 *   JsonUtils.setCustomMapperFactory(newFactory);
 * </code></pre>
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
 *    {@literal @}Override
 *     public ObjectMapper createObjectMapper()
 *     {
 *       // Fetch the initial object mapper from the superclass, then add your
 *       // customizations. Do not instantiate a new ObjectMapper.
 *       ObjectMapper mapper = super.createObjectMapper();
 *
 *       // Add the desired customizations.
 *       SimpleModule module = new SimpleModule();
 *       module.addSerializer(DesiredClass.class, new CustomSerializer());
 *       module.addDeserializer(DesiredClass.class, new CustomDeserializer());
 *       mapper.registerModule(module);
 *
 *       return mapper;
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
  @NotNull
  private Map<DeserializationFeature, Boolean> deserializationCustomFeatures =
      Collections.emptyMap();

  @NotNull
  private Map<JsonReadFeature, Boolean> jsonParserCustomFeatures =
      Collections.emptyMap();

  @NotNull
  private Map<StreamWriteFeature, Boolean> jsonGeneratorCustomFeatures =
      Collections.emptyMap();

  @NotNull
  private Map<MapperFeature, Boolean> mapperCustomFeatures =
      Collections.emptyMap();

  @NotNull
  private Map<SerializationFeature, Boolean> serializationCustomFeatures =
      Collections.emptyMap();

  /**
   * Sets custom deserialization features for any JSON ObjectMapper that is
   * used and returned by the SCIM 2 SDK.  This class should be used
   * to configure any object mapper customizations needed prior to using
   * any method in the JsonUtils class.
   *
   * @param customFeatures The list of custom deserialization feature settings.
   * @return this object.
   */
  @NotNull
  public MapperFactory setDeserializationCustomFeatures(
      @NotNull final Map<DeserializationFeature, Boolean> customFeatures)
  {
    deserializationCustomFeatures = customFeatures;
    return this;
  }

  // TODO: Is it possible to delete this one?
  /**
   * Sets custom JSON generator features for any JSON ObjectMapper that is
   * used and returned by the SCIM 2 SDK.  This class should be used
   * to configure any object mapper customizations needed prior to using
   * any method in the JsonUtils class.
   *
   * @param customFeatures The list of custom JSON generator feature settings.
   * @return this object.
   */
  @NotNull
  public MapperFactory setJsonGeneratorCustomFeatures(
      @NotNull final Map<JsonReadFeature, Boolean> customFeatures)
  {
//    jsonGeneratorCustomFeatures = customFeatures;
    return this;
  }

  /**
   * Sets custom JSON parser features for any JSON ObjectMapper that is
   * used and returned by the SCIM 2 SDK.  This class should be used
   * to configure any object mapper customizations needed prior to using
   * any method in the JsonUtils class.
   *
   * @param customFeatures The list of custom JSON parser feature settings.
   * @return this object.
   */
  @NotNull
  public MapperFactory setJsonParserCustomFeatures(
      @NotNull final Map<JsonReadFeature, Boolean> customFeatures)
  {
    jsonParserCustomFeatures = customFeatures;
    return this;
  }

  /**
   * Sets custom mapper features for any JSON ObjectMapper that is
   * used and returned by the SCIM 2 SDK.  This class should be used
   * to configure any object mapper customizations needed prior to using
   * any method in the JsonUtils class.
   *
   * @param customFeatures The list of custom mapper feature settings.
   * @return this object.
   */
  @NotNull
  public MapperFactory setMapperCustomFeatures(
      @NotNull final Map<MapperFeature, Boolean> customFeatures)
  {
    mapperCustomFeatures = customFeatures;
    return this;
  }

  /**
   * Sets custom serialization features for any JSON ObjectMapper that is
   * used and returned by the SCIM 2 SDK.  This class should be used
   * to configure any object mapper customizations needed prior to using
   * any method in the JsonUtils class.
   *
   * @param customFeatures The list of custom serialization feature settings.
   * @return this object.
  */
  @NotNull
  public MapperFactory setSerializationCustomFeatures(
      @NotNull final Map<SerializationFeature, Boolean> customFeatures)
  {
    serializationCustomFeatures = customFeatures;
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
    // Create a new object mapper with case-insensitive settings.
    JsonMapper.Builder builder = JsonMapper.builder(new ScimJsonFactory());

    // Do not care about case when de-serializing POJOs.
    builder.enable(ACCEPT_CASE_INSENSITIVE_PROPERTIES);

    // TODO: This is enabled for debugging, delete this later.
    builder.enable(INCLUDE_SOURCE_IN_LOCATION);

    // The SCIM SDK relies on its own custom deserializers for timestamps.
    builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Preserve original behavior from Jackson 2.x. This ensures, for example,
    // that "userName" is one of the first attributes listed when printing a
    // UserResource.
    builder.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

    // Don't serialize POJO nulls as JSON nulls.
    var nonNull = JsonInclude.Include.NON_NULL;
    builder.changeDefaultPropertyInclusion(i -> i.withValueInclusion(nonNull))
        .changeDefaultPropertyInclusion(i -> i.withContentInclusion(nonNull));

    // Only use xsd:dateTime format for dates.
    SimpleModule dateTimeModule = new SimpleModule();
    dateTimeModule.addSerializer(Calendar.class, new CalendarSerializer());
    dateTimeModule.addDeserializer(Calendar.class, new CalendarDeserializer());
    dateTimeModule.addSerializer(Date.class, new DateSerializer());
    dateTimeModule.addDeserializer(Date.class, new DateDeserializer());
    builder.addModule(dateTimeModule);

    // Use case-insensitive JsonNodes.
    builder.nodeFactory(new ScimJsonNodeFactory());

    // Add any custom mapper features. This is the last step before building the
    // object mapper to ensure that customizations are not overwritten.
    mapperCustomFeatures.forEach(builder::configure);
    deserializationCustomFeatures.forEach(builder::configure);
    jsonGeneratorCustomFeatures.forEach(builder::configure);
    jsonParserCustomFeatures.forEach(builder::configure);
    serializationCustomFeatures.forEach(builder::configure);

    return builder.build();
  }
}
