/*
 * Copyright 2015-2026 Ping Identity Corporation
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
 * Copyright 2015-2026 Ping Identity Corporation
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

import com.unboundid.scim2.common.annotations.NotNull;
import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.JsonFactory;

import java.io.Reader;


/**
 * Custom JsonFactory implementation for SCIM.
 */
public class ScimJsonFactory extends JsonFactory
{
  /**
   * Creates a new SCIM-compatible JsonFactory instance.
   */
  public ScimJsonFactory()
  {
    super();
  }

  /**
   * A constructor used when copying an existing SCIM JsonFactory instance.
   *
   * @param sourceFactory   The original ScimJsonFactory.
   */
  protected ScimJsonFactory(@NotNull final ScimJsonFactory sourceFactory)
  {
    super(sourceFactory);
  }

  /**
   * Create a parser that can be used for parsing JSON objects contained
   * within a SCIM filter specification.
   *
   * @param r Reader to use for reading JSON content to parse
   * @return ScimFilterJsonParser object
   */
  @NotNull
  JsonParser createScimFilterParser(@NotNull final Reader r)
  {
    var config = ErrorReportConfiguration.defaults();
    ContentReference reference = ContentReference.construct(true, r, config);
    IOContext ctxt = _createContext(reference, false);

    final ObjectReadContext readCtxt = getReadContext(r);
    return new ScimFilterJsonParser(readCtxt,
        ctxt,
        readCtxt.getStreamReadFeatures(0),
        readCtxt.getFormatReadFeatures(0),
        r,
        _rootCharSymbols.makeChild()
    );
  }

  /**
   * Provides another instance of this factory object.
   *
   * @return A new ScimJsonFactory instance.
   */
  @Override
  @NotNull
  public ScimJsonFactory copy()
  {
    return new ScimJsonFactory(this);
  }

  /**
   * Provides a Jackson reader context based on the settings established in the
   * SCIM SDK's JsonMapper.
   */
  @NotNull
  private static ObjectReadContext getReadContext(@NotNull final Reader r)
  {
    // Create a parser to obtain access to an ObjectReadContext, then close the
    // parser to avoid leaking an object on every invocation. When Jackson
    // closes a parser, the underlying Reader, r, is not closed when the caller
    // owns the resource, so this is safe. An alternative to this approach is to
    // use SDK_OBJECT_MAPPER._deserializationContext() as the ObjectReadContext.
    // However, that method is labelled for unit test usage only, so it is not
    // guaranteed to be a stable API in future versions of Jackson.
    var parser = JsonUtils.getObjectReader().createParser(r);
    ObjectReadContext context = parser.objectReadContext();
    parser.close();
    return context;
  }
}
