package com.subrosagames.subrosa.service.impl;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.api.dto.JoinTeamRequest;
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
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.domain.player.TeamNotFoundException;
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
    public List<? extends Team> listTeams(String gameUrl) throws GameNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.getTeams();
    }

    @Override
    public Team getTeam(String gameUrl, Integer teamId) throws GameNotFoundException, TeamNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.getTeam(teamId);
    }

    @Override
    public Team createTeam(String gameUrl, TeamDescriptor teamDescriptor) throws GameNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.addTeam(teamDescriptor);
    }

    @Override
    @Transactional
    public Team updateTeam(String gameUrl, Integer teamId, TeamDescriptor teamDescriptor) throws GameNotFoundException, TeamNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.updateTeam(teamId, teamDescriptor);
    }

    @Override
    @Transactional
    public Team joinTeam(Player player, Team team, JoinTeamRequest joinTeamRequest) {
        return team.join(player, joinTeamRequest);
    }

}
