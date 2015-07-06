package com.subrosagames.subrosa.service.impl;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.api.dto.TargetDescriptor;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.service.GameService;

/**
 * Implements game service with direct method calls.
 */
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private AccountFactory accountFactory;

    @Override
    @Transactional
    public Game createGame(GameDescriptor gameDescriptor, Account account) throws GameValidationException, ImageNotFoundException {
        Game game = gameFactory.forDto(gameDescriptor, account);
        return game.create();
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() && hasPermission(#gameUrl, 'Game', 'WRITE_GAME')")
    public Game updateGame(String gameUrl, GameDescriptor gameDescriptor) throws GameValidationException, GameNotFoundException, ImageNotFoundException {
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
    @Transactional
    public Player joinGame(String gameUrl, Integer accountId, JoinGameRequest joinGameRequest) throws GameNotFoundException, PlayerValidationException,
            AddressNotFoundException, ImageNotFoundException, AccountNotFoundException, PlayerProfileNotFoundException
    {
        Game game = gameFactory.getGame(gameUrl);
        Account account = accountFactory.getAccount(accountId);
        return game.joinGame(account, joinGameRequest);
    }

    @Override
    public Game getGame(String gameUrl, String... expansions) throws GameNotFoundException {
        return gameFactory.getGame(gameUrl, expansions);
    }

    @Override
    @Transactional
    @PostAuthorize("isAuthenticated() && hasPermission(returnObject, 'READ_PLAYER')")
    public Player getGamePlayer(String gameUrl, Integer playerId) throws GameNotFoundException, PlayerNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.getPlayer(playerId);
    }

    @Override
    @Transactional
    @PostAuthorize("isAuthenticated() && hasPermission(returnObject.getAccount(), 'WRITE_ACCOUNT')")
    public Player updateGamePlayer(String gameUrl, Integer playerId, JoinGameRequest joinGameRequest) throws GameNotFoundException, AddressNotFoundException,
            PlayerNotFoundException, ImageNotFoundException, PlayerProfileNotFoundException
    {
        Game game = gameFactory.getGame(gameUrl);
        return game.updatePlayer(playerId, joinGameRequest);
    }

    @Override
    public List<Target> listTargets(String gameUrl) {
        return null;
    }

    @Override
    public Target getTarget(String gameUrl, Integer integer) {
        return null;
    }

    @Override
    public Target createTarget(String gameUrl, TargetDescriptor targetDescriptor) {
        return null;
    }

    @Override
    public Target updateTarget(String gameUrl, Integer integer, TargetDescriptor targetDescriptor) {
        return null;
    }

    @Override
    public List<Team> listTeams(String gameUrl) throws GameNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.getTeams();
    }

    @Override
    public Team getTeam(String gameUrl, Integer integer) {
        return null;
    }

    @Override
    public Team createTeam(String gameUrl, TeamDescriptor teamDescriptor) {
        return null;
    }

    @Override
    public Team updateTeam(String gameUrl, Integer integer, TeamDescriptor teamDescriptor) {
        return null;
    }

}
