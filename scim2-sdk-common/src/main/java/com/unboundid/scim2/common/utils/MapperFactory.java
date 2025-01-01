/*
 * Copyright 2016-2025 Ping Identity Corporation
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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.unboundid.scim2.common.annotations.NotNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES;

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
 * <pre>
 *   MapperFactory newFactory = new MapperFactory();
 *   newFactory.setMapperCustomFeatures(
 *       Map.of(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false)
 *   );
 *
 *   // Register the new MapperFactory with the SCIM SDK.
 *   JsonUtils.setCustomMapperFactory(newFactory);
 * </pre>
 *
 * If your desired customization is more complicated than enabling/disabling
 * Jackson features, an alternative is to create a custom {@code MapperFactory}
 * class that overrides the behavior of {@link #createObjectMapper}. When
 * overriding this method, the subclass <em>must</em> first fetch an object
 * mapper from the superclass to ensure that the SCIM SDK's original
 * configuration is preserved. For example:
 * <pre>
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
 * </pre>
 *
 * When your application starts up, register your customer mapper factory with
 * the SCIM SDK to use the object mapper returned by the custom class:
 * <pre>
 *   JsonUtils.setCustomMapperFactory(new CustomMapperFactory());
 * </pre>
 */
public class MapperFactory
{
  @NotNull
  private Map<DeserializationFeature, Boolean> deserializationCustomFeatures =
      Collections.emptyMap();

  @NotNull
  private Map<JsonParser.Feature, Boolean> jsonParserCustomFeatures =
      Collections.emptyMap();

  @NotNull
  private Map<JsonGenerator.Feature, Boolean> jsonGeneratorCustomFeatures =
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
      @NotNull final Map<JsonGenerator.Feature, Boolean> customFeatures)
  {
    jsonGeneratorCustomFeatures = customFeatures;
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
      @NotNull final Map<JsonParser.Feature, Boolean> customFeatures)
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
  public ObjectMapper createObjectMapper()
  {
    // Create a new object mapper with case-insensitive settings.
    var objectMapperBuilder = JsonMapper.builder(new ScimJsonFactory());

    // Do not care about case when de-serializing POJOs.
    objectMapperBuilder.enable(ACCEPT_CASE_INSENSITIVE_PROPERTIES);

    // Add any custom mapper features. This must be done before other fields
    // (e.g., serializationCustomFeatures) because it must be configured on the
    // builder object.
    mapperCustomFeatures.forEach(objectMapperBuilder::configure);

    final ObjectMapper mapper = objectMapperBuilder.build();

    // Don't serialize POJO nulls as JSON nulls.
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // Only use xsd:dateTime format for dates.
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    SimpleModule dateTimeModule = new SimpleModule();
    dateTimeModule.addSerializer(Calendar.class, new CalendarSerializer());
    dateTimeModule.addDeserializer(Calendar.class, new CalendarDeserializer());
    dateTimeModule.addSerializer(Date.class, new DateSerializer());
    dateTimeModule.addDeserializer(Date.class, new DateDeserializer());
    mapper.registerModule(dateTimeModule);

    // Use the case-insensitive JsonNodes.
    mapper.setNodeFactory(new ScimJsonNodeFactory());

    // Configure the custom Jackson features for object mappers created and used
    // by the SCIM SDK. This step is performed last to ensure that
    // customizations are not overwritten.
    deserializationCustomFeatures.forEach(mapper::configure);
    jsonGeneratorCustomFeatures.forEach(mapper::configure);
    jsonParserCustomFeatures.forEach(mapper::configure);
    serializationCustomFeatures.forEach(mapper::configure);

    return mapper;
  }
}
