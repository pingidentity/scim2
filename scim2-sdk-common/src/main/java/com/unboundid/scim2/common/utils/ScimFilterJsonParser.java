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

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;

import java.io.Reader;

/**
 * A parser that can be used for parsing JSON objects contained
 * within a SCIM filter specification.
 */
public class ScimFilterJsonParser extends ReaderBasedJsonParser
{
  /**
   * Constructor.
   *
   * @param ctxt  see superclass
   * @param features  see superclass
   * @param r see superclass
   * @param codec see superclass
   * @param st see superclass
   */
  public ScimFilterJsonParser(
      @NotNull final IOContext ctxt,
      final int features,
      @Nullable final Reader r,
      @Nullable final ObjectCodec codec,
      @NotNull final CharsToNameCanonicalizer st)
  {
    super(ctxt, features, r, codec, st);
    // By default, the JSON read context is set to JsonStreamContext.TYPE_ROOT,
    // which will require whitespace after any unquoted token (e.g., a number).
    // We don't want this restriction when parsing a SCIM filter, so set the
    // context type to -1, which is effectively "none".
    this._parsingContext = new JsonReadContext(null, 0, null, -1, 1, 0);
  }
}
