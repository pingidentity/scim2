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

package com.unboundid.scim2.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.utils.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An interface for writing list/query results using the SCIM ListResponse
 * container to an OutputStream.
 */
public class ListResponseWriter<T extends ScimResource>
{
  @NotNull
  private final JsonGenerator jsonGenerator;

  @NotNull
  private final AtomicBoolean startedResourcesArray = new AtomicBoolean();

  @NotNull
  private final AtomicBoolean sentTotalResults = new AtomicBoolean();

  @NotNull
  private final AtomicInteger resultsSent = new AtomicInteger();

  @NotNull
  private ObjectNode deferredFields;

  /**
   * Create a new ListResponseOutputStream that will write to the provided
   * output stream.
   *
   * @param outputStream The output stream to write to.
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  public ListResponseWriter(@NotNull final OutputStream outputStream)
      throws IOException
  {
    jsonGenerator =
        JsonUtils.getObjectReader().getFactory().createGenerator(outputStream);
    deferredFields = JsonUtils.getJsonNodeFactory().objectNode();
  }

  /**
   * Start the response.
   *
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  void startResponse() throws IOException
  {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeArrayFieldStart("schemas");
    jsonGenerator.writeString(
        "urn:ietf:params:scim:api:messages:2.0:ListResponse");
    jsonGenerator.writeEndArray();
  }

  /**
   * End the response.
   *
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  void endResponse() throws IOException
  {
    if(!sentTotalResults.get() && !deferredFields.has("totalResults"))
    {
      // The total results was never set. Set it to the calculated one.
      totalResults(resultsSent.get());
    }
    if(startedResourcesArray.get())
    {
      // Close the resources array if currently writing it.
      jsonGenerator.writeEndArray();
    }

    Iterator<Map.Entry<String, JsonNode>> i = deferredFields.fields();
    while(i.hasNext())
    {
      Map.Entry<String, JsonNode> field = i.next();
      jsonGenerator.writeObjectField(field.getKey(), field.getValue());
    }
    jsonGenerator.writeEndObject();
    jsonGenerator.flush();
    jsonGenerator.close();
  }

  /**
   * Write the startIndex to the output stream immediately if no resources have
   * been streamed, otherwise it will be written after the resources array.
   *
   * @param startIndex The startIndex to write.
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  public void startIndex(final int startIndex) throws IOException
  {
    if(startedResourcesArray.get())
    {
      deferredFields.put("startIndex", startIndex);
    }
    else
    {
      jsonGenerator.writeNumberField("startIndex", startIndex);
    }
  }

  /**
   * Write the itemsPerPage to the output stream immediately if no resources
   * have been streamed, otherwise it will be written after the resources array.
   *
   * @param itemsPerPage The itemsPerPage to write.
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  public void itemsPerPage(final int itemsPerPage) throws IOException
  {
    if(startedResourcesArray.get())
    {
      deferredFields.put("itemsPerPage", itemsPerPage);
    }
    else
    {
      jsonGenerator.writeNumberField("itemsPerPage", itemsPerPage);
    }
  }

  /**
   * Write the totalResults to the output stream immediately if no resources
   * have been streamed, otherwise it will be written after the resources array.
   *
   * @param totalResults The totalResults to write.
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  public void totalResults(final int totalResults) throws IOException
  {
    if(startedResourcesArray.get())
    {
      deferredFields.put("totalResults", totalResults);
    }
    else
    {
      jsonGenerator.writeNumberField("totalResults", totalResults);
      sentTotalResults.set(true);
    }
  }

  /**
   * Write the result resource to the output stream immediately.
   *
   * @param scimResource The resource to write.
   * @throws IOException If an exception occurs while writing to the output
   * stream.
   */
  public void resource(@Nullable final T scimResource) throws IOException
  {
    if(startedResourcesArray.compareAndSet(false, true))
    {
      jsonGenerator.writeArrayFieldStart("Resources");
    }
    jsonGenerator.writeObject(scimResource);
    resultsSent.incrementAndGet();
  }
}
