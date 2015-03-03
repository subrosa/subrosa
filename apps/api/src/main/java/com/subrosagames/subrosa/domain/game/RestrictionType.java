package com.subrosagames.subrosa.domain.game;

import java.util.Date;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.subrosagames.subrosa.domain.account.Account;

/**
 * Enumeration of game participation restrictions.
 */
public enum RestrictionType {
    /**
     * Restricts a game to players of a minimum age.
     */
    AGE
            {
                @Override
                public boolean satisfied(Account account, String value) {
                    Date dateOfBirth = account.getDateOfBirth();
                    if (dateOfBirth == null) {
                        return false;
                    }
                    Integer age = Integer.valueOf(value);
                    long now = new Date().getTime();
                    Period period = new Period(dateOfBirth.getTime(), now, PeriodType.years());
                    return period.getYears() >= age;
                }

                @Override
                public String message(String value) {
                    return "atLeast[" + value + "]";
                }

                @Override
                public String field() {
                    return "age";
                }
            };

    /**
     * Returns whether the given account satisfies the restriction.
     *
     * @param account account
     * @param value   restriction value
     * @return whether restriction is satisfied
     */
    public abstract boolean satisfied(Account account, String value);

    /**
     * Provides failure message.
     *
     * @param value restriction value
     * @return failure message
     */
    public abstract String message(String value);

    /**
     * Provides failure field.
     *
     * @return failure field
     */
    public abstract String field();
}
