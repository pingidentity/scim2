# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## v2.2.1 - unreleased
Maven POM changes: Removed unused dependencies, declared previously implicit dependencies, declared 'test' scope as appropriate, and added a dependencyManagement section to the scim2-parent POM.

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

