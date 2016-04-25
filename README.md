# SCIM 2 SDK
This is UnboundID's open source Java SDK for SCIM 2.0. 

SCIM, or _System for Cross-domain Identity Management_, is an IETF standard that defines an extensible schema mechanism and REST API for managing users.

The SCIM 2 SDK consists of the following components:
* **scim2-sdk-client**: The SCIM 2 client library.
* **scim2-sdk-common**: Shared model, exception, and utility classes.
* **scim2-sdk-server**: Classes for use by SCIM 2 service providers.
* **scim2-ubid-extensions**: Model classes representing UnboundID extensions to the SCIM standard.

# How to get it
The SCIM 2 SDK is available as a component in Maven Central.

```xml
<dependency>
  <groupId>com.unboundid.product.scim2</groupId>
  <artifactId>scim2-sdk-client</artifactId>
  <version>1.1.34</version>
</dependency>
```

You may also download SCIM 2 SDK builds from the [Releases](https://github.com/UnboundID/scim2/releases) page.

# How to use it
The SCIM 2 SDK requires Java 6 or greater. 

The primary point of entry for a client is the `ScimService` class, which represents a SCIM service provider, such as the UnboundID Data Broker. This class acts as a wrapper for a JAX-RS client instance, providing methods for building requests and facilities for pathing through JSON objects.

```java
// Create a ScimService
ClientConfig clientConfig = …
Client client = ClientBuilder.newClient(clientConfig);
WebTarget target = client.target("https://example.com/scim/v2");
ScimService scimService = new ScimService(target);

// Retrieve a user as a UserResource and modify using PUT
UserResource user1 = scimClient.retrieve(ScimService.ME_URI, UserResource.class);
user1.setNickName("Babs");
scimClient.replace(user);

// Retrieve a user as a GenericScimResource and modify using PUT
GenericScimResource user2 = scimClient.retrieve(ScimService.ME_URI, GenericScimResource.class);
user2.replaceValue("nickName", TextNode.valueOf("Babs"));
service.replaceRequest(user2);

// Modify using PATCH
GenericScimResource user3 = scimClient.retrieve(ScimService.ME_URI, GenericScimResource.class);
service.modifyRequest(user3).addOperation(
          PatchOperation.replace("nickName", TextNode.valueOf("Babs")))
          .invoke(GenericScimResource.class);
```

For detailed information about using the SCIM 2 SDK, including more examples, please see the [wiki](https://github.com/UnboundID/scim2/wiki).

# License
The SCIM 2 SDK is LGPL-licensed. See the [LICENSE.txt](LICENSE) file for more info.
