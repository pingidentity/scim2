/*
 * Copyright 2016-2019 Ping Identity Corporation
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
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Class used to customize the object mapper that is used by the SCIM 2 SDK.
 */
public class MapperFactory
{
  private static Map<DeserializationFeature, Boolean> deserializationCustomFeatures =
      Collections.<DeserializationFeature, Boolean>emptyMap();
  private static Map<JsonParser.Feature, Boolean> jsonParserCustomFeatures =
      Collections.<JsonParser.Feature, Boolean>emptyMap();
  private static Map<JsonGenerator.Feature, Boolean> jsonGeneratorCustomFeatures =
      Collections.<JsonGenerator.Feature, Boolean>emptyMap();
  private static Map<MapperFeature, Boolean> mapperCustomFeatures =
      Collections.<MapperFeature, Boolean>emptyMap();
  private static Map<SerializationFeature, Boolean> serializationCustomFeatures =
      Collections.<SerializationFeature, Boolean>emptyMap();

  /**
   * Sets custom deserialization features for any JSON ObjectMapper that is
   * used and returned by the SCIM 2 SDK.  This class should be used
   * to configure any object mapper customizations needed prior to using
   * any method in the JsonUtils class.
   *
   * @param customFeatures The list of custom deserialization feature settings.
   * @return this object.
   */
  public MapperFactory setDeserializationCustomFeatures(
      final Map<DeserializationFeature, Boolean> customFeatures)
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
  public MapperFactory setJsonGeneratorCustomFeatures(
      final Map<JsonGenerator.Feature, Boolean> customFeatures)
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
  public MapperFactory setJsonParserCustomFeatures(
      final Map<JsonParser.Feature, Boolean> customFeatures)
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
  public MapperFactory setMapperCustomFeatures(
      final Map<MapperFeature, Boolean> customFeatures)
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
  public MapperFactory setSerializationCustomFeatures(
      final Map<SerializationFeature, Boolean> customFeatures)
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
  public static ObjectMapper createObjectMapper()
  {
    ObjectMapper mapper = new ObjectMapper(new ScimJsonFactory());

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

    // Do not care about case when de-serializing POJOs.
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    // Use the case-insensitive JsonNodes.
    mapper.setNodeFactory(new ScimJsonNodeFactory());

    for (DeserializationFeature feature : deserializationCustomFeatures.keySet())
    {
      mapper.configure(feature, deserializationCustomFeatures.get(feature));
    }

    for (JsonGenerator.Feature feature : jsonGeneratorCustomFeatures.keySet())
    {
      mapper.configure(feature, jsonGeneratorCustomFeatures.get(feature));
    }

    for (JsonParser.Feature feature : jsonParserCustomFeatures.keySet())
    {
      mapper.configure(feature, jsonParserCustomFeatures.get(feature));
    }

    for (MapperFeature feature : mapperCustomFeatures.keySet())
    {
      mapper.configure(feature, mapperCustomFeatures.get(feature));
    }

    for (SerializationFeature feature : serializationCustomFeatures.keySet())
    {
      mapper.configure(feature, serializationCustomFeatures.get(feature));
    }

    return mapper;
  }
}
