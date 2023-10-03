/*
 * Copyright 2015-2023 Ping Identity Corporation
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
  public abstract void write(ListResponseWriter<T> os) throws IOException;


  /**
   * {@inheritDoc}
   */
  public final void write(final OutputStream os) throws IOException
  {
    ListResponseWriter<T> handler =
        new ListResponseWriter<T>(os);
    handler.startResponse();
    write(handler);
    handler.endResponse();
  }
}
