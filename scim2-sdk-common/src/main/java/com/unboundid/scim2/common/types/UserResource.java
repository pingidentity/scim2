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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URI;
import java.util.List;

import static com.unboundid.scim2.common.utils.StaticUtils.toList;

/**
 * SCIM provides a resource type for "{@code User}" resources.  The core schema
 * for "{@code User}" is identified using the URI:
 * "{@code urn:ietf:params:scim:schemas:core:2.0:User}".
 */
@SuppressWarnings("UnusedReturnValue")
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:User",
    name="User", description = "User Account")
public class UserResource extends BaseScimResource
{
  @Attribute(description = "Unique identifier for the User typically " +
      "used by the user to directly authenticate to the service provider.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.SERVER)
  private String userName;

  @Attribute(description = "The components of the user's real name.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private Name name;

  @Attribute(description = "The name of the User, suitable for display " +
      "to end-users. The name SHOULD be the full name of the User being " +
      "described if known.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String displayName;

  @Attribute(description = "The casual way to address the user in real " +
      "life, e.g.'Bob' or 'Bobby' instead of 'Robert'. This attribute SHOULD " +
      "NOT be used to represent a User's username " +
      "(e.g., bjensen or mpepperidge)",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String nickName;

  @Attribute(description = "A fully qualified URL to a page " +
      "representing the User's online profile",
      isRequired = false,
      referenceTypes = { "external" },
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private URI profileUrl;

  @Attribute(description = "The user's title, such as \"Vice " +
      "President\".",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String title;

  @Attribute(description = "Used to identify the organization to user " +
      "relationship. Typical values used might be 'Contractor', 'Employee', " +
      "'Intern', 'Temp', 'External', and 'Unknown' but any value may be used.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String userType;

  @Attribute(description = "Indicates the User's preferred written or " +
      "spoken language.  Generally used for selecting a localized User " +
      "interface. e.g., 'en_US' specifies the language English and country US.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String preferredLanguage;

  @Attribute(description = "Used to indicate the User's default " +
      "location for purposes of localizing items such as currency, date time " +
      "format, numerical representations, etc.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String locale;

  @Attribute(description = "The User's time zone in the 'Olson' " +
      "timezone database format; e.g.,'America/Los_Angeles'",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String timezone;

  @Attribute(description = "A Boolean value indicating the User's " +
      "administrative status.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private Boolean active;

  @Attribute(description = "The User's clear text password. This " +
      "attribute is intended to be used as a means to specify an initial " +
      "password when creating a new User or to reset an existing User's " +
      "password.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.WRITE_ONLY,
      returned = AttributeDefinition.Returned.NEVER,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String password;

  @Attribute(description = "E-mail addresses for the user. The value " +
      "SHOULD be canonicalized by the Service Provider, e.g., " +
      "bjensen@example.com instead of bjensen@EXAMPLE.COM. Canonical Type " +
      "values of work, home, and other.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = Email.class)
  private List<Email> emails;

  @Attribute(description = "Phone numbers for the User.  The value " +
      "SHOULD be canonicalized by the Service Provider according to format " +
      "in RFC3966 e.g., 'tel:+1-201-555-0123'.  Canonical Type values of " +
      "work, home, mobile, fax, pager and other.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = PhoneNumber.class)
  private List<PhoneNumber> phoneNumbers;

  @Attribute(description = "Instant messaging addresses for the User.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = InstantMessagingAddress.class)
  private List<InstantMessagingAddress> ims;

  @Attribute(description = "URIs of photos of the User.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = Photo.class)
  private List<Photo> photos;

  @Attribute(description = "Physical mailing addresses for this User.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = Address.class)
  private List<Address> addresses;

  @Attribute(description = "A list of groups that the user belongs to, " +
      "either thorough direct membership, nested groups, or dynamically " +
      "calculated.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Group.class)
  private List<Group> groups;

  @Attribute(description = "A list of entitlements for the User that " +
      "represent a thing the User has.",
      isRequired = false,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Entitlement.class)
  private List<Entitlement> entitlements;

  @Attribute(description = "A list of roles for the User that " +
      "collectively represent who the User is; e.g., 'Student', 'Faculty'.",
      isRequired = false,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Role.class)
  private List<Role> roles;

  @Attribute(description = "A list of certificates issued to the User.",
      isRequired = false,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = X509Certificate.class)
  private List<X509Certificate> x509Certificates;

  /**
   * Retrieves the unique identifier for the User typically used by the user
   * to directly authenticate to the service provider.
   *
   * @return The unique identifier for the User typically used by the user
   * to directly authenticate to the service provider.
   */
  public String getUserName()
  {
    return userName;
  }

  /**
   * Specifies the unique identifier for the User typically used by the user
   * to directly authenticate to the service provider.
   *
   * @param userName The unique identifier for the User typically used by the
   *                 user to directly authenticate to the service provider.
   * @return This object.
   */
  public UserResource setUserName(final String userName)
  {
    this.userName = userName;
    return this;
  }

  /**
   * Retrieves the components of the user's full name.
   *
   * @return The components of the user's full name.
   */
  public Name getName()
  {
    return name;
  }

  /**
   * Specifies the components of the user's full name.
   *
   * @param name The components of the user's full name.
   * @return The components of the user's full name.
   */
  public UserResource setName(final Name name)
  {
    this.name = name;
    return this;
  }

  /**
   * Retrieves the name of the User, suitable for display to end-users.
   *
   * @return The name of the User, suitable for display to end-users.
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Specifies the name of the User, suitable for display to end-users.
   *
   * @param displayName The name of the User, suitable for display to end-users.
   * @return This object.
   */
  public UserResource setDisplayName(final String displayName)
  {
    this.displayName = displayName;
    return this;
  }

  /**
   * Retrieves the casual way to address the user in real life, for example, 'Bob' or
   * 'Bobby' instead of 'Robert'.
   *
   * @return The casual way to address the user in real life.
   */
  public String getNickName()
  {
    return nickName;
  }

  /**
   * Specifies the casual way to address the user in real life, for example, 'Bob' or
   * 'Bobby' instead of 'Robert'.
   *
   * @param nickName The casual way to address the user in real life.
   * @return This object.
   */
  public UserResource setNickName(final String nickName)
  {
    this.nickName = nickName;
    return this;
  }

  /**
   * Retrieves the fully qualified URL to a page representing the User's online
   * profile.
   *
   * @return The fully qualified URL to a page representing the User's online
   * profile.
   */
  public URI getProfileUrl()
  {
    return profileUrl;
  }

  /**
   * Specifies the fully qualified URI to a page representing the User's online
   * profile.
   *
   * @param profileUrl the fully qualified URL to a page representing the User's
   *                   online profile.
   * @return This object.
   */
  public UserResource setProfileUrl(final URI profileUrl)
  {
    this.profileUrl = profileUrl;
    return this;
  }

  /**
   * Retrieves the user's title, such as "{@code Vice President}".

   * @return The user's title.
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * Specifies the user's title, such as "{@code Vice President}".
   *
   * @param title The user's title.
   * @return This object.
   */
  public UserResource setTitle(final String title)
  {
    this.title = title;
    return this;
  }

  /**
   * Retrieves the string used to identify the organization to user
   * relationship. Typical values used might be 'Contractor', 'Employee',
   * 'Intern', 'Temp', 'External', and 'Unknown' but any value may be used.
   *
   * @return The string used to identify the organization to user
   * relationship.
   */
  public String getUserType()
  {
    return userType;
  }

  /**
   * Specifies the string used to identify the organization to user
   * relationship. Typical values used might be 'Contractor', 'Employee',
   * 'Intern', 'Temp', 'External', and 'Unknown' but any value may be used.
   *
   * @param userType The string used to identify the organization to user
   * relationship.
   * @return This object.
   */
  public UserResource setUserType(final String userType)
  {
    this.userType = userType;
    return this;
  }

  /**
   * Retrieves the User's preferred written or spoken language.  Generally
   * used for selecting a localized User interface. for example, 'en_US' specifies the
   * language English and country US.
   *
   * @return The User's preferred written or spoken language.
   */
  public String getPreferredLanguage()
  {
    return preferredLanguage;
  }

  /**
   * Specifies the User's preferred written or spoken language.  Generally
   * used for selecting a localized User interface. for example, 'en_US' specifies the
   * language English and country US.
   *
   * @param preferredLanguage The User's preferred written or spoken language.
   * @return This object.
   */
  public UserResource setPreferredLanguage(final String preferredLanguage)
  {
    this.preferredLanguage = preferredLanguage;
    return this;
  }

  /**
   * Retrieves the User's default location for purposes of localizing items such
   * as currency, date time format, numerical representations, etc.
   *
   * @return The User's default location
   */
  public String getLocale()
  {
    return locale;
  }

  /**
   * Specifies the User's default location for purposes of localizing items such
   * as currency, date time format, numerical representations, etc.
   *
   * @param locale The User's default location
   * @return This object.
   */
  public UserResource setLocale(final String locale)
  {
    this.locale = locale;
    return this;
  }

  /**
   * Retrieves the User's time zone in the 'Olson' timezone database format;
   * for example, 'America/Los_Angeles'.
   *
   * @return The User's time zone in the 'Olson' timezone database format.
   */
  public String getTimezone()
  {
    return timezone;
  }

  /**
   * Specifies the User's time zone in the 'Olson' timezone database format;
   * for example, 'America/Los_Angeles'.
   *
   * @param timezone The User's time zone in the 'Olson' timezone database
   *                 format.
   * @return This object.
   */
  public UserResource setTimezone(final String timezone)
  {
    this.timezone = timezone;
    return this;
  }

  /**
   * Retrieves the Boolean value indicating the User's administrative status.
   *
   * @return The Boolean value indicating the User's administrative status.
   */
  public Boolean getActive()
  {
    return active;
  }

  /**
   * Specifies the Boolean value indicating the User's administrative status.
   *
   * @param active The Boolean value indicating the User's administrative
   *               status.
   * @return This object.
   */
  public UserResource setActive(final Boolean active)
  {
    this.active = active;
    return this;
  }

  /**
   * Retrieves the User's clear text password.
   *
   * @return The User's clear text password.
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Specifies the User's clear text password.
   *
   * @param password The User's clear text password.
   * @return This object.
   */
  public UserResource setPassword(final String password)
  {
    this.password = password;
    return this;
  }

  /**
   * Retrieves the email addresses for the user.
   *
   * @return The email addresses for the user.
   */
  public List<Email> getEmails()
  {
    return emails;
  }

  /**
   * Specifies the email addresses for the user.
   *
   * @param emails The email addresses for the user.
   * @return This object.
   */
  public UserResource setEmails(final List<Email> emails)
  {
    this.emails = emails;
    return this;
  }

  /**
   * Alternate version of {@link #setEmails(List)}.
   *
   * @param email   A non-null email.
   * @param emails  An optional set of additional arguments. Any
   *                {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setEmails(final Email email, final Email... emails)
  {
    setEmails(toList(email, emails));
    return this;
  }

  /**
   * Retrieves the phone numbers for the User.
   *
   * @return The Phone numbers for the User.
   */
  public List<PhoneNumber> getPhoneNumbers()
  {
    return phoneNumbers;
  }

  /**
   * Specifies the phone numbers for the User.
   *
   * @param phoneNumbers The phone numbers for the User.
   * @return This object.
   */
  public UserResource setPhoneNumbers(final List<PhoneNumber> phoneNumbers)
  {
    this.phoneNumbers = phoneNumbers;
    return this;
  }

  /**
   * Alternate version of {@link #setPhoneNumbers(List)}.
   *
   * @param phoneNumber   A non-null phone number.
   * @param phoneNumbers  An optional set of additional arguments. Any
   *                      {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setPhoneNumbers(final PhoneNumber phoneNumber,
                                      final PhoneNumber... phoneNumbers)
  {
    setPhoneNumbers(toList(phoneNumber, phoneNumbers));
    return this;
  }

  /**
   * Retrieves the instant messaging addresses for the User.
   *
   * @return The instant messaging addresses for the User.
   */
  public List<InstantMessagingAddress> getIms()
  {
    return ims;
  }

  /**
   * Specifies the instant messaging addresses for the User.
   *
   * @param ims The instant messaging addresses for the User.
   * @return This object.
   */
  public UserResource setIms(final List<InstantMessagingAddress> ims)
  {
    this.ims = ims;
    return this;
  }

  /**
   * Alternate version of {@link #setIms(List)}.
   *
   * @param ims1   A non-null instant messaging address.
   * @param ims    An optional set of additional arguments. Any
   *               {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setIms(final InstantMessagingAddress ims1,
                             final InstantMessagingAddress... ims)
  {
    setIms(toList(ims1, ims));
    return this;
  }

  /**
   * Retrieves the URIs of photos of the User.
   *
   * @return The URIs of photos of the User.
   */
  public List<Photo> getPhotos()
  {
    return photos;
  }

  /**
   * Specifies the URIs of photos of the User.
   *
   * @param photos The URIs of photos of the User.
   * @return This object.
   */
  public UserResource setPhotos(final List<Photo> photos)
  {
    this.photos = photos;
    return this;
  }

  /**
   * Alternate version of {@link #setPhotos(List)}.
   *
   * @param photo   A non-null photo.
   * @param photos  An optional set of additional arguments. Any
   *                {@code null}  values will be ignored.
   * @return This object.
   */
  public UserResource setPhotos(final Photo photo, final Photo... photos)
  {
    setPhotos(toList(photo, photos));
    return this;
  }

  /**
   * Retrieves the physical mailing addresses for this User.
   *
   * @return The physical mailing addresses for this User.
   */
  public List<Address> getAddresses()
  {
    return addresses;
  }

  /**
   * Specifies the physical mailing addresses for this User.
   *
   * @param addresses The physical mailing addresses for this User.
   * @return This object.
   */
  public UserResource setAddresses(final List<Address> addresses)
  {
    this.addresses = addresses;
    return this;
  }

  /**
   * Alternate version of {@link #setAddresses(List)}.
   *
   * @param address    A non-null address.
   * @param addresses  An optional set of additional arguments. Any
   *                   {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setAddresses(final Address address,
                                   final Address... addresses)
  {
    setAddresses(toList(address, addresses));
    return this;
  }

  /**
   * Retrieves the list of groups that the user belongs to, either thorough
   * direct membership, nested groups, or dynamically calculated.
   *
   * @return The list of groups that the user belongs to.
   */
  public List<Group> getGroups()
  {
    return groups;
  }

  /**
   * Specifies the list of groups that the user belongs to, either thorough
   * direct membership, nested groups, or dynamically calculated.
   *
   * @param groups The list of groups that the user belongs to.
   * @return This object.
   */
  public UserResource setGroups(final List<Group> groups)
  {
    this.groups = groups;
    return this;
  }

  /**
   * Alternate version of {@link #setGroups(List)}.
   *
   * @param group1   A non-null group.
   * @param groups   An optional set of additional arguments. Any
   *                 {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setGroups(final Group group1, final Group... groups)
  {
    setGroups(toList(group1, groups));
    return this;
  }

  /**
   * Retrieves the list of entitlements for the User that represent a thing the
   * User has.

   * @return The list of entitlements for the User.
   */
  public List<Entitlement> getEntitlements()
  {
    return entitlements;
  }

  /**
   * Specifies the list of entitlements for the User that represent a thing the
   * User has.
   *
   * @param entitlements The list of entitlements for the User.
   * @return This object.
   */
  public UserResource setEntitlements(final List<Entitlement> entitlements)
  {
    this.entitlements = entitlements;
    return this;
  }

  /**
   * Alternate version of {@link #setEntitlements(List)}.
   *
   * @param entitlement1   A non-null entitlement.
   * @param entitlements   An optional set of additional arguments. Any
   *                       {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setEntitlements(final Entitlement entitlement1,
                                      final Entitlement... entitlements)
  {
    setEntitlements(toList(entitlement1, entitlements));
    return this;
  }

  /**
   * Retrieves the list of roles for the User that collectively represent who
   * the User is; for example, 'Student', 'Faculty'.
   *
   * @return The list of roles for the User.
   */
  public List<Role> getRoles()
  {
    return roles;
  }

  /**
   * Specifies the list of roles for the User that collectively represent who
   * the User is; for example, 'Student', 'Faculty'.
   *
   * @param roles The list of roles for the User.
   * @return This object.
   */
  public UserResource setRoles(final List<Role> roles)
  {
    this.roles = roles;
    return this;
  }

  /**
   * Alternate version of {@link #setRoles(List)}.
   *
   * @param role1  A non-null role.
   * @param roles  An optional set of additional arguments. Any
   *               {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setRoles(final Role role1, final Role... roles)
  {
    setRoles(toList(role1, roles));
    return this;
  }

  /**
   * Retrieves the list of certificates issued to the User.
   *
   * @return The list of certificates issued to the User.
   */
  public List<X509Certificate> getX509Certificates()
  {
    return x509Certificates;
  }

  /**
   * Specifies the list of certificates issued to the User.
   *
   * @param x509Certificates The list of certificates issued to the User.
   * @return This object.
   */
  public UserResource setX509Certificates(
      final List<X509Certificate> x509Certificates)
  {
    this.x509Certificates = x509Certificates;
    return this;
  }

  /**
   * Alternate version of {@link #setX509Certificates(List)}.
   *
   * @param x509Certificate1  A non-null certificate.
   * @param x509Certificates  An optional set of additional arguments. Any
   *                          {@code null} values will be ignored.
   * @return This object.
   */
  public UserResource setX509Certificates(
      final X509Certificate x509Certificate1,
      final X509Certificate... x509Certificates)
  {
    setX509Certificates(toList(x509Certificate1, x509Certificates));
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    UserResource that = (UserResource) o;

    if (userName != null ? !userName.equals(that.userName) :
        that.userName != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (displayName != null ? !displayName.equals(that.displayName) :
        that.displayName != null)
    {
      return false;
    }
    if (nickName != null ? !nickName.equals(that.nickName) :
        that.nickName != null)
    {
      return false;
    }
    if (profileUrl != null ? !profileUrl.equals(that.profileUrl) :
        that.profileUrl != null)
    {
      return false;
    }
    if (title != null ? !title.equals(that.title) : that.title != null)
    {
      return false;
    }
    if (userType != null ? !userType.equals(that.userType) :
        that.userType != null)
    {
      return false;
    }
    if (preferredLanguage != null ? !preferredLanguage.equals(
        that.preferredLanguage) : that.preferredLanguage != null)
    {
      return false;
    }
    if (locale != null ? !locale.equals(that.locale) : that.locale != null)
    {
      return false;
    }
    if (timezone != null ? !timezone.equals(that.timezone) :
        that.timezone != null)
    {
      return false;
    }
    if (active != null ? !active.equals(that.active) : that.active != null)
    {
      return false;
    }
    if (password != null ? !password.equals(that.password) :
        that.password != null)
    {
      return false;
    }
    if (emails != null ? !emails.equals(that.emails) : that.emails != null)
    {
      return false;
    }
    if (phoneNumbers != null ? !phoneNumbers.equals(that.phoneNumbers) :
        that.phoneNumbers != null)
    {
      return false;
    }
    if (ims != null ? !ims.equals(that.ims) : that.ims != null)
    {
      return false;
    }
    if (photos != null ? !photos.equals(that.photos) : that.photos != null)
    {
      return false;
    }
    if (addresses != null ? !addresses.equals(that.addresses) :
        that.addresses != null)
    {
      return false;
    }
    if (groups != null ? !groups.equals(that.groups) : that.groups != null)
    {
      return false;
    }
    if (entitlements != null ? !entitlements.equals(that.entitlements) :
        that.entitlements != null)
    {
      return false;
    }
    if (roles != null ? !roles.equals(that.roles) : that.roles != null)
    {
      return false;
    }
    return !(x509Certificates != null ? !x509Certificates.equals(
        that.x509Certificates) : that.x509Certificates != null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (userName != null ? userName.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
    result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
    result = 31 * result + (profileUrl != null ? profileUrl.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (userType != null ? userType.hashCode() : 0);
    result = 31 * result + (preferredLanguage != null ?
        preferredLanguage.hashCode() : 0);
    result = 31 * result + (locale != null ? locale.hashCode() : 0);
    result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
    result = 31 * result + (active != null ? active.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    result = 31 * result + (emails != null ? emails.hashCode() : 0);
    result = 31 * result + (phoneNumbers != null ? phoneNumbers.hashCode() : 0);
    result = 31 * result + (ims != null ? ims.hashCode() : 0);
    result = 31 * result + (photos != null ? photos.hashCode() : 0);
    result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
    result = 31 * result + (groups != null ? groups.hashCode() : 0);
    result = 31 * result + (entitlements != null ? entitlements.hashCode() : 0);
    result = 31 * result + (roles != null ? roles.hashCode() : 0);
    result = 31 * result + (x509Certificates != null ?
        x509Certificates.hashCode() : 0);
    return result;
  }
}
