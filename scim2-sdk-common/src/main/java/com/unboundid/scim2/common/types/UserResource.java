/*
 * Copyright 2015-2025 Ping Identity Corporation
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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.net.URI;
import java.util.List;
import java.util.Objects;

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
  @Nullable
  @Attribute(description = "Unique identifier for the User typically " +
      "used by the user to directly authenticate to the service provider.",
      isRequired = true,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.SERVER)
  private String userName;

  @Nullable
  @Attribute(description = "The components of the user's real name.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private Name name;

  @Nullable
  @Attribute(description = "The name of the User, suitable for display " +
      "to end-users. The name SHOULD be the full name of the User being " +
      "described if known.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String displayName;

  @Nullable
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

  @Nullable
  @Attribute(description = "A fully qualified URL to a page " +
      "representing the User's online profile",
      isRequired = false,
      referenceTypes = { "external" },
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private URI profileUrl;

  @Nullable
  @Attribute(description = "The user's title, such as \"Vice " +
      "President\".",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String title;

  @Nullable
  @Attribute(description = "Used to identify the organization to user " +
      "relationship. Typical values used might be 'Contractor', 'Employee', " +
      "'Intern', 'Temp', 'External', and 'Unknown' but any value may be used.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String userType;

  @Nullable
  @Attribute(description = "Indicates the User's preferred written or " +
      "spoken language.  Generally used for selecting a localized User " +
      "interface. e.g., 'en_US' specifies the language English and country US.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String preferredLanguage;

  @Nullable
  @Attribute(description = "Used to indicate the User's default " +
      "location for purposes of localizing items such as currency, date time " +
      "format, numerical representations, etc.",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String locale;

  @Nullable
  @Attribute(description = "The User's time zone in the 'Olson' " +
      "timezone database format; e.g.,'America/Los_Angeles'",
      isRequired = false,
      isCaseExact = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private String timezone;

  @Nullable
  @Attribute(description = "A Boolean value indicating the User's " +
      "administrative status.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE)
  private Boolean active;

  @Nullable
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

  @Nullable
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

  @Nullable
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

  @Nullable
  @Attribute(description = "Instant messaging addresses for the User.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = InstantMessagingAddress.class)
  private List<InstantMessagingAddress> ims;

  @Nullable
  @Attribute(description = "URIs of photos of the User.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = Photo.class)
  private List<Photo> photos;

  @Nullable
  @Attribute(description = "Physical mailing addresses for this User.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      returned = AttributeDefinition.Returned.DEFAULT,
      uniqueness = AttributeDefinition.Uniqueness.NONE,
      multiValueClass = Address.class)
  private List<Address> addresses;

  @Nullable
  @Attribute(description = "A list of groups that the user belongs to, " +
      "either thorough direct membership, nested groups, or dynamically " +
      "calculated.",
      isRequired = false,
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Group.class)
  private List<Group> groups;

  @Nullable
  @Attribute(description = "A list of entitlements for the User that " +
      "represent a thing the User has.",
      isRequired = false,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Entitlement.class)
  private List<Entitlement> entitlements;

  @Nullable
  @Attribute(description = "A list of roles for the User that " +
      "collectively represent who the User is; e.g., 'Student', 'Faculty'.",
      isRequired = false,
      returned = AttributeDefinition.Returned.DEFAULT,
      multiValueClass = Role.class)
  private List<Role> roles;

  @Nullable
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
  @Nullable
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
  @NotNull
  public UserResource setUserName(@Nullable final String userName)
  {
    this.userName = userName;
    return this;
  }

  /**
   * Retrieves the components of the user's full name.
   *
   * @return The components of the user's full name.
   */
  @Nullable
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
  @NotNull
  public UserResource setName(@Nullable final Name name)
  {
    this.name = name;
    return this;
  }

  /**
   * Retrieves the name of the User, suitable for display to end-users.
   *
   * @return The name of the User, suitable for display to end-users.
   */
  @Nullable
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
  @NotNull
  public UserResource setDisplayName(@Nullable final String displayName)
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
  @Nullable
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
  @NotNull
  public UserResource setNickName(@Nullable final String nickName)
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
  @Nullable
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
  @NotNull
  public UserResource setProfileUrl(@Nullable final URI profileUrl)
  {
    this.profileUrl = profileUrl;
    return this;
  }

  /**
   * Retrieves the user's title, such as "{@code Vice President}".
   *
   * @return The user's title.
   */
  @Nullable
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
  @NotNull
  public UserResource setTitle(@Nullable final String title)
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
  @Nullable
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
  @NotNull
  public UserResource setUserType(@Nullable final String userType)
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
  @Nullable
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
  @NotNull
  public UserResource setPreferredLanguage(
      @Nullable final String preferredLanguage)
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
  @Nullable
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
  @NotNull
  public UserResource setLocale(@Nullable final String locale)
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
  @Nullable
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
  @NotNull
  public UserResource setTimezone(@Nullable final String timezone)
  {
    this.timezone = timezone;
    return this;
  }

  /**
   * Retrieves the Boolean value indicating the User's administrative status.
   *
   * @return The Boolean value indicating the User's administrative status.
   */
  @Nullable
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
  @NotNull
  public UserResource setActive(@Nullable final Boolean active)
  {
    this.active = active;
    return this;
  }

  /**
   * Retrieves the User's clear text password.
   *
   * @return The User's clear text password.
   */
  @Nullable
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
  @NotNull
  public UserResource setPassword(@Nullable final String password)
  {
    this.password = password;
    return this;
  }

  /**
   * Retrieves the email addresses for the user.
   *
   * @return The email addresses for the user.
   */
  @Nullable
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
  @NotNull
  public UserResource setEmails(@Nullable final List<Email> emails)
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
  @NotNull
  public UserResource setEmails(@NotNull final Email email,
                                @Nullable final Email... emails)
  {
    setEmails(toList(email, emails));
    return this;
  }

  /**
   * Retrieves the phone numbers for the User.
   *
   * @return The Phone numbers for the User.
   */
  @Nullable
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
  @NotNull
  public UserResource setPhoneNumbers(
      @Nullable final List<PhoneNumber> phoneNumbers)
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
  @NotNull
  public UserResource setPhoneNumbers(
      @NotNull final PhoneNumber phoneNumber,
      @Nullable final PhoneNumber... phoneNumbers)
  {
    setPhoneNumbers(toList(phoneNumber, phoneNumbers));
    return this;
  }

  /**
   * Retrieves the instant messaging addresses for the User.
   *
   * @return The instant messaging addresses for the User.
   */
  @Nullable
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
  @NotNull
  public UserResource setIms(@Nullable final List<InstantMessagingAddress> ims)
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
  @NotNull
  public UserResource setIms(@NotNull final InstantMessagingAddress ims1,
                             @Nullable final InstantMessagingAddress... ims)
  {
    setIms(toList(ims1, ims));
    return this;
  }

  /**
   * Retrieves the URIs of photos of the User.
   *
   * @return The URIs of photos of the User.
   */
  @Nullable
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
  @NotNull
  public UserResource setPhotos(@Nullable final List<Photo> photos)
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
  @NotNull
  public UserResource setPhotos(@NotNull final Photo photo,
                                @Nullable final Photo... photos)
  {
    setPhotos(toList(photo, photos));
    return this;
  }

  /**
   * Retrieves the physical mailing addresses for this User.
   *
   * @return The physical mailing addresses for this User.
   */
  @Nullable
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
  @NotNull
  public UserResource setAddresses(@Nullable final List<Address> addresses)
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
  @NotNull
  public UserResource setAddresses(@NotNull final Address address,
                                   @Nullable final Address... addresses)
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
  @Nullable
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
  @NotNull
  public UserResource setGroups(@Nullable final List<Group> groups)
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
  @NotNull
  public UserResource setGroups(@NotNull final Group group1,
                                @Nullable final Group... groups)
  {
    setGroups(toList(group1, groups));
    return this;
  }

  /**
   * Retrieves the list of entitlements for the User that represent a thing the
   * User has.
   *
   * @return The list of entitlements for the User.
   */
  @Nullable
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
  @NotNull
  public UserResource setEntitlements(
      @Nullable final List<Entitlement> entitlements)
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
  @NotNull
  public UserResource setEntitlements(
      @NotNull final Entitlement entitlement1,
      @Nullable final Entitlement... entitlements)
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
  @Nullable
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
  @NotNull
  public UserResource setRoles(@Nullable final List<Role> roles)
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
  @NotNull
  public UserResource setRoles(@NotNull final Role role1,
                               @Nullable final Role... roles)
  {
    setRoles(toList(role1, roles));
    return this;
  }

  /**
   * Retrieves the list of certificates issued to the User.
   *
   * @return The list of certificates issued to the User.
   */
  @Nullable
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
  @NotNull
  public UserResource setX509Certificates(
      @Nullable final List<X509Certificate> x509Certificates)
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
  @NotNull
  public UserResource setX509Certificates(
      @NotNull final X509Certificate x509Certificate1,
      @Nullable final X509Certificate... x509Certificates)
  {
    setX509Certificates(toList(x509Certificate1, x509Certificates));
    return this;
  }

  /**
   * Indicates whether the provided object is equal to this SCIM user resource.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this user
   *            resource, or {@code false} if not.
   */
  @Override
  public boolean equals(@Nullable final Object o)
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
    if (!Objects.equals(userName, that.userName))
    {
      return false;
    }
    if (!Objects.equals(name, that.name))
    {
      return false;
    }
    if (!Objects.equals(displayName, that.displayName))
    {
      return false;
    }
    if (!Objects.equals(nickName, that.nickName))
    {
      return false;
    }
    if (!Objects.equals(profileUrl, that.profileUrl))
    {
      return false;
    }
    if (!Objects.equals(title, that.title))
    {
      return false;
    }
    if (!Objects.equals(userType, that.userType))
    {
      return false;
    }
    if (!Objects.equals(preferredLanguage, that.preferredLanguage))
    {
      return false;
    }
    if (!Objects.equals(locale, that.locale))
    {
      return false;
    }
    if (!Objects.equals(timezone, that.timezone))
    {
      return false;
    }
    if (!Objects.equals(active, that.active))
    {
      return false;
    }
    if (!Objects.equals(password, that.password))
    {
      return false;
    }
    if (!Objects.equals(emails, that.emails))
    {
      return false;
    }
    if (!Objects.equals(phoneNumbers, that.phoneNumbers))
    {
      return false;
    }
    if (!Objects.equals(ims, that.ims))
    {
      return false;
    }
    if (!Objects.equals(photos, that.photos))
    {
      return false;
    }
    if (!Objects.equals(addresses, that.addresses))
    {
      return false;
    }
    if (!Objects.equals(groups, that.groups))
    {
      return false;
    }
    if (!Objects.equals(entitlements, that.entitlements))
    {
      return false;
    }
    if (!Objects.equals(roles, that.roles))
    {
      return false;
    }
    return Objects.equals(x509Certificates, that.x509Certificates);
  }

  /**
   * Retrieves a hash code for this SCIM user resource.
   *
   * @return  A hash code for this SCIM user resource.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), userName, name, displayName, nickName,
        profileUrl, title, userType, preferredLanguage, locale, timezone,
        active, password, emails, phoneNumbers, ims, photos, addresses, groups,
        entitlements, roles, x509Certificates);
  }
}
