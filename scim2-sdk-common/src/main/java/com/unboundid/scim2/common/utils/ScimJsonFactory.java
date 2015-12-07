/*
 * Copyright 2015 UnboundID Corp.
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
import com.fasterxml.jackson.core.io.IOContext;

import java.io.IOException;
import java.io.Reader;


/**
 * Custom JsonFactory implementation for SCIM.
 */
public class ScimJsonFactory extends JsonFactory {


  /**
   * Create a parser that can be used for parsing JSON objects contained
   * within a SCIM filter specification.
   * @param r Reader to use for reading JSON content to parse
   * @return ScimFilterJsonParser object
   * @throws IOException on parse error
   */
  JsonParser createScimFilterParser(final Reader r)
      throws IOException {

    IOContext ctxt = _createContext(r, false);
    return new ScimFilterJsonParser(ctxt, _parserFeatures, r, _objectCodec,
        _rootCharSymbols.makeChild(_factoryFeatures));
  }

}
