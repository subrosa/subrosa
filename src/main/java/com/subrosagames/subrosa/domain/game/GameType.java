package com.subrosagames.subrosa.domain.game;

/**
 * The different types of games offered.
 * In the future we hope to add humans vs. zombies, paparazzi, etc.
 */
public enum GameType {

    // CHECKSTYLE-OFF: JavadocVariable
    ASSASSIN(new AssassinGameRuleSetBuilder())
    ;
    // CHECKSTYLE-ON: JavadocVariable

    private GameRuleSetBuilder gameRuleSetBuilder;

    GameType(GameRuleSetBuilder gameRuleSetBuilder) {
        this.gameRuleSetBuilder = gameRuleSetBuilder;
    }

    public GameRuleSetBuilder getGameRuleSetBuilder() {
        return gameRuleSetBuilder;
    }
}
