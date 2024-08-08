# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

## v3.1.1 - TBD
Fixed an issue where `AndFilter.equals()` and `OrFilter.equals()` could incorrectly evaluate to
true.

## v3.1.0 - 2024-Jun-25
Updated all classes within the UnboundID SCIM 2 SDK to utilize `@Nullable` and `@NotNull`
annotations for all non-primitive input parameters, member variables, and return values. These
annotations provide additional context on the nullability of certain values when working with the
SDK, and the annotations will also appear in the Javadocs. Note that the fully-qualified class names
of the annotations take the form of `com.unboundid.scim2.common.annotations.Nullable`.

Resolved an issue with replace operations that set the `value` field to an empty array. When these
operations are applied, the SCIM SDK now clears all matching values of the targeted multi-valued
attribute. If the `path` of the replace operation does not have a filter, then the multi-valued
attribute will be deleted from the resource.

## v3.0.0 - 2023-Oct-03
Removed support for Java 8. The UnboundID SCIM 2 SDK now requires Java 11 or a later release.

Migrated `javax.*` namespaces to use Jakarta EE. Since Oracle has given stewardship of the Java EE
project to Eclipse, this change facilitates the usage of these libraries under the Jakarta name,
which provides better integration with projects such as Spring Boot 3.0. If your project uses
components from JAX-RS or Jersey related to the SCIM SDK, then these references must be updated to
match the new method definitions. For example, any code that creates a
`javax.ws.rs.client.WebTarget` before passing it to the SCIM SDK will need to provide a
`jakarta.ws.rs.client.WebTarget` instead. To support this change, the following project dependencies
were also upgraded:
* Updated `javax.xml.bind-api` to `jakarta.xml.bind-api` version 4.0.1
* Updated `javax.ws.rs-api` to `jakarta.ws.rs-api` version 3.1.0
* Updated `javax.annotation-api` to `jakarta.annotation-api` version 2.1.1
* Updated Jersey from 2.39.1 to 3.1.3

Overhauled many of the class-level Javadocs in the scim2-sdk-common package. This provides better
descriptions for SCIM entities, SDK-specific constructs (e.g., `ScimResource`), and more background
on SCIM conventions such as filtering. The new documentation also provides better descriptions for
how certain classes in the SDK should be used, such as the `Filter` class.

Added new constructors for some exception types involving the `scimType` field. This field is empty
in many cases, so these new constructors set the `scimType` value to be `null` with the goal of
simplifying the process of creating exceptions.

Added support for patch operations of type ADD that contain a value selection filter in the path,
e.g., `emails[type eq "work"].value`. This type of request is used by some SCIM service providers to
append extra data for multi-valued attributes such as `emails` or `addresses`.

Removed deprecated methods in PatchOperation.java and GenericScimResource.java that utilized
multi-valued boolean arrays.

## v2.4.0 - 2023-Jul-28
Fixed an issue with PatchOperations that prevented setting the `value` field to an empty array. The
constructor would previously reject this kind of operation with a BadRequestException.

Added a variety of methods for providing multi-valued parameters directly into a method without
needing to wrap the arguments into a List. For example, setting a single email on a UserResource used
to be done with `user.setEmails(Collections.singletonList(email))`, but this can now be shortened to
`user.setEmails(email)`. Note that the existing methods are still available and have not been
deprecated. Other examples include `BaseScimResource.setSchemaUrns()`,
`GenericScimResource.addStringValues()`, `PatchOperation.addDoubleValues()`, and the `PatchRequest`
constructor.

Updated the schema URNs field of `BaseScimResource` to use a `LinkedHashSet` instead of a generic
`HashSet`. This allows for a SCIM resource with multiple schema URNs to have a predictable order
when the resource is deserialized into JSON.

Deprecated methods of the form `addBooleanValues()` and `getBooleanValueList()` on the
`GenericScimResource` and `PatchOperation` classes. These methods provided an interface for
so-called "multi-valued boolean arrays", but boolean data is always single-valued in nature.
Updating a boolean value should always be done with a `replace` operation type rather than an `add`.

Fixed an issue where `AttributeDefinition.toString()` would not print the mutability of an
attribute.

Added a `type` field to the `Member` class as defined by RFC 7643 section 8.7.1.

Fixed an issue with the attribute definitions of the `members` field of a GroupResource. The
attribute definitions now indicate that the sub-attributes of `members` are all immutable.

Fixed an issue where calling `ObjectMapper.copy()` failed for object mappers that were created with
`JsonUtils.createObjectMapper()`.

