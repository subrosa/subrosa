package com.subrosagames.subrosa.domain.game.dispute;

/**
 * The different types of disputes that can occur in a game.
 */
public enum DisputeType {
    BAD_PHOTO ("Misleading or invalid photo",
            "The target's photo ID is either obscured or is not an actual photo of his or herself."),
    BAD_ACCOUNT_INFO ("Misleading or incorrect account information",
            "The target has submitted a name, address, email or telephone number that is incorrect, "
                    + "false or misleading."),
    BREAKING_GAME_RULES ("Violating game rules", "Your target or assassin violated a game rule or broke a law.  "
            + "This includes trying to make a hit at work, using an unapproved weapon, breaking and entering "
            + "into a target's home, etc."),
    UNSPORTSMANLIKE_CONDUCT ("Profane or unsportsmanlike conduct", "Your target or assassin has communicated "
            + "or acted towards you in an overly aggressive, inappropriate or otherwise vile manner."),
    KILL_DISAGREEMENT ("Kill disagreement", "You and your target/assassin disagree over the outcome of a kill."),
    OTHER ("Other", "If none of our other well thought out dispute categories apply, then use this one.");

    private final String description;
    private final String explanation;

    DisputeType(String description, String explanation) {
        this.description = description;
        this.explanation = explanation;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getExplanation() {
        return explanation;
    }
}
