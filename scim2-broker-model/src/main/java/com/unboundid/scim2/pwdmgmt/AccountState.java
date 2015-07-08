/*
 * Copyright 2015 UnboundID Corp.
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

package com.unboundid.scim2.pwdmgmt;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.Calendar;
import java.util.List;

/**
 * This object contains all of the information about the account state.
 * The individual properties are stored as JsonReference objects so that
 * the server can tell if the client set a value or not, even if the value
 * was set to null.  The custom serializer will not serialize any properties
 * that were not set.  If the property was set to null however (or any other
 * value), it will be included in the json string.  Then on the server side,
 * this object gets deserialized into a generic scim object.  That object
 * will not include values that were not in the json string allowing the
 * server to tell the difference between null and not present.
 */
@JsonSerialize(using = JsonRefBeanSerializer.class)
@Schema(id="urn:unboundid:schemas:2.0:AccountState", name="AccountState",
    description = "Account state is used to retrieve and update the user's " +
        "account state.")
public class AccountState extends BaseScimResource
{
  @Attribute(description = "True if the account is disabled, or false if " +
      "not.  Set to null to clear.")
  private JsonReference<Boolean> accountDisabled;

  @Attribute(description = "Time of account expiration.  Set to null " +
      "to clear.")
  private JsonReference<Calendar> accountExpirationTime;

