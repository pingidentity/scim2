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

package com.unboundid.scim2.extension.messages.pwdmgmt;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.utils.JsonRefBeanSerializer;
import com.unboundid.scim2.common.types.JsonReference;
import com.unboundid.scim2.common.types.AttributeDefinition;
import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.annotations.Attribute;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * This object contains all of the information about the account state.
 * <br><br>
 *
 * Implementation note: the individual properties are stored as JsonReferences
 * so that the server can tell if the client set a value or not, even if the
 * value was set to {@code null}.  The custom serializer will not serialize any
 * properties that were not set.  If the property was set to {@code null} (or
 * any other value), it will be included in the json string.  Then on the server
 * side, this object gets deserialized into a GenericScimResource.  That object
 * will not include values that were not in the json string allowing the
 * server to tell the difference between {@code null} and "not present".
 */
@JsonSerialize(using = JsonRefBeanSerializer.class)
@Schema(id="urn:pingidentity:schemas:2.0:AccountState", name="AccountState",
    description = "Account state is used to retrieve and update the user's " +
        "account state.")
public class AccountState extends BaseScimResource
{
  @Nullable
  @Attribute(description = "True if the account is disabled, or false if " +
      "not.  Set to null to clear.")
  private JsonReference<Boolean> accountDisabled;

  @Nullable
  @Attribute(description = "Time of account expiration.  Set to null " +
      "to clear.")
  private JsonReference<Calendar> accountExpirationTime;

