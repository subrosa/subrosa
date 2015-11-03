package com.subrosagames.subrosa.service;

import java.util.List;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.domain.player.TeamNotFoundException;

/**
 * Handles interactions between players and games.
 */
public interface GameService {

    /**
     * Create a game with the given information.
     *
     * @param gameDescriptor game information
     * @param account        game creator
     * @return created game
     * @throws GameValidationException if game is invalid for creation
     * @throws ImageNotFoundException  if image is not found
     */
    Game createGame(GameDescriptor gameDescriptor, Account account) throws GameValidationException, ImageNotFoundException;

    /**
     * Update game with the given information.
     *
     * @param gameUrl        game url
     * @param gameDescriptor game information
     * @return updated game
     * @throws GameNotFoundException   if game is not found
     * @throws GameValidationException if game information is invalid
     * @throws ImageNotFoundException  if image is not found
     */
    Game updateGame(String gameUrl, GameDescriptor gameDescriptor) throws GameValidationException, GameNotFoundException, ImageNotFoundException;

    /**
     * Publish the indicated game.
     *
     * @param gameUrl game url
     * @return published game
     * @throws GameValidationException if game is not valid for publishing
     * @throws GameNotFoundException   if specified game url does not map to a game
     */
    Game publishGame(String gameUrl) throws GameValidationException, GameNotFoundException;

    /**
     * Creates a game player for an account.
     *
     * @param gameUrl         game url
     * @param accountId       account to enroll
     * @param joinGameRequest join game request
     * @return created player
     * @throws GameNotFoundException                                                   if game is not found
     * @throws com.subrosagames.subrosa.domain.player.InsufficientInformationException if required player information is missing
     * @throws com.subrosagames.subrosa.domain.player.PlayRestrictedException          if account does not satisfy game requirements
     * @throws PlayerValidationException                                               if player information is not valid
     * @throws AddressNotFoundException                                                if address is not found
     * @throws ImageNotFoundException                                                  if image is not found
     * @throws AccountNotFoundException                                                if account is not found
     * @throws PlayerProfileNotFoundException                                          if the specified player profile is not found
     */
    Player joinGame(String gameUrl, Integer accountId, JoinGameRequest joinGameRequest) throws GameNotFoundException, PlayerValidationException,
            AddressNotFoundException, ImageNotFoundException, AccountNotFoundException, PlayerProfileNotFoundException;

    /**
     * Get game with expansions.
     *
     * @param gameUrl    game url
     * @param expansions fields to expand
     * @return game
     * @throws GameNotFoundException if game does not exist
     */
    Game getGame(String gameUrl, String... expansions) throws GameNotFoundException;

    /**
     * Get the specified game player.
     *
     * @param gameUrl  game url
     * @param playerId player id
     * @return game player
     * @throws GameNotFoundException   if game is not found
     * @throws PlayerNotFoundException if player is not found
     */
    Player getGamePlayer(String gameUrl, Integer playerId) throws GameNotFoundException, PlayerNotFoundException;

    /**
     * Update the specified game player.
     *
     * @param gameUrl         game url
     * @param playerId        player id
     * @param joinGameRequest player information
     * @return updated player
     * @throws GameNotFoundException          if game is not found
     * @throws AddressNotFoundException       if address is not found
     * @throws PlayerNotFoundException        if player is not found
     * @throws ImageNotFoundException         if image is not found
     * @throws PlayerProfileNotFoundException if the specified player profile is not found
     */
    Player updateGamePlayer(String gameUrl, Integer playerId, JoinGameRequest joinGameRequest) throws GameNotFoundException, AddressNotFoundException,
            PlayerNotFoundException, ImageNotFoundException, PlayerProfileNotFoundException;

    List<? extends Team> listTeams(String gameUrl) throws GameNotFoundException;

    Team getTeam(String gameUrl, Integer integer) throws GameNotFoundException, TeamNotFoundException;

    Team createTeam(String gameUrl, TeamDescriptor teamDescriptor) throws GameNotFoundException;

    Team updateTeam(String gameUrl, Integer integer, TeamDescriptor teamDescriptor) throws GameNotFoundException, TeamNotFoundException;
}
