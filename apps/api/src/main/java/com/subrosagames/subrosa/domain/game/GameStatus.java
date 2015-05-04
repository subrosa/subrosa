package com.subrosagames.subrosa.domain.game;

/**
 * Enumeration of possible game statuses.
 */
public enum GameStatus {

    /**
     * Game has not yet been published.
     */
    DRAFT,
    /**
     * Game registration has not yet opened.
     */
    PREREGISTRATION,
    /**
     * Game registration is in progress.
     */
    REGISTRATION,
    /**
     * Game registration is over, but game is not running.
     */
    POSTREGISTRATION,
    /**
     * Game is running.
     */
    RUNNING,
    /**
     * Game is over.
     */
    ARCHIVED
}
