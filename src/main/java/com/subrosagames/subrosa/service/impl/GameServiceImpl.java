package com.subrosagames.subrosa.service.impl;

import com.google.common.base.Optional;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.domain.game.*;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.security.SubrosaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.service.GameService;

import javax.transaction.Transactional;

/**
 * Implements game service with direct method calls.
 */
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameFactory gameFactory;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Game updateGame(String gameUrl, GameDescriptor gameDescriptor) throws GameValidationException, GameNotFoundException {
        Account user = getAuthenticatedUser();
        Game game = gameFactory.getGame(gameUrl);
        if (!user.getId().equals(game.getOwner().getId())) {
            throw new GameNotFoundException("Could not find game to update at " + gameUrl);
        }
        // read-only fields
        gameDescriptor.setId(game.getId());
        gameDescriptor.setUrl(Optional.of(game.getUrl()));
        gameDescriptor.setGameType(Optional.of(game.getGameType()));
        return game.update(gameDescriptor);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
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

    private Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }
}
