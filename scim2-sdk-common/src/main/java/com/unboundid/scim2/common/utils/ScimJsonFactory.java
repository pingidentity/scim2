/*
 * Copyright 2015-2025 Ping Identity Corporation
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


package com.unboundid.scim2.common.utils;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.io.IOException;
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
   * @param codec           The codec that defines the way that objects should
   *                        be serialized and deserialized. This may be
   *                        {@code null}.
   */
  protected ScimJsonFactory(@NotNull final ScimJsonFactory sourceFactory,
                            @Nullable final ObjectCodec codec)
  {
    super(sourceFactory, codec);
  }

  /**
   * Create a parser that can be used for parsing JSON objects contained
   * within a SCIM filter specification.
   *
   * @param r Reader to use for reading JSON content to parse
   * @return ScimFilterJsonParser object
   * @throws IOException on parse error
   */
  @NotNull
  JsonParser createScimFilterParser(@NotNull final Reader r)
      throws IOException
  {
    ContentReference reference = ContentReference.construct(true, r,
        ErrorReportConfiguration.defaults());
    IOContext ctxt = _createContext(reference, false);
    return new ScimFilterJsonParser(ctxt, _parserFeatures, r, _objectCodec,
        _rootCharSymbols.makeChild());
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
    return new ScimJsonFactory(this, _objectCodec);
  }
}
