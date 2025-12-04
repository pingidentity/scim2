/*
 * Copyright 2025 Ping Identity Corporation
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
 * Copyright 2025 Ping Identity Corporation
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

import com.unboundid.scim2.common.messages.ErrorResponse;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.nio.BufferOverflowException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLClientInfoException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for {@link ScimException} and its subclasses.
 */
public class ScimExceptionTest
{
  /**
   * Tests for the base {@link ScimException} class.
   */
  @Test
  public void testScimException()
  {
    final int customErrorCode = 418;

    ScimException e = new ScimException(customErrorCode, "I'm a teapot.");
    assertThat(e.getScimError().getStatus()).isEqualTo(customErrorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("I'm a teapot.");
    assertThat(e.getCause()).isNull();

    e = ScimException.createException(new ErrorResponse(304), null);
    assertThat(e).isInstanceOf(NotModifiedException.class);
    e = ScimException.createException(new ErrorResponse(400), null);
    assertThat(e).isInstanceOf(BadRequestException.class);
    e = ScimException.createException(new ErrorResponse(401), null);
    assertThat(e).isInstanceOf(UnauthorizedException.class);
    e = ScimException.createException(new ErrorResponse(403), null);
    assertThat(e).isInstanceOf(ForbiddenException.class);
    e = ScimException.createException(new ErrorResponse(404), null);
    assertThat(e).isInstanceOf(ResourceNotFoundException.class);
    e = ScimException.createException(new ErrorResponse(405), null);
    assertThat(e).isInstanceOf(MethodNotAllowedException.class);
    e = ScimException.createException(new ErrorResponse(409), null);
    assertThat(e).isInstanceOf(ResourceConflictException.class);
    e = ScimException.createException(new ErrorResponse(412), null);
    assertThat(e).isInstanceOf(PreconditionFailedException.class);
    e = ScimException.createException(new ErrorResponse(500), null);
    assertThat(e).isInstanceOf(ServerErrorException.class);
    e = ScimException.createException(new ErrorResponse(501), null);
    assertThat(e).isInstanceOf(NotImplementedException.class);
    e = ScimException.createException(new ErrorResponse(1389), null);
    assertThat(e).isInstanceOf(ScimException.class);
  }

  /**
   * Tests for {@link NotModifiedException}.
   */
  @Test
  public void testNotModifiedException()
  {
    final int errorCode = 304;

    NotModifiedException e =
        new NotModifiedException("The resource has not been modified.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("has not been modified.");
    assertThat(e.getCause()).isNull();

    assertThat(e.getVersion()).isNull();

    e = new NotModifiedException("Not modified", null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("Not modified");
    assertThat(e.getCause()).isNull();

    assertThat(e.getVersion()).isNull();

    e = new NotModifiedException("Still not modified",
        "unmodified",
        "currentVersionTag",
        null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("unmodified");
    assertThat(e.getMessage()).isEqualTo("Still not modified");
    assertThat(e.getCause()).isNull();
  }

  /**
   * Tests for {@link BadRequestException}.
   */
  @Test
  public void testBadRequestException()
  {
    final int errorCode = 400;

    BadRequestException e =
        new BadRequestException("Detailed message explaining the error.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("message explaining the error.");
    assertThat(e.getCause()).isNull();

    e = new BadRequestException("Bad request", new ParseException("failed", 0));
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("Bad request");
    assertThat(e.getCause()).isInstanceOf(ParseException.class);

    e = new BadRequestException("No", "justNo", new SQLClientInfoException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("justNo");
    assertThat(e.getMessage()).isEqualTo("No");
    assertThat(e.getCause()).isInstanceOf(SQLClientInfoException.class);

    e = BadRequestException.invalidFilter("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidFilter");
    e = BadRequestException.tooMany("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("tooMany");
    e = BadRequestException.uniqueness("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("uniqueness");
    e = BadRequestException.mutability("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("mutability");
    e = BadRequestException.invalidSyntax("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidSyntax");
    e = BadRequestException.invalidPath("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidPath");
    e = BadRequestException.noTarget("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("noTarget");
    e = BadRequestException.invalidValue("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidValue");
    e = BadRequestException.invalidVersion("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidVersion");
    e = BadRequestException.invalidCursor("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidCursor");
    e = BadRequestException.expiredCursor("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("expiredCursor");
    e = BadRequestException.invalidCount("Message");
    assertThat(e.getScimError().getScimType()).isEqualTo("invalidCount");
  }

  /**
   * Tests for {@link UnauthorizedException}.
   */
  @Test
  public void testUnauthorizedException()
  {
    final int errorCode = 401;

    UnauthorizedException e = new UnauthorizedException(
        "The client is not authorized to perform the operation.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("client is not authorized to perform");
    assertThat(e.getCause()).isNull();

    e = new UnauthorizedException("Not authorized", "illegal",
        new IllegalStateException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("illegal");
    assertThat(e.getMessage()).isEqualTo("Not authorized");
    assertThat(e.getCause()).isInstanceOf(IllegalStateException.class);

    var errorResponse = new ErrorResponse(errorCode);
    errorResponse.setDetail("unauthorized detail message");
    e = new UnauthorizedException(errorResponse, new ClassCastException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("unauthorized detail message");
    assertThat(e.getCause()).isInstanceOf(ClassCastException.class);
  }

  /**
   * Tests for {@link ForbiddenException}.
   */
  @Test
  public void testForbiddenException()
  {
    final int errorCode = 403;

    ForbiddenException e = new ForbiddenException("Access denied.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("Access denied.");
    assertThat(e.getCause()).isNull();

    e = new ForbiddenException("Other method", "customScimType",
        new AccessDeniedException("Denied"));
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("customScimType");
    assertThat(e.getMessage()).isEqualTo("Other method");
    assertThat(e.getCause()).isInstanceOf(AccessDeniedException.class);

    var errorResponse = new ErrorResponse(errorCode);
    errorResponse.setDetail("detailMessage");
    e = new ForbiddenException(errorResponse, new SecurityException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("detailMessage");
    assertThat(e.getCause()).isInstanceOf(SecurityException.class);

    e = ForbiddenException.sensitive("That's sensitive, use POST search");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("sensitive");
    assertThat(e.getMessage()).contains("That's sensitive");
    assertThat(e.getCause()).isNull();
  }

  /**
   * Tests for {@link ResourceNotFoundException}.
   */
  @Test
  public void testNotFoundException()
  {
    final int errorCode = 404;

    ResourceNotFoundException e =
        new ResourceNotFoundException("The requested resource was not found.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("requested resource was not found.");
    assertThat(e.getCause()).isNull();

    e = new ResourceNotFoundException("Not Found", "notFound", new ConnectException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("notFound");
    assertThat(e.getMessage()).isEqualTo("Not Found");
    assertThat(e.getCause()).isInstanceOf(ConnectException.class);
  }

  /**
   * Tests for {@link MethodNotAllowedException}.
   */
  @Test
  public void testMethodNotAllowedException()
  {
    final int errorCode = 405;

    MethodNotAllowedException e = new MethodNotAllowedException(
        "The /.search endpoint only supports POST requests.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("only supports POST requests");
    assertThat(e.getCause()).isNull();

    var errorResponse = new ErrorResponse(errorCode);
    errorResponse.setDetail("GET is not supported here.");
    e = new MethodNotAllowedException(errorResponse, null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("GET is not supported here.");
    assertThat(e.getCause()).isNull();
  }

  /**
   * Tests for {@link ResourceConflictException}.
   */
  @Test
  public void testResourceConflictException()
  {
    final int errorCode = 409;

    ResourceConflictException e =
        new ResourceConflictException("Detailed error message.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("Detailed error message.");
    assertThat(e.getCause()).isNull();

    e = ResourceConflictException.uniqueness("The userName is already in use.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("uniqueness");
    assertThat(e.getMessage()).isEqualTo("The userName is already in use.");
    assertThat(e.getCause()).isNull();

    e = new ResourceConflictException(
        "Resource already exists", "customScimType", null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("customScimType");
    assertThat(e.getMessage()).isEqualTo("Resource already exists");
    assertThat(e.getCause()).isNull();

    var errorResponse = new ErrorResponse(errorCode);
    errorResponse.setDetail("The value is already in use.");
    e = new ResourceConflictException(errorResponse, null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("The value is already in use.");
    assertThat(e.getCause()).isNull();
  }

  /**
   * Tests for {@link PreconditionFailedException}.
   */
  @Test
  public void testPreconditionFailedException()
  {
    final int errorCode = 412;

    PreconditionFailedException e = new PreconditionFailedException(
        "Failed to update. The resource changed on the server.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("The resource changed on the server.");
    assertThat(e.getCause()).isNull();

    assertThat(e.getVersion()).isNull();

    e = new PreconditionFailedException("Version tag did not match", null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("Version tag did not match");
    assertThat(e.getCause()).isNull();

    assertThat(e.getVersion()).isNull();

    e = new PreconditionFailedException(
        "Provided version tag still does not match",
        "preconditionFailed",
        "currentVersionTagOnServer",
        null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("preconditionFailed");
    assertThat(e.getMessage()).contains("still does not match");
    assertThat(e.getCause()).isNull();

    assertThat(e.getVersion()).isEqualTo("currentVersionTagOnServer");

    var errorResponse = new ErrorResponse(errorCode);
    errorResponse.setDetail("Invalid version tag");
    e = new PreconditionFailedException(errorResponse, "serverVersion", null);
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("Invalid version tag");
    assertThat(e.getCause()).isNull();

    assertThat(e.getVersion()).isEqualTo("serverVersion");
  }

  /**
   * Tests for {@link ServerErrorException}.
   */
  @Test
  public void testServerErrorException()
  {
    final int errorCode = 500;

    ServerErrorException e =
        new ServerErrorException("An unexpected error occurred.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("An unexpected error occurred.");
    assertThat(e.getCause()).isNull();

    e = new ServerErrorException("Unexpected", "surprise",
        new ArrayIndexOutOfBoundsException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("surprise");
    assertThat(e.getMessage()).isEqualTo("Unexpected");
    assertThat(e.getCause()).isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  /**
   * Tests for {@link NotImplementedException}.
   */
  @Test
  public void testNotImplementedException()
  {
    final int errorCode = 501;

    NotImplementedException e =
        new NotImplementedException("The requested endpoint is not supported.");
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).contains("requested endpoint is not supported.");
    assertThat(e.getCause()).isNull();

    e = new NotImplementedException("Not Implemented", "notImplemented",
        new BufferOverflowException());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isEqualTo("notImplemented");
    assertThat(e.getMessage()).isEqualTo("Not Implemented");
    assertThat(e.getCause()).isInstanceOf(BufferOverflowException.class);

    var errorResponse = new ErrorResponse(errorCode);
    errorResponse.setDetail("It's just not implemented.");
    e = new NotImplementedException(errorResponse, new StackOverflowError());
    assertThat(e.getScimError().getStatus()).isEqualTo(errorCode);
    assertThat(e.getScimError().getScimType()).isNull();
    assertThat(e.getMessage()).isEqualTo("It's just not implemented.");
    assertThat(e.getCause()).isInstanceOf(StackOverflowError.class);
  }
}
