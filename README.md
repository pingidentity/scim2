[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.unboundid.product.scim2/scim2-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.unboundid.product.scim2/scim2-parent)
[![Javadocs](http://javadoc.io/badge/com.unboundid.product.scim2/scim2-parent.svg)](http://javadoc.io/doc/com.unboundid.product.scim2/scim2-parent) 
[![Build Status](https://travis-ci.org/pingidentity/scim2.svg?branch=master)](https://travis-ci.org/pingidentity/scim2)
# SCIM 2 SDK
 [SCIM](http://www.simplecloud.info), or _System for Cross-domain Identity Management_, is an IETF standard that defines an extensible schema mechanism and REST API for **managing users and other identity data**. SCIM is used by a variety of vendors — including Facebook, Salesforce, Microsoft, Cisco, Sailpoint, and UnboundID — for a variety of purposes, including user provisioning, directory services, attribute exchange, and more.

The UnboundID SCIM 2 SDK for Java provides a powerful and flexible set of APIs for interacting with SCIM service providers and resources. Use it to build applications and servers that interoperate with SCIM servers such as the [PingDataGovernance Server](https://www.pingidentity.com/en/software/pingdatagovernance.html).

The SCIM 2 SDK consists of the following components:

| Component name | What it is | Who needs it |
| --- | --- | --- |
| `scim2-sdk-client` | The SCIM 2 client API. | SCIM client developers. |
| `scim2-ubid-extensions` | Model classes representing UnboundID extensions to the SCIM standard. This component is subject to API changes and should be considered experimental. | SCIM client developers using features specific to UnboundID servers. |
| `scim2-sdk-server` | Classes for use by SCIM 2 service providers. | SCIM service provider implementers. |
| `scim2-sdk-common` | Shared model, exception, and utility classes. | Included as a transitive dependency of all of the above. |

# How to get it
The SCIM 2 SDK is available from Maven Central and can be included in your product like any other Maven dependency. Check Maven Central for the latest available version of SCIM 2 SDK dependencies.

For general-purpose clients:

```xml
<dependency>
  <groupId>com.unboundid.product.scim2</groupId>
  <artifactId>scim2-sdk-client</artifactId>
  <version>VERSION</version>
</dependency>
```

For clients using UnboundID-specific features:

```xml
<dependency>
  <groupId>com.unboundid.product.scim2</groupId>
  <artifactId>scim2-ubid-extensions</artifactId>
  <version>VERSION</version>
</dependency>
```

You may also download SCIM 2 SDK builds from the [Releases](https://github.com/pingidentity/scim2/releases) page.

If you're looking for a Java SDK for SCIM 1.1, you can find it [here](https://github.com/pingidentity/scim).

# How to use it
The SCIM 2 SDK requires Java 7 or greater.

The primary point of entry for a client is the `ScimService` class, which represents a SCIM service provider, such as the PingDataGovernance Server. This class acts as a wrapper for a [JAX-RS](https://jax-rs-spec.java.net) client instance, providing methods for building and making requests.

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

// Create a ScimService
Client client = ClientBuilder.newClient().register(OAuth2ClientSupport.feature("..bearerToken.."));;
WebTarget target = client.target("https://example.com/scim/v2");
ScimService scimService = new ScimService(target);

// Create a user
UserResource user = new UserResource();
user.setUserName("babs");
user.setPassword("secret");
Name name = new Name()
  .setGivenName("Barbara")
  .setFamilyName("Jensen");
user.setName(name);
Email email = new Email()
  .setType("home")
  .setPrimary(true)
  .setValue("babs@example.com");
user.setEmails(Collections.singletonList(email));
user = scimService.create("Users", user);

// Retrieve the user as a UserResource and replace with a modified instance using PUT
user = scimService.retrieve("Users", user.getId(), UserResource.class);
user.setDisplayName("Babs");
user = scimService.replace(user);

// Retrieve the user as a GenericScimResource and replace with a modified instance using PUT
GenericScimResource genericUser =
    scimService.retrieve("Users", user.getId(), GenericScimResource.class);
genericUser.replaceValue("displayName", TextNode.valueOf("Babs Jensen"));
genericUser = scimService.replaceRequest(genericUser).invoke();

// Perform a partial modification of the user using PATCH
scimService.modifyRequest("Users", user.getId())
           .replaceValue("displayName", "Babs")
           .invoke(GenericScimResource.class);

// Perform a password change using PATCH
scimService.modifyRequest("Users", user.getId())
           .replaceValue("password", "new-password")
           .invoke(GenericScimResource.class);

// Search for users with the same last name as our user
ListResponse<UserResource> searchResponse =
  scimService.searchRequest("Users")
        .filter(Filter.eq("name.familyName", user.getName().getFamilyName()).toString())
        .page(1, 5)
        .attributes("name")
        .invoke(UserResource.class);
```

For detailed information about using the SCIM 2 SDK, including more examples, please see the [wiki](https://github.com/pingidentity/scim2/wiki).

# Reporting issues

Please report bug reports and enhancement requests through this project's [issue tracker](https://github.com/pingidentity/scim2/issues). See the [contribution guidelines](CONTRIBUTING.md) for more information.

# License
The UnboundID SCIM2 SDK is available under three licenses: the GNU General Public License version 2 (GPLv2), the GNU Lesser General Public License version 2.1 (LGPLv2.1), and a free-right-to-use license created by UnboundID Corp. See the [LICENSE](https://github.com/pingidentity/scim2/blob/master/resource/LICENSE.txt) file for more info.
