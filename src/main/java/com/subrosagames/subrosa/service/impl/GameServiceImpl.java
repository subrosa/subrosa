package com.subrosagames.subrosa.service.impl;

import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.service.GameService;

/**
 * Implements game service with direct method calls.
 */
@Service
public class GameServiceImpl implements GameService {

    @Override
    public Player enrollInTeam(Account account, Team team, String teamPassword) {
        return null;
    }

    @Override
    public Player enrollInGame(Account account, Game game) {
        return null;
    }
}
