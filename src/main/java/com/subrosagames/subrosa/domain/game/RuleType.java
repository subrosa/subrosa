package com.subrosagames.subrosa.domain.game;

/**
 * Enumeration of rule types.
 */
public enum RuleType {

    /**
     * Rule applies to all games.
     */
    ALL_GAMES,
    /**
     * Rule applies to specific games.
     */
    GAME_SPECIFIC,

    // games with targets
    /**
     * Rule applies to games with location restrictions.
     */
    LOCATION_RESTRICTION,
    /**
     * Rule applies to games with safe zones.
     */
    SAFE_ZONES,

    // game type
    /**
     * Rule applies to assassin games.
     *
     * @see com.subrosagames.subrosa.domain.game.support.assassin.AssassinGame
     */
    ASSASSIN,
    /**
     * Rule applies to round robin assignment games.
     */
    ROUND_ROBIN,

    /**
     * Rule applies to games with spoons and socks as weapons.
     */
    SPOONS_AND_SOCKS,
    /**
     * Rule applies to games with nerf weapons as weapons.
     */
    NERF_WEAPONS,
    /**
     * Rule applies to games with water weapons as weapons.
     */
    WATER_WEAPONS,

}
