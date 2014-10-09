package com.subrosagames.subrosa.domain.game.support;

import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.support.assassin.AssassinGame;
import com.subrosagames.subrosa.domain.game.support.scavenger.ScavengerGame;

/**
 * Maps a game type to its entity type.
 */
public final class GameTypeToEntityMapper {

    private GameTypeToEntityMapper() {
    }

    /**
     * Map the given game type to an entity type.
     *
     * @param gameType game type
     * @return game entity
     */
    public static BaseGame forType(GameType gameType) {
        if (gameType == null) {
            // TODO this can't be right
            return new BaseGame();
//            throw new IllegalArgumentException("Attempt to get entity for null game type.");
        }
        switch (gameType) {
            case ASSASSIN:
                return new AssassinGame();
            case SCAVENGER:
                return new ScavengerGame();
            default:
                throw new IllegalStateException("Entity not defined for game type " + gameType.name());
        }
    }

}
