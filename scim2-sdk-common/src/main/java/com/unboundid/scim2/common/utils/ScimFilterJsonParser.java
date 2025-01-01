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
    // By default the JSON read context is set to JsonStreamContext.TYPE_ROOT,
    // which will require whitespace after any unquoted token (for example, a number).
    // We don't want this restriction when parsing a SCIM filter , so set the
    // context type to -1, which is effectively "none".
    this._parsingContext = new JsonReadContext(null, null, -1, 1, 0);
  }
}
