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

package com.unboundid.scim2.server;

import com.unboundid.scim2.common.ScimResource;

import com.unboundid.scim2.common.annotations.NotNull;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for streaming list/query results using the ListResponse container.
 */
public abstract class ListResponseStreamingOutput<T extends ScimResource>
    implements StreamingOutput
{
  /**
   * Start streaming the contents of the list response. The list response will
   * be considered complete upon return;
   *
   * @param os The list response output stream used to stream back elements of
   *           the list response.
   * @throws IOException if an error occurs while writing.
   */
  public abstract void write(@NotNull ListResponseWriter<T> os)
      throws IOException;


  /**
   * {@inheritDoc}
   */
  public final void write(@NotNull final OutputStream os)
      throws IOException
  {
    ListResponseWriter<T> handler = new ListResponseWriter<>(os);
    handler.startResponse();
    write(handler);
    handler.endResponse();
  }
}
