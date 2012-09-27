package com.subrosagames.subrosa.domain.account;

public enum AccoladeType {

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

    private static final int KILLING_SPREE_NUMBER_OF_KILLS = 3;
    private static final int RAMPAGE_NUMBER_OF_KILLS = 5;
    private static final int DOMINATING_NUMBER_OF_KILLS = 7;
    private static final int UNSTOPPABLE_NUMBER_OF_KILLS = 10;

}
