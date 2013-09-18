package com.subrosagames.subrosa.domain.gamesupport.scavenger;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.player.Player;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

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
