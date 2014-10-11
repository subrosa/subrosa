package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.account.Account;

/**
 * Represents a restriction on who can play a game.
 */
public interface Restriction {

    /**
     * Returns whether restriction is satisfied by provided account.
     * @param account account
     * @return whether restriction is satisfied
     */
    boolean satisfied(Account account);

    /**
     * Restriction message.
     * @return restriction message
     */
    String message();

    /**
     * Restricted field.
     *
     * @return restricted field
     */
    String field();

    /**
     * Type of restriction.
     * @return type of restriction
     */
    RestrictionType getType();

    /**
     * Value against restriction is evaluated.
     * @return restriction value
     */
    String getValue();
}
