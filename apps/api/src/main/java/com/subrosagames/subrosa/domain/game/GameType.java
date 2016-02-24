package com.subrosagames.subrosa.domain.game;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The different types of games offered.
 * In the future we hope to add humans vs. zombies, paparazzi, etc.
 */
public enum GameType {

    // CHECKSTYLE-OFF: JavadocVariable
    UNKNOWN,
    ASSASSIN,
    PAPARAZZI,
    SCAVENGER;
    // CHECKSTYLE-ON: JavadocVariable

    private static final Map<String, GameType> NAME_TO_GAME_TYPE_MAP = new HashMap<>();

    static {
        for (GameType gameType : GameType.values()) {
            NAME_TO_GAME_TYPE_MAP.put(gameType.name(), gameType);
        }
    }

    /**
     * Get enum value for given name, or {@code GameType.UNKNOWN} if it does not exist.
     *
     * @param name enum name
     * @return enum value
     */
    @JsonCreator
    public static GameType fromName(String name) {
        if (NAME_TO_GAME_TYPE_MAP.containsKey(name)) {
            return NAME_TO_GAME_TYPE_MAP.get(name);
        }
        return GameType.UNKNOWN;
    }

}
