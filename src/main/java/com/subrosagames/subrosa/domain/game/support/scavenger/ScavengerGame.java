package com.subrosagames.subrosa.domain.game.support.scavenger;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.subrosagames.subrosa.domain.game.BaseGame;

/**
 * Represents an assassin-based game.
 */
@Entity
@DiscriminatorValue(ScavengerGame.GAME_TYPE_SCAVENGER)
public class ScavengerGame extends BaseGame {

    /**
     * Game type identifier.
     */
    public static final String GAME_TYPE_SCAVENGER = "SCAVENGER";

    /**
     * Default constructor.
     */
    public ScavengerGame() {
    }

}
