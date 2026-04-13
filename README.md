[![Maven Central](https://img.shields.io/maven-central/v/com.unboundid.product.scim2/scim2-parent)](https://central.sonatype.com/search?q=unboundid.scim2-parent)
[![Javadocs](http://javadoc.io/badge/com.unboundid.product.scim2/scim2-parent.svg)](http://javadoc.io/doc/com.unboundid.product.scim2/scim2-parent)
[![Build Status](https://github.com/pingidentity/scim2/actions/workflows/build-and-test.yaml/badge.svg)](https://github.com/pingidentity/scim2/actions/workflows/build-and-test.yaml)

# UnboundID SCIM 2 SDK for Java
The UnboundID SCIM 2 SDK for Java provides a powerful and flexible set of APIs that helps developers
create applications that interface with the SCIM protocol. This SDK contains a foundation for
building both client and server applications, and contains support for managing sensitive user data,
filtering, request handling, generating error responses, and more. It is completely free and open
source, and is developed by [Ping Identity Corporation](https://www.pingidentity.com/).


## What Is SCIM?
[SCIM](https://www.simplecloud.info), or _System for Cross-domain Identity Management_, is an IETF
standard that defines an extensible schema mechanism and REST API for **managing users and other
identity data**. SCIM is used by a variety of vendors — including Salesforce, Microsoft,
Cisco, Sailpoint, and Ping Identity — for a variety of purposes, including user provisioning,
directory services, attribute exchange, and more.

SCIM is an open standard that is interoperable, which means that SCIM 2 clients should be able to
communicate with any SCIM 2 service. This avoids the need to accommodate multiple APIs to
communicate with different platforms, and also avoids vendor lock-in concerns. In practice,
different SCIM services can have slight variance in behavior by enforcing their own constraints, but
still provide much common ground.

## Advantages of the UnboundID SCIM 2 SDK
The UnboundID SCIM 2 SDK provides many strong benefits for applications that need to communicate
with SCIM 2.0 clients or servers:

* Full support for the SCIM 2.0 protocol as defined by the latest specification. This includes
  support for PATCH operations, filter processing, bulk operations, and cursor-based pagination.
* A simple and intuitive Jackson-based API that facilitates SCIM workflows, minimizing the amount
  of code you need to write for tasks such as performing CRUD operations on resources.
* `@NotNull` and `@Nullable` annotations are documented for all library input parameters, member
  variables, and method return values. This explicitly documents nullity expectations to reduce the
  likelihood of NullPointerExceptions in your code.
* Helpful model classes that allow you to interact with any SCIM resource as a Java object. This
  includes a powerful interface for manipulating
  [generic JSON data](https://javadoc.io/doc/com.unboundid.product.scim2/scim2-parent/latest/com/unboundid/scim2/common/GenericScimResource.html),
  even when there is not a clear model class or strong schema.
* An extendable framework that allows defining custom resource types by
  [extending core model classes](https://github.com/pingidentity/scim2/wiki/Common-Problems-and-FAQ#scim-service-provider-has-special-users).
* Extensive [documentation](https://javadoc.io/doc/com.unboundid.product.scim2/scim2-parent/latest/index.html)
  that thoroughly explains SCIM concepts and semantics, such as
  [service provider configuration](https://javadoc.io/doc/com.unboundid.product.scim2/scim2-parent/latest/com/unboundid/scim2/common/types/ServiceProviderConfigResource.html)
  and [bulk requests](https://javadoc.io/doc/com.unboundid.product.scim2/scim2-parent/latest/com/unboundid/scim2/common/bulk/BulkRequest.html).
* As with other Java libraries, this SDK can be used in projects written in other JVM languages such
  as Kotlin and Scala.
* Full support for interacting with Ping Identity SCIM services such as the
  [PingOne SCIM API](https://developer.pingidentity.com/pingone-api/platform/scim.html).

## Supported Versions
As of version 4.0.0, the UnboundID SCIM SDK requires Java SE 17 or greater.
This library also depends on the Jackson 2.x libraries for JSON serialization and deserialization.

## Structure
This library is separated into multiple modules to target specific use cases, and consists of the
following components:

| Component name          | What it is                                                                                | Who needs it                                                               |
|-------------------------|-------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| `scim2-sdk-common`      | This package contains core SCIM model classes, parsers, filter and PATCH processing, etc. | Developers looking for model classes, utilities, or general SCIM support.  |
| `scim2-sdk-client`      | This package contains classes to help create SCIM 2 client applications.                  | SCIM client developers using JAX-RS.                                       |
| `scim2-sdk-server`      | This package contains classes to help create SCIM 2 service providers.                    | SCIM service developers using JAX-RS. Spring projects should use `common`. |
| `scim2-ubid-extensions` | This package contains classes representing Ping Identity extensions to the SCIM standard. | Developers using features specific to Ping Identity services.              |
| `scim2-parent`          | This package contains all of the dependencies listed above.                               | Anyone who needs all of the above.                                         |

More information on each section:
* `scim2-sdk-common`: This package contains the core SCIM constructs and utility classes. This
  contains logic for SCIM filtering, performing updates/searches on JSON data, bulk request
  processing, and more. It may be used standalone without the other components.
* `scim2-sdk-client`: This component helps create SCIM 2 client applications using
  [JAX-RS](https://projects.eclipse.org/projects/ee4j.rest). For more information, see the
  [wiki](https://github.com/pingidentity/scim2/wiki/JAX-RS-Client-examples).
* `scim2-sdk-server`: This component helps create SCIM 2 services using JAX-RS. If you are not using
  JAX-RS and are using another solution (e.g., Spring), the `scim2-sdk-common` component is
  generally a better choice and may be used directly.
* `scim2-ubid-extensions`: Provides model classes specific to Ping Identity based constructs and
  SCIM extensions. This component is subject to API changes and should be considered experimental.
* `scim2-parent`: The parent module for all of the other components.

## How to Get It
The UnboundID SCIM SDK is available from Maven Central and can be included in your product like any other
Maven dependency. Check Maven Central for the latest available versions.
```xml
<dependency>
  <groupId>com.unboundid.product.scim2</groupId>
  <artifactId>scim2-sdk-common</artifactId>
  <version>${scim2.sdk.version}</version>
</dependency>
```

You may also download SCIM SDK builds from the [Releases](https://github.com/pingidentity/scim2/releases) page.

## How to Use It
The primary point of entry for a client is the `ScimService` class, which represents a SCIM service
provider. This class acts as a wrapper for a [JAX-RS](https://projects.eclipse.org/projects/ee4j.rest)
client instance, providing methods for building and making requests.

Other classes provide facilities for selecting attributes by path, building query filters, and working with JSON documents. SCIM resources returned from a service provider can either be represented as POJOs or using an API based on the [Jackson](https://github.com/FasterXML/jackson-docs) tree model.

```java
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.filters.Filter;

// Create a ScimService.
Client client = ClientBuilder.newClient()
    .register(OAuth2ClientSupport.feature("..bearerToken.."));
WebTarget target = client.target("https://example.com/scim/v2");
ScimService scimService = new ScimService(target);

// Create a user with a POST request by calling create().
UserResource user = new UserResource()
    .setUserName("babs")
    .setPassword("secret");
user.setName(new Name()
    .setGivenName("Barbara")
    .setFamilyName("Jensen"));
user.setEmails(new Email()
    .setType("home")
    .setPrimary(true)
    .setValue("babs@example.com"));
user = scimService.create("Users", user);

// Retrieve the latest version of the user as a UserResource, and
// update/replace it with a PUT request.
user = scimService.retrieve("Users", user.getId(), UserResource.class);
user.setDisplayName("Babs");
user = scimService.replace(user);

// Retrieve the user as a GenericScimResource and update/replace it with a PUT
// request.
GenericScimResource genericUser =
    scimService.retrieve("Users", user.getId(), GenericScimResource.class);
genericUser.replace("displayName", "Babs Jensen");
genericUser = scimService.replaceRequest(genericUser).invoke();

// Perform a partial modification of the user with a PATCH request.
scimService.modifyRequest("Users", user.getId())
    .replaceValue("displayName", "Babs")
    .invoke(GenericScimResource.class);

// Perform a password change with a PATCH request.
scimService.modifyRequest("Users", user.getId())
    .replaceValue("password", "new-password")
    .invoke(GenericScimResource.class);

// Search for users with the same last name as our user. Fetch the first page
// of results, which should have a maximum of five resources on the page. The
// service should only return the value of the "name" attribute.
ListResponse<UserResource> searchResponse =
    scimService.searchRequest("Users")
        .filter(Filter.eq("name.familyName", user.getName().getFamilyName()))
        .page(1, 5)
        .attributes("name")
        .invoke(UserResource.class);
```

For detailed information about using the UnboundID SCIM SDK, including more examples, please see the
[wiki](https://github.com/pingidentity/scim2/wiki).

## Building
This project is built with Maven. To build the source code contained in this repository, run:
```bash
# Linux or Mac
./mvnw clean package

# Windows
mvnw.cmd clean package
```
If desired, the code styling rules enforced by Checkstyle can be ignored with the
`-Dcheckstyle.skip=true` flag.

## Nullability Annotations
As stated above, all inputs, member variables, and return values in this library are labelled with
`com.unboundid.scim2.common.annotations.Nullable` and `com.unboundid.scim2.common.annotations.NotNull`
annotations. To showcase an example, the following method in the `UserResource.java` class accepts
`null` username values, and will never return a `null` object:
```java
@NotNull
public UserResource setUserName(@Nullable final String userName);
```
These annotations can be seen in the Javadocs. This aims to provide insight when invoking SCIM SDK
library methods, help interface with languages like Kotlin (which has built-in null types for
variables), and to help applications become less prone to nullability problems. These annotations
can be leveraged by IDE tools to help validate objects that are given to and received from the SCIM
SDK library. To configure/integrate this into your environment, view the documentation of your IDE
of choice (e.g., [IntelliJ IDEA](https://www.jetbrains.com/help/idea/annotating-source-code.html#configure-nullability-annotations),
[Eclipse](https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-using_null_annotations.htm)).

## Reporting Issues
Please report bug reports and enhancement requests through this project's [issue tracker](https://github.com/pingidentity/scim2/issues). See the [contribution guidelines](CONTRIBUTING.md) for more information.
Note that Ping Identity does not accept third-party code contributions.

## License
As of the 5.0.0 release, the UnboundID SCIM SDK is available under the terms of the following
licenses:
* The [Apache License, version 2.0](LICENSE.md) (recommended).
* The GNU General Public License version 2 ([GPLv2](resource/LICENSE-GPLv2.txt))
* The GNU Lesser General Public License version 2.1 ([LGPLv2.1](resource/LICENSE-LGPLv2.1.txt))
* The legacy [UnboundID Free Use License](resource/LICENSE-UnboundID-SCIM2.txt)

For further background on project licensing, see the [LICENSE.md](LICENSE.md) file.
