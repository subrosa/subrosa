package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Team;

/**
 * Handles interactions between players and games.
 */
public interface GameService {

    /**
     * Enroll the given account into a team with a team password.
     * @param account account to enroll
     * @param team team into which to enroll
     * @param teamPassword team password
     * @return the resulting game player
     */
    Player enrollInTeam(Account account, Team team, String teamPassword);

    /**
     * Enroll the given account as a player of a game.
     * @param account account to enroll
     * @param game game into which to enroll
     * @return the resulting game player
     */
    Player enrollInGame(Account account, Game game);
}
