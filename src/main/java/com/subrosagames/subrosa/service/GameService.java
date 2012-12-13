package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.Player;
import com.subrosagames.subrosa.domain.game.Team;

/**
 * Handles interactions between players and games.
 */
public interface GameService {

    AbstractGame createGame(Account gameMaster, AbstractGame game);
    Player enrollInTeam(Account account, Team team, String teamPassword);
    Player enrollInGame(Account account, AbstractGame game);
}
