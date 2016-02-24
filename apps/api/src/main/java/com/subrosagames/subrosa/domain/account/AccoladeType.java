package com.subrosagames.subrosa.domain.account;

/**
 * Enum for the various types of {@link Accolade}s.
 */
public enum AccoladeType {

    // CHECKSTYLE-OFF: JavadocVariable
    FIRST_BLOOD,
    FIRST_DEATH,
    KILLING_SPREE,
    RAMPAGE,
    DOMINATING,
    UNSTOPPABLE,
    THIRD_MOST_KILLS,
    SECOND_MOST_KILLS,
    FIRST_MOST_KILLS,
    ULTIMATE_ASSASSIN;
    // CHECKSTYLE-ON: JavadocVariable

    private static final int KILLING_SPREE_NUMBER_OF_KILLS = 3;
    private static final int RAMPAGE_NUMBER_OF_KILLS = 5;
    private static final int DOMINATING_NUMBER_OF_KILLS = 7;
    private static final int UNSTOPPABLE_NUMBER_OF_KILLS = 10;

}