  @Nullable
  @Attribute(description = "Seconds until account is expired.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilAccountExpiration;

  @Nullable
  @Attribute(description = "Password changed time.  Set to null to clear.")
  private JsonReference<Calendar> passwordChangedTime;

  @Nullable
  @Attribute(description = "Password expiration warned time.  Set to null " +
      "to clear.")
  private JsonReference<Calendar> passwordExpirationWarnedTime;

  @Nullable
  @Attribute(description = "Seconds until password will expire.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilPasswordExpiration;

  @Nullable
  @Attribute(description = "Seconds until password expiration warning.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilPasswordExpirationWarning;

  @Nullable
  @Attribute(description = "Times of previous authenticationFailures.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Calendar.class)
  private JsonReference<List<Calendar>> authenticationFailureTimes;

  @Nullable
  @Attribute(description = "Seconds until authentication failure unlock.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilAuthenticationFailureUnlock;

  @Nullable
  @Attribute(description = "Remaining authentication failure count.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> remainingAuthenticationFailureCount;

  @Nullable
  @Attribute(description = "Last login time.  Set to null to clear.")
  private JsonReference<Calendar> lastLoginTime;

  @Nullable
  @Attribute(description = "Seconds until idle lockout.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilIdleLockout;

  @Nullable
  @Attribute(description = "Must change password.")
  private JsonReference<Boolean> mustChangePassword;

  @Nullable
  @Attribute(description = "Seconds until password reset lockout.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilPasswordResetLockout;

  @Nullable
  @Attribute(description = "Times of previous grace logins.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = Calendar.class)
  private JsonReference<List<Calendar>> graceLoginTimes;

  @Nullable
  @Attribute(description = "Remaining grace login count.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Integer> remainingGraceLoginCount;

  @Nullable
  @Attribute(description = "Password change by required time.  Set to null " +
      "to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<Calendar> passwordChangedByRequiredTime;

  @Nullable
  @Attribute(description = "Seconds until require change time.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilRequiredChangeTime;

  @Nullable
  @Attribute(description = "Password history.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE,
      multiValueClass = String.class)
  private JsonReference<List<String>> passwordHistory;

  @Nullable
  @Attribute(description = "Retired password information.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<RetiredPassword> retiredPassword;

  @Nullable
  @Attribute(description = "Time of account activation.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<Calendar> accountActivationTime;

  @Nullable
  @Attribute(description = "Seconds until account is activated.",
      mutability = AttributeDefinition.Mutability.READ_ONLY)
  private JsonReference<Long> secondsUntilAccountActivation;

  @Nullable
  @Attribute(description = "Last login IP address.  Set to null to clear.",
      mutability = AttributeDefinition.Mutability.READ_WRITE)
  private JsonReference<String> lastLoginIpAddress;

  @Nullable
  @Attribute(description = "Account usability notices.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AccountUsabilityIssue.class)
  private JsonReference<List<AccountUsabilityIssue>> accountUsabilityNotices;

  @Nullable
  @Attribute(description = "Account usability warnings.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AccountUsabilityIssue.class)
  private JsonReference<List<AccountUsabilityIssue>> accountUsabilityWarnings;

  @Nullable
  @Attribute(description = "Account usability errors.",
      mutability = AttributeDefinition.Mutability.READ_ONLY,
      multiValueClass = AccountUsabilityIssue.class)
  private JsonReference<List<AccountUsabilityIssue>> accountUsabilityErrors;

  /**
   * Gets a boolean indicating whether or not the account is disabled.
   *
   * @return a boolean indicating whether or not the account is disabled.
   */
  @Nullable
  public Boolean isAccountDisabled()
  {
    return JsonReference.getObject(accountDisabled);
  }

  /**
   * Sets a boolean indicating whether or not the account is disabled.
   *
   * @param accountDisabled a boolean indicating whether or not the account
   *                        is disabled.
   */
  public void setAccountDisabled(@Nullable final Boolean accountDisabled)
  {
    this.accountDisabled = new JsonReference<>(accountDisabled);
  }

  /**
   * Gets the account expiration time.
   *
   * @return the account expiration time.
   */
  @Nullable
  public Calendar getAccountExpirationTime()
  {
    return JsonReference.getObject(accountExpirationTime);
  }

  /**
   * Sets the account expiration time.
   *
   * @param accountExpirationTime the account expiration time.
   */
  public void setAccountExpirationTime(
      @Nullable final Calendar accountExpirationTime)
  {
    this.accountExpirationTime = new JsonReference<>(accountExpirationTime);
  }

  /**
   * Gets the seconds until the account expires.
   *
   * @return the seconds until the account expires.
   */
  @Nullable
  public Long getSecondsUntilAccountExpiration()
  {
    return JsonReference.getObject(secondsUntilAccountExpiration);
  }

  /**
   * Sets the seconds until the account expires.
   *
   * @param secondsUntilAccountExpiration the seconds until the account expires.
   */
  private void setSecondsUntilAccountExpiration(
      @Nullable final Long secondsUntilAccountExpiration)
  {
    this.secondsUntilAccountExpiration =
        new JsonReference<>(secondsUntilAccountExpiration);
  }

  /**
   * Gets the password changed time.
   *
   * @return the password changed time.
   */
  @Nullable
  public Calendar getPasswordChangedTime()
  {
    return JsonReference.getObject(passwordChangedTime);
  }

  /**
   * Sets the password changed time.
   *
   * @param passwordChangedTime the password changed time.
   */
  public void setPasswordChangedTime(
      @Nullable final Calendar passwordChangedTime)
  {
    this.passwordChangedTime = new JsonReference<>(passwordChangedTime);
  }

  /**
   * Gets the password expiration warned time.
   *
   * @return the password expiration warned time.
   */
  @Nullable
  public Calendar getPasswordExpirationWarnedTime()
  {
    return JsonReference.getObject(passwordExpirationWarnedTime);
  }

  /**
   * Sets the password expiration warned time.
   *
   * @param passwordExpirationWarnedTime the passwordExpiration warned time.
   */
  public void setPasswordExpirationWarnedTime(
      @Nullable final Calendar passwordExpirationWarnedTime)
  {
    this.passwordExpirationWarnedTime =
        new JsonReference<>(passwordExpirationWarnedTime);
  }

  /**
   * Gets the seconds until password expiration.
   *
   * @return the seconds until password expiration.
   */
  @Nullable
  public Long getSecondsUntilPasswordExpiration()
  {
    return JsonReference.getObject(secondsUntilPasswordExpiration);
  }

  /**
   * Sets the seconds until password expiration.
   *
   * @param secondsUntilPasswordExpiration the seconds until
   *                                       password expiration.
   */
  private void setSecondsUntilPasswordExpiration(
      @Nullable final Long secondsUntilPasswordExpiration)
  {
    this.secondsUntilPasswordExpiration =
        new JsonReference<>(secondsUntilPasswordExpiration);
  }

  /**
   * Gets the seconds until password expiration warning.
   *
   * @return the seconds until password expiration warning.
   */
  @Nullable
  public Long getSecondsUntilPasswordExpirationWarning()
  {
    return JsonReference.getObject(secondsUntilPasswordExpirationWarning);
  }

  /**
   * Sets the seconds until password expiration warning.
   *
   * @param secondsUntilPasswordExpirationWarning the seconds until
   *                                              password expiration warning.
   */
  private void setSecondsUntilPasswordExpirationWarning(
      @Nullable final Long secondsUntilPasswordExpirationWarning)
  {
    this.secondsUntilPasswordExpirationWarning =
        new JsonReference<>(secondsUntilPasswordExpirationWarning);
  }

  /**
   * Gets the seconds until authentication failure unlock.
   *
   * @return the seconds until authentication failure unlock.
   */
  @Nullable
  public Long getSecondsUntilAuthenticationFailureUnlock()
  {
    return JsonReference.getObject(secondsUntilAuthenticationFailureUnlock);
  }

  /**
   * Sets the seconds until authentication failure unlock.
   *
   * @param secondsUntilAuthenticationFailureUnlock the seconds until
   *     authentication failure unlock.
   */
  private void setSecondsUntilAuthenticationFailureUnlock(
      @Nullable final Long secondsUntilAuthenticationFailureUnlock)
  {
    this.secondsUntilAuthenticationFailureUnlock =
        new JsonReference<>(secondsUntilAuthenticationFailureUnlock);
  }

  /**
   * Gets the remaining authentication failure count.
   *
   * @return the remaining authentication failure count.
   */
  @Nullable
  public Integer getRemainingAuthenticationFailureCount()
  {
    return JsonReference.getObject(remainingAuthenticationFailureCount);
  }

  /**
   * Sets the remaining authentication failure count.
   *
   * @param remainingAuthenticationFailureCount the remaining authentication
   *                                            failure count.
   */
  private void setRemainingAuthenticationFailureCount(
      @Nullable final Integer remainingAuthenticationFailureCount)
  {
    this.remainingAuthenticationFailureCount =
        new JsonReference<>(remainingAuthenticationFailureCount);
  }

  /**
   * Gets the last login time.
   *
   * @return the last login time.
   */
  @Nullable
  public Calendar getLastLoginTime()
  {
    return JsonReference.getObject(lastLoginTime);
  }

  /**
   * Sets the last login time.
   *
   * @param lastLoginTime the last login time.
   */
  public void setLastLoginTime(@Nullable final Calendar lastLoginTime)
  {
    this.lastLoginTime = new JsonReference<>(lastLoginTime);
  }

  /**
   * Gets the seconds until idle lockout.
   *
   * @return the seconds until idle lockout.
   */
  @Nullable
  public Long getSecondsUntilIdleLockout()
  {
    return JsonReference.getObject(secondsUntilIdleLockout);
  }

  /**
   * Sets the seconds until idle lockout.
   *
   * @param secondsUntilIdleLockout the seconds until idle lockout.
   */
  private void setSecondsUntilIdleLockout(
      @Nullable final Long secondsUntilIdleLockout)
  {
    this.secondsUntilIdleLockout = new JsonReference<>(secondsUntilIdleLockout);
  }

  /**
   * Gets a boolean indicating if a user must change his/her password.
   *
   * @return a boolean indicating if a user must change his/her password.
   */
  @Nullable
  public Boolean isMustChangePassword()
  {
    return JsonReference.getObject(mustChangePassword);
  }

  /**
   * Sets a boolean indicating if a user must change his/her password.
   *
   * @param mustChangePassword a boolean indicating if a user must
   *                           change his/her password.
   */
  public void setMustChangePassword(@Nullable final Boolean mustChangePassword)
  {
    this.mustChangePassword = new JsonReference<>(mustChangePassword);
  }

  /**
   * Gets the seconds until password reset lockout.
   *
   * @return the seconds until password reset lockout.
   */
  @Nullable
  public Long getSecondsUntilPasswordResetLockout()
  {
    return JsonReference.getObject(secondsUntilPasswordResetLockout);
  }

  /**
   * Sets the seconds until password reset lockout.
   *
   * @param secondsUntilPasswordResetLockout the seconds until password
   *                                         reset lockout.
   */
  private void setSecondsUntilPasswordResetLockout(
      @Nullable final Long secondsUntilPasswordResetLockout)
  {
    this.secondsUntilPasswordResetLockout =
        new JsonReference<>(secondsUntilPasswordResetLockout);
  }

  /**
   * Gets the remaining grace login count.
   *
   * @return the remaining grace login count.
   */
  @Nullable
  public Integer getRemainingGraceLoginCount()
  {
    return JsonReference.getObject(remainingGraceLoginCount);
  }

  /**
   * Sets the remaining grace login count.
   *
   * @param remainingGraceLoginCount the remaining grace login count.
   */
  private void setRemainingGraceLoginCount(
      @Nullable final Integer remainingGraceLoginCount)
  {
    this.remainingGraceLoginCount =
        new JsonReference<>(remainingGraceLoginCount);
  }

  /**
   * Gets password changed by required time.
   *
   * @return the password changed by required time.
   */
  @Nullable
  public Calendar getPasswordChangedByRequiredTime()
  {
    return JsonReference.getObject(passwordChangedByRequiredTime);
  }

  /**
   * Sets password changed by required time.
   *
   * @param passwordChangedByRequiredTime the password changed by required time.
   */
  public void setPasswordChangedByRequiredTime(
      @Nullable final Calendar passwordChangedByRequiredTime)
  {
    this.passwordChangedByRequiredTime =
        new JsonReference<>(passwordChangedByRequiredTime);
  }

  /**
   * Gets the seconds until required password change time.
   *
   * @return the seconds until required password change time.
   */
  @Nullable
  public Long getSecondsUntilRequiredChangeTime()
  {
    return JsonReference.getObject(secondsUntilRequiredChangeTime);
  }

  /**
   * Sets the seconds until required password change time.
   *
   * @param secondsUntilRequiredChangeTime the seconds until required
   *                                       password change time.
   */
  private void setSecondsUntilRequiredChangeTime(
      @Nullable final Long secondsUntilRequiredChangeTime)
  {
    this.secondsUntilRequiredChangeTime =
        new JsonReference<>(secondsUntilRequiredChangeTime);
  }

  /**
   * Gets the authentication failure times.
   *
   * @return the authentication failure times.
   */
  @Nullable
  public List<Calendar> getAuthenticationFailureTimes()
  {
    return JsonReference.getObject(authenticationFailureTimes);
  }

  /**
   * Sets the authentication failure times.
   *
   * @param authenticationFailureTimes the authentication failure times.
   */
  public void setAuthenticationFailureTimes(
      @Nullable final List<Calendar> authenticationFailureTimes)
  {
    this.authenticationFailureTimes =
        new JsonReference<>(authenticationFailureTimes);
  }

  /**
   * Gets the grace login times.
   *
   * @return the grace login times.
   */
  @Nullable
  public List<Calendar> getGraceLoginTimes()
  {
    return JsonReference.getObject(graceLoginTimes);
  }

  /**
   * Sets the grace login times.
   *
   * @param graceLoginTimes the grace login times.
   */
  public void setGraceLoginTimes(@Nullable final List<Calendar> graceLoginTimes)
  {
    this.graceLoginTimes = new JsonReference<>(graceLoginTimes);
  }

  /**
   * Gets the password history.
   *
   * @return the password history.
   */
  @Nullable
  public List<String> getPasswordHistory()
  {
    return JsonReference.getObject(passwordHistory);
  }

  /**
   * Sets the password history.
   *
   * @param passwordHistory the password history.
   */
  private void setPasswordHistory(@Nullable final List<String> passwordHistory)
  {
    this.passwordHistory = new JsonReference<>(passwordHistory);
  }

  /**
   * Clears the password history.
   */
  public void clearPasswordHistory()
  {
    this.passwordHistory = new JsonReference<>(null);
  }

  /**
   * Gets the retired password information.
   *
   * @return the retired password information.
   */
  @Nullable
  public RetiredPassword getRetiredPassword()
  {
    return JsonReference.getObject(retiredPassword);
  }

  /**
   * Sets the retired password information.
   *
   * @param retiredPassword the retired password information.
   */
  private void setRetiredPassword(
      @Nullable final RetiredPassword retiredPassword)
  {
    this.retiredPassword = new JsonReference<>(retiredPassword);
  }

  /**
   * Purge the retired password information.
   */
  public void purgeRetiredPassword()
  {
    this.retiredPassword = new JsonReference<>(null);
  }

  /**
   * Gets the account activation time.
   *
   * @return the account activation time.
   */
  @Nullable
  public Calendar getAccountActivationTime()
  {
    return JsonReference.getObject(accountActivationTime);
  }

  /**
   * Sets the account activation time.
   *
   * @param accountActivationTime the account activation time.
   */
  public void setAccountActivationTime(
      @Nullable final Calendar accountActivationTime)
  {
    this.accountActivationTime = new JsonReference<>(accountActivationTime);
  }

  /**
   * Gets the seconds until the account is activated.
   *
   * @return the seconds until the account is activated.
   */
  @Nullable
  public Long getSecondsUntilAccountActivation()
  {
    return JsonReference.getObject(secondsUntilAccountActivation);
  }

  /**
   * Sets the seconds until the account is activated.
   *
   * @param secondsUntilAccountActivation the seconds until the account is
   *                                      activated.
   */
  private void setSecondsUntilAccountActivation(
      @Nullable final Long secondsUntilAccountActivation)
  {
    this.secondsUntilAccountActivation =
        new JsonReference<>(secondsUntilAccountActivation);
  }

  /**
   * Gets the account usability notices.
   *
   * @return the account usability notices.
   */
  @Nullable
  public List<AccountUsabilityIssue> getAccountUsabilityNotices()
  {
    return JsonReference.getObject(accountUsabilityNotices);
  }

  /**
   * Sets the account usability notices.
   *
   * @param accountUsabilityNotices the account usability notices.
   */
  private void setAccountUsabilityNotices(
      @Nullable final List<AccountUsabilityIssue> accountUsabilityNotices)
  {
    this.accountUsabilityNotices = new JsonReference<>(accountUsabilityNotices);
  }

  /**
   * Gets the account usability warnings.
   *
   * @return the account usability warnings.
   */
  @Nullable
  public List<AccountUsabilityIssue> getAccountUsabilityWarnings()
  {
    return JsonReference.getObject(accountUsabilityWarnings);
  }

  /**
   * Sets the account usability warnings.
   *
   * @param accountUsabilityWarnings the account usability warnings.
   */
  private void setAccountUsabilityWarnings(
      @Nullable final List<AccountUsabilityIssue> accountUsabilityWarnings)
  {
    this.accountUsabilityWarnings =
        new JsonReference<>(accountUsabilityWarnings);
  }

  /**
   * Gets the account usability errors.
   *
   * @return the account usability errors.
   */
  @Nullable
  public List<AccountUsabilityIssue> getAccountUsabilityErrors()
  {
    return JsonReference.getObject(accountUsabilityErrors);
  }

  /**
   * Sets the account usability errors.
   *
   * @param accountUsabilityErrors the account usability errors.
   */
  private void setAccountUsabilityErrors(
      @Nullable final List<AccountUsabilityIssue> accountUsabilityErrors)
  {
    this.accountUsabilityErrors = new JsonReference<>(accountUsabilityErrors);
  }

  /**
   * Indicates whether the provided object is equal to this account state.
   *
   * @param o   The object to compare.
   * @return    {@code true} if the provided object is equal to this account
   *            state, or {@code false} if not.
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

    AccountState that = (AccountState) o;
    if (!Objects.equals(accountDisabled, that.accountDisabled))
    {
      return false;
    }
    if (!Objects.equals(accountExpirationTime, that.accountExpirationTime))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilAccountExpiration,
        that.secondsUntilAccountExpiration))
    {
      return false;
    }
    if (!Objects.equals(passwordChangedTime, that.passwordChangedTime))
    {
      return false;
    }
    if (!Objects.equals(passwordExpirationWarnedTime,
        that.passwordExpirationWarnedTime))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilPasswordExpiration,
        that.secondsUntilPasswordExpiration))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilPasswordExpirationWarning,
        that.secondsUntilPasswordExpirationWarning))
    {
      return false;
    }
    if (!Objects.equals(authenticationFailureTimes,
        that.authenticationFailureTimes))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilAuthenticationFailureUnlock,
        that.secondsUntilAuthenticationFailureUnlock))
    {
      return false;
    }
    if (!Objects.equals(remainingAuthenticationFailureCount,
        that.remainingAuthenticationFailureCount))
    {
      return false;
    }
    if (!Objects.equals(lastLoginTime, that.lastLoginTime))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilIdleLockout, that.secondsUntilIdleLockout))
    {
      return false;
    }
    if (!Objects.equals(mustChangePassword, that.mustChangePassword))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilPasswordResetLockout,
        that.secondsUntilPasswordResetLockout))
    {
      return false;
    }
    if (!Objects.equals(graceLoginTimes, that.graceLoginTimes))
    {
      return false;
    }
    if (!Objects.equals(remainingGraceLoginCount,
        that.remainingGraceLoginCount))
    {
      return false;
    }
    if (!Objects.equals(passwordChangedByRequiredTime,
        that.passwordChangedByRequiredTime))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilRequiredChangeTime,
        that.secondsUntilRequiredChangeTime))
    {
      return false;
    }
    if (!Objects.equals(passwordHistory, that.passwordHistory))
    {
      return false;
    }
    if (!Objects.equals(retiredPassword, that.retiredPassword))
    {
      return false;
    }
    if (!Objects.equals(accountActivationTime, that.accountActivationTime))
    {
      return false;
    }
    if (!Objects.equals(secondsUntilAccountActivation,
        that.secondsUntilAccountActivation))
    {
      return false;
    }
    if (!Objects.equals(lastLoginIpAddress, that.lastLoginIpAddress))
    {
      return false;
    }
    if (!Objects.equals(accountUsabilityNotices, that.accountUsabilityNotices))
    {
      return false;
    }
    if (!Objects.equals(accountUsabilityWarnings,
        that.accountUsabilityWarnings))
    {
      return false;
    }
    return Objects.equals(accountUsabilityErrors, that.accountUsabilityErrors);
  }

  /**
   * Retrieves a hash code for this account state.
   *
   * @return  A hash code for this account state.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(),
        accountDisabled,
        accountExpirationTime,
        secondsUntilAccountExpiration,
        passwordChangedTime,
        passwordExpirationWarnedTime,
        secondsUntilPasswordExpiration,
        secondsUntilPasswordExpirationWarning,
        authenticationFailureTimes,
        secondsUntilAuthenticationFailureUnlock,
        remainingAuthenticationFailureCount,
        lastLoginTime,
        secondsUntilIdleLockout,
        mustChangePassword,
        secondsUntilPasswordResetLockout,
        graceLoginTimes,
        remainingGraceLoginCount,
        passwordChangedByRequiredTime,
        secondsUntilRequiredChangeTime,
        passwordHistory,
        retiredPassword,
        accountActivationTime,
        secondsUntilAccountActivation,
        lastLoginIpAddress,
        accountUsabilityNotices,
        accountUsabilityWarnings,
        accountUsabilityErrors);
  }
}
