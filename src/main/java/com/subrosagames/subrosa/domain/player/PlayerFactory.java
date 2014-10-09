package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Factory for managing game players.
 */
public interface PlayerFactory {

    /**
     * Create a new player in a game.
     *
     * @param game game
     * @param account account
     * @param playerDescriptor player information
     * @return created player
     * @throws PlayerValidationException if player information is invalid
     */
    Player createPlayerForGame(Game game, Account account, PlayerDescriptor playerDescriptor) throws PlayerValidationException;

}
