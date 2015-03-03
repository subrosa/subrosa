package com.subrosagames.subrosa.domain.game;

/**
 * Represents a game rule.
 */
public interface Rule {

    /**
     * Get rule type.
     *
     * @return rule type
     */
    RuleType getRuleType();

    /**
     * Get rule description.
     *
     * @return rule description
     */
    String getDescription();
}
