package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 *
 */
public interface PlayerFactory {

    Player createPlayerForGame(Game gameId, Account account);

    Player getPlayerForEntity(PlayerEntity input);
}
