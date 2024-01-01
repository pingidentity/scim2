/*
 * Copyright 2015-2024 Ping Identity Corporation
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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unboundid.scim2.common.utils.JsonRefBeanSerializer;
import com.unboundid.scim2.common.types.JsonReference;
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
 * was set to "{@code null}".  The custom serializer will not serialize any
 * properties that were not set.  If the property was set to "{@code null}" however (or
 * any other value), it will be included in the json string.  Then on the server
 * side, this object gets deserialized into a generic scim object.  That object
 * will not include values that were not in the json string allowing the
 * server to tell the difference between "{@code null}" and not present.
 */
@JsonSerialize(using = JsonRefBeanSerializer.class)
@Schema(id="urn:pingidentity:schemas:2.0:AccountState", name="AccountState",
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
  private JsonReference<Long> secondsUntilAccountExpiration;

  @Attribute(description = "Password changed time.  Set to null to clear.")
  private JsonReference<Calendar> passwordChangedTime;

  @Attribute(description = "Password expiration warned time.  Set to null " +
      "to clear.")
  private JsonReference<Calendar> passwordExpirationWarnedTime;

  @Attribute(description = "Seconds until password will expire.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilPasswordExpiration;

  @Attribute(description = "Seconds until password expiration warning.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilPasswordExpirationWarning;

  @Attribute(description = "Times of previous authenticationFailures.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Calendar.class)
  private JsonReference<List<Calendar>> authenticationFailureTimes;

  @Attribute(description = "Seconds until authentication failure unlock.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilAuthenticationFailureUnlock;

  @Attribute(description = "Remaining authentication failure count.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> remainingAuthenticationFailureCount;

  @Attribute(description = "Last login time.  Set to null to clear.")
  private JsonReference<Calendar> lastLoginTime;

  @Attribute(description = "Seconds until idle lockout.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilIdleLockout;

  @Attribute(description = "Must change password.")
  private JsonReference<Boolean> mustChangePassword;

  @Attribute(description = "Seconds until password reset lockout.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilPasswordResetLockout;

  @Attribute(description = "Times of previous grace logins.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Calendar.class)
  private JsonReference<List<Calendar>> graceLoginTimes;

  @Attribute(description = "Remaining grace login count.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> remainingGraceLoginCount;

  @Attribute(description = "Password change by required time.  Set to null " +
      "to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<Calendar> passwordChangedByRequiredTime;

  @Attribute(description = "Seconds until require change time.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilRequiredChangeTime;

  @Attribute(description = "Password history.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = String.class)
  private JsonReference<List<String>> passwordHistory;

  @Attribute(description = "Retired password information.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<RetiredPassword> retiredPassword;

  @Attribute(description = "Time of account activation.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<Calendar> accountActivationTime;

  @Attribute(description = "Seconds until account is activated.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilAccountActivation;

  @Attribute(description = "Last login IP address.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<String> lastLoginIpAddress;

  @Attribute(description = "Account usability notices.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AccountUsabilityIssue.class)
  private JsonReference<List<AccountUsabilityIssue>> accountUsabilityNotices;

  @Attribute(description = "Account usability warnings.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AccountUsabilityIssue.class)
  private JsonReference<List<AccountUsabilityIssue>> accountUsabilityWarnings;

  @Attribute(description = "Account usability errors.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AccountUsabilityIssue.class)
  private JsonReference<List<AccountUsabilityIssue>> accountUsabilityErrors;

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
  public Long getSecondsUntilAccountExpiration()
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
      final Long secondsUntilAccountExpiration)
  {
    this.secondsUntilAccountExpiration =
        new JsonReference<Long>(secondsUntilAccountExpiration);
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
  public Long getSecondsUntilPasswordExpiration()
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
      final Long secondsUntilPasswordExpiration)
  {
    this.secondsUntilPasswordExpiration =
        new JsonReference<Long>(secondsUntilPasswordExpiration);
  }

  /**
   * Gets the seconds until password expiration warning.
   *
   * @return the seconds until password expiration warning.
   */
  public Long getSecondsUntilPasswordExpirationWarning()
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
      final Long secondsUntilPasswordExpirationWarning)
  {
    this.secondsUntilPasswordExpirationWarning =
        new JsonReference<Long>(secondsUntilPasswordExpirationWarning);
  }

  /**
   * Gets the seconds until authentication failure unlock.
   *
   * @return the seconds until authentication failure unlock.
   */
  public Long getSecondsUntilAuthenticationFailureUnlock()
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
      final Long secondsUntilAuthenticationFailureUnlock)
  {
    this.secondsUntilAuthenticationFailureUnlock =
        new JsonReference<Long>(secondsUntilAuthenticationFailureUnlock);
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
  public Long getSecondsUntilIdleLockout()
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
      final Long secondsUntilIdleLockout)
  {
    this.secondsUntilIdleLockout =
        new JsonReference<Long>(secondsUntilIdleLockout);
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
  public Long getSecondsUntilPasswordResetLockout()
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
      final Long secondsUntilPasswordResetLockout)
  {
    this.secondsUntilPasswordResetLockout =
        new JsonReference<Long>(secondsUntilPasswordResetLockout);
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
  public Long getSecondsUntilRequiredChangeTime()
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
      final Long secondsUntilRequiredChangeTime)
  {
    this.secondsUntilRequiredChangeTime =
        new JsonReference<Long>(secondsUntilRequiredChangeTime);
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

  /**
   * Gets the account activation time.
   *
   * @return the account activation time.
   */
  public Calendar getAccountActivationTime()
  {
    return (accountActivationTime == null) ?
        null : accountActivationTime.getObj();
  }

  /**
   * Sets the account activation time.
   *
   * @param accountActivationTime the account activation time.
   */
  public void setAccountActivationTime(
      final Calendar accountActivationTime)
  {
    this.accountActivationTime =
        new JsonReference<Calendar>(accountActivationTime);
  }

  /**
   * Gets the seconds until the account is activated.
   *
   * @return the seconds until the account is activated.
   */
  public Long getSecondsUntilAccountActivation()
  {
    return (secondsUntilAccountActivation == null) ?
        null : secondsUntilAccountActivation.getObj();
  }

  /**
   * Sets the seconds until the account is activated.
   *
   * @param secondsUntilAccountActivation the seconds until the account is
   *                                      activated.
   */
  private void setSecondsUntilAccountActivation(
      final Long secondsUntilAccountActivation)
  {
    this.secondsUntilAccountActivation =
        new JsonReference<Long>(secondsUntilAccountActivation);
  }

  /**
   * Gets the account usability notices.
   *
   * @return the account usability notices.
   */
  public List<AccountUsabilityIssue> getAccountUsabilityNotices()
  {
    return (accountUsabilityNotices == null) ?
        null : accountUsabilityNotices.getObj();
  }

  /**
   * Sets the account usability notices.
   *
   * @param accountUsabilityNotices the account usability notices.
   */
  private void setAccountUsabilityNotices(
      final List<AccountUsabilityIssue> accountUsabilityNotices)
  {
    this.accountUsabilityNotices =
        new JsonReference<List<AccountUsabilityIssue>>(accountUsabilityNotices);
  }

  /**
   * Gets the account usability warnings.
   *
   * @return the account usability warnings.
   */
  public List<AccountUsabilityIssue> getAccountUsabilityWarnings()
  {
    return (accountUsabilityWarnings == null) ?
        null : accountUsabilityWarnings.getObj();
  }

  /**
   * Sets the account usability warnings.
   *
   * @param accountUsabilityWarnings the account usability warnings.
   */
  private void setAccountUsabilityWarnings(
      final List<AccountUsabilityIssue> accountUsabilityWarnings)
  {
    this.accountUsabilityWarnings =
        new JsonReference<List<AccountUsabilityIssue>>(
            accountUsabilityWarnings);
  }

  /**
   * Gets the account usability errors.
   *
   * @return the account usability errors.
   */
  public List<AccountUsabilityIssue> getAccountUsabilityErrors()
  {
    return (accountUsabilityErrors == null) ?
        null : accountUsabilityErrors.getObj();
  }

  /**
   * Sets the account usability errors.
   *
   * @param accountUsabilityErrors the account usability errors.
   */
  private void setAccountUsabilityErrors(
      final List<AccountUsabilityIssue> accountUsabilityErrors)
  {
    this.accountUsabilityErrors =
        new JsonReference<List<AccountUsabilityIssue>>(accountUsabilityErrors);
  }

  /**
   * Indicates whether the provided object is equal to this account state.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this account
   *            state, or {@code false} if not.
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

    AccountState that = (AccountState) o;

    if (accountDisabled != null ?
        !accountDisabled.equals(that.accountDisabled) :
        that.accountDisabled != null)
    {
      return false;
    }

    if (accountExpirationTime != null ?
        !accountExpirationTime.equals(that.accountExpirationTime) :
        that.accountExpirationTime != null)
    {
      return false;
    }

    if (secondsUntilAccountExpiration != null
        ? !secondsUntilAccountExpiration.equals(
        that.secondsUntilAccountExpiration) :
        that.secondsUntilAccountExpiration != null)
    {
      return false;
    }
    if (passwordChangedTime != null ?
        !passwordChangedTime.equals(that.passwordChangedTime) :
        that.passwordChangedTime != null)
    {
      return false;
    }
    if (passwordExpirationWarnedTime != null ?
        !passwordExpirationWarnedTime.equals(that.passwordExpirationWarnedTime)
        : that.passwordExpirationWarnedTime != null)
    {
      return false;
    }
    if (secondsUntilPasswordExpiration != null
        ? !secondsUntilPasswordExpiration.equals(
        that.secondsUntilPasswordExpiration) :
        that.secondsUntilPasswordExpiration != null)
    {
      return false;
    }
    if (secondsUntilPasswordExpirationWarning != null ?
        !secondsUntilPasswordExpirationWarning.equals(
            that.secondsUntilPasswordExpirationWarning) :
        that.secondsUntilPasswordExpirationWarning != null)
    {
      return false;
    }
    if (authenticationFailureTimes != null ?
        !authenticationFailureTimes.equals(that.authenticationFailureTimes) :
        that.authenticationFailureTimes != null)
    {
      return false;
    }
    if (secondsUntilAuthenticationFailureUnlock != null ?
        !secondsUntilAuthenticationFailureUnlock.equals(
            that.secondsUntilAuthenticationFailureUnlock) :
        that.secondsUntilAuthenticationFailureUnlock != null)
    {
      return false;
    }
    if (remainingAuthenticationFailureCount != null ?
        !remainingAuthenticationFailureCount.equals(
            that.remainingAuthenticationFailureCount) :
        that.remainingAuthenticationFailureCount != null)
    {
      return false;
    }
    if (lastLoginTime != null ?
        !lastLoginTime.equals(that.lastLoginTime) : that.lastLoginTime != null)
    {
      return false;
    }
    if (secondsUntilIdleLockout != null ?
        !secondsUntilIdleLockout.equals(that.secondsUntilIdleLockout)
        : that.secondsUntilIdleLockout != null)
    {
      return false;
    }
    if (mustChangePassword != null ?
        !mustChangePassword.equals(that.mustChangePassword) :
        that.mustChangePassword != null)
    {
      return false;
    }
    if (secondsUntilPasswordResetLockout != null ?
        !secondsUntilPasswordResetLockout.equals(
            that.secondsUntilPasswordResetLockout) :
        that.secondsUntilPasswordResetLockout != null)
    {
      return false;
    }
    if (graceLoginTimes != null ?
        !graceLoginTimes.equals(that.graceLoginTimes) :
        that.graceLoginTimes != null)
    {
      return false;
    }
    if (remainingGraceLoginCount != null ?
        !remainingGraceLoginCount.equals(that.remainingGraceLoginCount) :
        that.remainingGraceLoginCount != null)
    {
      return false;
    }
    if (passwordChangedByRequiredTime != null ?
        !passwordChangedByRequiredTime.equals(
            that.passwordChangedByRequiredTime) :
        that.passwordChangedByRequiredTime != null)
    {
      return false;
    }
    if (secondsUntilRequiredChangeTime != null ?
        !secondsUntilRequiredChangeTime.equals(
            that.secondsUntilRequiredChangeTime) :
        that.secondsUntilRequiredChangeTime != null)
    {
      return false;
    }
    if (passwordHistory != null ?
        !passwordHistory.equals(that.passwordHistory) :
        that.passwordHistory != null)
    {
      return false;
    }
    if (retiredPassword != null ?
        !retiredPassword.equals(that.retiredPassword) :
        that.retiredPassword != null)
    {
      return false;
    }
    if (accountActivationTime != null ?
        !accountActivationTime.equals(that.accountActivationTime) :
        that.accountActivationTime != null)
    {
      return false;
    }
    if (secondsUntilAccountActivation != null ?
        !secondsUntilAccountActivation.equals(
            that.secondsUntilAccountActivation) :
        that.secondsUntilAccountActivation != null)
    {
      return false;
    }
    if (lastLoginIpAddress != null ?
        !lastLoginIpAddress.equals(that.lastLoginIpAddress) :
        that.lastLoginIpAddress != null)
    {
      return false;
    }
    if (accountUsabilityNotices != null ?
        !accountUsabilityNotices.equals(that.accountUsabilityNotices) :
        that.accountUsabilityNotices != null)
    {
      return false;
    }
    if (accountUsabilityWarnings != null ?
        !accountUsabilityWarnings.equals(that.accountUsabilityWarnings)
        : that.accountUsabilityWarnings != null)
    {
      return false;
    }
    return !(accountUsabilityErrors != null ?
        !accountUsabilityErrors.equals(that.accountUsabilityErrors) :
        that.accountUsabilityErrors != null);

  }

  /**
   * Retrieves a hash code for this account state.
   *
   * @return  A hash code for this account state.
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (accountDisabled != null ?
        accountDisabled.hashCode() : 0);
    result = 31 * result + (accountExpirationTime != null ?
        accountExpirationTime.hashCode() : 0);
    result = 31 * result + (secondsUntilAccountExpiration != null ?
        secondsUntilAccountExpiration.hashCode() : 0);
    result = 31 * result + (passwordChangedTime != null ?
        passwordChangedTime.hashCode() : 0);
    result = 31 * result + (passwordExpirationWarnedTime != null ?
        passwordExpirationWarnedTime.hashCode() : 0);
    result = 31 * result + (secondsUntilPasswordExpiration != null ?
        secondsUntilPasswordExpiration.hashCode() : 0);
    result = 31 * result + (secondsUntilPasswordExpirationWarning != null ?
        secondsUntilPasswordExpirationWarning.hashCode() : 0);
    result = 31 * result + (authenticationFailureTimes != null ?
        authenticationFailureTimes.hashCode() : 0);
    result = 31 * result + (secondsUntilAuthenticationFailureUnlock != null ?
        secondsUntilAuthenticationFailureUnlock.hashCode() : 0);
    result = 31 * result + (remainingAuthenticationFailureCount != null ?
        remainingAuthenticationFailureCount.hashCode() : 0);
    result = 31 * result + (lastLoginTime != null ?
        lastLoginTime.hashCode() : 0);
    result = 31 * result + (secondsUntilIdleLockout != null ?
        secondsUntilIdleLockout.hashCode() : 0);
    result = 31 * result + (mustChangePassword != null ?
        mustChangePassword.hashCode() : 0);
    result = 31 * result + (secondsUntilPasswordResetLockout != null ?
        secondsUntilPasswordResetLockout.hashCode() : 0);
    result = 31 * result + (graceLoginTimes != null ?
        graceLoginTimes.hashCode() : 0);
    result = 31 * result + (remainingGraceLoginCount != null ?
        remainingGraceLoginCount.hashCode() : 0);
    result = 31 * result + (passwordChangedByRequiredTime != null ?
        passwordChangedByRequiredTime.hashCode() : 0);
    result = 31 * result + (secondsUntilRequiredChangeTime != null ?
        secondsUntilRequiredChangeTime.hashCode() : 0);
    result = 31 * result + (passwordHistory != null ?
        passwordHistory.hashCode() : 0);
    result = 31 * result + (retiredPassword != null ?
        retiredPassword.hashCode() : 0);
    result = 31 * result + (accountActivationTime != null ?
        accountActivationTime.hashCode() : 0);
    result = 31 * result + (secondsUntilAccountActivation != null ?
        secondsUntilAccountActivation.hashCode() : 0);
    result = 31 * result + (lastLoginIpAddress != null ?
        lastLoginIpAddress.hashCode() : 0);
    result = 31 * result + (accountUsabilityNotices != null ?
        accountUsabilityNotices.hashCode() : 0);
    result = 31 * result + (accountUsabilityWarnings != null ?
        accountUsabilityWarnings.hashCode() : 0);
    result = 31 * result + (accountUsabilityErrors != null ?
        accountUsabilityErrors.hashCode() : 0);
    return result;
  }
}
