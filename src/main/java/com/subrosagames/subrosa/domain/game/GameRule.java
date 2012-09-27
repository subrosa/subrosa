package com.subrosagames.subrosa.domain.game;

/**
 * Encapsulates a rule in a game.
 */
public class GameRule {

    private String name;
    private String description;

    /**
     * Constructor for a GameRule.
     * @param name the name of the rule.
     * @param description the description of the rule.
     */
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
