package com.subrosagames.subrosa.domain.account;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/26/12
 * Time: 7:40 午後
 * To change this template use File | Settings | File Templates.
 */
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

    private final int KILLING_SPREE_NUMBER_OF_KILLS = 3;
    private final int RAMPAGE_NUMBER_OF_KILLS = 5;
    private final int DOMINATING_NUMBER_OF_KILLS = 7;
    private final int UNSTOPPABLE_NUMBER_OF_KILLS = 10;

}