## v2.3.8 - 2023-05-17
Updated the deserialized form of ListResponse objects so that the `itemsPerPage` and `startIndex`
fields are listed at the top with `totalResults`. This matches the form of ListResponses shown in
RFC 7644.

Updated the PatchOperation class to accept operation types where the first letter is capitalized,
e.g., `"Add"`. This makes the SDK more permissive of operation types that violate RFC 7644, which
is a known problem with Azure Active Directory as a SCIM client.

Updated the Jackson and jackson-databind dependencies to 2.14.2.

Updated the jersey dependency to 2.39.1 to address a `ClassCastException` error.


## v2.3.7 - 2021-09-07
Added ParserOptions class, which may be used to extend the set of characters allowed in filtered
attribute names.

Updated the TestNG dependency to 7.4.0 and corrected its scope from the default 'compile' to 'test'.


## v2.3.6 - 2021-06-11
Bump jackson-databind from 2.10.2 to 2.10.5.1.


## v2.3.5 - 2020-08-23
Update the Guava dependency to 29.0-jre, which addresses latest known potential security issues.


## v2.3.4 - 2020-06-08
Fix possible template variable injection within search parameters and returned resource location URLs.


## v2.3.3 - 2020-03-17
[Unused release version]


## v2.3.2 - 2019-10-04
Updated the jackson-databind dependency to 2.9.10, which addresses latest known potential security issues.


## v2.3.1 - 2019-08-16
Updated the jackson-databind dependency to 2.9.9.3, which addresses a number of potential security issues.


## v2.3.0 - 2019-08-05
The SCIM 2 DateTime handling code has been updated to rely on xsd:dateTime code provided by JAXB rather than on Jackson APIs that are deprecated as of version 2.9.x. A DateTimeUtils class has been added to perform conversions between SCIM 2 DateTime strings and Java Date and Calendar objects. The ScimDateFormat class is now deprecated and should not be used.

The Version class is no longer generated at build time for each module, and is now only generated for scim2-sdk-common. Because all other modules include scim2-sdk-common as a dependency, the Version class will continue to be available for all modules. This should eliminate duplicate class warnings when the SCIM 2 SDK is included in projects built using (for example) the Maven Shade Plugin.

A GroupResource POJO class is now provided for working with SCIM Group resources.

The `ListResponse#getTotalResults()` method has been updated to return a value of type int rather than long.

Changed the mutability value of the UserResource groups attribute to 'readOnly' for conformance with RFC 7643.


## v2.2.2 - 2019-06-24
Updated the Jackson dependencies to 2.9.9, which addresses a potential security issue found in earlier versions of that library.


## v2.2.1 - 2019-03-11
Updated the jackson-databind dependency to 2.7.9.5, which addresses a number of security issues found in earlier versions of that library.

Maven POM changes: Removed unused dependencies, declared previously implicit dependencies, declared 'test' scope as appropriate, and added a dependencyManagement section to the scim2-parent POM.

Fixed an issue with `GenericScimResource.replaceValue(String, Date)` wrapping date values in double quotes.


## v2.2.0 - 2018-05-21
Updated ErrorResponse to serialize its "status" field as a JSON string rather than as a number for compliance with RFC 7644. Deserialization of this field is backwards compatible and will accept either a number or a string. Clients expecting the "status" field as a JSON string (including older SCIM 2 SDK clients) will need to be updated for compatibility.


## v2.1.3 - 2017-11-29
Fixed several issues around binary attribute handling.


## v2.1.2 - 2017-08-09
Fixed a SCIM issue to restore the use of the implicit "value" sub-attribute in filters to reference specific values of simple multi-valued attributes


## v2.1.1 - 2017-02-13
Allow replacement of singular value using a value filter if the value filter uses the special-case "value" path.

Added ability to change "accept" and "content-type" headers in the SCIM client.

Fixed the handling of sub-attribute paths in the excludedAttributes parameter.

PUT requests will now replace the value of a caseExact attribute whose only change is to case.

Updated the EmailValidationRequest and TelephonyValidationRequest for compatibility with Data Governance Broker 6.0.1.0.


## v2.0.4 - 2016-09-26
Changed unboundid URNs to pingidentity.

Add exception class for 405 Method Not Allowed exceptions, and corrected some exception mapping issues.

Fixed a couple filtering and search issues.

Make sure the Response is always closed after a SCIM operation.

## v1.2.9 - 2016-07-08
Initial Public Release

