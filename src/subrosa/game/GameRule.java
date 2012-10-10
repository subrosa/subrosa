package com.subrosa.game;

/**
 * Encapsulates a rule in a game.
 */
public class GameRule {

    private String name;
    private String description;

    public GameRule(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}