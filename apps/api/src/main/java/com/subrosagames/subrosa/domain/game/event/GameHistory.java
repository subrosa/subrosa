package com.subrosagames.subrosa.domain.game.event;

/**
 * Model for game events.
 * Examples of game events include assignments and assignment type changes.
 */
public interface GameHistory {

    /**
     * Get history id.
     *
     * @return history id
     */
    Integer getHistoryId();

    /**
     * Get game id.
     *
     * @return game id
     */
    Integer getGameId();

    /**
     * Get assassin id.
     *
     * @return assassin id
     */
    Integer getAssassinId();

    /**
     * Get victim id.
     *
     * @return victim id
     */
    Integer getVictimId();

    /**
     * Get obituary.
     *
     * @return obituary
     */
    String getObituary();

    /**
     * Get history type.
     *
     * @return history type
     */
    String getType();

}

