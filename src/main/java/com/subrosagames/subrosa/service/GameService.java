package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.Player;
import com.subrosagames.subrosa.domain.game.Team;
import org.springframework.stereotype.Service;

/**
 * Handles interactions between players and games.
 */
public interface GameService {

    Game createGame(Account gameMaster, Game game);
    Player enrollInTeam(Account account, Team team, String teamPassword);
    Player enrollInGame(Account account, Game game);
}
