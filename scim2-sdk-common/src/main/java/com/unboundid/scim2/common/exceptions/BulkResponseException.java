/*
 * Copyright 2026 Ping Identity Corporation
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
 * Copyright 2026 Ping Identity Corporation
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

package com.unboundid.scim2.common.exceptions;

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;


/**
 * This class represents a SCIM exception pertaining to an invalid state for
 * bulk requests. This exception indicates that server-provided data in a bulk
 * response would have resulted in an invalid state. This exception type should
 * generally be caught and re-thrown as a {@link ServerErrorException} to
 * indicate an unexpected error in server-side processing.
 *
 * @see BulkRequestException
 */
public class BulkResponseException extends RuntimeException
{
  /**
   * Constructs a bulk request exception.
   *
   * @param message  The error message for this SCIM bulk exception.
   */
  public BulkResponseException(@NotNull final String message)
  {
    super(message);
  }

  /**
   * Constructs a bulk request exception.
   *
   * @param message  The error message for this SCIM bulk exception.
   * @param cause    The cause (which is saved for later retrieval by the
   *                 {@link #getCause()} method). A {@code null} value is
   *                 permitted, and indicates that the cause is nonexistent or
   *                 unknown.
   */
  public BulkResponseException(@NotNull final String message,
                               @Nullable final Throwable cause)
  {
    super(message, cause);
  }
}
