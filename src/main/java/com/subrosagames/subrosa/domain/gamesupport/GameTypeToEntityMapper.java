package com.subrosagames.subrosa.domain.gamesupport;

import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.gamesupport.scavenger.ScavengerGame;

/**
 */
public final class GameTypeToEntityMapper {

    private GameTypeToEntityMapper() { }

    public static GameEntity forType(GameType gameType) {
        if (gameType == null) {
            // TODO this can't be right
            return new GameEntity();
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
