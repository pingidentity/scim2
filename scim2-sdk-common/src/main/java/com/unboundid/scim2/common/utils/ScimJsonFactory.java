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


package com.unboundid.scim2.common.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
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
    IOContext ctxt = _createContext(r, false);
    return new ScimFilterJsonParser(ctxt, _parserFeatures, r, _objectCodec,
        _rootCharSymbols.makeChild(_factoryFeatures));
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
