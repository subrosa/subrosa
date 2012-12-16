package com.subrosagames.subrosa.service.impl;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.Player;
import com.subrosagames.subrosa.domain.game.Team;
import com.subrosagames.subrosa.service.GameService;
import org.springframework.stereotype.Service;

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
    public Player enrollInGame(Account account, AbstractGame game) {
        return null;
    }
}
