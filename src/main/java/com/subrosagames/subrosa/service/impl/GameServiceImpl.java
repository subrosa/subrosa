package com.subrosagames.subrosa.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.GameService;

/**
 * Implements game service with direct method calls.
 */
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameFactory gameFactory;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() && hasPermission(#gameUrl, 'Game', 'WRITE_GAME')")
    public Game updateGame(String gameUrl, GameDescriptor gameDescriptor) throws GameValidationException, GameNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.update(gameDescriptor);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() && hasPermission(#gameUrl, 'Game', 'WRITE_GAME')")
    public Game publishGame(String gameUrl) throws GameValidationException, GameNotFoundException {
        return gameFactory.getGame(gameUrl).publish();
    }

    @Override
    public Player enrollInTeam(Account account, Team team, String teamPassword) {
        return null;
    }

    @Override
    public Player enrollInGame(Account account, Game game) {
        return null;
    }

    @Override
    @Transactional
    public Player joinGame(String gameUrl, Account account, JoinGameRequest joinGameRequest) throws GameNotFoundException, PlayerValidationException {
        Game game = gameFactory.getGame(gameUrl);
        return game.joinGame(getAuthenticatedUser(), joinGameRequest);
    }

    @Override
    public Game getGame(String gameUrl, String... expansions) throws GameNotFoundException {
        return gameFactory.getGame(gameUrl, expansions);
    }

    private Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }
}