  @Attribute(description = "Seconds until account is expired.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilAccountExpiration;

  @Attribute(description = "Password changed time.  Set to null to clear.")
  private JsonReference<Calendar> passwordChangedTime;

  @Attribute(description = "Password expiration warned time.  Set to null " +
      "to clear.")
  private JsonReference<Calendar> passwordExpirationWarnedTime;

  @Attribute(description = "Seconds until password will expire.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilPasswordExpiration;

  @Attribute(description = "Seconds until password expiration warning.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilPasswordExpirationWarning;

  @Attribute(description = "Times of previous authenticationFailures.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Calendar.class)
  private JsonReference<List<Calendar>> authenticationFailureTimes;

  @Attribute(description = "Seconds until authentication failure unlock.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilAuthenticationFailureUnlock;

  @Attribute(description = "Remaining authentication failure count.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> remainingAuthenticationFailureCount;

  @Attribute(description = "Last login time.  Set to null to clear.")
  private JsonReference<Calendar> lastLoginTime;

  @Attribute(description = "Seconds until idle lockout.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilIdleLockout;

  @Attribute(description = "Must change password.")
  private JsonReference<Boolean> mustChangePassword;

  @Attribute(description = "Seconds until password reset lockout.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilPasswordResetLockout;

  @Attribute(description = "Times of previous grace logins.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Calendar.class)
  private JsonReference<List<Calendar>> graceLoginTimes;


  @Attribute(description = "Remaining grace login count.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> remainingGraceLoginCount;

  @Attribute(description = "Password change by required time.  Set to null " +
      "to clear.")
  private JsonReference<Calendar> passwordChangedByRequiredTime;

  @Attribute(description = "Seconds until require change time.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> secondsUntilRequiredChangeTime;

  @Attribute(description = "Password history.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = String.class)
  private JsonReference<List<String>> passwordHistory;

  @Attribute(description = "Retired password information.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<RetiredPassword> retiredPassword;

  /**
   * Gets a boolean indicating whether or not the account is disabled.
   *
   * @return a boolean indicating whether or not the account is disabled.
   */
   public Boolean isAccountDisabled()
  {
    return (accountDisabled == null) ? null : accountDisabled.getObj();
  }

  /**
   * Sets a boolean indicating whether or not the account is disabled.
   *
   * @param accountDisabled a boolean indicating whether or not the account
   *                        is disabled.
   */
  public void setAccountDisabled(final Boolean accountDisabled)
  {
    this.accountDisabled = new JsonReference<Boolean>(accountDisabled);
  }

  /**
   * Gets the account expiration time.
   *
   * @return the account expiration time.
   */
  public Calendar getAccountExpirationTime()
  {
    return (accountExpirationTime == null) ?
        null : accountExpirationTime.getObj();
  }

  /**
   * Sets the account expiration time.
   *
   * @param accountExpirationTime the account expiration time.
   */
  public void setAccountExpirationTime(
      final Calendar accountExpirationTime)
  {
    this.accountExpirationTime =
        new JsonReference<Calendar>(accountExpirationTime);
  }

  /**
   * Gets the seconds until the account expires.
   *
   * @return the seconds until the account expires.
   */
  public Integer getSecondsUntilAccountExpiration()
  {
    return (secondsUntilAccountExpiration == null) ?
        null : secondsUntilAccountExpiration.getObj();
  }

  /**
   * Sets the seconds until the account expires.
   *
   * @param secondsUntilAccountExpiration the seconds until the account expires.
   */
  private void setSecondsUntilAccountExpiration(
      final Integer secondsUntilAccountExpiration)
  {
    this.secondsUntilAccountExpiration =
        new JsonReference<Integer>(secondsUntilAccountExpiration);
  }

  /**
   * Gets the password changed time.
   *
   * @return the password changed time.
   */
  public Calendar getPasswordChangedTime()
  {
    return (passwordChangedTime == null) ? null : passwordChangedTime.getObj();
  }

  /**
   * Sets the password changed time.
   *
   * @param passwordChangedTime the password changed time.
   */
  public void setPasswordChangedTime(final Calendar passwordChangedTime)
  {
    this.passwordChangedTime = new JsonReference<Calendar>(passwordChangedTime);
  }

  /**
   * Gets the password expiration warned time.
   *
   * @return the password expiration warned time.
   */
  public Calendar getPasswordExpirationWarnedTime()
  {
    return (passwordExpirationWarnedTime == null) ?
        null : passwordExpirationWarnedTime.getObj();
  }

  /**
   * Sets the password expiration warned time.
   *
   * @param passwordExpirationWarnedTime the passwordExpiration warned time.
   */
  public void setPasswordExpirationWarnedTime(
      final Calendar passwordExpirationWarnedTime)
  {
    this.passwordExpirationWarnedTime =
        new JsonReference<Calendar>(passwordExpirationWarnedTime);
  }

  /**
   * Gets the seconds until password expiration.
   *
   * @return the seconds until password expiration.
   */
  public Integer getSecondsUntilPasswordExpiration()
  {
    return (secondsUntilPasswordExpiration == null) ?
        null : secondsUntilPasswordExpiration.getObj();
  }

  /**
   * Sets the seconds until password expiration.
   *
   * @param secondsUntilPasswordExpiration the seconds until
   *                                       password expiration.
   */
  private void setSecondsUntilPasswordExpiration(
      final Integer secondsUntilPasswordExpiration)
  {
    this.secondsUntilPasswordExpiration =
        new JsonReference<Integer>(secondsUntilPasswordExpiration);
  }

  /**
   * Gets the seconds until password expiration warning.
   *
   * @return the seconds until password expiration warning.
   */
  public Integer getSecondsUntilPasswordExpirationWarning()
  {
    return (secondsUntilPasswordExpirationWarning == null) ?
        null : secondsUntilPasswordExpirationWarning.getObj();
  }

  /**
   * Sets the seconds until password expiration warning.
   *
   * @param secondsUntilPasswordExpirationWarning the seconds until
   *                                              password expiration warning.
   */
  private void setSecondsUntilPasswordExpirationWarning(
      final Integer secondsUntilPasswordExpirationWarning)
  {
    this.secondsUntilPasswordExpirationWarning =
        new JsonReference<Integer>(secondsUntilPasswordExpirationWarning);
  }

  /**
   * Gets the seconds until authentication failure unlock.
   *
   * @return the seconds until authentication failure unlock.
   */
  public Integer getSecondsUntilAuthenticationFailureUnlock()
  {
    return (secondsUntilAuthenticationFailureUnlock == null) ?
        null : secondsUntilAuthenticationFailureUnlock.getObj();
  }

  /**
   * Sets the seconds until authentication failure unlock.
   *
   * @param secondsUntilAuthenticationFailureUnlock the seconds until
   *     authentication failure unlock.
   */
  private void setSecondsUntilAuthenticationFailureUnlock(
      final Integer secondsUntilAuthenticationFailureUnlock)
  {
    this.secondsUntilAuthenticationFailureUnlock =
        new JsonReference<Integer>(secondsUntilAuthenticationFailureUnlock);
  }

  /**
   * Gets the remaining authentication failure count.
   *
   * @return the remaining authentication failure count.
   */
  public Integer getRemainingAuthenticationFailureCount()
  {
    return (remainingAuthenticationFailureCount == null) ?
        null : remainingAuthenticationFailureCount.getObj();
  }

  /**
   * Sets the remaining authentication failure count.
   *
   * @param remainingAuthenticationFailureCount the remaining authentication
   *                                            failure count.
   */
  private void setRemainingAuthenticationFailureCount(
      final Integer remainingAuthenticationFailureCount)
  {
    this.remainingAuthenticationFailureCount =
        new JsonReference<Integer>(remainingAuthenticationFailureCount);
  }

  /**
   * Gets the last login time.
   *
   * @return the last login time.
   */
  public Calendar getLastLoginTime()
  {
    return (lastLoginTime == null) ? null : lastLoginTime.getObj();
  }

  /**
   * Sets the last login time.
   *
   * @param lastLoginTime the last login time.
   */
  public void setLastLoginTime(final Calendar lastLoginTime)
  {
    this.lastLoginTime = new JsonReference<Calendar>(lastLoginTime);
  }

  /**
   * Gets the seconds until idle lockout.
   *
   * @return the seconds until idle lockout.
   */
  public Integer getSecondsUntilIdleLockout()
  {
    return (secondsUntilIdleLockout == null) ?
        null : secondsUntilIdleLockout.getObj();
  }

  /**
   * Sets the seconds until idle lockout.
   *
   * @param secondsUntilIdleLockout the seconds until idle lockout.
   */
  private void setSecondsUntilIdleLockout(
      final Integer secondsUntilIdleLockout)
  {
    this.secondsUntilIdleLockout =
        new JsonReference<Integer>(secondsUntilIdleLockout);
  }

  /**
   * Gets a boolean indicating if a user must change his/her password.
   *
   * @return a boolean indicating if a user must change his/her password.
   */
  public Boolean isMustChangePassword()
  {
    return (mustChangePassword == null) ? null : mustChangePassword.getObj();
  }

  /**
   * Sets a boolean indicating if a user must change his/her password.
   *
   * @param mustChangePassword a boolean indicating if a user must
   *                           change his/her password.
   */
  public void setMustChangePassword(final Boolean mustChangePassword)
  {
    this.mustChangePassword = new JsonReference<Boolean>(mustChangePassword);
  }

  /**
   * Gets the seconds until password reset lockout.
   *
   * @return the seconds until password reset lockout.
   */
  public Integer getSecondsUntilPasswordResetLockout()
  {
    return (secondsUntilPasswordResetLockout == null) ?
        null : secondsUntilPasswordResetLockout.getObj();
  }

  /**
   * Sets the seconds until password reset lockout.
   *
   * @param secondsUntilPasswordResetLockout the seconds until password
   *                                         reset lockout.
   */
  private void setSecondsUntilPasswordResetLockout(
      final Integer secondsUntilPasswordResetLockout)
  {
    this.secondsUntilPasswordResetLockout =
        new JsonReference<Integer>(secondsUntilPasswordResetLockout);
  }

  /**
   * Gets the remaining grace login count.
   *
   * @return the remaining grace login count.
   */
  public Integer getRemainingGraceLoginCount()
  {
    return (remainingGraceLoginCount == null) ?
        null : remainingGraceLoginCount.getObj();
  }

  /**
   * Sets the remaining grace login count.
   *
   * @param remainingGraceLoginCount the remaining grace login count.
   */
  private void setRemainingGraceLoginCount(
      final Integer remainingGraceLoginCount)
  {
    this.remainingGraceLoginCount =
        new JsonReference<Integer>(remainingGraceLoginCount);
  }

  /**
   * Gets password changed by required time.
   *
   * @return the password changed by required time.
   */
  public Calendar getPasswordChangedByRequiredTime()
  {
    return (passwordChangedByRequiredTime == null) ?
        null : passwordChangedByRequiredTime.getObj();
  }

  /**
   * Sets password changed by required time.
   *
   * @param passwordChangedByRequiredTime the password changed by required time.
   */
  public void setPasswordChangedByRequiredTime(
      final Calendar passwordChangedByRequiredTime)
  {
    this.passwordChangedByRequiredTime =
        new JsonReference<Calendar>(passwordChangedByRequiredTime);
  }

  /**
   * Gets the seconds until required password change time.
   *
   * @return the seconds until required password change time.
   */
  public Integer getSecondsUntilRequiredChangeTime()
  {
    return (secondsUntilRequiredChangeTime == null) ?
        null : secondsUntilRequiredChangeTime.getObj();
  }

  /**
   * Sets the seconds until required password change time.
   *
   * @param secondsUntilRequiredChangeTime the seconds until required
   *                                       password change time.
   */
  private void setSecondsUntilRequiredChangeTime(
      final Integer secondsUntilRequiredChangeTime)
  {
    this.secondsUntilRequiredChangeTime =
        new JsonReference<Integer>(secondsUntilRequiredChangeTime);
  }

  /**
   * Gets the authentication failure times.
   *
   * @return the authentication failure times.
   */
  public List<Calendar> getAuthenticationFailureTimes()
  {
    return (authenticationFailureTimes == null) ?
        null : authenticationFailureTimes.getObj();
  }

  /**
   * Sets the authentication failure times.
   *
   * @param authenticationFailureTimes the authentication failure times.
   */
  public void setAuthenticationFailureTimes(
      final List<Calendar> authenticationFailureTimes)
  {
    this.authenticationFailureTimes =
        new JsonReference<List<Calendar>>(authenticationFailureTimes);
  }

  /**
   * Gets the grace login times.
   *
   * @return the grace login times.
   */
  public List<Calendar> getGraceLoginTimes()
  {
    return (graceLoginTimes == null) ? null : graceLoginTimes.getObj();
  }

  /**
   * Sets the grace login times.
   *
   * @param graceLoginTimes the grace login times.
   */
  public void setGraceLoginTimes(final List<Calendar> graceLoginTimes)
  {
    this.graceLoginTimes = new JsonReference<List<Calendar>>(graceLoginTimes);
  }

  /**
   * Gets the password history.
   *
   * @return the password history.
   */
  public List<String> getPasswordHistory()
  {
    return (passwordHistory == null) ? null : passwordHistory.getObj();
  }

  /**
   * Sets the password history.
   *
   * @param passwordHistory the password history.
   */
  private void setPasswordHistory(final List<String> passwordHistory)
  {
    this.passwordHistory = new JsonReference<List<String>>(passwordHistory);
  }

  /**
   * Clears the password history.
   */
  public void clearPasswordHistory()
  {
    this.passwordHistory = new JsonReference<List<String>>(null);
  }

  /**
   * Gets the retired password information.
   *
   * @return the retired password information.
   */
  public RetiredPassword getRetiredPassword()
  {
    return (retiredPassword == null) ? null : retiredPassword.getObj();
  }

  /**
   * Sets the retired password information.
   *
   * @param retiredPassword the retired password information.
   */
  private void setRetiredPassword(final RetiredPassword retiredPassword)
  {
    this.retiredPassword = new JsonReference<RetiredPassword>(retiredPassword);
  }

  /**
   * Purge the retired password information.
   */
  public void purgeRetiredPassword()
  {
    this.retiredPassword = new JsonReference<RetiredPassword>(null);
  }
}
