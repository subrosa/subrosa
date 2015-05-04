package com.subrosagames.subrosa.domain.game.support;

import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.GameTypeUnknownException;
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
     * @throws GameTypeUnknownException if game type is not supported
     */
    public static BaseGame forType(GameType gameType) throws GameTypeUnknownException {
        if (gameType == null) {
            // this will fail later validation
            return new BaseGame();
        }
        switch (gameType) {
            case ASSASSIN:
                return new AssassinGame();
            case SCAVENGER:
                return new ScavengerGame();
            case UNKNOWN:
            default:
                throw new GameTypeUnknownException("Entity not defined for game type " + gameType.name());
        }
    }

}
