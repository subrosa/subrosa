package com.subrosagames.subrosa.domain.gamesupport.scavenger;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Represents an assassin-based game.
 */
@Entity
@DiscriminatorValue(ScavengerGame.GAME_TYPE_SCAVENGER)
public class ScavengerGame extends GameEntity {

    private static final Logger LOG = LoggerFactory.getLogger(ScavengerGame.class);

    public static final String GAME_TYPE_SCAVENGER = "SCAVENGER";

    public ScavengerGame() { }

}
