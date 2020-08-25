# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## v2.3.6 - TBD
TBD


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

